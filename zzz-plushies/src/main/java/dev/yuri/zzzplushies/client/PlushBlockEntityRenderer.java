package dev.yuri.zzzplushies.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.yuri.zzzplushies.ZzzPlushies;
import dev.yuri.zzzplushies.block.PlushBlock;
import dev.yuri.zzzplushies.block.PlushBlockEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

public final class PlushBlockEntityRenderer implements BlockEntityRenderer<PlushBlockEntity> {
    private final PlayerModel<LivingEntity> model;

    public PlushBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        model = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false);
        // Each part gets its own world-space pivot; the player cubes only supply skin UVs.
        reset(model.head);
        reset(model.body);
        reset(model.leftArm);
        reset(model.rightArm);
        reset(model.leftLeg);
        reset(model.rightLeg);
        model.hat.copyFrom(model.head);
        model.jacket.copyFrom(model.body);
        model.leftSleeve.copyFrom(model.leftArm);
        model.rightSleeve.copyFrom(model.rightArm);
        model.leftPants.copyFrom(model.leftLeg);
        model.rightPants.copyFrom(model.rightLeg);
    }

    private static void reset(ModelPart part) {
        part.x = 0.0F;
        part.y = 0.0F;
        part.z = 0.0F;
        part.xRot = 0.0F;
        part.yRot = 0.0F;
        part.zRot = 0.0F;
    }

    @Override
    public void render(PlushBlockEntity plush, float partialTick, PoseStack pose,
                       MultiBufferSource buffers, int light, int overlay) {
        ResourceLocation key = ForgeRegistries.BLOCKS.getKey(plush.getBlockState().getBlock());
        if (key == null) return;
        ResourceLocation texture = new ResourceLocation(ZzzPlushies.MOD_ID,
                "textures/entity/" + key.getPath() + ".png");
        VertexConsumer vertices = buffers.getBuffer(RenderType.entityCutoutNoCull(texture));
        pose.pushPose();
        pose.translate(0.5D, 0.0D, 0.5D);
        // Keep the feet at y=0; the 15-pixel reference is 9.75 pixels tall here.
        pose.scale(0.65F, 0.65F, 0.65F);
        pose.mulPose(Axis.YP.rotationDegrees(180.0F - plush.getBlockState().getValue(PlushBlock.FACING).toYRot()));

        // Dimensions and pivots follow the supplied llary-plush Blockbench model, in block pixels.
        pose.pushPose();
        pose.translate(0.0D, 6.0D / 16.0D, 0.0D);
        pose.scale(0.75F, -0.50F, 1.50F); // torso: 6 x 6 x 6
        renderPair(model.body, model.jacket, pose, vertices, light);
        pose.popPose();

        pose.pushPose();
        pose.translate(0.0D, 6.0D / 16.0D, 0.0D);
        pose.scale(1.25F, -1.125F, 1.25F); // head: 10 x 9 x 10
        renderPair(model.head, model.hat, pose, vertices, light);
        pose.popPose();

        renderArm(model.rightArm, model.rightSleeve, 2.0F, 2.0F, 7.0F, 22.5F,
                -3.0F, pose, vertices, light);
        renderArm(model.leftArm, model.leftSleeve, 10.0F, 4.0F, 7.0F, -22.5F,
                -1.0F, pose, vertices, light);
        renderLeg(model.rightLeg, model.rightPants, 3.0F, 2.0F, 22.5F,
                pose, vertices, light);
        renderLeg(model.leftLeg, model.leftPants, 10.0F, 1.0F, -22.5F,
                pose, vertices, light);
        pose.popPose();
    }

    private static void renderArm(ModelPart base, ModelPart clothing,
                                  float pivotX, float pivotY, float pivotZ, float angle, float sourceMinX,
                                  PoseStack pose, VertexConsumer vertices, int light) {
        pose.pushPose();
        pose.translate((pivotX - 8.0F) / 16.0F, pivotY / 16.0F, (pivotZ - 8.0F) / 16.0F);
        pose.mulPose(Axis.ZP.rotationDegrees(angle));
        // Turn the skin's 4 x 12 x 4 arm into the reference's horizontal 5 x 2 x 2 arm.
        pose.translate((5.0F / 6.0F) / 16.0F, (2.0F + sourceMinX / 2.0F) / 16.0F, 1.0F / 16.0F);
        pose.mulPose(Axis.ZP.rotationDegrees(-90.0F));
        pose.scale(0.50F, 5.0F / 12.0F, 0.50F);
        renderPair(base, clothing, pose, vertices, light);
        pose.popPose();
    }

    private static void renderLeg(ModelPart base, ModelPart clothing,
                                  float pivotX, float pivotZ, float angle,
                                  PoseStack pose, VertexConsumer vertices, int light) {
        pose.pushPose();
        pose.translate((pivotX - 8.0F) / 16.0F, 0.0D, (pivotZ - 8.0F) / 16.0F);
        pose.mulPose(Axis.YP.rotationDegrees(angle));
        pose.translate(1.5F / 16.0F, 1.5F / 16.0F, 0.0D);
        pose.mulPose(Axis.XP.rotationDegrees(90.0F));
        pose.scale(0.75F, 5.0F / 12.0F, -0.75F); // leg: 3 x 3 x 5
        renderPair(base, clothing, pose, vertices, light);
        pose.popPose();
    }

    private static void renderPair(ModelPart base, ModelPart clothing, PoseStack pose,
                                   VertexConsumer vertices, int light) {
        base.render(pose, vertices, light, OverlayTexture.NO_OVERLAY);
        clothing.render(pose, vertices, light, OverlayTexture.NO_OVERLAY);
    }
}
