package com.grobe.techrebirth.block.custom.bank;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.entity.BaseMachineBlockEntity;
import com.grobe.techrebirth.block.custom.entity.bank.EnergyBankBlockEntity;
import com.grobe.techrebirth.util.EnergySideConfig;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class EnergyBankBlock extends BaseEntityBlock {
    public EnergyBankBlock(Properties properties) {
        super(properties);
    }

    public static final MapCodec<EnergyBankBlock> CODEC = simpleCodec(EnergyBankBlock::new);
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyBankBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(net.minecraft.world.level.Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, ModBlockEntities.ENERGY_BANK.get(), EnergyBankBlockEntity::tick);
    }

    public InteractionResult tryPickupWithWrench(BlockState state, Level level, BlockPos pos, Player player, ItemStack wrench) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof EnergyBankBlockEntity bank) {
            ItemStack machineStack = new ItemStack(this);
            CompoundTag customData = new CompoundTag();
            customData.putInt("StoredEnergy", bank.getLocalEnergy().getEnergyStored());
            
            CompoundTag configTag = new CompoundTag();
            for (Direction dir : Direction.values()) {
                configTag.putString(dir.getName(), bank.getSideConfig(dir).name());
            }
            customData.put("side_configs", configTag);

            CustomData dataComponent = CustomData.of(customData);
            machineStack.set(DataComponents.CUSTOM_DATA, dataComponent);

            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), machineStack);
            level.removeBlock(pos, false);
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.8F, 1.0F);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    public InteractionResult onWrenchRightClick(BlockState state, Level level, BlockPos pos, Player player, Direction side) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof EnergyBankBlockEntity bank) {
            bank.cycleSideConfig(side);
            EnergySideConfig newConfig = bank.getSideConfig(side);
            player.displayClientMessage(Component.literal("Side " + side.getName() + ": " + newConfig.name()), true);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (level.isClientSide) return;

        // Čitanje iz CUSTOM_DATA
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains("StoredEnergy")) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof EnergyBankBlockEntity bank) {
                    int savedEnergy = tag.getInt("StoredEnergy");
                    bank.setStoredEnergy(savedEnergy);
                    if (tag.contains("side_configs")) {
                        CompoundTag configTag = tag.getCompound("side_configs");

                        for (Direction dir : Direction.values()) {
                            if (configTag.contains(dir.getName())) {
                                try {
                                    EnergySideConfig config = EnergySideConfig.valueOf(
                                            configTag.getString(dir.getName())
                                    );

                                    bank.setSideConfigs(dir, config);
                                } catch (IllegalArgumentException e) {
                                    // Zadrži default konfiguraciju ako je podatak neispravan.
                                }
                            }
                        }
                    }
                    bank.setChanged();

                    System.out.println(this.getClass().getSimpleName() + " ENERGY LOADED: " + savedEnergy + " RF");
                }
            }

        }
    }
}
