package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;

public class EntityAISwimming extends EntityAIBase {
   private final EntityLiving entity;

   public EntityAISwimming(EntityLiving p_i1624_1_) {
      this.entity = p_i1624_1_;
      this.setMutexBits(4);
      p_i1624_1_.getNavigator().setCanSwim(true);
   }

   public boolean shouldExecute() {
      return this.entity.isInWater() && this.entity.getSubmergedHeight() > 0.4D || this.entity.isInLava();
   }

   public void updateTask() {
      if (this.entity.getRNG().nextFloat() < 0.8F) {
         this.entity.getJumpHelper().setJumping();
      }

   }
}
