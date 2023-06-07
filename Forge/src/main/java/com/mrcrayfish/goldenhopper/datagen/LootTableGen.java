package com.mrcrayfish.goldenhopper.datagen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mrcrayfish.goldenhopper.Constants;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LootTableGen extends LootTableProvider
{
    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> tables = ImmutableList.of(Pair.of(BlockProvider::new, LootContextParamSets.BLOCK));

    public LootTableGen(DataGenerator generator)
    {
        super(generator);
    }

    @Override
    public List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables()
    {
        return tables;
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext context) {}

    private static class BlockProvider extends BlockLoot
    {
        @Override
        protected void addTables()
        {
            CommonLootTableGen.generate(this::dropSelf);
        }

        @Override
        protected Iterable<Block> getKnownBlocks()
        {
            return ForgeRegistries.BLOCKS.getValues().stream().filter(block -> Constants.MOD_ID.equals(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).getNamespace())).collect(Collectors.toSet());
        }
    }
}
