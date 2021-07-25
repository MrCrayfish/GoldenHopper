package com.mrcrayfish.goldenhopper.world.level.block.entity;

import com.mrcrayfish.goldenhopper.init.ModBlockEntities;
import com.mrcrayfish.goldenhopper.items.wrapper.GoldenHopperItemHandler;
import com.mrcrayfish.goldenhopper.world.inventory.GoldenHopperMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

/**
 * Author: MrCrayfish
 */
public class GoldenHopperBlockEntity extends AbstractHopperBlockEntity implements WorldlyContainer
{
    private static final int TRANSFER_COOLDOWN = 8;
    private static final int CONTAINER_SIZE = 6;

    public GoldenHopperBlockEntity(BlockPos pos, BlockState state)
    {
        super(ModBlockEntities.GOLDEN_HOPPER.get(), pos, state, TRANSFER_COOLDOWN);
    }

    @Override
    public int getContainerSize()
    {
        return CONTAINER_SIZE;
    }

    @Override
    public int[] getTransferableSlots()
    {
        return IntStream.range(1, this.items.size()).toArray();
    }

    @Override
    protected Component getDefaultName()
    {
        return new TranslatableComponent("container.goldenhopper.golden_hopper");
    }

    @Override
    protected AbstractContainerMenu createMenu(int windowId, Inventory playerInventory)
    {
        return new GoldenHopperMenu(windowId, playerInventory, this);
    }

    @Override
    protected net.minecraftforge.items.IItemHandler createUnSidedHandler()
    {
        return new GoldenHopperItemHandler(this);
    }

    @Override
    public int[] getSlotsForFace(Direction side)
    {
        return this.getTransferableSlots();
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack)
    {
        return index != 0 && (this.items.get(0).isEmpty() || stack.getItem() == this.items.get(0).getItem());
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction)
    {
        return this.items.get(0).isEmpty() || stack.getItem() == this.items.get(0).getItem();
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction)
    {
        return index != 0;
    }
}
