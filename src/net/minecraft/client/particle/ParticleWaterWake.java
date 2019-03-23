package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleWaterWake extends Particle {
   protected ParticleWaterWake(World p_i45073_1_, double p_i45073_2_, double p_i45073_4_, double p_i45073_6_, double p_i45073_8_, double p_i45073_10_, double p_i45073_12_) {
      super(p_i45073_1_, p_i45073_2_, p_i45073_4_, p_i45073_6_, 0.0D, 0.0D, 0.0D);
      this.motionX *= (double)0.3F;
      this.motionY = Math.random() * (double)0.2F + (double)0.1F;
      this.motionZ *= (double)0.3F;
      this.particleRed = 1.0F;
      this.particleGreen = 1.0F;
      this.particleBlue = 1.0F;
      this.setParticleTextureIndex(19);
      this.setSize(0.01F, 0.01F);
      this.maxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
      this.particleGravity = 0.0F;
      this.motionX = p_i45073_8_;
      this.motionY = p_i45073_10_;
      this.motionZ = p_i45073_12_;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.motionY -= (double)this.particleGravity;
      this.move(this.motionX, this.motionY, this.motionZ);
      this.motionX *= (double)0.98F;
      this.motionY *= (double)0.98F;
      this.motionZ *= (double)0.98F;
      int i = 60 - this.maxAge;
      float f = (float)i * 0.001F;
      this.setSize(f, f);
      this.setParticleTextureIndex(19 + i % 4);
      if (this.maxAge-- <= 0) {
         this.setExpired();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new ParticleWaterWake(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
      }
   }
}
