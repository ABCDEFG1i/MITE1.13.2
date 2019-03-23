package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.math.AxisAlignedBB;

public class EntityAIHurtByTarget extends EntityAITarget {
   private final boolean entityCallsForHelp;
   private int revengeTimerOld;
   private final Class<?>[] excludedReinforcementTypes;

   public EntityAIHurtByTarget(EntityCreature p_i45885_1_, boolean p_i45885_2_, Class<?>... p_i45885_3_) {
      super(p_i45885_1_, true);
      this.entityCallsForHelp = p_i45885_2_;
      this.excludedReinforcementTypes = p_i45885_3_;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      int i = this.taskOwner.getRevengeTimer();
      EntityLivingBase entitylivingbase = this.taskOwner.getRevengeTarget();
      return i != this.revengeTimerOld && entitylivingbase != null && this.isSuitableTarget(entitylivingbase, false);
   }

   public void startExecuting() {
      this.taskOwner.setAttackTarget(this.taskOwner.getRevengeTarget());
      this.target = this.taskOwner.getAttackTarget();
      this.revengeTimerOld = this.taskOwner.getRevengeTimer();
      this.unseenMemoryTicks = 300;
      if (this.entityCallsForHelp) {
         this.alertOthers();
      }

      super.startExecuting();
   }

   protected void alertOthers() {
      double d0 = this.getTargetDistance();

      for(EntityCreature entitycreature : this.taskOwner.world.getEntitiesWithinAABB(this.taskOwner.getClass(), (new AxisAlignedBB(this.taskOwner.posX, this.taskOwner.posY, this.taskOwner.posZ, this.taskOwner.posX + 1.0D, this.taskOwner.posY + 1.0D, this.taskOwner.posZ + 1.0D)).grow(d0, 10.0D, d0))) {
         if (this.taskOwner != entitycreature && entitycreature.getAttackTarget() == null && (!(this.taskOwner instanceof EntityTameable) || ((EntityTameable)this.taskOwner).getOwner() == ((EntityTameable)entitycreature).getOwner()) && !entitycreature.isOnSameTeam(this.taskOwner.getRevengeTarget())) {
            boolean flag = false;

            for(Class<?> oclass : this.excludedReinforcementTypes) {
               if (entitycreature.getClass() == oclass) {
                  flag = true;
                  break;
               }
            }

            if (!flag) {
               this.setEntityAttackTarget(entitycreature, this.taskOwner.getRevengeTarget());
            }
         }
      }

   }

   protected void setEntityAttackTarget(EntityCreature p_179446_1_, EntityLivingBase p_179446_2_) {
      p_179446_1_.setAttackTarget(p_179446_2_);
   }
}
