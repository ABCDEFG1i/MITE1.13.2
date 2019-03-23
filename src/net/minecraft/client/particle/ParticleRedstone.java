package net.minecraft.client.particle;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleRedstone extends Particle {
   private final float reddustParticleScale;

   public ParticleRedstone(World p_i47642_1_, double p_i47642_2_, double p_i47642_4_, double p_i47642_6_, double p_i47642_8_, double p_i47642_10_, double p_i47642_12_, RedstoneParticleData p_i47642_14_) {
      super(p_i47642_1_, p_i47642_2_, p_i47642_4_, p_i47642_6_, p_i47642_8_, p_i47642_10_, p_i47642_12_);
      this.motionX *= (double)0.1F;
      this.motionY *= (double)0.1F;
      this.motionZ *= (double)0.1F;
      float f = (float)Math.random() * 0.4F + 0.6F;
      this.particleRed = ((float)(Math.random() * (double)0.2F) + 0.8F) * p_i47642_14_.getRed() * f;
      this.particleGreen = ((float)(Math.random() * (double)0.2F) + 0.8F) * p_i47642_14_.getGreen() * f;
      this.particleBlue = ((float)(Math.random() * (double)0.2F) + 0.8F) * p_i47642_14_.getBlue() * f;
      this.particleScale *= 0.75F;
      this.particleScale *= p_i47642_14_.getAlpha();
      this.reddustParticleScale = this.particleScale;
      this.maxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
      this.maxAge = (int)((float)this.maxAge * p_i47642_14_.getAlpha());
      this.maxAge = Math.max(this.maxAge, 1);
   }

   public void renderParticle(BufferBuilder p_180434_1_, Entity p_180434_2_, float p_180434_3_, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
      float f = ((float)this.age + p_180434_3_) / (float)this.maxAge * 32.0F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      this.particleScale = this.reddustParticleScale * f;
      super.renderParticle(p_180434_1_, p_180434_2_, p_180434_3_, p_180434_4_, p_180434_5_, p_180434_6_, p_180434_7_, p_180434_8_);
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      }

      this.setParticleTextureIndex(7 - this.age * 8 / this.maxAge);
      this.move(this.motionX, this.motionY, this.motionZ);
      if (this.posY == this.prevPosY) {
         this.motionX *= 1.1D;
         this.motionZ *= 1.1D;
      }

      this.motionX *= (double)0.96F;
      this.motionY *= (double)0.96F;
      this.motionZ *= (double)0.96F;
      if (this.onGround) {
         this.motionX *= (double)0.7F;
         this.motionZ *= (double)0.7F;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<RedstoneParticleData> {
      public Particle makeParticle(RedstoneParticleData p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new ParticleRedstone(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, p_199234_1_);
      }
   }
}
