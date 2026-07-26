package luckyblocks.entity;

import luckyblocks.registry.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class LuckySlimeEntity extends Slime {

    public LuckySlimeEntity(EntityType<? extends Slime> type, Level level) {
        super(type, level);
        setSize(2, true);
    }

    @Override
    public void die(net.minecraft.world.damagesource.DamageSource damageSource) {
        super.die(damageSource);
        if (!level().isClientSide()) {
            // Drop a random lucky item on death
            int roll = level().getRandom().nextInt(3);
            ItemStack drop = switch (roll) {
                case 0 -> new ItemStack(ModItems.LUCKY_COIN, 1 + level().getRandom().nextInt(3));
                case 1 -> new ItemStack(ModItems.BLESSED_INGOT, 1);
                default -> new ItemStack(ModItems.CURSED_SHARD, 1);
            };
            spawnAtLocation(drop);
        }
    }
}
