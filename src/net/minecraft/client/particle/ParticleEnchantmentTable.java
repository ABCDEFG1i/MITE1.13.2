package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleEnchantmentTable extends Particle {
   private final double coordX;
   private final double coordY;
   private final double coordZ;

   protected ParticleEnchantmentTable(World p_i1204_1_, double p_i1204_2_, double p_i1204_4_, double p_i1204_6_, double p_i1204_8_, double p_i1204_10_, double p_i1204_12_) {
      super(p_i1204_1_, p_i1204_2_, p_i1204_4_, p_i1204_6_, p_i1204_8_, p_i1204_10_, p_i1204_12_);
      this.motionX = p_i1204_8_;
      this.motionY = p_i1204_10_;
      this.motionZ = p_i1204_12_;
      this.coordX = p_i1204_2_;
      this.coordY = p_i1204_4_;
      this.coordZ = p_i1204_6_;
      this.prevPosX = p_i1204_2_ + p_i1204_8_;
      this.prevPosY = p_i1204_4_ + p_i1204_10_;
      this.prevPosZ = p_i1204_6_ + p_i1204_12_;
      this.posX = this.prevPosX;
      this.posY = this.prevPosY;
      this.posZ = this.prevPosZ;
      float f = this.rand.nextFloat() * 0.6F + 0.4F;
      this.particleScale = this.rand.nextFloat() * 0.5F + 0.2F;
      this.particleRed = 0.9F * f;
      this.particleGreen = 0.9F * f;
      this.particleBlue = f;
      this.canCollide = false;
      this.maxAge = (int)(Math.random() * 10.0D) + 30;
      this.setParticleTextureIndex((int)(Math.random() * 26.0D + 1.0D + 224.0D));
   }

   public void move(double p_187110_1_, double p_187110_3_, double p_187110_5_) {
      this.setBoundingBox(this.getBoundingBox().offset(p_187110_1_, p_187110_3_, p_187110_5_));
      this.resetPositionToBB();
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
      f = 1.0F - f;
      float f1 = 1.0F - f;
      f1 = f1 * f1;
      f1 = f1 * f1;
      this.posX = this.coordX + this.motionX * (double)f;
      this.posY = this.coordY + this.motionY * (double)f - (double)(f1 * 1.2F);
      this.posZ = this.coordZ + this.motionZ * (double)f;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class EnchantmentTable implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new ParticleEnchantmentTable(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class NautilusFactory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         ParticleEnchantmentTable particleenchantmenttable = new ParticleEnchantmentTable(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
         particleenchantmenttable.setParticleTextureIndex(208);
         return particleenchantmenttable;
      }
   }
}
