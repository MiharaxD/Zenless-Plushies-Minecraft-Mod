package dev.yuri.zzzplushies.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public final class PlushItemClient {
    private PlushItemClient() {}

    public static void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    Minecraft minecraft = Minecraft.getInstance();
                    renderer = new PlushItemRenderer(minecraft.getBlockEntityRenderDispatcher(),
                            minecraft.getEntityModels());
                }
                return renderer;
            }
        });
    }
}
