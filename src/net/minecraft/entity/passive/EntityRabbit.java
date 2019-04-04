package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCarrot;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityRabbit extends EntityAnimal {
   private static final DataParameter<Integer> RABBIT_TYPE = EntityDataManager.createKey(EntityRabbit.class, DataSerializers.VARINT);
   private static final ResourceLocation field_200611_bx = new ResourceLocation("killer_bunny");
   private int jumpTicks;
   private int jumpDuration;
   private boolean wasOnGround;
   private int currentMoveTypeDuration;
   private int carrotTicks;

   public EntityRabbit(World p_i45869_1_) {
      super(EntityType.RABBIT, p_i45869_1_);
      this.setSize(0.4F, 0.5F);
      this.jumpHelper = new EntityRabbit.RabbitJumpHelper(this);
      this.moveHelper = new EntityRabbit.RabbitMoveHelper(this);
      this.setMovementSpeed(0.0D);
   }

   protected void initEntityAI() {
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityRabbit.AIPanic(this, 2.2D));
      this.tasks.addTask(2, new EntityAIMate(this, 0.8D));
      this.tasks.addTask(3, new EntityAITempt(this, 1.0D, Ingredient.fromItems(Items.CARROT, Items.GOLDEN_CARROT, Blocks.DANDELION), false));
      this.tasks.addTask(4, new EntityRabbit.AIAvoidEntity<>(this, EntityPlayer.class, 8.0F, 2.2D, 2.2D));
      this.tasks.addTask(4, new EntityRabbit.AIAvoidEntity<>(this, EntityWolf.class, 10.0F, 2.2D, 2.2D));
      this.tasks.addTask(4, new EntityRabbit.AIAvoidEntity<>(this, EntityMob.class, 4.0F, 2.2D, 2.2D));
      this.tasks.addTask(5, new EntityRabbit.AIRaidFarm(this));
      this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 0.6D));
      this.tasks.addTask(11, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
   }

   protected float getJumpUpwardsMotion() {
      if (!this.collidedHorizontally && (!this.moveHelper.isUpdating() || !(this.moveHelper.getY() > this.posY + 0.5D))) {
         Path path = this.navigator.getPath();
         if (path != null && path.getCurrentPathIndex() < path.getCurrentPathLength()) {
            Vec3d vec3d = path.getPosition(this);
            if (vec3d.y > this.posY + 0.5D) {
               return 0.5F;
            }
         }

         return this.moveHelper.getSpeed() <= 0.6D ? 0.2F : 0.3F;
      } else {
         return 0.5F;
      }
   }

   protected void jump() {
      super.jump();
      double d0 = this.moveHelper.getSpeed();
      if (d0 > 0.0D) {
         double d1 = this.motionX * this.motionX + this.motionZ * this.motionZ;
         if (d1 < 0.010000000000000002D) {
            this.moveRelative(0.0F, 0.0F, 1.0F, 0.1F);
         }
      }

      if (!this.world.isRemote) {
         this.world.setEntityState(this, (byte)1);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float getJumpCompletion(float p_175521_1_) {
      return this.jumpDuration == 0 ? 0.0F : ((float)this.jumpTicks + p_175521_1_) / (float)this.jumpDuration;
   }

   public void setMovementSpeed(double p_175515_1_) {
      this.getNavigator().setSpeed(p_175515_1_);
      this.moveHelper.setMoveTo(this.moveHelper.getX(), this.moveHelper.getY(), this.moveHelper.getZ(), p_175515_1_);
   }

   public void setJumping(boolean p_70637_1_) {
      super.setJumping(p_70637_1_);
      if (p_70637_1_) {
         this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
      }

   }

   public void startJumping() {
      this.setJumping(true);
      this.jumpDuration = 10;
      this.jumpTicks = 0;
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(RABBIT_TYPE, 0);
   }

   public void updateAITasks() {
      if (this.currentMoveTypeDuration > 0) {
         --this.currentMoveTypeDuration;
      }

      if (this.carrotTicks > 0) {
         this.carrotTicks -= this.rand.nextInt(3);
         if (this.carrotTicks < 0) {
            this.carrotTicks = 0;
         }
      }

      if (this.onGround) {
         if (!this.wasOnGround) {
            this.setJumping(false);
            this.checkLandingDelay();
         }

         if (this.getRabbitType() == 99 && this.currentMoveTypeDuration == 0) {
            EntityLivingBase entitylivingbase = this.getAttackTarget();
            if (entitylivingbase != null && this.getDistanceSq(entitylivingbase) < 16.0D) {
               this.calculateRotationYaw(entitylivingbase.posX, entitylivingbase.posZ);
               this.moveHelper.setMoveTo(entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, this.moveHelper.getSpeed());
               this.startJumping();
               this.wasOnGround = true;
            }
         }

         EntityRabbit.RabbitJumpHelper entityrabbit$rabbitjumphelper = (EntityRabbit.RabbitJumpHelper)this.jumpHelper;
         if (!entityrabbit$rabbitjumphelper.getIsJumping()) {
            if (this.moveHelper.isUpdating() && this.currentMoveTypeDuration == 0) {
               Path path = this.navigator.getPath();
               Vec3d vec3d = new Vec3d(this.moveHelper.getX(), this.moveHelper.getY(), this.moveHelper.getZ());
               if (path != null && path.getCurrentPathIndex() < path.getCurrentPathLength()) {
                  vec3d = path.getPosition(this);
               }

               this.calculateRotationYaw(vec3d.x, vec3d.z);
               this.startJumping();
            }
         } else if (!entityrabbit$rabbitjumphelper.canJump()) {
            this.enableJumpControl();
         }
      }

      this.wasOnGround = this.onGround;
   }

   public void spawnRunningParticles() {
   }

   private void calculateRotationYaw(double p_175533_1_, double p_175533_3_) {
      this.rotationYaw = (float)(MathHelper.atan2(p_175533_3_ - this.posZ, p_175533_1_ - this.posX) * (double)(180F / (float)Math.PI)) - 90.0F;
   }

   private void enableJumpControl() {
      ((EntityRabbit.RabbitJumpHelper)this.jumpHelper).setCanJump(true);
   }

   private void disableJumpControl() {
      ((EntityRabbit.RabbitJumpHelper)this.jumpHelper).setCanJump(false);
   }

   private void updateMoveTypeDuration() {
      if (this.moveHelper.getSpeed() < 2.2D) {
         this.currentMoveTypeDuration = 10;
      } else {
         this.currentMoveTypeDuration = 1;
      }

   }

   private void checkLandingDelay() {
      this.updateMoveTypeDuration();
      this.disableJumpControl();
   }

   public void livingTick() {
      super.livingTick();
      if (this.jumpTicks != this.jumpDuration) {
         ++this.jumpTicks;
      } else if (this.jumpDuration != 0) {
         this.jumpTicks = 0;
         this.jumpDuration = 0;
         this.setJumping(false);
      }

   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(3.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.3F);
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setInteger("RabbitType", this.getRabbitType());
      p_70014_1_.setInteger("MoreCarrotTicks", this.carrotTicks);
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      this.setRabbitType(p_70037_1_.getInteger("RabbitType"));
      this.carrotTicks = p_70037_1_.getInteger("MoreCarrotTicks");
   }

   protected SoundEvent getJumpSound() {
      return SoundEvents.ENTITY_RABBIT_JUMP;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_RABBIT_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_RABBIT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_RABBIT_DEATH;
   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      if (this.getRabbitType() == 99) {
         this.playSound(SoundEvents.ENTITY_RABBIT_ATTACK, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         return p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), 8.0F);
      } else {
         return p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
      }
   }

   public SoundCategory getSoundCategory() {
      return this.getRabbitType() == 99 ? SoundCategory.HOSTILE : SoundCategory.NEUTRAL;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      return !this.isInvulnerableTo(p_70097_1_) && super.attackEntityFrom(p_70097_1_, p_70097_2_);
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_RABBIT;
   }

   private boolean isRabbitBreedingItem(Item p_175525_1_) {
      return p_175525_1_ == Items.CARROT || p_175525_1_ == Items.GOLDEN_CARROT || p_175525_1_ == Blocks.DANDELION.asItem();
   }

   public EntityRabbit createChild(EntityAgeable p_90011_1_) {
      EntityRabbit entityrabbit = new EntityRabbit(this.world);
      int i = this.getRandomRabbitType();
      if (this.rand.nextInt(20) != 0) {
         if (p_90011_1_ instanceof EntityRabbit && this.rand.nextBoolean()) {
            i = ((EntityRabbit)p_90011_1_).getRabbitType();
         } else {
            i = this.getRabbitType();
         }
      }

      entityrabbit.setRabbitType(i);
      return entityrabbit;
   }

   public boolean isBreedingItem(ItemStack p_70877_1_) {
      return this.isRabbitBreedingItem(p_70877_1_.getItem());
   }

   public int getRabbitType() {
      return this.dataManager.get(RABBIT_TYPE);
   }

   public void setRabbitType(int p_175529_1_) {
      if (p_175529_1_ == 99) {
         this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(8.0D);
         this.tasks.addTask(4, new EntityRabbit.AIEvilAttack(this));
         this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
         this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
         this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityWolf.class, true));
         if (!this.hasCustomName()) {
            this.setCustomName(new TextComponentTranslation(Util.makeTranslationKey("entity", field_200611_bx)));
         }
      }

      this.dataManager.set(RABBIT_TYPE, p_175529_1_);
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance p_204210_1_, @Nullable IEntityLivingData p_204210_2_, @Nullable NBTTagCompound p_204210_3_) {
      p_204210_2_ = super.onInitialSpawn(p_204210_1_, p_204210_2_, p_204210_3_);
      int i = this.getRandomRabbitType();
      boolean flag = false;
      if (p_204210_2_ instanceof EntityRabbit.RabbitTypeData) {
         i = ((EntityRabbit.RabbitTypeData)p_204210_2_).typeData;
         flag = true;
      } else {
         p_204210_2_ = new EntityRabbit.RabbitTypeData(i);
      }

      this.setRabbitType(i);
      if (flag) {
         this.setGrowingAge(-24000);
      }

      return p_204210_2_;
   }

   private int getRandomRabbitType() {
      Biome biome = this.world.getBiome(new BlockPos(this));
      int i = this.rand.nextInt(100);
      if (biome.getPrecipitation() == Biome.RainType.SNOW) {
         return i < 80 ? 1 : 3;
      } else if (biome.getBiomeCategory() == Biome.Category.DESERT) {
         return 4;
      } else {
         return i < 50 ? 0 : (i < 90 ? 5 : 2);
      }
   }

   public boolean func_205020_a(IWorld p_205020_1_, boolean p_205020_2_) {
      int i = MathHelper.floor(this.posX);
      int j = MathHelper.floor(this.getEntityBoundingBox().minY);
      int k = MathHelper.floor(this.posZ);
      BlockPos blockpos = new BlockPos(i, j, k);
      Block block = p_205020_1_.getBlockState(blockpos.down()).getBlock();
      return block == Blocks.GRASS || block == Blocks.SNOW || block == Blocks.SAND || super.func_205020_a(p_205020_1_,
              p_205020_2_);
   }

   private boolean isCarrotEaten() {
      return this.carrotTicks == 0;
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 1) {
         this.createRunningParticles();
         this.jumpDuration = 10;
         this.jumpTicks = 0;
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   static class AIAvoidEntity<T extends Entity> extends EntityAIAvoidEntity<T> {
      private final EntityRabbit rabbit;

      public AIAvoidEntity(EntityRabbit p_i46403_1_, Class<T> p_i46403_2_, float p_i46403_3_, double p_i46403_4_, double p_i46403_6_) {
         super(p_i46403_1_, p_i46403_2_, p_i46403_3_, p_i46403_4_, p_i46403_6_);
         this.rabbit = p_i46403_1_;
      }

      public boolean shouldExecute() {
         return this.rabbit.getRabbitType() != 99 && super.shouldExecute();
      }
   }

   static class AIEvilAttack extends EntityAIAttackMelee {
      public AIEvilAttack(EntityRabbit p_i45867_1_) {
         super(p_i45867_1_, 1.4D, true);
      }

      protected double getAttackReachSqr(EntityLivingBase p_179512_1_) {
         return (double)(4.0F + p_179512_1_.width);
      }
   }

   static class AIPanic extends EntityAIPanic {
      private final EntityRabbit rabbit;

      public AIPanic(EntityRabbit p_i45861_1_, double p_i45861_2_) {
         super(p_i45861_1_, p_i45861_2_);
         this.rabbit = p_i45861_1_;
      }

      public void updateTask() {
         super.updateTask();
         this.rabbit.setMovementSpeed(this.speed);
      }
   }

   static class AIRaidFarm extends EntityAIMoveToBlock {
      private final EntityRabbit rabbit;
      private boolean wantsToRaid;
      private boolean canRaid;

      public AIRaidFarm(EntityRabbit p_i45860_1_) {
         super(p_i45860_1_, (double)0.7F, 16);
         this.rabbit = p_i45860_1_;
      }

      public boolean shouldExecute() {
         if (this.runDelay <= 0) {
            if (!this.rabbit.world.getGameRules().getBoolean("mobGriefing")) {
               return false;
            }

            this.canRaid = false;
            this.wantsToRaid = this.rabbit.isCarrotEaten();
            this.wantsToRaid = true;
         }

         return super.shouldExecute();
      }

      public boolean shouldContinueExecuting() {
         return this.canRaid && super.shouldContinueExecuting();
      }

      public void updateTask() {
         super.updateTask();
         this.rabbit.getLookHelper().setLookPosition((double)this.destinationBlock.getX() + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)this.destinationBlock.getZ() + 0.5D, 10.0F, (float)this.rabbit.getVerticalFaceSpeed());
         if (this.getIsAboveDestination()) {
            World world = this.rabbit.world;
            BlockPos blockpos = this.destinationBlock.up();
            IBlockState iblockstate = world.getBlockState(blockpos);
            Block block = iblockstate.getBlock();
            if (this.canRaid && block instanceof BlockCarrot) {
               Integer integer = iblockstate.get(BlockCarrot.AGE);
               if (integer == 0) {
                  world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 2);
                  world.destroyBlock(blockpos, true);
               } else {
                  world.setBlockState(blockpos, iblockstate.with(BlockCarrot.AGE, Integer.valueOf(integer - 1)), 2);
                  world.playEvent(2001, blockpos, Block.getStateId(iblockstate));
               }

               this.rabbit.carrotTicks = 40;
            }

            this.canRaid = false;
            this.runDelay = 10;
         }

      }

      protected boolean shouldMoveTo(IWorldReaderBase p_179488_1_, BlockPos p_179488_2_) {
         Block block = p_179488_1_.getBlockState(p_179488_2_).getBlock();
         if (block == Blocks.FARMLAND && this.wantsToRaid && !this.canRaid) {
            p_179488_2_ = p_179488_2_.up();
            IBlockState iblockstate = p_179488_1_.getBlockState(p_179488_2_);
            block = iblockstate.getBlock();
            if (block instanceof BlockCarrot && ((BlockCarrot)block).isMaxAge(iblockstate)) {
               this.canRaid = true;
               return true;
            }
         }

         return false;
      }
   }

   public class RabbitJumpHelper extends EntityJumpHelper {
      private final EntityRabbit rabbit;
      private boolean canJump;

      public RabbitJumpHelper(EntityRabbit p_i45863_2_) {
         super(p_i45863_2_);
         this.rabbit = p_i45863_2_;
      }

      public boolean getIsJumping() {
         return this.isJumping;
      }

      public boolean canJump() {
         return this.canJump;
      }

      public void setCanJump(boolean p_180066_1_) {
         this.canJump = p_180066_1_;
      }

      public void tick() {
         if (this.isJumping) {
            this.rabbit.startJumping();
            this.isJumping = false;
         }

      }
   }

   static class RabbitMoveHelper extends EntityMoveHelper {
      private final EntityRabbit rabbit;
      private double nextJumpSpeed;

      public RabbitMoveHelper(EntityRabbit p_i45862_1_) {
         super(p_i45862_1_);
         this.rabbit = p_i45862_1_;
      }

      public void tick() {
         if (this.rabbit.onGround && !this.rabbit.isJumping && !((EntityRabbit.RabbitJumpHelper)this.rabbit.jumpHelper).getIsJumping()) {
            this.rabbit.setMovementSpeed(0.0D);
         } else if (this.isUpdating()) {
            this.rabbit.setMovementSpeed(this.nextJumpSpeed);
         }

         super.tick();
      }

      public void setMoveTo(double p_75642_1_, double p_75642_3_, double p_75642_5_, double p_75642_7_) {
         if (this.rabbit.isInWater()) {
            p_75642_7_ = 1.5D;
         }

         super.setMoveTo(p_75642_1_, p_75642_3_, p_75642_5_, p_75642_7_);
         if (p_75642_7_ > 0.0D) {
            this.nextJumpSpeed = p_75642_7_;
         }

      }
   }

   public static class RabbitTypeData implements IEntityLivingData {
      public int typeData;

      public RabbitTypeData(int p_i45864_1_) {
         this.typeData = p_i45864_1_;
      }
   }
}
