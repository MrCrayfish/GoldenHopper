package com.mrcrayfish.goldenhopper.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.goldenhopper.world.inventory.GoldenHopperMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

/**
 * Author: MrCrayfish
 */
public class GoldenHopperScreen extends AbstractContainerScreen<GoldenHopperMenu>
{
    private static final ResourceLocation HOPPER_GUI_TEXTURE = new ResourceLocation("goldenhopper:textures/gui/container/golden_hopper.png");

    public GoldenHopperScreen(GoldenHopperMenu container, Inventory playerInventory, Component titleIn)
    {
        super(container, playerInventory, titleIn);
        this.imageHeight = 133;
        this.inventoryLabelY = 40;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int startX = (this.width - this.imageWidth) / 2;
        int startY = (this.height - this.imageHeight) / 2;
        graphics.blit(HOPPER_GUI_TEXTURE, startX, startY, 0, 0, this.imageWidth, this.imageHeight);
        Slot slot = this.menu.getSlot(0);
        if(!slot.hasItem())
        {
            graphics.blit(HOPPER_GUI_TEXTURE, this.leftPos + slot.x, this.topPos + slot.y, this.imageWidth, 0, 16, 16);
        }
    }
}
