package io.github.smokahs.hungeroverhauled.growth;

import java.util.Locale;

import javax.annotation.Nullable;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;
import io.github.smokahs.hungeroverhauled.config.Config;
import io.github.smokahs.hungeroverhauled.food.FoodOverrides;

// the growth table we ship with, keyed by block class so modded crops get it for free
public final class GrowthDefaults {

    private GrowthDefaults() {
    }

    public static void rebuild() {
        PlantGrowthModule.clear();

        PlantGrowthModification crop = new PlantGrowthModification()
                .setNeedsSunlight(true)
                .setGrowthTickProbability(Config.cropRegrowthMultiplier)
                .setBiomeGrowthModifier(BiomeTags.IS_FOREST, 1.0F)
                .setBiomeGrowthModifier(Tags.Biomes.IS_PLAINS, 1.0F);
        PlantGrowthModule.register(CropBlock.class, crop);

        PlantGrowthModification stem = new PlantGrowthModification()
                .setNeedsSunlight(true)
                .setGrowthTickProbability(Config.cropRegrowthMultiplier)
                .setBiomeGrowthModifier(BiomeTags.IS_JUNGLE, 1.0F)
                .setBiomeGrowthModifier(Tags.Biomes.IS_SWAMP, 1.0F);
        PlantGrowthModule.register(StemBlock.class, stem);
        PlantGrowthModule.register(AttachedStemBlock.class, stem);

        PlantGrowthModification reed = new PlantGrowthModification()
                .setNeedsSunlight(true)
                .setGrowthTickProbability(Config.sugarcaneRegrowthMultiplier)
                .setBiomeGrowthModifier(BiomeTags.IS_JUNGLE, 1.0F)
                .setBiomeGrowthModifier(Tags.Biomes.IS_SWAMP, 1.0F)
                .setWrongBiomeMultiplier(Config.wrongBiomeRegrowthMultiplierSugarcane);
        PlantGrowthModule.register(SugarCaneBlock.class, reed);

        PlantGrowthModification cocoa = new PlantGrowthModification()
                .setNeedsSunlight(false)
                .setGrowthTickProbability(Config.cocoaRegrowthMultiplier)
                .setBiomeGrowthModifier(BiomeTags.IS_JUNGLE, 1.0F);
        PlantGrowthModule.register(CocoaBlock.class, cocoa);

        PlantGrowthModification cactus = new PlantGrowthModification()
                .setNeedsSunlight(false)
                .setGrowthTickProbability(Config.cactusRegrowthMultiplier)
                .setBiomeGrowthModifier(Tags.Biomes.IS_SANDY, 1.0F);
        PlantGrowthModule.register(CactusBlock.class, cactus);

        PlantGrowthModification sapling = new PlantGrowthModification()
                .setGrowthTickProbability(Config.saplingRegrowthMultiplier);
        PlantGrowthModule.register(SaplingBlock.class, sapling);

        PlantGrowthModification netherWart = new PlantGrowthModification()
                .setNeedsSunlight(false)
                .setGrowthTickProbability(Config.netherWartRegrowthMultiplier)
                .setBiomeGrowthModifier(BiomeTags.IS_NETHER, 1.0F);
        PlantGrowthModule.register(NetherWartBlock.class, netherWart);

        PlantGrowthModification berryBush = new PlantGrowthModification()
                .setNeedsSunlight(true)
                .setGrowthTickProbability(Config.treeCropRegrowthMultiplier);
        PlantGrowthModule.register(SweetBerryBushBlock.class, berryBush);

        applyJsonOverrides();
    }

    private static void applyJsonOverrides() {
        for (FoodOverrides.GrowthEntry entry : FoodOverrides.jsonGrowth()) {
            ResourceLocation id = ResourceLocation.tryParse(entry.name());

            if (id == null) {
                HungerOverhauled.LOGGER.warn("Skipping growth override with malformed id '{}'", entry.name());
                continue;
            }

            Block block = ForgeRegistries.BLOCKS.getValue(id);

            if (block == null || !ForgeRegistries.BLOCKS.containsKey(id)) {
                // the mod that owns this block simply is not installed
                continue;
            }

            Float probability = probabilityFor(entry.category());

            if (probability == null) {
                HungerOverhauled.LOGGER.warn("Skipping growth override for {}: unknown category '{}'",
                        entry.name(), entry.category());
                continue;
            }

            PlantGrowthModification modification = new PlantGrowthModification()
                    .setNeedsSunlight(entry.needsSunlight() == null || entry.needsSunlight())
                    .setGrowthTickProbability(probability);

            if ("sugarcane".equals(entry.category())) {
                modification.setWrongBiomeMultiplier(Config.wrongBiomeRegrowthMultiplierSugarcane);
            }

            for (String biome : entry.biomes()) {
                TagKey<Biome> tag = biomeTag(biome);

                if (tag != null) {
                    modification.setBiomeGrowthModifier(tag, 1.0F);
                }
            }

            PlantGrowthModule.register(block, modification);
        }
    }

    @Nullable
    private static Float probabilityFor(String category) {
        return switch (category == null ? "" : category.toLowerCase(Locale.ROOT)) {
            case "crop" -> Config.cropRegrowthMultiplier;
            case "flower" -> Config.flowerRegrowthMultiplier;
            case "tree_crop" -> Config.treeCropRegrowthMultiplier;
            case "sapling" -> Config.saplingRegrowthMultiplier;
            case "sugarcane" -> Config.sugarcaneRegrowthMultiplier;
            case "cactus" -> Config.cactusRegrowthMultiplier;
            case "cocoa" -> Config.cocoaRegrowthMultiplier;
            case "nether_wart" -> Config.netherWartRegrowthMultiplier;
            default -> null;
        };
    }

    @Nullable
    private static TagKey<Biome> biomeTag(String raw) {
        ResourceLocation id = ResourceLocation.tryParse(raw.startsWith("#") ? raw.substring(1) : raw);

        return id == null ? null : TagKey.create(Registries.BIOME, id);
    }
}
