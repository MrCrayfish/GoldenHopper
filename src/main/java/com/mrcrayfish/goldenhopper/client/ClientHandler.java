package com.mrcrayfish.goldenhopper.client;

import com.mrcrayfish.goldenhopper.client.screen.GoldenHopperScreen;
import com.mrcrayfish.goldenhopper.init.ModContainers;
import com.mrcrayfish.goldenhopper.init.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.resources.sounds.MinecartSoundInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

/**
 * Author: MrCrayfish
 */
public class ClientHandler
{
    public static void init()
    {
        MenuScreens.register(ModContainers.GOLDEN_HOPPER.get(), GoldenHopperScreen::new);
        EntityRenderers.register(ModEntities.GOLDEN_HOPPER_MINECART.get(), context -> {
            return new MinecartRenderer<>(context, ModelLayers.HOPPER_MINECART);
        });
    }

    public static void handleGoldenHopperMinecartSpawn(Entity entity)
    {
        if(entity != null)
        {
            if(entity instanceof AbstractMinecart)
            {
                Minecraft.getInstance().getSoundManager().play(new MinecartSoundInstance((AbstractMinecart) entity));
            }
        }
    }
}
