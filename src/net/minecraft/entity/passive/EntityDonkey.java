package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityType;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityDonkey extends AbstractChestHorse {
   public EntityDonkey(World p_i47298_1_) {
      super(EntityType.DONKEY, p_i47298_1_);
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_DONKEY;
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.ENTITY_DONKEY_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.ENTITY_DONKEY_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      super.getHurtSound(p_184601_1_);
      return SoundEvents.ENTITY_DONKEY_HURT;
   }

   public boolean canMateWith(EntityAnimal p_70878_1_) {
      if (p_70878_1_ == this) {
         return false;
      } else if (!(p_70878_1_ instanceof EntityDonkey) && !(p_70878_1_ instanceof EntityHorse)) {
         return false;
      } else {
         return this.canMate() && ((AbstractHorse)p_70878_1_).canMate();
      }
   }

   public EntityAgeable createChild(EntityAgeable p_90011_1_) {
      AbstractHorse abstracthorse = p_90011_1_ instanceof EntityHorse ? new EntityMule(this.world) : new EntityDonkey(this.world);
      this.setOffspringAttributes(p_90011_1_, abstracthorse);
      return abstracthorse;
   }
}
