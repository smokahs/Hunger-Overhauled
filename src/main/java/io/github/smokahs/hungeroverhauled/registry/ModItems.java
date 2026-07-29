package io.github.smokahs.hungeroverhauled.registry;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;
import io.github.smokahs.hungeroverhauled.config.Config;
import io.github.smokahs.hungeroverhauled.item.HealingAxeItem;

public final class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, HungerOverhauled.MOD_ID);

    // always registered, otherwise flipping the toggle would break saves that already hold one.
    // 'enableHealingAxe' decides whether it is reachable, not whether it exists
    public static final RegistryObject<Item> HEALING_AXE = ITEMS.register("healing_axe",
            () -> new HealingAxeItem(new Item.Properties()));

    private ModItems() {
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
        modBus.addListener(ModItems::onBuildCreativeTabs);
    }

    // keeping it out of the tabs is also what keeps it out of recipe viewers
    private static void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (!Config.COMMON_SPEC.isLoaded() || !Config.enableHealingAxe) {
            return;
        }

        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES || event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(HEALING_AXE);
        }
    }
}
