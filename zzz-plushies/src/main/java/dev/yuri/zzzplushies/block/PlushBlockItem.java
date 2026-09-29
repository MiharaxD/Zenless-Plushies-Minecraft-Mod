package dev.yuri.zzzplushies.block;

import dev.yuri.zzzplushies.client.PlushItemClient;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public final class PlushBlockItem extends BlockItem {
    public PlushBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        PlushItemClient.initializeClient(consumer);
    }
}
