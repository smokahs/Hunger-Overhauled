package io.github.smokahs.hungeroverhauled.food;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;
import io.github.smokahs.hungeroverhauled.compat.SolPotPie;
import io.github.smokahs.hungeroverhauled.config.Config;
import io.github.smokahs.hungeroverhauled.registry.ModEffects;

@Mod.EventBusSubscriber(modid = HungerOverhauled.MOD_ID)
public final class EatingEvents {

    private EatingEvents() {
    }

    // big meals take longer to chew
    @SubscribeEvent
    public static void onUseStart(LivingEntityUseItemEvent.Start event) {
        if (!Config.modifyFoodEatingSpeed) {
            return;
        }

        FoodProperties food = food(event.getItem(), event.getEntity() instanceof Player player ? player : null);

        if (food != null && food.getNutrition() > 0) {
            event.setDuration(food.getNutrition() * 8 + 8);
        }
    }

    // runs before spice of life so we can ask what the meal is worth before it bumps its own counters
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide()) {
            return;
        }

        FoodProperties food = food(event.getItem(), player);

        if (food == null) {
            return;
        }

        // a food you have been spamming fills less, so it should reward less too
        int nutrition = SolPotPie.mealNutrition(player, event.getItem().getItem(), food);

        boolean naturalRegen = player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);

        if (Config.addWellFedEffect && naturalRegen && Config.healthRegenRatePercentage > 0) {
            int duration = (int) (Math.pow(nutrition * 100.0D, 1.2D) * Config.wellFedDurationMultiplier);

            if (duration >= 30) {
                MobEffectInstance current = player.getEffect(ModEffects.WELL_FED.get());

                if (current != null) {
                    duration += current.getDuration();
                }

                player.addEffect(new MobEffectInstance(ModEffects.WELL_FED.get(), duration, 0, true, true));
            }
        }

        if (Config.foodRegensHealth) {
            float toHeal = Math.round(nutrition / (float) Config.foodHealDivider);
            float canHeal = player.getMaxHealth() - player.getHealth();

            if (toHeal > canHeal) {
                toHeal = canHeal;
            }

            if (toHeal > 0.0F) {
                player.heal(toHeal);
            }
        }
    }

    private static FoodProperties food(ItemStack stack, Player player) {
        return stack.isEmpty() ? null : stack.getFoodProperties(player);
    }
}
