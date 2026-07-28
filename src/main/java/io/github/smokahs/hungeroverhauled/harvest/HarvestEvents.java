package io.github.smokahs.hungeroverhauled.harvest;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;
import io.github.smokahs.hungeroverhauled.config.Config;
import io.github.smokahs.hungeroverhauled.registry.ModTags;

@Mod.EventBusSubscriber(modid = HungerOverhauled.MOD_ID)
public final class HarvestEvents {

    private HarvestEvents() {
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        ItemStack held = event.getItemStack();

        if (Config.foodsUnplantable && held.is(ModTags.UNPLANTABLE_FOODS)) {
            event.setUseItem(Event.Result.DENY);
        }

        if (!Config.enableRightClickHarvesting || player.isSecondaryUseActive()) {
            return;
        }

        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        if (CropHelper.isHarvestBlacklisted(state.getBlock()) || !CropHelper.isFullyGrown(state)) {
            return;
        }

        BlockState reset = CropHelper.harvestedState(state);

        if (reset == null) {
            return;
        }

        if (level instanceof ServerLevel serverLevel) {
            List<ItemStack> drops = Block.getDrops(state, serverLevel, pos, null, player, held);

            if (Config.modifyCropDropsRightClick) {
                drops = CropHelper.modifyRightClickDrops(drops, serverLevel, pos, state);
            }

            for (ItemStack drop : drops) {
                Block.popResource(serverLevel, pos, drop);
            }

            serverLevel.setBlock(pos, reset, Block.UPDATE_ALL);
            serverLevel.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 0.8F);
        }

        // cancelling on both sides keeps the client from also trying to place whatever is in hand
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide()));
    }
}
