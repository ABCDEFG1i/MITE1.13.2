package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleFallingDust extends Particle {
   private final float oSize;
   private final float rotSpeed;

   protected ParticleFallingDust(World p_i47135_1_, double p_i47135_2_, double p_i47135_4_, double p_i47135_6_, float p_i47135_8_, float p_i47135_9_, float p_i47135_10_) {
      super(p_i47135_1_, p_i47135_2_, p_i47135_4_, p_i47135_6_, 0.0D, 0.0D, 0.0D);
      this.motionX = 0.0D;
      this.motionY = 0.0D;
      this.motionZ = 0.0D;
      this.particleRed = p_i47135_8_;
      this.particleGreen = p_i47135_9_;
      this.particleBlue = p_i47135_10_;
      float f = 0.9F;
      this.particleScale *= 0.75F;
      this.particleScale *= 0.9F;
      this.oSize = this.particleScale;
      this.maxAge = (int)(32.0D / (Math.random() * 0.8D + 0.2D));
      this.maxAge = (int)((float)this.maxAge * 0.9F);
      this.maxAge = Math.max(this.maxAge, 1);
      this.rotSpeed = ((float)Math.random() - 0.5F) * 0.1F;
      this.particleAngle = (float)Math.random() * ((float)Math.PI * 2F);
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

      this.prevParticleAngle = this.particleAngle;
      this.particleAngle += (float)Math.PI * this.rotSpeed * 2.0F;
      if (this.onGround) {
         this.prevParticleAngle = this.particleAngle = 0.0F;
      }

      this.setParticleTextureIndex(7 - this.age * 8 / this.maxAge);
      this.move(this.motionX, this.motionY, this.motionZ);
      this.motionY -= (double)0.003F;
      this.motionY = Math.max(this.motionY, (double)-0.14F);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BlockParticleData> {
      @Nullable
      public Particle makeParticle(BlockParticleData p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         IBlockState iblockstate = p_199234_1_.func_197584_c();
         if (!iblockstate.isAir() && iblockstate.getRenderType() == EnumBlockRenderType.INVISIBLE) {
            return null;
         } else {
            int i = Minecraft.getInstance().getBlockColors().getColor(iblockstate, p_199234_2_, new BlockPos(p_199234_3_, p_199234_5_, p_199234_7_));
            if (iblockstate.getBlock() instanceof BlockFalling) {
               i = ((BlockFalling)iblockstate.getBlock()).getDustColor(iblockstate);
            }

            float f = (float)(i >> 16 & 255) / 255.0F;
            float f1 = (float)(i >> 8 & 255) / 255.0F;
            float f2 = (float)(i & 255) / 255.0F;
            return new ParticleFallingDust(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, f, f1, f2);
         }
      }
   }
}
