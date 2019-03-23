package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleSpell extends Particle {
   private static final Random RANDOM = new Random();
   private int baseSpellTextureIndex = 128;

   protected ParticleSpell(World p_i1229_1_, double p_i1229_2_, double p_i1229_4_, double p_i1229_6_, double p_i1229_8_, double p_i1229_10_, double p_i1229_12_) {
      super(p_i1229_1_, p_i1229_2_, p_i1229_4_, p_i1229_6_, 0.5D - RANDOM.nextDouble(), p_i1229_10_, 0.5D - RANDOM.nextDouble());
      this.motionY *= (double)0.2F;
      if (p_i1229_8_ == 0.0D && p_i1229_12_ == 0.0D) {
         this.motionX *= (double)0.1F;
         this.motionZ *= (double)0.1F;
      }

      this.particleScale *= 0.75F;
      this.maxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
      this.canCollide = false;
   }

   public boolean shouldDisableDepth() {
      return true;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      }

      this.setParticleTextureIndex(this.baseSpellTextureIndex + 7 - this.age * 8 / this.maxAge);
      this.motionY += 0.004D;
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

   public void setBaseSpellTextureIndex(int p_70589_1_) {
      this.baseSpellTextureIndex = p_70589_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public static class AmbientMobFactory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         Particle particle = new ParticleSpell(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
         particle.setAlphaF(0.15F);
         particle.setColor((float)p_199234_9_, (float)p_199234_11_, (float)p_199234_13_);
         return particle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new ParticleSpell(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class InstantFactory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         Particle particle = new ParticleSpell(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
         ((ParticleSpell)particle).setBaseSpellTextureIndex(144);
         return particle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class MobFactory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         Particle particle = new ParticleSpell(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
         particle.setColor((float)p_199234_9_, (float)p_199234_11_, (float)p_199234_13_);
         return particle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class WitchFactory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         Particle particle = new ParticleSpell(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
         ((ParticleSpell)particle).setBaseSpellTextureIndex(144);
         float f = p_199234_2_.rand.nextFloat() * 0.5F + 0.35F;
         particle.setColor(1.0F * f, 0.0F * f, 1.0F * f);
         return particle;
      }
   }
}
