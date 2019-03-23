package net.minecraft.client.gui;

import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiDisconnected extends GuiScreen {
   private final String reason;
   private final ITextComponent message;
   private List<String> multilineMessage;
   private final GuiScreen parentScreen;
   private int textHeight;

   public GuiDisconnected(GuiScreen p_i45020_1_, String p_i45020_2_, ITextComponent p_i45020_3_) {
      this.parentScreen = p_i45020_1_;
      this.reason = I18n.format(p_i45020_2_);
      this.message = p_i45020_3_;
   }

   public boolean allowCloseWithEscape() {
      return false;
   }

   protected void initGui() {
      this.multilineMessage = this.fontRenderer.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
      this.textHeight = this.multilineMessage.size() * this.fontRenderer.FONT_HEIGHT;
      this.addButton(new GuiButton(0, this.width / 2 - 100, Math.min(this.height / 2 + this.textHeight / 2 + this.fontRenderer.FONT_HEIGHT, this.height - 30), I18n.format("gui.toMenu")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiDisconnected.this.mc.displayGuiScreen(GuiDisconnected.this.parentScreen);
         }
      });
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, this.reason, this.width / 2, this.height / 2 - this.textHeight / 2 - this.fontRenderer.FONT_HEIGHT * 2, 11184810);
      int i = this.height / 2 - this.textHeight / 2;
      if (this.multilineMessage != null) {
         for(String s : this.multilineMessage) {
            this.drawCenteredString(this.fontRenderer, s, this.width / 2, i, 16777215);
            i += this.fontRenderer.FONT_HEIGHT;
         }
      }

      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
   }
}
