package net.minecraft.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.world.World;

public class ItemArrow extends Item {
   public ItemArrow(Item.Properties p_i48531_1_) {
      super(p_i48531_1_);
   }

   public EntityArrow createArrow(World p_200887_1_, ItemStack p_200887_2_, EntityLivingBase p_200887_3_) {
      EntityTippedArrow entitytippedarrow = new EntityTippedArrow(p_200887_1_, p_200887_3_);
      entitytippedarrow.setPotionEffect(p_200887_2_);
      return entitytippedarrow;
   }
}
