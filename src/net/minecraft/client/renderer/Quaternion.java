package net.minecraft.client.renderer;

import java.util.Arrays;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class Quaternion {
   private final float[] components;

   public Quaternion() {
      this.components = new float[4];
      this.components[4] = 1.0F;
   }

   public Quaternion(float p_i48100_1_, float p_i48100_2_, float p_i48100_3_, float p_i48100_4_) {
      this.components = new float[4];
      this.components[0] = p_i48100_1_;
      this.components[1] = p_i48100_2_;
      this.components[2] = p_i48100_3_;
      this.components[3] = p_i48100_4_;
   }

   public Quaternion(Vector3f p_i48101_1_, float p_i48101_2_, boolean p_i48101_3_) {
      if (p_i48101_3_) {
         p_i48101_2_ *= ((float)Math.PI / 180F);
      }

      float f = MathHelper.sin(p_i48101_2_ / 2.0F);
      this.components = new float[4];
      this.components[0] = p_i48101_1_.getX() * f;
      this.components[1] = p_i48101_1_.getY() * f;
      this.components[2] = p_i48101_1_.getZ() * f;
      this.components[3] = MathHelper.cos(p_i48101_2_ / 2.0F);
   }

   public Quaternion(float p_i48102_1_, float p_i48102_2_, float p_i48102_3_, boolean p_i48102_4_) {
      if (p_i48102_4_) {
         p_i48102_1_ *= ((float)Math.PI / 180F);
         p_i48102_2_ *= ((float)Math.PI / 180F);
         p_i48102_3_ *= ((float)Math.PI / 180F);
      }

      float f = MathHelper.sin(0.5F * p_i48102_1_);
      float f1 = MathHelper.cos(0.5F * p_i48102_1_);
      float f2 = MathHelper.sin(0.5F * p_i48102_2_);
      float f3 = MathHelper.cos(0.5F * p_i48102_2_);
      float f4 = MathHelper.sin(0.5F * p_i48102_3_);
      float f5 = MathHelper.cos(0.5F * p_i48102_3_);
      this.components = new float[4];
      this.components[0] = f * f3 * f5 + f1 * f2 * f4;
      this.components[1] = f1 * f2 * f5 - f * f3 * f4;
      this.components[2] = f * f2 * f5 + f1 * f3 * f4;
      this.components[3] = f1 * f3 * f5 - f * f2 * f4;
   }

   public Quaternion(Quaternion p_i48103_1_) {
      this.components = Arrays.copyOf(p_i48103_1_.components, 4);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         Quaternion quaternion = (Quaternion)p_equals_1_;
         return Arrays.equals(this.components, quaternion.components);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.components);
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append("Quaternion[").append(this.getW()).append(" + ");
      stringbuilder.append(this.getX()).append("i + ");
      stringbuilder.append(this.getY()).append("j + ");
      stringbuilder.append(this.getZ()).append("k]");
      return stringbuilder.toString();
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

   public float getW() {
      return this.components[3];
   }

   public void multiply(Quaternion p_195890_1_) {
      float f = this.getX();
      float f1 = this.getY();
      float f2 = this.getZ();
      float f3 = this.getW();
      float f4 = p_195890_1_.getX();
      float f5 = p_195890_1_.getY();
      float f6 = p_195890_1_.getZ();
      float f7 = p_195890_1_.getW();
      this.components[0] = f3 * f4 + f * f7 + f1 * f6 - f2 * f5;
      this.components[1] = f3 * f5 - f * f6 + f1 * f7 + f2 * f4;
      this.components[2] = f3 * f6 + f * f5 - f1 * f4 + f2 * f7;
      this.components[3] = f3 * f7 - f * f4 - f1 * f5 - f2 * f6;
   }

   public void conjugate() {
      this.components[0] = -this.components[0];
      this.components[1] = -this.components[1];
      this.components[2] = -this.components[2];
   }
}
