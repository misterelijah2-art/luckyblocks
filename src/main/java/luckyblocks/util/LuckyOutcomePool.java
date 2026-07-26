package luckyblocks.util;

import luckyblocks.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public enum LuckyOutcomePool {

    COAL(List.of(
        // GOOD 1: coal rain shower
        (level, pos, player) -> {
            for (int i = 0; i < 20; i++) {
                double ox = (level.getRandom().nextDouble() - 0.5) * 8;
                double oz = (level.getRandom().nextDouble() - 0.5) * 8;
                dropItems(level, new BlockPos((int)(pos.getX()+ox), pos.getY()+1, (int)(pos.getZ()+oz)), new ItemStack(Items.COAL, 4));
            }
            sendMessage(player, "§6Coal shower!");
        },
        // GOOD 2: fire resist + speed + strength
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000, 0));
            giveEffect(player, new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 3000, 2));
            giveEffect(player, new MobEffectInstance(MobEffects.DAMAGE_BOOST, 3000, 1));
            sendMessage(player, "§6The coal ember awakens within you!");
        },
        // GOOD 3: Fortune V Eff V Unbreaking III diamond pickaxe
        (level, pos, player) -> {
            ItemStack pick = new ItemStack(Items.DIAMOND_PICKAXE);
            pick.enchant(Enchantments.BLOCK_EFFICIENCY, 5);
            pick.enchant(Enchantments.UNBREAKING, 3);
            pick.enchant(Enchantments.BLOCK_FORTUNE, 3);
            dropItems(level, pos, pick);
            sendMessage(player, "§bA miner's dream!");
        },
        // GOOD 4: 5 coal blocks
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.COAL_BLOCK, 5));
            sendMessage(player, "§7Coal block stash!");
        },
        // GOOD 5: campfire + smoker + blast furnace drop (full smelting setup)
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.CAMPFIRE, 2));
            dropItems(level, pos, new ItemStack(Items.SMOKER, 1));
            dropItems(level, pos, new ItemStack(Items.BLAST_FURNACE, 1));
            dropItems(level, pos, new ItemStack(Items.COAL, 64));
            sendMessage(player, "§6Smelting paradise!");
        },
        // GOOD 6: night vision + saturation for 10 min
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.NIGHT_VISION, 12000, 0));
            giveEffect(player, new MobEffectInstance(MobEffects.SATURATION, 12000, 2));
            giveEffect(player, new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 6000, 2));
            sendMessage(player, "§6Miner's blessing!");
        },
        // GOOD 7: full cooked food feast
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.COOKED_BEEF, 32));
            dropItems(level, pos, new ItemStack(Items.COOKED_PORKCHOP, 32));
            dropItems(level, pos, new ItemStack(Items.COOKED_CHICKEN, 32));
            dropItems(level, pos, new ItemStack(Items.GOLDEN_CARROT, 16));
            sendMessage(player, "§6Feast from the flames!");
        },
        // GOOD 8: 4 TNT minecarts drop
        (level, pos, player) -> {
            for (int i = 0; i < 4; i++) dropItems(level, pos, new ItemStack(Items.TNT_MINECART));
            dropItems(level, pos, new ItemStack(Items.MINECART, 4));
            sendMessage(player, "§6Miner's special delivery!");
        },

        // BAD 1: ring of fire
        (level, pos, player) -> {
            for (int dx = -3; dx <= 3; dx++)
                for (int dz = -3; dz <= 3; dz++)
                    if (Math.abs(dx) == 3 || Math.abs(dz) == 3) {
                        BlockPos fp = player.blockPosition().offset(dx, 0, dz);
                        if (level.isEmptyBlock(fp)) level.setBlockAndUpdate(fp, BaseFireBlock.getState(level, fp));
                    }
            sendMessage(player, "§4Ring of fire!");
        },
        // BAD 2: 5 blazes
        (level, pos, player) -> {
            for (int i = 0; i < 5; i++)
                spawnMob(level, pos.offset(level.getRandom().nextInt(5)-2, 1, level.getRandom().nextInt(5)-2), EntityType.BLAZE);
            sendMessage(player, "§4Blaze army!");
        },
        // BAD 3: on fire + damage
        (level, pos, player) -> {
            player.setSecondsOnFire(15);
            player.hurt(level.damageSources().inFire(), 10.0f);
            sendMessage(player, "§4You've been burned!");
        },
        // BAD 4: 3x3 lava floor
        (level, pos, player) -> {
            for (int dx = -1; dx <= 1; dx++)
                for (int dz = -1; dz <= 1; dz++)
                    level.setBlockAndUpdate(player.blockPosition().offset(dx, -1, dz), Blocks.LAVA.defaultBlockState());
            sendMessage(player, "§4The ground melts beneath you!");
        },
        // BAD 5: coal blocks fall on player from above
        (level, pos, player) -> {
            for (int i = 0; i < 10; i++) {
                double ox = (level.getRandom().nextDouble()-0.5)*4;
                double oz = (level.getRandom().nextDouble()-0.5)*4;
                BlockPos drop = player.blockPosition().offset((int)ox, 10, (int)oz);
                level.setBlockAndUpdate(drop, Blocks.COAL_BLOCK.defaultBlockState());
            }
            sendMessage(player, "§4Coal avalanche!");
        },
        // BAD 6: coal block prison
        (level, pos, player) -> {
            for (int dx = -1; dx <= 1; dx++)
                for (int dy = 0; dy <= 2; dy++)
                    for (int dz = -1; dz <= 1; dz++)
                        if (level.isEmptyBlock(player.blockPosition().offset(dx,dy,dz)))
                            level.setBlockAndUpdate(player.blockPosition().offset(dx,dy,dz), Blocks.COAL_BLOCK.defaultBlockState());
            sendMessage(player, "§4Coal tomb!");
        },
        // BAD 7: wither effect + hunger
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.WITHER, 300, 3));
            giveEffect(player, new MobEffectInstance(MobEffects.HUNGER, 1200, 4));
            giveEffect(player, new MobEffectInstance(MobEffects.WEAKNESS, 600, 2));
            sendMessage(player, "§4Coal ash sickness!");
        },
        // BAD 8: magma blocks replace floor 5x5
        (level, pos, player) -> {
            for (int dx = -2; dx <= 2; dx++)
                for (int dz = -2; dz <= 2; dz++)
                    level.setBlockAndUpdate(player.blockPosition().offset(dx, -1, dz), Blocks.MAGMA_BLOCK.defaultBlockState());
            sendMessage(player, "§4The earth burns below!");
        }
    )),

    IRON(List.of(
        // GOOD 1: enchanted iron armor set
        (level, pos, player) -> {
            for (ItemStack s : List.of(new ItemStack(Items.IRON_HELMET), new ItemStack(Items.IRON_CHESTPLATE),
                    new ItemStack(Items.IRON_LEGGINGS), new ItemStack(Items.IRON_BOOTS))) {
                s.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 4);
                dropItems(level, pos, s);
            }
            sendMessage(player, "§7Iron Knight rises!");
        },
        // GOOD 2: 32 ingots + iron golem
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.IRON_INGOT, 32));
            spawnMob(level, pos, EntityType.IRON_GOLEM);
            sendMessage(player, "§7Your iron guardian awakens!");
        },
        // GOOD 3: resistance IV + strength III
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 3600, 3));
            giveEffect(player, new MobEffectInstance(MobEffects.DAMAGE_BOOST, 3600, 2));
            sendMessage(player, "§7Iron will!");
        },
        // GOOD 4: netherite sword Sharp V Fire II
        (level, pos, player) -> {
            ItemStack sword = new ItemStack(Items.NETHERITE_SWORD);
            sword.enchant(Enchantments.SHARPNESS, 5);
            sword.enchant(Enchantments.FIRE_ASPECT, 2);
            dropItems(level, pos, sword);
            sendMessage(player, "§6The strongest blade!");
        },
        // GOOD 5: iron block platform builds under player
        (level, pos, player) -> {
            for (int dx = -3; dx <= 3; dx++)
                for (int dz = -3; dz <= 3; dz++)
                    level.setBlockAndUpdate(player.blockPosition().offset(dx,-1,dz), Blocks.IRON_BLOCK.defaultBlockState());
            dropItems(level, pos, new ItemStack(Items.IRON_INGOT, 64));
            sendMessage(player, "§7Iron fortress rises!");
        },
        // GOOD 6: full crossbow + shield kit
        (level, pos, player) -> {
            ItemStack crossbow = new ItemStack(Items.CROSSBOW);
            crossbow.enchant(Enchantments.QUICK_CHARGE, 3);
            crossbow.enchant(Enchantments.MULTISHOT, 1);
            ItemStack shield = new ItemStack(Items.SHIELD);
            shield.enchant(Enchantments.UNBREAKING, 3);
            dropItems(level, pos, crossbow);
            dropItems(level, pos, shield);
            dropItems(level, pos, new ItemStack(Items.ARROW, 64));
            sendMessage(player, "§7Iron warrior kit!");
        },
        // GOOD 7: regeneration + absorption + health boost
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.REGENERATION, 2400, 4));
            giveEffect(player, new MobEffectInstance(MobEffects.ABSORPTION, 2400, 10));
            giveEffect(player, new MobEffectInstance(MobEffects.HEALTH_BOOST, 6000, 9));
            sendMessage(player, "§7Iron heart!");
        },
        // GOOD 8: anvil + enchanting table + grindstone drop
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.ANVIL, 2));
            dropItems(level, pos, new ItemStack(Items.ENCHANTING_TABLE));
            dropItems(level, pos, new ItemStack(Items.GRINDSTONE));
            dropItems(level, pos, new ItemStack(Items.IRON_INGOT, 16));
            sendMessage(player, "§7The blacksmith's gift!");
        },

        // BAD 1: 10 iron golems
        (level, pos, player) -> {
            for (int i = 0; i < 10; i++)
                spawnMob(level, pos.offset(level.getRandom().nextInt(7)-3, 0, level.getRandom().nextInt(7)-3), EntityType.IRON_GOLEM);
            sendMessage(player, "§4IRON ARMY!");
        },
        // BAD 2: size 8 explosion
        (level, pos, player) -> {
            level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 8.0f, Level.ExplosionInteraction.BLOCK);
            sendMessage(player, "§4BOOM!");
        },
        // BAD 3: iron bar prison
        (level, pos, player) -> {
            for (int dx = -1; dx <= 1; dx++)
                for (int dy = 0; dy <= 2; dy++)
                    for (int dz = -1; dz <= 1; dz++)
                        if (level.isEmptyBlock(player.blockPosition().offset(dx,dy,dz)))
                            level.setBlockAndUpdate(player.blockPosition().offset(dx,dy,dz), Blocks.IRON_BARS.defaultBlockState());
            sendMessage(player, "§4Iron prison!");
        },
        // BAD 4: all armor durability destroyed
        (level, pos, player) -> {
            for (ItemStack s : player.getArmorSlots())
                if (!s.isEmpty()) s.setDamageValue(s.getMaxDamage() - 1);
            sendMessage(player, "§4Your armor crumbles!");
        },
        // BAD 5: anvil drops on player from y+20
        (level, pos, player) -> {
            for (int i = 0; i < 5; i++) {
                BlockPos ap = player.blockPosition().offset(
                    level.getRandom().nextInt(3)-1, 20, level.getRandom().nextInt(3)-1);
                level.setBlockAndUpdate(ap, Blocks.ANVIL.defaultBlockState());
            }
            sendMessage(player, "§4ANVIL RAIN!");
        },
        // BAD 6: every item in hotbar renamed "Junk"
        (level, pos, player) -> {
            for (int i = 0; i < 9; i++) {
                ItemStack s = player.getInventory().getItem(i);
                if (!s.isEmpty()) s.setHoverName(Component.literal("§4Junk"));
            }
            sendMessage(player, "§4The iron curse trashes your items!");
        },
        // BAD 7: mining fatigue V + slowness IV
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 2400, 4));
            giveEffect(player, new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2400, 3));
            giveEffect(player, new MobEffectInstance(MobEffects.WEAKNESS, 2400, 3));
            sendMessage(player, "§4Iron fatigue!");
        },
        // BAD 8: spider jockey army (5 skeleton + spider)
        (level, pos, player) -> {
            for (int i = 0; i < 5; i++) {
                spawnMob(level, pos.offset(level.getRandom().nextInt(5)-2, 0, level.getRandom().nextInt(5)-2), EntityType.SPIDER);
                spawnMob(level, pos.offset(level.getRandom().nextInt(5)-2, 1, level.getRandom().nextInt(5)-2), EntityType.SKELETON);
            }
            sendMessage(player, "§4Spider jockey army!");
        }
    )),

    GOLD(List.of(
        // GOOD 1: 5 enchanted golden apples
        (level, pos, player) -> {
            for (int i = 0; i < 5; i++) dropItems(level, pos, new ItemStack(Items.ENCHANTED_GOLDEN_APPLE));
            sendMessage(player, "§eGolden rain!");
        },
        // GOOD 2: all positive effects maxed
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.REGENERATION, 2400, 4));
            giveEffect(player, new MobEffectInstance(MobEffects.ABSORPTION, 2400, 4));
            giveEffect(player, new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2400, 4));
            giveEffect(player, new MobEffectInstance(MobEffects.LUCK, 6000, 2));
            sendMessage(player, "§6Golden blessing!");
        },
        // GOOD 3: 64 gold + 3 lucky coins
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.GOLD_INGOT, 64));
            dropItems(level, pos, new ItemStack(ModItems.LUCKY_COIN, 3));
        },
        // GOOD 4: enchanted golden sword
        (level, pos, player) -> {
            ItemStack sword = new ItemStack(Items.GOLDEN_SWORD);
            sword.enchant(Enchantments.SHARPNESS, 5);
            sword.enchant(Enchantments.KNOCKBACK, 2);
            sword.enchant(Enchantments.FIRE_ASPECT, 2);
            sword.enchant(Enchantments.LOOTING, 3);
            dropItems(level, pos, sword);
            sendMessage(player, "§6The golden blade!");
        },
        // GOOD 5: gold block pyramid builds around player
        (level, pos, player) -> {
            for (int dx = -2; dx <= 2; dx++)
                for (int dz = -2; dz <= 2; dz++)
                    level.setBlockAndUpdate(player.blockPosition().offset(dx,-1,dz), Blocks.GOLD_BLOCK.defaultBlockState());
            sendMessage(player, "§eGold platform!");
        },
        // GOOD 6: +50 XP levels + luck IV
        (level, pos, player) -> {
            player.giveExperienceLevels(50);
            giveEffect(player, new MobEffectInstance(MobEffects.LUCK, 12000, 3));
            sendMessage(player, "§eGolden fortune! +50 levels!");
        },
        // GOOD 7: piglin trades — drop 16 random gold trade items
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.CRYING_OBSIDIAN, 8));
            dropItems(level, pos, new ItemStack(Items.GILDED_BLACKSTONE, 16));
            dropItems(level, pos, new ItemStack(Items.SPECTRAL_ARROW, 32));
            dropItems(level, pos, new ItemStack(Items.FIRE_CHARGE, 8));
            dropItems(level, pos, new ItemStack(Items.SOUL_SAND, 10));
            sendMessage(player, "§ePiglin tribute!");
        },
        // GOOD 8: totem of undying x3
        (level, pos, player) -> {
            for (int i = 0; i < 3; i++) dropItems(level, pos, new ItemStack(Items.TOTEM_OF_UNDYING));
            sendMessage(player, "§eTriple totem!");
        },

        // BAD 1: 20 piglin brutes
        (level, pos, player) -> {
            for (int i = 0; i < 20; i++) spawnMob(level, pos, EntityType.PIGLIN_BRUTE);
            sendMessage(player, "§4PIGLIN RAID!");
        },
        // BAD 2: entire inventory dropped
        (level, pos, player) -> {
            player.getInventory().dropAll();
            sendMessage(player, "§4The gold curse stole everything!");
        },
        // BAD 3: floor removed 3x3
        (level, pos, player) -> {
            for (int dx = -1; dx <= 1; dx++)
                for (int dz = -1; dz <= 1; dz++)
                    level.setBlockAndUpdate(player.blockPosition().offset(dx,-1,dz), Blocks.AIR.defaultBlockState());
            sendMessage(player, "§4The floor is gone!");
        },
        // BAD 4: 10 lightning strikes
        (level, pos, player) -> {
            for (int i = 0; i < 10; i++) strikeLightningAt(level, player.blockPosition());
            sendMessage(player, "§4Zeus is angry!");
        },
        // BAD 5: gold block prison
        (level, pos, player) -> {
            for (int dx = -1; dx <= 1; dx++)
                for (int dy = 0; dy <= 2; dy++)
                    for (int dz = -1; dz <= 1; dz++)
                        if (level.isEmptyBlock(player.blockPosition().offset(dx,dy,dz)))
                            level.setBlockAndUpdate(player.blockPosition().offset(dx,dy,dz), Blocks.GOLD_BLOCK.defaultBlockState());
            sendMessage(player, "§4Gold tomb! (Worth it?)");
        },
        // BAD 6: 15 zombie piglins + 5 piglin brutes
        (level, pos, player) -> {
            for (int i = 0; i < 15; i++) spawnMob(level, pos.offset(level.getRandom().nextInt(9)-4, 0, level.getRandom().nextInt(9)-4), EntityType.ZOMBIFIED_PIGLIN);
            for (int i = 0; i < 5; i++) spawnMob(level, pos, EntityType.PIGLIN_BRUTE);
            sendMessage(player, "§4NETHER HORDE!");
        },
        // BAD 7: nausea + blindness + levitation
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.CONFUSION, 400, 5));
            giveEffect(player, new MobEffectInstance(MobEffects.BLINDNESS, 400, 2));
            giveEffect(player, new MobEffectInstance(MobEffects.LEVITATION, 100, 4));
            sendMessage(player, "§4Golden delirium!");
        },
        // BAD 8: all XP levels stolen
        (level, pos, player) -> {
            player.giveExperienceLevels(-player.experienceLevel);
            sendMessage(player, "§4The gold curse drained your experience!");
        }
    )),

    DIAMOND(List.of(
        // GOOD 1: full netherite armor
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.NETHERITE_HELMET));
            dropItems(level, pos, new ItemStack(Items.NETHERITE_CHESTPLATE));
            dropItems(level, pos, new ItemStack(Items.NETHERITE_LEGGINGS));
            dropItems(level, pos, new ItemStack(Items.NETHERITE_BOOTS));
            sendMessage(player, "§bNetherite blessing!");
        },
        // GOOD 2: 64 diamonds
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.DIAMOND, 64));
            sendMessage(player, "§bDiamond jackpot!");
        },
        // GOOD 3: max diamond sword + infinity bow
        (level, pos, player) -> {
            ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
            sword.enchant(Enchantments.SHARPNESS, 5); sword.enchant(Enchantments.UNBREAKING, 3);
            ItemStack bow = new ItemStack(Items.BOW);
            bow.enchant(Enchantments.POWER_ARROWS, 5); bow.enchant(Enchantments.PUNCH_ARROWS, 2);
            bow.enchant(Enchantments.INFINITY_ARROWS, 1);
            dropItems(level, pos, sword); dropItems(level, pos, bow);
            sendMessage(player, "§bThe ultimate arsenal!");
        },
        // GOOD 4: GOD MODE
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 255));
            giveEffect(player, new MobEffectInstance(MobEffects.REGENERATION, 6000, 4));
            giveEffect(player, new MobEffectInstance(MobEffects.ABSORPTION, 6000, 10));
            sendMessage(player, "§bDIAMOND GOD MODE!");
        },
        // GOOD 5: diamond block tower 10 high + 64 diamonds
        (level, pos, player) -> {
            for (int i = 1; i <= 10; i++)
                level.setBlockAndUpdate(pos.above(i), Blocks.DIAMOND_BLOCK.defaultBlockState());
            dropItems(level, pos, new ItemStack(Items.DIAMOND, 64));
            sendMessage(player, "§bDiamond monument!");
        },
        // GOOD 6: max netherite pickaxe + silk touch shovel
        (level, pos, player) -> {
            ItemStack pick = new ItemStack(Items.NETHERITE_PICKAXE);
            pick.enchant(Enchantments.BLOCK_EFFICIENCY, 5);
            pick.enchant(Enchantments.UNBREAKING, 3);
            pick.enchant(Enchantments.BLOCK_FORTUNE, 3);
            pick.enchant(Enchantments.MENDING, 1);
            ItemStack shovel = new ItemStack(Items.NETHERITE_SHOVEL);
            shovel.enchant(Enchantments.SILK_TOUCH, 1);
            dropItems(level, pos, pick); dropItems(level, pos, shovel);
            sendMessage(player, "§bNetherite miner set!");
        },
        // GOOD 7: beacon drops + diamond blocks
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.BEACON));
            dropItems(level, pos, new ItemStack(Items.DIAMOND_BLOCK, 9));
            dropItems(level, pos, new ItemStack(Items.OBSIDIAN, 16));
            sendMessage(player, "§bBeacon of the diamonds!");
        },
        // GOOD 8: instant health x5 + max food
        (level, pos, player) -> {
            for (int i = 0; i < 5; i++) giveEffect(player, new MobEffectInstance(MobEffects.HEAL, 1, 4));
            giveEffect(player, new MobEffectInstance(MobEffects.SATURATION, 600, 10));
            giveEffect(player, new MobEffectInstance(MobEffects.ABSORPTION, 6000, 20));
            sendMessage(player, "§bDiamond vitality!");
        },

        // BAD 1: 3 wardens
        (level, pos, player) -> {
            for (int i = 0; i < 3; i++) spawnMob(level, pos, EntityType.WARDEN);
            sendMessage(player, "§4THE ANCIENT CITY OPENS!");
        },
        // BAD 2: size 15 nuclear explosion
        (level, pos, player) -> {
            level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 15.0f, Level.ExplosionInteraction.BLOCK);
            sendMessage(player, "§4NUCLEAR DIAMOND!");
        },
        // BAD 3: void drop y=-100
        (level, pos, player) -> {
            player.teleportTo(player.getX(), -100, player.getZ());
            sendMessage(player, "§4Into the void you go!");
        },
        // BAD 4: all negative effects max
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.WITHER, 400, 4));
            giveEffect(player, new MobEffectInstance(MobEffects.BLINDNESS, 400, 2));
            giveEffect(player, new MobEffectInstance(MobEffects.WEAKNESS, 400, 4));
            giveEffect(player, new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 10));
            giveEffect(player, new MobEffectInstance(MobEffects.POISON, 400, 4));
            sendMessage(player, "§4Diamond curse!");
        },
        // BAD 5: obsidian cage 5x5x5
        (level, pos, player) -> {
            for (int dx = -2; dx <= 2; dx++)
                for (int dy = -1; dy <= 3; dy++)
                    for (int dz = -2; dz <= 2; dz++)
                        if (Math.abs(dx)==2 || Math.abs(dz)==2 || dy==-1 || dy==3)
                            level.setBlockAndUpdate(player.blockPosition().offset(dx,dy,dz), Blocks.OBSIDIAN.defaultBlockState());
            sendMessage(player, "§4Obsidian tomb!");
        },
        // BAD 6: 5 ender dragons worth of chaos — spawn 10 endermen + 5 shulkers
        (level, pos, player) -> {
            for (int i = 0; i < 10; i++) spawnMob(level, pos.offset(level.getRandom().nextInt(9)-4, 0, level.getRandom().nextInt(9)-4), EntityType.ENDERMAN);
            for (int i = 0; i < 5; i++) spawnMob(level, pos, EntityType.SHULKER);
            sendMessage(player, "§4END INVASION!");
        },
        // BAD 7: random teleport 1000 blocks away
        (level, pos, player) -> {
            double angle = level.getRandom().nextDouble() * Math.PI * 2;
            double dist = 500 + level.getRandom().nextDouble() * 500;
            player.teleportTo(player.getX() + Math.cos(angle)*dist, player.getY(), player.getZ() + Math.sin(angle)*dist);
            sendMessage(player, "§4Diamond banishment!");
        },
        // BAD 8: all items in inventory lose max durability (set to 1)
        (level, pos, player) -> {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack s = player.getInventory().getItem(i);
                if (!s.isEmpty() && s.isDamageableItem()) s.setDamageValue(s.getMaxDamage()-1);
            }
            sendMessage(player, "§4Diamond decay!");
        }
    )),

    EMERALD(List.of(
        // GOOD 1: 64 emeralds + XP bottles + lucky coins
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.EMERALD, 64));
            dropItems(level, pos, new ItemStack(Items.EXPERIENCE_BOTTLE, 20));
            dropItems(level, pos, new ItemStack(ModItems.LUCKY_COIN, 5));
            sendMessage(player, "§aThe village rewards you!");
        },
        // GOOD 2: +30 XP levels
        (level, pos, player) -> {
            player.giveExperienceLevels(30);
            sendMessage(player, "§aEXPERIENCE SURGE! +30 levels!");
        },
        // GOOD 3: luck IV + enchanted books
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.LUCK, 12000, 3));
            for (int i = 0; i < 5; i++) dropItems(level, pos, new ItemStack(Items.ENCHANTED_BOOK));
            sendMessage(player, "§aFortune smiles upon you!");
        },
        // GOOD 4: wandering traders
        (level, pos, player) -> {
            for (int i = 0; i < 3; i++) spawnMob(level, pos, EntityType.WANDERING_TRADER);
            sendMessage(player, "§aThe merchants arrive!");
        },
        // GOOD 5: emerald block palace floor
        (level, pos, player) -> {
            for (int dx = -3; dx <= 3; dx++)
                for (int dz = -3; dz <= 3; dz++)
                    level.setBlockAndUpdate(player.blockPosition().offset(dx,-1,dz), Blocks.EMERALD_BLOCK.defaultBlockState());
            sendMessage(player, "§aEmerald throne!");
        },
        // GOOD 6: hero of the village + max regeneration + saturation
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 12000, 5));
            giveEffect(player, new MobEffectInstance(MobEffects.REGENERATION, 6000, 4));
            giveEffect(player, new MobEffectInstance(MobEffects.SATURATION, 6000, 5));
            sendMessage(player, "§aVillage hero!");
        },
        // GOOD 7: rare loot crate (nether star + dragon egg + elytra)
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.NETHER_STAR));
            dropItems(level, pos, new ItemStack(Items.DRAGON_EGG));
            dropItems(level, pos, new ItemStack(Items.ELYTRA));
            sendMessage(player, "§aLEGENDARY LOOT!");
        },
        // GOOD 8: +100 XP levels instant
        (level, pos, player) -> {
            player.giveExperienceLevels(100);
            giveEffect(player, new MobEffectInstance(MobEffects.LUCK, 6000, 5));
            sendMessage(player, "§aEMERALD JACKPOT! +100 levels!");
        },

        // BAD 1: pillager + ravager raid
        (level, pos, player) -> {
            for (int i = 0; i < 8; i++) spawnMob(level, pos, EntityType.PILLAGER);
            for (int i = 0; i < 3; i++) spawnMob(level, pos, EntityType.RAVAGER);
            sendMessage(player, "§4RAID!");
        },
        // BAD 2: bad omen V + unluck IV
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.BAD_OMEN, 999999, 4));
            giveEffect(player, new MobEffectInstance(MobEffects.UNLUCK, 6000, 4));
            sendMessage(player, "§4CURSED BY THE VILLAGE!");
        },
        // BAD 3: 1HP + weakness IV
        (level, pos, player) -> {
            player.setHealth(1.0f);
            giveEffect(player, new MobEffectInstance(MobEffects.WEAKNESS, 2400, 4));
            sendMessage(player, "§4The emerald drained your life!");
        },
        // BAD 4: 10 evokers
        (level, pos, player) -> {
            for (int i = 0; i < 10; i++) spawnMob(level, pos, EntityType.EVOKER);
            sendMessage(player, "§4THE EVOKERS COME FOR YOU!");
        },
        // BAD 5: all XP wiped
        (level, pos, player) -> {
            player.giveExperienceLevels(-player.experienceLevel);
            sendMessage(player, "§4All experience drained!");
        },
        // BAD 6: emerald blocks trap
        (level, pos, player) -> {
            for (int dx = -1; dx <= 1; dx++)
                for (int dy = 0; dy <= 2; dy++)
                    for (int dz = -1; dz <= 1; dz++)
                        if (level.isEmptyBlock(player.blockPosition().offset(dx,dy,dz)))
                            level.setBlockAndUpdate(player.blockPosition().offset(dx,dy,dz), Blocks.EMERALD_BLOCK.defaultBlockState());
            sendMessage(player, "§4Emerald prison!");
        },
        // BAD 7: 5 vindicators + 5 vexes
        (level, pos, player) -> {
            for (int i = 0; i < 5; i++) spawnMob(level, pos, EntityType.VINDICATOR);
            for (int i = 0; i < 5; i++) spawnMob(level, pos, EntityType.VEX);
            sendMessage(player, "§4Emerald wrath!");
        },
        // BAD 8: floor drops 20 blocks (air column under player)
        (level, pos, player) -> {
            for (int dy = 0; dy >= -20; dy--)
                level.setBlockAndUpdate(player.blockPosition().offset(0,dy,0), Blocks.AIR.defaultBlockState());
            sendMessage(player, "§4Emerald pit!");
        }
    )),

    LAPIS(List.of(
        // GOOD 1: hotbar gets Prot IV
        (level, pos, player) -> {
            for (int i = 0; i < 9; i++) {
                ItemStack s = player.getInventory().getItem(i);
                if (!s.isEmpty() && s.isEnchantable()) s.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 4);
            }
            sendMessage(player, "§9Lapis enchantment surge!");
        },
        // GOOD 2: permanent night vision + luck + speed
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.NIGHT_VISION, 999999, 0));
            giveEffect(player, new MobEffectInstance(MobEffects.LUCK, 999999, 3));
            giveEffect(player, new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 999999, 1));
            sendMessage(player, "§9The lapis vision is eternal!");
        },
        // GOOD 3: 64 lapis + 5 enchanted books
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.LAPIS_LAZULI, 64));
            for (int i = 0; i < 5; i++) dropItems(level, pos, new ItemStack(Items.ENCHANTED_BOOK));
            sendMessage(player, "§9Enchanting fortune!");
        },
        // GOOD 4: unbreaking 3 on all inventory
        (level, pos, player) -> {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack s = player.getInventory().getItem(i);
                if (!s.isEmpty() && s.isEnchantable()) s.enchant(Enchantments.UNBREAKING, 3);
            }
            sendMessage(player, "§9Unbreakable!");
        },
        // GOOD 5: lapis block floor + enchanting room build
        (level, pos, player) -> {
            for (int dx = -2; dx <= 2; dx++)
                for (int dz = -2; dz <= 2; dz++)
                    level.setBlockAndUpdate(player.blockPosition().offset(dx,-1,dz), Blocks.LAPIS_BLOCK.defaultBlockState());
            dropItems(level, pos, new ItemStack(Items.ENCHANTING_TABLE));
            dropItems(level, pos, new ItemStack(Items.BOOKSHELF, 15));
            sendMessage(player, "§9Enchanting sanctum!");
        },
        // GOOD 6: mending on all worn armor
        (level, pos, player) -> {
            for (ItemStack s : player.getArmorSlots())
                if (!s.isEmpty() && s.isEnchantable()) s.enchant(Enchantments.MENDING, 1);
            sendMessage(player, "§9Mending blessing!");
        },
        // GOOD 7: 20 enchanted books shower
        (level, pos, player) -> {
            for (int i = 0; i < 20; i++) dropItems(level, pos, new ItemStack(Items.ENCHANTED_BOOK));
            sendMessage(player, "§9Book storm!");
        },
        // GOOD 8: max sharpness + looting on mainhand item
        (level, pos, player) -> {
            ItemStack main = player.getMainHandItem();
            if (!main.isEmpty() && main.isEnchantable()) {
                main.enchant(Enchantments.SHARPNESS, 5);
                main.enchant(Enchantments.LOOTING, 3);
                main.enchant(Enchantments.MENDING, 1);
            } else {
                dropItems(level, pos, new ItemStack(Items.LAPIS_LAZULI, 64));
            }
            sendMessage(player, "§9Lapis weapon enchant!");
        },

        // BAD 1: all effects wiped + confusion + blindness
        (level, pos, player) -> {
            player.removeAllEffects();
            giveEffect(player, new MobEffectInstance(MobEffects.CONFUSION, 600, 5));
            giveEffect(player, new MobEffectInstance(MobEffects.BLINDNESS, 600, 0));
            giveEffect(player, new MobEffectInstance(MobEffects.WEAKNESS, 600, 4));
            sendMessage(player, "§4Lapis confusion!");
        },
        // BAD 2: all enchantments stripped
        (level, pos, player) -> {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                player.getInventory().getItem(i).removeTagKey("Enchantments");
                player.getInventory().getItem(i).removeTagKey("StoredEnchantments");
            }
            sendMessage(player, "§4All enchantments erased!");
        },
        // BAD 3: 20 witches
        (level, pos, player) -> {
            for (int i = 0; i < 20; i++) spawnMob(level, pos, EntityType.WITCH);
            sendMessage(player, "§4WITCH COVEN!");
        },
        // BAD 4: teleport 500 blocks away
        (level, pos, player) -> {
            double angle = level.getRandom().nextDouble() * Math.PI * 2;
            double dist = 200 + level.getRandom().nextDouble() * 300;
            player.teleportTo(player.getX() + Math.cos(angle)*dist, player.getY(), player.getZ() + Math.sin(angle)*dist);
            sendMessage(player, "§4LAPIS BANISHMENT!");
        },
        // BAD 5: lapis blocks fall from sky 20 of them
        (level, pos, player) -> {
            for (int i = 0; i < 20; i++)
                level.setBlockAndUpdate(player.blockPosition().offset(
                    level.getRandom().nextInt(5)-2, 15+level.getRandom().nextInt(5),
                    level.getRandom().nextInt(5)-2), Blocks.LAPIS_BLOCK.defaultBlockState());
            sendMessage(player, "§4Lapis avalanche!");
        },
        // BAD 6: random teleport to y=300 (fall to death)
        (level, pos, player) -> {
            player.teleportTo(player.getX(), 300, player.getZ());
            sendMessage(player, "§4LAPIS LAUNCH!");
        },
        // BAD 7: mining fatigue + blindness permanent
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 999999, 4));
            giveEffect(player, new MobEffectInstance(MobEffects.BLINDNESS, 999999, 0));
            sendMessage(player, "§4Lapis curse! (Good luck removing that...)");
        },
        // BAD 8: 10 phantoms + 10 vexes
        (level, pos, player) -> {
            for (int i = 0; i < 10; i++) spawnMob(level, pos, EntityType.PHANTOM);
            for (int i = 0; i < 10; i++) spawnMob(level, pos, EntityType.VEX);
            sendMessage(player, "§4SPECTRAL SWARM!");
        }
    )),

    REDSTONE(List.of(
        // GOOD 1: 20 primed TNT party
        (level, pos, player) -> {
            for (int i = 0; i < 20; i++) {
                PrimedTnt tnt = new PrimedTnt(level,
                    pos.getX()+(level.getRandom().nextDouble()-0.5)*10,
                    pos.getY()+5,
                    pos.getZ()+(level.getRandom().nextDouble()-0.5)*10, null);
                tnt.setFuse(60+level.getRandom().nextInt(40));
                level.addFreshEntity(tnt);
            }
            sendMessage(player, "§cRedstone TNT party!");
        },
        // GOOD 2: speed XI + jump VI
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 6000, 10));
            giveEffect(player, new MobEffectInstance(MobEffects.JUMP, 6000, 5));
            sendMessage(player, "§cREDSTONE OVERDRIVE!");
        },
        // GOOD 3: redstone engineering kit
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.REDSTONE, 64));
            dropItems(level, pos, new ItemStack(Items.OBSERVER, 10));
            dropItems(level, pos, new ItemStack(Items.PISTON, 10));
            dropItems(level, pos, new ItemStack(Items.STICKY_PISTON, 5));
            dropItems(level, pos, new ItemStack(Items.DISPENSER, 5));
            dropItems(level, pos, new ItemStack(Items.DROPPER, 5));
        },
        // GOOD 4: lightning purge all nearby mobs
        (level, pos, player) -> {
            level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class,
                player.getBoundingBox().inflate(15)).forEach(e -> {
                    if (!(e instanceof Player)) strikeLightningAt(level, e.blockPosition());
                });
            sendMessage(player, "§cLightning purge!");
        },
        // GOOD 5: flying machine launch (velocity burst upward + elytra)
        (level, pos, player) -> {
            player.setDeltaMovement(new Vec3(0, 8.0, 0));
            player.hurtMarked = true;
            ItemStack elytra = new ItemStack(Items.ELYTRA);
            elytra.enchant(Enchantments.UNBREAKING, 3);
            dropItems(level, pos, elytra);
            dropItems(level, pos, new ItemStack(Items.FIREWORK_ROCKET, 16));
            sendMessage(player, "§cRedstone launch! Catch your elytra!");
        },
        // GOOD 6: 10 dispensers loaded with arrows build around player
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.REDSTONE_BLOCK, 16));
            dropItems(level, pos, new ItemStack(Items.COMPARATOR, 8));
            dropItems(level, pos, new ItemStack(Items.REPEATER, 8));
            dropItems(level, pos, new ItemStack(Items.TARGET, 4));
            sendMessage(player, "§cRedstone lab!");
        },
        // GOOD 7: haste V + efficiency blessing
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.DIG_SPEED, 6000, 4));
            giveEffect(player, new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 6000, 3));
            giveEffect(player, new MobEffectInstance(MobEffects.DAMAGE_BOOST, 6000, 2));
            sendMessage(player, "§cRedstone surge!");
        },
        // GOOD 8: 64 TNT + 32 flint and steel
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.TNT, 64));
            dropItems(level, pos, new ItemStack(Items.FLINT_AND_STEEL, 4));
            dropItems(level, pos, new ItemStack(Items.FIRE_CHARGE, 16));
            sendMessage(player, "§cBlow it all up!");
        },

        // BAD 1: chain of 5 explosions
        (level, pos, player) -> {
            for (int i = 0; i < 5; i++)
                level.explode(null, pos.getX()+level.getRandom().nextInt(20)-10, pos.getY(), pos.getZ()+level.getRandom().nextInt(20)-10, 5.0f, Level.ExplosionInteraction.BLOCK);
            sendMessage(player, "§4CHAIN REACTION!");
        },
        // BAD 2: 10 charged creepers
        (level, pos, player) -> {
            for (int i = 0; i < 10; i++) {
                Creeper creeper = (Creeper) EntityType.CREEPER.spawn(level, (CompoundTag)null, null,
                    pos.offset(level.getRandom().nextInt(5)-2, 0, level.getRandom().nextInt(5)-2),
                    MobSpawnType.TRIGGERED, false, false);
                if (creeper != null) creeper.setPowered(true);
            }
            sendMessage(player, "§4CHARGED CREEPER SWARM!");
        },
        // BAD 3: player launched to sky
        (level, pos, player) -> {
            player.setDeltaMovement(new Vec3((level.getRandom().nextDouble()-0.5)*2, 5.0, (level.getRandom().nextDouble()-0.5)*2));
            player.hurtMarked = true;
            sendMessage(player, "§4REDSTONE LAUNCHED!");
        },
        // BAD 4: size 12 nuke under player
        (level, pos, player) -> {
            level.explode(null, player.getX(), player.getY(), player.getZ(), 12.0f, Level.ExplosionInteraction.BLOCK);
        },
        // BAD 5: 50 primed TNT directly on player
        (level, pos, player) -> {
            for (int i = 0; i < 50; i++) {
                PrimedTnt tnt = new PrimedTnt(level, player.getX(), player.getY()+1, player.getZ(), null);
                tnt.setFuse(5);
                level.addFreshEntity(tnt);
            }
            sendMessage(player, "§4POINT BLANK TNT STORM!");
        },
        // BAD 6: redstone block floor (conducting) + 20 lightning
        (level, pos, player) -> {
            for (int dx = -3; dx <= 3; dx++)
                for (int dz = -3; dz <= 3; dz++)
                    level.setBlockAndUpdate(player.blockPosition().offset(dx,-1,dz), Blocks.REDSTONE_BLOCK.defaultBlockState());
            for (int i = 0; i < 20; i++) strikeLightningAt(level, player.blockPosition().offset(level.getRandom().nextInt(7)-3, 0, level.getRandom().nextInt(7)-3));
            sendMessage(player, "§4Redstone lightning grid!");
        },
        // BAD 7: freeze in place (slowness X + mining fatigue V)
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2400, 10));
            giveEffect(player, new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 2400, 4));
            giveEffect(player, new MobEffectInstance(MobEffects.JUMP, 2400, 128)); // negative jump
            sendMessage(player, "§4Redstone lockdown!");
        },
        // BAD 8: random repeated explosions for 10 seconds (10 small blasts)
        (level, pos, player) -> {
            for (int i = 0; i < 10; i++) {
                double ox = (level.getRandom().nextDouble()-0.5)*16;
                double oz = (level.getRandom().nextDouble()-0.5)*16;
                level.explode(null, player.getX()+ox, player.getY(), player.getZ()+oz, 3.0f, Level.ExplosionInteraction.BLOCK);
            }
            sendMessage(player, "§4RANDOM EXPLOSION CARPET!");
        }
    )),

    COPPER(List.of(
        // GOOD 1: lightning purge 20 blocks
        (level, pos, player) -> {
            level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class,
                player.getBoundingBox().inflate(20)).forEach(e -> {
                    if (!(e instanceof Player)) strikeLightningAt(level, e.blockPosition());
                });
            sendMessage(player, "§6Copper storm purge!");
        },
        // GOOD 2: copper tower + 64 ingots
        (level, pos, player) -> {
            for (int i = 1; i <= 5; i++)
                level.setBlockAndUpdate(pos.above(i), Blocks.COPPER_BLOCK.defaultBlockState());
            dropItems(level, pos, new ItemStack(Items.COPPER_INGOT, 64));
            sendMessage(player, "§6Copper monument!");
        },
        // GOOD 3: resistance + fire resistance
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 3));
            giveEffect(player, new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000, 0));
            sendMessage(player, "§6Copper shield!");
        },
        // GOOD 4: max trident
        (level, pos, player) -> {
            ItemStack trident = new ItemStack(Items.TRIDENT);
            trident.enchant(Enchantments.IMPALING, 5);
            trident.enchant(Enchantments.CHANNELING, 1);
            trident.enchant(Enchantments.LOYALTY, 3);
            dropItems(level, pos, trident);
            sendMessage(player, "§6The storm trident!");
        },
        // GOOD 5: copper lightning rod grid (builds 5 rods around player)
        (level, pos, player) -> {
            int[][] offsets = {{3,0},{-3,0},{0,3},{0,-3},{0,0}};
            for (int[] o : offsets) {
                BlockPos top = player.blockPosition().offset(o[0], 5, o[1]);
                level.setBlockAndUpdate(top, Blocks.LIGHTNING_ROD.defaultBlockState());
            }
            dropItems(level, pos, new ItemStack(Items.LIGHTNING_ROD, 10));
            sendMessage(player, "§6Lightning fortress!");
        },
        // GOOD 6: full copper building set
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.COPPER_BLOCK, 32));
            dropItems(level, pos, new ItemStack(Items.CUT_COPPER, 32));
            dropItems(level, pos, new ItemStack(Items.COPPER_INGOT, 64));
            dropItems(level, pos, new ItemStack(Items.SPYGLASS, 2));
            sendMessage(player, "§6Copper architect!");
        },
        // GOOD 7: channel storm — all nearby entities get permanent slowness but player gets speed
        (level, pos, player) -> {
            level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class,
                player.getBoundingBox().inflate(15)).forEach(e -> {
                    if (!(e instanceof Player)) e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 5));
                });
            giveEffect(player, new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 5));
            sendMessage(player, "§6Copper field dominance!");
        },
        // GOOD 8: warden bane — instant kill all wardens in 50 block radius
        (level, pos, player) -> {
            level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class,
                player.getBoundingBox().inflate(50)).forEach(e -> {
                    if (e.getType() == EntityType.WARDEN) e.kill();
                });
            giveEffect(player, new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 3000, 4));
            sendMessage(player, "§6Copper warden slayer!");
        },

        // BAD 1: 30 lightning strikes on player
        (level, pos, player) -> {
            for (int i = 0; i < 30; i++)
                strikeLightningAt(level, player.blockPosition().offset(level.getRandom().nextInt(7)-3, 0, level.getRandom().nextInt(7)-3));
            sendMessage(player, "§4THUNDERSTORM!");
        },
        // BAD 2: on fire 30s + weakness + slowness
        (level, pos, player) -> {
            player.setSecondsOnFire(30);
            giveEffect(player, new MobEffectInstance(MobEffects.WEAKNESS, 1200, 3));
            giveEffect(player, new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1200, 3));
            sendMessage(player, "§4Copper oxidation burns!");
        },
        // BAD 3: copper block prison
        (level, pos, player) -> {
            for (int dx = -1; dx <= 1; dx++)
                for (int dy = 0; dy <= 2; dy++)
                    for (int dz = -1; dz <= 1; dz++)
                        if (!(dx==0 && dz==0) || dy==2)
                            level.setBlockAndUpdate(player.blockPosition().offset(dx,dy,dz), Blocks.COPPER_BLOCK.defaultBlockState());
            sendMessage(player, "§4Copper entombment!");
        },
        // BAD 4: struck 5 times + armor destroyed
        (level, pos, player) -> {
            for (int i = 0; i < 5; i++) strikeLightningAt(level, player.blockPosition());
            for (ItemStack s : player.getArmorSlots())
                if (!s.isEmpty()) s.setDamageValue(s.getMaxDamage()-1);
            sendMessage(player, "§4Oxidized and struck!");
        },
        // BAD 5: copper block avalanche from sky
        (level, pos, player) -> {
            for (int i = 0; i < 15; i++) {
                int ox = level.getRandom().nextInt(7)-3;
                int oz = level.getRandom().nextInt(7)-3;
                level.setBlockAndUpdate(player.blockPosition().offset(ox, 20, oz), Blocks.COPPER_BLOCK.defaultBlockState());
            }
            sendMessage(player, "§4Copper meteor shower!");
        },
        // BAD 6: all nearby passive mobs struck and converted to hostile
        (level, pos, player) -> {
            for (int i = 0; i < 15; i++) strikeLightningAt(level, pos.offset(level.getRandom().nextInt(11)-5, 0, level.getRandom().nextInt(11)-5));
            sendMessage(player, "§4The storm corrupts all life!");
        },
        // BAD 7: player encased and launched (copper blocks above + velocity down)
        (level, pos, player) -> {
            level.setBlockAndUpdate(player.blockPosition().above(3), Blocks.COPPER_BLOCK.defaultBlockState());
            player.setDeltaMovement(new Vec3(0, -5.0, 0));
            player.hurtMarked = true;
            sendMessage(player, "§4Copper slam!");
        },
        // BAD 8: 25 lightning rods placed pointing at player + 25 lightning
        (level, pos, player) -> {
            for (int i = 0; i < 25; i++) strikeLightningAt(level, player.blockPosition().offset(level.getRandom().nextInt(3)-1, 0, level.getRandom().nextInt(3)-1));
            sendMessage(player, "§4DIRECT LIGHTNING STRIKE x25!");
        }
    )),

    AMETHYST(List.of(
        // GOOD 1: elytra + 64 fireworks
        (level, pos, player) -> {
            ItemStack elytra = new ItemStack(Items.ELYTRA);
            elytra.enchant(Enchantments.UNBREAKING, 3);
            dropItems(level, pos, elytra);
            dropItems(level, pos, new ItemStack(Items.FIREWORK_ROCKET, 64));
            sendMessage(player, "§dFly, you fool!");
        },
        // GOOD 2: vision quest — all effects positive
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.NIGHT_VISION, 999999, 0));
            giveEffect(player, new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 3));
            giveEffect(player, new MobEffectInstance(MobEffects.REGENERATION, 6000, 4));
            giveEffect(player, new MobEffectInstance(MobEffects.ABSORPTION, 6000, 10));
            sendMessage(player, "§dAmethyst vision granted!");
        },
        // GOOD 3: 64 shards + amethyst blocks + blessed ingots
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.AMETHYST_SHARD, 64));
            dropItems(level, pos, new ItemStack(Items.AMETHYST_BLOCK, 16));
            dropItems(level, pos, new ItemStack(ModItems.BLESSED_INGOT, 5));
        },
        // GOOD 4: duplicate hotbar
        (level, pos, player) -> {
            for (int i = 0; i < 9; i++) {
                ItemStack s = player.getInventory().getItem(i);
                if (!s.isEmpty()) dropItems(level, pos, s.copy());
            }
            sendMessage(player, "§dAmethyst mirror!");
        },
        // GOOD 5: crystal palace — amethyst block floor + pillars
        (level, pos, player) -> {
            for (int dx = -4; dx <= 4; dx++)
                for (int dz = -4; dz <= 4; dz++)
                    level.setBlockAndUpdate(player.blockPosition().offset(dx,-1,dz), Blocks.AMETHYST_BLOCK.defaultBlockState());
            for (int[] corner : new int[][]{{4,4},{4,-4},{-4,4},{-4,-4}})
                for (int dy = 0; dy <= 5; dy++)
                    level.setBlockAndUpdate(player.blockPosition().offset(corner[0],dy,corner[1]), Blocks.AMETHYST_BLOCK.defaultBlockState());
            sendMessage(player, "§dCrystal palace!");
        },
        // GOOD 6: time stop — all nearby mobs frozen (slowness 255)
        (level, pos, player) -> {
            level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class,
                player.getBoundingBox().inflate(20)).forEach(e -> {
                    if (!(e instanceof Player)) {
                        e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 255));
                        e.addEffect(new MobEffectInstance(MobEffects.JUMP, 200, 128));
                    }
                });
            sendMessage(player, "§dTime crystal — everything freezes!");
        },
        // GOOD 7: random teleport to surface with full effects
        (level, pos, player) -> {
            double angle = level.getRandom().nextDouble() * Math.PI * 2;
            double dist = 100 + level.getRandom().nextDouble() * 200;
            player.teleportTo(player.getX() + Math.cos(angle)*dist, 200, player.getZ() + Math.sin(angle)*dist);
            giveEffect(player, new MobEffectInstance(MobEffects.SLOW_FALLING, 600, 0));
            giveEffect(player, new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 600, 0));
            sendMessage(player, "§dCrystal transport! (Soft landing incoming)");
        },
        // GOOD 8: full weapon kit — sword bow crossbow trident axe all maxed
        (level, pos, player) -> {
            ItemStack sword = new ItemStack(Items.NETHERITE_SWORD);
            sword.enchant(Enchantments.SHARPNESS, 5); sword.enchant(Enchantments.LOOTING, 3);
            ItemStack axe = new ItemStack(Items.NETHERITE_AXE);
            axe.enchant(Enchantments.SHARPNESS, 5); axe.enchant(Enchantments.EFFICIENCY, 5);
            dropItems(level, pos, sword); dropItems(level, pos, axe);
            dropItems(level, pos, new ItemStack(Items.TOTEM_OF_UNDYING, 2));
            sendMessage(player, "§dCrystal armory!");
        },

        // BAD 1: teleport to world border
        (level, pos, player) -> {
            double border = level.getWorldBorder().getSize() / 2.0 - 5;
            int side = level.getRandom().nextInt(4);
            double tx = side==0 ? border : side==1 ? -border : player.getX();
            double tz = side==2 ? border : side==3 ? -border : player.getZ();
            player.teleportTo(tx, player.getY(), tz);
            sendMessage(player, "§4BANISHED TO THE EDGE!");
        },
        // BAD 2: dimensional rift — wither + blindness + levitation
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.WITHER, 600, 3));
            giveEffect(player, new MobEffectInstance(MobEffects.BLINDNESS, 600, 2));
            giveEffect(player, new MobEffectInstance(MobEffects.LEVITATION, 200, 3));
            giveEffect(player, new MobEffectInstance(MobEffects.CONFUSION, 600, 5));
            sendMessage(player, "§4DIMENSIONAL RIFT!");
        },
        // BAD 3: amethyst cage 5x5
        (level, pos, player) -> {
            for (int dx = -2; dx <= 2; dx++)
                for (int dy = -1; dy <= 3; dy++)
                    for (int dz = -2; dz <= 2; dz++)
                        if (Math.abs(dx)==2 || Math.abs(dz)==2 || dy==-1 || dy==3)
                            level.setBlockAndUpdate(player.blockPosition().offset(dx,dy,dz), Blocks.AMETHYST_BLOCK.defaultBlockState());
            sendMessage(player, "§4Amethyst prison!");
        },
        // BAD 4: 3 withers
        (level, pos, player) -> {
            for (int i = 0; i < 3; i++)
                spawnMob(level, pos.offset(level.getRandom().nextInt(10)-5, 0, level.getRandom().nextInt(10)-5), EntityType.WITHER);
            sendMessage(player, "§4THE WITHERS AWAKEN!");
        },
        // BAD 5: random teleport + blindness + no slow fall
        (level, pos, player) -> {
            double angle = level.getRandom().nextDouble() * Math.PI * 2;
            player.teleportTo(player.getX()+Math.cos(angle)*300, 300, player.getZ()+Math.sin(angle)*300);
            giveEffect(player, new MobEffectInstance(MobEffects.BLINDNESS, 600, 2));
            sendMessage(player, "§4Crystal banishment from height!");
        },
        // BAD 6: mirror curse — reverses player velocity repeatedly
        (level, pos, player) -> {
            player.setDeltaMovement(new Vec3(-player.getDeltaMovement().x * 3, 4.0, -player.getDeltaMovement().z * 3));
            player.hurtMarked = true;
            giveEffect(player, new MobEffectInstance(MobEffects.CONFUSION, 600, 5));
            sendMessage(player, "§4Crystal mirror curse!");
        },
        // BAD 7: duplicate all hostile mobs in range (doubles all threats)
        (level, pos, player) -> {
            level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class,
                player.getBoundingBox().inflate(20)).forEach(e -> {
                    if (!(e instanceof Player)) spawnMob(level, e.blockPosition(), e.getType());
                });
            sendMessage(player, "§4AMETHYST DOUBLES EVERYTHING!");
        },
        // BAD 8: crystal shatter — 10 explosions in a spiral around player
        (level, pos, player) -> {
            for (int i = 0; i < 10; i++) {
                double angle = i * (Math.PI * 2 / 10);
                double r = 5.0;
                level.explode(null, player.getX()+Math.cos(angle)*r, player.getY(), player.getZ()+Math.sin(angle)*r, 4.0f, Level.ExplosionInteraction.BLOCK);
            }
            sendMessage(player, "§4Crystal shatter spiral!");
        }
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
        ItemEntity entity = new ItemEntity(level, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, stack);
        level.addFreshEntity(entity);
    }

    private static void giveEffect(Player player, MobEffectInstance effect) {
        player.addEffect(effect);
    }

    private static void spawnMob(ServerLevel level, BlockPos pos, EntityType<?> type) {
        type.spawn(level, (CompoundTag) null, null, pos.above(), MobSpawnType.TRIGGERED, false, false);
    }

    private static void strikeLightningAt(ServerLevel level, BlockPos pos) {
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt != null) {
            bolt.moveTo(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5);
            level.addFreshEntity(bolt);
        }
    }

    private static void sendMessage(Player player, String msg) {
        player.sendSystemMessage(Component.literal(msg));
    }
}
