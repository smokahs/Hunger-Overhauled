package io.github.smokahs.hungeroverhauled.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;

import io.github.smokahs.hungeroverhauled.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Redirect(method = "aiStep",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    private boolean hungeroverhauled$peacefulRegen(GameRules rules, GameRules.Key<GameRules.BooleanValue> key) {
        if (!Config.COMMON_SPEC.isLoaded()) {
            return rules.getBoolean(key);
        }

        return false;
    }
}
