package com.mrcrayfish.goldenhopper.inventory;

import com.mrcrayfish.goldenhopper.Constants;
import net.minecraft.client.gui.GuiGraphics;
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
        super(container, playerInventory, titleIn);
        this.imageHeight = 133;
        this.inventoryLabelY = 40;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
    {
        int startX = (this.width - this.imageWidth) / 2;
        int startY = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, startX, startY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
}
