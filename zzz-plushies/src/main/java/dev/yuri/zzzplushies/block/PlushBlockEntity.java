package dev.yuri.zzzplushies.block;

import dev.yuri.zzzplushies.ZzzPlushies;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class PlushBlockEntity extends BlockEntity {
    public PlushBlockEntity(BlockPos pos, BlockState state) {
        super(ZzzPlushies.PLUSH_BLOCK_ENTITY.get(), pos, state);
    }
}
