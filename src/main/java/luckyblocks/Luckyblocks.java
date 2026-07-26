package luckyblocks;

import luckyblocks.registry.ModBlocks;
import luckyblocks.registry.ModEntities;
import luckyblocks.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Luckyblocks implements ModInitializer {
    public static final String MOD_ID = "luckyblocks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.init();
        ModBlocks.init();
        ModEntities.init();
        LOGGER.info("Lucky Blocks mod initialized!");
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
