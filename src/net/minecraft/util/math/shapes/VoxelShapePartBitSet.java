package net.minecraft.util.math.shapes;

import java.util.BitSet;
import net.minecraft.util.EnumFacing;

public final class VoxelShapePartBitSet extends VoxelShapePart {
   private final BitSet bitSet;
   private int startX;
   private int startY;
   private int startZ;
   private int endX;
   private int endY;
   private int endZ;

   public VoxelShapePartBitSet(int p_i47690_1_, int p_i47690_2_, int p_i47690_3_) {
      this(p_i47690_1_, p_i47690_2_, p_i47690_3_, p_i47690_1_, p_i47690_2_, p_i47690_3_, 0, 0, 0);
   }

   public VoxelShapePartBitSet(int p_i48183_1_, int p_i48183_2_, int p_i48183_3_, int p_i48183_4_, int p_i48183_5_, int p_i48183_6_, int p_i48183_7_, int p_i48183_8_, int p_i48183_9_) {
      super(p_i48183_1_, p_i48183_2_, p_i48183_3_);
      this.bitSet = new BitSet(p_i48183_1_ * p_i48183_2_ * p_i48183_3_);
      this.startX = p_i48183_4_;
      this.startY = p_i48183_5_;
      this.startZ = p_i48183_6_;
      this.endX = p_i48183_7_;
      this.endY = p_i48183_8_;
      this.endZ = p_i48183_9_;
   }

   public VoxelShapePartBitSet(VoxelShapePart p_i47692_1_) {
      super(p_i47692_1_.xSize, p_i47692_1_.ySize, p_i47692_1_.zSize);
      if (p_i47692_1_ instanceof VoxelShapePartBitSet) {
         this.bitSet = (BitSet)((VoxelShapePartBitSet)p_i47692_1_).bitSet.clone();
      } else {
         this.bitSet = new BitSet(this.xSize * this.ySize * this.zSize);

         for(int i = 0; i < this.xSize; ++i) {
            for(int j = 0; j < this.ySize; ++j) {
               for(int k = 0; k < this.zSize; ++k) {
                  if (p_i47692_1_.isFilled(i, j, k)) {
                     this.bitSet.set(this.getIndex(i, j, k));
                  }
               }
            }
         }
      }

      this.startX = p_i47692_1_.getStart(EnumFacing.Axis.X);
      this.startY = p_i47692_1_.getStart(EnumFacing.Axis.Y);
      this.startZ = p_i47692_1_.getStart(EnumFacing.Axis.Z);
      this.endX = p_i47692_1_.getEnd(EnumFacing.Axis.X);
      this.endY = p_i47692_1_.getEnd(EnumFacing.Axis.Y);
      this.endZ = p_i47692_1_.getEnd(EnumFacing.Axis.Z);
   }

   protected int getIndex(int p_197848_1_, int p_197848_2_, int p_197848_3_) {
      return (p_197848_1_ * this.ySize + p_197848_2_) * this.zSize + p_197848_3_;
   }

   public boolean isFilled(int p_197835_1_, int p_197835_2_, int p_197835_3_) {
      return this.bitSet.get(this.getIndex(p_197835_1_, p_197835_2_, p_197835_3_));
   }

   public void func_199625_a(int p_199625_1_, int p_199625_2_, int p_199625_3_, boolean p_199625_4_, boolean p_199625_5_) {
      this.bitSet.set(this.getIndex(p_199625_1_, p_199625_2_, p_199625_3_), p_199625_5_);
      if (p_199625_4_ && p_199625_5_) {
         this.startX = Math.min(this.startX, p_199625_1_);
         this.startY = Math.min(this.startY, p_199625_2_);
         this.startZ = Math.min(this.startZ, p_199625_3_);
         this.endX = Math.max(this.endX, p_199625_1_ + 1);
         this.endY = Math.max(this.endY, p_199625_2_ + 1);
         this.endZ = Math.max(this.endZ, p_199625_3_ + 1);
      }

   }

   public boolean isEmpty() {
      return this.bitSet.isEmpty();
   }

   public int getStart(EnumFacing.Axis p_199623_1_) {
      return p_199623_1_.getCoordinate(this.startX, this.startY, this.startZ);
   }

   public int getEnd(EnumFacing.Axis p_199624_1_) {
      return p_199624_1_.getCoordinate(this.endX, this.endY, this.endZ);
   }

   protected boolean isZAxisLineFull(int p_197833_1_, int p_197833_2_, int p_197833_3_, int p_197833_4_) {
      if (p_197833_3_ >= 0 && p_197833_4_ >= 0 && p_197833_1_ >= 0) {
         if (p_197833_3_ < this.xSize && p_197833_4_ < this.ySize && p_197833_2_ <= this.zSize) {
            return this.bitSet.nextClearBit(this.getIndex(p_197833_3_, p_197833_4_, p_197833_1_)) >= this.getIndex(p_197833_3_, p_197833_4_, p_197833_2_);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected void setZAxisLine(int p_197834_1_, int p_197834_2_, int p_197834_3_, int p_197834_4_, boolean p_197834_5_) {
      this.bitSet.set(this.getIndex(p_197834_3_, p_197834_4_, p_197834_1_), this.getIndex(p_197834_3_, p_197834_4_, p_197834_2_), p_197834_5_);
   }

   static VoxelShapePartBitSet func_197852_a(VoxelShapePart p_197852_0_, VoxelShapePart p_197852_1_, IDoubleListMerger p_197852_2_, IDoubleListMerger p_197852_3_, IDoubleListMerger p_197852_4_, IBooleanFunction p_197852_5_) {
      VoxelShapePartBitSet voxelshapepartbitset = new VoxelShapePartBitSet(p_197852_2_.func_212435_a().size() - 1, p_197852_3_.func_212435_a().size() - 1, p_197852_4_.func_212435_a().size() - 1);
      int[] aint = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
      p_197852_2_.func_197855_a((p_199628_7_, p_199628_8_, p_199628_9_) -> {
         boolean[] aboolean = new boolean[]{false};
         boolean flag = p_197852_3_.func_197855_a((p_199627_10_, p_199627_11_, p_199627_12_) -> {
            boolean[] aboolean1 = new boolean[]{false};
            boolean flag1 = p_197852_4_.func_197855_a((p_199629_12_, p_199629_13_, p_199629_14_) -> {
               boolean flag2 = p_197852_5_.apply(p_197852_0_.contains(p_199628_7_, p_199627_10_, p_199629_12_), p_197852_1_.contains(p_199628_8_, p_199627_11_, p_199629_13_));
               if (flag2) {
                  voxelshapepartbitset.bitSet.set(voxelshapepartbitset.getIndex(p_199628_9_, p_199627_12_, p_199629_14_));
                  aint[2] = Math.min(aint[2], p_199629_14_);
                  aint[5] = Math.max(aint[5], p_199629_14_);
                  aboolean1[0] = true;
               }

               return true;
            });
            if (aboolean1[0]) {
               aint[1] = Math.min(aint[1], p_199627_12_);
               aint[4] = Math.max(aint[4], p_199627_12_);
               aboolean[0] = true;
            }

            return flag1;
         });
         if (aboolean[0]) {
            aint[0] = Math.min(aint[0], p_199628_9_);
            aint[3] = Math.max(aint[3], p_199628_9_);
         }

         return flag;
      });
      voxelshapepartbitset.startX = aint[0];
      voxelshapepartbitset.startY = aint[1];
      voxelshapepartbitset.startZ = aint[2];
      voxelshapepartbitset.endX = aint[3] + 1;
      voxelshapepartbitset.endY = aint[4] + 1;
      voxelshapepartbitset.endZ = aint[5] + 1;
      return voxelshapepartbitset;
   }
}
