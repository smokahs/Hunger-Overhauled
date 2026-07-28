package io.github.smokahs.hungeroverhauled.food;

import net.minecraft.world.food.FoodProperties;

// just the two numbers we change, the rest of the food props get copied over
public record FoodValues(int nutrition, float saturationModifier) {

    public static FoodValues of(FoodProperties properties) {
        return new FoodValues(properties.getNutrition(), properties.getSaturationModifier());
    }

    // total saturation restored, used for the tooltip wording
    public float saturation() {
        return this.nutrition * this.saturationModifier * 2.0F;
    }
}
