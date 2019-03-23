package net.minecraft.util;

public enum Rotation {
   NONE,
   CLOCKWISE_90,
   CLOCKWISE_180,
   COUNTERCLOCKWISE_90;

   public Rotation add(Rotation p_185830_1_) {
      switch(p_185830_1_) {
      case CLOCKWISE_180:
         switch(this) {
         case NONE:
            return CLOCKWISE_180;
         case CLOCKWISE_90:
            return COUNTERCLOCKWISE_90;
         case CLOCKWISE_180:
            return NONE;
         case COUNTERCLOCKWISE_90:
            return CLOCKWISE_90;
         }
      case COUNTERCLOCKWISE_90:
         switch(this) {
         case NONE:
            return COUNTERCLOCKWISE_90;
         case CLOCKWISE_90:
            return NONE;
         case CLOCKWISE_180:
            return CLOCKWISE_90;
         case COUNTERCLOCKWISE_90:
            return CLOCKWISE_180;
         }
      case CLOCKWISE_90:
         switch(this) {
         case NONE:
            return CLOCKWISE_90;
         case CLOCKWISE_90:
            return CLOCKWISE_180;
         case CLOCKWISE_180:
            return COUNTERCLOCKWISE_90;
         case COUNTERCLOCKWISE_90:
            return NONE;
         }
      default:
         return this;
      }
   }

   public EnumFacing rotate(EnumFacing p_185831_1_) {
      if (p_185831_1_.getAxis() == EnumFacing.Axis.Y) {
         return p_185831_1_;
      } else {
         switch(this) {
         case CLOCKWISE_90:
            return p_185831_1_.rotateY();
         case CLOCKWISE_180:
            return p_185831_1_.getOpposite();
         case COUNTERCLOCKWISE_90:
            return p_185831_1_.rotateYCCW();
         default:
            return p_185831_1_;
         }
      }
   }

   public int rotate(int p_185833_1_, int p_185833_2_) {
      switch(this) {
      case CLOCKWISE_90:
         return (p_185833_1_ + p_185833_2_ / 4) % p_185833_2_;
      case CLOCKWISE_180:
         return (p_185833_1_ + p_185833_2_ / 2) % p_185833_2_;
      case COUNTERCLOCKWISE_90:
         return (p_185833_1_ + p_185833_2_ * 3 / 4) % p_185833_2_;
      default:
         return p_185833_1_;
      }
   }
}
