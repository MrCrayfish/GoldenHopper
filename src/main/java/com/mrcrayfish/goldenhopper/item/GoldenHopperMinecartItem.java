package com.mrcrayfish.goldenhopper.item;

import com.mrcrayfish.goldenhopper.entity.GoldenHopperMinecart;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.MinecartItem;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class GoldenHopperMinecartItem extends Item
{
    private static final IDispenseItemBehavior MINECART_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior()
    {
        private final DefaultDispenseItemBehavior behaviourDefaultDispenseItem = new DefaultDispenseItemBehavior();

        @Override
        public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
        {
            Direction direction = source.getBlockState().get(DispenserBlock.FACING);
            World world = source.getWorld();
            double posX = source.getX() + (double) direction.getXOffset() * 1.125D;
            double posY = Math.floor(source.getY()) + (double) direction.getYOffset();
            double posZ = source.getZ() + (double) direction.getZOffset() * 1.125D;
            BlockPos adjacentPos = source.getBlockPos().offset(direction);
            BlockState adjacentState = world.getBlockState(adjacentPos);
            RailShape adjacentShape = adjacentState.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock) adjacentState.getBlock()).getRailDirection(adjacentState, world, adjacentPos, null) : RailShape.NORTH_SOUTH;
            double yOffset;
            // TODO: MCP-name: func_235714_a_ -> isIn
            if(adjacentState.func_235714_a_(BlockTags.RAILS))
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
                // TODO: MCP-name: func_235714_a_ -> isIn
                if(!adjacentState.isAir(world, adjacentPos) || !world.getBlockState(adjacentPos.down()).func_235714_a_(BlockTags.RAILS))
                {
                    return this.behaviourDefaultDispenseItem.dispense(source, stack);
                }

                BlockState state = world.getBlockState(adjacentPos.down());
                RailShape shape = state.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock) state.getBlock()).getRailDirection(state, world, adjacentPos.down(), null) : RailShape.NORTH_SOUTH;
                if(direction != Direction.DOWN && shape.isAscending())
                {
                    yOffset = -0.4D;
                }
                else
                {
                    yOffset = -0.9D;
                }
            }

            AbstractMinecartEntity minecart = new GoldenHopperMinecart(world, posX, posY + yOffset, posZ);
            if(stack.hasDisplayName())
            {
                minecart.setCustomName(stack.getDisplayName());
            }

            world.addEntity(minecart);
            stack.shrink(1);
            return stack;
        }

        @Override
        protected void playDispenseSound(IBlockSource source)
        {
            source.getWorld().playEvent(1000, source.getBlockPos(), 0);
        }
    };

    public GoldenHopperMinecartItem(Item.Properties builder)
    {
        super(builder);
        DispenserBlock.registerDispenseBehavior(this, MINECART_DISPENSER_BEHAVIOR);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        BlockState state = world.getBlockState(pos);
        // TODO: MCP-name: func_235714_a_ -> isIn
        if(!state.func_235714_a_(BlockTags.RAILS))
        {
            return ActionResultType.FAIL;
        }

        ItemStack stack = context.getItem();
        if(!world.isRemote)
        {
            RailShape shape = state.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock) state.getBlock()).getRailDirection(state, world, pos, null) : RailShape.NORTH_SOUTH;
            double yOffset = 0.0D;
            if(shape.isAscending())
            {
                yOffset = 0.5D;
            }
            AbstractMinecartEntity minecart = new GoldenHopperMinecart(world, pos.getX() + 0.5, pos.getY() + 0.0625 + yOffset, pos.getZ() + 0.5);
            if(stack.hasDisplayName())
            {
                minecart.setCustomName(stack.getDisplayName());
            }
            world.addEntity(minecart);
        }
        stack.shrink(1);
        return ActionResultType.SUCCESS;
    }
}
