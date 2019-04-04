package net.minecraft.client.gui;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiGameOver extends GuiScreen {
   private int enableButtonsTimer;
   private final ITextComponent causeOfDeath;

   public GuiGameOver(@Nullable ITextComponent p_i46598_1_) {
      this.causeOfDeath = p_i46598_1_;
   }

   protected void initGui() {
      this.enableButtonsTimer = 0;
      String s;
      String s1;
      if (this.mc.world.getWorldInfo().isHardcoreModeEnabled()) {
         s = I18n.format("deathScreen.spectate");
         s1 = I18n.format("deathScreen." + (this.mc.isIntegratedServerRunning() ? "deleteWorld" : "leaveServer"));
      } else {
         s = I18n.format("deathScreen.respawn");
         s1 = I18n.format("deathScreen.titleScreen");
      }

      this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 72, s) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiGameOver.this.mc.player.respawnPlayer();
            GuiGameOver.this.mc.displayGuiScreen(null);
         }
      });
      GuiButton guibutton = this.addButton(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 96, s1) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            if (GuiGameOver.this.mc.world.getWorldInfo().isHardcoreModeEnabled()) {
               GuiGameOver.this.mc.displayGuiScreen(new GuiMainMenu());
            } else {
               GuiYesNo guiyesno = new GuiYesNo(GuiGameOver.this, I18n.format("deathScreen.quit.confirm"), "", I18n.format("deathScreen.titleScreen"), I18n.format("deathScreen.respawn"), 0);
               GuiGameOver.this.mc.displayGuiScreen(guiyesno);
               guiyesno.setButtonDelay(20);
            }
         }
      });
      if (!this.mc.world.getWorldInfo().isHardcoreModeEnabled() && this.mc.getSession() == null) {
         guibutton.enabled = false;
      }

      for(GuiButton guibutton1 : this.buttons) {
         guibutton1.enabled = false;
      }

   }

   public boolean allowCloseWithEscape() {
      return false;
   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      if (p_confirmResult_2_ == 31102009) {
         super.confirmResult(p_confirmResult_1_, p_confirmResult_2_);
      } else if (p_confirmResult_1_) {
         if (this.mc.world != null) {
            this.mc.world.sendQuittingDisconnectingPacket();
         }

         this.mc.loadWorld(null, new GuiDirtMessageScreen(I18n.format("menu.savingLevel")));
         this.mc.displayGuiScreen(new GuiMainMenu());
      } else {
         this.mc.player.respawnPlayer();
         this.mc.displayGuiScreen(null);
      }

   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      boolean flag = this.mc.world.getWorldInfo().isHardcoreModeEnabled();
      this.drawGradientRect(0, 0, this.width, this.height, 1615855616, -1602211792);
      GlStateManager.pushMatrix();
      GlStateManager.scalef(2.0F, 2.0F, 2.0F);
      this.drawCenteredString(this.fontRenderer, I18n.format(flag ? "deathScreen.title.hardcore" : "deathScreen.title"), this.width / 2 / 2, 30, 16777215);
      GlStateManager.popMatrix();
      if (this.causeOfDeath != null) {
         this.drawCenteredString(this.fontRenderer, this.causeOfDeath.getFormattedText(), this.width / 2, 85, 16777215);
      }

      this.drawCenteredString(this.fontRenderer, I18n.format("deathScreen.score") + ": " + TextFormatting.YELLOW + this.mc.player.getScore(), this.width / 2, 100, 16777215);
      if (this.causeOfDeath != null && p_73863_2_ > 85 && p_73863_2_ < 85 + this.fontRenderer.FONT_HEIGHT) {
         ITextComponent itextcomponent = this.getClickedComponentAt(p_73863_1_);
         if (itextcomponent != null && itextcomponent.getStyle().getHoverEvent() != null) {
            this.handleComponentHover(itextcomponent, p_73863_1_, p_73863_2_);
         }
      }

      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
   }

   @Nullable
   public ITextComponent getClickedComponentAt(int p_184870_1_) {
      if (this.causeOfDeath == null) {
         return null;
      } else {
         int i = this.mc.fontRenderer.getStringWidth(this.causeOfDeath.getFormattedText());
         int j = this.width / 2 - i / 2;
         int k = this.width / 2 + i / 2;
         int l = j;
         if (p_184870_1_ >= j && p_184870_1_ <= k) {
            for(ITextComponent itextcomponent : this.causeOfDeath) {
               l += this.mc.fontRenderer.getStringWidth(GuiUtilRenderComponents.removeTextColorsIfConfigured(itextcomponent.getUnformattedComponentText(), false));
               if (l > p_184870_1_) {
                  return itextcomponent;
               }
            }

            return null;
         } else {
            return null;
         }
      }
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.causeOfDeath != null && p_mouseClicked_3_ > 85.0D && p_mouseClicked_3_ < (double)(85 + this.fontRenderer.FONT_HEIGHT)) {
         ITextComponent itextcomponent = this.getClickedComponentAt((int)p_mouseClicked_1_);
         if (itextcomponent != null && itextcomponent.getStyle().getClickEvent() != null && itextcomponent.getStyle().getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
            this.handleComponentClick(itextcomponent);
            return false;
         }
      }

      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void tick() {
      super.tick();
      ++this.enableButtonsTimer;
      if (this.enableButtonsTimer == 20) {
         for(GuiButton guibutton : this.buttons) {
            guibutton.enabled = true;
         }
      }

   }
}
