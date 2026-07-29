package io.github.smokahs.hungeroverhauled.config;

import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import org.apache.commons.lang3.tuple.Pair;

// all the old 1.12 options, baked into plain fields since they get read every tick
public final class Config {

    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final Client CLIENT;

    static {
        Pair<Common, ForgeConfigSpec> common = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = common.getLeft();
        COMMON_SPEC = common.getRight();

        Pair<Client, ForgeConfigSpec> client = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT = client.getLeft();
        CLIENT_SPEC = client.getRight();
    }

    private Config() {
    }

    // baked values

    // getting seeds
    public static boolean allSeedsEqual;
    public static boolean removeTallGrassSeeds;
    public static boolean modifyHoeUse;
    public static boolean removeHoeRecipes;
    public static int hoeToolDamageMultiplier;
    public static int seedChance;
    public static boolean addSeedsCraftingRecipe;

    // delays
    public static float noSunlightRegrowthMultiplier;
    public static float wrongBiomeRegrowthMultiplier;
    public static float wrongBiomeRegrowthMultiplierSugarcane;
    public static float flowerRegrowthMultiplier;
    public static float cropRegrowthMultiplier;
    public static float cactusRegrowthMultiplier;
    public static float cocoaRegrowthMultiplier;
    public static float sugarcaneRegrowthMultiplier;
    public static float treeCropRegrowthMultiplier;
    public static float saplingRegrowthMultiplier;
    public static float netherWartRegrowthMultiplier;
    public static float eggTimeoutMultiplier;
    public static float breedingTimeoutMultiplier;
    public static float childDurationMultiplier;
    public static int milkedTimeout;

    // harvesting
    public static boolean enableRightClickHarvesting;
    public static boolean modifyCropDropsRightClick;
    public static boolean modifyCropDropsBreak;
    public static int seedsPerHarvestRightClickMin;
    public static int seedsPerHarvestRightClickMax;
    public static int seedsPerHarvestBreakMin;
    public static int seedsPerHarvestBreakMax;
    public static int producePerHarvestRightClickMin;
    public static int producePerHarvestRightClickMax;
    public static int producePerHarvestBreakMin;
    public static int producePerHarvestBreakMax;
    public static float bonemealEffectiveness;
    public static boolean modifyBonemealGrowth;

    // village field
    public static boolean addCustomVillageField;
    public static int fieldNormalWeight;
    public static int fieldReedWeight;
    public static int fieldStemWeight;

    // difficulty scaling
    public static boolean difficultyScaling;
    public static boolean difficultyScalingBoneMeal;
    public static boolean difficultyScalingEffects;
    public static boolean difficultyScalingHealing;
    public static boolean difficultyScalingHunger;
    public static boolean difficultyScalingRespawnHunger;

    // food
    public static boolean modifyFoodValues;
    public static boolean useHOFoodValues;
    public static boolean modifyFoodStackSize;
    public static boolean modifyFoodEatingSpeed;
    public static int foodStackSizeMultiplier;
    public static float foodHungerDivider;
    public static float foodSaturationDivider;
    public static float foodHungerToSaturationDivider;
    public static boolean addWellFedEffect;
    public static float wellFedDurationMultiplier;
    public static float wellFedEffectiveness;
    public static List<? extends String> foodValueBlacklist;

    // trades + loot
    public static boolean addTradesButcher;
    public static boolean addCropTradesFarmer;
    public static boolean addSaplingTradesFarmer;
    public static boolean addFoodChestLoot;
    public static int chestLootMaxStackSize;
    public static float chestLootChance;
    public static int highTierFoodNutrition;
    public static boolean foodsUnplantable;

    // hunger
    public static boolean constantHungerLoss;
    public static int damageOnStarve;
    public static boolean enableRespawnHunger;
    public static int respawnHungerValue;
    public static int respawnHungerDifficultyModifier;
    public static boolean disableHealingHungerDrain;
    public static float hungerLossRatePercentage;

    // low stats
    public static boolean addLowStatEffects;
    public static boolean addLowHealthNausea;
    public static boolean addLowHungerNausea;
    public static boolean addLowHealthSlowness;
    public static boolean addLowHungerSlowness;
    public static boolean addLowHealthWeakness;
    public static boolean addLowHungerWeakness;
    public static boolean addLowHealthMiningSlowdown;
    public static boolean addLowHungerMiningSlowdown;

    // health
    public static int minHungerToHeal;
    public static boolean foodRegensHealth;
    public static int foodHealDivider;
    public static int healthRegenRatePercentage;
    public static boolean modifyRegenRateOnLowHealth;
    public static int lowHealthRegenRateModifier;

    // recipes
    public static List<? extends String> removedRecipes;

    // healing axe
    public static boolean enableHealingAxe;
    public static boolean enableHealingAxeRecipe;

    // gtnh
    public static boolean setToNewHorizonsDefaults;
    public static boolean explodeInhumaneKills;
    public static float inhumaneKillChance;
    public static float inhumaneKillExplosionPower;
    public static boolean inhumaneKillBreaksBlocks;
    public static boolean inhumaneKillDestroysDrops;

    // client
    public static boolean addGuiText;
    public static boolean addFoodTooltips;
    public static boolean addGrowsBestInTooltips;

    public static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == COMMON_SPEC) {
            // may rewrite options, so it has to run before we bake them
            NewHorizonsPreset.sync();
            bakeCommon();
        } else if (event.getConfig().getSpec() == CLIENT_SPEC) {
            bakeClient();
        }
    }

    public static void bakeCommon() {
        Common c = COMMON;

        allSeedsEqual = c.allSeedsEqual.get();
        removeTallGrassSeeds = c.removeTallGrassSeeds.get();
        modifyHoeUse = c.modifyHoeUse.get();
        removeHoeRecipes = c.removeHoeRecipes.get();
        hoeToolDamageMultiplier = c.hoeToolDamageMultiplier.get();
        seedChance = c.seedChance.get();
        addSeedsCraftingRecipe = c.addSeedsCraftingRecipe.get();

        noSunlightRegrowthMultiplier = c.noSunlightRegrowthMultiplier.get().floatValue();
        wrongBiomeRegrowthMultiplier = c.wrongBiomeRegrowthMultiplier.get().floatValue();
        wrongBiomeRegrowthMultiplierSugarcane = c.wrongBiomeRegrowthMultiplierSugarcane.get().floatValue();
        flowerRegrowthMultiplier = c.flowerRegrowthMultiplier.get().floatValue();
        cropRegrowthMultiplier = c.cropRegrowthMultiplier.get().floatValue();
        cactusRegrowthMultiplier = c.cactusRegrowthMultiplier.get().floatValue();
        cocoaRegrowthMultiplier = c.cocoaRegrowthMultiplier.get().floatValue();
        sugarcaneRegrowthMultiplier = c.sugarcaneRegrowthMultiplier.get().floatValue();
        treeCropRegrowthMultiplier = c.treeCropRegrowthMultiplier.get().floatValue();
        saplingRegrowthMultiplier = c.saplingRegrowthMultiplier.get().floatValue();
        netherWartRegrowthMultiplier = c.netherWartRegrowthMultiplier.get().floatValue();
        eggTimeoutMultiplier = c.eggTimeoutMultiplier.get().floatValue();
        breedingTimeoutMultiplier = c.breedingTimeoutMultiplier.get().floatValue();
        childDurationMultiplier = c.childDurationMultiplier.get().floatValue();
        milkedTimeout = c.milkedTimeout.get();

        enableRightClickHarvesting = c.enableRightClickHarvesting.get();
        modifyCropDropsRightClick = c.modifyCropDropsRightClick.get();
        modifyCropDropsBreak = c.modifyCropDropsBreak.get();
        seedsPerHarvestRightClickMin = c.seedsPerHarvestRightClickMin.get();
        seedsPerHarvestRightClickMax = c.seedsPerHarvestRightClickMax.get();
        seedsPerHarvestBreakMin = c.seedsPerHarvestBreakMin.get();
        seedsPerHarvestBreakMax = c.seedsPerHarvestBreakMax.get();
        producePerHarvestRightClickMin = c.producePerHarvestRightClickMin.get();
        producePerHarvestRightClickMax = c.producePerHarvestRightClickMax.get();
        producePerHarvestBreakMin = c.producePerHarvestBreakMin.get();
        producePerHarvestBreakMax = c.producePerHarvestBreakMax.get();
        bonemealEffectiveness = c.bonemealEffectiveness.get().floatValue();
        modifyBonemealGrowth = c.modifyBonemealGrowth.get();

        addCustomVillageField = c.addCustomVillageField.get();
        fieldNormalWeight = c.fieldNormalWeight.get();
        fieldReedWeight = c.fieldReedWeight.get();
        fieldStemWeight = c.fieldStemWeight.get();

        difficultyScaling = c.difficultyScaling.get();
        difficultyScalingBoneMeal = c.difficultyScalingBoneMeal.get() && difficultyScaling;
        difficultyScalingEffects = c.difficultyScalingEffects.get() && difficultyScaling;
        difficultyScalingHealing = c.difficultyScalingHealing.get() && difficultyScaling;
        difficultyScalingHunger = c.difficultyScalingHunger.get() && difficultyScaling;
        difficultyScalingRespawnHunger = c.difficultyScalingRespawnHunger.get() && difficultyScaling;

        modifyFoodValues = c.modifyFoodValues.get();
        useHOFoodValues = c.useHOFoodValues.get();
        modifyFoodStackSize = c.modifyFoodStackSize.get();
        modifyFoodEatingSpeed = c.modifyFoodEatingSpeed.get();
        foodStackSizeMultiplier = c.foodStackSizeMultiplier.get();
        foodHungerDivider = c.foodHungerDivider.get().floatValue();
        foodSaturationDivider = c.foodSaturationDivider.get().floatValue();
        foodHungerToSaturationDivider = c.foodHungerToSaturationDivider.get().floatValue();
        addWellFedEffect = c.addWellFedEffect.get();
        wellFedDurationMultiplier = c.wellFedDurationMultiplier.get().floatValue();
        wellFedEffectiveness = c.wellFedEffectiveness.get().floatValue();
        foodValueBlacklist = c.foodValueBlacklist.get();

        addTradesButcher = c.addTradesButcher.get();
        addCropTradesFarmer = c.addCropTradesFarmer.get();
        addSaplingTradesFarmer = c.addSaplingTradesFarmer.get();
        addFoodChestLoot = c.addFoodChestLoot.get();
        chestLootMaxStackSize = c.chestLootMaxStackSize.get();
        chestLootChance = c.chestLootChance.get().floatValue();
        highTierFoodNutrition = c.highTierFoodNutrition.get();
        foodsUnplantable = c.foodsUnplantable.get();

        constantHungerLoss = c.constantHungerLoss.get();
        damageOnStarve = c.damageOnStarve.get();
        enableRespawnHunger = c.enableRespawnHunger.get();
        respawnHungerValue = c.respawnHungerValue.get();
        respawnHungerDifficultyModifier = c.respawnHungerDifficultyModifier.get();
        disableHealingHungerDrain = c.disableHealingHungerDrain.get();
        hungerLossRatePercentage = c.hungerLossRatePercentage.get().floatValue();

        addLowStatEffects = c.addLowStatEffects.get();
        addLowHealthNausea = c.addLowHealthNausea.get();
        addLowHungerNausea = c.addLowHungerNausea.get();
        addLowHealthSlowness = c.addLowHealthSlowness.get();
        addLowHungerSlowness = c.addLowHungerSlowness.get();
        addLowHealthWeakness = c.addLowHealthWeakness.get();
        addLowHungerWeakness = c.addLowHungerWeakness.get();
        addLowHealthMiningSlowdown = c.addLowHealthMiningSlowdown.get();
        addLowHungerMiningSlowdown = c.addLowHungerMiningSlowdown.get();

        minHungerToHeal = c.minHungerToHeal.get();
        foodRegensHealth = c.foodRegensHealth.get();
        foodHealDivider = c.foodHealDivider.get();
        healthRegenRatePercentage = c.healthRegenRatePercentage.get();
        modifyRegenRateOnLowHealth = c.modifyRegenRateOnLowHealth.get();
        lowHealthRegenRateModifier = c.lowHealthRegenRateModifier.get();

        removedRecipes = c.removedRecipes.get();

        enableHealingAxe = c.enableHealingAxe.get();
        enableHealingAxeRecipe = c.enableHealingAxeRecipe.get() && enableHealingAxe;

        setToNewHorizonsDefaults = c.setToNewHorizonsDefaults.get();
        explodeInhumaneKills = c.explodeInhumaneKills.get();
        inhumaneKillChance = c.inhumaneKillChance.get().floatValue();
        inhumaneKillExplosionPower = c.inhumaneKillExplosionPower.get().floatValue();
        inhumaneKillBreaksBlocks = c.inhumaneKillBreaksBlocks.get();
        inhumaneKillDestroysDrops = c.inhumaneKillDestroysDrops.get();
    }

    public static void bakeClient() {
        addGuiText = CLIENT.addGuiText.get();
        addFoodTooltips = CLIENT.addFoodTooltips.get();
        addGrowsBestInTooltips = CLIENT.addGrowsBestInTooltips.get();
    }

    public static final class Common {

        // getting seeds
        final ForgeConfigSpec.BooleanValue allSeedsEqual;
        final ForgeConfigSpec.BooleanValue removeTallGrassSeeds;
        final ForgeConfigSpec.BooleanValue modifyHoeUse;
        final ForgeConfigSpec.BooleanValue removeHoeRecipes;
        final ForgeConfigSpec.IntValue hoeToolDamageMultiplier;
        final ForgeConfigSpec.IntValue seedChance;
        final ForgeConfigSpec.BooleanValue addSeedsCraftingRecipe;

        // delays
        final ForgeConfigSpec.DoubleValue noSunlightRegrowthMultiplier;
        final ForgeConfigSpec.DoubleValue wrongBiomeRegrowthMultiplier;
        final ForgeConfigSpec.DoubleValue wrongBiomeRegrowthMultiplierSugarcane;
        final ForgeConfigSpec.DoubleValue flowerRegrowthMultiplier;
        final ForgeConfigSpec.DoubleValue cropRegrowthMultiplier;
        final ForgeConfigSpec.DoubleValue cactusRegrowthMultiplier;
        final ForgeConfigSpec.DoubleValue cocoaRegrowthMultiplier;
        final ForgeConfigSpec.DoubleValue sugarcaneRegrowthMultiplier;
        final ForgeConfigSpec.DoubleValue treeCropRegrowthMultiplier;
        final ForgeConfigSpec.DoubleValue saplingRegrowthMultiplier;
        final ForgeConfigSpec.DoubleValue netherWartRegrowthMultiplier;
        final ForgeConfigSpec.DoubleValue eggTimeoutMultiplier;
        final ForgeConfigSpec.DoubleValue breedingTimeoutMultiplier;
        final ForgeConfigSpec.DoubleValue childDurationMultiplier;
        final ForgeConfigSpec.IntValue milkedTimeout;

        // harvesting
        final ForgeConfigSpec.BooleanValue enableRightClickHarvesting;
        final ForgeConfigSpec.BooleanValue modifyCropDropsRightClick;
        final ForgeConfigSpec.BooleanValue modifyCropDropsBreak;
        final ForgeConfigSpec.IntValue seedsPerHarvestRightClickMin;
        final ForgeConfigSpec.IntValue seedsPerHarvestRightClickMax;
        final ForgeConfigSpec.IntValue seedsPerHarvestBreakMin;
        final ForgeConfigSpec.IntValue seedsPerHarvestBreakMax;
        final ForgeConfigSpec.IntValue producePerHarvestRightClickMin;
        final ForgeConfigSpec.IntValue producePerHarvestRightClickMax;
        final ForgeConfigSpec.IntValue producePerHarvestBreakMin;
        final ForgeConfigSpec.IntValue producePerHarvestBreakMax;
        final ForgeConfigSpec.DoubleValue bonemealEffectiveness;
        final ForgeConfigSpec.BooleanValue modifyBonemealGrowth;

        // village field
        final ForgeConfigSpec.BooleanValue addCustomVillageField;
        final ForgeConfigSpec.IntValue fieldNormalWeight;
        final ForgeConfigSpec.IntValue fieldReedWeight;
        final ForgeConfigSpec.IntValue fieldStemWeight;

        // difficulty scaling
        final ForgeConfigSpec.BooleanValue difficultyScaling;
        final ForgeConfigSpec.BooleanValue difficultyScalingBoneMeal;
        final ForgeConfigSpec.BooleanValue difficultyScalingEffects;
        final ForgeConfigSpec.BooleanValue difficultyScalingHealing;
        final ForgeConfigSpec.BooleanValue difficultyScalingHunger;
        final ForgeConfigSpec.BooleanValue difficultyScalingRespawnHunger;

        // food
        final ForgeConfigSpec.BooleanValue modifyFoodValues;
        final ForgeConfigSpec.BooleanValue useHOFoodValues;
        final ForgeConfigSpec.BooleanValue modifyFoodStackSize;
        final ForgeConfigSpec.BooleanValue modifyFoodEatingSpeed;
        final ForgeConfigSpec.IntValue foodStackSizeMultiplier;
        final ForgeConfigSpec.DoubleValue foodHungerDivider;
        final ForgeConfigSpec.DoubleValue foodSaturationDivider;
        final ForgeConfigSpec.DoubleValue foodHungerToSaturationDivider;
        final ForgeConfigSpec.BooleanValue addWellFedEffect;
        final ForgeConfigSpec.DoubleValue wellFedDurationMultiplier;
        final ForgeConfigSpec.DoubleValue wellFedEffectiveness;
        final ForgeConfigSpec.ConfigValue<List<? extends String>> foodValueBlacklist;

        // trades + loot
        final ForgeConfigSpec.BooleanValue addTradesButcher;
        final ForgeConfigSpec.BooleanValue addCropTradesFarmer;
        final ForgeConfigSpec.BooleanValue addSaplingTradesFarmer;
        final ForgeConfigSpec.BooleanValue addFoodChestLoot;
        final ForgeConfigSpec.IntValue chestLootMaxStackSize;
        final ForgeConfigSpec.DoubleValue chestLootChance;
        final ForgeConfigSpec.IntValue highTierFoodNutrition;
        final ForgeConfigSpec.BooleanValue foodsUnplantable;

        // hunger
        final ForgeConfigSpec.BooleanValue constantHungerLoss;
        final ForgeConfigSpec.IntValue damageOnStarve;
        final ForgeConfigSpec.BooleanValue enableRespawnHunger;
        final ForgeConfigSpec.IntValue respawnHungerValue;
        final ForgeConfigSpec.IntValue respawnHungerDifficultyModifier;
        final ForgeConfigSpec.BooleanValue disableHealingHungerDrain;
        final ForgeConfigSpec.DoubleValue hungerLossRatePercentage;

        // low stats
        final ForgeConfigSpec.BooleanValue addLowStatEffects;
        final ForgeConfigSpec.BooleanValue addLowHealthNausea;
        final ForgeConfigSpec.BooleanValue addLowHungerNausea;
        final ForgeConfigSpec.BooleanValue addLowHealthSlowness;
        final ForgeConfigSpec.BooleanValue addLowHungerSlowness;
        final ForgeConfigSpec.BooleanValue addLowHealthWeakness;
        final ForgeConfigSpec.BooleanValue addLowHungerWeakness;
        final ForgeConfigSpec.BooleanValue addLowHealthMiningSlowdown;
        final ForgeConfigSpec.BooleanValue addLowHungerMiningSlowdown;

        // health
        final ForgeConfigSpec.IntValue minHungerToHeal;
        final ForgeConfigSpec.BooleanValue foodRegensHealth;
        final ForgeConfigSpec.IntValue foodHealDivider;
        final ForgeConfigSpec.IntValue healthRegenRatePercentage;
        final ForgeConfigSpec.BooleanValue modifyRegenRateOnLowHealth;
        final ForgeConfigSpec.IntValue lowHealthRegenRateModifier;

        // recipes
        final ForgeConfigSpec.ConfigValue<List<? extends String>> removedRecipes;

        // healing axe
        final ForgeConfigSpec.BooleanValue enableHealingAxe;
        final ForgeConfigSpec.BooleanValue enableHealingAxeRecipe;

        // gtnh
        final ForgeConfigSpec.BooleanValue setToNewHorizonsDefaults;
        final ForgeConfigSpec.BooleanValue explodeInhumaneKills;
        final ForgeConfigSpec.DoubleValue inhumaneKillChance;
        final ForgeConfigSpec.DoubleValue inhumaneKillExplosionPower;
        final ForgeConfigSpec.BooleanValue inhumaneKillBreaksBlocks;
        final ForgeConfigSpec.BooleanValue inhumaneKillDestroysDrops;

        Common(ForgeConfigSpec.Builder b) {
            b.comment("Options for obtaining seeds").push("getting seeds");
            allSeedsEqual = b
                    .comment("Each seed has an equal chance to drop (grass drops and via hoes)")
                    .define("allSeedsEqual", true);
            removeTallGrassSeeds = b
                    .comment("Removes seed drops when breaking tall grass")
                    .define("removeTallGrassSeeds", true);
            modifyHoeUse = b
                    .comment("Changes the use of hoes depending on the availability of water")
                    .define("modifyHoeUse", true);
            removeHoeRecipes = b
                    .comment("Whether wood and stone hoe recipes are removed")
                    .define("removeHoeRecipes", true);
            hoeToolDamageMultiplier = b
                    .comment("Multiplier on tool damage taken when a hoe is used ('modifyHoeUse' must be true)")
                    .defineInRange("hoeToolDamageMultiplier", 5, 1, Integer.MAX_VALUE);
            seedChance = b
                    .comment("Percent chance for a seed to drop from hoe use on normal difficulty ('modifyHoeUse' must be true)")
                    .defineInRange("seedChance", 20, 0, 100);
            addSeedsCraftingRecipe = b
                    .comment("Adds a crafting recipe to turn 1 wheat into 1 seed")
                    .define("addSeedsCraftingRecipe", true);
            b.pop();

            b.comment("Delays for various food-related activities").push("delays");
            noSunlightRegrowthMultiplier = b
                    .comment("Multiplier on crop growth time without sunlight (1 to disable, 0 to make crops only grow in sunlight)")
                    .defineInRange("noSunlightRegrowthMultiplier", 2.0D, 0.0D, Double.MAX_VALUE);
            wrongBiomeRegrowthMultiplier = b
                    .comment("Multiplier on crop growth time (except sugarcane) in the wrong biome (1 to disable, 0 to only grow in the correct biome)")
                    .defineInRange("wrongBiomeRegrowthMultiplier", 2.0D, 0.0D, Double.MAX_VALUE);
            wrongBiomeRegrowthMultiplierSugarcane = b
                    .comment("Multiplier on sugarcane growth time in the wrong biome (1 to disable, 0 to only grow in the correct biome)")
                    .defineInRange("wrongBiomeRegrowthMultiplierSugarcane", 2.0D, 0.0D, Double.MAX_VALUE);
            flowerRegrowthMultiplier = b
                    .comment("Multiplier on the time it takes a flower crop to grow")
                    .defineInRange("flowerRegrowthMultiplier", 1.0D, 0.0D, Double.MAX_VALUE);
            cropRegrowthMultiplier = b
                    .comment("Multiplier on the time it takes a non-tree crop to grow")
                    .defineInRange("cropRegrowthMultiplier", 4.0D, 0.0D, Double.MAX_VALUE);
            cactusRegrowthMultiplier = b
                    .comment("Multiplier on the time it takes cactus to grow")
                    .defineInRange("cactusRegrowthMultiplier", 4.0D, 0.0D, Double.MAX_VALUE);
            cocoaRegrowthMultiplier = b
                    .comment("Multiplier on the time it takes cocoa to grow")
                    .defineInRange("cocoaRegrowthMultiplier", 4.0D, 0.0D, Double.MAX_VALUE);
            sugarcaneRegrowthMultiplier = b
                    .comment("Multiplier on the time it takes sugarcane to grow")
                    .defineInRange("sugarcaneRegrowthMultiplier", 4.0D, 0.0D, Double.MAX_VALUE);
            treeCropRegrowthMultiplier = b
                    .comment("Multiplier on the time it takes a tree crop (fruit on a log/leaf block) to grow")
                    .defineInRange("treeCropRegrowthMultiplier", 4.0D, 0.0D, Double.MAX_VALUE);
            saplingRegrowthMultiplier = b
                    .comment("Multiplier on the time it takes a sapling to grow into a tree")
                    .defineInRange("saplingRegrowthMultiplier", 4.0D, 0.0D, Double.MAX_VALUE);
            netherWartRegrowthMultiplier = b
                    .comment("Multiplier on the time it takes nether wart to grow")
                    .defineInRange("netherWartRegrowthMultiplier", 4.0D, 0.0D, Double.MAX_VALUE);
            eggTimeoutMultiplier = b
                    .comment("Multiplier applied to the delay between chicken egg laying")
                    .defineInRange("eggTimeoutMultiplier", 4.0D, 0.0D, Double.MAX_VALUE);
            breedingTimeoutMultiplier = b
                    .comment("Multiplier applied to the delay between breeding entities")
                    .defineInRange("breedingTimeoutMultiplier", 4.0D, 0.0D, Double.MAX_VALUE);
            childDurationMultiplier = b
                    .comment("Multiplier applied to the delay before children become adults")
                    .defineInRange("childDurationMultiplier", 4.0D, 0.0D, Double.MAX_VALUE);
            milkedTimeout = b
                    .comment("Delay (in minutes) after milking a cow before it can be milked again (0 to disable)")
                    .defineInRange("milkedTimeout", 20, 0, Integer.MAX_VALUE);
            b.pop();

            b.comment("Options related to drops from crops").push("harvesting");
            enableRightClickHarvesting = b
                    .comment("Enables harvesting crops by right clicking them")
                    .define("enableRightClickHarvesting", true);
            modifyCropDropsRightClick = b
                    .comment("Enables modification of the item drops of crops when right clicking them (produce and seeds)")
                    .define("modifyCropDropsRightClick", true);
            modifyCropDropsBreak = b
                    .comment("Enables modification of the item drops of crops when breaking them (produce and seeds)")
                    .define("modifyCropDropsBreak", true);
            seedsPerHarvestRightClickMin = b
                    .comment("Minimum seeds from harvesting a non-tree crop with right click")
                    .defineInRange("seedsPerHarvestRightClickMin", 0, 0, Integer.MAX_VALUE);
            seedsPerHarvestRightClickMax = b
                    .comment("Maximum seeds from harvesting a non-tree crop with right click")
                    .defineInRange("seedsPerHarvestRightClickMax", 0, 0, Integer.MAX_VALUE);
            seedsPerHarvestBreakMin = b
                    .comment("Minimum seeds from harvesting a non-tree crop by breaking it")
                    .defineInRange("seedsPerHarvestBreakMin", 0, 0, Integer.MAX_VALUE);
            seedsPerHarvestBreakMax = b
                    .comment("Maximum seeds from harvesting a non-tree crop by breaking it")
                    .defineInRange("seedsPerHarvestBreakMax", 0, 0, Integer.MAX_VALUE);
            producePerHarvestRightClickMin = b
                    .comment("Minimum produce from harvesting a non-tree crop with right click")
                    .defineInRange("producePerHarvestRightClickMin", 2, 0, Integer.MAX_VALUE);
            producePerHarvestRightClickMax = b
                    .comment("Maximum produce from harvesting a non-tree crop with right click")
                    .defineInRange("producePerHarvestRightClickMax", 4, 0, Integer.MAX_VALUE);
            producePerHarvestBreakMin = b
                    .comment("Minimum produce from harvesting a non-tree crop by breaking it")
                    .defineInRange("producePerHarvestBreakMin", 2, 0, Integer.MAX_VALUE);
            producePerHarvestBreakMax = b
                    .comment("Maximum produce from harvesting a non-tree crop by breaking it")
                    .defineInRange("producePerHarvestBreakMax", 4, 0, Integer.MAX_VALUE);
            bonemealEffectiveness = b
                    .comment("Multiplier on the effectiveness of bonemeal; the smaller this is, the more often bonemeal fails. Set to 0 to disable bonemeal completely.")
                    .defineInRange("bonemealEffectiveness", 0.5D, 0.0D, 1.0D);
            modifyBonemealGrowth = b
                    .comment("Reduces the amount of growth from a successful bonemeal on certain plants (uses IguanaMan's opinionated values)")
                    .define("modifyBonemealGrowth", true);
            b.pop();

            b.comment("Options for enabling and manipulating a custom field in villages").push("custom field");
            addCustomVillageField = b
                    .comment("Adds a custom field to villages")
                    .define("addCustomVillageField", true);
            fieldNormalWeight = b
                    .comment("Weighted chance for the custom field to contain 'normal' crops")
                    .defineInRange("fieldNormalWeight", 70, 0, Integer.MAX_VALUE);
            fieldReedWeight = b
                    .comment("Weighted chance for the custom field to contain reeds")
                    .defineInRange("fieldReedWeight", 10, 0, Integer.MAX_VALUE);
            fieldStemWeight = b
                    .comment("Weighted chance for the custom field to contain pumpkins/melons")
                    .defineInRange("fieldStemWeight", 10, 0, Integer.MAX_VALUE);
            b.pop();

            b.comment("Options to scale the difficulty of certain elements based on game difficulty").push("difficulty scaling");
            difficultyScaling = b
                    .comment("Enable/disable all difficulty scaling effects in one setting")
                    .define("difficultyScaling", true);
            difficultyScalingBoneMeal = b
                    .comment("Effects of bone meal depend on difficulty ('difficultyScaling' and 'modifyBonemealGrowth' must be true)")
                    .define("difficultyScalingBoneMeal", true);
            difficultyScalingEffects = b
                    .comment("Negative effects on low health/hunger scale by difficulty ('difficultyScaling' must be true)")
                    .define("difficultyScalingEffects", true);
            difficultyScalingHealing = b
                    .comment("Healing rate scales by difficulty ('difficultyScaling' must be true)")
                    .define("difficultyScalingHealing", true);
            difficultyScalingHunger = b
                    .comment("Hunger loss rate scales by difficulty ('difficultyScaling' must be true)")
                    .define("difficultyScalingHunger", true);
            difficultyScalingRespawnHunger = b
                    .comment("Hunger value after respawn is affected by difficulty ('difficultyScaling' must be true)")
                    .define("difficultyScalingRespawnHunger", true);
            b.pop();

            b.comment("Food related options").push("food");
            modifyFoodValues = b
                    .comment("Enables all food value modification")
                    .define("modifyFoodValues", true);
            useHOFoodValues = b
                    .comment("Enables Hunger Overhauled manually setting food values for vanilla items and anything listed in config/hungeroverhauled/*.json ('modifyFoodValues' must be true)")
                    .define("useHOFoodValues", true);
            modifyFoodStackSize = b
                    .comment("Changes the stack size of food to depend on the food's replenishment value")
                    .define("modifyFoodStackSize", true);
            modifyFoodEatingSpeed = b
                    .comment("Changes the eating animation speed to depend on the food's replenishment value")
                    .define("modifyFoodEatingSpeed", true);
            foodStackSizeMultiplier = b
                    .comment("Multiplier on the stack size of food ('modifyFoodStackSize' must be true)")
                    .defineInRange("foodStackSizeMultiplier", 1, 1, 64);
            foodHungerDivider = b
                    .comment("Food values not manually set (see 'useHOFoodValues') have their hunger value divided by this ('modifyFoodValues' must be true)")
                    .defineInRange("foodHungerDivider", 4.0D, 1.0D, Double.MAX_VALUE);
            foodSaturationDivider = b
                    .comment("Food values not manually set have their saturation modifier divided by this.",
                            "Note: applied after 'foodHungerToSaturationDivider'")
                    .defineInRange("foodSaturationDivider", 1.0D, 1.0D, Double.MAX_VALUE);
            foodHungerToSaturationDivider = b
                    .comment("Food values not manually set have their saturation modifier set to <modified hunger> divided by this.",
                            "Set to 0 to keep the original saturation modifier instead.")
                    .defineInRange("foodHungerToSaturationDivider", 20.0D, 0.0D, Double.MAX_VALUE);
            addWellFedEffect = b
                    .comment("Adds a 'well fed' effect that gives slight health regen")
                    .define("addWellFedEffect", true);
            wellFedDurationMultiplier = b
                    .comment("The base duration (in ticks) for the well fed effect is (hunger_value * 100)^1.2, multiplied by this value")
                    .defineInRange("wellFedDurationMultiplier", 1.0D, 0.0D, Double.MAX_VALUE);
            wellFedEffectiveness = b
                    .comment("How much faster the well fed effect makes health regen. 0.25 means health regens 25% faster; 1 means maximum speed.")
                    .defineInRange("wellFedEffectiveness", 0.25D, 0.0D, 1.0D);
            foodValueBlacklist = b
                    .comment("Items whose food values Hunger Overhauled must not touch.",
                            "Accepts item ids (modid:item) and item tags (#modid:tag).")
                    .defineList("foodValueBlacklist", List.of("minecraft:golden_apple",
                            "minecraft:enchanted_golden_apple",
                            "minecraft:golden_carrot",
                            "minecraft:chorus_fruit",
                            "minecraft:suspicious_stew"), o -> o instanceof String);
            b.pop();

            b.comment("Adding food to villager trades and chest loot").push("trades and loot");
            addTradesButcher = b
                    .comment("Add high tier foods to the items butcher villagers will sell")
                    .define("addTradesButcher", true);
            addCropTradesFarmer = b
                    .comment("Add crop produce to the items farmer villagers will buy")
                    .define("addCropTradesFarmer", true);
            addSaplingTradesFarmer = b
                    .comment("Add saplings to the items farmer villagers will sell")
                    .define("addSaplingTradesFarmer", true);
            addFoodChestLoot = b
                    .comment("High tier food added to dungeon/mineshaft/temple chests")
                    .define("addFoodChestLoot", true);
            chestLootMaxStackSize = b
                    .comment("Max stack size for food found in chests ('addFoodChestLoot' must be true)")
                    .defineInRange("chestLootMaxStackSize", 16, 1, 64);
            chestLootChance = b
                    .comment("Chance for food to be found in a chest ('addFoodChestLoot' must be true)")
                    .defineInRange("chestLootChance", 0.25D, 0.0D, 1.0D);
            highTierFoodNutrition = b
                    .comment("Nutrition (after Hunger Overhauled's changes) an item needs before it counts as 'high tier'",
                            "for butcher trades and chest loot.",
                            "The 1.12 version hardcoded 9, which almost never matched once food values were divided.")
                    .defineInRange("highTierFoodNutrition", 3, 1, 20);
            foodsUnplantable = b
                    .comment("Denies planting edible items listed in the #hungeroverhauled:unplantable_foods tag,",
                            "so a real seed item is required to start those crops. The tag ships empty.")
                    .define("foodsUnplantable", true);
            b.pop();

            b.comment("Options related to hunger").push("hunger");
            constantHungerLoss = b
                    .comment("You lose hunger (very slowly) at all times")
                    .define("constantHungerLoss", true);
            damageOnStarve = b
                    .comment("Amount of damage you take when hunger hits zero (vanilla is 1)")
                    .defineInRange("damageOnStarve", 200, 1, Integer.MAX_VALUE);
            enableRespawnHunger = b
                    .comment("Enable setting hunger after respawning (see 'respawnHungerValue')")
                    .define("enableRespawnHunger", true);
            respawnHungerValue = b
                    .comment("Hunger value set after respawning on peaceful/easy difficulty")
                    .defineInRange("respawnHungerValue", 20, 0, 20);
            respawnHungerDifficultyModifier = b
                    .comment("How much difficulty modifies the hunger value set after respawning ('difficultyScalingRespawnHunger' must be true)")
                    .defineInRange("respawnHungerDifficultyModifier", 4, 0, 20);
            disableHealingHungerDrain = b
                    .comment("Disable the hunger drain when healing that was introduced in vanilla 1.6.2")
                    .define("disableHealingHungerDrain", true);
            hungerLossRatePercentage = b
                    .comment("Speed up or slow down the rate that hunger drops (set to 0 to disable hunger loss entirely)")
                    .defineInRange("hungerLossRatePercentage", 400.0D / 3.0D, 0.0D, Double.MAX_VALUE);
            b.pop();

            b.comment("Options for how to handle the player getting low health/hunger").push("low stats");
            addLowStatEffects = b
                    .comment("Enables all low hunger/health effects")
                    .define("addLowStatEffects", true);
            addLowHealthNausea = b.comment("Nausea effect when health is really low").define("addLowHealthNausea", true);
            addLowHungerNausea = b.comment("Nausea effect when hunger is really low").define("addLowHungerNausea", true);
            addLowHealthSlowness = b.comment("Slowness effect when health is low").define("addLowHealthSlowness", true);
            addLowHungerSlowness = b.comment("Slowness effect when hunger is low").define("addLowHungerSlowness", true);
            addLowHealthWeakness = b.comment("Weakness effect when health is low").define("addLowHealthWeakness", true);
            addLowHungerWeakness = b.comment("Weakness effect when hunger is low").define("addLowHungerWeakness", true);
            addLowHealthMiningSlowdown = b.comment("Mining slowdown effect when health is low").define("addLowHealthMiningSlowdown", true);
            addLowHungerMiningSlowdown = b.comment("Mining slowdown effect when hunger is low").define("addLowHungerMiningSlowdown", true);
            b.pop();

            b.comment("Options related to health").push("health");
            minHungerToHeal = b
                    .comment("Minimum hunger level before healing starts (vanilla is 18)")
                    .defineInRange("minHungerToHeal", 7, 0, 20);
            foodRegensHealth = b
                    .comment("Eating food regenerates health")
                    .define("foodRegensHealth", false);
            foodHealDivider = b
                    .comment("Health food restores is the food value divided by this number ('foodRegensHealth' must be true)")
                    .defineInRange("foodHealDivider", 4, 1, Integer.MAX_VALUE);
            healthRegenRatePercentage = b
                    .comment("Speed up or slow down the rate that health regenerates (0 to disable regen)")
                    .defineInRange("healthRegenRatePercentage", 100, 0, Integer.MAX_VALUE);
            modifyRegenRateOnLowHealth = b
                    .comment("The lower your health the longer it takes to regen")
                    .define("modifyRegenRateOnLowHealth", true);
            lowHealthRegenRateModifier = b
                    .comment("Strength of 'modifyRegenRateOnLowHealth' (lower = less effect)")
                    .defineInRange("lowHealthRegenRateModifier", 5, 0, Integer.MAX_VALUE);
            b.pop();

            b.comment("Recipe removal").push("recipes");
            removedRecipes = b
                    .comment("Recipe ids removed from the game on world load. Supports '*' as a trailing wildcard.",
                            "Wood and stone hoes are handled by 'removeHoeRecipes' and do not need an entry here.")
                    .defineList("removedRecipes", Arrays.asList(new String[0]), o -> o instanceof String);
            b.pop();

            b.comment("Extra Utilities' healing axe, ported from the 1.7.10 New Horizons era.",
                    "Left click a mob to heal it 4 health for 3.5 of your own, or to hit an undead one for 16.",
                    "Left clicking a zombie villager cures it on the spot. Holding the axe feeds you slowly,",
                    "but every swing costs 10 exhaustion, so swinging it is not free food.",
                    "The axe never wears out and never lands a normal hit.").push("healing axe");
            enableHealingAxe = b
                    .comment("Puts the healing axe in the creative tabs and recipe viewers.",
                            "The item is always registered so worlds holding one keep loading; this only",
                            "decides whether it is reachable. Takes a restart to show up.")
                    .define("enableHealingAxe", false);
            enableHealingAxeRecipe = b
                    .comment("Adds a crafting recipe for the axe: netherite ingot + nether star over",
                            "netherite ingot + stick over stick ('enableHealingAxe' must be true)")
                    .define("enableHealingAxeRecipe", false);
            b.pop();

            b.comment("Settings borrowed from GregTech: New Horizons").push("GTNH");
            setToNewHorizonsDefaults = b
                    .comment("Swaps this config over to the values the New Horizons pack ships.",
                            "Turning it on saves whatever you had first, so turning it off again puts your own",
                            "numbers back exactly as they were.",
                            "Careful: these are softer than our defaults, not harder. Crops grow twice as fast,",
                            "food is halved instead of quartered, and starving does 2 damage instead of 200.",
                            "New Horizons does that on purpose because Spice of Life carries the food difficulty",
                            "over there, so only use this preset if you run Spice of Life too. On its own it",
                            "just makes the game easier.")
                    .define("setToNewHorizonsDefaults", false);
            explodeInhumaneKills = b
                    .comment("Animals have a small chance to explode when not slaughtered humanely (with a knife!).",
                            "Anything in the #hungeroverhauled:humane_slaughter_tools tag counts as a knife,",
                            "which already covers Farmer's Delight knives and everything that copies them.")
                    .define("explodeInhumaneKills", false);
            inhumaneKillChance = b
                    .comment("Chance for an animal killed without a knife to go off ('explodeInhumaneKills' must be true)")
                    .defineInRange("inhumaneKillChance", 0.05D, 0.0D, 1.0D);
            inhumaneKillExplosionPower = b
                    .comment("Size of the blast. TNT is 4, a creeper is 3, 1.5 still hurts lol.")
                    .defineInRange("inhumaneKillExplosionPower", 1.5D, 0.0D, 10.0D);
            inhumaneKillBreaksBlocks = b
                    .comment("Whether the blast breaks blocks.")
                    .define("inhumaneKillBreaksBlocks", false);
            inhumaneKillDestroysDrops = b
                    .comment("Whether the meat is lost in the blast.")
                    .define("inhumaneKillDestroysDrops", true);
            b.pop();
        }
    }

    public static final class Client {

        final ForgeConfigSpec.BooleanValue addGuiText;
        final ForgeConfigSpec.BooleanValue addFoodTooltips;
        final ForgeConfigSpec.BooleanValue addGrowsBestInTooltips;

        Client(ForgeConfigSpec.Builder b) {
            b.push("display");
            addGuiText = b
                    .comment("Shows onscreen text when hunger/health is low")
                    .define("addGuiText", true);
            addFoodTooltips = b
                    .comment("Add tooltips to food items hinting at their food value")
                    .define("addFoodTooltips", true);
            addGrowsBestInTooltips = b
                    .comment("Add 'crop grows best in' tooltips to seeds and plantables")
                    .define("addGrowsBestInTooltips", true);
            b.pop();
        }
    }
}
