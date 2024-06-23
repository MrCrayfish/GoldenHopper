package com.mrcrayfish.goldenhopper;

import com.mrcrayfish.goldenhopper.client.ClientHandler;
import com.mrcrayfish.goldenhopper.core.ModItems;
import com.mrcrayfish.goldenhopper.data.ForgeBlockTagGen;
import com.mrcrayfish.goldenhopper.data.ForgeItemTagGen;
import com.mrcrayfish.goldenhopper.data.ForgeLootTableGen;
import com.mrcrayfish.goldenhopper.data.ForgeRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.concurrent.CompletableFuture;

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
        bus.addListener(this::onCreativeTabBuilding);
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        event.enqueueWork(ClientHandler::init);
    }

    private void onGatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        generator.addProvider(event.includeServer(), new ForgeRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new ForgeLootTableGen(output, lookupProvider));
        ForgeBlockTagGen blockTagGen = new ForgeBlockTagGen(output, lookupProvider, event.getExistingFileHelper());
        generator.addProvider(event.includeServer(), blockTagGen);
        generator.addProvider(event.includeServer(), new ForgeItemTagGen(output, lookupProvider, blockTagGen.contentsGetter(), event.getExistingFileHelper()));
    }

    private void onCreativeTabBuilding(BuildCreativeModeTabContentsEvent event)
    {
        if(event.getTabKey().equals(CreativeModeTabs.REDSTONE_BLOCKS))
        {
            event.accept(ModItems.GOLDEN_HOPPER::get);
            event.accept(ModItems.GOLDEN_HOPPER_MINECART::get);
        }
    }
}
