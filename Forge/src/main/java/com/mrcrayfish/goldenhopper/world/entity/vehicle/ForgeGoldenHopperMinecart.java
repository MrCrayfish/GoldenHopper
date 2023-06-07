package com.mrcrayfish.goldenhopper.world.entity.vehicle;

import com.mrcrayfish.goldenhopper.core.ModItems;
import com.mrcrayfish.goldenhopper.world.level.block.entity.AbstractHopperBlockEntity;
import com.mrcrayfish.goldenhopper.world.level.block.entity.ForgeGoldenHopperBlockEntity;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Author: MrCrayfish
 */
public class ForgeGoldenHopperMinecart extends GoldenHopperMinecart
{
    public ForgeGoldenHopperMinecart(EntityType<?> type, Level level)
    {
        super(type, level);
    }

    public ForgeGoldenHopperMinecart(Level level, double x, double y, double z)
    {
        super(level, x, y, z);
    }

    @Override
    protected Item getDropItem()
    {
        return ModItems.GOLDEN_HOPPER_MINECART.get();
    }

    @Override
    public Packet<?> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public int getComparatorLevel()
    {
        float filled = IntStream.range(1, this.getContainerSize())
                .mapToObj(this::getItem)
                .filter(stack -> !stack.isEmpty())
                .map(stack -> stack.getCount() / (float) Math.min(this.getMaxStackSize(), stack.getMaxStackSize()))
                .reduce(0F, Float::sum);
        filled /= (this.getContainerSize() - 1.0F);
        return Mth.floor(filled * 14.0F) + (filled > 0 ? 1 : 0);
    }
}
