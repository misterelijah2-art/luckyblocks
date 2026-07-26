package luckyblocks.event;

import luckyblocks.registry.ModBlocks;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class OreLootHandler {

    private static final float DROP_CHANCE = 0.10f;

    public static void register() {
        injectDrop("minecraft:blocks/coal_ore",                ModBlocks.COAL_LUCKY_BLOCK.asItem());
        injectDrop("minecraft:blocks/deepslate_coal_ore",      ModBlocks.COAL_LUCKY_BLOCK.asItem());

        injectDrop("minecraft:blocks/iron_ore",                ModBlocks.IRON_LUCKY_BLOCK.asItem());
        injectDrop("minecraft:blocks/deepslate_iron_ore",      ModBlocks.IRON_LUCKY_BLOCK.asItem());

        injectDrop("minecraft:blocks/gold_ore",                ModBlocks.GOLD_LUCKY_BLOCK.asItem());
        injectDrop("minecraft:blocks/deepslate_gold_ore",      ModBlocks.GOLD_LUCKY_BLOCK.asItem());
        injectDrop("minecraft:blocks/nether_gold_ore",         ModBlocks.GOLD_LUCKY_BLOCK.asItem());

        injectDrop("minecraft:blocks/diamond_ore",             ModBlocks.DIAMOND_LUCKY_BLOCK.asItem());
        injectDrop("minecraft:blocks/deepslate_diamond_ore",   ModBlocks.DIAMOND_LUCKY_BLOCK.asItem());

        injectDrop("minecraft:blocks/emerald_ore",             ModBlocks.EMERALD_LUCKY_BLOCK.asItem());
        injectDrop("minecraft:blocks/deepslate_emerald_ore",   ModBlocks.EMERALD_LUCKY_BLOCK.asItem());

        injectDrop("minecraft:blocks/lapis_ore",               ModBlocks.LAPIS_LUCKY_BLOCK.asItem());
        injectDrop("minecraft:blocks/deepslate_lapis_ore",     ModBlocks.LAPIS_LUCKY_BLOCK.asItem());

        injectDrop("minecraft:blocks/redstone_ore",            ModBlocks.REDSTONE_LUCKY_BLOCK.asItem());
        injectDrop("minecraft:blocks/deepslate_redstone_ore",  ModBlocks.REDSTONE_LUCKY_BLOCK.asItem());

        injectDrop("minecraft:blocks/copper_ore",              ModBlocks.COPPER_LUCKY_BLOCK.asItem());
        injectDrop("minecraft:blocks/deepslate_copper_ore",    ModBlocks.COPPER_LUCKY_BLOCK.asItem());

        injectDrop("minecraft:blocks/amethyst_cluster",        ModBlocks.AMETHYST_LUCKY_BLOCK.asItem());
    }

    private static void injectDrop(String tableId, Item item) {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (!source.isBuiltin()) return;
            if (!id.equals(new ResourceLocation(tableId))) return;

            // withPool() requires a LootPool.Builder, NOT a built LootPool
            LootPool.Builder pool = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(item))
                    .when(LootItemRandomChanceCondition.randomChance(DROP_CHANCE));

            tableBuilder.withPool(pool);
        });
    }
}
