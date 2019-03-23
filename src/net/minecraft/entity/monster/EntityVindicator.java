package net.minecraft.entity.monster;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityVindicator extends AbstractIllager {
   private boolean johnny;
   private static final Predicate<Entity> JOHNNY_SELECTOR = (p_210136_0_) -> {
      return p_210136_0_ instanceof EntityLivingBase && ((EntityLivingBase)p_210136_0_).attackable();
   };

   public EntityVindicator(World p_i47279_1_) {
      super(EntityType.VINDICATOR, p_i47279_1_);
      this.setSize(0.6F, 1.95F);
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.0D, false));
      this.tasks.addTask(8, new EntityAIWander(this, 0.6D));
      this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
      this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, EntityVindicator.class));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityVillager.class, true));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityIronGolem.class, true));
      this.targetTasks.addTask(4, new EntityVindicator.AIJohnnyAttack(this));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.35F);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(12.0D);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(24.0D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
   }

   protected void registerData() {
      super.registerData();
   }

   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_VINDICATION_ILLAGER;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isAggressive() {
      return this.isAggressive(1);
   }

   public void setAggressive(boolean p_190636_1_) {
      this.setAggressive(1, p_190636_1_);
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      if (this.johnny) {
         p_70014_1_.setBoolean("Johnny", true);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public AbstractIllager.IllagerArmPose getArmPose() {
      return this.isAggressive() ? AbstractIllager.IllagerArmPose.ATTACKING : AbstractIllager.IllagerArmPose.CROSSED;
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      if (p_70037_1_.hasKey("Johnny", 99)) {
         this.johnny = p_70037_1_.getBoolean("Johnny");
      }

   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance p_204210_1_, @Nullable IEntityLivingData p_204210_2_, @Nullable NBTTagCompound p_204210_3_) {
      IEntityLivingData ientitylivingdata = super.onInitialSpawn(p_204210_1_, p_204210_2_, p_204210_3_);
      this.setEquipmentBasedOnDifficulty(p_204210_1_);
      this.setEnchantmentBasedOnDifficulty(p_204210_1_);
      return ientitylivingdata;
   }

   protected void setEquipmentBasedOnDifficulty(DifficultyInstance p_180481_1_) {
      this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
   }

   protected void updateAITasks() {
      super.updateAITasks();
      this.setAggressive(this.getAttackTarget() != null);
   }

   public boolean isOnSameTeam(Entity p_184191_1_) {
      if (super.isOnSameTeam(p_184191_1_)) {
         return true;
      } else if (p_184191_1_ instanceof EntityLivingBase && ((EntityLivingBase)p_184191_1_).getCreatureAttribute() == CreatureAttribute.ILLAGER) {
         return this.getTeam() == null && p_184191_1_.getTeam() == null;
      } else {
         return false;
      }
   }

   public void setCustomName(@Nullable ITextComponent p_200203_1_) {
      super.setCustomName(p_200203_1_);
      if (!this.johnny && p_200203_1_ != null && p_200203_1_.getString().equals("Johnny")) {
         this.johnny = true;
      }

   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_VINDICATOR_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_VINDICATOR_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_VINDICATOR_HURT;
   }

   static class AIJohnnyAttack extends EntityAINearestAttackableTarget<EntityLivingBase> {
      public AIJohnnyAttack(EntityVindicator p_i47345_1_) {
         super(p_i47345_1_, EntityLivingBase.class, 0, true, true, EntityVindicator.JOHNNY_SELECTOR);
      }

      public boolean shouldExecute() {
         return ((EntityVindicator)this.taskOwner).johnny && super.shouldExecute();
      }
   }
}
