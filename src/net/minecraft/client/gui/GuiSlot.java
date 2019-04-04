package net.minecraft.client.gui;

import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class GuiSlot extends GuiEventHandler {
   protected final Minecraft mc;
   public int width;
   public int height;
   public int top;
   public int bottom;
   public int right;
   public int left;
   public final int slotHeight;
   protected boolean centerListVertically = true;
   protected int initialClickY = -2;
   protected double amountScrolled;
   protected int selectedElement;
   protected long lastClicked = Long.MIN_VALUE;
   protected boolean visible = true;
   protected boolean showSelectionBox = true;
   protected boolean hasListHeader;
   public int headerPadding;
   private boolean clickedScrollbar;

   public GuiSlot(Minecraft p_i1052_1_, int p_i1052_2_, int p_i1052_3_, int p_i1052_4_, int p_i1052_5_, int p_i1052_6_) {
      this.mc = p_i1052_1_;
      this.width = p_i1052_2_;
      this.height = p_i1052_3_;
      this.top = p_i1052_4_;
      this.bottom = p_i1052_5_;
      this.slotHeight = p_i1052_6_;
      this.left = 0;
      this.right = p_i1052_2_;
   }

   public void setDimensions(int p_148122_1_, int p_148122_2_, int p_148122_3_, int p_148122_4_) {
      this.width = p_148122_1_;
      this.height = p_148122_2_;
      this.top = p_148122_3_;
      this.bottom = p_148122_4_;
      this.left = 0;
      this.right = p_148122_1_;
   }

   public void setShowSelectionBox(boolean p_193651_1_) {
      this.showSelectionBox = p_193651_1_;
   }

   protected void setHasListHeader(boolean p_148133_1_, int p_148133_2_) {
      this.hasListHeader = p_148133_1_;
      this.headerPadding = p_148133_2_;
      if (!p_148133_1_) {
         this.headerPadding = 0;
      }

   }

   public boolean isVisible() {
      return this.visible;
   }

   protected abstract int getSize();

   public void setSelectedEntry(int p_195080_1_) {
   }

   protected List<? extends IGuiEventListener> getChildren() {
      return Collections.emptyList();
   }

   protected boolean mouseClicked(int p_195078_1_, int p_195078_2_, double p_195078_3_, double p_195078_5_) {
      return true;
   }

   protected abstract boolean isSelected(int p_148131_1_);

   protected int getContentHeight() {
      return this.getSize() * this.slotHeight + this.headerPadding;
   }

   protected abstract void drawBackground();

   protected void updateItemPos(int p_192639_1_, int p_192639_2_, int p_192639_3_, float p_192639_4_) {
   }

   protected abstract void drawSlot(int p_192637_1_, int p_192637_2_, int p_192637_3_, int p_192637_4_, int p_192637_5_, int p_192637_6_, float p_192637_7_);

   protected void drawListHeader(int p_148129_1_, int p_148129_2_, Tessellator p_148129_3_) {
   }

   protected void clickedHeader(int p_148132_1_, int p_148132_2_) {
   }

   protected void renderDecorations(int p_148142_1_, int p_148142_2_) {
   }

   public int getEntryAt(double p_195083_1_, double p_195083_3_) {
      int i = this.left + this.width / 2 - this.getListWidth() / 2;
      int j = this.left + this.width / 2 + this.getListWidth() / 2;
      int k = MathHelper.floor(p_195083_3_ - (double)this.top) - this.headerPadding + (int)this.amountScrolled - 4;
      int l = k / this.slotHeight;
      return p_195083_1_ < (double)this.getScrollBarX() && p_195083_1_ >= (double)i && p_195083_1_ <= (double)j && l >= 0 && k >= 0 && l < this.getSize() ? l : -1;
   }

   protected void bindAmountScrolled() {
      this.amountScrolled = MathHelper.clamp(this.amountScrolled, 0.0D, (double)this.getMaxScroll());
   }

   public int getMaxScroll() {
      return Math.max(0, this.getContentHeight() - (this.bottom - this.top - 4));
   }

   public int getAmountScrolled() {
      return (int)this.amountScrolled;
   }

   public boolean func_195079_b(double p_195079_1_, double p_195079_3_) {
      return p_195079_3_ >= (double)this.top && p_195079_3_ <= (double)this.bottom && p_195079_1_ >= (double)this.left && p_195079_1_ <= (double)this.right;
   }

   public void scrollBy(int p_148145_1_) {
      this.amountScrolled += (double)p_148145_1_;
      this.bindAmountScrolled();
      this.initialClickY = -2;
   }

   public void drawScreen(int p_148128_1_, int p_148128_2_, float p_148128_3_) {
      if (this.visible) {
         this.drawBackground();
         int i = this.getScrollBarX();
         int j = i + 6;
         this.bindAmountScrolled();
         GlStateManager.disableLighting();
         GlStateManager.disableFog();
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         this.mc.getTextureManager().bindTexture(Gui.OPTIONS_BACKGROUND);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         float f = 32.0F;
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         bufferbuilder.pos((double)this.left, (double)this.bottom, 0.0D).tex((double)((float)this.left / 32.0F), (double)((float)(this.bottom + (int)this.amountScrolled) / 32.0F)).color(32, 32, 32, 255).endVertex();
         bufferbuilder.pos((double)this.right, (double)this.bottom, 0.0D).tex((double)((float)this.right / 32.0F), (double)((float)(this.bottom + (int)this.amountScrolled) / 32.0F)).color(32, 32, 32, 255).endVertex();
         bufferbuilder.pos((double)this.right, (double)this.top, 0.0D).tex((double)((float)this.right / 32.0F), (double)((float)(this.top + (int)this.amountScrolled) / 32.0F)).color(32, 32, 32, 255).endVertex();
         bufferbuilder.pos((double)this.left, (double)this.top, 0.0D).tex((double)((float)this.left / 32.0F), (double)((float)(this.top + (int)this.amountScrolled) / 32.0F)).color(32, 32, 32, 255).endVertex();
         tessellator.draw();
         int k = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
         int l = this.top + 4 - (int)this.amountScrolled;
         if (this.hasListHeader) {
            this.drawListHeader(k, l, tessellator);
         }

         this.drawSelectionBox(k, l, p_148128_1_, p_148128_2_, p_148128_3_);
         GlStateManager.disableDepthTest();
         this.overlayBackground(0, this.top, 255, 255);
         this.overlayBackground(this.bottom, this.height, 255, 255);
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
         GlStateManager.disableAlphaTest();
         GlStateManager.shadeModel(7425);
         GlStateManager.disableTexture2D();
         int i1 = 4;
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         bufferbuilder.pos((double)this.left, (double)(this.top + 4), 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 0).endVertex();
         bufferbuilder.pos((double)this.right, (double)(this.top + 4), 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 0).endVertex();
         bufferbuilder.pos((double)this.right, (double)this.top, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.pos((double)this.left, (double)this.top, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         tessellator.draw();
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         bufferbuilder.pos((double)this.left, (double)this.bottom, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.pos((double)this.right, (double)this.bottom, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.pos((double)this.right, (double)(this.bottom - 4), 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 0).endVertex();
         bufferbuilder.pos((double)this.left, (double)(this.bottom - 4), 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 0).endVertex();
         tessellator.draw();
         int j1 = this.getMaxScroll();
         if (j1 > 0) {
            int k1 = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getContentHeight());
            k1 = MathHelper.clamp(k1, 32, this.bottom - this.top - 8);
            int l1 = (int)this.amountScrolled * (this.bottom - this.top - k1) / j1 + this.top;
            if (l1 < this.top) {
               l1 = this.top;
            }

            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos((double)i, (double)this.bottom, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos((double)j, (double)this.bottom, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos((double)j, (double)this.top, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos((double)i, (double)this.top, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
            tessellator.draw();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos((double)i, (double)(l1 + k1), 0.0D).tex(0.0D, 1.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos((double)j, (double)(l1 + k1), 0.0D).tex(1.0D, 1.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos((double)j, (double)l1, 0.0D).tex(1.0D, 0.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos((double)i, (double)l1, 0.0D).tex(0.0D, 0.0D).color(128, 128, 128, 255).endVertex();
            tessellator.draw();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos((double)i, (double)(l1 + k1 - 1), 0.0D).tex(0.0D, 1.0D).color(192, 192, 192, 255).endVertex();
            bufferbuilder.pos((double)(j - 1), (double)(l1 + k1 - 1), 0.0D).tex(1.0D, 1.0D).color(192, 192, 192, 255).endVertex();
            bufferbuilder.pos((double)(j - 1), (double)l1, 0.0D).tex(1.0D, 0.0D).color(192, 192, 192, 255).endVertex();
            bufferbuilder.pos((double)i, (double)l1, 0.0D).tex(0.0D, 0.0D).color(192, 192, 192, 255).endVertex();
            tessellator.draw();
         }

         this.renderDecorations(p_148128_1_, p_148128_2_);
         GlStateManager.enableTexture2D();
         GlStateManager.shadeModel(7424);
         GlStateManager.enableAlphaTest();
         GlStateManager.disableBlend();
      }
   }

   protected void checkScrollbarClick(double p_195077_1_, double p_195077_3_, int p_195077_5_) {
      this.clickedScrollbar = p_195077_5_ == 0 && p_195077_1_ >= (double)this.getScrollBarX() && p_195077_1_ < (double)(this.getScrollBarX() + 6);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      this.checkScrollbarClick(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      if (this.isVisible() && this.func_195079_b(p_mouseClicked_1_, p_mouseClicked_3_)) {
         int i = this.getEntryAt(p_mouseClicked_1_, p_mouseClicked_3_);
         if (i == -1 && p_mouseClicked_5_ == 0) {
            this.clickedHeader((int)(p_mouseClicked_1_ - (double)(this.left + this.width / 2 - this.getListWidth() / 2)), (int)(p_mouseClicked_3_ - (double)this.top) + (int)this.amountScrolled - 4);
            return true;
         } else if (i != -1 && this.mouseClicked(i, p_mouseClicked_5_, p_mouseClicked_1_, p_mouseClicked_3_)) {
            if (this.getChildren().size() > i) {
               this.setFocused(this.getChildren().get(i));
            }

            this.setDragging(true);
            this.setSelectedEntry(i);
            return true;
         } else {
            return this.clickedScrollbar;
         }
      } else {
         return false;
      }
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      if (this.getFocused() != null) {
         this.getFocused().mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
      }

      this.getChildren().forEach((p_195081_5_) -> {
         p_195081_5_.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
      });
      return false;
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      if (super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_)) {
         return true;
      } else if (this.isVisible() && p_mouseDragged_5_ == 0 && this.clickedScrollbar) {
         if (p_mouseDragged_3_ < (double)this.top) {
            this.amountScrolled = 0.0D;
         } else if (p_mouseDragged_3_ > (double)this.bottom) {
            this.amountScrolled = (double)this.getMaxScroll();
         } else {
            double d0 = (double)this.getMaxScroll();
            if (d0 < 1.0D) {
               d0 = 1.0D;
            }

            int i = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getContentHeight());
            i = MathHelper.clamp(i, 32, this.bottom - this.top - 8);
            double d1 = d0 / (double)(this.bottom - this.top - i);
            if (d1 < 1.0D) {
               d1 = 1.0D;
            }

            this.amountScrolled += p_mouseDragged_8_ * d1;
            this.bindAmountScrolled();
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_) {
      if (!this.isVisible()) {
         return false;
      } else {
         this.amountScrolled -= p_mouseScrolled_1_ * (double)this.slotHeight / 2.0D;
         return true;
      }
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      return this.isVisible() && super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      return this.isVisible() && super.charTyped(p_charTyped_1_, p_charTyped_2_);
   }

   public int getListWidth() {
      return 220;
   }

   protected void drawSelectionBox(int p_192638_1_, int p_192638_2_, int p_192638_3_, int p_192638_4_, float p_192638_5_) {
      int i = this.getSize();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();

      for(int j = 0; j < i; ++j) {
         int k = p_192638_2_ + j * this.slotHeight + this.headerPadding;
         int l = this.slotHeight - 4;
         if (k > this.bottom || k + l < this.top) {
            this.updateItemPos(j, p_192638_1_, k, p_192638_5_);
         }

         if (this.showSelectionBox && this.isSelected(j)) {
            int i1 = this.left + this.width / 2 - this.getListWidth() / 2;
            int j1 = this.left + this.width / 2 + this.getListWidth() / 2;
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableTexture2D();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos((double)i1, (double)(k + l + 2), 0.0D).tex(0.0D, 1.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos((double)j1, (double)(k + l + 2), 0.0D).tex(1.0D, 1.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos((double)j1, (double)(k - 2), 0.0D).tex(1.0D, 0.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos((double)i1, (double)(k - 2), 0.0D).tex(0.0D, 0.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos((double)(i1 + 1), (double)(k + l + 1), 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos((double)(j1 - 1), (double)(k + l + 1), 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos((double)(j1 - 1), (double)(k - 1), 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos((double)(i1 + 1), (double)(k - 1), 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
         }

         this.drawSlot(j, p_192638_1_, k, l, p_192638_3_, p_192638_4_, p_192638_5_);
      }

   }

   protected int getScrollBarX() {
      return this.width / 2 + 124;
   }

   protected void overlayBackground(int p_148136_1_, int p_148136_2_, int p_148136_3_, int p_148136_4_) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      this.mc.getTextureManager().bindTexture(Gui.OPTIONS_BACKGROUND);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float f = 32.0F;
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      bufferbuilder.pos((double)this.left, (double)p_148136_2_, 0.0D).tex(0.0D, (double)((float)p_148136_2_ / 32.0F)).color(64, 64, 64, p_148136_4_).endVertex();
      bufferbuilder.pos((double)(this.left + this.width), (double)p_148136_2_, 0.0D).tex((double)((float)this.width / 32.0F), (double)((float)p_148136_2_ / 32.0F)).color(64, 64, 64, p_148136_4_).endVertex();
      bufferbuilder.pos((double)(this.left + this.width), (double)p_148136_1_, 0.0D).tex((double)((float)this.width / 32.0F), (double)((float)p_148136_1_ / 32.0F)).color(64, 64, 64, p_148136_3_).endVertex();
      bufferbuilder.pos((double)this.left, (double)p_148136_1_, 0.0D).tex(0.0D, (double)((float)p_148136_1_ / 32.0F)).color(64, 64, 64, p_148136_3_).endVertex();
      tessellator.draw();
   }

   public void setSlotXBoundsFromLeft(int p_148140_1_) {
      this.left = p_148140_1_;
      this.right = p_148140_1_ + this.width;
   }

   public int getSlotHeight() {
      return this.slotHeight;
   }
}
