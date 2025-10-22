package com.grobe.techrebirth.block.custom.cable;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.block.custom.entity.EnergyCableBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class EnergyCableBlock extends BaseEntityBlock implements EntityBlock {

    // Properties za svaku stranu
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    // Voxel shapes za različite konekcije
    private static final VoxelShape CORE = Block.box(6, 6, 6, 10, 10, 10);
    private static final VoxelShape NORTH_ARM = Block.box(6, 6, 0, 10, 10, 6);
    private static final VoxelShape SOUTH_ARM = Block.box(6, 6, 10, 10, 10, 16);
    private static final VoxelShape EAST_ARM = Block.box(10, 6, 6, 16, 10, 10);
    private static final VoxelShape WEST_ARM = Block.box(0, 6, 6, 6, 10, 10);
    private static final VoxelShape UP_ARM = Block.box(6, 10, 6, 10, 16, 10);
    private static final VoxelShape DOWN_ARM = Block.box(6, 0, 6, 10, 6, 10);

    public EnergyCableBlock(Properties properties) {
        super(properties.noOcclusion());
        // Postavi default state sa svim konekcijama na false
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false));
    }

    public static final MapCodec<EnergyCableBlock> CODEC = Block.simpleCodec(EnergyCableBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CORE;

        // Dodaj "krake" ovisno o konekcijama
        if (state.getValue(NORTH)) shape = Shapes.or(shape, NORTH_ARM);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, SOUTH_ARM);
        if (state.getValue(EAST)) shape = Shapes.or(shape, EAST_ARM);
        if (state.getValue(WEST)) shape = Shapes.or(shape, WEST_ARM);
        if (state.getValue(UP)) shape = Shapes.or(shape, UP_ARM);
        if (state.getValue(DOWN)) shape = Shapes.or(shape, DOWN_ARM);

        return shape;
    }

    @Override
    public void onPlace(BlockState newState, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(newState, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            updateConnections(level, pos);
            notifyNeighbors(level, pos);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);
        notifyNeighbors(level, pos);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block,
                                BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);

        if (!level.isClientSide) {
            updateConnections(level, pos);

            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof EnergyCableBlockEntity cable) {
                cable.markNetworkDirty();
            }
        }
    }


    /**
     * Provjerava treba li se kabel spojiti na susjedni blok
     */
    private boolean shouldConnectTo(Level level, BlockPos pos, Direction direction) {
        BlockPos neighborPos = pos.relative(direction);

        // Spajaj se na druge kablove
        if (level.getBlockState(neighborPos).getBlock() instanceof EnergyCableBlock) {
            return true;
        }

        // Spajaj se na mašine koje imaju energy capability
        return level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.BLOCK,
                neighborPos, direction.getOpposite()) != null;
    }

    /**
     * Vraća BooleanProperty za dani smjer
     */
    private BooleanProperty getPropertyForDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }

    /**
     * Ažurira sve konekcije za ovaj kabel
     */
    private void updateConnections(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        BlockState newState = state;

        for (Direction direction : Direction.values()) {
            boolean shouldConnect = shouldConnectTo(level, pos, direction);
            newState = newState.setValue(getPropertyForDirection(direction), shouldConnect);
        }

        // Postavi novi state SAMO ako se stvarno promijenio
        if (!state.equals(newState)) {
            level.setBlock(pos, newState, Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);
        }
    }

    private void notifyNeighbors(Level level, BlockPos pos) {
        // Obavijesti susjedne kablove
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            BlockEntity be = level.getBlockEntity(neighborPos);
            if (be instanceof EnergyCableBlockEntity cable) {
                cable.markNetworkDirty();
            }
        }

        // Također obavijesti sve susjedne blokove da ažuriraju svoje konekcije
        for (Direction direction : Direction.values()) {
            level.updateNeighborsAt(pos.relative(direction), this);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyCableBlockEntity(pos, state);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, ModBlockEntities.ENERGY_CABLE.get(), EnergyCableBlockEntity::tick);

    }
    @Override
    public RenderShape getRenderShape(BlockState state){
        return RenderShape.MODEL;
    }
}