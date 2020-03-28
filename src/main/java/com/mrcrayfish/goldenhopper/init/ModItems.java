package com.mrcrayfish.goldenhopper.init;

import com.mrcrayfish.goldenhopper.Reference;
import com.mrcrayfish.goldenhopper.item.GoldenHopperMinecartItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Author: MrCrayfish
 */
public class ModItems
{
    public static final DeferredRegister<Item> REGISTER = new DeferredRegister<>(ForgeRegistries.ITEMS, Reference.MOD_ID);

    public static final RegistryObject<Item> GOLDEN_HOPPER_MINECART = REGISTER.register("golden_hopper_minecart", () -> new GoldenHopperMinecartItem(new Item.Properties().group(ItemGroup.TRANSPORTATION).maxStackSize(1)));
}
