package com.mrcrayfish.goldenhopper;

import com.mrcrayfish.goldenhopper.client.ClientHandler;
import com.mrcrayfish.goldenhopper.init.ModBlockEntities;
import com.mrcrayfish.goldenhopper.init.ModBlocks;
import com.mrcrayfish.goldenhopper.init.ModContainers;
import com.mrcrayfish.goldenhopper.init.ModEntities;
import com.mrcrayfish.goldenhopper.init.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Author: MrCrayfish
 */
@Mod(Reference.MOD_ID)
public class GoldenHopper
{
    public GoldenHopper()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.REGISTER.register(bus);
        ModItems.REGISTER.register(bus);
        ModEntities.REGISTER.register(bus);
        ModBlockEntities.REGISTER.register(bus);
        ModContainers.REGISTER.register(bus);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        ClientHandler.init();
    }
}
