package com.mrcrayfish.goldenhopper.world.level.item;

import com.mrcrayfish.goldenhopper.platform.Services;
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
            Level level = source.getLevel();
            Direction facing = source.getBlockState().getValue(DispenserBlock.FACING);
            BlockPos railPos = source.getPos().relative(facing);
            BlockState railState = level.getBlockState(railPos);
            RailShape railShape = this.getRailShape(railState, level, railPos);
            double yOffset = railShape.isAscending() ? 0.6 : 0.1;
            if(!railState.is(BlockTags.RAILS))
            {
                if(!railState.isAir() || !level.getBlockState(railPos.below()).is(BlockTags.RAILS))
                {
                    return this.behaviourDefaultDispenseItem.dispense(source, stack);
                }
                railState = level.getBlockState(railPos.below());
                railShape = this.getRailShape(railState, level, railPos.below());
                yOffset = facing != Direction.DOWN && railShape.isAscending() ? -0.4 : -0.9;
            }

            double posX = source.x() + facing.getStepX() * 1.125;
            double posY = Math.floor(source.y()) + facing.getStepY();
            double posZ = source.z() + facing.getStepZ() * 1.125;

            AbstractMinecart minecart = Services.PLATFORM.createGoldenHopperMinecart(level, posX, posY + yOffset, posZ);
            if(stack.hasCustomHoverName())
            {
                minecart.setCustomName(stack.getHoverName());
            }

            level.addFreshEntity(minecart);
            stack.shrink(1);
            return stack;
        }

        private RailShape getRailShape(BlockState state, Level level, BlockPos pos)
        {
            if(state.getBlock() instanceof BaseRailBlock railBlock)
            {
                return Services.PLATFORM.getRailDirection(railBlock, state, level, pos);
            }
            return RailShape.NORTH_SOUTH;
        }

        @Override
        protected void playSound(BlockSource source)
        {
            source.getLevel().levelEvent(1000, source.getPos(), 0);
        }
    };

    public GoldenHopperMinecartItem(Properties builder)
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
            RailShape shape = state.getBlock() instanceof BaseRailBlock railBlock ? Services.PLATFORM.getRailDirection(railBlock, state, level, pos) : RailShape.NORTH_SOUTH;
            double yOffset = shape.isAscending() ? 0.5 : 0.0;
            AbstractMinecart minecart = Services.PLATFORM.createGoldenHopperMinecart(level, pos.getX() + 0.5, pos.getY() + 0.0625 + yOffset, pos.getZ() + 0.5);
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
