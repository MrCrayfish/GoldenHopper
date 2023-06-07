package com.mrcrayfish.goldenhopper.world.entity;

import com.mrcrayfish.goldenhopper.Constants;
import com.mrcrayfish.goldenhopper.util.StorageHelper;
import com.mrcrayfish.goldenhopper.world.level.block.entity.AbstractHopperBlockEntity;
import com.mrcrayfish.goldenhopper.world.level.block.entity.GoldenHopperBlockEntity;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
@SuppressWarnings("UnstableApiUsage")
public class FabricGoldenHopperBlockEntity extends GoldenHopperBlockEntity
{
    private BlockApiCache<Storage<ItemVariant>, Direction> cache;

    public FabricGoldenHopperBlockEntity(BlockPos pos, BlockState state)
    {
        super(pos, state);
    }

    private BlockApiCache<Storage<ItemVariant>, Direction> getCache(ServerLevel level, BlockState state)
    {
        if(this.cache == null)
        {
            Direction direction = state.getValue(BlockStateProperties.FACING_HOPPER);
            double x = this.getLevelX() + direction.getStepX();
            double y = this.getLevelY() + direction.getStepY();
            double z = this.getLevelZ() + direction.getStepZ();
            BlockPos pos = BlockPos.containing(x, y, z);
            this.cache = BlockApiCache.create(ItemStorage.SIDED, level, pos);
        }
        return this.cache;
    }

    @Override
    protected boolean pushItems(Level level, BlockPos pos, BlockState state)
    {
        return this.pushItemsToStorage(level, state) || super.pushItems(level, pos, state);
    }

    private boolean pushItemsToStorage(Level level, BlockState state)
    {
        return this.getFacingStorage((ServerLevel) level, state).map(pair ->
        {
            InventoryStorage storage = pair.getRight();
            if(!storage.supportsInsertion())
                return false;

            if(StorageHelper.isFull(storage))
                return false;

            for(int index : this.getTransferableSlots())
            {
                ItemStack stack = this.getItem(index);
                if(stack.isEmpty())
                    continue;

                ItemStack copyStack = stack.copy();
                ItemStack insertStack = this.removeItem(index, 1);
                ItemStack resultStack = this.attemptMoveStackToStorage(this, pair.getLeft(), storage, insertStack);
                if(resultStack.isEmpty())
                    return true;

                this.setItem(index, copyStack);
            }
            return false;
        }).orElse(false);
    }

    private ItemStack attemptMoveStackToStorage(AbstractHopperBlockEntity sourceHopper, Container target, InventoryStorage storage, ItemStack stack)
    {
        try(Transaction transaction = Transaction.openOuter())
        {
            int remaining = stack.getCount();
            boolean storageWasEmpty = StorageHelper.isEmpty(storage);
            List<SingleSlotStorage<ItemVariant>> slots = storage.getSlots();
            for(int i = 0; i < slots.size() && remaining > 0; i++)
            {
                SingleSlotStorage<ItemVariant> slot = slots.get(i);
                try(Transaction nested = Transaction.openNested(transaction))
                {
                    ItemVariant resource = ItemVariant.of(stack);
                    long inserted = slot.insert(resource, remaining, nested);
                    if(inserted > 0)
                    {
                        remaining -= inserted;
                        nested.commit();
                    }
                }
            }
            if(remaining == 0)
            {
                transaction.commit();
                if(storageWasEmpty)
                {
                    AbstractHopperBlockEntity.applyTransferCooldown(sourceHopper, target);
                }
                return ItemStack.EMPTY;
            }
            transaction.abort();
        }
        return stack;
    }

    private Optional<Pair<Container, InventoryStorage>> getFacingStorage(ServerLevel level, BlockState state)
    {
        Direction direction = state.getValue(BlockStateProperties.FACING_HOPPER);
        BlockApiCache<Storage<ItemVariant>, Direction> cache = this.getCache(level, state);
        Storage<ItemVariant> storage = cache.find(state, direction.getOpposite());
        BlockEntity entity = cache.getBlockEntity();
        if(storage instanceof InventoryStorage && entity instanceof Container)
        {
            return Optional.of(Pair.of((Container) entity, (InventoryStorage) storage));
        }
        return Optional.empty();
    }
}
