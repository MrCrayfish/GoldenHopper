package com.mrcrayfish.goldenhopper.data;

import com.mrcrayfish.goldenhopper.core.ModItems;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Author: MrCrayfish
 */
public class CommonModelProvider implements DataProvider
{
    private final PackOutput.PathProvider itemsPathProvider;
    private final Map<Item, ClientItem> clientItems = new HashMap<>();

    public CommonModelProvider(PackOutput output)
    {
        this.itemsPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
    }

    private void generate()
    {
        this.existingModel(ModItems.GOLDEN_HOPPER.get());
        this.existingModel(ModItems.GOLDEN_HOPPER_MINECART.get());
    }

    private void existingModel(Item item)
    {
        this.clientItems.put(item, this.defaultClientItem(ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item))));
    }

    private ClientItem defaultClientItem(ItemModel.Unbaked unbaked)
    {
        return new ClientItem(unbaked, ClientItem.Properties.DEFAULT);
    }

    @Override
    @SuppressWarnings("deprecation")
    public CompletableFuture<?> run(CachedOutput output)
    {
        this.generate();
        return DataProvider.saveAll(output, ClientItem.CODEC, item -> {
            return this.itemsPathProvider.json(item.builtInRegistryHolder().key().identifier());
        }, this.clientItems);
    }

    @Override
    public String getName()
    {
        return "Model Definitions";
    }
}
