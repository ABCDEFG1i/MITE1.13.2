package net.minecraft.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class EntityFireball extends Entity {
   public EntityLivingBase shootingEntity;
   private int ticksAlive;
   private int ticksInAir;
   public double accelerationX;
   public double accelerationY;
   public double accelerationZ;

   protected EntityFireball(EntityType<?> p_i48543_1_, World p_i48543_2_, float p_i48543_3_, float p_i48543_4_) {
      super(p_i48543_1_, p_i48543_2_);
      this.setSize(p_i48543_3_, p_i48543_4_);
   }

   public EntityFireball(EntityType<?> p_i48544_1_, double p_i48544_2_, double p_i48544_4_, double p_i48544_6_, double p_i48544_8_, double p_i48544_10_, double p_i48544_12_, World p_i48544_14_, float p_i48544_15_, float p_i48544_16_) {
      this(p_i48544_1_, p_i48544_14_, p_i48544_15_, p_i48544_16_);
      this.setLocationAndAngles(p_i48544_2_, p_i48544_4_, p_i48544_6_, this.rotationYaw, this.rotationPitch);
      this.setPosition(p_i48544_2_, p_i48544_4_, p_i48544_6_);
      double d0 = (double)MathHelper.sqrt(p_i48544_8_ * p_i48544_8_ + p_i48544_10_ * p_i48544_10_ + p_i48544_12_ * p_i48544_12_);
      this.accelerationX = p_i48544_8_ / d0 * 0.1D;
      this.accelerationY = p_i48544_10_ / d0 * 0.1D;
      this.accelerationZ = p_i48544_12_ / d0 * 0.1D;
   }

   public EntityFireball(EntityType<?> p_i48545_1_, EntityLivingBase p_i48545_2_, double p_i48545_3_, double p_i48545_5_, double p_i48545_7_, World p_i48545_9_, float p_i48545_10_, float p_i48545_11_) {
      this(p_i48545_1_, p_i48545_9_, p_i48545_10_, p_i48545_11_);
      this.shootingEntity = p_i48545_2_;
      this.setLocationAndAngles(p_i48545_2_.posX, p_i48545_2_.posY, p_i48545_2_.posZ, p_i48545_2_.rotationYaw, p_i48545_2_.rotationPitch);
      this.setPosition(this.posX, this.posY, this.posZ);
      this.motionX = 0.0D;
      this.motionY = 0.0D;
      this.motionZ = 0.0D;
      p_i48545_3_ = p_i48545_3_ + this.rand.nextGaussian() * 0.4D;
      p_i48545_5_ = p_i48545_5_ + this.rand.nextGaussian() * 0.4D;
      p_i48545_7_ = p_i48545_7_ + this.rand.nextGaussian() * 0.4D;
      double d0 = (double)MathHelper.sqrt(p_i48545_3_ * p_i48545_3_ + p_i48545_5_ * p_i48545_5_ + p_i48545_7_ * p_i48545_7_);
      this.accelerationX = p_i48545_3_ / d0 * 0.1D;
      this.accelerationY = p_i48545_5_ / d0 * 0.1D;
      this.accelerationZ = p_i48545_7_ / d0 * 0.1D;
   }

   protected void registerData() {
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double p_70112_1_) {
      double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;
      if (Double.isNaN(d0)) {
         d0 = 4.0D;
      }

      d0 = d0 * 64.0D;
      return p_70112_1_ < d0 * d0;
   }

   public void tick() {
      if (this.world.isRemote || (this.shootingEntity == null || !this.shootingEntity.isDead) && this.world.isBlockLoaded(new BlockPos(this))) {
         super.tick();
         if (this.isFireballFiery()) {
            this.setFire(1);
         }

         ++this.ticksInAir;
         RayTraceResult raytraceresult = ProjectileHelper.forwardsRaycast(this, true, this.ticksInAir >= 25, this.shootingEntity);
         if (raytraceresult != null) {
            this.onImpact(raytraceresult);
         }

         this.posX += this.motionX;
         this.posY += this.motionY;
         this.posZ += this.motionZ;
         ProjectileHelper.rotateTowardsMovement(this, 0.2F);
         float f = this.getMotionFactor();
         if (this.isInWater()) {
            for(int i = 0; i < 4; ++i) {
               float f1 = 0.25F;
               this.world.spawnParticle(Particles.BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ);
            }

            f = 0.8F;
         }

         this.motionX += this.accelerationX;
         this.motionY += this.accelerationY;
         this.motionZ += this.accelerationZ;
         this.motionX *= (double)f;
         this.motionY *= (double)f;
         this.motionZ *= (double)f;
         this.world.spawnParticle(this.func_195057_f(), this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
         this.setPosition(this.posX, this.posY, this.posZ);
      } else {
         this.setDead();
      }
   }

   protected boolean isFireballFiery() {
      return true;
   }

   protected IParticleData func_195057_f() {
      return Particles.SMOKE;
   }

   protected float getMotionFactor() {
      return 0.95F;
   }

   protected abstract void onImpact(RayTraceResult p_70227_1_);

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      p_70014_1_.setTag("direction", this.newDoubleNBTList(new double[]{this.motionX, this.motionY, this.motionZ}));
      p_70014_1_.setTag("power", this.newDoubleNBTList(new double[]{this.accelerationX, this.accelerationY, this.accelerationZ}));
      p_70014_1_.setInteger("life", this.ticksAlive);
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      if (p_70037_1_.hasKey("power", 9)) {
         NBTTagList nbttaglist = p_70037_1_.getTagList("power", 6);
         if (nbttaglist.size() == 3) {
            this.accelerationX = nbttaglist.getDoubleAt(0);
            this.accelerationY = nbttaglist.getDoubleAt(1);
            this.accelerationZ = nbttaglist.getDoubleAt(2);
         }
      }

      this.ticksAlive = p_70037_1_.getInteger("life");
      if (p_70037_1_.hasKey("direction", 9) && p_70037_1_.getTagList("direction", 6).size() == 3) {
         NBTTagList nbttaglist1 = p_70037_1_.getTagList("direction", 6);
         this.motionX = nbttaglist1.getDoubleAt(0);
         this.motionY = nbttaglist1.getDoubleAt(1);
         this.motionZ = nbttaglist1.getDoubleAt(2);
      } else {
         this.setDead();
      }

   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public float getCollisionBorderSize() {
      return 1.0F;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         this.markVelocityChanged();
         if (p_70097_1_.getTrueSource() != null) {
            Vec3d vec3d = p_70097_1_.getTrueSource().getLookVec();
            if (vec3d != null) {
               this.motionX = vec3d.x;
               this.motionY = vec3d.y;
               this.motionZ = vec3d.z;
               this.accelerationX = this.motionX * 0.1D;
               this.accelerationY = this.motionY * 0.1D;
               this.accelerationZ = this.motionZ * 0.1D;
            }

            if (p_70097_1_.getTrueSource() instanceof EntityLivingBase) {
               this.shootingEntity = (EntityLivingBase)p_70097_1_.getTrueSource();
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public float getBrightness() {
      return 1.0F;
   }

   @OnlyIn(Dist.CLIENT)
   public int getBrightnessForRender() {
      return 15728880;
   }
}
