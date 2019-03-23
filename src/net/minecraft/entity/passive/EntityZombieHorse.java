package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityZombieHorse extends AbstractHorse {
   public EntityZombieHorse(World p_i47293_1_) {
      super(EntityType.ZOMBIE_HORSE, p_i47293_1_);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.2F);
      this.getAttribute(JUMP_STRENGTH).setBaseValue(this.getModifiedJumpStrength());
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.UNDEAD;
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.ENTITY_ZOMBIE_HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.ENTITY_ZOMBIE_HORSE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      super.getHurtSound(p_184601_1_);
      return SoundEvents.ENTITY_ZOMBIE_HORSE_HURT;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_ZOMBIE_HORSE;
   }

   @Nullable
   public EntityAgeable createChild(EntityAgeable p_90011_1_) {
      return new EntityZombieHorse(this.world);
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
            if (!this.isHorseSaddled() && itemstack.getItem() == Items.SADDLE) {
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

   protected void func_205714_dM() {
   }
}
