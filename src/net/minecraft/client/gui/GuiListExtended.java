package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.AbstractList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class GuiListExtended<E extends GuiListExtended.IGuiListEntry<E>> extends GuiSlot {
   private final List<E> entries = new GuiListExtended.UpdatingList();

   public GuiListExtended(Minecraft p_i45010_1_, int p_i45010_2_, int p_i45010_3_, int p_i45010_4_, int p_i45010_5_, int p_i45010_6_) {
      super(p_i45010_1_, p_i45010_2_, p_i45010_3_, p_i45010_4_, p_i45010_5_, p_i45010_6_);
   }

   protected boolean mouseClicked(int p_195078_1_, int p_195078_2_, double p_195078_3_, double p_195078_5_) {
      return this.getListEntry(p_195078_1_).mouseClicked(p_195078_3_, p_195078_5_, p_195078_2_);
   }

   protected boolean isSelected(int p_148131_1_) {
      return false;
   }

   protected void drawBackground() {
   }

   protected void drawSlot(int p_192637_1_, int p_192637_2_, int p_192637_3_, int p_192637_4_, int p_192637_5_, int p_192637_6_, float p_192637_7_) {
      this.getListEntry(p_192637_1_).drawEntry(this.getListWidth(), p_192637_4_, p_192637_5_, p_192637_6_, this.func_195079_b((double)p_192637_5_, (double)p_192637_6_) && this.getEntryAt((double)p_192637_5_, (double)p_192637_6_) == p_192637_1_, p_192637_7_);
   }

   protected void updateItemPos(int p_192639_1_, int p_192639_2_, int p_192639_3_, float p_192639_4_) {
      this.getListEntry(p_192639_1_).func_195000_a(p_192639_4_);
   }

   public final List<E> getChildren() {
      return this.entries;
   }

   protected final void clearEntries() {
      this.entries.clear();
   }

   private E getListEntry(int p_148180_1_) {
      return (E)(this.getChildren().get(p_148180_1_));
   }

   protected final void addEntry(E p_195085_1_) {
      this.entries.add(p_195085_1_);
   }

   public void setSelectedEntry(int p_195080_1_) {
      this.selectedElement = p_195080_1_;
      this.lastClicked = Util.milliTime();
   }

   protected final int getSize() {
      return this.getChildren().size();
   }

   @OnlyIn(Dist.CLIENT)
   public abstract static class IGuiListEntry<E extends GuiListExtended.IGuiListEntry<E>> implements IGuiEventListener {
      protected GuiListExtended<E> list;
      protected int index;

      protected GuiListExtended<E> getList() {
         return this.list;
      }

      protected int getIndex() {
         return this.index;
      }

      protected int getY() {
         return this.list.top + 4 - this.list.getAmountScrolled() + this.index * this.list.slotHeight + this.list.headerPadding;
      }

      protected int getX() {
         return this.list.left + this.list.width / 2 - this.list.getListWidth() / 2 + 2;
      }

      protected void func_195000_a(float p_195000_1_) {
      }

      public abstract void drawEntry(int p_194999_1_, int p_194999_2_, int p_194999_3_, int p_194999_4_, boolean p_194999_5_, float p_194999_6_);
   }

   @OnlyIn(Dist.CLIENT)
   class UpdatingList extends AbstractList<E> {
      private final List<E> innerList = Lists.newArrayList();

      private UpdatingList() {
      }

      public E get(int p_get_1_) {
         return (E)(this.innerList.get(p_get_1_));
      }

      public int size() {
         return this.innerList.size();
      }

      public E set(int p_set_1_, E p_set_2_) {
         E e = this.innerList.set(p_set_1_, p_set_2_);
         p_set_2_.list = GuiListExtended.this;
         p_set_2_.index = p_set_1_;
         return e;
      }

      public void add(int p_add_1_, E p_add_2_) {
         this.innerList.add(p_add_1_, p_add_2_);
         p_add_2_.list = GuiListExtended.this;
         p_add_2_.index = p_add_1_;

         for(int i = p_add_1_ + 1; i < this.size(); this.get(i).index = i++) {
            ;
         }

      }

      public E remove(int p_remove_1_) {
         E e = this.innerList.remove(p_remove_1_);

         for(int i = p_remove_1_; i < this.size(); this.get(i).index = i++) {
            ;
         }

         return e;
      }
   }
}
