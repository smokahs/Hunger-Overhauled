package io.github.smokahs.hungeroverhauled.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;
import io.github.smokahs.hungeroverhauled.mixin.ZombieVillagerAccessor;

// extra utilities' healing axe, as it played in 1.7.10 new horizons. that means the base item plus the
// two hodgepodge fixes the pack ships on by default: the heal actually lands, and the axe never wears out
public class HealingAxeItem extends AxeItem {

    // 'k' in the original, doubled before anything reads it
    private static final float HEAL_AMOUNT = 4.0F;

    // undead take four times what a living mob would have been healed for
    private static final float UNDEAD_DAMAGE = HEAL_AMOUNT * 4.0F;

    // the swing is paid for out of your own health, half a heart less than the target gains
    private static final float HEAL_COST = HEAL_AMOUNT - 0.5F;

    // two and a half haunches per swing, so it eats the hunger the axe hands back
    private static final float HIT_EXHAUSTION = 10.0F;

    private static final int FEED_INTERVAL = 40;

    private static final int PARTICLE_COUNT = 5;

    // instant health, the potion the original pulled its particle colour from
    private static final int HEAL_PARTICLE_COLOR = 0xF82423;

    private static final String TOOLTIP = "item." + HungerOverhauled.MOD_ID + ".healing_axe.";

    public HealingAxeItem(Properties properties) {
        // diamond is 1.7.10's EMERALD tier, and 2.0 puts the tooltip back on the 6 damage it showed there.
        // cosmetic either way, since the axe cancels every hit it makes
        super(Tiers.DIAMOND, 2.0F, -3.0F, properties);
    }

    // returning true cancels the attack, so nothing is ever hurt by the swing itself
    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        player.causeFoodExhaustion(HIT_EXHAUSTION);

        // the original only ever looked at mobs, so players and armour stands take nothing either way
        if (!(entity instanceof Mob target)) {
            return true;
        }

        Level level = target.level();

        if (level.isClientSide()) {
            spawnHealParticles(target);
            return true;
        }

        if (!target.isAlive()) {
            return true;
        }

        if (target.getMobType() == MobType.UNDEAD) {
            if (target instanceof ZombieVillager zombie) {
                // conversion time 0, so the cure finishes on the next tick like it did in 1.7.10
                ((ZombieVillagerAccessor) zombie).invokeStartConverting(player.getUUID(), 0);
            } else {
                target.hurt(player.damageSources().playerAttack(player), UNDEAD_DAMAGE);
            }

            return true;
        }

        if (target.getHealth() > 0.0F && target.getHealth() < target.getMaxHealth()) {
            payHealthCost(player);
            target.heal(HEAL_AMOUNT);
        }

        return true;
    }

    // slowly tops your hunger bar up while you hold it
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide() || !isSelected || !(entity instanceof Player player)) {
            return;
        }

        if (level.getGameTime() % FEED_INTERVAL == 0L) {
            player.getFoodData().eat(1, 0.2F);
        }
    }

    // hodgepodge's unbreakable fix. the base item never spent durability either, it just showed a full bar
    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    // the original hid the glint even with efficiency on it
    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    // only ever called while a tooltip is being drawn, so the client-only shift check is safe here
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(TOOLTIP + "durability").withStyle(ChatFormatting.GRAY));

        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable(TOOLTIP + "hint").withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        tooltip.add(Component.empty());
        tooltip.add(entry("passive", ChatFormatting.GOLD));
        tooltip.add(Component.empty());
        tooltip.add(entry("heal", ChatFormatting.GREEN));
        tooltip.add(entry("cure", ChatFormatting.LIGHT_PURPLE));
    }

    // a coloured label with the description behind it, kept white so it does not inherit the label colour
    private static MutableComponent entry(String key, ChatFormatting labelColor) {
        return Component.translatable(TOOLTIP + key).withStyle(labelColor)
                .append(Component.translatable(TOOLTIP + key + ".desc").withStyle(ChatFormatting.WHITE));
    }

    // setHealth while you would survive it, real damage otherwise, so a fatal swing is credited properly
    private static void payHealthCost(Player player) {
        if (player.getHealth() - HEAL_COST > 0.0F) {
            player.setHealth(player.getHealth() - HEAL_COST);
        } else {
            player.hurt(player.damageSources().playerAttack(player), HEAL_COST);
        }
    }

    private static void spawnHealParticles(Mob target) {
        Level level = target.level();
        RandomSource random = level.getRandom();

        float red = (HEAL_PARTICLE_COLOR >> 16 & 0xFF) / 255.0F;
        float green = (HEAL_PARTICLE_COLOR >> 8 & 0xFF) / 255.0F;
        float blue = (HEAL_PARTICLE_COLOR & 0xFF) / 255.0F;

        for (int i = 0; i < PARTICLE_COUNT; i++) {
            // entity effect reads its speed arguments as a colour, the same trick 'mobSpell' used
            level.addParticle(ParticleTypes.ENTITY_EFFECT,
                    target.getX() + (random.nextDouble() - 0.5D) * target.getBbWidth(),
                    target.getY() + random.nextDouble() * target.getBbHeight(),
                    target.getZ() + (random.nextDouble() - 0.5D) * target.getBbWidth(),
                    red, green, blue);
        }
    }
}
