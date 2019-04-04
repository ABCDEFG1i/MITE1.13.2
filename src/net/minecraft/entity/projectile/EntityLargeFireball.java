package net.minecraft.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityLargeFireball extends EntityFireball {
   public int explosionPower = 1;

   public EntityLargeFireball(World p_i1767_1_) {
      super(EntityType.FIREBALL, p_i1767_1_, 1.0F, 1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public EntityLargeFireball(World p_i1768_1_, double p_i1768_2_, double p_i1768_4_, double p_i1768_6_, double p_i1768_8_, double p_i1768_10_, double p_i1768_12_) {
      super(EntityType.FIREBALL, p_i1768_2_, p_i1768_4_, p_i1768_6_, p_i1768_8_, p_i1768_10_, p_i1768_12_, p_i1768_1_, 1.0F, 1.0F);
   }

   public EntityLargeFireball(World p_i1769_1_, EntityLivingBase p_i1769_2_, double p_i1769_3_, double p_i1769_5_, double p_i1769_7_) {
      super(EntityType.FIREBALL, p_i1769_2_, p_i1769_3_, p_i1769_5_, p_i1769_7_, p_i1769_1_, 1.0F, 1.0F);
   }

   protected void onImpact(RayTraceResult p_70227_1_) {
      if (!this.world.isRemote) {
         if (p_70227_1_.entity != null) {
            p_70227_1_.entity.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 6.0F);
            this.applyEnchantments(this.shootingEntity, p_70227_1_.entity);
         }

         boolean flag = this.world.getGameRules().getBoolean("mobGriefing");
         this.world.newExplosion(null, this.posX, this.posY, this.posZ, (float)this.explosionPower, flag, flag);
         this.setDead();
      }

   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setInteger("ExplosionPower", this.explosionPower);
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      if (p_70037_1_.hasKey("ExplosionPower", 99)) {
         this.explosionPower = p_70037_1_.getInteger("ExplosionPower");
      }

   }
}
