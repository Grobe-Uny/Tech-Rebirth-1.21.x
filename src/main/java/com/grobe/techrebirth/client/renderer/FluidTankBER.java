/*package com.grobe.techrebirth.client.renderer;

import com.grobe.techrebirth.block.custom.entity.FluidTankBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
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
}*/

package com.grobe.techrebirth.client.renderer;

import com.grobe.techrebirth.block.custom.entity.FluidTankBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidTankBER implements BlockEntityRenderer<FluidTankBlockEntity> {
    public FluidTankBER(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(FluidTankBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack,
                       MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        FluidStack fluidStack = pBlockEntity.getFluid();
        if (fluidStack.isEmpty()) {
            return;
        }

        float fluidLevel = (float) fluidStack.getAmount() / FluidTankBlockEntity.CAPACITY;
        if (fluidLevel <= 0) {
            return;
        }

        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        int fluidTint = fluidTypeExtensions.getTintColor(fluidStack);
        TextureAtlasSprite fluidSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(fluidTypeExtensions.getStillTexture(fluidStack));

        VertexConsumer builder = pBufferSource.getBuffer(RenderType.translucent());

        pPoseStack.pushPose();

        // Koristi relativne koordinate umjesto translacije u centar

        float shrink = 0.002f;


        float x1 = 0.0625f + shrink;  // 1/16
        float x2 = 0.9375f - shrink;  // 15/16
        float y1 = 0.0625f + shrink;
        float y2 = y1 + (0.875f * fluidLevel) - shrink; // Fluid raste od dna prema gore
        float z1 = 0.0625f + shrink;
        float z2 = 0.9375f - shrink;

        // Izračunaj boje za svaki kanal
        float r = (float)(fluidTint >> 16 & 255) / 255.0F;
        float g = (float)(fluidTint >> 8 & 255) / 255.0F;
        float b = (float)(fluidTint & 255) / 255.0F;
        float a = 1.0f;

        // TOP face
        addVertex(builder, pPoseStack, x1, y2, z1, r, g, b, a, fluidSprite.getU0(), fluidSprite.getV0(), pPackedLight);
        addVertex(builder, pPoseStack, x1, y2, z2, r, g, b, a, fluidSprite.getU0(), fluidSprite.getV1(), pPackedLight);
        addVertex(builder, pPoseStack, x2, y2, z2, r, g, b, a, fluidSprite.getU1(), fluidSprite.getV1(), pPackedLight);
        addVertex(builder, pPoseStack, x2, y2, z1, r, g, b, a, fluidSprite.getU1(), fluidSprite.getV0(), pPackedLight);

        // BOTTOM face
        addVertex(builder, pPoseStack, x2, y1, z1, r, g, b, a, fluidSprite.getU1(), fluidSprite.getV0(), pPackedLight);
        addVertex(builder, pPoseStack, x2, y1, z2, r, g, b, a, fluidSprite.getU1(), fluidSprite.getV1(), pPackedLight);
        addVertex(builder, pPoseStack, x1, y1, z2, r, g, b, a, fluidSprite.getU0(), fluidSprite.getV1(), pPackedLight);
        addVertex(builder, pPoseStack, x1, y1, z1, r, g, b, a, fluidSprite.getU0(), fluidSprite.getV0(), pPackedLight);

        // NORTH face
        addVertex(builder, pPoseStack, x1, y1, z1, r, g, b, a, fluidSprite.getU0(), fluidSprite.getV1(), pPackedLight);
        addVertex(builder, pPoseStack, x1, y2, z1, r, g, b, a, fluidSprite.getU0(), fluidSprite.getV0(), pPackedLight);
        addVertex(builder, pPoseStack, x2, y2, z1, r, g, b, a, fluidSprite.getU1(), fluidSprite.getV0(), pPackedLight);
        addVertex(builder, pPoseStack, x2, y1, z1, r, g, b, a, fluidSprite.getU1(), fluidSprite.getV1(), pPackedLight);

        // SOUTH face
        addVertex(builder, pPoseStack, x2, y1, z2, r, g, b, a, fluidSprite.getU1(), fluidSprite.getV1(), pPackedLight);
        addVertex(builder, pPoseStack, x2, y2, z2, r, g, b, a, fluidSprite.getU1(), fluidSprite.getV0(), pPackedLight);
        addVertex(builder, pPoseStack, x1, y2, z2, r, g, b, a, fluidSprite.getU0(), fluidSprite.getV0(), pPackedLight);
        addVertex(builder, pPoseStack, x1, y1, z2, r, g, b, a, fluidSprite.getU0(), fluidSprite.getV1(), pPackedLight);

        // WEST face
        addVertex(builder, pPoseStack, x1, y1, z2, r, g, b, a, fluidSprite.getU0(), fluidSprite.getV1(), pPackedLight);
        addVertex(builder, pPoseStack, x1, y2, z2, r, g, b, a, fluidSprite.getU0(), fluidSprite.getV0(), pPackedLight);
        addVertex(builder, pPoseStack, x1, y2, z1, r, g, b, a, fluidSprite.getU1(), fluidSprite.getV0(), pPackedLight);
        addVertex(builder, pPoseStack, x1, y1, z1, r, g, b, a, fluidSprite.getU1(), fluidSprite.getV1(), pPackedLight);

        // EAST face
        addVertex(builder, pPoseStack, x2, y1, z1, r, g, b, a, fluidSprite.getU1(), fluidSprite.getV1(), pPackedLight);
        addVertex(builder, pPoseStack, x2, y2, z1, r, g, b, a, fluidSprite.getU1(), fluidSprite.getV0(), pPackedLight);
        addVertex(builder, pPoseStack, x2, y2, z2, r, g, b, a, fluidSprite.getU0(), fluidSprite.getV0(), pPackedLight);
        addVertex(builder, pPoseStack, x2, y1, z2, r, g, b, a, fluidSprite.getU0(), fluidSprite.getV1(), pPackedLight);

        pPoseStack.popPose();
    }

    private void addVertex(VertexConsumer builder, PoseStack poseStack, float x, float y, float z,
                           float r, float g, float b, float a, float u, float v, int light) {
        builder.addVertex(poseStack.last().pose(), x, y, z)
                .setColor(r, g, b, a)
                .setUv(u, v)
                .setUv2(light, light)
                .setNormal(poseStack.last(), 0, 1, 0);
    }
}
