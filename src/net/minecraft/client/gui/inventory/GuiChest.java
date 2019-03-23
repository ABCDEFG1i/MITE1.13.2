package net.minecraft.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiChest extends GuiContainer {
   private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
   private final IInventory upperChestInventory;
   private final IInventory lowerChestInventory;
   private final int inventoryRows;

   public GuiChest(IInventory p_i46315_1_, IInventory p_i46315_2_) {
      super(new ContainerChest(p_i46315_1_, p_i46315_2_, Minecraft.getInstance().player));
      this.upperChestInventory = p_i46315_1_;
      this.lowerChestInventory = p_i46315_2_;
      this.allowUserInput = false;
      int i = 222;
      int j = 114;
      this.inventoryRows = p_i46315_2_.getSizeInventory() / 9;
      this.ySize = 114 + this.inventoryRows * 18;
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawDefaultBackground();
      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
      this.renderHoveredToolTip(p_73863_1_, p_73863_2_);
   }

   protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
      this.fontRenderer.drawString(this.lowerChestInventory.getDisplayName().getFormattedText(), 8.0F, 6.0F, 4210752);
      this.fontRenderer.drawString(this.upperChestInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
      this.drawTexturedModalRect(i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
   }
}
