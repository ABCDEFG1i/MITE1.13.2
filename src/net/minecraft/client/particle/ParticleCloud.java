package net.minecraft.client.particle;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleCloud extends Particle {
   private final float oSize;

   protected ParticleCloud(World p_i1221_1_, double p_i1221_2_, double p_i1221_4_, double p_i1221_6_, double p_i1221_8_, double p_i1221_10_, double p_i1221_12_) {
      super(p_i1221_1_, p_i1221_2_, p_i1221_4_, p_i1221_6_, 0.0D, 0.0D, 0.0D);
      float f = 2.5F;
      this.motionX *= (double)0.1F;
      this.motionY *= (double)0.1F;
      this.motionZ *= (double)0.1F;
      this.motionX += p_i1221_8_;
      this.motionY += p_i1221_10_;
      this.motionZ += p_i1221_12_;
      float f1 = 1.0F - (float)(Math.random() * (double)0.3F);
      this.particleRed = f1;
      this.particleGreen = f1;
      this.particleBlue = f1;
      this.particleScale *= 0.75F;
      this.particleScale *= 2.5F;
      this.oSize = this.particleScale;
      this.maxAge = (int)(8.0D / (Math.random() * 0.8D + 0.3D));
      this.maxAge = (int)((float)this.maxAge * 2.5F);
      this.maxAge = Math.max(this.maxAge, 1);
      this.canCollide = false;
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

      this.setParticleTextureIndex(7 - this.age * 8 / this.maxAge);
      this.move(this.motionX, this.motionY, this.motionZ);
      this.motionX *= (double)0.96F;
      this.motionY *= (double)0.96F;
      this.motionZ *= (double)0.96F;
      EntityPlayer entityplayer = this.world.getClosestPlayer(this.posX, this.posY, this.posZ, 2.0D, false);
      if (entityplayer != null) {
         AxisAlignedBB axisalignedbb = entityplayer.getEntityBoundingBox();
         if (this.posY > axisalignedbb.minY) {
            this.posY += (axisalignedbb.minY - this.posY) * 0.2D;
            this.motionY += (entityplayer.motionY - this.motionY) * 0.2D;
            this.setPosition(this.posX, this.posY, this.posZ);
         }
      }

      if (this.onGround) {
         this.motionX *= (double)0.7F;
         this.motionZ *= (double)0.7F;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new ParticleCloud(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
      }
   }
}
