package com.mrcrayfish.goldenhopper.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.VanillaInventoryCodeHooks;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
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
    protected abstract Component getDefaultName();

    @Override
    protected abstract AbstractContainerMenu createMenu(int windowId, Inventory playerInventory);

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

    @Override
    protected abstract net.minecraftforge.items.IItemHandler createUnSidedHandler();

    public static void serverTick(Level level, BlockPos pos, BlockState state, AbstractHopperBlockEntity hopper)
    {
        hopper.transferCooldown--;
        hopper.tickedGameTime = level.getGameTime();
        if(!hopper.isCoolingDown())
        {
            hopper.setTransferCooldown(0);
            attemptTransferItems(level, pos, state, hopper, () -> HopperBlockEntity.suckInItems(level, hopper)); //TODO need to make custom one that filters
        }
    }

    private static void attemptTransferItems(Level level, BlockPos pos, BlockState state, AbstractHopperBlockEntity hopper, Supplier<Boolean> supplier)
    {
        if(level.isClientSide())
            return;

        if(hopper.isCoolingDown() || !hopper.getBlockState().getValue(BlockStateProperties.ENABLED))
            return;

        //TODO THIS MAY NOT WORK
        boolean pushedItems = !hopper.isEmpty() && transferItems(level, pos, state, hopper);
        pushedItems |= !hopper.isInventoryFull() && supplier.get();
        if(pushedItems)
        {
            hopper.setTransferCooldown(hopper.transferSpeed);
            hopper.setChanged();
        }
    }

    private static boolean transferItems(Level level, BlockPos pos, BlockState state, AbstractHopperBlockEntity hopper)
    {
        if(transferItemsToCaps(level, state, hopper))
            return true;

        Container container = getTargetContainer(level, pos, state);
        if(container == null)
            return false;

        Direction direction = state.getValue(BlockStateProperties.FACING_HOPPER).getOpposite();
        if(isContainerFull(container, direction))
            return false;

        for(int index : hopper.getTransferableSlots())
        {
            ItemStack stack = hopper.getItem(index);
            if(stack.isEmpty())
                continue;
            ItemStack copyStack = stack.copy();
            ItemStack resultStack = HopperBlockEntity.addItem(hopper, container, hopper.removeItem(index, 1), direction);
            if(resultStack.isEmpty())
            {
                container.setChanged();
                return true;
            }
            hopper.setItem(index, copyStack);
        }
        return false;
    }

    private static ItemStack attemptMoveStackToContainer(@Nullable Container source, Object target, IItemHandler handler, ItemStack stack, @Nullable Direction direction)
    {
        if(target instanceof WorldlyContainer worldlyContainer && direction != null)
        {
            int[] slots = worldlyContainer.getSlotsForFace(direction);
            for(int i = 0; i < slots.length && !stack.isEmpty(); i++)
            {
                stack = attemptMoveStackToSlot(source, target, handler, stack, i, direction);
            }
            return stack;
        }

        if(target instanceof Container container)
        {
            for(int i = 0; i < container.getContainerSize() && !stack.isEmpty(); ++i)
            {
                stack = attemptMoveStackToSlot(source, target, handler, stack, i, direction);
            }
            return stack;
        }

        for(int i = 0; i < handler.getSlots() && !stack.isEmpty(); i++)
        {
            stack = attemptMoveStackToSlot(source, target, handler, stack, i, direction);
        }

        return stack;
    }

    private static ItemStack attemptMoveStackToSlot(@Nullable Container source, Object target, IItemHandler handler, ItemStack stack, int index, @Nullable Direction direction)
    {
        if(!canPlaceInContainerSlot(target, stack, index, direction))
            return stack;

        boolean moved = false;
        boolean targetWasEmpty = !isItemHandlerEmpty(handler);
        ItemStack targetStack = handler.getStackInSlot(index);
        if(targetStack.isEmpty())
        {
            stack = handler.insertItem(index, stack, false);
            moved = stack.isEmpty();
        }
        else if(canMergeStacks(stack, targetStack))
        {
            int remaining = Math.min(stack.getCount(), stack.getMaxStackSize() - targetStack.getCount());
            stack.shrink(remaining);
            targetStack.grow(remaining);
            moved = remaining > 0;
        }

        if(moved)
        {
            if(targetWasEmpty)
            {
                int cooldown = 0;
                if(target instanceof HopperBlockEntity hopper && !hopper.isOnCustomCooldown())
                {
                    if(source instanceof AbstractHopperBlockEntity abstractHopper && hopper.getLastUpdateTime() >= abstractHopper.tickedGameTime)
                        cooldown = 1;
                    hopper.setCooldown(HopperBlockEntity.MOVE_ITEM_SPEED - cooldown);
                }
                else if(target instanceof AbstractHopperBlockEntity hopper && !hopper.isCustomCooldown())
                {
                    if(source instanceof AbstractHopperBlockEntity abstractHopper && hopper.getLastUpdateTime() >= abstractHopper.tickedGameTime)
                        cooldown = 1;
                    hopper.setTransferCooldown(hopper.transferSpeed - cooldown);
                }
            }
            if(target instanceof Container container)
            {
                container.setChanged();
            }
        }

        return stack;
    }

    public static void entityCollide(Level level, BlockPos pos, BlockState state, Entity entity, AbstractHopperBlockEntity hopper)
    {
        if(entity instanceof ItemEntity && Shapes.joinIsNotEmpty(Shapes.create(entity.getBoundingBox().move(-pos.getX(), -pos.getY(), -pos.getZ())), hopper.getSuckShape(), BooleanOp.AND))
        {

            attemptTransferItems(level, pos, state, hopper, () -> {
                AtomicBoolean returnValue = new AtomicBoolean(false);
                hopper.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(handler -> {
                    returnValue.set(addItemEntity(hopper, handler, (ItemEntity) entity));
                });
                return returnValue.get();
            });
        }
    }

    public static boolean addItemEntity(Container container, IItemHandler handler, ItemEntity entity)
    {
        ItemStack copyStack = entity.getItem().copy();
        ItemStack resultStack = attemptMoveStackToContainer(null, container, handler, copyStack, null);
        if(resultStack.isEmpty())
        {
            entity.discard();
            return true;
        }
        entity.setItem(resultStack);
        return false;
    }

    private static boolean transferItemsToCaps(Level level, BlockState state, AbstractHopperBlockEntity hopper)
    {
        Optional<Pair<IItemHandler, Object>> optional = getItemHandler(level, state, hopper);
        return optional.map(pair ->
        {
            IItemHandler handler = pair.getKey();
            if(isItemHandlerFull(handler))
                return false;

            Object value = pair.getValue();
            for(int index : hopper.getTransferableSlots())
            {
                ItemStack stack = hopper.getItem(index);
                if(stack.isEmpty())
                    continue;

                ItemStack copyStack = hopper.getItem(index).copy();
                ItemStack insertStack = hopper.removeItem(index, 1);
                ItemStack resultStack = attemptMoveStackToContainer(hopper, value, handler, insertStack, state.getValue(BlockStateProperties.FACING_HOPPER).getOpposite());
                if(resultStack.isEmpty())
                {
                    if(value instanceof Container container)
                        container.setChanged();
                    return true;
                }
                hopper.setItem(index, copyStack);
            }
            return false;
        }).orElse(false);
    }

    private static Optional<Pair<IItemHandler, Object>> getItemHandler(Level level, BlockState state, Hopper hopper)
    {
        Direction direction = state.getValue(BlockStateProperties.FACING_HOPPER);
        double x = hopper.getLevelX() + (double) direction.getStepX();
        double y = hopper.getLevelY() + (double) direction.getStepY();
        double z = hopper.getLevelZ() + (double) direction.getStepZ();
        return VanillaInventoryCodeHooks.getItemHandler(level, x, y, z, direction.getOpposite());
    }

    private static boolean isItemHandlerFull(IItemHandler handler)
    {
        return IntStream.range(0, handler.getSlots()).noneMatch(index -> {
            ItemStack stack = handler.getStackInSlot(index);
            return stack.isEmpty() || stack.getCount() < handler.getSlotLimit(index);
        });
    }

    private static boolean isItemHandlerEmpty(IItemHandler handler)
    {
        return IntStream.range(0, handler.getSlots()).noneMatch(index -> {
            ItemStack stack = handler.getStackInSlot(index);
            return stack.isEmpty();
        });
    }

    private static boolean canPlaceInContainerSlot(Object target, ItemStack stack, int index, @Nullable Direction direction)
    {
        if(target instanceof Container container)
            return container.canPlaceItem(index, stack) && (!(container instanceof WorldlyContainer) || ((WorldlyContainer) container).canPlaceItemThroughFace(index, stack, direction));
        return true;
    }

    private static boolean canMergeStacks(ItemStack source, ItemStack target)
    {
        return source.is(target.getItem()) && source.getDamageValue() == target.getDamageValue() && source.getCount() <= source.getMaxStackSize() && ItemStack.tagMatches(source, target);
    }

    @Nullable
    private static Container getTargetContainer(Level level, BlockPos pos, BlockState state)
    {
        return HopperBlockEntity.getContainerAt(level, pos.relative(state.getValue(BlockStateProperties.FACING_HOPPER)));
    }

    private static boolean isContainerFull(Container container, Direction direction)
    {
        return getContainerSlots(container, direction).mapToObj(container::getItem).allMatch(stack -> stack.getCount() >= stack.getMaxStackSize());
    }

    private static IntStream getContainerSlots(Container container, Direction direction)
    {
        return container instanceof WorldlyContainer ? IntStream.of(((WorldlyContainer) container).getSlotsForFace(direction)) : IntStream.range(0, container.getContainerSize());
    }
}
