package net.minecraft.entity.ai;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public abstract class EntityAITarget extends EntityAIBase {
   protected final EntityCreature taskOwner;
   protected boolean shouldCheckSight;
   private final boolean nearbyOnly;
   private int targetSearchStatus;
   private int targetSearchDelay;
   private int targetUnseenTicks;
   protected EntityLivingBase target;
   protected int unseenMemoryTicks = 60;

   public EntityAITarget(EntityCreature p_i1669_1_, boolean p_i1669_2_) {
      this(p_i1669_1_, p_i1669_2_, false);
   }

   public EntityAITarget(EntityCreature p_i1670_1_, boolean p_i1670_2_, boolean p_i1670_3_) {
      this.taskOwner = p_i1670_1_;
      this.shouldCheckSight = p_i1670_2_;
      this.nearbyOnly = p_i1670_3_;
   }

   public boolean shouldContinueExecuting() {
      EntityLivingBase entitylivingbase = this.taskOwner.getAttackTarget();
      if (entitylivingbase == null) {
         entitylivingbase = this.target;
      }

      if (entitylivingbase == null) {
         return false;
      } else if (!entitylivingbase.isEntityAlive()) {
         return false;
      } else {
         Team team = this.taskOwner.getTeam();
         Team team1 = entitylivingbase.getTeam();
         if (team != null && team1 == team) {
            return false;
         } else {
            double d0 = this.getTargetDistance();
            if (this.taskOwner.getDistanceSq(entitylivingbase) > d0 * d0) {
               return false;
            } else {
               if (this.shouldCheckSight) {
                  if (this.taskOwner.getEntitySenses().canSee(entitylivingbase)) {
                     this.targetUnseenTicks = 0;
                  } else if (++this.targetUnseenTicks > this.unseenMemoryTicks) {
                     return false;
                  }
               }

               if (entitylivingbase instanceof EntityPlayer && ((EntityPlayer)entitylivingbase).capabilities.disableDamage) {
                  return false;
               } else {
                  this.taskOwner.setAttackTarget(entitylivingbase);
                  return true;
               }
            }
         }
      }
   }

   protected double getTargetDistance() {
      IAttributeInstance iattributeinstance = this.taskOwner.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
      return iattributeinstance == null ? 16.0D : iattributeinstance.getAttributeValue();
   }

   public void startExecuting() {
      this.targetSearchStatus = 0;
      this.targetSearchDelay = 0;
      this.targetUnseenTicks = 0;
   }

   public void resetTask() {
      this.taskOwner.setAttackTarget(null);
      this.target = null;
   }

   public static boolean isSuitableTarget(EntityLiving p_179445_0_, @Nullable EntityLivingBase p_179445_1_, boolean p_179445_2_, boolean p_179445_3_) {
      if (p_179445_1_ == null) {
         return false;
      } else if (p_179445_1_ == p_179445_0_) {
         return false;
      } else if (!p_179445_1_.isEntityAlive()) {
         return false;
      } else if (!p_179445_0_.canAttackClass(p_179445_1_.getClass())) {
         return false;
      } else if (p_179445_0_.isOnSameTeam(p_179445_1_)) {
         return false;
      } else {
         if (p_179445_0_ instanceof IEntityOwnable && ((IEntityOwnable)p_179445_0_).getOwnerId() != null) {
            if (p_179445_1_ instanceof IEntityOwnable && ((IEntityOwnable)p_179445_0_).getOwnerId().equals(((IEntityOwnable)p_179445_1_).getOwnerId())) {
               return false;
            }

            if (p_179445_1_ == ((IEntityOwnable)p_179445_0_).getOwner()) {
               return false;
            }
         } else if (p_179445_1_ instanceof EntityPlayer && !p_179445_2_ && ((EntityPlayer)p_179445_1_).capabilities.disableDamage) {
            return false;
         }

         return !p_179445_3_ || p_179445_0_.getEntitySenses().canSee(p_179445_1_);
      }
   }

   protected boolean isSuitableTarget(@Nullable EntityLivingBase p_75296_1_, boolean p_75296_2_) {
      if (!isSuitableTarget(this.taskOwner, p_75296_1_, p_75296_2_, this.shouldCheckSight)) {
         return false;
      } else if (!this.taskOwner.isWithinHomeDistanceFromPosition(new BlockPos(p_75296_1_))) {
         return false;
      } else {
         if (this.nearbyOnly) {
            if (--this.targetSearchDelay <= 0) {
               this.targetSearchStatus = 0;
            }

            if (this.targetSearchStatus == 0) {
               this.targetSearchStatus = this.canEasilyReach(p_75296_1_) ? 1 : 2;
            }

             return this.targetSearchStatus != 2;
         }

         return true;
      }
   }

   private boolean canEasilyReach(EntityLivingBase p_75295_1_) {
      this.targetSearchDelay = 10 + this.taskOwner.getRNG().nextInt(5);
      Path path = this.taskOwner.getNavigator().getPathToEntityLiving(p_75295_1_);
      if (path == null) {
         return false;
      } else {
         PathPoint pathpoint = path.getFinalPathPoint();
         if (pathpoint == null) {
            return false;
         } else {
            int i = pathpoint.x - MathHelper.floor(p_75295_1_.posX);
            int j = pathpoint.z - MathHelper.floor(p_75295_1_.posZ);
            return (double)(i * i + j * j) <= 2.25D;
         }
      }
   }

   public EntityAITarget setUnseenMemoryTicks(int p_190882_1_) {
      this.unseenMemoryTicks = p_190882_1_;
      return this;
   }
}
