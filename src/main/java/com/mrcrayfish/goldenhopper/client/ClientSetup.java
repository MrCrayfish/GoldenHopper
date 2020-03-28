package com.mrcrayfish.goldenhopper.client;

import com.mrcrayfish.goldenhopper.client.screen.GoldenHopperScreen;
import com.mrcrayfish.goldenhopper.init.ModContainers;
import net.minecraft.client.gui.ScreenManager;

/**
 * Author: MrCrayfish
 */
public class ClientSetup
{
    public static void init()
    {
        ScreenManager.registerFactory(ModContainers.GOLDEN_HOPPER.get(), GoldenHopperScreen::new);
    }
}
