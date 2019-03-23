package net.minecraft.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.world.World;

public class ItemSpectralArrow extends ItemArrow {
   public ItemSpectralArrow(Item.Properties p_i48464_1_) {
      super(p_i48464_1_);
   }

   public EntityArrow createArrow(World p_200887_1_, ItemStack p_200887_2_, EntityLivingBase p_200887_3_) {
      return new EntitySpectralArrow(p_200887_1_, p_200887_3_);
   }
}
