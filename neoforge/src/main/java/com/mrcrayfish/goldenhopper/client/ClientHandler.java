package com.mrcrayfish.goldenhopper.client;

import com.mrcrayfish.goldenhopper.Constants;
import com.mrcrayfish.goldenhopper.core.ModContainers;
import com.mrcrayfish.goldenhopper.core.ModEntities;
import com.mrcrayfish.goldenhopper.data.*;
import com.mrcrayfish.goldenhopper.inventory.GoldenHopperScreen;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * Author: MrCrayfish
 */
@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public final class ClientHandler
{
    @SubscribeEvent
    private static void onClientSetup(FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> {
            EntityRenderers.register(ModEntities.GOLDEN_HOPPER_MINECART.get(), context ->
                new MinecartRenderer(context, ModelLayers.HOPPER_MINECART));
        });
    }

    @SubscribeEvent
    private static void onRegisterScreens(RegisterMenuScreensEvent event)
    {
        event.register(ModContainers.GOLDEN_HOPPER.get(), GoldenHopperScreen::new);
    }

    @SubscribeEvent
    private static void onGatherClientData(GatherDataEvent.Client event)
    {
        event.createProvider(CommonModelProvider::new);
        event.createProvider(CommonRecipeProvider.Runner::new);
        event.createProvider(CommonLootTableProvider::new);
        event.createProvider(CommonBlockTagsProvider::new);
        event.createProvider(CommonItemTagsProvider::new);
    }
}
