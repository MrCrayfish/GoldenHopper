package com.mrcrayfish.goldenhopper.data;

import com.mrcrayfish.goldenhopper.core.ModBlocks;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class CommonBlockTagsProvider
{
    public static void addTags(Function<TagKey<Block>, TagAppender<Block>> tag)
    {
        tag.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.GOLDEN_HOPPER.getKey());
        tag.apply(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.GOLDEN_HOPPER.getKey());
    }
}
