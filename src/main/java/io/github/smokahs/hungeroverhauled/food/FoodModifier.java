package io.github.smokahs.hungeroverhauled.food;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import com.mojang.datafixers.util.Pair;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;
import io.github.smokahs.hungeroverhauled.config.Config;
import io.github.smokahs.hungeroverhauled.mixin.ItemAccessor;
import io.github.smokahs.hungeroverhauled.util.Matchers;

// rewrites food values and stack sizes on the items, originals kept so reload works
public final class FoodModifier {

    private static final Map<Item, FoodProperties> ORIGINAL_FOOD = new HashMap<>();
    private static final Map<Item, Integer> ORIGINAL_STACK_SIZE = new HashMap<>();

    // foods that still count as a real meal after the nerf, used by trades and chest loot
    private static final List<Item> HIGH_TIER = new ArrayList<>();

    private static boolean captured;

    private FoodModifier() {
    }

    // reset everything, then reapply the current config
    public static void apply() {
        captureOriginals();
        restoreOriginals();
        HIGH_TIER.clear();

        int changedValues = 0;
        int changedStacks = 0;

        for (Item item : ForgeRegistries.ITEMS) {
            FoodProperties original = ORIGINAL_FOOD.get(item);

            if (original == null) {
                continue;
            }

            FoodValues values = FoodValues.of(original);

            if (Config.modifyFoodValues && !isBlacklisted(item)) {
                FoodValues modified = modify(item, values);

                if (modified.nutrition() != values.nutrition()
                        || modified.saturationModifier() != values.saturationModifier()) {
                    ((ItemAccessor) item).setFoodProperties(rebuild(original, modified));
                    changedValues++;
                }

                values = modified;
            }

            if (Config.modifyFoodStackSize && applyStackSize(item, values)) {
                changedStacks++;
            }

            if (values.nutrition() >= Config.highTierFoodNutrition) {
                HIGH_TIER.add(item);
            }
        }

        HungerOverhauled.LOGGER.info("Adjusted food values on {} item(s) and stack sizes on {} item(s)",
                changedValues, changedStacks);
    }

    public static List<Item> highTierFoods() {
        return Collections.unmodifiableList(HIGH_TIER);
    }

    private static void captureOriginals() {
        if (captured) {
            return;
        }

        for (Item item : ForgeRegistries.ITEMS) {
            FoodProperties food = item.getFoodProperties();

            if (food != null) {
                ORIGINAL_FOOD.put(item, food);
                ORIGINAL_STACK_SIZE.put(item, item.getMaxStackSize());
            }
        }

        captured = true;
    }

    private static void restoreOriginals() {
        ORIGINAL_FOOD.forEach((item, food) -> ((ItemAccessor) item).setFoodProperties(food));
        ORIGINAL_STACK_SIZE.forEach((item, size) -> ((ItemAccessor) item).setMaxStackSize(size));
    }

    private static boolean isBlacklisted(Item item) {
        return Matchers.matchesItem(Config.foodValueBlacklist, item)
                || Matchers.matchesItem(FoodOverrides.jsonFoodBlacklist(), item);
    }

    // explicit table first, otherwise the dividers
    public static FoodValues modify(Item item, FoodValues original) {
        if (Config.useHOFoodValues) {
            FoodValues explicit = FoodOverrides.lookup(item);

            if (explicit != null) {
                return explicit;
            }
        }

        return divide(original);
    }

    private static FoodValues divide(FoodValues original) {
        int nutrition = Math.max(Math.round(original.nutrition() / Config.foodHungerDivider), 1);
        float saturationModifier = original.saturationModifier();

        if (Config.foodHungerToSaturationDivider != 0.0F) {
            saturationModifier = nutrition / Config.foodHungerToSaturationDivider;
        }

        saturationModifier /= Config.foodSaturationDivider;

        return new FoodValues(nutrition, saturationModifier);
    }

    // copy everything but the two numbers we are changing
    private static FoodProperties rebuild(FoodProperties original, FoodValues values) {
        FoodProperties.Builder builder = new FoodProperties.Builder()
                .nutrition(values.nutrition())
                .saturationMod(values.saturationModifier());

        if (original.isMeat()) {
            builder.meat();
        }

        if (original.canAlwaysEat()) {
            builder.alwaysEat();
        }

        if (original.isFastFood()) {
            builder.fast();
        }

        for (Pair<MobEffectInstance, Float> effect : original.getEffects()) {
            MobEffectInstance source = effect.getFirst();

            if (source == null) {
                continue;
            }

            builder.effect(() -> new MobEffectInstance(source.getEffect(), source.getDuration(),
                    source.getAmplifier(), source.isAmbient(), source.isVisible(), source.showIcon()),
                    effect.getSecond());
        }

        return builder.build();
    }

    // true if the stack size changed
    private static boolean applyStackSize(Item item, FoodValues values) {
        int current = ORIGINAL_STACK_SIZE.getOrDefault(item, item.getMaxStackSize());
        int target;

        if (values.nutrition() <= 2) {
            target = 16 * Config.foodStackSizeMultiplier;
        } else if (values.nutrition() <= 5) {
            target = 8 * Config.foodStackSizeMultiplier;
        } else if (values.nutrition() <= 8) {
            target = 4 * Config.foodStackSizeMultiplier;
        } else if (values.nutrition() <= 11) {
            target = 2 * Config.foodStackSizeMultiplier;
        } else {
            target = Config.foodStackSizeMultiplier;
        }

        // rotten flesh is trade fodder, keep it stackable
        if (item == net.minecraft.world.item.Items.ROTTEN_FLESH) {
            target = Math.max(8 * Config.foodStackSizeMultiplier, 40);
        }

        target = Math.max(1, Math.min(64, target));

        if (current > target) {
            ((ItemAccessor) item).setMaxStackSize(target);
            return true;
        }

        return false;
    }
}
