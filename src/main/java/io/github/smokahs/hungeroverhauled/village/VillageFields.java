package io.github.smokahs.hungeroverhauled.village;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.mojang.datafixers.util.Pair;

import io.github.smokahs.hungeroverhauled.HungerOverhauled;
import io.github.smokahs.hungeroverhauled.config.Config;
import io.github.smokahs.hungeroverhauled.mixin.StructureTemplatePoolAccessor;

// adds a second helping of farm to every village style, run through CropFieldProcessor
@Mod.EventBusSubscriber(modid = HungerOverhauled.MOD_ID)
public final class VillageFields {

    // same weight vanilla gives its own farms
    private static final int WEIGHT = 4;

    // reusing vanilla's farm layouts avoids shipping structure nbt of our own
    private static final Map<String, String> FIELDS = Map.of(
            "village/plains/houses", "village/plains/houses/plains_small_farm_1",
            "village/savanna/houses", "village/savanna/houses/savanna_small_farm",
            "village/snowy/houses", "village/snowy/houses/snowy_farm_1",
            "village/taiga/houses", "village/taiga/houses/taiga_small_farm_1",
            "village/desert/houses", "village/desert/houses/desert_farm_1");

    private VillageFields() {
    }

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        if (!Config.addCustomVillageField
                || Config.fieldNormalWeight + Config.fieldReedWeight + Config.fieldStemWeight <= 0) {
            return;
        }

        Registry<StructureTemplatePool> pools = event.getServer().registryAccess()
                .registryOrThrow(Registries.TEMPLATE_POOL);
        Registry<StructureProcessorList> processors = event.getServer().registryAccess()
                .registryOrThrow(Registries.PROCESSOR_LIST);

        Optional<Holder.Reference<StructureProcessorList>> processorList = processors
                .getHolder(ResourceKey.create(Registries.PROCESSOR_LIST, HungerOverhauled.id("village_fields")));

        if (processorList.isEmpty()) {
            HungerOverhauled.LOGGER.warn("Village field processor list is missing, skipping village fields");
            return;
        }

        int added = 0;

        for (Map.Entry<String, String> entry : FIELDS.entrySet()) {
            if (inject(pools, processorList.get(), entry.getKey(), entry.getValue())) {
                added++;
            }
        }

        HungerOverhauled.LOGGER.info("Added custom fields to {} village pool(s)", added);
    }

    private static boolean inject(Registry<StructureTemplatePool> pools,
                                  Holder<StructureProcessorList> processorList,
                                  String poolPath, String structurePath) {
        StructureTemplatePool pool = pools.get(new ResourceLocation("minecraft", poolPath));

        if (pool == null) {
            return false;
        }

        StructurePoolElement element = StructurePoolElement
                .legacy("minecraft:" + structurePath, processorList)
                .apply(StructureTemplatePool.Projection.RIGID);

        StructureTemplatePoolAccessor accessor = (StructureTemplatePoolAccessor) pool;

        // templates holds one entry per weight point, rawTemplates is the immutable source list
        for (int i = 0; i < WEIGHT; i++) {
            accessor.getTemplates().add(element);
        }

        List<Pair<StructurePoolElement, Integer>> raw = new ArrayList<>(accessor.getRawTemplates());
        raw.add(Pair.of(element, WEIGHT));
        accessor.setRawTemplates(raw);

        return true;
    }
}
