package com.mrcrayfish.goldenhopper.datagen;

import com.mrcrayfish.goldenhopper.Constants;
import com.mrcrayfish.goldenhopper.core.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
public class BlockTagGen extends BlockTagsProvider
{
    public BlockTagGen(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper)
    {
        super(generator, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags()
    {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.GOLDEN_HOPPER.get());
        this.tag(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.GOLDEN_HOPPER.get());
    }
}
