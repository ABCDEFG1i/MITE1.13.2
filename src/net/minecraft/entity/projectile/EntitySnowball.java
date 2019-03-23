package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.init.Particles;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntitySnowball extends EntityThrowable {
   public EntitySnowball(World p_i1773_1_) {
      super(EntityType.SNOWBALL, p_i1773_1_);
   }

   public EntitySnowball(World p_i1774_1_, EntityLivingBase p_i1774_2_) {
      super(EntityType.SNOWBALL, p_i1774_2_, p_i1774_1_);
   }

   public EntitySnowball(World p_i1775_1_, double p_i1775_2_, double p_i1775_4_, double p_i1775_6_) {
      super(EntityType.SNOWBALL, p_i1775_2_, p_i1775_4_, p_i1775_6_, p_i1775_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 3) {
         for(int i = 0; i < 8; ++i) {
            this.world.spawnParticle(Particles.ITEM_SNOWBALL, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   protected void onImpact(RayTraceResult p_70184_1_) {
      if (p_70184_1_.entity != null) {
         int i = 0;
         if (p_70184_1_.entity instanceof EntityBlaze) {
            i = 3;
         }

         p_70184_1_.entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), (float)i);
      }

      if (!this.world.isRemote) {
         this.world.setEntityState(this, (byte)3);
         this.setDead();
      }

   }
}
