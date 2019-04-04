package net.minecraft.client.gui;

import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.ArrayUtils;

@OnlyIn(Dist.CLIENT)
public class GuiKeyBindingList extends GuiListExtended<GuiKeyBindingList.Entry> {
   private final GuiControls controlsScreen;
   private final Minecraft mc;
   private int maxListLabelWidth;

   public GuiKeyBindingList(GuiControls p_i45031_1_, Minecraft p_i45031_2_) {
      super(p_i45031_2_, p_i45031_1_.width + 45, p_i45031_1_.height, 63, p_i45031_1_.height - 32, 20);
      this.controlsScreen = p_i45031_1_;
      this.mc = p_i45031_2_;
      KeyBinding[] akeybinding = ArrayUtils.clone(p_i45031_2_.gameSettings.keyBindings);
      Arrays.sort(akeybinding);
      String s = null;

      for(KeyBinding keybinding : akeybinding) {
         String s1 = keybinding.getKeyCategory();
         if (!s1.equals(s)) {
            s = s1;
            this.addEntry(new GuiKeyBindingList.CategoryEntry(s1));
         }

         int i = p_i45031_2_.fontRenderer.getStringWidth(I18n.format(keybinding.getKeyDescription()));
         if (i > this.maxListLabelWidth) {
            this.maxListLabelWidth = i;
         }

         this.addEntry(new GuiKeyBindingList.KeyEntry(keybinding));
      }

   }

   protected int getScrollBarX() {
      return super.getScrollBarX() + 15;
   }

   public int getListWidth() {
      return super.getListWidth() + 32;
   }

   @OnlyIn(Dist.CLIENT)
   public class CategoryEntry extends GuiKeyBindingList.Entry {
      private final String labelText;
      private final int labelWidth;

      public CategoryEntry(String p_i45028_2_) {
         this.labelText = I18n.format(p_i45028_2_);
         this.labelWidth = GuiKeyBindingList.this.mc.fontRenderer.getStringWidth(this.labelText);
      }

      public void drawEntry(int p_194999_1_, int p_194999_2_, int p_194999_3_, int p_194999_4_, boolean p_194999_5_, float p_194999_6_) {
         GuiKeyBindingList.this.mc.fontRenderer.drawString(this.labelText, (float)(GuiKeyBindingList.this.mc.currentScreen.width / 2 - this.labelWidth / 2), (float)(this.getY() + p_194999_2_ - GuiKeyBindingList.this.mc.fontRenderer.FONT_HEIGHT - 1), 16777215);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public abstract static class Entry extends GuiListExtended.IGuiListEntry<GuiKeyBindingList.Entry> {
   }

   @OnlyIn(Dist.CLIENT)
   public class KeyEntry extends GuiKeyBindingList.Entry {
      private final KeyBinding keybinding;
      private final String keyDesc;
      private final GuiButton btnChangeKeyBinding;
      private final GuiButton btnReset;

      private KeyEntry(final KeyBinding p_i45029_2_) {
         this.keybinding = p_i45029_2_;
         this.keyDesc = I18n.format(p_i45029_2_.getKeyDescription());
         this.btnChangeKeyBinding = new GuiButton(0, 0, 0, 75, 20, I18n.format(p_i45029_2_.getKeyDescription())) {
            public void onClick(double p_194829_1_, double p_194829_3_) {
               GuiKeyBindingList.this.controlsScreen.buttonId = p_i45029_2_;
            }
         };
         this.btnReset = new GuiButton(0, 0, 0, 50, 20, I18n.format("controls.reset")) {
            public void onClick(double p_194829_1_, double p_194829_3_) {
               GuiKeyBindingList.this.mc.gameSettings.func_198014_a(p_i45029_2_, p_i45029_2_.getDefault());
               KeyBinding.resetKeyBindingArrayAndHash();
            }
         };
      }

      public void drawEntry(int p_194999_1_, int p_194999_2_, int p_194999_3_, int p_194999_4_, boolean p_194999_5_, float p_194999_6_) {
         int i = this.getY();
         int j = this.getX();
         boolean flag = GuiKeyBindingList.this.controlsScreen.buttonId == this.keybinding;
         GuiKeyBindingList.this.mc.fontRenderer.drawString(this.keyDesc, (float)(j + 90 - GuiKeyBindingList.this.maxListLabelWidth), (float)(i + p_194999_2_ / 2 - GuiKeyBindingList.this.mc.fontRenderer.FONT_HEIGHT / 2), 16777215);
         this.btnReset.x = j + 190;
         this.btnReset.y = i;
         this.btnReset.enabled = !this.keybinding.func_197985_l();
         this.btnReset.render(p_194999_3_, p_194999_4_, p_194999_6_);
         this.btnChangeKeyBinding.x = j + 105;
         this.btnChangeKeyBinding.y = i;
         this.btnChangeKeyBinding.displayString = this.keybinding.func_197978_k();
         boolean flag1 = false;
         if (!this.keybinding.isInvalid()) {
            for(KeyBinding keybinding : GuiKeyBindingList.this.mc.gameSettings.keyBindings) {
               if (keybinding != this.keybinding && this.keybinding.func_197983_b(keybinding)) {
                  flag1 = true;
                  break;
               }
            }
         }

         if (flag) {
            this.btnChangeKeyBinding.displayString = TextFormatting.WHITE + "> " + TextFormatting.YELLOW + this.btnChangeKeyBinding.displayString + TextFormatting.WHITE + " <";
         } else if (flag1) {
            this.btnChangeKeyBinding.displayString = TextFormatting.RED + this.btnChangeKeyBinding.displayString;
         }

         this.btnChangeKeyBinding.render(p_194999_3_, p_194999_4_, p_194999_6_);
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         if (this.btnChangeKeyBinding.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
            return true;
         } else {
            return this.btnReset.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
         }
      }

      public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
         return this.btnChangeKeyBinding.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_) || this.btnReset.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
      }
   }
}
