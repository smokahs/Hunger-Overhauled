package io.github.smokahs.hungeroverhauled.util;

import net.minecraft.util.RandomSource;

public final class RandomHelper {

    public static final RandomSource RANDOM = RandomSource.create();

    private RandomHelper() {
    }

    public static int getRandomIntFromRange(int min, int max) {
        return min + (max > min ? RANDOM.nextInt(1 + max - min) : 0);
    }

    // nextInt but for floats, check the result against 1 to roll a fractional chance
    public static float nextFloat(RandomSource random, float bound) {
        return random.nextFloat() * bound;
    }
}
