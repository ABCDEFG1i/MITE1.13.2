package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GuiWorldSelection extends GuiScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   protected GuiScreen prevScreen;
   protected String title = "Select world";
   private String notification = "(Only show supported worlds)";
   private String worldVersTooltip;
   private GuiButton deleteButton;
   private GuiButton selectButton;
   private GuiButton renameButton;
   private GuiButton copyButton;
   protected GuiTextField field_212352_g;
   private GuiListWorldSelection selectionList;

   public GuiWorldSelection(GuiScreen p_i46592_1_) {
      this.prevScreen = p_i46592_1_;
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_) {
      return this.selectionList.mouseScrolled(p_mouseScrolled_1_);
   }

   public void tick() {
      this.field_212352_g.tick();
   }

   protected void initGui() {
      this.mc.keyboardListener.enableRepeatEvents(true);
      this.title = I18n.format("selectWorld.title");
      this.notification = I18n.format("selectWorld.notification");
      this.field_212352_g = new GuiTextField(0, this.fontRenderer, this.width / 2 - 100, 22, 200, 20, this.field_212352_g) {
         public void setFocused(boolean p_146195_1_) {
            super.setFocused(true);
         }
      };
      this.field_212352_g.setTextAcceptHandler((p_212350_1_, p_212350_2_) -> {
         this.selectionList.func_212330_a(() -> {
            return p_212350_2_;
         }, false);
      });
      this.selectionList = new GuiListWorldSelection(this, this.mc, this.width, this.height, 48, this.height - 64, 36, () -> {
         return this.field_212352_g.getText();
      }, this.selectionList);
      this.selectButton = this.addButton(new GuiButton(1, this.width / 2 - 154, this.height - 52, 150, 20, I18n.format("selectWorld.select")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiListWorldSelectionEntry guilistworldselectionentry = GuiWorldSelection.this.selectionList.getSelectedWorld();
            if (guilistworldselectionentry != null) {
               guilistworldselectionentry.joinWorld();
            }

         }
      });
      this.addButton(new GuiButton(3, this.width / 2 + 4, this.height - 52, 150, 20, I18n.format("selectWorld.create")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiWorldSelection.this.mc.displayGuiScreen(new GuiCreateWorld(GuiWorldSelection.this));
         }
      });
      this.renameButton = this.addButton(new GuiButton(4, this.width / 2 - 154, this.height - 28, 72, 20, I18n.format("selectWorld.edit")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiListWorldSelectionEntry guilistworldselectionentry = GuiWorldSelection.this.selectionList.getSelectedWorld();
            if (guilistworldselectionentry != null) {
               guilistworldselectionentry.editWorld();
            }

         }
      });
      this.deleteButton = this.addButton(new GuiButton(2, this.width / 2 - 76, this.height - 28, 72, 20, I18n.format("selectWorld.delete")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiListWorldSelectionEntry guilistworldselectionentry = GuiWorldSelection.this.selectionList.getSelectedWorld();
            if (guilistworldselectionentry != null) {
               guilistworldselectionentry.deleteWorld();
            }

         }
      });
      this.copyButton = this.addButton(new GuiButton(5, this.width / 2 + 4, this.height - 28, 72, 20, I18n.format("selectWorld.recreate")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiListWorldSelectionEntry guilistworldselectionentry = GuiWorldSelection.this.selectionList.getSelectedWorld();
            if (guilistworldselectionentry != null) {
               guilistworldselectionentry.recreateWorld();
            }

         }
      });
      this.addButton(new GuiButton(0, this.width / 2 + 82, this.height - 28, 72, 20, I18n.format("gui.cancel")) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiWorldSelection.this.mc.displayGuiScreen(GuiWorldSelection.this.prevScreen);
         }
      });
      this.selectButton.enabled = false;
      this.deleteButton.enabled = false;
      this.renameButton.enabled = false;
      this.copyButton.enabled = false;
      this.eventListeners.add(this.field_212352_g);
      this.eventListeners.add(this.selectionList);
      this.field_212352_g.setFocused(true);
      this.field_212352_g.setCanLoseFocus(false);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) || this.field_212352_g.keyPressed(
              p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      return this.field_212352_g.charTyped(p_charTyped_1_, p_charTyped_2_);
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.worldVersTooltip = null;
      this.selectionList.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
      this.field_212352_g.drawTextField(p_73863_1_, p_73863_2_, p_73863_3_);
      this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 0, 16777215);
      this.drawCenteredString(this.fontRenderer, this.notification, this.width / 2, 10, 16777215);
      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
      if (this.worldVersTooltip != null) {
         this.drawHoveringText(Lists.newArrayList(Splitter.on("\n").split(this.worldVersTooltip)), p_73863_1_, p_73863_2_);
      }

   }

   public void setVersionTooltip(String p_184861_1_) {
      this.worldVersTooltip = p_184861_1_;
   }

   public void selectWorld(@Nullable GuiListWorldSelectionEntry p_184863_1_) {
      boolean flag = p_184863_1_ != null;
      this.selectButton.enabled = flag;
      this.deleteButton.enabled = flag;
      this.renameButton.enabled = flag;
      this.copyButton.enabled = flag;
   }

   public void onGuiClosed() {
      if (this.selectionList != null) {
         this.selectionList.getChildren().forEach(GuiListWorldSelectionEntry::close);
      }

   }
}
