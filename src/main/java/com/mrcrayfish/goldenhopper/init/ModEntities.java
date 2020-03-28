package com.mrcrayfish.goldenhopper.init;

import com.mrcrayfish.goldenhopper.Reference;
import com.mrcrayfish.goldenhopper.client.ClientHandler;
import com.mrcrayfish.goldenhopper.entity.GoldenHopperMinecart;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Author: MrCrayfish
 */
public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> REGISTER = new DeferredRegister<>(ForgeRegistries.ENTITIES, Reference.MOD_ID);

    public static final RegistryObject<EntityType<GoldenHopperMinecart>> GOLDEN_HOPPER_MINECART = REGISTER.register("golden_hopper_minecart", () -> {
        EntityType<GoldenHopperMinecart> type = EntityType.Builder.<GoldenHopperMinecart>create(GoldenHopperMinecart::new, EntityClassification.MISC)
            .size(0.98F, 0.7F)
            .setCustomClientFactory((entity, world) -> {
                GoldenHopperMinecart minecart = new GoldenHopperMinecart(world);
                ClientHandler.handleGoldenHopperMinecartSpawn(minecart);
                return minecart;
            }).build("golden_hopper_minecart");
        return type;
    });
}
