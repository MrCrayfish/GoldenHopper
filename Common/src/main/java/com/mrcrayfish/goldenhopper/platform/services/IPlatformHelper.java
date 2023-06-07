package com.mrcrayfish.goldenhopper.platform.services;

import com.mrcrayfish.goldenhopper.world.entity.vehicle.GoldenHopperMinecart;
import com.mrcrayfish.goldenhopper.world.level.block.entity.GoldenHopperBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;

public interface IPlatformHelper
{
    GoldenHopperMinecart createGoldenHopperMinecart(EntityType<GoldenHopperMinecart> entityType, Level level);

    GoldenHopperMinecart createGoldenHopperMinecart(Level level, double posX, double posY, double posZ);

    GoldenHopperBlockEntity createGoldenHopperBlockEntity(BlockPos pos, BlockState state);

    boolean isHopperOnCustomCooldown(HopperBlockEntity hopper);

    long getHopperLastGameTime(HopperBlockEntity hopper);

    void setHopperCooldown(HopperBlockEntity hopper, int offset);

    void openScreen(ServerPlayer player, MenuProvider provider);

    RailShape getRailDirection(BaseRailBlock block, BlockState state, Level level, BlockPos pos);

    boolean isFullContainer(Container container, Direction direction);
}