package com.mrcrayfish.goldenhopper.data;

import com.mrcrayfish.goldenhopper.core.ModBlocks;
import com.mrcrayfish.goldenhopper.platform.Services;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.VanillaItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

/**
 * Author: MrCrayfish
 */
public class CommonItemTagsProvider extends VanillaItemTagsProvider
{
    public CommonItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> providerCompletableFuture)
    {
        super(output, providerCompletableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        this.tag(Services.PLATFORM.getGoldIngotsTag()).add(Items.GOLD_INGOT);
        this.tag(ItemTags.PIGLIN_LOVED).add(ModBlocks.GOLDEN_HOPPER.get().asItem());
    }
}
