package net.minecraft.util.math.shapes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.math.DoubleMath;
import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.util.AxisRotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class VoxelShapes {
   private static final VoxelShape field_197886_a = new VoxelShapeArray(new VoxelShapePartBitSet(0, 0, 0),
           new DoubleArrayList(new double[]{0.0D}),
           new DoubleArrayList(new double[]{0.0D}),
           new DoubleArrayList(new double[]{0.0D}));
   private static final VoxelShape field_197887_b = Util.make(() -> {
      VoxelShapePart voxelshapepart = new VoxelShapePartBitSet(1, 1, 1);
      voxelshapepart.func_199625_a(0, 0, 0, true, true);
      return new VoxelShapeCube(voxelshapepart);
   });

   public static VoxelShape func_197880_a() {
      return field_197886_a;
   }

   public static VoxelShape func_197868_b() {
      return field_197887_b;
   }

   public static VoxelShape func_197873_a(double p_197873_0_, double p_197873_2_, double p_197873_4_, double p_197873_6_, double p_197873_8_, double p_197873_10_) {
      return func_197881_a(new AxisAlignedBB(p_197873_0_, p_197873_2_, p_197873_4_, p_197873_6_, p_197873_8_, p_197873_10_));
   }

   public static VoxelShape func_197881_a(AxisAlignedBB p_197881_0_) {
      int i = func_197885_a(p_197881_0_.minX, p_197881_0_.maxX);
      int j = func_197885_a(p_197881_0_.minY, p_197881_0_.maxY);
      int k = func_197885_a(p_197881_0_.minZ, p_197881_0_.maxZ);
      if (i >= 0 && j >= 0 && k >= 0) {
         if (i == 0 && j == 0 && k == 0) {
            return p_197881_0_.contains(0.5D, 0.5D, 0.5D) ? func_197868_b() : func_197880_a();
         } else {
            int l = 1 << i;
            int i1 = 1 << j;
            int j1 = 1 << k;
            int k1 = (int)Math.round(p_197881_0_.minX * (double)l);
            int l1 = (int)Math.round(p_197881_0_.maxX * (double)l);
            int i2 = (int)Math.round(p_197881_0_.minY * (double)i1);
            int j2 = (int)Math.round(p_197881_0_.maxY * (double)i1);
            int k2 = (int)Math.round(p_197881_0_.minZ * (double)j1);
            int l2 = (int)Math.round(p_197881_0_.maxZ * (double)j1);
            VoxelShapePartBitSet voxelshapepartbitset = new VoxelShapePartBitSet(l, i1, j1, k1, i2, k2, l1, j2, l2);

            for(long i3 = (long)k1; i3 < (long)l1; ++i3) {
               for(long j3 = (long)i2; j3 < (long)j2; ++j3) {
                  for(long k3 = (long)k2; k3 < (long)l2; ++k3) {
                     voxelshapepartbitset.func_199625_a((int)i3, (int)j3, (int)k3, false, true);
                  }
               }
            }

            return new VoxelShapeCube(voxelshapepartbitset);
         }
      } else {
         return new VoxelShapeArray(field_197887_b.field_197768_g, new double[]{p_197881_0_.minX, p_197881_0_.maxX}, new double[]{p_197881_0_.minY, p_197881_0_.maxY}, new double[]{p_197881_0_.minZ, p_197881_0_.maxZ});
      }
   }

   private static int func_197885_a(double p_197885_0_, double p_197885_2_) {
      if (!(p_197885_0_ < -1.0E-7D) && !(p_197885_2_ > 1.0000001D)) {
         for(int i = 0; i <= 3; ++i) {
            double d0 = p_197885_0_ * (double)(1 << i);
            double d1 = p_197885_2_ * (double)(1 << i);
            boolean flag = Math.abs(d0 - Math.floor(d0)) < 1.0E-7D;
            boolean flag1 = Math.abs(d1 - Math.floor(d1)) < 1.0E-7D;
            if (flag && flag1) {
               return i;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   protected static long func_197877_a(int p_197877_0_, int p_197877_1_) {
      return (long)p_197877_0_ * (long)(p_197877_1_ / IntMath.gcd(p_197877_0_, p_197877_1_));
   }

   public static VoxelShape func_197872_a(VoxelShape p_197872_0_, VoxelShape p_197872_1_) {
      return func_197878_a(p_197872_0_, p_197872_1_, IBooleanFunction.OR);
   }

   public static VoxelShape func_197878_a(VoxelShape p_197878_0_, VoxelShape p_197878_1_, IBooleanFunction p_197878_2_) {
      return func_197882_b(p_197878_0_, p_197878_1_, p_197878_2_).simplify();
   }

   public static VoxelShape func_197882_b(VoxelShape p_197882_0_, VoxelShape p_197882_1_, IBooleanFunction p_197882_2_) {
      if (p_197882_2_.apply(false, false)) {
         throw new IllegalArgumentException();
      } else if (p_197882_0_ == p_197882_1_) {
         return p_197882_2_.apply(true, true) ? p_197882_0_ : func_197880_a();
      } else {
         boolean flag = p_197882_2_.apply(true, false);
         boolean flag1 = p_197882_2_.apply(false, true);
         if (p_197882_0_.isEmpty()) {
            return flag1 ? p_197882_1_ : func_197880_a();
         } else if (p_197882_1_.isEmpty()) {
            return flag ? p_197882_0_ : func_197880_a();
         } else {
            IDoubleListMerger idoublelistmerger = func_199410_a(1, p_197882_0_.getValues(EnumFacing.Axis.X), p_197882_1_.getValues(EnumFacing.Axis.X), flag, flag1);
            IDoubleListMerger idoublelistmerger1 = func_199410_a(idoublelistmerger.func_212435_a().size() - 1, p_197882_0_.getValues(EnumFacing.Axis.Y), p_197882_1_.getValues(EnumFacing.Axis.Y), flag, flag1);
            IDoubleListMerger idoublelistmerger2 = func_199410_a((idoublelistmerger.func_212435_a().size() - 1) * (idoublelistmerger1.func_212435_a().size() - 1), p_197882_0_.getValues(EnumFacing.Axis.Z), p_197882_1_.getValues(EnumFacing.Axis.Z), flag, flag1);
            VoxelShapePartBitSet voxelshapepartbitset = VoxelShapePartBitSet.func_197852_a(p_197882_0_.field_197768_g, p_197882_1_.field_197768_g, idoublelistmerger, idoublelistmerger1, idoublelistmerger2, p_197882_2_);
            return idoublelistmerger instanceof DoubleCubeMergingList && idoublelistmerger1 instanceof DoubleCubeMergingList && idoublelistmerger2 instanceof DoubleCubeMergingList ? new VoxelShapeCube(voxelshapepartbitset) : new VoxelShapeArray(voxelshapepartbitset, idoublelistmerger.func_212435_a(), idoublelistmerger1.func_212435_a(), idoublelistmerger2.func_212435_a());
         }
      }
   }

   public static boolean func_197879_c(VoxelShape p_197879_0_, VoxelShape p_197879_1_, IBooleanFunction p_197879_2_) {
      if (p_197879_2_.apply(false, false)) {
         throw new IllegalArgumentException();
      } else if (p_197879_0_ == p_197879_1_) {
         return p_197879_2_.apply(true, true);
      } else if (p_197879_0_.isEmpty()) {
         return p_197879_2_.apply(false, !p_197879_1_.isEmpty());
      } else if (p_197879_1_.isEmpty()) {
         return p_197879_2_.apply(!p_197879_0_.isEmpty(), false);
      } else {
         boolean flag = p_197879_2_.apply(true, false);
         boolean flag1 = p_197879_2_.apply(false, true);

         for(EnumFacing.Axis enumfacing$axis : AxisRotation.AXES) {
            if (p_197879_0_.getEnd(enumfacing$axis) < p_197879_1_.getStart(enumfacing$axis) - 1.0E-7D) {
               return flag || flag1;
            }

            if (p_197879_1_.getEnd(enumfacing$axis) < p_197879_0_.getStart(enumfacing$axis) - 1.0E-7D) {
               return flag || flag1;
            }
         }

         IDoubleListMerger idoublelistmerger = func_199410_a(1, p_197879_0_.getValues(EnumFacing.Axis.X), p_197879_1_.getValues(EnumFacing.Axis.X), flag, flag1);
         IDoubleListMerger idoublelistmerger1 = func_199410_a(idoublelistmerger.func_212435_a().size() - 1, p_197879_0_.getValues(EnumFacing.Axis.Y), p_197879_1_.getValues(EnumFacing.Axis.Y), flag, flag1);
         IDoubleListMerger idoublelistmerger2 = func_199410_a((idoublelistmerger.func_212435_a().size() - 1) * (idoublelistmerger1.func_212435_a().size() - 1), p_197879_0_.getValues(EnumFacing.Axis.Z), p_197879_1_.getValues(EnumFacing.Axis.Z), flag, flag1);
         return func_197874_a(idoublelistmerger, idoublelistmerger1, idoublelistmerger2, p_197879_0_.field_197768_g, p_197879_1_.field_197768_g, p_197879_2_);
      }
   }

   private static boolean func_197874_a(IDoubleListMerger p_197874_0_, IDoubleListMerger p_197874_1_, IDoubleListMerger p_197874_2_, VoxelShapePart p_197874_3_, VoxelShapePart p_197874_4_, IBooleanFunction p_197874_5_) {
      return !p_197874_0_.func_197855_a((p_199861_5_, p_199861_6_, p_199861_7_) -> {
         return p_197874_1_.func_197855_a((p_199860_6_, p_199860_7_, p_199860_8_) -> {
            return p_197874_2_.func_197855_a((p_199862_7_, p_199862_8_, p_199862_9_) -> {
               return !p_197874_5_.apply(p_197874_3_.contains(p_199861_5_, p_199860_6_, p_199862_7_), p_197874_4_.contains(p_199861_6_, p_199860_7_, p_199862_8_));
            });
         });
      });
   }

   public static double func_212437_a(EnumFacing.Axis p_212437_0_, AxisAlignedBB p_212437_1_, Stream<VoxelShape> p_212437_2_, double p_212437_3_) {
      for(Iterator<VoxelShape> iterator = p_212437_2_.iterator(); iterator.hasNext(); p_212437_3_ = iterator.next().func_212430_a(p_212437_0_, p_212437_1_, p_212437_3_)) {
         if (Math.abs(p_212437_3_) < 1.0E-7D) {
            return 0.0D;
         }
      }

      return p_212437_3_;
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean func_197875_a(VoxelShape p_197875_0_, VoxelShape p_197875_1_, EnumFacing p_197875_2_) {
      if (p_197875_0_ == func_197868_b() && p_197875_1_ == func_197868_b()) {
         return true;
      } else if (p_197875_1_.isEmpty()) {
         return false;
      } else {
         EnumFacing.Axis enumfacing$axis = p_197875_2_.getAxis();
         EnumFacing.AxisDirection enumfacing$axisdirection = p_197875_2_.getAxisDirection();
         VoxelShape voxelshape = enumfacing$axisdirection == EnumFacing.AxisDirection.POSITIVE ? p_197875_0_ : p_197875_1_;
         VoxelShape voxelshape1 = enumfacing$axisdirection == EnumFacing.AxisDirection.POSITIVE ? p_197875_1_ : p_197875_0_;
         IBooleanFunction ibooleanfunction = enumfacing$axisdirection == EnumFacing.AxisDirection.POSITIVE ? IBooleanFunction.ONLY_FIRST : IBooleanFunction.ONLY_SECOND;
         return DoubleMath.fuzzyEquals(voxelshape.getEnd(enumfacing$axis), 1.0D, 1.0E-7D) && DoubleMath.fuzzyEquals(voxelshape1.getStart(enumfacing$axis), 0.0D, 1.0E-7D) && !func_197879_c(new VoxelShapeSplit(voxelshape, enumfacing$axis, voxelshape.field_197768_g.getSize(enumfacing$axis) - 1), new VoxelShapeSplit(voxelshape1, enumfacing$axis, 0), ibooleanfunction);
      }
   }

   public static boolean func_204642_b(VoxelShape p_204642_0_, VoxelShape p_204642_1_, EnumFacing p_204642_2_) {
      if (p_204642_0_ != func_197868_b() && p_204642_1_ != func_197868_b()) {
         EnumFacing.Axis enumfacing$axis = p_204642_2_.getAxis();
         EnumFacing.AxisDirection enumfacing$axisdirection = p_204642_2_.getAxisDirection();
         VoxelShape voxelshape = enumfacing$axisdirection == EnumFacing.AxisDirection.POSITIVE ? p_204642_0_ : p_204642_1_;
         VoxelShape voxelshape1 = enumfacing$axisdirection == EnumFacing.AxisDirection.POSITIVE ? p_204642_1_ : p_204642_0_;
         if (!DoubleMath.fuzzyEquals(voxelshape.getEnd(enumfacing$axis), 1.0D, 1.0E-7D)) {
            voxelshape = func_197880_a();
         }

         if (!DoubleMath.fuzzyEquals(voxelshape1.getStart(enumfacing$axis), 0.0D, 1.0E-7D)) {
            voxelshape1 = func_197880_a();
         }

         return !func_197879_c(func_197868_b(), func_197882_b(new VoxelShapeSplit(voxelshape, enumfacing$axis, voxelshape.field_197768_g.getSize(enumfacing$axis) - 1), new VoxelShapeSplit(voxelshape1, enumfacing$axis, 0), IBooleanFunction.OR), IBooleanFunction.ONLY_FIRST);
      } else {
         return true;
      }
   }

   @VisibleForTesting
   protected static IDoubleListMerger func_199410_a(int p_199410_0_, DoubleList p_199410_1_, DoubleList p_199410_2_, boolean p_199410_3_, boolean p_199410_4_) {
      if (p_199410_1_ instanceof DoubleRangeList && p_199410_2_ instanceof DoubleRangeList) {
         int i = p_199410_1_.size() - 1;
         int j = p_199410_2_.size() - 1;
         long k = func_197877_a(i, j);
         if ((long)p_199410_0_ * k <= 256L) {
            return new DoubleCubeMergingList(i, j);
         }
      }

      if (p_199410_1_.getDouble(p_199410_1_.size() - 1) < p_199410_2_.getDouble(0) - 1.0E-7D) {
         return new NonOverlappingMerger(p_199410_1_, p_199410_2_, false);
      } else if (p_199410_2_.getDouble(p_199410_2_.size() - 1) < p_199410_1_.getDouble(0) - 1.0E-7D) {
         return new NonOverlappingMerger(p_199410_2_, p_199410_1_, true);
      } else if (Objects.equals(p_199410_1_, p_199410_2_)) {
         if (p_199410_1_ instanceof SimpleDoubleMerger) {
            return (IDoubleListMerger)p_199410_1_;
         } else {
            return p_199410_2_ instanceof SimpleDoubleMerger ? (IDoubleListMerger)p_199410_2_ : new SimpleDoubleMerger(p_199410_1_);
         }
      } else {
         return new IndirectMerger(p_199410_1_, p_199410_2_, p_199410_3_, p_199410_4_);
      }
   }

   public interface LineConsumer {
      void consume(double p_consume_1_, double p_consume_3_, double p_consume_5_, double p_consume_7_, double p_consume_9_, double p_consume_11_);
   }
}
