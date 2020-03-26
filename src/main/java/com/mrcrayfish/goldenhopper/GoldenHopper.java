package com.mrcrayfish.goldenhopper;

import com.mrcrayfish.goldenhopper.init.ModBlocks;
import com.mrcrayfish.goldenhopper.init.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
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
    }
}
