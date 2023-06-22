package com.mrcrayfish.goldenhopper.core;

import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import com.mrcrayfish.goldenhopper.Constants;
import com.mrcrayfish.goldenhopper.world.level.block.GoldenHopperBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class ModBlocks
{
    public static final RegistryEntry<Block> GOLDEN_HOPPER = RegistryEntry.block(new ResourceLocation(Constants.MOD_ID, "golden_hopper"), () -> new GoldenHopperBlock(Block.Properties.of().sound(SoundType.METAL).mapColor(MapColor.GOLD).strength(2.0F)));
}
