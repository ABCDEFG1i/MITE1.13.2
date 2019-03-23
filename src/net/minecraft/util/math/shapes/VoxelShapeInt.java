package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.EnumFacing;

public final class VoxelShapeInt extends VoxelShape {
   private final int field_197779_a;
   private final int field_197780_b;
   private final int field_197781_c;

   public VoxelShapeInt(VoxelShapePart p_i47679_1_, int p_i47679_2_, int p_i47679_3_, int p_i47679_4_) {
      super(p_i47679_1_);
      this.field_197779_a = p_i47679_2_;
      this.field_197780_b = p_i47679_3_;
      this.field_197781_c = p_i47679_4_;
   }

   protected DoubleList getValues(EnumFacing.Axis p_197757_1_) {
      return new IntRangeList(this.field_197768_g.getSize(p_197757_1_), p_197757_1_.getCoordinate(this.field_197779_a, this.field_197780_b, this.field_197781_c));
   }
}
