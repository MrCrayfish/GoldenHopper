package com.mrcrayfish.goldenhopper.data;

import com.mrcrayfish.goldenhopper.Constants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

/**
 * Author: MrCrayfish
 */
@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGeneration
{
    @SubscribeEvent
    private static void onGatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        generator.addProvider(event.includeServer(), new NeoForgeRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new NeoForgeLootTableGen(output, lookupProvider));
        NeoForgeBlockTagGen blockTagGen = new NeoForgeBlockTagGen(output, lookupProvider, event.getExistingFileHelper());
        generator.addProvider(event.includeServer(), blockTagGen);
        generator.addProvider(event.includeServer(), new NeoForgeItemTagGen(output, lookupProvider, blockTagGen.contentsGetter(), event.getExistingFileHelper()));
    }
}
