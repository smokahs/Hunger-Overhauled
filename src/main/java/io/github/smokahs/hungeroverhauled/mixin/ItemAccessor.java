package io.github.smokahs.hungeroverhauled.mixin;

import javax.annotation.Nullable;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

// both fields are private final, writing them is how other mods see the new values
@Mixin(Item.class)
public interface ItemAccessor {

    @Mutable
    @Accessor("foodProperties")
    void setFoodProperties(@Nullable FoodProperties foodProperties);

    @Mutable
    @Accessor("maxStackSize")
    void setMaxStackSize(int maxStackSize);
}
