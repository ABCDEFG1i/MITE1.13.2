package net.minecraft.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiScreenHorseInventory extends GuiContainer {
   private static final ResourceLocation HORSE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/horse.png");
   private final IInventory playerInventory;
   private final IInventory horseInventory;
   private final AbstractHorse horseEntity;
   private float mousePosx;
   private float mousePosY;

   public GuiScreenHorseInventory(IInventory p_i1093_1_, IInventory p_i1093_2_, AbstractHorse p_i1093_3_) {
      super(new ContainerHorseInventory(p_i1093_1_, p_i1093_2_, p_i1093_3_, Minecraft.getInstance().player));
      this.playerInventory = p_i1093_1_;
      this.horseInventory = p_i1093_2_;
      this.horseEntity = p_i1093_3_;
      this.allowUserInput = false;
   }

   protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
      this.fontRenderer.drawString(this.horseInventory.getDisplayName().getFormattedText(), 8.0F, 6.0F, 4210752);
      this.fontRenderer.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(HORSE_GUI_TEXTURES);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
      if (this.horseEntity instanceof AbstractChestHorse) {
         AbstractChestHorse abstractchesthorse = (AbstractChestHorse)this.horseEntity;
         if (abstractchesthorse.hasChest()) {
            this.drawTexturedModalRect(i + 79, j + 17, 0, this.ySize, abstractchesthorse.getInventoryColumns() * 18, 54);
         }
      }

      if (this.horseEntity.canBeSaddled()) {
         this.drawTexturedModalRect(i + 7, j + 35 - 18, 18, this.ySize + 54, 18, 18);
      }

      if (this.horseEntity.wearsArmor()) {
         if (this.horseEntity instanceof EntityLlama) {
            this.drawTexturedModalRect(i + 7, j + 35, 36, this.ySize + 54, 18, 18);
         } else {
            this.drawTexturedModalRect(i + 7, j + 35, 0, this.ySize + 54, 18, 18);
         }
      }

      GuiInventory.drawEntityOnScreen(i + 51, j + 60, 17, (float)(i + 51) - this.mousePosx, (float)(j + 75 - 50) - this.mousePosY, this.horseEntity);
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawDefaultBackground();
      this.mousePosx = (float)p_73863_1_;
      this.mousePosY = (float)p_73863_2_;
      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
      this.renderHoveredToolTip(p_73863_1_, p_73863_2_);
   }
}
