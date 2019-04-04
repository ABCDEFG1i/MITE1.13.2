package net.minecraft.entity.projectile;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class EntityThrowable extends Entity implements IProjectile {
   private int xTile = -1;
   private int yTile = -1;
   private int zTile = -1;
   protected boolean inGround;
   public int throwableShake;
   protected EntityLivingBase thrower;
   private UUID field_200218_h;
   public Entity ignoreEntity;
   private int ignoreTime;

   protected EntityThrowable(EntityType<?> p_i48540_1_, World p_i48540_2_) {
      super(p_i48540_1_, p_i48540_2_);
      this.setSize(0.25F, 0.25F);
   }

   protected EntityThrowable(EntityType<?> p_i48541_1_, double p_i48541_2_, double p_i48541_4_, double p_i48541_6_, World p_i48541_8_) {
      this(p_i48541_1_, p_i48541_8_);
      this.setPosition(p_i48541_2_, p_i48541_4_, p_i48541_6_);
   }

   protected EntityThrowable(EntityType<?> p_i48542_1_, EntityLivingBase p_i48542_2_, World p_i48542_3_) {
      this(p_i48542_1_, p_i48542_2_.posX, p_i48542_2_.posY + (double)p_i48542_2_.getEyeHeight() - (double)0.1F, p_i48542_2_.posZ, p_i48542_3_);
      this.thrower = p_i48542_2_;
      this.field_200218_h = p_i48542_2_.getUniqueID();
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

   public void shoot(Entity p_184538_1_, float p_184538_2_, float p_184538_3_, float p_184538_4_, float p_184538_5_, float p_184538_6_) {
      float f = -MathHelper.sin(p_184538_3_ * ((float)Math.PI / 180F)) * MathHelper.cos(p_184538_2_ * ((float)Math.PI / 180F));
      float f1 = -MathHelper.sin((p_184538_2_ + p_184538_4_) * ((float)Math.PI / 180F));
      float f2 = MathHelper.cos(p_184538_3_ * ((float)Math.PI / 180F)) * MathHelper.cos(p_184538_2_ * ((float)Math.PI / 180F));
      this.shoot((double)f, (double)f1, (double)f2, p_184538_5_, p_184538_6_);
      this.motionX += p_184538_1_.motionX;
      this.motionZ += p_184538_1_.motionZ;
      if (!p_184538_1_.onGround) {
         this.motionY += p_184538_1_.motionY;
      }

   }

   public void shoot(double p_70186_1_, double p_70186_3_, double p_70186_5_, float p_70186_7_, float p_70186_8_) {
      float f = MathHelper.sqrt(p_70186_1_ * p_70186_1_ + p_70186_3_ * p_70186_3_ + p_70186_5_ * p_70186_5_);
      p_70186_1_ = p_70186_1_ / (double)f;
      p_70186_3_ = p_70186_3_ / (double)f;
      p_70186_5_ = p_70186_5_ / (double)f;
      p_70186_1_ = p_70186_1_ + this.rand.nextGaussian() * (double)0.0075F * (double)p_70186_8_;
      p_70186_3_ = p_70186_3_ + this.rand.nextGaussian() * (double)0.0075F * (double)p_70186_8_;
      p_70186_5_ = p_70186_5_ + this.rand.nextGaussian() * (double)0.0075F * (double)p_70186_8_;
      p_70186_1_ = p_70186_1_ * (double)p_70186_7_;
      p_70186_3_ = p_70186_3_ * (double)p_70186_7_;
      p_70186_5_ = p_70186_5_ * (double)p_70186_7_;
      this.motionX = p_70186_1_;
      this.motionY = p_70186_3_;
      this.motionZ = p_70186_5_;
      float f1 = MathHelper.sqrt(p_70186_1_ * p_70186_1_ + p_70186_5_ * p_70186_5_);
      this.rotationYaw = (float)(MathHelper.atan2(p_70186_1_, p_70186_5_) * (double)(180F / (float)Math.PI));
      this.rotationPitch = (float)(MathHelper.atan2(p_70186_3_, (double)f1) * (double)(180F / (float)Math.PI));
      this.prevRotationYaw = this.rotationYaw;
      this.prevRotationPitch = this.rotationPitch;
   }

   @OnlyIn(Dist.CLIENT)
   public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
      this.motionX = p_70016_1_;
      this.motionY = p_70016_3_;
      this.motionZ = p_70016_5_;
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float f = MathHelper.sqrt(p_70016_1_ * p_70016_1_ + p_70016_5_ * p_70016_5_);
         this.rotationYaw = (float)(MathHelper.atan2(p_70016_1_, p_70016_5_) * (double)(180F / (float)Math.PI));
         this.rotationPitch = (float)(MathHelper.atan2(p_70016_3_, (double)f) * (double)(180F / (float)Math.PI));
         this.prevRotationYaw = this.rotationYaw;
         this.prevRotationPitch = this.rotationPitch;
      }

   }

   public void tick() {
      this.lastTickPosX = this.posX;
      this.lastTickPosY = this.posY;
      this.lastTickPosZ = this.posZ;
      super.tick();
      if (this.throwableShake > 0) {
         --this.throwableShake;
      }

      if (this.inGround) {
         this.inGround = false;
         this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
         this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
         this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
      }

      Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);
      Vec3d vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
      RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d, vec3d1);
      vec3d = new Vec3d(this.posX, this.posY, this.posZ);
      vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
      if (raytraceresult != null) {
         vec3d1 = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
      }

      Entity entity = null;
      List<Entity> list = this.world.func_72839_b(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D));
      double d0 = 0.0D;
      boolean flag = false;

      for(int i = 0; i < list.size(); ++i) {
         Entity entity1 = list.get(i);
         if (entity1.canBeCollidedWith()) {
            if (entity1 == this.ignoreEntity) {
               flag = true;
            } else if (this.thrower != null && this.ticksExisted < 2 && this.ignoreEntity == null) {
               this.ignoreEntity = entity1;
               flag = true;
            } else {
               flag = false;
               AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow((double)0.3F);
               RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(vec3d, vec3d1);
               if (raytraceresult1 != null) {
                  double d1 = vec3d.squareDistanceTo(raytraceresult1.hitVec);
                  if (d1 < d0 || d0 == 0.0D) {
                     entity = entity1;
                     d0 = d1;
                  }
               }
            }
         }
      }

      if (this.ignoreEntity != null) {
         if (flag) {
            this.ignoreTime = 2;
         } else if (this.ignoreTime-- <= 0) {
            this.ignoreEntity = null;
         }
      }

      if (entity != null) {
         raytraceresult = new RayTraceResult(entity);
      }

      if (raytraceresult != null) {
         if (raytraceresult.type == RayTraceResult.Type.BLOCK && this.world.getBlockState(raytraceresult.getBlockPos()).getBlock() == Blocks.NETHER_PORTAL) {
            this.setPortal(raytraceresult.getBlockPos());
         } else {
            this.onImpact(raytraceresult);
         }
      }

      this.posX += this.motionX;
      this.posY += this.motionY;
      this.posZ += this.motionZ;
      float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
      this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * (double)(180F / (float)Math.PI));

      for(this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f) * (double)(180F / (float)Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
      }

      while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
         this.prevRotationPitch += 360.0F;
      }

      while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
         this.prevRotationYaw -= 360.0F;
      }

      while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
         this.prevRotationYaw += 360.0F;
      }

      this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
      this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
      float f1 = 0.99F;
      float f2 = this.getGravityVelocity();
      if (this.isInWater()) {
         for(int j = 0; j < 4; ++j) {
            float f3 = 0.25F;
            this.world.spawnParticle(Particles.BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ);
         }

         f1 = 0.8F;
      }

      this.motionX *= (double)f1;
      this.motionY *= (double)f1;
      this.motionZ *= (double)f1;
      if (!this.hasNoGravity()) {
         this.motionY -= (double)f2;
      }

      this.setPosition(this.posX, this.posY, this.posZ);
   }

   protected float getGravityVelocity() {
      return 0.03F;
   }

   protected abstract void onImpact(RayTraceResult p_70184_1_);

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      p_70014_1_.setInteger("xTile", this.xTile);
      p_70014_1_.setInteger("yTile", this.yTile);
      p_70014_1_.setInteger("zTile", this.zTile);
      p_70014_1_.setByte("shake", (byte)this.throwableShake);
      p_70014_1_.setByte("inGround", (byte)(this.inGround ? 1 : 0));
      if (this.field_200218_h != null) {
         p_70014_1_.setTag("owner", NBTUtil.createUUIDTag(this.field_200218_h));
      }

   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      this.xTile = p_70037_1_.getInteger("xTile");
      this.yTile = p_70037_1_.getInteger("yTile");
      this.zTile = p_70037_1_.getInteger("zTile");
      this.throwableShake = p_70037_1_.getByte("shake") & 255;
      this.inGround = p_70037_1_.getByte("inGround") == 1;
      this.thrower = null;
      if (p_70037_1_.hasKey("owner", 10)) {
         this.field_200218_h = NBTUtil.getUUIDFromTag(p_70037_1_.getCompoundTag("owner"));
      }

   }

   @Nullable
   public EntityLivingBase getThrower() {
      if (this.thrower == null && this.field_200218_h != null && this.world instanceof WorldServer) {
         Entity entity = ((WorldServer)this.world).getEntityFromUuid(this.field_200218_h);
         if (entity instanceof EntityLivingBase) {
            this.thrower = (EntityLivingBase)entity;
         } else {
            this.field_200218_h = null;
         }
      }

      return this.thrower;
   }
}
