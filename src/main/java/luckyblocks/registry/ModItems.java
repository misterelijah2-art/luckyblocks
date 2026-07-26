package luckyblocks.registry;

import luckyblocks.Luckyblocks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public class ModItems {

    public static final Item LUCKY_COIN = register("lucky_coin", new Item(new FabricItemSettings().stacksTo(64)));
    public static final Item CURSED_SHARD = register("cursed_shard", new Item(new FabricItemSettings().stacksTo(64)));
    public static final Item BLESSED_INGOT = register("blessed_ingot", new Item(new FabricItemSettings().stacksTo(64)));

    private static Item register(String id, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, Luckyblocks.id(id), item);
    }

    public static void init() {}
}
