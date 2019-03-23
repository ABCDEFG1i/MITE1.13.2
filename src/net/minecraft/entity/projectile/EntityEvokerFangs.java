package net.minecraft.entity.projectile;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityEvokerFangs extends Entity {
   private int warmupDelayTicks;
   private boolean sentSpikeEvent;
   private int lifeTicks = 22;
   private boolean clientSideAttackStarted;
   private EntityLivingBase caster;
   private UUID casterUuid;

   public EntityEvokerFangs(World p_i47275_1_) {
      super(EntityType.EVOKER_FANGS, p_i47275_1_);
      this.setSize(0.5F, 0.8F);
   }

   public EntityEvokerFangs(World p_i47276_1_, double p_i47276_2_, double p_i47276_4_, double p_i47276_6_, float p_i47276_8_, int p_i47276_9_, EntityLivingBase p_i47276_10_) {
      this(p_i47276_1_);
      this.warmupDelayTicks = p_i47276_9_;
      this.setCaster(p_i47276_10_);
      this.rotationYaw = p_i47276_8_ * (180F / (float)Math.PI);
      this.setPosition(p_i47276_2_, p_i47276_4_, p_i47276_6_);
   }

   protected void registerData() {
   }

   public void setCaster(@Nullable EntityLivingBase p_190549_1_) {
      this.caster = p_190549_1_;
      this.casterUuid = p_190549_1_ == null ? null : p_190549_1_.getUniqueID();
   }

   @Nullable
   public EntityLivingBase getCaster() {
      if (this.caster == null && this.casterUuid != null && this.world instanceof WorldServer) {
         Entity entity = ((WorldServer)this.world).getEntityFromUuid(this.casterUuid);
         if (entity instanceof EntityLivingBase) {
            this.caster = (EntityLivingBase)entity;
         }
      }

      return this.caster;
   }

   protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      this.warmupDelayTicks = p_70037_1_.getInteger("Warmup");
      if (p_70037_1_.hasUniqueId("OwnerUUID")) {
         this.casterUuid = p_70037_1_.getUniqueId("OwnerUUID");
      }

   }

   protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      p_70014_1_.setInteger("Warmup", this.warmupDelayTicks);
      if (this.casterUuid != null) {
         p_70014_1_.setUniqueId("OwnerUUID", this.casterUuid);
      }

   }

   public void tick() {
      super.tick();
      if (this.world.isRemote) {
         if (this.clientSideAttackStarted) {
            --this.lifeTicks;
            if (this.lifeTicks == 14) {
               for(int i = 0; i < 12; ++i) {
                  double d0 = this.posX + (this.rand.nextDouble() * 2.0D - 1.0D) * (double)this.width * 0.5D;
                  double d1 = this.posY + 0.05D + this.rand.nextDouble();
                  double d2 = this.posZ + (this.rand.nextDouble() * 2.0D - 1.0D) * (double)this.width * 0.5D;
                  double d3 = (this.rand.nextDouble() * 2.0D - 1.0D) * 0.3D;
                  double d4 = 0.3D + this.rand.nextDouble() * 0.3D;
                  double d5 = (this.rand.nextDouble() * 2.0D - 1.0D) * 0.3D;
                  this.world.spawnParticle(Particles.CRIT, d0, d1 + 1.0D, d2, d3, d4, d5);
               }
            }
         }
      } else if (--this.warmupDelayTicks < 0) {
         if (this.warmupDelayTicks == -8) {
            for(EntityLivingBase entitylivingbase : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(0.2D, 0.0D, 0.2D))) {
               this.damage(entitylivingbase);
            }
         }

         if (!this.sentSpikeEvent) {
            this.world.setEntityState(this, (byte)4);
            this.sentSpikeEvent = true;
         }

         if (--this.lifeTicks < 0) {
            this.setDead();
         }
      }

   }

   private void damage(EntityLivingBase p_190551_1_) {
      EntityLivingBase entitylivingbase = this.getCaster();
      if (p_190551_1_.isEntityAlive() && !p_190551_1_.isInvulnerable() && p_190551_1_ != entitylivingbase) {
         if (entitylivingbase == null) {
            p_190551_1_.attackEntityFrom(DamageSource.MAGIC, 6.0F);
         } else {
            if (entitylivingbase.isOnSameTeam(p_190551_1_)) {
               return;
            }

            p_190551_1_.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, entitylivingbase), 6.0F);
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      super.handleStatusUpdate(p_70103_1_);
      if (p_70103_1_ == 4) {
         this.clientSideAttackStarted = true;
         if (!this.isSilent()) {
            this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.ENTITY_EVOKER_FANGS_ATTACK, this.getSoundCategory(), 1.0F, this.rand.nextFloat() * 0.2F + 0.85F, false);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float getAnimationProgress(float p_190550_1_) {
      if (!this.clientSideAttackStarted) {
         return 0.0F;
      } else {
         int i = this.lifeTicks - 2;
         return i <= 0 ? 1.0F : 1.0F - ((float)i - p_190550_1_) / 20.0F;
      }
   }
}
