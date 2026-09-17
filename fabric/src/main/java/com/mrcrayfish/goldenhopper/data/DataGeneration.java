package com.mrcrayfish.goldenhopper.data;

import com.mrcrayfish.goldenhopper.core.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.fabricmc.fabric.impl.loot.FabricLootTable;
import net.fabricmc.fabric.impl.loot.LootTableHolderProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

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
        pack.addProvider((output, lookup) -> new FabricRecipeProvider(output, lookup) {
            @Override
            protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, BootstrapContext<Recipe<?>> recipes, BootstrapContext<Advancement> advancements) {
                return new CommonRecipeProvider(recipes, advancements);
            }
        });
        pack.addProvider((output, lookup) -> new FabricBlockLootSubProvider(output, lookup) {
            @Override
            public void generate() {
                this.dropSelf(ModBlocks.GOLDEN_HOPPER.get());
            }
        });
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
