package com.mrcrayfish.goldenhopper.data;

import com.mrcrayfish.goldenhopper.core.ModBlocks;
import com.mrcrayfish.goldenhopper.core.ModItems;
import com.mrcrayfish.goldenhopper.platform.Services;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.MultiRegistryBootstrap;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Author: MrCrayfish
 */
public class CommonRecipeProvider extends RecipeProvider
{
    private final HolderGetter<Item> items;

    public CommonRecipeProvider(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput)
    {
        super(recipeOutput, advancementOutput);
        this.items = recipeOutput.lookup(Registries.ITEM);
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

    public static MultiRegistryBootstrap create()
    {
        return new MultiRegistryBootstrap()
        {
            @Override
            public Set<ResourceKey<? extends @NotNull Registry<?>>> requestedRegistries()
            {
                return Set.of(Registries.RECIPE, Registries.ADVANCEMENT);
            }

            @Override
            public void run(MultiRegistryBootstrap.BootstrapGetter getter)
            {
                new CommonRecipeProvider(getter.get(Registries.RECIPE), getter.get(Registries.ADVANCEMENT)).buildRecipes();
            }
        };
    }
}
