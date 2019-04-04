package net.minecraft.entity.monster;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class EntityMob extends EntityCreature implements IMob {
   protected EntityMob(EntityType<?> p_i48553_1_, World p_i48553_2_) {
      super(p_i48553_1_, p_i48553_2_);
      this.experienceValue = 5;
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.HOSTILE;
   }

   public void livingTick() {
      this.updateArmSwingProgress();
      float f = this.getBrightness();
      if (f > 0.5F) {
         this.idleTime += 2;
      }

      super.livingTick();
   }

   public void tick() {
      super.tick();
      if (!this.world.isRemote && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
         this.setDead();
      }

   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.ENTITY_HOSTILE_SWIM;
   }

   protected SoundEvent getSplashSound() {
      return SoundEvents.ENTITY_HOSTILE_SPLASH;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      return !this.isInvulnerableTo(p_70097_1_) && super.attackEntityFrom(p_70097_1_, p_70097_2_);
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_HOSTILE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_HOSTILE_DEATH;
   }

   protected SoundEvent getFallSound(int p_184588_1_) {
      return p_184588_1_ > 4 ? SoundEvents.ENTITY_HOSTILE_BIG_FALL : SoundEvents.ENTITY_HOSTILE_SMALL_FALL;
   }

   public float getBlockPathWeight(BlockPos p_205022_1_, IWorldReaderBase p_205022_2_) {
      return 0.5F - p_205022_2_.getBrightness(p_205022_1_);
   }

   protected boolean isValidLightLevel() {
      BlockPos blockpos = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);
      if (this.world.getLightFor(EnumLightType.SKY, blockpos) > this.rand.nextInt(32)) {
         return false;
      } else {
         int i = this.world.isThundering() ? this.world.getNeighborAwareLightSubtracted(blockpos, 10) : this.world.getLight(blockpos);
         return i <= this.rand.nextInt(8);
      }
   }

   public boolean func_205020_a(IWorld p_205020_1_, boolean p_205020_2_) {
      return p_205020_1_.getDifficulty() != EnumDifficulty.PEACEFUL && this.isValidLightLevel() && super.func_205020_a(p_205020_1_, p_205020_2_);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
   }

   protected boolean canDropLoot() {
      return true;
   }

   public boolean isPreventingPlayerRest(EntityPlayer p_191990_1_) {
      return true;
   }
}
