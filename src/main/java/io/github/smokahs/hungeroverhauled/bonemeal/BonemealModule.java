package io.github.smokahs.hungeroverhauled.bonemeal;

import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;
import io.github.smokahs.hungeroverhauled.config.Config;
import io.github.smokahs.hungeroverhauled.mixin.CropBlockAccessor;

@Mod.EventBusSubscriber(modid = HungerOverhauled.MOD_ID)
public final class BonemealModule {

    private BonemealModule() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBonemeal(BonemealEvent event) {
        // the client rolls different numbers than the server, which desyncs the particles
        if (event.getLevel().isClientSide()) {
            return;
        }

        if (event.getResult() != Event.Result.DEFAULT || event.isCanceled() || Config.bonemealEffectiveness == 1.0F) {
            return;
        }

        BlockState state = event.getBlock();

        if (!(state.getBlock() instanceof CropBlock) && !(state.getBlock() instanceof NetherWartBlock)) {
            return;
        }

        if (Config.bonemealEffectiveness == 0.0F) {
            event.setCanceled(true);
            event.setResult(Event.Result.DENY);
            return;
        }

        Level level = event.getLevel();
        RandomSource random = level.getRandom();

        if (random.nextFloat() >= Config.bonemealEffectiveness) {
            // consumed, nothing happened
            event.setResult(Event.Result.ALLOW);
            return;
        }

        if (!Config.modifyBonemealGrowth) {
            // fall through to vanilla's growth amount
            return;
        }

        BlockState grown = grow(level, state, random);

        if (!state.equals(grown)) {
            level.setBlock(event.getPos(), grown, Block.UPDATE_ALL);
            event.setResult(Event.Result.ALLOW);
        }
    }

    private static BlockState grow(Level level, BlockState state, RandomSource random) {
        if (state.getBlock() instanceof CropBlock crop) {
            int age = ((CropBlockAccessor) crop).callGetAge(state);
            int increase = 1;

            if (Config.difficultyScalingBoneMeal && level.getDifficulty() == Difficulty.PEACEFUL) {
                increase = random.nextInt(3);
            }

            return crop.getStateForAge(Math.min(age + increase, crop.getMaxAge()));
        }

        if (state.getBlock() instanceof NetherWartBlock) {
            return state.setValue(NetherWartBlock.AGE, Math.min(state.getValue(NetherWartBlock.AGE) + 1, 3));
        }

        return state;
    }
}
