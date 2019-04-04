package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsScrolledSelectionList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsScrolledSelectionListProxy extends GuiSlot {
   private final RealmsScrolledSelectionList scrolledSelectionList;

   public RealmsScrolledSelectionListProxy(RealmsScrolledSelectionList p_i49325_1_, int p_i49325_2_, int p_i49325_3_, int p_i49325_4_, int p_i49325_5_, int p_i49325_6_) {
      super(Minecraft.getInstance(), p_i49325_2_, p_i49325_3_, p_i49325_4_, p_i49325_5_, p_i49325_6_);
      this.scrolledSelectionList = p_i49325_1_;
   }

   protected int getSize() {
      return this.scrolledSelectionList.getItemCount();
   }

   protected boolean mouseClicked(int p_195078_1_, int p_195078_2_, double p_195078_3_, double p_195078_5_) {
      return this.scrolledSelectionList.selectItem(p_195078_1_, p_195078_2_, p_195078_3_, p_195078_5_);
   }

   protected boolean isSelected(int p_148131_1_) {
      return this.scrolledSelectionList.isSelectedItem(p_148131_1_);
   }

   protected void drawBackground() {
      this.scrolledSelectionList.renderBackground();
   }

   protected void drawSlot(int p_192637_1_, int p_192637_2_, int p_192637_3_, int p_192637_4_, int p_192637_5_, int p_192637_6_, float p_192637_7_) {
      this.scrolledSelectionList.renderItem(p_192637_1_, p_192637_2_, p_192637_3_, p_192637_4_, p_192637_5_, p_192637_6_);
   }

   public int width() {
      return this.width;
   }

   protected int getContentHeight() {
      return this.scrolledSelectionList.getMaxPosition();
   }

   protected int getScrollBarX() {
      return this.scrolledSelectionList.getScrollbarPosition();
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_) {
      return this.scrolledSelectionList.mouseScrolled(p_mouseScrolled_1_) || super.mouseScrolled(p_mouseScrolled_1_);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return this.scrolledSelectionList.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_,
              p_mouseClicked_5_) || super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      return this.scrolledSelectionList.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      return this.scrolledSelectionList.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
   }
}
