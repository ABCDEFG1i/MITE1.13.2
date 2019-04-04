package net.minecraft.inventory;

import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public interface IRecipeHolder {
   void setRecipeUsed(@Nullable IRecipe p_193056_1_);

   @Nullable
   IRecipe getRecipeUsed();

   default void onCrafting(EntityPlayer p_201560_1_) {
      IRecipe irecipe = this.getRecipeUsed();
      if (irecipe != null && !irecipe.isDynamic()) {
         p_201560_1_.unlockRecipes(Lists.newArrayList(irecipe));
         this.setRecipeUsed(null);
      }

   }

   default boolean canUseRecipe(World p_201561_1_, EntityPlayerMP p_201561_2_, @Nullable IRecipe p_201561_3_) {
      if (p_201561_3_ == null || !p_201561_3_.isDynamic() && p_201561_1_.getGameRules().getBoolean("doLimitedCrafting") && !p_201561_2_.getRecipeBook().isUnlocked(p_201561_3_)) {
         return false;
      } else {
         this.setRecipeUsed(p_201561_3_);
         return true;
      }
   }
}
