package net.minecraft.entity.passive;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollow;
import net.minecraft.entity.ai.EntityAIFollowOwnerFlying;
import net.minecraft.entity.ai.EntityAILandOnOwnersShoulder;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWaterFlying;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityFlyHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityParrot extends EntityShoulderRiding implements IFlyingAnimal {
   private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EntityParrot.class, DataSerializers.VARINT);
   private static final Predicate<EntityLiving> CAN_MIMIC = new Predicate<EntityLiving>() {
      public boolean test(@Nullable EntityLiving p_test_1_) {
         return p_test_1_ != null && EntityParrot.IMITATION_SOUND_EVENTS.containsKey(p_test_1_.getType());
      }
   };
   private static final Item DEADLY_ITEM = Items.COOKIE;
   private static final Set<Item> TAME_ITEMS = Sets.newHashSet(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS);
   private static final Map<EntityType<?>, SoundEvent> IMITATION_SOUND_EVENTS = Util.make(Maps.newHashMap(), (p_200609_0_) -> {
      p_200609_0_.put(EntityType.BLAZE, SoundEvents.ENTITY_PARROT_IMITATE_BLAZE);
      p_200609_0_.put(EntityType.CAVE_SPIDER, SoundEvents.ENTITY_PARROT_IMITATE_SPIDER);
      p_200609_0_.put(EntityType.CREEPER, SoundEvents.ENTITY_PARROT_IMITATE_CREEPER);
      p_200609_0_.put(EntityType.DROWNED, SoundEvents.ENTITY_PARROT_IMITATE_DROWNED);
      p_200609_0_.put(EntityType.ELDER_GUARDIAN, SoundEvents.ENTITY_PARROT_IMITATE_ELDER_GUARDIAN);
      p_200609_0_.put(EntityType.ENDER_DRAGON, SoundEvents.ENTITY_PARROT_IMITATE_ENDER_DRAGON);
      p_200609_0_.put(EntityType.ENDERMAN, SoundEvents.ENTITY_PARROT_IMITATE_ENDERMAN);
      p_200609_0_.put(EntityType.ENDERMITE, SoundEvents.ENTITY_PARROT_IMITATE_ENDERMITE);
      p_200609_0_.put(EntityType.EVOKER, SoundEvents.ENTITY_PARROT_IMITATE_EVOKER);
      p_200609_0_.put(EntityType.GHAST, SoundEvents.ENTITY_PARROT_IMITATE_GHAST);
      p_200609_0_.put(EntityType.HUSK, SoundEvents.ENTITY_PARROT_IMITATE_HUSK);
      p_200609_0_.put(EntityType.ILLUSIONER, SoundEvents.ENTITY_PARROT_IMITATE_ILLUSIONER);
      p_200609_0_.put(EntityType.MAGMA_CUBE, SoundEvents.ENTITY_PARROT_IMITATE_MAGMA_CUBE);
      p_200609_0_.put(EntityType.ZOMBIE_PIGMAN, SoundEvents.ENTITY_PARROT_IMITATE_ZOMBIE_PIGMAN);
      p_200609_0_.put(EntityType.PHANTOM, SoundEvents.ENTITY_PARROT_IMITATE_PHANTOM);
      p_200609_0_.put(EntityType.POLAR_BEAR, SoundEvents.ENTITY_PARROT_IMITATE_POLAR_BEAR);
      p_200609_0_.put(EntityType.SHULKER, SoundEvents.ENTITY_PARROT_IMITATE_SHULKER);
      p_200609_0_.put(EntityType.SILVERFISH, SoundEvents.ENTITY_PARROT_IMITATE_SILVERFISH);
      p_200609_0_.put(EntityType.SKELETON, SoundEvents.ENTITY_PARROT_IMITATE_SKELETON);
      p_200609_0_.put(EntityType.SLIME, SoundEvents.ENTITY_PARROT_IMITATE_SLIME);
      p_200609_0_.put(EntityType.SPIDER, SoundEvents.ENTITY_PARROT_IMITATE_SPIDER);
      p_200609_0_.put(EntityType.STRAY, SoundEvents.ENTITY_PARROT_IMITATE_STRAY);
      p_200609_0_.put(EntityType.VEX, SoundEvents.ENTITY_PARROT_IMITATE_VEX);
      p_200609_0_.put(EntityType.VINDICATOR, SoundEvents.ENTITY_PARROT_IMITATE_VINDICATOR);
      p_200609_0_.put(EntityType.WITCH, SoundEvents.ENTITY_PARROT_IMITATE_WITCH);
      p_200609_0_.put(EntityType.WITHER, SoundEvents.ENTITY_PARROT_IMITATE_WITHER);
      p_200609_0_.put(EntityType.WITHER_SKELETON, SoundEvents.ENTITY_PARROT_IMITATE_WITHER_SKELETON);
      p_200609_0_.put(EntityType.WOLF, SoundEvents.ENTITY_PARROT_IMITATE_WOLF);
      p_200609_0_.put(EntityType.ZOMBIE, SoundEvents.ENTITY_PARROT_IMITATE_ZOMBIE);
      p_200609_0_.put(EntityType.ZOMBIE_VILLAGER, SoundEvents.ENTITY_PARROT_IMITATE_ZOMBIE_VILLAGER);
   });
   public float flap;
   public float flapSpeed;
   public float oFlapSpeed;
   public float oFlap;
   public float flapping = 1.0F;
   private boolean partyParrot;
   private BlockPos jukeboxPosition;

   public EntityParrot(World p_i47411_1_) {
      super(EntityType.PARROT, p_i47411_1_);
      this.setSize(0.5F, 0.9F);
      this.moveHelper = new EntityFlyHelper(this);
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance p_204210_1_, @Nullable IEntityLivingData p_204210_2_, @Nullable NBTTagCompound p_204210_3_) {
      this.setVariant(this.rand.nextInt(5));
      return super.onInitialSpawn(p_204210_1_, p_204210_2_, p_204210_3_);
   }

   protected void initEntityAI() {
      this.aiSit = new EntityAISit(this);
      this.tasks.addTask(0, new EntityAIPanic(this, 1.25D));
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(2, this.aiSit);
      this.tasks.addTask(2, new EntityAIFollowOwnerFlying(this, 1.0D, 5.0F, 1.0F));
      this.tasks.addTask(2, new EntityAIWanderAvoidWaterFlying(this, 1.0D));
      this.tasks.addTask(3, new EntityAILandOnOwnersShoulder(this));
      this.tasks.addTask(3, new EntityAIFollow(this, 1.0D, 3.0F, 7.0F));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6.0D);
      this.getAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue((double)0.4F);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.2F);
   }

   protected PathNavigate createNavigator(World p_175447_1_) {
      PathNavigateFlying pathnavigateflying = new PathNavigateFlying(this, p_175447_1_);
      pathnavigateflying.setCanOpenDoors(false);
      pathnavigateflying.setCanSwim(true);
      pathnavigateflying.setCanEnterDoors(true);
      return pathnavigateflying;
   }

   public float getEyeHeight() {
      return this.height * 0.6F;
   }

   public void livingTick() {
      playMimicSound(this.world, this);
      if (this.jukeboxPosition == null || this.jukeboxPosition.distanceSq(this.posX, this.posY, this.posZ) > 12.0D || this.world.getBlockState(this.jukeboxPosition).getBlock() != Blocks.JUKEBOX) {
         this.partyParrot = false;
         this.jukeboxPosition = null;
      }

      super.livingTick();
      this.calculateFlapping();
   }

   @OnlyIn(Dist.CLIENT)
   public void setPartying(BlockPos p_191987_1_, boolean p_191987_2_) {
      this.jukeboxPosition = p_191987_1_;
      this.partyParrot = p_191987_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isPartying() {
      return this.partyParrot;
   }

   private void calculateFlapping() {
      this.oFlap = this.flap;
      this.oFlapSpeed = this.flapSpeed;
      this.flapSpeed = (float)((double)this.flapSpeed + (double)(this.onGround ? -1 : 4) * 0.3D);
      this.flapSpeed = MathHelper.clamp(this.flapSpeed, 0.0F, 1.0F);
      if (!this.onGround && this.flapping < 1.0F) {
         this.flapping = 1.0F;
      }

      this.flapping = (float)((double)this.flapping * 0.9D);
      if (!this.onGround && this.motionY < 0.0D) {
         this.motionY *= 0.6D;
      }

      this.flap += this.flapping * 2.0F;
   }

   private static boolean playMimicSound(World p_192006_0_, Entity p_192006_1_) {
      if (!p_192006_1_.isSilent() && p_192006_0_.rand.nextInt(50) == 0) {
         List<EntityLiving> list = p_192006_0_.getEntitiesWithinAABB(EntityLiving.class, p_192006_1_.getEntityBoundingBox().grow(20.0D), CAN_MIMIC);
         if (!list.isEmpty()) {
            EntityLiving entityliving = list.get(p_192006_0_.rand.nextInt(list.size()));
            if (!entityliving.isSilent()) {
               SoundEvent soundevent = func_200610_a(entityliving.getType());
               p_192006_0_.playSound((EntityPlayer)null, p_192006_1_.posX, p_192006_1_.posY, p_192006_1_.posZ, soundevent, p_192006_1_.getSoundCategory(), 0.7F, getPitch(p_192006_0_.rand));
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean processInteract(EntityPlayer p_184645_1_, EnumHand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getHeldItem(p_184645_2_);
      if (!this.isTamed() && TAME_ITEMS.contains(itemstack.getItem())) {
         if (!p_184645_1_.capabilities.isCreativeMode) {
            itemstack.shrink(1);
         }

         if (!this.isSilent()) {
            this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_PARROT_EAT, this.getSoundCategory(), 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
         }

         if (!this.world.isRemote) {
            if (this.rand.nextInt(10) == 0) {
               this.setTamedBy(p_184645_1_);
               this.playTameEffect(true);
               this.world.setEntityState(this, (byte)7);
            } else {
               this.playTameEffect(false);
               this.world.setEntityState(this, (byte)6);
            }
         }

         return true;
      } else if (itemstack.getItem() == DEADLY_ITEM) {
         if (!p_184645_1_.capabilities.isCreativeMode) {
            itemstack.shrink(1);
         }

         this.addPotionEffect(new PotionEffect(MobEffects.POISON, 900));
         if (p_184645_1_.isCreative() || !this.isInvulnerable()) {
            this.attackEntityFrom(DamageSource.causePlayerDamage(p_184645_1_), Float.MAX_VALUE);
         }

         return true;
      } else {
         if (!this.world.isRemote && !this.isFlying() && this.isTamed() && this.isOwner(p_184645_1_)) {
            this.aiSit.setSitting(!this.isSitting());
         }

         return super.processInteract(p_184645_1_, p_184645_2_);
      }
   }

   public boolean isBreedingItem(ItemStack p_70877_1_) {
      return false;
   }

   public boolean func_205020_a(IWorld p_205020_1_, boolean p_205020_2_) {
      int i = MathHelper.floor(this.posX);
      int j = MathHelper.floor(this.getEntityBoundingBox().minY);
      int k = MathHelper.floor(this.posZ);
      BlockPos blockpos = new BlockPos(i, j, k);
      Block block = p_205020_1_.getBlockState(blockpos.down()).getBlock();
      return block instanceof BlockLeaves || block == Blocks.GRASS || block instanceof BlockLog || block == Blocks.AIR && super.func_205020_a(p_205020_1_, p_205020_2_);
   }

   public void fall(float p_180430_1_, float p_180430_2_) {
   }

   protected void updateFallState(double p_184231_1_, boolean p_184231_3_, IBlockState p_184231_4_, BlockPos p_184231_5_) {
   }

   public boolean canMateWith(EntityAnimal p_70878_1_) {
      return false;
   }

   @Nullable
   public EntityAgeable createChild(EntityAgeable p_90011_1_) {
      return null;
   }

   public static void playAmbientSound(World p_192005_0_, Entity p_192005_1_) {
      if (!p_192005_1_.isSilent() && !playMimicSound(p_192005_0_, p_192005_1_) && p_192005_0_.rand.nextInt(200) == 0) {
         p_192005_0_.playSound((EntityPlayer)null, p_192005_1_.posX, p_192005_1_.posY, p_192005_1_.posZ, getAmbientSound(p_192005_0_.rand), p_192005_1_.getSoundCategory(), 1.0F, getPitch(p_192005_0_.rand));
      }

   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      return p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
   }

   @Nullable
   public SoundEvent getAmbientSound() {
      return getAmbientSound(this.rand);
   }

   private static SoundEvent getAmbientSound(Random p_192003_0_) {
      if (p_192003_0_.nextInt(1000) == 0) {
         List<EntityType<?>> list = Lists.newArrayList(IMITATION_SOUND_EVENTS.keySet());
         return func_200610_a(list.get(p_192003_0_.nextInt(list.size())));
      } else {
         return SoundEvents.ENTITY_PARROT_AMBIENT;
      }
   }

   public static SoundEvent func_200610_a(EntityType<?> p_200610_0_) {
      return IMITATION_SOUND_EVENTS.getOrDefault(p_200610_0_, SoundEvents.ENTITY_PARROT_AMBIENT);
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_PARROT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_PARROT_DEATH;
   }

   protected void playStepSound(BlockPos p_180429_1_, IBlockState p_180429_2_) {
      this.playSound(SoundEvents.ENTITY_PARROT_STEP, 0.15F, 1.0F);
   }

   protected float playFlySound(float p_191954_1_) {
      this.playSound(SoundEvents.ENTITY_PARROT_FLY, 0.15F, 1.0F);
      return p_191954_1_ + this.flapSpeed / 2.0F;
   }

   protected boolean makeFlySound() {
      return true;
   }

   protected float getSoundPitch() {
      return getPitch(this.rand);
   }

   private static float getPitch(Random p_192000_0_) {
      return (p_192000_0_.nextFloat() - p_192000_0_.nextFloat()) * 0.2F + 1.0F;
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.NEUTRAL;
   }

   public boolean canBePushed() {
      return true;
   }

   protected void collideWithEntity(Entity p_82167_1_) {
      if (!(p_82167_1_ instanceof EntityPlayer)) {
         super.collideWithEntity(p_82167_1_);
      }
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         if (this.aiSit != null) {
            this.aiSit.setSitting(false);
         }

         return super.attackEntityFrom(p_70097_1_, p_70097_2_);
      }
   }

   public int getVariant() {
      return MathHelper.clamp(this.dataManager.get(VARIANT), 0, 4);
   }

   public void setVariant(int p_191997_1_) {
      this.dataManager.set(VARIANT, p_191997_1_);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(VARIANT, 0);
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setInteger("Variant", this.getVariant());
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      this.setVariant(p_70037_1_.getInteger("Variant"));
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_PARROT;
   }

   public boolean isFlying() {
      return !this.onGround;
   }
}
