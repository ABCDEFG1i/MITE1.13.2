package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiDownloadTerrain extends GuiScreen {
   public boolean allowCloseWithEscape() {
      return false;
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawBackground(0);
      this.drawCenteredString(this.fontRenderer, I18n.format("multiplayer.downloadingTerrain"), this.width / 2, this.height / 2 - 50, 16777215);
      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
   }

   public boolean doesGuiPauseGame() {
      return false;
   }
}
