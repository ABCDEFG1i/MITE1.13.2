package net.minecraft.entity.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityExpBottle extends EntityThrowable {
   public EntityExpBottle(World p_i1785_1_) {
      super(EntityType.EXPERIENCE_BOTTLE, p_i1785_1_);
   }

   public EntityExpBottle(World p_i1786_1_, EntityLivingBase p_i1786_2_) {
      super(EntityType.EXPERIENCE_BOTTLE, p_i1786_2_, p_i1786_1_);
   }

   public EntityExpBottle(World p_i1787_1_, double p_i1787_2_, double p_i1787_4_, double p_i1787_6_) {
      super(EntityType.EXPERIENCE_BOTTLE, p_i1787_2_, p_i1787_4_, p_i1787_6_, p_i1787_1_);
   }

   protected float getGravityVelocity() {
      return 0.07F;
   }

   protected void onImpact(RayTraceResult p_70184_1_) {
      if (!this.world.isRemote) {
         this.world.playEvent(2002, new BlockPos(this), PotionUtils.getPotionColor(PotionTypes.WATER));
         int i = 3 + this.world.rand.nextInt(5) + this.world.rand.nextInt(5);

         while(i > 0) {
            int j = EntityXPOrb.getXPSplit(i);
            i -= j;
            this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, j));
         }

         this.setDead();
      }

   }
}
