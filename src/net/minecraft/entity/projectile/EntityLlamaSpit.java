package net.minecraft.entity.projectile;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.init.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityLlamaSpit extends Entity implements IProjectile {
   public EntityLlama owner;
   private NBTTagCompound ownerNbt;

   public EntityLlamaSpit(World p_i47272_1_) {
      super(EntityType.LLAMA_SPIT, p_i47272_1_);
      this.setSize(0.25F, 0.25F);
   }

   public EntityLlamaSpit(World p_i47273_1_, EntityLlama p_i47273_2_) {
      this(p_i47273_1_);
      this.owner = p_i47273_2_;
      this.setPosition(p_i47273_2_.posX - (double)(p_i47273_2_.width + 1.0F) * 0.5D * (double)MathHelper.sin(p_i47273_2_.renderYawOffset * ((float)Math.PI / 180F)), p_i47273_2_.posY + (double)p_i47273_2_.getEyeHeight() - (double)0.1F, p_i47273_2_.posZ + (double)(p_i47273_2_.width + 1.0F) * 0.5D * (double)MathHelper.cos(p_i47273_2_.renderYawOffset * ((float)Math.PI / 180F)));
   }

   @OnlyIn(Dist.CLIENT)
   public EntityLlamaSpit(World p_i47274_1_, double p_i47274_2_, double p_i47274_4_, double p_i47274_6_, double p_i47274_8_, double p_i47274_10_, double p_i47274_12_) {
      this(p_i47274_1_);
      this.setPosition(p_i47274_2_, p_i47274_4_, p_i47274_6_);

      for(int i = 0; i < 7; ++i) {
         double d0 = 0.4D + 0.1D * (double)i;
         p_i47274_1_.spawnParticle(Particles.SPIT, p_i47274_2_, p_i47274_4_, p_i47274_6_, p_i47274_8_ * d0, p_i47274_10_, p_i47274_12_ * d0);
      }

      this.motionX = p_i47274_8_;
      this.motionY = p_i47274_10_;
      this.motionZ = p_i47274_12_;
   }

   public void tick() {
      super.tick();
      if (this.ownerNbt != null) {
         this.restoreOwnerFromSave();
      }

      Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);
      Vec3d vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
      RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d, vec3d1);
      vec3d = new Vec3d(this.posX, this.posY, this.posZ);
      vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
      if (raytraceresult != null) {
         vec3d1 = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
      }

      Entity entity = this.getHitEntity(vec3d, vec3d1);
      if (entity != null) {
         raytraceresult = new RayTraceResult(entity);
      }

      if (raytraceresult != null) {
         this.onHit(raytraceresult);
      }

      this.posX += this.motionX;
      this.posY += this.motionY;
      this.posZ += this.motionZ;
      float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
      this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * (double)(180F / (float)Math.PI));

      for(this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f) * (double)(180F / (float)Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
         ;
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
      float f2 = 0.06F;
      if (!this.world.isMaterialInBB(this.getEntityBoundingBox(), Material.AIR)) {
         this.setDead();
      } else if (this.isInWaterOrBubbleColumn()) {
         this.setDead();
      } else {
         this.motionX *= (double)0.99F;
         this.motionY *= (double)0.99F;
         this.motionZ *= (double)0.99F;
         if (!this.hasNoGravity()) {
            this.motionY -= (double)0.06F;
         }

         this.setPosition(this.posX, this.posY, this.posZ);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
      this.motionX = p_70016_1_;
      this.motionY = p_70016_3_;
      this.motionZ = p_70016_5_;
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float f = MathHelper.sqrt(p_70016_1_ * p_70016_1_ + p_70016_5_ * p_70016_5_);
         this.rotationPitch = (float)(MathHelper.atan2(p_70016_3_, (double)f) * (double)(180F / (float)Math.PI));
         this.rotationYaw = (float)(MathHelper.atan2(p_70016_1_, p_70016_5_) * (double)(180F / (float)Math.PI));
         this.prevRotationPitch = this.rotationPitch;
         this.prevRotationYaw = this.rotationYaw;
         this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
      }

   }

   @Nullable
   private Entity getHitEntity(Vec3d p_190538_1_, Vec3d p_190538_2_) {
      Entity entity = null;
      List<Entity> list = this.world.func_72839_b(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D));
      double d0 = 0.0D;

      for(Entity entity1 : list) {
         if (entity1 != this.owner) {
            AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow((double)0.3F);
            RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(p_190538_1_, p_190538_2_);
            if (raytraceresult != null) {
               double d1 = p_190538_1_.squareDistanceTo(raytraceresult.hitVec);
               if (d1 < d0 || d0 == 0.0D) {
                  entity = entity1;
                  d0 = d1;
               }
            }
         }
      }

      return entity;
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

   public void onHit(RayTraceResult p_190536_1_) {
      if (p_190536_1_.entity != null && this.owner != null) {
         p_190536_1_.entity.attackEntityFrom(DamageSource.causeIndirectDamage(this, this.owner).setProjectile(), 1.0F);
      }

      if (!this.world.isRemote) {
         this.setDead();
      }

   }

   protected void registerData() {
   }

   protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      if (p_70037_1_.hasKey("Owner", 10)) {
         this.ownerNbt = p_70037_1_.getCompoundTag("Owner");
      }

   }

   protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      if (this.owner != null) {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         UUID uuid = this.owner.getUniqueID();
         nbttagcompound.setUniqueId("OwnerUUID", uuid);
         p_70014_1_.setTag("Owner", nbttagcompound);
      }

   }

   private void restoreOwnerFromSave() {
      if (this.ownerNbt != null && this.ownerNbt.hasUniqueId("OwnerUUID")) {
         UUID uuid = this.ownerNbt.getUniqueId("OwnerUUID");

         for(EntityLlama entityllama : this.world.getEntitiesWithinAABB(EntityLlama.class, this.getEntityBoundingBox().grow(15.0D))) {
            if (entityllama.getUniqueID().equals(uuid)) {
               this.owner = entityllama;
               break;
            }
         }
      }

      this.ownerNbt = null;
   }
}
