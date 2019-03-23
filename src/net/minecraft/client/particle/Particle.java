package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReuseableStream;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Particle {
   private static final AxisAlignedBB EMPTY_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   protected World world;
   protected double prevPosX;
   protected double prevPosY;
   protected double prevPosZ;
   protected double posX;
   protected double posY;
   protected double posZ;
   protected double motionX;
   protected double motionY;
   protected double motionZ;
   private AxisAlignedBB boundingBox = EMPTY_AABB;
   protected boolean onGround;
   protected boolean canCollide;
   protected boolean isExpired;
   protected float width = 0.6F;
   protected float height = 1.8F;
   protected Random rand = new Random();
   protected int particleTextureIndexX;
   protected int particleTextureIndexY;
   protected float particleTextureJitterX;
   protected float particleTextureJitterY;
   protected int age;
   protected int maxAge;
   protected float particleScale;
   protected float particleGravity;
   protected float particleRed;
   protected float particleGreen;
   protected float particleBlue;
   protected float particleAlpha = 1.0F;
   protected TextureAtlasSprite particleTexture;
   protected float particleAngle;
   protected float prevParticleAngle;
   public static double interpPosX;
   public static double interpPosY;
   public static double interpPosZ;
   public static Vec3d cameraViewDir;

   protected Particle(World p_i46352_1_, double p_i46352_2_, double p_i46352_4_, double p_i46352_6_) {
      this.world = p_i46352_1_;
      this.setSize(0.2F, 0.2F);
      this.setPosition(p_i46352_2_, p_i46352_4_, p_i46352_6_);
      this.prevPosX = p_i46352_2_;
      this.prevPosY = p_i46352_4_;
      this.prevPosZ = p_i46352_6_;
      this.particleRed = 1.0F;
      this.particleGreen = 1.0F;
      this.particleBlue = 1.0F;
      this.particleTextureJitterX = this.rand.nextFloat() * 3.0F;
      this.particleTextureJitterY = this.rand.nextFloat() * 3.0F;
      this.particleScale = (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;
      this.maxAge = (int)(4.0F / (this.rand.nextFloat() * 0.9F + 0.1F));
      this.age = 0;
      this.canCollide = true;
   }

   public Particle(World p_i1219_1_, double p_i1219_2_, double p_i1219_4_, double p_i1219_6_, double p_i1219_8_, double p_i1219_10_, double p_i1219_12_) {
      this(p_i1219_1_, p_i1219_2_, p_i1219_4_, p_i1219_6_);
      this.motionX = p_i1219_8_ + (Math.random() * 2.0D - 1.0D) * (double)0.4F;
      this.motionY = p_i1219_10_ + (Math.random() * 2.0D - 1.0D) * (double)0.4F;
      this.motionZ = p_i1219_12_ + (Math.random() * 2.0D - 1.0D) * (double)0.4F;
      float f = (float)(Math.random() + Math.random() + 1.0D) * 0.15F;
      float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
      this.motionX = this.motionX / (double)f1 * (double)f * (double)0.4F;
      this.motionY = this.motionY / (double)f1 * (double)f * (double)0.4F + (double)0.1F;
      this.motionZ = this.motionZ / (double)f1 * (double)f * (double)0.4F;
   }

   public Particle multiplyVelocity(float p_70543_1_) {
      this.motionX *= (double)p_70543_1_;
      this.motionY = (this.motionY - (double)0.1F) * (double)p_70543_1_ + (double)0.1F;
      this.motionZ *= (double)p_70543_1_;
      return this;
   }

   public Particle multipleParticleScaleBy(float p_70541_1_) {
      this.setSize(0.2F * p_70541_1_, 0.2F * p_70541_1_);
      this.particleScale *= p_70541_1_;
      return this;
   }

   public void setColor(float p_70538_1_, float p_70538_2_, float p_70538_3_) {
      this.particleRed = p_70538_1_;
      this.particleGreen = p_70538_2_;
      this.particleBlue = p_70538_3_;
   }

   public void setAlphaF(float p_82338_1_) {
      this.particleAlpha = p_82338_1_;
   }

   public boolean shouldDisableDepth() {
      return false;
   }

   public float getRedColorF() {
      return this.particleRed;
   }

   public float getGreenColorF() {
      return this.particleGreen;
   }

   public float getBlueColorF() {
      return this.particleBlue;
   }

   public void setMaxAge(int p_187114_1_) {
      this.maxAge = p_187114_1_;
   }

   public int getMaxAge() {
      return this.maxAge;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      }

      this.motionY -= 0.04D * (double)this.particleGravity;
      this.move(this.motionX, this.motionY, this.motionZ);
      this.motionX *= (double)0.98F;
      this.motionY *= (double)0.98F;
      this.motionZ *= (double)0.98F;
      if (this.onGround) {
         this.motionX *= (double)0.7F;
         this.motionZ *= (double)0.7F;
      }

   }

   public void renderParticle(BufferBuilder p_180434_1_, Entity p_180434_2_, float p_180434_3_, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
      float f = (float)this.particleTextureIndexX / 32.0F;
      float f1 = f + 0.03121875F;
      float f2 = (float)this.particleTextureIndexY / 32.0F;
      float f3 = f2 + 0.03121875F;
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
      if (this.particleAngle != 0.0F) {
         float f8 = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * p_180434_3_;
         float f9 = MathHelper.cos(f8 * 0.5F);
         float f10 = MathHelper.sin(f8 * 0.5F) * (float)cameraViewDir.x;
         float f11 = MathHelper.sin(f8 * 0.5F) * (float)cameraViewDir.y;
         float f12 = MathHelper.sin(f8 * 0.5F) * (float)cameraViewDir.z;
         Vec3d vec3d = new Vec3d((double)f10, (double)f11, (double)f12);

         for(int l = 0; l < 4; ++l) {
            avec3d[l] = vec3d.scale(2.0D * avec3d[l].dotProduct(vec3d)).add(avec3d[l].scale((double)(f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(avec3d[l]).scale((double)(2.0F * f9)));
         }
      }

      p_180434_1_.pos((double)f5 + avec3d[0].x, (double)f6 + avec3d[0].y, (double)f7 + avec3d[0].z).tex((double)f1, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
      p_180434_1_.pos((double)f5 + avec3d[1].x, (double)f6 + avec3d[1].y, (double)f7 + avec3d[1].z).tex((double)f1, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
      p_180434_1_.pos((double)f5 + avec3d[2].x, (double)f6 + avec3d[2].y, (double)f7 + avec3d[2].z).tex((double)f, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
      p_180434_1_.pos((double)f5 + avec3d[3].x, (double)f6 + avec3d[3].y, (double)f7 + avec3d[3].z).tex((double)f, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
   }

   public int getFXLayer() {
      return 0;
   }

   public void setParticleTexture(TextureAtlasSprite p_187117_1_) {
      int i = this.getFXLayer();
      if (i == 1) {
         this.particleTexture = p_187117_1_;
      } else {
         throw new RuntimeException("Invalid call to Particle.setTex, use coordinate methods");
      }
   }

   public void setParticleTextureIndex(int p_70536_1_) {
      if (this.getFXLayer() != 0) {
         throw new RuntimeException("Invalid call to Particle.setMiscTex");
      } else {
         this.particleTextureIndexX = p_70536_1_ % 16;
         this.particleTextureIndexY = p_70536_1_ / 16;
      }
   }

   public void nextTextureIndexX() {
      ++this.particleTextureIndexX;
   }

   public String toString() {
      return this.getClass().getSimpleName() + ", Pos (" + this.posX + "," + this.posY + "," + this.posZ + "), RGBA (" + this.particleRed + "," + this.particleGreen + "," + this.particleBlue + "," + this.particleAlpha + "), Age " + this.age;
   }

   public void setExpired() {
      this.isExpired = true;
   }

   protected void setSize(float p_187115_1_, float p_187115_2_) {
      if (p_187115_1_ != this.width || p_187115_2_ != this.height) {
         this.width = p_187115_1_;
         this.height = p_187115_2_;
         AxisAlignedBB axisalignedbb = this.getBoundingBox();
         double d0 = (axisalignedbb.minX + axisalignedbb.maxX - (double)p_187115_1_) / 2.0D;
         double d1 = (axisalignedbb.minZ + axisalignedbb.maxZ - (double)p_187115_1_) / 2.0D;
         this.setBoundingBox(new AxisAlignedBB(d0, axisalignedbb.minY, d1, d0 + (double)this.width, axisalignedbb.minY + (double)this.height, d1 + (double)this.width));
      }

   }

   public void setPosition(double p_187109_1_, double p_187109_3_, double p_187109_5_) {
      this.posX = p_187109_1_;
      this.posY = p_187109_3_;
      this.posZ = p_187109_5_;
      float f = this.width / 2.0F;
      float f1 = this.height;
      this.setBoundingBox(new AxisAlignedBB(p_187109_1_ - (double)f, p_187109_3_, p_187109_5_ - (double)f, p_187109_1_ + (double)f, p_187109_3_ + (double)f1, p_187109_5_ + (double)f));
   }

   public void move(double p_187110_1_, double p_187110_3_, double p_187110_5_) {
      double d0 = p_187110_3_;
      if (this.canCollide && (p_187110_1_ != 0.0D || p_187110_3_ != 0.0D || p_187110_5_ != 0.0D)) {
         ReuseableStream<VoxelShape> reuseablestream = new ReuseableStream<>(this.world.func_199406_a((Entity)null, this.getBoundingBox(), p_187110_1_, p_187110_3_, p_187110_5_));
         p_187110_3_ = VoxelShapes.func_212437_a(EnumFacing.Axis.Y, this.getBoundingBox(), reuseablestream.func_212761_a(), p_187110_3_);
         this.setBoundingBox(this.getBoundingBox().offset(0.0D, p_187110_3_, 0.0D));
         p_187110_1_ = VoxelShapes.func_212437_a(EnumFacing.Axis.X, this.getBoundingBox(), reuseablestream.func_212761_a(), p_187110_1_);
         if (p_187110_1_ != 0.0D) {
            this.setBoundingBox(this.getBoundingBox().offset(p_187110_1_, 0.0D, 0.0D));
         }

         p_187110_5_ = VoxelShapes.func_212437_a(EnumFacing.Axis.Z, this.getBoundingBox(), reuseablestream.func_212761_a(), p_187110_5_);
         if (p_187110_5_ != 0.0D) {
            this.setBoundingBox(this.getBoundingBox().offset(0.0D, 0.0D, p_187110_5_));
         }
      } else {
         this.setBoundingBox(this.getBoundingBox().offset(p_187110_1_, p_187110_3_, p_187110_5_));
      }

      this.resetPositionToBB();
      this.onGround = p_187110_3_ != p_187110_3_ && d0 < 0.0D;
      if (p_187110_1_ != p_187110_1_) {
         this.motionX = 0.0D;
      }

      if (p_187110_5_ != p_187110_5_) {
         this.motionZ = 0.0D;
      }

   }

   protected void resetPositionToBB() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      this.posX = (axisalignedbb.minX + axisalignedbb.maxX) / 2.0D;
      this.posY = axisalignedbb.minY;
      this.posZ = (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0D;
   }

   public int getBrightnessForRender(float p_189214_1_) {
      BlockPos blockpos = new BlockPos(this.posX, this.posY, this.posZ);
      return this.world.isBlockLoaded(blockpos) ? this.world.getCombinedLight(blockpos, 0) : 0;
   }

   public boolean isAlive() {
      return !this.isExpired;
   }

   public AxisAlignedBB getBoundingBox() {
      return this.boundingBox;
   }

   public void setBoundingBox(AxisAlignedBB p_187108_1_) {
      this.boundingBox = p_187108_1_;
   }
}
