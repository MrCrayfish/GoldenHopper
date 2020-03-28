package com.mrcrayfish.goldenhopper.item;

import com.mrcrayfish.goldenhopper.tileentity.GoldenHopperTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nonnull;

/**
 * Author: MrCrayfish
 */
public class GoldenHopperItemHandler extends SidedInvWrapper
{
    private final GoldenHopperTileEntity hopper;

    public GoldenHopperItemHandler(GoldenHopperTileEntity hopper)
    {
        super(hopper, null);
        this.hopper = hopper;
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if(simulate)
        {
            return super.insertItem(slot, stack, simulate);
        }

        boolean wasEmpty = this.inv.isEmpty();
        int originalStackSize = stack.getCount();
        stack = super.insertItem(slot, stack, simulate);
        if(wasEmpty && originalStackSize > stack.getCount())
        {
            if(!this.hopper.mayTransfer())
            {
                this.hopper.setTransferCooldown(8);
            }
        }
        return stack;
    }
}
