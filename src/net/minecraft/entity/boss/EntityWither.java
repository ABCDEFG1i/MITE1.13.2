package net.minecraft.entity.boss;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityWither extends EntityMob implements IRangedAttackMob {
   private static final DataParameter<Integer> FIRST_HEAD_TARGET = EntityDataManager.createKey(EntityWither.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> SECOND_HEAD_TARGET = EntityDataManager.createKey(EntityWither.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> THIRD_HEAD_TARGET = EntityDataManager.createKey(EntityWither.class, DataSerializers.VARINT);
   private static final List<DataParameter<Integer>> HEAD_TARGETS = ImmutableList.of(FIRST_HEAD_TARGET, SECOND_HEAD_TARGET, THIRD_HEAD_TARGET);
   private static final DataParameter<Integer> INVULNERABILITY_TIME = EntityDataManager.createKey(EntityWither.class, DataSerializers.VARINT);
   private final float[] xRotationHeads = new float[2];
   private final float[] yRotationHeads = new float[2];
   private final float[] xRotOHeads = new float[2];
   private final float[] yRotOHeads = new float[2];
   private final int[] nextHeadUpdate = new int[2];
   private final int[] idleHeadUpdates = new int[2];
   private int blockBreakCounter;
   private final BossInfoServer bossInfo = (BossInfoServer)(new BossInfoServer(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS)).setDarkenSky(true);
   private static final Predicate<Entity> NOT_UNDEAD = (p_210135_0_) -> {
      return p_210135_0_ instanceof EntityLivingBase && ((EntityLivingBase)p_210135_0_).getCreatureAttribute() != CreatureAttribute.UNDEAD && ((EntityLivingBase)p_210135_0_).attackable();
   };

   public EntityWither(World p_i1701_1_) {
      super(EntityType.WITHER, p_i1701_1_);
      this.setHealth(this.getMaxHealth());
      this.setSize(0.9F, 3.5F);
      this.isImmuneToFire = true;
      this.getNavigator().setCanSwim(true);
      this.experienceValue = 50;
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntityWither.AIDoNothing());
      this.tasks.addTask(2, new EntityAIAttackRanged(this, 1.0D, 40, 20.0F));
      this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
      this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(7, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityLiving.class, 0, false, false, NOT_UNDEAD));
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(FIRST_HEAD_TARGET, 0);
      this.dataManager.register(SECOND_HEAD_TARGET, 0);
      this.dataManager.register(THIRD_HEAD_TARGET, 0);
      this.dataManager.register(INVULNERABILITY_TIME, 0);
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setInteger("Invul", this.getInvulTime());
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      this.setInvulTime(p_70037_1_.getInteger("Invul"));
      if (this.hasCustomName()) {
         this.bossInfo.setName(this.getDisplayName());
      }

   }

   public void setCustomName(@Nullable ITextComponent p_200203_1_) {
      super.setCustomName(p_200203_1_);
      this.bossInfo.setName(this.getDisplayName());
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_WITHER_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_WITHER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_WITHER_DEATH;
   }

   public void livingTick() {
      this.motionY *= (double)0.6F;
      if (!this.world.isRemote && this.getWatchedTargetId(0) > 0) {
         Entity entity = this.world.getEntityByID(this.getWatchedTargetId(0));
         if (entity != null) {
            if (this.posY < entity.posY || !this.isArmored() && this.posY < entity.posY + 5.0D) {
               if (this.motionY < 0.0D) {
                  this.motionY = 0.0D;
               }

               this.motionY += (0.5D - this.motionY) * (double)0.6F;
            }

            double d0 = entity.posX - this.posX;
            double d1 = entity.posZ - this.posZ;
            double d3 = d0 * d0 + d1 * d1;
            if (d3 > 9.0D) {
               double d5 = (double)MathHelper.sqrt(d3);
               this.motionX += (d0 / d5 * 0.5D - this.motionX) * (double)0.6F;
               this.motionZ += (d1 / d5 * 0.5D - this.motionZ) * (double)0.6F;
            }
         }
      }

      if (this.motionX * this.motionX + this.motionZ * this.motionZ > (double)0.05F) {
         this.rotationYaw = (float)MathHelper.atan2(this.motionZ, this.motionX) * (180F / (float)Math.PI) - 90.0F;
      }

      super.livingTick();

      for(int i = 0; i < 2; ++i) {
         this.yRotOHeads[i] = this.yRotationHeads[i];
         this.xRotOHeads[i] = this.xRotationHeads[i];
      }

      for(int j = 0; j < 2; ++j) {
         int k = this.getWatchedTargetId(j + 1);
         Entity entity1 = null;
         if (k > 0) {
            entity1 = this.world.getEntityByID(k);
         }

         if (entity1 != null) {
            double d11 = this.getHeadX(j + 1);
            double d12 = this.getHeadY(j + 1);
            double d13 = this.getHeadZ(j + 1);
            double d6 = entity1.posX - d11;
            double d7 = entity1.posY + (double)entity1.getEyeHeight() - d12;
            double d8 = entity1.posZ - d13;
            double d9 = (double)MathHelper.sqrt(d6 * d6 + d8 * d8);
            float f = (float)(MathHelper.atan2(d8, d6) * (double)(180F / (float)Math.PI)) - 90.0F;
            float f1 = (float)(-(MathHelper.atan2(d7, d9) * (double)(180F / (float)Math.PI)));
            this.xRotationHeads[j] = this.rotlerp(this.xRotationHeads[j], f1, 40.0F);
            this.yRotationHeads[j] = this.rotlerp(this.yRotationHeads[j], f, 10.0F);
         } else {
            this.yRotationHeads[j] = this.rotlerp(this.yRotationHeads[j], this.renderYawOffset, 10.0F);
         }
      }

      boolean flag = this.isArmored();

      for(int l = 0; l < 3; ++l) {
         double d10 = this.getHeadX(l);
         double d2 = this.getHeadY(l);
         double d4 = this.getHeadZ(l);
         this.world.spawnParticle(Particles.SMOKE, d10 + this.rand.nextGaussian() * (double)0.3F, d2 + this.rand.nextGaussian() * (double)0.3F, d4 + this.rand.nextGaussian() * (double)0.3F, 0.0D, 0.0D, 0.0D);
         if (flag && this.world.rand.nextInt(4) == 0) {
            this.world.spawnParticle(Particles.ENTITY_EFFECT, d10 + this.rand.nextGaussian() * (double)0.3F, d2 + this.rand.nextGaussian() * (double)0.3F, d4 + this.rand.nextGaussian() * (double)0.3F, (double)0.7F, (double)0.7F, 0.5D);
         }
      }

      if (this.getInvulTime() > 0) {
         for(int i1 = 0; i1 < 3; ++i1) {
            this.world.spawnParticle(Particles.ENTITY_EFFECT, this.posX + this.rand.nextGaussian(), this.posY + (double)(this.rand.nextFloat() * 3.3F), this.posZ + this.rand.nextGaussian(), (double)0.7F, (double)0.7F, (double)0.9F);
         }
      }

   }

   protected void updateAITasks() {
      if (this.getInvulTime() > 0) {
         int j1 = this.getInvulTime() - 1;
         if (j1 <= 0) {
            this.world.newExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, 7.0F, false, this.world.getGameRules().getBoolean("mobGriefing"));
            this.world.playBroadcastSound(1023, new BlockPos(this), 0);
         }

         this.setInvulTime(j1);
         if (this.ticksExisted % 10 == 0) {
            this.heal(10.0F);
         }

      } else {
         super.updateAITasks();

         for(int i = 1; i < 3; ++i) {
            if (this.ticksExisted >= this.nextHeadUpdate[i - 1]) {
               this.nextHeadUpdate[i - 1] = this.ticksExisted + 10 + this.rand.nextInt(10);
               if (this.world.getDifficulty() == EnumDifficulty.NORMAL || this.world.getDifficulty() == EnumDifficulty.HARD) {
                  int j3 = i - 1;
                  int k3 = this.idleHeadUpdates[i - 1];
                  this.idleHeadUpdates[j3] = this.idleHeadUpdates[i - 1] + 1;
                  if (k3 > 15) {
                     float f = 10.0F;
                     float f1 = 5.0F;
                     double d0 = MathHelper.nextDouble(this.rand, this.posX - 10.0D, this.posX + 10.0D);
                     double d1 = MathHelper.nextDouble(this.rand, this.posY - 5.0D, this.posY + 5.0D);
                     double d2 = MathHelper.nextDouble(this.rand, this.posZ - 10.0D, this.posZ + 10.0D);
                     this.launchWitherSkullToCoords(i + 1, d0, d1, d2, true);
                     this.idleHeadUpdates[i - 1] = 0;
                  }
               }

               int k1 = this.getWatchedTargetId(i);
               if (k1 > 0) {
                  Entity entity = this.world.getEntityByID(k1);
                  if (entity != null && entity.isEntityAlive() && !(this.getDistanceSq(entity) > 900.0D) && this.canEntityBeSeen(entity)) {
                     if (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.disableDamage) {
                        this.updateWatchedTargetId(i, 0);
                     } else {
                        this.launchWitherSkullToEntity(i + 1, (EntityLivingBase)entity);
                        this.nextHeadUpdate[i - 1] = this.ticksExisted + 40 + this.rand.nextInt(20);
                        this.idleHeadUpdates[i - 1] = 0;
                     }
                  } else {
                     this.updateWatchedTargetId(i, 0);
                  }
               } else {
                  List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(20.0D, 8.0D, 20.0D), NOT_UNDEAD.and(EntitySelectors.NOT_SPECTATING));

                  for(int j2 = 0; j2 < 10 && !list.isEmpty(); ++j2) {
                     EntityLivingBase entitylivingbase = list.get(this.rand.nextInt(list.size()));
                     if (entitylivingbase != this && entitylivingbase.isEntityAlive() && this.canEntityBeSeen(entitylivingbase)) {
                        if (entitylivingbase instanceof EntityPlayer) {
                           if (!((EntityPlayer)entitylivingbase).capabilities.disableDamage) {
                              this.updateWatchedTargetId(i, entitylivingbase.getEntityId());
                           }
                        } else {
                           this.updateWatchedTargetId(i, entitylivingbase.getEntityId());
                        }
                        break;
                     }

                     list.remove(entitylivingbase);
                  }
               }
            }
         }

         if (this.getAttackTarget() != null) {
            this.updateWatchedTargetId(0, this.getAttackTarget().getEntityId());
         } else {
            this.updateWatchedTargetId(0, 0);
         }

         if (this.blockBreakCounter > 0) {
            --this.blockBreakCounter;
            if (this.blockBreakCounter == 0 && this.world.getGameRules().getBoolean("mobGriefing")) {
               int i1 = MathHelper.floor(this.posY);
               int l1 = MathHelper.floor(this.posX);
               int i2 = MathHelper.floor(this.posZ);
               boolean flag = false;

               for(int k2 = -1; k2 <= 1; ++k2) {
                  for(int l2 = -1; l2 <= 1; ++l2) {
                     for(int j = 0; j <= 3; ++j) {
                        int i3 = l1 + k2;
                        int k = i1 + j;
                        int l = i2 + l2;
                        BlockPos blockpos = new BlockPos(i3, k, l);
                        IBlockState iblockstate = this.world.getBlockState(blockpos);
                        Block block = iblockstate.getBlock();
                        if (!iblockstate.isAir() && canDestroyBlock(block)) {
                           flag = this.world.destroyBlock(blockpos, true) || flag;
                        }
                     }
                  }
               }

               if (flag) {
                  this.world.playEvent(null, 1022, new BlockPos(this), 0);
               }
            }
         }

         if (this.ticksExisted % 20 == 0) {
            this.heal(1.0F);
         }

         this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
      }
   }

   public static boolean canDestroyBlock(Block p_181033_0_) {
      return p_181033_0_ != Blocks.BEDROCK && p_181033_0_ != Blocks.END_PORTAL && p_181033_0_ != Blocks.END_PORTAL_FRAME && p_181033_0_ != Blocks.COMMAND_BLOCK && p_181033_0_ != Blocks.REPEATING_COMMAND_BLOCK && p_181033_0_ != Blocks.CHAIN_COMMAND_BLOCK && p_181033_0_ != Blocks.BARRIER && p_181033_0_ != Blocks.STRUCTURE_BLOCK && p_181033_0_ != Blocks.STRUCTURE_VOID && p_181033_0_ != Blocks.MOVING_PISTON && p_181033_0_ != Blocks.END_GATEWAY;
   }

   public void ignite() {
      this.setInvulTime(220);
      this.setHealth(this.getMaxHealth() / 3.0F);
   }

   public void setInWeb() {
   }

   public void addTrackingPlayer(EntityPlayerMP p_184178_1_) {
      super.addTrackingPlayer(p_184178_1_);
      this.bossInfo.addPlayer(p_184178_1_);
   }

   public void removeTrackingPlayer(EntityPlayerMP p_184203_1_) {
      super.removeTrackingPlayer(p_184203_1_);
      this.bossInfo.removePlayer(p_184203_1_);
   }

   private double getHeadX(int p_82214_1_) {
      if (p_82214_1_ <= 0) {
         return this.posX;
      } else {
         float f = (this.renderYawOffset + (float)(180 * (p_82214_1_ - 1))) * ((float)Math.PI / 180F);
         float f1 = MathHelper.cos(f);
         return this.posX + (double)f1 * 1.3D;
      }
   }

   private double getHeadY(int p_82208_1_) {
      return p_82208_1_ <= 0 ? this.posY + 3.0D : this.posY + 2.2D;
   }

   private double getHeadZ(int p_82213_1_) {
      if (p_82213_1_ <= 0) {
         return this.posZ;
      } else {
         float f = (this.renderYawOffset + (float)(180 * (p_82213_1_ - 1))) * ((float)Math.PI / 180F);
         float f1 = MathHelper.sin(f);
         return this.posZ + (double)f1 * 1.3D;
      }
   }

   private float rotlerp(float p_82204_1_, float p_82204_2_, float p_82204_3_) {
      float f = MathHelper.wrapDegrees(p_82204_2_ - p_82204_1_);
      if (f > p_82204_3_) {
         f = p_82204_3_;
      }

      if (f < -p_82204_3_) {
         f = -p_82204_3_;
      }

      return p_82204_1_ + f;
   }

   private void launchWitherSkullToEntity(int p_82216_1_, EntityLivingBase p_82216_2_) {
      this.launchWitherSkullToCoords(p_82216_1_, p_82216_2_.posX, p_82216_2_.posY + (double)p_82216_2_.getEyeHeight() * 0.5D, p_82216_2_.posZ, p_82216_1_ == 0 && this.rand.nextFloat() < 0.001F);
   }

   private void launchWitherSkullToCoords(int p_82209_1_, double p_82209_2_, double p_82209_4_, double p_82209_6_, boolean p_82209_8_) {
      this.world.playEvent(null, 1024, new BlockPos(this), 0);
      double d0 = this.getHeadX(p_82209_1_);
      double d1 = this.getHeadY(p_82209_1_);
      double d2 = this.getHeadZ(p_82209_1_);
      double d3 = p_82209_2_ - d0;
      double d4 = p_82209_4_ - d1;
      double d5 = p_82209_6_ - d2;
      EntityWitherSkull entitywitherskull = new EntityWitherSkull(this.world, this, d3, d4, d5);
      if (p_82209_8_) {
         entitywitherskull.setSkullInvulnerable(true);
      }

      entitywitherskull.posY = d1;
      entitywitherskull.posX = d0;
      entitywitherskull.posZ = d2;
      this.world.spawnEntity(entitywitherskull);
   }

   public void attackEntityWithRangedAttack(EntityLivingBase p_82196_1_, float p_82196_2_) {
      this.launchWitherSkullToEntity(0, p_82196_1_);
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else if (p_70097_1_ != DamageSource.DROWN && !(p_70097_1_.getTrueSource() instanceof EntityWither)) {
         if (this.getInvulTime() > 0 && p_70097_1_ != DamageSource.OUT_OF_WORLD) {
            return false;
         } else {
            if (this.isArmored()) {
               Entity entity = p_70097_1_.getImmediateSource();
               if (entity instanceof EntityArrow) {
                  return false;
               }
            }

            Entity entity1 = p_70097_1_.getTrueSource();
            if (entity1 != null && !(entity1 instanceof EntityPlayer) && entity1 instanceof EntityLivingBase && ((EntityLivingBase)entity1).getCreatureAttribute() == this.getCreatureAttribute()) {
               return false;
            } else {
               if (this.blockBreakCounter <= 0) {
                  this.blockBreakCounter = 20;
               }

               for(int i = 0; i < this.idleHeadUpdates.length; ++i) {
                  this.idleHeadUpdates[i] += 3;
               }

               return super.attackEntityFrom(p_70097_1_, p_70097_2_);
            }
         }
      } else {
         return false;
      }
   }

   protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
      EntityItem entityitem = this.entityDropItem(Items.NETHER_STAR);
      if (entityitem != null) {
         entityitem.setNoDespawn();
      }

   }

   protected void checkDespawn() {
      this.idleTime = 0;
   }

   @OnlyIn(Dist.CLIENT)
   public int getBrightnessForRender() {
      return 15728880;
   }

   public void fall(float p_180430_1_, float p_180430_2_, boolean isNormalBlock) {
   }

   public boolean addPotionEffect(PotionEffect p_195064_1_) {
      return false;
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(300.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.6F);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
      this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(4.0D);
   }

   @OnlyIn(Dist.CLIENT)
   public float getHeadYRotation(int p_82207_1_) {
      return this.yRotationHeads[p_82207_1_];
   }

   @OnlyIn(Dist.CLIENT)
   public float getHeadXRotation(int p_82210_1_) {
      return this.xRotationHeads[p_82210_1_];
   }

   public int getInvulTime() {
      return this.dataManager.get(INVULNERABILITY_TIME);
   }

   public void setInvulTime(int p_82215_1_) {
      this.dataManager.set(INVULNERABILITY_TIME, p_82215_1_);
   }

   public int getWatchedTargetId(int p_82203_1_) {
      return this.dataManager.get(HEAD_TARGETS.get(p_82203_1_));
   }

   public void updateWatchedTargetId(int p_82211_1_, int p_82211_2_) {
      this.dataManager.set(HEAD_TARGETS.get(p_82211_1_), p_82211_2_);
   }

   public boolean isArmored() {
      return this.getHealth() <= this.getMaxHealth() / 2.0F;
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.UNDEAD;
   }

   protected boolean canBeRidden(Entity p_184228_1_) {
      return false;
   }

   public boolean isNonBoss() {
      return false;
   }

   public void setSwingingArms(boolean p_184724_1_) {
   }

   class AIDoNothing extends EntityAIBase {
      public AIDoNothing() {
         this.setMutexBits(7);
      }

      public boolean shouldExecute() {
         return EntityWither.this.getInvulTime() > 0;
      }
   }
}
