package io.github.smokahs.hungeroverhauled.registry;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;

public final class ModEffects {

    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, HungerOverhauled.MOD_ID);

    // just a marker, the actual regen speedup happens in HungerLogic
    public static final RegistryObject<MobEffect> WELL_FED = EFFECTS.register("well_fed",
            () -> new WellFed(MobEffectCategory.BENEFICIAL, 0xE8C170));

    private ModEffects() {
    }

    public static void register(IEventBus modBus) {
        EFFECTS.register(modBus);
    }

    private static final class WellFed extends MobEffect {

        private WellFed(MobEffectCategory category, int color) {
            super(category, color);
        }

        // forge only calls this on the client, so the client-only class stays unloaded on servers
        @Override
        public void initializeClient(java.util.function.Consumer<
                net.minecraftforge.client.extensions.common.IClientMobEffectExtensions> consumer) {
            consumer.accept(io.github.smokahs.hungeroverhauled.client.WellFedIcon.INSTANCE);
        }
    }
}
