package net.minecraft.util.math;

import net.minecraft.entity.Entity;

public class ChunkPos {
   public final int x;
   public final int z;

   public ChunkPos(int p_i1947_1_, int p_i1947_2_) {
      this.x = p_i1947_1_;
      this.z = p_i1947_2_;
   }

   public ChunkPos(BlockPos p_i46717_1_) {
      this.x = p_i46717_1_.getX() >> 4;
      this.z = p_i46717_1_.getZ() >> 4;
   }

   public ChunkPos(long p_i48713_1_) {
      this.x = (int)p_i48713_1_;
      this.z = (int)(p_i48713_1_ >> 32);
   }

   public long asLong() {
      return asLong(this.x, this.z);
   }

   public static long asLong(int p_77272_0_, int p_77272_1_) {
      return (long)p_77272_0_ & 4294967295L | ((long)p_77272_1_ & 4294967295L) << 32;
   }

   public static int func_212578_a(long p_212578_0_) {
      return (int)(p_212578_0_ & 4294967295L);
   }

   public static int func_212579_b(long p_212579_0_) {
      return (int)(p_212579_0_ >>> 32 & 4294967295L);
   }

   public int hashCode() {
      int i = 1664525 * this.x + 1013904223;
      int j = 1664525 * (this.z ^ -559038737) + 1013904223;
      return i ^ j;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof ChunkPos)) {
         return false;
      } else {
         ChunkPos chunkpos = (ChunkPos)p_equals_1_;
         return this.x == chunkpos.x && this.z == chunkpos.z;
      }
   }

   public double getDistanceSq(Entity p_185327_1_) {
      double d0 = (double)(this.x * 16 + 8);
      double d1 = (double)(this.z * 16 + 8);
      double d2 = d0 - p_185327_1_.posX;
      double d3 = d1 - p_185327_1_.posZ;
      return d2 * d2 + d3 * d3;
   }

   public int getXStart() {
      return this.x << 4;
   }

   public int getZStart() {
      return this.z << 4;
   }

   public int getXEnd() {
      return (this.x << 4) + 15;
   }

   public int getZEnd() {
      return (this.z << 4) + 15;
   }

   public BlockPos getBlock(int p_180331_1_, int p_180331_2_, int p_180331_3_) {
      return new BlockPos((this.x << 4) + p_180331_1_, p_180331_2_, (this.z << 4) + p_180331_3_);
   }

   public String toString() {
      return "[" + this.x + ", " + this.z + "]";
   }

   public BlockPos asBlockPos() {
      return new BlockPos(this.x << 4, 0, this.z << 4);
   }
}
