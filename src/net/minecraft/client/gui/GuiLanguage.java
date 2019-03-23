package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiLanguage extends GuiScreen {
   protected GuiScreen parentScreen;
   private GuiLanguage.List list;
   private final GameSettings game_settings_3;
   private final LanguageManager languageManager;
   private GuiOptionButton field_211832_i;
   private GuiOptionButton confirmSettingsBtn;

   public GuiLanguage(GuiScreen p_i1043_1_, GameSettings p_i1043_2_, LanguageManager p_i1043_3_) {
      this.parentScreen = p_i1043_1_;
      this.game_settings_3 = p_i1043_2_;
      this.languageManager = p_i1043_3_;
   }

   public IGuiEventListener getFocused() {
      return this.list;
   }

   protected void initGui() {
      this.list = new GuiLanguage.List(this.mc);
      this.eventListeners.add(this.list);
      this.field_211832_i = this.addButton(new GuiOptionButton(100, this.width / 2 - 155, this.height - 38, GameSettings.Options.FORCE_UNICODE_FONT, this.game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT)) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiLanguage.this.game_settings_3.setOptionValue(this.getOption(), 1);
            this.displayString = GuiLanguage.this.game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT);
            GuiLanguage.this.func_195181_h();
         }
      });
      this.confirmSettingsBtn = this.addButton(new GuiOptionButton(6, this.width / 2 - 155 + 160, this.height - 38, I18n.format("gui.done")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiLanguage.this.mc.displayGuiScreen(GuiLanguage.this.parentScreen);
         }
      });
      super.initGui();
   }

   private void func_195181_h() {
      this.mc.mainWindow.updateSize();
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.list.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
      this.drawCenteredString(this.fontRenderer, I18n.format("options.language"), this.width / 2, 16, 16777215);
      this.drawCenteredString(this.fontRenderer, "(" + I18n.format("options.languageWarning") + ")", this.width / 2, this.height - 56, 8421504);
      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
   }

   @OnlyIn(Dist.CLIENT)
   class List extends GuiSlot {
      private final java.util.List<String> langCodeList = Lists.newArrayList();
      private final Map<String, Language> languageMap = Maps.newHashMap();

      public List(Minecraft p_i45519_2_) {
         super(p_i45519_2_, GuiLanguage.this.width, GuiLanguage.this.height, 32, GuiLanguage.this.height - 65 + 4, 18);

         for(Language language : GuiLanguage.this.languageManager.getLanguages()) {
            this.languageMap.put(language.getLanguageCode(), language);
            this.langCodeList.add(language.getLanguageCode());
         }

      }

      protected int getSize() {
         return this.langCodeList.size();
      }

      protected boolean mouseClicked(int p_195078_1_, int p_195078_2_, double p_195078_3_, double p_195078_5_) {
         Language language = this.languageMap.get(this.langCodeList.get(p_195078_1_));
         GuiLanguage.this.languageManager.setCurrentLanguage(language);
         GuiLanguage.this.game_settings_3.language = language.getLanguageCode();
         this.mc.refreshResources();
         GuiLanguage.this.fontRenderer.setBidiFlag(GuiLanguage.this.languageManager.isCurrentLanguageBidirectional());
         GuiLanguage.this.confirmSettingsBtn.displayString = I18n.format("gui.done");
         GuiLanguage.this.field_211832_i.displayString = GuiLanguage.this.game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT);
         GuiLanguage.this.game_settings_3.saveOptions();
         GuiLanguage.this.func_195181_h();
         return true;
      }

      protected boolean isSelected(int p_148131_1_) {
         return this.langCodeList.get(p_148131_1_).equals(GuiLanguage.this.languageManager.getCurrentLanguage().getLanguageCode());
      }

      protected int getContentHeight() {
         return this.getSize() * 18;
      }

      protected void drawBackground() {
         GuiLanguage.this.drawDefaultBackground();
      }

      protected void drawSlot(int p_192637_1_, int p_192637_2_, int p_192637_3_, int p_192637_4_, int p_192637_5_, int p_192637_6_, float p_192637_7_) {
         GuiLanguage.this.fontRenderer.setBidiFlag(true);
         this.drawCenteredString(GuiLanguage.this.fontRenderer, this.languageMap.get(this.langCodeList.get(p_192637_1_)).toString(), this.width / 2, p_192637_3_ + 1, 16777215);
         GuiLanguage.this.fontRenderer.setBidiFlag(GuiLanguage.this.languageManager.getCurrentLanguage().isBidirectional());
      }
   }
}
