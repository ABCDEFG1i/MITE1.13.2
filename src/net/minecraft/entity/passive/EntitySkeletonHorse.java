package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAISkeletonRiders;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntitySkeletonHorse extends AbstractHorse {
   private final EntityAISkeletonRiders skeletonTrapAI = new EntityAISkeletonRiders(this);
   private boolean skeletonTrap;
   private int skeletonTrapTime;

   public EntitySkeletonHorse(World p_i47295_1_) {
      super(EntityType.SKELETON_HORSE, p_i47295_1_);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.2F);
      this.getAttribute(JUMP_STRENGTH).setBaseValue(this.getModifiedJumpStrength());
   }

   protected void func_205714_dM() {
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return this.areEyesInFluid(FluidTags.WATER) ? SoundEvents.ENTITY_SKELETON_HORSE_AMBIENT_WATER : SoundEvents.ENTITY_SKELETON_HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.ENTITY_SKELETON_HORSE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      super.getHurtSound(p_184601_1_);
      return SoundEvents.ENTITY_SKELETON_HORSE_HURT;
   }

   protected SoundEvent getSwimSound() {
      if (this.onGround) {
         if (!this.isBeingRidden()) {
            return SoundEvents.ENTITY_SKELETON_HORSE_STEP_WATER;
         }

         ++this.gallopTime;
         if (this.gallopTime > 5 && this.gallopTime % 3 == 0) {
            return SoundEvents.ENTITY_SKELETON_HORSE_GALLOP_WATER;
         }

         if (this.gallopTime <= 5) {
            return SoundEvents.ENTITY_SKELETON_HORSE_STEP_WATER;
         }
      }

      return SoundEvents.ENTITY_SKELETON_HORSE_SWIM;
   }

   protected void playSwimSound(float p_203006_1_) {
      if (this.onGround) {
         super.playSwimSound(0.3F);
      } else {
         super.playSwimSound(Math.min(0.1F, p_203006_1_ * 25.0F));
      }

   }

   protected void func_205715_ee() {
      if (this.isInWater()) {
         this.playSound(SoundEvents.ENTITY_SKELETON_HORSE_JUMP_WATER, 0.4F, 1.0F);
      } else {
         super.func_205715_ee();
      }

   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.UNDEAD;
   }

   public double getMountedYOffset() {
      return super.getMountedYOffset() - 0.1875D;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_SKELETON_HORSE;
   }

   public void livingTick() {
      super.livingTick();
      if (this.isTrap() && this.skeletonTrapTime++ >= 18000) {
         this.setDead();
      }

   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setBoolean("SkeletonTrap", this.isTrap());
      p_70014_1_.setInteger("SkeletonTrapTime", this.skeletonTrapTime);
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      this.setTrap(p_70037_1_.getBoolean("SkeletonTrap"));
      this.skeletonTrapTime = p_70037_1_.getInteger("SkeletonTrapTime");
   }

   public boolean canBeRiddenInWater() {
      return true;
   }

   protected float getWaterSlowDown() {
      return 0.96F;
   }

   public boolean isTrap() {
      return this.skeletonTrap;
   }

   public void setTrap(boolean p_190691_1_) {
      if (p_190691_1_ != this.skeletonTrap) {
         this.skeletonTrap = p_190691_1_;
         if (p_190691_1_) {
            this.tasks.addTask(1, this.skeletonTrapAI);
         } else {
            this.tasks.removeTask(this.skeletonTrapAI);
         }

      }
   }

   @Nullable
   public EntityAgeable createChild(EntityAgeable p_90011_1_) {
      return new EntitySkeletonHorse(this.world);
   }

   public boolean processInteract(EntityPlayer p_184645_1_, EnumHand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getHeldItem(p_184645_2_);
      if (itemstack.getItem() instanceof ItemSpawnEgg) {
         return super.processInteract(p_184645_1_, p_184645_2_);
      } else if (!this.isTame()) {
         return false;
      } else if (this.isChild()) {
         return super.processInteract(p_184645_1_, p_184645_2_);
      } else if (p_184645_1_.isSneaking()) {
         this.openGUI(p_184645_1_);
         return true;
      } else if (this.isBeingRidden()) {
         return super.processInteract(p_184645_1_, p_184645_2_);
      } else {
         if (!itemstack.isEmpty()) {
            if (itemstack.getItem() == Items.SADDLE && !this.isHorseSaddled()) {
               this.openGUI(p_184645_1_);
               return true;
            }

            if (itemstack.interactWithEntity(p_184645_1_, this, p_184645_2_)) {
               return true;
            }
         }

         this.mountTo(p_184645_1_);
         return true;
      }
   }
}
