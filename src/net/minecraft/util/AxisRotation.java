package net.minecraft.util;

public enum AxisRotation {
   NONE {
      public int getCoordinate(int p_197517_1_, int p_197517_2_, int p_197517_3_, EnumFacing.Axis p_197517_4_) {
         return p_197517_4_.getCoordinate(p_197517_1_, p_197517_2_, p_197517_3_);
      }

      public EnumFacing.Axis rotate(EnumFacing.Axis p_197513_1_) {
         return p_197513_1_;
      }

      public AxisRotation reverse() {
         return this;
      }
   },
   FORWARD {
      public int getCoordinate(int p_197517_1_, int p_197517_2_, int p_197517_3_, EnumFacing.Axis p_197517_4_) {
         return p_197517_4_.getCoordinate(p_197517_3_, p_197517_1_, p_197517_2_);
      }

      public EnumFacing.Axis rotate(EnumFacing.Axis p_197513_1_) {
         return AXES[Math.floorMod(p_197513_1_.ordinal() + 1, 3)];
      }

      public AxisRotation reverse() {
         return BACKWARD;
      }
   },
   BACKWARD {
      public int getCoordinate(int p_197517_1_, int p_197517_2_, int p_197517_3_, EnumFacing.Axis p_197517_4_) {
         return p_197517_4_.getCoordinate(p_197517_2_, p_197517_3_, p_197517_1_);
      }

      public EnumFacing.Axis rotate(EnumFacing.Axis p_197513_1_) {
         return AXES[Math.floorMod(p_197513_1_.ordinal() - 1, 3)];
      }

      public AxisRotation reverse() {
         return FORWARD;
      }
   };

   public static final EnumFacing.Axis[] AXES = EnumFacing.Axis.values();
   public static final AxisRotation[] AXIS_ROTATIONS = values();

   private AxisRotation() {
   }

   public abstract int getCoordinate(int p_197517_1_, int p_197517_2_, int p_197517_3_, EnumFacing.Axis p_197517_4_);

   public abstract EnumFacing.Axis rotate(EnumFacing.Axis p_197513_1_);

   public abstract AxisRotation reverse();

   public static AxisRotation from(EnumFacing.Axis p_197516_0_, EnumFacing.Axis p_197516_1_) {
      return AXIS_ROTATIONS[Math.floorMod(p_197516_1_.ordinal() - p_197516_0_.ordinal(), 3)];
   }
}
