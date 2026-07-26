package luckyblocks.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface LuckyOutcome {
    void execute(ServerLevel level, BlockPos pos, Player player);
}
