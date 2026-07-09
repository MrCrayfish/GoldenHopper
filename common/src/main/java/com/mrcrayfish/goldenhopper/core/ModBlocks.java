package com.mrcrayfish.goldenhopper.core;

import com.mrcrayfish.framework.api.registry.BlockRegistryEntry;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import com.mrcrayfish.goldenhopper.Constants;
import com.mrcrayfish.goldenhopper.block.GoldenHopperBlock;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class ModBlocks
{
    public static final BlockRegistryEntry<Block, BlockItem> GOLDEN_HOPPER = RegistryEntry.block(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "golden_hopper"), GoldenHopperBlock::new, () -> Block.Properties.of().sound(SoundType.METAL).mapColor(MapColor.GOLD).strength(2.0F));
}
