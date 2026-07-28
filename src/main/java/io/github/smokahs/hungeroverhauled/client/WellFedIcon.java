package io.github.smokahs.hungeroverhauled.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

// the 1.12 mod had no art, it pointed at cell (7,0) of vanilla's potion sheet, so point at the same sprite
// rather than shipping a copy of mojang's png
public final class WellFedIcon implements IClientMobEffectExtensions {

    public static final WellFedIcon INSTANCE = new WellFedIcon();

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("minecraft", "textures/mob_effect/jump_boost.png");

    private static final int SIZE = 18;

    private WellFedIcon() {
    }

    @Override
    public boolean renderInventoryIcon(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen,
                                       GuiGraphics guiGraphics, int x, int y, int blitOffset) {
        guiGraphics.blit(TEXTURE, x + 6, y + 7, 0, 0, 0, SIZE, SIZE, SIZE, SIZE);
        return true;
    }

    @Override
    public boolean renderGuiIcon(MobEffectInstance instance, Gui gui, GuiGraphics guiGraphics, int x, int y,
                                 float z, float alpha) {
        guiGraphics.blit(TEXTURE, x + 3, y + 3, 0, 0, 0, SIZE, SIZE, SIZE, SIZE);
        return true;
    }
}
