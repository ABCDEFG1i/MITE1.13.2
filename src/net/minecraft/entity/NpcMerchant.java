package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NpcMerchant implements IMerchant {
   private final InventoryMerchant merchantInventory;
   private final EntityPlayer customer;
   private MerchantRecipeList recipeList;
   private final ITextComponent name;

   public NpcMerchant(EntityPlayer p_i45817_1_, ITextComponent p_i45817_2_) {
      this.customer = p_i45817_1_;
      this.name = p_i45817_2_;
      this.merchantInventory = new InventoryMerchant(p_i45817_1_, this);
   }

   @Nullable
   public EntityPlayer getCustomer() {
      return this.customer;
   }

   public void setCustomer(@Nullable EntityPlayer p_70932_1_) {
   }

   @Nullable
   public MerchantRecipeList getRecipes(EntityPlayer p_70934_1_) {
      return this.recipeList;
   }

   public void setRecipes(@Nullable MerchantRecipeList p_70930_1_) {
      this.recipeList = p_70930_1_;
   }

   public void useRecipe(MerchantRecipe p_70933_1_) {
      p_70933_1_.incrementToolUses();
   }

   public void verifySellingItem(ItemStack p_110297_1_) {
   }

   public ITextComponent getDisplayName() {
      return (ITextComponent)(this.name != null ? this.name : new TextComponentTranslation("entity.Villager.name"));
   }

   public World getWorld() {
      return this.customer.world;
   }

   public BlockPos getPos() {
      return new BlockPos(this.customer);
   }
}
