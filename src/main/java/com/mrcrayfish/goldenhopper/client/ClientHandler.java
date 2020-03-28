package com.mrcrayfish.goldenhopper.client;

import com.mrcrayfish.goldenhopper.client.screen.GoldenHopperScreen;
import com.mrcrayfish.goldenhopper.init.ModContainers;
import com.mrcrayfish.goldenhopper.init.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MinecartTickableSound;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

/**
 * Author: MrCrayfish
 */
public class ClientHandler
{
    public static void init()
    {
        ScreenManager.registerFactory(ModContainers.GOLDEN_HOPPER.get(), GoldenHopperScreen::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.GOLDEN_HOPPER_MINECART.get(), MinecartRenderer::new);
    }

    public static void handleGoldenHopperMinecartSpawn(Entity entity)
    {
        if(entity != null)
        {
            if(entity instanceof AbstractMinecartEntity)
            {
                Minecraft.getInstance().getSoundHandler().play(new MinecartTickableSound((AbstractMinecartEntity) entity));
            }
        }
    }
}
