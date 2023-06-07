package com.mrcrayfish.goldenhopper.util;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;

/**
 * Author: MrCrayfish
 */
@SuppressWarnings("UnstableApiUsage")
public class StorageHelper
{
    public static boolean isFull(InventoryStorage storage)
    {
        return storage.getSlots().stream().noneMatch(view -> {
            return view.isResourceBlank() || view.getAmount() < view.getCapacity();
        });
    }

    public static boolean isEmpty(InventoryStorage storage)
    {
        return storage.getSlots().stream().allMatch(StorageView::isResourceBlank);
    }
}
