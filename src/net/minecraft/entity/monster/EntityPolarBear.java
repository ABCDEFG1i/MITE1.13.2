package net.minecraft.entity.monster;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityPolarBear extends EntityAnimal {
   private static final DataParameter<Boolean> IS_STANDING = EntityDataManager.createKey(EntityPolarBear.class, DataSerializers.BOOLEAN);
   private float clientSideStandAnimation0;
   private float clientSideStandAnimation;
   private int warningSoundTicks;

   public EntityPolarBear(World p_i47154_1_) {
      super(EntityType.POLAR_BEAR, p_i47154_1_);
      this.setSize(1.3F, 1.4F);
   }

   public EntityAgeable createChild(EntityAgeable p_90011_1_) {
      return new EntityPolarBear(this.world);
   }

   public boolean isBreedingItem(ItemStack p_70877_1_) {
      return false;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityPolarBear.AIMeleeAttack());
      this.tasks.addTask(1, new EntityPolarBear.AIPanic());
      this.tasks.addTask(4, new EntityAIFollowParent(this, 1.25D));
      this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
      this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(7, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityPolarBear.AIHurtByTarget());
      this.targetTasks.addTask(2, new EntityPolarBear.AIAttackPlayer());
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
   }

   public boolean func_205020_a(IWorld p_205020_1_, boolean p_205020_2_) {
      int i = MathHelper.floor(this.posX);
      int j = MathHelper.floor(this.getEntityBoundingBox().minY);
      int k = MathHelper.floor(this.posZ);
      BlockPos blockpos = new BlockPos(i, j, k);
      Biome biome = p_205020_1_.getBiome(blockpos);
      if (biome != Biomes.FROZEN_OCEAN && biome != Biomes.DEEP_FROZEN_OCEAN) {
         return super.func_205020_a(p_205020_1_, p_205020_2_);
      } else {
         return p_205020_1_.getLightSubtracted(blockpos, 0) > 8 && p_205020_1_.getBlockState(blockpos.down()).getBlock() == Blocks.ICE;
      }
   }

   protected SoundEvent getAmbientSound() {
      return this.isChild() ? SoundEvents.ENTITY_POLAR_BEAR_AMBIENT_BABY : SoundEvents.ENTITY_POLAR_BEAR_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_POLAR_BEAR_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_POLAR_BEAR_DEATH;
   }

   protected void playStepSound(BlockPos p_180429_1_, IBlockState p_180429_2_) {
      this.playSound(SoundEvents.ENTITY_POLAR_BEAR_STEP, 0.15F, 1.0F);
   }

   protected void playWarningSound() {
      if (this.warningSoundTicks <= 0) {
         this.playSound(SoundEvents.ENTITY_POLAR_BEAR_WARNING, 1.0F, 1.0F);
         this.warningSoundTicks = 40;
      }

   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_POLAR_BEAR;
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(IS_STANDING, false);
   }

   public void tick() {
      super.tick();
      if (this.world.isRemote) {
         this.clientSideStandAnimation0 = this.clientSideStandAnimation;
         if (this.isStanding()) {
            this.clientSideStandAnimation = MathHelper.clamp(this.clientSideStandAnimation + 1.0F, 0.0F, 6.0F);
         } else {
            this.clientSideStandAnimation = MathHelper.clamp(this.clientSideStandAnimation - 1.0F, 0.0F, 6.0F);
         }
      }

      if (this.warningSoundTicks > 0) {
         --this.warningSoundTicks;
      }

   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      boolean flag = p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
      if (flag) {
         this.applyEnchantments(this, p_70652_1_);
      }

      return flag;
   }

   public boolean isStanding() {
      return this.dataManager.get(IS_STANDING);
   }

   public void setStanding(boolean p_189794_1_) {
      this.dataManager.set(IS_STANDING, p_189794_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public float getStandingAnimationScale(float p_189795_1_) {
      return (this.clientSideStandAnimation0 + (this.clientSideStandAnimation - this.clientSideStandAnimation0) * p_189795_1_) / 6.0F;
   }

   protected float getWaterSlowDown() {
      return 0.98F;
   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance p_204210_1_, @Nullable IEntityLivingData p_204210_2_, @Nullable NBTTagCompound p_204210_3_) {
      if (p_204210_2_ instanceof EntityPolarBear.GroupData) {
         if (((EntityPolarBear.GroupData)p_204210_2_).madeParent) {
            this.setGrowingAge(-24000);
         }
      } else {
         EntityPolarBear.GroupData entitypolarbear$groupdata = new EntityPolarBear.GroupData();
         entitypolarbear$groupdata.madeParent = true;
         p_204210_2_ = entitypolarbear$groupdata;
      }

      return p_204210_2_;
   }

   class AIAttackPlayer extends EntityAINearestAttackableTarget<EntityPlayer> {
      public AIAttackPlayer() {
         super(EntityPolarBear.this, EntityPlayer.class, 20, true, true, (Predicate<EntityPlayer>)null);
      }

      public boolean shouldExecute() {
         if (EntityPolarBear.this.isChild()) {
            return false;
         } else {
            if (super.shouldExecute()) {
               for(EntityPolarBear entitypolarbear : EntityPolarBear.this.world.getEntitiesWithinAABB(EntityPolarBear.class, EntityPolarBear.this.getEntityBoundingBox().grow(8.0D, 4.0D, 8.0D))) {
                  if (entitypolarbear.isChild()) {
                     return true;
                  }
               }
            }

            EntityPolarBear.this.setAttackTarget((EntityLivingBase)null);
            return false;
         }
      }

      protected double getTargetDistance() {
         return super.getTargetDistance() * 0.5D;
      }
   }

   class AIHurtByTarget extends EntityAIHurtByTarget {
      public AIHurtByTarget() {
         super(EntityPolarBear.this, false);
      }

      public void startExecuting() {
         super.startExecuting();
         if (EntityPolarBear.this.isChild()) {
            this.alertOthers();
            this.resetTask();
         }

      }

      protected void setEntityAttackTarget(EntityCreature p_179446_1_, EntityLivingBase p_179446_2_) {
         if (p_179446_1_ instanceof EntityPolarBear && !p_179446_1_.isChild()) {
            super.setEntityAttackTarget(p_179446_1_, p_179446_2_);
         }

      }
   }

   class AIMeleeAttack extends EntityAIAttackMelee {
      public AIMeleeAttack() {
         super(EntityPolarBear.this, 1.25D, true);
      }

      protected void checkAndPerformAttack(EntityLivingBase p_190102_1_, double p_190102_2_) {
         double d0 = this.getAttackReachSqr(p_190102_1_);
         if (p_190102_2_ <= d0 && this.attackTick <= 0) {
            this.attackTick = 20;
            this.attacker.attackEntityAsMob(p_190102_1_);
            EntityPolarBear.this.setStanding(false);
         } else if (p_190102_2_ <= d0 * 2.0D) {
            if (this.attackTick <= 0) {
               EntityPolarBear.this.setStanding(false);
               this.attackTick = 20;
            }

            if (this.attackTick <= 10) {
               EntityPolarBear.this.setStanding(true);
               EntityPolarBear.this.playWarningSound();
            }
         } else {
            this.attackTick = 20;
            EntityPolarBear.this.setStanding(false);
         }

      }

      public void resetTask() {
         EntityPolarBear.this.setStanding(false);
         super.resetTask();
      }

      protected double getAttackReachSqr(EntityLivingBase p_179512_1_) {
         return (double)(4.0F + p_179512_1_.width);
      }
   }

   class AIPanic extends EntityAIPanic {
      public AIPanic() {
         super(EntityPolarBear.this, 2.0D);
      }

      public boolean shouldExecute() {
         return !EntityPolarBear.this.isChild() && !EntityPolarBear.this.isBurning() ? false : super.shouldExecute();
      }
   }

   static class GroupData implements IEntityLivingData {
      public boolean madeParent;

      private GroupData() {
      }
   }
}
