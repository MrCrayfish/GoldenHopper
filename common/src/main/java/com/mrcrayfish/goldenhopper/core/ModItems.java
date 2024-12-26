package com.mrcrayfish.goldenhopper.core;

import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import com.mrcrayfish.goldenhopper.Constants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.MinecartItem;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class ModItems
{
    public static final RegistryEntry<Item> GOLDEN_HOPPER = RegistryEntry.item(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "golden_hopper"), properties -> new BlockItem(ModBlocks.GOLDEN_HOPPER.get(), properties), () -> new Item.Properties().useBlockDescriptionPrefix());
    public static final RegistryEntry<Item> GOLDEN_HOPPER_MINECART = RegistryEntry.item(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "golden_hopper_minecart"), properties -> new MinecartItem(ModEntities.GOLDEN_HOPPER_MINECART.get(), properties), () -> new Item.Properties().stacksTo(1));
}
