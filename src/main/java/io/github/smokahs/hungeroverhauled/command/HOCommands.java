package io.github.smokahs.hungeroverhauled.command;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;
import io.github.smokahs.hungeroverhauled.food.FoodModifier;

@Mod.EventBusSubscriber(modid = HungerOverhauled.MOD_ID)
public final class HOCommands {

    private HOCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal(HungerOverhauled.MOD_ID)
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("reload").executes(context -> {
                    HungerOverhauled.Reloads.refresh();

                    context.getSource().sendSuccess(
                            () -> Component.literal("Hunger Overhauled: reloaded json overrides and reapplied food values"),
                            true);

                    return 1;
                }))
                .then(Commands.literal("dump").executes(context -> {
                    int count = FoodModifier.highTierFoods().size();

                    context.getSource().sendSuccess(
                            () -> Component.literal("Hunger Overhauled: " + count + " item(s) count as high tier food"),
                            false);

                    return 1;
                })));
    }
}
