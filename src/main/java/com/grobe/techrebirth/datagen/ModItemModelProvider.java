package com.grobe.techrebirth.datagen;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.LinkedHashMap;

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

    }

    private void trimmedArmorItem(DeferredItem<ArmorItem> itemDeferredItem){
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

               getBuilder(currentTrimName)
                       .parent(new ModelFile.UncheckedModelFile("item/generated"))
                       .texture("layer0", armorItemResLoc.getNamespace()+ ":item/" + armorItemResLoc.getPath())
                       .texture("layer1", trimResLoc);

               this.withExistingParent(itemDeferredItem.getId().getPath(),
                       mcLoc("item/generated"))
                       .override()
                       .model(new ModelFile.UncheckedModelFile(trimNameResLoc.getNamespace() + ":item/" + trimNameResLoc.getPath()))
                       .predicate(mcLoc("trim_type"), trimValue).end()
                       .texture("layer0", ResourceLocation.fromNamespaceAndPath(MODID, "item/" + itemDeferredItem.getId().getPath()));
           });
       }
    }
}
