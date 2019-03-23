package net.minecraft.client.gui;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenChatOptions extends GuiScreen {
   private static final GameSettings.Options[] CHAT_OPTIONS = new GameSettings.Options[]{GameSettings.Options.CHAT_VISIBILITY, GameSettings.Options.CHAT_COLOR, GameSettings.Options.CHAT_LINKS, GameSettings.Options.CHAT_OPACITY, GameSettings.Options.CHAT_LINKS_PROMPT, GameSettings.Options.CHAT_SCALE, GameSettings.Options.CHAT_HEIGHT_FOCUSED, GameSettings.Options.CHAT_HEIGHT_UNFOCUSED, GameSettings.Options.CHAT_WIDTH, GameSettings.Options.REDUCED_DEBUG_INFO, GameSettings.Options.NARRATOR, GameSettings.Options.AUTO_SUGGESTIONS};
   private final GuiScreen parentScreen;
   private final GameSettings game_settings;
   private String chatTitle;
   private GuiOptionButton narratorButton;

   public ScreenChatOptions(GuiScreen p_i1023_1_, GameSettings p_i1023_2_) {
      this.parentScreen = p_i1023_1_;
      this.game_settings = p_i1023_2_;
   }

   protected void initGui() {
      this.chatTitle = I18n.format("options.chat.title");
      int i = 0;

      for(GameSettings.Options gamesettings$options : CHAT_OPTIONS) {
         if (gamesettings$options.isFloat()) {
            this.addButton(new GuiOptionSlider(gamesettings$options.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), gamesettings$options));
         } else {
            GuiOptionButton guioptionbutton = new GuiOptionButton(gamesettings$options.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), gamesettings$options, this.game_settings.getKeyBinding(gamesettings$options)) {
               public void onClick(double p_194829_1_, double p_194829_3_) {
                  ScreenChatOptions.this.game_settings.setOptionValue(this.getOption(), 1);
                  this.displayString = ScreenChatOptions.this.game_settings.getKeyBinding(GameSettings.Options.byOrdinal(this.id));
               }
            };
            this.addButton(guioptionbutton);
            if (gamesettings$options == GameSettings.Options.NARRATOR) {
               this.narratorButton = guioptionbutton;
               guioptionbutton.enabled = NarratorChatListener.INSTANCE.isActive();
            }
         }

         ++i;
      }

      this.addButton(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 144, I18n.format("gui.done")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            ScreenChatOptions.this.mc.gameSettings.saveOptions();
            ScreenChatOptions.this.mc.displayGuiScreen(ScreenChatOptions.this.parentScreen);
         }
      });
   }

   public void close() {
      this.mc.gameSettings.saveOptions();
      super.close();
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, this.chatTitle, this.width / 2, 20, 16777215);
      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
   }

   public void updateNarratorButton() {
      this.narratorButton.displayString = this.game_settings.getKeyBinding(GameSettings.Options.byOrdinal(this.narratorButton.id));
   }
}
