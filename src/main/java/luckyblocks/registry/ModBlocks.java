package luckyblocks.registry;

import luckyblocks.Luckyblocks;
import luckyblocks.block.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ModBlocks {

    public static final Block COAL_LUCKY_BLOCK = register("coal_lucky_block",
            new CoalLuckyBlock(FabricBlockSettings.of().strength(3f).sounds(SoundType.STONE).requiresCorrectToolForDrops()));

    public static final Block IRON_LUCKY_BLOCK = register("iron_lucky_block",
            new IronLuckyBlock(FabricBlockSettings.of().strength(3f).sounds(SoundType.METAL).requiresCorrectToolForDrops()));

    public static final Block GOLD_LUCKY_BLOCK = register("gold_lucky_block",
            new GoldLuckyBlock(FabricBlockSettings.of().strength(3f).sounds(SoundType.METAL).requiresCorrectToolForDrops()));

    public static final Block DIAMOND_LUCKY_BLOCK = register("diamond_lucky_block",
            new DiamondLuckyBlock(FabricBlockSettings.of().strength(5f).sounds(SoundType.AMETHYST).requiresCorrectToolForDrops()));

    public static final Block EMERALD_LUCKY_BLOCK = register("emerald_lucky_block",
            new EmeraldLuckyBlock(FabricBlockSettings.of().strength(3f).sounds(SoundType.STONE).requiresCorrectToolForDrops()));

    public static final Block LAPIS_LUCKY_BLOCK = register("lapis_lucky_block",
            new LapisLuckyBlock(FabricBlockSettings.of().strength(3f).sounds(SoundType.STONE).requiresCorrectToolForDrops()));

    public static final Block REDSTONE_LUCKY_BLOCK = register("redstone_lucky_block",
            new RedstoneLuckyBlock(FabricBlockSettings.of().strength(3f).sounds(SoundType.STONE).requiresCorrectToolForDrops()));

    public static final Block COPPER_LUCKY_BLOCK = register("copper_lucky_block",
            new CopperLuckyBlock(FabricBlockSettings.of().strength(3f).sounds(SoundType.COPPER).requiresCorrectToolForDrops()));

    public static final Block AMETHYST_LUCKY_BLOCK = register("amethyst_lucky_block",
            new AmethystLuckyBlock(FabricBlockSettings.of().strength(3f).sounds(SoundType.AMETHYST).requiresCorrectToolForDrops()));

    private static Block register(String id, Block block) {
        ResourceLocation rl = Luckyblocks.id(id);
        Registry.register(BuiltInRegistries.BLOCK, rl, block);
        Registry.register(BuiltInRegistries.ITEM, rl, new BlockItem(block, new FabricItemSettings()));
        return block;
    }

    public static void init() {}
}
