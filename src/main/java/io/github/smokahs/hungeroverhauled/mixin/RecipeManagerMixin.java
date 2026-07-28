package io.github.smokahs.hungeroverhauled.mixin;

import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import com.google.gson.JsonElement;

import io.github.smokahs.hungeroverhauled.recipe.RecipeFilter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 1.12 pulled recipes out of the registry; datapack recipes have to be filtered as they load instead
@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;"
            + "Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"))
    private void hungeroverhauled$filterRecipes(Map<ResourceLocation, JsonElement> recipes,
                                              net.minecraft.server.packs.resources.ResourceManager resourceManager,
                                              net.minecraft.util.profiling.ProfilerFiller profiler, CallbackInfo ci) {
        RecipeFilter.filter(recipes);
    }
}
