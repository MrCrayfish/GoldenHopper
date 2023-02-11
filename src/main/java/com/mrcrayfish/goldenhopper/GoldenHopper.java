package com.mrcrayfish.goldenhopper;

import com.mrcrayfish.goldenhopper.client.ClientHandler;
import com.mrcrayfish.goldenhopper.data.LootTableGen;
import com.mrcrayfish.goldenhopper.data.RecipeGen;
import com.mrcrayfish.goldenhopper.init.ModBlockEntities;
import com.mrcrayfish.goldenhopper.init.ModBlocks;
import com.mrcrayfish.goldenhopper.init.ModContainers;
import com.mrcrayfish.goldenhopper.init.ModEntities;
import com.mrcrayfish.goldenhopper.init.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;

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
    }

    private void onCreativeTabBuilding(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab().equals(CreativeModeTabs.REDSTONE_BLOCKS))
        {
            ModItems.REGISTER.getEntries().stream().map(RegistryObject::get).forEach(event::accept);
        }
    }
}
