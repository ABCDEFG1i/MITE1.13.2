package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntitySnowman extends EntityGolem implements IRangedAttackMob {
   private static final DataParameter<Byte> PUMPKIN_EQUIPPED = EntityDataManager.createKey(EntitySnowman.class, DataSerializers.BYTE);

   public EntitySnowman(World p_i1692_1_) {
      super(EntityType.SNOW_GOLEM, p_i1692_1_);
      this.setSize(0.7F, 1.9F);
   }

   protected void initEntityAI() {
      this.tasks.addTask(1, new EntityAIAttackRanged(this, 1.25D, 20, 10.0F));
      this.tasks.addTask(2, new EntityAIWanderAvoidWater(this, 1.0D, 1.0000001E-5F));
      this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(4, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityLiving.class, 10, true, false, IMob.MOB_SELECTOR));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.2F);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(PUMPKIN_EQUIPPED, (byte)16);
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setBoolean("Pumpkin", this.isPumpkinEquipped());
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      if (p_70037_1_.hasKey("Pumpkin")) {
         this.setPumpkinEquipped(p_70037_1_.getBoolean("Pumpkin"));
      }

   }

   public void livingTick() {
      super.livingTick();
      if (!this.world.isRemote) {
         int i = MathHelper.floor(this.posX);
         int j = MathHelper.floor(this.posY);
         int k = MathHelper.floor(this.posZ);
         if (this.isInWaterRainOrBubbleColumn()) {
            this.attackEntityFrom(DamageSource.DROWN, 1.0F);
         }

         if (this.world.getBiome(new BlockPos(i, 0, k)).getTemperature(new BlockPos(i, j, k)) > 1.0F) {
            this.attackEntityFrom(DamageSource.ON_FIRE, 1.0F);
         }

         if (!this.world.getGameRules().getBoolean("mobGriefing")) {
            return;
         }

         IBlockState iblockstate = Blocks.SNOW.getDefaultState();

         for(int l = 0; l < 4; ++l) {
            i = MathHelper.floor(this.posX + (double)((float)(l % 2 * 2 - 1) * 0.25F));
            j = MathHelper.floor(this.posY);
            k = MathHelper.floor(this.posZ + (double)((float)(l / 2 % 2 * 2 - 1) * 0.25F));
            BlockPos blockpos = new BlockPos(i, j, k);
            if (this.world.getBlockState(blockpos).isAir() && this.world.getBiome(blockpos).getTemperature(blockpos) < 0.8F && iblockstate.isValidPosition(this.world, blockpos)) {
               this.world.setBlockState(blockpos, iblockstate);
            }
         }
      }

   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_SNOWMAN;
   }

   public void attackEntityWithRangedAttack(EntityLivingBase p_82196_1_, float p_82196_2_) {
      EntitySnowball entitysnowball = new EntitySnowball(this.world, this);
      double d0 = p_82196_1_.posY + (double)p_82196_1_.getEyeHeight() - (double)1.1F;
      double d1 = p_82196_1_.posX - this.posX;
      double d2 = d0 - entitysnowball.posY;
      double d3 = p_82196_1_.posZ - this.posZ;
      float f = MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.2F;
      entitysnowball.shoot(d1, d2 + (double)f, d3, 1.6F, 12.0F);
      this.playSound(SoundEvents.ENTITY_SNOW_GOLEM_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
      this.world.spawnEntity(entitysnowball);
   }

   public float getEyeHeight() {
      return 1.7F;
   }

   protected boolean processInteract(EntityPlayer p_184645_1_, EnumHand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getHeldItem(p_184645_2_);
      if (itemstack.getItem() == Items.SHEARS && this.isPumpkinEquipped() && !this.world.isRemote) {
         this.setPumpkinEquipped(false);
         itemstack.damageItem(1, p_184645_1_);
      }

      return super.processInteract(p_184645_1_, p_184645_2_);
   }

   public boolean isPumpkinEquipped() {
      return (this.dataManager.get(PUMPKIN_EQUIPPED) & 16) != 0;
   }

   public void setPumpkinEquipped(boolean p_184747_1_) {
      byte b0 = this.dataManager.get(PUMPKIN_EQUIPPED);
      if (p_184747_1_) {
         this.dataManager.set(PUMPKIN_EQUIPPED, (byte)(b0 | 16));
      } else {
         this.dataManager.set(PUMPKIN_EQUIPPED, (byte)(b0 & -17));
      }

   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SNOW_GOLEM_AMBIENT;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_SNOW_GOLEM_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SNOW_GOLEM_DEATH;
   }

   public void setSwingingArms(boolean p_184724_1_) {
   }
}
