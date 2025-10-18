package com.grobe.techrebirth.block.custom.cable;

import com.grobe.techrebirth.block.custom.entity.EnergyCableBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
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

public class EnergyCableBlock extends BaseEntityBlock {

    public static final MapCodec<EnergyCableBlock> CODEC = simpleCodec(EnergyCableBlock::new);
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    // Boolean properties for each direction to determine if the cable should connect
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    public EnergyCableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    // This method is called when the block is placed in the world
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState()
                .setValue(NORTH, shouldConnectTo(pContext.getLevel(), pContext.getClickedPos(), Direction.NORTH))
                .setValue(SOUTH, shouldConnectTo(pContext.getLevel(), pContext.getClickedPos(), Direction.SOUTH))
                .setValue(EAST, shouldConnectTo(pContext.getLevel(), pContext.getClickedPos(), Direction.EAST))
                .setValue(WEST, shouldConnectTo(pContext.getLevel(), pContext.getClickedPos(), Direction.WEST))
                .setValue(UP, shouldConnectTo(pContext.getLevel(), pContext.getClickedPos(), Direction.UP))
                .setValue(DOWN, shouldConnectTo(pContext.getLevel(), pContext.getClickedPos(), Direction.DOWN));
    }

    // This method is called when a neighboring block changes
    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        return pState.setValue(getProperty(pFacing), shouldConnectTo((Level) pLevel, pCurrentPos, pFacing));
    }

    // This method determines if the cable should connect to a block in a given direction
    private boolean shouldConnectTo(Level level, BlockPos pos, Direction direction) {
        BlockPos neighborPos = pos.relative(direction);
        BlockState neighborState = level.getBlockState(neighborPos);
        if (neighborState.getBlock() instanceof EnergyCableBlock) return true;
        BlockEntity be = level.getBlockEntity(neighborPos);
        if (be == null) return false;
        // Check if a neighbor exposes energy capability on the facing side
       // return level.getCapability(ModCapabilities.ELECTRIC_FURNACE_ENERGY, neighborPos, neighborState, be, direction.getOpposite()) != null;
        return false;
    }

    private boolean shouldConnectTo(LevelAccessor level, BlockPos pos, Direction direction) {
        BlockPos neighborPos = pos.relative(direction);
        BlockState neighborState = level.getBlockState(neighborPos);
        if (neighborState.getBlock() instanceof EnergyCableBlock) return true;
        if (level instanceof Level realLevel) {
            BlockEntity be = realLevel.getBlockEntity(neighborPos);
            if (be == null) return false;
            //return realLevel.getCapability(ModCapabilities.ELECTRIC_FURNACE_ENERGY, neighborPos, neighborState, be, direction.getOpposite()) != null;
        }
        return false;
    }

    // This method returns the correct BooleanProperty for a given direction
    private BooleanProperty getProperty(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }

    // This method defines the shape of the block
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        VoxelShape shape = Shapes.box(0.375, 0.375, 0.375, 0.625, 0.625, 0.625);
        if (pState.getValue(NORTH)) {
            shape = Shapes.or(shape, Shapes.box(0.375, 0.375, 0, 0.625, 0.625, 0.375));
        }
        if (pState.getValue(SOUTH)) {
            shape = Shapes.or(shape, Shapes.box(0.375, 0.375, 0.625, 0.625, 0.625, 1));
        }
        if (pState.getValue(EAST)) {
            shape = Shapes.or(shape, Shapes.box(0.625, 0.375, 0.375, 1, 0.625, 0.625));
        }
        if (pState.getValue(WEST)) {
            shape = Shapes.or(shape, Shapes.box(0, 0.375, 0.375, 0.375, 0.625, 0.625));
        }
        if (pState.getValue(UP)) {
            shape = Shapes.or(shape, Shapes.box(0.375, 0.625, 0.375, 0.625, 1, 0.625));
        }
        if (pState.getValue(DOWN)) {
            shape = Shapes.or(shape, Shapes.box(0.375, 0, 0.375, 0.625, 0.375, 0.625));
        }
        return shape;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new EnergyCableBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, com.grobe.techrebirth.block.ModBlockEntities.CABLE.get(),
                EnergyCableBlockEntity::tick);
    }
}