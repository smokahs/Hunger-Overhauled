package io.github.smokahs.hungeroverhauled.trades;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;
import io.github.smokahs.hungeroverhauled.config.Config;
import io.github.smokahs.hungeroverhauled.food.FoodModifier;

// 1.12 registered one trade per matching item; here a single listing rolls a random one at offer time,
// which also means the food pool does not have to be ready when the event fires
@Mod.EventBusSubscriber(modid = HungerOverhauled.MOD_ID)
public final class TradeEvents {

    private TradeEvents() {
    }

    @SubscribeEvent
    public static void onVillagerTrades(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.BUTCHER && Config.addTradesButcher) {
            event.getTrades().get(1).add(new SellRandomFood());
        }

        if (event.getType() == VillagerProfession.FARMER) {
            if (Config.addCropTradesFarmer) {
                event.getTrades().get(1).add(new BuyRandomCrop());
            }

            if (Config.addSaplingTradesFarmer) {
                event.getTrades().get(1).add(new SellRandomSapling());
            }
        }
    }

    private static List<Item> tagItems(net.minecraft.tags.TagKey<Item> key) {
        if (ForgeRegistries.ITEMS.tags() == null) {
            return List.of();
        }

        ITag<Item> tag = ForgeRegistries.ITEMS.tags().getTag(key);

        if (tag == null || tag.isEmpty()) {
            return List.of();
        }

        List<Item> items = new ArrayList<>();
        tag.forEach(items::add);

        return items;
    }

    private record SellRandomFood() implements VillagerTrades.ItemListing {

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity trader, RandomSource random) {
            List<Item> pool = FoodModifier.highTierFoods();

            if (pool.isEmpty()) {
                return null;
            }

            Item item = pool.get(random.nextInt(pool.size()));
            int count = Math.max(1, item.getMaxStackSize() / 2);

            return new MerchantOffer(new ItemStack(Items.EMERALD, 1 + random.nextInt(3)),
                    new ItemStack(item, count), 8, 2, 0.05F);
        }
    }

    private record BuyRandomCrop() implements VillagerTrades.ItemListing {

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity trader, RandomSource random) {
            List<Item> pool = tagItems(Tags.Items.CROPS);

            if (pool.isEmpty()) {
                return null;
            }

            Item item = pool.get(random.nextInt(pool.size()));

            return new MerchantOffer(new ItemStack(item, 16), new ItemStack(Items.EMERALD), 16, 2, 0.05F);
        }
    }

    private record SellRandomSapling() implements VillagerTrades.ItemListing {

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity trader, RandomSource random) {
            List<Item> pool = tagItems(ItemTags.SAPLINGS);

            if (pool.isEmpty()) {
                return null;
            }

            Item item = pool.get(random.nextInt(pool.size()));

            return new MerchantOffer(new ItemStack(Items.EMERALD, 1 + random.nextInt(2)),
                    new ItemStack(item), 8, 2, 0.05F);
        }
    }
}
