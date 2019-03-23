package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.util.math.MathHelper;

public class EntityAIAttackRanged extends EntityAIBase {
   private final EntityLiving entityHost;
   private final IRangedAttackMob rangedAttackEntityHost;
   private EntityLivingBase attackTarget;
   private int rangedAttackTime = -1;
   private final double entityMoveSpeed;
   private int seeTime;
   private final int attackIntervalMin;
   private final int maxRangedAttackTime;
   private final float attackRadius;
   private final float maxAttackDistance;

   public EntityAIAttackRanged(IRangedAttackMob p_i1649_1_, double p_i1649_2_, int p_i1649_4_, float p_i1649_5_) {
      this(p_i1649_1_, p_i1649_2_, p_i1649_4_, p_i1649_4_, p_i1649_5_);
   }

   public EntityAIAttackRanged(IRangedAttackMob p_i1650_1_, double p_i1650_2_, int p_i1650_4_, int p_i1650_5_, float p_i1650_6_) {
      if (!(p_i1650_1_ instanceof EntityLivingBase)) {
         throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
      } else {
         this.rangedAttackEntityHost = p_i1650_1_;
         this.entityHost = (EntityLiving)p_i1650_1_;
         this.entityMoveSpeed = p_i1650_2_;
         this.attackIntervalMin = p_i1650_4_;
         this.maxRangedAttackTime = p_i1650_5_;
         this.attackRadius = p_i1650_6_;
         this.maxAttackDistance = p_i1650_6_ * p_i1650_6_;
         this.setMutexBits(3);
      }
   }

   public boolean shouldExecute() {
      EntityLivingBase entitylivingbase = this.entityHost.getAttackTarget();
      if (entitylivingbase == null) {
         return false;
      } else {
         this.attackTarget = entitylivingbase;
         return true;
      }
   }

   public boolean shouldContinueExecuting() {
      return this.shouldExecute() || !this.entityHost.getNavigator().noPath();
   }

   public void resetTask() {
      this.attackTarget = null;
      this.seeTime = 0;
      this.rangedAttackTime = -1;
   }

   public void updateTask() {
      double d0 = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY, this.attackTarget.posZ);
      boolean flag = this.entityHost.getEntitySenses().canSee(this.attackTarget);
      if (flag) {
         ++this.seeTime;
      } else {
         this.seeTime = 0;
      }

      if (!(d0 > (double)this.maxAttackDistance) && this.seeTime >= 20) {
         this.entityHost.getNavigator().clearPath();
      } else {
         this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
      }

      this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);
      if (--this.rangedAttackTime == 0) {
         if (!flag) {
            return;
         }

         float f = MathHelper.sqrt(d0) / this.attackRadius;
         float lvt_5_1_ = MathHelper.clamp(f, 0.1F, 1.0F);
         this.rangedAttackEntityHost.attackEntityWithRangedAttack(this.attackTarget, lvt_5_1_);
         this.rangedAttackTime = MathHelper.floor(f * (float)(this.maxRangedAttackTime - this.attackIntervalMin) + (float)this.attackIntervalMin);
      } else if (this.rangedAttackTime < 0) {
         float f2 = MathHelper.sqrt(d0) / this.attackRadius;
         this.rangedAttackTime = MathHelper.floor(f2 * (float)(this.maxRangedAttackTime - this.attackIntervalMin) + (float)this.attackIntervalMin);
      }

   }
}
