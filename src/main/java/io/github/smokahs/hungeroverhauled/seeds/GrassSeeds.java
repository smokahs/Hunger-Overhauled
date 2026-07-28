package io.github.smokahs.hungeroverhauled.seeds;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import io.github.smokahs.hungeroverhauled.config.Config;
import io.github.smokahs.hungeroverhauled.registry.ModTags;

// 1.12 reached into ForgeHooks.seedList; 1.20.1 has no such list, so the pool is a tag
public final class GrassSeeds {

    private GrassSeeds() {
    }

    public static ItemStack seedFromTilling(RandomSource random) {
        List<Item> pool = pool();

        if (pool.isEmpty()) {
            return new ItemStack(Items.WHEAT_SEEDS);
        }

        if (!Config.allSeedsEqual && random.nextFloat() < 0.5F && pool.contains(Items.WHEAT_SEEDS)) {
            return new ItemStack(Items.WHEAT_SEEDS);
        }

        return new ItemStack(pool.get(random.nextInt(pool.size())));
    }

    private static List<Item> pool() {
        ITag<Item> tag = ForgeRegistries.ITEMS.tags() == null
                ? null
                : ForgeRegistries.ITEMS.tags().getTag(ModTags.TILLING_SEEDS);

        if (tag == null || tag.isEmpty()) {
            return List.of();
        }

        List<Item> items = new ArrayList<>();
        tag.forEach(items::add);

        return items;
    }
}
