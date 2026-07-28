package io.github.smokahs.hungeroverhauled.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;

import com.google.gson.JsonElement;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;
import io.github.smokahs.hungeroverhauled.config.Config;

public final class RecipeFilter {

    private RecipeFilter() {
    }

    public static void filter(Map<ResourceLocation, JsonElement> recipes) {
        if (!Config.COMMON_SPEC.isLoaded()) {
            return;
        }

        List<String> patterns = new ArrayList<>();

        if (Config.removeHoeRecipes) {
            patterns.add("minecraft:wooden_hoe");
            patterns.add("minecraft:stone_hoe");
        }

        if (!Config.addSeedsCraftingRecipe) {
            patterns.add(HungerOverhauled.MOD_ID + ":wheat_seeds_from_wheat");
        }

        if (Config.removedRecipes != null) {
            patterns.addAll(Config.removedRecipes);
        }

        if (patterns.isEmpty()) {
            return;
        }

        int before = recipes.size();
        recipes.keySet().removeIf(id -> matches(patterns, id.toString()));
        int removed = before - recipes.size();

        if (removed > 0) {
            HungerOverhauled.LOGGER.info("Removed {} recipe(s)", removed);
        }
    }

    private static boolean matches(List<String> patterns, String id) {
        for (String pattern : patterns) {
            if (pattern.endsWith("*")) {
                if (id.startsWith(pattern.substring(0, pattern.length() - 1))) {
                    return true;
                }
            } else if (id.equals(pattern)) {
                return true;
            }
        }

        return false;
    }
}
