package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.minecraft.client.GameSettings;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiSnooper extends GuiScreen {
   private final GuiScreen lastScreen;
   private final GameSettings game_settings_2;
   private final java.util.List<String> keys = Lists.newArrayList();
   private final java.util.List<String> values = Lists.newArrayList();
   private String title;
   private String[] desc;
   private GuiSnooper.List list;
   private GuiButton toggleButton;

   public GuiSnooper(GuiScreen p_i1061_1_, GameSettings p_i1061_2_) {
      this.lastScreen = p_i1061_1_;
      this.game_settings_2 = p_i1061_2_;
   }

   public IGuiEventListener getFocused() {
      return this.list;
   }

   protected void initGui() {
      this.title = I18n.format("options.snooper.title");
      String s = I18n.format("options.snooper.desc");
      java.util.List<String> list = Lists.newArrayList();

      for(String s1 : this.fontRenderer.listFormattedStringToWidth(s, this.width - 30)) {
         list.add(s1);
      }

      this.desc = list.toArray(new String[list.size()]);
      this.keys.clear();
      this.values.clear();
      GuiButton guibutton = new GuiButton(1, this.width / 2 - 152, this.height - 30, 150, 20, this.game_settings_2.getKeyBinding(GameSettings.Options.SNOOPER_ENABLED)) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiSnooper.this.game_settings_2.setOptionValue(GameSettings.Options.SNOOPER_ENABLED, 1);
            GuiSnooper.this.toggleButton.displayString = GuiSnooper.this.game_settings_2.getKeyBinding(GameSettings.Options.SNOOPER_ENABLED);
         }
      };
      guibutton.enabled = false;
      this.toggleButton = this.addButton(guibutton);
      this.addButton(new GuiButton(2, this.width / 2 + 2, this.height - 30, 150, 20, I18n.format("gui.done")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiSnooper.this.game_settings_2.saveOptions();
            GuiSnooper.this.game_settings_2.saveOptions();
            GuiSnooper.this.mc.displayGuiScreen(GuiSnooper.this.lastScreen);
         }
      });
      boolean flag = this.mc.getIntegratedServer() != null && this.mc.getIntegratedServer().getSnooper() != null;

      for(Entry<String, String> entry : (new TreeMap<>(this.mc.getSnooper().getCurrentStats())).entrySet()) {
         this.keys.add((flag ? "C " : "") + (String)entry.getKey());
         this.values.add(this.fontRenderer.trimStringToWidth(entry.getValue(), this.width - 220));
      }

      if (flag) {
         for(Entry<String, String> entry1 : (new TreeMap<>(this.mc.getIntegratedServer().getSnooper().getCurrentStats())).entrySet()) {
            this.keys.add("S " + (String)entry1.getKey());
            this.values.add(this.fontRenderer.trimStringToWidth(entry1.getValue(), this.width - 220));
         }
      }

      this.list = new GuiSnooper.List();
      this.eventListeners.add(this.list);
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawDefaultBackground();
      this.list.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
      this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 8, 16777215);
      int i = 22;

      for(String s : this.desc) {
         this.drawCenteredString(this.fontRenderer, s, this.width / 2, i, 8421504);
         i += this.fontRenderer.FONT_HEIGHT;
      }

      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
   }

   @OnlyIn(Dist.CLIENT)
   class List extends GuiSlot {
      public List() {
         super(GuiSnooper.this.mc, GuiSnooper.this.width, GuiSnooper.this.height, 80, GuiSnooper.this.height - 40, GuiSnooper.this.fontRenderer.FONT_HEIGHT + 1);
      }

      protected int getSize() {
         return GuiSnooper.this.keys.size();
      }

      protected boolean isSelected(int p_148131_1_) {
         return false;
      }

      protected void drawBackground() {
      }

      protected void drawSlot(int p_192637_1_, int p_192637_2_, int p_192637_3_, int p_192637_4_, int p_192637_5_, int p_192637_6_, float p_192637_7_) {
         GuiSnooper.this.fontRenderer.drawString(GuiSnooper.this.keys.get(p_192637_1_), 10.0F, (float)p_192637_3_, 16777215);
         GuiSnooper.this.fontRenderer.drawString(GuiSnooper.this.values.get(p_192637_1_), 230.0F, (float)p_192637_3_, 16777215);
      }

      protected int getScrollBarX() {
         return this.width - 10;
      }
   }
}
