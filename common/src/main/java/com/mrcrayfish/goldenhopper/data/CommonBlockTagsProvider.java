package com.mrcrayfish.goldenhopper.data;

import com.mrcrayfish.goldenhopper.core.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class CommonBlockTagsProvider extends IntrinsicHolderTagsProvider<Block>
{
    public CommonBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture)
    {
        super(output, Registries.BLOCK, completableFuture, block -> block.builtInRegistryHolder().key());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.GOLDEN_HOPPER.get());
        this.tag(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.GOLDEN_HOPPER.get());
    }
}
