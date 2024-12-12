package com.mrcrayfish.goldenhopper;

import com.mrcrayfish.goldenhopper.core.ModItems;
import com.mrcrayfish.goldenhopper.data.CommonBlockTagsProvider;
import com.mrcrayfish.goldenhopper.data.CommonItemTagsProvider;
import com.mrcrayfish.goldenhopper.data.CommonLootTableProvider;
import com.mrcrayfish.goldenhopper.data.CommonRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.concurrent.CompletableFuture;

/**
 * Author: MrCrayfish
 */
@Mod(Constants.MOD_ID)
public class GoldenHopper
{
    public GoldenHopper(IEventBus bus)
    {
        bus.addListener(this::onCreativeTabBuilding);
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
