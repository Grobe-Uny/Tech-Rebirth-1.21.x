package com.grobe.techrebirth.client.renderer;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.custom.entity.CrucibleBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class CrucibleRenderer implements BlockEntityRenderer<CrucibleBlockEntity> {
    private final Crucible model;
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "textures/block/crucible.png");

    public CrucibleRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new Crucible(context.bakeLayer(Crucible.LAYER_LOCATION));
    }

    @Override
    public void render(CrucibleBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.5f, 1.5f, 0.5f);
        pPoseStack.scale(-1.0f, -1.0f, 1.0f); // Blockbench models are usually flipped

        VertexConsumer vertexConsumer = pBufferSource.getBuffer(this.model.renderType(TEXTURE));
        this.model.renderToBuffer(pPoseStack, vertexConsumer, pPackedLight, pPackedOverlay, 0xFFFFFFFF);

        pPoseStack.popPose();
    }
}
