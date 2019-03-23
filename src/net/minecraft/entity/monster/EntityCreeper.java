package net.minecraft.entity.monster;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityCreeper extends EntityMob {
   private static final DataParameter<Integer> STATE = EntityDataManager.createKey(EntityCreeper.class, DataSerializers.VARINT);
   private static final DataParameter<Boolean> POWERED = EntityDataManager.createKey(EntityCreeper.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> IGNITED = EntityDataManager.createKey(EntityCreeper.class, DataSerializers.BOOLEAN);
   private int lastActiveTime;
   private int timeSinceIgnited;
   private int fuseTime = 30;
   private int explosionRadius = 3;
   private int droppedSkulls;

   public EntityCreeper(World p_i1733_1_) {
      super(EntityType.CREEPER, p_i1733_1_);
      this.setSize(0.6F, 1.7F);
   }

   protected void initEntityAI() {
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAICreeperSwell(this));
      this.tasks.addTask(3, new EntityAIAvoidEntity<>(this, EntityOcelot.class, 6.0F, 1.0D, 1.2D));
      this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.0D, false));
      this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.8D));
      this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(6, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
      this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
   }

   public int getMaxFallHeight() {
      return this.getAttackTarget() == null ? 3 : 3 + (int)(this.getHealth() - 1.0F);
   }

   public void fall(float p_180430_1_, float p_180430_2_) {
      super.fall(p_180430_1_, p_180430_2_);
      this.timeSinceIgnited = (int)((float)this.timeSinceIgnited + p_180430_1_ * 1.5F);
      if (this.timeSinceIgnited > this.fuseTime - 5) {
         this.timeSinceIgnited = this.fuseTime - 5;
      }

   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(STATE, -1);
      this.dataManager.register(POWERED, false);
      this.dataManager.register(IGNITED, false);
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      if (this.dataManager.get(POWERED)) {
         p_70014_1_.setBoolean("powered", true);
      }

      p_70014_1_.setShort("Fuse", (short)this.fuseTime);
      p_70014_1_.setByte("ExplosionRadius", (byte)this.explosionRadius);
      p_70014_1_.setBoolean("ignited", this.hasIgnited());
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      this.dataManager.set(POWERED, p_70037_1_.getBoolean("powered"));
      if (p_70037_1_.hasKey("Fuse", 99)) {
         this.fuseTime = p_70037_1_.getShort("Fuse");
      }

      if (p_70037_1_.hasKey("ExplosionRadius", 99)) {
         this.explosionRadius = p_70037_1_.getByte("ExplosionRadius");
      }

      if (p_70037_1_.getBoolean("ignited")) {
         this.ignite();
      }

   }

   public void tick() {
      if (this.isEntityAlive()) {
         this.lastActiveTime = this.timeSinceIgnited;
         if (this.hasIgnited()) {
            this.setCreeperState(1);
         }

         int i = this.getCreeperState();
         if (i > 0 && this.timeSinceIgnited == 0) {
            this.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 1.0F, 0.5F);
         }

         this.timeSinceIgnited += i;
         if (this.timeSinceIgnited < 0) {
            this.timeSinceIgnited = 0;
         }

         if (this.timeSinceIgnited >= this.fuseTime) {
            this.timeSinceIgnited = this.fuseTime;
            this.explode();
         }
      }

      super.tick();
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_CREEPER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_CREEPER_DEATH;
   }

   public void onDeath(DamageSource p_70645_1_) {
      super.onDeath(p_70645_1_);
      if (this.world.getGameRules().getBoolean("doMobLoot")) {
         if (p_70645_1_.getTrueSource() instanceof EntitySkeleton) {
            this.entityDropItem(ItemRecord.getRandom(this.rand));
         } else if (p_70645_1_.getTrueSource() instanceof EntityCreeper && p_70645_1_.getTrueSource() != this && ((EntityCreeper)p_70645_1_.getTrueSource()).getPowered() && ((EntityCreeper)p_70645_1_.getTrueSource()).ableToCauseSkullDrop()) {
            ((EntityCreeper)p_70645_1_.getTrueSource()).incrementDroppedSkulls();
            this.entityDropItem(Items.CREEPER_HEAD);
         }
      }

   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      return true;
   }

   public boolean getPowered() {
      return this.dataManager.get(POWERED);
   }

   @OnlyIn(Dist.CLIENT)
   public float getCreeperFlashIntensity(float p_70831_1_) {
      return ((float)this.lastActiveTime + (float)(this.timeSinceIgnited - this.lastActiveTime) * p_70831_1_) / (float)(this.fuseTime - 2);
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_CREEPER;
   }

   public int getCreeperState() {
      return this.dataManager.get(STATE);
   }

   public void setCreeperState(int p_70829_1_) {
      this.dataManager.set(STATE, p_70829_1_);
   }

   public void onStruckByLightning(EntityLightningBolt p_70077_1_) {
      super.onStruckByLightning(p_70077_1_);
      this.dataManager.set(POWERED, true);
   }

   protected boolean processInteract(EntityPlayer p_184645_1_, EnumHand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getHeldItem(p_184645_2_);
      if (itemstack.getItem() == Items.FLINT_AND_STEEL) {
         this.world.playSound(p_184645_1_, this.posX, this.posY, this.posZ, SoundEvents.ITEM_FLINTANDSTEEL_USE, this.getSoundCategory(), 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
         p_184645_1_.swingArm(p_184645_2_);
         if (!this.world.isRemote) {
            this.ignite();
            itemstack.damageItem(1, p_184645_1_);
            return true;
         }
      }

      return super.processInteract(p_184645_1_, p_184645_2_);
   }

   private void explode() {
      if (!this.world.isRemote) {
         boolean flag = this.world.getGameRules().getBoolean("mobGriefing");
         float f = this.getPowered() ? 2.0F : 1.0F;
         this.dead = true;
         this.world.createExplosion(this, this.posX, this.posY, this.posZ, (float)this.explosionRadius * f, flag);
         this.setDead();
         this.spawnLingeringCloud();
      }

   }

   private void spawnLingeringCloud() {
      Collection<PotionEffect> collection = this.getActivePotionEffects();
      if (!collection.isEmpty()) {
         EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(this.world, this.posX, this.posY, this.posZ);
         entityareaeffectcloud.setRadius(2.5F);
         entityareaeffectcloud.setRadiusOnUse(-0.5F);
         entityareaeffectcloud.setWaitTime(10);
         entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() / 2);
         entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());

         for(PotionEffect potioneffect : collection) {
            entityareaeffectcloud.addEffect(new PotionEffect(potioneffect));
         }

         this.world.spawnEntity(entityareaeffectcloud);
      }

   }

   public boolean hasIgnited() {
      return this.dataManager.get(IGNITED);
   }

   public void ignite() {
      this.dataManager.set(IGNITED, true);
   }

   public boolean ableToCauseSkullDrop() {
      return this.droppedSkulls < 1 && this.world.getGameRules().getBoolean("doMobLoot");
   }

   public void incrementDroppedSkulls() {
      ++this.droppedSkulls;
   }
}
