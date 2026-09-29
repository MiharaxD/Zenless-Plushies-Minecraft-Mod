package dev.yuri.zzzplushies.gacha;

import dev.yuri.zzzplushies.ZzzPlushies;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class GachaMachineBlock extends HorizontalDirectionalBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public GachaMachineBlock() {
        super(Properties.of().strength(2.5F).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion());
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos above = context.getClickedPos().above();
        if (above.getY() >= context.getLevel().getMaxBuildHeight()
                || !context.getLevel().getBlockState(above).canBeReplaced(context)) return null;
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        boolean lower = state.getValue(HALF) == DoubleBlockHalf.LOWER;
        if (direction == (lower ? Direction.UP : Direction.DOWN)
                && (!neighbor.is(this) || neighbor.getValue(HALF) == state.getValue(HALF))) {
            if (lower && level instanceof net.minecraft.server.level.ServerLevel server)
                Block.dropResources(state, server, pos);
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative() && state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockPos bottom = pos.below();
            if (level.getBlockState(bottom).is(this)) level.setBlock(bottom, Blocks.AIR.defaultBlockState(), 35);
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean lower = state.getValue(HALF) == DoubleBlockHalf.LOWER;
        double x0 = lower ? -8 : -7, x1 = lower ? 24 : 23;
        double z0 = 1, z1 = 16;
        return switch (state.getValue(FACING)) {
            case SOUTH -> box(16-x1, 0, 16-z1, 16-x0, 16, 16-z0);
            case EAST -> box(16-z1, 0, x0, 16-z0, 16, x1);
            case WEST -> box(z0, 0, 16-x1, z1, 16, 16-x0);
            default -> box(x0, 0, z0, x1, 16, z1);
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            BlockPos basePos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
            // Older saves contain a one-block machine. Complete it on first use if space permits.
            if (state.getValue(HALF) == DoubleBlockHalf.LOWER
                    && level.getBlockState(pos.above()).canBeReplaced()
                    && pos.getY() + 1 < level.getMaxBuildHeight())
                level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
            MenuProvider provider = new SimpleMenuProvider(
                    (id, inventory, p) -> new GachaMenu(id, inventory, basePos),
                    Component.translatable("container.zzzplushies.gacha_machine"));
            NetworkHooks.openScreen(serverPlayer, provider, basePos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
