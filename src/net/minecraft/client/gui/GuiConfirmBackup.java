package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiConfirmBackup extends GuiScreen {
   private final GuiScreen parentScreen;
   protected GuiConfirmBackup.ICallback callback;
   protected String title;
   private final String message;
   private final List<String> wrappedMessage = Lists.newArrayList();
   protected String confirmText;
   protected String skipBackupText;
   protected String cancelText;

   public GuiConfirmBackup(GuiScreen p_i49803_1_, GuiConfirmBackup.ICallback p_i49803_2_, String p_i49803_3_, String p_i49803_4_) {
      this.parentScreen = p_i49803_1_;
      this.callback = p_i49803_2_;
      this.title = p_i49803_3_;
      this.message = p_i49803_4_;
      this.confirmText = I18n.format("selectWorld.backupJoinConfirmButton");
      this.skipBackupText = I18n.format("selectWorld.backupJoinSkipButton");
      this.cancelText = I18n.format("gui.cancel");
   }

   protected void initGui() {
      super.initGui();
      this.wrappedMessage.clear();
      this.wrappedMessage.addAll(this.fontRenderer.listFormattedStringToWidth(this.message, this.width - 50));
      this.addButton(new GuiOptionButton(0, this.width / 2 - 155, 100 + (this.wrappedMessage.size() + 1) * this.fontRenderer.FONT_HEIGHT, this.confirmText) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiConfirmBackup.this.callback.proceed(true);
         }
      });
      this.addButton(new GuiOptionButton(1, this.width / 2 - 155 + 160, 100 + (this.wrappedMessage.size() + 1) * this.fontRenderer.FONT_HEIGHT, this.skipBackupText) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiConfirmBackup.this.callback.proceed(false);
         }
      });
      this.addButton(new GuiButton(1, this.width / 2 - 155 + 80, 124 + (this.wrappedMessage.size() + 1) * this.fontRenderer.FONT_HEIGHT, 150, 20, this.cancelText) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiConfirmBackup.this.mc.displayGuiScreen(GuiConfirmBackup.this.parentScreen);
         }
      });
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 70, 16777215);
      int i = 90;

      for(String s : this.wrappedMessage) {
         this.drawCenteredString(this.fontRenderer, s, this.width / 2, i, 16777215);
         i += this.fontRenderer.FONT_HEIGHT;
      }

      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
   }

   public boolean allowCloseWithEscape() {
      return false;
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         this.mc.displayGuiScreen(this.parentScreen);
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public interface ICallback {
      void proceed(boolean p_proceed_1_);
   }
}
