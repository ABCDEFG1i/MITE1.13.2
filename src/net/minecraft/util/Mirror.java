package net.minecraft.util;

public enum Mirror {
   NONE,
   LEFT_RIGHT,
   FRONT_BACK;

   public int mirrorRotation(int p_185802_1_, int p_185802_2_) {
      int i = p_185802_2_ / 2;
      int j = p_185802_1_ > i ? p_185802_1_ - p_185802_2_ : p_185802_1_;
      switch(this) {
      case FRONT_BACK:
         return (p_185802_2_ - j) % p_185802_2_;
      case LEFT_RIGHT:
         return (i - j + p_185802_2_) % p_185802_2_;
      default:
         return p_185802_1_;
      }
   }

   public Rotation toRotation(EnumFacing p_185800_1_) {
      EnumFacing.Axis enumfacing$axis = p_185800_1_.getAxis();
      return (this != LEFT_RIGHT || enumfacing$axis != EnumFacing.Axis.Z) && (this != FRONT_BACK || enumfacing$axis != EnumFacing.Axis.X) ? Rotation.NONE : Rotation.CLOCKWISE_180;
   }

   public EnumFacing mirror(EnumFacing p_185803_1_) {
      if (this == FRONT_BACK && p_185803_1_.getAxis() == EnumFacing.Axis.X) {
         return p_185803_1_.getOpposite();
      } else {
         return this == LEFT_RIGHT && p_185803_1_.getAxis() == EnumFacing.Axis.Z ? p_185803_1_.getOpposite() : p_185803_1_;
      }
   }
}
