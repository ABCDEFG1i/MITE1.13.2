package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;

public class EntityAIOpenDoor extends EntityAIDoorInteract {
   private final boolean closeDoor;
   private int closeDoorTemporisation;

   public EntityAIOpenDoor(EntityLiving p_i1644_1_, boolean p_i1644_2_) {
      super(p_i1644_1_);
      this.entity = p_i1644_1_;
      this.closeDoor = p_i1644_2_;
   }

   public boolean shouldContinueExecuting() {
      return this.closeDoor && this.closeDoorTemporisation > 0 && super.shouldContinueExecuting();
   }

   public void startExecuting() {
      this.closeDoorTemporisation = 20;
      this.func_195921_a(true);
   }

   public void resetTask() {
      this.func_195921_a(false);
   }

   public void updateTask() {
      --this.closeDoorTemporisation;
      super.updateTask();
   }
}
