package com.mrcrayfish.goldenhopper.mixin;

import com.mrcrayfish.goldenhopper.entity.vehicle.GoldenHopperMinecart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

/**
 * Author: MrCrayfish
 */
@Mixin(DetectorRailBlock.class)
public abstract class DetectorRailBlockMixin
{
    @Shadow
    protected abstract <T extends AbstractMinecart> List<T> getInteractingMinecartOfType(Level level, BlockPos pos, Class<T> minecartClass, Predicate<Entity> predicate);

    @Inject(method = "getAnalogOutputSignal", at = @At(value = "HEAD"), cancellable = true)
    private void goldenhopperGetAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir)
    {
        if(state.getValue(DetectorRailBlock.POWERED))
        {
            List<AbstractMinecart> carts = this.getInteractingMinecartOfType(level, pos, AbstractMinecart.class, EntitySelector.CONTAINER_ENTITY_SELECTOR);
            if(!carts.isEmpty() && carts.get(0) instanceof GoldenHopperMinecart hopper)
            {
                cir.setReturnValue(hopper.getComparatorLevel());
            }
        }
    }
}
