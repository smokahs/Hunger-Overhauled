package io.github.smokahs.hungeroverhauled.client;

import java.util.Locale;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;
import io.github.smokahs.hungeroverhauled.compat.SolPotPie;
import io.github.smokahs.hungeroverhauled.config.Config;
import io.github.smokahs.hungeroverhauled.growth.PlantGrowthModification;
import io.github.smokahs.hungeroverhauled.growth.PlantGrowthModule;

@Mod.EventBusSubscriber(modid = HungerOverhauled.MOD_ID, value = Dist.CLIENT)
public final class FoodTooltips {

    private static final String PREFIX = HungerOverhauled.MOD_ID + ".tooltip.meal.";

    private FoodTooltips() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (stack.isEmpty()) {
            return;
        }

        if (Config.addFoodTooltips) {
            addMealDescriptor(event, stack);
        }

        if (Config.addGrowsBestInTooltips && Config.wrongBiomeRegrowthMultiplier > 1.0F) {
            addGrowsBestIn(event, stack);
        }
    }

    private static void addMealDescriptor(ItemTooltipEvent event, ItemStack stack) {
        FoodProperties food = stack.getFoodProperties(event.getEntity());

        if (food == null) {
            return;
        }

        // spice of life hides this food's numbers until it has been eaten, our wording would give them away
        if (SolPotPie.hidesUneatenValues(event.getEntity(), stack.getItem())) {
            return;
        }

        int nutrition = food.getNutrition();

        // kept from the 1.12 version verbatim so the wording lands on the same words it always did
        float satiation = food.getSaturationModifier() * 20.0F - nutrition;

        String noun = noun(nutrition);
        String adjective = adjective(satiation);
        String descriptor;

        if (adjective != null && I18n.exists(PREFIX + adjective + "_" + noun)) {
            descriptor = I18n.get(PREFIX + adjective + "_" + noun);
        } else {
            descriptor = I18n.get(PREFIX + noun);

            if (adjective != null) {
                descriptor = I18n.get(PREFIX + adjective, descriptor);
            }
        }

        if (descriptor.isEmpty()) {
            return;
        }

        descriptor = descriptor.substring(0, 1).toUpperCase(Locale.ROOT) + descriptor.substring(1);

        int index = event.getToolTip().isEmpty() ? 0 : 1;
        event.getToolTip().add(index, Component.literal(descriptor).withStyle(ChatFormatting.GRAY));
    }

    private static String noun(int nutrition) {
        if (nutrition <= 1) {
            return "morsel";
        }

        if (nutrition <= 2) {
            return "snack";
        }

        if (nutrition <= 5) {
            return "lightmeal";
        }

        if (nutrition <= 8) {
            return "meal";
        }

        if (nutrition <= 11) {
            return "largemeal";
        }

        return "feast";
    }

    @Nullable
    private static String adjective(float satiation) {
        if (satiation >= 3.0F) {
            return "hearty";
        }

        if (satiation >= 2.0F) {
            return "wholesome";
        }

        if (satiation > 0.0F) {
            return "nourishing";
        }

        if (satiation < 0.0F) {
            return "unfulfilling";
        }

        return null;
    }

    private static void addGrowsBestIn(ItemTooltipEvent event, ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return;
        }

        Block block = blockItem.getBlock();
        PlantGrowthModification growth = PlantGrowthModule.get(block);

        if (growth == null || growth.biomeGrowthModifiers.isEmpty()) {
            return;
        }

        StringBuilder biomes = new StringBuilder();

        for (TagKey<Biome> tag : growth.biomeGrowthModifiers.keySet()) {
            if (biomes.length() > 0) {
                biomes.append(", ");
            }

            biomes.append(prettify(tag));
        }

        event.getToolTip().add(Component.translatable(HungerOverhauled.MOD_ID + ".tooltip.meal.crop_grows_best_in")
                .withStyle(ChatFormatting.GRAY));
        event.getToolTip().add(Component.literal(biomes.toString()).withStyle(ChatFormatting.DARK_GRAY));
    }

    // minecraft:is_jungle -> Jungle
    private static String prettify(TagKey<Biome> tag) {
        String path = tag.location().getPath();

        if (path.startsWith("is_")) {
            path = path.substring(3);
        }

        String[] words = path.split("_");
        StringBuilder out = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }

            if (out.length() > 0) {
                out.append(' ');
            }

            out.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }

        return out.toString();
    }
}
