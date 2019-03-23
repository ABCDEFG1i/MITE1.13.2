package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IMerchant {
   void setCustomer(@Nullable EntityPlayer p_70932_1_);

   @Nullable
   EntityPlayer getCustomer();

   @Nullable
   MerchantRecipeList getRecipes(EntityPlayer p_70934_1_);

   @OnlyIn(Dist.CLIENT)
   void setRecipes(@Nullable MerchantRecipeList p_70930_1_);

   void useRecipe(MerchantRecipe p_70933_1_);

   void verifySellingItem(ItemStack p_110297_1_);

   ITextComponent getDisplayName();

   World getWorld();

   BlockPos getPos();
}
