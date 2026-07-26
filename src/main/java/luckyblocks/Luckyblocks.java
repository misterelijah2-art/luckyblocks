package luckyblocks;

import luckyblocks.event.ModEvents;
import luckyblocks.registry.ModBlocks;
import luckyblocks.registry.ModEntities;
import luckyblocks.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Luckyblocks implements ModInitializer {
    public static final String MOD_ID = "luckyblocks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.init();
        ModBlocks.init();
        ModEntities.init();
        ModEvents.register();
        LOGGER.info("Lucky Blocks mod initialized!");
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
