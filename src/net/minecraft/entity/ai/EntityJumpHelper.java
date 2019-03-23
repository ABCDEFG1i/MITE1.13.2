package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;

public class EntityJumpHelper {
   private final EntityLiving entity;
   protected boolean isJumping;

   public EntityJumpHelper(EntityLiving p_i1612_1_) {
      this.entity = p_i1612_1_;
   }

   public void setJumping() {
      this.isJumping = true;
   }

   public void tick() {
      this.entity.setJumping(this.isJumping);
      this.isJumping = false;
   }
}
