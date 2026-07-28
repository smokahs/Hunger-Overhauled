package io.github.smokahs.hungeroverhauled.growth;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;
import io.github.smokahs.hungeroverhauled.config.Config;
import io.github.smokahs.hungeroverhauled.util.RandomHelper;

@Mod.EventBusSubscriber(modid = HungerOverhauled.MOD_ID)
public final class PlantGrowthModule {

    private static final Map<Class<? extends Block>, PlantGrowthModification> BY_CLASS = new HashMap<>();

    private static final Map<Block, PlantGrowthModification> BY_BLOCK = new HashMap<>();

    // growth ticks are hot and the class walk is not, so cache it
    private static final Map<Class<? extends Block>, PlantGrowthModification> RESOLVED = new ConcurrentHashMap<>();

    private static final PlantGrowthModification NONE = new PlantGrowthModification();

    private PlantGrowthModule() {
    }

    public static void clear() {
        BY_CLASS.clear();
        BY_BLOCK.clear();
        RESOLVED.clear();
    }

    public static void register(Class<? extends Block> blockClass, PlantGrowthModification modification) {
        BY_CLASS.put(blockClass, modification);
        RESOLVED.clear();
    }

    public static void register(Block block, PlantGrowthModification modification) {
        BY_BLOCK.put(block, modification);
        RESOLVED.clear();
    }

    @Nullable
    public static PlantGrowthModification get(@Nullable Block block) {
        if (block == null) {
            return null;
        }

        PlantGrowthModification exact = BY_BLOCK.get(block);

        return exact != null ? exact : get(block.getClass());
    }

    @Nullable
    public static PlantGrowthModification get(Class<? extends Block> blockClass) {
        PlantGrowthModification cached = RESOLVED.get(blockClass);

        if (cached != null) {
            return cached == NONE ? null : cached;
        }

        PlantGrowthModification found = BY_CLASS.get(blockClass);

        if (found == null) {
            // walk up to the most specific registered supertype so modded crops inherit their base class' settings
            Class<?> bestMatch = null;

            for (Map.Entry<Class<? extends Block>, PlantGrowthModification> entry : BY_CLASS.entrySet()) {
                if (entry.getKey().isAssignableFrom(blockClass)
                        && (bestMatch == null || bestMatch.isAssignableFrom(entry.getKey()))) {
                    bestMatch = entry.getKey();
                    found = entry.getValue();
                }
            }
        }

        RESOLVED.put(blockClass, found == null ? NONE : found);

        return found;
    }

    @SubscribeEvent
    public static void onCropGrow(BlockEvent.CropGrowEvent.Pre event) {
        PlantGrowthModification modification = get(event.getState().getBlock());

        if (modification == null) {
            return;
        }

        LevelAccessor level = event.getLevel();
        BlockPos pos = event.getPos();

        float sunlightModifier = 1.0F;

        if (modification.needsSunlight && !hasSunlight(level, pos)) {
            sunlightModifier = Config.noSunlightRegrowthMultiplier;
        }

        if (sunlightModifier == 0.0F) {
            event.setResult(Event.Result.DENY);
            return;
        }

        float biomeModifier = biomeModifier(modification, level, pos);

        if (biomeModifier == 0.0F) {
            event.setResult(Event.Result.DENY);
            return;
        }

        float probability = modification.growthTickProbability * biomeModifier * sunlightModifier;

        if (RandomHelper.nextFloat(level.getRandom(), probability) >= 1.0F) {
            event.setResult(Event.Result.DENY);
            return;
        }

        // let the block's own conditions still decide
        event.setResult(Event.Result.DEFAULT);
    }

    private static boolean hasSunlight(LevelAccessor level, BlockPos pos) {
        return level instanceof Level realLevel && realLevel.isDay() && realLevel.canSeeSky(pos);
    }

    private static float biomeModifier(PlantGrowthModification modification, LevelAccessor level, BlockPos pos) {
        if (modification.biomeGrowthModifiers.isEmpty()) {
            return modification.wrongBiomeMultiplier;
        }

        Holder<Biome> biome = level.getBiome(pos);

        for (Map.Entry<TagKey<Biome>, Float> entry : modification.biomeGrowthModifiers.entrySet()) {
            if (biome.is(entry.getKey())) {
                return entry.getValue();
            }
        }

        return modification.wrongBiomeMultiplier;
    }
}
