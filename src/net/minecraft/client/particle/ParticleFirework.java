package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemFireworkRocket;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleFirework {
   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         ParticleFirework.Spark particlefirework$spark = new ParticleFirework.Spark(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, Minecraft.getInstance().effectRenderer);
         particlefirework$spark.setAlphaF(0.99F);
         return particlefirework$spark;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Overlay extends Particle {
      protected Overlay(World p_i46466_1_, double p_i46466_2_, double p_i46466_4_, double p_i46466_6_) {
         super(p_i46466_1_, p_i46466_2_, p_i46466_4_, p_i46466_6_);
         this.maxAge = 4;
      }

      public void renderParticle(BufferBuilder p_180434_1_, Entity p_180434_2_, float p_180434_3_, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
         float f = 0.25F;
         float f1 = 0.5F;
         float f2 = 0.125F;
         float f3 = 0.375F;
         float f4 = 7.1F * MathHelper.sin(((float)this.age + p_180434_3_ - 1.0F) * 0.25F * (float)Math.PI);
         this.setAlphaF(0.6F - ((float)this.age + p_180434_3_ - 1.0F) * 0.25F * 0.5F);
         float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)p_180434_3_ - interpPosX);
         float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)p_180434_3_ - interpPosY);
         float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)p_180434_3_ - interpPosZ);
         int i = this.getBrightnessForRender(p_180434_3_);
         int j = i >> 16 & '\uffff';
         int k = i & '\uffff';
         p_180434_1_.pos((double)(f5 - p_180434_4_ * f4 - p_180434_7_ * f4), (double)(f6 - p_180434_5_ * f4), (double)(f7 - p_180434_6_ * f4 - p_180434_8_ * f4)).tex(0.5D, 0.375D).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
         p_180434_1_.pos((double)(f5 - p_180434_4_ * f4 + p_180434_7_ * f4), (double)(f6 + p_180434_5_ * f4), (double)(f7 - p_180434_6_ * f4 + p_180434_8_ * f4)).tex(0.5D, 0.125D).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
         p_180434_1_.pos((double)(f5 + p_180434_4_ * f4 + p_180434_7_ * f4), (double)(f6 + p_180434_5_ * f4), (double)(f7 + p_180434_6_ * f4 + p_180434_8_ * f4)).tex(0.25D, 0.125D).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
         p_180434_1_.pos((double)(f5 + p_180434_4_ * f4 - p_180434_7_ * f4), (double)(f6 - p_180434_5_ * f4), (double)(f7 + p_180434_6_ * f4 - p_180434_8_ * f4)).tex(0.25D, 0.375D).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Spark extends ParticleSimpleAnimated {
      private boolean trail;
      private boolean twinkle;
      private final ParticleManager effectRenderer;
      private float fadeColourRed;
      private float fadeColourGreen;
      private float fadeColourBlue;
      private boolean hasFadeColour;

      public Spark(World p_i46465_1_, double p_i46465_2_, double p_i46465_4_, double p_i46465_6_, double p_i46465_8_, double p_i46465_10_, double p_i46465_12_, ParticleManager p_i46465_14_) {
         super(p_i46465_1_, p_i46465_2_, p_i46465_4_, p_i46465_6_, 160, 8, -0.004F);
         this.motionX = p_i46465_8_;
         this.motionY = p_i46465_10_;
         this.motionZ = p_i46465_12_;
         this.effectRenderer = p_i46465_14_;
         this.particleScale *= 0.75F;
         this.maxAge = 48 + this.rand.nextInt(12);
      }

      public void setTrail(boolean p_92045_1_) {
         this.trail = p_92045_1_;
      }

      public void setTwinkle(boolean p_92043_1_) {
         this.twinkle = p_92043_1_;
      }

      public boolean shouldDisableDepth() {
         return true;
      }

      public void renderParticle(BufferBuilder p_180434_1_, Entity p_180434_2_, float p_180434_3_, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
         if (!this.twinkle || this.age < this.maxAge / 3 || (this.age + this.maxAge) / 3 % 2 == 0) {
            super.renderParticle(p_180434_1_, p_180434_2_, p_180434_3_, p_180434_4_, p_180434_5_, p_180434_6_, p_180434_7_, p_180434_8_);
         }

      }

      public void tick() {
         super.tick();
         if (this.trail && this.age < this.maxAge / 2 && (this.age + this.maxAge) % 2 == 0) {
            ParticleFirework.Spark particlefirework$spark = new ParticleFirework.Spark(this.world, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D, this.effectRenderer);
            particlefirework$spark.setAlphaF(0.99F);
            particlefirework$spark.setColor(this.particleRed, this.particleGreen, this.particleBlue);
            particlefirework$spark.age = particlefirework$spark.maxAge / 2;
            if (this.hasFadeColour) {
               particlefirework$spark.hasFadeColour = true;
               particlefirework$spark.fadeColourRed = this.fadeColourRed;
               particlefirework$spark.fadeColourGreen = this.fadeColourGreen;
               particlefirework$spark.fadeColourBlue = this.fadeColourBlue;
            }

            particlefirework$spark.twinkle = this.twinkle;
            this.effectRenderer.addEffect(particlefirework$spark);
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Starter extends Particle {
      private int fireworkAge;
      private final ParticleManager manager;
      private NBTTagList fireworkExplosions;
      private boolean twinkle;

      public Starter(World p_i46464_1_, double p_i46464_2_, double p_i46464_4_, double p_i46464_6_, double p_i46464_8_, double p_i46464_10_, double p_i46464_12_, ParticleManager p_i46464_14_, @Nullable NBTTagCompound p_i46464_15_) {
         super(p_i46464_1_, p_i46464_2_, p_i46464_4_, p_i46464_6_, 0.0D, 0.0D, 0.0D);
         this.motionX = p_i46464_8_;
         this.motionY = p_i46464_10_;
         this.motionZ = p_i46464_12_;
         this.manager = p_i46464_14_;
         this.maxAge = 8;
         if (p_i46464_15_ != null) {
            this.fireworkExplosions = p_i46464_15_.getTagList("Explosions", 10);
            if (this.fireworkExplosions.isEmpty()) {
               this.fireworkExplosions = null;
            } else {
               this.maxAge = this.fireworkExplosions.size() * 2 - 1;

               for(int i = 0; i < this.fireworkExplosions.size(); ++i) {
                  NBTTagCompound nbttagcompound = this.fireworkExplosions.getCompoundTagAt(i);
                  if (nbttagcompound.getBoolean("Flicker")) {
                     this.twinkle = true;
                     this.maxAge += 15;
                     break;
                  }
               }
            }
         }

      }

      public void renderParticle(BufferBuilder p_180434_1_, Entity p_180434_2_, float p_180434_3_, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
      }

      public void tick() {
         if (this.fireworkAge == 0 && this.fireworkExplosions != null) {
            boolean flag = this.isFarFromCamera();
            boolean flag1 = false;
            if (this.fireworkExplosions.size() >= 3) {
               flag1 = true;
            } else {
               for(int i = 0; i < this.fireworkExplosions.size(); ++i) {
                  NBTTagCompound nbttagcompound = this.fireworkExplosions.getCompoundTagAt(i);
                  if (ItemFireworkRocket.Shape.func_196070_a(nbttagcompound.getByte("Type")) == ItemFireworkRocket.Shape.LARGE_BALL) {
                     flag1 = true;
                     break;
                  }
               }
            }

            SoundEvent soundevent1;
            if (flag1) {
               soundevent1 = flag ? SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR : SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST;
            } else {
               soundevent1 = flag ? SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST_FAR : SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST;
            }

            this.world.playSound(this.posX, this.posY, this.posZ, soundevent1, SoundCategory.AMBIENT, 20.0F, 0.95F + this.rand.nextFloat() * 0.1F, true);
         }

         if (this.fireworkAge % 2 == 0 && this.fireworkExplosions != null && this.fireworkAge / 2 < this.fireworkExplosions.size()) {
            int k = this.fireworkAge / 2;
            NBTTagCompound nbttagcompound1 = this.fireworkExplosions.getCompoundTagAt(k);
            ItemFireworkRocket.Shape itemfireworkrocket$shape = ItemFireworkRocket.Shape.func_196070_a(nbttagcompound1.getByte("Type"));
            boolean flag4 = nbttagcompound1.getBoolean("Trail");
            boolean flag2 = nbttagcompound1.getBoolean("Flicker");
            int[] aint = nbttagcompound1.getIntArray("Colors");
            int[] aint1 = nbttagcompound1.getIntArray("FadeColors");
            if (aint.length == 0) {
               aint = new int[]{EnumDyeColor.BLACK.func_196060_f()};
            }

            switch(itemfireworkrocket$shape) {
            case SMALL_BALL:
            default:
               this.createBall(0.25D, 2, aint, aint1, flag4, flag2);
               break;
            case LARGE_BALL:
               this.createBall(0.5D, 4, aint, aint1, flag4, flag2);
               break;
            case STAR:
               this.createShaped(0.5D, new double[][]{{0.0D, 1.0D}, {0.3455D, 0.309D}, {0.9511D, 0.309D}, {0.3795918367346939D, -0.12653061224489795D}, {0.6122448979591837D, -0.8040816326530612D}, {0.0D, -0.35918367346938773D}}, aint, aint1, flag4, flag2, false);
               break;
            case CREEPER:
               this.createShaped(0.5D, new double[][]{{0.0D, 0.2D}, {0.2D, 0.2D}, {0.2D, 0.6D}, {0.6D, 0.6D}, {0.6D, 0.2D}, {0.2D, 0.2D}, {0.2D, 0.0D}, {0.4D, 0.0D}, {0.4D, -0.6D}, {0.2D, -0.6D}, {0.2D, -0.4D}, {0.0D, -0.4D}}, aint, aint1, flag4, flag2, true);
               break;
            case BURST:
               this.createBurst(aint, aint1, flag4, flag2);
            }

            int j = aint[0];
            float f = (float)((j & 16711680) >> 16) / 255.0F;
            float f1 = (float)((j & '\uff00') >> 8) / 255.0F;
            float f2 = (float)((j & 255) >> 0) / 255.0F;
            ParticleFirework.Overlay particlefirework$overlay = new ParticleFirework.Overlay(this.world, this.posX, this.posY, this.posZ);
            particlefirework$overlay.setColor(f, f1, f2);
            this.manager.addEffect(particlefirework$overlay);
         }

         ++this.fireworkAge;
         if (this.fireworkAge > this.maxAge) {
            if (this.twinkle) {
               boolean flag3 = this.isFarFromCamera();
               SoundEvent soundevent = flag3 ? SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR : SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE;
               this.world.playSound(this.posX, this.posY, this.posZ, soundevent, SoundCategory.AMBIENT, 20.0F, 0.9F + this.rand.nextFloat() * 0.15F, true);
            }

            this.setExpired();
         }

      }

      private boolean isFarFromCamera() {
         Minecraft minecraft = Minecraft.getInstance();
         return minecraft.getRenderViewEntity() == null || !(minecraft.getRenderViewEntity().getDistanceSq(this.posX, this.posY, this.posZ) < 256.0D);
      }

      private void createParticle(double p_92034_1_, double p_92034_3_, double p_92034_5_, double p_92034_7_, double p_92034_9_, double p_92034_11_, int[] p_92034_13_, int[] p_92034_14_, boolean p_92034_15_, boolean p_92034_16_) {
         ParticleFirework.Spark particlefirework$spark = new ParticleFirework.Spark(this.world, p_92034_1_, p_92034_3_, p_92034_5_, p_92034_7_, p_92034_9_, p_92034_11_, this.manager);
         particlefirework$spark.setAlphaF(0.99F);
         particlefirework$spark.setTrail(p_92034_15_);
         particlefirework$spark.setTwinkle(p_92034_16_);
         int i = this.rand.nextInt(p_92034_13_.length);
         particlefirework$spark.setColor(p_92034_13_[i]);
         if (p_92034_14_.length > 0) {
            particlefirework$spark.setColorFade(p_92034_14_[this.rand.nextInt(p_92034_14_.length)]);
         }

         this.manager.addEffect(particlefirework$spark);
      }

      private void createBall(double p_92035_1_, int p_92035_3_, int[] p_92035_4_, int[] p_92035_5_, boolean p_92035_6_, boolean p_92035_7_) {
         double d0 = this.posX;
         double d1 = this.posY;
         double d2 = this.posZ;

         for(int i = -p_92035_3_; i <= p_92035_3_; ++i) {
            for(int j = -p_92035_3_; j <= p_92035_3_; ++j) {
               for(int k = -p_92035_3_; k <= p_92035_3_; ++k) {
                  double d3 = (double)j + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5D;
                  double d4 = (double)i + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5D;
                  double d5 = (double)k + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5D;
                  double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5) / p_92035_1_ + this.rand.nextGaussian() * 0.05D;
                  this.createParticle(d0, d1, d2, d3 / d6, d4 / d6, d5 / d6, p_92035_4_, p_92035_5_, p_92035_6_, p_92035_7_);
                  if (i != -p_92035_3_ && i != p_92035_3_ && j != -p_92035_3_ && j != p_92035_3_) {
                     k += p_92035_3_ * 2 - 1;
                  }
               }
            }
         }

      }

      private void createShaped(double p_92038_1_, double[][] p_92038_3_, int[] p_92038_4_, int[] p_92038_5_, boolean p_92038_6_, boolean p_92038_7_, boolean p_92038_8_) {
         double d0 = p_92038_3_[0][0];
         double d1 = p_92038_3_[0][1];
         this.createParticle(this.posX, this.posY, this.posZ, d0 * p_92038_1_, d1 * p_92038_1_, 0.0D, p_92038_4_, p_92038_5_, p_92038_6_, p_92038_7_);
         float f = this.rand.nextFloat() * (float)Math.PI;
         double d2 = p_92038_8_ ? 0.034D : 0.34D;

         for(int i = 0; i < 3; ++i) {
            double d3 = (double)f + (double)((float)i * (float)Math.PI) * d2;
            double d4 = d0;
            double d5 = d1;

            for(int j = 1; j < p_92038_3_.length; ++j) {
               double d6 = p_92038_3_[j][0];
               double d7 = p_92038_3_[j][1];

               for(double d8 = 0.25D; d8 <= 1.0D; d8 += 0.25D) {
                  double d9 = (d4 + (d6 - d4) * d8) * p_92038_1_;
                  double d10 = (d5 + (d7 - d5) * d8) * p_92038_1_;
                  double d11 = d9 * Math.sin(d3);
                  d9 = d9 * Math.cos(d3);

                  for(double d12 = -1.0D; d12 <= 1.0D; d12 += 2.0D) {
                     this.createParticle(this.posX, this.posY, this.posZ, d9 * d12, d10, d11 * d12, p_92038_4_, p_92038_5_, p_92038_6_, p_92038_7_);
                  }
               }

               d4 = d6;
               d5 = d7;
            }
         }

      }

      private void createBurst(int[] p_92036_1_, int[] p_92036_2_, boolean p_92036_3_, boolean p_92036_4_) {
         double d0 = this.rand.nextGaussian() * 0.05D;
         double d1 = this.rand.nextGaussian() * 0.05D;

         for(int i = 0; i < 70; ++i) {
            double d2 = this.motionX * 0.5D + this.rand.nextGaussian() * 0.15D + d0;
            double d3 = this.motionZ * 0.5D + this.rand.nextGaussian() * 0.15D + d1;
            double d4 = this.motionY * 0.5D + this.rand.nextDouble() * 0.5D;
            this.createParticle(this.posX, this.posY, this.posZ, d2, d4, d3, p_92036_1_, p_92036_2_, p_92036_3_, p_92036_4_);
         }

      }

      public int getFXLayer() {
         return 0;
      }
   }
}
