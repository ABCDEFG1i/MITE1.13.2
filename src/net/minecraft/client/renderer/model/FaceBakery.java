package net.minecraft.client.renderer.model;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FaceBakery {
   private static final float field_178418_a = 1.0F / (float)Math.cos((double)((float)Math.PI / 8F)) - 1.0F;
   private static final float field_178417_b = 1.0F / (float)Math.cos((double)((float)Math.PI / 4F)) - 1.0F;
   private static final FaceBakery.Rotation[] field_188016_c = new FaceBakery.Rotation[ModelRotation.values().length * EnumFacing.values().length];
   private static final FaceBakery.Rotation field_188017_d = new FaceBakery.Rotation() {
      BlockFaceUV func_188007_a(float p_188007_1_, float p_188007_2_, float p_188007_3_, float p_188007_4_) {
         return new BlockFaceUV(new float[]{p_188007_1_, p_188007_2_, p_188007_3_, p_188007_4_}, 0);
      }
   };
   private static final FaceBakery.Rotation field_188018_e = new FaceBakery.Rotation() {
      BlockFaceUV func_188007_a(float p_188007_1_, float p_188007_2_, float p_188007_3_, float p_188007_4_) {
         return new BlockFaceUV(new float[]{p_188007_4_, 16.0F - p_188007_1_, p_188007_2_, 16.0F - p_188007_3_}, 270);
      }
   };
   private static final FaceBakery.Rotation field_188019_f = new FaceBakery.Rotation() {
      BlockFaceUV func_188007_a(float p_188007_1_, float p_188007_2_, float p_188007_3_, float p_188007_4_) {
         return new BlockFaceUV(new float[]{16.0F - p_188007_1_, 16.0F - p_188007_2_, 16.0F - p_188007_3_, 16.0F - p_188007_4_}, 0);
      }
   };
   private static final FaceBakery.Rotation field_188020_g = new FaceBakery.Rotation() {
      BlockFaceUV func_188007_a(float p_188007_1_, float p_188007_2_, float p_188007_3_, float p_188007_4_) {
         return new BlockFaceUV(new float[]{16.0F - p_188007_2_, p_188007_3_, 16.0F - p_188007_4_, p_188007_1_}, 90);
      }
   };

   public BakedQuad func_199332_a(Vector3f p_199332_1_, Vector3f p_199332_2_, BlockPartFace p_199332_3_, TextureAtlasSprite p_199332_4_, EnumFacing p_199332_5_, ModelRotation p_199332_6_, @Nullable BlockPartRotation p_199332_7_, boolean p_199332_8_, boolean p_199332_9_) {
      BlockFaceUV blockfaceuv = p_199332_3_.field_178243_e;
      if (p_199332_8_) {
         blockfaceuv = this.func_188010_a(p_199332_3_.field_178243_e, p_199332_5_, p_199332_6_);
      }

      int[] aint = this.func_188012_a(blockfaceuv, p_199332_4_, p_199332_5_, this.func_199337_a(p_199332_1_, p_199332_2_), p_199332_6_, p_199332_7_, p_199332_9_);
      EnumFacing enumfacing = func_178410_a(aint);
      if (p_199332_7_ == null) {
         this.func_178408_a(aint, enumfacing);
      }

      return new BakedQuad(aint, p_199332_3_.field_178245_c, enumfacing, p_199332_4_);
   }

   private BlockFaceUV func_188010_a(BlockFaceUV p_188010_1_, EnumFacing p_188010_2_, ModelRotation p_188010_3_) {
      return field_188016_c[func_188014_a(p_188010_3_, p_188010_2_)].func_188006_a(p_188010_1_);
   }

   private int[] func_188012_a(BlockFaceUV p_188012_1_, TextureAtlasSprite p_188012_2_, EnumFacing p_188012_3_, float[] p_188012_4_, ModelRotation p_188012_5_, @Nullable BlockPartRotation p_188012_6_, boolean p_188012_7_) {
      int[] aint = new int[28];

      for(int i = 0; i < 4; ++i) {
         this.func_188015_a(aint, i, p_188012_3_, p_188012_1_, p_188012_4_, p_188012_2_, p_188012_5_, p_188012_6_, p_188012_7_);
      }

      return aint;
   }

   private int func_178413_a(EnumFacing p_178413_1_) {
      float f = this.func_178412_b(p_178413_1_);
      int i = MathHelper.clamp((int)(f * 255.0F), 0, 255);
      return -16777216 | i << 16 | i << 8 | i;
   }

   private float func_178412_b(EnumFacing p_178412_1_) {
      switch(p_178412_1_) {
      case DOWN:
         return 0.5F;
      case UP:
         return 1.0F;
      case NORTH:
      case SOUTH:
         return 0.8F;
      case WEST:
      case EAST:
         return 0.6F;
      default:
         return 1.0F;
      }
   }

   private float[] func_199337_a(Vector3f p_199337_1_, Vector3f p_199337_2_) {
      float[] afloat = new float[EnumFacing.values().length];
      afloat[EnumFaceDirection.Constants.WEST_INDEX] = p_199337_1_.getX() / 16.0F;
      afloat[EnumFaceDirection.Constants.DOWN_INDEX] = p_199337_1_.getY() / 16.0F;
      afloat[EnumFaceDirection.Constants.NORTH_INDEX] = p_199337_1_.getZ() / 16.0F;
      afloat[EnumFaceDirection.Constants.EAST_INDEX] = p_199337_2_.getX() / 16.0F;
      afloat[EnumFaceDirection.Constants.UP_INDEX] = p_199337_2_.getY() / 16.0F;
      afloat[EnumFaceDirection.Constants.SOUTH_INDEX] = p_199337_2_.getZ() / 16.0F;
      return afloat;
   }

   private void func_188015_a(int[] p_188015_1_, int p_188015_2_, EnumFacing p_188015_3_, BlockFaceUV p_188015_4_, float[] p_188015_5_, TextureAtlasSprite p_188015_6_, ModelRotation p_188015_7_, @Nullable BlockPartRotation p_188015_8_, boolean p_188015_9_) {
      EnumFacing enumfacing = p_188015_7_.func_177523_a(p_188015_3_);
      int i = p_188015_9_ ? this.func_178413_a(enumfacing) : -1;
      EnumFaceDirection.VertexInformation enumfacedirection$vertexinformation = EnumFaceDirection.getFacing(p_188015_3_).getVertexInformation(p_188015_2_);
      Vector3f vector3f = new Vector3f(p_188015_5_[enumfacedirection$vertexinformation.xIndex], p_188015_5_[enumfacedirection$vertexinformation.yIndex], p_188015_5_[enumfacedirection$vertexinformation.zIndex]);
      this.func_199336_a(vector3f, p_188015_8_);
      int j = this.func_199335_a(vector3f, p_188015_3_, p_188015_2_, p_188015_7_);
      this.func_199333_a(p_188015_1_, j, p_188015_2_, vector3f, i, p_188015_6_, p_188015_4_);
   }

   private void func_199333_a(int[] p_199333_1_, int p_199333_2_, int p_199333_3_, Vector3f p_199333_4_, int p_199333_5_, TextureAtlasSprite p_199333_6_, BlockFaceUV p_199333_7_) {
      int i = p_199333_2_ * 7;
      p_199333_1_[i] = Float.floatToRawIntBits(p_199333_4_.getX());
      p_199333_1_[i + 1] = Float.floatToRawIntBits(p_199333_4_.getY());
      p_199333_1_[i + 2] = Float.floatToRawIntBits(p_199333_4_.getZ());
      p_199333_1_[i + 3] = p_199333_5_;
      p_199333_1_[i + 4] = Float.floatToRawIntBits(p_199333_6_.getInterpolatedU((double)p_199333_7_.func_178348_a(p_199333_3_)));
      p_199333_1_[i + 4 + 1] = Float.floatToRawIntBits(p_199333_6_.getInterpolatedV((double)p_199333_7_.func_178346_b(p_199333_3_)));
   }

   private void func_199336_a(Vector3f p_199336_1_, @Nullable BlockPartRotation p_199336_2_) {
      if (p_199336_2_ != null) {
         Vector3f vector3f;
         Vector3f vector3f1;
         switch(p_199336_2_.field_178342_b) {
         case X:
            vector3f = new Vector3f(1.0F, 0.0F, 0.0F);
            vector3f1 = new Vector3f(0.0F, 1.0F, 1.0F);
            break;
         case Y:
            vector3f = new Vector3f(0.0F, 1.0F, 0.0F);
            vector3f1 = new Vector3f(1.0F, 0.0F, 1.0F);
            break;
         case Z:
            vector3f = new Vector3f(0.0F, 0.0F, 1.0F);
            vector3f1 = new Vector3f(1.0F, 1.0F, 0.0F);
            break;
         default:
            throw new IllegalArgumentException("There are only 3 axes");
         }

         Quaternion quaternion = new Quaternion(vector3f, p_199336_2_.field_178343_c, true);
         if (p_199336_2_.field_178341_d) {
            if (Math.abs(p_199336_2_.field_178343_c) == 22.5F) {
               vector3f1.mul(field_178418_a);
            } else {
               vector3f1.mul(field_178417_b);
            }

            vector3f1.add(1.0F, 1.0F, 1.0F);
         } else {
            vector3f1.set(1.0F, 1.0F, 1.0F);
         }

         this.func_199334_a(p_199336_1_, new Vector3f(p_199336_2_.field_178344_a), quaternion, vector3f1);
      }
   }

   public int func_199335_a(Vector3f p_199335_1_, EnumFacing p_199335_2_, int p_199335_3_, ModelRotation p_199335_4_) {
      if (p_199335_4_ == ModelRotation.X0_Y0) {
         return p_199335_3_;
      } else {
         this.func_199334_a(p_199335_1_, new Vector3f(0.5F, 0.5F, 0.5F), p_199335_4_.func_195820_a(), new Vector3f(1.0F, 1.0F, 1.0F));
         return p_199335_4_.func_177520_a(p_199335_2_, p_199335_3_);
      }
   }

   private void func_199334_a(Vector3f p_199334_1_, Vector3f p_199334_2_, Quaternion p_199334_3_, Vector3f p_199334_4_) {
      Vector4f vector4f = new Vector4f(p_199334_1_.getX() - p_199334_2_.getX(), p_199334_1_.getY() - p_199334_2_.getY(), p_199334_1_.getZ() - p_199334_2_.getZ(), 1.0F);
      vector4f.func_195912_a(p_199334_3_);
      vector4f.scale(p_199334_4_);
      p_199334_1_.set(vector4f.getX() + p_199334_2_.getX(), vector4f.getY() + p_199334_2_.getY(), vector4f.getZ() + p_199334_2_.getZ());
   }

   public static EnumFacing func_178410_a(int[] p_178410_0_) {
      Vector3f vector3f = new Vector3f(Float.intBitsToFloat(p_178410_0_[0]), Float.intBitsToFloat(p_178410_0_[1]), Float.intBitsToFloat(p_178410_0_[2]));
      Vector3f vector3f1 = new Vector3f(Float.intBitsToFloat(p_178410_0_[7]), Float.intBitsToFloat(p_178410_0_[8]), Float.intBitsToFloat(p_178410_0_[9]));
      Vector3f vector3f2 = new Vector3f(Float.intBitsToFloat(p_178410_0_[14]), Float.intBitsToFloat(p_178410_0_[15]), Float.intBitsToFloat(p_178410_0_[16]));
      Vector3f vector3f3 = new Vector3f(vector3f);
      vector3f3.sub(vector3f1);
      Vector3f vector3f4 = new Vector3f(vector3f2);
      vector3f4.sub(vector3f1);
      Vector3f vector3f5 = new Vector3f(vector3f4);
      vector3f5.cross(vector3f3);
      vector3f5.normalize();
      EnumFacing enumfacing = null;
      float f = 0.0F;

      for(EnumFacing enumfacing1 : EnumFacing.values()) {
         Vec3i vec3i = enumfacing1.getDirectionVec();
         Vector3f vector3f6 = new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
         float f1 = vector3f5.dot(vector3f6);
         if (f1 >= 0.0F && f1 > f) {
            f = f1;
            enumfacing = enumfacing1;
         }
      }

      if (enumfacing == null) {
         return EnumFacing.UP;
      } else {
         return enumfacing;
      }
   }

   private void func_178408_a(int[] p_178408_1_, EnumFacing p_178408_2_) {
      int[] aint = new int[p_178408_1_.length];
      System.arraycopy(p_178408_1_, 0, aint, 0, p_178408_1_.length);
      float[] afloat = new float[EnumFacing.values().length];
      afloat[EnumFaceDirection.Constants.WEST_INDEX] = 999.0F;
      afloat[EnumFaceDirection.Constants.DOWN_INDEX] = 999.0F;
      afloat[EnumFaceDirection.Constants.NORTH_INDEX] = 999.0F;
      afloat[EnumFaceDirection.Constants.EAST_INDEX] = -999.0F;
      afloat[EnumFaceDirection.Constants.UP_INDEX] = -999.0F;
      afloat[EnumFaceDirection.Constants.SOUTH_INDEX] = -999.0F;

      for(int i = 0; i < 4; ++i) {
         int j = 7 * i;
         float f = Float.intBitsToFloat(aint[j]);
         float f1 = Float.intBitsToFloat(aint[j + 1]);
         float f2 = Float.intBitsToFloat(aint[j + 2]);
         if (f < afloat[EnumFaceDirection.Constants.WEST_INDEX]) {
            afloat[EnumFaceDirection.Constants.WEST_INDEX] = f;
         }

         if (f1 < afloat[EnumFaceDirection.Constants.DOWN_INDEX]) {
            afloat[EnumFaceDirection.Constants.DOWN_INDEX] = f1;
         }

         if (f2 < afloat[EnumFaceDirection.Constants.NORTH_INDEX]) {
            afloat[EnumFaceDirection.Constants.NORTH_INDEX] = f2;
         }

         if (f > afloat[EnumFaceDirection.Constants.EAST_INDEX]) {
            afloat[EnumFaceDirection.Constants.EAST_INDEX] = f;
         }

         if (f1 > afloat[EnumFaceDirection.Constants.UP_INDEX]) {
            afloat[EnumFaceDirection.Constants.UP_INDEX] = f1;
         }

         if (f2 > afloat[EnumFaceDirection.Constants.SOUTH_INDEX]) {
            afloat[EnumFaceDirection.Constants.SOUTH_INDEX] = f2;
         }
      }

      EnumFaceDirection enumfacedirection = EnumFaceDirection.getFacing(p_178408_2_);

      for(int i1 = 0; i1 < 4; ++i1) {
         int j1 = 7 * i1;
         EnumFaceDirection.VertexInformation enumfacedirection$vertexinformation = enumfacedirection.getVertexInformation(i1);
         float f8 = afloat[enumfacedirection$vertexinformation.xIndex];
         float f3 = afloat[enumfacedirection$vertexinformation.yIndex];
         float f4 = afloat[enumfacedirection$vertexinformation.zIndex];
         p_178408_1_[j1] = Float.floatToRawIntBits(f8);
         p_178408_1_[j1 + 1] = Float.floatToRawIntBits(f3);
         p_178408_1_[j1 + 2] = Float.floatToRawIntBits(f4);

         for(int k = 0; k < 4; ++k) {
            int l = 7 * k;
            float f5 = Float.intBitsToFloat(aint[l]);
            float f6 = Float.intBitsToFloat(aint[l + 1]);
            float f7 = Float.intBitsToFloat(aint[l + 2]);
            if (MathHelper.epsilonEquals(f8, f5) && MathHelper.epsilonEquals(f3, f6) && MathHelper.epsilonEquals(f4, f7)) {
               p_178408_1_[j1 + 4] = aint[l + 4];
               p_178408_1_[j1 + 4 + 1] = aint[l + 4 + 1];
            }
         }
      }

   }

   private static void func_188013_a(ModelRotation p_188013_0_, EnumFacing p_188013_1_, FaceBakery.Rotation p_188013_2_) {
      field_188016_c[func_188014_a(p_188013_0_, p_188013_1_)] = p_188013_2_;
   }

   private static int func_188014_a(ModelRotation p_188014_0_, EnumFacing p_188014_1_) {
      return ModelRotation.values().length * p_188014_1_.ordinal() + p_188014_0_.ordinal();
   }

   static {
      func_188013_a(ModelRotation.X0_Y0, EnumFacing.DOWN, field_188017_d);
      func_188013_a(ModelRotation.X0_Y0, EnumFacing.EAST, field_188017_d);
      func_188013_a(ModelRotation.X0_Y0, EnumFacing.NORTH, field_188017_d);
      func_188013_a(ModelRotation.X0_Y0, EnumFacing.SOUTH, field_188017_d);
      func_188013_a(ModelRotation.X0_Y0, EnumFacing.UP, field_188017_d);
      func_188013_a(ModelRotation.X0_Y0, EnumFacing.WEST, field_188017_d);
      func_188013_a(ModelRotation.X0_Y90, EnumFacing.EAST, field_188017_d);
      func_188013_a(ModelRotation.X0_Y90, EnumFacing.NORTH, field_188017_d);
      func_188013_a(ModelRotation.X0_Y90, EnumFacing.SOUTH, field_188017_d);
      func_188013_a(ModelRotation.X0_Y90, EnumFacing.WEST, field_188017_d);
      func_188013_a(ModelRotation.X0_Y180, EnumFacing.EAST, field_188017_d);
      func_188013_a(ModelRotation.X0_Y180, EnumFacing.NORTH, field_188017_d);
      func_188013_a(ModelRotation.X0_Y180, EnumFacing.SOUTH, field_188017_d);
      func_188013_a(ModelRotation.X0_Y180, EnumFacing.WEST, field_188017_d);
      func_188013_a(ModelRotation.X0_Y270, EnumFacing.EAST, field_188017_d);
      func_188013_a(ModelRotation.X0_Y270, EnumFacing.NORTH, field_188017_d);
      func_188013_a(ModelRotation.X0_Y270, EnumFacing.SOUTH, field_188017_d);
      func_188013_a(ModelRotation.X0_Y270, EnumFacing.WEST, field_188017_d);
      func_188013_a(ModelRotation.X90_Y0, EnumFacing.DOWN, field_188017_d);
      func_188013_a(ModelRotation.X90_Y0, EnumFacing.SOUTH, field_188017_d);
      func_188013_a(ModelRotation.X90_Y90, EnumFacing.DOWN, field_188017_d);
      func_188013_a(ModelRotation.X90_Y180, EnumFacing.DOWN, field_188017_d);
      func_188013_a(ModelRotation.X90_Y180, EnumFacing.NORTH, field_188017_d);
      func_188013_a(ModelRotation.X90_Y270, EnumFacing.DOWN, field_188017_d);
      func_188013_a(ModelRotation.X180_Y0, EnumFacing.DOWN, field_188017_d);
      func_188013_a(ModelRotation.X180_Y0, EnumFacing.UP, field_188017_d);
      func_188013_a(ModelRotation.X270_Y0, EnumFacing.SOUTH, field_188017_d);
      func_188013_a(ModelRotation.X270_Y0, EnumFacing.UP, field_188017_d);
      func_188013_a(ModelRotation.X270_Y90, EnumFacing.UP, field_188017_d);
      func_188013_a(ModelRotation.X270_Y180, EnumFacing.NORTH, field_188017_d);
      func_188013_a(ModelRotation.X270_Y180, EnumFacing.UP, field_188017_d);
      func_188013_a(ModelRotation.X270_Y270, EnumFacing.UP, field_188017_d);
      func_188013_a(ModelRotation.X0_Y270, EnumFacing.UP, field_188018_e);
      func_188013_a(ModelRotation.X0_Y90, EnumFacing.DOWN, field_188018_e);
      func_188013_a(ModelRotation.X90_Y0, EnumFacing.WEST, field_188018_e);
      func_188013_a(ModelRotation.X90_Y90, EnumFacing.WEST, field_188018_e);
      func_188013_a(ModelRotation.X90_Y180, EnumFacing.WEST, field_188018_e);
      func_188013_a(ModelRotation.X90_Y270, EnumFacing.NORTH, field_188018_e);
      func_188013_a(ModelRotation.X90_Y270, EnumFacing.SOUTH, field_188018_e);
      func_188013_a(ModelRotation.X90_Y270, EnumFacing.WEST, field_188018_e);
      func_188013_a(ModelRotation.X180_Y90, EnumFacing.UP, field_188018_e);
      func_188013_a(ModelRotation.X180_Y270, EnumFacing.DOWN, field_188018_e);
      func_188013_a(ModelRotation.X270_Y0, EnumFacing.EAST, field_188018_e);
      func_188013_a(ModelRotation.X270_Y90, EnumFacing.EAST, field_188018_e);
      func_188013_a(ModelRotation.X270_Y90, EnumFacing.NORTH, field_188018_e);
      func_188013_a(ModelRotation.X270_Y90, EnumFacing.SOUTH, field_188018_e);
      func_188013_a(ModelRotation.X270_Y180, EnumFacing.EAST, field_188018_e);
      func_188013_a(ModelRotation.X270_Y270, EnumFacing.EAST, field_188018_e);
      func_188013_a(ModelRotation.X0_Y180, EnumFacing.DOWN, field_188019_f);
      func_188013_a(ModelRotation.X0_Y180, EnumFacing.UP, field_188019_f);
      func_188013_a(ModelRotation.X90_Y0, EnumFacing.NORTH, field_188019_f);
      func_188013_a(ModelRotation.X90_Y0, EnumFacing.UP, field_188019_f);
      func_188013_a(ModelRotation.X90_Y90, EnumFacing.UP, field_188019_f);
      func_188013_a(ModelRotation.X90_Y180, EnumFacing.SOUTH, field_188019_f);
      func_188013_a(ModelRotation.X90_Y180, EnumFacing.UP, field_188019_f);
      func_188013_a(ModelRotation.X90_Y270, EnumFacing.UP, field_188019_f);
      func_188013_a(ModelRotation.X180_Y0, EnumFacing.EAST, field_188019_f);
      func_188013_a(ModelRotation.X180_Y0, EnumFacing.NORTH, field_188019_f);
      func_188013_a(ModelRotation.X180_Y0, EnumFacing.SOUTH, field_188019_f);
      func_188013_a(ModelRotation.X180_Y0, EnumFacing.WEST, field_188019_f);
      func_188013_a(ModelRotation.X180_Y90, EnumFacing.EAST, field_188019_f);
      func_188013_a(ModelRotation.X180_Y90, EnumFacing.NORTH, field_188019_f);
      func_188013_a(ModelRotation.X180_Y90, EnumFacing.SOUTH, field_188019_f);
      func_188013_a(ModelRotation.X180_Y90, EnumFacing.WEST, field_188019_f);
      func_188013_a(ModelRotation.X180_Y180, EnumFacing.DOWN, field_188019_f);
      func_188013_a(ModelRotation.X180_Y180, EnumFacing.EAST, field_188019_f);
      func_188013_a(ModelRotation.X180_Y180, EnumFacing.NORTH, field_188019_f);
      func_188013_a(ModelRotation.X180_Y180, EnumFacing.SOUTH, field_188019_f);
      func_188013_a(ModelRotation.X180_Y180, EnumFacing.UP, field_188019_f);
      func_188013_a(ModelRotation.X180_Y180, EnumFacing.WEST, field_188019_f);
      func_188013_a(ModelRotation.X180_Y270, EnumFacing.EAST, field_188019_f);
      func_188013_a(ModelRotation.X180_Y270, EnumFacing.NORTH, field_188019_f);
      func_188013_a(ModelRotation.X180_Y270, EnumFacing.SOUTH, field_188019_f);
      func_188013_a(ModelRotation.X180_Y270, EnumFacing.WEST, field_188019_f);
      func_188013_a(ModelRotation.X270_Y0, EnumFacing.DOWN, field_188019_f);
      func_188013_a(ModelRotation.X270_Y0, EnumFacing.NORTH, field_188019_f);
      func_188013_a(ModelRotation.X270_Y90, EnumFacing.DOWN, field_188019_f);
      func_188013_a(ModelRotation.X270_Y180, EnumFacing.DOWN, field_188019_f);
      func_188013_a(ModelRotation.X270_Y180, EnumFacing.SOUTH, field_188019_f);
      func_188013_a(ModelRotation.X270_Y270, EnumFacing.DOWN, field_188019_f);
      func_188013_a(ModelRotation.X0_Y90, EnumFacing.UP, field_188020_g);
      func_188013_a(ModelRotation.X0_Y270, EnumFacing.DOWN, field_188020_g);
      func_188013_a(ModelRotation.X90_Y0, EnumFacing.EAST, field_188020_g);
      func_188013_a(ModelRotation.X90_Y90, EnumFacing.EAST, field_188020_g);
      func_188013_a(ModelRotation.X90_Y90, EnumFacing.NORTH, field_188020_g);
      func_188013_a(ModelRotation.X90_Y90, EnumFacing.SOUTH, field_188020_g);
      func_188013_a(ModelRotation.X90_Y180, EnumFacing.EAST, field_188020_g);
      func_188013_a(ModelRotation.X90_Y270, EnumFacing.EAST, field_188020_g);
      func_188013_a(ModelRotation.X270_Y0, EnumFacing.WEST, field_188020_g);
      func_188013_a(ModelRotation.X180_Y90, EnumFacing.DOWN, field_188020_g);
      func_188013_a(ModelRotation.X180_Y270, EnumFacing.UP, field_188020_g);
      func_188013_a(ModelRotation.X270_Y90, EnumFacing.WEST, field_188020_g);
      func_188013_a(ModelRotation.X270_Y180, EnumFacing.WEST, field_188020_g);
      func_188013_a(ModelRotation.X270_Y270, EnumFacing.NORTH, field_188020_g);
      func_188013_a(ModelRotation.X270_Y270, EnumFacing.SOUTH, field_188020_g);
      func_188013_a(ModelRotation.X270_Y270, EnumFacing.WEST, field_188020_g);
   }

   @OnlyIn(Dist.CLIENT)
   abstract static class Rotation {
      private Rotation() {
      }

      public BlockFaceUV func_188006_a(BlockFaceUV p_188006_1_) {
         float f = p_188006_1_.func_178348_a(p_188006_1_.func_178345_c(0));
         float f1 = p_188006_1_.func_178346_b(p_188006_1_.func_178345_c(0));
         float f2 = p_188006_1_.func_178348_a(p_188006_1_.func_178345_c(2));
         float f3 = p_188006_1_.func_178346_b(p_188006_1_.func_178345_c(2));
         return this.func_188007_a(f, f1, f2, f3);
      }

      abstract BlockFaceUV func_188007_a(float p_188007_1_, float p_188007_2_, float p_188007_3_, float p_188007_4_);
   }
}
