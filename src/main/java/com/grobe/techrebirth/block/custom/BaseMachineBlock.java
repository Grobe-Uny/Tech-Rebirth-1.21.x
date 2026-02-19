package com.grobe.techrebirth.block.custom;

import com.grobe.techrebirth.block.custom.entity.BaseMachineBlockEntity;
import com.grobe.techrebirth.util.MachineTier;
import com.grobe.techrebirth.util.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

public abstract class BaseMachineBlock extends BaseEntityBlock implements EntityBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);


    private final int maxEnergy;

    public BaseMachineBlock(Properties properties, int maxEnergy) {
        super(properties);
        this.maxEnergy = maxEnergy;
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false).setValue(FACING, Direction.NORTH));
    }
    public BaseMachineBlock(Properties properties, MachineTier tier) {
        super(properties);
        this.maxEnergy = tier.energyCapacity;
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false).setValue(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof BaseMachineBlockEntity be) {
                ((ServerPlayer) player).openMenu(be, pos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    // ALTERNATIVA: Koristi postojeću CUSTOM_DATA komponentu
    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (!level.isClientSide && blockEntity instanceof BaseMachineBlockEntity machine) {
            ItemStack droppedStack = new ItemStack(this);

            // Koristi standardnu CUSTOM_DATA komponentu
            CompoundTag customData = new CompoundTag();
            customData.putInt("StoredEnergy", machine.getEnergyStorage().getEnergyStored());
            customData.putInt("Progress", machine.getProgress());
            customData.putInt("MaxProgress", machine.getMaxProgress());

            // Spremi specifične podatke
            saveMachineSpecificData(machine, customData);

            // Postavi custom data
            CustomData dataComponent = CustomData.of(customData);
            droppedStack.set(DataComponents.CUSTOM_DATA, dataComponent);

            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), droppedStack);

            handleMachineSpecificDrops(machine,level,pos);

            return;
        }
            super.playerDestroy(level, player, pos, state, blockEntity, tool);

    }

    public InteractionResult tryPickupWithWrench(BlockState state, Level level, BlockPos pos, Player player, ItemStack wrench) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BaseMachineBlockEntity machine) {

            // Spremi podatke mašine koristeći postojeći sistem
            ItemStack machineStack = new ItemStack(this);

            // Koristi isti način kao u playerDestroy
            CompoundTag customData = new CompoundTag();
            customData.putInt("StoredEnergy", machine.getEnergyStorage().getEnergyStored());

            // Spremi specifične podatke
            saveMachineSpecificData(machine, customData);

            // Postavi custom data
            CustomData dataComponent = CustomData.of(customData);
            machineStack.set(DataComponents.CUSTOM_DATA, dataComponent);

            // Dropaj item i ukloni block
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), machineStack);
            level.removeBlock(pos, false);


            // Dodaj sound efekte
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.8F, 1.0F);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (level.isClientSide) return;

        // Čitanje iz CUSTOM_DATA
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null /*&& customData.contains("StoredEnergy")*/) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains("StoredEnergy")) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof BaseMachineBlockEntity machine) {
                    int savedEnergy = tag.getInt("StoredEnergy");
                    machine.setEnergyStored(savedEnergy);

//                if (customData.contains("Progress")) {
//                    machine.progress = customData.getInt("Progress");
//                }
//                if (customData.contains("MaxProgress")) {
//                    machine.maxProgress = customData.getInt("MaxProgress");
//                }

                    loadMachineSpecificData(machine, tag);
                    machine.setChanged();

                    System.out.println("📂 " + this.getClass().getSimpleName() + " ENERGY LOADED: " + savedEnergy + " RF");
                }
            }

        }
    }

    // DODAJ: Standardni tooltip za sve mašine
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        // ISPRAVKA: Koristi getComponents() za čitanje NBT-a
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains("StoredEnergy")) {
                int energy = tag.getInt("StoredEnergy");
                int maxEnergy = getMaxEnergyForTier(); // Ovu metodu trebaš dodati

                energy = Math.max(0, energy);
                maxEnergy = Math.max(1, maxEnergy);

                tooltip.add(Component.literal("Stored Energy: " + energy + "/" + maxEnergy + " RF")
                        .withStyle(ChatFormatting.BLUE));

                // Progress bar...
                float percent = Math.max(0f,Math.min(1f, (float)energy/maxEnergy));
                int bars = (int) (percent * 10);
                bars = Math.max(0, Math.min(10,bars));
                String progressBar = "█".repeat(bars) + "▒".repeat(10 - bars);

                tooltip.add(Component.literal("[" + progressBar + "]")
                        .withStyle(getEnergyColor(percent)));
            }
        } else {
            tooltip.add(Component.literal("Stored Energy: 0/" + getMaxEnergyForTier() + " RF")
                    .withStyle(ChatFormatting.GRAY));
        }

        appendMachineSpecificTooltip(stack, context, tooltip, flag);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide() && state.getBlock() != newState.getBlock()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BaseMachineBlockEntity machine) {
                handleMachineSpecificDrops(machine, level, pos);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    // Apstraktna metoda za specifične drops - override u podklasama
    protected abstract void handleMachineSpecificDrops(BaseMachineBlockEntity machine, Level level, BlockPos pos);


    protected int getMaxEnergyForTier() {
        return maxEnergy;
    }


    // POMOĆNE METODE
    protected ChatFormatting getEnergyColor(float percent) {
        if (percent >= 0.8f) return ChatFormatting.GREEN;
        if (percent >= 0.3f) return ChatFormatting.YELLOW;
        return ChatFormatting.RED;
    }

    // METODE ZA OVERRIDE - prazne implementacije
    protected void saveMachineSpecificData(BaseMachineBlockEntity machine, CompoundTag tag) {
        // Override u podklasama za specifične podatke
    }

    protected void loadMachineSpecificData(BaseMachineBlockEntity machine, CompoundTag tag) {
        // Override u podklasama za specifične podatke
    }

    protected void appendMachineSpecificTooltip(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        // Override u podklasama za specifične tooltipove
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> type, BlockEntityType<? extends BaseMachineBlockEntity> targetType) {
        return createTickerHelper(type, targetType, (lvl, pos, st, be) -> be.tick(lvl, pos, st));
    }
}
