package com.grobe.techrebirth.item;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.item.custom.TinArmorItem;
import com.grobe.techrebirth.item.custom.UpgradeItem;
import com.grobe.techrebirth.item.custom.WrenchItem;
import com.grobe.techrebirth.util.MetalType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;


public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TechRebirth.MODID);

    // A map to hold dynamically registered nuggets
    public static final Map<MetalType, DeferredItem<Item>> NUGGETS = new EnumMap<>(MetalType.class);



    static {
        for (MetalType metal : MetalType.values()) {
            NUGGETS.put(metal, ITEMS.register(metal.getSerializedName() + "_nugget",
                    () -> new Item(new Item.Properties())));
        }
    }

    //stuff already in minecraft but adding support for other mod items
    public static final DeferredItem<Item> IRON_POWDER = ITEMS.register("iron_powder",
            () -> new Item (new Item.Properties()));
    public static final DeferredItem<Item> COPPER_POWDER = ITEMS.register("copper_powder",
            ()-> new Item (new Item.Properties()));
    public static final DeferredItem<Item> COPPER_GEAR = ITEMS.register("copper_gear",
            ()-> new Item(new Item.Properties()));
    public static final DeferredItem<Item> IRON_GEAR = ITEMS.register("iron_gear",
            ()-> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DIAMOND_POWDER = ITEMS.register("diamond_powder",
            () -> new Item(new Item.Properties()));


    public static final DeferredItem<Item> SPEED_UPGRADE = ITEMS.register("speed_upgrade",
            () -> new UpgradeItem(new Item.Properties()));

    public static final DeferredItem<Item> EFFICIENCY_UPGRADE = ITEMS.register("efficiency_upgrade",
            () -> new UpgradeItem(new Item.Properties()));

    // tin
    public static final DeferredItem<Item> RAW_TIN = ITEMS.register("raw_tin",
            ()-> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TIN_INGOT = ITEMS.register("tin_ingot",
            ()-> new Item (new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
                    pTooltipComponents.add(Component.translatable("tooltip.techrebirth.raw_tin"));
                    super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
                }
            });
    public static final DeferredItem<Item> TIN_POWDER = ITEMS.register("tin_powder",
            ()-> new Item (new Item.Properties()));
    public static final DeferredItem<Item> TIN_GEAR = ITEMS.register("tin_gear",
            ()-> new Item (new Item.Properties()));

    //nickel
    public static final DeferredItem<Item> RAW_NICKEL = ITEMS.register("raw_nickel",
            ()-> new Item(new Item.Properties()));
    public static final DeferredItem<Item> NICKEL_INGOT = ITEMS.register("nickel_ingot",
            ()-> new Item(new Item.Properties()));
    public static final DeferredItem<Item> NICKEL_POWDER = ITEMS.register("nickel_powder",
            ()-> new Item(new Item.Properties()));

    //invar
    public static final DeferredItem<Item> INVAR_INGOT = ITEMS.register("invar_ingot",
    () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INVAR_GEAR = ITEMS.register("invar_gear",
            ()-> new Item(new Item.Properties()));


    //lead
    public static final DeferredItem<Item> LEAD_INGOT = ITEMS.register("lead_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_LEAD = ITEMS.register("raw_lead",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LEAD_GEAR = ITEMS.register("lead_gear",
            () -> new Item(new Item.Properties()));



    //steel
    public static final DeferredItem<Item> STEEL_INGOT = ITEMS.register("steel_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STEEL_GEAR = ITEMS.register("steel_gear",
            () -> new Item(new Item.Properties()));

    //misc ingots
    public static final DeferredItem<Item> BASE_GOLDIUM_INGOT = ITEMS.register("base_goldium_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BLAZING_GOLD_INGOT = ITEMS.register("blazing_gold_ingot",
            () -> new Item(new Item.Properties()));



    public static final DeferredItem<Item> PURIFIED_IRON_POWDER = ITEMS.register("purified_iron_powder",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> PURIFIED_COPPER_POWDER = ITEMS.register("purified_copper_powder",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> PURIFIED_GOLD_POWDER = ITEMS.register("purified_gold_powder",
            () -> new Item(new Item.Properties()));



    //miscellaneous items
    public static final DeferredItem<Item> REDSTONE_RECEPTION_COIL = ITEMS.register("redstone_reception_coil",
            ()-> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LEAD_WRENCH = ITEMS.register("lead_wrench",
            () -> new WrenchItem(new Item.Properties()
                    .stacksTo(1)
            ));
    public static final DeferredItem<EnchantedBookItem> LIGHTNING_STRIKER_ENCHANTED_BOOK = ITEMS.register("lightning_striker_enchanted_book",
            () -> new EnchantedBookItem(new Item.Properties()));

// Armor
    // Blazing gold
    public static final DeferredItem<ArmorItem> BLAZING_GOLD_HELMET = ITEMS.register("blazing_gold_helmet",
            ()-> new ArmorItem(ModArmorMaterials.BLAZING_GOLD_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40))));

    public static final DeferredItem<ArmorItem> BLAZING_GOLD_CHESTPLATE = ITEMS.register("blazing_gold_chestplate",
            ()-> new ArmorItem(ModArmorMaterials.BLAZING_GOLD_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40))));

    public static final DeferredItem<ArmorItem> BLAZING_GOLD_LEGGINGS = ITEMS.register("blazing_gold_leggings",
            ()-> new ArmorItem(ModArmorMaterials.BLAZING_GOLD_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40))));

    public static final DeferredItem<ArmorItem> BLAZING_GOLD_BOOTS = ITEMS.register("blazing_gold_boots",
            ()-> new ArmorItem(ModArmorMaterials.BLAZING_GOLD_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40))));
    //Tin
    public static DeferredItem<ArmorItem> TIN_HELMET = ITEMS.register("tin_helmet",
            ()-> new TinArmorItem(ModArmorMaterials.TIN_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40))));

    public static final DeferredItem<ArmorItem> TIN_CHESTPLATE = ITEMS.register("tin_chestplate",
            ()-> new TinArmorItem(ModArmorMaterials.TIN_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40))));

    public static final DeferredItem<ArmorItem> TIN_LEGGINGS = ITEMS.register("tin_leggings",
            ()-> new TinArmorItem(ModArmorMaterials.TIN_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40))));

    public static final DeferredItem<ArmorItem> TIN_BOOTS = ITEMS.register("tin_boots",
            ()-> new TinArmorItem(ModArmorMaterials.TIN_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40))));


// Tools

    // Blazing Gold
    public static final DeferredItem<SwordItem> BLAZING_GOLD_SWORD = ITEMS.register("blazing_gold_sword",
            ()-> new SwordItem(ModToolTiers.BLAZING_GOLD, new Item.Properties()
                    .attributes(SwordItem.createAttributes(ModToolTiers.BLAZING_GOLD, 10.5f, -2.3f))));

    public static final DeferredItem<AxeItem> BLAZING_GOLD_AXE = ITEMS.register("blazing_gold_axe",
            ()-> new AxeItem(ModToolTiers.BLAZING_GOLD, new Item.Properties()
                    .attributes(AxeItem.createAttributes(ModToolTiers.BLAZING_GOLD, 10.5f, -3f))));

    public static final DeferredItem<PickaxeItem> BLAZING_GOLD_PICKAXE = ITEMS.register("blazing_gold_pickaxe",
            ()-> new PickaxeItem(ModToolTiers.BLAZING_GOLD, new Item.Properties()
                    .attributes(PickaxeItem.createAttributes(ModToolTiers.BLAZING_GOLD, 3f, -2.8f))));

    public static final DeferredItem<ShovelItem> BLAZING_GOLD_SHOVEL = ITEMS.register("blazing_gold_shovel",
            ()-> new ShovelItem(ModToolTiers.BLAZING_GOLD, new Item.Properties()
                    .attributes(ShovelItem.createAttributes(ModToolTiers.BLAZING_GOLD, 12f, -3.2f))));

    public static final DeferredItem<HoeItem> BLAZING_GOLD_HOE = ITEMS.register("blazing_gold_hoe",
            ()-> new HoeItem(ModToolTiers.BLAZING_GOLD, new Item.Properties()
                    .attributes(HoeItem.createAttributes(ModToolTiers.BLAZING_GOLD, 0f, -3f))));

    //Tin
    public static final DeferredItem<SwordItem> TIN_SWORD = ITEMS.register("tin_sword",
            () -> new SwordItem(ModToolTiers.TIN, new Item.Properties()
                    .attributes(SwordItem.createAttributes(ModToolTiers.TIN, 6f, -2.5f))));
    public static final DeferredItem<AxeItem> TIN_AXE = ITEMS.register("tin_axe",
            () -> new AxeItem(ModToolTiers.TIN, new Item.Properties()
                    .attributes(createNewTinAttributes())){
                @Override
                public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
                    pTooltipComponents.add(Component.translatable("tooltip.techrebirth.tin_pickaxe.detailed")
                            .withStyle(ChatFormatting.GREEN));
                    if(Screen.hasShiftDown()){
                        pTooltipComponents.add(Component.translatable("tooltip.techrebirth.tin_pickaxe_bonus"));
                    }
                    super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
                }
            });
    public static final DeferredItem<PickaxeItem> TIN_PICKAXE = ITEMS.register("tin_pickaxe",
            ()-> new PickaxeItem(ModToolTiers.TIN, new Item.Properties()
                    .attributes(createNewTinAttributes())){
    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(Component.translatable("tooltip.techrebirth.tin_pickaxe.detailed")
                .withStyle(ChatFormatting.GREEN));
        if(Screen.hasShiftDown()){
            pTooltipComponents.add(Component.translatable("tooltip.techrebirth.tin_pickaxe_bonus"));
        }
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
    }
});
    public static final DeferredItem<ShovelItem> TIN_SHOVEL = ITEMS.register("tin_shovel",
            ()-> new ShovelItem(ModToolTiers.TIN, new Item.Properties()
                    .attributes(createNewTinAttributes())){
                @Override
                public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
                    pTooltipComponents.add(Component.translatable("tooltip.techrebirth.tin_pickaxe.detailed")
                            .withStyle(ChatFormatting.GREEN));
                    if(Screen.hasShiftDown()){
                        pTooltipComponents.add(Component.translatable("tooltip.techrebirth.tin_pickaxe_bonus"));
                    }
                    super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
                }
            });
    public static final DeferredItem<HoeItem> TIN_HOE = ITEMS.register("tin_hoe",
            () -> new HoeItem(ModToolTiers.TIN, new Item.Properties()
                    .attributes(HoeItem.createAttributes(ModToolTiers.TIN, 0f, -3f))));
    //Food Items
    public static final DeferredItem<Item> COOKED_CARROT = ITEMS.register("cooked_carrot",
            () -> new Item(new Item.Properties().food(ModFoodProperties.COOKED_CARROT)));



    private static ItemAttributeModifiers createNewTinAttributes(){
        PickaxeItem.createAttributes(
                ModToolTiers.TIN,
                3f,
                -2.8f
        ).withModifierAdded(
                Attributes.MINING_EFFICIENCY, new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "tin_mining_bonus"), 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.MAINHAND
        );
        return ItemAttributeModifiers.builder().build();
    }


    public static void register(IEventBus eventbus){
        ITEMS.register(eventbus);
    }
}
