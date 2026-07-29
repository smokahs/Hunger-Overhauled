package io.github.smokahs.hungeroverhauled.mixin;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.world.entity.monster.ZombieVillager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// starting the cure is private, and the healing axe kicks it off from a left click
@Mixin(ZombieVillager.class)
public interface ZombieVillagerAccessor {

    @Invoker("startConverting")
    void invokeStartConverting(@Nullable UUID conversionStarter, int conversionTime);
}
