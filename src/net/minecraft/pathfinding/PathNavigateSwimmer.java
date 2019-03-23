package net.minecraft.pathfinding;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityDolphin;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PathNavigateSwimmer extends PathNavigate {
   private boolean field_205155_i;

   public PathNavigateSwimmer(EntityLiving p_i45873_1_, World p_i45873_2_) {
      super(p_i45873_1_, p_i45873_2_);
   }

   protected PathFinder getPathFinder() {
      this.field_205155_i = this.entity instanceof EntityDolphin;
      this.nodeProcessor = new SwimNodeProcessor(this.field_205155_i);
      return new PathFinder(this.nodeProcessor);
   }

   protected boolean canNavigate() {
      return this.field_205155_i || this.isInLiquid();
   }

   protected Vec3d getEntityPosition() {
      return new Vec3d(this.entity.posX, this.entity.posY + (double)this.entity.height * 0.5D, this.entity.posZ);
   }

   public void tick() {
      ++this.totalTicks;
      if (this.tryUpdatePath) {
         this.updatePath();
      }

      if (!this.noPath()) {
         if (this.canNavigate()) {
            this.pathFollow();
         } else if (this.currentPath != null && this.currentPath.getCurrentPathIndex() < this.currentPath.getCurrentPathLength()) {
            Vec3d vec3d = this.currentPath.getVectorFromIndex(this.entity, this.currentPath.getCurrentPathIndex());
            if (MathHelper.floor(this.entity.posX) == MathHelper.floor(vec3d.x) && MathHelper.floor(this.entity.posY) == MathHelper.floor(vec3d.y) && MathHelper.floor(this.entity.posZ) == MathHelper.floor(vec3d.z)) {
               this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
            }
         }

         this.debugPathFinding();
         if (!this.noPath()) {
            Vec3d vec3d1 = this.currentPath.getPosition(this.entity);
            this.entity.getMoveHelper().setMoveTo(vec3d1.x, vec3d1.y, vec3d1.z, this.speed);
         }
      }
   }

   protected void pathFollow() {
      if (this.currentPath != null) {
         Vec3d vec3d = this.getEntityPosition();
         float f = this.entity.width > 0.75F ? this.entity.width / 2.0F : 0.75F - this.entity.width / 2.0F;
         if ((double)MathHelper.abs((float)this.entity.motionX) > 0.2D || (double)MathHelper.abs((float)this.entity.motionZ) > 0.2D) {
            f *= MathHelper.sqrt(this.entity.motionX * this.entity.motionX + this.entity.motionY * this.entity.motionY + this.entity.motionZ * this.entity.motionZ) * 6.0F;
         }

         int i = 6;
         Vec3d vec3d1 = this.currentPath.getCurrentPos();
         if (MathHelper.abs((float)(this.entity.posX - (vec3d1.x + 0.5D))) < f && MathHelper.abs((float)(this.entity.posZ - (vec3d1.z + 0.5D))) < f && Math.abs(this.entity.posY - vec3d1.y) < (double)(f * 2.0F)) {
            this.currentPath.incrementPathIndex();
         }

         for(int j = Math.min(this.currentPath.getCurrentPathIndex() + 6, this.currentPath.getCurrentPathLength() - 1); j > this.currentPath.getCurrentPathIndex(); --j) {
            vec3d1 = this.currentPath.getVectorFromIndex(this.entity, j);
            if (!(vec3d1.squareDistanceTo(vec3d) > 36.0D) && this.isDirectPathBetweenPoints(vec3d, vec3d1, 0, 0, 0)) {
               this.currentPath.setCurrentPathIndex(j);
               break;
            }
         }

         this.checkForStuck(vec3d);
      }
   }

   protected void checkForStuck(Vec3d p_179677_1_) {
      if (this.totalTicks - this.ticksAtLastPos > 100) {
         if (p_179677_1_.squareDistanceTo(this.lastPosCheck) < 2.25D) {
            this.clearPath();
         }

         this.ticksAtLastPos = this.totalTicks;
         this.lastPosCheck = p_179677_1_;
      }

      if (this.currentPath != null && !this.currentPath.isFinished()) {
         Vec3d vec3d = this.currentPath.getCurrentPos();
         if (vec3d.equals(this.timeoutCachedNode)) {
            this.timeoutTimer += Util.milliTime() - this.lastTimeoutCheck;
         } else {
            this.timeoutCachedNode = vec3d;
            double d0 = p_179677_1_.distanceTo(this.timeoutCachedNode);
            this.timeoutLimit = this.entity.getAIMoveSpeed() > 0.0F ? d0 / (double)this.entity.getAIMoveSpeed() * 100.0D : 0.0D;
         }

         if (this.timeoutLimit > 0.0D && (double)this.timeoutTimer > this.timeoutLimit * 2.0D) {
            this.timeoutCachedNode = Vec3d.ZERO;
            this.timeoutTimer = 0L;
            this.timeoutLimit = 0.0D;
            this.clearPath();
         }

         this.lastTimeoutCheck = Util.milliTime();
      }

   }

   protected boolean isDirectPathBetweenPoints(Vec3d p_75493_1_, Vec3d p_75493_2_, int p_75493_3_, int p_75493_4_, int p_75493_5_) {
      RayTraceResult raytraceresult = this.world.rayTraceBlocks(p_75493_1_, new Vec3d(p_75493_2_.x, p_75493_2_.y + (double)this.entity.height * 0.5D, p_75493_2_.z), RayTraceFluidMode.NEVER, true, false);
      return raytraceresult == null || raytraceresult.type == RayTraceResult.Type.MISS;
   }

   public boolean canEntityStandOnPos(BlockPos p_188555_1_) {
      return !this.world.getBlockState(p_188555_1_).isOpaqueCube(this.world, p_188555_1_);
   }

   public void setCanSwim(boolean p_212239_1_) {
   }
}
