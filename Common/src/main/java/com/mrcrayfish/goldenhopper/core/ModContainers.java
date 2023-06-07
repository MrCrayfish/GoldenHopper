package com.mrcrayfish.goldenhopper.core;

import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import com.mrcrayfish.goldenhopper.Constants;
import com.mrcrayfish.goldenhopper.world.inventory.GoldenHopperMenu;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class ModContainers
{
    public static final RegistryEntry<MenuType<GoldenHopperMenu>> GOLDEN_HOPPER = RegistryEntry.menuType(new ResourceLocation(Constants.MOD_ID, "golden_hopper"), GoldenHopperMenu::new);
}
