package net.minecraft.entity.passive;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIRunAroundLikeCrazy;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ContainerHorseChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractHorse extends EntityAnimal implements IInventoryChangedListener, IJumpingMount {
   private static final Predicate<Entity> IS_HORSE_BREEDING = (p_200613_0_) -> {
      return p_200613_0_ instanceof AbstractHorse && ((AbstractHorse)p_200613_0_).isBreeding();
   };
   protected static final IAttribute JUMP_STRENGTH = (new RangedAttribute((IAttribute)null, "horse.jumpStrength", 0.7D, 0.0D, 2.0D)).setDescription("Jump Strength").setShouldWatch(true);
   private static final DataParameter<Byte> STATUS = EntityDataManager.createKey(AbstractHorse.class, DataSerializers.BYTE);
   private static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.createKey(AbstractHorse.class, DataSerializers.OPTIONAL_UNIQUE_ID);
   private int eatingCounter;
   private int openMouthCounter;
   private int jumpRearingCounter;
   public int tailCounter;
   public int sprintCounter;
   protected boolean horseJumping;
   protected ContainerHorseChest horseChest;
   protected int temper;
   protected float jumpPower;
   private boolean allowStandSliding;
   private float headLean;
   private float prevHeadLean;
   private float rearingAmount;
   private float prevRearingAmount;
   private float mouthOpenness;
   private float prevMouthOpenness;
   protected boolean canGallop = true;
   protected int gallopTime;

   protected AbstractHorse(EntityType<?> p_i48563_1_, World p_i48563_2_) {
      super(p_i48563_1_, p_i48563_2_);
      this.setSize(1.3964844F, 1.6F);
      this.stepHeight = 1.0F;
      this.initHorseChest();
   }

   protected void initEntityAI() {
      this.tasks.addTask(1, new EntityAIPanic(this, 1.2D));
      this.tasks.addTask(1, new EntityAIRunAroundLikeCrazy(this, 1.2D));
      this.tasks.addTask(2, new EntityAIMate(this, 1.0D, AbstractHorse.class));
      this.tasks.addTask(4, new EntityAIFollowParent(this, 1.0D));
      this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 0.7D));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.func_205714_dM();
   }

   protected void func_205714_dM() {
      this.tasks.addTask(0, new EntityAISwimming(this));
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(STATUS, (byte)0);
      this.dataManager.register(OWNER_UNIQUE_ID, Optional.empty());
   }

   protected boolean getHorseWatchableBoolean(int p_110233_1_) {
      return (this.dataManager.get(STATUS) & p_110233_1_) != 0;
   }

   protected void setHorseWatchableBoolean(int p_110208_1_, boolean p_110208_2_) {
      byte b0 = this.dataManager.get(STATUS);
      if (p_110208_2_) {
         this.dataManager.set(STATUS, (byte)(b0 | p_110208_1_));
      } else {
         this.dataManager.set(STATUS, (byte)(b0 & ~p_110208_1_));
      }

   }

   public boolean isTame() {
      return this.getHorseWatchableBoolean(2);
   }

   @Nullable
   public UUID getOwnerUniqueId() {
      return this.dataManager.get(OWNER_UNIQUE_ID).orElse((UUID)null);
   }

   public void setOwnerUniqueId(@Nullable UUID p_184779_1_) {
      this.dataManager.set(OWNER_UNIQUE_ID, Optional.ofNullable(p_184779_1_));
   }

   public float getHorseSize() {
      return 0.5F;
   }

   public void setScaleForAge(boolean p_98054_1_) {
      this.setScale(p_98054_1_ ? this.getHorseSize() : 1.0F);
   }

   public boolean isHorseJumping() {
      return this.horseJumping;
   }

   public void setHorseTamed(boolean p_110234_1_) {
      this.setHorseWatchableBoolean(2, p_110234_1_);
   }

   public void setHorseJumping(boolean p_110255_1_) {
      this.horseJumping = p_110255_1_;
   }

   public boolean canBeLeashedTo(EntityPlayer p_184652_1_) {
      return super.canBeLeashedTo(p_184652_1_) && this.getCreatureAttribute() != CreatureAttribute.UNDEAD;
   }

   protected void onLeashDistance(float p_142017_1_) {
      if (p_142017_1_ > 6.0F && this.isEatingHaystack()) {
         this.setEatingHaystack(false);
      }

   }

   public boolean isEatingHaystack() {
      return this.getHorseWatchableBoolean(16);
   }

   public boolean isRearing() {
      return this.getHorseWatchableBoolean(32);
   }

   public boolean isBreeding() {
      return this.getHorseWatchableBoolean(8);
   }

   public void setBreeding(boolean p_110242_1_) {
      this.setHorseWatchableBoolean(8, p_110242_1_);
   }

   public void setHorseSaddled(boolean p_110251_1_) {
      this.setHorseWatchableBoolean(4, p_110251_1_);
   }

   public int getTemper() {
      return this.temper;
   }

   public void setTemper(int p_110238_1_) {
      this.temper = p_110238_1_;
   }

   public int increaseTemper(int p_110198_1_) {
      int i = MathHelper.clamp(this.getTemper() + p_110198_1_, 0, this.getMaxTemper());
      this.setTemper(i);
      return i;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      Entity entity = p_70097_1_.getTrueSource();
      return this.isBeingRidden() && entity != null && this.isRidingOrBeingRiddenBy(entity) ? false : super.attackEntityFrom(p_70097_1_, p_70097_2_);
   }

   public boolean canBePushed() {
      return !this.isBeingRidden();
   }

   private void eatingHorse() {
      this.openHorseMouth();
      if (!this.isSilent()) {
         this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_HORSE_EAT, this.getSoundCategory(), 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
      }

   }

   public void fall(float p_180430_1_, float p_180430_2_) {
      if (p_180430_1_ > 1.0F) {
         this.playSound(SoundEvents.ENTITY_HORSE_LAND, 0.4F, 1.0F);
      }

      int i = MathHelper.ceil((p_180430_1_ * 0.5F - 3.0F) * p_180430_2_);
      if (i > 0) {
         this.attackEntityFrom(DamageSource.FALL, (float)i);
         if (this.isBeingRidden()) {
            for(Entity entity : this.getRecursivePassengers()) {
               entity.attackEntityFrom(DamageSource.FALL, (float)i);
            }
         }

         IBlockState iblockstate = this.world.getBlockState(new BlockPos(this.posX, this.posY - 0.2D - (double)this.prevRotationYaw, this.posZ));
         Block block = iblockstate.getBlock();
         if (!iblockstate.isAir() && !this.isSilent()) {
            SoundType soundtype = block.getSoundType();
            this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, soundtype.getStepSound(), this.getSoundCategory(), soundtype.getVolume() * 0.5F, soundtype.getPitch() * 0.75F);
         }

      }
   }

   protected int getInventorySize() {
      return 2;
   }

   protected void initHorseChest() {
      ContainerHorseChest containerhorsechest = this.horseChest;
      this.horseChest = new ContainerHorseChest(this.getName(), this.getInventorySize());
      this.horseChest.setCustomName(this.getCustomName());
      if (containerhorsechest != null) {
         containerhorsechest.removeListener(this);
         int i = Math.min(containerhorsechest.getSizeInventory(), this.horseChest.getSizeInventory());

         for(int j = 0; j < i; ++j) {
            ItemStack itemstack = containerhorsechest.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
               this.horseChest.setInventorySlotContents(j, itemstack.copy());
            }
         }
      }

      this.horseChest.addListener(this);
      this.updateHorseSlots();
   }

   protected void updateHorseSlots() {
      if (!this.world.isRemote) {
         this.setHorseSaddled(!this.horseChest.getStackInSlot(0).isEmpty() && this.canBeSaddled());
      }
   }

   public void onInventoryChanged(IInventory p_76316_1_) {
      boolean flag = this.isHorseSaddled();
      this.updateHorseSlots();
      if (this.ticksExisted > 20 && !flag && this.isHorseSaddled()) {
         this.playSound(SoundEvents.ENTITY_HORSE_SADDLE, 0.5F, 1.0F);
      }

   }

   @Nullable
   protected AbstractHorse getClosestHorse(Entity p_110250_1_, double p_110250_2_) {
      double d0 = Double.MAX_VALUE;
      Entity entity = null;

      for(Entity entity1 : this.world.func_175674_a(p_110250_1_, p_110250_1_.getEntityBoundingBox().expand(p_110250_2_, p_110250_2_, p_110250_2_), IS_HORSE_BREEDING)) {
         double d1 = entity1.getDistanceSq(p_110250_1_.posX, p_110250_1_.posY, p_110250_1_.posZ);
         if (d1 < d0) {
            entity = entity1;
            d0 = d1;
         }
      }

      return (AbstractHorse)entity;
   }

   public double getHorseJumpStrength() {
      return this.getAttribute(JUMP_STRENGTH).getAttributeValue();
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return null;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      if (this.rand.nextInt(3) == 0) {
         this.makeHorseRear();
      }

      return null;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (this.rand.nextInt(10) == 0 && !this.isMovementBlocked()) {
         this.makeHorseRear();
      }

      return null;
   }

   public boolean canBeSaddled() {
      return true;
   }

   public boolean isHorseSaddled() {
      return this.getHorseWatchableBoolean(4);
   }

   @Nullable
   protected SoundEvent getAngrySound() {
      this.makeHorseRear();
      return null;
   }

   protected void playStepSound(BlockPos p_180429_1_, IBlockState p_180429_2_) {
      if (!p_180429_2_.getMaterial().isLiquid()) {
         SoundType soundtype = p_180429_2_.getBlock().getSoundType();
         if (this.world.getBlockState(p_180429_1_.up()).getBlock() == Blocks.SNOW) {
            soundtype = Blocks.SNOW.getSoundType();
         }

         if (this.isBeingRidden() && this.canGallop) {
            ++this.gallopTime;
            if (this.gallopTime > 5 && this.gallopTime % 3 == 0) {
               this.playGallopSound(soundtype);
            } else if (this.gallopTime <= 5) {
               this.playSound(SoundEvents.ENTITY_HORSE_STEP_WOOD, soundtype.getVolume() * 0.15F, soundtype.getPitch());
            }
         } else if (soundtype == SoundType.WOOD) {
            this.playSound(SoundEvents.ENTITY_HORSE_STEP_WOOD, soundtype.getVolume() * 0.15F, soundtype.getPitch());
         } else {
            this.playSound(SoundEvents.ENTITY_HORSE_STEP, soundtype.getVolume() * 0.15F, soundtype.getPitch());
         }

      }
   }

   protected void playGallopSound(SoundType p_190680_1_) {
      this.playSound(SoundEvents.ENTITY_HORSE_GALLOP, p_190680_1_.getVolume() * 0.15F, p_190680_1_.getPitch());
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttributeMap().registerAttribute(JUMP_STRENGTH);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(53.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.225F);
   }

   public int getMaxSpawnedInChunk() {
      return 6;
   }

   public int getMaxTemper() {
      return 100;
   }

   protected float getSoundVolume() {
      return 0.8F;
   }

   public int getTalkInterval() {
      return 400;
   }

   public void openGUI(EntityPlayer p_110199_1_) {
      if (!this.world.isRemote && (!this.isBeingRidden() || this.isPassenger(p_110199_1_)) && this.isTame()) {
         this.horseChest.setCustomName(this.getCustomName());
         p_110199_1_.openGuiHorseInventory(this, this.horseChest);
      }

   }

   protected boolean handleEating(EntityPlayer p_190678_1_, ItemStack p_190678_2_) {
      boolean flag = false;
      float f = 0.0F;
      int i = 0;
      int j = 0;
      Item item = p_190678_2_.getItem();
      if (item == Items.WHEAT) {
         f = 2.0F;
         i = 20;
         j = 3;
      } else if (item == Items.SUGAR) {
         f = 1.0F;
         i = 30;
         j = 3;
      } else if (item == Blocks.HAY_BLOCK.asItem()) {
         f = 20.0F;
         i = 180;
      } else if (item == Items.APPLE) {
         f = 3.0F;
         i = 60;
         j = 3;
      } else if (item == Items.GOLDEN_CARROT) {
         f = 4.0F;
         i = 60;
         j = 5;
         if (this.isTame() && this.getGrowingAge() == 0 && !this.isInLove()) {
            flag = true;
            this.setInLove(p_190678_1_);
         }
      } else if (item == Items.GOLDEN_APPLE || item == Items.ENCHANTED_GOLDEN_APPLE) {
         f = 10.0F;
         i = 240;
         j = 10;
         if (this.isTame() && this.getGrowingAge() == 0 && !this.isInLove()) {
            flag = true;
            this.setInLove(p_190678_1_);
         }
      }

      if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
         this.heal(f);
         flag = true;
      }

      if (this.isChild() && i > 0) {
         this.world.spawnParticle(Particles.HAPPY_VILLAGER, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, 0.0D, 0.0D, 0.0D);
         if (!this.world.isRemote) {
            this.addGrowth(i);
         }

         flag = true;
      }

      if (j > 0 && (flag || !this.isTame()) && this.getTemper() < this.getMaxTemper()) {
         flag = true;
         if (!this.world.isRemote) {
            this.increaseTemper(j);
         }
      }

      if (flag) {
         this.eatingHorse();
      }

      return flag;
   }

   protected void mountTo(EntityPlayer p_110237_1_) {
      this.setEatingHaystack(false);
      this.setRearing(false);
      if (!this.world.isRemote) {
         p_110237_1_.rotationYaw = this.rotationYaw;
         p_110237_1_.rotationPitch = this.rotationPitch;
         p_110237_1_.startRiding(this);
      }

   }

   protected boolean isMovementBlocked() {
      return super.isMovementBlocked() && this.isBeingRidden() && this.isHorseSaddled() || this.isEatingHaystack() || this.isRearing();
   }

   public boolean isBreedingItem(ItemStack p_70877_1_) {
      return false;
   }

   private void moveTail() {
      this.tailCounter = 1;
   }

   public void onDeath(DamageSource p_70645_1_) {
      super.onDeath(p_70645_1_);
      if (!this.world.isRemote && this.horseChest != null) {
         for(int i = 0; i < this.horseChest.getSizeInventory(); ++i) {
            ItemStack itemstack = this.horseChest.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
               this.entityDropItem(itemstack);
            }
         }

      }
   }

   public void livingTick() {
      if (this.rand.nextInt(200) == 0) {
         this.moveTail();
      }

      super.livingTick();
      if (!this.world.isRemote) {
         if (this.rand.nextInt(900) == 0 && this.deathTime == 0) {
            this.heal(1.0F);
         }

         if (this.canEatGrass()) {
            if (!this.isEatingHaystack() && !this.isBeingRidden() && this.rand.nextInt(300) == 0 && this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.posY) - 1, MathHelper.floor(this.posZ))).getBlock() == Blocks.GRASS_BLOCK) {
               this.setEatingHaystack(true);
            }

            if (this.isEatingHaystack() && ++this.eatingCounter > 50) {
               this.eatingCounter = 0;
               this.setEatingHaystack(false);
            }
         }

         this.followMother();
      }
   }

   protected void followMother() {
      if (this.isBreeding() && this.isChild() && !this.isEatingHaystack()) {
         AbstractHorse abstracthorse = this.getClosestHorse(this, 16.0D);
         if (abstracthorse != null && this.getDistanceSq(abstracthorse) > 4.0D) {
            this.navigator.getPathToEntityLiving(abstracthorse);
         }
      }

   }

   public boolean canEatGrass() {
      return true;
   }

   public void tick() {
      super.tick();
      if (this.openMouthCounter > 0 && ++this.openMouthCounter > 30) {
         this.openMouthCounter = 0;
         this.setHorseWatchableBoolean(64, false);
      }

      if ((this.canPassengerSteer() || this.isServerWorld()) && this.jumpRearingCounter > 0 && ++this.jumpRearingCounter > 20) {
         this.jumpRearingCounter = 0;
         this.setRearing(false);
      }

      if (this.tailCounter > 0 && ++this.tailCounter > 8) {
         this.tailCounter = 0;
      }

      if (this.sprintCounter > 0) {
         ++this.sprintCounter;
         if (this.sprintCounter > 300) {
            this.sprintCounter = 0;
         }
      }

      this.prevHeadLean = this.headLean;
      if (this.isEatingHaystack()) {
         this.headLean += (1.0F - this.headLean) * 0.4F + 0.05F;
         if (this.headLean > 1.0F) {
            this.headLean = 1.0F;
         }
      } else {
         this.headLean += (0.0F - this.headLean) * 0.4F - 0.05F;
         if (this.headLean < 0.0F) {
            this.headLean = 0.0F;
         }
      }

      this.prevRearingAmount = this.rearingAmount;
      if (this.isRearing()) {
         this.headLean = 0.0F;
         this.prevHeadLean = this.headLean;
         this.rearingAmount += (1.0F - this.rearingAmount) * 0.4F + 0.05F;
         if (this.rearingAmount > 1.0F) {
            this.rearingAmount = 1.0F;
         }
      } else {
         this.allowStandSliding = false;
         this.rearingAmount += (0.8F * this.rearingAmount * this.rearingAmount * this.rearingAmount - this.rearingAmount) * 0.6F - 0.05F;
         if (this.rearingAmount < 0.0F) {
            this.rearingAmount = 0.0F;
         }
      }

      this.prevMouthOpenness = this.mouthOpenness;
      if (this.getHorseWatchableBoolean(64)) {
         this.mouthOpenness += (1.0F - this.mouthOpenness) * 0.7F + 0.05F;
         if (this.mouthOpenness > 1.0F) {
            this.mouthOpenness = 1.0F;
         }
      } else {
         this.mouthOpenness += (0.0F - this.mouthOpenness) * 0.7F - 0.05F;
         if (this.mouthOpenness < 0.0F) {
            this.mouthOpenness = 0.0F;
         }
      }

   }

   private void openHorseMouth() {
      if (!this.world.isRemote) {
         this.openMouthCounter = 1;
         this.setHorseWatchableBoolean(64, true);
      }

   }

   public void setEatingHaystack(boolean p_110227_1_) {
      this.setHorseWatchableBoolean(16, p_110227_1_);
   }

   public void setRearing(boolean p_110219_1_) {
      if (p_110219_1_) {
         this.setEatingHaystack(false);
      }

      this.setHorseWatchableBoolean(32, p_110219_1_);
   }

   private void makeHorseRear() {
      if (this.canPassengerSteer() || this.isServerWorld()) {
         this.jumpRearingCounter = 1;
         this.setRearing(true);
      }

   }

   public void makeMad() {
      this.makeHorseRear();
      SoundEvent soundevent = this.getAngrySound();
      if (soundevent != null) {
         this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
      }

   }

   public boolean setTamedBy(EntityPlayer p_110263_1_) {
      this.setOwnerUniqueId(p_110263_1_.getUniqueID());
      this.setHorseTamed(true);
      if (p_110263_1_ instanceof EntityPlayerMP) {
         CriteriaTriggers.TAME_ANIMAL.trigger((EntityPlayerMP)p_110263_1_, this);
      }

      this.world.setEntityState(this, (byte)7);
      return true;
   }

   public void travel(float p_191986_1_, float p_191986_2_, float p_191986_3_) {
      if (this.isBeingRidden() && this.canBeSteered() && this.isHorseSaddled()) {
         EntityLivingBase entitylivingbase = (EntityLivingBase)this.getControllingPassenger();
         this.rotationYaw = entitylivingbase.rotationYaw;
         this.prevRotationYaw = this.rotationYaw;
         this.rotationPitch = entitylivingbase.rotationPitch * 0.5F;
         this.setRotation(this.rotationYaw, this.rotationPitch);
         this.renderYawOffset = this.rotationYaw;
         this.rotationYawHead = this.renderYawOffset;
         p_191986_1_ = entitylivingbase.moveStrafing * 0.5F;
         p_191986_3_ = entitylivingbase.moveForward;
         if (p_191986_3_ <= 0.0F) {
            p_191986_3_ *= 0.25F;
            this.gallopTime = 0;
         }

         if (this.onGround && this.jumpPower == 0.0F && this.isRearing() && !this.allowStandSliding) {
            p_191986_1_ = 0.0F;
            p_191986_3_ = 0.0F;
         }

         if (this.jumpPower > 0.0F && !this.isHorseJumping() && this.onGround) {
            this.motionY = this.getHorseJumpStrength() * (double)this.jumpPower;
            if (this.isPotionActive(MobEffects.JUMP_BOOST)) {
               this.motionY += (double)((float)(this.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
            }

            this.setHorseJumping(true);
            this.isAirBorne = true;
            if (p_191986_3_ > 0.0F) {
               float f = MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F));
               float f1 = MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F));
               this.motionX += (double)(-0.4F * f * this.jumpPower);
               this.motionZ += (double)(0.4F * f1 * this.jumpPower);
               this.func_205715_ee();
            }

            this.jumpPower = 0.0F;
         }

         this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;
         if (this.canPassengerSteer()) {
            this.setAIMoveSpeed((float)this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
            super.travel(p_191986_1_, p_191986_2_, p_191986_3_);
         } else if (entitylivingbase instanceof EntityPlayer) {
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
         }

         if (this.onGround) {
            this.jumpPower = 0.0F;
            this.setHorseJumping(false);
         }

         this.prevLimbSwingAmount = this.limbSwingAmount;
         double d1 = this.posX - this.prevPosX;
         double d0 = this.posZ - this.prevPosZ;
         float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;
         if (f2 > 1.0F) {
            f2 = 1.0F;
         }

         this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4F;
         this.limbSwing += this.limbSwingAmount;
      } else {
         this.jumpMovementFactor = 0.02F;
         super.travel(p_191986_1_, p_191986_2_, p_191986_3_);
      }
   }

   protected void func_205715_ee() {
      this.playSound(SoundEvents.ENTITY_HORSE_JUMP, 0.4F, 1.0F);
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setBoolean("EatingHaystack", this.isEatingHaystack());
      p_70014_1_.setBoolean("Bred", this.isBreeding());
      p_70014_1_.setInteger("Temper", this.getTemper());
      p_70014_1_.setBoolean("Tame", this.isTame());
      if (this.getOwnerUniqueId() != null) {
         p_70014_1_.setString("OwnerUUID", this.getOwnerUniqueId().toString());
      }

      if (!this.horseChest.getStackInSlot(0).isEmpty()) {
         p_70014_1_.setTag("SaddleItem", this.horseChest.getStackInSlot(0).write(new NBTTagCompound()));
      }

   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      this.setEatingHaystack(p_70037_1_.getBoolean("EatingHaystack"));
      this.setBreeding(p_70037_1_.getBoolean("Bred"));
      this.setTemper(p_70037_1_.getInteger("Temper"));
      this.setHorseTamed(p_70037_1_.getBoolean("Tame"));
      String s;
      if (p_70037_1_.hasKey("OwnerUUID", 8)) {
         s = p_70037_1_.getString("OwnerUUID");
      } else {
         String s1 = p_70037_1_.getString("Owner");
         s = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), s1);
      }

      if (!s.isEmpty()) {
         this.setOwnerUniqueId(UUID.fromString(s));
      }

      IAttributeInstance iattributeinstance = this.getAttributeMap().getAttributeInstanceByName("Speed");
      if (iattributeinstance != null) {
         this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(iattributeinstance.getBaseValue() * 0.25D);
      }

      if (p_70037_1_.hasKey("SaddleItem", 10)) {
         ItemStack itemstack = ItemStack.loadFromNBT(p_70037_1_.getCompoundTag("SaddleItem"));
         if (itemstack.getItem() == Items.SADDLE) {
            this.horseChest.setInventorySlotContents(0, itemstack);
         }
      }

      this.updateHorseSlots();
   }

   public boolean canMateWith(EntityAnimal p_70878_1_) {
      return false;
   }

   protected boolean canMate() {
      return !this.isBeingRidden() && !this.isRiding() && this.isTame() && !this.isChild() && this.getHealth() >= this.getMaxHealth() && this.isInLove();
   }

   @Nullable
   public EntityAgeable createChild(EntityAgeable p_90011_1_) {
      return null;
   }

   protected void setOffspringAttributes(EntityAgeable p_190681_1_, AbstractHorse p_190681_2_) {
      double d0 = this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() + p_190681_1_.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() + (double)this.getModifiedMaxHealth();
      p_190681_2_.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(d0 / 3.0D);
      double d1 = this.getAttribute(JUMP_STRENGTH).getBaseValue() + p_190681_1_.getAttribute(JUMP_STRENGTH).getBaseValue() + this.getModifiedJumpStrength();
      p_190681_2_.getAttribute(JUMP_STRENGTH).setBaseValue(d1 / 3.0D);
      double d2 = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() + p_190681_1_.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() + this.getModifiedMovementSpeed();
      p_190681_2_.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(d2 / 3.0D);
   }

   public boolean canBeSteered() {
      return this.getControllingPassenger() instanceof EntityLivingBase;
   }

   @OnlyIn(Dist.CLIENT)
   public float getGrassEatingAmount(float p_110258_1_) {
      return this.prevHeadLean + (this.headLean - this.prevHeadLean) * p_110258_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public float getRearingAmount(float p_110223_1_) {
      return this.prevRearingAmount + (this.rearingAmount - this.prevRearingAmount) * p_110223_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public float getMouthOpennessAngle(float p_110201_1_) {
      return this.prevMouthOpenness + (this.mouthOpenness - this.prevMouthOpenness) * p_110201_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public void setJumpPower(int p_110206_1_) {
      if (this.isHorseSaddled()) {
         if (p_110206_1_ < 0) {
            p_110206_1_ = 0;
         } else {
            this.allowStandSliding = true;
            this.makeHorseRear();
         }

         if (p_110206_1_ >= 90) {
            this.jumpPower = 1.0F;
         } else {
            this.jumpPower = 0.4F + 0.4F * (float)p_110206_1_ / 90.0F;
         }

      }
   }

   public boolean canJump() {
      return this.isHorseSaddled();
   }

   public void handleStartJump(int p_184775_1_) {
      this.allowStandSliding = true;
      this.makeHorseRear();
   }

   public void handleStopJump() {
   }

   @OnlyIn(Dist.CLIENT)
   protected void spawnHorseParticles(boolean p_110216_1_) {
      IParticleData iparticledata = p_110216_1_ ? Particles.HEART : Particles.SMOKE;

      for(int i = 0; i < 7; ++i) {
         double d0 = this.rand.nextGaussian() * 0.02D;
         double d1 = this.rand.nextGaussian() * 0.02D;
         double d2 = this.rand.nextGaussian() * 0.02D;
         this.world.spawnParticle(iparticledata, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 7) {
         this.spawnHorseParticles(true);
      } else if (p_70103_1_ == 6) {
         this.spawnHorseParticles(false);
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   public void updatePassenger(Entity p_184232_1_) {
      super.updatePassenger(p_184232_1_);
      if (p_184232_1_ instanceof EntityLiving) {
         EntityLiving entityliving = (EntityLiving)p_184232_1_;
         this.renderYawOffset = entityliving.renderYawOffset;
      }

      if (this.prevRearingAmount > 0.0F) {
         float f3 = MathHelper.sin(this.renderYawOffset * ((float)Math.PI / 180F));
         float f = MathHelper.cos(this.renderYawOffset * ((float)Math.PI / 180F));
         float f1 = 0.7F * this.prevRearingAmount;
         float f2 = 0.15F * this.prevRearingAmount;
         p_184232_1_.setPosition(this.posX + (double)(f1 * f3), this.posY + this.getMountedYOffset() + p_184232_1_.getYOffset() + (double)f2, this.posZ - (double)(f1 * f));
         if (p_184232_1_ instanceof EntityLivingBase) {
            ((EntityLivingBase)p_184232_1_).renderYawOffset = this.renderYawOffset;
         }
      }

   }

   protected float getModifiedMaxHealth() {
      return 15.0F + (float)this.rand.nextInt(8) + (float)this.rand.nextInt(9);
   }

   protected double getModifiedJumpStrength() {
      return (double)0.4F + this.rand.nextDouble() * 0.2D + this.rand.nextDouble() * 0.2D + this.rand.nextDouble() * 0.2D;
   }

   protected double getModifiedMovementSpeed() {
      return ((double)0.45F + this.rand.nextDouble() * 0.3D + this.rand.nextDouble() * 0.3D + this.rand.nextDouble() * 0.3D) * 0.25D;
   }

   public boolean isOnLadder() {
      return false;
   }

   public float getEyeHeight() {
      return this.height;
   }

   public boolean wearsArmor() {
      return false;
   }

   public boolean isArmor(ItemStack p_190682_1_) {
      return false;
   }

   public boolean replaceItemInInventory(int p_174820_1_, ItemStack p_174820_2_) {
      int i = p_174820_1_ - 400;
      if (i >= 0 && i < 2 && i < this.horseChest.getSizeInventory()) {
         if (i == 0 && p_174820_2_.getItem() != Items.SADDLE) {
            return false;
         } else if (i != 1 || this.wearsArmor() && this.isArmor(p_174820_2_)) {
            this.horseChest.setInventorySlotContents(i, p_174820_2_);
            this.updateHorseSlots();
            return true;
         } else {
            return false;
         }
      } else {
         int j = p_174820_1_ - 500 + 2;
         if (j >= 2 && j < this.horseChest.getSizeInventory()) {
            this.horseChest.setInventorySlotContents(j, p_174820_2_);
            return true;
         } else {
            return false;
         }
      }
   }

   @Nullable
   public Entity getControllingPassenger() {
      return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance p_204210_1_, @Nullable IEntityLivingData p_204210_2_, @Nullable NBTTagCompound p_204210_3_) {
      p_204210_2_ = super.onInitialSpawn(p_204210_1_, p_204210_2_, p_204210_3_);
      if (this.rand.nextInt(5) == 0) {
         this.setGrowingAge(-24000);
      }

      return p_204210_2_;
   }
}
