package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAIOwnerHurtByTarget extends EntityAITarget {
   private final EntityTameable tameable;
   private EntityLivingBase attacker;
   private int timestamp;

   public EntityAIOwnerHurtByTarget(EntityTameable p_i1667_1_) {
      super(p_i1667_1_, false);
      this.tameable = p_i1667_1_;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      if (!this.tameable.isTamed()) {
         return false;
      } else {
         EntityLivingBase entitylivingbase = this.tameable.getOwner();
         if (entitylivingbase == null) {
            return false;
         } else {
            this.attacker = entitylivingbase.getRevengeTarget();
            int i = entitylivingbase.getRevengeTimer();
            return i != this.timestamp && this.isSuitableTarget(this.attacker, false) && this.tameable.shouldAttackEntity(this.attacker, entitylivingbase);
         }
      }
   }

   public void startExecuting() {
      this.taskOwner.setAttackTarget(this.attacker);
      EntityLivingBase entitylivingbase = this.tameable.getOwner();
      if (entitylivingbase != null) {
         this.timestamp = entitylivingbase.getRevengeTimer();
      }

      super.startExecuting();
   }
}
