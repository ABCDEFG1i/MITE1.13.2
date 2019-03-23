package net.minecraft.client.particle;

import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleSimpleAnimated extends Particle {
   protected final int textureIdx;
   protected final int numAgingFrames;
   private final float yAccel;
   private float baseAirFriction = 0.91F;
   private float fadeTargetRed;
   private float fadeTargetGreen;
   private float fadeTargetBlue;
   private boolean fadingColor;

   public ParticleSimpleAnimated(World p_i46578_1_, double p_i46578_2_, double p_i46578_4_, double p_i46578_6_, int p_i46578_8_, int p_i46578_9_, float p_i46578_10_) {
      super(p_i46578_1_, p_i46578_2_, p_i46578_4_, p_i46578_6_);
      this.textureIdx = p_i46578_8_;
      this.numAgingFrames = p_i46578_9_;
      this.yAccel = p_i46578_10_;
   }

   public void setColor(int p_187146_1_) {
      float f = (float)((p_187146_1_ & 16711680) >> 16) / 255.0F;
      float f1 = (float)((p_187146_1_ & '\uff00') >> 8) / 255.0F;
      float f2 = (float)((p_187146_1_ & 255) >> 0) / 255.0F;
      float f3 = 1.0F;
      this.setColor(f * 1.0F, f1 * 1.0F, f2 * 1.0F);
   }

   public void setColorFade(int p_187145_1_) {
      this.fadeTargetRed = (float)((p_187145_1_ & 16711680) >> 16) / 255.0F;
      this.fadeTargetGreen = (float)((p_187145_1_ & '\uff00') >> 8) / 255.0F;
      this.fadeTargetBlue = (float)((p_187145_1_ & 255) >> 0) / 255.0F;
      this.fadingColor = true;
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

      if (this.age > this.maxAge / 2) {
         this.setAlphaF(1.0F - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge);
         if (this.fadingColor) {
            this.particleRed += (this.fadeTargetRed - this.particleRed) * 0.2F;
            this.particleGreen += (this.fadeTargetGreen - this.particleGreen) * 0.2F;
            this.particleBlue += (this.fadeTargetBlue - this.particleBlue) * 0.2F;
         }
      }

      this.setParticleTextureIndex(this.textureIdx + this.numAgingFrames - 1 - this.age * this.numAgingFrames / this.maxAge);
      this.motionY += (double)this.yAccel;
      this.move(this.motionX, this.motionY, this.motionZ);
      this.motionX *= (double)this.baseAirFriction;
      this.motionY *= (double)this.baseAirFriction;
      this.motionZ *= (double)this.baseAirFriction;
      if (this.onGround) {
         this.motionX *= (double)0.7F;
         this.motionZ *= (double)0.7F;
      }

   }

   public int getBrightnessForRender(float p_189214_1_) {
      return 15728880;
   }

   protected void setBaseAirFriction(float p_191238_1_) {
      this.baseAirFriction = p_191238_1_;
   }
}
