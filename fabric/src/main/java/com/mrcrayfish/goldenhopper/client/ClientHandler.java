package com.mrcrayfish.goldenhopper.client;

import com.mrcrayfish.goldenhopper.core.ModContainers;
import com.mrcrayfish.goldenhopper.core.ModEntities;
import com.mrcrayfish.goldenhopper.core.ModItems;
import com.mrcrayfish.goldenhopper.inventory.GoldenHopperScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;

/**
 * Author: MrCrayfish
 */
public class ClientHandler implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        MenuScreens.register(ModContainers.GOLDEN_HOPPER.get(), GoldenHopperScreen::new);
        EntityRenderers.register(ModEntities.GOLDEN_HOPPER_MINECART.get(), context ->
            new MinecartRenderer(context, ModelLayers.HOPPER_MINECART));
        CreativeModeTabEvents.MODIFY_OUTPUT_ALL.register((group, entries) -> {
            if(group == BuiltInRegistries.CREATIVE_MODE_TAB.getValue(CreativeModeTabs.REDSTONE_BLOCKS)) {
                entries.accept(ModItems.GOLDEN_HOPPER::get);
                entries.accept(ModItems.GOLDEN_HOPPER_MINECART::get);
            }
        });
    }
}
