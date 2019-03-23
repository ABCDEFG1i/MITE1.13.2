package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.EnumFacing;

final class VoxelShapeCube extends VoxelShape {
   VoxelShapeCube(VoxelShapePart p_i48182_1_) {
      super(p_i48182_1_);
   }

   protected DoubleList getValues(EnumFacing.Axis p_197757_1_) {
      return new DoubleRangeList(this.field_197768_g.getSize(p_197757_1_));
   }
}
