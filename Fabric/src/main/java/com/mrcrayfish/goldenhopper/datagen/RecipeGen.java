package com.mrcrayfish.goldenhopper.datagen;

import com.mrcrayfish.goldenhopper.core.ModBlocks;
import com.mrcrayfish.goldenhopper.core.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class RecipeGen extends FabricRecipeProvider
{
    public RecipeGen(FabricDataOutput output)
    {
        super(output);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> exporter)
    {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModBlocks.GOLDEN_HOPPER.get())
                .pattern("ICI")
                .pattern("IHI")
                .pattern("RIR")
                .define('I', Items.GOLD_INGOT)
                .define('R', Items.REDSTONE)
                .define('H', Items.HOPPER)
                .define('C', Items.COMPARATOR)
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .unlockedBy("has_redstone", has(Items.REDSTONE))
                .unlockedBy("has_comparator", has(Items.COMPARATOR))
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ModItems.GOLDEN_HOPPER_MINECART.get())
                .pattern("A")
                .pattern("B")
                .define('A', ModBlocks.GOLDEN_HOPPER.get())
                .define('B', Items.MINECART)
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .unlockedBy("has_minecart", has(Items.MINECART))
                .save(exporter);
    }
}
