package com.mrcrayfish.goldenhopper;

import com.mrcrayfish.goldenhopper.client.ClientHandler;
import com.mrcrayfish.goldenhopper.datagen.BlockTagGen;
import com.mrcrayfish.goldenhopper.datagen.LootTableGen;
import com.mrcrayfish.goldenhopper.datagen.RecipeGen;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Author: MrCrayfish
 */
@Mod(Constants.MOD_ID)
public class GoldenHopper
{
    public GoldenHopper()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onClientSetup);
        bus.addListener(this::onGatherData);
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        event.enqueueWork(ClientHandler::init);
    }

    private void onGatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(event.includeServer(), new RecipeGen(generator));
        generator.addProvider(event.includeServer(), new LootTableGen(generator));
        generator.addProvider(event.includeServer(), new BlockTagGen(generator, event.getExistingFileHelper()));
    }
}
