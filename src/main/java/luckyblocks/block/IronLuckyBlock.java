package luckyblocks.block;

import luckyblocks.util.LuckyOutcomePool;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

public class IronLuckyBlock extends LuckyBlock {

    public IronLuckyBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void triggerOutcome(ServerLevel level, BlockPos pos, Player player) {
        runOutcome(LuckyOutcomePool.IRON.random(level.getRandom()), level, pos, player);
    }
}
