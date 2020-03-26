package com.mrcrayfish.goldenhopper.init;

import com.mrcrayfish.goldenhopper.Reference;
import com.mrcrayfish.goldenhopper.block.GoldenHopperBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class ModBlocks
{
    public static final DeferredRegister<Block> REGISTER = new DeferredRegister<>(ForgeRegistries.BLOCKS, Reference.MOD_ID);

    public static final RegistryObject<Block> GOLDEN_HOPPER = register("golden_hopper", new GoldenHopperBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(2.0F)));

    private static <T extends Block> RegistryObject<T> register(String id, T block)
    {
        return register(id, block, block1 -> new BlockItem(block1, new Item.Properties().group(ItemGroup.REDSTONE)));
    }

    private static <T extends Block> RegistryObject<T> register(String id, T block, @Nullable Function<T, BlockItem> supplier)
    {
        if(supplier != null)
        {
            ModItems.REGISTER.register(id, () -> supplier.apply(block));
        }
        return ModBlocks.REGISTER.register(id, () -> block);
    }
}
