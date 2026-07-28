package io.github.smokahs.hungeroverhauled.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;
import io.github.smokahs.hungeroverhauled.config.Config;
import io.github.smokahs.hungeroverhauled.registry.ModTags;

// kill an animal with anything but a knife and it might go off in your face
@Mod.EventBusSubscriber(modid = HungerOverhauled.MOD_ID)
public final class HumaneSlaughter {

    private HumaneSlaughter() {
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!Config.explodeInhumaneKills || Config.inhumaneKillChance <= 0.0F) {
            return;
        }

        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide() || !(entity instanceof Animal)) {
            return;
        }

        // only a player can be told off for this
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        if (player.getMainHandItem().is(ModTags.HUMANE_SLAUGHTER_TOOLS)) {
            return;
        }

        if (entity.getRandom().nextFloat() >= Config.inhumaneKillChance) {
            return;
        }

        if (Config.inhumaneKillDestroysDrops) {
            event.getDrops().clear();
        }

        Level.ExplosionInteraction blast = Config.inhumaneKillBreaksBlocks
                ? Level.ExplosionInteraction.MOB
                : Level.ExplosionInteraction.NONE;

        entity.level().explode(entity, entity.getX(), entity.getY(0.5D), entity.getZ(),
                Config.inhumaneKillExplosionPower, blast);
    }
}
