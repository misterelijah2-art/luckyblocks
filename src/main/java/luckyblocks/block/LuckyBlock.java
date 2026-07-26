package luckyblocks.block;

import luckyblocks.util.LuckyOutcome;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class LuckyBlock extends Block {

    public LuckyBlock(Properties properties) {
        super(properties);
    }

    /**
     * Fires server-side the moment the player breaks the block,
     * before the block is removed. We trigger the outcome here
     * then let vanilla remove the block normally.
     * The loot table is empty so no item drops back.
     */
    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            triggerOutcome((ServerLevel) level, pos, player);
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    protected abstract void triggerOutcome(ServerLevel level, BlockPos pos, Player player);

    protected void runOutcome(LuckyOutcome outcome, ServerLevel level, BlockPos pos, Player player) {
        outcome.execute(level, pos, player);
    }
}
