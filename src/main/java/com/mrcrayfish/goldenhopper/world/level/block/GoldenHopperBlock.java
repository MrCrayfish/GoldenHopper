package com.mrcrayfish.goldenhopper.world.level.block;

import com.mrcrayfish.goldenhopper.init.ModBlockEntities;
import com.mrcrayfish.goldenhopper.world.level.block.entity.AbstractHopperBlockEntity;
import com.mrcrayfish.goldenhopper.world.level.block.entity.GoldenHopperBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class GoldenHopperBlock extends AbstractHopperBlock
{
    public GoldenHopperBlock(Block.Properties properties)
    {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new GoldenHopperBlockEntity(pos, state);
    }

    @Override
    protected BlockEntityType<? extends AbstractHopperBlockEntity> getBlockEntityType()
    {
        return ModBlockEntities.GOLDEN_HOPPER.get();
    }
}
