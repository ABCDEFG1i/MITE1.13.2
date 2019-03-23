package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Biomes;
import net.minecraft.init.MobEffects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL;

@OnlyIn(Dist.CLIENT)
public class FogRenderer {
   private final FloatBuffer blackBuffer = GLAllocation.createDirectFloatBuffer(16);
   private final FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
   private float red;
   private float green;
   private float blue;
   private float lastRed = -1.0F;
   private float lastGreen = -1.0F;
   private float lastBlue = -1.0F;
   private int lastWaterFogColor = -1;
   private int waterFogColor = -1;
   private long waterFogUpdateTime = -1L;
   private final GameRenderer entityRenderer;
   private final Minecraft mc;

   public FogRenderer(GameRenderer p_i48951_1_) {
      this.entityRenderer = p_i48951_1_;
      this.mc = p_i48951_1_.func_205000_l();
      this.blackBuffer.put(0.0F).put(0.0F).put(0.0F).put(1.0F).flip();
   }

   public void updateFogColor(float p_78466_1_) {
      World world = this.mc.world;
      Entity entity = this.mc.getRenderViewEntity();
      ActiveRenderInfo.getBlockStateAtEntityViewpoint(this.mc.world, entity, p_78466_1_);
      IFluidState ifluidstate = ActiveRenderInfo.getFluidStateAtEntityViewpoint(this.mc.world, entity, p_78466_1_);
      if (ifluidstate.isTagged(FluidTags.WATER)) {
         this.updateWaterFog(entity, world, p_78466_1_);
      } else if (ifluidstate.isTagged(FluidTags.LAVA)) {
         this.red = 0.6F;
         this.green = 0.1F;
         this.blue = 0.0F;
         this.waterFogUpdateTime = -1L;
      } else {
         this.updateSurfaceFog(entity, world, p_78466_1_);
         this.waterFogUpdateTime = -1L;
      }

      double d0 = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)p_78466_1_) * world.dimension.getVoidFogYFactor();
      if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(MobEffects.BLINDNESS)) {
         int i = ((EntityLivingBase)entity).getActivePotionEffect(MobEffects.BLINDNESS).getDuration();
         if (i < 20) {
            d0 *= (double)(1.0F - (float)i / 20.0F);
         } else {
            d0 = 0.0D;
         }
      }

      if (d0 < 1.0D) {
         if (d0 < 0.0D) {
            d0 = 0.0D;
         }

         d0 = d0 * d0;
         this.red = (float)((double)this.red * d0);
         this.green = (float)((double)this.green * d0);
         this.blue = (float)((double)this.blue * d0);
      }

      if (this.entityRenderer.func_205002_d(p_78466_1_) > 0.0F) {
         float f = this.entityRenderer.func_205002_d(p_78466_1_);
         this.red = this.red * (1.0F - f) + this.red * 0.7F * f;
         this.green = this.green * (1.0F - f) + this.green * 0.6F * f;
         this.blue = this.blue * (1.0F - f) + this.blue * 0.6F * f;
      }

      if (ifluidstate.isTagged(FluidTags.WATER)) {
         float f1 = 0.0F;
         if (entity instanceof EntityPlayerSP) {
            EntityPlayerSP entityplayersp = (EntityPlayerSP)entity;
            f1 = entityplayersp.func_203719_J();
         }

         float f3 = 1.0F / this.red;
         if (f3 > 1.0F / this.green) {
            f3 = 1.0F / this.green;
         }

         if (f3 > 1.0F / this.blue) {
            f3 = 1.0F / this.blue;
         }

         this.red = this.red * (1.0F - f1) + this.red * f3 * f1;
         this.green = this.green * (1.0F - f1) + this.green * f3 * f1;
         this.blue = this.blue * (1.0F - f1) + this.blue * f3 * f1;
      } else if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(MobEffects.NIGHT_VISION)) {
         float f2 = this.entityRenderer.func_180438_a((EntityLivingBase)entity, p_78466_1_);
         float f4 = 1.0F / this.red;
         if (f4 > 1.0F / this.green) {
            f4 = 1.0F / this.green;
         }

         if (f4 > 1.0F / this.blue) {
            f4 = 1.0F / this.blue;
         }

         this.red = this.red * (1.0F - f2) + this.red * f4 * f2;
         this.green = this.green * (1.0F - f2) + this.green * f4 * f2;
         this.blue = this.blue * (1.0F - f2) + this.blue * f4 * f2;
      }

      GlStateManager.clearColor(this.red, this.green, this.blue, 0.0F);
   }

   private void updateSurfaceFog(Entity p_205089_1_, World p_205089_2_, float p_205089_3_) {
      float f = 0.25F + 0.75F * (float)this.mc.gameSettings.renderDistanceChunks / 32.0F;
      f = 1.0F - (float)Math.pow((double)f, 0.25D);
      Vec3d vec3d = p_205089_2_.getSkyColor(this.mc.getRenderViewEntity(), p_205089_3_);
      float f1 = (float)vec3d.x;
      float f2 = (float)vec3d.y;
      float f3 = (float)vec3d.z;
      Vec3d vec3d1 = p_205089_2_.getFogColor(p_205089_3_);
      this.red = (float)vec3d1.x;
      this.green = (float)vec3d1.y;
      this.blue = (float)vec3d1.z;
      if (this.mc.gameSettings.renderDistanceChunks >= 4) {
         double d0 = MathHelper.sin(p_205089_2_.getCelestialAngleRadians(p_205089_3_)) > 0.0F ? -1.0D : 1.0D;
         Vec3d vec3d2 = new Vec3d(d0, 0.0D, 0.0D);
         float f5 = (float)p_205089_1_.getLook(p_205089_3_).dotProduct(vec3d2);
         if (f5 < 0.0F) {
            f5 = 0.0F;
         }

         if (f5 > 0.0F) {
            float[] afloat = p_205089_2_.dimension.calcSunriseSunsetColors(p_205089_2_.getCelestialAngle(p_205089_3_), p_205089_3_);
            if (afloat != null) {
               f5 = f5 * afloat[3];
               this.red = this.red * (1.0F - f5) + afloat[0] * f5;
               this.green = this.green * (1.0F - f5) + afloat[1] * f5;
               this.blue = this.blue * (1.0F - f5) + afloat[2] * f5;
            }
         }
      }

      this.red += (f1 - this.red) * f;
      this.green += (f2 - this.green) * f;
      this.blue += (f3 - this.blue) * f;
      float f6 = p_205089_2_.getRainStrength(p_205089_3_);
      if (f6 > 0.0F) {
         float f4 = 1.0F - f6 * 0.5F;
         float f8 = 1.0F - f6 * 0.4F;
         this.red *= f4;
         this.green *= f4;
         this.blue *= f8;
      }

      float f7 = p_205089_2_.getThunderStrength(p_205089_3_);
      if (f7 > 0.0F) {
         float f9 = 1.0F - f7 * 0.5F;
         this.red *= f9;
         this.green *= f9;
         this.blue *= f9;
      }

   }

   private void updateWaterFog(Entity p_205086_1_, IWorldReaderBase p_205086_2_, float p_205086_3_) {
      long i = Util.milliTime();
      int j = p_205086_2_.getBiome(new BlockPos(ActiveRenderInfo.projectViewFromEntity(p_205086_1_, (double)p_205086_3_))).getWaterFogColor();
      if (this.waterFogUpdateTime < 0L) {
         this.lastWaterFogColor = j;
         this.waterFogColor = j;
         this.waterFogUpdateTime = i;
      }

      int k = this.lastWaterFogColor >> 16 & 255;
      int l = this.lastWaterFogColor >> 8 & 255;
      int i1 = this.lastWaterFogColor & 255;
      int j1 = this.waterFogColor >> 16 & 255;
      int k1 = this.waterFogColor >> 8 & 255;
      int l1 = this.waterFogColor & 255;
      float f = MathHelper.clamp((float)(i - this.waterFogUpdateTime) / 5000.0F, 0.0F, 1.0F);
      float f1 = (float)j1 + (float)(k - j1) * f;
      float f2 = (float)k1 + (float)(l - k1) * f;
      float f3 = (float)l1 + (float)(i1 - l1) * f;
      this.red = f1 / 255.0F;
      this.green = f2 / 255.0F;
      this.blue = f3 / 255.0F;
      if (this.lastWaterFogColor != j) {
         this.lastWaterFogColor = j;
         this.waterFogColor = MathHelper.floor(f1) << 16 | MathHelper.floor(f2) << 8 | MathHelper.floor(f3);
         this.waterFogUpdateTime = i;
      }

   }

   public void setupFog(int p_78468_1_, float p_78468_2_) {
      Entity entity = this.mc.getRenderViewEntity();
      this.applyFog(false);
      GlStateManager.normal3f(0.0F, -1.0F, 0.0F);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      IFluidState ifluidstate = ActiveRenderInfo.getFluidStateAtEntityViewpoint(this.mc.world, entity, p_78468_2_);
      if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(MobEffects.BLINDNESS)) {
         float f2 = 5.0F;
         int i = ((EntityLivingBase)entity).getActivePotionEffect(MobEffects.BLINDNESS).getDuration();
         if (i < 20) {
            f2 = 5.0F + (this.entityRenderer.func_205001_m() - 5.0F) * (1.0F - (float)i / 20.0F);
         }

         GlStateManager.fogMode(GlStateManager.FogMode.LINEAR);
         if (p_78468_1_ == -1) {
            GlStateManager.fogStart(0.0F);
            GlStateManager.fogEnd(f2 * 0.8F);
         } else {
            GlStateManager.fogStart(f2 * 0.25F);
            GlStateManager.fogEnd(f2);
         }

         if (GL.getCapabilities().GL_NV_fog_distance) {
            GlStateManager.fogi(34138, 34139);
         }
      } else if (ifluidstate.isTagged(FluidTags.WATER)) {
         GlStateManager.fogMode(GlStateManager.FogMode.EXP2);
         if (entity instanceof EntityLivingBase) {
            if (entity instanceof EntityPlayerSP) {
               EntityPlayerSP entityplayersp = (EntityPlayerSP)entity;
               float f = 0.05F - entityplayersp.func_203719_J() * entityplayersp.func_203719_J() * 0.03F;
               Biome biome = entityplayersp.world.getBiome(new BlockPos(entityplayersp));
               if (biome == Biomes.SWAMP || biome == Biomes.SWAMP_HILLS) {
                  f += 0.005F;
               }

               GlStateManager.fogDensity(f);
            } else {
               GlStateManager.fogDensity(0.05F);
            }
         } else {
            GlStateManager.fogDensity(0.1F);
         }
      } else if (ifluidstate.isTagged(FluidTags.LAVA)) {
         GlStateManager.fogMode(GlStateManager.FogMode.EXP);
         GlStateManager.fogDensity(2.0F);
      } else {
         float f1 = this.entityRenderer.func_205001_m();
         GlStateManager.fogMode(GlStateManager.FogMode.LINEAR);
         if (p_78468_1_ == -1) {
            GlStateManager.fogStart(0.0F);
            GlStateManager.fogEnd(f1);
         } else {
            GlStateManager.fogStart(f1 * 0.75F);
            GlStateManager.fogEnd(f1);
         }

         if (GL.getCapabilities().GL_NV_fog_distance) {
            GlStateManager.fogi(34138, 34139);
         }

         if (this.mc.world.dimension.doesXZShowFog((int)entity.posX, (int)entity.posZ) || this.mc.ingameGUI.getBossOverlay().shouldCreateFog()) {
            GlStateManager.fogStart(f1 * 0.05F);
            GlStateManager.fogEnd(Math.min(f1, 192.0F) * 0.5F);
         }
      }

      GlStateManager.enableColorMaterial();
      GlStateManager.enableFog();
      GlStateManager.colorMaterial(1028, 4608);
   }

   public void applyFog(boolean p_205090_1_) {
      if (p_205090_1_) {
         GlStateManager.fogfv(2918, this.blackBuffer);
      } else {
         GlStateManager.fogfv(2918, this.getFogBuffer());
      }

   }

   private FloatBuffer getFogBuffer() {
      if (this.lastRed != this.red || this.lastGreen != this.green || this.lastBlue != this.blue) {
         this.buffer.clear();
         this.buffer.put(this.red).put(this.green).put(this.blue).put(1.0F);
         this.buffer.flip();
         this.lastRed = this.red;
         this.lastGreen = this.green;
         this.lastBlue = this.blue;
      }

      return this.buffer;
   }
}
