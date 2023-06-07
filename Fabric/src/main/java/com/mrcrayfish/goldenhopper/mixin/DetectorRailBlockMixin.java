package com.mrcrayfish.goldenhopper.mixin;

import com.mrcrayfish.goldenhopper.world.vehicle.FabricGoldenHopperMinecart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

/**
 * Author: MrCrayfish
 */
@Mixin(DetectorRailBlock.class)
public class DetectorRailBlockMixin
{
    @Inject(method = "getAnalogOutputSignal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;getRedstoneSignalFromContainer(Lnet/minecraft/world/Container;)I"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void goldenhopperGetAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos, CallbackInfoReturnable<Integer> cir, List list, List<AbstractMinecart> minecarts)
    {
        if(!minecarts.isEmpty() && minecarts.get(0) instanceof FabricGoldenHopperMinecart hopper)
        {
            cir.setReturnValue(hopper.getComparatorLevel());
        }
    }
}
