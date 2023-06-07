package com.mrcrayfish.goldenhopper.world.level.block.entity;

import com.mrcrayfish.goldenhopper.items.wrapper.GoldenHopperItemHandler;
import com.mrcrayfish.goldenhopper.util.ItemHandlerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.VanillaInventoryCodeHooks;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class ForgeGoldenHopperBlockEntity extends GoldenHopperBlockEntity
{
    public ForgeGoldenHopperBlockEntity(BlockPos pos, BlockState state)
    {
        super(pos, state);
    }

    @Override
    protected IItemHandler createUnSidedHandler()
    {
        return new GoldenHopperItemHandler(this);
    }

    @Override
    protected boolean pushItems(Level level, BlockPos pos, BlockState state)
    {
        return this.pushItemsToHandler(level, state) || super.pushItems(level, pos, state);
    }

    private boolean pushItemsToHandler(Level level, BlockState state)
    {
        return this.getFacingItemHandler(level, state).map(pair ->
        {
            IItemHandler handler = pair.getKey();
            if(ItemHandlerHelper.isFull(handler))
                return false;

            Object value = pair.getValue();
            for(int index : this.getTransferableSlots())
            {
                ItemStack stack = this.getItem(index);
                if(stack.isEmpty())
                    continue;

                ItemStack copyStack = stack.copy();
                ItemStack insertStack = this.removeItem(index, 1);
                ItemStack resultStack = this.attemptMoveStackToHandler(this, value, handler, insertStack);
                if(resultStack.isEmpty())
                {
                    return true;
                }
                this.setItem(index, copyStack);
            }
            return false;
        }).orElse(false);
    }

    private ItemStack attemptMoveStackToHandler(AbstractHopperBlockEntity sourceHopper, Object target, IItemHandler handler, ItemStack stack)
    {
        for(int slotIndex = 0; slotIndex < handler.getSlots() && !stack.isEmpty(); slotIndex++)
        {
            stack = this.insertStackToHandler(sourceHopper, target, handler, stack, slotIndex);
        }
        return stack;
    }

    private ItemStack insertStackToHandler(AbstractHopperBlockEntity sourceHopper, Object target, IItemHandler handler, ItemStack stack, int slotIndex)
    {
        if(handler.insertItem(slotIndex, stack, true).isEmpty())
        {
            boolean movedItem = false;
            boolean targetWasEmpty = ItemHandlerHelper.isEmpty(handler);
            ItemStack targetStack = handler.getStackInSlot(slotIndex);
            if(targetStack.isEmpty())
            {
                handler.insertItem(slotIndex, stack, false);
                stack = ItemStack.EMPTY;
                movedItem = true;
            }
            else if(net.minecraftforge.items.ItemHandlerHelper.canItemStacksStack(targetStack, stack))
            {
                int originalSize = stack.getCount();
                stack = handler.insertItem(slotIndex, stack, false);
                movedItem = originalSize < stack.getCount();
            }
            if(movedItem)
            {
                if(targetWasEmpty)
                {
                    if(target instanceof HopperBlockEntity targetHopper && !targetHopper.isOnCustomCooldown())
                    {
                        int cooldown = targetHopper.getLastUpdateTime() >= sourceHopper.getLastUpdateTime() ? 1 : 0;
                        targetHopper.setCooldown(HopperBlockEntity.MOVE_ITEM_SPEED - cooldown);
                    }
                    else if(target instanceof AbstractHopperBlockEntity targetHopper && !targetHopper.isCustomCooldown())
                    {
                        int cooldown = targetHopper.getLastUpdateTime() >= sourceHopper.getLastUpdateTime() ? 1 : 0;
                        targetHopper.setTransferCooldown(targetHopper.transferSpeed - cooldown);
                    }
                }
            }
        }
        return stack;
    }

    private Optional<Pair<IItemHandler, Object>> getFacingItemHandler(Level level, BlockState state)
    {
        Direction direction = state.getValue(BlockStateProperties.FACING_HOPPER);
        double x = this.getLevelX() + direction.getStepX();
        double y = this.getLevelY() + direction.getStepY();
        double z = this.getLevelZ() + direction.getStepZ();
        return VanillaInventoryCodeHooks.getItemHandler(level, x, y, z, direction.getOpposite());
    }
}
