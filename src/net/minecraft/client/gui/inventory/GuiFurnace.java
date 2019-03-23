package net.minecraft.client.gui.inventory;

import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.recipebook.GuiFurnaceRecipeBook;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerRecipeBook;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiFurnace extends GuiContainer implements IRecipeShownListener {
   private static final ResourceLocation FURNACE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/furnace.png");
   private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
   private final InventoryPlayer playerInventory;
   private final IInventory tileFurnace;
   public final GuiFurnaceRecipeBook recipeBook = new GuiFurnaceRecipeBook();
   private boolean canRenderRecipeBook;

   public GuiFurnace(InventoryPlayer p_i45501_1_, IInventory p_i45501_2_) {
      super(new ContainerFurnace(p_i45501_1_, p_i45501_2_));
      this.playerInventory = p_i45501_1_;
      this.tileFurnace = p_i45501_2_;
   }

   public void initGui() {
      super.initGui();
      this.canRenderRecipeBook = this.width < 379;
      this.recipeBook.func_201520_a(this.width, this.height, this.mc, this.canRenderRecipeBook, (ContainerRecipeBook)this.inventorySlots);
      this.guiLeft = this.recipeBook.updateScreenPosition(this.canRenderRecipeBook, this.width, this.xSize);
      this.addButton(new GuiButtonImage(10, this.guiLeft + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiFurnace.this.recipeBook.func_201518_a(GuiFurnace.this.canRenderRecipeBook);
            GuiFurnace.this.recipeBook.toggleVisibility();
            GuiFurnace.this.guiLeft = GuiFurnace.this.recipeBook.updateScreenPosition(GuiFurnace.this.canRenderRecipeBook, GuiFurnace.this.width, GuiFurnace.this.xSize);
            this.setPosition(GuiFurnace.this.guiLeft + 20, GuiFurnace.this.height / 2 - 49);
         }
      });
   }

   public void tick() {
      super.tick();
      this.recipeBook.tick();
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawDefaultBackground();
      if (this.recipeBook.isVisible() && this.canRenderRecipeBook) {
         this.drawGuiContainerBackgroundLayer(p_73863_3_, p_73863_1_, p_73863_2_);
         this.recipeBook.render(p_73863_1_, p_73863_2_, p_73863_3_);
      } else {
         this.recipeBook.render(p_73863_1_, p_73863_2_, p_73863_3_);
         super.render(p_73863_1_, p_73863_2_, p_73863_3_);
         this.recipeBook.renderGhostRecipe(this.guiLeft, this.guiTop, true, p_73863_3_);
      }

      this.renderHoveredToolTip(p_73863_1_, p_73863_2_);
      this.recipeBook.renderTooltip(this.guiLeft, this.guiTop, p_73863_1_, p_73863_2_);
   }

   protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
      String s = this.tileFurnace.getDisplayName().getFormattedText();
      this.fontRenderer.drawString(s, (float)(this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2), 6.0F, 4210752);
      this.fontRenderer.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(FURNACE_GUI_TEXTURES);
      int i = this.guiLeft;
      int j = this.guiTop;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
      if (TileEntityFurnace.isBurning(this.tileFurnace)) {
         int k = this.getBurnLeftScaled(13);
         this.drawTexturedModalRect(i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
      }

      int l = this.getCookProgressScaled(24);
      this.drawTexturedModalRect(i + 79, j + 34, 176, 14, l + 1, 16);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.recipeBook.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         return true;
      } else {
         return this.canRenderRecipeBook && this.recipeBook.isVisible() ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      }
   }

   protected void handleMouseClick(Slot p_184098_1_, int p_184098_2_, int p_184098_3_, ClickType p_184098_4_) {
      super.handleMouseClick(p_184098_1_, p_184098_2_, p_184098_3_, p_184098_4_);
      this.recipeBook.slotClicked(p_184098_1_);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      return this.recipeBook.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) ? false : super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
   }

   protected boolean func_195361_a(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_) {
      boolean flag = p_195361_1_ < (double)p_195361_5_ || p_195361_3_ < (double)p_195361_6_ || p_195361_1_ >= (double)(p_195361_5_ + this.xSize) || p_195361_3_ >= (double)(p_195361_6_ + this.ySize);
      return this.recipeBook.func_195604_a(p_195361_1_, p_195361_3_, this.guiLeft, this.guiTop, this.xSize, this.ySize, p_195361_7_) && flag;
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      return this.recipeBook.charTyped(p_charTyped_1_, p_charTyped_2_) ? true : super.charTyped(p_charTyped_1_, p_charTyped_2_);
   }

   public void recipesUpdated() {
      this.recipeBook.recipesUpdated();
   }

   public GuiRecipeBook func_194310_f() {
      return this.recipeBook;
   }

   public void onGuiClosed() {
      this.recipeBook.removed();
      super.onGuiClosed();
   }

   private int getCookProgressScaled(int p_175381_1_) {
      int i = this.tileFurnace.getField(2);
      int j = this.tileFurnace.getField(3);
      return j != 0 && i != 0 ? i * p_175381_1_ / j : 0;
   }

   private int getBurnLeftScaled(int p_175382_1_) {
      int i = this.tileFurnace.getField(1);
      if (i == 0) {
         i = 200;
      }

      return this.tileFurnace.getField(0) * p_175382_1_ / i;
   }
}
