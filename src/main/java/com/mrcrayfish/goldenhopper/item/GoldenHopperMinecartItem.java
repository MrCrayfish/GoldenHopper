package com.mrcrayfish.goldenhopper.item;

import com.mrcrayfish.goldenhopper.entity.GoldenHopperMinecart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;

/**
 * Author: MrCrayfish
 */
public class GoldenHopperMinecartItem extends Item
{
    private static final DispenseItemBehavior MINECART_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior()
    {
        private final DefaultDispenseItemBehavior behaviourDefaultDispenseItem = new DefaultDispenseItemBehavior();

        @Override
        public ItemStack execute(BlockSource source, ItemStack stack)
        {
            Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
            Level level = source.getLevel();
            double posX = source.x() + (double) direction.getStepX() * 1.125D;
            double posY = Math.floor(source.y()) + (double) direction.getStepY();
            double posZ = source.z() + (double) direction.getStepZ() * 1.125D;
            BlockPos adjacentPos = source.getPos().relative(direction);
            BlockState adjacentState = level.getBlockState(adjacentPos);
            RailShape adjacentShape = adjacentState.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock) adjacentState.getBlock()).getRailDirection(adjacentState, level, adjacentPos, null) : RailShape.NORTH_SOUTH;
            double yOffset;
            if(adjacentState.is(BlockTags.RAILS))
            {
                if(adjacentShape.isAscending())
                {
                    yOffset = 0.6D;
                }
                else
                {
                    yOffset = 0.1D;
                }
            }
            else
            {
                if(!adjacentState.isAir() || !level.getBlockState(adjacentPos.below()).is(BlockTags.RAILS))
                {
                    return this.behaviourDefaultDispenseItem.dispense(source, stack);
                }

                BlockState state = level.getBlockState(adjacentPos.below());
                RailShape shape = state.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock) state.getBlock()).getRailDirection(state, level, adjacentPos.below(), null) : RailShape.NORTH_SOUTH;
                if(direction != Direction.DOWN && shape.isAscending())
                {
                    yOffset = -0.4D;
                }
                else
                {
                    yOffset = -0.9D;
                }
            }

            AbstractMinecart minecart = new GoldenHopperMinecart(level, posX, posY + yOffset, posZ);
            if(stack.hasCustomHoverName())
            {
                minecart.setCustomName(stack.getHoverName());
            }

            level.addFreshEntity(minecart);
            stack.shrink(1);
            return stack;
        }

        @Override
        protected void playSound(BlockSource source)
        {
            source.getLevel().levelEvent(1000, source.getPos(), 0);
        }
    };

    public GoldenHopperMinecartItem(Item.Properties builder)
    {
        super(builder);
        DispenserBlock.registerBehavior(this, MINECART_DISPENSER_BEHAVIOR);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if(!state.is(BlockTags.RAILS))
        {
            return InteractionResult.FAIL;
        }

        ItemStack stack = context.getItemInHand();
        if(!level.isClientSide)
        {
            RailShape shape = state.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock) state.getBlock()).getRailDirection(state, level, pos, null) : RailShape.NORTH_SOUTH;
            double yOffset = 0.0D;
            if(shape.isAscending())
            {
                yOffset = 0.5D;
            }
            AbstractMinecart minecart = new GoldenHopperMinecart(level, pos.getX() + 0.5, pos.getY() + 0.0625 + yOffset, pos.getZ() + 0.5);
            if(stack.hasCustomHoverName())
            {
                minecart.setCustomName(stack.getHoverName());
            }
            level.addFreshEntity(minecart);
        }
        stack.shrink(1);
        return InteractionResult.SUCCESS;
    }
}
