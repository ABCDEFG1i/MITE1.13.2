package net.minecraft.client.gui;

import javax.annotation.Nullable;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiOptionsRowList extends GuiListExtended<GuiOptionsRowList.Row> {
   public GuiOptionsRowList(Minecraft p_i45015_1_, int p_i45015_2_, int p_i45015_3_, int p_i45015_4_, int p_i45015_5_, int p_i45015_6_, GameSettings.Options... p_i45015_7_) {
      super(p_i45015_1_, p_i45015_2_, p_i45015_3_, p_i45015_4_, p_i45015_5_, p_i45015_6_);
      this.centerListVertically = false;
      this.addEntry(new GuiOptionsRowList.Row(p_i45015_2_, GameSettings.Options.FULLSCREEN_RESOLUTION));

      for(int i = 0; i < p_i45015_7_.length; i += 2) {
         GameSettings.Options gamesettings$options = p_i45015_7_[i];
         GameSettings.Options gamesettings$options1 = i < p_i45015_7_.length - 1 ? p_i45015_7_[i + 1] : null;
         this.addEntry(new GuiOptionsRowList.Row(p_i45015_2_, gamesettings$options, gamesettings$options1));
      }

   }

   @Nullable
   private static GuiButton func_195092_b(final Minecraft p_195092_0_, int p_195092_1_, int p_195092_2_, int p_195092_3_, @Nullable final GameSettings.Options p_195092_4_) {
      if (p_195092_4_ == null) {
         return null;
      } else {
         int i = p_195092_4_.getOrdinal();
         return (GuiButton)(p_195092_4_.isFloat() ? new GuiOptionSlider(i, p_195092_1_, p_195092_2_, p_195092_3_, 20, p_195092_4_, 0.0D, 1.0D) : new GuiOptionButton(i, p_195092_1_, p_195092_2_, p_195092_3_, 20, p_195092_4_, p_195092_0_.gameSettings.getKeyBinding(p_195092_4_)) {
            public void onClick(double p_194829_1_, double p_194829_3_) {
               p_195092_0_.gameSettings.setOptionValue(p_195092_4_, 1);
               this.displayString = p_195092_0_.gameSettings.getKeyBinding(GameSettings.Options.byOrdinal(this.id));
            }
         });
      }
   }

   public int getListWidth() {
      return 400;
   }

   protected int getScrollBarX() {
      return super.getScrollBarX() + 32;
   }

   @OnlyIn(Dist.CLIENT)
   public final class Row extends GuiListExtended.IGuiListEntry<GuiOptionsRowList.Row> {
      @Nullable
      private final GuiButton buttonA;
      @Nullable
      private final GuiButton buttonB;

      public Row(@Nullable GuiButton p_i48071_2_, @Nullable GuiButton p_i48071_3_) {
         this.buttonA = p_i48071_2_;
         this.buttonB = p_i48071_3_;
      }

      public Row(int p_i48072_2_, GameSettings.Options p_i48072_3_) {
         this(GuiOptionsRowList.func_195092_b(GuiOptionsRowList.this.mc, p_i48072_2_ / 2 - 155, 0, 310, p_i48072_3_), (GuiButton)null);
      }

      public Row(int p_i48073_2_, GameSettings.Options p_i48073_3_, @Nullable GameSettings.Options p_i48073_4_) {
         this(GuiOptionsRowList.func_195092_b(GuiOptionsRowList.this.mc, p_i48073_2_ / 2 - 155, 0, 150, p_i48073_3_), GuiOptionsRowList.func_195092_b(GuiOptionsRowList.this.mc, p_i48073_2_ / 2 - 155 + 160, 0, 150, p_i48073_4_));
      }

      public void drawEntry(int p_194999_1_, int p_194999_2_, int p_194999_3_, int p_194999_4_, boolean p_194999_5_, float p_194999_6_) {
         if (this.buttonA != null) {
            this.buttonA.y = this.getY();
            this.buttonA.render(p_194999_3_, p_194999_4_, p_194999_6_);
         }

         if (this.buttonB != null) {
            this.buttonB.y = this.getY();
            this.buttonB.render(p_194999_3_, p_194999_4_, p_194999_6_);
         }

      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         if (this.buttonA.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
            return true;
         } else {
            return this.buttonB != null && this.buttonB.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
         }
      }

      public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
         boolean flag = this.buttonA != null && this.buttonA.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
         boolean flag1 = this.buttonB != null && this.buttonB.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
         return flag || flag1;
      }

      public void func_195000_a(float p_195000_1_) {
      }
   }
}
