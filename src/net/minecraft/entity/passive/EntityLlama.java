package net.minecraft.entity.passive;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILlamaFollowCaravan;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIRunAroundLikeCrazy;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLlamaSpit;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityLlama extends AbstractChestHorse implements IRangedAttackMob {
   private static final DataParameter<Integer> DATA_STRENGTH_ID = EntityDataManager.createKey(EntityLlama.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> DATA_COLOR_ID = EntityDataManager.createKey(EntityLlama.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> DATA_VARIANT_ID = EntityDataManager.createKey(EntityLlama.class, DataSerializers.VARINT);
   private boolean didSpit;
   @Nullable
   private EntityLlama caravanHead;
   @Nullable
   private EntityLlama caravanTail;

   public EntityLlama(World p_i47297_1_) {
      super(EntityType.LLAMA, p_i47297_1_);
      this.setSize(0.9F, 1.87F);
   }

   private void setStrength(int p_190706_1_) {
      this.dataManager.set(DATA_STRENGTH_ID, Math.max(1, Math.min(5, p_190706_1_)));
   }

   private void setRandomStrength() {
      int i = this.rand.nextFloat() < 0.04F ? 5 : 3;
      this.setStrength(1 + this.rand.nextInt(i));
   }

   public int getStrength() {
      return this.dataManager.get(DATA_STRENGTH_ID);
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setInteger("Variant", this.getVariant());
      p_70014_1_.setInteger("Strength", this.getStrength());
      if (!this.horseChest.getStackInSlot(1).isEmpty()) {
         p_70014_1_.setTag("DecorItem", this.horseChest.getStackInSlot(1).write(new NBTTagCompound()));
      }

   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      this.setStrength(p_70037_1_.getInteger("Strength"));
      super.readEntityFromNBT(p_70037_1_);
      this.setVariant(p_70037_1_.getInteger("Variant"));
      if (p_70037_1_.hasKey("DecorItem", 10)) {
         this.horseChest.setInventorySlotContents(1, ItemStack.loadFromNBT(p_70037_1_.getCompoundTag("DecorItem")));
      }

      this.updateHorseSlots();
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityAIRunAroundLikeCrazy(this, 1.2D));
      this.tasks.addTask(2, new EntityAILlamaFollowCaravan(this, (double)2.1F));
      this.tasks.addTask(3, new EntityAIAttackRanged(this, 1.25D, 40, 20.0F));
      this.tasks.addTask(3, new EntityAIPanic(this, 1.2D));
      this.tasks.addTask(4, new EntityAIMate(this, 1.0D));
      this.tasks.addTask(5, new EntityAIFollowParent(this, 1.0D));
      this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 0.7D));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityLlama.AIHurtByTarget(this));
      this.targetTasks.addTask(2, new EntityLlama.AIDefendTarget(this));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(DATA_STRENGTH_ID, 0);
      this.dataManager.register(DATA_COLOR_ID, -1);
      this.dataManager.register(DATA_VARIANT_ID, 0);
   }

   public int getVariant() {
      return MathHelper.clamp(this.dataManager.get(DATA_VARIANT_ID), 0, 3);
   }

   public void setVariant(int p_190710_1_) {
      this.dataManager.set(DATA_VARIANT_ID, p_190710_1_);
   }

   protected int getInventorySize() {
      return this.hasChest() ? 2 + 3 * this.getInventoryColumns() : super.getInventorySize();
   }

   public void updatePassenger(Entity p_184232_1_) {
      if (this.isPassenger(p_184232_1_)) {
         float f = MathHelper.cos(this.renderYawOffset * ((float)Math.PI / 180F));
         float f1 = MathHelper.sin(this.renderYawOffset * ((float)Math.PI / 180F));
         float f2 = 0.3F;
         p_184232_1_.setPosition(this.posX + (double)(0.3F * f1), this.posY + this.getMountedYOffset() + p_184232_1_.getYOffset(), this.posZ - (double)(0.3F * f));
      }
   }

   public double getMountedYOffset() {
      return (double)this.height * 0.67D;
   }

   public boolean canBeSteered() {
      return false;
   }

   protected boolean handleEating(EntityPlayer p_190678_1_, ItemStack p_190678_2_) {
      int i = 0;
      int j = 0;
      float f = 0.0F;
      boolean flag = false;
      Item item = p_190678_2_.getItem();
      if (item == Items.WHEAT) {
         i = 10;
         j = 3;
         f = 2.0F;
      } else if (item == Blocks.HAY_BLOCK.asItem()) {
         i = 90;
         j = 6;
         f = 10.0F;
         if (this.isTame() && this.getGrowingAge() == 0 && this.canBreed()) {
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

      if (flag && !this.isSilent()) {
         this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_LLAMA_EAT, this.getSoundCategory(), 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
      }

      return flag;
   }

   protected boolean isMovementBlocked() {
      return this.getHealth() <= 0.0F || this.isEatingHaystack();
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance p_204210_1_, @Nullable IEntityLivingData p_204210_2_, @Nullable NBTTagCompound p_204210_3_) {
      p_204210_2_ = super.onInitialSpawn(p_204210_1_, p_204210_2_, p_204210_3_);
      this.setRandomStrength();
      int i;
      if (p_204210_2_ instanceof EntityLlama.GroupData) {
         i = ((EntityLlama.GroupData)p_204210_2_).variant;
      } else {
         i = this.rand.nextInt(4);
         p_204210_2_ = new EntityLlama.GroupData(i);
      }

      this.setVariant(i);
      return p_204210_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasColor() {
      return this.getColor() != null;
   }

   protected SoundEvent getAngrySound() {
      return SoundEvents.ENTITY_LLAMA_ANGRY;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_LLAMA_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_LLAMA_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_LLAMA_DEATH;
   }

   protected void playStepSound(BlockPos p_180429_1_, IBlockState p_180429_2_) {
      this.playSound(SoundEvents.ENTITY_LLAMA_STEP, 0.15F, 1.0F);
   }

   protected void playChestEquipSound() {
      this.playSound(SoundEvents.ENTITY_LLAMA_CHEST, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
   }

   public void makeMad() {
      SoundEvent soundevent = this.getAngrySound();
      if (soundevent != null) {
         this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
      }

   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_LLAMA;
   }

   public int getInventoryColumns() {
      return this.getStrength();
   }

   public boolean wearsArmor() {
      return true;
   }

   public boolean isArmor(ItemStack p_190682_1_) {
      Item item = p_190682_1_.getItem();
      return ItemTags.CARPETS.contains(item);
   }

   public boolean canBeSaddled() {
      return false;
   }

   public void onInventoryChanged(IInventory p_76316_1_) {
      EnumDyeColor enumdyecolor = this.getColor();
      super.onInventoryChanged(p_76316_1_);
      EnumDyeColor enumdyecolor1 = this.getColor();
      if (this.ticksExisted > 20 && enumdyecolor1 != null && enumdyecolor1 != enumdyecolor) {
         this.playSound(SoundEvents.ENTITY_LLAMA_SWAG, 0.5F, 1.0F);
      }

   }

   protected void updateHorseSlots() {
      if (!this.world.isRemote) {
         super.updateHorseSlots();
         this.setColor(func_195403_g(this.horseChest.getStackInSlot(1)));
      }
   }

   private void setColor(@Nullable EnumDyeColor p_190711_1_) {
      this.dataManager.set(DATA_COLOR_ID, p_190711_1_ == null ? -1 : p_190711_1_.getId());
   }

   @Nullable
   private static EnumDyeColor func_195403_g(ItemStack p_195403_0_) {
      Block block = Block.getBlockFromItem(p_195403_0_.getItem());
      return block instanceof BlockCarpet ? ((BlockCarpet)block).getColor() : null;
   }

   @Nullable
   public EnumDyeColor getColor() {
      int i = this.dataManager.get(DATA_COLOR_ID);
      return i == -1 ? null : EnumDyeColor.byId(i);
   }

   public int getMaxTemper() {
      return 30;
   }

   public boolean canMateWith(EntityAnimal p_70878_1_) {
      return p_70878_1_ != this && p_70878_1_ instanceof EntityLlama && this.canMate() && ((EntityLlama)p_70878_1_).canMate();
   }

   public EntityLlama createChild(EntityAgeable p_90011_1_) {
      EntityLlama entityllama = new EntityLlama(this.world);
      this.setOffspringAttributes(p_90011_1_, entityllama);
      EntityLlama entityllama1 = (EntityLlama)p_90011_1_;
      int i = this.rand.nextInt(Math.max(this.getStrength(), entityllama1.getStrength())) + 1;
      if (this.rand.nextFloat() < 0.03F) {
         ++i;
      }

      entityllama.setStrength(i);
      entityllama.setVariant(this.rand.nextBoolean() ? this.getVariant() : entityllama1.getVariant());
      return entityllama;
   }

   private void spit(EntityLivingBase p_190713_1_) {
      EntityLlamaSpit entityllamaspit = new EntityLlamaSpit(this.world, this);
      double d0 = p_190713_1_.posX - this.posX;
      double d1 = p_190713_1_.getEntityBoundingBox().minY + (double)(p_190713_1_.height / 3.0F) - entityllamaspit.posY;
      double d2 = p_190713_1_.posZ - this.posZ;
      float f = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.2F;
      entityllamaspit.shoot(d0, d1 + (double)f, d2, 1.5F, 10.0F);
      this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_LLAMA_SPIT, this.getSoundCategory(), 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
      this.world.spawnEntity(entityllamaspit);
      this.didSpit = true;
   }

   private void setDidSpit(boolean p_190714_1_) {
      this.didSpit = p_190714_1_;
   }

   public void fall(float p_180430_1_, float p_180430_2_) {
      int i = MathHelper.ceil((p_180430_1_ * 0.5F - 3.0F) * p_180430_2_);
      if (i > 0) {
         if (p_180430_1_ >= 6.0F) {
            this.attackEntityFrom(DamageSource.FALL, (float)i);
            if (this.isBeingRidden()) {
               for(Entity entity : this.getRecursivePassengers()) {
                  entity.attackEntityFrom(DamageSource.FALL, (float)i);
               }
            }
         }

         IBlockState iblockstate = this.world.getBlockState(new BlockPos(this.posX, this.posY - 0.2D - (double)this.prevRotationYaw, this.posZ));
         Block block = iblockstate.getBlock();
         if (!iblockstate.isAir() && !this.isSilent()) {
            SoundType soundtype = block.getSoundType();
            this.world.playSound(null, this.posX, this.posY, this.posZ, soundtype.getStepSound(), this.getSoundCategory(), soundtype.getVolume() * 0.5F, soundtype.getPitch() * 0.75F);
         }

      }
   }

   public void leaveCaravan() {
      if (this.caravanHead != null) {
         this.caravanHead.caravanTail = null;
      }

      this.caravanHead = null;
   }

   public void joinCaravan(EntityLlama p_190715_1_) {
      this.caravanHead = p_190715_1_;
      this.caravanHead.caravanTail = this;
   }

   public boolean hasCaravanTrail() {
      return this.caravanTail != null;
   }

   public boolean inCaravan() {
      return this.caravanHead != null;
   }

   @Nullable
   public EntityLlama getCaravanHead() {
      return this.caravanHead;
   }

   protected double followLeashSpeed() {
      return 2.0D;
   }

   protected void followMother() {
      if (!this.inCaravan() && this.isChild()) {
         super.followMother();
      }

   }

   public boolean canEatGrass() {
      return false;
   }

   public void attackEntityWithRangedAttack(EntityLivingBase p_82196_1_, float p_82196_2_) {
      this.spit(p_82196_1_);
   }

   public void setSwingingArms(boolean p_184724_1_) {
   }

   static class AIDefendTarget extends EntityAINearestAttackableTarget<EntityWolf> {
      public AIDefendTarget(EntityLlama p_i47285_1_) {
         super(p_i47285_1_, EntityWolf.class, 16, false, true, null);
      }

      public boolean shouldExecute() {
         if (super.shouldExecute() && this.targetEntity != null && !this.targetEntity.isTamed()) {
            return true;
         } else {
            this.taskOwner.setAttackTarget(null);
            return false;
         }
      }

      protected double getTargetDistance() {
         return super.getTargetDistance() * 0.25D;
      }
   }

   static class AIHurtByTarget extends EntityAIHurtByTarget {
      public AIHurtByTarget(EntityLlama p_i47282_1_) {
         super(p_i47282_1_, false);
      }

      public boolean shouldContinueExecuting() {
         if (this.taskOwner instanceof EntityLlama) {
            EntityLlama entityllama = (EntityLlama)this.taskOwner;
            if (entityllama.didSpit) {
               entityllama.setDidSpit(false);
               return false;
            }
         }

         return super.shouldContinueExecuting();
      }
   }

   static class GroupData implements IEntityLivingData {
      public int variant;

      private GroupData(int p_i47283_1_) {
         this.variant = p_i47283_1_;
      }
   }
}
