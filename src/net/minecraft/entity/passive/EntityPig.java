package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityPig extends EntityAnimal {
   private static final DataParameter<Boolean> SADDLED = EntityDataManager.createKey(EntityPig.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> BOOST_TIME = EntityDataManager.createKey(EntityPig.class, DataSerializers.VARINT);
   private static final Ingredient TEMPTATION_ITEMS = Ingredient.fromItems(Items.CARROT, Items.POTATO, Items.BEETROOT);
   private boolean boosting;
   private int boostTime;
   private int totalBoostTime;

   public EntityPig(World p_i1689_1_) {
      super(EntityType.PIG, p_i1689_1_);
      this.setSize(0.9F, 0.9F);
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityAIPanic(this, 1.25D));
      this.tasks.addTask(3, new EntityAIMate(this, 1.0D));
      this.tasks.addTask(4, new EntityAITempt(this, 1.2D, Ingredient.fromItems(Items.CARROT_ON_A_STICK), false));
      this.tasks.addTask(4, new EntityAITempt(this, 1.2D, false, TEMPTATION_ITEMS));
      this.tasks.addTask(5, new EntityAIFollowParent(this, 1.1D));
      this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 1.0D));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
   }

   @Nullable
   public Entity getControllingPassenger() {
      return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
   }

   public boolean canBeSteered() {
      Entity entity = this.getControllingPassenger();
      if (!(entity instanceof EntityPlayer)) {
         return false;
      } else {
         EntityPlayer entityplayer = (EntityPlayer)entity;
         return entityplayer.getHeldItemMainhand().getItem() == Items.CARROT_ON_A_STICK || entityplayer.getHeldItemOffhand().getItem() == Items.CARROT_ON_A_STICK;
      }
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      if (BOOST_TIME.equals(p_184206_1_) && this.world.isRemote) {
         this.boosting = true;
         this.boostTime = 0;
         this.totalBoostTime = this.dataManager.get(BOOST_TIME);
      }

      super.notifyDataManagerChange(p_184206_1_);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(SADDLED, false);
      this.dataManager.register(BOOST_TIME, 0);
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setBoolean("Saddle", this.getSaddled());
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      this.setSaddled(p_70037_1_.getBoolean("Saddle"));
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_PIG_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_PIG_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_PIG_DEATH;
   }

   protected void playStepSound(BlockPos p_180429_1_, IBlockState p_180429_2_) {
      this.playSound(SoundEvents.ENTITY_PIG_STEP, 0.15F, 1.0F);
   }

   public boolean processInteract(EntityPlayer p_184645_1_, EnumHand p_184645_2_) {
      if (!super.processInteract(p_184645_1_, p_184645_2_)) {
         ItemStack itemstack = p_184645_1_.getHeldItem(p_184645_2_);
         if (itemstack.getItem() == Items.NAME_TAG) {
            itemstack.interactWithEntity(p_184645_1_, this, p_184645_2_);
            return true;
         } else if (this.getSaddled() && !this.isBeingRidden()) {
            if (!this.world.isRemote) {
               p_184645_1_.startRiding(this);
            }

            return true;
         } else if (itemstack.getItem() == Items.SADDLE) {
            itemstack.interactWithEntity(p_184645_1_, this, p_184645_2_);
            return true;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public void onDeath(DamageSource p_70645_1_) {
      super.onDeath(p_70645_1_);
      if (!this.world.isRemote) {
         if (this.getSaddled()) {
            this.entityDropItem(Items.SADDLE);
         }

      }
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_PIG;
   }

   public boolean getSaddled() {
      return this.dataManager.get(SADDLED);
   }

   public void setSaddled(boolean p_70900_1_) {
      if (p_70900_1_) {
         this.dataManager.set(SADDLED, true);
      } else {
         this.dataManager.set(SADDLED, false);
      }

   }

   public void onStruckByLightning(EntityLightningBolt p_70077_1_) {
      if (!this.world.isRemote && !this.isDead) {
         EntityPigZombie entitypigzombie = new EntityPigZombie(this.world);
         entitypigzombie.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
         entitypigzombie.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
         entitypigzombie.setNoAI(this.isAIDisabled());
         if (this.hasCustomName()) {
            entitypigzombie.setCustomName(this.getCustomName());
            entitypigzombie.setCustomNameVisible(this.isCustomNameVisible());
         }

         this.world.spawnEntity(entitypigzombie);
         this.setDead();
      }
   }

   public void travel(float p_191986_1_, float p_191986_2_, float p_191986_3_) {
      Entity entity = this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
      if (this.isBeingRidden() && this.canBeSteered()) {
         this.rotationYaw = entity.rotationYaw;
         this.prevRotationYaw = this.rotationYaw;
         this.rotationPitch = entity.rotationPitch * 0.5F;
         this.setRotation(this.rotationYaw, this.rotationPitch);
         this.renderYawOffset = this.rotationYaw;
         this.rotationYawHead = this.rotationYaw;
         this.stepHeight = 1.0F;
         this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;
         if (this.boosting && this.boostTime++ > this.totalBoostTime) {
            this.boosting = false;
         }

         if (this.canPassengerSteer()) {
            float f = (float)this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() * 0.225F;
            if (this.boosting) {
               f += f * 1.15F * MathHelper.sin((float)this.boostTime / (float)this.totalBoostTime * (float)Math.PI);
            }

            this.setAIMoveSpeed(f);
            super.travel(0.0F, 0.0F, 1.0F);
         } else {
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
         }

         this.prevLimbSwingAmount = this.limbSwingAmount;
         double d1 = this.posX - this.prevPosX;
         double d0 = this.posZ - this.prevPosZ;
         float f1 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;
         if (f1 > 1.0F) {
            f1 = 1.0F;
         }

         this.limbSwingAmount += (f1 - this.limbSwingAmount) * 0.4F;
         this.limbSwing += this.limbSwingAmount;
      } else {
         this.stepHeight = 0.5F;
         this.jumpMovementFactor = 0.02F;
         super.travel(p_191986_1_, p_191986_2_, p_191986_3_);
      }
   }

   public boolean boost() {
      if (this.boosting) {
         return false;
      } else {
         this.boosting = true;
         this.boostTime = 0;
         this.totalBoostTime = this.getRNG().nextInt(841) + 140;
         this.getDataManager().set(BOOST_TIME, this.totalBoostTime);
         return true;
      }
   }

   public EntityPig createChild(EntityAgeable p_90011_1_) {
      return new EntityPig(this.world);
   }

   public boolean isBreedingItem(ItemStack p_70877_1_) {
      return TEMPTATION_ITEMS.test(p_70877_1_);
   }
}
