package io.github.smokahs.hungeroverhauled.mixin.appleskin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import io.github.smokahs.hungeroverhauled.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import squeek.appleskin.api.food.FoodValues;
import squeek.appleskin.helpers.FoodHelper;

// appleskin only estimates health from regen effects, so it misses the heal from foodRegensHealth
@Mixin(FoodHelper.class)
public abstract class FoodHelperMixin {

    @Inject(method = "getEstimatedHealthIncrement(Lnet/minecraft/world/item/ItemStack;"
            + "Lsqueek/appleskin/api/food/FoodValues;Lnet/minecraft/world/entity/player/Player;)F",
            at = @At("RETURN"), cancellable = true, remap = false)
    private static void hungeroverhauled$estimatedHealth(ItemStack stack, FoodValues values, Player player,
                                                       CallbackInfoReturnable<Float> cir) {
        if (!Config.COMMON_SPEC.isLoaded() || !Config.foodRegensHealth || Config.foodHealDivider <= 0) {
            return;
        }

        float fromFood = Math.round(values.hunger / (float) Config.foodHealDivider);

        if (fromFood > 0.0F) {
            cir.setReturnValue(cir.getReturnValue() + fromFood);
        }
    }
}
