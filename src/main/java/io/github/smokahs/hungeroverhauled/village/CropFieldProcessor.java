package io.github.smokahs.hungeroverhauled.village;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import com.mojang.serialization.Codec;

import io.github.smokahs.hungeroverhauled.config.Config;
import io.github.smokahs.hungeroverhauled.registry.ModProcessors;

// rerolls what a village field is growing: ordinary crops, reeds along the water channel, or pumpkin/melon stems
public class CropFieldProcessor extends StructureProcessor {

    public static final CropFieldProcessor INSTANCE = new CropFieldProcessor();

    public static final Codec<CropFieldProcessor> CODEC = Codec.unit(() -> INSTANCE);

    private static final Block[] CROPS = {
            Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES, Blocks.BEETROOTS
    };

    private static final Block[] STEMS = {
            Blocks.PUMPKIN_STEM, Blocks.MELON_STEM
    };

    private enum Mode {
        NORMAL, REED, STEM
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return ModProcessors.CROP_FIELD.get();
    }

    @Override
    public List<StructureTemplate.StructureBlockInfo> finalizeProcessing(
            ServerLevelAccessor level, BlockPos origin, BlockPos pos,
            List<StructureTemplate.StructureBlockInfo> original,
            List<StructureTemplate.StructureBlockInfo> processed,
            StructurePlaceSettings settings) {
        int normal = Math.max(0, Config.fieldNormalWeight);
        int reed = Math.max(0, Config.fieldReedWeight);
        int stem = Math.max(0, Config.fieldStemWeight);
        int total = normal + reed + stem;

        if (!Config.addCustomVillageField || total <= 0) {
            return processed;
        }

        // seeded off the piece position so a regenerated chunk lays out the same field
        RandomSource random = RandomSource.create(Mth.getSeed(origin));
        int roll = random.nextInt(total);
        Mode mode = roll < normal ? Mode.NORMAL : (roll < normal + reed ? Mode.REED : Mode.STEM);

        List<StructureTemplate.StructureBlockInfo> result = new ArrayList<>(processed);

        switch (mode) {
            case NORMAL -> replaceCrops(result, random, CROPS);
            case STEM -> replaceCrops(result, random, STEMS);
            case REED -> plantReeds(result);
        }

        return result;
    }

    private static void replaceCrops(List<StructureTemplate.StructureBlockInfo> processed, RandomSource random,
                                     Block[] options) {
        for (int i = 0; i < processed.size(); i++) {
            StructureTemplate.StructureBlockInfo info = processed.get(i);

            if (!(info.state().getBlock() instanceof CropBlock)) {
                continue;
            }

            Block replacement = options[random.nextInt(options.length)];
            BlockState state = ageLike(replacement, info.state(), random);

            processed.set(i, new StructureTemplate.StructureBlockInfo(info.pos(), state, info.nbt()));
        }
    }

    // keep roughly how grown the original crop was so fields still look part-tended
    private static BlockState ageLike(Block replacement, BlockState original, RandomSource random) {
        int max = replacement instanceof CropBlock crop ? crop.getMaxAge() : StemBlock.MAX_AGE;
        int age;

        if (original.getBlock() instanceof CropBlock originalCrop && originalCrop.getMaxAge() > 0) {
            age = Math.round(ageOf(original, originalCrop) / (float) originalCrop.getMaxAge() * max);
        } else {
            age = random.nextInt(max + 1);
        }

        if (replacement instanceof CropBlock crop) {
            return crop.getStateForAge(Math.min(age, max));
        }

        return replacement.defaultBlockState().setValue(StemBlock.AGE, Math.min(age, StemBlock.MAX_AGE));
    }

    private static int ageOf(BlockState state, CropBlock crop) {
        return state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_7)
                ? state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_7)
                : state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_3)
                ? state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_3)
                : crop.getMaxAge();
    }

    // sugar cane cannot sit on farmland, so the wet rows become dirt with reeds on top
    private static void plantReeds(List<StructureTemplate.StructureBlockInfo> processed) {
        Set<BlockPos> water = new HashSet<>();

        for (StructureTemplate.StructureBlockInfo info : processed) {
            if (info.state().is(Blocks.WATER)) {
                water.add(info.pos());
            }
        }

        if (water.isEmpty()) {
            return;
        }

        Set<BlockPos> converted = new HashSet<>();

        for (int i = 0; i < processed.size(); i++) {
            StructureTemplate.StructureBlockInfo info = processed.get(i);

            if (!info.state().is(Blocks.FARMLAND) || !touchesWater(info.pos(), water)) {
                continue;
            }

            processed.set(i, new StructureTemplate.StructureBlockInfo(info.pos(),
                    Blocks.DIRT.defaultBlockState(), info.nbt()));
            converted.add(info.pos());
        }

        for (int i = 0; i < processed.size(); i++) {
            StructureTemplate.StructureBlockInfo info = processed.get(i);

            if (info.state().getBlock() instanceof CropBlock && converted.contains(info.pos().below())) {
                processed.set(i, new StructureTemplate.StructureBlockInfo(info.pos(),
                        Blocks.SUGAR_CANE.defaultBlockState(), info.nbt()));
            }
        }
    }

    private static boolean touchesWater(BlockPos pos, Set<BlockPos> water) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (water.contains(pos.relative(direction))) {
                return true;
            }
        }

        return false;
    }
}
