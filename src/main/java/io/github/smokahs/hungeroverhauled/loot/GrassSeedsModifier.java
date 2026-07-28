package io.github.smokahs.hungeroverhauled.loot;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.TallGrassBlock;
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
import io.github.smokahs.hungeroverhauled.registry.ModTags;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

// strips seeds out of grass and fern loot so tilling is the only early source
public class GrassSeedsModifier extends LootModifier {

    public static final java.util.function.Supplier<Codec<GrassSeedsModifier>> CODEC = Suppliers.memoize(
            () -> RecordCodecBuilder.create(inst -> LootModifier.codecStart(inst).apply(inst, GrassSeedsModifier::new)));

    public GrassSeedsModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!Config.removeTallGrassSeeds) {
            return generatedLoot;
        }

        BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);

        if (state == null) {
            return generatedLoot;
        }

        if (!(state.getBlock() instanceof TallGrassBlock) && !(state.getBlock() instanceof DoublePlantBlock)) {
            return generatedLoot;
        }

        generatedLoot.removeIf(stack -> stack.is(ModTags.TILLING_SEEDS));

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
