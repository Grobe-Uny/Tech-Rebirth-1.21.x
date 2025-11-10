package com.grobe.techrebirth.client.renderer;

import com.grobe.techrebirth.block.custom.entity.FluidTankBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

public class FluidTankBER implements BlockEntityRenderer<FluidTankBlockEntity> {
    public FluidTankBER(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(FluidTankBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack,
                       MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        FluidStack fluidStack = pBlockEntity.getFluidTank().getFluid();
        if (fluidStack.isEmpty()) {
            return;
        }

        float fluidLevel = (float) fluidStack.getAmount() / pBlockEntity.getFluidTank().getCapacity();
        if (fluidLevel <= 0) {
            return;
        }

        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        int fluidTint = fluidTypeExtensions.getTintColor(fluidStack);
        TextureAtlasSprite fluidSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(fluidTypeExtensions.getStillTexture(fluidStack));

        VertexConsumer builder = pBufferSource.getBuffer(RenderType.translucent());

        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 0.5, 0.5);
        Matrix4f matrix = pPoseStack.last().pose();

        float x1 = -0.4375f;
        float x2 = 0.4375f;
        float y1 = -0.5f;
        float y2 = -0.5f + (1.0f * fluidLevel);
        float z1 = -0.4375f;
        float z2 = 0.4375f;

        // Top
        builder.vertex(matrix, x1, y2, z1).color(fluidTint).uv(fluidSprite.getU(0), fluidSprite.getV(0)).uv2(pPackedLight).normal(0, 1, 0).endVertex();
        builder.vertex(matrix, x1, y2, z2).color(fluidTint).uv(fluidSprite.getU(0), fluidSprite.getV(16)).uv2(pPackedLight).normal(0, 1, 0).endVertex();
        builder.vertex(matrix, x2, y2, z2).color(fluidTint).uv(fluidSprite.getU(16), fluidSprite.getV(16)).uv2(pPackedLight).normal(0, 1, 0).endVertex();
        builder.vertex(matrix, x2, y2, z1).color(fluidTint).uv(fluidSprite.getU(16), fluidSprite.getV(0)).uv2(pPackedLight).normal(0, 1, 0).endVertex();

        // North
        builder.vertex(matrix, x1, y1, z1).color(fluidTint).uv(fluidSprite.getU(0), fluidSprite.getV(0)).uv2(pPackedLight).normal(0, 0, -1).endVertex();
        builder.vertex(matrix, x2, y1, z1).color(fluidTint).uv(fluidSprite.getU(16), fluidSprite.getV(0)).uv2(pPackedLight).normal(0, 0, -1).endVertex();
        builder.vertex(matrix, x2, y2, z1).color(fluidTint).uv(fluidSprite.getU(16), fluidSprite.getV(16)).uv2(pPackedLight).normal(0, 0, -1).endVertex();
        builder.vertex(matrix, x1, y2, z1).color(fluidTint).uv(fluidSprite.getU(0), fluidSprite.getV(16)).uv2(pPackedLight).normal(0, 0, -1).endVertex();

        // South
        builder.vertex(matrix, x2, y1, z2).color(fluidTint).uv(fluidSprite.getU(0), fluidSprite.getV(0)).uv2(pPackedLight).normal(0, 0, 1).endVertex();
        builder.vertex(matrix, x1, y1, z2).color(fluidTint).uv(fluidSprite.getU(16), fluidSprite.getV(0)).uv2(pPackedLight).normal(0, 0, 1).endVertex();
        builder.vertex(matrix, x1, y2, z2).color(fluidTint).uv(fluidSprite.getU(16), fluidSprite.getV(16)).uv2(pPackedLight).normal(0, 0, 1).endVertex();
        builder.vertex(matrix, x2, y2, z2).color(fluidTint).uv(fluidSprite.getU(0), fluidSprite.getV(16)).uv2(pPackedLight).normal(0, 0, 1).endVertex();

        // West
        builder.vertex(matrix, x1, y1, z2).color(fluidTint).uv(fluidSprite.getU(0), fluidSprite.getV(0)).uv2(pPackedLight).normal(-1, 0, 0).endVertex();
        builder.vertex(matrix, x1, y1, z1).color(fluidTint).uv(fluidSprite.getU(16), fluidSprite.getV(0)).uv2(pPackedLight).normal(-1, 0, 0).endVertex();
        builder.vertex(matrix, x1, y2, z1).color(fluidTint).uv(fluidSprite.getU(16), fluidSprite.getV(16)).uv2(pPackedLight).normal(-1, 0, 0).endVertex();
        builder.vertex(matrix, x1, y2, z2).color(fluidTint).uv(fluidSprite.getU(0), fluidSprite.getV(16)).uv2(pPackedLight).normal(-1, 0, 0).endVertex();

        // East
        builder.vertex(matrix, x2, y1, z1).color(fluidTint).uv(fluidSprite.getU(0), fluidSprite.getV(0)).uv2(pPackedLight).normal(1, 0, 0).endVertex();
        builder.vertex(matrix, x2, y1, z2).color(fluidTint).uv(fluidSprite.getU(16), fluidSprite.getV(0)).uv2(pPackedLight).normal(1, 0, 0).endVertex();
        builder.vertex(matrix, x2, y2, z2).color(fluidTint).uv(fluidSprite.getU(16), fluidSprite.getV(16)).uv2(pPackedLight).normal(1, 0, 0).endVertex();
        builder.vertex(matrix, x2, y2, z1).color(fluidTint).uv(fluidSprite.getU(0), fluidSprite.getV(16)).uv2(pPackedLight).normal(1, 0, 0).endVertex();

        pPoseStack.popPose();
    }
}
