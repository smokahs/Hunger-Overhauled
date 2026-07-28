package io.github.smokahs.hungeroverhauled.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;
import io.github.smokahs.hungeroverhauled.config.Config;
import io.github.smokahs.hungeroverhauled.seeds.GrassSeeds;

// tilling only makes farmland near water; dry grass just gets torn up, sometimes coughing up a seed
@Mod.EventBusSubscriber(modid = HungerOverhauled.MOD_ID)
public final class HoeModule {

    private HoeModule() {
    }

    @SubscribeEvent
    public static void onToolModification(BlockEvent.BlockToolModificationEvent event) {
        if (!Config.modifyHoeUse || event.getToolAction() != ToolActions.HOE_TILL) {
            return;
        }

        Level level = event.getContext().getLevel();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();
        Player player = event.getPlayer();

        boolean grass = state.is(Blocks.GRASS_BLOCK);
        boolean dirt = state.is(Blocks.DIRT) || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.ROOTED_DIRT) || state.is(Blocks.DIRT_PATH);

        if (!grass && !dirt) {
            event.setCanceled(true);
            return;
        }

        if (isWaterNearby(level, pos)) {
            // normal tilling, just harder on the tool
            if (!event.isSimulated()) {
                damageHoe(event.getHeldItemStack(), player);
            }

            return;
        }

        if (!grass) {
            event.setCanceled(true);
            return;
        }

        event.setFinalState(Blocks.DIRT.defaultBlockState());

        if (event.isSimulated()) {
            return;
        }

        level.playSound(player, pos, SoundEvents.ROOTED_DIRT_BREAK, SoundSource.BLOCKS, 1.0F, 0.8F);

        if (!level.isClientSide() && Config.seedChance > 0) {
            int seedChance = Config.seedChance;
            Difficulty difficulty = level.getDifficulty();

            if (difficulty == Difficulty.PEACEFUL || difficulty == Difficulty.EASY) {
                seedChance *= 2;
            } else if (difficulty == Difficulty.HARD) {
                seedChance = Math.max(Math.round(seedChance / 2.0F), 1);
            }

            if (level.random.nextInt(100) <= seedChance) {
                ItemStack seed = GrassSeeds.seedFromTilling(level.random);

                if (!seed.isEmpty()) {
                    Block.popResource(level, pos.above(), seed);
                }
            }
        }

        damageHoe(event.getHeldItemStack(), player);
    }

    private static void damageHoe(ItemStack stack, Player player) {
        if (Config.hoeToolDamageMultiplier <= 1 || stack.isEmpty() || player == null) {
            return;
        }

        stack.hurtAndBreak(Config.hoeToolDamageMultiplier - 1, player,
                p -> p.broadcastBreakEvent(p.getUsedItemHand()));
    }

    // x-4..x+4, y..y+1, z-4..z+4, same window vanilla farmland uses
    private static boolean isWaterNearby(LevelReader level, BlockPos pos) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int x = pos.getX() - 4; x <= pos.getX() + 4; ++x) {
            for (int y = pos.getY(); y <= pos.getY() + 1; ++y) {
                for (int z = pos.getZ() - 4; z <= pos.getZ() + 4; ++z) {
                    if (level.getFluidState(cursor.set(x, y, z)).is(FluidTags.WATER)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
