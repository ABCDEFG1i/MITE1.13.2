package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiScreenServerList extends GuiScreen {
   private GuiButton field_195170_a;
   private final GuiScreen lastScreen;
   private final ServerData serverData;
   private GuiTextField ipEdit;

   public GuiScreenServerList(GuiScreen p_i1031_1_, ServerData p_i1031_2_) {
      this.lastScreen = p_i1031_1_;
      this.serverData = p_i1031_2_;
   }

   public void tick() {
      this.ipEdit.tick();
   }

   protected void initGui() {
      this.mc.keyboardListener.enableRepeatEvents(true);
      this.field_195170_a = this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.format("selectServer.select")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiScreenServerList.this.func_195167_h();
         }
      });
      this.addButton(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiScreenServerList.this.lastScreen.confirmResult(false, 0);
         }
      });
      this.ipEdit = new GuiTextField(2, this.fontRenderer, this.width / 2 - 100, 116, 200, 20);
      this.ipEdit.setMaxStringLength(128);
      this.ipEdit.setFocused(true);
      this.ipEdit.setText(this.mc.gameSettings.lastServer);
      this.eventListeners.add(this.ipEdit);
      this.setFocused(this.ipEdit);
      this.func_195168_i();
   }

   public void onResize(Minecraft p_175273_1_, int p_175273_2_, int p_175273_3_) {
      String s = this.ipEdit.getText();
      this.setWorldAndResolution(p_175273_1_, p_175273_2_, p_175273_3_);
      this.ipEdit.setText(s);
   }

   private void func_195167_h() {
      this.serverData.serverIP = this.ipEdit.getText();
      this.lastScreen.confirmResult(true, 0);
   }

   public void onGuiClosed() {
      this.mc.keyboardListener.enableRepeatEvents(false);
      this.mc.gameSettings.lastServer = this.ipEdit.getText();
      this.mc.gameSettings.saveOptions();
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      if (this.ipEdit.charTyped(p_charTyped_1_, p_charTyped_2_)) {
         this.func_195168_i();
         return true;
      } else {
         return false;
      }
   }

   private void func_195168_i() {
      this.field_195170_a.enabled = !this.ipEdit.getText().isEmpty() && this.ipEdit.getText().split(":").length > 0;
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
         if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
            this.func_195168_i();
            return true;
         } else {
            return false;
         }
      } else {
         this.func_195167_h();
         return true;
      }
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, I18n.format("selectServer.direct"), this.width / 2, 20, 16777215);
      this.drawString(this.fontRenderer, I18n.format("addServer.enterIp"), this.width / 2 - 100, 100, 10526880);
      this.ipEdit.drawTextField(p_73863_1_, p_73863_2_, p_73863_3_);
      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
   }
}
