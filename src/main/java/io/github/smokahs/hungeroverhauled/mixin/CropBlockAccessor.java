package io.github.smokahs.hungeroverhauled.mixin;

import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// CropBlock#getAge is protected, but it is the only version-proof way to read a modded crop's age
@Mixin(CropBlock.class)
public interface CropBlockAccessor {

    @Invoker("getAge")
    int callGetAge(BlockState state);
}
