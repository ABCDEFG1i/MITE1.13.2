package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityWitherSkeleton extends AbstractSkeleton {
   public EntityWitherSkeleton(World p_i47278_1_) {
      super(EntityType.WITHER_SKELETON, p_i47278_1_);
      this.setSize(0.7F, 2.4F);
      this.isImmuneToFire = true;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_WITHER_SKELETON;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_WITHER_SKELETON_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_WITHER_SKELETON_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_WITHER_SKELETON_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_WITHER_SKELETON_STEP;
   }

   public void onDeath(DamageSource p_70645_1_) {
      super.onDeath(p_70645_1_);
      if (p_70645_1_.getTrueSource() instanceof EntityCreeper) {
         EntityCreeper entitycreeper = (EntityCreeper)p_70645_1_.getTrueSource();
         if (entitycreeper.getPowered() && entitycreeper.ableToCauseSkullDrop()) {
            entitycreeper.incrementDroppedSkulls();
            this.entityDropItem(Items.WITHER_SKELETON_SKULL);
         }
      }

   }

   protected void setEquipmentBasedOnDifficulty(DifficultyInstance p_180481_1_) {
      this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.WOODEN_SWORD));
   }

   protected void setEnchantmentBasedOnDifficulty(DifficultyInstance p_180483_1_) {
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance p_204210_1_, @Nullable IEntityLivingData p_204210_2_, @Nullable NBTTagCompound p_204210_3_) {
      IEntityLivingData ientitylivingdata = super.onInitialSpawn(p_204210_1_, p_204210_2_, p_204210_3_);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
      this.setCombatTask();
      return ientitylivingdata;
   }

   public float getEyeHeight() {
      return 2.1F;
   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      if (!super.attackEntityAsMob(p_70652_1_)) {
         return false;
      } else {
         if (p_70652_1_ instanceof EntityLivingBase) {
            ((EntityLivingBase)p_70652_1_).addPotionEffect(new PotionEffect(MobEffects.WITHER, 200));
         }

         return true;
      }
   }

   protected EntityArrow getArrow(float p_190726_1_) {
      EntityArrow entityarrow = super.getArrow(p_190726_1_);
      entityarrow.setFire(100);
      return entityarrow;
   }
}
