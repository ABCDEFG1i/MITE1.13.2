package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiMemoryErrorScreen extends GuiScreen {
   protected void initGui() {
      this.addButton(new GuiOptionButton(0, this.width / 2 - 155, this.height / 4 + 120 + 12, I18n.format("gui.toTitle")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiMemoryErrorScreen.this.mc.displayGuiScreen(new GuiMainMenu());
         }
      });
      this.addButton(new GuiOptionButton(1, this.width / 2 - 155 + 160, this.height / 4 + 120 + 12, I18n.format("menu.quit")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiMemoryErrorScreen.this.mc.shutdown();
         }
      });
   }

   public boolean allowCloseWithEscape() {
      return false;
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, "Out of memory!", this.width / 2, this.height / 4 - 60 + 20, 16777215);
      this.drawString(this.fontRenderer, "Minecraft has run out of memory.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 0, 10526880);
      this.drawString(this.fontRenderer, "This could be caused by a bug in the game or by the", this.width / 2 - 140, this.height / 4 - 60 + 60 + 18, 10526880);
      this.drawString(this.fontRenderer, "Java Virtual Machine not being allocated enough", this.width / 2 - 140, this.height / 4 - 60 + 60 + 27, 10526880);
      this.drawString(this.fontRenderer, "memory.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 36, 10526880);
      this.drawString(this.fontRenderer, "To prevent level corruption, the current game has quit.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 54, 10526880);
      this.drawString(this.fontRenderer, "We've tried to free up enough memory to let you go back to", this.width / 2 - 140, this.height / 4 - 60 + 60 + 63, 10526880);
      this.drawString(this.fontRenderer, "the main menu and back to playing, but this may not have worked.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 72, 10526880);
      this.drawString(this.fontRenderer, "Please restart the game if you see this message again.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 81, 10526880);
      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
   }
}
