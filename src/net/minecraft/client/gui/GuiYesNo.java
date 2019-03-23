package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiYesNo extends GuiScreen {
   protected GuiYesNoCallback parentScreen;
   protected String messageLine1;
   private final String messageLine2;
   private final List<String> listLines = Lists.newArrayList();
   protected String confirmButtonText;
   protected String cancelButtonText;
   protected int parentButtonClickedId;
   private int ticksUntilEnable;

   public GuiYesNo(GuiYesNoCallback p_i1082_1_, String p_i1082_2_, String p_i1082_3_, int p_i1082_4_) {
      this.parentScreen = p_i1082_1_;
      this.messageLine1 = p_i1082_2_;
      this.messageLine2 = p_i1082_3_;
      this.parentButtonClickedId = p_i1082_4_;
      this.confirmButtonText = I18n.format("gui.yes");
      this.cancelButtonText = I18n.format("gui.no");
   }

   public GuiYesNo(GuiYesNoCallback p_i1083_1_, String p_i1083_2_, String p_i1083_3_, String p_i1083_4_, String p_i1083_5_, int p_i1083_6_) {
      this.parentScreen = p_i1083_1_;
      this.messageLine1 = p_i1083_2_;
      this.messageLine2 = p_i1083_3_;
      this.confirmButtonText = p_i1083_4_;
      this.cancelButtonText = p_i1083_5_;
      this.parentButtonClickedId = p_i1083_6_;
   }

   protected void initGui() {
      super.initGui();
      this.addButton(new GuiOptionButton(0, this.width / 2 - 155, this.height / 6 + 96, this.confirmButtonText) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiYesNo.this.parentScreen.confirmResult(true, GuiYesNo.this.parentButtonClickedId);
         }
      });
      this.addButton(new GuiOptionButton(1, this.width / 2 - 155 + 160, this.height / 6 + 96, this.cancelButtonText) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiYesNo.this.parentScreen.confirmResult(false, GuiYesNo.this.parentButtonClickedId);
         }
      });
      this.listLines.clear();
      this.listLines.addAll(this.fontRenderer.listFormattedStringToWidth(this.messageLine2, this.width - 50));
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, this.messageLine1, this.width / 2, 70, 16777215);
      int i = 90;

      for(String s : this.listLines) {
         this.drawCenteredString(this.fontRenderer, s, this.width / 2, i, 16777215);
         i += this.fontRenderer.FONT_HEIGHT;
      }

      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
   }

   public void setButtonDelay(int p_146350_1_) {
      this.ticksUntilEnable = p_146350_1_;

      for(GuiButton guibutton : this.buttons) {
         guibutton.enabled = false;
      }

   }

   public void tick() {
      super.tick();
      if (--this.ticksUntilEnable == 0) {
         for(GuiButton guibutton : this.buttons) {
            guibutton.enabled = true;
         }
      }

   }

   public boolean allowCloseWithEscape() {
      return false;
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         this.parentScreen.confirmResult(false, this.parentButtonClickedId);
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }
}
