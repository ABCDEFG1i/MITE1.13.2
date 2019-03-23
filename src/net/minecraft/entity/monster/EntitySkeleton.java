package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntitySkeleton extends AbstractSkeleton {
   public EntitySkeleton(World p_i1741_1_) {
      super(EntityType.SKELETON, p_i1741_1_);
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_SKELETON;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SKELETON_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_SKELETON_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SKELETON_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_SKELETON_STEP;
   }

   public void onDeath(DamageSource p_70645_1_) {
      super.onDeath(p_70645_1_);
      if (p_70645_1_.getTrueSource() instanceof EntityCreeper) {
         EntityCreeper entitycreeper = (EntityCreeper)p_70645_1_.getTrueSource();
         if (entitycreeper.getPowered() && entitycreeper.ableToCauseSkullDrop()) {
            entitycreeper.incrementDroppedSkulls();
            this.entityDropItem(Items.SKELETON_SKULL);
         }
      }

   }

   protected EntityArrow getArrow(float p_190726_1_) {
      ItemStack itemstack = this.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
      if (itemstack.getItem() == Items.SPECTRAL_ARROW) {
         EntitySpectralArrow entityspectralarrow = new EntitySpectralArrow(this.world, this);
         entityspectralarrow.setEnchantmentEffectsFromEntity(this, p_190726_1_);
         return entityspectralarrow;
      } else {
         EntityArrow entityarrow = super.getArrow(p_190726_1_);
         if (itemstack.getItem() == Items.TIPPED_ARROW && entityarrow instanceof EntityTippedArrow) {
            ((EntityTippedArrow)entityarrow).setPotionEffect(itemstack);
         }

         return entityarrow;
      }
   }
}
