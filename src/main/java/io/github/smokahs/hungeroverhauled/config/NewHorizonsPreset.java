package io.github.smokahs.hungeroverhauled.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import net.minecraftforge.common.ForgeConfigSpec;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;

// writes the new horizons numbers straight into the config file, so the options you see are the real ones
public final class NewHorizonsPreset {

    // option -> the value new horizons runs for it, by identity so options never collide
    private static final Map<ForgeConfigSpec.ConfigValue<?>, Object> HORIZONS = new IdentityHashMap<>();

    private NewHorizonsPreset() {
    }

    // tags an option with its new horizons value while the config is being built
    static <T, C extends ForgeConfigSpec.ConfigValue<T>> C preset(C option, T horizons) {
        HORIZONS.put(option, horizons);
        return option;
    }

    // runs on every load and reload, so flipping the toggle takes hold right away and stays put
    public static void apply() {
        if (!Config.COMMON_SPEC.isLoaded() || !Config.COMMON.setToNewHorizonsDefaults.get()) {
            return;
        }

        List<String> written = new ArrayList<>();

        HORIZONS.forEach((option, horizons) -> {
            if (!horizons.equals(option.get())) {
                set(option, horizons);
                written.add(String.join(".", option.getPath()));
            }
        });

        if (written.isEmpty()) {
            return;
        }

        // one save covers everything set above
        Config.COMMON_SPEC.save();

        Collections.sort(written);
        HungerOverhauled.LOGGER.info("New Horizons mode wrote {} option(s) into the config: {}",
                written.size(), written);
    }

    @SuppressWarnings("unchecked")
    private static <T> void set(ForgeConfigSpec.ConfigValue<T> option, Object horizons) {
        option.set((T) horizons);
    }
}
