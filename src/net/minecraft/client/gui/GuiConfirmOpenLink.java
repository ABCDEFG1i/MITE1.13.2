package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiConfirmOpenLink extends GuiYesNo {
   private final String openLinkWarning;
   private final String copyLinkButtonText;
   private final String linkText;
   private boolean showSecurityWarning = true;

   public GuiConfirmOpenLink(GuiYesNoCallback p_i1084_1_, String p_i1084_2_, int p_i1084_3_, boolean p_i1084_4_) {
      super(p_i1084_1_, I18n.format(p_i1084_4_ ? "chat.link.confirmTrusted" : "chat.link.confirm"), p_i1084_2_, p_i1084_3_);
      this.confirmButtonText = I18n.format(p_i1084_4_ ? "chat.link.open" : "gui.yes");
      this.cancelButtonText = I18n.format(p_i1084_4_ ? "gui.cancel" : "gui.no");
      this.copyLinkButtonText = I18n.format("chat.copy");
      this.openLinkWarning = I18n.format("chat.link.warning");
      this.linkText = p_i1084_2_;
   }

   protected void initGui() {
      super.initGui();
      this.buttons.clear();
      this.eventListeners.clear();
      this.addButton(new GuiButton(0, this.width / 2 - 50 - 105, this.height / 6 + 96, 100, 20, this.confirmButtonText) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiConfirmOpenLink.this.parentScreen.confirmResult(true, GuiConfirmOpenLink.this.parentButtonClickedId);
         }
      });
      this.addButton(new GuiButton(2, this.width / 2 - 50, this.height / 6 + 96, 100, 20, this.copyLinkButtonText) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiConfirmOpenLink.this.copyLinkToClipboard();
            GuiConfirmOpenLink.this.parentScreen.confirmResult(false, GuiConfirmOpenLink.this.parentButtonClickedId);
         }
      });
      this.addButton(new GuiButton(1, this.width / 2 - 50 + 105, this.height / 6 + 96, 100, 20, this.cancelButtonText) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiConfirmOpenLink.this.parentScreen.confirmResult(false, GuiConfirmOpenLink.this.parentButtonClickedId);
         }
      });
   }

   public void copyLinkToClipboard() {
      this.mc.keyboardListener.setClipboardString(this.linkText);
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
      if (this.showSecurityWarning) {
         this.drawCenteredString(this.fontRenderer, this.openLinkWarning, this.width / 2, 110, 16764108);
      }

   }

   public void disableSecurityWarning() {
      this.showSecurityWarning = false;
   }
}
