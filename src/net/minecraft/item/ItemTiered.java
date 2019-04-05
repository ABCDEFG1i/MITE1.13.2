package net.minecraft.item;

public class ItemTiered extends Item {
   private final IItemTier tier;

   public ItemTiered(int maxUses,IItemTier p_i48459_1_, Item.Properties p_i48459_2_) {
       super(p_i48459_2_.setDamageIfHavent(maxUses));
      this.tier = p_i48459_1_;
   }

   public IItemTier getTier() {
      return this.tier;
   }

   public int getItemEnchantability() {
      return this.tier.getEnchantability();
   }

   public boolean getIsRepairable(ItemStack p_82789_1_, ItemStack p_82789_2_) {
      return this.tier.getRepairMaterial().test(p_82789_2_) || super.getIsRepairable(p_82789_1_, p_82789_2_);
   }
}
