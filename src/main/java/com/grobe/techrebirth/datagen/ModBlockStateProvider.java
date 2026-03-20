package com.grobe.techrebirth.datagen;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.ModBlocks;
import com.grobe.techrebirth.util.MetalType;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
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
}
