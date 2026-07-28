package io.github.smokahs.hungeroverhauled.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLPaths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;

// swaps the config to the numbers the new horizons pack ships, keeping the old ones so it can swap back
public final class NewHorizonsPreset {

    private static final String BACKUP_FILE = "newhorizons_backup.json";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private NewHorizonsPreset() {
    }

    // one option the preset takes over, and the value new horizons uses for it
    private record Swap(ForgeConfigSpec.ConfigValue<?> option, Object newHorizons) {
    }

    private static List<Swap> swaps() {
        Config.Common c = Config.COMMON;

        return List.of(
                // growing, new horizons halves the wait where we quadruple it
                new Swap(c.cropRegrowthMultiplier, 2.0D),
                new Swap(c.cactusRegrowthMultiplier, 2.0D),
                new Swap(c.cocoaRegrowthMultiplier, 2.0D),
                new Swap(c.sugarcaneRegrowthMultiplier, 2.0D),
                new Swap(c.treeCropRegrowthMultiplier, 2.0D),
                new Swap(c.saplingRegrowthMultiplier, 2.0D),
                new Swap(c.netherWartRegrowthMultiplier, 2.0D),
                new Swap(c.eggTimeoutMultiplier, 2.0D),
                new Swap(c.breedingTimeoutMultiplier, 2.0D),
                new Swap(c.childDurationMultiplier, 2.0D),

                // bad light and wrong biome sting half as much
                new Swap(c.noSunlightRegrowthMultiplier, 1.0D),
                new Swap(c.wrongBiomeRegrowthMultiplier, 1.0D),
                new Swap(c.wrongBiomeRegrowthMultiplierSugarcane, 1.0D),

                // food is halved instead of quartered, and stacks and eating stay vanilla
                new Swap(c.foodHungerDivider, 2.0D),
                new Swap(c.modifyFoodStackSize, false),
                new Swap(c.modifyFoodEatingSpeed, false),

                // hoes stay craftable and give a few more seeds
                new Swap(c.removeHoeRecipes, false),
                new Swap(c.seedChance, 25),

                // bonemeal works every time
                new Swap(c.bonemealEffectiveness, 1.0D),

                new Swap(c.addTradesButcher, false),
                new Swap(c.chestLootMaxStackSize, 32),

                new Swap(c.minHungerToHeal, 8),

                // the big one, starving is a slap instead of instant death
                new Swap(c.constantHungerLoss, false),
                new Swap(c.damageOnStarve, 2),
                new Swap(c.hungerLossRatePercentage, 100.0D));
    }

    // called on every config load, does nothing unless the toggle moved since last time
    public static void sync() {
        Path backup = backupFile();
        boolean wanted = Config.COMMON.setToNewHorizonsDefaults.get();
        boolean applied = Files.exists(backup);

        if (wanted == applied) {
            return;
        }

        try {
            if (wanted) {
                // save first, so a write failure never eats someone's settings
                writeBackup(backup);
                swaps().forEach(swap -> set(swap.option(), swap.newHorizons()));
                HungerOverhauled.LOGGER.info("Switched {} option(s) to New Horizons values, old ones kept in {}",
                        swaps().size(), backup.getFileName());
            } else {
                readBackup(backup);
                Files.deleteIfExists(backup);
                HungerOverhauled.LOGGER.info("Put your own values back and dropped the New Horizons preset");
            }

            Config.COMMON_SPEC.save();
        } catch (IOException | RuntimeException e) {
            HungerOverhauled.LOGGER.error("Could not switch the New Horizons preset, config left alone", e);
        }
    }

    private static Path backupFile() {
        return FMLPaths.CONFIGDIR.get().resolve(HungerOverhauled.MOD_ID).resolve(BACKUP_FILE);
    }

    private static void writeBackup(Path backup) throws IOException {
        JsonObject json = new JsonObject();

        for (Swap swap : swaps()) {
            Object current = swap.option().get();

            if (current instanceof Boolean value) {
                json.addProperty(key(swap), value);
            } else if (current instanceof Number value) {
                json.addProperty(key(swap), value);
            }
        }

        Files.createDirectories(backup.getParent());
        Files.writeString(backup, GSON.toJson(json), StandardCharsets.UTF_8);
    }

    private static void readBackup(Path backup) throws IOException {
        JsonObject json = JsonParser.parseString(Files.readString(backup, StandardCharsets.UTF_8)).getAsJsonObject();

        for (Swap swap : swaps()) {
            if (!json.has(key(swap))) {
                continue;
            }

            JsonPrimitive stored = json.getAsJsonPrimitive(key(swap));

            // the preset value tells us what type the option wants back
            if (swap.newHorizons() instanceof Boolean) {
                set(swap.option(), stored.getAsBoolean());
            } else if (swap.newHorizons() instanceof Integer) {
                set(swap.option(), stored.getAsInt());
            } else {
                set(swap.option(), stored.getAsDouble());
            }
        }
    }

    private static String key(Swap swap) {
        return String.join(".", swap.option().getPath());
    }

    @SuppressWarnings("unchecked")
    private static void set(ForgeConfigSpec.ConfigValue<?> option, Object value) {
        ((ForgeConfigSpec.ConfigValue<Object>) option).set(value);
    }
}
