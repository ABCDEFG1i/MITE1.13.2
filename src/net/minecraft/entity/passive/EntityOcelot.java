package net.minecraft.entity.passive;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIOcelotAttack;
import net.minecraft.entity.ai.EntityAIOcelotSit;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityOcelot extends EntityTameable {
   private static final Ingredient field_195402_bB = Ingredient.fromItems(Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH);
   private static final DataParameter<Integer> OCELOT_VARIANT = EntityDataManager.createKey(EntityOcelot.class, DataSerializers.VARINT);
   private static final ResourceLocation field_200608_bC = new ResourceLocation("cat");
   private EntityAIAvoidEntity<EntityPlayer> avoidEntity;
   private EntityAITempt aiTempt;

   public EntityOcelot(World p_i1688_1_) {
      super(EntityType.OCELOT, p_i1688_1_);
      this.setSize(0.6F, 0.7F);
   }

   protected void initEntityAI() {
      this.aiSit = new EntityAISit(this);
      this.aiTempt = new EntityAITempt(this, 0.6D, field_195402_bB, true);
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, this.aiSit);
      this.tasks.addTask(3, this.aiTempt);
      this.tasks.addTask(5, new EntityAIFollowOwner(this, 1.0D, 10.0F, 5.0F));
      this.tasks.addTask(6, new EntityAIOcelotSit(this, 0.8D));
      this.tasks.addTask(7, new EntityAILeapAtTarget(this, 0.3F));
      this.tasks.addTask(8, new EntityAIOcelotAttack(this));
      this.tasks.addTask(9, new EntityAIMate(this, 0.8D));
      this.tasks.addTask(10, new EntityAIWanderAvoidWater(this, 0.8D, 1.0000001E-5F));
      this.tasks.addTask(11, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
      this.targetTasks.addTask(1, new EntityAITargetNonTamed<>(this, EntityChicken.class, false, null));
      this.targetTasks.addTask(1, new EntityAITargetNonTamed<>(this, EntityTurtle.class, false, EntityTurtle.TARGET_DRY_BABY));
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(OCELOT_VARIANT, 0);
   }

   public void updateAITasks() {
      if (this.getMoveHelper().isUpdating()) {
         double d0 = this.getMoveHelper().getSpeed();
         if (d0 == 0.6D) {
            this.setSneaking(true);
            this.setSprinting(false);
         } else if (d0 == 1.33D) {
            this.setSneaking(false);
            this.setSprinting(true);
         } else {
            this.setSneaking(false);
            this.setSprinting(false);
         }
      } else {
         this.setSneaking(false);
         this.setSprinting(false);
      }

   }

   public boolean canDespawn() {
      return !this.isTamed() && this.ticksExisted > 2400;
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.3F);
   }

   public void fall(float p_180430_1_, float p_180430_2_) {
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setInteger("CatType", this.getTameSkin());
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      this.setTameSkin(p_70037_1_.getInteger("CatType"));
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (this.isTamed()) {
         if (this.isInLove()) {
            return SoundEvents.ENTITY_CAT_PURR;
         } else {
            return this.rand.nextInt(4) == 0 ? SoundEvents.ENTITY_CAT_PURREOW : SoundEvents.ENTITY_CAT_AMBIENT;
         }
      } else {
         return null;
      }
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_CAT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_CAT_DEATH;
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      return p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         if (this.aiSit != null) {
            this.aiSit.setSitting(false);
         }

         return super.attackEntityFrom(p_70097_1_, p_70097_2_);
      }
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_OCELOT;
   }

   public boolean processInteract(EntityPlayer p_184645_1_, EnumHand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getHeldItem(p_184645_2_);
      if (this.isTamed()) {
         if (this.isOwner(p_184645_1_) && !this.world.isRemote && !this.isBreedingItem(itemstack)) {
            this.aiSit.setSitting(!this.isSitting());
         }
      } else if ((this.aiTempt == null || this.aiTempt.isRunning()) && field_195402_bB.test(itemstack) && p_184645_1_.getDistanceSq(this) < 9.0D) {
         if (!p_184645_1_.capabilities.isCreativeMode) {
            itemstack.shrink(1);
         }

         if (!this.world.isRemote) {
            if (this.rand.nextInt(3) == 0) {
               this.setTamedBy(p_184645_1_);
               this.setTameSkin(1 + this.world.rand.nextInt(3));
               this.playTameEffect(true);
               this.aiSit.setSitting(true);
               this.world.setEntityState(this, (byte)7);
            } else {
               this.playTameEffect(false);
               this.world.setEntityState(this, (byte)6);
            }
         }

         return true;
      }

      return super.processInteract(p_184645_1_, p_184645_2_);
   }

   public EntityOcelot createChild(EntityAgeable p_90011_1_) {
      EntityOcelot entityocelot = new EntityOcelot(this.world);
      if (this.isTamed()) {
         entityocelot.setOwnerId(this.getOwnerId());
         entityocelot.setTamed(true);
         entityocelot.setTameSkin(this.getTameSkin());
      }

      return entityocelot;
   }

   public boolean isBreedingItem(ItemStack p_70877_1_) {
      return field_195402_bB.test(p_70877_1_);
   }

   public boolean canMateWith(EntityAnimal p_70878_1_) {
      if (p_70878_1_ == this) {
         return false;
      } else if (!this.isTamed()) {
         return false;
      } else if (!(p_70878_1_ instanceof EntityOcelot)) {
         return false;
      } else {
         EntityOcelot entityocelot = (EntityOcelot)p_70878_1_;
         if (!entityocelot.isTamed()) {
            return false;
         } else {
            return this.isInLove() && entityocelot.isInLove();
         }
      }
   }

   public int getTameSkin() {
      return this.dataManager.get(OCELOT_VARIANT);
   }

   public void setTameSkin(int p_70912_1_) {
      this.dataManager.set(OCELOT_VARIANT, p_70912_1_);
   }

   public boolean func_205020_a(IWorld p_205020_1_, boolean p_205020_2_) {
      return this.rand.nextInt(3) != 0;
   }

   public boolean isNotColliding(IWorldReaderBase p_205019_1_) {
      if (p_205019_1_.checkNoEntityCollision(this, this.getEntityBoundingBox()) && p_205019_1_.isCollisionBoxesEmpty(this, this.getEntityBoundingBox()) && !p_205019_1_.containsAnyLiquid(this.getEntityBoundingBox())) {
         BlockPos blockpos = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);
         if (blockpos.getY() < p_205019_1_.getSeaLevel()) {
            return false;
         }

         IBlockState iblockstate = p_205019_1_.getBlockState(blockpos.down());
         Block block = iblockstate.getBlock();
          return block == Blocks.GRASS_BLOCK || iblockstate.isIn(BlockTags.LEAVES);
      }

      return false;
   }

   public ITextComponent getName() {
      ITextComponent itextcomponent = this.getCustomName();
      if (itextcomponent != null) {
         return itextcomponent;
      } else {
         return this.isTamed() ? new TextComponentTranslation(Util.makeTranslationKey("entity", field_200608_bC)) : super.getName();
      }
   }

   protected void setupTamedAI() {
      if (this.avoidEntity == null) {
         this.avoidEntity = new EntityAIAvoidEntity<>(this, EntityPlayer.class, 16.0F, 0.8D, 1.33D);
      }

      this.tasks.removeTask(this.avoidEntity);
      if (!this.isTamed()) {
         this.tasks.addTask(4, this.avoidEntity);
      }

   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance p_204210_1_, @Nullable IEntityLivingData p_204210_2_, @Nullable NBTTagCompound p_204210_3_) {
      p_204210_2_ = super.onInitialSpawn(p_204210_1_, p_204210_2_, p_204210_3_);
      if (this.getTameSkin() == 0 && this.world.rand.nextInt(7) == 0) {
         for(int i = 0; i < 2; ++i) {
            EntityOcelot entityocelot = new EntityOcelot(this.world);
            entityocelot.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
            entityocelot.setGrowingAge(-24000);
            this.world.spawnEntity(entityocelot);
         }
      }

      return p_204210_2_;
   }
}
