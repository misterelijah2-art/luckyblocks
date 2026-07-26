package luckyblocks.event;

import luckyblocks.entity.CursedMinerEntity;
import luckyblocks.registry.ModEntities;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

public class ModEvents {
    public static void register() {
        FabricDefaultAttributeRegistry.register(ModEntities.CURSED_MINER, CursedMinerEntity.createAttributes());
        OreLootHandler.register();
    }
}
