package io.github.smokahs.hungeroverhauled.compat;

import java.lang.reflect.Method;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.server.ServerLifecycleHooks;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;

// soft link to spice of life pot pie, all reflection so that mod stays optional
public final class SolPotPie {

    public static final String MOD_ID = "solpotpie";

    private static boolean linked;
    private static boolean available;
    private static boolean warned;

    private static Method hideValuesUntilEaten;
    private static Method limitProgressionToSurvival;
    private static Method foodCapability;
    private static Method hasEverEaten;
    private static Method diminish;
    private static Method mealHunger;
    private static Method rebuildScores;
    private static Method syncConfig;

    private SolPotPie() {
    }

    public static boolean isLoaded() {
        link();
        return available;
    }

    // true while spice of life is keeping this food a mystery
    public static boolean hidesUneatenValues(Player player, Item item) {
        if (player == null || !isLoaded()) {
            return false;
        }

        try {
            if (!(boolean) hideValuesUntilEaten.invoke(null)) {
                return false;
            }

            return !(boolean) hasEverEaten.invoke(foodCapability.invoke(null, player), item);
        } catch (Throwable e) {
            // players without the capability land here, and this runs every tooltip frame, so stay quiet
            return false;
        }
    }

    // what the meal is really worth after spice of life takes its cut for eating the same thing again
    public static int mealNutrition(Player player, Item item, FoodProperties food) {
        int nutrition = food.getNutrition();

        if (!isLoaded()) {
            return nutrition;
        }

        try {
            // spice of life leaves non survival meals alone when this is on, so we should too
            if ((boolean) limitProgressionToSurvival.invoke(null)
                    && (!(player instanceof ServerPlayer serverPlayer) || !serverPlayer.gameMode.isSurvival())) {
                return nutrition;
            }

            Object meal = diminish.invoke(foodCapability.invoke(null, player), item, nutrition,
                    nutrition * food.getSaturationModifier() * 2.0F);

            return (int) mealHunger.invoke(meal);
        } catch (Throwable e) {
            warn("diminishing returns", e);
            return nutrition;
        }
    }

    // spice of life scores every food once at server start, our reload can move those numbers under it
    public static void refreshScores() {
        if (!isLoaded()) {
            return;
        }

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        if (server == null) {
            return;
        }

        try {
            rebuildScores.invoke(null, server);

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                syncConfig.invoke(null, player);
            }
        } catch (Throwable e) {
            warn("score refresh", e);
        }
    }

    private static synchronized void link() {
        if (linked) {
            return;
        }

        linked = true;

        if (!ModList.get().isLoaded(MOD_ID)) {
            return;
        }

        try {
            Class<?> config = Class.forName("io.github.smokahs.solpotpie.SOLPotPieConfig");
            hideValuesUntilEaten = config.getMethod("shouldHideValuesUntilEaten");
            limitProgressionToSurvival = config.getMethod("limitProgressionToSurvival");

            foodCapability = Class.forName("io.github.smokahs.solpotpie.api.SOLPotPieAPI")
                    .getMethod("getFoodCapability", Player.class);
            hasEverEaten = Class.forName("io.github.smokahs.solpotpie.api.FoodCapability")
                    .getMethod("hasEverEaten", Item.class);

            diminish = Class.forName("io.github.smokahs.solpotpie.tracking.FoodList")
                    .getMethod("diminish", Item.class, int.class, float.class);
            mealHunger = Class.forName("io.github.smokahs.solpotpie.tracking.FoodList$MealValues")
                    .getMethod("hunger");

            Class<?> handler = Class.forName("io.github.smokahs.solpotpie.ConfigHandler");
            rebuildScores = handler.getMethod("rebuildScores", MinecraftServer.class);
            syncConfig = handler.getMethod("syncConfig", Player.class);

            available = true;
            HungerOverhauled.LOGGER.info("Spice of Life: Pot Pie Edition found, compat on");
        } catch (ReflectiveOperationException e) {
            HungerOverhauled.LOGGER.warn("Spice of Life: Pot Pie Edition is installed but is not shaped how we expect,"
                    + " compat off", e);
        }
    }

    // one line per problem, these sit on paths that run a lot
    private static void warn(String what, Throwable error) {
        if (warned) {
            return;
        }

        warned = true;
        HungerOverhauled.LOGGER.warn("Spice of Life compat failed on {}, using our own values instead", what, error);
    }
}
