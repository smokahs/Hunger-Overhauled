package io.github.smokahs.hungeroverhauled.growth;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import io.github.smokahs.hungeroverhauled.config.Config;

// how much slower a plant grows and which biomes it likes, keyed by biome tag
public class PlantGrowthModification {

    public boolean needsSunlight = true;

    public final Map<TagKey<Biome>, Float> biomeGrowthModifiers = new LinkedHashMap<>();

    public float growthTickProbability;

    public float wrongBiomeMultiplier = Config.wrongBiomeRegrowthMultiplier;

    public PlantGrowthModification setNeedsSunlight(boolean needsSunlight) {
        this.needsSunlight = needsSunlight;
        return this;
    }

    public PlantGrowthModification setBiomeGrowthModifier(TagKey<Biome> biomeTag, float growthModifier) {
        this.biomeGrowthModifiers.put(biomeTag, growthModifier);
        return this;
    }

    public PlantGrowthModification setGrowthTickProbability(float growthTickProbability) {
        this.growthTickProbability = growthTickProbability;
        return this;
    }

    public PlantGrowthModification setWrongBiomeMultiplier(float wrongBiomeMultiplier) {
        this.wrongBiomeMultiplier = wrongBiomeMultiplier;
        return this;
    }
}
