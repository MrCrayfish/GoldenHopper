package com.mrcrayfish.goldenhopper.util;

import com.mrcrayfish.goldenhopper.platform.Services;
import com.mrcrayfish.goldenhopper.world.level.block.entity.AbstractHopperBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.HopperBlockEntity;

/**
 * Author: MrCrayfish
 */
public class HopperHelper
{
    public static int getTransferCooldown(Container container)
    {
        if(container instanceof AbstractHopperBlockEntity hopper)
        {
            return hopper.getTransferSpeed();
        }
        return HopperBlockEntity.MOVE_ITEM_SPEED;
    }

    public static void setCooldown(Container container, int time)
    {
        if(container instanceof HopperBlockEntity hopper)
        {
            Services.PLATFORM.setHopperCooldown(hopper, time);
        }
        else if(container instanceof AbstractHopperBlockEntity hopper)
        {
            hopper.setTransferCooldown(time);
        }
    }

    public static long getLastUpdate(Container container)
    {
        if(container instanceof HopperBlockEntity hopper)
        {
            return Services.PLATFORM.getHopperLastGameTime(hopper);
        }
        else if(container instanceof AbstractHopperBlockEntity hopper)
        {
            return hopper.getLastUpdateTime();
        }
        return -1;
    }

    public static boolean isOnCustomCooldown(Container container)
    {
        if(container instanceof HopperBlockEntity hopper)
        {
            return Services.PLATFORM.isHopperOnCustomCooldown(hopper);
        }
        else if(container instanceof AbstractHopperBlockEntity hopper)
        {
            return hopper.isCustomCooldown();
        }
        return true;
    }
}
