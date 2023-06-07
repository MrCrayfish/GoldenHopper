package com.mrcrayfish.goldenhopper.core;

import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import com.mrcrayfish.goldenhopper.Constants;
import com.mrcrayfish.goldenhopper.platform.Services;
import com.mrcrayfish.goldenhopper.world.level.block.entity.AbstractHopperBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class ModBlockEntities
{
    public static final RegistryEntry<BlockEntityType<AbstractHopperBlockEntity>> GOLDEN_HOPPER = RegistryEntry.blockEntity(new ResourceLocation(Constants.MOD_ID, "golden_hopper"), Services.PLATFORM::createGoldenHopperBlockEntity, () -> new Block[]{ModBlocks.GOLDEN_HOPPER.get()});
}
