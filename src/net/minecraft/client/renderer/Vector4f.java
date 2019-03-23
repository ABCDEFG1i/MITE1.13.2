package net.minecraft.client.renderer;

import java.util.Arrays;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Vector4f {
   private final float[] components;

   public Vector4f(Vector4f p_i48095_1_) {
      this.components = Arrays.copyOf(p_i48095_1_.components, 4);
   }

   public Vector4f() {
      this.components = new float[4];
   }

   public Vector4f(float p_i48096_1_, float p_i48096_2_, float p_i48096_3_, float p_i48096_4_) {
      this.components = new float[]{p_i48096_1_, p_i48096_2_, p_i48096_3_, p_i48096_4_};
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         Vector4f vector4f = (Vector4f)p_equals_1_;
         return Arrays.equals(this.components, vector4f.components);
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

   public float getW() {
      return this.components[3];
   }

   public void scale(Vector3f p_195909_1_) {
      this.components[0] *= p_195909_1_.getX();
      this.components[1] *= p_195909_1_.getY();
      this.components[2] *= p_195909_1_.getZ();
   }

   public void set(float p_195911_1_, float p_195911_2_, float p_195911_3_, float p_195911_4_) {
      this.components[0] = p_195911_1_;
      this.components[1] = p_195911_2_;
      this.components[2] = p_195911_3_;
      this.components[3] = p_195911_4_;
   }

   public void func_195908_a(Matrix4f p_195908_1_) {
      float[] afloat = Arrays.copyOf(this.components, 4);

      for(int i = 0; i < 4; ++i) {
         this.components[i] = 0.0F;

         for(int j = 0; j < 4; ++j) {
            this.components[i] += p_195908_1_.get(i, j) * afloat[j];
         }
      }

   }

   public void func_195912_a(Quaternion p_195912_1_) {
      Quaternion quaternion = new Quaternion(p_195912_1_);
      quaternion.multiply(new Quaternion(this.getX(), this.getY(), this.getZ(), 0.0F));
      Quaternion quaternion1 = new Quaternion(p_195912_1_);
      quaternion1.conjugate();
      quaternion.multiply(quaternion1);
      this.set(quaternion.getX(), quaternion.getY(), quaternion.getZ(), this.getW());
   }
}
