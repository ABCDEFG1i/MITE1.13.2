package net.minecraft.client.renderer.texture;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class TextureUtil {
   private static final Logger LOGGER = LogManager.getLogger();

   public static int glGenTextures() {
      return GlStateManager.generateTexture();
   }

   public static void deleteTexture(int p_147942_0_) {
      GlStateManager.deleteTexture(p_147942_0_);
   }

   public static void allocateTexture(int p_110991_0_, int p_110991_1_, int p_110991_2_) {
      func_211682_a(NativeImage.PixelFormatGLCode.RGBA, p_110991_0_, 0, p_110991_1_, p_110991_2_);
   }

   public static void func_211681_a(NativeImage.PixelFormatGLCode p_211681_0_, int p_211681_1_, int p_211681_2_, int p_211681_3_) {
      func_211682_a(p_211681_0_, p_211681_1_, 0, p_211681_2_, p_211681_3_);
   }

   public static void allocateTextureImpl(int p_180600_0_, int p_180600_1_, int p_180600_2_, int p_180600_3_) {
      func_211682_a(NativeImage.PixelFormatGLCode.RGBA, p_180600_0_, p_180600_1_, p_180600_2_, p_180600_3_);
   }

   public static void func_211682_a(NativeImage.PixelFormatGLCode p_211682_0_, int p_211682_1_, int p_211682_2_, int p_211682_3_, int p_211682_4_) {
      bindTexture(p_211682_1_);
      if (p_211682_2_ >= 0) {
         GlStateManager.texParameteri(3553, 33085, p_211682_2_);
         GlStateManager.texParameteri(3553, 33082, 0);
         GlStateManager.texParameteri(3553, 33083, p_211682_2_);
         GlStateManager.texParameterf(3553, 34049, 0.0F);
      }

      for(int i = 0; i <= p_211682_2_; ++i) {
         GlStateManager.texImage2D(3553, i, p_211682_0_.func_211672_a(), p_211682_3_ >> i, p_211682_4_ >> i, 0, 6408, 5121,
                 null);
      }

   }

   private static void bindTexture(int p_94277_0_) {
      GlStateManager.bindTexture(p_94277_0_);
   }

   @Deprecated
   public static int[] makePixelArray(IResourceManager p_195725_0_, ResourceLocation p_195725_1_) throws IOException {
      Object object;
      try (
         IResource iresource = p_195725_0_.getResource(p_195725_1_);
         NativeImage nativeimage = NativeImage.read(iresource.getInputStream())) {
         object = nativeimage.makePixelArray();
      }

      return (int[])object;
   }

   public static ByteBuffer readToNativeBuffer(InputStream p_195724_0_) throws IOException {
      ByteBuffer bytebuffer;
      if (p_195724_0_ instanceof FileInputStream) {
         FileInputStream fileinputstream = (FileInputStream)p_195724_0_;
         FileChannel filechannel = fileinputstream.getChannel();
         bytebuffer = MemoryUtil.memAlloc((int)filechannel.size() + 1);

         while(filechannel.read(bytebuffer) != -1) {
         }
      } else {
         bytebuffer = MemoryUtil.memAlloc(8192);
         ReadableByteChannel readablebytechannel = Channels.newChannel(p_195724_0_);

         while(readablebytechannel.read(bytebuffer) != -1) {
            if (bytebuffer.remaining() == 0) {
               bytebuffer = MemoryUtil.memRealloc(bytebuffer, bytebuffer.capacity() * 2);
            }
         }
      }

      return bytebuffer;
   }
}
