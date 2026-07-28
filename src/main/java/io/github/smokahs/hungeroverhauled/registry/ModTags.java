package io.github.smokahs.hungeroverhauled.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;

public final class ModTags {

    // seeds a hoe can turn up from grass, and the seeds stripped from grass loot
    public static final TagKey<Item> TILLING_SEEDS = itemTag("tilling_seeds");

    // edible items that must not be plantable when foodsUnplantable is on
    public static final TagKey<Item> UNPLANTABLE_FOODS = itemTag("unplantable_foods");

    // counts as a knife, so the animal does not go off in your face
    public static final TagKey<Item> HUMANE_SLAUGHTER_TOOLS = itemTag("humane_slaughter_tools");

    private ModTags() {
    }

    private static TagKey<Item> itemTag(String path) {
        return TagKey.create(Registries.ITEM, HungerOverhauled.id(path));
    }
}
