package io.github.smokahs.hungeroverhauled.player;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;
import io.github.smokahs.hungeroverhauled.config.Config;
import io.github.smokahs.hungeroverhauled.hunger.FoodDataAccess;

@Mod.EventBusSubscriber(modid = HungerOverhauled.MOD_ID)
public final class PlayerEvents {

    // the 1.12 version stored a countdown in NBT to run this every 10 ticks
    private static final int CHECK_INTERVAL = 10;

    // where each player's walk distance was at the last check
    private static final Map<Player, Float> LAST_WALK_DIST = new WeakHashMap<>();

    private PlayerEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;

        if (event.phase != TickEvent.Phase.END || player.level().isClientSide()) {
            return;
        }

        if (player.tickCount % CHECK_INTERVAL != 0 || player.isDeadOrDying()) {
            return;
        }

        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        if (Config.constantHungerLoss) {
            player.causeFoodExhaustion(0.01F);
        }

        applyWalkExhaustion(player);

        if (!Config.addLowStatEffects) {
            return;
        }

        float healthPercent = player.getHealth() / player.getMaxHealth();

        if (healthPercent <= 0.0F) {
            return;
        }

        int foodLevel = player.getFoodData().getFoodLevel();
        int scale = 2;

        if (Config.difficultyScalingEffects) {
            scale = player.level().getDifficulty().getId();
        }

        applySlowness(player, healthPercent, foodLevel, scale);
        applyMiningFatigue(player, healthPercent, foodLevel, scale);
        applyWeakness(player, healthPercent, foodLevel, scale);

        if ((Config.addLowHungerNausea && foodLevel <= 1) || (Config.addLowHealthNausea && healthPercent <= 0.05F)) {
            add(player, MobEffects.CONFUSION, 79, 0);
        }
    }

    // walking was free since vanilla 1.11, this brings back the old cost
    private static void applyWalkExhaustion(Player player) {
        float walked = player.walkDist;
        Float last = LAST_WALK_DIST.put(player, walked);

        if (last == null || Config.walkExhaustionPerBlock <= 0.0F) {
            return;
        }

        float delta = walked - last;

        // sprinting and swimming already cost hunger, riding and flying are free
        if (delta <= 0.0F || !player.onGround() || player.isSprinting() || player.isSwimming()
                || player.isPassenger() || player.getAbilities().flying) {
            return;
        }

        player.causeFoodExhaustion(delta * Config.walkExhaustionPerBlock);
    }

    private static void applySlowness(Player player, float healthPercent, int foodLevel, int scale) {
        if (!Config.addLowHealthSlowness && !Config.addLowHungerSlowness) {
            return;
        }

        int amplifier = tier(healthPercent, foodLevel, scale, Config.addLowHungerSlowness, Config.addLowHealthSlowness);

        if (amplifier >= 0) {
            add(player, MobEffects.MOVEMENT_SLOWDOWN, 19, amplifier);
        }
    }

    private static void applyMiningFatigue(Player player, float healthPercent, int foodLevel, int scale) {
        if (!Config.addLowHealthMiningSlowdown && !Config.addLowHungerMiningSlowdown) {
            return;
        }

        int amplifier = tier(healthPercent, foodLevel, scale,
                Config.addLowHungerMiningSlowdown, Config.addLowHealthMiningSlowdown);

        if (amplifier >= 0) {
            add(player, MobEffects.DIG_SLOWDOWN, 19, amplifier);
        }
    }

    // weakness only kicks in on the three worst tiers, and only at higher difficulties
    private static void applyWeakness(Player player, float healthPercent, int foodLevel, int scale) {
        if (!Config.addLowHealthWeakness && !Config.addLowHungerWeakness) {
            return;
        }

        boolean hunger = Config.addLowHungerWeakness;
        boolean health = Config.addLowHealthWeakness;

        if (((hunger && foodLevel <= 1) || (health && healthPercent <= 0.05F)) && scale >= 1) {
            add(player, MobEffects.WEAKNESS, 19, scale - 1);
        } else if (((hunger && foodLevel <= 2) || (health && healthPercent <= 0.10F)) && scale >= 2) {
            add(player, MobEffects.WEAKNESS, 19, scale - 2);
        } else if (((hunger && foodLevel <= 3) || (health && healthPercent <= 0.15F)) && scale >= 3) {
            add(player, MobEffects.WEAKNESS, 19, scale - 3);
        }
    }

    // five severity bands; the milder ones only apply once difficulty is high enough
    private static int tier(float healthPercent, int foodLevel, int scale, boolean hunger, boolean health) {
        if ((hunger && foodLevel <= 1) || (health && healthPercent <= 0.05F)) {
            return scale + 1;
        }

        if ((hunger && foodLevel <= 2) || (health && healthPercent <= 0.10F)) {
            return scale;
        }

        if (((hunger && foodLevel <= 3) || (health && healthPercent <= 0.15F)) && scale >= 1) {
            return scale - 1;
        }

        if (((hunger && foodLevel <= 4) || (health && healthPercent <= 0.20F)) && scale >= 2) {
            return scale - 2;
        }

        if (((hunger && foodLevel <= 5) || (health && healthPercent <= 0.25F)) && scale >= 3) {
            return scale - 3;
        }

        return -1;
    }

    private static void add(Player player, MobEffect effect, int duration, int amplifier) {
        player.addEffect(new MobEffectInstance(effect, duration, Math.max(0, amplifier), true, true));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!Config.enableRespawnHunger || event.isEndConquered()) {
            return;
        }

        Player player = event.getEntity();
        int hunger = Config.respawnHungerValue;
        Difficulty difficulty = player.level().getDifficulty();

        if (Config.difficultyScalingRespawnHunger && difficulty.getId() > Difficulty.EASY.getId()) {
            hunger -= (difficulty.getId() - 1) * Config.respawnHungerDifficultyModifier;
        }

        FoodData data = player.getFoodData();
        data.setFoodLevel(Math.min(Math.max(hunger, 1), 20));

        if (data.getSaturationLevel() > data.getFoodLevel()) {
            data.setSaturation(data.getFoodLevel());
        }
    }

    // taking a hit restarts the regen timer, same as the 1.12 version did through AppleCore
    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player) {
            ((FoodDataAccess) player.getFoodData()).hungeroverhauled$setTickTimer(0);
        }
    }
}
