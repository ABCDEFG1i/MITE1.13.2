package net.minecraft.entity.ai;

import net.minecraft.entity.monster.EntityZombie;

public class EntityAIZombieAttack extends EntityAIAttackMelee {
   private final EntityZombie zombie;
   private int raiseArmTicks;

   public EntityAIZombieAttack(EntityZombie p_i46803_1_, double p_i46803_2_, boolean p_i46803_4_) {
      super(p_i46803_1_, p_i46803_2_, p_i46803_4_);
      this.zombie = p_i46803_1_;
   }

   public void startExecuting() {
      super.startExecuting();
      this.raiseArmTicks = 0;
   }

   public void resetTask() {
      super.resetTask();
      this.zombie.setSwingingArms(false);
   }

   public void updateTask() {
      super.updateTask();
      ++this.raiseArmTicks;
      if (this.raiseArmTicks >= 5 && this.attackTick < 10) {
         this.zombie.setSwingingArms(true);
      } else {
         this.zombie.setSwingingArms(false);
      }

   }
}
