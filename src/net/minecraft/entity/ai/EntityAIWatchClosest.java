package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;

public class EntityAIWatchClosest extends EntityAIBase {
   protected EntityLiving entity;
   protected Entity closestEntity;
   protected float maxDistance;
   private int lookTime;
   private final float chance;
   protected Class<? extends Entity> watchedClass;

   public EntityAIWatchClosest(EntityLiving p_i1631_1_, Class<? extends Entity> p_i1631_2_, float p_i1631_3_) {
      this(p_i1631_1_, p_i1631_2_, p_i1631_3_, 0.02F);
   }

   public EntityAIWatchClosest(EntityLiving p_i1632_1_, Class<? extends Entity> p_i1632_2_, float p_i1632_3_, float p_i1632_4_) {
      this.entity = p_i1632_1_;
      this.watchedClass = p_i1632_2_;
      this.maxDistance = p_i1632_3_;
      this.chance = p_i1632_4_;
      this.setMutexBits(2);
   }

   public boolean shouldExecute() {
      if (this.entity.getRNG().nextFloat() >= this.chance) {
         return false;
      } else {
         if (this.entity.getAttackTarget() != null) {
            this.closestEntity = this.entity.getAttackTarget();
         }

         if (this.watchedClass == EntityPlayer.class) {
            this.closestEntity = this.entity.world.getClosestPlayer(this.entity.posX, this.entity.posY, this.entity.posZ, (double)this.maxDistance, EntitySelectors.NOT_SPECTATING.and(EntitySelectors.func_200820_b(this.entity)));
         } else {
            this.closestEntity = this.entity.world.findNearestEntityWithinAABB(this.watchedClass, this.entity.getEntityBoundingBox().grow((double)this.maxDistance, 3.0D, (double)this.maxDistance), this.entity);
         }

         return this.closestEntity != null;
      }
   }

   public boolean shouldContinueExecuting() {
      if (!this.closestEntity.isEntityAlive()) {
         return false;
      } else if (this.entity.getDistanceSq(this.closestEntity) > (double)(this.maxDistance * this.maxDistance)) {
         return false;
      } else {
         return this.lookTime > 0;
      }
   }

   public void startExecuting() {
      this.lookTime = 40 + this.entity.getRNG().nextInt(40);
   }

   public void resetTask() {
      this.closestEntity = null;
   }

   public void updateTask() {
      this.entity.getLookHelper().setLookPosition(this.closestEntity.posX, this.closestEntity.posY + (double)this.closestEntity.getEyeHeight(), this.closestEntity.posZ, (float)this.entity.getHorizontalFaceSpeed(), (float)this.entity.getVerticalFaceSpeed());
      --this.lookTime;
   }
}
