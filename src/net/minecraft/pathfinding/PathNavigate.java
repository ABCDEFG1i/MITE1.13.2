package net.minecraft.pathfinding;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.Region;
import net.minecraft.world.World;

public abstract class PathNavigate {
   protected EntityLiving entity;
   protected World world;
   @Nullable
   protected Path currentPath;
   protected double speed;
   private final IAttributeInstance pathSearchRange;
   protected int totalTicks;
   protected int ticksAtLastPos;
   protected Vec3d lastPosCheck = Vec3d.ZERO;
   protected Vec3d timeoutCachedNode = Vec3d.ZERO;
   protected long timeoutTimer;
   protected long lastTimeoutCheck;
   protected double timeoutLimit;
   protected float maxDistanceToWaypoint = 0.5F;
   protected boolean tryUpdatePath;
   protected long lastTimeUpdated;
   protected NodeProcessor nodeProcessor;
   private BlockPos targetPos;
   private PathFinder pathFinder;

   public PathNavigate(EntityLiving p_i1671_1_, World p_i1671_2_) {
      this.entity = p_i1671_1_;
      this.world = p_i1671_2_;
      this.pathSearchRange = p_i1671_1_.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
      this.pathFinder = this.getPathFinder();
   }

   public BlockPos func_208485_j() {
      return this.targetPos;
   }

   protected abstract PathFinder getPathFinder();

   public void setSpeed(double p_75489_1_) {
      this.speed = p_75489_1_;
   }

   public float getPathSearchRange() {
      return (float)this.pathSearchRange.getAttributeValue();
   }

   public boolean canUpdatePathOnTimeout() {
      return this.tryUpdatePath;
   }

   public void updatePath() {
      if (this.world.getTotalWorldTime() - this.lastTimeUpdated > 20L) {
         if (this.targetPos != null) {
            this.currentPath = null;
            this.currentPath = this.getPathToPos(this.targetPos);
            this.lastTimeUpdated = this.world.getTotalWorldTime();
            this.tryUpdatePath = false;
         }
      } else {
         this.tryUpdatePath = true;
      }

   }

   @Nullable
   public final Path getPathToXYZ(double p_75488_1_, double p_75488_3_, double p_75488_5_) {
      return this.getPathToPos(new BlockPos(p_75488_1_, p_75488_3_, p_75488_5_));
   }

   @Nullable
   public Path getPathToPos(BlockPos p_179680_1_) {
      if (!this.canNavigate()) {
         return null;
      } else if (this.currentPath != null && !this.currentPath.isFinished() && p_179680_1_.equals(this.targetPos)) {
         return this.currentPath;
      } else {
         this.targetPos = p_179680_1_;
         float f = this.getPathSearchRange();
         this.world.profiler.startSection("pathfind");
         BlockPos blockpos = new BlockPos(this.entity);
         int i = (int)(f + 8.0F);
         IBlockReader iblockreader = new Region(this.world, blockpos.add(-i, -i, -i), blockpos.add(i, i, i), 0);
         Path path = this.pathFinder.findPath(iblockreader, this.entity, this.targetPos, f);
         this.world.profiler.endSection();
         return path;
      }
   }

   @Nullable
   public Path getPathToEntityLiving(Entity p_75494_1_) {
      if (!this.canNavigate()) {
         return null;
      } else {
         BlockPos blockpos = new BlockPos(p_75494_1_);
         if (this.currentPath != null && !this.currentPath.isFinished() && blockpos.equals(this.targetPos)) {
            return this.currentPath;
         } else {
            this.targetPos = blockpos;
            float f = this.getPathSearchRange();
            this.world.profiler.startSection("pathfind");
            BlockPos blockpos1 = (new BlockPos(this.entity)).up();
            int i = (int)(f + 16.0F);
            IBlockReader iblockreader = new Region(this.world, blockpos1.add(-i, -i, -i), blockpos1.add(i, i, i), 0);
            Path path = this.pathFinder.findPath(iblockreader, this.entity, p_75494_1_, f);
            this.world.profiler.endSection();
            return path;
         }
      }
   }

   public boolean tryMoveToXYZ(double p_75492_1_, double p_75492_3_, double p_75492_5_, double p_75492_7_) {
      return this.setPath(this.getPathToXYZ(p_75492_1_, p_75492_3_, p_75492_5_), p_75492_7_);
   }

   public boolean tryMoveToEntityLiving(Entity p_75497_1_, double p_75497_2_) {
      Path path = this.getPathToEntityLiving(p_75497_1_);
      return path != null && this.setPath(path, p_75497_2_);
   }

   public boolean setPath(@Nullable Path p_75484_1_, double p_75484_2_) {
      if (p_75484_1_ == null) {
         this.currentPath = null;
         return false;
      } else {
         if (!p_75484_1_.isSamePath(this.currentPath)) {
            this.currentPath = p_75484_1_;
         }

         this.trimPath();
         if (this.currentPath.getCurrentPathLength() <= 0) {
            return false;
         } else {
            this.speed = p_75484_2_;
            Vec3d vec3d = this.getEntityPosition();
            this.ticksAtLastPos = this.totalTicks;
            this.lastPosCheck = vec3d;
            return true;
         }
      }
   }

   @Nullable
   public Path getPath() {
      return this.currentPath;
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
            Vec3d vec3d = this.getEntityPosition();
            Vec3d vec3d1 = this.currentPath.getVectorFromIndex(this.entity, this.currentPath.getCurrentPathIndex());
            if (vec3d.y > vec3d1.y && !this.entity.onGround && MathHelper.floor(vec3d.x) == MathHelper.floor(vec3d1.x) && MathHelper.floor(vec3d.z) == MathHelper.floor(vec3d1.z)) {
               this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
            }
         }

         this.debugPathFinding();
         if (!this.noPath()) {
            Vec3d vec3d2 = this.currentPath.getPosition(this.entity);
            BlockPos blockpos = new BlockPos(vec3d2);
            this.entity.getMoveHelper().setMoveTo(vec3d2.x, this.world.getBlockState(blockpos.down()).isAir() ? vec3d2.y : WalkNodeProcessor.func_197682_a(this.world, blockpos), vec3d2.z, this.speed);
         }
      }
   }

   protected void debugPathFinding() {
   }

   protected void pathFollow() {
      Vec3d vec3d = this.getEntityPosition();
      int i = this.currentPath.getCurrentPathLength();

      for(int j = this.currentPath.getCurrentPathIndex(); j < this.currentPath.getCurrentPathLength(); ++j) {
         if ((double)this.currentPath.getPathPointFromIndex(j).y != Math.floor(vec3d.y)) {
            i = j;
            break;
         }
      }

      this.maxDistanceToWaypoint = this.entity.width > 0.75F ? this.entity.width / 2.0F : 0.75F - this.entity.width / 2.0F;
      Vec3d vec3d1 = this.currentPath.getCurrentPos();
      if (MathHelper.abs((float)(this.entity.posX - (vec3d1.x + 0.5D))) < this.maxDistanceToWaypoint && MathHelper.abs((float)(this.entity.posZ - (vec3d1.z + 0.5D))) < this.maxDistanceToWaypoint && Math.abs(this.entity.posY - vec3d1.y) < 1.0D) {
         this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
      }

      int k = MathHelper.ceil(this.entity.width);
      int l = MathHelper.ceil(this.entity.height);
      int i1 = k;

      for(int j1 = i - 1; j1 >= this.currentPath.getCurrentPathIndex(); --j1) {
         if (this.isDirectPathBetweenPoints(vec3d, this.currentPath.getVectorFromIndex(this.entity, j1), k, l, i1)) {
            this.currentPath.setCurrentPathIndex(j1);
            break;
         }
      }

      this.checkForStuck(vec3d);
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
            this.timeoutLimit = this.entity.getAIMoveSpeed() > 0.0F ? d0 / (double)this.entity.getAIMoveSpeed() * 1000.0D : 0.0D;
         }

         if (this.timeoutLimit > 0.0D && (double)this.timeoutTimer > this.timeoutLimit * 3.0D) {
            this.timeoutCachedNode = Vec3d.ZERO;
            this.timeoutTimer = 0L;
            this.timeoutLimit = 0.0D;
            this.clearPath();
         }

         this.lastTimeoutCheck = Util.milliTime();
      }

   }

   public boolean noPath() {
      return this.currentPath == null || this.currentPath.isFinished();
   }

   public void clearPath() {
      this.currentPath = null;
   }

   protected abstract Vec3d getEntityPosition();

   protected abstract boolean canNavigate();

   protected boolean isInLiquid() {
      return this.entity.isInWaterOrBubbleColumn() || this.entity.isInLava();
   }

   protected void trimPath() {
      if (this.currentPath != null) {
         for(int i = 0; i < this.currentPath.getCurrentPathLength(); ++i) {
            PathPoint pathpoint = this.currentPath.getPathPointFromIndex(i);
            PathPoint pathpoint1 = i + 1 < this.currentPath.getCurrentPathLength() ? this.currentPath.getPathPointFromIndex(i + 1) : null;
            IBlockState iblockstate = this.world.getBlockState(new BlockPos(pathpoint.x, pathpoint.y, pathpoint.z));
            Block block = iblockstate.getBlock();
            if (block == Blocks.CAULDRON) {
               this.currentPath.setPoint(i, pathpoint.cloneMove(pathpoint.x, pathpoint.y + 1, pathpoint.z));
               if (pathpoint1 != null && pathpoint.y >= pathpoint1.y) {
                  this.currentPath.setPoint(i + 1, pathpoint1.cloneMove(pathpoint1.x, pathpoint.y + 1, pathpoint1.z));
               }
            }
         }

      }
   }

   protected abstract boolean isDirectPathBetweenPoints(Vec3d p_75493_1_, Vec3d p_75493_2_, int p_75493_3_, int p_75493_4_, int p_75493_5_);

   public boolean canEntityStandOnPos(BlockPos p_188555_1_) {
      BlockPos blockpos = p_188555_1_.down();
      return this.world.getBlockState(blockpos).isOpaqueCube(this.world, blockpos);
   }

   public NodeProcessor getNodeProcessor() {
      return this.nodeProcessor;
   }

   public void setCanSwim(boolean p_212239_1_) {
      this.nodeProcessor.setCanSwim(p_212239_1_);
   }

   public boolean getCanSwim() {
      return this.nodeProcessor.getCanSwim();
   }
}
