package com.grobe.techrebirth.block;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.custom.BaseMachineBlock;
import com.grobe.techrebirth.block.custom.CreativeElectricFurnaceBlock;
import com.grobe.techrebirth.block.custom.ElectricCrusherBlock;
import com.grobe.techrebirth.block.custom.alloy.AlloySmelterBlock;
import com.grobe.techrebirth.block.custom.alloy.HardenedAlloySmelterBlock;
import com.grobe.techrebirth.block.custom.cable.EnergyCableBlock;
import com.grobe.techrebirth.block.custom.bank.EnergyBankBlock;
import com.grobe.techrebirth.block.custom.entity.BaseMachineBlockEntity;
import com.grobe.techrebirth.block.custom.entity.furnace.ElectricFurnaceBlockEntity;
import com.grobe.techrebirth.block.custom.furnace.ElectricFurnaceBlock;
import com.grobe.techrebirth.block.custom.furnace.HardenedElectricFurnaceBlock;
import com.grobe.techrebirth.block.custom.FluidTankBlock;
import com.grobe.techrebirth.block.custom.furnace.ReinforcedElectricFurnaceBlock;
import com.grobe.techrebirth.block.custom.generator.GeneratorBlock;
import com.grobe.techrebirth.item.ModItems;
import com.grobe.techrebirth.util.MachineTier;
import com.grobe.techrebirth.util.MetalType;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TechRebirth.MODID);

    // A map to hold dynamically registered ore blocks
    public static final Map<MetalType, DeferredBlock<Block>> ORE_BLOCKS = new EnumMap<>(MetalType.class);

    static {
        for(MetalType metal : MetalType.values()){
            // Skip Diamond, as we only want the nugget, not the block
            if (metal == MetalType.DIAMOND) continue;

            ORE_BLOCKS.put(metal, ModBlocks.registerBlock(metal.getSerializedName() + "_block",
                    () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)


                    )));
        }
    }

    public static final DeferredBlock<Block> MACHINE_BASE = registerBlock("machine_base",
            ()-> new Block(BlockBehaviour.Properties.of()
                    .strength(2.5f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
            ));

    public static final DeferredBlock<Block> TIN_ORE = registerBlock("tin_ore",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),
                    BlockBehaviour.Properties.of()
                            .strength(3f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)
            ));

    public static final DeferredBlock<Block> TIN_DEEPSLATE_ORE = registerBlock("tin_deepslate_ore",
            () -> new DropExperienceBlock(UniformInt.of(2, 5),
                    BlockBehaviour.Properties.of()
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)
            ));
    public static final DeferredBlock<Block> LEAD_ORE = registerBlock("lead_ore",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),
                    BlockBehaviour.Properties.of()
                            .strength(3f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)
            ));

    public static final DeferredBlock<Block> LEAD_DEEPSLATE_ORE = registerBlock("lead_deepslate_ore",
            () -> new DropExperienceBlock(UniformInt.of(2, 5),
                    BlockBehaviour.Properties.of()
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)
            ));
    public static final DeferredBlock<Block> NICKEL_ORE = registerBlock("nickel_ore",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),
                    BlockBehaviour.Properties.of()
                            .strength(3f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)
            ));

    public static final DeferredBlock<Block> NICKEL_DEEPSLATE_ORE = registerBlock("nickel_deepslate_ore",
            () -> new DropExperienceBlock(UniformInt.of(2, 5),
                    BlockBehaviour.Properties.of()
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)
            ));
//    public static final DeferredBlock<Block> INVAR_BLOCK = registerBlock("invar_block",
//            () ->new Block(BlockBehaviour.Properties.of()
//                            .strength(4f)
//                            .requiresCorrectToolForDrops()
//                            .sound(SoundType.METAL)
//            ));


    /// Block Entities


    public static final DeferredBlock<Block> ELECTRIC_FURNACE = registerBlock("electric_furnace",
            () -> new ElectricFurnaceBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
            ));

    public static final DeferredBlock<Block> HARDENED_ELECTRIC_FURNACE = registerBlock("hardened_electric_furnace",
            () -> new HardenedElectricFurnaceBlock(BlockBehaviour.Properties.ofFullCopy(ModBlocks.ELECTRIC_FURNACE.get())));
    public static final DeferredBlock<Block> REINFORCED_ELECTRIC_FURNACE = registerBlock("reinforced_electric_furnace",
            () -> new ReinforcedElectricFurnaceBlock(BlockBehaviour.Properties.ofFullCopy(ModBlocks.ELECTRIC_FURNACE.get())));

    public static final DeferredBlock<Block> ALLOY_SMELTER = registerBlock("alloy_smelter",
            ()-> new AlloySmelterBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
            ));
    public static final DeferredBlock<Block> HARDENED_ALLOY_SMELTER = registerBlock("hardened_alloy_smelter",
            ()-> new HardenedAlloySmelterBlock(BlockBehaviour.Properties.ofFullCopy(ALLOY_SMELTER.get())
            ));

    public static final DeferredBlock<Block> ELECTRIC_CRUSHER = registerBlock("electric_crusher",
            () -> new ElectricCrusherBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
            ));
    public static final DeferredBlock<Block> CREATIVE_ELECTRIC_FURNACE = registerBlock("creative_electric_furnace",
            () -> new CreativeElectricFurnaceBlock(BlockBehaviour.Properties.ofFullCopy(ModBlocks.ELECTRIC_FURNACE.get())));


    public static final DeferredBlock<Block> GENERATOR = registerBlock("generator",
            () -> new GeneratorBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
            ));

    public static final DeferredBlock<Block> ENERGY_CABLE = registerBlock("energy_cable",
            () -> new EnergyCableBlock(BlockBehaviour.Properties.of()
                    .strength(0.5f)
                    .sound(SoundType.METAL)
                    .noOcclusion()
            ));

    public static final DeferredBlock<Block> ENERGY_BANK = registerBlock("energy_bank",
            () -> new EnergyBankBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
            ));

    public static final DeferredBlock<Block> ELECTRIC_CENTRIFUGE = registerBlock("electric_centrifuge",
            () -> new com.grobe.techrebirth.block.custom.ElectricCentrifugeBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
            ));

    public static final DeferredBlock<Block> FLUID_TANK = registerBlock("fluid_tank",
            () -> new FluidTankBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
                    .noOcclusion()
            ));

    private static <T extends  Block> DeferredBlock<T> registerBlock (String name, Supplier<T> block){
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block){
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }


    public static void register (IEventBus eventbus){
        BLOCKS.register(eventbus);
    }
}
