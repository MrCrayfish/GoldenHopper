package com.mrcrayfish.goldenhopper.tileentity;

import com.mrcrayfish.goldenhopper.block.GoldenHopperBlock;
import com.mrcrayfish.goldenhopper.init.ModTileEntities;
import com.mrcrayfish.goldenhopper.inventory.container.GoldenHopperContainer;
import com.mrcrayfish.goldenhopper.item.GoldenHopperItemHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.*;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.VanillaInventoryCodeHooks;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Author: MrCrayfish
 */
public class GoldenHopperTileEntity extends LockableLootTileEntity implements IHopper, ITickableTileEntity, ISidedInventory
{
    private static final int MAX_COOLDOWN = 8;

    private NonNullList<ItemStack> inventory = NonNullList.withSize(6, ItemStack.EMPTY);
    private int transferCooldown = -1;
    private long tickedGameTime;

    public GoldenHopperTileEntity()
    {
        super(ModTileEntities.GOLDEN_HOPPER.get());
    }

    @Override
    public void read(CompoundNBT compound)
    {
        super.read(compound);
        this.inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        if(!this.checkLootAndRead(compound))
        {
            ItemStackHelper.loadAllItems(compound, this.inventory);
        }
        this.transferCooldown = compound.getInt("TransferCooldown");
    }

    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        super.write(compound);
        if(!this.checkLootAndWrite(compound))
        {
            ItemStackHelper.saveAllItems(compound, this.inventory);
        }

        compound.putInt("TransferCooldown", this.transferCooldown);
        return compound;
    }

    @Override
    public int getSizeInventory()
    {
        return this.inventory.size();
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        this.fillWithLoot(null);
        return ItemStackHelper.getAndSplit(this.getItems(), index, count);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.fillWithLoot(null);
        this.getItems().set(index, stack);
        if(stack.getCount() > this.getInventoryStackLimit())
        {
            stack.setCount(this.getInventoryStackLimit());
        }
    }

    @Override
    protected ITextComponent getDefaultName()
    {
        return new TranslationTextComponent("container.goldenhopper.golden_hopper");
    }

    @Override
    public void tick()
    {
        if(this.world != null && !this.world.isRemote)
        {
            if(this.transferCooldown > 0)
            {
                this.transferCooldown--;
                return;
            }
            this.tickedGameTime = this.world.getGameTime();
            this.updateHopper(() -> pullItems(this)); //TODO need to make custom one that filters
        }
    }

    private boolean updateHopper(Supplier<Boolean> supplier)
    {
        if(this.world != null && !this.world.isRemote)
        {
            if(!this.isOnTransferCooldown() && this.getBlockState().get(GoldenHopperBlock.ENABLED))
            {
                boolean pulledItems = false;
                if(!this.isEmpty())
                {
                    pulledItems = this.transferItemsOut();
                }
                if(!this.isFull())
                {
                    pulledItems |= supplier.get();
                }
                if(pulledItems)
                {
                    this.setTransferCooldown(MAX_COOLDOWN);
                    this.markDirty();
                    return true;
                }
            }
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

        IInventory inventory = this.getInventoryForHopperTransfer();
        if(inventory == null)
        {
            return false;
        }

        Direction direction = this.getBlockState().get(GoldenHopperBlock.FACING).getOpposite();
        if(this.isInventoryFull(inventory, direction))
        {
            return false;
        }

        for(int index = 1; index < this.getSizeInventory(); index++)
        {
            if(!this.getStackInSlot(index).isEmpty())
            {
                ItemStack copy = this.getStackInSlot(index).copy();
                ItemStack result = putStackInInventoryAllSlots(this, inventory, this.decrStackSize(index, 1), direction);
                if(result.isEmpty())
                {
                    inventory.markDirty();
                    return true;
                }
                this.setInventorySlotContents(index, copy);
            }
        }
        return false;
    }

    private static IntStream getSlotsStream(IInventory inventory, Direction direction)
    {
        return inventory instanceof ISidedInventory ? IntStream.of(((ISidedInventory) inventory).getSlotsForFace(direction)) : IntStream.range(0, inventory.getSizeInventory());
    }

    private boolean isInventoryFull(IInventory inventory, Direction direction)
    {
        return getSlotsStream(inventory, direction).allMatch((index) -> {
            ItemStack stack = inventory.getStackInSlot(index);
            return stack.getCount() >= stack.getMaxStackSize();
        });
    }

    private static boolean isInventoryEmpty(IInventory inventory, Direction direction)
    {
        return getSlotsStream(inventory, direction).allMatch((index) -> inventory.getStackInSlot(index).isEmpty());
    }

    public static boolean pullItems(IHopper hopper)
    {
        Boolean ret = net.minecraftforge.items.VanillaInventoryCodeHooks.extractHook(hopper);
        if(ret != null)
        {
            return ret;
        }

        IInventory inventory = getSourceInventory(hopper);
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
        for(ItemEntity entity : getItemEntities(hopper))
        {
            if(captureItemEntity(hopper, entity))
            {
                return true;
            }
        }

        return false;
    }

    private static boolean pullItemFromSlot(IHopper hopper, IInventory inventory, int index, Direction direction)
    {
        ItemStack stack = inventory.getStackInSlot(index);
        if(!stack.isEmpty() && canExtractItemFromSlot(inventory, stack, index, direction))
        {
            ItemStack copy = stack.copy();
            ItemStack result = putStackInInventoryAllSlots(inventory, hopper, inventory.decrStackSize(index, 1), (Direction) null);
            if(result.isEmpty())
            {
                inventory.markDirty();
                return true;
            }
            inventory.setInventorySlotContents(index, copy);
        }
        return false;
    }

    public static boolean captureItemEntity(IInventory inventory, ItemEntity entity)
    {
        boolean captured = false;
        ItemStack copy = entity.getItem().copy();
        ItemStack result = putStackInInventoryAllSlots(null, inventory, copy, null);
        if(result.isEmpty())
        {
            captured = true;
            entity.remove();
        }
        else
        {
            entity.setItem(result);
        }
        return captured;
    }

    private static ItemStack putStackInInventoryAllSlots(@Nullable IInventory source, IInventory destination, ItemStack stack, @Nullable Direction direction)
    {
        if(destination instanceof ISidedInventory && direction != null)
        {
            ISidedInventory sidedInventory = (ISidedInventory) destination;
            int[] slots = sidedInventory.getSlotsForFace(direction);
            for(int i = 0; i < slots.length && !stack.isEmpty(); i++)
            {
                stack = insertStack(source, destination, stack, slots[i], direction);
            }
        }
        else
        {
            int i = destination.getSizeInventory();
            for(int j = 0; j < i && !stack.isEmpty(); ++j)
            {
                stack = insertStack(source, destination, stack, j, direction);
            }
        }
        return stack;
    }

    private static boolean canInsertItemInSlot(IInventory inventory, ItemStack stack, int index, @Nullable Direction side)
    {
        if(!inventory.isItemValidForSlot(index, stack))
        {
            return false;
        }
        return !(inventory instanceof ISidedInventory) || ((ISidedInventory) inventory).canInsertItem(index, stack, side);
    }

    private static boolean canExtractItemFromSlot(IInventory inventory, ItemStack stack, int index, Direction side)
    {
        return !(inventory instanceof ISidedInventory) || ((ISidedInventory) inventory).canExtractItem(index, stack, side);
    }

    private static ItemStack insertStack(@Nullable IInventory source, IInventory destination, ItemStack stack, int index, @Nullable Direction direction)
    {
        ItemStack slotStack = destination.getStackInSlot(index);
        if(canInsertItemInSlot(destination, stack, index, direction))
        {
            boolean shouldInsert = false;
            boolean destinationEmpty = destination.isEmpty();
            if(slotStack.isEmpty())
            {
                destination.setInventorySlotContents(index, stack);
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
                if(destinationEmpty && destination instanceof HopperTileEntity)
                {
                    HopperTileEntity hopper = (HopperTileEntity) destination;
                    if(!hopper.mayTransfer())
                    {
                        int cooldownAmount = 0;
                        if(source instanceof GoldenHopperTileEntity)
                        {
                            GoldenHopperTileEntity goldenHopper = (GoldenHopperTileEntity) source;
                            if(hopper.getLastUpdateTime() >= goldenHopper.tickedGameTime)
                            {
                                cooldownAmount = 1;
                            }
                        }
                        hopper.setTransferCooldown(MAX_COOLDOWN - cooldownAmount);
                    }
                }
                destination.markDirty();
            }
        }

        return stack;
    }

    @Nullable
    private IInventory getInventoryForHopperTransfer()
    {
        Direction direction = this.getBlockState().get(GoldenHopperBlock.FACING);
        return getInventoryAtPosition(this.getWorld(), this.pos.offset(direction));
    }

    @Nullable
    public static IInventory getSourceInventory(IHopper hopper)
    {
        return getInventoryAtPosition(hopper.getWorld(), hopper.getXPos(), hopper.getYPos() + 1.0D, hopper.getZPos());
    }

    public static List<ItemEntity> getItemEntities(IHopper hopper)
    {
        return hopper.getCollectionArea().toBoundingBoxList().stream().flatMap((box) -> hopper.getWorld().getEntitiesWithinAABB(ItemEntity.class, box.offset(hopper.getXPos() - 0.5, hopper.getYPos() - 0.5, hopper.getZPos() - 0.5), EntityPredicates.IS_ALIVE).stream()).collect(Collectors.toList());
    }

    @Nullable
    private static IInventory getInventoryAtPosition(World world, BlockPos pos)
    {
        return getInventoryAtPosition(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    @Nullable
    private static IInventory getInventoryAtPosition(World worldIn, double x, double y, double z)
    {
        IInventory targetInventory = null;
        BlockPos targetPos = new BlockPos(x, y, z);
        BlockState targetState = worldIn.getBlockState(targetPos);
        Block targetBlock = targetState.getBlock();
        if(targetBlock instanceof ISidedInventoryProvider)
        {
            targetInventory = ((ISidedInventoryProvider) targetBlock).createInventory(targetState, worldIn, targetPos);
        }
        else if(targetState.hasTileEntity())
        {
            TileEntity tileEntity = worldIn.getTileEntity(targetPos);
            if(tileEntity instanceof IInventory)
            {
                targetInventory = (IInventory) tileEntity;
                if(targetInventory instanceof ChestTileEntity && targetBlock instanceof ChestBlock)
                {
                    targetInventory = ChestBlock.func_226916_a_((ChestBlock) targetBlock, targetState, worldIn, targetPos, true);
                }
            }
        }

        if(targetInventory == null)
        {
            List<Entity> itemEntities = worldIn.getEntitiesInAABBexcluding(null, new AxisAlignedBB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntityPredicates.HAS_INVENTORY);
            if(!itemEntities.isEmpty())
            {
                targetInventory = (IInventory) itemEntities.get(worldIn.rand.nextInt(itemEntities.size()));
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
        else if(stack1.getDamage() != stack2.getDamage())
        {
            return false;
        }
        else if(stack1.getCount() > stack1.getMaxStackSize())
        {
            return false;
        }
        else
        {
            return ItemStack.areItemStackTagsEqual(stack1, stack2);
        }
    }

    @Override
    public double getXPos()
    {
        return this.pos.getX() + 0.5;
    }

    @Override
    public double getYPos()
    {
        return this.pos.getY() + 0.5;
    }

    @Override
    public double getZPos()
    {
        return this.pos.getZ() + 0.5;
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
        return this.transferCooldown > MAX_COOLDOWN;
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
            BlockPos pos = this.getPos();
            if(VoxelShapes.compare(VoxelShapes.create(entity.getBoundingBox().offset(-pos.getX(), -pos.getY(), -pos.getZ())), this.getCollectionArea(), IBooleanFunction.AND))
            {
                this.updateHopper(() -> captureItemEntity(this, (ItemEntity) entity));
            }
        }
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player)
    {
        return new GoldenHopperContainer(id, player, this);
    }

    @Override
    protected net.minecraftforge.items.IItemHandler createUnSidedHandler()
    {
        return new GoldenHopperItemHandler(this);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return index != 0 && (this.inventory.get(0).isEmpty() || stack.getItem() == this.inventory.get(0).getItem());
    }

    @Override
    public int[] getSlotsForFace(Direction side)
    {
        return IntStream.range(1, this.inventory.size()).toArray();
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, @Nullable Direction direction)
    {
        return this.inventory.get(0).isEmpty() || stack.getItem() == this.inventory.get(0).getItem();
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, Direction direction)
    {
        return index != 0;
    }

    public static boolean insertHook(GoldenHopperTileEntity hopper)
    {
        Direction direction = hopper.getBlockState().get(GoldenHopperBlock.FACING);
        double x = hopper.getXPos() + (double) direction.getXOffset();
        double y = hopper.getYPos() + (double) direction.getYOffset();
        double z = hopper.getZPos() + (double) direction.getZOffset();
        LazyOptional<Pair<IItemHandler, Object>> handler = VanillaInventoryCodeHooks.getItemHandler(hopper.getWorld(), x, y, z, direction);
        return handler.map(destinationResult ->
        {
            IItemHandler itemHandler = destinationResult.getKey();
            if(isFull(itemHandler))
            {
                return false;
            }

            Object destination = destinationResult.getValue();
            for(int i = 0; i < hopper.getSizeInventory(); ++i)
            {
                if(!hopper.getStackInSlot(i).isEmpty() && hopper.canExtractItem(i, hopper.getStackInSlot(i), direction))
                {
                    ItemStack originalSlotContents = hopper.getStackInSlot(i).copy();
                    ItemStack insertStack = hopper.decrStackSize(i, 1);
                    ItemStack remainder = putStackInInventoryAllSlots(hopper, (IInventory) destination, insertStack, direction.getOpposite());
                    if(remainder.isEmpty())
                    {
                        return true;
                    }
                    hopper.setInventorySlotContents(i, originalSlotContents);
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
}
