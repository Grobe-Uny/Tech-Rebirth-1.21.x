package com.grobe.techrebirth.datagen;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.ModBlocks;
import com.grobe.techrebirth.item.ModItems;
import com.grobe.techrebirth.util.MetalType;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModItemModelProvider extends ItemModelProvider {
   private static LinkedHashMap<ResourceKey<TrimMaterial>, Float> trimMaterials = new LinkedHashMap<>();
   static{
       trimMaterials.put(TrimMaterials.QUARTZ, 0.1f);
       trimMaterials.put(TrimMaterials.IRON, 0.2f);
       trimMaterials.put(TrimMaterials.NETHERITE, 0.3f);
       trimMaterials.put(TrimMaterials.REDSTONE, 0.4f);
       trimMaterials.put(TrimMaterials.COPPER, 0.5f);
       trimMaterials.put(TrimMaterials.GOLD, 0.6f);
       trimMaterials.put(TrimMaterials.EMERALD, 0.7f);
       trimMaterials.put(TrimMaterials.DIAMOND, 0.8f);
       trimMaterials.put(TrimMaterials.LAPIS, 0.9f);
       trimMaterials.put(TrimMaterials.AMETHYST, 1.0f);

   }


    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TechRebirth.MODID, existingFileHelper);
    }


    @Override
    protected void registerModels() {

       trimmedArmorItem(ModItems.BLAZING_GOLD_HELMET);
       trimmedArmorItem(ModItems.BLAZING_GOLD_CHESTPLATE);
       trimmedArmorItem(ModItems.BLAZING_GOLD_LEGGINGS);
       trimmedArmorItem(ModItems.BLAZING_GOLD_BOOTS);
       trimmedArmorItem(ModItems.TIN_HELMET);
       trimmedArmorItem(ModItems.TIN_CHESTPLATE);
       trimmedArmorItem(ModItems.TIN_LEGGINGS);
       trimmedArmorItem(ModItems.TIN_BOOTS);

       handheldItem(ModItems.BLAZING_GOLD_SWORD);
       handheldItem(ModItems.BLAZING_GOLD_AXE);
       handheldItem(ModItems.BLAZING_GOLD_PICKAXE);
       handheldItem(ModItems.BLAZING_GOLD_SHOVEL);
       handheldItem(ModItems.BLAZING_GOLD_HOE);
       handheldItem(ModItems.TIN_PICKAXE);
       handheldItem(ModItems.TIN_AXE);
       handheldItem(ModItems.TIN_HOE);
       handheldItem(ModItems.TIN_SHOVEL);
       handheldItem(ModItems.TIN_SWORD);

       powderHandheldItem(ModItems.PURIFIED_IRON_POWDER);




       // Generate models for all nuggets using a single base texture
       for (Map.Entry<MetalType, DeferredItem<Item>> entry : ModItems.NUGGETS.entrySet()) {
           simpleItem(entry.getValue(), "base_nugget");
       }

       // Generate item models for all ore blocks
       for (Map.Entry<MetalType, DeferredBlock<Block>> entry : ModBlocks.ORE_BLOCKS.entrySet()) {
           MetalType metal = entry.getKey();
           if (metal == MetalType.DIAMOND) continue; // Skip Diamond Block item models

           DeferredBlock<Block> block = entry.getValue();
           // Parent the item model to the block model
           withExistingParent(block.getId().getPath(), modLoc("block/" + metal.getSerializedName() + "_block"));
       }

    }

    private ItemModelBuilder simpleItem(DeferredItem<Item> item, String textureName) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "item/" + textureName));
    }

    private void trimmedArmorItem(DeferredItem<? extends ArmorItem> itemDeferredItem){
       final String MODID = TechRebirth.MODID;

       if(itemDeferredItem.get() instanceof ArmorItem armorItem){
           trimMaterials.forEach((trimMaterials, value) ->{
               float trimValue = value;

               String armorType = switch (armorItem.getEquipmentSlot()){
                   case HEAD -> "helmet";
                   case CHEST -> "chestplate";
                   case LEGS -> "leggings";
                   case FEET -> "boots";
                   default -> "";
               };

               String armorItemPath = armorItem.toString();
               String trimPath = "trims/items/" + armorType + "_trim_" + trimMaterials.location().getPath();
               String currentTrimName = armorItemPath + "_" + trimMaterials.location().getPath()+"_trim";
               ResourceLocation armorItemResLoc = ResourceLocation.parse(armorItemPath);
               ResourceLocation trimResLoc = ResourceLocation.parse(trimPath);
               ResourceLocation trimNameResLoc = ResourceLocation.parse(currentTrimName);

               existingFileHelper.trackGenerated(trimResLoc, PackType.CLIENT_RESOURCES, ".png", "textures");

               // Automatically determine texture path based on item name
               String itemName = itemDeferredItem.getId().getPath();
               String materialName = itemName.replace("_helmet", "")
                       .replace("_chestplate", "")
                       .replace("_leggings", "")
                       .replace("_boots", "");

               String texturePath = "item/armor/" + materialName + "/" + itemName;

               getBuilder(currentTrimName)
                       .parent(new ModelFile.UncheckedModelFile("item/generated"))
                       .texture("layer0", ResourceLocation.fromNamespaceAndPath(MODID, texturePath))
                       .texture("layer1", trimResLoc);

               this.withExistingParent(itemDeferredItem.getId().getPath(),
                       mcLoc("item/generated"))
                       .override()
                       .model(new ModelFile.UncheckedModelFile(trimNameResLoc.getNamespace() + ":item/" + trimNameResLoc.getPath()))
                       .predicate(mcLoc("trim_type"), trimValue).end()
                       .texture("layer0", ResourceLocation.fromNamespaceAndPath(MODID, texturePath));
           });
       }
    }

    private ItemModelBuilder handheldItem(DeferredItem<?> item){
       return withExistingParent(item.getId().getPath(),
               ResourceLocation.parse("item/handheld")).texture("layer0",
               ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID,  "item/tools/" + item.getId().getPath()));
    }

    private ItemModelBuilder powderHandheldItem(DeferredItem<?> item){
       return withExistingParent(item.getId().getPath(),
               ResourceLocation.parse("item/generated")).texture("layer0",
               ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID,  "item/powder/" + item.getId().getPath()));
    }
}
