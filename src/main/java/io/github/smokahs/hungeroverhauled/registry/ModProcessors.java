package io.github.smokahs.hungeroverhauled.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;
import io.github.smokahs.hungeroverhauled.village.CropFieldProcessor;

public final class ModProcessors {

    public static final DeferredRegister<StructureProcessorType<?>> PROCESSORS =
            DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, HungerOverhauled.MOD_ID);

    public static final RegistryObject<StructureProcessorType<CropFieldProcessor>> CROP_FIELD =
            PROCESSORS.register("crop_field", () -> () -> CropFieldProcessor.CODEC);

    private ModProcessors() {
    }

    public static void register(IEventBus modBus) {
        PROCESSORS.register(modBus);
    }
}
