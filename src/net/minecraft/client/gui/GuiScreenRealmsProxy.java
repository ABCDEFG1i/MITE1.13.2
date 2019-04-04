package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.HashSet;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsGuiEventListener;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GuiScreenRealmsProxy extends GuiScreen {
   private final RealmsScreen proxy;
   private static final Logger field_212333_f = LogManager.getLogger();

   public GuiScreenRealmsProxy(RealmsScreen p_i1087_1_) {
      this.proxy = p_i1087_1_;
   }

   public RealmsScreen getProxy() {
      return this.proxy;
   }

   public void setWorldAndResolution(Minecraft p_146280_1_, int p_146280_2_, int p_146280_3_) {
      this.proxy.init(p_146280_1_, p_146280_2_, p_146280_3_);
      super.setWorldAndResolution(p_146280_1_, p_146280_2_, p_146280_3_);
   }

   protected void initGui() {
      this.proxy.init();
      super.initGui();
   }

   public void drawCenteredString(String p_154325_1_, int p_154325_2_, int p_154325_3_, int p_154325_4_) {
      super.drawCenteredString(this.fontRenderer, p_154325_1_, p_154325_2_, p_154325_3_, p_154325_4_);
   }

   public void drawString(String p_207734_1_, int p_207734_2_, int p_207734_3_, int p_207734_4_, boolean p_207734_5_) {
      if (p_207734_5_) {
         super.drawString(this.fontRenderer, p_207734_1_, p_207734_2_, p_207734_3_, p_207734_4_);
      } else {
         this.fontRenderer.drawString(p_207734_1_, (float)p_207734_2_, (float)p_207734_3_, p_207734_4_);
      }

   }

   public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
      this.proxy.blit(x, y, textureX, textureY, width, height);
      super.drawTexturedModalRect(x, y, textureX, textureY, width, height);
   }

   public void drawGradientRect(int p_73733_1_, int p_73733_2_, int p_73733_3_, int p_73733_4_, int p_73733_5_, int p_73733_6_) {
      super.drawGradientRect(p_73733_1_, p_73733_2_, p_73733_3_, p_73733_4_, p_73733_5_, p_73733_6_);
   }

   public void drawDefaultBackground() {
      super.drawDefaultBackground();
   }

   public boolean doesGuiPauseGame() {
      return super.doesGuiPauseGame();
   }

   public void drawWorldBackground(int p_146270_1_) {
      super.drawWorldBackground(p_146270_1_);
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.proxy.render(p_73863_1_, p_73863_2_, p_73863_3_);
   }

   public void renderToolTip(ItemStack p_146285_1_, int p_146285_2_, int p_146285_3_) {
      super.renderToolTip(p_146285_1_, p_146285_2_, p_146285_3_);
   }

   public void drawHoveringText(String p_146279_1_, int p_146279_2_, int p_146279_3_) {
      super.drawHoveringText(p_146279_1_, p_146279_2_, p_146279_3_);
   }

   public void drawHoveringText(List<String> p_146283_1_, int p_146283_2_, int p_146283_3_) {
      super.drawHoveringText(p_146283_1_, p_146283_2_, p_146283_3_);
   }

   public void tick() {
      this.proxy.tick();
      super.tick();
   }

   public int getFontHeight() {
      return this.fontRenderer.FONT_HEIGHT;
   }

   public int fontWidth(String p_207731_1_) {
      return this.fontRenderer.getStringWidth(p_207731_1_);
   }

   public void fontDrawShadow(String p_207728_1_, int p_207728_2_, int p_207728_3_, int p_207728_4_) {
      this.fontRenderer.drawStringWithShadow(p_207728_1_, (float)p_207728_2_, (float)p_207728_3_, p_207728_4_);
   }

   public List<String> fontSplit(String p_154323_1_, int p_154323_2_) {
      return this.fontRenderer.listFormattedStringToWidth(p_154323_1_, p_154323_2_);
   }

   public void childrenClear() {
      this.eventListeners.clear();
   }

   public void addWidget(RealmsGuiEventListener p_207730_1_) {
      if (this.func_212332_c(p_207730_1_) || !this.eventListeners.add(p_207730_1_.getProxy())) {
         field_212333_f.error("Tried to add the same widget multiple times: " + p_207730_1_);
      }

   }

   public void removeWidget(RealmsGuiEventListener p_207733_1_) {
      if (!this.func_212332_c(p_207733_1_) || !this.eventListeners.remove(p_207733_1_.getProxy())) {
         field_212333_f.error("Tried to add the same widget multiple times: " + p_207733_1_);
      }

   }

   public boolean func_212332_c(RealmsGuiEventListener p_212332_1_) {
      return this.eventListeners.contains(p_212332_1_.getProxy());
   }

   public void buttonsAdd(RealmsButton p_154327_1_) {
      this.addButton(p_154327_1_.getProxy());
   }

   public List<RealmsButton> buttons() {
      List<RealmsButton> list = Lists.newArrayListWithExpectedSize(this.buttons.size());

      for(GuiButton guibutton : this.buttons) {
         list.add(((GuiButtonRealmsProxy)guibutton).getRealmsButton());
      }

      return list;
   }

   public void buttonsClear() {
      HashSet<IGuiEventListener> hashset = new HashSet<>(this.buttons);
      this.eventListeners.removeIf(hashset::contains);
      this.buttons.clear();
   }

   public void removeButton(RealmsButton p_207732_1_) {
      this.eventListeners.remove(p_207732_1_.getProxy());
      this.buttons.remove(p_207732_1_.getProxy());
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return this.proxy.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_) || super.mouseClicked(
              p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      return this.proxy.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      return this.proxy.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_,
              p_mouseDragged_8_) || super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_,
              p_mouseDragged_6_, p_mouseDragged_8_);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      return this.proxy.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) || super.keyPressed(
              p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      return this.proxy.charTyped(p_charTyped_1_, p_charTyped_2_) || super.charTyped(p_charTyped_1_, p_charTyped_2_);
   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      this.proxy.confirmResult(p_confirmResult_1_, p_confirmResult_2_);
   }

   public void onGuiClosed() {
      this.proxy.removed();
      super.onGuiClosed();
   }

   public int draw(String p_209208_1_, int p_209208_2_, int p_209208_3_, int p_209208_4_, boolean p_209208_5_) {
      return p_209208_5_ ? this.fontRenderer.drawStringWithShadow(p_209208_1_, (float)p_209208_2_, (float)p_209208_3_, p_209208_4_) : this.fontRenderer.drawString(p_209208_1_, (float)p_209208_2_, (float)p_209208_3_, p_209208_4_);
   }
}
