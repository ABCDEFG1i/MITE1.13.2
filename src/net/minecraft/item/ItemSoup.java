package net.minecraft.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.world.World;

public class ItemSoup extends ItemFood {
   public ItemSoup(int p_i48521_1_, Item.Properties p_i48521_2_) {
      super(p_i48521_1_, 0.6F, false, p_i48521_2_);
   }

   public ItemStack onItemUseFinish(ItemStack p_77654_1_, World p_77654_2_, EntityLivingBase p_77654_3_) {
      super.onItemUseFinish(p_77654_1_, p_77654_2_, p_77654_3_);
      return new ItemStack(Items.BOWL);
   }
}
