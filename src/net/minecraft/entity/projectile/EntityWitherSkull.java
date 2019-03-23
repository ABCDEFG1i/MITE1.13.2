package net.minecraft.entity.projectile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityWitherSkull extends EntityFireball {
   private static final DataParameter<Boolean> INVULNERABLE = EntityDataManager.createKey(EntityWitherSkull.class, DataSerializers.BOOLEAN);

   public EntityWitherSkull(World p_i1793_1_) {
      super(EntityType.WITHER_SKULL, p_i1793_1_, 0.3125F, 0.3125F);
   }

   public EntityWitherSkull(World p_i1794_1_, EntityLivingBase p_i1794_2_, double p_i1794_3_, double p_i1794_5_, double p_i1794_7_) {
      super(EntityType.WITHER_SKULL, p_i1794_2_, p_i1794_3_, p_i1794_5_, p_i1794_7_, p_i1794_1_, 0.3125F, 0.3125F);
   }

   @OnlyIn(Dist.CLIENT)
   public EntityWitherSkull(World p_i1795_1_, double p_i1795_2_, double p_i1795_4_, double p_i1795_6_, double p_i1795_8_, double p_i1795_10_, double p_i1795_12_) {
      super(EntityType.WITHER_SKULL, p_i1795_2_, p_i1795_4_, p_i1795_6_, p_i1795_8_, p_i1795_10_, p_i1795_12_, p_i1795_1_, 0.3125F, 0.3125F);
   }

   protected float getMotionFactor() {
      return this.isSkullInvulnerable() ? 0.73F : super.getMotionFactor();
   }

   public boolean isBurning() {
      return false;
   }

   public float getExplosionResistance(Explosion p_180428_1_, IBlockReader p_180428_2_, BlockPos p_180428_3_, IBlockState p_180428_4_, IFluidState p_180428_5_, float p_180428_6_) {
      return this.isSkullInvulnerable() && EntityWither.canDestroyBlock(p_180428_4_.getBlock()) ? Math.min(0.8F, p_180428_6_) : p_180428_6_;
   }

   protected void onImpact(RayTraceResult p_70227_1_) {
      if (!this.world.isRemote) {
         if (p_70227_1_.entity != null) {
            if (this.shootingEntity != null) {
               if (p_70227_1_.entity.attackEntityFrom(DamageSource.causeMobDamage(this.shootingEntity), 8.0F)) {
                  if (p_70227_1_.entity.isEntityAlive()) {
                     this.applyEnchantments(this.shootingEntity, p_70227_1_.entity);
                  } else {
                     this.shootingEntity.heal(5.0F);
                  }
               }
            } else {
               p_70227_1_.entity.attackEntityFrom(DamageSource.MAGIC, 5.0F);
            }

            if (p_70227_1_.entity instanceof EntityLivingBase) {
               int i = 0;
               if (this.world.getDifficulty() == EnumDifficulty.NORMAL) {
                  i = 10;
               } else if (this.world.getDifficulty() == EnumDifficulty.HARD) {
                  i = 40;
               }

               if (i > 0) {
                  ((EntityLivingBase)p_70227_1_.entity).addPotionEffect(new PotionEffect(MobEffects.WITHER, 20 * i, 1));
               }
            }
         }

         this.world.newExplosion(this, this.posX, this.posY, this.posZ, 1.0F, false, this.world.getGameRules().getBoolean("mobGriefing"));
         this.setDead();
      }

   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      return false;
   }

   protected void registerData() {
      this.dataManager.register(INVULNERABLE, false);
   }

   public boolean isSkullInvulnerable() {
      return this.dataManager.get(INVULNERABLE);
   }

   public void setSkullInvulnerable(boolean p_82343_1_) {
      this.dataManager.set(INVULNERABLE, p_82343_1_);
   }

   protected boolean isFireballFiery() {
      return false;
   }
}
