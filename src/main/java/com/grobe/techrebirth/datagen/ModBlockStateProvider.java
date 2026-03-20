package com.grobe.techrebirth.datagen;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.ModBlocks;
import com.grobe.techrebirth.block.custom.cable.EnergyCableBlock;
import com.grobe.techrebirth.util.MetalType;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Map;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, TechRebirth.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        //oreBlock(ModBlocks.LITHIUM_ORE); // Assuming this was a placeholder or exists

        columnBlock(ModBlocks.ENERGY_BANK);

        cableBlock(ModBlocks.ENERGY_CABLE);

        // Loop through all the dynamically registered ore blocks
        for (Map.Entry<MetalType, DeferredBlock<Block>> entry : ModBlocks.ORE_BLOCKS.entrySet()) {
            MetalType metal = entry.getKey();
            if (metal == MetalType.DIAMOND) continue; // Skip Diamond Block models

            DeferredBlock<Block> block = entry.getValue();

            // Create a custom model with tintindex set to 0 for all faces
            BlockModelBuilder model = models().withExistingParent(metal.getSerializedName() + "_block", "block/cube")
                    .texture("particle", modLoc("block/base_metal_block"))
                    .texture("all", modLoc("block/base_metal_block"))
                    .element()
                    .from(0, 0, 0)
                    .to(16, 16, 16)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#all").tintindex(0).cullface(direction))
                    .end();

            simpleBlock(block.get(), model);
        }
    }

    private void blockWithItem(DeferredBlock<Block> deferredBlock){
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
    private void oreBlock(DeferredBlock<Block> deferredBlock){
        String name = deferredBlock.getId().getPath();

        var model = models().cubeAll(name, modLoc("block/ore/" + name));

        simpleBlockWithItem(deferredBlock.get(), model);
    }

    private void columnBlock(DeferredBlock<Block> deferredBlock){
        String name = deferredBlock.getId().getPath();

        var model = models().cubeColumn(name,
                modLoc("block/" + name + "_side"),
                modLoc("block/" + name + "_top"));

        simpleBlockWithItem(deferredBlock.get(), model);
    }

    private void cableBlock(DeferredBlock<Block> deferredBlock) {
        String name = deferredBlock.getId().getPath();
        ResourceLocation texture = modLoc("block/cable/" + name);

        // Core Model: 6x6x6 cube in center
        BlockModelBuilder coreModel = models().withExistingParent(name + "_core", "block/block")
                .texture("particle", texture)
                .texture("all", texture)
                .element().from(5, 5, 5).to(11, 11, 11)
                .allFaces((d, f) -> f.texture("#all")).end();

        // Part Model: Arm extending North (6,6,0 to 10,10,6)
        BlockModelBuilder partModel = models().withExistingParent(name + "_part", "block/block")
                .texture("particle", texture)
                .texture("all", texture)
                .element().from(6, 6, 0).to(10, 10, 6)
                .allFaces((d, f) -> f.texture("#all")).end();

        getMultipartBuilder(deferredBlock.get())
                .part().modelFile(coreModel).addModel().end()
                .part().modelFile(partModel).addModel().condition(EnergyCableBlock.NORTH, true).end()
                .part().modelFile(partModel).rotationY(180).addModel().condition(EnergyCableBlock.SOUTH, true).end()
                .part().modelFile(partModel).rotationY(90).addModel().condition(EnergyCableBlock.EAST, true).end()
                .part().modelFile(partModel).rotationY(270).addModel().condition(EnergyCableBlock.WEST, true).end()
                .part().modelFile(partModel).rotationX(270).addModel().condition(EnergyCableBlock.UP, true).end()
                .part().modelFile(partModel).rotationX(90).addModel().condition(EnergyCableBlock.DOWN, true).end();
    }
}
