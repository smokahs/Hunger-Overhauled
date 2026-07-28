package io.github.smokahs.hungeroverhauled.hunger;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import io.github.smokahs.hungeroverhauled.config.Config;
import io.github.smokahs.hungeroverhauled.registry.ModEffects;

// applecore is gone, so we redo the food tick ourselves
public final class HungerLogic {

    // vanilla exhaustion threshold
    private static final float VANILLA_MAX_EXHAUSTION = 4.0F;

    // vanilla regen period in ticks
    private static final float VANILLA_REGEN_PERIOD = 80.0F;

    private HungerLogic() {
    }

    public static void tick(FoodData data, Player player) {
        Level level = player.level();
        Difficulty difficulty = level.getDifficulty();
        FoodDataAccess access = (FoodDataAccess) data;

        access.hungeroverhauled$setLastFoodLevel(data.getFoodLevel());

        if (Config.hungerLossRatePercentage <= 0.0F) {
            // hunger loss off, pin just below full
            data.setFoodLevel(19);
            data.setSaturation(0.0F);
            data.setExhaustion(0.0F);
        } else {
            float maxExhaustion = maxExhaustionLevel(difficulty);

            if (data.getExhaustionLevel() > maxExhaustion) {
                data.setExhaustion(data.getExhaustionLevel() - maxExhaustion);

                if (data.getSaturationLevel() > 0.0F) {
                    data.setSaturation(Math.max(data.getSaturationLevel() - 1.0F, 0.0F));
                } else {
                    // unlike vanilla, hunger also drains on peaceful
                    data.setFoodLevel(Math.max(data.getFoodLevel() - 1, 0));
                }
            }
        }

        boolean naturalRegen = level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
        int tickTimer = access.hungeroverhauled$getTickTimer();

        if (naturalRegen && data.getSaturationLevel() > 0.0F && player.isHurt() && data.getFoodLevel() >= 20) {
            // vanilla fast regen, left alone
            ++tickTimer;

            if (tickTimer >= 10) {
                float healed = Math.min(data.getSaturationLevel(), 6.0F);
                player.heal(healed / 6.0F);
                addRegenExhaustion(data, healed);
                tickTimer = 0;
            }
        } else if (allowRegen(player, data, naturalRegen)) {
            ++tickTimer;

            if (tickTimer >= regenTickPeriod(player, difficulty)) {
                player.heal(1.0F);
                addRegenExhaustion(data, 6.0F);
                tickTimer = 0;
            }
        } else if (data.getFoodLevel() <= 0) {
            ++tickTimer;

            if (tickTimer >= 80) {
                if (player.getHealth() > 10.0F
                        || difficulty == Difficulty.HARD
                        || (player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL)) {
                    player.hurt(player.damageSources().starve(), Config.damageOnStarve);
                }

                tickTimer = 0;
            }
        } else {
            tickTimer = 0;
        }

        access.hungeroverhauled$setTickTimer(tickTimer);
    }

    // exhaustion needed before a hunger point is spent, lower drains faster
    public static float maxExhaustionLevel(Difficulty difficulty) {
        float maxExhaustion = VANILLA_MAX_EXHAUSTION / (Config.hungerLossRatePercentage / 100.0F);

        if (Config.difficultyScalingHunger) {
            if (difficulty == Difficulty.PEACEFUL) {
                maxExhaustion *= 5.0F / 3.0F;
            } else if (difficulty == Difficulty.EASY) {
                maxExhaustion *= 4.0F / 3.0F;
            }
        }

        return maxExhaustion;
    }

    private static boolean allowRegen(Player player, FoodData data, boolean naturalRegen) {
        return naturalRegen
                && Config.healthRegenRatePercentage > 0
                && data.getFoodLevel() >= Config.minHungerToHeal
                && player.isHurt();
    }

    // ticks between each heal, vanilla is a flat 80
    public static int regenTickPeriod(Player player, Difficulty difficulty) {
        float wellFedModifier = 1.0F;

        if (Config.addWellFedEffect && player.hasEffect(ModEffects.WELL_FED.get())) {
            wellFedModifier = 1.0F - Config.wellFedEffectiveness;
        }

        float difficultyModifier = 1.0F;

        if (Config.difficultyScalingHealing) {
            if (difficulty == Difficulty.PEACEFUL || difficulty == Difficulty.EASY) {
                difficultyModifier = 0.75F;
            } else if (difficulty == Difficulty.HARD) {
                difficultyModifier = 1.5F;
            }
        }

        float lowHealthModifier = 1.0F;

        if (Config.modifyRegenRateOnLowHealth) {
            lowHealthModifier = player.getMaxHealth() - player.getHealth();
            lowHealthModifier *= Config.lowHealthRegenRateModifier / 100.0F;
            lowHealthModifier *= difficultyModifier;
            lowHealthModifier = (float) Math.pow(lowHealthModifier + 1.0F, 1.5F);
        }

        float period = VANILLA_REGEN_PERIOD * difficultyModifier * wellFedModifier * lowHealthModifier
                / (Config.healthRegenRatePercentage / 100.0F);

        return Math.max(1, Math.round(period));
    }

    private static void addRegenExhaustion(FoodData data, float amount) {
        if (!Config.disableHealingHungerDrain) {
            data.addExhaustion(amount);
        }
    }
}
