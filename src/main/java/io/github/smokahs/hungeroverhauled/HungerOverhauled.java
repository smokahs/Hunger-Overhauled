package io.github.smokahs.hungeroverhauled;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.mojang.logging.LogUtils;

import io.github.smokahs.hungeroverhauled.compat.SolPotPie;
import io.github.smokahs.hungeroverhauled.config.Config;
import io.github.smokahs.hungeroverhauled.food.FoodModifier;
import io.github.smokahs.hungeroverhauled.food.FoodOverrides;
import io.github.smokahs.hungeroverhauled.growth.GrowthDefaults;
import io.github.smokahs.hungeroverhauled.registry.ModEffects;
import io.github.smokahs.hungeroverhauled.registry.ModLootModifiers;
import io.github.smokahs.hungeroverhauled.registry.ModProcessors;
import org.slf4j.Logger;

@Mod(HungerOverhauled.MOD_ID)
public class HungerOverhauled {

    public static final String MOD_ID = "hungeroverhauled";

    public static final Logger LOGGER = LogUtils.getLogger();

    public HungerOverhauled() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModEffects.register(modBus);
        ModLootModifiers.register(modBus);
        ModProcessors.register(modBus);

        modBus.addListener(Config::onLoad);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);

        MinecraftForge.EVENT_BUS.register(Reloads.class);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    // food values and growth tables need tags, so rebuild them whenever tags bind
    public static final class Reloads {

        private static boolean serverUp;

        private Reloads() {
        }

        @SubscribeEvent
        public static void onTagsUpdated(TagsUpdatedEvent event) {
            refresh();

            // spice of life scores food at server start, so only a later /reload can leave it stale
            if (serverUp && event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD) {
                SolPotPie.refreshScores();
            }
        }

        @SubscribeEvent
        public static void onServerStarted(ServerStartedEvent event) {
            serverUp = true;
        }

        @SubscribeEvent
        public static void onServerStopped(ServerStoppedEvent event) {
            serverUp = false;
        }

        @SubscribeEvent
        public static void onAddReloadListener(AddReloadListenerEvent event) {
            // the json lives in config, but /reload is a good moment to pick up edits
            FoodOverrides.reload();
        }

        public static void refresh() {
            FoodOverrides.reload();
            GrowthDefaults.rebuild();
            FoodModifier.apply();
        }
    }
}
