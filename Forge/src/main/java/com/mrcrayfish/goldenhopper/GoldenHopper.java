package com.mrcrayfish.goldenhopper;

import com.mrcrayfish.goldenhopper.client.ClientHandler;
import com.mrcrayfish.goldenhopper.core.ModItems;
import com.mrcrayfish.goldenhopper.datagen.BlockTagGen;
import com.mrcrayfish.goldenhopper.datagen.LootTableGen;
import com.mrcrayfish.goldenhopper.datagen.RecipeGen;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
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
        generator.addProvider(event.includeServer(), new RecipeGen(output));
        generator.addProvider(event.includeServer(), new LootTableGen(output));
        generator.addProvider(event.includeServer(), new BlockTagGen(output, event.getLookupProvider(), event.getExistingFileHelper()));
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
