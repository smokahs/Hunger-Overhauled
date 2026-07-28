package io.github.smokahs.hungeroverhauled.harvest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;

import io.github.smokahs.hungeroverhauled.config.Config;
import io.github.smokahs.hungeroverhauled.food.FoodOverrides;
import io.github.smokahs.hungeroverhauled.mixin.CropBlockAccessor;
import io.github.smokahs.hungeroverhauled.util.Matchers;
import io.github.smokahs.hungeroverhauled.util.RandomHelper;

// shared crop identification + drop rewriting for both right-click and break harvesting
public final class CropHelper {

    private CropHelper() {
    }

    public static boolean isHarvestBlacklisted(Block block) {
        return Matchers.matchesBlock(FoodOverrides.jsonHarvestBlacklist(), block);
    }

    public static boolean isDropsBlacklisted(Block block) {
        return Matchers.matchesBlock(FoodOverrides.jsonDropsBlacklist(), block);
    }

    // true when the plant is at its final growth stage
    public static boolean isFullyGrown(BlockState state) {
        Block block = state.getBlock();

        if (block instanceof CropBlock crop) {
            return crop.isMaxAge(state);
        }

        if (block instanceof NetherWartBlock) {
            return state.getValue(NetherWartBlock.AGE) >= 3;
        }

        if (block instanceof CocoaBlock) {
            return state.getValue(CocoaBlock.AGE) >= 2;
        }

        return false;
    }

    // the state the plant resets to after being harvested, or null if it is not resettable
    @Nullable
    public static BlockState harvestedState(BlockState state) {
        Block block = state.getBlock();

        if (block instanceof CropBlock crop) {
            return crop.getStateForAge(0);
        }

        if (block instanceof NetherWartBlock) {
            return state.setValue(NetherWartBlock.AGE, 0);
        }

        if (block instanceof CocoaBlock) {
            return state.setValue(CocoaBlock.AGE, 0);
        }

        return null;
    }

    public static int age(BlockState state) {
        Block block = state.getBlock();

        if (block instanceof CropBlock) {
            return ((CropBlockAccessor) block).callGetAge(state);
        }

        if (block instanceof NetherWartBlock) {
            return state.getValue(NetherWartBlock.AGE);
        }

        if (block instanceof CocoaBlock) {
            return state.getValue(CocoaBlock.AGE);
        }

        return 0;
    }

    // the item that replants this crop, for carrots and potatoes that is the produce itself
    public static ItemStack seedOf(BlockGetter level, BlockPos pos, BlockState state) {
        return state.getBlock().getCloneItemStack(level, pos, state);
    }

    // swap the seed and produce part of the loot for our own amounts, leave the rest alone
    public static List<ItemStack> modifyDrops(List<ItemStack> drops, BlockGetter level, BlockPos pos, BlockState state,
                                              int minSeeds, int maxSeeds, int minProduce, int maxProduce) {
        ItemStack seed = seedOf(level, pos, state);

        if (seed.isEmpty()) {
            return drops;
        }

        ItemStack produce = ItemStack.EMPTY;

        for (ItemStack drop : drops) {
            if (!drop.isEmpty() && !ItemStack.isSameItem(drop, seed)) {
                produce = drop;
                break;
            }
        }

        // carrots, potatoes, nether wart: the seed is the produce
        boolean produceIsNotSeed = !produce.isEmpty();

        if (produce.isEmpty()) {
            produce = seed;
        }

        int seedCount = RandomHelper.getRandomIntFromRange(minSeeds, maxSeeds);
        int produceCount = RandomHelper.getRandomIntFromRange(minProduce, maxProduce);

        List<ItemStack> modified = new ArrayList<>();

        for (ItemStack drop : drops) {
            if (ItemStack.isSameItem(drop, seed) || ItemStack.isSameItem(drop, produce)) {
                continue;
            }

            modified.add(drop);
        }

        if (produceIsNotSeed && seedCount > 0) {
            modified.add(seed.copyWithCount(seedCount));
        }

        if (produceCount > 0) {
            modified.add(produce.copyWithCount(produceCount));
        }

        return modified;
    }

    public static List<ItemStack> modifyBreakDrops(List<ItemStack> drops, BlockGetter level, BlockPos pos,
                                                   BlockState state) {
        return modifyDrops(drops, level, pos, state,
                Config.seedsPerHarvestBreakMin, Config.seedsPerHarvestBreakMax,
                Config.producePerHarvestBreakMin, Config.producePerHarvestBreakMax);
    }

    public static List<ItemStack> modifyRightClickDrops(List<ItemStack> drops, BlockGetter level, BlockPos pos,
                                                        BlockState state) {
        return modifyDrops(drops, level, pos, state,
                Config.seedsPerHarvestRightClickMin, Config.seedsPerHarvestRightClickMax,
                Config.producePerHarvestRightClickMin, Config.producePerHarvestRightClickMax);
    }
}
