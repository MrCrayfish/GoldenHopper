package com.mrcrayfish.goldenhopper.init;

import com.mrcrayfish.goldenhopper.Reference;
import com.mrcrayfish.goldenhopper.world.level.block.GoldenHopperBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class ModBlocks
{
    public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MOD_ID);

    public static final RegistryObject<Block> GOLDEN_HOPPER = register("golden_hopper", new GoldenHopperBlock(Block.Properties.of(Material.METAL).strength(2.0F)));

    private static <T extends Block> RegistryObject<T> register(String id, T block)
    {
        return register(id, block, block1 -> new BlockItem(block1, new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE)));
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
