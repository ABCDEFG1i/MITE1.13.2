package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntitySpectralArrow extends EntityArrow {
   private int duration = 200;

   public EntitySpectralArrow(World p_i46767_1_) {
      super(EntityType.SPECTRAL_ARROW, p_i46767_1_);
   }

   public EntitySpectralArrow(World p_i46768_1_, EntityLivingBase p_i46768_2_) {
      super(EntityType.SPECTRAL_ARROW, p_i46768_2_, p_i46768_1_);
   }

   public EntitySpectralArrow(World p_i46769_1_, double p_i46769_2_, double p_i46769_4_, double p_i46769_6_) {
      super(EntityType.SPECTRAL_ARROW, p_i46769_2_, p_i46769_4_, p_i46769_6_, p_i46769_1_);
   }

   public void tick() {
      super.tick();
      if (this.world.isRemote && !this.inGround) {
         this.world.spawnParticle(Particles.INSTANT_EFFECT, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
      }

   }

   protected ItemStack getArrowStack() {
      return new ItemStack(Items.SPECTRAL_ARROW);
   }

   protected void arrowHit(EntityLivingBase p_184548_1_) {
      super.arrowHit(p_184548_1_);
      PotionEffect potioneffect = new PotionEffect(MobEffects.GLOWING, this.duration, 0);
      p_184548_1_.addPotionEffect(potioneffect);
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      if (p_70037_1_.hasKey("Duration")) {
         this.duration = p_70037_1_.getInteger("Duration");
      }

   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setInteger("Duration", this.duration);
   }
}
