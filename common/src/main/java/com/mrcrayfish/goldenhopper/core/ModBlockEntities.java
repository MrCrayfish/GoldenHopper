package com.mrcrayfish.goldenhopper.core;

import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import com.mrcrayfish.goldenhopper.Constants;
import com.mrcrayfish.goldenhopper.blockentity.GoldenHopperBlockEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class ModBlockEntities
{
    public static final RegistryEntry<BlockEntityType<GoldenHopperBlockEntity>> GOLDEN_HOPPER = RegistryEntry.blockEntity(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "golden_hopper"), GoldenHopperBlockEntity::new, () -> new Block[]{ModBlocks.GOLDEN_HOPPER.get()});
}
