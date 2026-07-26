package luckyblocks.util;

import luckyblocks.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;

import java.util.List;

public enum LuckyOutcomePool {

    COAL(List.of(
        // Good
        (level, pos, player) -> dropItems(level, pos, new ItemStack(Items.COAL, 16)),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(Items.TORCH, 32)),
        (level, pos, player) -> giveEffect(player, new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1200, 0)),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(Items.CHARCOAL, 24)),
        // Bad
        (level, pos, player) -> setFire(level, pos.above()),
        (level, pos, player) -> player.hurt(level.damageSources().inFire(), 4.0f),
        (level, pos, player) -> spawnMob(level, pos, EntityType.BLAZE),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(ModItems.CURSED_SHARD, 1))
    )),

    IRON(List.of(
        // Good
        (level, pos, player) -> dropItems(level, pos, new ItemStack(Items.IRON_INGOT, 16)),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(Items.IRON_SWORD, 1)),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(Items.IRON_CHESTPLATE, 1)),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(ModItems.BLESSED_INGOT, 2)),
        // Bad
        (level, pos, player) -> spawnMob(level, pos, EntityType.IRON_GOLEM),
        (level, pos, player) -> player.hurt(level.damageSources().generic(), 6.0f),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(ModItems.CURSED_SHARD, 2)),
        (level, pos, player) -> level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 2.0f, Level.ExplosionInteraction.BLOCK)
    )),

    GOLD(List.of(
        // Good
        (level, pos, player) -> dropItems(level, pos, new ItemStack(Items.GOLD_INGOT, 24)),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(Items.GOLDEN_APPLE, 3)),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(ModItems.LUCKY_COIN, 5)),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 1)),
        // Bad
        (level, pos, player) -> spawnMob(level, pos, EntityType.PIGLIN_BRUTE),
        (level, pos, player) -> spawnMob(level, pos, EntityType.ZOMBIFIED_PIGLIN),
        (level, pos, player) -> giveEffect(player, new MobEffectInstance(MobEffects.WEAKNESS, 600, 1)),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(ModItems.CURSED_SHARD, 3))
    )),

    DIAMOND(List.of(
        // Good
        (level, pos, player) -> dropItems(level, pos, new ItemStack(Items.DIAMOND, 16)),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(Items.DIAMOND_SWORD, 1)),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(Items.NETHERITE_SCRAP, 4)),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(ModItems.BLESSED_INGOT, 4)),
        // Bad
        (level, pos, player) -> level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 4.0f, Level.ExplosionInteraction.BLOCK),
        (level, pos, player) -> spawnMob(level, pos, EntityType.WARDEN),
        (level, pos, player) -> giveEffect(player, new MobEffectInstance(MobEffects.BLINDNESS, 200, 0)),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(ModItems.CURSED_SHARD, 5))
    )),

    EMERALD(List.of(
        // Good
        (level, pos, player) -> dropItems(level, pos, new ItemStack(Items.EMERALD, 20)),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(Items.EXPERIENCE_BOTTLE, 10)),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(ModItems.LUCKY_COIN, 3)),
        (level, pos, player) -> giveEffect(player, new MobEffectInstance(MobEffects.LUCK, 1200, 1)),
        // Bad
        (level, pos, player) -> spawnMob(level, pos, EntityType.VILLAGER),
        (level, pos, player) -> giveEffect(player, new MobEffectInstance(MobEffects.BAD_OMEN, 600, 0)),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(ModItems.CURSED_SHARD, 2)),
        (level, pos, player) -> giveEffect(player, new MobEffectInstance(MobEffects.UNLUCK, 600, 1))
    )),

    LAPIS(List.of(
        // Good
        (level, pos, player) -> dropItems(level, pos, new ItemStack(Items.LAPIS_LAZULI, 32)),
        (level, pos, player) -> giveEffect(player, new MobEffectInstance(MobEffects.NIGHT_VISION, 1200, 0)),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(ModItems.BLESSED_INGOT, 2)),
        (level, pos, player) -> giveEffect(player, new MobEffectInstance(MobEffects.LUCK, 600, 2)),
        // Bad
        (level, pos, player) -> giveEffect(player, new MobEffectInstance(MobEffects.CONFUSION, 400, 1)),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(ModItems.CURSED_SHARD, 3)),
        (level, pos, player) -> giveEffect(player, new MobEffectInstance(MobEffects.BLINDNESS, 300, 0)),
        (level, pos, player) -> spawnMob(level, pos, EntityType.WITCH)
    )),

    REDSTONE(List.of(
        // Good
        (level, pos, player) -> dropItems(level, pos, new ItemStack(Items.REDSTONE, 32)),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(Items.TNT, 4)),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(ModItems.LUCKY_COIN, 2)),
        (level, pos, player) -> giveEffect(player, new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 2)),
        // Bad
        (level, pos, player) -> level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 3.0f, Level.ExplosionInteraction.BLOCK),
        (level, pos, player) -> spawnMob(level, pos, EntityType.CREEPER),
        (level, pos, player) -> giveEffect(player, new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 2)),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(ModItems.CURSED_SHARD, 2))
    )),

    COPPER(List.of(
        // Good
        (level, pos, player) -> dropItems(level, pos, new ItemStack(Items.COPPER_INGOT, 20)),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(Items.LIGHTNING_ROD, 2)),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(ModItems.LUCKY_COIN, 2)),
        (level, pos, player) -> giveEffect(player, new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 1)),
        // Bad
        (level, pos, player) -> strikeLightning(level, pos),
        (level, pos, player) -> player.hurt(level.damageSources().lightningBolt(), 8.0f),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(ModItems.CURSED_SHARD, 2)),
        (level, pos, player) -> giveEffect(player, new MobEffectInstance(MobEffects.WEAKNESS, 400, 1))
    )),

    AMETHYST(List.of(
        // Good
        (level, pos, player) -> dropItems(level, pos, new ItemStack(Items.AMETHYST_SHARD, 24)),
        (level, pos, player) -> teleportPlayer(player),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(ModItems.BLESSED_INGOT, 3)),
        (level, pos, player) -> giveEffect(player, new MobEffectInstance(MobEffects.ABSORPTION, 600, 2)),
        // Bad
        (level, pos, player) -> teleportPlayerRandom(level, player),
        (level, pos, player) -> giveEffect(player, new MobEffectInstance(MobEffects.WITHER, 200, 1)),
        (level, pos, player) -> dropItems(level, pos, new ItemStack(ModItems.CURSED_SHARD, 3)),
        (level, pos, player) -> spawnMob(level, pos, EntityType.ENDERMAN)
    ));

    private final List<LuckyOutcome> outcomes;

    LuckyOutcomePool(List<LuckyOutcome> outcomes) {
        this.outcomes = outcomes;
    }

    public LuckyOutcome random(RandomSource random) {
        return outcomes.get(random.nextInt(outcomes.size()));
    }

    // --- Helpers ---

    private static void dropItems(ServerLevel level, BlockPos pos, ItemStack stack) {
        ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
        level.addFreshEntity(entity);
    }

    private static void giveEffect(Player player, MobEffectInstance effect) {
        player.addEffect(effect);
    }

    private static void spawnMob(ServerLevel level, BlockPos pos, EntityType<?> type) {
        type.spawn(level, (CompoundTag) null, null, pos.above(), MobSpawnType.TRIGGERED, false, false);
    }

    private static void setFire(ServerLevel level, BlockPos pos) {
        if (level.isEmptyBlock(pos)) {
            level.setBlockAndUpdate(pos, BaseFireBlock.getState(level, pos));
        }
    }

    private static void strikeLightning(ServerLevel level, BlockPos pos) {
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt != null) {
            bolt.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            level.addFreshEntity(bolt);
        }
    }

    private static void teleportPlayer(Player player) {
        player.teleportTo(player.getX(), player.getY() + 5, player.getZ());
    }

    private static void teleportPlayerRandom(ServerLevel level, Player player) {
        double x = player.getX() + (level.getRandom().nextDouble() - 0.5) * 32;
        double z = player.getZ() + (level.getRandom().nextDouble() - 0.5) * 32;
        player.teleportTo(x, player.getY(), z);
    }
}
