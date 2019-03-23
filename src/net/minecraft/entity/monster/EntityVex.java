package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityVex extends EntityMob {
   protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityVex.class, DataSerializers.BYTE);
   private EntityLiving owner;
   @Nullable
   private BlockPos boundOrigin;
   private boolean limitedLifespan;
   private int limitedLifeTicks;

   public EntityVex(World p_i47280_1_) {
      super(EntityType.VEX, p_i47280_1_);
      this.isImmuneToFire = true;
      this.moveHelper = new EntityVex.AIMoveControl(this);
      this.setSize(0.4F, 0.8F);
      this.experienceValue = 3;
   }

   public void move(MoverType p_70091_1_, double p_70091_2_, double p_70091_4_, double p_70091_6_) {
      super.move(p_70091_1_, p_70091_2_, p_70091_4_, p_70091_6_);
      this.doBlockCollisions();
   }

   public void tick() {
      this.noClip = true;
      super.tick();
      this.noClip = false;
      this.setNoGravity(true);
      if (this.limitedLifespan && --this.limitedLifeTicks <= 0) {
         this.limitedLifeTicks = 20;
         this.attackEntityFrom(DamageSource.STARVE, 1.0F);
      }

   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(4, new EntityVex.AIChargeAttack());
      this.tasks.addTask(8, new EntityVex.AIMoveRandom());
      this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
      this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, EntityVex.class));
      this.targetTasks.addTask(2, new EntityVex.AICopyOwnerTarget(this));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(14.0D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(VEX_FLAGS, (byte)0);
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      if (p_70037_1_.hasKey("BoundX")) {
         this.boundOrigin = new BlockPos(p_70037_1_.getInteger("BoundX"), p_70037_1_.getInteger("BoundY"), p_70037_1_.getInteger("BoundZ"));
      }

      if (p_70037_1_.hasKey("LifeTicks")) {
         this.setLimitedLife(p_70037_1_.getInteger("LifeTicks"));
      }

   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      if (this.boundOrigin != null) {
         p_70014_1_.setInteger("BoundX", this.boundOrigin.getX());
         p_70014_1_.setInteger("BoundY", this.boundOrigin.getY());
         p_70014_1_.setInteger("BoundZ", this.boundOrigin.getZ());
      }

      if (this.limitedLifespan) {
         p_70014_1_.setInteger("LifeTicks", this.limitedLifeTicks);
      }

   }

   public EntityLiving getOwner() {
      return this.owner;
   }

   @Nullable
   public BlockPos getBoundOrigin() {
      return this.boundOrigin;
   }

   public void setBoundOrigin(@Nullable BlockPos p_190651_1_) {
      this.boundOrigin = p_190651_1_;
   }

   private boolean getVexFlag(int p_190656_1_) {
      int i = this.dataManager.get(VEX_FLAGS);
      return (i & p_190656_1_) != 0;
   }

   private void setVexFlag(int p_190660_1_, boolean p_190660_2_) {
      int i = this.dataManager.get(VEX_FLAGS);
      if (p_190660_2_) {
         i = i | p_190660_1_;
      } else {
         i = i & ~p_190660_1_;
      }

      this.dataManager.set(VEX_FLAGS, (byte)(i & 255));
   }

   public boolean isCharging() {
      return this.getVexFlag(1);
   }

   public void setCharging(boolean p_190648_1_) {
      this.setVexFlag(1, p_190648_1_);
   }

   public void setOwner(EntityLiving p_190658_1_) {
      this.owner = p_190658_1_;
   }

   public void setLimitedLife(int p_190653_1_) {
      this.limitedLifespan = true;
      this.limitedLifeTicks = p_190653_1_;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_VEX_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_VEX_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_VEX_HURT;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_VEX;
   }

   @OnlyIn(Dist.CLIENT)
   public int getBrightnessForRender() {
      return 15728880;
   }

   public float getBrightness() {
      return 1.0F;
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance p_204210_1_, @Nullable IEntityLivingData p_204210_2_, @Nullable NBTTagCompound p_204210_3_) {
      this.setEquipmentBasedOnDifficulty(p_204210_1_);
      this.setEnchantmentBasedOnDifficulty(p_204210_1_);
      return super.onInitialSpawn(p_204210_1_, p_204210_2_, p_204210_3_);
   }

   protected void setEquipmentBasedOnDifficulty(DifficultyInstance p_180481_1_) {
      this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
      this.setDropChance(EntityEquipmentSlot.MAINHAND, 0.0F);
   }

   class AIChargeAttack extends EntityAIBase {
      public AIChargeAttack() {
         this.setMutexBits(1);
      }

      public boolean shouldExecute() {
         if (EntityVex.this.getAttackTarget() != null && !EntityVex.this.getMoveHelper().isUpdating() && EntityVex.this.rand.nextInt(7) == 0) {
            return EntityVex.this.getDistanceSq(EntityVex.this.getAttackTarget()) > 4.0D;
         } else {
            return false;
         }
      }

      public boolean shouldContinueExecuting() {
         return EntityVex.this.getMoveHelper().isUpdating() && EntityVex.this.isCharging() && EntityVex.this.getAttackTarget() != null && EntityVex.this.getAttackTarget().isEntityAlive();
      }

      public void startExecuting() {
         EntityLivingBase entitylivingbase = EntityVex.this.getAttackTarget();
         Vec3d vec3d = entitylivingbase.getEyePosition(1.0F);
         EntityVex.this.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1.0D);
         EntityVex.this.setCharging(true);
         EntityVex.this.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0F, 1.0F);
      }

      public void resetTask() {
         EntityVex.this.setCharging(false);
      }

      public void updateTask() {
         EntityLivingBase entitylivingbase = EntityVex.this.getAttackTarget();
         if (EntityVex.this.getEntityBoundingBox().intersects(entitylivingbase.getEntityBoundingBox())) {
            EntityVex.this.attackEntityAsMob(entitylivingbase);
            EntityVex.this.setCharging(false);
         } else {
            double d0 = EntityVex.this.getDistanceSq(entitylivingbase);
            if (d0 < 9.0D) {
               Vec3d vec3d = entitylivingbase.getEyePosition(1.0F);
               EntityVex.this.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1.0D);
            }
         }

      }
   }

   class AICopyOwnerTarget extends EntityAITarget {
      public AICopyOwnerTarget(EntityCreature p_i47231_2_) {
         super(p_i47231_2_, false);
      }

      public boolean shouldExecute() {
         return EntityVex.this.owner != null && EntityVex.this.owner.getAttackTarget() != null && this.isSuitableTarget(EntityVex.this.owner.getAttackTarget(), false);
      }

      public void startExecuting() {
         EntityVex.this.setAttackTarget(EntityVex.this.owner.getAttackTarget());
         super.startExecuting();
      }
   }

   class AIMoveControl extends EntityMoveHelper {
      public AIMoveControl(EntityVex p_i47230_2_) {
         super(p_i47230_2_);
      }

      public void tick() {
         if (this.action == EntityMoveHelper.Action.MOVE_TO) {
            double d0 = this.posX - EntityVex.this.posX;
            double d1 = this.posY - EntityVex.this.posY;
            double d2 = this.posZ - EntityVex.this.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            d3 = (double)MathHelper.sqrt(d3);
            if (d3 < EntityVex.this.getEntityBoundingBox().getAverageEdgeLength()) {
               this.action = EntityMoveHelper.Action.WAIT;
               EntityVex.this.motionX *= 0.5D;
               EntityVex.this.motionY *= 0.5D;
               EntityVex.this.motionZ *= 0.5D;
            } else {
               EntityVex.this.motionX += d0 / d3 * 0.05D * this.speed;
               EntityVex.this.motionY += d1 / d3 * 0.05D * this.speed;
               EntityVex.this.motionZ += d2 / d3 * 0.05D * this.speed;
               if (EntityVex.this.getAttackTarget() == null) {
                  EntityVex.this.rotationYaw = -((float)MathHelper.atan2(EntityVex.this.motionX, EntityVex.this.motionZ)) * (180F / (float)Math.PI);
                  EntityVex.this.renderYawOffset = EntityVex.this.rotationYaw;
               } else {
                  double d4 = EntityVex.this.getAttackTarget().posX - EntityVex.this.posX;
                  double d5 = EntityVex.this.getAttackTarget().posZ - EntityVex.this.posZ;
                  EntityVex.this.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * (180F / (float)Math.PI);
                  EntityVex.this.renderYawOffset = EntityVex.this.rotationYaw;
               }
            }

         }
      }
   }

   class AIMoveRandom extends EntityAIBase {
      public AIMoveRandom() {
         this.setMutexBits(1);
      }

      public boolean shouldExecute() {
         return !EntityVex.this.getMoveHelper().isUpdating() && EntityVex.this.rand.nextInt(7) == 0;
      }

      public boolean shouldContinueExecuting() {
         return false;
      }

      public void updateTask() {
         BlockPos blockpos = EntityVex.this.getBoundOrigin();
         if (blockpos == null) {
            blockpos = new BlockPos(EntityVex.this);
         }

         for(int i = 0; i < 3; ++i) {
            BlockPos blockpos1 = blockpos.add(EntityVex.this.rand.nextInt(15) - 7, EntityVex.this.rand.nextInt(11) - 5, EntityVex.this.rand.nextInt(15) - 7);
            if (EntityVex.this.world.isAirBlock(blockpos1)) {
               EntityVex.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);
               if (EntityVex.this.getAttackTarget() == null) {
                  EntityVex.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
               }
               break;
            }
         }

      }
   }
}
