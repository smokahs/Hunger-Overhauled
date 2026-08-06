package io.github.smokahs.hungeroverhauled.client;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;
import io.github.smokahs.hungeroverhauled.config.Config;

// stands in for 1.12's RenderGameOverlayEvent.Text, which has no 1.20.1 equivalent
@Mod.EventBusSubscriber(modid = HungerOverhauled.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class StatusOverlay {

    private static final String PREFIX = HungerOverhauled.MOD_ID + ".ui.";

    private StatusOverlay() {
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.AIR_LEVEL.id(), "status_text", StatusOverlay::render);
    }

    private static void render(ForgeGui gui, GuiGraphics graphics, float partialTick,
                               int screenWidth, int screenHeight) {
        if (!Config.addGuiText) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null || player.isDeadOrDying() || player.isCreative() || player.isSpectator()) {
            return;
        }

        if (minecraft.options.hideGui || minecraft.options.renderDebug) {
            return;
        }

        Font font = minecraft.font;
        int leftY = screenHeight - gui.leftHeight;
        int rightY = screenHeight - gui.rightHeight;

        String health = healthKey(player.getHealth() / player.getMaxHealth());

        if (health != null) {
            graphics.drawString(font, Component.translatable(PREFIX + health).withStyle(healthColor(health)),
                    screenWidth / 2 - 91 + Config.healthTextOffsetX, leftY + Config.healthTextOffsetY,
                    0xFFFFFF, true);
        }

        String hunger = hungerKey(player.getFoodData().getFoodLevel());

        if (hunger != null) {
            Component text = Component.translatable(PREFIX + hunger).withStyle(hungerColor(hunger));
            graphics.drawString(font, text,
                    screenWidth / 2 + 91 - font.width(text) + Config.hungerTextOffsetX,
                    rightY + Config.hungerTextOffsetY, 0xFFFFFF, true);
        }
    }

    @Nullable
    private static String healthKey(float percent) {
        if (percent <= 0.15F) {
            return "health.dying";
        }

        if (percent <= 0.3F) {
            return "health.injured";
        }

        if (percent < 0.5F) {
            return "health.hurt";
        }

        return null;
    }

    private static ChatFormatting healthColor(String key) {
        return switch (key) {
            case "health.dying" -> ChatFormatting.RED;
            case "health.injured" -> ChatFormatting.YELLOW;
            default -> ChatFormatting.WHITE;
        };
    }

    @Nullable
    private static String hungerKey(int foodLevel) {
        if (foodLevel <= 6) {
            return "hunger.starving";
        }

        if (foodLevel <= 10) {
            return "hunger.hungry";
        }

        if (foodLevel <= 14) {
            return "hunger.peckish";
        }

        return null;
    }

    private static ChatFormatting hungerColor(String key) {
        return switch (key) {
            case "hunger.starving" -> ChatFormatting.RED;
            case "hunger.hungry" -> ChatFormatting.YELLOW;
            default -> ChatFormatting.WHITE;
        };
    }
}
