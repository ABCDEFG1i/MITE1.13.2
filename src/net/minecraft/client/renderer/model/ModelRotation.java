package net.minecraft.client.renderer.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum ModelRotation {
   X0_Y0(0, 0),
   X0_Y90(0, 90),
   X0_Y180(0, 180),
   X0_Y270(0, 270),
   X90_Y0(90, 0),
   X90_Y90(90, 90),
   X90_Y180(90, 180),
   X90_Y270(90, 270),
   X180_Y0(180, 0),
   X180_Y90(180, 90),
   X180_Y180(180, 180),
   X180_Y270(180, 270),
   X270_Y0(270, 0),
   X270_Y90(270, 90),
   X270_Y180(270, 180),
   X270_Y270(270, 270);

   private static final Map<Integer, ModelRotation> field_177546_q = Arrays.stream(values()).sorted(Comparator.comparingInt((p_199757_0_) -> {
      return p_199757_0_.field_177545_r;
   })).collect(Collectors.toMap((p_199756_0_) -> {
      return p_199756_0_.field_177545_r;
   }, (p_199758_0_) -> {
      return p_199758_0_;
   }));
   private final int field_177545_r;
   private final Quaternion field_177544_s;
   private final int field_177543_t;
   private final int field_177542_u;

   private static int func_177521_b(int p_177521_0_, int p_177521_1_) {
      return p_177521_0_ * 360 + p_177521_1_;
   }

   private ModelRotation(int p_i46087_3_, int p_i46087_4_) {
      this.field_177545_r = func_177521_b(p_i46087_3_, p_i46087_4_);
      Quaternion quaternion = new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), (float)(-p_i46087_4_), true);
      quaternion.multiply(new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), (float)(-p_i46087_3_), true));
      this.field_177544_s = quaternion;
      this.field_177543_t = MathHelper.abs(p_i46087_3_ / 90);
      this.field_177542_u = MathHelper.abs(p_i46087_4_ / 90);
   }

   public Quaternion func_195820_a() {
      return this.field_177544_s;
   }

   public EnumFacing func_177523_a(EnumFacing p_177523_1_) {
      EnumFacing enumfacing = p_177523_1_;

      for(int i = 0; i < this.field_177543_t; ++i) {
         enumfacing = enumfacing.rotateAround(EnumFacing.Axis.X);
      }

      if (enumfacing.getAxis() != EnumFacing.Axis.Y) {
         for(int j = 0; j < this.field_177542_u; ++j) {
            enumfacing = enumfacing.rotateAround(EnumFacing.Axis.Y);
         }
      }

      return enumfacing;
   }

   public int func_177520_a(EnumFacing p_177520_1_, int p_177520_2_) {
      int i = p_177520_2_;
      if (p_177520_1_.getAxis() == EnumFacing.Axis.X) {
         i = (p_177520_2_ + this.field_177543_t) % 4;
      }

      EnumFacing enumfacing = p_177520_1_;

      for(int j = 0; j < this.field_177543_t; ++j) {
         enumfacing = enumfacing.rotateAround(EnumFacing.Axis.X);
      }

      if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
         i = (i + this.field_177542_u) % 4;
      }

      return i;
   }

   public static ModelRotation func_177524_a(int p_177524_0_, int p_177524_1_) {
      return field_177546_q.get(func_177521_b(MathHelper.normalizeAngle(p_177524_0_, 360), MathHelper.normalizeAngle(p_177524_1_, 360)));
   }
}
