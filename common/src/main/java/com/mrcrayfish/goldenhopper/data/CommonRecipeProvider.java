package com.mrcrayfish.goldenhopper.data;

import com.mrcrayfish.goldenhopper.core.ModBlocks;
import com.mrcrayfish.goldenhopper.core.ModItems;
import com.mrcrayfish.goldenhopper.platform.Services;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

/**
 * Author: MrCrayfish
 */
public class CommonRecipeProvider extends RecipeProvider
{
    private final HolderLookup.RegistryLookup<Item> items;

    public CommonRecipeProvider(HolderLookup.Provider provider, RecipeOutput output)
    {
        super(provider, output);
        this.items = provider.lookupOrThrow(Registries.ITEM);
    }

    @Override
    public void buildRecipes()
    {
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.REDSTONE, ModBlocks.GOLDEN_HOPPER.get())
            .pattern("ICI")
            .pattern("IHI")
            .pattern("RIR")
            .define('I', Services.PLATFORM.getGoldIngotsTag())
            .define('R', Items.REDSTONE)
            .define('H', Items.HOPPER)
            .define('C', Items.COMPARATOR)
            .unlockedBy("has_gold_ingot", this.has(Services.PLATFORM.getGoldIngotsTag()))
            .unlockedBy("has_redstone", this.has(Items.REDSTONE))
            .unlockedBy("has_comparator", this.has(Items.COMPARATOR))
            .unlockedBy("has_hopper", this.has(Items.HOPPER))
            .save(this.output);

        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.TRANSPORTATION, ModItems.GOLDEN_HOPPER_MINECART.get())
            .pattern("A")
            .pattern("B")
            .define('A', ModBlocks.GOLDEN_HOPPER.get())
            .define('B', Items.MINECART)
            .unlockedBy("has_gold_ingot", this.has(Services.PLATFORM.getGoldIngotsTag()))
            .unlockedBy("has_minecart", this.has(Items.MINECART))
            .save(this.output);
    }

    public static final class Runner extends RecipeProvider.Runner
    {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture)
        {
            super(output, completableFuture);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput output)
        {
            return new CommonRecipeProvider(provider, output);
        }

        @Override
        public String getName()
        {
            return "Golden Hopper Recipes";
        }
    }
}
