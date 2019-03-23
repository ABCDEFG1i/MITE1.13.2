package net.minecraft.entity.passive;

import com.google.common.collect.Sets;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTurtleEgg;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.pathfinding.WalkAndSwimNodeProcessor;
import net.minecraft.stats.StatList;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityTurtle extends EntityAnimal {
   private static final DataParameter<BlockPos> HOME_POS = EntityDataManager.createKey(EntityTurtle.class, DataSerializers.BLOCK_POS);
   private static final DataParameter<Boolean> HAS_EGG = EntityDataManager.createKey(EntityTurtle.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> field_203024_bB = EntityDataManager.createKey(EntityTurtle.class, DataSerializers.BOOLEAN);
   private static final DataParameter<BlockPos> TRAVEL_POS = EntityDataManager.createKey(EntityTurtle.class, DataSerializers.BLOCK_POS);
   private static final DataParameter<Boolean> GOING_HOME = EntityDataManager.createKey(EntityTurtle.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> field_203027_bE = EntityDataManager.createKey(EntityTurtle.class, DataSerializers.BOOLEAN);
   private int field_203028_bF;
   public static final Predicate<Entity> TARGET_DRY_BABY = (p_210131_0_) -> {
      if (!(p_210131_0_ instanceof EntityLivingBase)) {
         return false;
      } else {
         return ((EntityLivingBase)p_210131_0_).isChild() && !p_210131_0_.isInWater();
      }
   };

   public EntityTurtle(World p_i48794_1_) {
      super(EntityType.TURTLE, p_i48794_1_);
      this.setSize(1.2F, 0.4F);
      this.moveHelper = new EntityTurtle.MoveHelper(this);
      this.spawnableBlock = Blocks.SAND;
      this.stepHeight = 1.0F;
   }

   public void setHome(BlockPos p_203011_1_) {
      this.dataManager.set(HOME_POS, p_203011_1_);
   }

   private BlockPos getHome() {
      return this.dataManager.get(HOME_POS);
   }

   private void func_203019_h(BlockPos p_203019_1_) {
      this.dataManager.set(TRAVEL_POS, p_203019_1_);
   }

   private BlockPos func_203013_dB() {
      return this.dataManager.get(TRAVEL_POS);
   }

   public boolean func_203020_dx() {
      return this.dataManager.get(HAS_EGG);
   }

   private void func_203017_r(boolean p_203017_1_) {
      this.dataManager.set(HAS_EGG, p_203017_1_);
   }

   public boolean func_203023_dy() {
      return this.dataManager.get(field_203024_bB);
   }

   private void func_203015_s(boolean p_203015_1_) {
      this.field_203028_bF = p_203015_1_ ? 1 : 0;
      this.dataManager.set(field_203024_bB, p_203015_1_);
   }

   private boolean func_203022_dF() {
      return this.dataManager.get(GOING_HOME);
   }

   private void func_203012_t(boolean p_203012_1_) {
      this.dataManager.set(GOING_HOME, p_203012_1_);
   }

   private boolean func_203014_dG() {
      return this.dataManager.get(field_203027_bE);
   }

   private void func_203021_u(boolean p_203021_1_) {
      this.dataManager.set(field_203027_bE, p_203021_1_);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(HOME_POS, BlockPos.ORIGIN);
      this.dataManager.register(HAS_EGG, false);
      this.dataManager.register(TRAVEL_POS, BlockPos.ORIGIN);
      this.dataManager.register(GOING_HOME, false);
      this.dataManager.register(field_203027_bE, false);
      this.dataManager.register(field_203024_bB, false);
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setInteger("HomePosX", this.getHome().getX());
      p_70014_1_.setInteger("HomePosY", this.getHome().getY());
      p_70014_1_.setInteger("HomePosZ", this.getHome().getZ());
      p_70014_1_.setBoolean("HasEgg", this.func_203020_dx());
      p_70014_1_.setInteger("TravelPosX", this.func_203013_dB().getX());
      p_70014_1_.setInteger("TravelPosY", this.func_203013_dB().getY());
      p_70014_1_.setInteger("TravelPosZ", this.func_203013_dB().getZ());
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      int i = p_70037_1_.getInteger("HomePosX");
      int j = p_70037_1_.getInteger("HomePosY");
      int k = p_70037_1_.getInteger("HomePosZ");
      this.setHome(new BlockPos(i, j, k));
      super.readEntityFromNBT(p_70037_1_);
      this.func_203017_r(p_70037_1_.getBoolean("HasEgg"));
      int l = p_70037_1_.getInteger("TravelPosX");
      int i1 = p_70037_1_.getInteger("TravelPosY");
      int j1 = p_70037_1_.getInteger("TravelPosZ");
      this.func_203019_h(new BlockPos(l, i1, j1));
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance p_204210_1_, @Nullable IEntityLivingData p_204210_2_, @Nullable NBTTagCompound p_204210_3_) {
      this.setHome(new BlockPos(this.posX, this.posY, this.posZ));
      this.func_203019_h(BlockPos.ORIGIN);
      return super.onInitialSpawn(p_204210_1_, p_204210_2_, p_204210_3_);
   }

   public boolean func_205020_a(IWorld p_205020_1_, boolean p_205020_2_) {
      BlockPos blockpos = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);
      return blockpos.getY() < p_205020_1_.getSeaLevel() + 4 && super.func_205020_a(p_205020_1_, p_205020_2_);
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntityTurtle.AIPanic(this, 1.2D));
      this.tasks.addTask(1, new EntityTurtle.AIMate(this, 1.0D));
      this.tasks.addTask(1, new EntityTurtle.AILayEgg(this, 1.0D));
      this.tasks.addTask(2, new EntityTurtle.AIPlayerTempt(this, 1.1D, Blocks.SEAGRASS.asItem()));
      this.tasks.addTask(3, new EntityTurtle.AIGoToWater(this, 1.0D));
      this.tasks.addTask(4, new EntityTurtle.AIGoHome(this, 1.0D));
      this.tasks.addTask(7, new EntityTurtle.AITravel(this, 1.0D));
      this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(9, new EntityTurtle.AIWander(this, 1.0D, 100));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
   }

   public boolean isPushedByWater() {
      return false;
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.WATER;
   }

   public int getTalkInterval() {
      return 200;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return !this.isInWater() && this.onGround && !this.isChild() ? SoundEvents.ENTITY_TURTLE_AMBIENT_LAND : super.getAmbientSound();
   }

   protected void playSwimSound(float p_203006_1_) {
      super.playSwimSound(p_203006_1_ * 1.5F);
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.ENTITY_TURTLE_SWIM;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return this.isChild() ? SoundEvents.ENTITY_TURTLE_HURT_BABY : SoundEvents.ENTITY_TURTLE_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return this.isChild() ? SoundEvents.ENTITY_TURTLE_DEATH_BABY : SoundEvents.ENTITY_TURTLE_DEATH;
   }

   protected void playStepSound(BlockPos p_180429_1_, IBlockState p_180429_2_) {
      SoundEvent soundevent = this.isChild() ? SoundEvents.ENTITY_TURTLE_SHAMBLE_BABY : SoundEvents.ENTITY_TURTLE_SHAMBLE;
      this.playSound(soundevent, 0.15F, 1.0F);
   }

   public boolean canBreed() {
      return super.canBreed() && !this.func_203020_dx();
   }

   protected float determineNextStepDistance() {
      return this.distanceWalkedOnStepModified + 0.15F;
   }

   public void setScaleForAge(boolean p_98054_1_) {
      this.setScale(p_98054_1_ ? 0.3F : 1.0F);
   }

   protected PathNavigate createNavigator(World p_175447_1_) {
      return new EntityTurtle.PathNavigater(this, p_175447_1_);
   }

   @Nullable
   public EntityAgeable createChild(EntityAgeable p_90011_1_) {
      return new EntityTurtle(this.world);
   }

   public boolean isBreedingItem(ItemStack p_70877_1_) {
      return p_70877_1_.getItem() == Blocks.SEAGRASS.asItem();
   }

   public float getBlockPathWeight(BlockPos p_205022_1_, IWorldReaderBase p_205022_2_) {
      return !this.func_203022_dF() && p_205022_2_.getFluidState(p_205022_1_).isTagged(FluidTags.WATER) ? 10.0F : super.getBlockPathWeight(p_205022_1_, p_205022_2_);
   }

   public void livingTick() {
      super.livingTick();
      if (this.func_203023_dy() && this.field_203028_bF >= 1 && this.field_203028_bF % 5 == 0) {
         BlockPos blockpos = new BlockPos(this);
         if (this.world.getBlockState(blockpos.down()).getBlock() == Blocks.SAND) {
            this.world.playEvent(2001, blockpos, Block.getStateId(Blocks.SAND.getDefaultState()));
         }
      }

   }

   protected void onGrowingAdult() {
      super.onGrowingAdult();
      if (this.world.getGameRules().getBoolean("doMobLoot")) {
         this.entityDropItem(Items.SCUTE, 1);
      }

   }

   public void travel(float p_191986_1_, float p_191986_2_, float p_191986_3_) {
      if (this.isServerWorld() && this.isInWater()) {
         this.moveRelative(p_191986_1_, p_191986_2_, p_191986_3_, 0.1F);
         this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
         this.motionX *= (double)0.9F;
         this.motionY *= (double)0.9F;
         this.motionZ *= (double)0.9F;
         if (this.getAttackTarget() == null && (!this.func_203022_dF() || !(this.getDistanceSq(this.getHome()) < 400.0D))) {
            this.motionY -= 0.005D;
         }
      } else {
         super.travel(p_191986_1_, p_191986_2_, p_191986_3_);
      }

   }

   public boolean canBeLeashedTo(EntityPlayer p_184652_1_) {
      return false;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_TURTLE;
   }

   public void onStruckByLightning(EntityLightningBolt p_70077_1_) {
      this.attackEntityFrom(DamageSource.LIGHTNING_BOLT, Float.MAX_VALUE);
   }

   public void onDeath(DamageSource p_70645_1_) {
      super.onDeath(p_70645_1_);
      if (p_70645_1_ == DamageSource.LIGHTNING_BOLT) {
         this.entityDropItem(new ItemStack(Items.BOWL, 1), 0.0F);
      }

   }

   static class AIGoHome extends EntityAIBase {
      private final EntityTurtle field_203127_a;
      private final double field_203128_b;
      private boolean field_203129_c;
      private int field_203130_d;

      AIGoHome(EntityTurtle p_i48821_1_, double p_i48821_2_) {
         this.field_203127_a = p_i48821_1_;
         this.field_203128_b = p_i48821_2_;
      }

      public boolean shouldExecute() {
         if (this.field_203127_a.isChild()) {
            return false;
         } else if (this.field_203127_a.func_203020_dx()) {
            return true;
         } else if (this.field_203127_a.getRNG().nextInt(700) != 0) {
            return false;
         } else {
            return this.field_203127_a.getDistanceSq(this.field_203127_a.getHome()) >= 4096.0D;
         }
      }

      public void startExecuting() {
         this.field_203127_a.func_203012_t(true);
         this.field_203129_c = false;
         this.field_203130_d = 0;
      }

      public void resetTask() {
         this.field_203127_a.func_203012_t(false);
      }

      public boolean shouldContinueExecuting() {
         return this.field_203127_a.getDistanceSq(this.field_203127_a.getHome()) >= 49.0D && !this.field_203129_c && this.field_203130_d <= 600;
      }

      public void updateTask() {
         BlockPos blockpos = this.field_203127_a.getHome();
         boolean flag = this.field_203127_a.getDistanceSq(blockpos) <= 256.0D;
         if (flag) {
            ++this.field_203130_d;
         }

         if (this.field_203127_a.getNavigator().noPath()) {
            Vec3d vec3d = RandomPositionGenerator.func_203155_a(this.field_203127_a, 16, 3, new Vec3d((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ()), (double)((float)Math.PI / 10F));
            if (vec3d == null) {
               vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.field_203127_a, 8, 7, new Vec3d((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ()));
            }

            if (vec3d != null && !flag && this.field_203127_a.world.getBlockState(new BlockPos(vec3d)).getBlock() != Blocks.WATER) {
               vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.field_203127_a, 16, 5, new Vec3d((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ()));
            }

            if (vec3d == null) {
               this.field_203129_c = true;
               return;
            }

            this.field_203127_a.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, this.field_203128_b);
         }

      }
   }

   static class AIGoToWater extends EntityAIMoveToBlock {
      private final EntityTurtle field_203121_f;

      private AIGoToWater(EntityTurtle p_i48819_1_, double p_i48819_2_) {
         super(p_i48819_1_, p_i48819_1_.isChild() ? 2.0D : p_i48819_2_, 24);
         this.field_203121_f = p_i48819_1_;
         this.field_203112_e = -1;
      }

      public boolean shouldContinueExecuting() {
         return !this.field_203121_f.isInWater() && this.timeoutCounter <= 1200 && this.shouldMoveTo(this.field_203121_f.world, this.destinationBlock);
      }

      public boolean shouldExecute() {
         if (this.field_203121_f.isChild() && !this.field_203121_f.isInWater()) {
            return super.shouldExecute();
         } else {
            return !this.field_203121_f.func_203022_dF() && !this.field_203121_f.isInWater() && !this.field_203121_f.func_203020_dx() ? super.shouldExecute() : false;
         }
      }

      public int getTargetYOffset() {
         return 1;
      }

      public boolean shouldMove() {
         return this.timeoutCounter % 160 == 0;
      }

      protected boolean shouldMoveTo(IWorldReaderBase p_179488_1_, BlockPos p_179488_2_) {
         Block block = p_179488_1_.getBlockState(p_179488_2_).getBlock();
         return block == Blocks.WATER;
      }
   }

   static class AILayEgg extends EntityAIMoveToBlock {
      private final EntityTurtle field_203122_f;

      AILayEgg(EntityTurtle p_i48818_1_, double p_i48818_2_) {
         super(p_i48818_1_, p_i48818_2_, 16);
         this.field_203122_f = p_i48818_1_;
      }

      public boolean shouldExecute() {
         return this.field_203122_f.func_203020_dx() && this.field_203122_f.getDistanceSq(this.field_203122_f.getHome()) < 81.0D ? super.shouldExecute() : false;
      }

      public boolean shouldContinueExecuting() {
         return super.shouldContinueExecuting() && this.field_203122_f.func_203020_dx() && this.field_203122_f.getDistanceSq(this.field_203122_f.getHome()) < 81.0D;
      }

      public void updateTask() {
         super.updateTask();
         BlockPos blockpos = new BlockPos(this.field_203122_f);
         if (!this.field_203122_f.isInWater() && this.getIsAboveDestination()) {
            if (this.field_203122_f.field_203028_bF < 1) {
               this.field_203122_f.func_203015_s(true);
            } else if (this.field_203122_f.field_203028_bF > 200) {
               World world = this.field_203122_f.world;
               world.playSound((EntityPlayer)null, blockpos, SoundEvents.ENTITY_TURTLE_LAY_EGG, SoundCategory.BLOCKS, 0.3F, 0.9F + world.rand.nextFloat() * 0.2F);
               world.setBlockState(this.destinationBlock.up(), Blocks.TURTLE_EGG.getDefaultState().with(BlockTurtleEgg.EGGS, Integer.valueOf(this.field_203122_f.rand.nextInt(4) + 1)), 3);
               this.field_203122_f.func_203017_r(false);
               this.field_203122_f.func_203015_s(false);
               this.field_203122_f.func_204700_e(600);
            }

            if (this.field_203122_f.func_203023_dy()) {
               this.field_203122_f.field_203028_bF++;
            }
         }

      }

      protected boolean shouldMoveTo(IWorldReaderBase p_179488_1_, BlockPos p_179488_2_) {
         if (!p_179488_1_.isAirBlock(p_179488_2_.up())) {
            return false;
         } else {
            Block block = p_179488_1_.getBlockState(p_179488_2_).getBlock();
            return block == Blocks.SAND;
         }
      }
   }

   static class AIMate extends EntityAIMate {
      private final EntityTurtle field_203107_f;

      AIMate(EntityTurtle p_i48822_1_, double p_i48822_2_) {
         super(p_i48822_1_, p_i48822_2_);
         this.field_203107_f = p_i48822_1_;
      }

      public boolean shouldExecute() {
         return super.shouldExecute() && !this.field_203107_f.func_203020_dx();
      }

      protected void spawnBaby() {
         EntityPlayerMP entityplayermp = this.animal.getLoveCause();
         if (entityplayermp == null && this.targetMate.getLoveCause() != null) {
            entityplayermp = this.targetMate.getLoveCause();
         }

         if (entityplayermp != null) {
            entityplayermp.addStat(StatList.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(entityplayermp, this.animal, this.targetMate, (EntityAgeable)null);
         }

         this.field_203107_f.func_203017_r(true);
         this.animal.resetInLove();
         this.targetMate.resetInLove();
         Random random = this.animal.getRNG();
         if (this.world.getGameRules().getBoolean("doMobLoot")) {
            this.world.spawnEntity(new EntityXPOrb(this.world, this.animal.posX, this.animal.posY, this.animal.posZ, random.nextInt(7) + 1));
         }

      }
   }

   static class AIPanic extends EntityAIPanic {
      AIPanic(EntityTurtle p_i48816_1_, double p_i48816_2_) {
         super(p_i48816_1_, p_i48816_2_);
      }

      public boolean shouldExecute() {
         if (this.creature.getRevengeTarget() == null && !this.creature.isBurning()) {
            return false;
         } else {
            BlockPos blockpos = this.getRandPos(this.creature.world, this.creature, 7, 4);
            if (blockpos != null) {
               this.randPosX = (double)blockpos.getX();
               this.randPosY = (double)blockpos.getY();
               this.randPosZ = (double)blockpos.getZ();
               return true;
            } else {
               return this.findRandomPosition();
            }
         }
      }
   }

   static class AIPlayerTempt extends EntityAIBase {
      private final EntityTurtle field_203132_a;
      private final double field_203133_b;
      private EntityPlayer field_203134_c;
      private int field_203135_d;
      private final Set<Item> field_203136_e;

      AIPlayerTempt(EntityTurtle p_i48812_1_, double p_i48812_2_, Item p_i48812_4_) {
         this.field_203132_a = p_i48812_1_;
         this.field_203133_b = p_i48812_2_;
         this.field_203136_e = Sets.newHashSet(p_i48812_4_);
         this.setMutexBits(3);
      }

      public boolean shouldExecute() {
         if (this.field_203135_d > 0) {
            --this.field_203135_d;
            return false;
         } else {
            this.field_203134_c = this.field_203132_a.world.getClosestPlayerToEntity(this.field_203132_a, 10.0D);
            if (this.field_203134_c == null) {
               return false;
            } else {
               return this.func_203131_a(this.field_203134_c.getHeldItemMainhand()) || this.func_203131_a(this.field_203134_c.getHeldItemOffhand());
            }
         }
      }

      private boolean func_203131_a(ItemStack p_203131_1_) {
         return this.field_203136_e.contains(p_203131_1_.getItem());
      }

      public boolean shouldContinueExecuting() {
         return this.shouldExecute();
      }

      public void resetTask() {
         this.field_203134_c = null;
         this.field_203132_a.getNavigator().clearPath();
         this.field_203135_d = 100;
      }

      public void updateTask() {
         this.field_203132_a.getLookHelper().setLookPositionWithEntity(this.field_203134_c, (float)(this.field_203132_a.getHorizontalFaceSpeed() + 20), (float)this.field_203132_a.getVerticalFaceSpeed());
         if (this.field_203132_a.getDistanceSq(this.field_203134_c) < 6.25D) {
            this.field_203132_a.getNavigator().clearPath();
         } else {
            this.field_203132_a.getNavigator().tryMoveToEntityLiving(this.field_203134_c, this.field_203133_b);
         }

      }
   }

   static class AITravel extends EntityAIBase {
      private final EntityTurtle field_203137_a;
      private final double field_203138_b;
      private boolean field_203139_c;

      AITravel(EntityTurtle p_i48811_1_, double p_i48811_2_) {
         this.field_203137_a = p_i48811_1_;
         this.field_203138_b = p_i48811_2_;
      }

      public boolean shouldExecute() {
         return !this.field_203137_a.func_203022_dF() && !this.field_203137_a.func_203020_dx() && this.field_203137_a.isInWater();
      }

      public void startExecuting() {
         int i = 512;
         int j = 4;
         Random random = this.field_203137_a.rand;
         int k = random.nextInt(1025) - 512;
         int l = random.nextInt(9) - 4;
         int i1 = random.nextInt(1025) - 512;
         if ((double)l + this.field_203137_a.posY > (double)(this.field_203137_a.world.getSeaLevel() - 1)) {
            l = 0;
         }

         BlockPos blockpos = new BlockPos((double)k + this.field_203137_a.posX, (double)l + this.field_203137_a.posY, (double)i1 + this.field_203137_a.posZ);
         this.field_203137_a.func_203019_h(blockpos);
         this.field_203137_a.func_203021_u(true);
         this.field_203139_c = false;
      }

      public void updateTask() {
         if (this.field_203137_a.getNavigator().noPath()) {
            BlockPos blockpos = this.field_203137_a.func_203013_dB();
            Vec3d vec3d = RandomPositionGenerator.func_203155_a(this.field_203137_a, 16, 3, new Vec3d((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ()), (double)((float)Math.PI / 10F));
            if (vec3d == null) {
               vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.field_203137_a, 8, 7, new Vec3d((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ()));
            }

            if (vec3d != null) {
               int i = MathHelper.floor(vec3d.x);
               int j = MathHelper.floor(vec3d.z);
               int k = 34;
               MutableBoundingBox mutableboundingbox = new MutableBoundingBox(i - 34, 0, j - 34, i + 34, 0, j + 34);
               if (!this.field_203137_a.world.isAreaLoaded(mutableboundingbox)) {
                  vec3d = null;
               }
            }

            if (vec3d == null) {
               this.field_203139_c = true;
               return;
            }

            this.field_203137_a.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, this.field_203138_b);
         }

      }

      public boolean shouldContinueExecuting() {
         return !this.field_203137_a.getNavigator().noPath() && !this.field_203139_c && !this.field_203137_a.func_203022_dF() && !this.field_203137_a.isInLove() && !this.field_203137_a.func_203020_dx();
      }

      public void resetTask() {
         this.field_203137_a.func_203021_u(false);
         super.resetTask();
      }
   }

   static class AIWander extends EntityAIWander {
      private final EntityTurtle field_203123_h;

      private AIWander(EntityTurtle p_i48813_1_, double p_i48813_2_, int p_i48813_4_) {
         super(p_i48813_1_, p_i48813_2_, p_i48813_4_);
         this.field_203123_h = p_i48813_1_;
      }

      public boolean shouldExecute() {
         return !this.entity.isInWater() && !this.field_203123_h.func_203022_dF() && !this.field_203123_h.func_203020_dx() ? super.shouldExecute() : false;
      }
   }

   static class MoveHelper extends EntityMoveHelper {
      private final EntityTurtle turtle;

      MoveHelper(EntityTurtle p_i48817_1_) {
         super(p_i48817_1_);
         this.turtle = p_i48817_1_;
      }

      private void func_203102_g() {
         if (this.turtle.isInWater()) {
            this.turtle.motionY += 0.005D;
            if (this.turtle.getDistanceSq(this.turtle.getHome()) > 256.0D) {
               this.turtle.setAIMoveSpeed(Math.max(this.turtle.getAIMoveSpeed() / 2.0F, 0.08F));
            }

            if (this.turtle.isChild()) {
               this.turtle.setAIMoveSpeed(Math.max(this.turtle.getAIMoveSpeed() / 3.0F, 0.06F));
            }
         } else if (this.turtle.onGround) {
            this.turtle.setAIMoveSpeed(Math.max(this.turtle.getAIMoveSpeed() / 2.0F, 0.06F));
         }

      }

      public void tick() {
         this.func_203102_g();
         if (this.action == EntityMoveHelper.Action.MOVE_TO && !this.turtle.getNavigator().noPath()) {
            double d0 = this.posX - this.turtle.posX;
            double d1 = this.posY - this.turtle.posY;
            double d2 = this.posZ - this.turtle.posZ;
            double d3 = (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
            d1 = d1 / d3;
            float f = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
            this.turtle.rotationYaw = this.limitAngle(this.turtle.rotationYaw, f, 90.0F);
            this.turtle.renderYawOffset = this.turtle.rotationYaw;
            float f1 = (float)(this.speed * this.turtle.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
            this.turtle.setAIMoveSpeed(this.turtle.getAIMoveSpeed() + (f1 - this.turtle.getAIMoveSpeed()) * 0.125F);
            this.turtle.motionY += (double)this.turtle.getAIMoveSpeed() * d1 * 0.1D;
         } else {
            this.turtle.setAIMoveSpeed(0.0F);
         }
      }
   }

   static class PathNavigater extends PathNavigateSwimmer {
      PathNavigater(EntityTurtle p_i48815_1_, World p_i48815_2_) {
         super(p_i48815_1_, p_i48815_2_);
      }

      protected boolean canNavigate() {
         return true;
      }

      protected PathFinder getPathFinder() {
         return new PathFinder(new WalkAndSwimNodeProcessor());
      }

      public boolean canEntityStandOnPos(BlockPos p_188555_1_) {
         if (this.entity instanceof EntityTurtle) {
            EntityTurtle entityturtle = (EntityTurtle)this.entity;
            if (entityturtle.func_203014_dG()) {
               return this.world.getBlockState(p_188555_1_).getBlock() == Blocks.WATER;
            }
         }

         return !this.world.getBlockState(p_188555_1_.down()).isAir();
      }
   }
}
