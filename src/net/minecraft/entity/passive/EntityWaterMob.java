package net.minecraft.entity.passive;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class EntityWaterMob extends EntityCreature implements IAnimal {
   protected EntityWaterMob(EntityType<?> p_i48565_1_, World p_i48565_2_) {
      super(p_i48565_1_, p_i48565_2_);
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.WATER;
   }

   public boolean isNotColliding(IWorldReaderBase p_205019_1_) {
      return p_205019_1_.checkNoEntityCollision(this, this.getEntityBoundingBox()) && p_205019_1_.isCollisionBoxesEmpty(this, this.getEntityBoundingBox());
   }

   public int getTalkInterval() {
      return 120;
   }

   public boolean canDespawn() {
      return true;
   }

   protected int getExperiencePoints(EntityPlayer p_70693_1_) {
      return 1 + this.world.rand.nextInt(3);
   }

   protected void updateAir(int p_209207_1_) {
      if (this.isEntityAlive() && !this.isInWaterOrBubbleColumn()) {
         this.setAir(p_209207_1_ - 1);
         if (this.getAir() == -20) {
            this.setAir(0);
            this.attackEntityFrom(DamageSource.DROWN, 2.0F);
         }
      } else {
         this.setAir(300);
      }

   }

   public void baseTick() {
      int i = this.getAir();
      super.baseTick();
      this.updateAir(i);
   }

   public boolean isPushedByWater() {
      return false;
   }

   public boolean canBeLeashedTo(EntityPlayer p_184652_1_) {
      return false;
   }
}
