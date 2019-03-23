package net.minecraft.util.math.shapes;

import net.minecraft.util.EnumFacing;

final class VoxelShapePartSplit extends VoxelShapePart {
   private final VoxelShapePart part;
   private final int startX;
   private final int startY;
   private final int startZ;
   private final int endX;
   private final int endY;
   private final int endZ;

   public VoxelShapePartSplit(VoxelShapePart p_i47681_1_, int p_i47681_2_, int p_i47681_3_, int p_i47681_4_, int p_i47681_5_, int p_i47681_6_, int p_i47681_7_) {
      super(p_i47681_5_ - p_i47681_2_, p_i47681_6_ - p_i47681_3_, p_i47681_7_ - p_i47681_4_);
      this.part = p_i47681_1_;
      this.startX = p_i47681_2_;
      this.startY = p_i47681_3_;
      this.startZ = p_i47681_4_;
      this.endX = p_i47681_5_;
      this.endY = p_i47681_6_;
      this.endZ = p_i47681_7_;
   }

   public boolean isFilled(int p_197835_1_, int p_197835_2_, int p_197835_3_) {
      return this.part.isFilled(this.startX + p_197835_1_, this.startY + p_197835_2_, this.startZ + p_197835_3_);
   }

   public void func_199625_a(int p_199625_1_, int p_199625_2_, int p_199625_3_, boolean p_199625_4_, boolean p_199625_5_) {
      this.part.func_199625_a(this.startX + p_199625_1_, this.startY + p_199625_2_, this.startZ + p_199625_3_, p_199625_4_, p_199625_5_);
   }

   public int getStart(EnumFacing.Axis p_199623_1_) {
      return Math.max(0, this.part.getStart(p_199623_1_) - p_199623_1_.getCoordinate(this.startX, this.startY, this.startZ));
   }

   public int getEnd(EnumFacing.Axis p_199624_1_) {
      return Math.min(p_199624_1_.getCoordinate(this.endX, this.endY, this.endZ), this.part.getEnd(p_199624_1_) - p_199624_1_.getCoordinate(this.startX, this.startY, this.startZ));
   }
}
