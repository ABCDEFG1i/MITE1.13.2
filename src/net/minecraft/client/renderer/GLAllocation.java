package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GLAllocation {
   public static synchronized int generateDisplayLists(int p_74526_0_) {
      int i = GlStateManager.genLists(p_74526_0_);
      if (i == 0) {
         int j = GlStateManager.getError();
         String s = "No error code reported";
         if (j != 0) {
            s = OpenGlHelper.func_195917_n(j);
         }

         throw new IllegalStateException("glGenLists returned an ID of 0 for a count of " + p_74526_0_ + ", GL error (" + j + "): " + s);
      } else {
         return i;
      }
   }

   public static synchronized void deleteDisplayLists(int p_178874_0_, int p_178874_1_) {
      GlStateManager.deleteLists(p_178874_0_, p_178874_1_);
   }

   public static synchronized void deleteDisplayLists(int p_74523_0_) {
      deleteDisplayLists(p_74523_0_, 1);
   }

   public static synchronized ByteBuffer createDirectByteBuffer(int p_74524_0_) {
      return ByteBuffer.allocateDirect(p_74524_0_).order(ByteOrder.nativeOrder());
   }

   public static FloatBuffer createDirectFloatBuffer(int p_74529_0_) {
      return createDirectByteBuffer(p_74529_0_ << 2).asFloatBuffer();
   }
}
