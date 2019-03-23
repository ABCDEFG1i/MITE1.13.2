package net.minecraft.client.particle;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleCrit extends Particle {
   private final float oSize;

   protected ParticleCrit(World p_i46284_1_, double p_i46284_2_, double p_i46284_4_, double p_i46284_6_, double p_i46284_8_, double p_i46284_10_, double p_i46284_12_) {
      this(p_i46284_1_, p_i46284_2_, p_i46284_4_, p_i46284_6_, p_i46284_8_, p_i46284_10_, p_i46284_12_, 1.0F);
   }

   protected ParticleCrit(World p_i46285_1_, double p_i46285_2_, double p_i46285_4_, double p_i46285_6_, double p_i46285_8_, double p_i46285_10_, double p_i46285_12_, float p_i46285_14_) {
      super(p_i46285_1_, p_i46285_2_, p_i46285_4_, p_i46285_6_, 0.0D, 0.0D, 0.0D);
      this.motionX *= (double)0.1F;
      this.motionY *= (double)0.1F;
      this.motionZ *= (double)0.1F;
      this.motionX += p_i46285_8_ * 0.4D;
      this.motionY += p_i46285_10_ * 0.4D;
      this.motionZ += p_i46285_12_ * 0.4D;
      float f = (float)(Math.random() * (double)0.3F + (double)0.6F);
      this.particleRed = f;
      this.particleGreen = f;
      this.particleBlue = f;
      this.particleScale *= 0.75F;
      this.particleScale *= p_i46285_14_;
      this.oSize = this.particleScale;
      this.maxAge = (int)(6.0D / (Math.random() * 0.8D + 0.6D));
      this.maxAge = (int)((float)this.maxAge * p_i46285_14_);
      this.maxAge = Math.max(this.maxAge, 1);
      this.canCollide = false;
      this.setParticleTextureIndex(65);
      this.tick();
   }

   public void renderParticle(BufferBuilder p_180434_1_, Entity p_180434_2_, float p_180434_3_, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
      float f = ((float)this.age + p_180434_3_) / (float)this.maxAge * 32.0F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      this.particleScale = this.oSize * f;
      super.renderParticle(p_180434_1_, p_180434_2_, p_180434_3_, p_180434_4_, p_180434_5_, p_180434_6_, p_180434_7_, p_180434_8_);
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      }

      this.move(this.motionX, this.motionY, this.motionZ);
      this.particleGreen = (float)((double)this.particleGreen * 0.96D);
      this.particleBlue = (float)((double)this.particleBlue * 0.9D);
      this.motionX *= (double)0.7F;
      this.motionY *= (double)0.7F;
      this.motionZ *= (double)0.7F;
      this.motionY -= (double)0.02F;
      if (this.onGround) {
         this.motionX *= (double)0.7F;
         this.motionZ *= (double)0.7F;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class DamageIndicatorFactory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         Particle particle = new ParticleCrit(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_ + 1.0D, p_199234_13_, 1.0F);
         particle.setMaxAge(20);
         particle.setParticleTextureIndex(67);
         return particle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new ParticleCrit(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class MagicFactory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         Particle particle = new ParticleCrit(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
         particle.setColor(particle.getRedColorF() * 0.3F, particle.getGreenColorF() * 0.8F, particle.getBlueColorF());
         particle.nextTextureIndexX();
         return particle;
      }
   }
}
