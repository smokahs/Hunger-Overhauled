package io.github.smokahs.hungeroverhauled.mixin.appleskin;

import net.minecraft.world.entity.player.Player;

import io.github.smokahs.hungeroverhauled.config.Config;
import io.github.smokahs.hungeroverhauled.hunger.HungerLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import squeek.appleskin.helpers.HungerHelper;

// appleskin hardcodes 4.0 here, but we move the threshold, so its exhaustion bar fills at the wrong rate
@Mixin(HungerHelper.class)
public abstract class HungerHelperMixin {

    @Inject(method = "getMaxExhaustion", at = @At("HEAD"), cancellable = true, remap = false)
    private static void hungeroverhauled$maxExhaustion(Player player, CallbackInfoReturnable<Float> cir) {
        if (!Config.COMMON_SPEC.isLoaded() || Config.hungerLossRatePercentage <= 0.0F) {
            return;
        }

        cir.setReturnValue(HungerLogic.maxExhaustionLevel(player.level().getDifficulty()));
    }
}
