package net.minecraft.entity.monster;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBreakBlock;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIZombieAttack;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityTurtle;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityZombie extends EntityMob {
   protected static final IAttribute SPAWN_REINFORCEMENTS_CHANCE = (new RangedAttribute((IAttribute)null, "zombie.spawnReinforcements", 0.0D, 0.0D, 1.0D)).setDescription("Spawn Reinforcements Chance");
   private static final UUID BABY_SPEED_BOOST_ID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
   private static final AttributeModifier BABY_SPEED_BOOST = new AttributeModifier(BABY_SPEED_BOOST_ID, "Baby speed boost", 0.5D, 1);
   private static final DataParameter<Boolean> IS_CHILD = EntityDataManager.createKey(EntityZombie.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> VILLAGER_TYPE = EntityDataManager.createKey(EntityZombie.class, DataSerializers.VARINT);
   private static final DataParameter<Boolean> ARMS_RAISED = EntityDataManager.createKey(EntityZombie.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> DROWNING = EntityDataManager.createKey(EntityZombie.class, DataSerializers.BOOLEAN);
   private final EntityAIBreakDoor breakDoor = new EntityAIBreakDoor(this);
   private boolean isBreakDoorsTaskSet;
   private int inWaterTime;
   private int drownedConversionTime;
   private float zombieWidth = -1.0F;
   private float zombieHeight;

   public EntityZombie(EntityType<?> p_i48549_1_, World p_i48549_2_) {
      super(p_i48549_1_, p_i48549_2_);
      this.setSize(0.6F, 1.95F);
   }

   public EntityZombie(World p_i1745_1_) {
      this(EntityType.ZOMBIE, p_i1745_1_);
   }

   protected void initEntityAI() {
      this.tasks.addTask(4, new EntityZombie.AIAttackTurtleEgg(Blocks.TURTLE_EGG, this, 1.0D, 3));
      this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
      this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.applyEntityAI();
   }

   protected void applyEntityAI() {
      this.tasks.addTask(2, new EntityAIZombieAttack(this, 1.0D, false));
      this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
      this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, EntityPigZombie.class));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityVillager.class, false));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityIronGolem.class, true));
      this.targetTasks.addTask(5, new EntityAINearestAttackableTarget<>(this, EntityTurtle.class, 10, true, false, EntityTurtle.TARGET_DRY_BABY));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.23F);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
      this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D);
      this.getAttributeMap().registerAttribute(SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(this.rand.nextDouble() * (double)0.1F);
   }

   protected void registerData() {
      super.registerData();
      this.getDataManager().register(IS_CHILD, false);
      this.getDataManager().register(VILLAGER_TYPE, 0);
      this.getDataManager().register(ARMS_RAISED, false);
      this.getDataManager().register(DROWNING, false);
   }

   public boolean isDrowning() {
      return this.getDataManager().get(DROWNING);
   }

   public void setSwingingArms(boolean p_184724_1_) {
      this.getDataManager().set(ARMS_RAISED, p_184724_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isArmsRaised() {
      return this.getDataManager().get(ARMS_RAISED);
   }

   public boolean isBreakDoorsTaskSet() {
      return this.isBreakDoorsTaskSet;
   }

   public void setBreakDoorsAItask(boolean p_146070_1_) {
      if (this.canBreakDoors()) {
         if (this.isBreakDoorsTaskSet != p_146070_1_) {
            this.isBreakDoorsTaskSet = p_146070_1_;
            ((PathNavigateGround)this.getNavigator()).setBreakDoors(p_146070_1_);
            if (p_146070_1_) {
               this.tasks.addTask(1, this.breakDoor);
            } else {
               this.tasks.removeTask(this.breakDoor);
            }
         }
      } else if (this.isBreakDoorsTaskSet) {
         this.tasks.removeTask(this.breakDoor);
         this.isBreakDoorsTaskSet = false;
      }

   }

   protected boolean canBreakDoors() {
      return true;
   }

   public boolean isChild() {
      return this.getDataManager().get(IS_CHILD);
   }

   protected int getExperiencePoints(EntityPlayer p_70693_1_) {
      if (this.isChild()) {
         this.experienceValue = (int)((float)this.experienceValue * 2.5F);
      }

      return super.getExperiencePoints(p_70693_1_);
   }

   public void setChild(boolean p_82227_1_) {
      this.getDataManager().set(IS_CHILD, p_82227_1_);
      if (this.world != null && !this.world.isRemote) {
         IAttributeInstance iattributeinstance = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
         iattributeinstance.removeModifier(BABY_SPEED_BOOST);
         if (p_82227_1_) {
            iattributeinstance.applyModifier(BABY_SPEED_BOOST);
         }
      }

      this.setChildSize(p_82227_1_);
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      if (IS_CHILD.equals(p_184206_1_)) {
         this.setChildSize(this.isChild());
      }

      super.notifyDataManagerChange(p_184206_1_);
   }

   protected boolean shouldDrown() {
      return true;
   }

   public void tick() {
      if (!this.world.isRemote) {
         if (this.isDrowning()) {
            --this.drownedConversionTime;
            if (this.drownedConversionTime < 0) {
               this.onDrowned();
            }
         } else if (this.shouldDrown()) {
            if (this.areEyesInFluid(FluidTags.WATER)) {
               ++this.inWaterTime;
               if (this.inWaterTime >= 600) {
                  this.startDrowning(300);
               }
            } else {
               this.inWaterTime = -1;
            }
         }
      }

      super.tick();
   }

   public void livingTick() {
      boolean flag = this.shouldBurnInDay() && this.isInDaylight();
      if (flag) {
         ItemStack itemstack = this.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
         if (!itemstack.isEmpty()) {
            if (itemstack.isDamageable()) {
               itemstack.setDamage(itemstack.getDamage() + this.rand.nextInt(2));
               if (itemstack.getDamage() >= itemstack.getMaxDamage()) {
                  this.renderBrokenItemStack(itemstack);
                  this.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
               }
            }

            flag = false;
         }

         if (flag) {
            this.setFire(8);
         }
      }

      super.livingTick();
   }

   private void startDrowning(int p_204704_1_) {
      this.drownedConversionTime = p_204704_1_;
      this.getDataManager().set(DROWNING, true);
   }

   protected void onDrowned() {
      this.convertInto(new EntityDrowned(this.world));
      this.world.playEvent((EntityPlayer)null, 1040, new BlockPos((int)this.posX, (int)this.posY, (int)this.posZ), 0);
   }

   protected void convertInto(EntityZombie p_207305_1_) {
      if (!this.world.isRemote && !this.isDead) {
         p_207305_1_.copyLocationAndAnglesFrom(this);
         p_207305_1_.func_207301_a(this.canPickUpLoot(), this.isBreakDoorsTaskSet(), this.isChild(), this.isAIDisabled());

         for(EntityEquipmentSlot entityequipmentslot : EntityEquipmentSlot.values()) {
            ItemStack itemstack = this.getItemStackFromSlot(entityequipmentslot);
            if (!itemstack.isEmpty()) {
               p_207305_1_.setItemStackToSlot(entityequipmentslot, itemstack);
               p_207305_1_.setDropChance(entityequipmentslot, this.func_205712_c(entityequipmentslot));
            }
         }

         if (this.hasCustomName()) {
            p_207305_1_.setCustomName(this.getCustomName());
            p_207305_1_.setCustomNameVisible(this.isCustomNameVisible());
         }

         this.world.spawnEntity(p_207305_1_);
         this.setDead();
      }
   }

   protected boolean shouldBurnInDay() {
      return true;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (super.attackEntityFrom(p_70097_1_, p_70097_2_)) {
         EntityLivingBase entitylivingbase = this.getAttackTarget();
         if (entitylivingbase == null && p_70097_1_.getTrueSource() instanceof EntityLivingBase) {
            entitylivingbase = (EntityLivingBase)p_70097_1_.getTrueSource();
         }

         if (entitylivingbase != null && this.world.getDifficulty() == EnumDifficulty.HARD && (double)this.rand.nextFloat() < this.getAttribute(SPAWN_REINFORCEMENTS_CHANCE).getAttributeValue() && this.world.getGameRules().getBoolean("doMobSpawning")) {
            int i = MathHelper.floor(this.posX);
            int j = MathHelper.floor(this.posY);
            int k = MathHelper.floor(this.posZ);
            EntityZombie entityzombie = new EntityZombie(this.world);

            for(int l = 0; l < 50; ++l) {
               int i1 = i + MathHelper.nextInt(this.rand, 7, 40) * MathHelper.nextInt(this.rand, -1, 1);
               int j1 = j + MathHelper.nextInt(this.rand, 7, 40) * MathHelper.nextInt(this.rand, -1, 1);
               int k1 = k + MathHelper.nextInt(this.rand, 7, 40) * MathHelper.nextInt(this.rand, -1, 1);
               if (this.world.getBlockState(new BlockPos(i1, j1 - 1, k1)).isTopSolid() && this.world.getLight(new BlockPos(i1, j1, k1)) < 10) {
                  entityzombie.setPosition((double)i1, (double)j1, (double)k1);
                  if (!this.world.isAnyPlayerWithinRangeAt((double)i1, (double)j1, (double)k1, 7.0D) && this.world.checkNoEntityCollision(entityzombie, entityzombie.getEntityBoundingBox()) && this.world.isCollisionBoxesEmpty(entityzombie, entityzombie.getEntityBoundingBox()) && !this.world.containsAnyLiquid(entityzombie.getEntityBoundingBox())) {
                     this.world.spawnEntity(entityzombie);
                     entityzombie.setAttackTarget(entitylivingbase);
                     entityzombie.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(entityzombie)), (IEntityLivingData)null, (NBTTagCompound)null);
                     this.getAttribute(SPAWN_REINFORCEMENTS_CHANCE).applyModifier(new AttributeModifier("Zombie reinforcement caller charge", (double)-0.05F, 0));
                     entityzombie.getAttribute(SPAWN_REINFORCEMENTS_CHANCE).applyModifier(new AttributeModifier("Zombie reinforcement callee charge", (double)-0.05F, 0));
                     break;
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      boolean flag = super.attackEntityAsMob(p_70652_1_);
      if (flag) {
         float f = this.world.getDifficultyForLocation(new BlockPos(this)).getAdditionalDifficulty();
         if (this.getHeldItemMainhand().isEmpty() && this.isBurning() && this.rand.nextFloat() < f * 0.3F) {
            p_70652_1_.setFire(2 * (int)f);
         }
      }

      return flag;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_ZOMBIE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_ZOMBIE_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_ZOMBIE_STEP;
   }

   protected void playStepSound(BlockPos p_180429_1_, IBlockState p_180429_2_) {
      this.playSound(this.getStepSound(), 0.15F, 1.0F);
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.UNDEAD;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_ZOMBIE;
   }

   protected void setEquipmentBasedOnDifficulty(DifficultyInstance p_180481_1_) {
      super.setEquipmentBasedOnDifficulty(p_180481_1_);
      if (this.rand.nextFloat() < (this.world.getDifficulty() == EnumDifficulty.HARD ? 0.05F : 0.01F)) {
         int i = this.rand.nextInt(3);
         if (i == 0) {
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
         } else {
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
         }
      }

   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      if (this.isChild()) {
         p_70014_1_.setBoolean("IsBaby", true);
      }

      p_70014_1_.setBoolean("CanBreakDoors", this.isBreakDoorsTaskSet());
      p_70014_1_.setInteger("InWaterTime", this.isInWater() ? this.inWaterTime : -1);
      p_70014_1_.setInteger("DrownedConversionTime", this.isDrowning() ? this.drownedConversionTime : -1);
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      if (p_70037_1_.getBoolean("IsBaby")) {
         this.setChild(true);
      }

      this.setBreakDoorsAItask(p_70037_1_.getBoolean("CanBreakDoors"));
      this.inWaterTime = p_70037_1_.getInteger("InWaterTime");
      if (p_70037_1_.hasKey("DrownedConversionTime", 99) && p_70037_1_.getInteger("DrownedConversionTime") > -1) {
         this.startDrowning(p_70037_1_.getInteger("DrownedConversionTime"));
      }

   }

   public void onKillEntity(EntityLivingBase p_70074_1_) {
      super.onKillEntity(p_70074_1_);
      if ((this.world.getDifficulty() == EnumDifficulty.NORMAL || this.world.getDifficulty() == EnumDifficulty.HARD) && p_70074_1_ instanceof EntityVillager) {
         if (this.world.getDifficulty() != EnumDifficulty.HARD && this.rand.nextBoolean()) {
            return;
         }

         EntityVillager entityvillager = (EntityVillager)p_70074_1_;
         EntityZombieVillager entityzombievillager = new EntityZombieVillager(this.world);
         entityzombievillager.copyLocationAndAnglesFrom(entityvillager);
         this.world.removeEntity(entityvillager);
         entityzombievillager.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(entityzombievillager)), new EntityZombie.GroupData(false), (NBTTagCompound)null);
         entityzombievillager.setProfession(entityvillager.getProfession());
         entityzombievillager.setChild(entityvillager.isChild());
         entityzombievillager.setNoAI(entityvillager.isAIDisabled());
         if (entityvillager.hasCustomName()) {
            entityzombievillager.setCustomName(entityvillager.getCustomName());
            entityzombievillager.setCustomNameVisible(entityvillager.isCustomNameVisible());
         }

         this.world.spawnEntity(entityzombievillager);
         this.world.playEvent((EntityPlayer)null, 1026, new BlockPos(this), 0);
      }

   }

   public float getEyeHeight() {
      float f = 1.74F;
      if (this.isChild()) {
         f = (float)((double)f - 0.81D);
      }

      return f;
   }

   protected boolean canEquipItem(ItemStack p_175448_1_) {
      return p_175448_1_.getItem() == Items.EGG && this.isChild() && this.isRiding() ? false : super.canEquipItem(p_175448_1_);
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance p_204210_1_, @Nullable IEntityLivingData p_204210_2_, @Nullable NBTTagCompound p_204210_3_) {
      p_204210_2_ = super.onInitialSpawn(p_204210_1_, p_204210_2_, p_204210_3_);
      float f = p_204210_1_.getClampedAdditionalDifficulty();
      this.setCanPickUpLoot(this.rand.nextFloat() < 0.55F * f);
      if (p_204210_2_ == null) {
         p_204210_2_ = new EntityZombie.GroupData(this.world.rand.nextFloat() < 0.05F);
      }

      if (p_204210_2_ instanceof EntityZombie.GroupData) {
         EntityZombie.GroupData entityzombie$groupdata = (EntityZombie.GroupData)p_204210_2_;
         if (entityzombie$groupdata.isChild) {
            this.setChild(true);
            if ((double)this.world.rand.nextFloat() < 0.05D) {
               List<EntityChicken> list = this.world.getEntitiesWithinAABB(EntityChicken.class, this.getEntityBoundingBox().grow(5.0D, 3.0D, 5.0D), EntitySelectors.IS_STANDALONE);
               if (!list.isEmpty()) {
                  EntityChicken entitychicken = list.get(0);
                  entitychicken.setChickenJockey(true);
                  this.startRiding(entitychicken);
               }
            } else if ((double)this.world.rand.nextFloat() < 0.05D) {
               EntityChicken entitychicken1 = new EntityChicken(this.world);
               entitychicken1.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
               entitychicken1.onInitialSpawn(p_204210_1_, (IEntityLivingData)null, (NBTTagCompound)null);
               entitychicken1.setChickenJockey(true);
               this.world.spawnEntity(entitychicken1);
               this.startRiding(entitychicken1);
            }
         }

         this.setBreakDoorsAItask(this.canBreakDoors() && this.rand.nextFloat() < f * 0.1F);
         this.setEquipmentBasedOnDifficulty(p_204210_1_);
         this.setEnchantmentBasedOnDifficulty(p_204210_1_);
      }

      if (this.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty()) {
         LocalDate localdate = LocalDate.now();
         int i = localdate.get(ChronoField.DAY_OF_MONTH);
         int j = localdate.get(ChronoField.MONTH_OF_YEAR);
         if (j == 10 && i == 31 && this.rand.nextFloat() < 0.25F) {
            this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(this.rand.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
            this.inventoryArmorDropChances[EntityEquipmentSlot.HEAD.getIndex()] = 0.0F;
         }
      }

      this.applyAttributeBonuses(f);
      return p_204210_2_;
   }

   protected void func_207301_a(boolean p_207301_1_, boolean p_207301_2_, boolean p_207301_3_, boolean p_207301_4_) {
      this.setCanPickUpLoot(p_207301_1_);
      this.setBreakDoorsAItask(this.canBreakDoors() && p_207301_2_);
      this.applyAttributeBonuses(this.world.getDifficultyForLocation(new BlockPos(this)).getClampedAdditionalDifficulty());
      this.setChild(p_207301_3_);
      this.setNoAI(p_207301_4_);
   }

   protected void applyAttributeBonuses(float p_207304_1_) {
      this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextDouble() * (double)0.05F, 0));
      double d0 = this.rand.nextDouble() * 1.5D * (double)p_207304_1_;
      if (d0 > 1.0D) {
         this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(new AttributeModifier("Random zombie-spawn bonus", d0, 2));
      }

      if (this.rand.nextFloat() < p_207304_1_ * 0.05F) {
         this.getAttribute(SPAWN_REINFORCEMENTS_CHANCE).applyModifier(new AttributeModifier("Leader zombie bonus", this.rand.nextDouble() * 0.25D + 0.5D, 0));
         this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier("Leader zombie bonus", this.rand.nextDouble() * 3.0D + 1.0D, 2));
         this.setBreakDoorsAItask(this.canBreakDoors());
      }

   }

   public void setChildSize(boolean p_146071_1_) {
      this.multiplySize(p_146071_1_ ? 0.5F : 1.0F);
   }

   protected final void setSize(float p_70105_1_, float p_70105_2_) {
      boolean flag = this.zombieWidth > 0.0F && this.zombieHeight > 0.0F;
      this.zombieWidth = p_70105_1_;
      this.zombieHeight = p_70105_2_;
      if (!flag) {
         this.multiplySize(1.0F);
      }

   }

   protected final void multiplySize(float p_146069_1_) {
      super.setSize(this.zombieWidth * p_146069_1_, this.zombieHeight * p_146069_1_);
   }

   public double getYOffset() {
      return this.isChild() ? 0.0D : -0.45D;
   }

   public void onDeath(DamageSource p_70645_1_) {
      super.onDeath(p_70645_1_);
      if (p_70645_1_.getTrueSource() instanceof EntityCreeper) {
         EntityCreeper entitycreeper = (EntityCreeper)p_70645_1_.getTrueSource();
         if (entitycreeper.getPowered() && entitycreeper.ableToCauseSkullDrop()) {
            entitycreeper.incrementDroppedSkulls();
            ItemStack itemstack = this.getSkullDrop();
            if (!itemstack.isEmpty()) {
               this.entityDropItem(itemstack);
            }
         }
      }

   }

   protected ItemStack getSkullDrop() {
      return new ItemStack(Items.ZOMBIE_HEAD);
   }

   class AIAttackTurtleEgg extends EntityAIBreakBlock {
      AIAttackTurtleEgg(Block p_i48792_2_, EntityCreature p_i48792_3_, double p_i48792_4_, int p_i48792_6_) {
         super(p_i48792_2_, p_i48792_3_, p_i48792_4_, p_i48792_6_);
      }

      public void playBreakingSound(IWorld p_203114_1_, BlockPos p_203114_2_) {
         p_203114_1_.playSound((EntityPlayer)null, p_203114_2_, SoundEvents.ENTITY_ZOMBIE_DESTROY_EGG, SoundCategory.HOSTILE, 0.5F, 0.9F + EntityZombie.this.rand.nextFloat() * 0.2F);
      }

      public void playBrokenSound(World p_203116_1_, BlockPos p_203116_2_) {
         p_203116_1_.playSound((EntityPlayer)null, p_203116_2_, SoundEvents.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + p_203116_1_.rand.nextFloat() * 0.2F);
      }

      public double getTargetDistanceSq() {
         return 1.3D;
      }
   }

   public class GroupData implements IEntityLivingData {
      public boolean isChild;

      private GroupData(boolean p_i47328_2_) {
         this.isChild = p_i47328_2_;
      }
   }
}
