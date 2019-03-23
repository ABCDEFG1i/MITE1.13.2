package net.minecraft.client.gui;

import net.minecraft.client.GameSettings;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiCustomizeSkin extends GuiScreen {
   private final GuiScreen parentScreen;
   private String title;

   public GuiCustomizeSkin(GuiScreen p_i45516_1_) {
      this.parentScreen = p_i45516_1_;
   }

   protected void initGui() {
      int i = 0;
      this.title = I18n.format("options.skinCustomisation.title");

      for(EnumPlayerModelParts enumplayermodelparts : EnumPlayerModelParts.values()) {
         this.addButton(new GuiCustomizeSkin.ButtonPart(enumplayermodelparts.getPartId(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, enumplayermodelparts));
         ++i;
      }

      this.addButton(new GuiOptionButton(199, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), GameSettings.Options.MAIN_HAND, this.mc.gameSettings.getKeyBinding(GameSettings.Options.MAIN_HAND)) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiCustomizeSkin.this.mc.gameSettings.setOptionValue(GameSettings.Options.MAIN_HAND, 1);
            this.displayString = GuiCustomizeSkin.this.mc.gameSettings.getKeyBinding(GameSettings.Options.MAIN_HAND);
            GuiCustomizeSkin.this.mc.gameSettings.sendSettingsToServer();
         }
      });
      ++i;
      if (i % 2 == 1) {
         ++i;
      }

      this.addButton(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), I18n.format("gui.done")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiCustomizeSkin.this.mc.gameSettings.saveOptions();
            GuiCustomizeSkin.this.mc.displayGuiScreen(GuiCustomizeSkin.this.parentScreen);
         }
      });
   }

   public void close() {
      this.mc.gameSettings.saveOptions();
      super.close();
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 20, 16777215);
      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
   }

   private String getMessage(EnumPlayerModelParts p_175358_1_) {
      String s;
      if (this.mc.gameSettings.getModelParts().contains(p_175358_1_)) {
         s = I18n.format("options.on");
      } else {
         s = I18n.format("options.off");
      }

      return p_175358_1_.getName().getFormattedText() + ": " + s;
   }

   @OnlyIn(Dist.CLIENT)
   class ButtonPart extends GuiButton {
      private final EnumPlayerModelParts playerModelParts;

      private ButtonPart(int p_i45514_2_, int p_i45514_3_, int p_i45514_4_, int p_i45514_5_, int p_i45514_6_, EnumPlayerModelParts p_i45514_7_) {
         super(p_i45514_2_, p_i45514_3_, p_i45514_4_, p_i45514_5_, p_i45514_6_, GuiCustomizeSkin.this.getMessage(p_i45514_7_));
         this.playerModelParts = p_i45514_7_;
      }

      public void onClick(double p_194829_1_, double p_194829_3_) {
         GuiCustomizeSkin.this.mc.gameSettings.switchModelPartEnabled(this.playerModelParts);
         this.displayString = GuiCustomizeSkin.this.getMessage(this.playerModelParts);
      }
   }
}
