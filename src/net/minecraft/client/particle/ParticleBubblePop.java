package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleBubblePop extends Particle {
   protected ParticleBubblePop(World p_i48832_1_, double p_i48832_2_, double p_i48832_4_, double p_i48832_6_, double p_i48832_8_, double p_i48832_10_, double p_i48832_12_) {
      super(p_i48832_1_, p_i48832_2_, p_i48832_4_, p_i48832_6_, 0.0D, 0.0D, 0.0D);
      this.particleRed = 1.0F;
      this.particleGreen = 1.0F;
      this.particleBlue = 1.0F;
      this.setParticleTextureIndex(256);
      this.maxAge = 4;
      this.particleGravity = 0.008F;
      this.motionX = p_i48832_8_;
      this.motionY = p_i48832_10_;
      this.motionZ = p_i48832_12_;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.motionY -= (double)this.particleGravity;
      this.move(this.motionX, this.motionY, this.motionZ);
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      } else {
         int i = this.age * 5 / this.maxAge;
         if (i <= 4) {
            this.setParticleTextureIndex(256 + i);
         }
      }
   }

   public void renderParticle(BufferBuilder p_180434_1_, Entity p_180434_2_, float p_180434_3_, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
      float f = (float)this.particleTextureIndexX / 32.0F;
      float f1 = f + 0.0624375F;
      float f2 = (float)this.particleTextureIndexY / 32.0F;
      float f3 = f2 + 0.0624375F;
      float f4 = 0.1F * this.particleScale;
      if (this.particleTexture != null) {
         f = this.particleTexture.getMinU();
         f1 = this.particleTexture.getMaxU();
         f2 = this.particleTexture.getMinV();
         f3 = this.particleTexture.getMaxV();
      }

      float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)p_180434_3_ - interpPosX);
      float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)p_180434_3_ - interpPosY);
      float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)p_180434_3_ - interpPosZ);
      int i = this.getBrightnessForRender(p_180434_3_);
      int j = i >> 16 & '\uffff';
      int k = i & '\uffff';
      Vec3d[] avec3d = new Vec3d[]{new Vec3d((double)(-p_180434_4_ * f4 - p_180434_7_ * f4), (double)(-p_180434_5_ * f4), (double)(-p_180434_6_ * f4 - p_180434_8_ * f4)), new Vec3d((double)(-p_180434_4_ * f4 + p_180434_7_ * f4), (double)(p_180434_5_ * f4), (double)(-p_180434_6_ * f4 + p_180434_8_ * f4)), new Vec3d((double)(p_180434_4_ * f4 + p_180434_7_ * f4), (double)(p_180434_5_ * f4), (double)(p_180434_6_ * f4 + p_180434_8_ * f4)), new Vec3d((double)(p_180434_4_ * f4 - p_180434_7_ * f4), (double)(-p_180434_5_ * f4), (double)(p_180434_6_ * f4 - p_180434_8_ * f4))};
      p_180434_1_.pos((double)f5 + avec3d[0].x, (double)f6 + avec3d[0].y, (double)f7 + avec3d[0].z).tex((double)f1, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
      p_180434_1_.pos((double)f5 + avec3d[1].x, (double)f6 + avec3d[1].y, (double)f7 + avec3d[1].z).tex((double)f1, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
      p_180434_1_.pos((double)f5 + avec3d[2].x, (double)f6 + avec3d[2].y, (double)f7 + avec3d[2].z).tex((double)f, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
      p_180434_1_.pos((double)f5 + avec3d[3].x, (double)f6 + avec3d[3].y, (double)f7 + avec3d[3].z).tex((double)f, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
   }

   public void setParticleTextureIndex(int p_70536_1_) {
      if (this.getFXLayer() != 0) {
         throw new RuntimeException("Invalid call to Particle.setMiscTex");
      } else {
         this.particleTextureIndexX = 2 * p_70536_1_ % 16;
         this.particleTextureIndexY = p_70536_1_ / 16;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      @Nullable
      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new ParticleBubblePop(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
      }
   }
}
