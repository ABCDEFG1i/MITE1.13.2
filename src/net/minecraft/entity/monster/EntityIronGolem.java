package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIDefendVillage;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookAtVillager;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Fluids;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.Village;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityIronGolem extends EntityGolem {
   protected static final DataParameter<Byte> PLAYER_CREATED = EntityDataManager.createKey(EntityIronGolem.class, DataSerializers.BYTE);
   private int homeCheckTimer;
   @Nullable
   private Village village;
   private int attackTimer;
   private int holdRoseTick;

   public EntityIronGolem(World p_i1694_1_) {
      super(EntityType.IRON_GOLEM, p_i1694_1_);
      this.setSize(1.4F, 2.7F);
   }

   protected void initEntityAI() {
      this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, true));
      this.tasks.addTask(2, new EntityAIMoveTowardsTarget(this, 0.9D, 32.0F));
      this.tasks.addTask(3, new EntityAIMoveThroughVillage(this, 0.6D, true));
      this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0D));
      this.tasks.addTask(5, new EntityAILookAtVillager(this));
      this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 0.6D));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIDefendVillage(this));
      this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityLiving.class, 10, false, true, (p_210132_0_) -> {
         return p_210132_0_ != null && IMob.VISIBLE_MOB_SELECTOR.test(p_210132_0_) && !(p_210132_0_ instanceof EntityCreeper);
      }));
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(PLAYER_CREATED, (byte)0);
   }

   protected void updateAITasks() {
      if (--this.homeCheckTimer <= 0) {
         this.homeCheckTimer = 70 + this.rand.nextInt(50);
         this.village = this.world.getVillageCollection().getNearestVillage(new BlockPos(this), 32);
         if (this.village == null) {
            this.detachHome();
         } else {
            BlockPos blockpos = this.village.getCenter();
            this.setHomePosAndDistance(blockpos, (int)((float)this.village.getVillageRadius() * 0.6F));
         }
      }

      super.updateAITasks();
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
      this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
   }

   protected int decreaseAirSupply(int p_70682_1_) {
      return p_70682_1_;
   }

   protected void collideWithEntity(Entity p_82167_1_) {
      if (p_82167_1_ instanceof IMob && !(p_82167_1_ instanceof EntityCreeper) && this.getRNG().nextInt(20) == 0) {
         this.setAttackTarget((EntityLivingBase)p_82167_1_);
      }

      super.collideWithEntity(p_82167_1_);
   }

   public void livingTick() {
      super.livingTick();
      if (this.attackTimer > 0) {
         --this.attackTimer;
      }

      if (this.holdRoseTick > 0) {
         --this.holdRoseTick;
      }

      if (this.motionX * this.motionX + this.motionZ * this.motionZ > (double)2.5000003E-7F && this.rand.nextInt(5) == 0) {
         int i = MathHelper.floor(this.posX);
         int j = MathHelper.floor(this.posY - (double)0.2F);
         int k = MathHelper.floor(this.posZ);
         IBlockState iblockstate = this.world.getBlockState(new BlockPos(i, j, k));
         if (!iblockstate.isAir()) {
            this.world.spawnParticle(new BlockParticleData(Particles.BLOCK, iblockstate), this.posX + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, this.getEntityBoundingBox().minY + 0.1D, this.posZ + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, 4.0D * ((double)this.rand.nextFloat() - 0.5D), 0.5D, ((double)this.rand.nextFloat() - 0.5D) * 4.0D);
         }
      }

   }

   public boolean canAttackClass(Class<? extends EntityLivingBase> p_70686_1_) {
      if (this.isPlayerCreated() && EntityPlayer.class.isAssignableFrom(p_70686_1_)) {
         return false;
      } else {
         return p_70686_1_ != EntityCreeper.class && super.canAttackClass(p_70686_1_);
      }
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setBoolean("PlayerCreated", this.isPlayerCreated());
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      this.setPlayerCreated(p_70037_1_.getBoolean("PlayerCreated"));
   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      this.attackTimer = 10;
      this.world.setEntityState(this, (byte)4);
      boolean flag = p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), (float)(7 + this.rand.nextInt(15)));
      if (flag) {
         p_70652_1_.motionY += (double)0.4F;
         this.applyEnchantments(this, p_70652_1_);
      }

      this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
      return flag;
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 4) {
         this.attackTimer = 10;
         this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
      } else if (p_70103_1_ == 11) {
         this.holdRoseTick = 400;
      } else if (p_70103_1_ == 34) {
         this.holdRoseTick = 0;
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   public Village getVillage() {
      return this.village;
   }

   @OnlyIn(Dist.CLIENT)
   public int getAttackTimer() {
      return this.attackTimer;
   }

   public void setHoldingRose(boolean p_70851_1_) {
      if (p_70851_1_) {
         this.holdRoseTick = 400;
         this.world.setEntityState(this, (byte)11);
      } else {
         this.holdRoseTick = 0;
         this.world.setEntityState(this, (byte)34);
      }

   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_IRON_GOLEM_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_IRON_GOLEM_DEATH;
   }

   protected void playStepSound(BlockPos p_180429_1_, IBlockState p_180429_2_) {
      this.playSound(SoundEvents.ENTITY_IRON_GOLEM_STEP, 1.0F, 1.0F);
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_IRON_GOLEM;
   }

   public int getHoldRoseTick() {
      return this.holdRoseTick;
   }

   public boolean isPlayerCreated() {
      return (this.dataManager.get(PLAYER_CREATED) & 1) != 0;
   }

   public void setPlayerCreated(boolean p_70849_1_) {
      byte b0 = this.dataManager.get(PLAYER_CREATED);
      if (p_70849_1_) {
         this.dataManager.set(PLAYER_CREATED, (byte)(b0 | 1));
      } else {
         this.dataManager.set(PLAYER_CREATED, (byte)(b0 & -2));
      }

   }

   public void onDeath(DamageSource p_70645_1_) {
      if (!this.isPlayerCreated() && this.attackingPlayer != null && this.village != null) {
         this.village.modifyPlayerReputation(this.attackingPlayer.getGameProfile().getName(), -5);
      }

      super.onDeath(p_70645_1_);
   }

   public boolean isNotColliding(IWorldReaderBase p_205019_1_) {
      BlockPos blockpos = new BlockPos(this.posX, this.posY, this.posZ);
      IBlockState iblockstate = p_205019_1_.getBlockState(blockpos);
      IBlockState iblockstate1 = p_205019_1_.getBlockState(blockpos.down());
      IBlockState iblockstate2 = p_205019_1_.getBlockState(blockpos.up());
      return iblockstate1.isTopSolid() && WorldEntitySpawner.func_206851_a(iblockstate2, iblockstate2.getFluidState()) && WorldEntitySpawner.func_206851_a(iblockstate, Fluids.EMPTY.getDefaultState()) && p_205019_1_.isCollisionBoxesEmpty(this, this.getEntityBoundingBox()) && p_205019_1_.checkNoEntityCollision(this, this.getEntityBoundingBox());
   }
}
