package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextureAtlasSprite {
   private final ResourceLocation iconName;
   protected final int width;
   protected final int height;
   protected NativeImage[] frames;
   @Nullable
   protected int[] framesX;
   @Nullable
   protected int[] framesY;
   protected NativeImage[] interpolatedFrameData;
   private AnimationMetadataSection animationMetadata;
   protected boolean rotated;
   protected int x;
   protected int y;
   private float minU;
   private float maxU;
   private float minV;
   private float maxV;
   protected int frameCounter;
   protected int tickCounter;
   private static final int[] MIPMAP_BUFFER = new int[4];
   private static final float[] COLOR_GAMMAS = Util.make(new float[256], (p_203415_0_) -> {
      for(int i = 0; i < p_203415_0_.length; ++i) {
         p_203415_0_[i] = (float)Math.pow((double)((float)i / 255.0F), 2.2D);
      }

   });

   protected TextureAtlasSprite(ResourceLocation p_i48117_1_, int p_i48117_2_, int p_i48117_3_) {
      this.iconName = p_i48117_1_;
      this.width = p_i48117_2_;
      this.height = p_i48117_3_;
   }

   protected TextureAtlasSprite(ResourceLocation p_i48118_1_, PngSizeInfo p_i48118_2_, @Nullable AnimationMetadataSection p_i48118_3_) {
      this.iconName = p_i48118_1_;
      if (p_i48118_3_ != null) {
         int i = Math.min(p_i48118_2_.width, p_i48118_2_.height);
         this.height = this.width = i;
      } else {
         if (p_i48118_2_.height != p_i48118_2_.width) {
            throw new RuntimeException("broken aspect ratio and not an animation");
         }

         this.width = p_i48118_2_.width;
         this.height = p_i48118_2_.height;
      }

      this.animationMetadata = p_i48118_3_;
   }

   private void generateMipmapsUnchecked(int p_195666_1_) {
      NativeImage[] anativeimage = new NativeImage[p_195666_1_ + 1];
      anativeimage[0] = this.frames[0];
      if (p_195666_1_ > 0) {
         boolean flag = false;

         label71:
         for(int i = 0; i < this.frames[0].getWidth(); ++i) {
            for(int j = 0; j < this.frames[0].getHeight(); ++j) {
               if (this.frames[0].getPixelRGBA(i, j) >> 24 == 0) {
                  flag = true;
                  break label71;
               }
            }
         }

         for(int k1 = 1; k1 <= p_195666_1_; ++k1) {
            if (this.frames.length > k1 && this.frames[k1] != null) {
               anativeimage[k1] = this.frames[k1];
            } else {
               NativeImage nativeimage1 = anativeimage[k1 - 1];
               NativeImage nativeimage = new NativeImage(nativeimage1.getWidth() >> 1, nativeimage1.getHeight() >> 1, false);
               int k = nativeimage.getWidth();
               int l = nativeimage.getHeight();

               for(int i1 = 0; i1 < k; ++i1) {
                  for(int j1 = 0; j1 < l; ++j1) {
                     nativeimage.setPixelRGBA(i1, j1, blendColors(nativeimage1.getPixelRGBA(i1 * 2 + 0, j1 * 2 + 0), nativeimage1.getPixelRGBA(i1 * 2 + 1, j1 * 2 + 0), nativeimage1.getPixelRGBA(i1 * 2 + 0, j1 * 2 + 1), nativeimage1.getPixelRGBA(i1 * 2 + 1, j1 * 2 + 1), flag));
                  }
               }

               anativeimage[k1] = nativeimage;
            }
         }

         for(int l1 = p_195666_1_ + 1; l1 < this.frames.length; ++l1) {
            if (this.frames[l1] != null) {
               this.frames[l1].close();
            }
         }
      }

      this.frames = anativeimage;
   }

   private static int blendColors(int p_195661_0_, int p_195661_1_, int p_195661_2_, int p_195661_3_, boolean p_195661_4_) {
      if (p_195661_4_) {
         MIPMAP_BUFFER[0] = p_195661_0_;
         MIPMAP_BUFFER[1] = p_195661_1_;
         MIPMAP_BUFFER[2] = p_195661_2_;
         MIPMAP_BUFFER[3] = p_195661_3_;
         float f = 0.0F;
         float f1 = 0.0F;
         float f2 = 0.0F;
         float f3 = 0.0F;

         for(int i1 = 0; i1 < 4; ++i1) {
            if (MIPMAP_BUFFER[i1] >> 24 != 0) {
               f += getColorGamma(MIPMAP_BUFFER[i1] >> 24);
               f1 += getColorGamma(MIPMAP_BUFFER[i1] >> 16);
               f2 += getColorGamma(MIPMAP_BUFFER[i1] >> 8);
               f3 += getColorGamma(MIPMAP_BUFFER[i1] >> 0);
            }
         }

         f = f / 4.0F;
         f1 = f1 / 4.0F;
         f2 = f2 / 4.0F;
         f3 = f3 / 4.0F;
         int i2 = (int)(Math.pow((double)f, 0.45454545454545453D) * 255.0D);
         int j1 = (int)(Math.pow((double)f1, 0.45454545454545453D) * 255.0D);
         int k1 = (int)(Math.pow((double)f2, 0.45454545454545453D) * 255.0D);
         int l1 = (int)(Math.pow((double)f3, 0.45454545454545453D) * 255.0D);
         if (i2 < 96) {
            i2 = 0;
         }

         return i2 << 24 | j1 << 16 | k1 << 8 | l1;
      } else {
         int i = blendColorComponent(p_195661_0_, p_195661_1_, p_195661_2_, p_195661_3_, 24);
         int j = blendColorComponent(p_195661_0_, p_195661_1_, p_195661_2_, p_195661_3_, 16);
         int k = blendColorComponent(p_195661_0_, p_195661_1_, p_195661_2_, p_195661_3_, 8);
         int l = blendColorComponent(p_195661_0_, p_195661_1_, p_195661_2_, p_195661_3_, 0);
         return i << 24 | j << 16 | k << 8 | l;
      }
   }

   private static int blendColorComponent(int p_195669_0_, int p_195669_1_, int p_195669_2_, int p_195669_3_, int p_195669_4_) {
      float f = getColorGamma(p_195669_0_ >> p_195669_4_);
      float f1 = getColorGamma(p_195669_1_ >> p_195669_4_);
      float f2 = getColorGamma(p_195669_2_ >> p_195669_4_);
      float f3 = getColorGamma(p_195669_3_ >> p_195669_4_);
      float f4 = (float)((double)((float)Math.pow((double)(f + f1 + f2 + f3) * 0.25D, 0.45454545454545453D)));
      return (int)((double)f4 * 255.0D);
   }

   private static float getColorGamma(int p_195660_0_) {
      return COLOR_GAMMAS[p_195660_0_ & 255];
   }

   private void uploadFrames(int p_195659_1_) {
      int i = 0;
      int j = 0;
      if (this.framesX != null) {
         i = this.framesX[p_195659_1_] * this.width;
         j = this.framesY[p_195659_1_] * this.height;
      }

      this.uploadFrames(i, j, this.frames);
   }

   private void uploadFrames(int p_195667_1_, int p_195667_2_, NativeImage[] p_195667_3_) {
      for(int i = 0; i < this.frames.length; ++i) {
         p_195667_3_[i].uploadTextureSub(i, this.x >> i, this.y >> i, p_195667_1_ >> i, p_195667_2_ >> i, this.width >> i, this.height >> i, this.frames.length > 1);
      }

   }

   public void initSprite(int p_110971_1_, int p_110971_2_, int p_110971_3_, int p_110971_4_, boolean p_110971_5_) {
      this.x = p_110971_3_;
      this.y = p_110971_4_;
      this.rotated = p_110971_5_;
      float f = (float)((double)0.01F / (double)p_110971_1_);
      float f1 = (float)((double)0.01F / (double)p_110971_2_);
      this.minU = (float)p_110971_3_ / (float)((double)p_110971_1_) + f;
      this.maxU = (float)(p_110971_3_ + this.width) / (float)((double)p_110971_1_) - f;
      this.minV = (float)p_110971_4_ / (float)p_110971_2_ + f1;
      this.maxV = (float)(p_110971_4_ + this.height) / (float)p_110971_2_ - f1;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public float getMinU() {
      return this.minU;
   }

   public float getMaxU() {
      return this.maxU;
   }

   public float getInterpolatedU(double p_94214_1_) {
      float f = this.maxU - this.minU;
      return this.minU + f * (float)p_94214_1_ / 16.0F;
   }

   public float getUnInterpolatedU(float p_188537_1_) {
      float f = this.maxU - this.minU;
      return (p_188537_1_ - this.minU) / f * 16.0F;
   }

   public float getMinV() {
      return this.minV;
   }

   public float getMaxV() {
      return this.maxV;
   }

   public float getInterpolatedV(double p_94207_1_) {
      float f = this.maxV - this.minV;
      return this.minV + f * (float)p_94207_1_ / 16.0F;
   }

   public float getUnInterpolatedV(float p_188536_1_) {
      float f = this.maxV - this.minV;
      return (p_188536_1_ - this.minV) / f * 16.0F;
   }

   public ResourceLocation getName() {
      return this.iconName;
   }

   public void updateAnimation() {
      ++this.tickCounter;
      if (this.tickCounter >= this.animationMetadata.getFrameTimeSingle(this.frameCounter)) {
         int i = this.animationMetadata.getFrameIndex(this.frameCounter);
         int j = this.animationMetadata.getFrameCount() == 0 ? this.getFrameCount() : this.animationMetadata.getFrameCount();
         this.frameCounter = (this.frameCounter + 1) % j;
         this.tickCounter = 0;
         int k = this.animationMetadata.getFrameIndex(this.frameCounter);
         if (i != k && k >= 0 && k < this.getFrameCount()) {
            this.uploadFrames(k);
         }
      } else if (this.animationMetadata.isInterpolate()) {
         this.updateAnimationInterpolated();
      }

   }

   private void updateAnimationInterpolated() {
      double d0 = 1.0D - (double)this.tickCounter / (double)this.animationMetadata.getFrameTimeSingle(this.frameCounter);
      int i = this.animationMetadata.getFrameIndex(this.frameCounter);
      int j = this.animationMetadata.getFrameCount() == 0 ? this.getFrameCount() : this.animationMetadata.getFrameCount();
      int k = this.animationMetadata.getFrameIndex((this.frameCounter + 1) % j);
      if (i != k && k >= 0 && k < this.getFrameCount()) {
         if (this.interpolatedFrameData == null || this.interpolatedFrameData.length != this.frames.length) {
            if (this.interpolatedFrameData != null) {
               for(NativeImage nativeimage : this.interpolatedFrameData) {
                  if (nativeimage != null) {
                     nativeimage.close();
                  }
               }
            }

            this.interpolatedFrameData = new NativeImage[this.frames.length];
         }

         for(int j2 = 0; j2 < this.frames.length; ++j2) {
            int k2 = this.width >> j2;
            int l2 = this.height >> j2;
            if (this.interpolatedFrameData[j2] == null) {
               this.interpolatedFrameData[j2] = new NativeImage(k2, l2, false);
            }

            for(int i3 = 0; i3 < l2; ++i3) {
               for(int l = 0; l < k2; ++l) {
                  int i1 = this.func_195665_a(i, j2, l, i3);
                  int j1 = this.func_195665_a(k, j2, l, i3);
                  int k1 = this.interpolateColor(d0, i1 >> 16 & 255, j1 >> 16 & 255);
                  int l1 = this.interpolateColor(d0, i1 >> 8 & 255, j1 >> 8 & 255);
                  int i2 = this.interpolateColor(d0, i1 & 255, j1 & 255);
                  this.interpolatedFrameData[j2].setPixelRGBA(l, i3, i1 & -16777216 | k1 << 16 | l1 << 8 | i2);
               }
            }
         }

         this.uploadFrames(0, 0, this.interpolatedFrameData);
      }

   }

   private int interpolateColor(double p_188535_1_, int p_188535_3_, int p_188535_4_) {
      return (int)(p_188535_1_ * (double)p_188535_3_ + (1.0D - p_188535_1_) * (double)p_188535_4_);
   }

   public int getFrameCount() {
      return this.framesX == null ? 0 : this.framesX.length;
   }

   public void loadSpriteFrames(IResource p_195664_1_, int p_195664_2_) throws IOException {
      NativeImage nativeimage = NativeImage.read(p_195664_1_.getInputStream());
      this.frames = new NativeImage[p_195664_2_];
      this.frames[0] = nativeimage;
      int i;
      if (this.animationMetadata != null && this.animationMetadata.getFrameWidth() != -1) {
         i = nativeimage.getWidth() / this.animationMetadata.getFrameWidth();
      } else {
         i = nativeimage.getWidth() / this.width;
      }

      int j;
      if (this.animationMetadata != null && this.animationMetadata.getFrameHeight() != -1) {
         j = nativeimage.getHeight() / this.animationMetadata.getFrameHeight();
      } else {
         j = nativeimage.getHeight() / this.height;
      }

      if (this.animationMetadata != null && this.animationMetadata.getFrameCount() > 0) {
         int k1 = this.animationMetadata.getFrameIndexSet().stream().max(Integer::compareTo).get() + 1;
         this.framesX = new int[k1];
         this.framesY = new int[k1];
         Arrays.fill(this.framesX, -1);
         Arrays.fill(this.framesY, -1);

         for(int i2 : this.animationMetadata.getFrameIndexSet()) {
            if (i2 >= i * j) {
               throw new RuntimeException("invalid frameindex " + i2);
            }

            int j2 = i2 / i;
            int k2 = i2 % i;
            this.framesX[i2] = k2;
            this.framesY[i2] = j2;
         }
      } else {
         List<AnimationFrame> list = Lists.newArrayList();
         int k = i * j;
         this.framesX = new int[k];
         this.framesY = new int[k];

         for(int l = 0; l < j; ++l) {
            for(int i1 = 0; i1 < i; ++i1) {
               int j1 = l * i + i1;
               this.framesX[j1] = i1;
               this.framesY[j1] = l;
               list.add(new AnimationFrame(j1, -1));
            }
         }

         int l1 = 1;
         boolean flag = false;
         if (this.animationMetadata != null) {
            l1 = this.animationMetadata.getFrameTime();
            flag = this.animationMetadata.isInterpolate();
         }

         this.animationMetadata = new AnimationMetadataSection(list, this.width, this.height, l1, flag);
      }

   }

   public void generateMipmaps(int p_147963_1_) {
      try {
         this.generateMipmapsUnchecked(p_147963_1_);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Generating mipmaps for frame");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Frame being iterated");
         crashreportcategory.addDetail("Frame sizes", () -> {
            StringBuilder stringbuilder = new StringBuilder();

            for(NativeImage nativeimage : this.frames) {
               if (stringbuilder.length() > 0) {
                  stringbuilder.append(", ");
               }

               stringbuilder.append(nativeimage == null ? "null" : nativeimage.getWidth() + "x" + nativeimage.getHeight());
            }

            return stringbuilder.toString();
         });
         throw new ReportedException(crashreport);
      }
   }

   public void clearFramesTextureData() {
      if (this.frames != null) {
         for(NativeImage nativeimage : this.frames) {
            if (nativeimage != null) {
               nativeimage.close();
            }
         }
      }

      this.frames = null;
      if (this.interpolatedFrameData != null) {
         for(NativeImage nativeimage1 : this.interpolatedFrameData) {
            if (nativeimage1 != null) {
               nativeimage1.close();
            }
         }
      }

      this.interpolatedFrameData = null;
   }

   public boolean hasAnimationMetadata() {
      return this.animationMetadata != null && this.animationMetadata.getFrameCount() > 1;
   }

   public String toString() {
      int i = this.framesX == null ? 0 : this.framesX.length;
      return "TextureAtlasSprite{name='" + this.iconName + '\'' + ", frameCount=" + i + ", rotated=" + this.rotated + ", x=" + this.x + ", y=" + this.y + ", height=" + this.height + ", width=" + this.width + ", u0=" + this.minU + ", u1=" + this.maxU + ", v0=" + this.minV + ", v1=" + this.maxV + '}';
   }

   private int func_195665_a(int p_195665_1_, int p_195665_2_, int p_195665_3_, int p_195665_4_) {
      return this.frames[p_195665_2_].getPixelRGBA(p_195665_3_ + (this.framesX[p_195665_1_] * this.width >> p_195665_2_), p_195665_4_ + (this.framesY[p_195665_1_] * this.height >> p_195665_2_));
   }

   public boolean isPixelTransparent(int p_195662_1_, int p_195662_2_, int p_195662_3_) {
      return (this.frames[0].getPixelRGBA(p_195662_2_ + this.framesX[p_195662_1_] * this.width, p_195662_3_ + this.framesY[p_195662_1_] * this.height) >> 24 & 255) == 0;
   }

   public void func_195663_q() {
      this.uploadFrames(0);
   }
}
