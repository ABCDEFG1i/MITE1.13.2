package net.minecraft.client.gui;

import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.gui.advancements.GuiScreenAdvancements;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiIngameMenu extends GuiScreen {
   protected void initGui() {
      int i = -16;
      int j = 98;
      GuiButton guibutton = this.addButton(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + -16, I18n.format("menu.returnToMenu")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            boolean flag = GuiIngameMenu.this.mc.isIntegratedServerRunning();
            boolean flag1 = GuiIngameMenu.this.mc.isConnectedToRealms();
            this.enabled = false;
            GuiIngameMenu.this.mc.world.sendQuittingDisconnectingPacket();
            if (flag) {
               GuiIngameMenu.this.mc.loadWorld((WorldClient)null, new GuiDirtMessageScreen(I18n.format("menu.savingLevel")));
            } else {
               GuiIngameMenu.this.mc.loadWorld((WorldClient)null);
            }

            if (flag) {
               GuiIngameMenu.this.mc.displayGuiScreen(new GuiMainMenu());
            } else if (flag1) {
               RealmsBridge realmsbridge = new RealmsBridge();
               realmsbridge.switchToRealms(new GuiMainMenu());
            } else {
               GuiIngameMenu.this.mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
            }

         }
      });
      if (!this.mc.isIntegratedServerRunning()) {
         guibutton.displayString = I18n.format("menu.disconnect");
      }

      this.addButton(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 24 + -16, I18n.format("menu.returnToGame")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiIngameMenu.this.mc.displayGuiScreen((GuiScreen)null);
            GuiIngameMenu.this.mc.mouseHelper.grabMouse();
         }
      });
      this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + -16, 98, 20, I18n.format("menu.options")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiIngameMenu.this.mc.displayGuiScreen(new GuiOptions(GuiIngameMenu.this, GuiIngameMenu.this.mc.gameSettings));
         }
      });
      GuiButton guibutton1 = this.addButton(new GuiButton(7, this.width / 2 + 2, this.height / 4 + 96 + -16, 98, 20, I18n.format("menu.shareToLan")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiIngameMenu.this.mc.displayGuiScreen(new GuiShareToLan(GuiIngameMenu.this));
         }
      });
      guibutton1.enabled = this.mc.isSingleplayer() && !this.mc.getIntegratedServer().getPublic();
      this.addButton(new GuiButton(5, this.width / 2 - 100, this.height / 4 + 48 + -16, 98, 20, I18n.format("gui.advancements")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiIngameMenu.this.mc.displayGuiScreen(new GuiScreenAdvancements(GuiIngameMenu.this.mc.player.connection.getAdvancementManager()));
         }
      });
      this.addButton(new GuiButton(6, this.width / 2 + 2, this.height / 4 + 48 + -16, 98, 20, I18n.format("gui.stats")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiIngameMenu.this.mc.displayGuiScreen(new GuiStats(GuiIngameMenu.this, GuiIngameMenu.this.mc.player.getStatFileWriter()));
         }
      });
   }

   public void tick() {
      super.tick();
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, I18n.format("menu.game"), this.width / 2, 40, 16777215);
      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
   }
}
