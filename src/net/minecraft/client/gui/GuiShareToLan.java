package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiShareToLan extends GuiScreen {
   private final GuiScreen lastScreen;
   private GuiButton allowCheatsButton;
   private GuiButton gameModeButton;
   private String gameMode = "survival";
   private boolean allowCheats = false;

   public GuiShareToLan(GuiScreen p_i1055_1_) {
      this.lastScreen = p_i1055_1_;
   }

   protected void initGui() {
      this.addButton(new GuiButton(101, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("lanServer.start")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiShareToLan.this.mc.displayGuiScreen(null);
            int i = HttpUtil.getSuitableLanPort();
            ITextComponent itextcomponent;
            if (GuiShareToLan.this.mc.getIntegratedServer().func_195565_a(GameType.getByName(GuiShareToLan.this.gameMode), GuiShareToLan.this.allowCheats, i)) {
               itextcomponent = new TextComponentTranslation("commands.publish.started", i);
            } else {
               itextcomponent = new TextComponentTranslation("commands.publish.failed");
            }

            GuiShareToLan.this.mc.ingameGUI.getChatGUI().printChatMessage(itextcomponent);
         }
      });
      this.addButton(new GuiButton(102, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiShareToLan.this.mc.displayGuiScreen(GuiShareToLan.this.lastScreen);
         }
      });
      this.gameModeButton = this.addButton(new GuiButton(104, this.width / 2 - 155, 100, 150, 20, I18n.format("selectWorld.gameMode")) {
      });
      this.gameModeButton.enabled = false;
      this.allowCheatsButton = this.addButton(new GuiButton(103, this.width / 2 + 5, 100, 150, 20, I18n.format("selectWorld.allowCommands")) {
      });
      this.allowCheatsButton.enabled = false;
      this.updateDisplayNames();
   }

   private void updateDisplayNames() {
      this.gameModeButton.displayString = I18n.format("selectWorld.gameMode") + ": " + I18n.format("selectWorld.gameMode." + this.gameMode);
      this.allowCheatsButton.displayString = I18n.format("selectWorld.allowCommands") + " ";
      if (this.allowCheats) {
         this.allowCheatsButton.displayString = this.allowCheatsButton.displayString + I18n.format("options.on");
      } else {
         this.allowCheatsButton.displayString = this.allowCheatsButton.displayString + I18n.format("options.off");
      }

   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, I18n.format("lanServer.title"), this.width / 2, 50, 16777215);
      this.drawCenteredString(this.fontRenderer, I18n.format("lanServer.otherPlayers"), this.width / 2, 82, 16777215);
      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
   }
}
