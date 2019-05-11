package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.IAnimal;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public abstract class EntityGolem extends EntityCreature implements IAnimal {
   protected EntityGolem(EntityType<?> p_i48569_1_, World p_i48569_2_) {
      super(p_i48569_1_, p_i48569_2_);
   }

   public void fall(float p_180430_1_, float p_180430_2_, boolean isNormalBlock) {
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return null;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return null;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return null;
   }

   public int getTalkInterval() {
      return 120;
   }

   public boolean canDespawn() {
      return false;
   }
}
