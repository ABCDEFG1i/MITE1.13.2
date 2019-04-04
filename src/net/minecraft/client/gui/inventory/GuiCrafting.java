package net.minecraft.client.gui.inventory;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiCrafting extends GuiContainer implements IRecipeShownListener {
   private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/crafting_table.png");
   private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
   private final GuiRecipeBook recipeBookGui = new GuiRecipeBook();
   private final ContainerWorkbench workbench;
   private boolean widthTooNarrow;
   private final InventoryPlayer field_212354_A;

   public GuiCrafting(InventoryPlayer p_i45504_1_, World p_i45504_2_) {
      super(new ContainerWorkbench(p_i45504_1_,p_i45504_2_, BlockPos.ORIGIN));
      this.field_212354_A = p_i45504_1_;
      this.workbench = (ContainerWorkbench) super.inventorySlots;
   }

   protected void initGui() {
      super.initGui();
      this.widthTooNarrow = this.width < 379;
      this.recipeBookGui.func_201520_a(this.width, this.height, this.mc, this.widthTooNarrow, (ContainerRecipeBook)this.inventorySlots);
      this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
      this.eventListeners.add(this.recipeBookGui);
      this.addButton(new GuiButtonImage(10, this.guiLeft + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            GuiCrafting.this.recipeBookGui.func_201518_a(GuiCrafting.this.widthTooNarrow);
            GuiCrafting.this.recipeBookGui.toggleVisibility();
            GuiCrafting.this.guiLeft = GuiCrafting.this.recipeBookGui.updateScreenPosition(GuiCrafting.this.widthTooNarrow, GuiCrafting.this.width, GuiCrafting.this.xSize);
            this.setPosition(GuiCrafting.this.guiLeft + 5, GuiCrafting.this.height / 2 - 49);
         }
      });
   }

   @Nullable
   public IGuiEventListener getFocused() {
      return this.recipeBookGui;
   }

   public void tick() {
      super.tick();
      this.recipeBookGui.tick();
   }

   public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawDefaultBackground();
      if (this.recipeBookGui.isVisible() && this.widthTooNarrow) {
         this.drawGuiContainerBackgroundLayer(p_73863_3_, p_73863_1_, p_73863_2_);
         this.recipeBookGui.render(p_73863_1_, p_73863_2_, p_73863_3_);
      } else {
         this.recipeBookGui.render(p_73863_1_, p_73863_2_, p_73863_3_);
         super.render(p_73863_1_, p_73863_2_, p_73863_3_);
         this.recipeBookGui.renderGhostRecipe(this.guiLeft, this.guiTop, true, p_73863_3_);
      }

      this.renderHoveredToolTip(p_73863_1_, p_73863_2_);
      this.recipeBookGui.renderTooltip(this.guiLeft, this.guiTop, p_73863_1_, p_73863_2_);
   }

   protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
      this.fontRenderer.drawString(I18n.format("container.crafting"), 28.0F, 6.0F, 4210752);
      this.fontRenderer.drawString(this.field_212354_A.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(CRAFTING_TABLE_GUI_TEXTURES);
      int i = this.guiLeft;
      int j = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

      double craftingProgressPercent = (double) this.workbench.getCraftingTime() / (double) this.workbench.getTotalCraftingTime();
      this.drawTexturedModalRect(i+90,j+35,23,169,(int) (23*craftingProgressPercent),15);

   }

   protected boolean isPointInRegion(int p_195359_1_, int p_195359_2_, int p_195359_3_, int p_195359_4_, double p_195359_5_, double p_195359_7_) {
      return (!this.widthTooNarrow || !this.recipeBookGui.isVisible()) && super.isPointInRegion(p_195359_1_, p_195359_2_, p_195359_3_, p_195359_4_, p_195359_5_, p_195359_7_);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.recipeBookGui.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         return true;
      } else {
         return this.widthTooNarrow && this.recipeBookGui.isVisible() || super.mouseClicked(p_mouseClicked_1_,
                 p_mouseClicked_3_, p_mouseClicked_5_);
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
      this.recipeBookGui.removed();
      super.onGuiClosed();
   }

   public GuiRecipeBook func_194310_f() {
      return this.recipeBookGui;
   }
}
