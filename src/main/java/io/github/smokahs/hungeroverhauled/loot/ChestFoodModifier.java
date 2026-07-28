package io.github.smokahs.hungeroverhauled.loot;

import java.util.List;
import java.util.Set;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.smokahs.hungeroverhauled.config.Config;
import io.github.smokahs.hungeroverhauled.food.FoodModifier;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

// drops a stack of high tier food into the chests the 1.12 version targeted
public class ChestFoodModifier extends LootModifier {

    public static final java.util.function.Supplier<Codec<ChestFoodModifier>> CODEC = Suppliers.memoize(
            () -> RecordCodecBuilder.create(inst -> LootModifier.codecStart(inst).apply(inst, ChestFoodModifier::new)));

    private static final Set<ResourceLocation> TABLES = Set.of(
            BuiltInLootTables.SIMPLE_DUNGEON,
            BuiltInLootTables.ABANDONED_MINESHAFT,
            BuiltInLootTables.DESERT_PYRAMID,
            BuiltInLootTables.JUNGLE_TEMPLE);

    public ChestFoodModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!Config.addFoodChestLoot || !TABLES.contains(context.getQueriedLootTableId())) {
            return generatedLoot;
        }

        if (context.getRandom().nextFloat() >= Config.chestLootChance) {
            return generatedLoot;
        }

        List<Item> pool = FoodModifier.highTierFoods();

        if (pool.isEmpty()) {
            return generatedLoot;
        }

        Item item = pool.get(context.getRandom().nextInt(pool.size()));
        int max = Math.min(Config.chestLootMaxStackSize, item.getMaxStackSize());

        generatedLoot.add(new ItemStack(item, 1 + context.getRandom().nextInt(Math.max(1, max))));

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
