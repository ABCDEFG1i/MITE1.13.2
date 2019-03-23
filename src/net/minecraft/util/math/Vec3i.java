package net.minecraft.util.math;

import com.google.common.base.MoreObjects;
import javax.annotation.concurrent.Immutable;

@Immutable
public class Vec3i implements Comparable<Vec3i> {
   public static final Vec3i NULL_VECTOR = new Vec3i(0, 0, 0);
   private final int x;
   private final int y;
   private final int z;

   public Vec3i(int p_i46007_1_, int p_i46007_2_, int p_i46007_3_) {
      this.x = p_i46007_1_;
      this.y = p_i46007_2_;
      this.z = p_i46007_3_;
   }

   public Vec3i(double p_i46008_1_, double p_i46008_3_, double p_i46008_5_) {
      this(MathHelper.floor(p_i46008_1_), MathHelper.floor(p_i46008_3_), MathHelper.floor(p_i46008_5_));
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof Vec3i)) {
         return false;
      } else {
         Vec3i vec3i = (Vec3i)p_equals_1_;
         if (this.getX() != vec3i.getX()) {
            return false;
         } else if (this.getY() != vec3i.getY()) {
            return false;
         } else {
            return this.getZ() == vec3i.getZ();
         }
      }
   }

   public int hashCode() {
      return (this.getY() + this.getZ() * 31) * 31 + this.getX();
   }

   public int compareTo(Vec3i p_compareTo_1_) {
      if (this.getY() == p_compareTo_1_.getY()) {
         return this.getZ() == p_compareTo_1_.getZ() ? this.getX() - p_compareTo_1_.getX() : this.getZ() - p_compareTo_1_.getZ();
      } else {
         return this.getY() - p_compareTo_1_.getY();
      }
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getZ() {
      return this.z;
   }

   public Vec3i crossProduct(Vec3i p_177955_1_) {
      return new Vec3i(this.getY() * p_177955_1_.getZ() - this.getZ() * p_177955_1_.getY(), this.getZ() * p_177955_1_.getX() - this.getX() * p_177955_1_.getZ(), this.getX() * p_177955_1_.getY() - this.getY() * p_177955_1_.getX());
   }

   public double getDistance(int p_185332_1_, int p_185332_2_, int p_185332_3_) {
      double d0 = (double)(this.getX() - p_185332_1_);
      double d1 = (double)(this.getY() - p_185332_2_);
      double d2 = (double)(this.getZ() - p_185332_3_);
      return Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
   }

   public double getDistance(Vec3i p_196233_1_) {
      return this.getDistance(p_196233_1_.getX(), p_196233_1_.getY(), p_196233_1_.getZ());
   }

   public double distanceSq(double p_177954_1_, double p_177954_3_, double p_177954_5_) {
      double d0 = (double)this.getX() - p_177954_1_;
      double d1 = (double)this.getY() - p_177954_3_;
      double d2 = (double)this.getZ() - p_177954_5_;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   public double distanceSqToCenter(double p_177957_1_, double p_177957_3_, double p_177957_5_) {
      double d0 = (double)this.getX() + 0.5D - p_177957_1_;
      double d1 = (double)this.getY() + 0.5D - p_177957_3_;
      double d2 = (double)this.getZ() + 0.5D - p_177957_5_;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   public double distanceSq(Vec3i p_177951_1_) {
      return this.distanceSq((double)p_177951_1_.getX(), (double)p_177951_1_.getY(), (double)p_177951_1_.getZ());
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
   }
}
