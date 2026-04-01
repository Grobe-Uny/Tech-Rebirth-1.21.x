package com.grobe.techrebirth.datagen;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.ModBlocks;
import com.grobe.techrebirth.block.custom.cable.EnergyCableBlock;
import com.grobe.techrebirth.util.MetalType;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Map;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, TechRebirth.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        oreBlock(ModBlocks.LITHIUM_ORE);

        columnBlock(ModBlocks.ENERGY_BANK);

        cableBlock(ModBlocks.ENERGY_CABLE);

        fluidBlock(ModBlocks.LIQUIFIED_COAL_BLOCK);

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

        // Add Electric Purifier
        //directionalMachineBlock(ModBlocks.ELECTRIC_PURIFIER.get(), "electric_purifier");

        directionalMachineBlock(ModBlocks.FLUID_INFUSER.get(), "fluid_infuser", "/infuser/");
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

    private void directionalMachineBlock(Block block, String name, String folder) {
        ResourceLocation offTexture = modLoc("block/machine" + folder + name + "_front");
        ResourceLocation onTexture = modLoc("block/machine" + folder + name + "_front_on");
        //ResourceLocation sideTexture = modLoc("block/" + name + "_side");
        ResourceLocation sideTexture = modLoc("block/machine/machine_side");
        //ResourceLocation topTexture = modLoc("block/" + name + "_top");
        ResourceLocation topTexture = modLoc("block/machine/machine_top");

        ModelFile modelOff = models().cube(name,
                        sideTexture, // down
                        topTexture,  // up
                        offTexture,  // north
                        sideTexture, // south
                        sideTexture, // east
                        sideTexture  // west
                )
                .texture("particle", offTexture);

        ModelFile modelOn = models().cube(name + "_on",
                        sideTexture, // down
                        topTexture,  // up
                        onTexture,   // north
                        sideTexture, // south
                        sideTexture, // east
                        sideTexture  // west
                )
                .texture("particle", onTexture);

        getVariantBuilder(block)
                .forAllStates(state -> {
                    // Check if LIT property exists, otherwise default to off (or on)
                    boolean lit = state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT);

                    int rotationY = (((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360);

                    return ConfiguredModel.builder()
                            .modelFile(lit ? modelOn : modelOff)
                            .rotationY(rotationY)
                            .build();
                     });
        simpleBlockItem(block, modelOff);
    }

    private void cableBlock(DeferredBlock<Block> deferredBlock) {
        String name = deferredBlock.getId().getPath();
        ResourceLocation coreTexture = modLoc("block/cable/" + name + "_core");
        ResourceLocation armTexture = modLoc("block/cable/" + name + "_arm");

        // Core Model: 6x6x6 cube in center. 
        // Explicitly map UVs to 0,0 -> 16,16 to use the full texture on the small face.
        BlockModelBuilder coreModel = models().withExistingParent(name + "_core", "block/block")
                .texture("particle", coreTexture)
                .texture("all", coreTexture)
                .element().from(5, 5, 5).to(11, 11, 11)
                .allFaces((d, f) -> f.texture("#all").uvs(0, 0, 16, 16)).end();

        // Part Model: Arm extending North (6,6,0 to 10,10,8)
        // Explicitly map UVs to 0,0 -> 16,16 for sides to use full texture.
        // For the end faces (North/South), map to 0,0 -> 16,16 as well.
        BlockModelBuilder partModel = models().withExistingParent(name + "_part", "block/block")
                .texture("particle", armTexture)
                .texture("all", armTexture)
                .element().from(6, 6, 0).to(10, 10, 8)
                .face(Direction.UP).texture("#all").uvs(0, 0, 8, 16).end()
                .face(Direction.DOWN).texture("#all").uvs(0, 0, 8, 16).end()
                .face(Direction.EAST).texture("#all").uvs(0, 0, 8, 16).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
                .face(Direction.WEST).texture("#all").uvs(0, 0, 8, 16).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()

                // NORTH (vrh kabela koji vidiš kad nije spojen)
                // Ovdje uzimamo samo onaj središnji crveni dio (kvadrat)
                .face(Direction.NORTH).texture("#all").uvs(2, 4, 6, 8).end()
                .end();

        getMultipartBuilder(deferredBlock.get())
                // Core only renders if RENDER_CORE property is true
                .part().modelFile(coreModel).addModel().condition(EnergyCableBlock.RENDER_CORE, true).end()
                
                // Arms render based on direction properties
                .part().modelFile(partModel).addModel().condition(EnergyCableBlock.NORTH, true).end()
                .part().modelFile(partModel).rotationY(180).addModel().condition(EnergyCableBlock.SOUTH, true).end()
                .part().modelFile(partModel).rotationY(90).addModel().condition(EnergyCableBlock.EAST, true).end()
                .part().modelFile(partModel).rotationY(270).addModel().condition(EnergyCableBlock.WEST, true).end()
                .part().modelFile(partModel).rotationX(270).addModel().condition(EnergyCableBlock.UP, true).end()
                .part().modelFile(partModel).rotationX(90).addModel().condition(EnergyCableBlock.DOWN, true).end();


        // --- DODATAK ZA ITEM MODEL ---
        // Ovo kreira JSON u assets/modid/models/item/ime_kabela.json
        // koji pokazuje na model jezgre (core)
        itemModels().withExistingParent(name, modLoc("block/" + name + "_core"))
                .transforms()
                // Postavke za prikaz u GUI-u (inventar)
                .transform(ItemDisplayContext.GUI)
                .rotation(30, 225, 0) // Standardni kosi kut za blokove
                .translation(0, 0, 0)
                .scale(2f, 2f, 2f) // POVEĆAJ OVDIJE (npr. 2.5 puta)
                .end()
                // Postavke kad ga držiš u ruci (First Person)
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                .rotation(0, 45, 0)
                .scale(1.1f, 1.1f, 1.1f)
                .end()
                .end();
    }

    private void fluidBlock(DeferredBlock<?> deferredBlock) {
        simpleBlock(deferredBlock.get(), models().withExistingParent(deferredBlock.getId().getPath(), mcLoc("block/water")));
    }
}
