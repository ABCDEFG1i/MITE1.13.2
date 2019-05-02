package net.minecraft.client.gui.toasts;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeToast implements IToast {
   private final List<IRecipe> recipes = Lists.newArrayList();
   private long firstDrawTime;
   private boolean hasNewOutputs;

   public RecipeToast(IRecipe p_i48624_1_) {
      this.recipes.add(p_i48624_1_);
   }

   public IToast.Visibility draw(GuiToast p_193653_1_, long p_193653_2_) {
      if (this.hasNewOutputs) {
         this.firstDrawTime = p_193653_2_;
         this.hasNewOutputs = false;
      }

      if (this.recipes.isEmpty()) {
         return IToast.Visibility.HIDE;
      } else {
         p_193653_1_.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
         GlStateManager.color3f(1.0F, 1.0F, 1.0F);
         p_193653_1_.drawTexturedModalRect(0, 0, 0, 32, 160, 32);
         p_193653_1_.getMinecraft().fontRenderer.drawString(I18n.format("recipe.toast.title"), 30.0F, 7.0F, -11534256);
         p_193653_1_.getMinecraft().fontRenderer.drawString(I18n.format("recipe.toast.description"), 30.0F, 18.0F, -16777216);
         RenderHelper.enableGUIStandardItemLighting();
         IRecipe irecipe = this.recipes.get((int)(p_193653_2_ / (5000L / (long)this.recipes.size()) % (long)this.recipes.size()));
         ItemStack itemstack;
         if (irecipe instanceof FurnaceRecipe) {
            itemstack = new ItemStack(Blocks.FURNACE);
         } else {
            itemstack = new ItemStack(Blocks.ADAMANTIUM_CRAFTING_TABLE);
         }

         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.6F, 0.6F, 1.0F);
         p_193653_1_.getMinecraft().getItemRenderer().renderItemAndEffectIntoGUI(null, itemstack, 3, 3);
         GlStateManager.popMatrix();
         p_193653_1_.getMinecraft().getItemRenderer().renderItemAndEffectIntoGUI(null, irecipe.getRecipeOutput(), 8, 8);
         return p_193653_2_ - this.firstDrawTime >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
      }
   }

   public void addRecipe(IRecipe p_202905_1_) {
      if (this.recipes.add(p_202905_1_)) {
         this.hasNewOutputs = true;
      }

   }

   public static void addOrUpdate(GuiToast p_193665_0_, IRecipe p_193665_1_) {
      RecipeToast recipetoast = p_193665_0_.getToast(RecipeToast.class, NO_TOKEN);
      if (recipetoast == null) {
         p_193665_0_.add(new RecipeToast(p_193665_1_));
      } else {
         recipetoast.addRecipe(p_193665_1_);
      }

   }
}
