package net.minecraft.util;

import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum EnumFacing implements IStringSerializable {
   DOWN(0, 1, -1, "down", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.Y, new Vec3i(0, -1, 0)),
   UP(1, 0, -1, "up", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.Y, new Vec3i(0, 1, 0)),
   NORTH(2, 3, 2, "north", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.Z, new Vec3i(0, 0, -1)),
   SOUTH(3, 2, 0, "south", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.Z, new Vec3i(0, 0, 1)),
   WEST(4, 5, 1, "west", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.X, new Vec3i(-1, 0, 0)),
   EAST(5, 4, 3, "east", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.X, new Vec3i(1, 0, 0));

   private final int index;
   private final int opposite;
   private final int horizontalIndex;
   private final String name;
   private final EnumFacing.Axis axis;
   private final EnumFacing.AxisDirection axisDirection;
   private final Vec3i directionVec;
   private static final EnumFacing[] VALUES = values();
   private static final Map<String, EnumFacing> NAME_LOOKUP = Arrays.stream(VALUES).collect(Collectors.toMap(EnumFacing::getName2, (p_199787_0_) -> {
      return p_199787_0_;
   }));
   private static final EnumFacing[] BY_INDEX = Arrays.stream(VALUES).sorted(Comparator.comparingInt((p_199790_0_) -> {
      return p_199790_0_.index;
   })).toArray((p_199788_0_) -> {
      return new EnumFacing[p_199788_0_];
   });
   private static final EnumFacing[] BY_HORIZONTAL_INDEX = Arrays.stream(VALUES).filter((p_199786_0_) -> {
      return p_199786_0_.getAxis().isHorizontal();
   }).sorted(Comparator.comparingInt((p_199789_0_) -> {
      return p_199789_0_.horizontalIndex;
   })).toArray((p_199791_0_) -> {
      return new EnumFacing[p_199791_0_];
   });

   private EnumFacing(int p_i46016_3_, int p_i46016_4_, int p_i46016_5_, String p_i46016_6_, EnumFacing.AxisDirection p_i46016_7_, EnumFacing.Axis p_i46016_8_, Vec3i p_i46016_9_) {
      this.index = p_i46016_3_;
      this.horizontalIndex = p_i46016_5_;
      this.opposite = p_i46016_4_;
      this.name = p_i46016_6_;
      this.axis = p_i46016_8_;
      this.axisDirection = p_i46016_7_;
      this.directionVec = p_i46016_9_;
   }

   public static EnumFacing[] getFacingDirections(Entity p_196054_0_) {
      float f = p_196054_0_.getPitch(1.0F) * ((float)Math.PI / 180F);
      float f1 = -p_196054_0_.getYaw(1.0F) * ((float)Math.PI / 180F);
      float f2 = MathHelper.sin(f);
      float f3 = MathHelper.cos(f);
      float f4 = MathHelper.sin(f1);
      float f5 = MathHelper.cos(f1);
      boolean flag = f4 > 0.0F;
      boolean flag1 = f2 < 0.0F;
      boolean flag2 = f5 > 0.0F;
      float f6 = flag ? f4 : -f4;
      float f7 = flag1 ? -f2 : f2;
      float f8 = flag2 ? f5 : -f5;
      float f9 = f6 * f3;
      float f10 = f8 * f3;
      EnumFacing enumfacing = flag ? EAST : WEST;
      EnumFacing enumfacing1 = flag1 ? UP : DOWN;
      EnumFacing enumfacing2 = flag2 ? SOUTH : NORTH;
      if (f6 > f8) {
         if (f7 > f9) {
            return compose(enumfacing1, enumfacing, enumfacing2);
         } else {
            return f10 > f7 ? compose(enumfacing, enumfacing2, enumfacing1) : compose(enumfacing, enumfacing1, enumfacing2);
         }
      } else if (f7 > f10) {
         return compose(enumfacing1, enumfacing2, enumfacing);
      } else {
         return f9 > f7 ? compose(enumfacing2, enumfacing, enumfacing1) : compose(enumfacing2, enumfacing1, enumfacing);
      }
   }

   private static EnumFacing[] compose(EnumFacing p_196053_0_, EnumFacing p_196053_1_, EnumFacing p_196053_2_) {
      return new EnumFacing[]{p_196053_0_, p_196053_1_, p_196053_2_, p_196053_2_.getOpposite(), p_196053_1_.getOpposite(), p_196053_0_.getOpposite()};
   }

   public int getIndex() {
      return this.index;
   }

   public int getHorizontalIndex() {
      return this.horizontalIndex;
   }

   public EnumFacing.AxisDirection getAxisDirection() {
      return this.axisDirection;
   }

   public EnumFacing getOpposite() {
      return byIndex(this.opposite);
   }

   @OnlyIn(Dist.CLIENT)
   public EnumFacing rotateAround(EnumFacing.Axis p_176732_1_) {
      switch(p_176732_1_) {
      case X:
         if (this != WEST && this != EAST) {
            return this.rotateX();
         }

         return this;
      case Y:
         if (this != UP && this != DOWN) {
            return this.rotateY();
         }

         return this;
      case Z:
         if (this != NORTH && this != SOUTH) {
            return this.rotateZ();
         }

         return this;
      default:
         throw new IllegalStateException("Unable to get CW facing for axis " + p_176732_1_);
      }
   }

   public EnumFacing rotateY() {
      switch(this) {
      case NORTH:
         return EAST;
      case EAST:
         return SOUTH;
      case SOUTH:
         return WEST;
      case WEST:
         return NORTH;
      default:
         throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
      }
   }

   @OnlyIn(Dist.CLIENT)
   private EnumFacing rotateX() {
      switch(this) {
      case NORTH:
         return DOWN;
      case EAST:
      case WEST:
      default:
         throw new IllegalStateException("Unable to get X-rotated facing of " + this);
      case SOUTH:
         return UP;
      case UP:
         return NORTH;
      case DOWN:
         return SOUTH;
      }
   }

   @OnlyIn(Dist.CLIENT)
   private EnumFacing rotateZ() {
      switch(this) {
      case EAST:
         return DOWN;
      case SOUTH:
      default:
         throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
      case WEST:
         return UP;
      case UP:
         return EAST;
      case DOWN:
         return WEST;
      }
   }

   public EnumFacing rotateYCCW() {
      switch(this) {
      case NORTH:
         return WEST;
      case EAST:
         return NORTH;
      case SOUTH:
         return EAST;
      case WEST:
         return SOUTH;
      default:
         throw new IllegalStateException("Unable to get CCW facing of " + this);
      }
   }

   public int getXOffset() {
      return this.axis == EnumFacing.Axis.X ? this.axisDirection.getOffset() : 0;
   }

   public int getYOffset() {
      return this.axis == EnumFacing.Axis.Y ? this.axisDirection.getOffset() : 0;
   }

   public int getZOffset() {
      return this.axis == EnumFacing.Axis.Z ? this.axisDirection.getOffset() : 0;
   }

   public String getName2() {
      return this.name;
   }

   public EnumFacing.Axis getAxis() {
      return this.axis;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static EnumFacing byName(@Nullable String p_176739_0_) {
      return p_176739_0_ == null ? null : NAME_LOOKUP.get(p_176739_0_.toLowerCase(Locale.ROOT));
   }

   public static EnumFacing byIndex(int p_82600_0_) {
      return BY_INDEX[MathHelper.abs(p_82600_0_ % BY_INDEX.length)];
   }

   public static EnumFacing byHorizontalIndex(int p_176731_0_) {
      return BY_HORIZONTAL_INDEX[MathHelper.abs(p_176731_0_ % BY_HORIZONTAL_INDEX.length)];
   }

   public static EnumFacing fromAngle(double p_176733_0_) {
      return byHorizontalIndex(MathHelper.floor(p_176733_0_ / 90.0D + 0.5D) & 3);
   }

   public static EnumFacing getFacingFromAxisDirection(EnumFacing.Axis p_211699_0_, EnumFacing.AxisDirection p_211699_1_) {
      switch(p_211699_0_) {
      case X:
         return p_211699_1_ == EnumFacing.AxisDirection.POSITIVE ? EAST : WEST;
      case Y:
         return p_211699_1_ == EnumFacing.AxisDirection.POSITIVE ? UP : DOWN;
      case Z:
      default:
         return p_211699_1_ == EnumFacing.AxisDirection.POSITIVE ? SOUTH : NORTH;
      }
   }

   public float getHorizontalAngle() {
      return (float)((this.horizontalIndex & 3) * 90);
   }

   public static EnumFacing random(Random p_176741_0_) {
      return values()[p_176741_0_.nextInt(values().length)];
   }

   public static EnumFacing getFacingFromVector(double p_210769_0_, double p_210769_2_, double p_210769_4_) {
      return getFacingFromVector((float)p_210769_0_, (float)p_210769_2_, (float)p_210769_4_);
   }

   public static EnumFacing getFacingFromVector(float p_176737_0_, float p_176737_1_, float p_176737_2_) {
      EnumFacing enumfacing = NORTH;
      float f = Float.MIN_VALUE;

      for(EnumFacing enumfacing1 : VALUES) {
         float f1 = p_176737_0_ * (float)enumfacing1.directionVec.getX() + p_176737_1_ * (float)enumfacing1.directionVec.getY() + p_176737_2_ * (float)enumfacing1.directionVec.getZ();
         if (f1 > f) {
            f = f1;
            enumfacing = enumfacing1;
         }
      }

      return enumfacing;
   }

   public String toString() {
      return this.name;
   }

   public String getName() {
      return this.name;
   }

   public static EnumFacing getFacingFromAxis(EnumFacing.AxisDirection p_181076_0_, EnumFacing.Axis p_181076_1_) {
      for(EnumFacing enumfacing : values()) {
         if (enumfacing.getAxisDirection() == p_181076_0_ && enumfacing.getAxis() == p_181076_1_) {
            return enumfacing;
         }
      }

      throw new IllegalArgumentException("No such direction: " + p_181076_0_ + " " + p_181076_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public Vec3i getDirectionVec() {
      return this.directionVec;
   }

   public static enum Axis implements Predicate<EnumFacing>, IStringSerializable {
      X("x") {
         public int getCoordinate(int p_196052_1_, int p_196052_2_, int p_196052_3_) {
            return p_196052_1_;
         }

         public double getCoordinate(double p_196051_1_, double p_196051_3_, double p_196051_5_) {
            return p_196051_1_;
         }
      },
      Y("y") {
         public int getCoordinate(int p_196052_1_, int p_196052_2_, int p_196052_3_) {
            return p_196052_2_;
         }

         public double getCoordinate(double p_196051_1_, double p_196051_3_, double p_196051_5_) {
            return p_196051_3_;
         }
      },
      Z("z") {
         public int getCoordinate(int p_196052_1_, int p_196052_2_, int p_196052_3_) {
            return p_196052_3_;
         }

         public double getCoordinate(double p_196051_1_, double p_196051_3_, double p_196051_5_) {
            return p_196051_5_;
         }
      };

      private static final Map<String, EnumFacing.Axis> NAME_LOOKUP = Arrays.stream(values()).collect(Collectors.toMap(EnumFacing.Axis::getName2, (p_199785_0_) -> {
         return p_199785_0_;
      }));
      private final String name;

      private Axis(String p_i49394_3_) {
         this.name = p_i49394_3_;
      }

      @Nullable
      @OnlyIn(Dist.CLIENT)
      public static EnumFacing.Axis byName(String p_176717_0_) {
         return NAME_LOOKUP.get(p_176717_0_.toLowerCase(Locale.ROOT));
      }

      public String getName2() {
         return this.name;
      }

      public boolean isVertical() {
         return this == Y;
      }

      public boolean isHorizontal() {
         return this == X || this == Z;
      }

      public String toString() {
         return this.name;
      }

      public boolean test(@Nullable EnumFacing p_test_1_) {
         return p_test_1_ != null && p_test_1_.getAxis() == this;
      }

      public EnumFacing.Plane getPlane() {
         switch(this) {
         case X:
         case Z:
            return EnumFacing.Plane.HORIZONTAL;
         case Y:
            return EnumFacing.Plane.VERTICAL;
         default:
            throw new Error("Someone's been tampering with the universe!");
         }
      }

      public String getName() {
         return this.name;
      }

      public abstract int getCoordinate(int p_196052_1_, int p_196052_2_, int p_196052_3_);

      public abstract double getCoordinate(double p_196051_1_, double p_196051_3_, double p_196051_5_);
   }

   public static enum AxisDirection {
      POSITIVE(1, "Towards positive"),
      NEGATIVE(-1, "Towards negative");

      private final int offset;
      private final String description;

      private AxisDirection(int p_i46014_3_, String p_i46014_4_) {
         this.offset = p_i46014_3_;
         this.description = p_i46014_4_;
      }

      public int getOffset() {
         return this.offset;
      }

      public String toString() {
         return this.description;
      }
   }

   public static enum Plane implements Iterable<EnumFacing>, Predicate<EnumFacing> {
      HORIZONTAL(new EnumFacing[]{EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST}, new EnumFacing.Axis[]{EnumFacing.Axis.X, EnumFacing.Axis.Z}),
      VERTICAL(new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN}, new EnumFacing.Axis[]{EnumFacing.Axis.Y});

      private final EnumFacing[] facingValues;
      private final EnumFacing.Axis[] axisValues;

      private Plane(EnumFacing[] p_i49393_3_, EnumFacing.Axis[] p_i49393_4_) {
         this.facingValues = p_i49393_3_;
         this.axisValues = p_i49393_4_;
      }

      public EnumFacing random(Random p_179518_1_) {
         return this.facingValues[p_179518_1_.nextInt(this.facingValues.length)];
      }

      public boolean test(@Nullable EnumFacing p_test_1_) {
         return p_test_1_ != null && p_test_1_.getAxis().getPlane() == this;
      }

      public Iterator<EnumFacing> iterator() {
         return Iterators.forArray(this.facingValues);
      }
   }
}
