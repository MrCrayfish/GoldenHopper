package com.mrcrayfish.goldenhopper.data;

import com.mrcrayfish.goldenhopper.platform.Services;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class CommonItemTagsProvider
{
    public static void accept(Function<TagKey<Item>, TagBuilder<Item>> builder)
    {
        builder.apply(Services.PLATFORM.getGoldIngotsTag()).add(Items.GOLD_INGOT);
    }
}
