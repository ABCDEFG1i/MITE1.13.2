package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsClickableScrolledSelectionList;
import net.minecraft.realms.Tezzelator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsClickableScrolledSelectionListProxy extends GuiSlot {
   private final RealmsClickableScrolledSelectionList field_207723_v;

   public RealmsClickableScrolledSelectionListProxy(RealmsClickableScrolledSelectionList p_i49326_1_, int p_i49326_2_, int p_i49326_3_, int p_i49326_4_, int p_i49326_5_, int p_i49326_6_) {
      super(Minecraft.getInstance(), p_i49326_2_, p_i49326_3_, p_i49326_4_, p_i49326_5_, p_i49326_6_);
      this.field_207723_v = p_i49326_1_;
   }

   protected int getSize() {
      return this.field_207723_v.getItemCount();
   }

   protected boolean mouseClicked(int p_195078_1_, int p_195078_2_, double p_195078_3_, double p_195078_5_) {
      return this.field_207723_v.selectItem(p_195078_1_, p_195078_2_, p_195078_3_, p_195078_5_);
   }

   protected boolean isSelected(int p_148131_1_) {
      return this.field_207723_v.isSelectedItem(p_148131_1_);
   }

   protected void drawBackground() {
      this.field_207723_v.renderBackground();
   }

   protected void drawSlot(int p_192637_1_, int p_192637_2_, int p_192637_3_, int p_192637_4_, int p_192637_5_, int p_192637_6_, float p_192637_7_) {
      this.field_207723_v.renderItem(p_192637_1_, p_192637_2_, p_192637_3_, p_192637_4_, p_192637_5_, p_192637_6_);
   }

   public int width() {
      return this.width;
   }

   protected int getContentHeight() {
      return this.field_207723_v.getMaxPosition();
   }

   protected int getScrollBarX() {
      return this.field_207723_v.getScrollbarPosition();
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_) {
      return this.field_207723_v.mouseScrolled(p_mouseScrolled_1_) ? true : super.mouseScrolled(p_mouseScrolled_1_);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return this.field_207723_v.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_) ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      return this.field_207723_v.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      return this.field_207723_v.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_) ? true : super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
   }

   public void renderSelected(int p_207719_1_, int p_207719_2_, int p_207719_3_, Tezzelator p_207719_4_) {
      this.field_207723_v.renderSelected(p_207719_1_, p_207719_2_, p_207719_3_, p_207719_4_);
   }

   protected void drawSelectionBox(int p_192638_1_, int p_192638_2_, int p_192638_3_, int p_192638_4_, float p_192638_5_) {
      int i = this.getSize();

      for(int j = 0; j < i; ++j) {
         int k = p_192638_2_ + j * this.slotHeight + this.headerPadding;
         int l = this.slotHeight - 4;
         if (k > this.bottom || k + l < this.top) {
            this.updateItemPos(j, p_192638_1_, k, p_192638_5_);
         }

         if (this.showSelectionBox && this.isSelected(j)) {
            this.renderSelected(this.width, k, l, Tezzelator.instance);
         }

         this.drawSlot(j, p_192638_1_, k, l, p_192638_3_, p_192638_4_, p_192638_5_);
      }

   }

   public int y0() {
      return this.top;
   }

   public int y1() {
      return this.bottom;
   }

   public int headerHeight() {
      return this.headerPadding;
   }

   public double yo() {
      return this.amountScrolled;
   }

   public int itemHeight() {
      return this.slotHeight;
   }
}
