package com.grobe.techrebirth.datagen;

import com.grobe.techrebirth.block.ModBlocks;
import com.grobe.techrebirth.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(pOutput, pRegistries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput){
        List<ItemLike> TIN_SMELTABLES = List.of(ModItems.RAW_TIN,
                ModBlocks.TIN_ORE, ModBlocks.TIN_DEEPSLATE_ORE);
        List<ItemLike> NICKEL_SMELTABLES = List.of(ModItems.RAW_NICKEL,
                ModBlocks.NICKEL_ORE, ModBlocks.NICKEL_DEEPSLATE_ORE);
        List<ItemLike> LEAD_SMELTABLES = List.of(ModItems.RAW_LEAD
                /*ModBlocks.NICKEL_ORE, ModBlocks.NICKEL_DEEPSLATE_ORE*/);
        List<ItemLike> POWDER_SMELTABLES = List.of(ModItems.COPPER_POWDER,
                ModItems.IRON_POWDER, ModItems.TIN_POWDER, ModItems.NICKEL_POWDER);


        buildGearRecipe(ModItems.COPPER_GEAR, Items.COPPER_INGOT,"has_copper", Items.COPPER_INGOT, recipeOutput);
        buildGearRecipe(ModItems.IRON_GEAR, Items.IRON_INGOT,"has_iron", Items.IRON_INGOT, recipeOutput);

        buildGearRecipe(ModItems.TIN_GEAR, ModItems.TIN_INGOT, "has_tin", ModItems.TIN_INGOT,recipeOutput);
        buildGearRecipe(ModItems.INVAR_GEAR, ModItems.INVAR_INGOT, "has_invar", ModItems.INVAR_INGOT,recipeOutput);

        buildBlocksFromIngotsRecipe(ModBlocks.INVAR_BLOCK.asItem(), ModItems.INVAR_INGOT.asItem(), "has_invar", ModItems.INVAR_INGOT.asItem(), recipeOutput);
        buildIngotsFromBlocksRecipe(ModItems.INVAR_INGOT.asItem(), 9, ModBlocks.INVAR_BLOCK, "has_invar", ModBlocks.INVAR_BLOCK, recipeOutput, "invar_ingot_from_invar_block");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.MACHINE_BASE.get())
                .pattern("YZY")
                .pattern("ZXZ")
                .pattern("YZY")
                .define('X', ModItems.TIN_GEAR.get())
                .define('Y', Items.IRON_INGOT.asItem())
                .define('Z', Items.GLASS.asItem())
                .unlockedBy("has_tin_gear", has(ModItems.TIN_GEAR)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ELECTRIC_FURNACE.get())
                .pattern(" P ")
                .pattern("WXW")
                .pattern("YZY").define('P', Items.PISTON.asItem())
                .define('W', Items.COBBLESTONE.asItem())
                .define('X', ModBlocks.MACHINE_BASE.get())
                .define('Y', ModItems.IRON_GEAR.get())
                .define('Z', Items.REDSTONE.asItem()).unlockedBy("has_machine_base", has(ModBlocks.MACHINE_BASE)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.REDSTONE_RECEPTION_COIL.get())
                .pattern("  Y")
                .pattern(" X ")
                .pattern("Y  ")
                .define('X', Items.GOLD_INGOT.asItem())
                .define('Y', Items.REDSTONE.asItem())
                .unlockedBy("has_gold", has(Items.GOLD_INGOT)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.GENERATOR.get())
                        .pattern("III").pattern("GMG").pattern("XFX").define('M', ModBlocks.MACHINE_BASE.get()).define('X', ModItems.REDSTONE_RECEPTION_COIL.get()).define('F', Items.FURNACE.asItem()).define('G', ModItems.IRON_GEAR.get()).define('I', ModItems.INVAR_INGOT.get())
                .unlockedBy("has_machine_base", has(ModBlocks.MACHINE_BASE)).save(recipeOutput);

        //upgrades
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.EFFICIENCY_UPGRADE.get())
                .pattern("IRI").pattern("RDR").pattern("IRI")
                .define('I', ModItems.INVAR_INGOT.asItem())
                .define('R', Items.REDSTONE.asItem()).define('D', Items.DIAMOND.asItem())
                .unlockedBy("has_invar", has(ModItems.INVAR_INGOT)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SPEED_UPGRADE.get())
                .pattern("IRI").pattern("RGR").pattern("IRI")
                .define('I', ModItems.INVAR_INGOT.asItem())
                .define('R', Items.REDSTONE.asItem()).define('G', Items.GOLD_INGOT)
                .unlockedBy("has_invar", has(ModItems.INVAR_INGOT)).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INVAR_INGOT, 3).requires(Items.IRON_INGOT.asItem(), 2).requires(ModItems.NICKEL_INGOT.asItem(), 1).unlockedBy("has_iron",has(Items.IRON_INGOT.asItem())).save(recipeOutput, "techrebirth:invar_ingot_from_nickel_and_iron_ingots");

        buildStonecuttingRecipe(Items.COPPER_INGOT.asItem(), RecipeCategory.MISC, ModItems.COPPER_POWDER, 1, "has_copper", Items.COPPER_INGOT.asItem(), recipeOutput, "copper_powder_from_ingot");
        buildStonecuttingRecipe(Items.IRON_INGOT.asItem(), RecipeCategory.MISC, ModItems.IRON_POWDER, 1, "has_iron", Items.IRON_INGOT.asItem(), recipeOutput, "iron_powder_from_ingot");
        buildStonecuttingRecipe(ModItems.TIN_INGOT.asItem(), RecipeCategory.MISC, ModItems.TIN_POWDER, 1, "has_tin", ModItems.TIN_INGOT.asItem(), recipeOutput, "tin_powder_from_ingot");
        buildStonecuttingRecipe(ModItems.NICKEL_INGOT.asItem(), RecipeCategory.MISC, ModItems.NICKEL_POWDER, 1, "has_nickel", ModItems.NICKEL_INGOT.asItem(), recipeOutput,"nickel_powder_from_ingot");

        buildStonecuttingRecipe(Blocks.COPPER_ORE.asItem(), RecipeCategory.MISC, ModItems.COPPER_POWDER,2, "has_copper_ore", Blocks.COPPER_ORE.asItem(), recipeOutput, "copper_powder_from_ore");
        buildStonecuttingRecipe(Blocks.DEEPSLATE_COPPER_ORE.asItem(), RecipeCategory.MISC, ModItems.COPPER_POWDER,3, "has_deepslate_copper_ore", Blocks.DEEPSLATE_COPPER_ORE.asItem(), recipeOutput, "copper_powder_from_deepslate");
        buildStonecuttingRecipe(Blocks.IRON_ORE.asItem(), RecipeCategory.MISC, ModItems.IRON_POWDER,2, "has_iron_ore", Blocks.IRON_ORE.asItem(), recipeOutput, "iron_powder_from_ore");
        buildStonecuttingRecipe(Blocks.DEEPSLATE_IRON_ORE.asItem(), RecipeCategory.MISC, ModItems.IRON_POWDER,3, "has_deepslate_iron_ore", Blocks.DEEPSLATE_IRON_ORE.asItem(), recipeOutput, "iron_powder_from_deepslate");

        buildStonecuttingRecipe(ModBlocks.TIN_ORE.asItem(), RecipeCategory.MISC, ModItems.TIN_POWDER, 2, "has_tin_ore", ModBlocks.TIN_ORE.asItem(), recipeOutput, "tin_powder_from_ore");
        buildStonecuttingRecipe(ModBlocks.TIN_ORE.asItem(), RecipeCategory.MISC, ModItems.TIN_POWDER, 3, "has_deepslate_tin_ore", ModBlocks.TIN_DEEPSLATE_ORE.asItem(), recipeOutput,"tin_powder_from_deepslate");
        buildStonecuttingRecipe(ModBlocks.NICKEL_ORE.asItem(), RecipeCategory.MISC, ModItems.NICKEL_POWDER, 2, "has_nickel_ore", ModBlocks.NICKEL_ORE.asItem(), recipeOutput, "nickel_powder_from_ore");
        buildStonecuttingRecipe(ModBlocks.NICKEL_DEEPSLATE_ORE.asItem(), RecipeCategory.MISC, ModItems.NICKEL_POWDER, 3, "has_deepslate_nickel_ore", ModBlocks.NICKEL_DEEPSLATE_ORE.asItem(), recipeOutput,"nickel_powder_from_deepslate");


        oreSmelting(recipeOutput, TIN_SMELTABLES, RecipeCategory.MISC, ModItems.TIN_INGOT, 8f, 70, "tin");
        oreBlasting(recipeOutput, TIN_SMELTABLES, RecipeCategory.MISC, ModItems.TIN_INGOT, 8f, 90, "tin");
        oreSmelting(recipeOutput, NICKEL_SMELTABLES, RecipeCategory.MISC, ModItems.NICKEL_INGOT, 10f, 35, "nickel");
        oreBlasting(recipeOutput, NICKEL_SMELTABLES, RecipeCategory.MISC, ModItems.NICKEL_INGOT, 10f, 45, "nickel");
        oreSmelting(recipeOutput, LEAD_SMELTABLES, RecipeCategory.MISC, ModItems.LEAD_INGOT, 9f, 40, "lead");
        oreBlasting(recipeOutput, LEAD_SMELTABLES, RecipeCategory.MISC, ModItems.LEAD_INGOT, 9f, 30, "lead");


        buildFoodCookingRecipe(Items.CARROT, ModItems.COOKED_CARROT, 2, 160, recipeOutput,"has_carrot", Items.CARROT);

        super.buildRecipes(recipeOutput);
    }

    public static void buildStonecuttingRecipe(ItemLike inputItem,  RecipeCategory recipeCategory, ItemLike outputItem, int Amount, String criteria, ItemLike Criteria, RecipeOutput recipeoutput, String resourceLocation){
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(inputItem.asItem()), recipeCategory, outputItem, Amount)
                .unlockedBy(criteria, has(Criteria))
                .save(recipeoutput, "techrebirth:" + resourceLocation);
    }
    public static void buildGearRecipe(ItemLike outputGear, ItemLike inputItem, String Criteria, ItemLike criteria, RecipeOutput recipeOutput){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, outputGear.asItem())
                .pattern(" X ")
                .pattern("X X")
                .pattern(" X ")
                .define('X', inputItem.asItem())
                .unlockedBy(Criteria, has(criteria)).save(recipeOutput);
    }
    public static void buildBlocksFromIngotsRecipe(ItemLike outputBlock, ItemLike inputItem, String Criteria, ItemLike criteria, RecipeOutput recipeOutput){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, outputBlock.asItem())
                .pattern("XXX")
                .pattern("XXX")
                .pattern("XXX")
                .define('X', inputItem.asItem())
                .unlockedBy(Criteria, has(criteria)).save(recipeOutput);
    }
    public static void buildIngotsFromBlocksRecipe(ItemLike outputIngots, int count, ItemLike inputBlock, String Criteria, ItemLike criteria, RecipeOutput recipeOutput, String resourceLocation){
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, outputIngots, count)
                .requires(inputBlock.asItem())
                .unlockedBy(Criteria, has(criteria))
                .save(recipeOutput, "techrebirth:"+ resourceLocation);
    }
    public static void buildFoodCookingRecipe(ItemLike inputFood, ItemLike OutputFood, float ExpirienceReward, int CookingTime, RecipeOutput recipeOutput,String Criteria, ItemLike criteria){
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(inputFood),
                RecipeCategory.FOOD,
                OutputFood,
                ExpirienceReward,
                CookingTime
        ).unlockedBy(Criteria, has(criteria)).save(recipeOutput);
    }
}
