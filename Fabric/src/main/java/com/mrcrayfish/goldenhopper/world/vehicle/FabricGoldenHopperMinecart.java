package com.mrcrayfish.goldenhopper.world.vehicle;

import com.mrcrayfish.goldenhopper.core.ModItems;
import com.mrcrayfish.goldenhopper.world.entity.vehicle.GoldenHopperMinecart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.stream.IntStream;

/**
 * Author: MrCrayfish
 */
public class FabricGoldenHopperMinecart extends GoldenHopperMinecart
{
    public FabricGoldenHopperMinecart(EntityType<?> type, Level level)
    {
        super(type, level);
    }

    public FabricGoldenHopperMinecart(Level level, double x, double y, double z)
    {
        super(level, x, y, z);
    }

    @Override
    public Item getDropItem()
    {
        return ModItems.GOLDEN_HOPPER_MINECART.get();
    }

    /*@Override
    public Packet<ClientGamePacketListener> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }*/

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
