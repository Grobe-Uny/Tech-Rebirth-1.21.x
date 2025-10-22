package com.grobe.techrebirth.compat.jade;

import com.grobe.techrebirth.block.custom.furnace.ElectricFurnaceBlock;
import com.grobe.techrebirth.block.custom.entity.furnace.ElectricFurnaceBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class TechRebirthJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        // Electric Furnace server data
        registration.registerBlockDataProvider(ElectricFurnaceJadeProvider.INSTANCE, ElectricFurnaceBlockEntity.class);
        // Generator server data
        registration.registerBlockDataProvider(GeneratorJadeProvider.INSTANCE, com.grobe.techrebirth.block.custom.entity.GeneratorBlockEntity.class);
        // Energy Bank server data
        registration.registerBlockDataProvider(EnergyBankJadeProvider.INSTANCE, com.grobe.techrebirth.block.custom.entity.bank.EnergyBankBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        // Register for the base ElectricFurnaceBlock so it also covers the CreativeElectricFurnaceBlock (it extends the base)
        registration.registerBlockComponent(ElectricFurnaceJadeProvider.INSTANCE, ElectricFurnaceBlock.class);
        // Register client components for Generator and Energy Bank blocks
        registration.registerBlockComponent(GeneratorJadeProvider.INSTANCE, com.grobe.techrebirth.block.custom.generator.GeneratorBlock.class);
        registration.registerBlockComponent(EnergyBankJadeProvider.INSTANCE, com.grobe.techrebirth.block.custom.bank.EnergyBankBlock.class);
    }
}
