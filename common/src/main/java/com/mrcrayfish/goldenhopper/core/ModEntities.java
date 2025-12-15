package com.mrcrayfish.goldenhopper.core;

import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import com.mrcrayfish.goldenhopper.Constants;
import com.mrcrayfish.goldenhopper.entity.vehicle.GoldenHopperMinecart;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class ModEntities
{
    public static final RegistryEntry<EntityType<GoldenHopperMinecart>> GOLDEN_HOPPER_MINECART = RegistryEntry.entityType(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "golden_hopper_minecart"), () -> EntityType.Builder.<GoldenHopperMinecart>of(GoldenHopperMinecart::new, MobCategory.MISC).sized(0.98F, 0.7F));
}
