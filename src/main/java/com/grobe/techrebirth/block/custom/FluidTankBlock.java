package com.grobe.techrebirth.block.custom;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.entity.FluidTankBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidUtil;

import javax.annotation.Nullable;

public class FluidTankBlock extends BaseEntityBlock {

    public FluidTankBlock(Properties properties) {
        super(properties.noOcclusion());
    }

    private static final VoxelShape SHAPE = FluidTankBlock.box(2,0,2,14,16,14 );

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context){
        return SHAPE;
    }


    public static final MapCodec<FluidTankBlock> CODEC = simpleCodec(FluidTankBlock::new);
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.FLUID_TANK.get().create(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool){
        if (!level.isClientSide && blockEntity instanceof FluidTankBlockEntity tank) {
            ItemStack tankStack = new ItemStack(this);

            CompoundTag customData = new CompoundTag();
            CompoundTag fluidTag = new CompoundTag();

            tank.getFluidTank().writeToNBT(level.registryAccess(), fluidTag);
            customData.put("FluidTank", fluidTag);

            tankStack.set(DataComponents.CUSTOM_DATA, CustomData.of(customData));

            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), tankStack);
            return;
        }

        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack){

        super.setPlacedBy(level, pos, state, placer, stack);

        if (level.isClientSide) return;

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);

        if (customData != null) {
            CompoundTag tag = customData.copyTag();

            if (tag.contains("FluidTank")) {
                BlockEntity blockEntity = level.getBlockEntity(pos);

                if (blockEntity instanceof FluidTankBlockEntity tank) {
                    tank.getFluidTank().readFromNBT(
                            level.registryAccess(),
                            tag.getCompound("FluidTank")
                    );

                    tank.setChanged();
                }
            }
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        ItemStack itemInHand = player.getMainHandItem();

        // Samo fluid handler interakcije (kante, bucketi)
        if (FluidUtil.getFluidHandler(itemInHand).isPresent()) {
            if (!level.isClientSide()) {
                BlockEntity entity = level.getBlockEntity(pos);
                if (entity instanceof FluidTankBlockEntity) {
                    // Koristi FluidUtil za interakciju s kantičama
                    boolean success = FluidUtil.interactWithFluidHandler(player, InteractionHand.MAIN_HAND, level, pos, null);
                    return success ? InteractionResult.SUCCESS : InteractionResult.FAIL;
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        // Za sve ostalo - PASS (ne radi ništa)
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(type, ModBlockEntities.FLUID_TANK.get(), FluidTankBlockEntity::tick);
    }
}
