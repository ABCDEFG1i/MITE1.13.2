package net.minecraft.client.particle;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticlePortal extends Particle {
   private final float portalParticleScale;
   private final double portalPosX;
   private final double portalPosY;
   private final double portalPosZ;

   protected ParticlePortal(World p_i46351_1_, double p_i46351_2_, double p_i46351_4_, double p_i46351_6_, double p_i46351_8_, double p_i46351_10_, double p_i46351_12_) {
      super(p_i46351_1_, p_i46351_2_, p_i46351_4_, p_i46351_6_, p_i46351_8_, p_i46351_10_, p_i46351_12_);
      this.motionX = p_i46351_8_;
      this.motionY = p_i46351_10_;
      this.motionZ = p_i46351_12_;
      this.posX = p_i46351_2_;
      this.posY = p_i46351_4_;
      this.posZ = p_i46351_6_;
      this.portalPosX = this.posX;
      this.portalPosY = this.posY;
      this.portalPosZ = this.posZ;
      float f = this.rand.nextFloat() * 0.6F + 0.4F;
      this.particleScale = this.rand.nextFloat() * 0.2F + 0.5F;
      this.portalParticleScale = this.particleScale;
      this.particleRed = f * 0.9F;
      this.particleGreen = f * 0.3F;
      this.particleBlue = f;
      this.maxAge = (int)(Math.random() * 10.0D) + 40;
      this.setParticleTextureIndex((int)(Math.random() * 8.0D));
   }

   public void move(double p_187110_1_, double p_187110_3_, double p_187110_5_) {
      this.setBoundingBox(this.getBoundingBox().offset(p_187110_1_, p_187110_3_, p_187110_5_));
      this.resetPositionToBB();
   }

   public void renderParticle(BufferBuilder p_180434_1_, Entity p_180434_2_, float p_180434_3_, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
      float f = ((float)this.age + p_180434_3_) / (float)this.maxAge;
      f = 1.0F - f;
      f = f * f;
      f = 1.0F - f;
      this.particleScale = this.portalParticleScale * f;
      super.renderParticle(p_180434_1_, p_180434_2_, p_180434_3_, p_180434_4_, p_180434_5_, p_180434_6_, p_180434_7_, p_180434_8_);
   }

   public int getBrightnessForRender(float p_189214_1_) {
      int i = super.getBrightnessForRender(p_189214_1_);
      float f = (float)this.age / (float)this.maxAge;
      f = f * f;
      f = f * f;
      int j = i & 255;
      int k = i >> 16 & 255;
      k = k + (int)(f * 15.0F * 16.0F);
      if (k > 240) {
         k = 240;
      }

      return j | k << 16;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      float f = (float)this.age / (float)this.maxAge;
      float f1 = -f + f * f * 2.0F;
      float f2 = 1.0F - f1;
      this.posX = this.portalPosX + this.motionX * (double)f2;
      this.posY = this.portalPosY + this.motionY * (double)f2 + (double)(1.0F - f);
      this.posZ = this.portalPosZ + this.motionZ * (double)f2;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new ParticlePortal(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
      }
   }
}
