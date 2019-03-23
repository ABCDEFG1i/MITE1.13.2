package net.minecraft.entity.monster;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIZombieAttack;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityPigZombie extends EntityZombie {
   private static final UUID ATTACK_SPEED_BOOST_MODIFIER_UUID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
   private static final AttributeModifier ATTACK_SPEED_BOOST_MODIFIER = (new AttributeModifier(ATTACK_SPEED_BOOST_MODIFIER_UUID, "Attacking speed boost", 0.05D, 0)).setSaved(false);
   private int angerLevel;
   private int randomSoundDelay;
   private UUID angerTargetUUID;

   public EntityPigZombie(World p_i1739_1_) {
      super(EntityType.ZOMBIE_PIGMAN, p_i1739_1_);
      this.isImmuneToFire = true;
   }

   public void setRevengeTarget(@Nullable EntityLivingBase p_70604_1_) {
      super.setRevengeTarget(p_70604_1_);
      if (p_70604_1_ != null) {
         this.angerTargetUUID = p_70604_1_.getUniqueID();
      }

   }

   protected void applyEntityAI() {
      this.tasks.addTask(2, new EntityAIZombieAttack(this, 1.0D, false));
      this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
      this.targetTasks.addTask(1, new EntityPigZombie.AIHurtByAggressor(this));
      this.targetTasks.addTask(2, new EntityPigZombie.AITargetAggressor(this));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.23F);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
   }

   protected boolean shouldDrown() {
      return false;
   }

   protected void updateAITasks() {
      IAttributeInstance iattributeinstance = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
      if (this.isAngry()) {
         if (!this.isChild() && !iattributeinstance.hasModifier(ATTACK_SPEED_BOOST_MODIFIER)) {
            iattributeinstance.applyModifier(ATTACK_SPEED_BOOST_MODIFIER);
         }

         --this.angerLevel;
      } else if (iattributeinstance.hasModifier(ATTACK_SPEED_BOOST_MODIFIER)) {
         iattributeinstance.removeModifier(ATTACK_SPEED_BOOST_MODIFIER);
      }

      if (this.randomSoundDelay > 0 && --this.randomSoundDelay == 0) {
         this.playSound(SoundEvents.ENTITY_ZOMBIE_PIGMAN_ANGRY, this.getSoundVolume() * 2.0F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 1.8F);
      }

      if (this.angerLevel > 0 && this.angerTargetUUID != null && this.getRevengeTarget() == null) {
         EntityPlayer entityplayer = this.world.getPlayerEntityByUUID(this.angerTargetUUID);
         this.setRevengeTarget(entityplayer);
         this.attackingPlayer = entityplayer;
         this.recentlyHit = this.getRevengeTimer();
      }

      super.updateAITasks();
   }

   public boolean func_205020_a(IWorld p_205020_1_, boolean p_205020_2_) {
      return p_205020_1_.getDifficulty() != EnumDifficulty.PEACEFUL;
   }

   public boolean isNotColliding(IWorldReaderBase p_205019_1_) {
      return p_205019_1_.checkNoEntityCollision(this, this.getEntityBoundingBox()) && p_205019_1_.isCollisionBoxesEmpty(this, this.getEntityBoundingBox()) && !p_205019_1_.containsAnyLiquid(this.getEntityBoundingBox());
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setShort("Anger", (short)this.angerLevel);
      if (this.angerTargetUUID != null) {
         p_70014_1_.setString("HurtBy", this.angerTargetUUID.toString());
      } else {
         p_70014_1_.setString("HurtBy", "");
      }

   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      this.angerLevel = p_70037_1_.getShort("Anger");
      String s = p_70037_1_.getString("HurtBy");
      if (!s.isEmpty()) {
         this.angerTargetUUID = UUID.fromString(s);
         EntityPlayer entityplayer = this.world.getPlayerEntityByUUID(this.angerTargetUUID);
         this.setRevengeTarget(entityplayer);
         if (entityplayer != null) {
            this.attackingPlayer = entityplayer;
            this.recentlyHit = this.getRevengeTimer();
         }
      }

   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         Entity entity = p_70097_1_.getTrueSource();
         if (entity instanceof EntityPlayer && !((EntityPlayer)entity).isCreative()) {
            this.becomeAngryAt(entity);
         }

         return super.attackEntityFrom(p_70097_1_, p_70097_2_);
      }
   }

   private void becomeAngryAt(Entity p_70835_1_) {
      this.angerLevel = 400 + this.rand.nextInt(400);
      this.randomSoundDelay = this.rand.nextInt(40);
      if (p_70835_1_ instanceof EntityLivingBase) {
         this.setRevengeTarget((EntityLivingBase)p_70835_1_);
      }

   }

   public boolean isAngry() {
      return this.angerLevel > 0;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_ZOMBIE_PIGMAN_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_ZOMBIE_PIGMAN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_ZOMBIE_PIGMAN_DEATH;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_ZOMBIE_PIGMAN;
   }

   public boolean processInteract(EntityPlayer p_184645_1_, EnumHand p_184645_2_) {
      return false;
   }

   protected void setEquipmentBasedOnDifficulty(DifficultyInstance p_180481_1_) {
      this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
   }

   protected ItemStack getSkullDrop() {
      return ItemStack.EMPTY;
   }

   public boolean isPreventingPlayerRest(EntityPlayer p_191990_1_) {
      return this.isAngry();
   }

   static class AIHurtByAggressor extends EntityAIHurtByTarget {
      public AIHurtByAggressor(EntityPigZombie p_i45828_1_) {
         super(p_i45828_1_, true);
      }

      protected void setEntityAttackTarget(EntityCreature p_179446_1_, EntityLivingBase p_179446_2_) {
         super.setEntityAttackTarget(p_179446_1_, p_179446_2_);
         if (p_179446_1_ instanceof EntityPigZombie) {
            ((EntityPigZombie)p_179446_1_).becomeAngryAt(p_179446_2_);
         }

      }
   }

   static class AITargetAggressor extends EntityAINearestAttackableTarget<EntityPlayer> {
      public AITargetAggressor(EntityPigZombie p_i45829_1_) {
         super(p_i45829_1_, EntityPlayer.class, true);
      }

      public boolean shouldExecute() {
         return ((EntityPigZombie)this.taskOwner).isAngry() && super.shouldExecute();
      }
   }
}
