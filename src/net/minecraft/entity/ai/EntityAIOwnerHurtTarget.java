package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAIOwnerHurtTarget extends EntityAITarget {
   private final EntityTameable tameable;
   private EntityLivingBase attacker;
   private int timestamp;

   public EntityAIOwnerHurtTarget(EntityTameable p_i1668_1_) {
      super(p_i1668_1_, false);
      this.tameable = p_i1668_1_;
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
            this.attacker = entitylivingbase.getLastAttackedEntity();
            int i = entitylivingbase.getLastAttackedEntityTime();
            return i != this.timestamp && this.isSuitableTarget(this.attacker, false) && this.tameable.shouldAttackEntity(this.attacker, entitylivingbase);
         }
      }
   }

   public void startExecuting() {
      this.taskOwner.setAttackTarget(this.attacker);
      EntityLivingBase entitylivingbase = this.tameable.getOwner();
      if (entitylivingbase != null) {
         this.timestamp = entitylivingbase.getLastAttackedEntityTime();
      }

      super.startExecuting();
   }
}
