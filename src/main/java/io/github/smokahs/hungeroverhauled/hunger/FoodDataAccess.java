package io.github.smokahs.hungeroverhauled.hunger;

// lets us reach the private fields on FoodData
public interface FoodDataAccess {

    int hungeroverhauled$getTickTimer();

    void hungeroverhauled$setTickTimer(int tickTimer);

    void hungeroverhauled$setLastFoodLevel(int lastFoodLevel);
}
