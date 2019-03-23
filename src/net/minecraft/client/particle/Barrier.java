package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.IItemProvider;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Barrier extends Particle {
   protected Barrier(World p_i48192_1_, double p_i48192_2_, double p_i48192_4_, double p_i48192_6_, IItemProvider p_i48192_8_) {
      super(p_i48192_1_, p_i48192_2_, p_i48192_4_, p_i48192_6_, 0.0D, 0.0D, 0.0D);
      this.setParticleTexture(Minecraft.getInstance().getItemRenderer().getItemModelMesher().getParticleIcon(p_i48192_8_));
      this.particleRed = 1.0F;
      this.particleGreen = 1.0F;
      this.particleBlue = 1.0F;
      this.motionX = 0.0D;
      this.motionY = 0.0D;
      this.motionZ = 0.0D;
      this.particleGravity = 0.0F;
      this.maxAge = 80;
      this.canCollide = false;
   }

   public int getFXLayer() {
      return 1;
   }

   public void renderParticle(BufferBuilder p_180434_1_, Entity p_180434_2_, float p_180434_3_, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
      float f = this.particleTexture.getMinU();
      float f1 = this.particleTexture.getMaxU();
      float f2 = this.particleTexture.getMinV();
      float f3 = this.particleTexture.getMaxV();
      float f4 = 0.5F;
      float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)p_180434_3_ - interpPosX);
      float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)p_180434_3_ - interpPosY);
      float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)p_180434_3_ - interpPosZ);
      int i = this.getBrightnessForRender(p_180434_3_);
      int j = i >> 16 & '\uffff';
      int k = i & '\uffff';
      p_180434_1_.pos((double)(f5 - p_180434_4_ * 0.5F - p_180434_7_ * 0.5F), (double)(f6 - p_180434_5_ * 0.5F), (double)(f7 - p_180434_6_ * 0.5F - p_180434_8_ * 0.5F)).tex((double)f1, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
      p_180434_1_.pos((double)(f5 - p_180434_4_ * 0.5F + p_180434_7_ * 0.5F), (double)(f6 + p_180434_5_ * 0.5F), (double)(f7 - p_180434_6_ * 0.5F + p_180434_8_ * 0.5F)).tex((double)f1, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
      p_180434_1_.pos((double)(f5 + p_180434_4_ * 0.5F + p_180434_7_ * 0.5F), (double)(f6 + p_180434_5_ * 0.5F), (double)(f7 + p_180434_6_ * 0.5F + p_180434_8_ * 0.5F)).tex((double)f, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
      p_180434_1_.pos((double)(f5 + p_180434_4_ * 0.5F - p_180434_7_ * 0.5F), (double)(f6 - p_180434_5_ * 0.5F), (double)(f7 + p_180434_6_ * 0.5F - p_180434_8_ * 0.5F)).tex((double)f, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new Barrier(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, Blocks.BARRIER.asItem());
      }
   }
}
