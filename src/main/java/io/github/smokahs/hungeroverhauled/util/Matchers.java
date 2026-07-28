package io.github.smokahs.hungeroverhauled.util;

import java.util.List;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

// config lists take an id like minecraft:bread or a tag like #forge:crops
public final class Matchers {

    private Matchers() {
    }

    public static boolean matchesItem(List<? extends String> entries, Item item) {
        if (entries == null || entries.isEmpty()) {
            return false;
        }

        ResourceLocation id = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(item);

        for (String entry : entries) {
            if (entry.startsWith("#")) {
                ResourceLocation tagId = ResourceLocation.tryParse(entry.substring(1));

                if (tagId != null && item.builtInRegistryHolder().is(TagKey.create(Registries.ITEM, tagId))) {
                    return true;
                }
            } else if (id != null && id.toString().equals(entry)) {
                return true;
            }
        }

        return false;
    }

    public static boolean matchesBlock(List<? extends String> entries, Block block) {
        if (entries == null || entries.isEmpty()) {
            return false;
        }

        ResourceLocation id = net.minecraftforge.registries.ForgeRegistries.BLOCKS.getKey(block);

        for (String entry : entries) {
            if (entry.startsWith("#")) {
                ResourceLocation tagId = ResourceLocation.tryParse(entry.substring(1));

                if (tagId != null && block.builtInRegistryHolder().is(TagKey.create(Registries.BLOCK, tagId))) {
                    return true;
                }
            } else if (id != null && id.toString().equals(entry)) {
                return true;
            }
        }

        return false;
    }
}
