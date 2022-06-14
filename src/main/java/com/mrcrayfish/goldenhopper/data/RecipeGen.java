package com.mrcrayfish.goldenhopper.data;

import com.mrcrayfish.goldenhopper.init.ModBlocks;
import com.mrcrayfish.goldenhopper.init.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class RecipeGen extends RecipeProvider
{
    public RecipeGen(DataGenerator generator)
    {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer)
    {
        ShapedRecipeBuilder.shaped(ModBlocks.GOLDEN_HOPPER.get())
                .pattern("ICI")
                .pattern("IHI")
                .pattern("RIR")
                .define('I', Tags.Items.INGOTS_GOLD)
                .define('R', Items.REDSTONE)
                .define('H', Items.HOPPER)
                .define('C', Items.COMPARATOR)
                .unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
                .unlockedBy("has_redstone", has(Items.REDSTONE))
                .unlockedBy("has_comparator", has(Items.COMPARATOR))
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.GOLDEN_HOPPER_MINECART.get())
                .pattern("A")
                .pattern("B")
                .define('A', ModBlocks.GOLDEN_HOPPER.get())
                .define('B', Items.MINECART)
                .unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
                .unlockedBy("has_minecart", has(Items.MINECART))
                .save(consumer);
    }
}