package com.mrcrayfish.goldenhopper.entity;

import com.mrcrayfish.goldenhopper.init.ModBlocks;
import com.mrcrayfish.goldenhopper.init.ModEntities;
import com.mrcrayfish.goldenhopper.init.ModItems;
import com.mrcrayfish.goldenhopper.inventory.container.GoldenHopperContainer;
import com.mrcrayfish.goldenhopper.tileentity.GoldenHopperTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.minecart.ContainerMinecartEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.tileentity.IHopper;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Author: MrCrayfish
 */
public class GoldenHopperMinecart extends ContainerMinecartEntity implements IHopper, ISidedInventory
{
    private boolean blocked = true;
    private int transferTicker = -1;
    private final BlockPos lastPosition = BlockPos.ZERO;

    public GoldenHopperMinecart(World world)
    {
        super(ModEntities.GOLDEN_HOPPER_MINECART.get(), world);
    }

    public GoldenHopperMinecart(World world, double x, double y, double z)
    {
        super(ModEntities.GOLDEN_HOPPER_MINECART.get(), x, y, z, world);
    }

    public GoldenHopperMinecart(EntityType<?> type, World world)
    {
        super(type, world);
    }

    public boolean isBlocked()
    {
        return blocked;
    }

    public void setBlocked(boolean blocked)
    {
        this.blocked = blocked;
    }

    public int getTransferTicker()
    {
        return transferTicker;
    }

    public void setTransferTicker(int transferTicker)
    {
        this.transferTicker = transferTicker;
    }

    public BlockPos getLastPosition()
    {
        return lastPosition;
    }

    public boolean canTransfer()
    {
        return this.transferTicker > 0;
    }

    @Override
    protected Container createContainer(int windowId, PlayerInventory playerInventory)
    {
        return new GoldenHopperContainer(windowId, playerInventory, this);
    }

    @Override
    public BlockState getDefaultDisplayTile()
    {
        return ModBlocks.GOLDEN_HOPPER.get().getDefaultState();
    }

    @Override
    public int getDefaultDisplayTileOffset()
    {
        return 1;
    }

    @Override
    public ItemStack getCartItem()
    {
        return new ItemStack(ModItems.GOLDEN_HOPPER_MINECART.get());
    }

    @Override
    public Type getMinecartType()
    {
        return Type.HOPPER;
    }

    @Nullable
    @Override
    public World getWorld()
    {
        return this.world;
    }

    @Override
    public double getXPos()
    {
        return this.getPosX();
    }

    @Override
    public double getYPos()
    {
        return this.getPosY() + 0.5D;
    }

    @Override
    public double getZPos()
    {
        return this.getPosZ();
    }

    @Override
    public int getSizeInventory()
    {
        return 6;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return index != 0 && (this.getStackInSlot(0).isEmpty() || stack.getItem() == this.getStackInSlot(0).getItem());
    }

    @Override
    public int[] getSlotsForFace(Direction side)
    {
        return IntStream.range(1, this.getSizeInventory()).toArray();
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, @Nullable Direction direction)
    {
        return index != 0 && (this.getStackInSlot(0).isEmpty() || stack.getItem() == this.getStackInSlot(0).getItem());
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, Direction direction)
    {
        return index != 0;
    }

    @Override
    public void onActivatorRailPass(int x, int y, int z, boolean receivingPower)
    {
        if(receivingPower == this.isBlocked())
        {
            this.setBlocked(!receivingPower);
        }
    }

    @Override
    public void tick()
    {
        super.tick();
        if(!this.world.isRemote && this.isAlive() && this.isBlocked())
        {
            BlockPos pos = this.getPosition();
            if(pos.equals(this.lastPosition))
            {
                this.transferTicker--;
            }
            else
            {
                this.setTransferTicker(0);
            }

            if(!this.canTransfer())
            {
                this.setTransferTicker(0);
                if(this.captureDroppedItems())
                {
                    this.setTransferTicker(4);
                    this.markDirty();
                }
            }
        }

    }

    private boolean captureDroppedItems()
    {
        if(GoldenHopperTileEntity.pullItems(this))
        {
            return true;
        }
        List<ItemEntity> list = this.world.getEntitiesWithinAABB(ItemEntity.class, this.getBoundingBox().grow(0.25D, 0.0D, 0.25D), EntityPredicates.IS_ALIVE);
        if(!list.isEmpty())
        {
            GoldenHopperTileEntity.captureItemEntity(this, list.get(0));
        }
        return false;
    }

    @Override
    protected void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        compound.putInt("TransferCooldown", this.transferTicker);
        compound.putBoolean("Enabled", this.blocked);
    }

    @Override
    protected void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        this.transferTicker = compound.getInt("TransferCooldown");
        this.blocked = !compound.contains("Enabled") || compound.getBoolean("Enabled");
    }

    @Override
    public void killMinecart(DamageSource source)
    {
        super.killMinecart(source);
        if(this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS))
        {
            this.entityDropItem(ModBlocks.GOLDEN_HOPPER.get());
        }
    }

    @Override
    public IPacket<?> createSpawnPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
