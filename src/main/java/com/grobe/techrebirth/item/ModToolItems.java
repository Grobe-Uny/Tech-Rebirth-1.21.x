package com.grobe.techrebirth.item;

import com.grobe.techrebirth.TechRebirth;
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

import java.util.List;

public class ModToolItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TechRebirth.MODID);


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
