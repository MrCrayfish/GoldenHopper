package com.mrcrayfish.goldenhopper.world.level.block.entity;

import com.mrcrayfish.goldenhopper.platform.Services;
import com.mrcrayfish.goldenhopper.util.HopperHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * Author: MrCrayfish
 */
public abstract class AbstractHopperBlockEntity extends RandomizableContainerBlockEntity implements Hopper
{
    protected NonNullList<ItemStack> items;
    protected final int transferSpeed;
    protected int transferCooldown = -1;
    protected long tickedGameTime;

    protected AbstractHopperBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int transferSpeed)
    {
        super(type, pos, state);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        this.transferSpeed = transferSpeed;
    }

    @Override
    protected abstract Component getDefaultName();

    @Override
    protected abstract AbstractContainerMenu createMenu(int windowId, Inventory playerInventory);

    @Override
    protected NonNullList<ItemStack> getItems()
    {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items)
    {
        this.items = items;
    }

    @Override
    public double getLevelX()
    {
        return this.worldPosition.getX() + 0.5;
    }

    @Override
    public double getLevelY()
    {
        return this.worldPosition.getY() + 0.5;
    }

    @Override
    public double getLevelZ()
    {
        return this.worldPosition.getZ() + 0.5;
    }

    @Override
    public int getContainerSize()
    {
        return 5;
    }

    public void setTransferCooldown(int transferCooldown)
    {
        this.transferCooldown = transferCooldown;
    }

    public int getTransferSpeed()
    {
        return this.transferSpeed;
    }

    public boolean isCoolingDown()
    {
        return this.transferCooldown > 0;
    }

    public boolean isCustomCooldown()
    {
        return this.transferCooldown > this.transferSpeed;
    }

    public long getLastUpdateTime()
    {
        return this.tickedGameTime;
    }

    public boolean isInventoryFull()
    {
        return this.items.stream().noneMatch(stack -> stack.isEmpty() || stack.getCount() != stack.getMaxStackSize());
    }

    public int[] getTransferableSlots()
    {
        return IntStream.range(0, this.items.size()).toArray();
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if(!this.tryLoadLootTable(tag))
        {
            ContainerHelper.loadAllItems(tag, this.items);
        }
        this.transferCooldown = tag.getInt("TransferCooldown");
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        if(!this.trySaveLootTable(tag))
        {
            ContainerHelper.saveAllItems(tag, this.items);
        }
        tag.putInt("TransferCooldown", this.transferCooldown);
    }

    @Override
    public ItemStack removeItem(int index, int count)
    {
        this.unpackLootTable(null);
        return ContainerHelper.removeItem(this.getItems(), index, count);
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        this.unpackLootTable(null);
        this.getItems().set(index, stack);
        if(stack.getCount() > this.getMaxStackSize())
        {
            stack.setCount(this.getMaxStackSize());
        }
    }

    public void onEntityCollide(Level level, BlockPos pos, BlockState state, Entity entity)
    {
        if(entity instanceof ItemEntity && Shapes.joinIsNotEmpty(Shapes.create(entity.getBoundingBox().move(-pos.getX(), -pos.getY(), -pos.getZ())), this.getSuckShape(), BooleanOp.AND))
        {
            this.attemptTransferItems(level, pos, state, () -> HopperBlockEntity.addItem(this, (ItemEntity) entity));
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AbstractHopperBlockEntity hopper)
    {
        hopper.serverTick(level, pos, state);
    }

    protected void serverTick(Level level, BlockPos pos, BlockState state)
    {
        this.transferCooldown--;
        this.tickedGameTime = level.getGameTime();
        if(!this.isCoolingDown())
        {
            this.setTransferCooldown(0);
            this.attemptTransferItems(level, pos, state, () -> HopperBlockEntity.suckInItems(level, this));
        }
    }

    protected void attemptTransferItems(Level level, BlockPos pos, BlockState state, Supplier<Boolean> pullItem)
    {
        if(!level.isClientSide() && !this.isCoolingDown() && this.getBlockState().getValue(BlockStateProperties.ENABLED))
        {
            boolean transferred;
            transferred = !this.isEmpty() && this.pushItems(level, pos, state);
            transferred |= !this.isInventoryFull() && pullItem.get();
            if(transferred)
            {
                this.setTransferCooldown(this.transferSpeed);
                this.setChanged();
            }
        }
    }

    protected boolean pushItems(Level level, BlockPos pos, BlockState state)
    {
        // Gets the container this hopper is pointing at
        Container container = getTargetContainer(level, pos, state);
        if(container == null)
            return false;

        // Checks if the slots provided for the face are not full
        Direction direction = state.getValue(BlockStateProperties.FACING_HOPPER).getOpposite();
        if(Services.PLATFORM.isFullContainer(container, direction))
            return false;

        // Transfer items from this hopper's transferable slots into the container
        for(int slotIndex : this.getTransferableSlots())
        {
            // Get item and skip if empty
            ItemStack stack = this.getItem(slotIndex);
            if(stack.isEmpty())
                continue;

            // Try transferring one item. If the result stack is empty, transfer was successful.
            ItemStack copyStack = stack.copy();
            ItemStack resultStack = HopperBlockEntity.addItem(this, container, this.removeItem(slotIndex, 1), direction);
            if(resultStack.isEmpty())
            {
                container.setChanged();
                return true;
            }

            // Restore copy if no item was transferred
            this.setItem(slotIndex, copyStack);
        }
        return false;
    }

    @Nullable
    private static Container getTargetContainer(Level level, BlockPos pos, BlockState state)
    {
        return HopperBlockEntity.getContainerAt(level, pos.relative(state.getValue(BlockStateProperties.FACING_HOPPER)));
    }

    public static void applyTransferCooldown(Container source, Container target)
    {
        if(!HopperHelper.isOnCustomCooldown(target))
        {
            long targetLastUpdate = HopperHelper.getLastUpdate(target);
            long sourceLastUpdate = HopperHelper.getLastUpdate(source);
            int offset = sourceLastUpdate > 0 && targetLastUpdate >= sourceLastUpdate ? 1 : 0;
            int cooldown = HopperHelper.getTransferCooldown(target) - offset;
            HopperHelper.setCooldown(target, cooldown);
        }
    }
}
