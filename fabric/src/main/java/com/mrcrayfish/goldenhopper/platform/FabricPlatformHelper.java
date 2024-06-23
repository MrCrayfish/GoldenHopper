package com.mrcrayfish.goldenhopper.platform;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * Author: MrCrayfish
 */
public class FabricPlatformHelper implements IPlatformHelper
{
    @Override
    public TagKey<Item> getGoldIngotsTag()
    {
        return ConventionalItemTags.GOLD_INGOTS;
    }
}
