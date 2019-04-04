package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiErrorScreen extends GuiScreen {
   private final String title;
   private final String message;

   public GuiErrorScreen(String p_i46319_1_, String p_i46319_2_) {
      this.title = p_i46319_1_;
      this.message = p_i46319_2_;
   }

   protected void initGui() {
      super.initGui();
      this.addButton(new GuiButton(0, this.width / 2 - 100, 140, I18n.format("gui.cancel")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiErrorScreen.this.mc.displayGuiScreen(null);
         }
      });
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawGradientRect(0, 0, this.width, this.height, -12574688, -11530224);
      this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 90, 16777215);
      this.drawCenteredString(this.fontRenderer, this.message, this.width / 2, 110, 16777215);
      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
   }

   public boolean allowCloseWithEscape() {
      return false;
   }
}
