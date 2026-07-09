package com.mrcrayfish.goldenhopper.data;

import com.mrcrayfish.goldenhopper.core.ModBlocks;
import com.mrcrayfish.goldenhopper.platform.Services;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.tags.VanillaItemTagsProvider;
import net.minecraft.references.ItemIds;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class CommonItemTagsProvider
{
    public static void addTags(Function<TagKey<Item>, TagAppender<Item>> tag)
    {
        tag.apply(Services.PLATFORM.getGoldIngotsTag()).add(ItemIds.GOLD_INGOT);
        tag.apply(ItemTags.PIGLIN_LOVED).add(ModBlocks.GOLDEN_HOPPER.getItemKey());
    }
}
