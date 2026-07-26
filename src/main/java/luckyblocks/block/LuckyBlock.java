package luckyblocks.block;

import luckyblocks.util.LuckyOutcome;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class LuckyBlock extends Block {

    public LuckyBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                  InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            triggerOutcome((ServerLevel) level, pos, player);
            level.removeBlock(pos, false);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    protected abstract void triggerOutcome(ServerLevel level, BlockPos pos, Player player);

    protected void runOutcome(LuckyOutcome outcome, ServerLevel level, BlockPos pos, Player player) {
        outcome.execute(level, pos, player);
    }
}
