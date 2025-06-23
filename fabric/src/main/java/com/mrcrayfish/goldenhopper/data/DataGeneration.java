package com.mrcrayfish.goldenhopper.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.client.data.models.ModelProvider;

/**
 * Author: MrCrayfish
 */
public class DataGeneration implements DataGeneratorEntrypoint
{
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator)
    {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider((FabricDataGenerator.Pack.Factory<CommonModelProvider>) CommonModelProvider::new);
        pack.addProvider(CommonLootTableProvider::new);
        pack.addProvider(CommonRecipeProvider.Runner::new);
        pack.addProvider(CommonBlockTagsProvider::new);
        pack.addProvider(CommonItemTagsProvider::new);
    }
}
