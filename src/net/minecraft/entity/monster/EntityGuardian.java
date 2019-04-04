package net.minecraft.entity.monster;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityGuardian extends EntityMob {
   private static final DataParameter<Boolean> MOVING = EntityDataManager.createKey(EntityGuardian.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager.createKey(EntityGuardian.class, DataSerializers.VARINT);
   protected float clientSideTailAnimation;
   protected float clientSideTailAnimationO;
   protected float clientSideTailAnimationSpeed;
   protected float clientSideSpikesAnimation;
   protected float clientSideSpikesAnimationO;
   private EntityLivingBase targetedEntity;
   private int clientSideAttackTime;
   private boolean clientSideTouchedGround;
   protected EntityAIWander wander;

   protected EntityGuardian(EntityType<?> p_i48554_1_, World p_i48554_2_) {
      super(p_i48554_1_, p_i48554_2_);
      this.experienceValue = 10;
      this.setSize(0.85F, 0.85F);
      this.moveHelper = new EntityGuardian.GuardianMoveHelper(this);
      this.clientSideTailAnimation = this.rand.nextFloat();
      this.clientSideTailAnimationO = this.clientSideTailAnimation;
   }

   public EntityGuardian(World p_i45835_1_) {
      this(EntityType.GUARDIAN, p_i45835_1_);
   }

   protected void initEntityAI() {
      EntityAIMoveTowardsRestriction entityaimovetowardsrestriction = new EntityAIMoveTowardsRestriction(this, 1.0D);
      this.wander = new EntityAIWander(this, 1.0D, 80);
      this.tasks.addTask(4, new EntityGuardian.AIGuardianAttack(this));
      this.tasks.addTask(5, entityaimovetowardsrestriction);
      this.tasks.addTask(7, this.wander);
      this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityGuardian.class, 12.0F, 0.01F));
      this.tasks.addTask(9, new EntityAILookIdle(this));
      this.wander.setMutexBits(3);
      entityaimovetowardsrestriction.setMutexBits(3);
      this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 10, true, false, new EntityGuardian.GuardianTargetSelector(this)));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
   }

   protected PathNavigate createNavigator(World p_175447_1_) {
      return new PathNavigateSwimmer(this, p_175447_1_);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(MOVING, false);
      this.dataManager.register(TARGET_ENTITY, 0);
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.WATER;
   }

   public boolean isMoving() {
      return this.dataManager.get(MOVING);
   }

   private void setMoving(boolean p_175476_1_) {
      this.dataManager.set(MOVING, p_175476_1_);
   }

   public int getAttackDuration() {
      return 80;
   }

   private void setTargetedEntity(int p_175463_1_) {
      this.dataManager.set(TARGET_ENTITY, p_175463_1_);
   }

   public boolean hasTargetedEntity() {
      return this.dataManager.get(TARGET_ENTITY) != 0;
   }

   @Nullable
   public EntityLivingBase getTargetedEntity() {
      if (!this.hasTargetedEntity()) {
         return null;
      } else if (this.world.isRemote) {
         if (this.targetedEntity != null) {
            return this.targetedEntity;
         } else {
            Entity entity = this.world.getEntityByID(this.dataManager.get(TARGET_ENTITY));
            if (entity instanceof EntityLivingBase) {
               this.targetedEntity = (EntityLivingBase)entity;
               return this.targetedEntity;
            } else {
               return null;
            }
         }
      } else {
         return this.getAttackTarget();
      }
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      super.notifyDataManagerChange(p_184206_1_);
      if (TARGET_ENTITY.equals(p_184206_1_)) {
         this.clientSideAttackTime = 0;
         this.targetedEntity = null;
      }

   }

   public int getTalkInterval() {
      return 160;
   }

   protected SoundEvent getAmbientSound() {
      return this.isInWaterOrBubbleColumn() ? SoundEvents.ENTITY_GUARDIAN_AMBIENT : SoundEvents.ENTITY_GUARDIAN_AMBIENT_LAND;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return this.isInWaterOrBubbleColumn() ? SoundEvents.ENTITY_GUARDIAN_HURT : SoundEvents.ENTITY_GUARDIAN_HURT_LAND;
   }

   protected SoundEvent getDeathSound() {
      return this.isInWaterOrBubbleColumn() ? SoundEvents.ENTITY_GUARDIAN_DEATH : SoundEvents.ENTITY_GUARDIAN_DEATH_LAND;
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   public float getEyeHeight() {
      return this.height * 0.5F;
   }

   public float getBlockPathWeight(BlockPos p_205022_1_, IWorldReaderBase p_205022_2_) {
      return p_205022_2_.getFluidState(p_205022_1_).isTagged(FluidTags.WATER) ? 10.0F + p_205022_2_.getBrightness(p_205022_1_) - 0.5F : super.getBlockPathWeight(p_205022_1_, p_205022_2_);
   }

   public void livingTick() {
      if (this.world.isRemote) {
         this.clientSideTailAnimationO = this.clientSideTailAnimation;
         if (!this.isInWater()) {
            this.clientSideTailAnimationSpeed = 2.0F;
            if (this.motionY > 0.0D && this.clientSideTouchedGround && !this.isSilent()) {
               this.world.playSound(this.posX, this.posY, this.posZ, this.getFlopSound(), this.getSoundCategory(), 1.0F, 1.0F, false);
            }

            this.clientSideTouchedGround = this.motionY < 0.0D && this.world.isTopSolid((new BlockPos(this)).down());
         } else if (this.isMoving()) {
            if (this.clientSideTailAnimationSpeed < 0.5F) {
               this.clientSideTailAnimationSpeed = 4.0F;
            } else {
               this.clientSideTailAnimationSpeed += (0.5F - this.clientSideTailAnimationSpeed) * 0.1F;
            }
         } else {
            this.clientSideTailAnimationSpeed += (0.125F - this.clientSideTailAnimationSpeed) * 0.2F;
         }

         this.clientSideTailAnimation += this.clientSideTailAnimationSpeed;
         this.clientSideSpikesAnimationO = this.clientSideSpikesAnimation;
         if (!this.isInWaterOrBubbleColumn()) {
            this.clientSideSpikesAnimation = this.rand.nextFloat();
         } else if (this.isMoving()) {
            this.clientSideSpikesAnimation += (0.0F - this.clientSideSpikesAnimation) * 0.25F;
         } else {
            this.clientSideSpikesAnimation += (1.0F - this.clientSideSpikesAnimation) * 0.06F;
         }

         if (this.isMoving() && this.isInWater()) {
            Vec3d vec3d = this.getLook(0.0F);

            for(int i = 0; i < 2; ++i) {
               this.world.spawnParticle(Particles.BUBBLE, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width - vec3d.x * 1.5D, this.posY + this.rand.nextDouble() * (double)this.height - vec3d.y * 1.5D, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width - vec3d.z * 1.5D, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.hasTargetedEntity()) {
            if (this.clientSideAttackTime < this.getAttackDuration()) {
               ++this.clientSideAttackTime;
            }

            EntityLivingBase entitylivingbase = this.getTargetedEntity();
            if (entitylivingbase != null) {
               this.getLookHelper().setLookPositionWithEntity(entitylivingbase, 90.0F, 90.0F);
               this.getLookHelper().tick();
               double d5 = (double)this.getAttackAnimationScale(0.0F);
               double d0 = entitylivingbase.posX - this.posX;
               double d1 = entitylivingbase.posY + (double)(entitylivingbase.height * 0.5F) - (this.posY + (double)this.getEyeHeight());
               double d2 = entitylivingbase.posZ - this.posZ;
               double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
               d0 = d0 / d3;
               d1 = d1 / d3;
               d2 = d2 / d3;
               double d4 = this.rand.nextDouble();

               while(d4 < d3) {
                  d4 += 1.8D - d5 + this.rand.nextDouble() * (1.7D - d5);
                  this.world.spawnParticle(Particles.BUBBLE, this.posX + d0 * d4, this.posY + d1 * d4 + (double)this.getEyeHeight(), this.posZ + d2 * d4, 0.0D, 0.0D, 0.0D);
               }
            }
         }
      }

      if (this.isInWaterOrBubbleColumn()) {
         this.setAir(300);
      } else if (this.onGround) {
         this.motionY += 0.5D;
         this.motionX += (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 0.4F);
         this.motionZ += (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 0.4F);
         this.rotationYaw = this.rand.nextFloat() * 360.0F;
         this.onGround = false;
         this.isAirBorne = true;
      }

      if (this.hasTargetedEntity()) {
         this.rotationYaw = this.rotationYawHead;
      }

      super.livingTick();
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.ENTITY_GUARDIAN_FLOP;
   }

   @OnlyIn(Dist.CLIENT)
   public float getTailAnimation(float p_175471_1_) {
      return this.clientSideTailAnimationO + (this.clientSideTailAnimation - this.clientSideTailAnimationO) * p_175471_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public float getSpikesAnimation(float p_175469_1_) {
      return this.clientSideSpikesAnimationO + (this.clientSideSpikesAnimation - this.clientSideSpikesAnimationO) * p_175469_1_;
   }

   public float getAttackAnimationScale(float p_175477_1_) {
      return ((float)this.clientSideAttackTime + p_175477_1_) / (float)this.getAttackDuration();
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_GUARDIAN;
   }

   protected boolean isValidLightLevel() {
      return true;
   }

   public boolean isNotColliding(IWorldReaderBase p_205019_1_) {
      return p_205019_1_.checkNoEntityCollision(this, this.getEntityBoundingBox()) && p_205019_1_.isCollisionBoxesEmpty(this, this.getEntityBoundingBox());
   }

   public boolean func_205020_a(IWorld p_205020_1_, boolean p_205020_2_) {
      return (this.rand.nextInt(20) == 0 || !p_205020_1_.canBlockSeeSky(new BlockPos(this))) && super.func_205020_a(p_205020_1_, p_205020_2_);
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (!this.isMoving() && !p_70097_1_.isMagicDamage() && p_70097_1_.getImmediateSource() instanceof EntityLivingBase) {
         EntityLivingBase entitylivingbase = (EntityLivingBase)p_70097_1_.getImmediateSource();
         if (!p_70097_1_.isExplosion()) {
            entitylivingbase.attackEntityFrom(DamageSource.causeThornsDamage(this), 2.0F);
         }
      }

      if (this.wander != null) {
         this.wander.makeUpdate();
      }

      return super.attackEntityFrom(p_70097_1_, p_70097_2_);
   }

   public int getVerticalFaceSpeed() {
      return 180;
   }

   public void travel(float p_191986_1_, float p_191986_2_, float p_191986_3_) {
      if (this.isServerWorld() && this.isInWater()) {
         this.moveRelative(p_191986_1_, p_191986_2_, p_191986_3_, 0.1F);
         this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
         this.motionX *= (double)0.9F;
         this.motionY *= (double)0.9F;
         this.motionZ *= (double)0.9F;
         if (!this.isMoving() && this.getAttackTarget() == null) {
            this.motionY -= 0.005D;
         }
      } else {
         super.travel(p_191986_1_, p_191986_2_, p_191986_3_);
      }

   }

   static class AIGuardianAttack extends EntityAIBase {
      private final EntityGuardian guardian;
      private int tickCounter;
      private final boolean isElder;

      public AIGuardianAttack(EntityGuardian p_i45833_1_) {
         this.guardian = p_i45833_1_;
         this.isElder = p_i45833_1_ instanceof EntityElderGuardian;
         this.setMutexBits(3);
      }

      public boolean shouldExecute() {
         EntityLivingBase entitylivingbase = this.guardian.getAttackTarget();
         return entitylivingbase != null && entitylivingbase.isEntityAlive();
      }

      public boolean shouldContinueExecuting() {
         return super.shouldContinueExecuting() && (this.isElder || this.guardian.getDistanceSq(this.guardian.getAttackTarget()) > 9.0D);
      }

      public void startExecuting() {
         this.tickCounter = -10;
         this.guardian.getNavigator().clearPath();
         this.guardian.getLookHelper().setLookPositionWithEntity(this.guardian.getAttackTarget(), 90.0F, 90.0F);
         this.guardian.isAirBorne = true;
      }

      public void resetTask() {
         this.guardian.setTargetedEntity(0);
         this.guardian.setAttackTarget(null);
         this.guardian.wander.makeUpdate();
      }

      public void updateTask() {
         EntityLivingBase entitylivingbase = this.guardian.getAttackTarget();
         this.guardian.getNavigator().clearPath();
         this.guardian.getLookHelper().setLookPositionWithEntity(entitylivingbase, 90.0F, 90.0F);
         if (!this.guardian.canEntityBeSeen(entitylivingbase)) {
            this.guardian.setAttackTarget(null);
         } else {
            ++this.tickCounter;
            if (this.tickCounter == 0) {
               this.guardian.setTargetedEntity(this.guardian.getAttackTarget().getEntityId());
               this.guardian.world.setEntityState(this.guardian, (byte)21);
            } else if (this.tickCounter >= this.guardian.getAttackDuration()) {
               float f = 1.0F;
               if (this.guardian.world.getDifficulty() == EnumDifficulty.HARD) {
                  f += 2.0F;
               }

               if (this.isElder) {
                  f += 2.0F;
               }

               entitylivingbase.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this.guardian, this.guardian), f);
               entitylivingbase.attackEntityFrom(DamageSource.causeMobDamage(this.guardian), (float)this.guardian.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
               this.guardian.setAttackTarget(null);
            }

            super.updateTask();
         }
      }
   }

   static class GuardianMoveHelper extends EntityMoveHelper {
      private final EntityGuardian entityGuardian;

      public GuardianMoveHelper(EntityGuardian p_i45831_1_) {
         super(p_i45831_1_);
         this.entityGuardian = p_i45831_1_;
      }

      public void tick() {
         if (this.action == EntityMoveHelper.Action.MOVE_TO && !this.entityGuardian.getNavigator().noPath()) {
            double d0 = this.posX - this.entityGuardian.posX;
            double d1 = this.posY - this.entityGuardian.posY;
            double d2 = this.posZ - this.entityGuardian.posZ;
            double d3 = (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
            d1 = d1 / d3;
            float f = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
            this.entityGuardian.rotationYaw = this.limitAngle(this.entityGuardian.rotationYaw, f, 90.0F);
            this.entityGuardian.renderYawOffset = this.entityGuardian.rotationYaw;
            float f1 = (float)(this.speed * this.entityGuardian.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
            this.entityGuardian.setAIMoveSpeed(this.entityGuardian.getAIMoveSpeed() + (f1 - this.entityGuardian.getAIMoveSpeed()) * 0.125F);
            double d4 = Math.sin((double)(this.entityGuardian.ticksExisted + this.entityGuardian.getEntityId()) * 0.5D) * 0.05D;
            double d5 = Math.cos((double)(this.entityGuardian.rotationYaw * ((float)Math.PI / 180F)));
            double d6 = Math.sin((double)(this.entityGuardian.rotationYaw * ((float)Math.PI / 180F)));
            this.entityGuardian.motionX += d4 * d5;
            this.entityGuardian.motionZ += d4 * d6;
            d4 = Math.sin((double)(this.entityGuardian.ticksExisted + this.entityGuardian.getEntityId()) * 0.75D) * 0.05D;
            this.entityGuardian.motionY += d4 * (d6 + d5) * 0.25D;
            this.entityGuardian.motionY += (double)this.entityGuardian.getAIMoveSpeed() * d1 * 0.1D;
            EntityLookHelper entitylookhelper = this.entityGuardian.getLookHelper();
            double d7 = this.entityGuardian.posX + d0 / d3 * 2.0D;
            double d8 = (double)this.entityGuardian.getEyeHeight() + this.entityGuardian.posY + d1 / d3;
            double d9 = this.entityGuardian.posZ + d2 / d3 * 2.0D;
            double d10 = entitylookhelper.getLookPosX();
            double d11 = entitylookhelper.getLookPosY();
            double d12 = entitylookhelper.getLookPosZ();
            if (!entitylookhelper.getIsLooking()) {
               d10 = d7;
               d11 = d8;
               d12 = d9;
            }

            this.entityGuardian.getLookHelper().setLookPosition(d10 + (d7 - d10) * 0.125D, d11 + (d8 - d11) * 0.125D, d12 + (d9 - d12) * 0.125D, 10.0F, 40.0F);
            this.entityGuardian.setMoving(true);
         } else {
            this.entityGuardian.setAIMoveSpeed(0.0F);
            this.entityGuardian.setMoving(false);
         }
      }
   }

   static class GuardianTargetSelector implements Predicate<EntityLivingBase> {
      private final EntityGuardian parentEntity;

      public GuardianTargetSelector(EntityGuardian p_i45832_1_) {
         this.parentEntity = p_i45832_1_;
      }

      public boolean test(@Nullable EntityLivingBase p_test_1_) {
         return (p_test_1_ instanceof EntityPlayer || p_test_1_ instanceof EntitySquid) && p_test_1_.getDistanceSq(this.parentEntity) > 9.0D;
      }
   }
}
