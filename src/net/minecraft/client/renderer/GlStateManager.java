package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.LWJGLMemoryUntracker;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class GlStateManager {
   private static final FloatBuffer BUF_FLOAT_16 = Util.make(MemoryUtil.memAllocFloat(16), (p_209238_0_) -> {
      LWJGLMemoryUntracker.untrack(MemoryUtil.memAddress(p_209238_0_));
   });
   private static final FloatBuffer BUF_FLOAT_4 = Util.make(MemoryUtil.memAllocFloat(4), (p_209236_0_) -> {
      LWJGLMemoryUntracker.untrack(MemoryUtil.memAddress(p_209236_0_));
   });
   private static final GlStateManager.AlphaState ALPHA = new GlStateManager.AlphaState();
   private static final GlStateManager.BooleanState LIGHTING = new GlStateManager.BooleanState(2896);
   private static final GlStateManager.BooleanState[] LIGHTS = IntStream.range(0, 8).mapToObj((p_199933_0_) -> {
      return new GlStateManager.BooleanState(16384 + p_199933_0_);
   }).toArray((p_199930_0_) -> {
      return new GlStateManager.BooleanState[p_199930_0_];
   });
   private static final GlStateManager.ColorMaterialState COLOR_MATERIAL = new GlStateManager.ColorMaterialState();
   private static final GlStateManager.BlendState BLEND = new GlStateManager.BlendState();
   private static final GlStateManager.DepthState DEPTH = new GlStateManager.DepthState();
   private static final GlStateManager.FogState FOG = new GlStateManager.FogState();
   private static final GlStateManager.CullState CULL = new GlStateManager.CullState();
   private static final GlStateManager.PolygonOffsetState POLYGON_OFFSET = new GlStateManager.PolygonOffsetState();
   private static final GlStateManager.ColorLogicState COLOR_LOGIC = new GlStateManager.ColorLogicState();
   private static final GlStateManager.TexGenState TEX_GEN = new GlStateManager.TexGenState();
   private static final GlStateManager.ClearState CLEAR = new GlStateManager.ClearState();
   private static final GlStateManager.StencilState STENCIL = new GlStateManager.StencilState();
   private static final GlStateManager.BooleanState NORMALIZE = new GlStateManager.BooleanState(2977);
   private static int activeTexture;
   private static final GlStateManager.TextureState[] TEXTURES = IntStream.range(0, 8).mapToObj((p_199931_0_) -> {
      return new GlStateManager.TextureState();
   }).toArray((p_199932_0_) -> {
      return new GlStateManager.TextureState[p_199932_0_];
   });
   private static int activeShadeModel = 7425;
   private static final GlStateManager.BooleanState RESCALE_NORMAL = new GlStateManager.BooleanState(32826);
   private static final GlStateManager.ColorMask COLOR_MASK = new GlStateManager.ColorMask();
   private static final GlStateManager.Color COLOR = new GlStateManager.Color();

   public static void pushLightingAttrib() {
      GL11.glPushAttrib(8256);
   }

   public static void popAttrib() {
      GL11.glPopAttrib();
   }

   public static void disableAlphaTest() {
      ALPHA.test.setDisabled();
   }

   public static void enableAlphaTest() {
      ALPHA.test.setEnabled();
   }

   public static void alphaFunc(int p_179092_0_, float p_179092_1_) {
      if (p_179092_0_ != ALPHA.func || p_179092_1_ != ALPHA.ref) {
         ALPHA.func = p_179092_0_;
         ALPHA.ref = p_179092_1_;
         GL11.glAlphaFunc(p_179092_0_, p_179092_1_);
      }

   }

   public static void enableLighting() {
      LIGHTING.setEnabled();
   }

   public static void disableLighting() {
      LIGHTING.setDisabled();
   }

   public static void enableLight(int p_179085_0_) {
      LIGHTS[p_179085_0_].setEnabled();
   }

   public static void disableLight(int p_179122_0_) {
      LIGHTS[p_179122_0_].setDisabled();
   }

   public static void enableColorMaterial() {
      COLOR_MATERIAL.colorMaterial.setEnabled();
   }

   public static void disableColorMaterial() {
      COLOR_MATERIAL.colorMaterial.setDisabled();
   }

   public static void colorMaterial(int p_179104_0_, int p_179104_1_) {
      if (p_179104_0_ != COLOR_MATERIAL.face || p_179104_1_ != COLOR_MATERIAL.mode) {
         COLOR_MATERIAL.face = p_179104_0_;
         COLOR_MATERIAL.mode = p_179104_1_;
         GL11.glColorMaterial(p_179104_0_, p_179104_1_);
      }

   }

   public static void lightfv(int p_187438_0_, int p_187438_1_, FloatBuffer p_187438_2_) {
      GL11.glLightfv(p_187438_0_, p_187438_1_, p_187438_2_);
   }

   public static void lightModelfv(int p_187424_0_, FloatBuffer p_187424_1_) {
      GL11.glLightModelfv(p_187424_0_, p_187424_1_);
   }

   public static void normal3f(float p_187432_0_, float p_187432_1_, float p_187432_2_) {
      GL11.glNormal3f(p_187432_0_, p_187432_1_, p_187432_2_);
   }

   public static void disableDepthTest() {
      DEPTH.test.setDisabled();
   }

   public static void enableDepthTest() {
      DEPTH.test.setEnabled();
   }

   public static void depthFunc(int p_179143_0_) {
      if (p_179143_0_ != DEPTH.func) {
         DEPTH.func = p_179143_0_;
         GL11.glDepthFunc(p_179143_0_);
      }

   }

   public static void depthMask(boolean p_179132_0_) {
      if (p_179132_0_ != DEPTH.mask) {
         DEPTH.mask = p_179132_0_;
         GL11.glDepthMask(p_179132_0_);
      }

   }

   public static void disableBlend() {
      BLEND.blend.setDisabled();
   }

   public static void enableBlend() {
      BLEND.blend.setEnabled();
   }

   public static void blendFunc(GlStateManager.SourceFactor p_187401_0_, GlStateManager.DestFactor p_187401_1_) {
      blendFunc(p_187401_0_.factor, p_187401_1_.factor);
   }

   public static void blendFunc(int p_179112_0_, int p_179112_1_) {
      if (p_179112_0_ != BLEND.srcFactor || p_179112_1_ != BLEND.dstFactor) {
         BLEND.srcFactor = p_179112_0_;
         BLEND.dstFactor = p_179112_1_;
         GL11.glBlendFunc(p_179112_0_, p_179112_1_);
      }

   }

   public static void blendFuncSeparate(GlStateManager.SourceFactor p_187428_0_, GlStateManager.DestFactor p_187428_1_, GlStateManager.SourceFactor p_187428_2_, GlStateManager.DestFactor p_187428_3_) {
      blendFuncSeparate(p_187428_0_.factor, p_187428_1_.factor, p_187428_2_.factor, p_187428_3_.factor);
   }

   public static void blendFuncSeparate(int p_179120_0_, int p_179120_1_, int p_179120_2_, int p_179120_3_) {
      if (p_179120_0_ != BLEND.srcFactor || p_179120_1_ != BLEND.dstFactor || p_179120_2_ != BLEND.srcFactorAlpha || p_179120_3_ != BLEND.dstFactorAlpha) {
         BLEND.srcFactor = p_179120_0_;
         BLEND.dstFactor = p_179120_1_;
         BLEND.srcFactorAlpha = p_179120_2_;
         BLEND.dstFactorAlpha = p_179120_3_;
         OpenGlHelper.glBlendFuncSeparate(p_179120_0_, p_179120_1_, p_179120_2_, p_179120_3_);
      }

   }

   public static void blendEquation(int p_187398_0_) {
      GL14.glBlendEquation(p_187398_0_);
   }

   public static void enableOutlineMode(int p_187431_0_) {
      BUF_FLOAT_4.put(0, (float)(p_187431_0_ >> 16 & 255) / 255.0F);
      BUF_FLOAT_4.put(1, (float)(p_187431_0_ >> 8 & 255) / 255.0F);
      BUF_FLOAT_4.put(2, (float)(p_187431_0_ >> 0 & 255) / 255.0F);
      BUF_FLOAT_4.put(3, (float)(p_187431_0_ >> 24 & 255) / 255.0F);
      texEnvfv(8960, 8705, BUF_FLOAT_4);
      texEnvi(8960, 8704, 34160);
      texEnvi(8960, 34161, 7681);
      texEnvi(8960, 34176, 34166);
      texEnvi(8960, 34192, 768);
      texEnvi(8960, 34162, 7681);
      texEnvi(8960, 34184, 5890);
      texEnvi(8960, 34200, 770);
   }

   public static void disableOutlineMode() {
      texEnvi(8960, 8704, 8448);
      texEnvi(8960, 34161, 8448);
      texEnvi(8960, 34162, 8448);
      texEnvi(8960, 34176, 5890);
      texEnvi(8960, 34184, 5890);
      texEnvi(8960, 34192, 768);
      texEnvi(8960, 34200, 770);
   }

   public static void enableFog() {
      FOG.fog.setEnabled();
   }

   public static void disableFog() {
      FOG.fog.setDisabled();
   }

   public static void fogMode(GlStateManager.FogMode p_187430_0_) {
      fogMode(p_187430_0_.capabilityId);
   }

   private static void fogMode(int p_179093_0_) {
      if (p_179093_0_ != FOG.mode) {
         FOG.mode = p_179093_0_;
         GL11.glFogi(2917, p_179093_0_);
      }

   }

   public static void fogDensity(float p_179095_0_) {
      if (p_179095_0_ != FOG.density) {
         FOG.density = p_179095_0_;
         GL11.glFogf(2914, p_179095_0_);
      }

   }

   public static void fogStart(float p_179102_0_) {
      if (p_179102_0_ != FOG.start) {
         FOG.start = p_179102_0_;
         GL11.glFogf(2915, p_179102_0_);
      }

   }

   public static void fogEnd(float p_179153_0_) {
      if (p_179153_0_ != FOG.end) {
         FOG.end = p_179153_0_;
         GL11.glFogf(2916, p_179153_0_);
      }

   }

   public static void fogfv(int p_187402_0_, FloatBuffer p_187402_1_) {
      GL11.glFogfv(p_187402_0_, p_187402_1_);
   }

   public static void fogi(int p_187412_0_, int p_187412_1_) {
      GL11.glFogi(p_187412_0_, p_187412_1_);
   }

   public static void enableCull() {
      CULL.cullFace.setEnabled();
   }

   public static void disableCull() {
      CULL.cullFace.setDisabled();
   }

   public static void cullFace(GlStateManager.CullFace p_187407_0_) {
      cullFace(p_187407_0_.mode);
   }

   private static void cullFace(int p_179107_0_) {
      if (p_179107_0_ != CULL.mode) {
         CULL.mode = p_179107_0_;
         GL11.glCullFace(p_179107_0_);
      }

   }

   public static void polygonMode(int p_187409_0_, int p_187409_1_) {
      GL11.glPolygonMode(p_187409_0_, p_187409_1_);
   }

   public static void enablePolygonOffset() {
      POLYGON_OFFSET.fill.setEnabled();
   }

   public static void disablePolygonOffset() {
      POLYGON_OFFSET.fill.setDisabled();
   }

   public static void polygonOffset(float p_179136_0_, float p_179136_1_) {
      if (p_179136_0_ != POLYGON_OFFSET.factor || p_179136_1_ != POLYGON_OFFSET.units) {
         POLYGON_OFFSET.factor = p_179136_0_;
         POLYGON_OFFSET.units = p_179136_1_;
         GL11.glPolygonOffset(p_179136_0_, p_179136_1_);
      }

   }

   public static void enableColorLogic() {
      COLOR_LOGIC.colorLogicOp.setEnabled();
   }

   public static void disableColorLogic() {
      COLOR_LOGIC.colorLogicOp.setDisabled();
   }

   public static void logicOp(GlStateManager.LogicOp p_187422_0_) {
      logicOp(p_187422_0_.opcode);
   }

   public static void logicOp(int p_179116_0_) {
      if (p_179116_0_ != COLOR_LOGIC.opcode) {
         COLOR_LOGIC.opcode = p_179116_0_;
         GL11.glLogicOp(p_179116_0_);
      }

   }

   public static void enableTexGen(GlStateManager.TexGen p_179087_0_) {
      texGenCoord(p_179087_0_).textureGen.setEnabled();
   }

   public static void disableTexGen(GlStateManager.TexGen p_179100_0_) {
      texGenCoord(p_179100_0_).textureGen.setDisabled();
   }

   public static void texGenMode(GlStateManager.TexGen p_179149_0_, int p_179149_1_) {
      GlStateManager.TexGenCoord glstatemanager$texgencoord = texGenCoord(p_179149_0_);
      if (p_179149_1_ != glstatemanager$texgencoord.mode) {
         glstatemanager$texgencoord.mode = p_179149_1_;
         GL11.glTexGeni(glstatemanager$texgencoord.coord, 9472, p_179149_1_);
      }

   }

   public static void texGenParam(GlStateManager.TexGen p_179105_0_, int p_179105_1_, FloatBuffer p_179105_2_) {
      GL11.glTexGenfv(texGenCoord(p_179105_0_).coord, p_179105_1_, p_179105_2_);
   }

   private static GlStateManager.TexGenCoord texGenCoord(GlStateManager.TexGen p_179125_0_) {
      switch(p_179125_0_) {
      case S:
         return TEX_GEN.s;
      case T:
         return TEX_GEN.t;
      case R:
         return TEX_GEN.r;
      case Q:
         return TEX_GEN.q;
      default:
         return TEX_GEN.s;
      }
   }

   public static void activeTexture(int p_179138_0_) {
      if (activeTexture != p_179138_0_ - OpenGlHelper.GL_TEXTURE0) {
         activeTexture = p_179138_0_ - OpenGlHelper.GL_TEXTURE0;
         OpenGlHelper.glActiveTexture(p_179138_0_);
      }

   }

   public static void enableTexture2D() {
      TEXTURES[activeTexture].texture2DState.setEnabled();
   }

   public static void disableTexture2D() {
      TEXTURES[activeTexture].texture2DState.setDisabled();
   }

   public static void texEnvfv(int p_187448_0_, int p_187448_1_, FloatBuffer p_187448_2_) {
      GL11.glTexEnvfv(p_187448_0_, p_187448_1_, p_187448_2_);
   }

   public static void texEnvi(int p_187399_0_, int p_187399_1_, int p_187399_2_) {
      GL11.glTexEnvi(p_187399_0_, p_187399_1_, p_187399_2_);
   }

   public static void texEnvf(int p_187436_0_, int p_187436_1_, float p_187436_2_) {
      GL11.glTexEnvf(p_187436_0_, p_187436_1_, p_187436_2_);
   }

   public static void texParameterf(int p_187403_0_, int p_187403_1_, float p_187403_2_) {
      GL11.glTexParameterf(p_187403_0_, p_187403_1_, p_187403_2_);
   }

   public static void texParameteri(int p_187421_0_, int p_187421_1_, int p_187421_2_) {
      GL11.glTexParameteri(p_187421_0_, p_187421_1_, p_187421_2_);
   }

   public static int glGetTexLevelParameteri(int p_187411_0_, int p_187411_1_, int p_187411_2_) {
      return GL11.glGetTexLevelParameteri(p_187411_0_, p_187411_1_, p_187411_2_);
   }

   public static int generateTexture() {
      return GL11.glGenTextures();
   }

   public static void deleteTexture(int p_179150_0_) {
      GL11.glDeleteTextures(p_179150_0_);

      for(GlStateManager.TextureState glstatemanager$texturestate : TEXTURES) {
         if (glstatemanager$texturestate.textureName == p_179150_0_) {
            glstatemanager$texturestate.textureName = -1;
         }
      }

   }

   public static void bindTexture(int p_179144_0_) {
      if (p_179144_0_ != TEXTURES[activeTexture].textureName) {
         TEXTURES[activeTexture].textureName = p_179144_0_;
         GL11.glBindTexture(3553, p_179144_0_);
      }

   }

   public static void texImage2D(int p_187419_0_, int p_187419_1_, int p_187419_2_, int p_187419_3_, int p_187419_4_, int p_187419_5_, int p_187419_6_, int p_187419_7_, @Nullable IntBuffer p_187419_8_) {
      GL11.glTexImage2D(p_187419_0_, p_187419_1_, p_187419_2_, p_187419_3_, p_187419_4_, p_187419_5_, p_187419_6_, p_187419_7_, p_187419_8_);
   }

   public static void texSubImage2D(int p_199298_0_, int p_199298_1_, int p_199298_2_, int p_199298_3_, int p_199298_4_, int p_199298_5_, int p_199298_6_, int p_199298_7_, long p_199298_8_) {
      GL11.glTexSubImage2D(p_199298_0_, p_199298_1_, p_199298_2_, p_199298_3_, p_199298_4_, p_199298_5_, p_199298_6_, p_199298_7_, p_199298_8_);
   }

   public static void getTexImage(int p_199295_0_, int p_199295_1_, int p_199295_2_, int p_199295_3_, long p_199295_4_) {
      GL11.glGetTexImage(p_199295_0_, p_199295_1_, p_199295_2_, p_199295_3_, p_199295_4_);
   }

   public static void enableNormalize() {
      NORMALIZE.setEnabled();
   }

   public static void disableNormalize() {
      NORMALIZE.setDisabled();
   }

   public static void shadeModel(int p_179103_0_) {
      if (p_179103_0_ != activeShadeModel) {
         activeShadeModel = p_179103_0_;
         GL11.glShadeModel(p_179103_0_);
      }

   }

   public static void enableRescaleNormal() {
      RESCALE_NORMAL.setEnabled();
   }

   public static void disableRescaleNormal() {
      RESCALE_NORMAL.setDisabled();
   }

   public static void viewport(int p_179083_0_, int p_179083_1_, int p_179083_2_, int p_179083_3_) {
      GlStateManager.Viewport.INSTANCE.x = p_179083_0_;
      GlStateManager.Viewport.INSTANCE.y = p_179083_1_;
      GlStateManager.Viewport.INSTANCE.width = p_179083_2_;
      GlStateManager.Viewport.INSTANCE.height = p_179083_3_;
      GL11.glViewport(p_179083_0_, p_179083_1_, p_179083_2_, p_179083_3_);
   }

   public static void colorMask(boolean p_179135_0_, boolean p_179135_1_, boolean p_179135_2_, boolean p_179135_3_) {
      if (p_179135_0_ != COLOR_MASK.red || p_179135_1_ != COLOR_MASK.green || p_179135_2_ != COLOR_MASK.blue || p_179135_3_ != COLOR_MASK.alpha) {
         COLOR_MASK.red = p_179135_0_;
         COLOR_MASK.green = p_179135_1_;
         COLOR_MASK.blue = p_179135_2_;
         COLOR_MASK.alpha = p_179135_3_;
         GL11.glColorMask(p_179135_0_, p_179135_1_, p_179135_2_, p_179135_3_);
      }

   }

   public static void clearDepth(double p_179151_0_) {
      if (p_179151_0_ != CLEAR.depth) {
         CLEAR.depth = p_179151_0_;
         GL11.glClearDepth(p_179151_0_);
      }

   }

   public static void clearColor(float p_179082_0_, float p_179082_1_, float p_179082_2_, float p_179082_3_) {
      if (p_179082_0_ != CLEAR.color.red || p_179082_1_ != CLEAR.color.green || p_179082_2_ != CLEAR.color.blue || p_179082_3_ != CLEAR.color.alpha) {
         CLEAR.color.red = p_179082_0_;
         CLEAR.color.green = p_179082_1_;
         CLEAR.color.blue = p_179082_2_;
         CLEAR.color.alpha = p_179082_3_;
         GL11.glClearColor(p_179082_0_, p_179082_1_, p_179082_2_, p_179082_3_);
      }

   }

   public static void clear(int p_179086_0_) {
      GL11.glClear(p_179086_0_);
      if (Minecraft.IS_RUNNING_ON_MAC) {
         getError();
      }

   }

   public static void matrixMode(int p_179128_0_) {
      GL11.glMatrixMode(p_179128_0_);
   }

   public static void loadIdentity() {
      GL11.glLoadIdentity();
   }

   public static void pushMatrix() {
      GL11.glPushMatrix();
   }

   public static void popMatrix() {
      GL11.glPopMatrix();
   }

   public static void getFloatv(int p_179111_0_, FloatBuffer p_179111_1_) {
      GL11.glGetFloatv(p_179111_0_, p_179111_1_);
   }

   public static void ortho(double p_179130_0_, double p_179130_2_, double p_179130_4_, double p_179130_6_, double p_179130_8_, double p_179130_10_) {
      GL11.glOrtho(p_179130_0_, p_179130_2_, p_179130_4_, p_179130_6_, p_179130_8_, p_179130_10_);
   }

   public static void rotatef(float p_179114_0_, float p_179114_1_, float p_179114_2_, float p_179114_3_) {
      GL11.glRotatef(p_179114_0_, p_179114_1_, p_179114_2_, p_179114_3_);
   }

   public static void func_212477_a(double p_212477_0_, double p_212477_2_, double p_212477_4_, double p_212477_6_) {
      GL11.glRotated(p_212477_0_, p_212477_2_, p_212477_4_, p_212477_6_);
   }

   public static void scalef(float p_179152_0_, float p_179152_1_, float p_179152_2_) {
      GL11.glScalef(p_179152_0_, p_179152_1_, p_179152_2_);
   }

   public static void scaled(double p_179139_0_, double p_179139_2_, double p_179139_4_) {
      GL11.glScaled(p_179139_0_, p_179139_2_, p_179139_4_);
   }

   public static void translatef(float p_179109_0_, float p_179109_1_, float p_179109_2_) {
      GL11.glTranslatef(p_179109_0_, p_179109_1_, p_179109_2_);
   }

   public static void translated(double p_179137_0_, double p_179137_2_, double p_179137_4_) {
      GL11.glTranslated(p_179137_0_, p_179137_2_, p_179137_4_);
   }

   public static void multMatrixf(FloatBuffer p_179110_0_) {
      GL11.glMultMatrixf(p_179110_0_);
   }

   public static void multMatrixf(Matrix4f p_199294_0_) {
      p_199294_0_.write(BUF_FLOAT_16);
      BUF_FLOAT_16.rewind();
      GL11.glMultMatrixf(BUF_FLOAT_16);
   }

   public static void color4f(float p_179131_0_, float p_179131_1_, float p_179131_2_, float p_179131_3_) {
      if (p_179131_0_ != COLOR.red || p_179131_1_ != COLOR.green || p_179131_2_ != COLOR.blue || p_179131_3_ != COLOR.alpha) {
         COLOR.red = p_179131_0_;
         COLOR.green = p_179131_1_;
         COLOR.blue = p_179131_2_;
         COLOR.alpha = p_179131_3_;
         GL11.glColor4f(p_179131_0_, p_179131_1_, p_179131_2_, p_179131_3_);
      }

   }

   public static void color3f(float p_179124_0_, float p_179124_1_, float p_179124_2_) {
      color4f(p_179124_0_, p_179124_1_, p_179124_2_, 1.0F);
   }

   public static void resetColor() {
      COLOR.red = -1.0F;
      COLOR.green = -1.0F;
      COLOR.blue = -1.0F;
      COLOR.alpha = -1.0F;
   }

   public static void normalPointer(int p_204611_0_, int p_204611_1_, int p_204611_2_) {
      GL11.glNormalPointer(p_204611_0_, p_204611_1_, (long)p_204611_2_);
   }

   public static void normalPointer(int p_187446_0_, int p_187446_1_, ByteBuffer p_187446_2_) {
      GL11.glNormalPointer(p_187446_0_, p_187446_1_, p_187446_2_);
   }

   public static void texCoordPointer(int p_187405_0_, int p_187405_1_, int p_187405_2_, int p_187405_3_) {
      GL11.glTexCoordPointer(p_187405_0_, p_187405_1_, p_187405_2_, (long)p_187405_3_);
   }

   public static void texCoordPointer(int p_187404_0_, int p_187404_1_, int p_187404_2_, ByteBuffer p_187404_3_) {
      GL11.glTexCoordPointer(p_187404_0_, p_187404_1_, p_187404_2_, p_187404_3_);
   }

   public static void vertexPointer(int p_187420_0_, int p_187420_1_, int p_187420_2_, int p_187420_3_) {
      GL11.glVertexPointer(p_187420_0_, p_187420_1_, p_187420_2_, (long)p_187420_3_);
   }

   public static void vertexPointer(int p_187427_0_, int p_187427_1_, int p_187427_2_, ByteBuffer p_187427_3_) {
      GL11.glVertexPointer(p_187427_0_, p_187427_1_, p_187427_2_, p_187427_3_);
   }

   public static void colorPointer(int p_187406_0_, int p_187406_1_, int p_187406_2_, int p_187406_3_) {
      GL11.glColorPointer(p_187406_0_, p_187406_1_, p_187406_2_, (long)p_187406_3_);
   }

   public static void colorPointer(int p_187400_0_, int p_187400_1_, int p_187400_2_, ByteBuffer p_187400_3_) {
      GL11.glColorPointer(p_187400_0_, p_187400_1_, p_187400_2_, p_187400_3_);
   }

   public static void disableClientState(int p_187429_0_) {
      GL11.glDisableClientState(p_187429_0_);
   }

   public static void enableClientState(int p_187410_0_) {
      GL11.glEnableClientState(p_187410_0_);
   }

   public static void drawArrays(int p_187439_0_, int p_187439_1_, int p_187439_2_) {
      GL11.glDrawArrays(p_187439_0_, p_187439_1_, p_187439_2_);
   }

   public static void lineWidth(float p_187441_0_) {
      GL11.glLineWidth(p_187441_0_);
   }

   public static void callList(int p_179148_0_) {
      GL11.glCallList(p_179148_0_);
   }

   public static void deleteLists(int p_187449_0_, int p_187449_1_) {
      GL11.glDeleteLists(p_187449_0_, p_187449_1_);
   }

   public static void newList(int p_187423_0_, int p_187423_1_) {
      GL11.glNewList(p_187423_0_, p_187423_1_);
   }

   public static void endList() {
      GL11.glEndList();
   }

   public static int genLists(int p_187442_0_) {
      return GL11.glGenLists(p_187442_0_);
   }

   public static void pixelStorei(int p_187425_0_, int p_187425_1_) {
      GL11.glPixelStorei(p_187425_0_, p_187425_1_);
   }

   public static void pixelTransferf(int p_199297_0_, float p_199297_1_) {
      GL11.glPixelTransferf(p_199297_0_, p_199297_1_);
   }

   public static void readPixels(int p_199296_0_, int p_199296_1_, int p_199296_2_, int p_199296_3_, int p_199296_4_, int p_199296_5_, long p_199296_6_) {
      GL11.glReadPixels(p_199296_0_, p_199296_1_, p_199296_2_, p_199296_3_, p_199296_4_, p_199296_5_, p_199296_6_);
   }

   public static int getError() {
      return GL11.glGetError();
   }

   public static String getString(int p_187416_0_) {
      return GL11.glGetString(p_187416_0_);
   }

   public static void enableBlendProfile(GlStateManager.Profile p_187408_0_) {
      p_187408_0_.apply();
   }

   public static void disableBlendProfile(GlStateManager.Profile p_187440_0_) {
      p_187440_0_.clean();
   }

   @OnlyIn(Dist.CLIENT)
   static class AlphaState {
      public GlStateManager.BooleanState test = new GlStateManager.BooleanState(3008);
      public int func = 519;
      public float ref = -1.0F;

      private AlphaState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class BlendState {
      public GlStateManager.BooleanState blend = new GlStateManager.BooleanState(3042);
      public int srcFactor = 1;
      public int dstFactor = 0;
      public int srcFactorAlpha = 1;
      public int dstFactorAlpha = 0;

      private BlendState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class BooleanState {
      private final int capability;
      private boolean currentState;

      public BooleanState(int p_i46267_1_) {
         this.capability = p_i46267_1_;
      }

      public void setDisabled() {
         this.setState(false);
      }

      public void setEnabled() {
         this.setState(true);
      }

      public void setState(boolean p_179199_1_) {
         if (p_179199_1_ != this.currentState) {
            this.currentState = p_179199_1_;
            if (p_179199_1_) {
               GL11.glEnable(this.capability);
            } else {
               GL11.glDisable(this.capability);
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   static class ClearState {
      public double depth = 1.0D;
      public GlStateManager.Color color = new GlStateManager.Color(0.0F, 0.0F, 0.0F, 0.0F);

      private ClearState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class Color {
      public float red = 1.0F;
      public float green = 1.0F;
      public float blue = 1.0F;
      public float alpha = 1.0F;

      public Color() {
         this(1.0F, 1.0F, 1.0F, 1.0F);
      }

      public Color(float p_i46265_1_, float p_i46265_2_, float p_i46265_3_, float p_i46265_4_) {
         this.red = p_i46265_1_;
         this.green = p_i46265_2_;
         this.blue = p_i46265_3_;
         this.alpha = p_i46265_4_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class ColorLogicState {
      public GlStateManager.BooleanState colorLogicOp = new GlStateManager.BooleanState(3058);
      public int opcode = 5379;

      private ColorLogicState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class ColorMask {
      public boolean red = true;
      public boolean green = true;
      public boolean blue = true;
      public boolean alpha = true;

      private ColorMask() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class ColorMaterialState {
      public GlStateManager.BooleanState colorMaterial = new GlStateManager.BooleanState(2903);
      public int face = 1032;
      public int mode = 5634;

      private ColorMaterialState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum CullFace {
      FRONT(1028),
      BACK(1029),
      FRONT_AND_BACK(1032);

      public final int mode;

      private CullFace(int p_i46520_3_) {
         this.mode = p_i46520_3_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class CullState {
      public GlStateManager.BooleanState cullFace = new GlStateManager.BooleanState(2884);
      public int mode = 1029;

      private CullState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class DepthState {
      public GlStateManager.BooleanState test = new GlStateManager.BooleanState(2929);
      public boolean mask = true;
      public int func = 513;

      private DepthState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum DestFactor {
      CONSTANT_ALPHA(32771),
      CONSTANT_COLOR(32769),
      DST_ALPHA(772),
      DST_COLOR(774),
      ONE(1),
      ONE_MINUS_CONSTANT_ALPHA(32772),
      ONE_MINUS_CONSTANT_COLOR(32770),
      ONE_MINUS_DST_ALPHA(773),
      ONE_MINUS_DST_COLOR(775),
      ONE_MINUS_SRC_ALPHA(771),
      ONE_MINUS_SRC_COLOR(769),
      SRC_ALPHA(770),
      SRC_COLOR(768),
      ZERO(0);

      public final int factor;

      private DestFactor(int p_i46519_3_) {
         this.factor = p_i46519_3_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum FogMode {
      LINEAR(9729),
      EXP(2048),
      EXP2(2049);

      public final int capabilityId;

      private FogMode(int p_i46518_3_) {
         this.capabilityId = p_i46518_3_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class FogState {
      public GlStateManager.BooleanState fog = new GlStateManager.BooleanState(2912);
      public int mode = 2048;
      public float density = 1.0F;
      public float start;
      public float end = 1.0F;

      private FogState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum LogicOp {
      AND(5377),
      AND_INVERTED(5380),
      AND_REVERSE(5378),
      CLEAR(5376),
      COPY(5379),
      COPY_INVERTED(5388),
      EQUIV(5385),
      INVERT(5386),
      NAND(5390),
      NOOP(5381),
      NOR(5384),
      OR(5383),
      OR_INVERTED(5389),
      OR_REVERSE(5387),
      SET(5391),
      XOR(5382);

      public final int opcode;

      private LogicOp(int p_i46517_3_) {
         this.opcode = p_i46517_3_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class PolygonOffsetState {
      public GlStateManager.BooleanState fill = new GlStateManager.BooleanState(32823);
      public GlStateManager.BooleanState line = new GlStateManager.BooleanState(10754);
      public float factor;
      public float units;

      private PolygonOffsetState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Profile {
      DEFAULT {
         public void apply() {
            GlStateManager.disableAlphaTest();
            GlStateManager.alphaFunc(519, 0.0F);
            GlStateManager.disableLighting();
            GlStateManager.lightModelfv(2899, RenderHelper.setColorBuffer(0.2F, 0.2F, 0.2F, 1.0F));

            for(int i = 0; i < 8; ++i) {
               GlStateManager.disableLight(i);
               GlStateManager.lightfv(16384 + i, 4608, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
               GlStateManager.lightfv(16384 + i, 4611, RenderHelper.setColorBuffer(0.0F, 0.0F, 1.0F, 0.0F));
               if (i == 0) {
                  GlStateManager.lightfv(16384 + i, 4609, RenderHelper.setColorBuffer(1.0F, 1.0F, 1.0F, 1.0F));
                  GlStateManager.lightfv(16384 + i, 4610, RenderHelper.setColorBuffer(1.0F, 1.0F, 1.0F, 1.0F));
               } else {
                  GlStateManager.lightfv(16384 + i, 4609, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                  GlStateManager.lightfv(16384 + i, 4610, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
               }
            }

            GlStateManager.disableColorMaterial();
            GlStateManager.colorMaterial(1032, 5634);
            GlStateManager.disableDepthTest();
            GlStateManager.depthFunc(513);
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendEquation(32774);
            GlStateManager.disableFog();
            GlStateManager.fogi(2917, 2048);
            GlStateManager.fogDensity(1.0F);
            GlStateManager.fogStart(0.0F);
            GlStateManager.fogEnd(1.0F);
            GlStateManager.fogfv(2918, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            if (GL.getCapabilities().GL_NV_fog_distance) {
               GlStateManager.fogi(2917, 34140);
            }

            GlStateManager.polygonOffset(0.0F, 0.0F);
            GlStateManager.disableColorLogic();
            GlStateManager.logicOp(5379);
            GlStateManager.disableTexGen(GlStateManager.TexGen.S);
            GlStateManager.texGenMode(GlStateManager.TexGen.S, 9216);
            GlStateManager.texGenParam(GlStateManager.TexGen.S, 9474, RenderHelper.setColorBuffer(1.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.S, 9217, RenderHelper.setColorBuffer(1.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.disableTexGen(GlStateManager.TexGen.T);
            GlStateManager.texGenMode(GlStateManager.TexGen.T, 9216);
            GlStateManager.texGenParam(GlStateManager.TexGen.T, 9474, RenderHelper.setColorBuffer(0.0F, 1.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.T, 9217, RenderHelper.setColorBuffer(0.0F, 1.0F, 0.0F, 0.0F));
            GlStateManager.disableTexGen(GlStateManager.TexGen.R);
            GlStateManager.texGenMode(GlStateManager.TexGen.R, 9216);
            GlStateManager.texGenParam(GlStateManager.TexGen.R, 9474, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.R, 9217, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.disableTexGen(GlStateManager.TexGen.Q);
            GlStateManager.texGenMode(GlStateManager.TexGen.Q, 9216);
            GlStateManager.texGenParam(GlStateManager.TexGen.Q, 9474, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.Q, 9217, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.activeTexture(0);
            GlStateManager.texParameteri(3553, 10240, 9729);
            GlStateManager.texParameteri(3553, 10241, 9986);
            GlStateManager.texParameteri(3553, 10242, 10497);
            GlStateManager.texParameteri(3553, 10243, 10497);
            GlStateManager.texParameteri(3553, 33085, 1000);
            GlStateManager.texParameteri(3553, 33083, 1000);
            GlStateManager.texParameteri(3553, 33082, -1000);
            GlStateManager.texParameterf(3553, 34049, 0.0F);
            GlStateManager.texEnvi(8960, 8704, 8448);
            GlStateManager.texEnvfv(8960, 8705, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texEnvi(8960, 34161, 8448);
            GlStateManager.texEnvi(8960, 34162, 8448);
            GlStateManager.texEnvi(8960, 34176, 5890);
            GlStateManager.texEnvi(8960, 34177, 34168);
            GlStateManager.texEnvi(8960, 34178, 34166);
            GlStateManager.texEnvi(8960, 34184, 5890);
            GlStateManager.texEnvi(8960, 34185, 34168);
            GlStateManager.texEnvi(8960, 34186, 34166);
            GlStateManager.texEnvi(8960, 34192, 768);
            GlStateManager.texEnvi(8960, 34193, 768);
            GlStateManager.texEnvi(8960, 34194, 770);
            GlStateManager.texEnvi(8960, 34200, 770);
            GlStateManager.texEnvi(8960, 34201, 770);
            GlStateManager.texEnvi(8960, 34202, 770);
            GlStateManager.texEnvf(8960, 34163, 1.0F);
            GlStateManager.texEnvf(8960, 3356, 1.0F);
            GlStateManager.disableNormalize();
            GlStateManager.shadeModel(7425);
            GlStateManager.disableRescaleNormal();
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.clearDepth(1.0D);
            GlStateManager.lineWidth(1.0F);
            GlStateManager.normal3f(0.0F, 0.0F, 1.0F);
            GlStateManager.polygonMode(1028, 6914);
            GlStateManager.polygonMode(1029, 6914);
         }

         public void clean() {
         }
      },
      PLAYER_SKIN {
         public void apply() {
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(770, 771, 1, 0);
         }

         public void clean() {
            GlStateManager.disableBlend();
         }
      },
      TRANSPARENT_MODEL {
         public void apply() {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.15F);
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.alphaFunc(516, 0.003921569F);
         }

         public void clean() {
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.depthMask(true);
         }
      };

      private Profile() {
      }

      public abstract void apply();

      public abstract void clean();
   }

   @OnlyIn(Dist.CLIENT)
   public static enum SourceFactor {
      CONSTANT_ALPHA(32771),
      CONSTANT_COLOR(32769),
      DST_ALPHA(772),
      DST_COLOR(774),
      ONE(1),
      ONE_MINUS_CONSTANT_ALPHA(32772),
      ONE_MINUS_CONSTANT_COLOR(32770),
      ONE_MINUS_DST_ALPHA(773),
      ONE_MINUS_DST_COLOR(775),
      ONE_MINUS_SRC_ALPHA(771),
      ONE_MINUS_SRC_COLOR(769),
      SRC_ALPHA(770),
      SRC_ALPHA_SATURATE(776),
      SRC_COLOR(768),
      ZERO(0);

      public final int factor;

      private SourceFactor(int p_i46514_3_) {
         this.factor = p_i46514_3_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class StencilFunc {
      public int func = 519;
      public int mask = -1;

      private StencilFunc() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class StencilState {
      public GlStateManager.StencilFunc func = new GlStateManager.StencilFunc();
      public int mask = -1;
      public int fail = 7680;
      public int zfail = 7680;
      public int zpass = 7680;

      private StencilState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum TexGen {
      S,
      T,
      R,
      Q;
   }

   @OnlyIn(Dist.CLIENT)
   static class TexGenCoord {
      public GlStateManager.BooleanState textureGen;
      public int coord;
      public int mode = -1;

      public TexGenCoord(int p_i46254_1_, int p_i46254_2_) {
         this.coord = p_i46254_1_;
         this.textureGen = new GlStateManager.BooleanState(p_i46254_2_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class TexGenState {
      public GlStateManager.TexGenCoord s = new GlStateManager.TexGenCoord(8192, 3168);
      public GlStateManager.TexGenCoord t = new GlStateManager.TexGenCoord(8193, 3169);
      public GlStateManager.TexGenCoord r = new GlStateManager.TexGenCoord(8194, 3170);
      public GlStateManager.TexGenCoord q = new GlStateManager.TexGenCoord(8195, 3171);

      private TexGenState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class TextureState {
      public GlStateManager.BooleanState texture2DState = new GlStateManager.BooleanState(3553);
      public int textureName;

      private TextureState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Viewport {
      INSTANCE;

      protected int x;
      protected int y;
      protected int width;
      protected int height;
   }
}
