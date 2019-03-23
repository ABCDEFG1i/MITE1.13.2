package net.minecraft.entity.ai;

import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class EntityAIFollowBoat extends EntityAIBase {
   private int field_205143_a;
   private final EntityCreature field_205144_b;
   private EntityLivingBase field_205145_c;
   private BoatGoals field_205146_d;

   public EntityAIFollowBoat(EntityCreature p_i48939_1_) {
      this.field_205144_b = p_i48939_1_;
   }

   public boolean shouldExecute() {
      List<EntityBoat> list = this.field_205144_b.world.getEntitiesWithinAABB(EntityBoat.class, this.field_205144_b.getEntityBoundingBox().grow(5.0D));
      boolean flag = false;

      for(EntityBoat entityboat : list) {
         if (entityboat.getControllingPassenger() != null && (MathHelper.abs(((EntityLivingBase)entityboat.getControllingPassenger()).moveStrafing) > 0.0F || MathHelper.abs(((EntityLivingBase)entityboat.getControllingPassenger()).moveForward) > 0.0F)) {
            flag = true;
            break;
         }
      }

      return this.field_205145_c != null && (MathHelper.abs(this.field_205145_c.moveStrafing) > 0.0F || MathHelper.abs(this.field_205145_c.moveForward) > 0.0F) || flag;
   }

   public boolean isInterruptible() {
      return true;
   }

   public boolean shouldContinueExecuting() {
      return this.field_205145_c != null && this.field_205145_c.isRiding() && (MathHelper.abs(this.field_205145_c.moveStrafing) > 0.0F || MathHelper.abs(this.field_205145_c.moveForward) > 0.0F);
   }

   public void startExecuting() {
      for(EntityBoat entityboat : this.field_205144_b.world.getEntitiesWithinAABB(EntityBoat.class, this.field_205144_b.getEntityBoundingBox().grow(5.0D))) {
         if (entityboat.getControllingPassenger() != null && entityboat.getControllingPassenger() instanceof EntityLivingBase) {
            this.field_205145_c = (EntityLivingBase)entityboat.getControllingPassenger();
            break;
         }
      }

      this.field_205143_a = 0;
      this.field_205146_d = BoatGoals.GO_TO_BOAT;
   }

   public void resetTask() {
      this.field_205145_c = null;
   }

   public void updateTask() {
      boolean flag = MathHelper.abs(this.field_205145_c.moveStrafing) > 0.0F || MathHelper.abs(this.field_205145_c.moveForward) > 0.0F;
      float f = this.field_205146_d == BoatGoals.GO_IN_BOAT_DIRECTION ? (flag ? 0.17999999F : 0.0F) : 0.135F;
      this.field_205144_b.moveRelative(this.field_205144_b.moveStrafing, this.field_205144_b.moveVertical, this.field_205144_b.moveForward, f);
      this.field_205144_b.move(MoverType.SELF, this.field_205144_b.motionX, this.field_205144_b.motionY, this.field_205144_b.motionZ);
      if (--this.field_205143_a <= 0) {
         this.field_205143_a = 10;
         if (this.field_205146_d == BoatGoals.GO_TO_BOAT) {
            BlockPos blockpos = (new BlockPos(this.field_205145_c)).offset(this.field_205145_c.getHorizontalFacing().getOpposite());
            blockpos = blockpos.add(0, -1, 0);
            this.field_205144_b.getNavigator().tryMoveToXYZ((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), 1.0D);
            if (this.field_205144_b.getDistance(this.field_205145_c) < 4.0F) {
               this.field_205143_a = 0;
               this.field_205146_d = BoatGoals.GO_IN_BOAT_DIRECTION;
            }
         } else if (this.field_205146_d == BoatGoals.GO_IN_BOAT_DIRECTION) {
            EnumFacing enumfacing = this.field_205145_c.getAdjustedHorizontalFacing();
            BlockPos blockpos1 = (new BlockPos(this.field_205145_c)).offset(enumfacing, 10);
            this.field_205144_b.getNavigator().tryMoveToXYZ((double)blockpos1.getX(), (double)(blockpos1.getY() - 1), (double)blockpos1.getZ(), 1.0D);
            if (this.field_205144_b.getDistance(this.field_205145_c) > 12.0F) {
               this.field_205143_a = 0;
               this.field_205146_d = BoatGoals.GO_TO_BOAT;
            }
         }

      }
   }
}
