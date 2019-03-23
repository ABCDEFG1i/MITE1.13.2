package net.minecraft.entity.passive;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityPufferFish extends AbstractFish {
   private static final DataParameter<Integer> PUFF_STATE = EntityDataManager.createKey(EntityPufferFish.class, DataSerializers.VARINT);
   private int puffTimer;
   private int deflateTimer;
   private static final Predicate<EntityLivingBase> ENEMY_MATCHER = (p_210139_0_) -> {
      if (p_210139_0_ == null) {
         return false;
      } else if (!(p_210139_0_ instanceof EntityPlayer) || !((EntityPlayer)p_210139_0_).isSpectator() && !((EntityPlayer)p_210139_0_).isCreative()) {
         return p_210139_0_.getCreatureAttribute() != CreatureAttribute.WATER;
      } else {
         return false;
      }
   };
   private float originalWidth = -1.0F;
   private float originalHeight;

   public EntityPufferFish(World p_i48853_1_) {
      super(EntityType.PUFFERFISH, p_i48853_1_);
      this.setSize(0.7F, 0.7F);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(PUFF_STATE, 0);
   }

   public int getPuffState() {
      return this.dataManager.get(PUFF_STATE);
   }

   public void setPuffState(int p_203714_1_) {
      this.dataManager.set(PUFF_STATE, p_203714_1_);
      this.onPuffStateChanged(p_203714_1_);
   }

   private void onPuffStateChanged(int p_205718_1_) {
      float f = 1.0F;
      if (p_205718_1_ == 1) {
         f = 0.7F;
      } else if (p_205718_1_ == 0) {
         f = 0.5F;
      }

      this.updateSize(f);
   }

   protected final void setSize(float p_70105_1_, float p_70105_2_) {
      boolean flag = this.originalWidth > 0.0F;
      this.originalWidth = p_70105_1_;
      this.originalHeight = p_70105_2_;
      if (!flag) {
         this.updateSize(1.0F);
      }

   }

   private void updateSize(float p_205717_1_) {
      super.setSize(this.originalWidth * p_205717_1_, this.originalHeight * p_205717_1_);
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      this.onPuffStateChanged(this.getPuffState());
      super.notifyDataManagerChange(p_184206_1_);
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setInteger("PuffState", this.getPuffState());
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      this.setPuffState(p_70037_1_.getInteger("PuffState"));
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_PUFFERFISH;
   }

   protected ItemStack getFishBucket() {
      return new ItemStack(Items.PUFFERFISH_BUCKET);
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(1, new EntityPufferFish.AIPuff(this));
   }

   public void tick() {
      if (this.isEntityAlive() && !this.world.isRemote) {
         if (this.puffTimer > 0) {
            if (this.getPuffState() == 0) {
               this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getSoundPitch());
               this.setPuffState(1);
            } else if (this.puffTimer > 40 && this.getPuffState() == 1) {
               this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getSoundPitch());
               this.setPuffState(2);
            }

            ++this.puffTimer;
         } else if (this.getPuffState() != 0) {
            if (this.deflateTimer > 60 && this.getPuffState() == 2) {
               this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getSoundPitch());
               this.setPuffState(1);
            } else if (this.deflateTimer > 100 && this.getPuffState() == 1) {
               this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getSoundPitch());
               this.setPuffState(0);
            }

            ++this.deflateTimer;
         }
      }

      super.tick();
   }

   public void livingTick() {
      super.livingTick();
      if (this.getPuffState() > 0) {
         for(EntityLiving entityliving : this.world.getEntitiesWithinAABB(EntityLiving.class, this.getEntityBoundingBox().grow(0.3D), ENEMY_MATCHER)) {
            if (entityliving.isEntityAlive()) {
               this.attack(entityliving);
            }
         }
      }

   }

   private void attack(EntityLiving p_205719_1_) {
      int i = this.getPuffState();
      if (p_205719_1_.attackEntityFrom(DamageSource.causeMobDamage(this), (float)(1 + i))) {
         p_205719_1_.addPotionEffect(new PotionEffect(MobEffects.POISON, 60 * i, 0));
         this.playSound(SoundEvents.ENTITY_PUFFER_FISH_STING, 1.0F, 1.0F);
      }

   }

   public void onCollideWithPlayer(EntityPlayer p_70100_1_) {
      int i = this.getPuffState();
      if (p_70100_1_ instanceof EntityPlayerMP && i > 0 && p_70100_1_.attackEntityFrom(DamageSource.causeMobDamage(this), (float)(1 + i))) {
         ((EntityPlayerMP)p_70100_1_).connection.sendPacket(new SPacketChangeGameState(9, 0.0F));
         p_70100_1_.addPotionEffect(new PotionEffect(MobEffects.POISON, 60 * i, 0));
      }

   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_PUFFER_FISH_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_PUFFER_FISH_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_PUFFER_FISH_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.ENTITY_PUFFER_FISH_FLOP;
   }

   static class AIPuff extends EntityAIBase {
      private final EntityPufferFish field_203789_a;

      public AIPuff(EntityPufferFish p_i48861_1_) {
         this.field_203789_a = p_i48861_1_;
      }

      public boolean shouldExecute() {
         List<EntityLivingBase> list = this.field_203789_a.world.getEntitiesWithinAABB(EntityLivingBase.class, this.field_203789_a.getEntityBoundingBox().grow(2.0D), EntityPufferFish.ENEMY_MATCHER);
         return !list.isEmpty();
      }

      public void startExecuting() {
         this.field_203789_a.puffTimer = 1;
         this.field_203789_a.deflateTimer = 0;
      }

      public void resetTask() {
         this.field_203789_a.puffTimer = 0;
      }

      public boolean shouldContinueExecuting() {
         List<EntityLivingBase> list = this.field_203789_a.world.getEntitiesWithinAABB(EntityLivingBase.class, this.field_203789_a.getEntityBoundingBox().grow(2.0D), EntityPufferFish.ENEMY_MATCHER);
         return !list.isEmpty();
      }
   }
}
