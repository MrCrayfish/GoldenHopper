package com.mrcrayfish.goldenhopper.core;

import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import com.mrcrayfish.goldenhopper.Constants;
import com.mrcrayfish.goldenhopper.platform.Services;
import com.mrcrayfish.goldenhopper.world.entity.vehicle.GoldenHopperMinecart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class ModEntities
{
    public static final RegistryEntry<EntityType<GoldenHopperMinecart>> GOLDEN_HOPPER_MINECART = RegistryEntry.entityType(new ResourceLocation(Constants.MOD_ID, "golden_hopper_minecart"), () -> EntityType.Builder.<GoldenHopperMinecart>of(Services.PLATFORM::createGoldenHopperMinecart, MobCategory.MISC).sized(0.98F, 0.7F).build("golden_hopper_minecart"));
}
