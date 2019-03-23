package net.minecraft.client.renderer;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ImageBufferDownload implements IImageBuffer {
   public NativeImage parseUserSkin(NativeImage p_195786_1_) {
      boolean flag = p_195786_1_.getHeight() == 32;
      if (flag) {
         NativeImage nativeimage = new NativeImage(64, 64, true);
         nativeimage.copyImageData(p_195786_1_);
         p_195786_1_.close();
         p_195786_1_ = nativeimage;
         nativeimage.fillAreaRGBA(0, 32, 64, 32, 0);
         nativeimage.copyAreaRGBA(4, 16, 16, 32, 4, 4, true, false);
         nativeimage.copyAreaRGBA(8, 16, 16, 32, 4, 4, true, false);
         nativeimage.copyAreaRGBA(0, 20, 24, 32, 4, 12, true, false);
         nativeimage.copyAreaRGBA(4, 20, 16, 32, 4, 12, true, false);
         nativeimage.copyAreaRGBA(8, 20, 8, 32, 4, 12, true, false);
         nativeimage.copyAreaRGBA(12, 20, 16, 32, 4, 12, true, false);
         nativeimage.copyAreaRGBA(44, 16, -8, 32, 4, 4, true, false);
         nativeimage.copyAreaRGBA(48, 16, -8, 32, 4, 4, true, false);
         nativeimage.copyAreaRGBA(40, 20, 0, 32, 4, 12, true, false);
         nativeimage.copyAreaRGBA(44, 20, -8, 32, 4, 12, true, false);
         nativeimage.copyAreaRGBA(48, 20, -16, 32, 4, 12, true, false);
         nativeimage.copyAreaRGBA(52, 20, -8, 32, 4, 12, true, false);
      }

      setAreaOpaque(p_195786_1_, 0, 0, 32, 16);
      if (flag) {
         setAreaTransparent(p_195786_1_, 32, 0, 64, 32);
      }

      setAreaOpaque(p_195786_1_, 0, 16, 64, 32);
      setAreaOpaque(p_195786_1_, 16, 48, 48, 64);
      return p_195786_1_;
   }

   public void skinAvailable() {
   }

   private static void setAreaTransparent(NativeImage p_195788_0_, int p_195788_1_, int p_195788_2_, int p_195788_3_, int p_195788_4_) {
      for(int i = p_195788_1_; i < p_195788_3_; ++i) {
         for(int j = p_195788_2_; j < p_195788_4_; ++j) {
            int k = p_195788_0_.getPixelRGBA(i, j);
            if ((k >> 24 & 255) < 128) {
               return;
            }
         }
      }

      for(int l = p_195788_1_; l < p_195788_3_; ++l) {
         for(int i1 = p_195788_2_; i1 < p_195788_4_; ++i1) {
            p_195788_0_.setPixelRGBA(l, i1, p_195788_0_.getPixelRGBA(l, i1) & 16777215);
         }
      }

   }

   private static void setAreaOpaque(NativeImage p_195787_0_, int p_195787_1_, int p_195787_2_, int p_195787_3_, int p_195787_4_) {
      for(int i = p_195787_1_; i < p_195787_3_; ++i) {
         for(int j = p_195787_2_; j < p_195787_4_; ++j) {
            p_195787_0_.setPixelRGBA(i, j, p_195787_0_.getPixelRGBA(i, j) | -16777216);
         }
      }

   }
}
