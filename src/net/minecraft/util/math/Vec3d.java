package net.minecraft.util.math;

import java.util.EnumSet;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Vec3d {
   public static final Vec3d ZERO = new Vec3d(0.0D, 0.0D, 0.0D);
   public final double x;
   public final double y;
   public final double z;

   public Vec3d(double p_i47092_1_, double p_i47092_3_, double p_i47092_5_) {
      this.x = p_i47092_1_;
      this.y = p_i47092_3_;
      this.z = p_i47092_5_;
   }

   public Vec3d(Vec3i p_i47093_1_) {
      this((double)p_i47093_1_.getX(), (double)p_i47093_1_.getY(), (double)p_i47093_1_.getZ());
   }

   public Vec3d subtractReverse(Vec3d p_72444_1_) {
      return new Vec3d(p_72444_1_.x - this.x, p_72444_1_.y - this.y, p_72444_1_.z - this.z);
   }

   public Vec3d normalize() {
      double d0 = (double)MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
      return d0 < 1.0E-4D ? ZERO : new Vec3d(this.x / d0, this.y / d0, this.z / d0);
   }

   public double dotProduct(Vec3d p_72430_1_) {
      return this.x * p_72430_1_.x + this.y * p_72430_1_.y + this.z * p_72430_1_.z;
   }

   public Vec3d crossProduct(Vec3d p_72431_1_) {
      return new Vec3d(this.y * p_72431_1_.z - this.z * p_72431_1_.y, this.z * p_72431_1_.x - this.x * p_72431_1_.z, this.x * p_72431_1_.y - this.y * p_72431_1_.x);
   }

   public Vec3d subtract(Vec3d p_178788_1_) {
      return this.subtract(p_178788_1_.x, p_178788_1_.y, p_178788_1_.z);
   }

   public Vec3d subtract(double p_178786_1_, double p_178786_3_, double p_178786_5_) {
      return this.add(-p_178786_1_, -p_178786_3_, -p_178786_5_);
   }

   public Vec3d add(Vec3d p_178787_1_) {
      return this.add(p_178787_1_.x, p_178787_1_.y, p_178787_1_.z);
   }

   public Vec3d add(double p_72441_1_, double p_72441_3_, double p_72441_5_) {
      return new Vec3d(this.x + p_72441_1_, this.y + p_72441_3_, this.z + p_72441_5_);
   }

   public double distanceTo(Vec3d p_72438_1_) {
      double d0 = p_72438_1_.x - this.x;
      double d1 = p_72438_1_.y - this.y;
      double d2 = p_72438_1_.z - this.z;
      return (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
   }

   public double squareDistanceTo(Vec3d p_72436_1_) {
      double d0 = p_72436_1_.x - this.x;
      double d1 = p_72436_1_.y - this.y;
      double d2 = p_72436_1_.z - this.z;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   public double squareDistanceTo(double p_186679_1_, double p_186679_3_, double p_186679_5_) {
      double d0 = p_186679_1_ - this.x;
      double d1 = p_186679_3_ - this.y;
      double d2 = p_186679_5_ - this.z;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   public Vec3d scale(double p_186678_1_) {
      return new Vec3d(this.x * p_186678_1_, this.y * p_186678_1_, this.z * p_186678_1_);
   }

   public double length() {
      return (double)MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
   }

   public double lengthSquared() {
      return this.x * this.x + this.y * this.y + this.z * this.z;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof Vec3d)) {
         return false;
      } else {
         Vec3d vec3d = (Vec3d)p_equals_1_;
         if (Double.compare(vec3d.x, this.x) != 0) {
            return false;
         } else if (Double.compare(vec3d.y, this.y) != 0) {
            return false;
         } else {
            return Double.compare(vec3d.z, this.z) == 0;
         }
      }
   }

   public int hashCode() {
      long j = Double.doubleToLongBits(this.x);
      int i = (int)(j ^ j >>> 32);
      j = Double.doubleToLongBits(this.y);
      i = 31 * i + (int)(j ^ j >>> 32);
      j = Double.doubleToLongBits(this.z);
      i = 31 * i + (int)(j ^ j >>> 32);
      return i;
   }

   public String toString() {
      return "(" + this.x + ", " + this.y + ", " + this.z + ")";
   }

   public Vec3d rotatePitch(float p_178789_1_) {
      float f = MathHelper.cos(p_178789_1_);
      float f1 = MathHelper.sin(p_178789_1_);
      double d0 = this.x;
      double d1 = this.y * (double)f + this.z * (double)f1;
      double d2 = this.z * (double)f - this.y * (double)f1;
      return new Vec3d(d0, d1, d2);
   }

   public Vec3d rotateYaw(float p_178785_1_) {
      float f = MathHelper.cos(p_178785_1_);
      float f1 = MathHelper.sin(p_178785_1_);
      double d0 = this.x * (double)f + this.z * (double)f1;
      double d1 = this.y;
      double d2 = this.z * (double)f - this.x * (double)f1;
      return new Vec3d(d0, d1, d2);
   }

   @OnlyIn(Dist.CLIENT)
   public static Vec3d fromPitchYaw(Vec2f p_189984_0_) {
      return fromPitchYaw(p_189984_0_.x, p_189984_0_.y);
   }

   @OnlyIn(Dist.CLIENT)
   public static Vec3d fromPitchYaw(float p_189986_0_, float p_189986_1_) {
      float f = MathHelper.cos(-p_189986_1_ * ((float)Math.PI / 180F) - (float)Math.PI);
      float f1 = MathHelper.sin(-p_189986_1_ * ((float)Math.PI / 180F) - (float)Math.PI);
      float f2 = -MathHelper.cos(-p_189986_0_ * ((float)Math.PI / 180F));
      float f3 = MathHelper.sin(-p_189986_0_ * ((float)Math.PI / 180F));
      return new Vec3d((double)(f1 * f2), (double)f3, (double)(f * f2));
   }

   public Vec3d func_197746_a(EnumSet<EnumFacing.Axis> p_197746_1_) {
      double d0 = p_197746_1_.contains(EnumFacing.Axis.X) ? (double)MathHelper.floor(this.x) : this.x;
      double d1 = p_197746_1_.contains(EnumFacing.Axis.Y) ? (double)MathHelper.floor(this.y) : this.y;
      double d2 = p_197746_1_.contains(EnumFacing.Axis.Z) ? (double)MathHelper.floor(this.z) : this.z;
      return new Vec3d(d0, d1, d2);
   }
}
