package net.minecraft.entity.passive;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityBat extends EntityAmbientCreature {
   private static final DataParameter<Byte> HANGING = EntityDataManager.createKey(EntityBat.class, DataSerializers.BYTE);
   private BlockPos spawnPosition;

   public EntityBat(World p_i1680_1_) {
      super(EntityType.BAT, p_i1680_1_);
      this.setSize(0.5F, 0.9F);
      this.setIsBatHanging(true);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(HANGING, (byte)0);
   }

   protected float getSoundVolume() {
      return 0.1F;
   }

   protected float getSoundPitch() {
      return super.getSoundPitch() * 0.95F;
   }

   @Nullable
   public SoundEvent getAmbientSound() {
      return this.getIsBatHanging() && this.rand.nextInt(4) != 0 ? null : SoundEvents.ENTITY_BAT_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_BAT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_BAT_DEATH;
   }

   public boolean canBePushed() {
      return false;
   }

   protected void collideWithEntity(Entity p_82167_1_) {
   }

   protected void collideWithNearbyEntities() {
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6.0D);
   }

   public boolean getIsBatHanging() {
      return (this.dataManager.get(HANGING) & 1) != 0;
   }

   public void setIsBatHanging(boolean p_82236_1_) {
      byte b0 = this.dataManager.get(HANGING);
      if (p_82236_1_) {
         this.dataManager.set(HANGING, (byte)(b0 | 1));
      } else {
         this.dataManager.set(HANGING, (byte)(b0 & -2));
      }

   }

   public void tick() {
      super.tick();
      if (this.getIsBatHanging()) {
         this.motionX = 0.0D;
         this.motionY = 0.0D;
         this.motionZ = 0.0D;
         this.posY = (double)MathHelper.floor(this.posY) + 1.0D - (double)this.height;
      } else {
         this.motionY *= (double)0.6F;
      }

   }

   protected void updateAITasks() {
      super.updateAITasks();
      BlockPos blockpos = new BlockPos(this);
      BlockPos blockpos1 = blockpos.up();
      if (this.getIsBatHanging()) {
         if (this.world.getBlockState(blockpos1).isNormalCube()) {
            if (this.rand.nextInt(200) == 0) {
               this.rotationYawHead = (float)this.rand.nextInt(360);
            }

            if (this.world.getNearestPlayerNotCreative(this, 4.0D) != null) {
               this.setIsBatHanging(false);
               this.world.playEvent(null, 1025, blockpos, 0);
            }
         } else {
            this.setIsBatHanging(false);
            this.world.playEvent(null, 1025, blockpos, 0);
         }
      } else {
         if (this.spawnPosition != null && (!this.world.isAirBlock(this.spawnPosition) || this.spawnPosition.getY() < 1)) {
            this.spawnPosition = null;
         }

         if (this.spawnPosition == null || this.rand.nextInt(30) == 0 || this.spawnPosition.distanceSq((double)((int)this.posX), (double)((int)this.posY), (double)((int)this.posZ)) < 4.0D) {
            this.spawnPosition = new BlockPos((int)this.posX + this.rand.nextInt(7) - this.rand.nextInt(7), (int)this.posY + this.rand.nextInt(6) - 2, (int)this.posZ + this.rand.nextInt(7) - this.rand.nextInt(7));
         }

         double d0 = (double)this.spawnPosition.getX() + 0.5D - this.posX;
         double d1 = (double)this.spawnPosition.getY() + 0.1D - this.posY;
         double d2 = (double)this.spawnPosition.getZ() + 0.5D - this.posZ;
         this.motionX += (Math.signum(d0) * 0.5D - this.motionX) * (double)0.1F;
         this.motionY += (Math.signum(d1) * (double)0.7F - this.motionY) * (double)0.1F;
         this.motionZ += (Math.signum(d2) * 0.5D - this.motionZ) * (double)0.1F;
         float f = (float)(MathHelper.atan2(this.motionZ, this.motionX) * (double)(180F / (float)Math.PI)) - 90.0F;
         float f1 = MathHelper.wrapDegrees(f - this.rotationYaw);
         this.moveForward = 0.5F;
         this.rotationYaw += f1;
         if (this.rand.nextInt(100) == 0 && this.world.getBlockState(blockpos1).isNormalCube()) {
            this.setIsBatHanging(true);
         }
      }

   }

   protected boolean canTriggerWalking() {
      return false;
   }

   public void fall(float p_180430_1_, float p_180430_2_) {
   }

   protected void updateFallState(double p_184231_1_, boolean p_184231_3_, IBlockState p_184231_4_, BlockPos p_184231_5_) {
   }

   public boolean doesEntityNotTriggerPressurePlate() {
      return true;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         if (!this.world.isRemote && this.getIsBatHanging()) {
            this.setIsBatHanging(false);
         }

         return super.attackEntityFrom(p_70097_1_, p_70097_2_);
      }
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      this.dataManager.set(HANGING, p_70037_1_.getByte("BatFlags"));
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setByte("BatFlags", this.dataManager.get(HANGING));
   }

   public boolean func_205020_a(IWorld p_205020_1_, boolean p_205020_2_) {
      BlockPos blockpos = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);
      if (blockpos.getY() >= p_205020_1_.getSeaLevel()) {
         return false;
      } else {
         int i = p_205020_1_.getLight(blockpos);
         int j = 4;
         if (this.func_205021_dt()) {
            j = 7;
         } else if (this.rand.nextBoolean()) {
            return false;
         }

         return i <= this.rand.nextInt(j) && super.func_205020_a(p_205020_1_, p_205020_2_);
      }
   }

   private boolean func_205021_dt() {
      LocalDate localdate = LocalDate.now();
      int i = localdate.get(ChronoField.DAY_OF_MONTH);
      int j = localdate.get(ChronoField.MONTH_OF_YEAR);
      return j == 10 && i >= 20 || j == 11 && i <= 3;
   }

   public float getEyeHeight() {
      return this.height / 2.0F;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_BAT;
   }
}
