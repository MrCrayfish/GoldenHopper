package com.mrcrayfish.goldenhopper.client;

import com.mrcrayfish.goldenhopper.client.gui.screens.inventory.GoldenHopperScreen;
import com.mrcrayfish.goldenhopper.core.ModContainers;
import com.mrcrayfish.goldenhopper.core.ModEntities;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.MinecartRenderer;

/**
 * Author: MrCrayfish
 */
public class ClientHandler
{
    public static void init()
    {
        MenuScreens.register(ModContainers.GOLDEN_HOPPER.get(), GoldenHopperScreen::new);
        EntityRenderers.register(ModEntities.GOLDEN_HOPPER_MINECART.get(), context ->
                new MinecartRenderer<>(context, ModelLayers.HOPPER_MINECART));
    }
}
