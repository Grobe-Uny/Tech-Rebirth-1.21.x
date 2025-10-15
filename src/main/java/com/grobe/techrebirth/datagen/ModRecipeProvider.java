package com.grobe.techrebirth.datagen;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.ModBlocks;
import com.grobe.techrebirth.item.ModItems;
import com.grobe.techrebirth.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static com.grobe.techrebirth.recipe.CrushingRecipeBuilder.crushing;
import static com.grobe.techrebirth.recipe.GeneratorFuelRecipeBuilder.fuel;
import static com.grobe.techrebirth.recipe.AlloySmeltingRecipeBuilder.alloySmelting;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(pOutput, pRegistries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput){
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
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.INVAR_GEAR)
                .pattern(" X ").pattern("X X").pattern(" X ")
                .define('X', ModTags.Items.INGOTS_INVAR)
                .unlockedBy("has_invar", has(ModTags.Items.INGOTS_INVAR))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "gear/invar_gear"));

        //endregion

        buildBlocksFromIngotsRecipe(ModBlocks.INVAR_BLOCK.asItem(), ModItems.INVAR_INGOT.asItem(), "has_invar", ModItems.INVAR_INGOT.asItem(), recipeOutput);
        buildIngotsFromBlocksRecipe(ModItems.INVAR_INGOT.asItem(), 9, ModBlocks.INVAR_BLOCK, "has_invar", ModBlocks.INVAR_BLOCK, recipeOutput, "invar_ingot_from_invar_block");

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
                .define('I', ModTags.Items.INGOTS_INVAR)
                .unlockedBy("has_machine_base", has(ModBlocks.MACHINE_BASE)).save(recipeOutput);

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

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INVAR_INGOT, 3)
                .requires(Ingredient.of(ModTags.Items.INGOTS_IRON), 2)
                .requires(Ingredient.of(ModTags.Items.INGOTS_NICKEL))
                .unlockedBy("has_iron", has(ModTags.Items.INGOTS_IRON))
                .save(recipeOutput, "techrebirth:invar_ingot_from_nickel_and_iron_ingots");

        //region custom mod recipes
        crushing(Ingredient.of(ModTags.Items.INGOTS_IRON), new ItemStack(ModItems.IRON_POWDER.get(), 1)).time(100)
                .unlockedBy("has_electrical_crusher", has(ModBlocks.ELECTRIC_CRUSHER)).save(recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "crushing/iron_powder_from_ingot"));
        buildCrushingRecipes(Ingredient.of(Items.RAW_IRON, Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE), new ItemStack(ModItems.IRON_POWDER.get(), 2), 100, "has_electrical_crusher", ModBlocks.ELECTRIC_CRUSHER, recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "crushing/iron_powder_from_raw_and_ores"));

        buildCrushingRecipes(Ingredient.of(Items.RAW_COPPER, Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE), new ItemStack(ModItems.COPPER_POWDER.get(), 2), 100, "has_electrical_crusher", ModBlocks.ELECTRIC_CRUSHER, recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "crushing/copper_powder_from_raw_and_ores"));
        buildCrushingRecipes(Ingredient.of(ModTags.Items.INGOTS_COPPER), new ItemStack(ModItems.COPPER_POWDER.get(), 1), 100, "has_electrical_crusher", ModBlocks.ELECTRIC_CRUSHER, recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "crushing/copper_powder_from_ingot"));

        buildCrushingRecipes(Ingredient.of(ModTags.Items.INGOTS_NICKEL), new ItemStack(ModItems.NICKEL_POWDER.get(), 1), 100, "has_electrical_crusher", ModBlocks.ELECTRIC_CRUSHER, recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "crushing/nickel_powder_from_ingot"));
        buildCrushingRecipes(Ingredient.of(ModItems.RAW_NICKEL, ModBlocks.NICKEL_ORE, ModBlocks.NICKEL_DEEPSLATE_ORE), new ItemStack(ModItems.NICKEL_POWDER.get(), 2), 100, "has_electrical_crusher", ModBlocks.ELECTRIC_CRUSHER, recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "crushing/nickel_powder_from_raw_and_ores"));

        buildCrushingRecipes(Ingredient.of(ModTags.Items.INGOTS_TIN), new ItemStack(ModItems.TIN_POWDER.get(), 1), 100, "has_electrical_crusher", ModBlocks.ELECTRIC_CRUSHER, recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "crushing/tin_powder_from_ingot"));
        buildCrushingRecipes(Ingredient.of(ModItems.RAW_TIN, ModBlocks.TIN_ORE, ModBlocks.TIN_DEEPSLATE_ORE), new ItemStack(ModItems.TIN_POWDER.get(), 2), 100, "has_electrical_crusher", ModBlocks.ELECTRIC_CRUSHER, recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "crushing/tin_powder_from_raw_and_ores"));
        // new recipes using custom mod recipes
        buildCrushingRecipes(Ingredient.of(Blocks.COBBLESTONE), new ItemStack(Blocks.GRAVEL.asItem(), 1), 80, "has_electrical_crusher", ModBlocks.ELECTRIC_CRUSHER, recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "crushing/gravel_from_cobblestone"));
        buildCrushingRecipes(Ingredient.of(Blocks.GRAVEL), new ItemStack(Blocks.SAND.asItem(), 1), 70, "has_electrical_crusher", ModBlocks.ELECTRIC_CRUSHER, recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "crushing/sand_from_gravel"));
        //endregion

        //region generator fuels
        addGeneratorFuels(lowLevelFuels, 80, 20, recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "generator_fuel/low_level_fuels"));
        addGeneratorFuels(coalFuels, 1600, 120, recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "generator_fuel/coal_fuels"));
        addGeneratorFuels(Ingredient.of(new ItemStack(Items.LAVA_BUCKET)), 25000, 400, recipeOutput, ResourceLocation.fromNamespaceAndPath("techrebirth", "generator_fuel/lava_bucket"));
        //endregion

        //region alloy recipes
        var invarReq = createIngredients(Items.IRON_INGOT, Items.IRON_INGOT, ModItems.NICKEL_INGOT.asItem());
        buildAlloyRecipes(invarReq, new ItemStack(ModItems.INVAR_INGOT.get(), 3), 100, "has_nickel", ModTags.Items.INGOTS_NICKEL_D, recipeOutput, ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "alloys/invar_alloy"));

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

        // Example food: keep near ~1.4–1.8s in machine; 160 vanilla -> 28.8 ticks (~1.44s)
        buildFoodCookingRecipe(Items.CARROT, ModItems.COOKED_CARROT, 2, 160, recipeOutput, "has_carrot", Items.CARROT);

        super.buildRecipes(recipeOutput);
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
    public static void buildCrushingRecipes(Ingredient ingredient, ItemStack output, int length, String Criteria, ItemLike criteria, RecipeOutput rOutput, ResourceLocation resourceLocation){
        crushing(ingredient, output).time(length).unlockedBy(Criteria, has(criteria)).save(rOutput,  resourceLocation);
    }
    public static void addGeneratorFuels(Ingredient ingredient, int burnTime, int powerPerTick, RecipeOutput recipeOutput, ResourceLocation resourceLocation){
        fuel(ingredient, burnTime, powerPerTick).unlockedBy("has_generator", has(ModBlocks.GENERATOR.get())).save(recipeOutput, resourceLocation);
    }
    public static void buildAlloyRecipes(NonNullList<Ingredient> ingredients, ItemStack output, int ticks, String Criteria, ModTags.Items.Dual criteria, RecipeOutput recipeOutput, ResourceLocation resourceLocation){
        alloySmelting(ingredients, output).time(ticks).unlockedBy(Criteria, has(criteria.neoforge())).save(recipeOutput, resourceLocation);
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
