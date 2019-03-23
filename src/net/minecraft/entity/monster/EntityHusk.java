package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityHusk extends EntityZombie {
   public EntityHusk(World p_i47286_1_) {
      super(EntityType.HUSK, p_i47286_1_);
   }

   public boolean func_205020_a(IWorld p_205020_1_, boolean p_205020_2_) {
      return super.func_205020_a(p_205020_1_, p_205020_2_) && (p_205020_2_ || p_205020_1_.canSeeSky(new BlockPos(this)));
   }

   protected boolean shouldBurnInDay() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_HUSK_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_HUSK_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_HUSK_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_HUSK_STEP;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_HUSK;
   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      boolean flag = super.attackEntityAsMob(p_70652_1_);
      if (flag && this.getHeldItemMainhand().isEmpty() && p_70652_1_ instanceof EntityLivingBase) {
         float f = this.world.getDifficultyForLocation(new BlockPos(this)).getAdditionalDifficulty();
         ((EntityLivingBase)p_70652_1_).addPotionEffect(new PotionEffect(MobEffects.HUNGER, 140 * (int)f));
      }

      return flag;
   }

   protected boolean shouldDrown() {
      return true;
   }

   protected void onDrowned() {
      this.convertInto(new EntityZombie(this.world));
      this.world.playEvent((EntityPlayer)null, 1041, new BlockPos((int)this.posX, (int)this.posY, (int)this.posZ), 0);
   }

   protected ItemStack getSkullDrop() {
      return ItemStack.EMPTY;
   }
}
