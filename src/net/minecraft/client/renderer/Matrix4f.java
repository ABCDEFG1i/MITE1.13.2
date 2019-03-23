package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import java.util.Arrays;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class Matrix4f {
   private final float[] elements;

   public Matrix4f() {
      this.elements = new float[16];
   }

   public Matrix4f(Quaternion p_i48104_1_) {
      this();
      float f = p_i48104_1_.getX();
      float f1 = p_i48104_1_.getY();
      float f2 = p_i48104_1_.getZ();
      float f3 = p_i48104_1_.getW();
      float f4 = 2.0F * f * f;
      float f5 = 2.0F * f1 * f1;
      float f6 = 2.0F * f2 * f2;
      this.elements[0] = 1.0F - f5 - f6;
      this.elements[5] = 1.0F - f6 - f4;
      this.elements[10] = 1.0F - f4 - f5;
      this.elements[15] = 1.0F;
      float f7 = f * f1;
      float f8 = f1 * f2;
      float f9 = f2 * f;
      float f10 = f * f3;
      float f11 = f1 * f3;
      float f12 = f2 * f3;
      this.elements[1] = 2.0F * (f7 + f12);
      this.elements[4] = 2.0F * (f7 - f12);
      this.elements[2] = 2.0F * (f9 - f11);
      this.elements[8] = 2.0F * (f9 + f11);
      this.elements[6] = 2.0F * (f8 + f10);
      this.elements[9] = 2.0F * (f8 - f10);
   }

   public Matrix4f(Matrix4f p_i48105_1_) {
      this.elements = Arrays.copyOf(p_i48105_1_.elements, 16);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         Matrix4f matrix4f = (Matrix4f)p_equals_1_;
         return Arrays.equals(this.elements, matrix4f.elements);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.elements);
   }

   public void read(FloatBuffer p_195874_1_) {
      this.read(p_195874_1_, false);
   }

   public void read(FloatBuffer p_195883_1_, boolean p_195883_2_) {
      if (p_195883_2_) {
         for(int i = 0; i < 4; ++i) {
            for(int j = 0; j < 4; ++j) {
               this.elements[i * 4 + j] = p_195883_1_.get(j * 4 + i);
            }
         }
      } else {
         p_195883_1_.get(this.elements);
      }

   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append("Matrix4f:\n");

      for(int i = 0; i < 4; ++i) {
         for(int j = 0; j < 4; ++j) {
            stringbuilder.append(this.elements[i + j * 4]);
            if (j != 3) {
               stringbuilder.append(" ");
            }
         }

         stringbuilder.append("\n");
      }

      return stringbuilder.toString();
   }

   public void write(FloatBuffer p_195879_1_) {
      this.write(p_195879_1_, false);
   }

   public void write(FloatBuffer p_195873_1_, boolean p_195873_2_) {
      if (p_195873_2_) {
         for(int i = 0; i < 4; ++i) {
            for(int j = 0; j < 4; ++j) {
               p_195873_1_.put(j * 4 + i, this.elements[i * 4 + j]);
            }
         }
      } else {
         p_195873_1_.put(this.elements);
      }

   }

   public void setIdentity() {
      this.elements[0] = 1.0F;
      this.elements[1] = 0.0F;
      this.elements[2] = 0.0F;
      this.elements[3] = 0.0F;
      this.elements[4] = 0.0F;
      this.elements[5] = 1.0F;
      this.elements[6] = 0.0F;
      this.elements[7] = 0.0F;
      this.elements[8] = 0.0F;
      this.elements[9] = 0.0F;
      this.elements[10] = 1.0F;
      this.elements[11] = 0.0F;
      this.elements[12] = 0.0F;
      this.elements[13] = 0.0F;
      this.elements[14] = 0.0F;
      this.elements[15] = 1.0F;
   }

   public float get(int p_195885_1_, int p_195885_2_) {
      return this.elements[p_195885_1_ + 4 * p_195885_2_];
   }

   public void set(int p_195878_1_, int p_195878_2_, float p_195878_3_) {
      this.elements[p_195878_1_ + 4 * p_195878_2_] = p_195878_3_;
   }

   public void mul(Matrix4f p_195882_1_) {
      float[] afloat = Arrays.copyOf(this.elements, 16);

      for(int i = 0; i < 4; ++i) {
         for(int j = 0; j < 4; ++j) {
            this.elements[i + j * 4] = 0.0F;

            for(int k = 0; k < 4; ++k) {
               this.elements[i + j * 4] += afloat[i + k * 4] * p_195882_1_.elements[k + j * 4];
            }
         }
      }

   }

   public void scale(float p_195875_1_) {
      for(int i = 0; i < 16; ++i) {
         this.elements[i] *= p_195875_1_;
      }

   }

   public void add(Matrix4f p_195880_1_) {
      for(int i = 0; i < 16; ++i) {
         this.elements[i] += p_195880_1_.elements[i];
      }

   }

   public void sub(Matrix4f p_195886_1_) {
      for(int i = 0; i < 16; ++i) {
         this.elements[i] -= p_195886_1_.elements[i];
      }

   }

   public float trace() {
      float f = 0.0F;

      for(int i = 0; i < 4; ++i) {
         f += this.elements[i + 4 * i];
      }

      return f;
   }

   public void func_195887_c() {
      Matrix4f matrix4f = new Matrix4f();
      Matrix4f matrix4f1 = new Matrix4f(this);
      Matrix4f matrix4f2 = new Matrix4f(this);
      matrix4f1.mul(this);
      matrix4f2.mul(matrix4f1);
      float f = this.trace();
      float f1 = matrix4f1.trace();
      float f2 = matrix4f2.trace();
      this.scale((f1 - f * f) / 2.0F);
      matrix4f.setIdentity();
      matrix4f.scale((f * f * f - 3.0F * f * f1 + 2.0F * f2) / 6.0F);
      this.add(matrix4f);
      matrix4f1.scale(f);
      this.add(matrix4f1);
      this.sub(matrix4f2);
   }

   public static Matrix4f perspective(double p_195876_0_, float p_195876_2_, float p_195876_3_, float p_195876_4_) {
      float f = (float)(1.0D / Math.tan(p_195876_0_ * (double)((float)Math.PI / 180F) / 2.0D));
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.set(0, 0, f / p_195876_2_);
      matrix4f.set(1, 1, f);
      matrix4f.set(2, 2, (p_195876_4_ + p_195876_3_) / (p_195876_3_ - p_195876_4_));
      matrix4f.set(3, 2, -1.0F);
      matrix4f.set(2, 3, 2.0F * p_195876_4_ * p_195876_3_ / (p_195876_3_ - p_195876_4_));
      return matrix4f;
   }

   public static Matrix4f orthographic(float p_195877_0_, float p_195877_1_, float p_195877_2_, float p_195877_3_) {
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.set(0, 0, 2.0F / p_195877_0_);
      matrix4f.set(1, 1, 2.0F / p_195877_1_);
      float f = p_195877_3_ - p_195877_2_;
      matrix4f.set(2, 2, -2.0F / f);
      matrix4f.set(3, 3, 1.0F);
      matrix4f.set(0, 3, -1.0F);
      matrix4f.set(1, 3, -1.0F);
      matrix4f.set(2, 3, -(p_195877_3_ + p_195877_2_) / f);
      return matrix4f;
   }
}
