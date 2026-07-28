package io.github.smokahs.hungeroverhauled.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

import io.github.smokahs.hungeroverhauled.config.Config;
import io.github.smokahs.hungeroverhauled.hunger.FoodDataAccess;
import io.github.smokahs.hungeroverhauled.hunger.HungerLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public abstract class FoodDataMixin implements FoodDataAccess {

    @Shadow
    private int tickTimer;

    @Shadow
    private int lastFoodLevel;

    @Override
    public int hungeroverhauled$getTickTimer() {
        return this.tickTimer;
    }

    @Override
    public void hungeroverhauled$setTickTimer(int tickTimer) {
        this.tickTimer = tickTimer;
    }

    @Override
    public void hungeroverhauled$setLastFoodLevel(int lastFoodLevel) {
        this.lastFoodLevel = lastFoodLevel;
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void hungeroverhauled$tick(Player player, CallbackInfo ci) {
        if (!Config.COMMON_SPEC.isLoaded()) {
            return;
        }

        HungerLogic.tick((FoodData) (Object) this, player);
        ci.cancel();
    }

    // with hunger loss off, eating has to do nothing or the bar creeps back up
    @Inject(method = "eat(IF)V", at = @At("HEAD"), cancellable = true)
    private void hungeroverhauled$eat(int nutrition, float saturationModifier, CallbackInfo ci) {
        if (Config.COMMON_SPEC.isLoaded() && Config.hungerLossRatePercentage <= 0.0F) {
            ci.cancel();
        }
    }
}
