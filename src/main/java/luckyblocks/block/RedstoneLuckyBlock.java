package luckyblocks.block;

import luckyblocks.util.LuckyOutcomePool;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

public class RedstoneLuckyBlock extends LuckyBlock {

    public RedstoneLuckyBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void triggerOutcome(ServerLevel level, BlockPos pos, Player player) {
        runOutcome(LuckyOutcomePool.REDSTONE.random(level.getRandom()), level, pos, player);
    }
}
