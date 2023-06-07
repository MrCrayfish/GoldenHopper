package com.mrcrayfish.goldenhopper.client;

import com.mrcrayfish.goldenhopper.client.gui.screens.inventory.GoldenHopperScreen;
import com.mrcrayfish.goldenhopper.core.ModContainers;
import com.mrcrayfish.goldenhopper.core.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.MinecartRenderer;

/**
 * Author: MrCrayfish
 */
public class ClientHandler implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        MenuScreens.register(ModContainers.GOLDEN_HOPPER.get(), GoldenHopperScreen::new);
        EntityRendererRegistry.register(ModEntities.GOLDEN_HOPPER_MINECART.get(), context ->
                new MinecartRenderer<>(context, ModelLayers.HOPPER_MINECART));
    }
}
