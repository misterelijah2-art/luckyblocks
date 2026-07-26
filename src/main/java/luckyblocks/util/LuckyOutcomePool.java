package luckyblocks.util;

import luckyblocks.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
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
        // Good: anvil of coal rains from the sky
        (level, pos, player) -> {
            for (int i = 0; i < 20; i++) {
                double ox = (level.getRandom().nextDouble() - 0.5) * 6;
                double oz = (level.getRandom().nextDouble() - 0.5) * 6;
                dropItems(level, new BlockPos((int)(pos.getX()+ox), pos.getY()+1, (int)(pos.getZ()+oz)), new ItemStack(Items.COAL, 4));
            }
        },
        // Good: full fire resistance + speed, message
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000, 0));
            giveEffect(player, new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 3000, 2));
            giveEffect(player, new MobEffectInstance(MobEffects.DAMAGE_BOOST, 3000, 1));
            sendMessage(player, "§6The coal ember awakens within you!");
        },
        // Good: enchanted diamond pick drops
        (level, pos, player) -> {
            ItemStack pick = new ItemStack(Items.DIAMOND_PICKAXE);
            pick.enchant(Enchantments.BLOCK_EFFICIENCY, 5);
            pick.enchant(Enchantments.UNBREAKING, 3);
            pick.enchant(Enchantments.BLOCK_FORTUNE, 3);
            dropItems(level, pos, pick);
            sendMessage(player, "§bA miner's dream!");
        },
        // Good: 5 coal blocks fall around the player
        (level, pos, player) -> {
            for (int i = 0; i < 5; i++) {
                dropItems(level, pos, new ItemStack(Items.COAL_BLOCK, 1));
            }
        },
        // Bad: ring of fire surrounds the player
        (level, pos, player) -> {
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    if (Math.abs(dx) == 2 || Math.abs(dz) == 2) {
                        BlockPos firePos = player.blockPosition().offset(dx, 0, dz);
                        if (level.isEmptyBlock(firePos)) {
                            level.setBlockAndUpdate(firePos, BaseFireBlock.getState(level, firePos));
                        }
                    }
                }
            }
            sendMessage(player, "§cThe coal curse surrounds you!");
        },
        // Bad: 5 blazes spawn at once
        (level, pos, player) -> {
            for (int i = 0; i < 5; i++) {
                BlockPos sp = pos.offset(
                    level.getRandom().nextInt(5) - 2, 1,
                    level.getRandom().nextInt(5) - 2);
                spawnMob(level, sp, EntityType.BLAZE);
            }
            sendMessage(player, "§4Blaze army incoming!");
        },
        // Bad: player inventory set on fire (fire damage + everything dropped)
        (level, pos, player) -> {
            player.setSecondsOnFire(10);
            player.hurt(level.damageSources().inFire(), 8.0f);
            sendMessage(player, "§4You've been burned!");
        },
        // Bad: floor under player replaced with lava in a 3x3
        (level, pos, player) -> {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos lavaPos = player.blockPosition().offset(dx, -1, dz);
                    level.setBlockAndUpdate(lavaPos, Blocks.LAVA.defaultBlockState());
                }
            }
            sendMessage(player, "§4The ground melts beneath you!");
        }
    )),

    IRON(List.of(
        // Good: full iron armor set drops enchanted
        (level, pos, player) -> {
            ItemStack helm = new ItemStack(Items.IRON_HELMET);
            ItemStack chest = new ItemStack(Items.IRON_CHESTPLATE);
            ItemStack legs = new ItemStack(Items.IRON_LEGGINGS);
            ItemStack boots = new ItemStack(Items.IRON_BOOTS);
            for (ItemStack s : List.of(helm, chest, legs, boots)) {
                s.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 4);
            }
            dropItems(level, pos, helm); dropItems(level, pos, chest);
            dropItems(level, pos, legs); dropItems(level, pos, boots);
            sendMessage(player, "§7Iron Knight rises!");
        },
        // Good: 32 iron ingots + iron golem spawns as ally (passive)
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.IRON_INGOT, 32));
            spawnMob(level, pos, EntityType.IRON_GOLEM);
            sendMessage(player, "§7Your iron guardian awakens!");
        },
        // Good: resistance + strength for 3 min
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 3600, 3));
            giveEffect(player, new MobEffectInstance(MobEffects.DAMAGE_BOOST, 3600, 2));
            sendMessage(player, "§7Iron will!");
        },
        // Good: netherite sword drops
        (level, pos, player) -> {
            ItemStack sword = new ItemStack(Items.NETHERITE_SWORD);
            sword.enchant(Enchantments.SHARPNESS, 5);
            sword.enchant(Enchantments.FIRE_ASPECT, 2);
            dropItems(level, pos, sword);
            sendMessage(player, "§6The strongest blade!");
        },
        // Bad: 10 iron golems spawn hostile
        (level, pos, player) -> {
            for (int i = 0; i < 10; i++) {
                BlockPos sp = pos.offset(
                    level.getRandom().nextInt(7) - 3, 0,
                    level.getRandom().nextInt(7) - 3);
                spawnMob(level, sp, EntityType.IRON_GOLEM);
            }
            sendMessage(player, "§4IRON ARMY!");
        },
        // Bad: massive explosion
        (level, pos, player) -> {
            level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 8.0f, Level.ExplosionInteraction.BLOCK);
            sendMessage(player, "§4BOOM!");
        },
        // Bad: player encased in iron bars 3x3x3
        (level, pos, player) -> {
            for (int dx = -1; dx <= 1; dx++)
                for (int dy = 0; dy <= 2; dy++)
                    for (int dz = -1; dz <= 1; dz++)
                        if (level.isEmptyBlock(player.blockPosition().offset(dx, dy, dz)))
                            level.setBlockAndUpdate(player.blockPosition().offset(dx, dy, dz), Blocks.IRON_BARS.defaultBlockState());
            sendMessage(player, "§4Iron prison!");
        },
        // Bad: all armor durability destroyed
        (level, pos, player) -> {
            for (ItemStack s : player.getArmorSlots()) {
                if (!s.isEmpty()) s.setDamageValue(s.getMaxDamage() - 1);
            }
            sendMessage(player, "§4Your armor crumbles!");
        }
    )),

    GOLD(List.of(
        // Good: enchanted golden apple shower
        (level, pos, player) -> {
            for (int i = 0; i < 5; i++) dropItems(level, pos, new ItemStack(Items.ENCHANTED_GOLDEN_APPLE));
            sendMessage(player, "§eGolden rain!");
        },
        // Good: max all positive effects
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.REGENERATION, 2400, 4));
            giveEffect(player, new MobEffectInstance(MobEffects.ABSORPTION, 2400, 4));
            giveEffect(player, new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2400, 4));
            giveEffect(player, new MobEffectInstance(MobEffects.LUCK, 6000, 2));
            sendMessage(player, "§6Golden blessing!");
        },
        // Good: 64 gold ingots + 3 lucky coins
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.GOLD_INGOT, 64));
            dropItems(level, pos, new ItemStack(ModItems.LUCKY_COIN, 3));
        },
        // Good: golden sword with all enchants
        (level, pos, player) -> {
            ItemStack sword = new ItemStack(Items.GOLDEN_SWORD);
            sword.enchant(Enchantments.SHARPNESS, 5);
            sword.enchant(Enchantments.KNOCKBACK, 2);
            sword.enchant(Enchantments.FIRE_ASPECT, 2);
            sword.enchant(Enchantments.LOOTING, 3);
            dropItems(level, pos, sword);
            sendMessage(player, "§6The golden blade!");
        },
        // Bad: 20 piglin brutes teleport onto player
        (level, pos, player) -> {
            for (int i = 0; i < 20; i++) spawnMob(level, pos, EntityType.PIGLIN_BRUTE);
            sendMessage(player, "§4PIGLIN RAID!");
        },
        // Bad: player inventory cleared (drops everything)
        (level, pos, player) -> {
            player.getInventory().dropAll();
            sendMessage(player, "§4The gold curse stole everything!");
        },
        // Bad: 3x3 gold blocks trap around player then disappear (falls)
        (level, pos, player) -> {
            for (int dx = -1; dx <= 1; dx++)
                for (int dz = -1; dz <= 1; dz++) {
                    level.setBlockAndUpdate(player.blockPosition().offset(dx, -1, dz), Blocks.AIR.defaultBlockState());
                }
            sendMessage(player, "§4The floor is gold... wait it's gone!");
        },
        // Bad: struck by 10 lightning bolts in sequence
        (level, pos, player) -> {
            for (int i = 0; i < 10; i++) strikeLightningAt(level, player.blockPosition());
            sendMessage(player, "§4Zeus is angry!");
        }
    )),

    DIAMOND(List.of(
        // Good: full netherite armor set
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.NETHERITE_HELMET));
            dropItems(level, pos, new ItemStack(Items.NETHERITE_CHESTPLATE));
            dropItems(level, pos, new ItemStack(Items.NETHERITE_LEGGINGS));
            dropItems(level, pos, new ItemStack(Items.NETHERITE_BOOTS));
            sendMessage(player, "§bNetherite blessing!");
        },
        // Good: 64 diamonds
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.DIAMOND, 64));
            sendMessage(player, "§bDiamond jackpot!");
        },
        // Good: max enchanted diamond sword + bow
        (level, pos, player) -> {
            ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
            sword.enchant(Enchantments.SHARPNESS, 5);
            sword.enchant(Enchantments.UNBREAKING, 3);
            ItemStack bow = new ItemStack(Items.BOW);
            bow.enchant(Enchantments.POWER_ARROWS, 5);
            bow.enchant(Enchantments.PUNCH_ARROWS, 2);
            bow.enchant(Enchantments.INFINITY_ARROWS, 1);
            dropItems(level, pos, sword);
            dropItems(level, pos, bow);
            sendMessage(player, "§bThe ultimate arsenal!");
        },
        // Good: 5 second invincibility + all effects max
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 255));
            giveEffect(player, new MobEffectInstance(MobEffects.REGENERATION, 6000, 4));
            giveEffect(player, new MobEffectInstance(MobEffects.ABSORPTION, 6000, 10));
            sendMessage(player, "§bDIAMOND GOD MODE!");
        },
        // Bad: warden + 5 wardens spawn
        (level, pos, player) -> {
            for (int i = 0; i < 3; i++) spawnMob(level, pos, EntityType.WARDEN);
            sendMessage(player, "§4THE ANCIENT CITY OPENS!");
        },
        // Bad: nuclear explosion
        (level, pos, player) -> {
            level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 15.0f, Level.ExplosionInteraction.BLOCK);
            sendMessage(player, "§4NUCLEAR DIAMOND!");
        },
        // Bad: player dropped into void (y = -100)
        (level, pos, player) -> {
            player.teleportTo(player.getX(), -100, player.getZ());
            sendMessage(player, "§4Into the void you go!");
        },
        // Bad: all effects negative max
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.WITHER, 400, 4));
            giveEffect(player, new MobEffectInstance(MobEffects.BLINDNESS, 400, 2));
            giveEffect(player, new MobEffectInstance(MobEffects.WEAKNESS, 400, 4));
            giveEffect(player, new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 10));
            giveEffect(player, new MobEffectInstance(MobEffects.POISON, 400, 4));
            sendMessage(player, "§4Diamond curse!");
        }
    )),

    EMERALD(List.of(
        // Good: villager gift — random rare trades drop
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.EMERALD, 64));
            dropItems(level, pos, new ItemStack(Items.EXPERIENCE_BOTTLE, 20));
            dropItems(level, pos, new ItemStack(ModItems.LUCKY_COIN, 5));
            sendMessage(player, "§aThe village rewards you!");
        },
        // Good: instant max XP levels
        (level, pos, player) -> {
            player.giveExperienceLevels(30);
            sendMessage(player, "§aEXPERIENCE SURGE! +30 levels!");
        },
        // Good: luck effect + 3 random enchant books
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.LUCK, 12000, 3));
            for (int i = 0; i < 3; i++) dropItems(level, pos, new ItemStack(Items.ENCHANTED_BOOK));
            sendMessage(player, "§aFortune smiles upon you!");
        },
        // Good: spawn 5 traders
        (level, pos, player) -> {
            for (int i = 0; i < 3; i++) spawnMob(level, pos, EntityType.WANDERING_TRADER);
            sendMessage(player, "§aThe merchants arrive!");
        },
        // Bad: raid spawns (many pillagers)
        (level, pos, player) -> {
            for (int i = 0; i < 8; i++) spawnMob(level, pos, EntityType.PILLAGER);
            for (int i = 0; i < 3; i++) spawnMob(level, pos, EntityType.RAVAGER);
            sendMessage(player, "§4RAID!");
        },
        // Bad: bad omen max level + instant raid trigger
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.BAD_OMEN, 999999, 4));
            giveEffect(player, new MobEffectInstance(MobEffects.UNLUCK, 6000, 4));
            sendMessage(player, "§4CURSED BY THE VILLAGE!");
        },
        // Bad: player shrunk to 1HP
        (level, pos, player) -> {
            player.setHealth(1.0f);
            giveEffect(player, new MobEffectInstance(MobEffects.WEAKNESS, 2400, 4));
            sendMessage(player, "§4The emerald drained your life!");
        },
        // Bad: 10 evokers spawn
        (level, pos, player) -> {
            for (int i = 0; i < 10; i++) spawnMob(level, pos, EntityType.EVOKER);
            sendMessage(player, "§4THE EVOKERS COME FOR YOU!");
        }
    )),

    LAPIS(List.of(
        // Good: entire hotbar gets enchanted
        (level, pos, player) -> {
            for (int i = 0; i < 9; i++) {
                ItemStack s = player.getInventory().getItem(i);
                if (!s.isEmpty() && s.isEnchantable()) {
                    s.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 4);
                }
            }
            sendMessage(player, "§9Lapis enchantment surge!");
        },
        // Good: night vision + luck + speed permanently (long duration)
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.NIGHT_VISION, 999999, 0));
            giveEffect(player, new MobEffectInstance(MobEffects.LUCK, 999999, 3));
            giveEffect(player, new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 999999, 1));
            sendMessage(player, "§9The lapis vision is eternal!");
        },
        // Good: 64 lapis + enchanted books shower
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.LAPIS_LAZULI, 64));
            for (int i = 0; i < 5; i++) dropItems(level, pos, new ItemStack(Items.ENCHANTED_BOOK));
            sendMessage(player, "§9Enchanting fortune!");
        },
        // Good: entire inventory enchanted with unbreaking 3
        (level, pos, player) -> {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack s = player.getInventory().getItem(i);
                if (!s.isEmpty() && s.isEnchantable()) s.enchant(Enchantments.UNBREAKING, 3);
            }
            sendMessage(player, "§9Unbreakable!");
        },
        // Bad: all effects wiped + random bad effects
        (level, pos, player) -> {
            player.removeAllEffects();
            giveEffect(player, new MobEffectInstance(MobEffects.CONFUSION, 600, 5));
            giveEffect(player, new MobEffectInstance(MobEffects.BLINDNESS, 600, 0));
            giveEffect(player, new MobEffectInstance(MobEffects.WEAKNESS, 600, 4));
            sendMessage(player, "§4Lapis confusion!");
        },
        // Bad: enchantments stripped from all held items
        (level, pos, player) -> {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                player.getInventory().getItem(i).removeTagKey("Enchantments");
                player.getInventory().getItem(i).removeTagKey("StoredEnchantments");
            }
            sendMessage(player, "§4All enchantments erased!");
        },
        // Bad: 20 witches spawn
        (level, pos, player) -> {
            for (int i = 0; i < 20; i++) spawnMob(level, pos, EntityType.WITCH);
            sendMessage(player, "§4WITCH COVEN!");
        },
        // Bad: random teleport 500 blocks away
        (level, pos, player) -> {
            double angle = level.getRandom().nextDouble() * Math.PI * 2;
            double dist = 200 + level.getRandom().nextDouble() * 300;
            player.teleportTo(player.getX() + Math.cos(angle) * dist, player.getY(), player.getZ() + Math.sin(angle) * dist);
            sendMessage(player, "§4LAPIS BANISHMENT!");
        }
    )),

    REDSTONE(List.of(
        // Good: TNT cannon — launches 20 primed TNT around area
        (level, pos, player) -> {
            for (int i = 0; i < 20; i++) {
                net.minecraft.world.entity.item.PrimedTnt tnt =
                    new net.minecraft.world.entity.item.PrimedTnt(level,
                        pos.getX() + (level.getRandom().nextDouble()-0.5)*10,
                        pos.getY() + 5,
                        pos.getZ() + (level.getRandom().nextDouble()-0.5)*10,
                        null);
                tnt.setFuse(60 + level.getRandom().nextInt(40));
                level.addFreshEntity(tnt);
            }
            sendMessage(player, "§cRedstone TNT party!");
        },
        // Good: speed god mode
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 6000, 10));
            giveEffect(player, new MobEffectInstance(MobEffects.JUMP, 6000, 5));
            sendMessage(player, "§cREDSTONE OVERDRIVE!");
        },
        // Good: 64 redstone + 10 observers + 10 pistons
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.REDSTONE, 64));
            dropItems(level, pos, new ItemStack(Items.OBSERVER, 10));
            dropItems(level, pos, new ItemStack(Items.PISTON, 10));
            dropItems(level, pos, new ItemStack(Items.STICKY_PISTON, 5));
        },
        // Good: lightning rod effect — all nearby mobs zapped
        (level, pos, player) -> {
            level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class,
                player.getBoundingBox().inflate(15)).forEach(e -> {
                    if (!(e instanceof Player)) strikeLightningAt(level, e.blockPosition());
                });
            sendMessage(player, "§cLightning purge!");
        },
        // Bad: chain explosion — 5 explosions chain across 20 blocks
        (level, pos, player) -> {
            for (int i = 0; i < 5; i++) {
                int dx = level.getRandom().nextInt(20) - 10;
                int dz = level.getRandom().nextInt(20) - 10;
                level.explode(null, pos.getX()+dx, pos.getY(), pos.getZ()+dz, 5.0f, Level.ExplosionInteraction.BLOCK);
            }
            sendMessage(player, "§4CHAIN REACTION!");
        },
        // Bad: creeper army (10 charged creepers)
        (level, pos, player) -> {
            for (int i = 0; i < 10; i++) {
                net.minecraft.world.entity.monster.Creeper creeper =
                    (net.minecraft.world.entity.monster.Creeper) EntityType.CREEPER.spawn(
                        level, (CompoundTag) null, null,
                        pos.offset(level.getRandom().nextInt(5)-2, 0, level.getRandom().nextInt(5)-2),
                        MobSpawnType.TRIGGERED, false, false);
                if (creeper != null) creeper.setPowered(true);
            }
            sendMessage(player, "§4CHARGED CREEPER SWARM!");
        },
        // Bad: player launched into the sky
        (level, pos, player) -> {
            player.setDeltaMovement(new Vec3(
                (level.getRandom().nextDouble()-0.5)*2,
                5.0,
                (level.getRandom().nextDouble()-0.5)*2));
            player.hurtMarked = true;
            sendMessage(player, "§4REDSTONE LAUNCHED!");
        },
        // Bad: instant nuke under player
        (level, pos, player) -> {
            level.explode(null, player.getX(), player.getY(), player.getZ(), 12.0f, Level.ExplosionInteraction.BLOCK);
        }
    )),

    COPPER(List.of(
        // Good: 10 lightning strikes around area killing all mobs
        (level, pos, player) -> {
            level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class,
                player.getBoundingBox().inflate(20)).forEach(e -> {
                    if (!(e instanceof Player)) strikeLightningAt(level, e.blockPosition());
                });
            sendMessage(player, "§6Copper storm purge!");
        },
        // Good: copper block tower builds itself (5 high)
        (level, pos, player) -> {
            for (int i = 0; i < 5; i++) {
                level.setBlockAndUpdate(pos.above(i+1), Blocks.COPPER_BLOCK.defaultBlockState());
            }
            dropItems(level, pos, new ItemStack(Items.COPPER_INGOT, 64));
            sendMessage(player, "§6Copper monument!");
        },
        // Good: resistance + thorns aura
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 3));
            giveEffect(player, new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000, 0));
            sendMessage(player, "§6Copper shield!");
        },
        // Good: trident with max enchants
        (level, pos, player) -> {
            ItemStack trident = new ItemStack(Items.TRIDENT);
            trident.enchant(Enchantments.IMPALING, 5);
            trident.enchant(Enchantments.CHANNELING, 1);
            trident.enchant(Enchantments.LOYALTY, 3);
            dropItems(level, pos, trident);
            sendMessage(player, "§6The storm trident!");
        },
        // Bad: thunderstorm of 30 lightning on player position
        (level, pos, player) -> {
            for (int i = 0; i < 30; i++) {
                int dx = level.getRandom().nextInt(7)-3;
                int dz = level.getRandom().nextInt(7)-3;
                strikeLightningAt(level, player.blockPosition().offset(dx, 0, dz));
            }
            sendMessage(player, "§4THUNDERSTORM!");
        },
        // Bad: player set on fire + weakness + slowness
        (level, pos, player) -> {
            player.setSecondsOnFire(30);
            giveEffect(player, new MobEffectInstance(MobEffects.WEAKNESS, 1200, 3));
            giveEffect(player, new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1200, 3));
            sendMessage(player, "§4Copper oxidation burns!");
        },
        // Bad: copper prison — encased in copper blocks
        (level, pos, player) -> {
            for (int dx = -1; dx <= 1; dx++)
                for (int dy = 0; dy <= 2; dy++)
                    for (int dz = -1; dz <= 1; dz++)
                        if (!(dx==0 && dz==0) || dy==2)
                            level.setBlockAndUpdate(player.blockPosition().offset(dx, dy, dz),
                                Blocks.COPPER_BLOCK.defaultBlockState());
            sendMessage(player, "§4Copper entombment!");
        },
        // Bad: struck 5 times + armor oxidized (damaged)
        (level, pos, player) -> {
            for (int i = 0; i < 5; i++) strikeLightningAt(level, player.blockPosition());
            for (ItemStack s : player.getArmorSlots()) {
                if (!s.isEmpty()) s.setDamageValue(s.getMaxDamage() - 1);
            }
            sendMessage(player, "§4Oxidized and struck!");
        }
    )),

    AMETHYST(List.of(
        // Good: teleport to random stronghold-y coords + elytra
        (level, pos, player) -> {
            ItemStack elytra = new ItemStack(Items.ELYTRA);
            elytra.enchant(Enchantments.UNBREAKING, 3);
            dropItems(level, pos, elytra);
            dropItems(level, pos, new ItemStack(Items.FIREWORK_ROCKET, 64));
            sendMessage(player, "§dFly, you fool!");
        },
        // Good: vision quest — all effects positive + see through walls
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.NIGHT_VISION, 999999, 0));
            giveEffect(player, new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 3));
            giveEffect(player, new MobEffectInstance(MobEffects.REGENERATION, 6000, 4));
            giveEffect(player, new MobEffectInstance(MobEffects.ABSORPTION, 6000, 10));
            sendMessage(player, "§dAmethyst vision granted!");
        },
        // Good: 64 amethyst shards + crystal building blocks
        (level, pos, player) -> {
            dropItems(level, pos, new ItemStack(Items.AMETHYST_SHARD, 64));
            dropItems(level, pos, new ItemStack(Items.AMETHYST_BLOCK, 16));
            dropItems(level, pos, new ItemStack(ModItems.BLESSED_INGOT, 5));
        },
        // Good: clone of player items (duplicate entire hotbar)
        (level, pos, player) -> {
            for (int i = 0; i < 9; i++) {
                ItemStack s = player.getInventory().getItem(i);
                if (!s.isEmpty()) dropItems(level, pos, s.copy());
            }
            sendMessage(player, "§dAmethyst mirror!");
        },
        // Bad: teleported to world border edge
        (level, pos, player) -> {
            double border = level.getWorldBorder().getSize() / 2.0 - 5;
            int side = level.getRandom().nextInt(4);
            double tx = side == 0 ? border : side == 1 ? -border : player.getX();
            double tz = side == 2 ? border : side == 3 ? -border : player.getZ();
            player.teleportTo(tx, player.getY(), tz);
            sendMessage(player, "§4BANISHED TO THE EDGE!");
        },
        // Bad: dimension swap feeling — wither + blindness + levitation
        (level, pos, player) -> {
            giveEffect(player, new MobEffectInstance(MobEffects.WITHER, 600, 3));
            giveEffect(player, new MobEffectInstance(MobEffects.BLINDNESS, 600, 2));
            giveEffect(player, new MobEffectInstance(MobEffects.LEVITATION, 200, 3));
            giveEffect(player, new MobEffectInstance(MobEffects.CONFUSION, 600, 5));
            sendMessage(player, "§4DIMENSIONAL RIFT!");
        },
        // Bad: crystal prison — amethyst blocks cage
        (level, pos, player) -> {
            for (int dx = -2; dx <= 2; dx++)
                for (int dy = -1; dy <= 3; dy++)
                    for (int dz = -2; dz <= 2; dz++)
                        if (Math.abs(dx)==2 || Math.abs(dz)==2 || dy==-1 || dy==3)
                            level.setBlockAndUpdate(player.blockPosition().offset(dx,dy,dz),
                                Blocks.AMETHYST_BLOCK.defaultBlockState());
            sendMessage(player, "§4Amethyst prison!");
        },
        // Bad: spawn 5 withers
        (level, pos, player) -> {
            for (int i = 0; i < 3; i++) {
                BlockPos sp = pos.offset(level.getRandom().nextInt(10)-5, 0, level.getRandom().nextInt(10)-5);
                spawnMob(level, sp, EntityType.WITHER);
            }
            sendMessage(player, "§4THE WITHERS AWAKEN!");
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

    private static void strikeLightningAt(ServerLevel level, BlockPos pos) {
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt != null) {
            bolt.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            level.addFreshEntity(bolt);
        }
    }

    private static void sendMessage(Player player, String msg) {
        player.sendSystemMessage(Component.literal(msg));
    }
}
