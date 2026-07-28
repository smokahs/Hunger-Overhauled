package io.github.smokahs.hungeroverhauled.registry;

import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import com.mojang.serialization.Codec;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;
import io.github.smokahs.hungeroverhauled.loot.ChestFoodModifier;
import io.github.smokahs.hungeroverhauled.loot.CropDropsModifier;
import io.github.smokahs.hungeroverhauled.loot.GrassSeedsModifier;

public final class ModLootModifiers {

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, HungerOverhauled.MOD_ID);

    static {
        SERIALIZERS.register("crop_drops", CropDropsModifier.CODEC);
        SERIALIZERS.register("grass_seeds", GrassSeedsModifier.CODEC);
        SERIALIZERS.register("chest_food", ChestFoodModifier.CODEC);
    }

    private ModLootModifiers() {
    }

    public static void register(IEventBus modBus) {
        SERIALIZERS.register(modBus);
    }
}
