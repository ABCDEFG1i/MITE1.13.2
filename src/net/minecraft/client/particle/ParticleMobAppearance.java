package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleMobAppearance extends Particle {
   private EntityLivingBase entity;

   protected ParticleMobAppearance(World p_i46283_1_, double p_i46283_2_, double p_i46283_4_, double p_i46283_6_) {
      super(p_i46283_1_, p_i46283_2_, p_i46283_4_, p_i46283_6_, 0.0D, 0.0D, 0.0D);
      this.particleRed = 1.0F;
      this.particleGreen = 1.0F;
      this.particleBlue = 1.0F;
      this.motionX = 0.0D;
      this.motionY = 0.0D;
      this.motionZ = 0.0D;
      this.particleGravity = 0.0F;
      this.maxAge = 30;
   }

   public int getFXLayer() {
      return 3;
   }

   public void tick() {
      super.tick();
      if (this.entity == null) {
         EntityElderGuardian entityelderguardian = new EntityElderGuardian(this.world);
         entityelderguardian.setGhost();
         this.entity = entityelderguardian;
      }

   }

   public void renderParticle(BufferBuilder p_180434_1_, Entity p_180434_2_, float p_180434_3_, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
      if (this.entity != null) {
         RenderManager rendermanager = Minecraft.getInstance().getRenderManager();
         rendermanager.setRenderPosition(Particle.interpPosX, Particle.interpPosY, Particle.interpPosZ);
         float f = 0.42553192F;
         float f1 = ((float)this.age + p_180434_3_) / (float)this.maxAge;
         GlStateManager.depthMask(true);
         GlStateManager.enableBlend();
         GlStateManager.enableDepthTest();
         GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
         float f2 = 240.0F;
         OpenGlHelper.glMultiTexCoord2f(OpenGlHelper.GL_TEXTURE1, 240.0F, 240.0F);
         GlStateManager.pushMatrix();
         float f3 = 0.05F + 0.5F * MathHelper.sin(f1 * (float)Math.PI);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, f3);
         GlStateManager.translatef(0.0F, 1.8F, 0.0F);
         GlStateManager.rotatef(180.0F - p_180434_2_.rotationYaw, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(60.0F - 150.0F * f1 - p_180434_2_.rotationPitch, 1.0F, 0.0F, 0.0F);
         GlStateManager.translatef(0.0F, -0.4F, -1.5F);
         GlStateManager.scalef(0.42553192F, 0.42553192F, 0.42553192F);
         this.entity.rotationYaw = 0.0F;
         this.entity.rotationYawHead = 0.0F;
         this.entity.prevRotationYaw = 0.0F;
         this.entity.prevRotationYawHead = 0.0F;
         rendermanager.renderEntity(this.entity, 0.0D, 0.0D, 0.0D, 0.0F, p_180434_3_, false);
         GlStateManager.popMatrix();
         GlStateManager.enableDepthTest();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new ParticleMobAppearance(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_);
      }
   }
}
