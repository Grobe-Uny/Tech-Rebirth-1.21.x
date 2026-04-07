package com.grobe.techrebirth.compat.ma;

import com.blakebr0.mysticalagriculture.api.IMysticalAgriculturePlugin;
import com.blakebr0.mysticalagriculture.api.MysticalAgriculturePlugin;
import com.blakebr0.mysticalagriculture.api.crop.Crop;
import com.blakebr0.mysticalagriculture.api.crop.CropTier;
import com.blakebr0.mysticalagriculture.api.crop.CropType;
import com.blakebr0.mysticalagriculture.api.lib.LazyIngredient;
import com.blakebr0.mysticalagriculture.api.registry.ICropRegistry;
import com.grobe.techrebirth.TechRebirth;
import net.minecraft.resources.ResourceLocation;

@MysticalAgriculturePlugin
public class TechRebirthMAPlugin implements IMysticalAgriculturePlugin {

    @Override
    public void onRegisterCrops(ICropRegistry registry) {
        // This plugin is now ready to register custom crops or modify existing ones
        // when Mystical Agriculture is present.
        registry.register(new Crop(
                ResourceLocation.fromNamespaceAndPath(TechRebirth.MODID, "conductium"),
                CropTier.THREE,
                CropType.RESOURCE,
                LazyIngredient.item("techrebirth:conductium_ingot")
        )
                .setColor(0xFF7070));
    }

}
