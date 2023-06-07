package com.mrcrayfish.goldenhopper.datagen;

import com.mrcrayfish.goldenhopper.core.ModBlocks;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class CommonLootTableGen
{
    public static void generate(Consumer<Block> dropSelf)
    {
        dropSelf.accept(ModBlocks.GOLDEN_HOPPER.get());
    }
}
