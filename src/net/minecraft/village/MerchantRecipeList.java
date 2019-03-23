package net.minecraft.village;

import java.io.IOException;
import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MerchantRecipeList extends ArrayList<MerchantRecipe> {
   public MerchantRecipeList() {
   }

   public MerchantRecipeList(NBTTagCompound p_i1944_1_) {
      this.readRecipiesFromTags(p_i1944_1_);
   }

   @Nullable
   public MerchantRecipe canRecipeBeUsed(ItemStack p_77203_1_, ItemStack p_77203_2_, int p_77203_3_) {
      if (p_77203_3_ > 0 && p_77203_3_ < this.size()) {
         MerchantRecipe merchantrecipe1 = this.get(p_77203_3_);
         return !this.areItemStacksExactlyEqual(p_77203_1_, merchantrecipe1.getItemToBuy()) || (!p_77203_2_.isEmpty() || merchantrecipe1.hasSecondItemToBuy()) && (!merchantrecipe1.hasSecondItemToBuy() || !this.areItemStacksExactlyEqual(p_77203_2_, merchantrecipe1.getSecondItemToBuy())) || p_77203_1_.getCount() < merchantrecipe1.getItemToBuy().getCount() || merchantrecipe1.hasSecondItemToBuy() && p_77203_2_.getCount() < merchantrecipe1.getSecondItemToBuy().getCount() ? null : merchantrecipe1;
      } else {
         for(int i = 0; i < this.size(); ++i) {
            MerchantRecipe merchantrecipe = this.get(i);
            if (this.areItemStacksExactlyEqual(p_77203_1_, merchantrecipe.getItemToBuy()) && p_77203_1_.getCount() >= merchantrecipe.getItemToBuy().getCount() && (!merchantrecipe.hasSecondItemToBuy() && p_77203_2_.isEmpty() || merchantrecipe.hasSecondItemToBuy() && this.areItemStacksExactlyEqual(p_77203_2_, merchantrecipe.getSecondItemToBuy()) && p_77203_2_.getCount() >= merchantrecipe.getSecondItemToBuy().getCount())) {
               return merchantrecipe;
            }
         }

         return null;
      }
   }

   private boolean areItemStacksExactlyEqual(ItemStack p_181078_1_, ItemStack p_181078_2_) {
      ItemStack itemstack = p_181078_1_.copy();
      if (itemstack.getItem().isDamageable()) {
         itemstack.setDamage(itemstack.getDamage());
      }

      return ItemStack.areItemsEqual(itemstack, p_181078_2_) && (!p_181078_2_.hasTag() || itemstack.hasTag() && NBTUtil.areNBTEquals(p_181078_2_.getTag(), itemstack.getTag(), false));
   }

   public void writeToBuf(PacketBuffer p_151391_1_) {
      p_151391_1_.writeByte((byte)(this.size() & 255));

      for(int i = 0; i < this.size(); ++i) {
         MerchantRecipe merchantrecipe = this.get(i);
         p_151391_1_.writeItemStack(merchantrecipe.getItemToBuy());
         p_151391_1_.writeItemStack(merchantrecipe.getItemToSell());
         ItemStack itemstack = merchantrecipe.getSecondItemToBuy();
         p_151391_1_.writeBoolean(!itemstack.isEmpty());
         if (!itemstack.isEmpty()) {
            p_151391_1_.writeItemStack(itemstack);
         }

         p_151391_1_.writeBoolean(merchantrecipe.isRecipeDisabled());
         p_151391_1_.writeInt(merchantrecipe.getToolUses());
         p_151391_1_.writeInt(merchantrecipe.getMaxTradeUses());
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static MerchantRecipeList readFromBuf(PacketBuffer p_151390_0_) throws IOException {
      MerchantRecipeList merchantrecipelist = new MerchantRecipeList();
      int i = p_151390_0_.readByte() & 255;

      for(int j = 0; j < i; ++j) {
         ItemStack itemstack = p_151390_0_.readItemStack();
         ItemStack itemstack1 = p_151390_0_.readItemStack();
         ItemStack itemstack2 = ItemStack.EMPTY;
         if (p_151390_0_.readBoolean()) {
            itemstack2 = p_151390_0_.readItemStack();
         }

         boolean flag = p_151390_0_.readBoolean();
         int k = p_151390_0_.readInt();
         int l = p_151390_0_.readInt();
         MerchantRecipe merchantrecipe = new MerchantRecipe(itemstack, itemstack2, itemstack1, k, l);
         if (flag) {
            merchantrecipe.compensateToolUses();
         }

         merchantrecipelist.add(merchantrecipe);
      }

      return merchantrecipelist;
   }

   public void readRecipiesFromTags(NBTTagCompound p_77201_1_) {
      NBTTagList nbttaglist = p_77201_1_.getTagList("Recipes", 10);

      for(int i = 0; i < nbttaglist.size(); ++i) {
         NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
         this.add(new MerchantRecipe(nbttagcompound));
      }

   }

   public NBTTagCompound getRecipiesAsTags() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      NBTTagList nbttaglist = new NBTTagList();

      for(int i = 0; i < this.size(); ++i) {
         MerchantRecipe merchantrecipe = this.get(i);
         nbttaglist.add((INBTBase)merchantrecipe.writeToTags());
      }

      nbttagcompound.setTag("Recipes", nbttaglist);
      return nbttagcompound;
   }
}
