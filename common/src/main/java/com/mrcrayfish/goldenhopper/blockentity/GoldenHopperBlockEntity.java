package com.mrcrayfish.goldenhopper.blockentity;

import com.mrcrayfish.goldenhopper.core.ModBlockEntities;
import com.mrcrayfish.goldenhopper.inventory.GoldenHopperMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Author: MrCrayfish
 */
public class GoldenHopperBlockEntity extends HopperBlockEntity implements WorldlyContainer
{
    public static final int CONTAINER_SIZE = 6;
    public static final int FILTER_SLOT_INDEX = 0;
    public static final int[] TRANSFERABLE_SLOTS = IntStream.range(1, CONTAINER_SIZE).toArray();
    public static boolean ejecting = false;

    public GoldenHopperBlockEntity(BlockPos pos, BlockState state)
    {
        super(pos, state);
        this.setItems(NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY));
        ((BlockEntityTypeSetter) this).goldenHopper$SetType(ModBlockEntities.GOLDEN_HOPPER.get());
    }

    @Override
    public BlockEntityType<?> getType()
    {
        return ModBlockEntities.GOLDEN_HOPPER.get();
    }

    @Override
    protected Component getDefaultName()
    {
        return Component.translatable("container.goldenhopper.golden_hopper");
    }

    @Override
    protected AbstractContainerMenu createMenu(int windowId, Inventory playerInventory)
    {
        return new GoldenHopperMenu(windowId, playerInventory, this);
    }

    @Override
    public ItemStack getItem(int slot)
    {
        if(ejecting && slot == FILTER_SLOT_INDEX)
        {
            return ItemStack.EMPTY;
        }
        return super.getItem(slot);
    }

    @Override
    public int[] getSlotsForFace(Direction side)
    {
        return TRANSFERABLE_SLOTS;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack)
    {
        if(index != FILTER_SLOT_INDEX)
        {
            ItemStack filter = this.getItems().get(FILTER_SLOT_INDEX);
            if(filter.isEmpty())
                return true;

            if(!stack.is(filter.getItem()))
                return false;

            // Potion contents must match if filter has potion contents
            if(filter.has(DataComponents.POTION_CONTENTS))
            {
                if(!stack.has(DataComponents.POTION_CONTENTS))
                    return false;

                PotionContents first = filter.get(DataComponents.POTION_CONTENTS);
                PotionContents second = stack.get(DataComponents.POTION_CONTENTS);
                if(first != null && second != null && !comparePotions(first, second))
                    return false;
            }

            // Item enchantments must match if filter has potion contents
            if(filter.has(DataComponents.STORED_ENCHANTMENTS))
            {
                if(!stack.has(DataComponents.STORED_ENCHANTMENTS))
                    return false;

                ItemEnchantments first = filter.get(DataComponents.STORED_ENCHANTMENTS);
                ItemEnchantments second = stack.get(DataComponents.STORED_ENCHANTMENTS);
                if(first != null && second != null && !compareEnchantments(first, second))
                    return false;
            }
        }
        return true;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction)
    {
        return this.getItems().get(FILTER_SLOT_INDEX).isEmpty() || stack.getItem() == this.getItems().get(FILTER_SLOT_INDEX).getItem();
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

    private static boolean comparePotions(PotionContents first, PotionContents second)
    {
        // Compare the potion value
        Optional<Holder<Potion>> firstOptional = first.potion();
        if(firstOptional.isPresent())
        {
            Optional<Holder<Potion>> secondOptional = second.potion();
            if(secondOptional.isEmpty())
                return false;

            Potion firstPotion = firstOptional.get().value();
            Potion secondPotion = secondOptional.get().value();
            if(firstPotion != secondPotion)
                return false;
        }

        // The matching stack must have at least the base effects of the filter.
        for(var instance : first.customEffects())
        {
            if(second.customEffects().stream().noneMatch(effect -> effect.getEffect().equals(instance.getEffect())))
            {
                return false;
            }
        }
        return true;
    }

    private static boolean compareEnchantments(ItemEnchantments first, ItemEnchantments second)
    {
        for(var holder : first.keySet())
        {
            if(second.getLevel(holder) <= 0)
            {
                return false;
            }
        }
        return true;
    }
}
