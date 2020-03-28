package com.mrcrayfish.goldenhopper.client;

import com.mrcrayfish.goldenhopper.client.screen.GoldenHopperScreen;
import com.mrcrayfish.goldenhopper.init.ModContainers;
import com.mrcrayfish.goldenhopper.init.ModEntities;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

/**
 * Author: MrCrayfish
 */
public class ClientSetup
{
    public static void init()
    {
        ScreenManager.registerFactory(ModContainers.GOLDEN_HOPPER.get(), GoldenHopperScreen::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.GOLDEN_HOPPER_MINECART.get(), MinecartRenderer::new);
    }
}
