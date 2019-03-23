package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.EnumFacing;

public class VoxelShapeSplit extends VoxelShape {
   private final VoxelShape shape;
   private final EnumFacing.Axis axis;
   private final DoubleList field_197778_c = new DoubleRangeList(1);

   public VoxelShapeSplit(VoxelShape p_i47682_1_, EnumFacing.Axis p_i47682_2_, int p_i47682_3_) {
      super(makeShapePart(p_i47682_1_.field_197768_g, p_i47682_2_, p_i47682_3_));
      this.shape = p_i47682_1_;
      this.axis = p_i47682_2_;
   }

   private static VoxelShapePart makeShapePart(VoxelShapePart p_197775_0_, EnumFacing.Axis p_197775_1_, int p_197775_2_) {
      return new VoxelShapePartSplit(p_197775_0_, p_197775_1_.getCoordinate(p_197775_2_, 0, 0), p_197775_1_.getCoordinate(0, p_197775_2_, 0), p_197775_1_.getCoordinate(0, 0, p_197775_2_), p_197775_1_.getCoordinate(p_197775_2_ + 1, p_197775_0_.xSize, p_197775_0_.xSize), p_197775_1_.getCoordinate(p_197775_0_.ySize, p_197775_2_ + 1, p_197775_0_.ySize), p_197775_1_.getCoordinate(p_197775_0_.zSize, p_197775_0_.zSize, p_197775_2_ + 1));
   }

   protected DoubleList getValues(EnumFacing.Axis p_197757_1_) {
      return p_197757_1_ == this.axis ? this.field_197778_c : this.shape.getValues(p_197757_1_);
   }
}
