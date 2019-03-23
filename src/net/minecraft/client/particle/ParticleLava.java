package net.minecraft.client.particle;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Particles;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleLava extends Particle {
   private final float lavaParticleScale;

   protected ParticleLava(World p_i1215_1_, double p_i1215_2_, double p_i1215_4_, double p_i1215_6_) {
      super(p_i1215_1_, p_i1215_2_, p_i1215_4_, p_i1215_6_, 0.0D, 0.0D, 0.0D);
      this.motionX *= (double)0.8F;
      this.motionY *= (double)0.8F;
      this.motionZ *= (double)0.8F;
      this.motionY = (double)(this.rand.nextFloat() * 0.4F + 0.05F);
      this.particleRed = 1.0F;
      this.particleGreen = 1.0F;
      this.particleBlue = 1.0F;
      this.particleScale *= this.rand.nextFloat() * 2.0F + 0.2F;
      this.lavaParticleScale = this.particleScale;
      this.maxAge = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
      this.setParticleTextureIndex(49);
   }

   public int getBrightnessForRender(float p_189214_1_) {
      int i = super.getBrightnessForRender(p_189214_1_);
      int j = 240;
      int k = i >> 16 & 255;
      return 240 | k << 16;
   }

   public void renderParticle(BufferBuilder p_180434_1_, Entity p_180434_2_, float p_180434_3_, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
      float f = ((float)this.age + p_180434_3_) / (float)this.maxAge;
      this.particleScale = this.lavaParticleScale * (1.0F - f * f);
      super.renderParticle(p_180434_1_, p_180434_2_, p_180434_3_, p_180434_4_, p_180434_5_, p_180434_6_, p_180434_7_, p_180434_8_);
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      }

      float f = (float)this.age / (float)this.maxAge;
      if (this.rand.nextFloat() > f) {
         this.world.spawnParticle(Particles.SMOKE, this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ);
      }

      this.motionY -= 0.03D;
      this.move(this.motionX, this.motionY, this.motionZ);
      this.motionX *= (double)0.999F;
      this.motionY *= (double)0.999F;
      this.motionZ *= (double)0.999F;
      if (this.onGround) {
         this.motionX *= (double)0.7F;
         this.motionZ *= (double)0.7F;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new ParticleLava(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_);
      }
   }
}
