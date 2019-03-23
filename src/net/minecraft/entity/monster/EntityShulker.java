package net.minecraft.entity.monster;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityBodyHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityShulker extends EntityGolem implements IMob {
   private static final UUID COVERED_ARMOR_BONUS_ID = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F");
   private static final AttributeModifier COVERED_ARMOR_BONUS_MODIFIER = (new AttributeModifier(COVERED_ARMOR_BONUS_ID, "Covered armor bonus", 20.0D, 0)).setSaved(false);
   protected static final DataParameter<EnumFacing> ATTACHED_FACE = EntityDataManager.createKey(EntityShulker.class, DataSerializers.FACING);
   protected static final DataParameter<Optional<BlockPos>> ATTACHED_BLOCK_POS = EntityDataManager.createKey(EntityShulker.class, DataSerializers.OPTIONAL_BLOCK_POS);
   protected static final DataParameter<Byte> PEEK_TICK = EntityDataManager.createKey(EntityShulker.class, DataSerializers.BYTE);
   protected static final DataParameter<Byte> COLOR = EntityDataManager.createKey(EntityShulker.class, DataSerializers.BYTE);
   private float prevPeekAmount;
   private float peekAmount;
   private BlockPos currentAttachmentPosition;
   private int clientSideTeleportInterpolation;

   public EntityShulker(World p_i46779_1_) {
      super(EntityType.SHULKER, p_i46779_1_);
      this.setSize(1.0F, 1.0F);
      this.prevRenderYawOffset = 180.0F;
      this.renderYawOffset = 180.0F;
      this.isImmuneToFire = true;
      this.currentAttachmentPosition = null;
      this.experienceValue = 5;
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance p_204210_1_, @Nullable IEntityLivingData p_204210_2_, @Nullable NBTTagCompound p_204210_3_) {
      this.renderYawOffset = 180.0F;
      this.prevRenderYawOffset = 180.0F;
      this.rotationYaw = 180.0F;
      this.prevRotationYaw = 180.0F;
      this.rotationYawHead = 180.0F;
      this.prevRotationYawHead = 180.0F;
      return super.onInitialSpawn(p_204210_1_, p_204210_2_, p_204210_3_);
   }

   protected void initEntityAI() {
      this.tasks.addTask(1, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(4, new EntityShulker.AIAttack());
      this.tasks.addTask(7, new EntityShulker.AIPeek());
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
      this.targetTasks.addTask(2, new EntityShulker.AIAttackNearest(this));
      this.targetTasks.addTask(3, new EntityShulker.AIDefenseAttack(this));
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SHULKER_AMBIENT;
   }

   public void playAmbientSound() {
      if (!this.isClosed()) {
         super.playAmbientSound();
      }

   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SHULKER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return this.isClosed() ? SoundEvents.ENTITY_SHULKER_HURT_CLOSED : SoundEvents.ENTITY_SHULKER_HURT;
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(ATTACHED_FACE, EnumFacing.DOWN);
      this.dataManager.register(ATTACHED_BLOCK_POS, Optional.empty());
      this.dataManager.register(PEEK_TICK, (byte)0);
      this.dataManager.register(COLOR, (byte)16);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
   }

   protected EntityBodyHelper createBodyHelper() {
      return new EntityShulker.BodyHelper(this);
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      this.dataManager.set(ATTACHED_FACE, EnumFacing.byIndex(p_70037_1_.getByte("AttachFace")));
      this.dataManager.set(PEEK_TICK, p_70037_1_.getByte("Peek"));
      this.dataManager.set(COLOR, p_70037_1_.getByte("Color"));
      if (p_70037_1_.hasKey("APX")) {
         int i = p_70037_1_.getInteger("APX");
         int j = p_70037_1_.getInteger("APY");
         int k = p_70037_1_.getInteger("APZ");
         this.dataManager.set(ATTACHED_BLOCK_POS, Optional.of(new BlockPos(i, j, k)));
      } else {
         this.dataManager.set(ATTACHED_BLOCK_POS, Optional.empty());
      }

   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setByte("AttachFace", (byte)this.dataManager.get(ATTACHED_FACE).getIndex());
      p_70014_1_.setByte("Peek", this.dataManager.get(PEEK_TICK));
      p_70014_1_.setByte("Color", this.dataManager.get(COLOR));
      BlockPos blockpos = this.getAttachmentPos();
      if (blockpos != null) {
         p_70014_1_.setInteger("APX", blockpos.getX());
         p_70014_1_.setInteger("APY", blockpos.getY());
         p_70014_1_.setInteger("APZ", blockpos.getZ());
      }

   }

   public void tick() {
      super.tick();
      BlockPos blockpos = this.dataManager.get(ATTACHED_BLOCK_POS).orElse((BlockPos)null);
      if (blockpos == null && !this.world.isRemote) {
         blockpos = new BlockPos(this);
         this.dataManager.set(ATTACHED_BLOCK_POS, Optional.of(blockpos));
      }

      if (this.isRiding()) {
         blockpos = null;
         float f = this.getRidingEntity().rotationYaw;
         this.rotationYaw = f;
         this.renderYawOffset = f;
         this.prevRenderYawOffset = f;
         this.clientSideTeleportInterpolation = 0;
      } else if (!this.world.isRemote) {
         IBlockState iblockstate = this.world.getBlockState(blockpos);
         if (!iblockstate.isAir()) {
            if (iblockstate.getBlock() == Blocks.MOVING_PISTON) {
               EnumFacing enumfacing = iblockstate.get(BlockPistonBase.FACING);
               if (this.world.isAirBlock(blockpos.offset(enumfacing))) {
                  blockpos = blockpos.offset(enumfacing);
                  this.dataManager.set(ATTACHED_BLOCK_POS, Optional.of(blockpos));
               } else {
                  this.tryTeleportToNewPosition();
               }
            } else if (iblockstate.getBlock() == Blocks.PISTON_HEAD) {
               EnumFacing enumfacing3 = iblockstate.get(BlockPistonExtension.FACING);
               if (this.world.isAirBlock(blockpos.offset(enumfacing3))) {
                  blockpos = blockpos.offset(enumfacing3);
                  this.dataManager.set(ATTACHED_BLOCK_POS, Optional.of(blockpos));
               } else {
                  this.tryTeleportToNewPosition();
               }
            } else {
               this.tryTeleportToNewPosition();
            }
         }

         BlockPos blockpos1 = blockpos.offset(this.getAttachmentFacing());
         if (!this.world.isTopSolid(blockpos1)) {
            boolean flag = false;

            for(EnumFacing enumfacing1 : EnumFacing.values()) {
               blockpos1 = blockpos.offset(enumfacing1);
               if (this.world.isTopSolid(blockpos1)) {
                  this.dataManager.set(ATTACHED_FACE, enumfacing1);
                  flag = true;
                  break;
               }
            }

            if (!flag) {
               this.tryTeleportToNewPosition();
            }
         }

         BlockPos blockpos2 = blockpos.offset(this.getAttachmentFacing().getOpposite());
         if (this.world.isTopSolid(blockpos2)) {
            this.tryTeleportToNewPosition();
         }
      }

      float f1 = (float)this.getPeekTick() * 0.01F;
      this.prevPeekAmount = this.peekAmount;
      if (this.peekAmount > f1) {
         this.peekAmount = MathHelper.clamp(this.peekAmount - 0.05F, f1, 1.0F);
      } else if (this.peekAmount < f1) {
         this.peekAmount = MathHelper.clamp(this.peekAmount + 0.05F, 0.0F, f1);
      }

      if (blockpos != null) {
         if (this.world.isRemote) {
            if (this.clientSideTeleportInterpolation > 0 && this.currentAttachmentPosition != null) {
               --this.clientSideTeleportInterpolation;
            } else {
               this.currentAttachmentPosition = blockpos;
            }
         }

         this.posX = (double)blockpos.getX() + 0.5D;
         this.posY = (double)blockpos.getY();
         this.posZ = (double)blockpos.getZ() + 0.5D;
         this.prevPosX = this.posX;
         this.prevPosY = this.posY;
         this.prevPosZ = this.posZ;
         this.lastTickPosX = this.posX;
         this.lastTickPosY = this.posY;
         this.lastTickPosZ = this.posZ;
         double d3 = 0.5D - (double)MathHelper.sin((0.5F + this.peekAmount) * (float)Math.PI) * 0.5D;
         double d4 = 0.5D - (double)MathHelper.sin((0.5F + this.prevPeekAmount) * (float)Math.PI) * 0.5D;
         double d5 = d3 - d4;
         double d0 = 0.0D;
         double d1 = 0.0D;
         double d2 = 0.0D;
         EnumFacing enumfacing2 = this.getAttachmentFacing();
         switch(enumfacing2) {
         case DOWN:
            this.setEntityBoundingBox(new AxisAlignedBB(this.posX - 0.5D, this.posY, this.posZ - 0.5D, this.posX + 0.5D, this.posY + 1.0D + d3, this.posZ + 0.5D));
            d1 = d5;
            break;
         case UP:
            this.setEntityBoundingBox(new AxisAlignedBB(this.posX - 0.5D, this.posY - d3, this.posZ - 0.5D, this.posX + 0.5D, this.posY + 1.0D, this.posZ + 0.5D));
            d1 = -d5;
            break;
         case NORTH:
            this.setEntityBoundingBox(new AxisAlignedBB(this.posX - 0.5D, this.posY, this.posZ - 0.5D, this.posX + 0.5D, this.posY + 1.0D, this.posZ + 0.5D + d3));
            d2 = d5;
            break;
         case SOUTH:
            this.setEntityBoundingBox(new AxisAlignedBB(this.posX - 0.5D, this.posY, this.posZ - 0.5D - d3, this.posX + 0.5D, this.posY + 1.0D, this.posZ + 0.5D));
            d2 = -d5;
            break;
         case WEST:
            this.setEntityBoundingBox(new AxisAlignedBB(this.posX - 0.5D, this.posY, this.posZ - 0.5D, this.posX + 0.5D + d3, this.posY + 1.0D, this.posZ + 0.5D));
            d0 = d5;
            break;
         case EAST:
            this.setEntityBoundingBox(new AxisAlignedBB(this.posX - 0.5D - d3, this.posY, this.posZ - 0.5D, this.posX + 0.5D, this.posY + 1.0D, this.posZ + 0.5D));
            d0 = -d5;
         }

         if (d5 > 0.0D) {
            List<Entity> list = this.world.func_72839_b(this, this.getEntityBoundingBox());
            if (!list.isEmpty()) {
               for(Entity entity : list) {
                  if (!(entity instanceof EntityShulker) && !entity.noClip) {
                     entity.move(MoverType.SHULKER, d0, d1, d2);
                  }
               }
            }
         }
      }

   }

   public void move(MoverType p_70091_1_, double p_70091_2_, double p_70091_4_, double p_70091_6_) {
      if (p_70091_1_ == MoverType.SHULKER_BOX) {
         this.tryTeleportToNewPosition();
      } else {
         super.move(p_70091_1_, p_70091_2_, p_70091_4_, p_70091_6_);
      }

   }

   public void setPosition(double p_70107_1_, double p_70107_3_, double p_70107_5_) {
      super.setPosition(p_70107_1_, p_70107_3_, p_70107_5_);
      if (this.dataManager != null && this.ticksExisted != 0) {
         Optional<BlockPos> optional = this.dataManager.get(ATTACHED_BLOCK_POS);
         Optional<BlockPos> optional1 = Optional.of(new BlockPos(p_70107_1_, p_70107_3_, p_70107_5_));
         if (!optional1.equals(optional)) {
            this.dataManager.set(ATTACHED_BLOCK_POS, optional1);
            this.dataManager.set(PEEK_TICK, (byte)0);
            this.isAirBorne = true;
         }

      }
   }

   protected boolean tryTeleportToNewPosition() {
      if (!this.isAIDisabled() && this.isEntityAlive()) {
         BlockPos blockpos = new BlockPos(this);

         for(int i = 0; i < 5; ++i) {
            BlockPos blockpos1 = blockpos.add(8 - this.rand.nextInt(17), 8 - this.rand.nextInt(17), 8 - this.rand.nextInt(17));
            if (blockpos1.getY() > 0 && this.world.isAirBlock(blockpos1) && this.world.isInsideWorldBorder(this) && this.world.isCollisionBoxesEmpty(this, new AxisAlignedBB(blockpos1))) {
               boolean flag = false;

               for(EnumFacing enumfacing : EnumFacing.values()) {
                  if (this.world.isTopSolid(blockpos1.offset(enumfacing))) {
                     this.dataManager.set(ATTACHED_FACE, enumfacing);
                     flag = true;
                     break;
                  }
               }

               if (flag) {
                  this.playSound(SoundEvents.ENTITY_SHULKER_TELEPORT, 1.0F, 1.0F);
                  this.dataManager.set(ATTACHED_BLOCK_POS, Optional.of(blockpos1));
                  this.dataManager.set(PEEK_TICK, (byte)0);
                  this.setAttackTarget((EntityLivingBase)null);
                  return true;
               }
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public void livingTick() {
      super.livingTick();
      this.motionX = 0.0D;
      this.motionY = 0.0D;
      this.motionZ = 0.0D;
      this.prevRenderYawOffset = 180.0F;
      this.renderYawOffset = 180.0F;
      this.rotationYaw = 180.0F;
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      if (ATTACHED_BLOCK_POS.equals(p_184206_1_) && this.world.isRemote && !this.isRiding()) {
         BlockPos blockpos = this.getAttachmentPos();
         if (blockpos != null) {
            if (this.currentAttachmentPosition == null) {
               this.currentAttachmentPosition = blockpos;
            } else {
               this.clientSideTeleportInterpolation = 6;
            }

            this.posX = (double)blockpos.getX() + 0.5D;
            this.posY = (double)blockpos.getY();
            this.posZ = (double)blockpos.getZ() + 0.5D;
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.lastTickPosX = this.posX;
            this.lastTickPosY = this.posY;
            this.lastTickPosZ = this.posZ;
         }
      }

      super.notifyDataManagerChange(p_184206_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void setPositionAndRotationDirect(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_, boolean p_180426_10_) {
      this.newPosRotationIncrements = 0;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isClosed()) {
         Entity entity = p_70097_1_.getImmediateSource();
         if (entity instanceof EntityArrow) {
            return false;
         }
      }

      if (super.attackEntityFrom(p_70097_1_, p_70097_2_)) {
         if ((double)this.getHealth() < (double)this.getMaxHealth() * 0.5D && this.rand.nextInt(4) == 0) {
            this.tryTeleportToNewPosition();
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean isClosed() {
      return this.getPeekTick() == 0;
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox() {
      return this.isEntityAlive() ? this.getEntityBoundingBox() : null;
   }

   public EnumFacing getAttachmentFacing() {
      return this.dataManager.get(ATTACHED_FACE);
   }

   @Nullable
   public BlockPos getAttachmentPos() {
      return this.dataManager.get(ATTACHED_BLOCK_POS).orElse((BlockPos)null);
   }

   public void setAttachmentPos(@Nullable BlockPos p_184694_1_) {
      this.dataManager.set(ATTACHED_BLOCK_POS, Optional.ofNullable(p_184694_1_));
   }

   public int getPeekTick() {
      return this.dataManager.get(PEEK_TICK);
   }

   public void updateArmorModifier(int p_184691_1_) {
      if (!this.world.isRemote) {
         this.getAttribute(SharedMonsterAttributes.ARMOR).removeModifier(COVERED_ARMOR_BONUS_MODIFIER);
         if (p_184691_1_ == 0) {
            this.getAttribute(SharedMonsterAttributes.ARMOR).applyModifier(COVERED_ARMOR_BONUS_MODIFIER);
            this.playSound(SoundEvents.ENTITY_SHULKER_CLOSE, 1.0F, 1.0F);
         } else {
            this.playSound(SoundEvents.ENTITY_SHULKER_OPEN, 1.0F, 1.0F);
         }
      }

      this.dataManager.set(PEEK_TICK, (byte)p_184691_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public float getClientPeekAmount(float p_184688_1_) {
      return this.prevPeekAmount + (this.peekAmount - this.prevPeekAmount) * p_184688_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public int getClientTeleportInterp() {
      return this.clientSideTeleportInterpolation;
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getOldAttachPos() {
      return this.currentAttachmentPosition;
   }

   public float getEyeHeight() {
      return 0.5F;
   }

   public int getVerticalFaceSpeed() {
      return 180;
   }

   public int getHorizontalFaceSpeed() {
      return 180;
   }

   public void applyEntityCollision(Entity p_70108_1_) {
   }

   public float getCollisionBorderSize() {
      return 0.0F;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isAttachedToBlock() {
      return this.currentAttachmentPosition != null && this.getAttachmentPos() != null;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_SHULKER;
   }

   @OnlyIn(Dist.CLIENT)
   public EnumDyeColor getColor() {
      Byte obyte = this.dataManager.get(COLOR);
      return obyte != 16 && obyte <= 15 ? EnumDyeColor.byId(obyte) : null;
   }

   class AIAttack extends EntityAIBase {
      private int attackTime;

      public AIAttack() {
         this.setMutexBits(3);
      }

      public boolean shouldExecute() {
         EntityLivingBase entitylivingbase = EntityShulker.this.getAttackTarget();
         if (entitylivingbase != null && entitylivingbase.isEntityAlive()) {
            return EntityShulker.this.world.getDifficulty() != EnumDifficulty.PEACEFUL;
         } else {
            return false;
         }
      }

      public void startExecuting() {
         this.attackTime = 20;
         EntityShulker.this.updateArmorModifier(100);
      }

      public void resetTask() {
         EntityShulker.this.updateArmorModifier(0);
      }

      public void updateTask() {
         if (EntityShulker.this.world.getDifficulty() != EnumDifficulty.PEACEFUL) {
            --this.attackTime;
            EntityLivingBase entitylivingbase = EntityShulker.this.getAttackTarget();
            EntityShulker.this.getLookHelper().setLookPositionWithEntity(entitylivingbase, 180.0F, 180.0F);
            double d0 = EntityShulker.this.getDistanceSq(entitylivingbase);
            if (d0 < 400.0D) {
               if (this.attackTime <= 0) {
                  this.attackTime = 20 + EntityShulker.this.rand.nextInt(10) * 20 / 2;
                  EntityShulkerBullet entityshulkerbullet = new EntityShulkerBullet(EntityShulker.this.world, EntityShulker.this, entitylivingbase, EntityShulker.this.getAttachmentFacing().getAxis());
                  EntityShulker.this.world.spawnEntity(entityshulkerbullet);
                  EntityShulker.this.playSound(SoundEvents.ENTITY_SHULKER_SHOOT, 2.0F, (EntityShulker.this.rand.nextFloat() - EntityShulker.this.rand.nextFloat()) * 0.2F + 1.0F);
               }
            } else {
               EntityShulker.this.setAttackTarget((EntityLivingBase)null);
            }

            super.updateTask();
         }
      }
   }

   class AIAttackNearest extends EntityAINearestAttackableTarget<EntityPlayer> {
      public AIAttackNearest(EntityShulker p_i47060_2_) {
         super(p_i47060_2_, EntityPlayer.class, true);
      }

      public boolean shouldExecute() {
         return EntityShulker.this.world.getDifficulty() == EnumDifficulty.PEACEFUL ? false : super.shouldExecute();
      }

      protected AxisAlignedBB getTargetableArea(double p_188511_1_) {
         EnumFacing enumfacing = ((EntityShulker)this.taskOwner).getAttachmentFacing();
         if (enumfacing.getAxis() == EnumFacing.Axis.X) {
            return this.taskOwner.getEntityBoundingBox().grow(4.0D, p_188511_1_, p_188511_1_);
         } else {
            return enumfacing.getAxis() == EnumFacing.Axis.Z ? this.taskOwner.getEntityBoundingBox().grow(p_188511_1_, p_188511_1_, 4.0D) : this.taskOwner.getEntityBoundingBox().grow(p_188511_1_, 4.0D, p_188511_1_);
         }
      }
   }

   static class AIDefenseAttack extends EntityAINearestAttackableTarget<EntityLivingBase> {
      public AIDefenseAttack(EntityShulker p_i47061_1_) {
         super(p_i47061_1_, EntityLivingBase.class, 10, true, false, (p_200826_0_) -> {
            return p_200826_0_ instanceof IMob;
         });
      }

      public boolean shouldExecute() {
         return this.taskOwner.getTeam() == null ? false : super.shouldExecute();
      }

      protected AxisAlignedBB getTargetableArea(double p_188511_1_) {
         EnumFacing enumfacing = ((EntityShulker)this.taskOwner).getAttachmentFacing();
         if (enumfacing.getAxis() == EnumFacing.Axis.X) {
            return this.taskOwner.getEntityBoundingBox().grow(4.0D, p_188511_1_, p_188511_1_);
         } else {
            return enumfacing.getAxis() == EnumFacing.Axis.Z ? this.taskOwner.getEntityBoundingBox().grow(p_188511_1_, p_188511_1_, 4.0D) : this.taskOwner.getEntityBoundingBox().grow(p_188511_1_, 4.0D, p_188511_1_);
         }
      }
   }

   class AIPeek extends EntityAIBase {
      private int peekTime;

      private AIPeek() {
      }

      public boolean shouldExecute() {
         return EntityShulker.this.getAttackTarget() == null && EntityShulker.this.rand.nextInt(40) == 0;
      }

      public boolean shouldContinueExecuting() {
         return EntityShulker.this.getAttackTarget() == null && this.peekTime > 0;
      }

      public void startExecuting() {
         this.peekTime = 20 * (1 + EntityShulker.this.rand.nextInt(3));
         EntityShulker.this.updateArmorModifier(30);
      }

      public void resetTask() {
         if (EntityShulker.this.getAttackTarget() == null) {
            EntityShulker.this.updateArmorModifier(0);
         }

      }

      public void updateTask() {
         --this.peekTime;
      }
   }

   class BodyHelper extends EntityBodyHelper {
      public BodyHelper(EntityLivingBase p_i47062_2_) {
         super(p_i47062_2_);
      }

      public void updateRenderAngles() {
      }
   }
}
