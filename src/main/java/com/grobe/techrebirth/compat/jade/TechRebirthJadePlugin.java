package com.grobe.techrebirth.compat.jade;

import com.grobe.techrebirth.TechRebirth;
import com.grobe.techrebirth.block.custom.ElectricFurnaceBlock;
import com.grobe.techrebirth.block.custom.entity.ElectricFurnaceBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class TechRebirthJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(ElectricFurnaceJadeProvider.INSTANCE, ElectricFurnaceBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        // Register for the base ElectricFurnaceBlock so it also covers the CreativeElectricFurnaceBlock (it extends the base)
        registration.registerBlockComponent(ElectricFurnaceJadeProvider.INSTANCE, ElectricFurnaceBlock.class);
    }
}
