package com.mrcrayfish.goldenhopper.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import org.jetbrains.annotations.NotNull;

/**
 * Author: MrCrayfish
 */
public class DataGeneration implements DataGeneratorEntrypoint
{
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator)
    {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider((FabricDataGenerator.Pack.Factory<@NotNull CommonModelProvider>) CommonModelProvider::new);
        pack.addProvider(CommonLootTableProvider::new);
        pack.addProvider(CommonRecipeProvider.Runner::new);
        pack.addProvider((output, lookup) -> new FabricTagsProvider<>(output, Registries.BLOCK, lookup) {
            @Override
            protected void addTags(HolderLookup.@NotNull Provider provider) {
                CommonBlockTagsProvider.addTags(this::tag);
            }
        });
        pack.addProvider((output, lookup) -> new FabricTagsProvider<>(output, Registries.ITEM, lookup) {
            @Override
            protected void addTags(HolderLookup.@NotNull Provider provider) {
                CommonItemTagsProvider.addTags(this::tag);
            }
        });
    }
}
