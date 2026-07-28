package io.github.smokahs.hungeroverhauled.food;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;

// per item food values, from our vanilla table plus json in config/hungeroverhauled
public final class FoodOverrides {

    public static final String FOLDER_NAME = HungerOverhauled.MOD_ID;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // json wins over the vanilla table
    private static final Map<ResourceLocation, FoodValues> JSON_VALUES = new HashMap<>();

    private static final List<String> JSON_FOOD_BLACKLIST = new ArrayList<>();
    private static final List<String> JSON_DROPS_BLACKLIST = new ArrayList<>();
    private static final List<String> JSON_HARVEST_BLACKLIST = new ArrayList<>();
    private static final List<GrowthEntry> JSON_GROWTH = new ArrayList<>();

    // growth override for one block, category picks the delay multiplier
    public record GrowthEntry(String name, String category, Boolean needsSunlight, List<String> biomes) {
    }

    private static final Map<Item, FoodValues> VANILLA_VALUES = new HashMap<>();

    static {
        vanilla(Items.APPLE, 1, 0.05F);
        vanilla(Items.BREAD, 3, 0.2F);
        vanilla(Items.PORKCHOP, 1, 0.05F);
        vanilla(Items.COOKED_PORKCHOP, 2, 0.15F);
        vanilla(Items.COD, 1, 0.05F);
        vanilla(Items.COOKED_COD, 2, 0.1F);
        vanilla(Items.SALMON, 1, 0.05F);
        vanilla(Items.COOKED_SALMON, 2, 0.1F);
        vanilla(Items.TROPICAL_FISH, 1, 0.05F);
        vanilla(Items.PUFFERFISH, 1, 0.05F);
        vanilla(Items.COOKIE, 1, 0.05F);
        vanilla(Items.MELON_SLICE, 1, 0.05F);
        vanilla(Items.BEEF, 1, 0.05F);
        vanilla(Items.COOKED_BEEF, 2, 0.15F);
        vanilla(Items.CHICKEN, 1, 0.05F);
        vanilla(Items.COOKED_CHICKEN, 2, 0.15F);
        vanilla(Items.MUTTON, 1, 0.05F);
        vanilla(Items.COOKED_MUTTON, 2, 0.15F);
        vanilla(Items.RABBIT, 1, 0.05F);
        vanilla(Items.COOKED_RABBIT, 2, 0.15F);
        vanilla(Items.RABBIT_STEW, 3, 0.15F);
        vanilla(Items.ROTTEN_FLESH, 1, 0.05F);
        vanilla(Items.BAKED_POTATO, 2, 0.15F);
        vanilla(Items.POISONOUS_POTATO, 1, 0.05F);
        vanilla(Items.PUMPKIN_PIE, 3, 0.15F);
        vanilla(Items.MUSHROOM_STEW, 2, 0.1F);
        vanilla(Items.BEETROOT_SOUP, 2, 0.1F);
        vanilla(Items.CARROT, 1, 0.05F);
        vanilla(Items.POTATO, 1, 0.05F);
        vanilla(Items.BEETROOT, 1, 0.05F);
        vanilla(Items.DRIED_KELP, 1, 0.05F);
        vanilla(Items.SWEET_BERRIES, 1, 0.05F);
        vanilla(Items.GLOW_BERRIES, 1, 0.05F);
        vanilla(Items.SPIDER_EYE, 1, 0.05F);
        vanilla(Items.HONEY_BOTTLE, 1, 0.05F);
    }

    private FoodOverrides() {
    }

    private static void vanilla(Item item, int nutrition, float saturationModifier) {
        VANILLA_VALUES.put(item, new FoodValues(nutrition, saturationModifier));
    }

    public static Path folder() {
        return FMLPaths.CONFIGDIR.get().resolve(FOLDER_NAME);
    }

    // rereads every json file, safe to call again later
    public static void reload() {
        JSON_VALUES.clear();
        JSON_FOOD_BLACKLIST.clear();
        JSON_DROPS_BLACKLIST.clear();
        JSON_HARVEST_BLACKLIST.clear();
        JSON_GROWTH.clear();

        Path folder = folder();

        try {
            Files.createDirectories(folder);
            writeExampleIfMissing(folder);
        } catch (IOException e) {
            HungerOverhauled.LOGGER.error("Could not prepare {}", folder, e);
            return;
        }

        try (Stream<Path> files = Files.list(folder)) {
            files.filter(p -> p.getFileName().toString().toLowerCase().endsWith(".json"))
                    .sorted()
                    .forEach(FoodOverrides::readFile);
        } catch (IOException e) {
            HungerOverhauled.LOGGER.error("Could not list {}", folder, e);
        }

        HungerOverhauled.LOGGER.info("Loaded {} food value override(s) from json", JSON_VALUES.size());
    }

    private static void readFile(Path path) {
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonElement root = JsonParser.parseReader(reader);

            if (!root.isJsonObject()) {
                HungerOverhauled.LOGGER.warn("Skipping {}: root element is not an object", path.getFileName());
                return;
            }

            JsonObject obj = root.getAsJsonObject();

            readFoods(obj.getAsJsonArray("foods"), path);
            readNames(obj.getAsJsonArray("foodsBlacklist"), JSON_FOOD_BLACKLIST);
            readNames(obj.getAsJsonArray("dropsBlacklist"), JSON_DROPS_BLACKLIST);
            readNames(obj.getAsJsonArray("harvestBlacklist"), JSON_HARVEST_BLACKLIST);
            readGrowth(obj.getAsJsonArray("growth"));
        } catch (Exception e) {
            HungerOverhauled.LOGGER.error("Failed to read {}", path.getFileName(), e);
        }
    }

    private static void readFoods(JsonArray array, Path path) {
        if (array == null) {
            return;
        }

        for (JsonElement element : array) {
            if (!element.isJsonObject()) {
                continue;
            }

            JsonObject food = element.getAsJsonObject();

            if (!food.has("name")) {
                continue;
            }

            ResourceLocation id = ResourceLocation.tryParse(food.get("name").getAsString());

            if (id == null) {
                HungerOverhauled.LOGGER.warn("Skipping malformed id '{}' in {}", food.get("name"), path.getFileName());
                continue;
            }

            int hunger = food.has("hunger") ? food.get("hunger").getAsInt() : 0;
            float saturation = food.has("saturationModifier") ? food.get("saturationModifier").getAsFloat() : 0.0F;

            JSON_VALUES.put(id, new FoodValues(hunger, saturation));
        }
    }

    private static void readNames(JsonArray array, List<String> target) {
        if (array == null) {
            return;
        }

        for (JsonElement element : array) {
            if (element.isJsonPrimitive()) {
                target.add(element.getAsString());
            } else if (element.isJsonObject() && element.getAsJsonObject().has("name")) {
                // the 1.12 format wrapped these in objects
                target.add(element.getAsJsonObject().get("name").getAsString());
            }
        }
    }

    private static void readGrowth(JsonArray array) {
        if (array == null) {
            return;
        }

        for (JsonElement element : array) {
            if (!element.isJsonObject()) {
                continue;
            }

            JsonObject growth = element.getAsJsonObject();

            if (!growth.has("name") || !growth.has("category")) {
                continue;
            }

            Boolean needsSunlight = growth.has("needsSunlight") ? growth.get("needsSunlight").getAsBoolean() : null;

            List<String> biomes = new ArrayList<>();

            if (growth.has("biomes") && growth.get("biomes").isJsonArray()) {
                for (JsonElement biome : growth.getAsJsonArray("biomes")) {
                    biomes.add(biome.getAsString());
                }
            }

            JSON_GROWTH.add(new GrowthEntry(growth.get("name").getAsString(),
                    growth.get("category").getAsString(), needsSunlight, biomes));
        }
    }

    private static void writeExampleIfMissing(Path folder) throws IOException {
        Path example = folder.resolve("_example.json.txt");

        if (Files.exists(example)) {
            return;
        }

        JsonObject foodExample = new JsonObject();
        foodExample.addProperty("name", "pamhc2foodcore:steamedspinach");
        foodExample.addProperty("hunger", 3);
        foodExample.addProperty("saturationModifier", 0.15F);

        JsonArray foods = new JsonArray();
        foods.add(foodExample);

        JsonArray foodsBlacklist = new JsonArray();
        foodsBlacklist.add("croptopia:anchovy");
        foodsBlacklist.add("#forge:raw_fishes");

        JsonArray dropsBlacklist = new JsonArray();
        dropsBlacklist.add("farmersdelight:rice");

        JsonArray harvestBlacklist = new JsonArray();
        harvestBlacklist.add("farmersdelight:rice");

        JsonObject growthExample = new JsonObject();
        growthExample.addProperty("name", "pamhc2trees:apricotfruit");
        growthExample.addProperty("category", "tree_crop");
        growthExample.addProperty("needsSunlight", true);
        JsonArray growthBiomes = new JsonArray();
        growthBiomes.add("#minecraft:is_jungle");
        growthExample.add("biomes", growthBiomes);

        JsonArray growth = new JsonArray();
        growth.add(growthExample);

        JsonObject root = new JsonObject();
        root.add("foods", foods);
        root.add("foodsBlacklist", foodsBlacklist);
        root.add("dropsBlacklist", dropsBlacklist);
        root.add("harvestBlacklist", harvestBlacklist);
        root.add("growth", growth);

        Files.writeString(example,
                "Rename to anything ending in .json to activate. Every list is optional.\n"
                        + "Blacklist entries accept registry ids and #tags.\n"
                        + "growth categories: crop, flower, tree_crop, sapling, sugarcane, cactus, cocoa, nether_wart\n\n"
                        + GSON.toJson(root),
                StandardCharsets.UTF_8);
    }

    // null means fall through to the dividers
    public static FoodValues lookup(Item item) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);

        if (id != null) {
            FoodValues fromJson = JSON_VALUES.get(id);

            if (fromJson != null) {
                return fromJson;
            }
        }

        return VANILLA_VALUES.get(item);
    }

    public static List<String> jsonFoodBlacklist() {
        return JSON_FOOD_BLACKLIST;
    }

    public static List<String> jsonDropsBlacklist() {
        return JSON_DROPS_BLACKLIST;
    }

    public static List<String> jsonHarvestBlacklist() {
        return JSON_HARVEST_BLACKLIST;
    }

    public static List<GrowthEntry> jsonGrowth() {
        return JSON_GROWTH;
    }
}
