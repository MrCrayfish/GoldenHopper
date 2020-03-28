package com.mrcrayfish.goldenhopper.init;

import com.mrcrayfish.goldenhopper.Reference;
import com.mrcrayfish.goldenhopper.entity.GoldenHopperMinecart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.BiFunction;

/**
 * Author: MrCrayfish
 */
public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> REGISTER = new DeferredRegister<>(ForgeRegistries.ENTITIES, Reference.MOD_ID);

    public static final RegistryObject<EntityType<GoldenHopperMinecart>> GOLDEN_HOPPER_MINECART = registerEntity("golden_hopper_minecart", GoldenHopperMinecart::new, 0.98F, 0.7F);

    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntity(String id, BiFunction<EntityType<T>, World, T> function, float width, float height)
    {
        EntityType<T> type = EntityType.Builder.create(function::apply, EntityClassification.MISC).size(width, height).build(id);
        return ModEntities.REGISTER.register(id, () -> type);
    }
}
