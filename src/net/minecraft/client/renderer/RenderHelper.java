package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderHelper {
   private static final FloatBuffer COLOR_BUFFER = GLAllocation.createDirectFloatBuffer(4);
   private static final Vec3d LIGHT0_POS = (new Vec3d((double)0.2F, 1.0D, (double)-0.7F)).normalize();
   private static final Vec3d LIGHT1_POS = (new Vec3d((double)-0.2F, 1.0D, (double)0.7F)).normalize();

   public static void disableStandardItemLighting() {
      GlStateManager.disableLighting();
      GlStateManager.disableLight(0);
      GlStateManager.disableLight(1);
      GlStateManager.disableColorMaterial();
   }

   public static void enableStandardItemLighting() {
      GlStateManager.enableLighting();
      GlStateManager.enableLight(0);
      GlStateManager.enableLight(1);
      GlStateManager.enableColorMaterial();
      GlStateManager.colorMaterial(1032, 5634);
      GlStateManager.lightfv(16384, 4611, setColorBuffer(LIGHT0_POS.x, LIGHT0_POS.y, LIGHT0_POS.z, 0.0D));
      float f = 0.6F;
      GlStateManager.lightfv(16384, 4609, setColorBuffer(0.6F, 0.6F, 0.6F, 1.0F));
      GlStateManager.lightfv(16384, 4608, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      GlStateManager.lightfv(16384, 4610, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      GlStateManager.lightfv(16385, 4611, setColorBuffer(LIGHT1_POS.x, LIGHT1_POS.y, LIGHT1_POS.z, 0.0D));
      GlStateManager.lightfv(16385, 4609, setColorBuffer(0.6F, 0.6F, 0.6F, 1.0F));
      GlStateManager.lightfv(16385, 4608, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      GlStateManager.lightfv(16385, 4610, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      GlStateManager.shadeModel(7424);
      float f1 = 0.4F;
      GlStateManager.lightModelfv(2899, setColorBuffer(0.4F, 0.4F, 0.4F, 1.0F));
   }

   private static FloatBuffer setColorBuffer(double p_74517_0_, double p_74517_2_, double p_74517_4_, double p_74517_6_) {
      return setColorBuffer((float)p_74517_0_, (float)p_74517_2_, (float)p_74517_4_, (float)p_74517_6_);
   }

   public static FloatBuffer setColorBuffer(float p_74521_0_, float p_74521_1_, float p_74521_2_, float p_74521_3_) {
      COLOR_BUFFER.clear();
      COLOR_BUFFER.put(p_74521_0_).put(p_74521_1_).put(p_74521_2_).put(p_74521_3_);
      COLOR_BUFFER.flip();
      return COLOR_BUFFER;
   }

   public static void enableGUIStandardItemLighting() {
      GlStateManager.pushMatrix();
      GlStateManager.rotatef(-30.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(165.0F, 1.0F, 0.0F, 0.0F);
      enableStandardItemLighting();
      GlStateManager.popMatrix();
   }
}
