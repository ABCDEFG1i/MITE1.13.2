package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemFishFood extends ItemFood {
   private final boolean cooked;
   private final ItemFishFood.FishType type;

   public ItemFishFood(ItemFishFood.FishType p_i48495_1_, boolean p_i48495_2_, Item.Properties p_i48495_3_) {
      super(0, 0.0F, false, p_i48495_3_);
      this.type = p_i48495_1_;
      this.cooked = p_i48495_2_;
   }

   public int getHealAmount(ItemStack p_150905_1_) {
      ItemFishFood.FishType itemfishfood$fishtype = ItemFishFood.FishType.byItemStack(p_150905_1_);
      return this.cooked && itemfishfood$fishtype.canCook() ? itemfishfood$fishtype.getCookedHealAmount() : itemfishfood$fishtype.getUncookedHealAmount();
   }

   public float getSaturationModifier(ItemStack p_150906_1_) {
      return this.cooked && this.type.canCook() ? this.type.getCookedSaturationModifier() : this.type.getUncookedSaturationModifier();
   }

   protected void onFoodEaten(ItemStack p_77849_1_, World p_77849_2_, EntityPlayer p_77849_3_) {
      ItemFishFood.FishType itemfishfood$fishtype = ItemFishFood.FishType.byItemStack(p_77849_1_);
      if (itemfishfood$fishtype == ItemFishFood.FishType.PUFFERFISH) {
         p_77849_3_.addPotionEffect(new PotionEffect(MobEffects.POISON, 1200, 3));
         p_77849_3_.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 300, 2));
         p_77849_3_.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 300, 1));
      }

      super.onFoodEaten(p_77849_1_, p_77849_2_, p_77849_3_);
   }

   public enum FishType {
      COD(2, 0.1F, 5, 0.6F),
      SALMON(2, 0.1F, 6, 0.8F),
      TROPICAL_FISH(1, 0.1F),
      PUFFERFISH(1, 0.1F);

      private final int uncookedHealAmount;
      private final float uncookedSaturationModifier;
      private final int cookedHealAmount;
      private final float cookedSaturationModifier;
      private final boolean cookable;

      FishType(int p_i49622_3_, float p_i49622_4_, int p_i49622_5_, float p_i49622_6_) {
         this.uncookedHealAmount = p_i49622_3_;
         this.uncookedSaturationModifier = p_i49622_4_;
         this.cookedHealAmount = p_i49622_5_;
         this.cookedSaturationModifier = p_i49622_6_;
         this.cookable = p_i49622_5_ != 0;
      }

      FishType(int p_i49623_3_, float p_i49623_4_) {
         this(p_i49623_3_, p_i49623_4_, 0, 0.0F);
      }

      public int getUncookedHealAmount() {
         return this.uncookedHealAmount;
      }

      public float getUncookedSaturationModifier() {
         return this.uncookedSaturationModifier;
      }

      public int getCookedHealAmount() {
         return this.cookedHealAmount;
      }

      public float getCookedSaturationModifier() {
         return this.cookedSaturationModifier;
      }

      public boolean canCook() {
         return this.cookable;
      }

      public static ItemFishFood.FishType byItemStack(ItemStack p_150978_0_) {
         Item item = p_150978_0_.getItem();
         return item instanceof ItemFishFood ? ((ItemFishFood)item).type : COD;
      }
   }
}
