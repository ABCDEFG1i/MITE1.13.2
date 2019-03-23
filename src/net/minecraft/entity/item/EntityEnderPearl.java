package net.minecraft.entity.item;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Particles;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityEnderPearl extends EntityThrowable {
   private EntityLivingBase perlThrower;

   public EntityEnderPearl(World p_i46455_1_) {
      super(EntityType.ENDER_PEARL, p_i46455_1_);
   }

   public EntityEnderPearl(World p_i1783_1_, EntityLivingBase p_i1783_2_) {
      super(EntityType.ENDER_PEARL, p_i1783_2_, p_i1783_1_);
      this.perlThrower = p_i1783_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public EntityEnderPearl(World p_i1784_1_, double p_i1784_2_, double p_i1784_4_, double p_i1784_6_) {
      super(EntityType.ENDER_PEARL, p_i1784_2_, p_i1784_4_, p_i1784_6_, p_i1784_1_);
   }

   protected void onImpact(RayTraceResult p_70184_1_) {
      EntityLivingBase entitylivingbase = this.getThrower();
      if (p_70184_1_.entity != null) {
         if (p_70184_1_.entity == this.perlThrower) {
            return;
         }

         p_70184_1_.entity.attackEntityFrom(DamageSource.causeThrownDamage(this, entitylivingbase), 0.0F);
      }

      if (p_70184_1_.type == RayTraceResult.Type.BLOCK) {
         BlockPos blockpos = p_70184_1_.getBlockPos();
         TileEntity tileentity = this.world.getTileEntity(blockpos);
         if (tileentity instanceof TileEntityEndGateway) {
            TileEntityEndGateway tileentityendgateway = (TileEntityEndGateway)tileentity;
            if (entitylivingbase != null) {
               if (entitylivingbase instanceof EntityPlayerMP) {
                  CriteriaTriggers.ENTER_BLOCK.trigger((EntityPlayerMP)entitylivingbase, this.world.getBlockState(blockpos));
               }

               tileentityendgateway.teleportEntity(entitylivingbase);
               this.setDead();
               return;
            }

            tileentityendgateway.teleportEntity(this);
            return;
         }
      }

      for(int i = 0; i < 32; ++i) {
         this.world.spawnParticle(Particles.PORTAL, this.posX, this.posY + this.rand.nextDouble() * 2.0D, this.posZ, this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian());
      }

      if (!this.world.isRemote) {
         if (entitylivingbase instanceof EntityPlayerMP) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)entitylivingbase;
            if (entityplayermp.connection.getNetworkManager().isChannelOpen() && entityplayermp.world == this.world && !entityplayermp.isPlayerSleeping()) {
               if (this.rand.nextFloat() < 0.05F && this.world.getGameRules().getBoolean("doMobSpawning")) {
                  EntityEndermite entityendermite = new EntityEndermite(this.world);
                  entityendermite.setSpawnedByPlayer(true);
                  entityendermite.setLocationAndAngles(entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, entitylivingbase.rotationYaw, entitylivingbase.rotationPitch);
                  this.world.spawnEntity(entityendermite);
               }

               if (entitylivingbase.isRiding()) {
                  entitylivingbase.dismountRidingEntity();
               }

               entitylivingbase.setPositionAndUpdate(this.posX, this.posY, this.posZ);
               entitylivingbase.fallDistance = 0.0F;
               entitylivingbase.attackEntityFrom(DamageSource.FALL, 5.0F);
            }
         } else if (entitylivingbase != null) {
            entitylivingbase.setPositionAndUpdate(this.posX, this.posY, this.posZ);
            entitylivingbase.fallDistance = 0.0F;
         }

         this.setDead();
      }

   }

   public void tick() {
      EntityLivingBase entitylivingbase = this.getThrower();
      if (entitylivingbase != null && entitylivingbase instanceof EntityPlayer && !entitylivingbase.isEntityAlive()) {
         this.setDead();
      } else {
         super.tick();
      }

   }

   @Nullable
   public Entity func_212321_a(DimensionType p_212321_1_) {
      if (this.thrower.dimension != p_212321_1_) {
         this.thrower = null;
      }

      return super.func_212321_a(p_212321_1_);
   }
}
