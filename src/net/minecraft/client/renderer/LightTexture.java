package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LightTexture implements AutoCloseable {
   private final DynamicTexture field_205110_a;
   private final NativeImage field_205111_b;
   private final ResourceLocation field_205112_c;
   private boolean field_205113_d;
   private float field_205114_e;
   private float field_205115_f;
   private final GameRenderer field_205116_g;
   private final Minecraft client;

   public LightTexture(GameRenderer p_i48950_1_) {
      this.field_205116_g = p_i48950_1_;
      this.client = p_i48950_1_.func_205000_l();
      this.field_205110_a = new DynamicTexture(16, 16, false);
      this.field_205112_c = this.client.getTextureManager().getDynamicTextureLocation("light_map", this.field_205110_a);
      this.field_205111_b = this.field_205110_a.func_195414_e();
   }

   public void close() {
      this.field_205110_a.close();
   }

   public void tick() {
      this.field_205115_f = (float)((double)this.field_205115_f + (Math.random() - Math.random()) * Math.random() * Math.random());
      this.field_205115_f = (float)((double)this.field_205115_f * 0.9D);
      this.field_205114_e += this.field_205115_f - this.field_205114_e;
      this.field_205113_d = true;
   }

   public void func_205108_b() {
      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE1);
      GlStateManager.disableTexture2D();
      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE0);
   }

   public void func_205109_c() {
      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE1);
      GlStateManager.matrixMode(5890);
      GlStateManager.loadIdentity();
      float f = 0.00390625F;
      GlStateManager.scalef(0.00390625F, 0.00390625F, 0.00390625F);
      GlStateManager.translatef(8.0F, 8.0F, 8.0F);
      GlStateManager.matrixMode(5888);
      this.client.getTextureManager().bindTexture(this.field_205112_c);
      GlStateManager.texParameteri(3553, 10241, 9729);
      GlStateManager.texParameteri(3553, 10240, 9729);
      GlStateManager.texParameteri(3553, 10242, 10496);
      GlStateManager.texParameteri(3553, 10243, 10496);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableTexture2D();
      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE0);
   }

   public void func_205106_a(float p_205106_1_) {
      if (this.field_205113_d) {
         this.client.profiler.startSection("lightTex");
         World world = this.client.world;
         if (world != null) {
            float f = world.getSunBrightness(1.0F);
            float f1 = f * 0.95F + 0.05F;
            float f3 = this.client.player.func_203719_J();
            float f2;
            if (this.client.player.isPotionActive(MobEffects.NIGHT_VISION)) {
               f2 = this.field_205116_g.func_180438_a(this.client.player, p_205106_1_);
            } else if (f3 > 0.0F && this.client.player.isPotionActive(MobEffects.CONDUIT_POWER)) {
               f2 = f3;
            } else {
               f2 = 0.0F;
            }

            for(int i = 0; i < 16; ++i) {
               for(int j = 0; j < 16; ++j) {
                  float f4 = world.dimension.getLightBrightnessTable()[i] * f1;
                  float f5 = world.dimension.getLightBrightnessTable()[j] * (this.field_205114_e * 0.1F + 1.5F);
                  if (world.getLastLightningBolt() > 0) {
                     f4 = world.dimension.getLightBrightnessTable()[i];
                  }

                  float f6 = f4 * (f * 0.65F + 0.35F);
                  float f7 = f4 * (f * 0.65F + 0.35F);
                  float f8 = f5 * ((f5 * 0.6F + 0.4F) * 0.6F + 0.4F);
                  float f9 = f5 * (f5 * f5 * 0.6F + 0.4F);
                  float f10 = f6 + f5;
                  float f11 = f7 + f8;
                  float f12 = f4 + f9;
                  f10 = f10 * 0.96F + 0.03F;
                  f11 = f11 * 0.96F + 0.03F;
                  f12 = f12 * 0.96F + 0.03F;
                  if (this.field_205116_g.func_205002_d(p_205106_1_) > 0.0F) {
                     float f13 = this.field_205116_g.func_205002_d(p_205106_1_);
                     f10 = f10 * (1.0F - f13) + f10 * 0.7F * f13;
                     f11 = f11 * (1.0F - f13) + f11 * 0.6F * f13;
                     f12 = f12 * (1.0F - f13) + f12 * 0.6F * f13;
                  }

                  if (world.dimension.getType() == DimensionType.THE_END) {
                     f10 = 0.22F + f5 * 0.75F;
                     f11 = 0.28F + f8 * 0.75F;
                     f12 = 0.25F + f9 * 0.75F;
                  }

                  if (f2 > 0.0F) {
                     float f17 = 1.0F / f10;
                     if (f17 > 1.0F / f11) {
                        f17 = 1.0F / f11;
                     }

                     if (f17 > 1.0F / f12) {
                        f17 = 1.0F / f12;
                     }

                     f10 = f10 * (1.0F - f2) + f10 * f17 * f2;
                     f11 = f11 * (1.0F - f2) + f11 * f17 * f2;
                     f12 = f12 * (1.0F - f2) + f12 * f17 * f2;
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

                  float f18 = (float)this.client.gameSettings.gammaSetting;
                  float f14 = 1.0F - f10;
                  float f15 = 1.0F - f11;
                  float f16 = 1.0F - f12;
                  f14 = 1.0F - f14 * f14 * f14 * f14;
                  f15 = 1.0F - f15 * f15 * f15 * f15;
                  f16 = 1.0F - f16 * f16 * f16 * f16;
                  f10 = f10 * (1.0F - f18) + f14 * f18;
                  f11 = f11 * (1.0F - f18) + f15 * f18;
                  f12 = f12 * (1.0F - f18) + f16 * f18;
                  f10 = f10 * 0.96F + 0.03F;
                  f11 = f11 * 0.96F + 0.03F;
                  f12 = f12 * 0.96F + 0.03F;
                  if (f10 > 1.0F) {
                     f10 = 1.0F;
                  }

                  if (f11 > 1.0F) {
                     f11 = 1.0F;
                  }

                  if (f12 > 1.0F) {
                     f12 = 1.0F;
                  }

                  if (f10 < 0.0F) {
                     f10 = 0.0F;
                  }

                  if (f11 < 0.0F) {
                     f11 = 0.0F;
                  }

                  if (f12 < 0.0F) {
                     f12 = 0.0F;
                  }

                  int k = 255;
                  int l = (int)(f10 * 255.0F);
                  int i1 = (int)(f11 * 255.0F);
                  int j1 = (int)(f12 * 255.0F);
                  this.field_205111_b.setPixelRGBA(j, i, -16777216 | j1 << 16 | i1 << 8 | l);
               }
            }

            this.field_205110_a.updateDynamicTexture();
            this.field_205113_d = false;
            this.client.profiler.endSection();
         }
      }
   }
}
