package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketSelectTrade;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GuiMerchant extends GuiContainer {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation MERCHANT_GUI_TEXTURE = new ResourceLocation("textures/gui/container/villager.png");
   private final IMerchant merchant;
   private GuiMerchant.MerchantButton nextButton;
   private GuiMerchant.MerchantButton previousButton;
   private int selectedMerchantRecipe;
   private final ITextComponent chatComponent;
   private final InventoryPlayer field_212355_D;

   public GuiMerchant(InventoryPlayer p_i45500_1_, IMerchant p_i45500_2_, World p_i45500_3_) {
      super(new ContainerMerchant(p_i45500_1_, p_i45500_2_, p_i45500_3_));
      this.merchant = p_i45500_2_;
      this.chatComponent = p_i45500_2_.getDisplayName();
      this.field_212355_D = p_i45500_1_;
   }

   private void func_195391_j() {
      ((ContainerMerchant)this.inventorySlots).setCurrentRecipeIndex(this.selectedMerchantRecipe);
      this.mc.getConnection().sendPacket(new CPacketSelectTrade(this.selectedMerchantRecipe));
   }

   protected void initGui() {
      super.initGui();
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.nextButton = this.addButton(new GuiMerchant.MerchantButton(1, i + 120 + 27, j + 24 - 1, true) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiMerchant.this.selectedMerchantRecipe++;
            MerchantRecipeList merchantrecipelist = GuiMerchant.this.merchant.getRecipes(GuiMerchant.this.mc.player);
            if (merchantrecipelist != null && GuiMerchant.this.selectedMerchantRecipe >= merchantrecipelist.size()) {
               GuiMerchant.this.selectedMerchantRecipe = merchantrecipelist.size() - 1;
            }

            GuiMerchant.this.func_195391_j();
         }
      });
      this.previousButton = this.addButton(new GuiMerchant.MerchantButton(2, i + 36 - 19, j + 24 - 1, false) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiMerchant.this.selectedMerchantRecipe--;
            if (GuiMerchant.this.selectedMerchantRecipe < 0) {
               GuiMerchant.this.selectedMerchantRecipe = 0;
            }

            GuiMerchant.this.func_195391_j();
         }
      });
      this.nextButton.enabled = false;
      this.previousButton.enabled = false;
   }

   protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
      String s = this.chatComponent.getFormattedText();
      this.fontRenderer.drawString(s, (float)(this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2), 6.0F, 4210752);
      this.fontRenderer.drawString(this.field_212355_D.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
   }

   public void tick() {
      super.tick();
      MerchantRecipeList merchantrecipelist = this.merchant.getRecipes(this.mc.player);
      if (merchantrecipelist != null) {
         this.nextButton.enabled = this.selectedMerchantRecipe < merchantrecipelist.size() - 1;
         this.previousButton.enabled = this.selectedMerchantRecipe > 0;
      }

   }

   protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
      MerchantRecipeList merchantrecipelist = this.merchant.getRecipes(this.mc.player);
      if (merchantrecipelist != null && !merchantrecipelist.isEmpty()) {
         int k = this.selectedMerchantRecipe;
         if (k < 0 || k >= merchantrecipelist.size()) {
            return;
         }

         MerchantRecipe merchantrecipe = merchantrecipelist.get(k);
         if (merchantrecipe.isRecipeDisabled()) {
            this.mc.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();
            this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 21, 212, 0, 28, 21);
            this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 51, 212, 0, 28, 21);
         }
      }

   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawDefaultBackground();
      super.render(p_73863_1_, p_73863_2_, p_73863_3_);
      MerchantRecipeList merchantrecipelist = this.merchant.getRecipes(this.mc.player);
      if (merchantrecipelist != null && !merchantrecipelist.isEmpty()) {
         int i = (this.width - this.xSize) / 2;
         int j = (this.height - this.ySize) / 2;
         int k = this.selectedMerchantRecipe;
         MerchantRecipe merchantrecipe = merchantrecipelist.get(k);
         ItemStack itemstack = merchantrecipe.getItemToBuy();
         ItemStack itemstack1 = merchantrecipe.getSecondItemToBuy();
         ItemStack itemstack2 = merchantrecipe.getItemToSell();
         GlStateManager.pushMatrix();
         RenderHelper.enableGUIStandardItemLighting();
         GlStateManager.disableLighting();
         GlStateManager.enableRescaleNormal();
         GlStateManager.enableColorMaterial();
         GlStateManager.enableLighting();
         this.itemRender.zLevel = 100.0F;
         this.itemRender.renderItemAndEffectIntoGUI(itemstack, i + 36, j + 24);
         this.itemRender.renderItemOverlays(this.fontRenderer, itemstack, i + 36, j + 24);
         if (!itemstack1.isEmpty()) {
            this.itemRender.renderItemAndEffectIntoGUI(itemstack1, i + 62, j + 24);
            this.itemRender.renderItemOverlays(this.fontRenderer, itemstack1, i + 62, j + 24);
         }

         this.itemRender.renderItemAndEffectIntoGUI(itemstack2, i + 120, j + 24);
         this.itemRender.renderItemOverlays(this.fontRenderer, itemstack2, i + 120, j + 24);
         this.itemRender.zLevel = 0.0F;
         GlStateManager.disableLighting();
         if (this.isPointInRegion(36, 24, 16, 16, (double)p_73863_1_, (double)p_73863_2_) && !itemstack.isEmpty()) {
            this.renderToolTip(itemstack, p_73863_1_, p_73863_2_);
         } else if (!itemstack1.isEmpty() && this.isPointInRegion(62, 24, 16, 16, (double)p_73863_1_, (double)p_73863_2_) && !itemstack1.isEmpty()) {
            this.renderToolTip(itemstack1, p_73863_1_, p_73863_2_);
         } else if (!itemstack2.isEmpty() && this.isPointInRegion(120, 24, 16, 16, (double)p_73863_1_, (double)p_73863_2_) && !itemstack2.isEmpty()) {
            this.renderToolTip(itemstack2, p_73863_1_, p_73863_2_);
         } else if (merchantrecipe.isRecipeDisabled() && (this.isPointInRegion(83, 21, 28, 21, (double)p_73863_1_, (double)p_73863_2_) || this.isPointInRegion(83, 51, 28, 21, (double)p_73863_1_, (double)p_73863_2_))) {
            this.drawHoveringText(I18n.format("merchant.deprecated"), p_73863_1_, p_73863_2_);
         }

         GlStateManager.popMatrix();
         GlStateManager.enableLighting();
         GlStateManager.enableDepthTest();
         RenderHelper.enableStandardItemLighting();
      }

      this.renderHoveredToolTip(p_73863_1_, p_73863_2_);
   }

   public IMerchant getMerchant() {
      return this.merchant;
   }

   @OnlyIn(Dist.CLIENT)
   abstract static class MerchantButton extends GuiButton {
      private final boolean forward;

      public MerchantButton(int p_i1095_1_, int p_i1095_2_, int p_i1095_3_, boolean p_i1095_4_) {
         super(p_i1095_1_, p_i1095_2_, p_i1095_3_, 12, 19, "");
         this.forward = p_i1095_4_;
      }

      public void render(int p_194828_1_, int p_194828_2_, float p_194828_3_) {
         if (this.visible) {
            Minecraft.getInstance().getTextureManager().bindTexture(GuiMerchant.MERCHANT_GUI_TEXTURE);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            boolean flag = p_194828_1_ >= this.x && p_194828_2_ >= this.y && p_194828_1_ < this.x + this.width && p_194828_2_ < this.y + this.height;
            int i = 0;
            int j = 176;
            if (!this.enabled) {
               j += this.width * 2;
            } else if (flag) {
               j += this.width;
            }

            if (!this.forward) {
               i += this.height;
            }

            this.drawTexturedModalRect(this.x, this.y, j, i, this.width, this.height);
         }
      }
   }
}
