package net.minecraft.util.math;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;

public class RayTraceResult {
   private BlockPos blockPos;
   public RayTraceResult.Type type;
   public EnumFacing sideHit;
   public Vec3d hitVec;
   public Entity entity;

   public RayTraceResult(Vec3d p_i47094_1_, EnumFacing p_i47094_2_, BlockPos p_i47094_3_) {
      this(RayTraceResult.Type.BLOCK, p_i47094_1_, p_i47094_2_, p_i47094_3_);
   }

   public RayTraceResult(Entity p_i2304_1_) {
      this(p_i2304_1_, new Vec3d(p_i2304_1_.posX, p_i2304_1_.posY, p_i2304_1_.posZ));
   }

   public RayTraceResult(RayTraceResult.Type p_i47096_1_, Vec3d p_i47096_2_, EnumFacing p_i47096_3_, BlockPos p_i47096_4_) {
      this.type = p_i47096_1_;
      this.blockPos = p_i47096_4_;
      this.sideHit = p_i47096_3_;
      this.hitVec = new Vec3d(p_i47096_2_.x, p_i47096_2_.y, p_i47096_2_.z);
   }

   public RayTraceResult(Entity p_i47097_1_, Vec3d p_i47097_2_) {
      this.type = RayTraceResult.Type.ENTITY;
      this.entity = p_i47097_1_;
      this.hitVec = p_i47097_2_;
   }

   public BlockPos getBlockPos() {
      return this.blockPos;
   }

   public String toString() {
      return "HitResult{type=" + this.type + ", blockpos=" + this.blockPos + ", f=" + this.sideHit + ", pos=" + this.hitVec + ", entity=" + this.entity + '}';
   }

   public enum Type {
      MISS,
      BLOCK,
      ENTITY
   }
}
