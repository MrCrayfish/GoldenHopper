package com.mrcrayfish.goldenhopper.inventory;

import com.mrcrayfish.goldenhopper.Constants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/**
 * Author: MrCrayfish
 */
public class GoldenHopperScreen extends AbstractContainerScreen<GoldenHopperMenu>
{
    private static final Identifier GUI_TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/container/golden_hopper.png");

    public GoldenHopperScreen(GoldenHopperMenu container, Inventory playerInventory, Component titleIn)
    {
        super(container, playerInventory, titleIn, DEFAULT_IMAGE_WIDTH, 133);
        this.inventoryLabelY = 40;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTicks)
    {
        super.extractRenderState(extractor, mouseX, mouseY, partialTicks);
        this.extractTooltip(extractor, mouseX, mouseY);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTicks)
    {
        super.extractBackground(extractor, mouseX, mouseY, partialTicks);
        int startX = (this.width - this.imageWidth) / 2;
        int startY = (this.height - this.imageHeight) / 2;
        extractor.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, startX, startY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
}
