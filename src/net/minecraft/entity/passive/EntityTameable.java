package net.minecraft.entity.passive;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class EntityTameable extends EntityAnimal implements IEntityOwnable {
   protected static final DataParameter<Byte> TAMED = EntityDataManager.createKey(EntityTameable.class, DataSerializers.BYTE);
   protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.createKey(EntityTameable.class, DataSerializers.OPTIONAL_UNIQUE_ID);
   protected EntityAISit aiSit;

   protected EntityTameable(EntityType<?> p_i48574_1_, World p_i48574_2_) {
      super(p_i48574_1_, p_i48574_2_);
      this.setupTamedAI();
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(TAMED, (byte)0);
      this.dataManager.register(OWNER_UNIQUE_ID, Optional.empty());
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      if (this.getOwnerId() == null) {
         p_70014_1_.setString("OwnerUUID", "");
      } else {
         p_70014_1_.setString("OwnerUUID", this.getOwnerId().toString());
      }

      p_70014_1_.setBoolean("Sitting", this.isSitting());
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      String s;
      if (p_70037_1_.hasKey("OwnerUUID", 8)) {
         s = p_70037_1_.getString("OwnerUUID");
      } else {
         String s1 = p_70037_1_.getString("Owner");
         s = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), s1);
      }

      if (!s.isEmpty()) {
         try {
            this.setOwnerId(UUID.fromString(s));
            this.setTamed(true);
         } catch (Throwable var4) {
            this.setTamed(false);
         }
      }

      if (this.aiSit != null) {
         this.aiSit.setSitting(p_70037_1_.getBoolean("Sitting"));
      }

      this.setSitting(p_70037_1_.getBoolean("Sitting"));
   }

   public boolean canBeLeashedTo(EntityPlayer p_184652_1_) {
      return !this.getLeashed();
   }

   protected void playTameEffect(boolean p_70908_1_) {
      IParticleData iparticledata = Particles.HEART;
      if (!p_70908_1_) {
         iparticledata = Particles.SMOKE;
      }

      for(int i = 0; i < 7; ++i) {
         double d0 = this.rand.nextGaussian() * 0.02D;
         double d1 = this.rand.nextGaussian() * 0.02D;
         double d2 = this.rand.nextGaussian() * 0.02D;
         this.world.spawnParticle(iparticledata, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 7) {
         this.playTameEffect(true);
      } else if (p_70103_1_ == 6) {
         this.playTameEffect(false);
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   public boolean isTamed() {
      return (this.dataManager.get(TAMED) & 4) != 0;
   }

   public void setTamed(boolean p_70903_1_) {
      byte b0 = this.dataManager.get(TAMED);
      if (p_70903_1_) {
         this.dataManager.set(TAMED, (byte)(b0 | 4));
      } else {
         this.dataManager.set(TAMED, (byte)(b0 & -5));
      }

      this.setupTamedAI();
   }

   protected void setupTamedAI() {
   }

   public boolean isSitting() {
      return (this.dataManager.get(TAMED) & 1) != 0;
   }

   public void setSitting(boolean p_70904_1_) {
      byte b0 = this.dataManager.get(TAMED);
      if (p_70904_1_) {
         this.dataManager.set(TAMED, (byte)(b0 | 1));
      } else {
         this.dataManager.set(TAMED, (byte)(b0 & -2));
      }

   }

   @Nullable
   public UUID getOwnerId() {
      return this.dataManager.get(OWNER_UNIQUE_ID).orElse(null);
   }

   public void setOwnerId(@Nullable UUID p_184754_1_) {
      this.dataManager.set(OWNER_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
   }

   public void setTamedBy(EntityPlayer p_193101_1_) {
      this.setTamed(true);
      this.setOwnerId(p_193101_1_.getUniqueID());
      if (p_193101_1_ instanceof EntityPlayerMP) {
         CriteriaTriggers.TAME_ANIMAL.trigger((EntityPlayerMP)p_193101_1_, this);
      }

   }

   @Nullable
   public EntityLivingBase getOwner() {
      try {
         UUID uuid = this.getOwnerId();
         return uuid == null ? null : this.world.getPlayerEntityByUUID(uuid);
      } catch (IllegalArgumentException var2) {
         return null;
      }
   }

   public boolean isOwner(EntityLivingBase p_152114_1_) {
      return p_152114_1_ == this.getOwner();
   }

   public EntityAISit getAISit() {
      return this.aiSit;
   }

   public boolean shouldAttackEntity(EntityLivingBase p_142018_1_, EntityLivingBase p_142018_2_) {
      return true;
   }

   public Team getTeam() {
      if (this.isTamed()) {
         EntityLivingBase entitylivingbase = this.getOwner();
         if (entitylivingbase != null) {
            return entitylivingbase.getTeam();
         }
      }

      return super.getTeam();
   }

   public boolean isOnSameTeam(Entity p_184191_1_) {
      if (this.isTamed()) {
         EntityLivingBase entitylivingbase = this.getOwner();
         if (p_184191_1_ == entitylivingbase) {
            return true;
         }

         if (entitylivingbase != null) {
            return entitylivingbase.isOnSameTeam(p_184191_1_);
         }
      }

      return super.isOnSameTeam(p_184191_1_);
   }

   public void onDeath(DamageSource p_70645_1_) {
      if (!this.world.isRemote && this.world.getGameRules().getBoolean("showDeathMessages") && this.getOwner() instanceof EntityPlayerMP) {
         this.getOwner().sendMessage(this.getCombatTracker().getDeathMessage());
      }

      super.onDeath(p_70645_1_);
   }
}
