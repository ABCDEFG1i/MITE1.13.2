package net.minecraft.entity.item;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class EntityTNTPrimed extends Entity {
   private static final DataParameter<Integer> FUSE = EntityDataManager.createKey(EntityTNTPrimed.class, DataSerializers.VARINT);
   @Nullable
   private EntityLivingBase tntPlacedBy;
   private int fuse = 80;

   public EntityTNTPrimed(World p_i1729_1_) {
      super(EntityType.TNT, p_i1729_1_);
      this.preventEntitySpawning = true;
      this.isImmuneToFire = true;
      this.setSize(0.98F, 0.98F);
   }

   public EntityTNTPrimed(World p_i1730_1_, double p_i1730_2_, double p_i1730_4_, double p_i1730_6_, @Nullable EntityLivingBase p_i1730_8_) {
      this(p_i1730_1_);
      this.setPosition(p_i1730_2_, p_i1730_4_, p_i1730_6_);
      float f = (float)(Math.random() * (double)((float)Math.PI * 2F));
      this.motionX = (double)(-((float)Math.sin((double)f)) * 0.02F);
      this.motionY = (double)0.2F;
      this.motionZ = (double)(-((float)Math.cos((double)f)) * 0.02F);
      this.setFuse(80);
      this.prevPosX = p_i1730_2_;
      this.prevPosY = p_i1730_4_;
      this.prevPosZ = p_i1730_6_;
      this.tntPlacedBy = p_i1730_8_;
   }

   protected void registerData() {
      this.dataManager.register(FUSE, 80);
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   public boolean canBeCollidedWith() {
      return !this.isDead;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (!this.hasNoGravity()) {
         this.motionY -= (double)0.04F;
      }

      this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
      this.motionX *= (double)0.98F;
      this.motionY *= (double)0.98F;
      this.motionZ *= (double)0.98F;
      if (this.onGround) {
         this.motionX *= (double)0.7F;
         this.motionZ *= (double)0.7F;
         this.motionY *= -0.5D;
      }

      --this.fuse;
      if (this.fuse <= 0) {
         this.setDead();
         if (!this.world.isRemote) {
            this.explode();
         }
      } else {
         this.handleWaterMovement();
         this.world.spawnParticle(Particles.SMOKE, this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
      }

   }

   private void explode() {
      float f = 4.0F;
      this.world.createExplosion(this, this.posX, this.posY + (double)(this.height / 16.0F), this.posZ, 4.0F, true);
   }

   protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      p_70014_1_.setShort("Fuse", (short)this.getFuse());
   }

   protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      this.setFuse(p_70037_1_.getShort("Fuse"));
   }

   @Nullable
   public EntityLivingBase getTntPlacedBy() {
      return this.tntPlacedBy;
   }

   public float getEyeHeight() {
      return 0.0F;
   }

   public void setFuse(int p_184534_1_) {
      this.dataManager.set(FUSE, p_184534_1_);
      this.fuse = p_184534_1_;
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      if (FUSE.equals(p_184206_1_)) {
         this.fuse = this.getFuseDataManager();
      }

   }

   public int getFuseDataManager() {
      return this.dataManager.get(FUSE);
   }

   public int getFuse() {
      return this.fuse;
   }
}
