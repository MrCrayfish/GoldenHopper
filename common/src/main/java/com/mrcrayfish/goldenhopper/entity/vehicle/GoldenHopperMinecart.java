package com.mrcrayfish.goldenhopper.entity.vehicle;

import com.mrcrayfish.goldenhopper.blockentity.GoldenHopperBlockEntity;
import com.mrcrayfish.goldenhopper.core.ModBlocks;
import com.mrcrayfish.goldenhopper.core.ModEntities;
import com.mrcrayfish.goldenhopper.core.ModItems;
import com.mrcrayfish.goldenhopper.inventory.GoldenHopperMenu;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.vehicle.minecart.MinecartHopper;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

/**
 * Author: MrCrayfish
 */
public class GoldenHopperMinecart extends MinecartHopper implements WorldlyContainer
{
    private static final int CONTAINER_SIZE = 6;
    private static final int FILTER_SLOT_INDEX = 0;

    public GoldenHopperMinecart(EntityType<? extends GoldenHopperMinecart> type, Level level)
    {
        super(type, level);
    }

    public GoldenHopperMinecart(Level level, double x, double y, double z)
    {
        super(ModEntities.GOLDEN_HOPPER_MINECART.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    public int getContainerSize()
    {
        return CONTAINER_SIZE;
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory)
    {
        return new GoldenHopperMenu(windowId, playerInventory, this);
    }

    @Override
    public BlockState getDefaultDisplayBlockState()
    {
        return ModBlocks.GOLDEN_HOPPER.get().defaultBlockState();
    }

    @Override
    public ItemStack getPickResult()
    {
        return new ItemStack(ModItems.GOLDEN_HOPPER_MINECART.get());
    }

    @Override
    protected Item getDropItem()
    {
        return ModItems.GOLDEN_HOPPER_MINECART.get();
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack)
    {
        return GoldenHopperBlockEntity.canInsertIntoSlot(index, FILTER_SLOT_INDEX, this.getItem(FILTER_SLOT_INDEX), stack);
    }

    @Override
    public int[] getSlotsForFace(Direction side)
    {
        return IntStream.range(0, this.getContainerSize()).filter(value -> value != FILTER_SLOT_INDEX).toArray();
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction)
    {
        return index != FILTER_SLOT_INDEX && this.canPlaceItem(index, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction)
    {
        return index != FILTER_SLOT_INDEX;
    }

    @Override
    public boolean canTakeItem(Container container, int index, ItemStack stack)
    {
        return index != FILTER_SLOT_INDEX;
    }

    public int getComparatorLevel()
    {
        float filled = IntStream.range(1, this.getContainerSize())
            .mapToObj(this::getItem)
            .filter(stack -> !stack.isEmpty())
            .map(stack -> stack.getCount() / (float) Math.min(this.getMaxStackSize(), stack.getMaxStackSize()))
            .reduce(0F, Float::sum);
        filled /= (this.getContainerSize() - 1.0F);
        return Mth.floor(filled * 14.0F) + (filled > 0 ? 1 : 0);
    }
}
