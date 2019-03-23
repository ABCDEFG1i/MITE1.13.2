package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.MathHelper;

public class EntityMoveHelper {
   protected final EntityLiving entity;
   protected double posX;
   protected double posY;
   protected double posZ;
   protected double speed;
   protected float moveForward;
   protected float moveStrafe;
   protected EntityMoveHelper.Action action = EntityMoveHelper.Action.WAIT;

   public EntityMoveHelper(EntityLiving p_i1614_1_) {
      this.entity = p_i1614_1_;
   }

   public boolean isUpdating() {
      return this.action == EntityMoveHelper.Action.MOVE_TO;
   }

   public double getSpeed() {
      return this.speed;
   }

   public void setMoveTo(double p_75642_1_, double p_75642_3_, double p_75642_5_, double p_75642_7_) {
      this.posX = p_75642_1_;
      this.posY = p_75642_3_;
      this.posZ = p_75642_5_;
      this.speed = p_75642_7_;
      if (this.action != EntityMoveHelper.Action.JUMPING) {
         this.action = EntityMoveHelper.Action.MOVE_TO;
      }

   }

   public void strafe(float p_188488_1_, float p_188488_2_) {
      this.action = EntityMoveHelper.Action.STRAFE;
      this.moveForward = p_188488_1_;
      this.moveStrafe = p_188488_2_;
      this.speed = 0.25D;
   }

   public void read(EntityMoveHelper p_188487_1_) {
      this.action = p_188487_1_.action;
      this.posX = p_188487_1_.posX;
      this.posY = p_188487_1_.posY;
      this.posZ = p_188487_1_.posZ;
      this.speed = Math.max(p_188487_1_.speed, 1.0D);
      this.moveForward = p_188487_1_.moveForward;
      this.moveStrafe = p_188487_1_.moveStrafe;
   }

   public void tick() {
      if (this.action == EntityMoveHelper.Action.STRAFE) {
         float f = (float)this.entity.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
         float f1 = (float)this.speed * f;
         float f2 = this.moveForward;
         float f3 = this.moveStrafe;
         float f4 = MathHelper.sqrt(f2 * f2 + f3 * f3);
         if (f4 < 1.0F) {
            f4 = 1.0F;
         }

         f4 = f1 / f4;
         f2 = f2 * f4;
         f3 = f3 * f4;
         float f5 = MathHelper.sin(this.entity.rotationYaw * ((float)Math.PI / 180F));
         float f6 = MathHelper.cos(this.entity.rotationYaw * ((float)Math.PI / 180F));
         float f7 = f2 * f6 - f3 * f5;
         float f8 = f3 * f6 + f2 * f5;
         PathNavigate pathnavigate = this.entity.getNavigator();
         if (pathnavigate != null) {
            NodeProcessor nodeprocessor = pathnavigate.getNodeProcessor();
            if (nodeprocessor != null && nodeprocessor.getPathNodeType(this.entity.world, MathHelper.floor(this.entity.posX + (double)f7), MathHelper.floor(this.entity.posY), MathHelper.floor(this.entity.posZ + (double)f8)) != PathNodeType.WALKABLE) {
               this.moveForward = 1.0F;
               this.moveStrafe = 0.0F;
               f1 = f;
            }
         }

         this.entity.setAIMoveSpeed(f1);
         this.entity.setMoveForward(this.moveForward);
         this.entity.setMoveStrafing(this.moveStrafe);
         this.action = EntityMoveHelper.Action.WAIT;
      } else if (this.action == EntityMoveHelper.Action.MOVE_TO) {
         this.action = EntityMoveHelper.Action.WAIT;
         double d0 = this.posX - this.entity.posX;
         double d1 = this.posZ - this.entity.posZ;
         double d2 = this.posY - this.entity.posY;
         double d3 = d0 * d0 + d2 * d2 + d1 * d1;
         if (d3 < (double)2.5000003E-7F) {
            this.entity.setMoveForward(0.0F);
            return;
         }

         float f9 = (float)(MathHelper.atan2(d1, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
         this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, f9, 90.0F);
         this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()));
         if (d2 > (double)this.entity.stepHeight && d0 * d0 + d1 * d1 < (double)Math.max(1.0F, this.entity.width)) {
            this.entity.getJumpHelper().setJumping();
            this.action = EntityMoveHelper.Action.JUMPING;
         }
      } else if (this.action == EntityMoveHelper.Action.JUMPING) {
         this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()));
         if (this.entity.onGround) {
            this.action = EntityMoveHelper.Action.WAIT;
         }
      } else {
         this.entity.setMoveForward(0.0F);
      }

   }

   protected float limitAngle(float p_75639_1_, float p_75639_2_, float p_75639_3_) {
      float f = MathHelper.wrapDegrees(p_75639_2_ - p_75639_1_);
      if (f > p_75639_3_) {
         f = p_75639_3_;
      }

      if (f < -p_75639_3_) {
         f = -p_75639_3_;
      }

      float f1 = p_75639_1_ + f;
      if (f1 < 0.0F) {
         f1 += 360.0F;
      } else if (f1 > 360.0F) {
         f1 -= 360.0F;
      }

      return f1;
   }

   public double getX() {
      return this.posX;
   }

   public double getY() {
      return this.posY;
   }

   public double getZ() {
      return this.posZ;
   }

   public static enum Action {
      WAIT,
      MOVE_TO,
      STRAFE,
      JUMPING;
   }
}
