package com.mrcrayfish.goldenhopper.data;

import com.mrcrayfish.goldenhopper.core.ModBlocks;
import com.mrcrayfish.goldenhopper.util.ReflectedField;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * Author: MrCrayfish
 */
public class CommonLootTableProvider extends LootTableProvider
{
    public CommonLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture)
    {
        super(output, Set.of(), List.of(new SubProviderEntry(Block::new, LootContextParamSets.BLOCK)), completableFuture);
    }

    public static class Block extends BlockLootSubProvider
    {
        private static final ReflectedField<BlockLootSubProvider, Map<ResourceKey<LootTable>, LootTable.Builder>> MAP_FIELD = new ReflectedField<>(BlockLootSubProvider.class, "map");

        protected Block(HolderLookup.Provider provider)
        {
            super(Collections.emptySet(), FeatureFlagSet.of(), provider);
        }

        @Override
        public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer)
        {
            this.generate();
            MAP_FIELD.get(this).forEach(consumer);
        }

        @Override
        public void generate()
        {
            this.dropSelf(ModBlocks.GOLDEN_HOPPER.get());
        }
    }
}
