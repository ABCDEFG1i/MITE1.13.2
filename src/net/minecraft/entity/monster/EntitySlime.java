package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearest;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;

public class EntitySlime extends EntityLiving implements IMob {
   private static final DataParameter<Integer> SLIME_SIZE = EntityDataManager.createKey(EntitySlime.class, DataSerializers.VARINT);
   public float squishAmount;
   public float squishFactor;
   public float prevSquishFactor;
   private boolean wasOnGround;

   protected EntitySlime(EntityType<?> p_i48552_1_, World p_i48552_2_) {
      super(p_i48552_1_, p_i48552_2_);
      this.moveHelper = new EntitySlime.SlimeMoveHelper(this);
   }

   public EntitySlime(World p_i1742_1_) {
      this(EntityType.SLIME, p_i1742_1_);
   }

   protected void initEntityAI() {
      this.tasks.addTask(1, new EntitySlime.AISlimeFloat(this));
      this.tasks.addTask(2, new EntitySlime.AISlimeAttack(this));
      this.tasks.addTask(3, new EntitySlime.AISlimeFaceRandom(this));
      this.tasks.addTask(5, new EntitySlime.AISlimeHop(this));
      this.targetTasks.addTask(1, new EntityAIFindEntityNearestPlayer(this));
      this.targetTasks.addTask(3, new EntityAIFindEntityNearest(this, EntityIronGolem.class));
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(SLIME_SIZE, 1);
   }

   protected void setSlimeSize(int p_70799_1_, boolean p_70799_2_) {
      this.dataManager.set(SLIME_SIZE, p_70799_1_);
      this.setSize(0.51000005F * (float)p_70799_1_, 0.51000005F * (float)p_70799_1_);
      this.setPosition(this.posX, this.posY, this.posZ);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)(p_70799_1_ * p_70799_1_));
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)(0.2F + 0.1F * (float)p_70799_1_));
      if (p_70799_2_) {
         this.setHealth(this.getMaxHealth());
      }

      this.experienceValue = p_70799_1_;
   }

   public int getSlimeSize() {
      return this.dataManager.get(SLIME_SIZE);
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setInteger("Size", this.getSlimeSize() - 1);
      p_70014_1_.setBoolean("wasOnGround", this.wasOnGround);
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      int i = p_70037_1_.getInteger("Size");
      if (i < 0) {
         i = 0;
      }

      this.setSlimeSize(i + 1, false);
      this.wasOnGround = p_70037_1_.getBoolean("wasOnGround");
   }

   public boolean isSmallSlime() {
      return this.getSlimeSize() <= 1;
   }

   protected IParticleData func_195404_m() {
      return Particles.ITEM_SLIME;
   }

   public void tick() {
      if (!this.world.isRemote && this.world.getDifficulty() == EnumDifficulty.PEACEFUL && this.getSlimeSize() > 0) {
         this.isDead = true;
      }

      this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;
      this.prevSquishFactor = this.squishFactor;
      super.tick();
      if (this.onGround && !this.wasOnGround) {
         int i = this.getSlimeSize();

         for(int j = 0; j < i * 8; ++j) {
            float f = this.rand.nextFloat() * ((float)Math.PI * 2F);
            float f1 = this.rand.nextFloat() * 0.5F + 0.5F;
            float f2 = MathHelper.sin(f) * (float)i * 0.5F * f1;
            float f3 = MathHelper.cos(f) * (float)i * 0.5F * f1;
            World world = this.world;
            IParticleData iparticledata = this.func_195404_m();
            double d0 = this.posX + (double)f2;
            double d1 = this.posZ + (double)f3;
            world.spawnParticle(iparticledata, d0, this.getEntityBoundingBox().minY, d1, 0.0D, 0.0D, 0.0D);
         }

         this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
         this.squishAmount = -0.5F;
      } else if (!this.onGround && this.wasOnGround) {
         this.squishAmount = 1.0F;
      }

      this.wasOnGround = this.onGround;
      this.alterSquishAmount();
   }

   protected void alterSquishAmount() {
      this.squishAmount *= 0.6F;
   }

   protected int getJumpDelay() {
      return this.rand.nextInt(20) + 10;
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      if (SLIME_SIZE.equals(p_184206_1_)) {
         int i = this.getSlimeSize();
         this.setSize(0.51000005F * (float)i, 0.51000005F * (float)i);
         this.rotationYaw = this.rotationYawHead;
         this.renderYawOffset = this.rotationYawHead;
         if (this.isInWater() && this.rand.nextInt(20) == 0) {
            this.doWaterSplashEffect();
         }
      }

      super.notifyDataManagerChange(p_184206_1_);
   }

   public EntityType<? extends EntitySlime> getType() {
      return (EntityType<? extends EntitySlime>) super.getType();
   }

   public void setDead() {
      int i = this.getSlimeSize();
      if (!this.world.isRemote && i > 1 && this.getHealth() <= 0.0F) {
         int j = 2 + this.rand.nextInt(3);

         for(int k = 0; k < j; ++k) {
            float f = ((float)(k % 2) - 0.5F) * (float)i / 4.0F;
            float f1 = ((float)(k / 2) - 0.5F) * (float)i / 4.0F;
            EntitySlime entityslime = this.getType().create(this.world);
            if (this.hasCustomName()) {
               entityslime.setCustomName(this.getCustomName());
            }

            if (this.isNoDespawnRequired()) {
               entityslime.enablePersistence();
            }

            entityslime.setSlimeSize(i / 2, true);
            entityslime.setLocationAndAngles(this.posX + (double)f, this.posY + 0.5D, this.posZ + (double)f1, this.rand.nextFloat() * 360.0F, 0.0F);
            this.world.spawnEntity(entityslime);
         }
      }

      super.setDead();
   }

   public void applyEntityCollision(Entity p_70108_1_) {
      super.applyEntityCollision(p_70108_1_);
      if (p_70108_1_ instanceof EntityIronGolem && this.canDamagePlayer()) {
         this.dealDamage((EntityLivingBase)p_70108_1_);
      }

   }

   public void onCollideWithPlayer(EntityPlayer p_70100_1_) {
      if (this.canDamagePlayer()) {
         this.dealDamage(p_70100_1_);
      }

   }

   protected void dealDamage(EntityLivingBase p_175451_1_) {
      int i = this.getSlimeSize();
      if (this.canEntityBeSeen(p_175451_1_) && this.getDistanceSq(p_175451_1_) < 0.6D * (double)i * 0.6D * (double)i && p_175451_1_.attackEntityFrom(DamageSource.causeMobDamage(this), (float)this.getAttackStrength())) {
         this.playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         this.applyEnchantments(this, p_175451_1_);
      }

   }

   public float getEyeHeight() {
      return 0.625F * this.height;
   }

   protected boolean canDamagePlayer() {
      return !this.isSmallSlime() && this.isServerWorld();
   }

   protected int getAttackStrength() {
      return this.getSlimeSize();
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return this.isSmallSlime() ? SoundEvents.ENTITY_SLIME_HURT_SMALL : SoundEvents.ENTITY_SLIME_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isSmallSlime() ? SoundEvents.ENTITY_SLIME_DEATH_SMALL : SoundEvents.ENTITY_SLIME_DEATH;
   }

   protected SoundEvent getSquishSound() {
      return this.isSmallSlime() ? SoundEvents.ENTITY_SLIME_SQUISH_SMALL : SoundEvents.ENTITY_SLIME_SQUISH;
   }

   protected Item getDropItem() {
      return this.getSlimeSize() == 1 ? Items.SLIME_BALL : null;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return this.getSlimeSize() == 1 ? LootTableList.ENTITIES_SLIME : LootTableList.EMPTY;
   }

   public boolean func_205020_a(IWorld p_205020_1_, boolean p_205020_2_) {
      BlockPos blockpos = new BlockPos(MathHelper.floor(this.posX), 0, MathHelper.floor(this.posZ));
      if (p_205020_1_.getWorldInfo().getTerrainType() == WorldType.FLAT && this.rand.nextInt(4) != 1) {
         return false;
      } else {
         if (p_205020_1_.getDifficulty() != EnumDifficulty.PEACEFUL) {
            Biome biome = p_205020_1_.getBiome(blockpos);
            if (biome == Biomes.SWAMP && this.posY > 50.0D && this.posY < 70.0D && this.rand.nextFloat() < 0.5F && this.rand.nextFloat() < p_205020_1_.getCurrentMoonPhaseFactor() && p_205020_1_.getLight(new BlockPos(this)) <= this.rand.nextInt(8)) {
               return super.func_205020_a(p_205020_1_, p_205020_2_);
            }

            ChunkPos chunkpos = new ChunkPos(blockpos);
            boolean flag = SharedSeedRandom.func_205190_a(chunkpos.x, chunkpos.z, p_205020_1_.getSeed(), 987234911L).nextInt(10) == 0;
            if (this.rand.nextInt(10) == 0 && flag && this.posY < 40.0D) {
               return super.func_205020_a(p_205020_1_, p_205020_2_);
            }
         }

         return false;
      }
   }

   protected float getSoundVolume() {
      return 0.4F * (float)this.getSlimeSize();
   }

   public int getVerticalFaceSpeed() {
      return 0;
   }

   protected boolean makesSoundOnJump() {
      return this.getSlimeSize() > 0;
   }

   protected void jump() {
      this.motionY = (double)0.42F;
      this.isAirBorne = true;
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance p_204210_1_, @Nullable IEntityLivingData p_204210_2_, @Nullable NBTTagCompound p_204210_3_) {
      int i = this.rand.nextInt(3);
      if (i < 2 && this.rand.nextFloat() < 0.5F * p_204210_1_.getClampedAdditionalDifficulty()) {
         ++i;
      }

      int j = 1 << i;
      this.setSlimeSize(j, true);
      return super.onInitialSpawn(p_204210_1_, p_204210_2_, p_204210_3_);
   }

   protected SoundEvent getJumpSound() {
      return this.isSmallSlime() ? SoundEvents.ENTITY_SLIME_JUMP_SMALL : SoundEvents.ENTITY_SLIME_JUMP;
   }

   static class AISlimeAttack extends EntityAIBase {
      private final EntitySlime slime;
      private int growTieredTimer;

      public AISlimeAttack(EntitySlime p_i45824_1_) {
         this.slime = p_i45824_1_;
         this.setMutexBits(2);
      }

      public boolean shouldExecute() {
         EntityLivingBase entitylivingbase = this.slime.getAttackTarget();
         if (entitylivingbase == null) {
            return false;
         } else if (!entitylivingbase.isEntityAlive()) {
            return false;
         } else {
            return !(entitylivingbase instanceof EntityPlayer) || !((EntityPlayer)entitylivingbase).capabilities.disableDamage;
         }
      }

      public void startExecuting() {
         this.growTieredTimer = 300;
         super.startExecuting();
      }

      public boolean shouldContinueExecuting() {
         EntityLivingBase entitylivingbase = this.slime.getAttackTarget();
         if (entitylivingbase == null) {
            return false;
         } else if (!entitylivingbase.isEntityAlive()) {
            return false;
         } else if (entitylivingbase instanceof EntityPlayer && ((EntityPlayer)entitylivingbase).capabilities.disableDamage) {
            return false;
         } else {
            return --this.growTieredTimer > 0;
         }
      }

      public void updateTask() {
         this.slime.faceEntity(this.slime.getAttackTarget(), 10.0F, 10.0F);
         ((EntitySlime.SlimeMoveHelper)this.slime.getMoveHelper()).setDirection(this.slime.rotationYaw, this.slime.canDamagePlayer());
      }
   }

   static class AISlimeFaceRandom extends EntityAIBase {
      private final EntitySlime slime;
      private float chosenDegrees;
      private int nextRandomizeTime;

      public AISlimeFaceRandom(EntitySlime p_i45820_1_) {
         this.slime = p_i45820_1_;
         this.setMutexBits(2);
      }

      public boolean shouldExecute() {
         return this.slime.getAttackTarget() == null && (this.slime.onGround || this.slime.isInWater() || this.slime.isInLava() || this.slime.isPotionActive(MobEffects.LEVITATION));
      }

      public void updateTask() {
         if (--this.nextRandomizeTime <= 0) {
            this.nextRandomizeTime = 40 + this.slime.getRNG().nextInt(60);
            this.chosenDegrees = (float)this.slime.getRNG().nextInt(360);
         }

         ((EntitySlime.SlimeMoveHelper)this.slime.getMoveHelper()).setDirection(this.chosenDegrees, false);
      }
   }

   static class AISlimeFloat extends EntityAIBase {
      private final EntitySlime slime;

      public AISlimeFloat(EntitySlime p_i45823_1_) {
         this.slime = p_i45823_1_;
         this.setMutexBits(5);
         p_i45823_1_.getNavigator().setCanSwim(true);
      }

      public boolean shouldExecute() {
         return this.slime.isInWater() || this.slime.isInLava();
      }

      public void updateTask() {
         if (this.slime.getRNG().nextFloat() < 0.8F) {
            this.slime.getJumpHelper().setJumping();
         }

         ((EntitySlime.SlimeMoveHelper)this.slime.getMoveHelper()).setSpeed(1.2D);
      }
   }

   static class AISlimeHop extends EntityAIBase {
      private final EntitySlime slime;

      public AISlimeHop(EntitySlime p_i45822_1_) {
         this.slime = p_i45822_1_;
         this.setMutexBits(5);
      }

      public boolean shouldExecute() {
         return true;
      }

      public void updateTask() {
         ((EntitySlime.SlimeMoveHelper)this.slime.getMoveHelper()).setSpeed(1.0D);
      }
   }

   static class SlimeMoveHelper extends EntityMoveHelper {
      private float yRot;
      private int jumpDelay;
      private final EntitySlime slime;
      private boolean isAggressive;

      public SlimeMoveHelper(EntitySlime p_i45821_1_) {
         super(p_i45821_1_);
         this.slime = p_i45821_1_;
         this.yRot = 180.0F * p_i45821_1_.rotationYaw / (float)Math.PI;
      }

      public void setDirection(float p_179920_1_, boolean p_179920_2_) {
         this.yRot = p_179920_1_;
         this.isAggressive = p_179920_2_;
      }

      public void setSpeed(double p_179921_1_) {
         this.speed = p_179921_1_;
         this.action = EntityMoveHelper.Action.MOVE_TO;
      }

      public void tick() {
         this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, this.yRot, 90.0F);
         this.entity.rotationYawHead = this.entity.rotationYaw;
         this.entity.renderYawOffset = this.entity.rotationYaw;
         if (this.action != EntityMoveHelper.Action.MOVE_TO) {
            this.entity.setMoveForward(0.0F);
         } else {
            this.action = EntityMoveHelper.Action.WAIT;
            if (this.entity.onGround) {
               this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()));
               if (this.jumpDelay-- <= 0) {
                  this.jumpDelay = this.slime.getJumpDelay();
                  if (this.isAggressive) {
                     this.jumpDelay /= 3;
                  }

                  this.slime.getJumpHelper().setJumping();
                  if (this.slime.makesSoundOnJump()) {
                     this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), ((this.slime.getRNG().nextFloat() - this.slime.getRNG().nextFloat()) * 0.2F + 1.0F) * 0.8F);
                  }
               } else {
                  this.slime.moveStrafing = 0.0F;
                  this.slime.moveForward = 0.0F;
                  this.entity.setAIMoveSpeed(0.0F);
               }
            } else {
               this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()));
            }

         }
      }
   }
}
