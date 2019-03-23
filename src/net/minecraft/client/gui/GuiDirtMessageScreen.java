package net.minecraft.client.gui;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiDirtMessageScreen extends GuiScreen {
   private final String field_205029_a;

   public GuiDirtMessageScreen(String p_i48952_1_) {
      this.field_205029_a = p_i48952_1_;
   }

   public boolean allowCloseWithEscape() {
      return false;
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawBackground(0);
      this.drawCenteredString(this.fontRenderer, this.field_205029_a, this.width / 2, 70, 16777215);
      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
   }
}
