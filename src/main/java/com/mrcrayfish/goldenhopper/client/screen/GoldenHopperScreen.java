package com.mrcrayfish.goldenhopper.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.goldenhopper.inventory.container.GoldenHopperContainer;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Author: MrCrayfish
 */
@OnlyIn(Dist.CLIENT)
public class GoldenHopperScreen extends ContainerScreen<GoldenHopperContainer>
{
    private static final ResourceLocation HOPPER_GUI_TEXTURE = new ResourceLocation("goldenhopper:textures/gui/container/golden_hopper.png");

    public GoldenHopperScreen(GoldenHopperContainer container, PlayerInventory playerInventory, ITextComponent titleIn)
    {
        super(container, playerInventory, titleIn);
        this.ySize = 133;
    }

    @Override
    // TODO MCP-name: public void render(int mouseX, int mouseY, float partialTicks)
    public void func_230430_a_(MatrixStack p_230450_1_, int mouseX, int mouseY, float partialTicks)
    {
        // TODO MCP-name: this.renderBackground();
        this.func_230446_a_(p_230450_1_);
        // TODO MCP-name: super.render(mouseX, mouseY, partialTicks);
        super.func_230430_a_(p_230450_1_, mouseX, mouseY, partialTicks);
        // TODO MCP-name: this.renderHoveredToolTip(mouseX, mouseY);
        this.func_230459_a_(p_230450_1_, mouseX, mouseY);
    }

    @Override
    // TODO MCP-name: protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    protected void func_230451_b_(MatrixStack p_230451_1_, int mouseX, int mouseY)
    {
        // TODO MCP-name: this.font.drawString(this.title.getFormattedText(), 8.0F, 6.0F, 4210752);
        this.field_230712_o_.func_238422_b_(p_230451_1_, this.field_230704_d_, 8.0F, 6.0F, 4210752);
        // TODO MCP-name: this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
        this.field_230712_o_.func_238422_b_(p_230451_1_, this.playerInventory.getDisplayName(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
    }

    @Override
    // TODO MCP-name: protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    protected void func_230450_a_(MatrixStack p_230450_1_, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        // TODO MCP-name: this.minecraft.getTextureManager().bindTexture(HOPPER_GUI_TEXTURE);
        this.field_230706_i_.getTextureManager().bindTexture(HOPPER_GUI_TEXTURE);
        // TODO MCP-name: int startX = (this.width - this.xSize) / 2;
        int startX = (this.field_230708_k_ - this.xSize) / 2;
        // TODO MCP-name: int startY = (this.height - this.ySize) / 2;
        int startY = (this.field_230709_l_ - this.ySize) / 2;
        // TODO MCP-name: this.blit(startX, startY, 0, 0, this.xSize, this.ySize);
        this.func_238474_b_(p_230450_1_, startX, startY, 0, 0, this.xSize, this.ySize);

        Slot slot = this.container.getSlot(0);
        if(!slot.getHasStack())
        {
            // TODO MCP-name: blit(this.guiLeft + slot.xPos, this.guiTop + slot.yPos, this.xSize, 0, 16, 16);
            func_238474_b_(p_230450_1_, this.guiLeft + slot.xPos, this.guiTop + slot.yPos, this.xSize, 0, 16, 16);
        }
    }
}
