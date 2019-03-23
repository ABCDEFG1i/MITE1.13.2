package net.minecraft.util.math;

import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AxisAlignedBB {
   public final double minX;
   public final double minY;
   public final double minZ;
   public final double maxX;
   public final double maxY;
   public final double maxZ;

   public AxisAlignedBB(double p_i2300_1_, double p_i2300_3_, double p_i2300_5_, double p_i2300_7_, double p_i2300_9_, double p_i2300_11_) {
      this.minX = Math.min(p_i2300_1_, p_i2300_7_);
      this.minY = Math.min(p_i2300_3_, p_i2300_9_);
      this.minZ = Math.min(p_i2300_5_, p_i2300_11_);
      this.maxX = Math.max(p_i2300_1_, p_i2300_7_);
      this.maxY = Math.max(p_i2300_3_, p_i2300_9_);
      this.maxZ = Math.max(p_i2300_5_, p_i2300_11_);
   }

   public AxisAlignedBB(BlockPos p_i46612_1_) {
      this((double)p_i46612_1_.getX(), (double)p_i46612_1_.getY(), (double)p_i46612_1_.getZ(), (double)(p_i46612_1_.getX() + 1), (double)(p_i46612_1_.getY() + 1), (double)(p_i46612_1_.getZ() + 1));
   }

   public AxisAlignedBB(BlockPos p_i45554_1_, BlockPos p_i45554_2_) {
      this((double)p_i45554_1_.getX(), (double)p_i45554_1_.getY(), (double)p_i45554_1_.getZ(), (double)p_i45554_2_.getX(), (double)p_i45554_2_.getY(), (double)p_i45554_2_.getZ());
   }

   @OnlyIn(Dist.CLIENT)
   public AxisAlignedBB(Vec3d p_i47144_1_, Vec3d p_i47144_2_) {
      this(p_i47144_1_.x, p_i47144_1_.y, p_i47144_1_.z, p_i47144_2_.x, p_i47144_2_.y, p_i47144_2_.z);
   }

   public double getMin(EnumFacing.Axis p_197745_1_) {
      return p_197745_1_.getCoordinate(this.minX, this.minY, this.minZ);
   }

   public double getMax(EnumFacing.Axis p_197742_1_) {
      return p_197742_1_.getCoordinate(this.maxX, this.maxY, this.maxZ);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof AxisAlignedBB)) {
         return false;
      } else {
         AxisAlignedBB axisalignedbb = (AxisAlignedBB)p_equals_1_;
         if (Double.compare(axisalignedbb.minX, this.minX) != 0) {
            return false;
         } else if (Double.compare(axisalignedbb.minY, this.minY) != 0) {
            return false;
         } else if (Double.compare(axisalignedbb.minZ, this.minZ) != 0) {
            return false;
         } else if (Double.compare(axisalignedbb.maxX, this.maxX) != 0) {
            return false;
         } else if (Double.compare(axisalignedbb.maxY, this.maxY) != 0) {
            return false;
         } else {
            return Double.compare(axisalignedbb.maxZ, this.maxZ) == 0;
         }
      }
   }

   public int hashCode() {
      long i = Double.doubleToLongBits(this.minX);
      int j = (int)(i ^ i >>> 32);
      i = Double.doubleToLongBits(this.minY);
      j = 31 * j + (int)(i ^ i >>> 32);
      i = Double.doubleToLongBits(this.minZ);
      j = 31 * j + (int)(i ^ i >>> 32);
      i = Double.doubleToLongBits(this.maxX);
      j = 31 * j + (int)(i ^ i >>> 32);
      i = Double.doubleToLongBits(this.maxY);
      j = 31 * j + (int)(i ^ i >>> 32);
      i = Double.doubleToLongBits(this.maxZ);
      j = 31 * j + (int)(i ^ i >>> 32);
      return j;
   }

   public AxisAlignedBB contract(double p_191195_1_, double p_191195_3_, double p_191195_5_) {
      double d0 = this.minX;
      double d1 = this.minY;
      double d2 = this.minZ;
      double d3 = this.maxX;
      double d4 = this.maxY;
      double d5 = this.maxZ;
      if (p_191195_1_ < 0.0D) {
         d0 -= p_191195_1_;
      } else if (p_191195_1_ > 0.0D) {
         d3 -= p_191195_1_;
      }

      if (p_191195_3_ < 0.0D) {
         d1 -= p_191195_3_;
      } else if (p_191195_3_ > 0.0D) {
         d4 -= p_191195_3_;
      }

      if (p_191195_5_ < 0.0D) {
         d2 -= p_191195_5_;
      } else if (p_191195_5_ > 0.0D) {
         d5 -= p_191195_5_;
      }

      return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
   }

   public AxisAlignedBB expand(double p_72321_1_, double p_72321_3_, double p_72321_5_) {
      double d0 = this.minX;
      double d1 = this.minY;
      double d2 = this.minZ;
      double d3 = this.maxX;
      double d4 = this.maxY;
      double d5 = this.maxZ;
      if (p_72321_1_ < 0.0D) {
         d0 += p_72321_1_;
      } else if (p_72321_1_ > 0.0D) {
         d3 += p_72321_1_;
      }

      if (p_72321_3_ < 0.0D) {
         d1 += p_72321_3_;
      } else if (p_72321_3_ > 0.0D) {
         d4 += p_72321_3_;
      }

      if (p_72321_5_ < 0.0D) {
         d2 += p_72321_5_;
      } else if (p_72321_5_ > 0.0D) {
         d5 += p_72321_5_;
      }

      return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
   }

   public AxisAlignedBB grow(double p_72314_1_, double p_72314_3_, double p_72314_5_) {
      double d0 = this.minX - p_72314_1_;
      double d1 = this.minY - p_72314_3_;
      double d2 = this.minZ - p_72314_5_;
      double d3 = this.maxX + p_72314_1_;
      double d4 = this.maxY + p_72314_3_;
      double d5 = this.maxZ + p_72314_5_;
      return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
   }

   public AxisAlignedBB grow(double p_186662_1_) {
      return this.grow(p_186662_1_, p_186662_1_, p_186662_1_);
   }

   public AxisAlignedBB intersect(AxisAlignedBB p_191500_1_) {
      double d0 = Math.max(this.minX, p_191500_1_.minX);
      double d1 = Math.max(this.minY, p_191500_1_.minY);
      double d2 = Math.max(this.minZ, p_191500_1_.minZ);
      double d3 = Math.min(this.maxX, p_191500_1_.maxX);
      double d4 = Math.min(this.maxY, p_191500_1_.maxY);
      double d5 = Math.min(this.maxZ, p_191500_1_.maxZ);
      return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
   }

   public AxisAlignedBB union(AxisAlignedBB p_111270_1_) {
      double d0 = Math.min(this.minX, p_111270_1_.minX);
      double d1 = Math.min(this.minY, p_111270_1_.minY);
      double d2 = Math.min(this.minZ, p_111270_1_.minZ);
      double d3 = Math.max(this.maxX, p_111270_1_.maxX);
      double d4 = Math.max(this.maxY, p_111270_1_.maxY);
      double d5 = Math.max(this.maxZ, p_111270_1_.maxZ);
      return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
   }

   public AxisAlignedBB offset(double p_72317_1_, double p_72317_3_, double p_72317_5_) {
      return new AxisAlignedBB(this.minX + p_72317_1_, this.minY + p_72317_3_, this.minZ + p_72317_5_, this.maxX + p_72317_1_, this.maxY + p_72317_3_, this.maxZ + p_72317_5_);
   }

   public AxisAlignedBB offset(BlockPos p_186670_1_) {
      return new AxisAlignedBB(this.minX + (double)p_186670_1_.getX(), this.minY + (double)p_186670_1_.getY(), this.minZ + (double)p_186670_1_.getZ(), this.maxX + (double)p_186670_1_.getX(), this.maxY + (double)p_186670_1_.getY(), this.maxZ + (double)p_186670_1_.getZ());
   }

   public AxisAlignedBB offset(Vec3d p_191194_1_) {
      return this.offset(p_191194_1_.x, p_191194_1_.y, p_191194_1_.z);
   }

   public boolean intersects(AxisAlignedBB p_72326_1_) {
      return this.intersects(p_72326_1_.minX, p_72326_1_.minY, p_72326_1_.minZ, p_72326_1_.maxX, p_72326_1_.maxY, p_72326_1_.maxZ);
   }

   public boolean intersects(double p_186668_1_, double p_186668_3_, double p_186668_5_, double p_186668_7_, double p_186668_9_, double p_186668_11_) {
      return this.minX < p_186668_7_ && this.maxX > p_186668_1_ && this.minY < p_186668_9_ && this.maxY > p_186668_3_ && this.minZ < p_186668_11_ && this.maxZ > p_186668_5_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean intersects(Vec3d p_189973_1_, Vec3d p_189973_2_) {
      return this.intersects(Math.min(p_189973_1_.x, p_189973_2_.x), Math.min(p_189973_1_.y, p_189973_2_.y), Math.min(p_189973_1_.z, p_189973_2_.z), Math.max(p_189973_1_.x, p_189973_2_.x), Math.max(p_189973_1_.y, p_189973_2_.y), Math.max(p_189973_1_.z, p_189973_2_.z));
   }

   public boolean contains(Vec3d p_72318_1_) {
      return this.contains(p_72318_1_.x, p_72318_1_.y, p_72318_1_.z);
   }

   public boolean contains(double p_197744_1_, double p_197744_3_, double p_197744_5_) {
      return p_197744_1_ >= this.minX && p_197744_1_ < this.maxX && p_197744_3_ >= this.minY && p_197744_3_ < this.maxY && p_197744_5_ >= this.minZ && p_197744_5_ < this.maxZ;
   }

   public double getAverageEdgeLength() {
      double d0 = this.maxX - this.minX;
      double d1 = this.maxY - this.minY;
      double d2 = this.maxZ - this.minZ;
      return (d0 + d1 + d2) / 3.0D;
   }

   public AxisAlignedBB shrink(double p_211539_1_, double p_211539_3_, double p_211539_5_) {
      return this.grow(-p_211539_1_, -p_211539_3_, -p_211539_5_);
   }

   public AxisAlignedBB shrink(double p_186664_1_) {
      return this.grow(-p_186664_1_);
   }

   @Nullable
   public RayTraceResult calculateIntercept(Vec3d p_72327_1_, Vec3d p_72327_2_) {
      return this.calculateIntercept(p_72327_1_, p_72327_2_, (BlockPos)null);
   }

   @Nullable
   public RayTraceResult calculateIntercept(Vec3d p_197739_1_, Vec3d p_197739_2_, @Nullable BlockPos p_197739_3_) {
      double[] adouble = new double[]{1.0D};
      EnumFacing enumfacing = null;
      double d0 = p_197739_2_.x - p_197739_1_.x;
      double d1 = p_197739_2_.y - p_197739_1_.y;
      double d2 = p_197739_2_.z - p_197739_1_.z;
      enumfacing = func_197741_a(p_197739_3_ == null ? this : this.offset(p_197739_3_), p_197739_1_, adouble, enumfacing, d0, d1, d2);
      if (enumfacing == null) {
         return null;
      } else {
         double d3 = adouble[0];
         return new RayTraceResult(p_197739_1_.add(d3 * d0, d3 * d1, d3 * d2), enumfacing, p_197739_3_ == null ? BlockPos.ORIGIN : p_197739_3_);
      }
   }

   @Nullable
   public static RayTraceResult rayTrace(Iterable<AxisAlignedBB> p_197743_0_, Vec3d p_197743_1_, Vec3d p_197743_2_, BlockPos p_197743_3_) {
      double[] adouble = new double[]{1.0D};
      EnumFacing enumfacing = null;
      double d0 = p_197743_2_.x - p_197743_1_.x;
      double d1 = p_197743_2_.y - p_197743_1_.y;
      double d2 = p_197743_2_.z - p_197743_1_.z;

      for(AxisAlignedBB axisalignedbb : p_197743_0_) {
         enumfacing = func_197741_a(axisalignedbb.offset(p_197743_3_), p_197743_1_, adouble, enumfacing, d0, d1, d2);
      }

      if (enumfacing == null) {
         return null;
      } else {
         double d3 = adouble[0];
         return new RayTraceResult(p_197743_1_.add(d3 * d0, d3 * d1, d3 * d2), enumfacing, p_197743_3_);
      }
   }

   @Nullable
   private static EnumFacing func_197741_a(AxisAlignedBB p_197741_0_, Vec3d p_197741_1_, double[] p_197741_2_, @Nullable EnumFacing p_197741_3_, double p_197741_4_, double p_197741_6_, double p_197741_8_) {
      if (p_197741_4_ > 1.0E-7D) {
         p_197741_3_ = func_197740_a(p_197741_2_, p_197741_3_, p_197741_4_, p_197741_6_, p_197741_8_, p_197741_0_.minX, p_197741_0_.minY, p_197741_0_.maxY, p_197741_0_.minZ, p_197741_0_.maxZ, EnumFacing.WEST, p_197741_1_.x, p_197741_1_.y, p_197741_1_.z);
      } else if (p_197741_4_ < -1.0E-7D) {
         p_197741_3_ = func_197740_a(p_197741_2_, p_197741_3_, p_197741_4_, p_197741_6_, p_197741_8_, p_197741_0_.maxX, p_197741_0_.minY, p_197741_0_.maxY, p_197741_0_.minZ, p_197741_0_.maxZ, EnumFacing.EAST, p_197741_1_.x, p_197741_1_.y, p_197741_1_.z);
      }

      if (p_197741_6_ > 1.0E-7D) {
         p_197741_3_ = func_197740_a(p_197741_2_, p_197741_3_, p_197741_6_, p_197741_8_, p_197741_4_, p_197741_0_.minY, p_197741_0_.minZ, p_197741_0_.maxZ, p_197741_0_.minX, p_197741_0_.maxX, EnumFacing.DOWN, p_197741_1_.y, p_197741_1_.z, p_197741_1_.x);
      } else if (p_197741_6_ < -1.0E-7D) {
         p_197741_3_ = func_197740_a(p_197741_2_, p_197741_3_, p_197741_6_, p_197741_8_, p_197741_4_, p_197741_0_.maxY, p_197741_0_.minZ, p_197741_0_.maxZ, p_197741_0_.minX, p_197741_0_.maxX, EnumFacing.UP, p_197741_1_.y, p_197741_1_.z, p_197741_1_.x);
      }

      if (p_197741_8_ > 1.0E-7D) {
         p_197741_3_ = func_197740_a(p_197741_2_, p_197741_3_, p_197741_8_, p_197741_4_, p_197741_6_, p_197741_0_.minZ, p_197741_0_.minX, p_197741_0_.maxX, p_197741_0_.minY, p_197741_0_.maxY, EnumFacing.NORTH, p_197741_1_.z, p_197741_1_.x, p_197741_1_.y);
      } else if (p_197741_8_ < -1.0E-7D) {
         p_197741_3_ = func_197740_a(p_197741_2_, p_197741_3_, p_197741_8_, p_197741_4_, p_197741_6_, p_197741_0_.maxZ, p_197741_0_.minX, p_197741_0_.maxX, p_197741_0_.minY, p_197741_0_.maxY, EnumFacing.SOUTH, p_197741_1_.z, p_197741_1_.x, p_197741_1_.y);
      }

      return p_197741_3_;
   }

   @Nullable
   private static EnumFacing func_197740_a(double[] p_197740_0_, @Nullable EnumFacing p_197740_1_, double p_197740_2_, double p_197740_4_, double p_197740_6_, double p_197740_8_, double p_197740_10_, double p_197740_12_, double p_197740_14_, double p_197740_16_, EnumFacing p_197740_18_, double p_197740_19_, double p_197740_21_, double p_197740_23_) {
      double d0 = (p_197740_8_ - p_197740_19_) / p_197740_2_;
      double d1 = p_197740_21_ + d0 * p_197740_4_;
      double d2 = p_197740_23_ + d0 * p_197740_6_;
      if (0.0D < d0 && d0 < p_197740_0_[0] && p_197740_10_ - 1.0E-7D < d1 && d1 < p_197740_12_ + 1.0E-7D && p_197740_14_ - 1.0E-7D < d2 && d2 < p_197740_16_ + 1.0E-7D) {
         p_197740_0_[0] = d0;
         return p_197740_18_;
      } else {
         return p_197740_1_;
      }
   }

   public String toString() {
      return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasNaN() {
      return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.minZ) || Double.isNaN(this.maxX) || Double.isNaN(this.maxY) || Double.isNaN(this.maxZ);
   }

   @OnlyIn(Dist.CLIENT)
   public Vec3d getCenter() {
      return new Vec3d(this.minX + (this.maxX - this.minX) * 0.5D, this.minY + (this.maxY - this.minY) * 0.5D, this.minZ + (this.maxZ - this.minZ) * 0.5D);
   }
}
