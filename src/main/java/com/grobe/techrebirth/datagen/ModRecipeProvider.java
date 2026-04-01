package com.grobe.techrebirth.datagen;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.ModBlocks;
import com.grobe.techrebirth.item.ModArmorItems;
import com.grobe.techrebirth.item.ModItems;
import com.grobe.techrebirth.item.ModToolItems;
import com.grobe.techrebirth.util.MetalType;
import com.grobe.techrebirth.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static com.grobe.techrebirth.recipe.CrushingRecipeBuilder.crushing;
import static com.grobe.techrebirth.recipe.GeneratorFuelRecipeBuilder.fuel;
import static com.grobe.techrebirth.recipe.AlloySmeltingRecipeBuilder.alloySmelting;
import static com.grobe.techrebirth.recipe.CentrifugeRecipeBuilder.centrifuging;
import static com.grobe.techrebirth.recipe.PurifierRecipeBuilder.purifying;
import static com.grobe.techrebirth.recipe.FluidInfuserRecipeBuilder.infusing;

import net.neoforged.neoforge.fluids.FluidStack;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(pOutput, pRegistries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput){

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.FLUID_INFUSER.get())
                .pattern(" G ")
                .pattern("PXP")
                .pattern("BRB")
                .define('P', ModItems.PURIFIED_IRON_POWDER)
                .define('X', ModBlocks.MACHINE_BASE)
                .define('B', Items.BUCKET)
                .define('R', ModItems.REDSTONE_RECEPTION_COIL)
                .define('G', ModItems.IRON_GEAR)
                .unlockedBy("has_machine_base", has(ModBlocks.MACHINE_BASE.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "machine/fluid_infuser"));
        // Helper to compute vanilla cooking time so Electric Furnace lands on target seconds
        // Electric Furnace maps vanilla cook time by MACHINE_SPEED_FACTOR (see ElectricFurnaceBlockEntity)
        final float EF_SPEED = 0.18f;
        java.util.function.Function<Float, Integer> secondsToVanillaTicks = secs -> Math.max(1, Math.round((secs * 20f) / EF_SPEED));

        // Smeltable groups
        List<ItemLike> TIN_SMELTABLES = List.of(ModItems.RAW_TIN,
                ModBlocks.TIN_ORE, ModBlocks.TIN_DEEPSLATE_ORE);
        List<ItemLike> NICKEL_SMELTABLES = List.of(ModItems.RAW_NICKEL,
                ModBlocks.NICKEL_ORE, ModBlocks.NICKEL_DEEPSLATE_ORE);
        List<ItemLike> LEAD_SMELTABLES = List.of(ModItems.RAW_LEAD,
                ModBlocks.LEAD_ORE, ModBlocks.LEAD_DEEPSLATE_ORE);

        // Generator fuel ingredient groups
        Ingredient lowLevelFuels = Ingredient.fromValues(Stream.of(
                new Ingredient.ItemValue(new ItemStack(Items.BAMBOO)),
                new Ingredient.ItemValue(new ItemStack(Blocks.SCAFFOLDING.asItem())),
                new Ingredient.TagValue(ItemTags.WOOL_CARPETS),
                new Ingredient.TagValue(ItemTags.WOOL)
        ));
        Ingredient coalFuels = Ingredient.fromValues(Stream.of(
                new Ingredient.ItemValue(new ItemStack(Items.COAL)),
                new Ingredient.ItemValue(new ItemStack(Items.CHARCOAL))
        ));

        //region gear recipes (use tag-based ingredients for cross-mod compatibility)
       buildGearRecipe(ModItems.COPPER_GEAR, Items.COPPER_INGOT, "has_copper", Items.COPPER_INGOT, recipeOutput,ResourceLocation.fromNamespaceAndPath("techrebirth", "gear/copper_gear"));
       buildGearRecipe(ModItems.IRON_GEAR, Items.IRON_INGOT, "has_iron", Items.IRON_INGOT, recipeOutput,ResourceLocation.fromNamespaceAndPath("techrebirth", "gear/iron_gear"));
       buildGearRecipeModed(ModItems.TIN_GEAR, ModTags.Items.INGOTS_TIN_D, "has_tin", ModTags.Items.INGOTS_TIN_D, recipeOutput,ResourceLocation.fromNamespaceAndPath("techrebirth", "gear/tin_gear"));
       buildGearRecipeModed(ModItems.LEAD_GEAR, ModTags.Items.INGOTS_LEAD_D, "has_lead", ModTags.Items.INGOTS_LEAD_D, recipeOutput,ResourceLocation.fromNamespaceAndPath("techrebirth", "gear/lead_gear"));
       buildGearRecipeModed(ModItems.STEEL_GEAR, ModTags.Items.INGOTS_STEEL_D, "has_steel", ModTags.Items.INGOTS_STEEL_D, recipeOutput,ResourceLocation.fromNamespaceAndPath("techrebirth", "gear/steel_gear"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.INVAR_GEAR)
                .pattern(" X ").pattern("X X").pattern(" X ")
                .define('X', ModTags.Items.INGOTS_INVAR)
                .unlockedBy("has_invar", has(ModTags.Items.INGOTS_INVAR))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "gear/invar_gear"));

        //endregion

        //region hardened machines

        buildHardenedMachines(ModBlocks.HARDENED_ELECTRIC_FURNACE.asItem(), ModBlocks.ELECTRIC_FURNACE.asItem(), recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "machine/hardened/hardened_electric_furnace"));
        buildHardenedMachines(ModBlocks.HARDENED_ALLOY_SMELTER.asItem(), ModBlocks.ALLOY_SMELTER.asItem(), recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "machine/hardened/hardened_alloy_smelter"));
        //endregion

        //region reinforced machines

        buildReinforcedMachines(ModBlocks.REINFORCED_ELECTRIC_FURNACE.asItem(), ModBlocks.HARDENED_ELECTRIC_FURNACE.asItem(), recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "machine/reinforced/reinforced_electric_furnace"));

        //endregion

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.FLUID_TANK.get())
                .pattern("XYX")
                .pattern("ZYZ")
                .pattern("XYX")
                .define('X', Items.IRON_INGOT).define('Y', Items.GLASS).define('Z', ModItems.STEEL_INGOT)
                .unlockedBy("has_steel_ingot", has(ModItems.STEEL_INGOT.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "machine/usable/fluid_tank"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ELECTRIC_CENTRIFUGE.get())
                .pattern("IGI")
                .pattern("YXY")
                .pattern("ZGZ")
                .define('I', Items.IRON_INGOT)
                .define('G', ModItems.LEAD_GEAR)
                .define('Z', ModItems.REDSTONE_RECEPTION_COIL)
                .define('Y', ModBlocks.FLUID_TANK)
                .define('X', ModBlocks.MACHINE_BASE)
                .unlockedBy("has_machine_base", has(ModBlocks.MACHINE_BASE.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "machine/electric_centrifuge"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ELECTRIC_PURIFIER.get())
                .pattern(" G ")
                .pattern("NXN")
                .pattern("BRB")
                .define('N', ModItems.NICKEL_INGOT)
                .define('X', ModBlocks.MACHINE_BASE)
                .define('B', Items.BUCKET)
                .define('R', ModItems.REDSTONE_RECEPTION_COIL)
                .define('G', ModItems.TIN_GEAR)
                .unlockedBy("has_machine_base", has(ModBlocks.MACHINE_BASE.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "machine/electric_purifier"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, new ItemStack (ModItems.ISOLATUM_BLEND.get(),16))
                .pattern("GSG")
                .pattern("SCS")
                .pattern("GSG")
                .define('G', Items.GRAVEL)
                .define('S', Items.SAND)
                .define('C', Items.COAL)
                .unlockedBy("has_coal", has(Items.COAL))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "cable/isolatum_blend"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ENERGY_BANK.get())
                .pattern("ILI")
                .pattern("LML")
                .pattern("ILI")
                .define('I', Items.IRON_INGOT)
                .define('L', ModItems.LITHIUM_INGOT)
                .define('M', ModBlocks.MACHINE_BASE)
                .unlockedBy("has_machine_base", has(ModBlocks.MACHINE_BASE.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "machine/energy_bank"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ENERGY_CABLE.get())
                .pattern("III").pattern("CCC").pattern("III")
                .define('I', ModItems.ISOLATUM_COMPOSITE).define('C', ModItems.CONDUCTIUM_INGOT.get())
                .unlockedBy("has_conductium_ingot", has(ModItems.CONDUCTIUM_INGOT.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "cable/energy_cable"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.MACHINE_BASE.get())
                .pattern("YZY")
                .pattern("ZXZ")
                .pattern("YZY")
                .define('X', ModItems.TIN_GEAR.get())
                .define('Y', Items.IRON_INGOT)
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
                .pattern("III").pattern("GMG").pattern("XFX")
                .define('M', ModBlocks.MACHINE_BASE.get())
                .define('X', ModItems.REDSTONE_RECEPTION_COIL.get())
                .define('F', Items.FURNACE.asItem())
                .define('G', ModItems.IRON_GEAR.get())
                .define('I', Items.IRON_INGOT.asItem())
                .unlockedBy("has_machine_base", has(ModBlocks.MACHINE_BASE)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ELECTRIC_CRUSHER.get())
                .pattern(" I ").pattern("GMG").pattern("XSX")
                .define('M', ModBlocks.MACHINE_BASE.get())
                .define('X', ModItems.REDSTONE_RECEPTION_COIL.get())
                .define('S', Items.STONECUTTER.asItem())
                .define('G', ModItems.IRON_GEAR.get())
                .define('I', ModTags.Items.INGOTS_INVAR_D.common())
                .unlockedBy("has_machine_base", has(ModBlocks.MACHINE_BASE)).save(recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "machine/electric_crusher"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ALLOY_SMELTER.get())
                .pattern("III").pattern("GMG").pattern("XFX")
                .define('M', ModBlocks.MACHINE_BASE.get())
                .define('X', Items.FURNACE.asItem())
                .define('F', ModItems.REDSTONE_RECEPTION_COIL.get())
                .define('G', ModItems.IRON_GEAR.get())
                .define('I', Items.IRON_INGOT.asItem())
                .unlockedBy("has_machine_base", has(ModBlocks.MACHINE_BASE)).save(recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "machine/alloy_smelter"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.LEAD_WRENCH.get())
                        .pattern("X X").pattern(" Y ").pattern(" X ")
                        .define('X', ModItems.LEAD_INGOT.get()).define('Y', Items.IRON_INGOT)
                        .unlockedBy("has_lead", has(ModItems.LEAD_INGOT)).save(recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "lead_wrench"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_GENERATOR.get())
                .pattern("GGG")
                .pattern("LCL")
                .pattern("RIR")
                .define('G',Items.GLASS_PANE)
                .define('L',Items.LAPIS_LAZULI)
                .define('C', ModItems.CONDUCTIUM_INGOT)
                .define('R',Items.REDSTONE)
                .define('I',Items.IRON_INGOT)
                .unlockedBy("has_conductium", has(ModItems.CONDUCTIUM_INGOT)).save(recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "solar_generator"));
        //region upgrades
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.EFFICIENCY_UPGRADE.get())
                .pattern("IRI").pattern("RDR").pattern("IRI")
                .define('I', ModTags.Items.INGOTS_INVAR)
                .define('R', Items.REDSTONE.asItem()).define('D', Items.DIAMOND.asItem())
                .unlockedBy("has_invar", has(ModTags.Items.INGOTS_INVAR)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SPEED_UPGRADE.get())
                .pattern("IRI").pattern("RGR").pattern("IRI")
                .define('I', ModTags.Items.INGOTS_INVAR)
                .define('R', Items.REDSTONE.asItem()).define('G', Items.GOLD_INGOT)
                .unlockedBy("has_invar", has(ModTags.Items.INGOTS_INVAR)).save(recipeOutput);
        //endregion

        //region custom mod recipes
        crushing(Ingredient.of(Items.IRON_INGOT), new ItemStack(ModItems.IRON_POWDER.get(), 1)).time(100)
                .unlockedBy("has_electrical_crusher", has(ModBlocks.ELECTRIC_CRUSHER)).save(recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "crushing/iron_powder_from_ingot"));
        buildCrushingRecipes(Ingredient.of(Items.RAW_IRON, Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE), new ItemStack(ModItems.IRON_POWDER.get(), 2), 100, "has_electrical_crusher", ModBlocks.ELECTRIC_CRUSHER, recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "crushing/iron_powder_from_raw_and_ores"));
        buildCrushingRecipes(Ingredient.of(Items.RAW_GOLD, Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE), new ItemStack(ModItems.GOLD_POWDER.get(), 2), 100, "has_electrical_crusher", ModBlocks.ELECTRIC_CRUSHER, recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "crushing/gold_powder_from_raw_and_ores"));

        buildCrushingRecipes(Ingredient.of(Items.COPPER_INGOT), new ItemStack(ModItems.COPPER_POWDER.get(), 1), 100, "has_electrical_crusher", ModBlocks.ELECTRIC_CRUSHER, recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "crushing/copper_powder_from_ingot"));
        buildCrushingRecipes(Ingredient.of(Items.GOLD_INGOT), new ItemStack(ModItems.GOLD_POWDER.get(), 1), 100, "has_electrical_crusher", ModBlocks.ELECTRIC_CRUSHER, recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "crushing/gold_powder_from_ingot"));

        buildCrushingRecipes(Ingredient.of(ModTags.Items.INGOTS_NICKEL), new ItemStack(ModItems.NICKEL_POWDER.get(), 1), 100, "has_electrical_crusher", ModBlocks.ELECTRIC_CRUSHER, recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "crushing/nickel_powder_from_ingot"));
        buildCrushingRecipes(Ingredient.of(ModItems.RAW_NICKEL, ModBlocks.NICKEL_ORE, ModBlocks.NICKEL_DEEPSLATE_ORE), new ItemStack(ModItems.NICKEL_POWDER.get(), 2), 100, "has_electrical_crusher", ModBlocks.ELECTRIC_CRUSHER, recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "crushing/nickel_powder_from_raw_and_ores"));

        buildCrushingRecipes(Ingredient.of(ModTags.Items.INGOTS_TIN), new ItemStack(ModItems.TIN_POWDER.get(), 1), 100, "has_electrical_crusher", ModBlocks.ELECTRIC_CRUSHER, recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "crushing/tin_powder_from_ingot"));
        buildCrushingRecipes(Ingredient.of(ModItems.RAW_TIN, ModBlocks.TIN_ORE, ModBlocks.TIN_DEEPSLATE_ORE), new ItemStack(ModItems.TIN_POWDER.get(), 2), 100, "has_electrical_crusher", ModBlocks.ELECTRIC_CRUSHER, recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "crushing/tin_powder_from_raw_and_ores"));
        // new recipes using custom mod recipes
        buildCrushingRecipesWithChances(Ingredient.of(Blocks.COBBLESTONE), new ItemStack(Blocks.GRAVEL.asItem(), 1), 80,new ItemStack(Blocks.SAND), 0.05f, "has_electrical_crusher", ModBlocks.ELECTRIC_CRUSHER, recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "crushing/gravel_from_cobblestone"));
        buildCrushingRecipesWithChances(Ingredient.of(Blocks.GRAVEL), new ItemStack(Blocks.SAND.asItem(), 1), 70,new ItemStack(Blocks.SAND), 0.08f, "has_electrical_crusher", ModBlocks.ELECTRIC_CRUSHER, recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "crushing/sand_from_gravel"));
        buildCrushingRecipesWithChances(Ingredient.of(Blocks.ANCIENT_DEBRIS), new ItemStack(Items.NETHERITE_SCRAP.asItem(), 2), 200, new ItemStack(Items.NETHERITE_SCRAP), 0.01f,"has_electrical_crusher",ModBlocks.ELECTRIC_CRUSHER, recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "crushing/netherite_scraps_from_debris"));
        buildCrushingRecipesWithChances(Ingredient.of(Items.RAW_COPPER, Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE), new ItemStack(ModItems.COPPER_POWDER.get(), 2), 200, new ItemStack(ModItems.GOLD_POWDER.get()), 0.1f,"has_electrical_crusher",ModBlocks.ELECTRIC_CRUSHER, recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "crushing/copper_powder_from_raw_and_ores"));
        buildCrushingRecipes(Ingredient.of(Items.DIAMOND), new ItemStack(ModItems.DIAMOND_POWDER.get()), 150, "has_electrical_crusher", ModBlocks.ELECTRIC_CRUSHER, recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "crushing/diamond_powder_from_diamond"));


        buildCrushingRecipes(Ingredient.of(Items.OBSIDIAN), new ItemStack(ModItems.OBSIDIAN_POWDER.get(), 2), 400, "has_electrical_crusher", ModBlocks.ELECTRIC_CRUSHER, recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "crushing/obsidian_powder_from_obsidian"));
        buildCrushingRecipes(Ingredient.of(Items.CRYING_OBSIDIAN), new ItemStack(ModItems.OBSIDIAN_POWDER.get(), 4), 520, "has_electrical_crusher", ModBlocks.ELECTRIC_CRUSHER, recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "crushing/obsidian_powder_from_crying_obsidian"));
        //endregion

        //region generator fuels
        addGeneratorFuels(lowLevelFuels, 80, 2, recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "generator_fuel/low_level_fuels"));
        addGeneratorFuels(coalFuels, 1600, 20, recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "generator_fuel/coal_fuels"));
        addGeneratorFuels(Ingredient.of(new ItemStack(Items.LAVA_BUCKET)), 5000, 25, recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "generator_fuel/lava_bucket"));
        //endregion

        //region alloy recipes
        var invarReq = createIngredients(Items.IRON_INGOT, Items.IRON_INGOT, ModItems.NICKEL_INGOT.asItem());
        buildAlloyRecipesD(invarReq, new ItemStack(ModItems.INVAR_INGOT.get(), 3), 100, "has_nickel", ModTags.Items.INGOTS_NICKEL_D, recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "alloys/invar_alloy"));
        var steelReq = createIngredients(ModItems.IRON_POWDER, Items.COAL);
        buildAlloyRecipes(steelReq, new ItemStack(ModItems.STEEL_INGOT.get()), 200, "has_iron_powder", ModItems.IRON_POWDER.get(), recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "alloys/steel_alloy"));
        var baseGoldiumReq = createMixedIngredients(Items.GOLD_INGOT,Items.GOLD_INGOT, Items.GLOWSTONE_DUST);
        buildAlloyRecipes(baseGoldiumReq, new ItemStack(ModItems.BASE_GOLDIUM_INGOT.get(), 2),200, "has_gold", ModItems.BASE_GOLDIUM_INGOT.get(),recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "alloys/base_goldium_alloy"));
        var baseObsidianReq = createIngredients(ModItems.OBSIDIAN_POWDER, Items.IRON_INGOT);
        buildAlloyRecipes(baseObsidianReq, new ItemStack(ModItems.BASE_OBSIDIAN_INGOT.get(), 1), 340, "has_obsidian_powder", ModItems.OBSIDIAN_POWDER.get(), recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "alloys/base_obsidian_alloy"));
        var conductiumReq = createIngredients(Items.IRON_INGOT, Items.REDSTONE);
        buildAlloyRecipes(conductiumReq, new ItemStack(ModItems.CONDUCTIUM_INGOT.get(), 1), 100, "has_conductium", ModItems.CONDUCTIUM_INGOT.get(), recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "alloys/conductium_alloy"));
        //endregion

        //region centrifuge recipes
        centrifuging(Ingredient.of(Items.GOLD_INGOT), Ingredient.of(Items.BLAZE_POWDER), 20, new ItemStack(ModItems.BLAZING_GOLD_INGOT.get()), 300)
                .unlockedBy("has_electric_centrifuge", has(ModBlocks.ELECTRIC_CENTRIFUGE.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "centrifuging/blazing_gold_alternate"));
        centrifuging(Ingredient.of(ModItems.BASE_GOLDIUM_INGOT), Ingredient.of(Items.BLAZE_POWDER), 10, new ItemStack(ModItems.BLAZING_GOLD_INGOT.get()), 300)
                .unlockedBy("has_electric_centrifuge", has(ModBlocks.ELECTRIC_CENTRIFUGE.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "centrifuging/blazing_gold"));

        centrifuging(Ingredient.of(ModItems.BASE_OBSIDIAN_INGOT), Ingredient.of(Items.DIAMOND), 40, new ItemStack(ModItems.REFINED_OBSIDIAN_INGOT.get()), 200)
                .unlockedBy("has_electric_centrifuge", has(ModBlocks.ELECTRIC_CENTRIFUGE.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "centrifuging/refined_obsidian"));        
        //endregion


        //region fluid infuser recipes
        infusing(Ingredient.of(Items.IRON_INGOT), new FluidStack(net.minecraft.world.level.material.Fluids.WATER, 1000), new ItemStack(ModItems.STEEL_INGOT.get()))
                .unlockedBy("has_fluid_infuser", has(ModBlocks.FLUID_INFUSER.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "fluid_infusing/steel_from_iron_and_water"));
        //endregion

        //region purifying recipes
        purifying(Ingredient.of(Items.RAW_IRON), new ItemStack(ModItems.PURIFIED_IRON_POWDER.get(), 4)).unlockedBy("has_electric_purifier", has(ModBlocks.ELECTRIC_PURIFIER.get())).save(recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "purifying/purified_iron_from_raw"));
        purifying(Ingredient.of(Items.RAW_COPPER), new ItemStack(ModItems.PURIFIED_COPPER_POWDER.get(), 4)).unlockedBy("has_electric_purifier", has(ModBlocks.ELECTRIC_PURIFIER.get())).save(recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "purifying/purified_copper_from_raw"));
        purifying(Ingredient.of(Items.RAW_GOLD), new ItemStack(ModItems.PURIFIED_GOLD_POWDER.get(), 4)).unlockedBy("has_electric_purifier", has(ModBlocks.ELECTRIC_PURIFIER.get())).save(recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "purifying/purified_gold_from_raw"));
        purifying(Ingredient.of(ModItems.RAW_TIN), new ItemStack(ModItems.PURIFIED_TIN_POWDER.get(), 3)).unlockedBy("has_electric_purifier", has(ModBlocks.ELECTRIC_PURIFIER.get())).save(recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "purifying/purified_tin_from_raw"));
        purifying(Ingredient.of(ModItems.RAW_NICKEL), new ItemStack(ModItems.PURIFIED_NICKEL_POWDER.get(), 3)).unlockedBy("has_electric_purifier", has(ModBlocks.ELECTRIC_PURIFIER.get())).save(recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "purifying/purified_nickel_from_raw"));
        //endregion

        // Smelting and blasting
        // Target Electric Furnace durations: ~3.0s smelting, ~2.0s blasting (base, no upgrades)
        int ORE_SMELT = secondsToVanillaTicks.apply(3.0f);   // ≈ 333 ticks -> ~3.0s in machine
        int ORE_BLAST = secondsToVanillaTicks.apply(2.0f);   // ≈ 222 ticks -> ~2.0s in machine

        oreSmelting(recipeOutput, TIN_SMELTABLES, RecipeCategory.MISC, ModItems.TIN_INGOT, 8f, ORE_SMELT, "tin");
        oreBlasting(recipeOutput, TIN_SMELTABLES, RecipeCategory.MISC, ModItems.TIN_INGOT, 8f, ORE_BLAST, "tin");
        oreSmelting(recipeOutput, NICKEL_SMELTABLES, RecipeCategory.MISC, ModItems.NICKEL_INGOT, 10f, ORE_SMELT, "nickel");
        oreBlasting(recipeOutput, NICKEL_SMELTABLES, RecipeCategory.MISC, ModItems.NICKEL_INGOT, 10f, ORE_BLAST, "nickel");
        oreSmelting(recipeOutput, LEAD_SMELTABLES, RecipeCategory.MISC, ModItems.LEAD_INGOT, 9f, ORE_SMELT, "lead");
        oreBlasting(recipeOutput, LEAD_SMELTABLES, RecipeCategory.MISC, ModItems.LEAD_INGOT, 9f, ORE_BLAST, "lead");


        simpleCookingRecipe(recipeOutput, "smelting", RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, 100, ModItems.PURIFIED_IRON_POWDER, Items.IRON_INGOT, 0.7f);
        simpleCookingRecipe(recipeOutput, "smelting", RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, 100, ModItems.PURIFIED_COPPER_POWDER, Items.COPPER_INGOT, 0.7f);
        simpleCookingRecipe(recipeOutput, "smelting", RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, 100, ModItems.PURIFIED_GOLD_POWDER, Items.GOLD_INGOT, 0.7f);
        simpleCookingRecipe(recipeOutput, "smelting", RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, 100, ModItems.PURIFIED_TIN_POWDER, ModItems.TIN_INGOT, 0.7f);
        simpleCookingRecipe(recipeOutput, "smelting", RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, 100, ModItems.PURIFIED_NICKEL_POWDER, ModItems.NICKEL_INGOT, 0.7f);
        simpleCookingRecipe(recipeOutput, "smelting", RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, 120, ModItems.LITHIUM_POWDER, ModItems.LITHIUM_INGOT, 0.6f);
        simpleCookingRecipe(recipeOutput, "smelting", RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, 50, ModItems.ISOLATUM_BLEND, ModItems.ISOLATUM_COMPOSITE, 0.3f);

        // Example food: keep near ~1.4–1.8s in machine; 160 vanilla -> 28.8 ticks (~1.44s)
        buildFoodCookingRecipe(Items.CARROT, ModItems.COOKED_CARROT, 2, 160, recipeOutput, "has_carrot", Items.CARROT);

        // armor and tool sets
        buildArmorSetRecipes(ModItems.BLAZING_GOLD_INGOT.asItem(), ModArmorItems.BLAZING_GOLD_HELMET.asItem(), ModArmorItems.BLAZING_GOLD_CHESTPLATE.asItem(), ModArmorItems.BLAZING_GOLD_LEGGINGS.asItem(),ModArmorItems.BLAZING_GOLD_BOOTS.asItem(),"has_blazing_gold", recipeOutput);
        buildToolRecipes(ModItems.BLAZING_GOLD_INGOT.asItem(), Items.STICK, ModToolItems.BLAZING_GOLD_SWORD.asItem(), ModToolItems.BLAZING_GOLD_AXE.asItem(), ModToolItems.BLAZING_GOLD_PICKAXE.asItem(), ModToolItems.BLAZING_GOLD_SHOVEL.asItem(), ModToolItems.BLAZING_GOLD_HOE.asItem(), "has_blazing_gold", recipeOutput);


        buildToolRecipes(ModItems.TIN_INGOT.asItem(), Items.STICK, ModToolItems.TIN_SWORD.asItem(), ModToolItems.TIN_AXE.asItem(), ModToolItems.TIN_PICKAXE.asItem(), ModToolItems.TIN_SHOVEL.asItem(), ModToolItems.TIN_HOE.asItem(), "has_tin", recipeOutput);
        buildArmorSetRecipes(Ingredient.of(ModTags.Items.INGOTS_TIN_D.common()), ModArmorItems.TIN_HELMET.asItem(), ModArmorItems.TIN_CHESTPLATE.asItem(), ModArmorItems.TIN_LEGGINGS.asItem(),ModArmorItems.TIN_BOOTS.asItem(),"has_tin", recipeOutput);

        
        // Automatic Nugget Recipes
        for (Map.Entry<MetalType, DeferredItem<Item>> entry : ModItems.NUGGETS.entrySet()) {
            MetalType type = entry.getKey();
            Item nugget = entry.getValue().get();
            ItemLike ingot = getIngotForMetal(type);

            if (ingot != null) {
                // Ingot -> 9 Nuggets
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, nugget, 9)
                        .requires(ingot)
                        .unlockedBy("has_" + type.getSerializedName() + "_ingot", has(ingot))
                        .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, type.getSerializedName() + "_nugget_from_ingot"));

                // 9 Nuggets -> Ingot
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ingot)
                        .pattern("XXX")
                        .pattern("XXX")
                        .pattern("XXX")
                        .define('X', nugget)
                        .unlockedBy("has_" + type.getSerializedName() + "_nugget", has(nugget))
                        .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, type.getSerializedName() + "_ingot_from_nugget"));
            }
        }

        // Automatic Block Recipes
        for (Map.Entry<MetalType, DeferredBlock<Block>> entry : ModBlocks.ORE_BLOCKS.entrySet()) {
            MetalType type = entry.getKey();
            if (type == MetalType.DIAMOND) continue; // Skip Diamond Block recipes

            Block block = entry.getValue().get();
            ItemLike ingot = getIngotForMetal(type);

            if (ingot != null) {
                // 9 Ingots -> 1 Block
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, block)
                        .pattern("XXX")
                        .pattern("XXX")
                        .pattern("XXX")
                        .define('X', ingot)
                        .unlockedBy("has_" + type.getSerializedName() + "_ingot", has(ingot))
                        .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, type.getSerializedName() + "_block_from_ingot"));

                // 1 Block -> 9 Ingots
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ingot, 9)
                        .requires(block)
                        .unlockedBy("has_" + type.getSerializedName() + "_block", has(block))
                        .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, type.getSerializedName() + "_ingot_from_block"));
            }
        }

        super.buildRecipes(recipeOutput);
    }

    private ItemLike getIngotForMetal(MetalType type) {
        return switch (type) {
            case TIN -> ModItems.TIN_INGOT.get();
            case NICKEL -> ModItems.NICKEL_INGOT.get();
            case INVAR -> ModItems.INVAR_INGOT.get();
            case LEAD -> ModItems.LEAD_INGOT.get();
            case STEEL -> ModItems.STEEL_INGOT.get();
            case DIAMOND -> Items.DIAMOND;
            case BLAZING_GOLD -> ModItems.BLAZING_GOLD_INGOT.get();
            default -> null;
        };
    }

    public static void buildGearRecipe(ItemLike outputGear, ItemLike inputItem, String Criteria, ItemLike criteria, RecipeOutput recipeOutput, ResourceLocation resourceLocation){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, outputGear.asItem())
                .pattern(" X ")
                .pattern("X X")
                .pattern(" X ")
                .define('X', inputItem)
                .unlockedBy(Criteria, has(criteria)).save(recipeOutput, resourceLocation);
    }
    
    public static Ingredient any(ModTags.Items.Dual dual) {
        // Accept either neoforge or common (c) namespace in the same ingredient
        return Ingredient.fromValues(
                java.util.stream.Stream.of(
                        new Ingredient.TagValue(dual.neoforge()),
                        new Ingredient.TagValue(dual.common())
                )
        );
    }
    
    public static void buildGearRecipeModed(ItemLike outputGear, ModTags.Items.Dual inputItem, String Criteria, ModTags.Items.Dual criteria, RecipeOutput recipeOutput, ResourceLocation resourceLocation){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, outputGear.asItem())
                .pattern(" X ")
                .pattern("X X")
                .pattern(" X ")
                .define('X', any(inputItem))
                .unlockedBy(Criteria, has(criteria.neoforge())).save(recipeOutput, resourceLocation);
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
    public static void buildCrushingRecipesWithChances(Ingredient ingredient, ItemStack output, int length, ItemStack chanceOutput, float chanceRate, String Criteria, ItemLike criteria, RecipeOutput rOutput, ResourceLocation resourceLocation){
        crushing(ingredient, output).time(length).chanceOutput(chanceOutput, chanceRate).unlockedBy(Criteria, has(criteria)).save(rOutput,  resourceLocation);
    }
    public static void buildCrushingRecipes(Ingredient ingredient, ItemStack output, int length, String Criteria, ItemLike criteria, RecipeOutput rOutput, ResourceLocation resourceLocation){
        crushing(ingredient, output).time(length).unlockedBy(Criteria, has(criteria)).save(rOutput,  resourceLocation);
    }
    public static void addGeneratorFuels(Ingredient ingredient, int burnTime, int powerPerTick, RecipeOutput recipeOutput, ResourceLocation resourceLocation){
        fuel(ingredient, burnTime, powerPerTick).unlockedBy("has_generator", has(ModBlocks.GENERATOR.get())).save(recipeOutput, resourceLocation);
    }
    public static void buildAlloyRecipesD(NonNullList<Ingredient> ingredients, ItemStack output, int ticks, String Criteria, ModTags.Items.Dual criteria, RecipeOutput recipeOutput, ResourceLocation resourceLocation){
        alloySmelting(ingredients, output).time(ticks).unlockedBy(Criteria, has(criteria.neoforge())).save(recipeOutput, resourceLocation);
    }
    public static void buildAlloyRecipes(NonNullList<Ingredient> ingredients, ItemStack output, int ticks, String Criteria, Item criteria, RecipeOutput recipeOutput, ResourceLocation resourceLocation){
        alloySmelting(ingredients, output).time(ticks).unlockedBy(Criteria, has(criteria)).save(recipeOutput, resourceLocation);
    }
    public static void buildHardenedMachines(ItemLike hardenedMachine, ItemLike baseMachine, RecipeOutput recipeOutput, ResourceLocation resourceLocation){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, hardenedMachine)
                .pattern("IGI")
                .pattern(" B ")
                .pattern("I I")
                .define('I', ModTags.Items.INGOTS_INVAR_D.common())
                .define('G', ModItems.INVAR_GEAR)
                .define('B', baseMachine.asItem())
                .unlockedBy("has_invar_gear", has(hardenedMachine))
                .save(recipeOutput, resourceLocation);
    }
    public static void buildReinforcedMachines(ItemLike reinforcedMachine, ItemLike hardenedMachine, RecipeOutput recipeOutput, ResourceLocation resourceLocation){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, reinforcedMachine)
                .pattern("IGI")
                .pattern(" B ")
                .pattern("I I")
                .define('I', ModTags.Items.INGOTS_STEEL_D.common())
                .define('G', ModItems.STEEL_GEAR)
                .define('B', hardenedMachine.asItem())
                .unlockedBy("has_steel_gear", has(hardenedMachine))
                .save(recipeOutput, resourceLocation);
    }
    public static void buildArmorSetRecipes(ItemLike material, ItemLike helmet, ItemLike chestplate, ItemLike leggings, ItemLike boots, String unlockCriterion, RecipeOutput recipeOutput) {
        buildArmorSetRecipes(Ingredient.of(material), helmet, chestplate, leggings, boots, unlockCriterion, recipeOutput);
    }

    public static void buildArmorSetRecipes(Ingredient material, ItemLike helmet, ItemLike chestplate, ItemLike leggings, ItemLike boots, String unlockCriterion, RecipeOutput recipeOutput) {
        // Helmet
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, helmet)
                .pattern("XXX")
                .pattern("X X")
                .define('X', material)
                .unlockedBy(unlockCriterion, has(helmet)) // Note: 'has' check usually needs an ItemLike, but we can't easily get one from Ingredient. Using helmet as a fallback or we need to pass a criterion item.
                .save(recipeOutput);

        // Chestplate
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, chestplate)
                .pattern("X X")
                .pattern("XXX")
                .pattern("XXX")
                .define('X', material)
                .unlockedBy(unlockCriterion, has(chestplate))
                .save(recipeOutput);

        // Leggings
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, leggings)
                .pattern("XXX")
                .pattern("X X")
                .pattern("X X")
                .define('X', material)
                .unlockedBy(unlockCriterion, has(leggings))
                .save(recipeOutput);

        // Boots
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, boots)
                .pattern("X X")
                .pattern("X X")
                .define('X', material)
                .unlockedBy(unlockCriterion, has(boots))
                .save(recipeOutput);
    }

    public static void buildToolRecipes(ItemLike material, ItemLike material2, ItemLike sword, ItemLike axe, ItemLike pickaxe, ItemLike shovel, ItemLike hoe, String unlockCriterion, RecipeOutput recipeOutput){
        //Sword
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, sword)
                .pattern(" X ")
                .pattern(" X ")
                .pattern(" Y ")
                .define('X', material).define('Y',material2)
                .unlockedBy(unlockCriterion, has(material))
                .save(recipeOutput);

        //Axe
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, axe)
                .pattern("XX ")
                .pattern("XY ")
                .pattern(" Y ")
                .define('X', material).define('Y',material2)
                .unlockedBy(unlockCriterion, has(material))
                .save(recipeOutput);

        //Pickaxe
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, pickaxe)
                .pattern("XXX")
                .pattern(" Y ")
                .pattern(" Y ")
                .define('X', material).define('Y',material2)
                .unlockedBy(unlockCriterion, has(material))
                .save(recipeOutput);

        //Shovel
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, shovel)
                .pattern(" X ")
                .pattern(" Y ")
                .pattern(" Y ")
                .define('X', material).define('Y',material2)
                .unlockedBy(unlockCriterion, has(material))
                .save(recipeOutput);

        //Hoe
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, hoe)
                .pattern("XX ")
                .pattern(" Y ")
                .pattern(" Y ")
                .define('X', material).define('Y',material2)
                .unlockedBy(unlockCriterion, has(material))
                .save(recipeOutput);

    }
    private static NonNullList<Ingredient> createIngredients(ItemLike... items){
        NonNullList<Ingredient>ingredients = NonNullList.create();
        for(ItemLike item : items)
            ingredients.add(Ingredient.of(item));

        return ingredients;
    }
    private static NonNullList<Ingredient> createMixedIngredients(Object... inputs) {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        for (Object input : inputs) {
            if (input instanceof ModTags.Items.Dual dual) {
                ingredients.add(any(dual));
            } else if (input instanceof ItemLike item) {
                ingredients.add(Ingredient.of(item));
            } else if (input instanceof Ingredient ingredient) {
                ingredients.add(ingredient);
            }
        }
        return ingredients;
    }
}
