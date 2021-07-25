package com.mrcrayfish.goldenhopper.block.entity;

import com.mrcrayfish.goldenhopper.block.GoldenHopperBlock;
import com.mrcrayfish.goldenhopper.init.ModBlockEntities;
import com.mrcrayfish.goldenhopper.inventory.container.GoldenHopperContainer;
import com.mrcrayfish.goldenhopper.item.GoldenHopperItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.VanillaInventoryCodeHooks;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Author: MrCrayfish
 */
public class GoldenHopperBlockEntity extends RandomizableContainerBlockEntity implements Hopper, WorldlyContainer
{
    private static final int TRANSFER_COOLDOWN = 8;
    private static final int CONTAINER_SIZE = 6;

    private NonNullList<ItemStack> inventory = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    private int transferCooldown = -1;
    private long tickedGameTime;

    public GoldenHopperBlockEntity(BlockPos pos, BlockState state)
    {
        super(ModBlockEntities.GOLDEN_HOPPER.get(), pos, state);
    }

    @Override
    public void load(CompoundTag compound)
    {
        super.load(compound);
        this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if(!this.tryLoadLootTable(compound))
        {
            ContainerHelper.loadAllItems(compound, this.inventory);
        }
        this.transferCooldown = compound.getInt("TransferCooldown");
    }

    @Override
    public CompoundTag save(CompoundTag compound)
    {
        super.save(compound);
        if(!this.trySaveLootTable(compound))
        {
            ContainerHelper.saveAllItems(compound, this.inventory);
        }
        compound.putInt("TransferCooldown", this.transferCooldown);
        return compound;
    }

    @Override
    public int getContainerSize()
    {
        return this.inventory.size();
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
    protected Component getDefaultName()
    {
        return new TranslatableComponent("container.goldenhopper.golden_hopper");
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GoldenHopperBlockEntity hopper)
    {
        hopper.transferCooldown--;
        hopper.tickedGameTime = level.getGameTime();
        if(!hopper.isOnTransferCooldown())
        {
            hopper.setTransferCooldown(0);
            transferItems(level, pos, state, hopper, () -> pullItems(level, hopper)); //TODO need to make custom one that filters
        }
    }

    private static boolean transferItems(Level level, BlockPos pos, BlockState state, GoldenHopperBlockEntity hopper, Supplier<Boolean> supplier)
    {
        if(level.isClientSide())
            return false;

        if(hopper.isOnTransferCooldown() || !hopper.getBlockState().getValue(GoldenHopperBlock.ENABLED))
            return false;

        boolean pushedItems = false;
        if(!hopper.isEmpty())
        {
            pushedItems = hopper.transferItemsOut();
        }
        if(!hopper.isFull())
        {
            pushedItems |= supplier.get();
        }
        if(pushedItems)
        {
            hopper.setTransferCooldown(TRANSFER_COOLDOWN);
            hopper.setChanged();
            return true;
        }
        return false;
    }

    private boolean isFull()
    {
        return this.inventory.stream().noneMatch(stack -> stack.isEmpty() || stack.getCount() != stack.getMaxStackSize());
    }

    private boolean transferItemsOut()
    {
        if(insertHook(this))
        {
            return true;
        }

        Container inventory = this.getInventoryForHopperTransfer();
        if(inventory == null)
        {
            return false;
        }

        Direction direction = this.getBlockState().getValue(GoldenHopperBlock.FACING).getOpposite();
        if(this.isInventoryFull(inventory, direction))
        {
            return false;
        }

        for(int index = 1; index < this.getContainerSize(); index++)
        {
            if(!this.getItem(index).isEmpty())
            {
                ItemStack copy = this.getItem(index).copy();
                ItemStack result = putStackInInventoryAllSlots(this, inventory, this.removeItem(index, 1), direction);
                if(result.isEmpty())
                {
                    inventory.setChanged();
                    return true;
                }
                this.setItem(index, copy);
            }
        }
        return false;
    }

    private static IntStream getSlotsStream(Container inventory, Direction direction)
    {
        return inventory instanceof WorldlyContainer ? IntStream.of(((WorldlyContainer) inventory).getSlotsForFace(direction)) : IntStream.range(0, inventory.getContainerSize());
    }

    private boolean isInventoryFull(Container inventory, Direction direction)
    {
        return getSlotsStream(inventory, direction).allMatch((index) -> {
            ItemStack stack = inventory.getItem(index);
            return stack.getCount() >= stack.getMaxStackSize();
        });
    }

    private static boolean isInventoryEmpty(Container inventory, Direction direction)
    {
        return getSlotsStream(inventory, direction).allMatch((index) -> inventory.getItem(index).isEmpty());
    }

    public static boolean pullItems(Level level, Hopper hopper)
    {
        Boolean ret = net.minecraftforge.items.VanillaInventoryCodeHooks.extractHook(level, hopper);
        if(ret != null)
        {
            return ret;
        }

        Container inventory = getSourceInventory(level, hopper);
        if(inventory != null)
        {
            Direction direction = Direction.DOWN;
            if(isInventoryEmpty(inventory, direction))
            {
                return false;
            }
            return getSlotsStream(inventory, direction).anyMatch((index) -> pullItemFromSlot(hopper, inventory, index, direction));
        }

        /* Pulls any item entities that are currently above the hopper */
        for(ItemEntity entity : getItemEntities(level, hopper))
        {
            if(captureItemEntity(hopper, entity))
            {
                return true;
            }
        }

        return false;
    }

    private static boolean pullItemFromSlot(Hopper hopper, Container inventory, int index, Direction direction)
    {
        ItemStack stack = inventory.getItem(index);
        if(!stack.isEmpty() && canExtractItemFromSlot(inventory, stack, index, direction))
        {
            ItemStack copy = stack.copy();
            ItemStack result = putStackInInventoryAllSlots(inventory, hopper, inventory.removeItem(index, 1), (Direction) null);
            if(result.isEmpty())
            {
                inventory.setChanged();
                return true;
            }
            inventory.setItem(index, copy);
        }
        return false;
    }

    public static boolean captureItemEntity(Container inventory, ItemEntity entity)
    {
        boolean captured = false;
        ItemStack copy = entity.getItem().copy();
        ItemStack result = putStackInInventoryAllSlots(null, inventory, copy, null);
        if(result.isEmpty())
        {
            captured = true;
            entity.remove(false);
        }
        else
        {
            entity.setItem(result);
        }
        return captured;
    }

    private static ItemStack putStackInInventoryAllSlots(BlockEntity source, Object destination, IItemHandler destInventory, ItemStack stack)
    {
        for(int slot = 0; slot < destInventory.getSlots() && !stack.isEmpty(); slot++)
        {
            stack = insertStack(source, destination, destInventory, stack, slot);
        }
        return stack;
    }

    private static ItemStack insertStack(BlockEntity source, Object destination, IItemHandler destInventory, ItemStack stack, int slot)
    {
        ItemStack itemstack = destInventory.getStackInSlot(slot);

        if(destInventory.insertItem(slot, stack, true).isEmpty())
        {
            boolean insertedItem = false;
            boolean inventoryWasEmpty = isEmpty(destInventory);

            if(itemstack.isEmpty())
            {
                destInventory.insertItem(slot, stack, false);
                stack = ItemStack.EMPTY;
                insertedItem = true;
            }
            else if(ItemHandlerHelper.canItemStacksStack(itemstack, stack))
            {
                int originalSize = stack.getCount();
                stack = destInventory.insertItem(slot, stack, false);
                insertedItem = originalSize < stack.getCount();
            }

            if(insertedItem)
            {
                if(inventoryWasEmpty && destination instanceof HopperBlockEntity)
                {
                    HopperBlockEntity destinationHopper = (HopperBlockEntity) destination;
                    if(!destinationHopper.isOnCustomCooldown())
                    {
                        int k = 0;
                        /*if (source instanceof TileEntityHopper)
                        {
                            if (destinationHopper.getLastUpdateTime() >= ((TileEntityHopper) source).getLastUpdateTime())
                            {
                                k = 1;
                            }
                        }*/
                        destinationHopper.setCooldown(8 - k);
                    }
                }
            }
        }

        return stack;
    }

    private static ItemStack putStackInInventoryAllSlots(@Nullable Container source, Container destination, ItemStack stack, @Nullable Direction direction)
    {
        if(destination instanceof WorldlyContainer container && direction != null)
        {
            int[] slots = container.getSlotsForFace(direction);
            for(int i = 0; i < slots.length && !stack.isEmpty(); i++)
            {
                stack = insertStack(source, container, stack, slots[i], direction);
            }
        }
        else
        {
            int i = destination.getContainerSize();
            for(int j = 0; j < i && !stack.isEmpty(); ++j)
            {
                stack = insertStack(source, destination, stack, j, direction);
            }
        }
        return stack;
    }

    private static boolean canInsertItemInSlot(Container inventory, ItemStack stack, int index, @Nullable Direction side)
    {
        if(!inventory.canPlaceItem(index, stack))
        {
            return false;
        }
        return !(inventory instanceof WorldlyContainer) || ((WorldlyContainer) inventory).canPlaceItemThroughFace(index, stack, side);
    }

    private static boolean canExtractItemFromSlot(Container inventory, ItemStack stack, int index, Direction side)
    {
        return !(inventory instanceof WorldlyContainer) || ((WorldlyContainer) inventory).canTakeItemThroughFace(index, stack, side);
    }

    private static ItemStack insertStack(@Nullable Container source, Container destination, ItemStack stack, int index, @Nullable Direction direction)
    {
        ItemStack slotStack = destination.getItem(index);
        if(canInsertItemInSlot(destination, stack, index, direction))
        {
            boolean shouldInsert = false;
            boolean destinationEmpty = destination.isEmpty();
            if(slotStack.isEmpty())
            {
                destination.setItem(index, stack);
                stack = ItemStack.EMPTY;
                shouldInsert = true;
            }
            else if(canCombine(slotStack, stack))
            {
                int remainingCount = stack.getMaxStackSize() - slotStack.getCount();
                int shrinkCount = Math.min(stack.getCount(), remainingCount);
                stack.shrink(shrinkCount);
                slotStack.grow(shrinkCount);
                shouldInsert = shrinkCount > 0;
            }

            if(shouldInsert)
            {
                if(destinationEmpty && destination instanceof HopperBlockEntity hopper)
                {
                    if(!hopper.isOnCustomCooldown())
                    {
                        int cooldownAmount = 0;
                        if(source instanceof GoldenHopperBlockEntity)
                        {
                            GoldenHopperBlockEntity goldenHopper = (GoldenHopperBlockEntity) source;
                            if(hopper.getLastUpdateTime() >= goldenHopper.tickedGameTime)
                            {
                                cooldownAmount = 1;
                            }
                        }
                        hopper.setCooldown(TRANSFER_COOLDOWN - cooldownAmount);
                    }
                }
                destination.setChanged();
            }
        }

        return stack;
    }

    @Nullable
    private Container getInventoryForHopperTransfer()
    {
        Direction direction = this.getBlockState().getValue(GoldenHopperBlock.FACING);
        return getInventoryAtPosition(this.getLevel(), this.worldPosition.relative(direction));
    }

    @Nullable
    public static Container getSourceInventory(Level level, Hopper hopper)
    {
        return getInventoryAtPosition(level, hopper.getLevelX(), hopper.getLevelY() + 1.0D, hopper.getLevelZ());
    }

    public static List<ItemEntity> getItemEntities(Level level, Hopper hopper)
    {
        return hopper.getSuckShape().toAabbs().stream().flatMap((box) -> level.getEntitiesOfClass(ItemEntity.class, box.move(hopper.getLevelX() - 0.5, hopper.getLevelY() - 0.5, hopper.getLevelZ() - 0.5), EntitySelector.ENTITY_STILL_ALIVE).stream()).collect(Collectors.toList());
    }

    @Nullable
    private static Container getInventoryAtPosition(Level level, BlockPos pos)
    {
        return getInventoryAtPosition(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    @Nullable
    private static Container getInventoryAtPosition(Level level, double x, double y, double z)
    {
        Container targetInventory = null;
        BlockPos targetPos = new BlockPos(x, y, z);
        BlockState targetState = level.getBlockState(targetPos);
        Block targetBlock = targetState.getBlock();
        if(targetBlock instanceof WorldlyContainerHolder holder)
        {
            targetInventory = holder.getContainer(targetState, level, targetPos);
        }
        else if(targetState.hasBlockEntity())
        {
            if(level.getBlockEntity(targetPos) instanceof Container container)
            {
                targetInventory = container;
                if(targetInventory instanceof ChestBlockEntity && targetBlock instanceof ChestBlock chestBlock)
                {
                    targetInventory = ChestBlock.getContainer(chestBlock, targetState, level, targetPos, true);
                }
            }
        }

        if(targetInventory == null)
        {
            List<Entity> itemEntities = level.getEntities((Entity) null, new AABB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntitySelector.CONTAINER_ENTITY_SELECTOR);
            if(!itemEntities.isEmpty())
            {
                targetInventory = (Container) itemEntities.get(level.random.nextInt(itemEntities.size()));
            }
        }

        return targetInventory;
    }

    private static boolean canCombine(ItemStack stack1, ItemStack stack2)
    {
        if(stack1.getItem() != stack2.getItem())
        {
            return false;
        }
        else if(stack1.getDamageValue() != stack2.getDamageValue())
        {
            return false;
        }
        else if(stack1.getCount() > stack1.getMaxStackSize())
        {
            return false;
        }
        else
        {
            return ItemStack.tagMatches(stack1, stack2);
        }
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

    public void setTransferCooldown(int ticks)
    {
        this.transferCooldown = ticks;
    }

    private boolean isOnTransferCooldown()
    {
        return this.transferCooldown > 0;
    }

    public boolean mayTransfer()
    {
        return this.transferCooldown > TRANSFER_COOLDOWN;
    }

    protected NonNullList<ItemStack> getItems()
    {
        return this.inventory;
    }

    protected void setItems(NonNullList<ItemStack> itemsIn)
    {
        this.inventory = itemsIn;
    }

    public void onEntityCollision(Entity entity)
    {
        if(entity instanceof ItemEntity)
        {
            BlockPos pos = this.getBlockPos();
            if(Shapes.joinIsNotEmpty(Shapes.create(entity.getBoundingBox().move(-pos.getX(), -pos.getY(), -pos.getZ())), this.getSuckShape(), BooleanOp.AND))
            {
                transferItems(this.level, this.worldPosition, this.getBlockState(), this, () -> captureItemEntity(this, (ItemEntity) entity));
            }
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player)
    {
        return new GoldenHopperContainer(id, player, this);
    }

    @Override
    protected net.minecraftforge.items.IItemHandler createUnSidedHandler()
    {
        return new GoldenHopperItemHandler(this);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack)
    {
        return index != 0 && (this.inventory.get(0).isEmpty() || stack.getItem() == this.inventory.get(0).getItem());
    }

    @Override
    public int[] getSlotsForFace(Direction side)
    {
        return IntStream.range(1, this.inventory.size()).toArray();
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction)
    {
        return this.inventory.get(0).isEmpty() || stack.getItem() == this.inventory.get(0).getItem();
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction)
    {
        return index != 0;
    }

    private static boolean insertHook(GoldenHopperBlockEntity hopper)
    {
        Direction direction = hopper.getBlockState().getValue(GoldenHopperBlock.FACING);
        double x = hopper.getLevelX() + (double) direction.getStepX();
        double y = hopper.getLevelY() + (double) direction.getStepY();
        double z = hopper.getLevelZ() + (double) direction.getStepZ();
        Optional<Pair<IItemHandler, Object>> handler = VanillaInventoryCodeHooks.getItemHandler(hopper.getLevel(), x, y, z, direction.getOpposite());
        return handler.map(destinationResult -> {
            IItemHandler itemHandler = destinationResult.getKey();
            if(isFull(itemHandler))
            {
                return false;
            }

            Object destination = destinationResult.getValue();
            for(int i = 0; i < hopper.getContainerSize(); ++i)
            {
                if(!hopper.getItem(i).isEmpty() && hopper.canTakeItemThroughFace(i, hopper.getItem(i), direction))
                {
                    ItemStack originalSlotContents = hopper.getItem(i).copy();
                    ItemStack insertStack = hopper.removeItem(i, 1);
                    ItemStack remainder = putStackInInventoryAllSlots(hopper, destination, itemHandler, insertStack);
                    if(remainder.isEmpty())
                    {
                        return true;
                    }
                    hopper.setItem(i, originalSlotContents);
                }
            }
            return false;
        }).orElse(false);
    }

    private static boolean isFull(IItemHandler handler)
    {
        for(int slot = 0; slot < handler.getSlots(); slot++)
        {
            ItemStack stack = handler.getStackInSlot(slot);
            if(stack.isEmpty() || stack.getCount() != stack.getMaxStackSize())
            {
                return false;
            }
        }
        return true;
    }

    private static boolean isEmpty(IItemHandler itemHandler)
    {
        for(int slot = 0; slot < itemHandler.getSlots(); slot++)
        {
            ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
            if(stackInSlot.getCount() > 0)
            {
                return false;
            }
        }
        return true;
    }
}
