package com.mrcrayfish.goldenhopper.platform;

import com.mrcrayfish.goldenhopper.platform.services.IPlatformHelper;
import com.mrcrayfish.goldenhopper.world.entity.FabricGoldenHopperBlockEntity;
import com.mrcrayfish.goldenhopper.world.entity.vehicle.GoldenHopperMinecart;
import com.mrcrayfish.goldenhopper.world.level.block.entity.GoldenHopperBlockEntity;
import com.mrcrayfish.goldenhopper.world.vehicle.FabricGoldenHopperMinecart;
import net.fabricmc.loader.api.FabricLoader;
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

public class FabricPlatformHelper implements IPlatformHelper
{
    @Override
    public GoldenHopperMinecart createGoldenHopperMinecart(EntityType<GoldenHopperMinecart> entityType, Level level)
    {
        return new FabricGoldenHopperMinecart(entityType, level);
    }

    @Override
    public GoldenHopperMinecart createGoldenHopperMinecart(Level level, double posX, double posY, double posZ)
    {
        return new FabricGoldenHopperMinecart(level, posX, posY, posZ);
    }

    @Override
    public GoldenHopperBlockEntity createGoldenHopperBlockEntity(BlockPos pos, BlockState state)
    {
        return new FabricGoldenHopperBlockEntity(pos, state);
    }

    @Override
    public boolean isHopperOnCustomCooldown(HopperBlockEntity hopper)
    {
        return hopper.isOnCustomCooldown();
    }

    @Override
    public long getHopperLastGameTime(HopperBlockEntity hopper)
    {
        return hopper.tickedGameTime;
    }

    @Override
    public void setHopperCooldown(HopperBlockEntity hopper, int time)
    {
        hopper.setCooldown(time);
    }

    @Override
    public void openScreen(ServerPlayer player, MenuProvider provider)
    {
        player.openMenu(provider);
    }

    @Override
    public RailShape getRailDirection(BaseRailBlock block, BlockState state, Level level, BlockPos pos)
    {
        return state.getValue(block.getShapeProperty());
    }

    @Override
    public boolean isFullContainer(Container container, Direction direction)
    {
        return HopperBlockEntity.isFullContainer(container, direction);
    }
}
