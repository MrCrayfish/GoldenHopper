package com.mrcrayfish.goldenhopper.util;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.stream.IntStream;

/**
 * Author: MrCrayfish
 */
public class ItemHandlerHelper
{
    /**
     * Determines if the item handler is full. Full is considered when all slots are not empty and
     * the item in each slot has reached its stack limit.
     *
     * @param handler the item handler to check
     * @return true if full
     */
    public static boolean isFull(IItemHandler handler)
    {
        return IntStream.range(0, handler.getSlots()).noneMatch(index -> {
            ItemStack stack = handler.getStackInSlot(index);
            return stack.isEmpty() || stack.getCount() < handler.getSlotLimit(index);
        });
    }

    /**
     * Determines if the item handler is empty. Empty is considered when all slots have no item.
     *
     * @param handler the item handler to check
     * @return true if empty
     */
    public static boolean isEmpty(IItemHandler handler)
    {
        return IntStream.range(0, handler.getSlots()).noneMatch(index -> {
            ItemStack stack = handler.getStackInSlot(index);
            return stack.isEmpty();
        });
    }
}
