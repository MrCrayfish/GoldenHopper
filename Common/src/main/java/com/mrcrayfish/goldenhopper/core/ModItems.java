package com.mrcrayfish.goldenhopper.core;

import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import com.mrcrayfish.goldenhopper.Constants;
import com.mrcrayfish.goldenhopper.world.level.item.GoldenHopperMinecartItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class ModItems
{
    public static final RegistryEntry<Item> GOLDEN_HOPPER = RegistryEntry.item(new ResourceLocation(Constants.MOD_ID, "golden_hopper"), () -> new BlockItem(ModBlocks.GOLDEN_HOPPER.get(), new Item.Properties()));
    public static final RegistryEntry<Item> GOLDEN_HOPPER_MINECART = RegistryEntry.item(new ResourceLocation(Constants.MOD_ID, "golden_hopper_minecart"), () -> new GoldenHopperMinecartItem(new Item.Properties().stacksTo(1)));
}
