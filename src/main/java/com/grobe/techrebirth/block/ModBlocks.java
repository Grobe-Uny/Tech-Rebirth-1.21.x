package com.grobe.techrebirth.block;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.custom.cable.EnergyCableBlock;
import com.grobe.techrebirth.block.custom.bank.EnergyBankBlock;
import com.grobe.techrebirth.block.custom.ElectricFurnaceBlock;
import com.grobe.techrebirth.block.custom.generator.GeneratorBlock;
import com.grobe.techrebirth.item.ModItems;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TechRebirth.MODID);

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
    public static final DeferredBlock<Block> INVAR_BLOCK = registerBlock("invar_block",
            () ->new Block(BlockBehaviour.Properties.of()
                            .strength(4f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.METAL)
            ));


    /// Block Entities


    public static final DeferredBlock<Block> ELECTRIC_FURNACE = registerBlock("electric_furnace",
            () -> new ElectricFurnaceBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
            ));
    public static final DeferredBlock<Block> CREATIVE_ELECTRIC_FURNACE = registerBlock("creative_electric_furnace",
            () -> new com.grobe.techrebirth.block.custom.CreativeElectricFurnaceBlock(BlockBehaviour.Properties.ofFullCopy(ModBlocks.ELECTRIC_FURNACE.get())));

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
            ));

    public static final DeferredBlock<Block> ENERGY_BANK = registerBlock("energy_bank",
            () -> new EnergyBankBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
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
