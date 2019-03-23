package net.minecraft.client.gui.inventory;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerRecipeBook;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiInventory extends InventoryEffectRenderer implements IRecipeShownListener {
   private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
   private float oldMouseX;
   private float oldMouseY;
   private final GuiRecipeBook recipeBookGui = new GuiRecipeBook();
   private final ContainerPlayer containerPlayer;
   private boolean field_212353_B;
   private boolean widthTooNarrow;
   private boolean buttonClicked;

   public GuiInventory(EntityPlayer p_i1094_1_) {

      super(p_i1094_1_.inventoryContainer);
      this.containerPlayer = (ContainerPlayer) p_i1094_1_.inventoryContainer;
      this.allowUserInput = true;
   }

   public void tick() {
      if (this.mc.playerController.isInCreativeMode()) {
         this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.player));
      } else {
         this.recipeBookGui.tick();
      }
   }

   protected void initGui() {
      if (this.mc.playerController.isInCreativeMode()) {
         this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.player));
      } else {
         super.initGui();
         this.widthTooNarrow = this.width < 379;
         this.recipeBookGui.func_201520_a(this.width, this.height, this.mc, this.widthTooNarrow, (ContainerRecipeBook)this.inventorySlots);
         this.field_212353_B = true;
         this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
         this.eventListeners.add(this.recipeBookGui);
         this.addButton(new GuiButtonImage(10, this.guiLeft + 104, this.height / 2 - 22, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE) {
            public void onClick(double p_194829_1_, double p_194829_3_) {
               GuiInventory.this.recipeBookGui.func_201518_a(GuiInventory.this.widthTooNarrow);
               GuiInventory.this.recipeBookGui.toggleVisibility();
               GuiInventory.this.guiLeft = GuiInventory.this.recipeBookGui.updateScreenPosition(GuiInventory.this.widthTooNarrow, GuiInventory.this.width, GuiInventory.this.xSize);
               this.setPosition(GuiInventory.this.guiLeft + 104, GuiInventory.this.height / 2 - 22);
               GuiInventory.this.buttonClicked = true;
            }
         });
      }
   }

   @Nullable
   public IGuiEventListener getFocused() {
      return this.recipeBookGui;
   }

   protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
      this.fontRenderer.drawString(I18n.format("container.crafting"), 97.0F, 8.0F, 4210752);
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawDefaultBackground();
      this.hasActivePotionEffects = !this.recipeBookGui.isVisible();
      if (this.recipeBookGui.isVisible() && this.widthTooNarrow) {
         this.drawGuiContainerBackgroundLayer(p_73863_3_, p_73863_1_, p_73863_2_);
         this.recipeBookGui.render(p_73863_1_, p_73863_2_, p_73863_3_);
      } else {
         this.recipeBookGui.render(p_73863_1_, p_73863_2_, p_73863_3_);
         super.render(p_73863_1_, p_73863_2_, p_73863_3_);
         this.recipeBookGui.renderGhostRecipe(this.guiLeft, this.guiTop, false, p_73863_3_);
      }

      this.renderHoveredToolTip(p_73863_1_, p_73863_2_);
      this.recipeBookGui.renderTooltip(this.guiLeft, this.guiTop, p_73863_1_, p_73863_2_);
      this.oldMouseX = (float)p_73863_1_;
      this.oldMouseY = (float)p_73863_2_;
   }

   protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
      int i = this.guiLeft;
      int j = this.guiTop;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
      drawEntityOnScreen(i + 51, j + 75, 30, (float)(i + 51) - this.oldMouseX, (float)(j + 75 - 50) - this.oldMouseY, this.mc.player);

      this.mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
      double craftingProgressPercent = (double) this.containerPlayer.getCraftingTime() / (double) this.containerPlayer.getTotalCraftingTime();
      this.drawTexturedModalRect(i+135 ,j+28,180,39,(int) (17*craftingProgressPercent),15);
   }

   public static void drawEntityOnScreen(int p_147046_0_, int p_147046_1_, int p_147046_2_, float p_147046_3_, float p_147046_4_, EntityLivingBase p_147046_5_) {
      GlStateManager.enableColorMaterial();
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)p_147046_0_, (float)p_147046_1_, 50.0F);
      GlStateManager.scalef((float)(-p_147046_2_), (float)p_147046_2_, (float)p_147046_2_);
      GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
      float f = p_147046_5_.renderYawOffset;
      float f1 = p_147046_5_.rotationYaw;
      float f2 = p_147046_5_.rotationPitch;
      float f3 = p_147046_5_.prevRotationYawHead;
      float f4 = p_147046_5_.rotationYawHead;
      GlStateManager.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
      RenderHelper.enableStandardItemLighting();
      GlStateManager.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(-((float)Math.atan((double)(p_147046_4_ / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
      p_147046_5_.renderYawOffset = (float)Math.atan((double)(p_147046_3_ / 40.0F)) * 20.0F;
      p_147046_5_.rotationYaw = (float)Math.atan((double)(p_147046_3_ / 40.0F)) * 40.0F;
      p_147046_5_.rotationPitch = -((float)Math.atan((double)(p_147046_4_ / 40.0F))) * 20.0F;
      p_147046_5_.rotationYawHead = p_147046_5_.rotationYaw;
      p_147046_5_.prevRotationYawHead = p_147046_5_.rotationYaw;
      GlStateManager.translatef(0.0F, 0.0F, 0.0F);
      RenderManager rendermanager = Minecraft.getInstance().getRenderManager();
      rendermanager.setPlayerViewY(180.0F);
      rendermanager.setRenderShadow(false);
      rendermanager.renderEntity(p_147046_5_, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
      rendermanager.setRenderShadow(true);
      p_147046_5_.renderYawOffset = f;
      p_147046_5_.rotationYaw = f1;
      p_147046_5_.rotationPitch = f2;
      p_147046_5_.prevRotationYawHead = f3;
      p_147046_5_.rotationYawHead = f4;
      GlStateManager.popMatrix();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableRescaleNormal();
      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE1);
      GlStateManager.disableTexture2D();
      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE0);
   }

   protected boolean isPointInRegion(int p_195359_1_, int p_195359_2_, int p_195359_3_, int p_195359_4_, double p_195359_5_, double p_195359_7_) {
      return (!this.widthTooNarrow || !this.recipeBookGui.isVisible()) && super.isPointInRegion(p_195359_1_, p_195359_2_, p_195359_3_, p_195359_4_, p_195359_5_, p_195359_7_);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.recipeBookGui.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         return true;
      } else {
         return (!this.widthTooNarrow || !this.recipeBookGui.isVisible()) && super.mouseClicked(p_mouseClicked_1_,
                                                                                                p_mouseClicked_3_,
                                                                                                p_mouseClicked_5_);
      }
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      if (this.buttonClicked) {
         this.buttonClicked = false;
         return true;
      } else {
         return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
      }
   }

   protected boolean func_195361_a(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_) {
      boolean flag = p_195361_1_ < (double)p_195361_5_ || p_195361_3_ < (double)p_195361_6_ || p_195361_1_ >= (double)(p_195361_5_ + this.xSize) || p_195361_3_ >= (double)(p_195361_6_ + this.ySize);
      return this.recipeBookGui.func_195604_a(p_195361_1_, p_195361_3_, this.guiLeft, this.guiTop, this.xSize, this.ySize, p_195361_7_) && flag;
   }

   protected void handleMouseClick(Slot p_184098_1_, int p_184098_2_, int p_184098_3_, ClickType p_184098_4_) {
      super.handleMouseClick(p_184098_1_, p_184098_2_, p_184098_3_, p_184098_4_);
      this.recipeBookGui.slotClicked(p_184098_1_);
   }

   public void recipesUpdated() {
      this.recipeBookGui.recipesUpdated();
   }

   public void onGuiClosed() {
      if (this.field_212353_B) {
         this.recipeBookGui.removed();
      }

      super.onGuiClosed();
   }

   public GuiRecipeBook func_194310_f() {
      return this.recipeBookGui;
   }
}
