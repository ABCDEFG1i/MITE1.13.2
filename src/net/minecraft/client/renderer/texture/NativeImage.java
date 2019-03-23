package net.minecraft.client.renderer.texture;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.LWJGLMemoryUntracker;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.lwjgl.stb.STBIWriteCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public final class NativeImage implements AutoCloseable {
   private static final Set<StandardOpenOption> OPEN_OPTIONS = EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
   private final NativeImage.PixelFormat pixelFormat;
   private final int width;
   private final int height;
   private final boolean stbiPointer;
   private long imagePointer;
   private final int size;

   public NativeImage(int p_i48122_1_, int p_i48122_2_, boolean p_i48122_3_) {
      this(NativeImage.PixelFormat.RGBA, p_i48122_1_, p_i48122_2_, p_i48122_3_);
   }

   public NativeImage(NativeImage.PixelFormat p_i49763_1_, int p_i49763_2_, int p_i49763_3_, boolean p_i49763_4_) {
      this.pixelFormat = p_i49763_1_;
      this.width = p_i49763_2_;
      this.height = p_i49763_3_;
      this.size = p_i49763_2_ * p_i49763_3_ * p_i49763_1_.getPixelSize();
      this.stbiPointer = false;
      if (p_i49763_4_) {
         this.imagePointer = MemoryUtil.nmemCalloc(1L, (long)this.size);
      } else {
         this.imagePointer = MemoryUtil.nmemAlloc((long)this.size);
      }

   }

   private NativeImage(NativeImage.PixelFormat p_i49764_1_, int p_i49764_2_, int p_i49764_3_, boolean p_i49764_4_, long p_i49764_5_) {
      this.pixelFormat = p_i49764_1_;
      this.width = p_i49764_2_;
      this.height = p_i49764_3_;
      this.stbiPointer = p_i49764_4_;
      this.imagePointer = p_i49764_5_;
      this.size = p_i49764_2_ * p_i49764_3_ * p_i49764_1_.getPixelSize();
   }

   public String toString() {
      return "NativeImage[" + this.pixelFormat + " " + this.width + "x" + this.height + "@" + this.imagePointer + (this.stbiPointer ? "S" : "N") + "]";
   }

   public static NativeImage read(InputStream p_195713_0_) throws IOException {
      return read(NativeImage.PixelFormat.RGBA, p_195713_0_);
   }

   public static NativeImage read(@Nullable NativeImage.PixelFormat p_211679_0_, InputStream p_211679_1_) throws IOException {
      ByteBuffer bytebuffer = null;

      NativeImage nativeimage;
      try {
         bytebuffer = TextureUtil.readToNativeBuffer(p_211679_1_);
         bytebuffer.rewind();
         nativeimage = read(p_211679_0_, bytebuffer);
      } finally {
         MemoryUtil.memFree(bytebuffer);
         IOUtils.closeQuietly(p_211679_1_);
      }

      return nativeimage;
   }

   public static NativeImage read(ByteBuffer p_195704_0_) throws IOException {
      return read(NativeImage.PixelFormat.RGBA, p_195704_0_);
   }

   public static NativeImage read(@Nullable NativeImage.PixelFormat p_211677_0_, ByteBuffer p_211677_1_) throws IOException {
      if (p_211677_0_ != null && !p_211677_0_.isSerializable()) {
         throw new UnsupportedOperationException("Don't know how to read format " + p_211677_0_);
      } else if (MemoryUtil.memAddress(p_211677_1_) == 0L) {
         throw new IllegalArgumentException("Invalid buffer");
      } else {
         NativeImage nativeimage;
         try (MemoryStack memorystack = MemoryStack.stackPush()) {
            IntBuffer intbuffer = memorystack.mallocInt(1);
            IntBuffer intbuffer1 = memorystack.mallocInt(1);
            IntBuffer intbuffer2 = memorystack.mallocInt(1);
            ByteBuffer bytebuffer = STBImage.stbi_load_from_memory(p_211677_1_, intbuffer, intbuffer1, intbuffer2, p_211677_0_ == null ? 0 : p_211677_0_.pixelSize);
            if (bytebuffer == null) {
               throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
            }

            nativeimage = new NativeImage(p_211677_0_ == null ? NativeImage.PixelFormat.fromChannelCount(intbuffer2.get(0)) : p_211677_0_, intbuffer.get(0), intbuffer1.get(0), true, MemoryUtil.memAddress(bytebuffer));
         }

         return nativeimage;
      }
   }

   private static void setWrapST(boolean p_195707_0_) {
      if (p_195707_0_) {
         GlStateManager.texParameteri(3553, 10242, 10496);
         GlStateManager.texParameteri(3553, 10243, 10496);
      } else {
         GlStateManager.texParameteri(3553, 10242, 10497);
         GlStateManager.texParameteri(3553, 10243, 10497);
      }

   }

   private static void setMinMagFilters(boolean p_195705_0_, boolean p_195705_1_) {
      if (p_195705_0_) {
         GlStateManager.texParameteri(3553, 10241, p_195705_1_ ? 9987 : 9729);
         GlStateManager.texParameteri(3553, 10240, 9729);
      } else {
         GlStateManager.texParameteri(3553, 10241, p_195705_1_ ? 9986 : 9728);
         GlStateManager.texParameteri(3553, 10240, 9728);
      }

   }

   private void checkImage() {
      if (this.imagePointer == 0L) {
         throw new IllegalStateException("Image is not allocated.");
      }
   }

   public void close() {
      if (this.imagePointer != 0L) {
         if (this.stbiPointer) {
            STBImage.nstbi_image_free(this.imagePointer);
         } else {
            MemoryUtil.nmemFree(this.imagePointer);
         }
      }

      this.imagePointer = 0L;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public NativeImage.PixelFormat getFormat() {
      return this.pixelFormat;
   }

   public int getPixelRGBA(int p_195709_1_, int p_195709_2_) {
      if (this.pixelFormat != NativeImage.PixelFormat.RGBA) {
         throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.pixelFormat));
      } else if (p_195709_1_ <= this.width && p_195709_2_ <= this.height) {
         this.checkImage();
         return MemoryUtil.memIntBuffer(this.imagePointer, this.size).get(p_195709_1_ + p_195709_2_ * this.width);
      } else {
         throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", p_195709_1_, p_195709_2_, this.width, this.height));
      }
   }

   public void setPixelRGBA(int p_195700_1_, int p_195700_2_, int p_195700_3_) {
      if (this.pixelFormat != NativeImage.PixelFormat.RGBA) {
         throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.pixelFormat));
      } else if (p_195700_1_ <= this.width && p_195700_2_ <= this.height) {
         this.checkImage();
         MemoryUtil.memIntBuffer(this.imagePointer, this.size).put(p_195700_1_ + p_195700_2_ * this.width, p_195700_3_);
      } else {
         throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", p_195700_1_, p_195700_2_, this.width, this.height));
      }
   }

   public byte getPixelLuminanceOrAlpha(int p_211675_1_, int p_211675_2_) {
      if (!this.pixelFormat.hasLuminanceOrAlpha()) {
         throw new IllegalArgumentException(String.format("no luminance or alpha in %s", this.pixelFormat));
      } else if (p_211675_1_ <= this.width && p_211675_2_ <= this.height) {
         return MemoryUtil.memByteBuffer(this.imagePointer, this.size).get((p_211675_1_ + p_211675_2_ * this.width) * this.pixelFormat.getPixelSize() + this.pixelFormat.func_211647_v() / 8);
      } else {
         throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", p_211675_1_, p_211675_2_, this.width, this.height));
      }
   }

   public void blendPixel(int p_195718_1_, int p_195718_2_, int p_195718_3_) {
      if (this.pixelFormat != NativeImage.PixelFormat.RGBA) {
         throw new UnsupportedOperationException("Can only call blendPixel with RGBA format");
      } else {
         int i = this.getPixelRGBA(p_195718_1_, p_195718_2_);
         float f = (float)(p_195718_3_ >> 24 & 255) / 255.0F;
         float f1 = (float)(p_195718_3_ >> 16 & 255) / 255.0F;
         float f2 = (float)(p_195718_3_ >> 8 & 255) / 255.0F;
         float f3 = (float)(p_195718_3_ >> 0 & 255) / 255.0F;
         float f4 = (float)(i >> 24 & 255) / 255.0F;
         float f5 = (float)(i >> 16 & 255) / 255.0F;
         float f6 = (float)(i >> 8 & 255) / 255.0F;
         float f7 = (float)(i >> 0 & 255) / 255.0F;
         float f8 = 1.0F - f;
         float f9 = f * f + f4 * f8;
         float f10 = f1 * f + f5 * f8;
         float f11 = f2 * f + f6 * f8;
         float f12 = f3 * f + f7 * f8;
         if (f9 > 1.0F) {
            f9 = 1.0F;
         }

         if (f10 > 1.0F) {
            f10 = 1.0F;
         }

         if (f11 > 1.0F) {
            f11 = 1.0F;
         }

         if (f12 > 1.0F) {
            f12 = 1.0F;
         }

         int j = (int)(f9 * 255.0F);
         int k = (int)(f10 * 255.0F);
         int l = (int)(f11 * 255.0F);
         int i1 = (int)(f12 * 255.0F);
         this.setPixelRGBA(p_195718_1_, p_195718_2_, j << 24 | k << 16 | l << 8 | i1 << 0);
      }
   }

   @Deprecated
   public int[] makePixelArray() {
      if (this.pixelFormat != NativeImage.PixelFormat.RGBA) {
         throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
      } else {
         this.checkImage();
         int[] aint = new int[this.getWidth() * this.getHeight()];

         for(int i = 0; i < this.getHeight(); ++i) {
            for(int j = 0; j < this.getWidth(); ++j) {
               int k = this.getPixelRGBA(j, i);
               int l = k >> 24 & 255;
               int i1 = k >> 16 & 255;
               int j1 = k >> 8 & 255;
               int k1 = k >> 0 & 255;
               int l1 = l << 24 | k1 << 16 | j1 << 8 | i1;
               aint[j + i * this.getWidth()] = l1;
            }
         }

         return aint;
      }
   }

   public void uploadTextureSub(int p_195697_1_, int p_195697_2_, int p_195697_3_, boolean p_195697_4_) {
      this.uploadTextureSub(p_195697_1_, p_195697_2_, p_195697_3_, 0, 0, this.width, this.height, p_195697_4_);
   }

   public void uploadTextureSub(int p_195706_1_, int p_195706_2_, int p_195706_3_, int p_195706_4_, int p_195706_5_, int p_195706_6_, int p_195706_7_, boolean p_195706_8_) {
      this.uploadTextureSub(p_195706_1_, p_195706_2_, p_195706_3_, p_195706_4_, p_195706_5_, p_195706_6_, p_195706_7_, false, false, p_195706_8_);
   }

   public void uploadTextureSub(int p_195712_1_, int p_195712_2_, int p_195712_3_, int p_195712_4_, int p_195712_5_, int p_195712_6_, int p_195712_7_, boolean p_195712_8_, boolean p_195712_9_, boolean p_195712_10_) {
      this.checkImage();
      setMinMagFilters(p_195712_8_, p_195712_10_);
      setWrapST(p_195712_9_);
      if (p_195712_6_ == this.getWidth()) {
         GlStateManager.pixelStorei(3314, 0);
      } else {
         GlStateManager.pixelStorei(3314, this.getWidth());
      }

      GlStateManager.pixelStorei(3316, p_195712_4_);
      GlStateManager.pixelStorei(3315, p_195712_5_);
      this.pixelFormat.setGlUnpackAlignment();
      GlStateManager.texSubImage2D(3553, p_195712_1_, p_195712_2_, p_195712_3_, p_195712_6_, p_195712_7_, this.pixelFormat.getGlFormat(), 5121, this.imagePointer);
   }

   public void downloadFromTexture(int p_195717_1_, boolean p_195717_2_) {
      this.checkImage();
      this.pixelFormat.setGlPackAlignment();
      GlStateManager.getTexImage(3553, p_195717_1_, this.pixelFormat.getGlFormat(), 5121, this.imagePointer);
      if (p_195717_2_ && this.pixelFormat.hasAlpha()) {
         for(int i = 0; i < this.getHeight(); ++i) {
            for(int j = 0; j < this.getWidth(); ++j) {
               this.setPixelRGBA(j, i, this.getPixelRGBA(j, i) | 255 << this.pixelFormat.func_211648_n());
            }
         }
      }

   }

   public void downloadFromFramebuffer(boolean p_195701_1_) {
      this.checkImage();
      this.pixelFormat.setGlPackAlignment();
      if (p_195701_1_) {
         GlStateManager.pixelTransferf(3357, Float.MAX_VALUE);
      }

      GlStateManager.readPixels(0, 0, this.width, this.height, this.pixelFormat.getGlFormat(), 5121, this.imagePointer);
      if (p_195701_1_) {
         GlStateManager.pixelTransferf(3357, 0.0F);
      }

   }

   public void write(File p_209271_1_) throws IOException {
      this.write(p_209271_1_.toPath());
   }

   public void renderGlyph(STBTTFontinfo p_211676_1_, int p_211676_2_, int p_211676_3_, int p_211676_4_, float p_211676_5_, float p_211676_6_, float p_211676_7_, float p_211676_8_, int p_211676_9_, int p_211676_10_) {
      if (p_211676_9_ >= 0 && p_211676_9_ + p_211676_3_ <= this.getWidth() && p_211676_10_ >= 0 && p_211676_10_ + p_211676_4_ <= this.getHeight()) {
         if (this.pixelFormat.getPixelSize() != 1) {
            throw new IllegalArgumentException("Can only write fonts into 1-component images.");
         } else {
            STBTruetype.nstbtt_MakeGlyphBitmapSubpixel(p_211676_1_.address(), this.imagePointer + (long)p_211676_9_ + (long)(p_211676_10_ * this.getWidth()), p_211676_3_, p_211676_4_, this.getWidth(), p_211676_5_, p_211676_6_, p_211676_7_, p_211676_8_, p_211676_2_);
         }
      } else {
         throw new IllegalArgumentException(String.format("Out of bounds: start: (%s, %s) (size: %sx%s); size: %sx%s", p_211676_9_, p_211676_10_, p_211676_3_, p_211676_4_, this.getWidth(), this.getHeight()));
      }
   }

   public void write(Path p_209270_1_) throws IOException {
      if (!this.pixelFormat.isSerializable()) {
         throw new UnsupportedOperationException("Don't know how to write format " + this.pixelFormat);
      } else {
         this.checkImage();
         WritableByteChannel writablebytechannel = Files.newByteChannel(p_209270_1_, OPEN_OPTIONS);
         Throwable throwable = null;

         try {
            NativeImage.WriteCallback nativeimage$writecallback = new NativeImage.WriteCallback(writablebytechannel);

            try {
               if (!STBImageWrite.stbi_write_png_to_func(nativeimage$writecallback, 0L, this.getWidth(), this.getHeight(), this.pixelFormat.getPixelSize(), MemoryUtil.memByteBuffer(this.imagePointer, this.size), 0)) {
                  throw new IOException("Could not write image to the PNG file \"" + p_209270_1_.toAbsolutePath() + "\": " + STBImage.stbi_failure_reason());
               }
            } finally {
               nativeimage$writecallback.free();
            }

            nativeimage$writecallback.propagateException();
         } catch (Throwable throwable2) {
            throwable = throwable2;
            throw throwable2;
         } finally {
            if (writablebytechannel != null) {
               if (throwable != null) {
                  try {
                     writablebytechannel.close();
                  } catch (Throwable throwable1) {
                     throwable.addSuppressed(throwable1);
                  }
               } else {
                  writablebytechannel.close();
               }
            }

         }

      }
   }

   public void copyImageData(NativeImage p_195703_1_) {
      if (p_195703_1_.getFormat() != this.pixelFormat) {
         throw new UnsupportedOperationException("Image formats don't match.");
      } else {
         int i = this.pixelFormat.getPixelSize();
         this.checkImage();
         p_195703_1_.checkImage();
         if (this.width == p_195703_1_.width) {
            MemoryUtil.memCopy(p_195703_1_.imagePointer, this.imagePointer, (long)Math.min(this.size, p_195703_1_.size));
         } else {
            int j = Math.min(this.getWidth(), p_195703_1_.getWidth());
            int k = Math.min(this.getHeight(), p_195703_1_.getHeight());

            for(int l = 0; l < k; ++l) {
               int i1 = l * p_195703_1_.getWidth() * i;
               int j1 = l * this.getWidth() * i;
               MemoryUtil.memCopy(p_195703_1_.imagePointer + (long)i1, this.imagePointer + (long)j1, (long)j);
            }
         }

      }
   }

   public void fillAreaRGBA(int p_195715_1_, int p_195715_2_, int p_195715_3_, int p_195715_4_, int p_195715_5_) {
      for(int i = p_195715_2_; i < p_195715_2_ + p_195715_4_; ++i) {
         for(int j = p_195715_1_; j < p_195715_1_ + p_195715_3_; ++j) {
            this.setPixelRGBA(j, i, p_195715_5_);
         }
      }

   }

   public void copyAreaRGBA(int p_195699_1_, int p_195699_2_, int p_195699_3_, int p_195699_4_, int p_195699_5_, int p_195699_6_, boolean p_195699_7_, boolean p_195699_8_) {
      for(int i = 0; i < p_195699_6_; ++i) {
         for(int j = 0; j < p_195699_5_; ++j) {
            int k = p_195699_7_ ? p_195699_5_ - 1 - j : j;
            int l = p_195699_8_ ? p_195699_6_ - 1 - i : i;
            int i1 = this.getPixelRGBA(p_195699_1_ + j, p_195699_2_ + i);
            this.setPixelRGBA(p_195699_1_ + p_195699_3_ + k, p_195699_2_ + p_195699_4_ + l, i1);
         }
      }

   }

   public void flip() {
      this.checkImage();

      try (MemoryStack memorystack = MemoryStack.stackPush()) {
         int i = this.pixelFormat.getPixelSize();
         int j = this.getWidth() * i;
         long k = memorystack.nmalloc(j);

         for(int l = 0; l < this.getHeight() / 2; ++l) {
            int i1 = l * this.getWidth() * i;
            int j1 = (this.getHeight() - 1 - l) * this.getWidth() * i;
            MemoryUtil.memCopy(this.imagePointer + (long)i1, k, (long)j);
            MemoryUtil.memCopy(this.imagePointer + (long)j1, this.imagePointer + (long)i1, (long)j);
            MemoryUtil.memCopy(k, this.imagePointer + (long)j1, (long)j);
         }
      }

   }

   public void resizeSubRectTo(int p_195708_1_, int p_195708_2_, int p_195708_3_, int p_195708_4_, NativeImage p_195708_5_) {
      this.checkImage();
      if (p_195708_5_.getFormat() != this.pixelFormat) {
         throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
      } else {
         int i = this.pixelFormat.getPixelSize();
         STBImageResize.nstbir_resize_uint8(this.imagePointer + (long)((p_195708_1_ + p_195708_2_ * this.getWidth()) * i), p_195708_3_, p_195708_4_, this.getWidth() * i, p_195708_5_.imagePointer, p_195708_5_.getWidth(), p_195708_5_.getHeight(), 0, i);
      }
   }

   public void untrack() {
      LWJGLMemoryUntracker.untrack(this.imagePointer);
   }

   @OnlyIn(Dist.CLIENT)
   public static enum PixelFormat {
      RGBA(4, 6408, true, true, true, false, true, 0, 8, 16, 255, 24, true),
      RGB(3, 6407, true, true, true, false, false, 0, 8, 16, 255, 255, true),
      LUMINANCE_ALPHA(2, 6410, false, false, false, true, true, 255, 255, 255, 0, 8, true),
      LUMINANCE(1, 6409, false, false, false, true, false, 0, 0, 0, 0, 255, true);

      private final int pixelSize;
      private final int glFormat;
      private final boolean field_211661_g;
      private final boolean field_211662_h;
      private final boolean field_211663_i;
      private final boolean hasLuminance;
      private final boolean hasAlpha;
      private final int field_211666_l;
      private final int field_211667_m;
      private final int field_211668_n;
      private final int field_211669_o;
      private final int field_211670_p;
      private final boolean serializable;

      private PixelFormat(int p_i49762_3_, int p_i49762_4_, boolean p_i49762_5_, boolean p_i49762_6_, boolean p_i49762_7_, boolean p_i49762_8_, boolean p_i49762_9_, int p_i49762_10_, int p_i49762_11_, int p_i49762_12_, int p_i49762_13_, int p_i49762_14_, boolean p_i49762_15_) {
         this.pixelSize = p_i49762_3_;
         this.glFormat = p_i49762_4_;
         this.field_211661_g = p_i49762_5_;
         this.field_211662_h = p_i49762_6_;
         this.field_211663_i = p_i49762_7_;
         this.hasLuminance = p_i49762_8_;
         this.hasAlpha = p_i49762_9_;
         this.field_211666_l = p_i49762_10_;
         this.field_211667_m = p_i49762_11_;
         this.field_211668_n = p_i49762_12_;
         this.field_211669_o = p_i49762_13_;
         this.field_211670_p = p_i49762_14_;
         this.serializable = p_i49762_15_;
      }

      public int getPixelSize() {
         return this.pixelSize;
      }

      public void setGlPackAlignment() {
         GlStateManager.pixelStorei(3333, this.getPixelSize());
      }

      public void setGlUnpackAlignment() {
         GlStateManager.pixelStorei(3317, this.getPixelSize());
      }

      public int getGlFormat() {
         return this.glFormat;
      }

      public boolean hasAlpha() {
         return this.hasAlpha;
      }

      public int func_211648_n() {
         return this.field_211670_p;
      }

      public boolean hasLuminanceOrAlpha() {
         return this.hasLuminance || this.hasAlpha;
      }

      public int func_211647_v() {
         return this.hasLuminance ? this.field_211669_o : this.field_211670_p;
      }

      public boolean isSerializable() {
         return this.serializable;
      }

      private static NativeImage.PixelFormat fromChannelCount(int p_211646_0_) {
         switch(p_211646_0_) {
         case 1:
            return LUMINANCE;
         case 2:
            return LUMINANCE_ALPHA;
         case 3:
            return RGB;
         case 4:
         default:
            return RGBA;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum PixelFormatGLCode {
      RGBA(6408),
      RGB(6407),
      LUMINANCE_ALPHA(6410),
      LUMINANCE(6409),
      INTENSITY(32841);

      private final int glConstant;

      private PixelFormatGLCode(int p_i49761_3_) {
         this.glConstant = p_i49761_3_;
      }

      int func_211672_a() {
         return this.glConstant;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class WriteCallback extends STBIWriteCallback {
      private final WritableByteChannel channel;
      private IOException exception;

      private WriteCallback(WritableByteChannel p_i49388_1_) {
         this.channel = p_i49388_1_;
      }

      public void invoke(long p_invoke_1_, long p_invoke_3_, int p_invoke_5_) {
         ByteBuffer bytebuffer = getData(p_invoke_3_, p_invoke_5_);

         try {
            this.channel.write(bytebuffer);
         } catch (IOException ioexception) {
            this.exception = ioexception;
         }

      }

      public void propagateException() throws IOException {
         if (this.exception != null) {
            throw this.exception;
         }
      }
   }
}
