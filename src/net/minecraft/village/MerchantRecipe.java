package net.minecraft.village;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MerchantRecipe {
   private ItemStack itemToBuy = ItemStack.EMPTY;
   private ItemStack secondItemToBuy = ItemStack.EMPTY;
   private ItemStack itemToSell = ItemStack.EMPTY;
   private int toolUses;
   private int maxTradeUses;
   private boolean rewardsExp;

   public MerchantRecipe(NBTTagCompound p_i1940_1_) {
      this.readFromTags(p_i1940_1_);
   }

   public MerchantRecipe(ItemStack p_i1941_1_, ItemStack p_i1941_2_, ItemStack p_i1941_3_) {
      this(p_i1941_1_, p_i1941_2_, p_i1941_3_, 0, 7);
   }

   public MerchantRecipe(ItemStack p_i45760_1_, ItemStack p_i45760_2_, ItemStack p_i45760_3_, int p_i45760_4_, int p_i45760_5_) {
      this.itemToBuy = p_i45760_1_;
      this.secondItemToBuy = p_i45760_2_;
      this.itemToSell = p_i45760_3_;
      this.toolUses = p_i45760_4_;
      this.maxTradeUses = p_i45760_5_;
      this.rewardsExp = true;
   }

   public MerchantRecipe(ItemStack p_i1942_1_, ItemStack p_i1942_2_) {
      this(p_i1942_1_, ItemStack.EMPTY, p_i1942_2_);
   }

   public MerchantRecipe(ItemStack p_i1943_1_, Item p_i1943_2_) {
      this(p_i1943_1_, new ItemStack(p_i1943_2_));
   }

   public ItemStack getItemToBuy() {
      return this.itemToBuy;
   }

   public ItemStack getSecondItemToBuy() {
      return this.secondItemToBuy;
   }

   public boolean hasSecondItemToBuy() {
      return !this.secondItemToBuy.isEmpty();
   }

   public ItemStack getItemToSell() {
      return this.itemToSell;
   }

   public int getToolUses() {
      return this.toolUses;
   }

   public int getMaxTradeUses() {
      return this.maxTradeUses;
   }

   public void incrementToolUses() {
      ++this.toolUses;
   }

   public void increaseMaxTradeUses(int p_82783_1_) {
      this.maxTradeUses += p_82783_1_;
   }

   public boolean isRecipeDisabled() {
      return this.toolUses >= this.maxTradeUses;
   }

   @OnlyIn(Dist.CLIENT)
   public void compensateToolUses() {
      this.toolUses = this.maxTradeUses;
   }

   public boolean getRewardsExp() {
      return this.rewardsExp;
   }

   public void readFromTags(NBTTagCompound p_77390_1_) {
      NBTTagCompound nbttagcompound = p_77390_1_.getCompoundTag("buy");
      this.itemToBuy = ItemStack.loadFromNBT(nbttagcompound);
      NBTTagCompound nbttagcompound1 = p_77390_1_.getCompoundTag("sell");
      this.itemToSell = ItemStack.loadFromNBT(nbttagcompound1);
      if (p_77390_1_.hasKey("buyB", 10)) {
         this.secondItemToBuy = ItemStack.loadFromNBT(p_77390_1_.getCompoundTag("buyB"));
      }

      if (p_77390_1_.hasKey("uses", 99)) {
         this.toolUses = p_77390_1_.getInteger("uses");
      }

      if (p_77390_1_.hasKey("maxUses", 99)) {
         this.maxTradeUses = p_77390_1_.getInteger("maxUses");
      } else {
         this.maxTradeUses = 7;
      }

      if (p_77390_1_.hasKey("rewardExp", 1)) {
         this.rewardsExp = p_77390_1_.getBoolean("rewardExp");
      } else {
         this.rewardsExp = true;
      }

   }

   public NBTTagCompound writeToTags() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setTag("buy", this.itemToBuy.write(new NBTTagCompound()));
      nbttagcompound.setTag("sell", this.itemToSell.write(new NBTTagCompound()));
      if (!this.secondItemToBuy.isEmpty()) {
         nbttagcompound.setTag("buyB", this.secondItemToBuy.write(new NBTTagCompound()));
      }

      nbttagcompound.setInteger("uses", this.toolUses);
      nbttagcompound.setInteger("maxUses", this.maxTradeUses);
      nbttagcompound.setBoolean("rewardExp", this.rewardsExp);
      return nbttagcompound;
   }
}
