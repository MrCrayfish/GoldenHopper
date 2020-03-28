package com.mrcrayfish.goldenhopper.inventory.container;

import com.mrcrayfish.goldenhopper.init.ModContainers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class GoldenHopperContainer extends Container
{
    private final IInventory hopperInventory;

    public GoldenHopperContainer(int windowId, PlayerInventory playerInventory)
    {
        this(windowId, playerInventory, new Inventory(6));
    }

    public GoldenHopperContainer(int windowId, PlayerInventory playerInventory, IInventory hopperInventory)
    {
        super(ModContainers.GOLDEN_HOPPER.get(), windowId);
        this.hopperInventory = hopperInventory;
        assertInventorySize(hopperInventory, 6);
        hopperInventory.openInventory(playerInventory.player);

        this.addSlot(new Slot(hopperInventory, 0, 26, 20));

        for(int i = 0; i < 5; i++)
        {
            this.addSlot(new Slot(hopperInventory, i + 1, 62 + i * 18, 20));
        }

        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, i * 18 + 51));
            }
        }

        for(int i = 0; i < 9; i++)
        {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 109));
        }

    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return this.hopperInventory.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
    {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if(slot != null && slot.getHasStack())
        {
            ItemStack slotStack = slot.getStack();
            result = slotStack.copy();
            if(index < this.hopperInventory.getSizeInventory())
            {
                if(!this.mergeItemStack(slotStack, this.hopperInventory.getSizeInventory(), this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(!this.mergeItemStack(slotStack, 1, this.hopperInventory.getSizeInventory(), false))
            {
                return ItemStack.EMPTY;
            }

            if(slotStack.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return result;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn)
    {
        super.onContainerClosed(playerIn);
        this.hopperInventory.closeInventory(playerIn);
    }
}
