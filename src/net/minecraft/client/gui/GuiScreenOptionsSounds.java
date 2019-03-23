package net.minecraft.client.gui;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiScreenOptionsSounds extends GuiScreen {
   private final GuiScreen parent;
   private final GameSettings game_settings_4;
   protected String title = "Options";
   private String offDisplayString;

   public GuiScreenOptionsSounds(GuiScreen p_i45025_1_, GameSettings p_i45025_2_) {
      this.parent = p_i45025_1_;
      this.game_settings_4 = p_i45025_2_;
   }

   protected void initGui() {
      this.title = I18n.format("options.sounds.title");
      this.offDisplayString = I18n.format("options.off");
      int i = 0;
      this.addButton(new GuiScreenOptionsSounds.Button(SoundCategory.MASTER.ordinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), SoundCategory.MASTER, true));
      i = i + 2;

      for(SoundCategory soundcategory : SoundCategory.values()) {
         if (soundcategory != SoundCategory.MASTER) {
            this.addButton(new GuiScreenOptionsSounds.Button(soundcategory.ordinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), soundcategory, false));
            ++i;
         }
      }

      int j = this.width / 2 - 75;
      int k = this.height / 6 - 12;
      ++i;
      this.addButton(new GuiOptionButton(201, j, k + 24 * (i >> 1), GameSettings.Options.SHOW_SUBTITLES, this.game_settings_4.getKeyBinding(GameSettings.Options.SHOW_SUBTITLES)) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiScreenOptionsSounds.this.mc.gameSettings.setOptionValue(GameSettings.Options.SHOW_SUBTITLES, 1);
            this.displayString = GuiScreenOptionsSounds.this.mc.gameSettings.getKeyBinding(GameSettings.Options.SHOW_SUBTITLES);
            GuiScreenOptionsSounds.this.mc.gameSettings.saveOptions();
         }
      });
      this.addButton(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiScreenOptionsSounds.this.mc.gameSettings.saveOptions();
            GuiScreenOptionsSounds.this.mc.displayGuiScreen(GuiScreenOptionsSounds.this.parent);
         }
      });
   }

   public void close() {
      this.mc.gameSettings.saveOptions();
      super.close();
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 15, 16777215);
      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
   }

   protected String getDisplayString(SoundCategory p_184097_1_) {
      float f = this.game_settings_4.getSoundLevel(p_184097_1_);
      return f == 0.0F ? this.offDisplayString : (int)(f * 100.0F) + "%";
   }

   @OnlyIn(Dist.CLIENT)
   class Button extends GuiButton {
      private final SoundCategory category;
      private final String categoryName;
      public double volume;
      public boolean pressed;

      public Button(int p_i46744_2_, int p_i46744_3_, int p_i46744_4_, SoundCategory p_i46744_5_, boolean p_i46744_6_) {
         super(p_i46744_2_, p_i46744_3_, p_i46744_4_, p_i46744_6_ ? 310 : 150, 20, "");
         this.category = p_i46744_5_;
         this.categoryName = I18n.format("soundCategory." + p_i46744_5_.getName());
         this.displayString = this.categoryName + ": " + GuiScreenOptionsSounds.this.getDisplayString(p_i46744_5_);
         this.volume = (double)GuiScreenOptionsSounds.this.game_settings_4.getSoundLevel(p_i46744_5_);
      }

      protected int getHoverState(boolean p_146114_1_) {
         return 0;
      }

      protected void renderBg(Minecraft p_146119_1_, int p_146119_2_, int p_146119_3_) {
         if (this.visible) {
            if (this.pressed) {
               this.volume = (double)((float)(p_146119_2_ - (this.x + 4)) / (float)(this.width - 8));
               this.volume = MathHelper.clamp(this.volume, 0.0D, 1.0D);
               p_146119_1_.gameSettings.setSoundLevel(this.category, (float)this.volume);
               p_146119_1_.gameSettings.saveOptions();
               this.displayString = this.categoryName + ": " + GuiScreenOptionsSounds.this.getDisplayString(this.category);
            }

            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.x + (int)(this.volume * (double)(this.width - 8)), this.y, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.x + (int)(this.volume * (double)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
         }
      }

      public void onClick(double p_194829_1_, double p_194829_3_) {
         this.volume = (p_194829_1_ - (double)(this.x + 4)) / (double)(this.width - 8);
         this.volume = MathHelper.clamp(this.volume, 0.0D, 1.0D);
         GuiScreenOptionsSounds.this.mc.gameSettings.setSoundLevel(this.category, (float)this.volume);
         GuiScreenOptionsSounds.this.mc.gameSettings.saveOptions();
         this.displayString = this.categoryName + ": " + GuiScreenOptionsSounds.this.getDisplayString(this.category);
         this.pressed = true;
      }

      public void playPressSound(SoundHandler p_146113_1_) {
      }

      public void onRelease(double p_194831_1_, double p_194831_3_) {
         if (this.pressed) {
            GuiScreenOptionsSounds.this.mc.getSoundHandler().play(SimpleSound.func_184371_a(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         }

         this.pressed = false;
      }
   }
}
