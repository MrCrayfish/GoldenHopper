package com.mrcrayfish.goldenhopper.mixin;

import com.mrcrayfish.goldenhopper.world.level.block.entity.AbstractHopperBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Author: MrCrayfish
 */
@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin
{
    private static boolean goldenhopperWasEmpty;

    // Normally I wouldn't do this but the bytecode was different on Fabric.
    @Inject(method = "tryMoveInItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void goldenhopperTryMoveInItemCapture(Container source, Container target, ItemStack $$2, int slotIndex, Direction face, CallbackInfoReturnable<ItemStack> cir, ItemStack stack, boolean movedItem, boolean wasEmpty)
    {
        goldenhopperWasEmpty = wasEmpty;
    }

    @Inject(method = "tryMoveInItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setChanged()V"))
    private static void goldenhopperTryMoveInItem(Container source, Container target, ItemStack $$2, int slotIndex, Direction face, CallbackInfoReturnable<ItemStack> cir)
    {
        if(goldenhopperWasEmpty)
        {
            AbstractHopperBlockEntity.applyTransferCooldown(source, target);
        }
    }
}
