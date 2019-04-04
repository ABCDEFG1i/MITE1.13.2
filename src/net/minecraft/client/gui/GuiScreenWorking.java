package net.minecraft.client.gui;

import java.util.Objects;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiScreenWorking extends GuiScreen implements IProgressUpdate {
   private String title = "";
   private String stage = "";
   private int progress;
   private boolean doneWorking;

   public boolean allowCloseWithEscape() {
      return false;
   }

   public void func_200210_a(ITextComponent p_200210_1_) {
      this.func_200211_b(p_200210_1_);
   }

   public void func_200211_b(ITextComponent p_200211_1_) {
      this.title = p_200211_1_.getFormattedText();
      this.func_200209_c(new TextComponentTranslation("progress.working"));
   }

   public void func_200209_c(ITextComponent p_200209_1_) {
      this.stage = p_200209_1_.getFormattedText();
      this.setLoadingProgress(0);
   }

   public void setLoadingProgress(int p_73718_1_) {
      this.progress = p_73718_1_;
   }

   public void setDoneWorking() {
      this.doneWorking = true;
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      if (this.doneWorking) {
         if (!this.mc.isConnectedToRealms()) {
            this.mc.displayGuiScreen(null);
         }

      } else {
         this.drawDefaultBackground();
         this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 70, 16777215);
         if (!Objects.equals(this.stage, "") && this.progress != 0) {
            this.drawCenteredString(this.fontRenderer, this.stage + " " + this.progress + "%", this.width / 2, 90, 16777215);
         }

         super.render(p_73863_1_, p_73863_2_, p_73863_3_);
      }
   }
}
