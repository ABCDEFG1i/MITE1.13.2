package net.minecraft.client.renderer;

import java.util.Arrays;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class Vector3f {
   private final float[] components;

   public Vector3f(Vector3f p_i48097_1_) {
      this.components = Arrays.copyOf(p_i48097_1_.components, 3);
   }

   public Vector3f() {
      this.components = new float[3];
   }

   public Vector3f(float p_i48098_1_, float p_i48098_2_, float p_i48098_3_) {
      this.components = new float[]{p_i48098_1_, p_i48098_2_, p_i48098_3_};
   }

   public Vector3f(EnumFacing p_i48099_1_) {
      Vec3i vec3i = p_i48099_1_.getDirectionVec();
      this.components = new float[]{(float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ()};
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         Vector3f vector3f = (Vector3f)p_equals_1_;
         return Arrays.equals(this.components, vector3f.components);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.components);
   }

   public float getX() {
      return this.components[0];
   }

   public float getY() {
      return this.components[1];
   }

   public float getZ() {
      return this.components[2];
   }

   public void mul(float p_195898_1_) {
      for(int i = 0; i < 3; ++i) {
         this.components[i] *= p_195898_1_;
      }

   }

   public void clamp(float p_195901_1_, float p_195901_2_) {
      this.components[0] = MathHelper.clamp(this.components[0], p_195901_1_, p_195901_2_);
      this.components[1] = MathHelper.clamp(this.components[1], p_195901_1_, p_195901_2_);
      this.components[2] = MathHelper.clamp(this.components[2], p_195901_1_, p_195901_2_);
   }

   public void set(float p_195905_1_, float p_195905_2_, float p_195905_3_) {
      this.components[0] = p_195905_1_;
      this.components[1] = p_195905_2_;
      this.components[2] = p_195905_3_;
   }

   public void add(float p_195904_1_, float p_195904_2_, float p_195904_3_) {
      this.components[0] += p_195904_1_;
      this.components[1] += p_195904_2_;
      this.components[2] += p_195904_3_;
   }

   public void sub(Vector3f p_195897_1_) {
      for(int i = 0; i < 3; ++i) {
         this.components[i] -= p_195897_1_.components[i];
      }

   }

   public float dot(Vector3f p_195903_1_) {
      float f = 0.0F;

      for(int i = 0; i < 3; ++i) {
         f += this.components[i] * p_195903_1_.components[i];
      }

      return f;
   }

   public void normalize() {
      float f = 0.0F;

      for(int i = 0; i < 3; ++i) {
         f += this.components[i] * this.components[i];
      }

      for(int j = 0; j < 3; ++j) {
         this.components[j] /= f;
      }

   }

   public void cross(Vector3f p_195896_1_) {
      float f = this.components[0];
      float f1 = this.components[1];
      float f2 = this.components[2];
      float f3 = p_195896_1_.getX();
      float f4 = p_195896_1_.getY();
      float f5 = p_195896_1_.getZ();
      this.components[0] = f1 * f5 - f2 * f4;
      this.components[1] = f2 * f3 - f * f5;
      this.components[2] = f * f4 - f1 * f3;
   }
}
