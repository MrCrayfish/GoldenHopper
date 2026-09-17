package com.mrcrayfish.goldenhopper.data;

import com.mrcrayfish.goldenhopper.core.ModBlocks;
import com.mrcrayfish.goldenhopper.util.ReflectedField;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.MultiRegistryBootstrap;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.SingleRegistryBootstrap;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Author: MrCrayfish
 */
public class CommonLootTableProvider extends LootTableProvider
{
    public CommonLootTableProvider()
    {
        super(Set.of(), List.of(new SubProviderEntry(Block::new, LootContextParamSets.BLOCK)));
    }

    public static class Block extends BlockLootSubProvider
    {
        private static final ReflectedField<BlockLootSubProvider, Map<ResourceKey<@NotNull LootTable>, LootTable.Builder>> MAP_FIELD = new ReflectedField<>(BlockLootSubProvider.class, "map");

        protected Block(LootTableSubProvider.Context context)
        {
            super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags(), context);
        }

        @Override
        public void run()
        {
            this.generate();
            MAP_FIELD.get(this).forEach(this.output::accept);
        }

        @Override
        public void generate()
        {
            this.dropSelf(ModBlocks.GOLDEN_HOPPER.get());
        }
    }

    public static SingleRegistryBootstrap<@NotNull LootTable> create()
    {
        return new CommonLootTableProvider();
    }
}
