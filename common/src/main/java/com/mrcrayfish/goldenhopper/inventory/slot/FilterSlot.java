package com.mrcrayfish.goldenhopper.inventory.slot;

import com.mrcrayfish.goldenhopper.Constants;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
public class FilterSlot extends Slot
{
    private static final Identifier EMPTY_SLOT_FILTER = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "container/slot/filter");

    public FilterSlot(Container container, int index, int x, int y)
    {
        super(container, index, x, y);
    }

    @Override
    public int getMaxStackSize()
    {
        return 1;
    }

    @Nullable
    @Override
    public Identifier getNoItemIcon()
    {
        return EMPTY_SLOT_FILTER;
    }
}
