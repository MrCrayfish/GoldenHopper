package com.mrcrayfish.goldenhopper.datagen;

import com.mrcrayfish.goldenhopper.core.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.tags.BlockTags;

/**
 * Author: MrCrayfish
 */
public class BlockTagGen extends FabricTagProvider.BlockTagProvider
{
    public BlockTagGen(FabricDataGenerator generator)
    {
        super(generator);
    }

    @Override
    protected void generateTags()
    {
        this.getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.GOLDEN_HOPPER.get());
        this.getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.GOLDEN_HOPPER.get());
    }
}
