package luckyblocks.registry;

import luckyblocks.Luckyblocks;
import luckyblocks.entity.CursedMinerEntity;
import luckyblocks.entity.LuckySlimeEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {

    public static final EntityType<LuckySlimeEntity> LUCKY_SLIME = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Luckyblocks.id("lucky_slime"),
            FabricEntityTypeBuilder.create(MobCategory.CREATURE, LuckySlimeEntity::new)
                    .dimensions(EntityDimensions.scalable(1.02f, 1.02f))
                    .build()
    );

    public static final EntityType<CursedMinerEntity> CURSED_MINER = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Luckyblocks.id("cursed_miner"),
            FabricEntityTypeBuilder.create(MobCategory.MONSTER, CursedMinerEntity::new)
                    .dimensions(EntityDimensions.scalable(0.6f, 1.95f))
                    .build()
    );

    public static void init() {}
}
