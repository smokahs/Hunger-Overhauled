package io.github.smokahs.hungeroverhauled.loot;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.smokahs.hungeroverhauled.config.Config;
import io.github.smokahs.hungeroverhauled.harvest.CropHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

// break drops for fully grown crops; 1.12 did this through HarvestDropsEvent, which no longer exists
public class CropDropsModifier extends LootModifier {

    public static final java.util.function.Supplier<Codec<CropDropsModifier>> CODEC = Suppliers.memoize(
            () -> RecordCodecBuilder.create(inst -> LootModifier.codecStart(inst).apply(inst, CropDropsModifier::new)));

    public CropDropsModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!Config.modifyCropDropsBreak) {
            return generatedLoot;
        }

        BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);

        if (state == null || !CropHelper.isFullyGrown(state) || CropHelper.isDropsBlacklisted(state.getBlock())) {
            return generatedLoot;
        }

        BlockPos pos = context.hasParam(LootContextParams.ORIGIN)
                ? BlockPos.containing(context.getParam(LootContextParams.ORIGIN))
                : BlockPos.ZERO;

        List<ItemStack> modified = CropHelper.modifyBreakDrops(generatedLoot, context.getLevel(), pos, state);

        return new ObjectArrayList<>(modified);
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
