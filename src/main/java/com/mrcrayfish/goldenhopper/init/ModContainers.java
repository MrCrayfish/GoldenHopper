package com.mrcrayfish.goldenhopper.init;

import com.mrcrayfish.goldenhopper.Reference;
import com.mrcrayfish.goldenhopper.world.inventory.GoldenHopperMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Author: MrCrayfish
 */
public class ModContainers
{
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.CONTAINERS, Reference.MOD_ID);

    public static final RegistryObject<MenuType<GoldenHopperMenu>> GOLDEN_HOPPER = register("golden_hopper", GoldenHopperMenu::new);

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String id, MenuType.MenuSupplier<T> factory)
    {
        return REGISTER.register(id, () -> new MenuType<>(factory));
    }

}
