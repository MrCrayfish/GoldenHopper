package com.mrcrayfish.goldenhopper.platform;

import com.mrcrayfish.goldenhopper.platform.services.IPlatformHelper;
import com.mrcrayfish.goldenhopper.world.entity.vehicle.ForgeGoldenHopperMinecart;
import com.mrcrayfish.goldenhopper.world.entity.vehicle.GoldenHopperMinecart;
import com.mrcrayfish.goldenhopper.world.level.block.entity.ForgeGoldenHopperBlockEntity;
import com.mrcrayfish.goldenhopper.world.level.block.entity.GoldenHopperBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraftforge.network.NetworkHooks;

/**
 * Author: MrCrayfish
 */
public class ForgePlatformHelper implements IPlatformHelper
{
    @Override
    public boolean isHopperOnCustomCooldown(HopperBlockEntity hopper)
    {
        return hopper.isOnCustomCooldown();
    }

    @Override
    public long getHopperLastGameTime(HopperBlockEntity hopper)
    {
        return hopper.getLastUpdateTime();
    }

    @Override
    public void setHopperCooldown(HopperBlockEntity hopper, int time)
    {
        hopper.setCooldown(time);
    }

    @Override
    public GoldenHopperMinecart createGoldenHopperMinecart(EntityType<GoldenHopperMinecart> entityType, Level level)
    {
        return new ForgeGoldenHopperMinecart(entityType, level);
    }

    @Override
    public GoldenHopperMinecart createGoldenHopperMinecart(Level level, double posX, double posY, double posZ)
    {
        return new ForgeGoldenHopperMinecart(level, posX, posY, posZ);
    }

    @Override
    public GoldenHopperBlockEntity createGoldenHopperBlockEntity(BlockPos pos, BlockState state)
    {
        return new ForgeGoldenHopperBlockEntity(pos, state);
    }

    @Override
    public void openScreen(ServerPlayer player, MenuProvider provider)
    {
        NetworkHooks.openScreen(player, provider);
    }

    @Override
    public RailShape getRailDirection(BaseRailBlock block, BlockState state, Level level, BlockPos pos)
    {
        return block.getRailDirection(state, level, pos, null);
    }

    @Override
    public boolean isFullContainer(Container container, Direction direction)
    {
        return HopperBlockEntity.isFullContainer(container, direction);
    }
}
