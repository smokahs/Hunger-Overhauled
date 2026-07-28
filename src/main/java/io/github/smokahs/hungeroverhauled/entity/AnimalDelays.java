package io.github.smokahs.hungeroverhauled.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;
import io.github.smokahs.hungeroverhauled.config.Config;
import io.github.smokahs.hungeroverhauled.util.RandomHelper;

// stretches out breeding, growing up, egg laying and milking
@Mod.EventBusSubscriber(modid = HungerOverhauled.MOD_ID)
public final class AnimalDelays {

    private static final String MILKED = "HungerOverhauledMilked";

    private AnimalDelays() {
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide() || !(entity instanceof Animal animal)) {
            return;
        }

        int age = animal.getAge();

        if (age > 0 && RandomHelper.nextFloat(animal.getRandom(), Config.breedingTimeoutMultiplier) >= 1.0F) {
            animal.setAge(age + 1);
        } else if (age < 0 && RandomHelper.nextFloat(animal.getRandom(), Config.childDurationMultiplier) >= 1.0F) {
            animal.setAge(age - 1);
        }

        if (Config.eggTimeoutMultiplier > 1.0F && animal instanceof Chicken chicken && chicken.eggTime > 0
                && RandomHelper.nextFloat(animal.getRandom(), Config.eggTimeoutMultiplier) >= 1.0F) {
            chicken.eggTime++;
        }

        if (Config.milkedTimeout > 0 && animal instanceof Cow && animal.level().getGameTime() % 20L == 0L) {
            CompoundTag tags = animal.getPersistentData();

            if (tags.contains(MILKED)) {
                int remaining = tags.getInt(MILKED) - 1;

                if (remaining <= 0) {
                    tags.remove(MILKED);
                } else {
                    tags.putInt(MILKED, remaining);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (Config.milkedTimeout <= 0 || !(event.getTarget() instanceof Cow cow)) {
            return;
        }

        Player player = event.getEntity();
        ItemStack held = player.getItemInHand(event.getHand());

        boolean milking = held.is(Items.BUCKET) || (cow instanceof MushroomCow && held.is(Items.BOWL));

        if (!milking) {
            return;
        }

        CompoundTag tags = cow.getPersistentData();

        if (tags.contains(MILKED)) {
            event.setCanceled(true);

            if (!player.level().isClientSide()) {
                cow.playSound(SoundEvents.COW_HURT, 0.4F,
                        (cow.getRandom().nextFloat() - cow.getRandom().nextFloat()) * 0.2F + 1.0F);
            }
        } else {
            tags.putInt(MILKED, Config.milkedTimeout * 60);
        }
    }
}
