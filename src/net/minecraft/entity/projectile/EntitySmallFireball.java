package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntitySmallFireball extends EntityFireball {
   public EntitySmallFireball(World p_i1770_1_) {
      super(EntityType.SMALL_FIREBALL, p_i1770_1_, 0.3125F, 0.3125F);
   }

   public EntitySmallFireball(World p_i1771_1_, EntityLivingBase p_i1771_2_, double p_i1771_3_, double p_i1771_5_, double p_i1771_7_) {
      super(EntityType.SMALL_FIREBALL, p_i1771_2_, p_i1771_3_, p_i1771_5_, p_i1771_7_, p_i1771_1_, 0.3125F, 0.3125F);
   }

   public EntitySmallFireball(World p_i1772_1_, double p_i1772_2_, double p_i1772_4_, double p_i1772_6_, double p_i1772_8_, double p_i1772_10_, double p_i1772_12_) {
      super(EntityType.SMALL_FIREBALL, p_i1772_2_, p_i1772_4_, p_i1772_6_, p_i1772_8_, p_i1772_10_, p_i1772_12_, p_i1772_1_, 0.3125F, 0.3125F);
   }

   protected void onImpact(RayTraceResult p_70227_1_) {
      if (!this.world.isRemote) {
         if (p_70227_1_.entity != null) {
            if (!p_70227_1_.entity.isImmuneToFire()) {
               p_70227_1_.entity.setFire(5);
               boolean flag = p_70227_1_.entity.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 5.0F);
               if (flag) {
                  this.applyEnchantments(this.shootingEntity, p_70227_1_.entity);
               }
            }
         } else {
            boolean flag1 = true;
            if (this.shootingEntity != null && this.shootingEntity instanceof EntityLiving) {
               flag1 = this.world.getGameRules().getBoolean("mobGriefing");
            }

            if (flag1) {
               BlockPos blockpos = p_70227_1_.getBlockPos().offset(p_70227_1_.sideHit);
               if (this.world.isAirBlock(blockpos)) {
                  this.world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
               }
            }
         }

         this.setDead();
      }

   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      return false;
   }
}
