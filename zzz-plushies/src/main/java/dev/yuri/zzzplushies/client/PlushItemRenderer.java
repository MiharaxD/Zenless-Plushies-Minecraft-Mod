package dev.yuri.zzzplushies.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.yuri.zzzplushies.block.PlushBlockEntity;
import dev.yuri.zzzplushies.block.PlushBlock;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class PlushItemRenderer extends BlockEntityWithoutLevelRenderer {
    private final BlockEntityRenderDispatcher dispatcher;

    public PlushItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet models) {
        super(dispatcher, models);
        this.dispatcher = dispatcher;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack pose,
                             MultiBufferSource buffers, int light, int overlay) {
        if (!(stack.getItem() instanceof BlockItem item)) return;
        // Item GUI views the +Z side; face the plush toward that camera.
        BlockState state = item.getBlock().defaultBlockState()
                .setValue(PlushBlock.FACING, Direction.SOUTH);
        PlushBlockEntity plush = new PlushBlockEntity(BlockPos.ZERO, state);
        dispatcher.renderItem(plush, pose, buffers, light, overlay);
    }
}
