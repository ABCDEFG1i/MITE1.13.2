package net.minecraft.entity;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockAbstractSkull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.Tag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class EntityLiving extends EntityLivingBase {
   private static final DataParameter<Byte> AI_FLAGS = EntityDataManager.createKey(EntityLiving.class, DataSerializers.BYTE);
   public int livingSoundTime;
   protected int experienceValue;
   protected EntityLookHelper lookHelper;
   protected EntityMoveHelper moveHelper;
   protected EntityJumpHelper jumpHelper;
   private final EntityBodyHelper bodyHelper;
   protected PathNavigate navigator;
   public final EntityAITasks tasks;
   public final EntityAITasks targetTasks;
   private EntityLivingBase attackTarget;
   private final EntitySenses senses;
   private final NonNullList<ItemStack> inventoryHands = NonNullList.withSize(2, ItemStack.EMPTY);
   protected float[] inventoryHandsDropChances = new float[2];
   private final NonNullList<ItemStack> inventoryArmor = NonNullList.withSize(4, ItemStack.EMPTY);
   protected float[] inventoryArmorDropChances = new float[4];
   private boolean canPickUpLoot;
   private boolean persistenceRequired;
   private final Map<PathNodeType, Float> mapPathPriority = Maps.newEnumMap(PathNodeType.class);
   private ResourceLocation deathLootTable;
   private long deathLootTableSeed;
   private boolean isLeashed;
   private Entity leashHolder;
   private NBTTagCompound leashNBTTag;

   protected EntityLiving(EntityType<?> p_i48576_1_, World p_i48576_2_) {
      super(p_i48576_1_, p_i48576_2_);
      this.tasks = new EntityAITasks(p_i48576_2_ != null && p_i48576_2_.profiler != null ? p_i48576_2_.profiler : null);
      this.targetTasks = new EntityAITasks(p_i48576_2_ != null && p_i48576_2_.profiler != null ? p_i48576_2_.profiler : null);
      this.lookHelper = new EntityLookHelper(this);
      this.moveHelper = new EntityMoveHelper(this);
      this.jumpHelper = new EntityJumpHelper(this);
      this.bodyHelper = this.createBodyHelper();
      this.navigator = this.createNavigator(p_i48576_2_);
      this.senses = new EntitySenses(this);
      Arrays.fill(this.inventoryArmorDropChances, 0.085F);
      Arrays.fill(this.inventoryHandsDropChances, 0.085F);
      if (p_i48576_2_ != null && !p_i48576_2_.isRemote) {
         this.initEntityAI();
      }

   }

   protected void initEntityAI() {
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
   }

   protected PathNavigate createNavigator(World p_175447_1_) {
      return new PathNavigateGround(this, p_175447_1_);
   }

   public float getPathPriority(PathNodeType p_184643_1_) {
      Float f = this.mapPathPriority.get(p_184643_1_);
      return f == null ? p_184643_1_.getPriority() : f;
   }

   public void setPathPriority(PathNodeType p_184644_1_, float p_184644_2_) {
      this.mapPathPriority.put(p_184644_1_, p_184644_2_);
   }

   protected EntityBodyHelper createBodyHelper() {
      return new EntityBodyHelper(this);
   }

   public EntityLookHelper getLookHelper() {
      return this.lookHelper;
   }

   public EntityMoveHelper getMoveHelper() {
      return this.moveHelper;
   }

   public EntityJumpHelper getJumpHelper() {
      return this.jumpHelper;
   }

   public PathNavigate getNavigator() {
      return this.navigator;
   }

   public EntitySenses getEntitySenses() {
      return this.senses;
   }

   @Nullable
   public EntityLivingBase getAttackTarget() {
      return this.attackTarget;
   }

   public void setAttackTarget(@Nullable EntityLivingBase p_70624_1_) {
      this.attackTarget = p_70624_1_;
   }

   public boolean canAttackClass(Class<? extends EntityLivingBase> p_70686_1_) {
      return p_70686_1_ != EntityGhast.class;
   }

   public void eatGrassBonus() {
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(AI_FLAGS, (byte)0);
   }

   public int getTalkInterval() {
      return 80;
   }

   public void playAmbientSound() {
      SoundEvent soundevent = this.getAmbientSound();
      if (soundevent != null) {
         this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
      }

   }

   public void baseTick() {
      super.baseTick();
      this.world.profiler.startSection("mobBaseTick");
      if (this.isEntityAlive() && this.rand.nextInt(1000) < this.livingSoundTime++) {
         this.applyEntityAI();
         this.playAmbientSound();
      }

      this.world.profiler.endSection();
   }

   protected void playHurtSound(DamageSource p_184581_1_) {
      this.applyEntityAI();
      super.playHurtSound(p_184581_1_);
   }

   private void applyEntityAI() {
      this.livingSoundTime = -this.getTalkInterval();
   }

   protected int getExperiencePoints(EntityPlayer p_70693_1_) {
      if (this.experienceValue > 0) {
         int i = this.experienceValue;

         for(int j = 0; j < this.inventoryArmor.size(); ++j) {
            if (!this.inventoryArmor.get(j).isEmpty() && this.inventoryArmorDropChances[j] <= 1.0F) {
               i += 1 + this.rand.nextInt(3);
            }
         }

         for(int k = 0; k < this.inventoryHands.size(); ++k) {
            if (!this.inventoryHands.get(k).isEmpty() && this.inventoryHandsDropChances[k] <= 1.0F) {
               i += 1 + this.rand.nextInt(3);
            }
         }

         return i;
      } else {
         return this.experienceValue;
      }
   }

   public void spawnExplosionParticle() {
      if (this.world.isRemote) {
         for(int i = 0; i < 20; ++i) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            double d3 = 10.0D;
            this.world.spawnParticle(Particles.POOF, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width - d0 * 10.0D, this.posY + (double)(this.rand.nextFloat() * this.height) - d1 * 10.0D, this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width - d2 * 10.0D, d0, d1, d2);
         }
      } else {
         this.world.setEntityState(this, (byte)20);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 20) {
         this.spawnExplosionParticle();
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   public void tick() {
      super.tick();
      if (!this.world.isRemote) {
         this.updateLeashedState();
         if (this.ticksExisted % 5 == 0) {
            boolean flag = !(this.getControllingPassenger() instanceof EntityLiving);
            boolean flag1 = !(this.getRidingEntity() instanceof EntityBoat);
            this.tasks.setControlFlag(1, flag);
            this.tasks.setControlFlag(4, flag && flag1);
            this.tasks.setControlFlag(2, flag);
         }
      }

   }

   protected float updateDistance(float p_110146_1_, float p_110146_2_) {
      this.bodyHelper.updateRenderAngles();
      return p_110146_2_;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return null;
   }

   @Nullable
   protected Item getDropItem() {
      return null;
   }

   protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
      Item item = this.getDropItem();
      if (item != null) {
         int i = this.rand.nextInt(3);
         if (p_70628_2_ > 0) {
            i += this.rand.nextInt(p_70628_2_ + 1);
         }

         for(int j = 0; j < i; ++j) {
            this.entityDropItem(item);
         }
      }

   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      p_70014_1_.setBoolean("CanPickUpLoot", this.canPickUpLoot());
      p_70014_1_.setBoolean("PersistenceRequired", this.persistenceRequired);
      NBTTagList nbttaglist = new NBTTagList();

      for(ItemStack itemstack : this.inventoryArmor) {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         if (!itemstack.isEmpty()) {
            itemstack.write(nbttagcompound);
         }

         nbttaglist.add(nbttagcompound);
      }

      p_70014_1_.setTag("ArmorItems", nbttaglist);
      NBTTagList nbttaglist1 = new NBTTagList();

      for(ItemStack itemstack1 : this.inventoryHands) {
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();
         if (!itemstack1.isEmpty()) {
            itemstack1.write(nbttagcompound1);
         }

         nbttaglist1.add(nbttagcompound1);
      }

      p_70014_1_.setTag("HandItems", nbttaglist1);
      NBTTagList nbttaglist2 = new NBTTagList();

      for(float f : this.inventoryArmorDropChances) {
         nbttaglist2.add(new NBTTagFloat(f));
      }

      p_70014_1_.setTag("ArmorDropChances", nbttaglist2);
      NBTTagList nbttaglist3 = new NBTTagList();

      for(float f1 : this.inventoryHandsDropChances) {
         nbttaglist3.add(new NBTTagFloat(f1));
      }

      p_70014_1_.setTag("HandDropChances", nbttaglist3);
      p_70014_1_.setBoolean("Leashed", this.isLeashed);
      if (this.leashHolder != null) {
         NBTTagCompound nbttagcompound2 = new NBTTagCompound();
         if (this.leashHolder instanceof EntityLivingBase) {
            UUID uuid = this.leashHolder.getUniqueID();
            nbttagcompound2.setUniqueId("UUID", uuid);
         } else if (this.leashHolder instanceof EntityHanging) {
            BlockPos blockpos = ((EntityHanging)this.leashHolder).getHangingPosition();
            nbttagcompound2.setInteger("X", blockpos.getX());
            nbttagcompound2.setInteger("Y", blockpos.getY());
            nbttagcompound2.setInteger("Z", blockpos.getZ());
         }

         p_70014_1_.setTag("Leash", nbttagcompound2);
      }

      p_70014_1_.setBoolean("LeftHanded", this.isLeftHanded());
      if (this.deathLootTable != null) {
         p_70014_1_.setString("DeathLootTable", this.deathLootTable.toString());
         if (this.deathLootTableSeed != 0L) {
            p_70014_1_.setLong("DeathLootTableSeed", this.deathLootTableSeed);
         }
      }

      if (this.isAIDisabled()) {
         p_70014_1_.setBoolean("NoAI", this.isAIDisabled());
      }

   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      if (p_70037_1_.hasKey("CanPickUpLoot", 1)) {
         this.setCanPickUpLoot(p_70037_1_.getBoolean("CanPickUpLoot"));
      }

      this.persistenceRequired = p_70037_1_.getBoolean("PersistenceRequired");
      if (p_70037_1_.hasKey("ArmorItems", 9)) {
         NBTTagList nbttaglist = p_70037_1_.getTagList("ArmorItems", 10);

         for(int i = 0; i < this.inventoryArmor.size(); ++i) {
            this.inventoryArmor.set(i, ItemStack.loadFromNBT(nbttaglist.getCompoundTagAt(i)));
         }
      }

      if (p_70037_1_.hasKey("HandItems", 9)) {
         NBTTagList nbttaglist1 = p_70037_1_.getTagList("HandItems", 10);

         for(int j = 0; j < this.inventoryHands.size(); ++j) {
            this.inventoryHands.set(j, ItemStack.loadFromNBT(nbttaglist1.getCompoundTagAt(j)));
         }
      }

      if (p_70037_1_.hasKey("ArmorDropChances", 9)) {
         NBTTagList nbttaglist2 = p_70037_1_.getTagList("ArmorDropChances", 5);

         for(int k = 0; k < nbttaglist2.size(); ++k) {
            this.inventoryArmorDropChances[k] = nbttaglist2.getFloatAt(k);
         }
      }

      if (p_70037_1_.hasKey("HandDropChances", 9)) {
         NBTTagList nbttaglist3 = p_70037_1_.getTagList("HandDropChances", 5);

         for(int l = 0; l < nbttaglist3.size(); ++l) {
            this.inventoryHandsDropChances[l] = nbttaglist3.getFloatAt(l);
         }
      }

      this.isLeashed = p_70037_1_.getBoolean("Leashed");
      if (this.isLeashed && p_70037_1_.hasKey("Leash", 10)) {
         this.leashNBTTag = p_70037_1_.getCompoundTag("Leash");
      }

      this.setLeftHanded(p_70037_1_.getBoolean("LeftHanded"));
      if (p_70037_1_.hasKey("DeathLootTable", 8)) {
         this.deathLootTable = new ResourceLocation(p_70037_1_.getString("DeathLootTable"));
         this.deathLootTableSeed = p_70037_1_.getLong("DeathLootTableSeed");
      }

      this.setNoAI(p_70037_1_.getBoolean("NoAI"));
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return null;
   }

   protected void dropLoot(boolean p_184610_1_, int p_184610_2_, DamageSource p_184610_3_) {
      ResourceLocation resourcelocation = this.deathLootTable;
      if (resourcelocation == null) {
         resourcelocation = this.getLootTable();
      }

      if (resourcelocation != null) {
         LootTable loottable = this.world.getServer().getLootTableManager().getLootTableFromLocation(resourcelocation);
         this.deathLootTable = null;
         LootContext.Builder lootcontext$builder = (new LootContext.Builder((WorldServer)this.world)).withLootedEntity(this).withDamageSource(p_184610_3_).withPosition(new BlockPos(this));
         if (p_184610_1_ && this.attackingPlayer != null) {
            lootcontext$builder = lootcontext$builder.withPlayer(this.attackingPlayer).withLuck(this.attackingPlayer.getLuck());
         }

         for(ItemStack itemstack : loottable.generateLootForPools(this.deathLootTableSeed == 0L ? this.rand : new Random(this.deathLootTableSeed), lootcontext$builder.build())) {
            this.entityDropItem(itemstack);
         }

         this.dropEquipment(p_184610_1_, p_184610_2_);
      } else {
         super.dropLoot(p_184610_1_, p_184610_2_, p_184610_3_);
      }

   }

   public void setMoveForward(float p_191989_1_) {
      this.moveForward = p_191989_1_;
   }

   public void setMoveVertical(float p_70657_1_) {
      this.moveVertical = p_70657_1_;
   }

   public void setMoveStrafing(float p_184646_1_) {
      this.moveStrafing = p_184646_1_;
   }

   public void setAIMoveSpeed(float p_70659_1_) {
      super.setAIMoveSpeed(p_70659_1_);
      this.setMoveForward(p_70659_1_);
   }

   public void livingTick() {
      super.livingTick();
      this.world.profiler.startSection("looting");
      if (!this.world.isRemote && this.canPickUpLoot() && !this.dead && this.world.getGameRules().getBoolean("mobGriefing")) {
         for(EntityItem entityitem : this.world.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().grow(1.0D, 0.0D, 1.0D))) {
            if (!entityitem.isDead && !entityitem.getItem().isEmpty() && !entityitem.cannotPickup()) {
               this.updateEquipmentIfNeeded(entityitem);
            }
         }
      }

      this.world.profiler.endSection();
   }

   protected void updateEquipmentIfNeeded(EntityItem p_175445_1_) {
      ItemStack itemstack = p_175445_1_.getItem();
      EntityEquipmentSlot entityequipmentslot = getSlotForItemStack(itemstack);
      ItemStack itemstack1 = this.getItemStackFromSlot(entityequipmentslot);
      boolean flag = this.shouldExchangeEquipment(itemstack, itemstack1, entityequipmentslot);
      if (flag && this.canEquipItem(itemstack)) {
         double d0 = (double)this.func_205712_c(entityequipmentslot);
         if (!itemstack1.isEmpty() && (double)(this.rand.nextFloat() - 0.1F) < d0) {
            this.entityDropItem(itemstack1);
         }

         this.setItemStackToSlot(entityequipmentslot, itemstack);
         switch(entityequipmentslot.getSlotType()) {
         case HAND:
            this.inventoryHandsDropChances[entityequipmentslot.getIndex()] = 2.0F;
            break;
         case ARMOR:
            this.inventoryArmorDropChances[entityequipmentslot.getIndex()] = 2.0F;
         }

         this.persistenceRequired = true;
         this.onItemPickup(p_175445_1_, itemstack.getCount());
         p_175445_1_.setDead();
      }

   }

   protected boolean shouldExchangeEquipment(ItemStack p_208003_1_, ItemStack p_208003_2_, EntityEquipmentSlot p_208003_3_) {
      boolean flag = true;
      if (!p_208003_2_.isEmpty()) {
         if (p_208003_3_.getSlotType() == EntityEquipmentSlot.Type.HAND) {
            if (p_208003_1_.getItem() instanceof ItemSword && !(p_208003_2_.getItem() instanceof ItemSword)) {
               flag = true;
            } else if (p_208003_1_.getItem() instanceof ItemSword && p_208003_2_.getItem() instanceof ItemSword) {
               ItemSword itemsword = (ItemSword)p_208003_1_.getItem();
               ItemSword itemsword1 = (ItemSword)p_208003_2_.getItem();
               if (itemsword.getAttackDamage() == itemsword1.getAttackDamage()) {
                  flag = p_208003_1_.getDamage() < p_208003_2_.getDamage() || p_208003_1_.hasTag() && !p_208003_2_.hasTag();
               } else {
                  flag = itemsword.getAttackDamage() > itemsword1.getAttackDamage();
               }
            } else if (p_208003_1_.getItem() instanceof ItemBow && p_208003_2_.getItem() instanceof ItemBow) {
               flag = p_208003_1_.hasTag() && !p_208003_2_.hasTag();
            } else {
               flag = false;
            }
         } else if (p_208003_1_.getItem() instanceof ItemArmor && !(p_208003_2_.getItem() instanceof ItemArmor)) {
            flag = true;
         } else if (p_208003_1_.getItem() instanceof ItemArmor && p_208003_2_.getItem() instanceof ItemArmor && !EnchantmentHelper.hasBindingCurse(p_208003_2_)) {
            ItemArmor itemarmor = (ItemArmor)p_208003_1_.getItem();
            ItemArmor itemarmor1 = (ItemArmor)p_208003_2_.getItem();
            if (itemarmor.getDamageReduceAmount() == itemarmor1.getDamageReduceAmount()) {
               flag = p_208003_1_.getDamage() < p_208003_2_.getDamage() || p_208003_1_.hasTag() && !p_208003_2_.hasTag();
            } else {
               flag = itemarmor.getDamageReduceAmount() > itemarmor1.getDamageReduceAmount();
            }
         } else {
            flag = false;
         }
      }

      return flag;
   }

   protected boolean canEquipItem(ItemStack p_175448_1_) {
      return true;
   }

   public boolean canDespawn() {
      return true;
   }

   protected void checkDespawn() {
      if (this.persistenceRequired) {
         this.idleTime = 0;
      } else {
         Entity entity = this.world.getClosestPlayerToEntity(this, -1.0D);
         if (entity != null) {
            double d0 = entity.posX - this.posX;
            double d1 = entity.posY - this.posY;
            double d2 = entity.posZ - this.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            if (this.canDespawn() && d3 > 16384.0D) {
               this.setDead();
            }

            if (this.idleTime > 600 && this.rand.nextInt(800) == 0 && d3 > 1024.0D && this.canDespawn()) {
               this.setDead();
            } else if (d3 < 1024.0D) {
               this.idleTime = 0;
            }
         }

      }
   }

   protected final void updateEntityActionState() {
      ++this.idleTime;
      this.world.profiler.startSection("checkDespawn");
      this.checkDespawn();
      this.world.profiler.endSection();
      this.world.profiler.startSection("sensing");
      this.senses.tick();
      this.world.profiler.endSection();
      this.world.profiler.startSection("targetSelector");
      this.targetTasks.tick();
      this.world.profiler.endSection();
      this.world.profiler.startSection("goalSelector");
      this.tasks.tick();
      this.world.profiler.endSection();
      this.world.profiler.startSection("navigation");
      this.navigator.tick();
      this.world.profiler.endSection();
      this.world.profiler.startSection("mob tick");
      this.updateAITasks();
      this.world.profiler.endSection();
      if (this.isRiding() && this.getRidingEntity() instanceof EntityLiving) {
         EntityLiving entityliving = (EntityLiving)this.getRidingEntity();
         entityliving.getNavigator().setPath(this.getNavigator().getPath(), 1.5D);
         entityliving.getMoveHelper().read(this.getMoveHelper());
      }

      this.world.profiler.startSection("controls");
      this.world.profiler.startSection("move");
      this.moveHelper.tick();
      this.world.profiler.endStartSection("look");
      this.lookHelper.tick();
      this.world.profiler.endStartSection("jump");
      this.jumpHelper.tick();
      this.world.profiler.endSection();
      this.world.profiler.endSection();
   }

   protected void updateAITasks() {
   }

   public int getVerticalFaceSpeed() {
      return 40;
   }

   public int getHorizontalFaceSpeed() {
      return 10;
   }

   public void faceEntity(Entity p_70625_1_, float p_70625_2_, float p_70625_3_) {
      double d0 = p_70625_1_.posX - this.posX;
      double d2 = p_70625_1_.posZ - this.posZ;
      double d1;
      if (p_70625_1_ instanceof EntityLivingBase) {
         EntityLivingBase entitylivingbase = (EntityLivingBase)p_70625_1_;
         d1 = entitylivingbase.posY + (double)entitylivingbase.getEyeHeight() - (this.posY + (double)this.getEyeHeight());
      } else {
         d1 = (p_70625_1_.getEntityBoundingBox().minY + p_70625_1_.getEntityBoundingBox().maxY) / 2.0D - (this.posY + (double)this.getEyeHeight());
      }

      double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
      float f = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
      float f1 = (float)(-(MathHelper.atan2(d1, d3) * (double)(180F / (float)Math.PI)));
      this.rotationPitch = this.updateRotation(this.rotationPitch, f1, p_70625_3_);
      this.rotationYaw = this.updateRotation(this.rotationYaw, f, p_70625_2_);
   }

   private float updateRotation(float p_70663_1_, float p_70663_2_, float p_70663_3_) {
      float f = MathHelper.wrapDegrees(p_70663_2_ - p_70663_1_);
      if (f > p_70663_3_) {
         f = p_70663_3_;
      }

      if (f < -p_70663_3_) {
         f = -p_70663_3_;
      }

      return p_70663_1_ + f;
   }

   public boolean func_205020_a(IWorld p_205020_1_, boolean p_205020_2_) {
      IBlockState iblockstate = p_205020_1_.getBlockState((new BlockPos(this)).down());
      return iblockstate.canEntitySpawn(this);
   }

   public final boolean isNotColliding() {
      return this.isNotColliding(this.world);
   }

   public boolean isNotColliding(IWorldReaderBase p_205019_1_) {
      return !p_205019_1_.containsAnyLiquid(this.getEntityBoundingBox()) && p_205019_1_.isCollisionBoxesEmpty(this, this.getEntityBoundingBox()) && p_205019_1_.checkNoEntityCollision(this, this.getEntityBoundingBox());
   }

   @OnlyIn(Dist.CLIENT)
   public float getRenderSizeModifier() {
      return 1.0F;
   }

   public int getMaxSpawnedInChunk() {
      return 4;
   }

   public boolean func_204209_c(int p_204209_1_) {
      return false;
   }

   public int getMaxFallHeight() {
      if (this.getAttackTarget() == null) {
         return 3;
      } else {
         int i = (int)(this.getHealth() - this.getMaxHealth() * 0.33F);
         i = i - (3 - this.world.getDifficulty().getId()) * 4;
         if (i < 0) {
            i = 0;
         }

         return i + 3;
      }
   }

   public Iterable<ItemStack> getHeldEquipment() {
      return this.inventoryHands;
   }

   public Iterable<ItemStack> getArmorInventoryList() {
      return this.inventoryArmor;
   }

   public ItemStack getItemStackFromSlot(EntityEquipmentSlot p_184582_1_) {
      switch(p_184582_1_.getSlotType()) {
      case HAND:
         return this.inventoryHands.get(p_184582_1_.getIndex());
      case ARMOR:
         return this.inventoryArmor.get(p_184582_1_.getIndex());
      default:
         return ItemStack.EMPTY;
      }
   }

   public void setItemStackToSlot(EntityEquipmentSlot p_184201_1_, ItemStack p_184201_2_) {
      switch(p_184201_1_.getSlotType()) {
      case HAND:
         this.inventoryHands.set(p_184201_1_.getIndex(), p_184201_2_);
         break;
      case ARMOR:
         this.inventoryArmor.set(p_184201_1_.getIndex(), p_184201_2_);
      }

   }

   protected void dropEquipment(boolean p_82160_1_, int p_82160_2_) {
      for(EntityEquipmentSlot entityequipmentslot : EntityEquipmentSlot.values()) {
         ItemStack itemstack = this.getItemStackFromSlot(entityequipmentslot);
         float f = this.func_205712_c(entityequipmentslot);
         boolean flag = f > 1.0F;
         if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack) && (p_82160_1_ || flag) && this.rand.nextFloat() - (float)p_82160_2_ * 0.01F < f) {
            if (!flag && itemstack.isDamageable()) {
               itemstack.setDamage(itemstack.getMaxDamage() - this.rand.nextInt(1 + this.rand.nextInt(Math.max(itemstack.getMaxDamage() - 3, 1))));
            }

            this.entityDropItem(itemstack);
         }
      }

   }

   protected float func_205712_c(EntityEquipmentSlot p_205712_1_) {
      float f;
      switch(p_205712_1_.getSlotType()) {
      case HAND:
         f = this.inventoryHandsDropChances[p_205712_1_.getIndex()];
         break;
      case ARMOR:
         f = this.inventoryArmorDropChances[p_205712_1_.getIndex()];
         break;
      default:
         f = 0.0F;
      }

      return f;
   }

   protected void setEquipmentBasedOnDifficulty(DifficultyInstance p_180481_1_) {
      if (this.rand.nextFloat() < 0.15F * p_180481_1_.getClampedAdditionalDifficulty()) {
         int i = this.rand.nextInt(2);
         float f = this.world.getDifficulty() == EnumDifficulty.HARD ? 0.1F : 0.25F;
         if (this.rand.nextFloat() < 0.095F) {
            ++i;
         }

         if (this.rand.nextFloat() < 0.095F) {
            ++i;
         }

         if (this.rand.nextFloat() < 0.095F) {
            ++i;
         }

         boolean flag = true;

         for(EntityEquipmentSlot entityequipmentslot : EntityEquipmentSlot.values()) {
            if (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR) {
               ItemStack itemstack = this.getItemStackFromSlot(entityequipmentslot);
               if (!flag && this.rand.nextFloat() < f) {
                  break;
               }

               flag = false;
               if (itemstack.isEmpty()) {
                  Item item = getArmorByChance(entityequipmentslot, i);
                  if (item != null) {
                     this.setItemStackToSlot(entityequipmentslot, new ItemStack(item));
                  }
               }
            }
         }
      }

   }

   public static EntityEquipmentSlot getSlotForItemStack(ItemStack p_184640_0_) {
      Item item = p_184640_0_.getItem();
      if (item != Blocks.CARVED_PUMPKIN.asItem() && (!(item instanceof ItemBlock) || !(((ItemBlock)item).getBlock() instanceof BlockAbstractSkull))) {
         if (item instanceof ItemArmor) {
            return ((ItemArmor)item).getEquipmentSlot();
         } else if (item == Items.ELYTRA) {
            return EntityEquipmentSlot.CHEST;
         } else {
            return item == Items.SHIELD ? EntityEquipmentSlot.OFFHAND : EntityEquipmentSlot.MAINHAND;
         }
      } else {
         return EntityEquipmentSlot.HEAD;
      }
   }

   @Nullable
   public static Item getArmorByChance(EntityEquipmentSlot p_184636_0_, int p_184636_1_) {
      switch(p_184636_0_) {
      case HEAD:
         if (p_184636_1_ == 0) {
            return Items.LEATHER_HELMET;
         } else if (p_184636_1_ == 1) {
            return Items.GOLDEN_HELMET;
         } else if (p_184636_1_ == 2) {
            return Items.CHAINMAIL_HELMET;
         } else if (p_184636_1_ == 3) {
            return Items.IRON_HELMET;
         } else if (p_184636_1_ == 4) {
            return Items.DIAMOND_HELMET;
         }
      case CHEST:
         if (p_184636_1_ == 0) {
            return Items.LEATHER_CHESTPLATE;
         } else if (p_184636_1_ == 1) {
            return Items.GOLDEN_CHESTPLATE;
         } else if (p_184636_1_ == 2) {
            return Items.CHAINMAIL_CHESTPLATE;
         } else if (p_184636_1_ == 3) {
            return Items.IRON_CHESTPLATE;
         } else if (p_184636_1_ == 4) {
            return Items.DIAMOND_CHESTPLATE;
         }
      case LEGS:
         if (p_184636_1_ == 0) {
            return Items.LEATHER_LEGGINGS;
         } else if (p_184636_1_ == 1) {
            return Items.GOLDEN_LEGGINGS;
         } else if (p_184636_1_ == 2) {
            return Items.CHAINMAIL_LEGGINGS;
         } else if (p_184636_1_ == 3) {
            return Items.IRON_LEGGINGS;
         } else if (p_184636_1_ == 4) {
            return Items.DIAMOND_LEGGINGS;
         }
      case FEET:
         if (p_184636_1_ == 0) {
            return Items.LEATHER_BOOTS;
         } else if (p_184636_1_ == 1) {
            return Items.GOLDEN_BOOTS;
         } else if (p_184636_1_ == 2) {
            return Items.CHAINMAIL_BOOTS;
         } else if (p_184636_1_ == 3) {
            return Items.IRON_BOOTS;
         } else if (p_184636_1_ == 4) {
            return Items.DIAMOND_BOOTS;
         }
      default:
         return null;
      }
   }

   protected void setEnchantmentBasedOnDifficulty(DifficultyInstance p_180483_1_) {
      float f = p_180483_1_.getClampedAdditionalDifficulty();
      if (!this.getHeldItemMainhand().isEmpty() && this.rand.nextFloat() < 0.25F * f) {
         this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, EnchantmentHelper.addRandomEnchantment(this.rand, this.getHeldItemMainhand(), (int)(5.0F + f * (float)this.rand.nextInt(18)), false));
      }

      for(EntityEquipmentSlot entityequipmentslot : EntityEquipmentSlot.values()) {
         if (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR) {
            ItemStack itemstack = this.getItemStackFromSlot(entityequipmentslot);
            if (!itemstack.isEmpty() && this.rand.nextFloat() < 0.5F * f) {
               this.setItemStackToSlot(entityequipmentslot, EnchantmentHelper.addRandomEnchantment(this.rand, itemstack, (int)(5.0F + f * (float)this.rand.nextInt(18)), false));
            }
         }
      }

   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance p_204210_1_, @Nullable IEntityLivingData p_204210_2_, @Nullable NBTTagCompound p_204210_3_) {
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextGaussian() * 0.05D, 1));
      if (this.rand.nextFloat() < 0.05F) {
         this.setLeftHanded(true);
      } else {
         this.setLeftHanded(false);
      }

      return p_204210_2_;
   }

   public boolean canBeSteered() {
      return false;
   }

   public void enablePersistence() {
      this.persistenceRequired = true;
   }

   public void setDropChance(EntityEquipmentSlot p_184642_1_, float p_184642_2_) {
      switch(p_184642_1_.getSlotType()) {
      case HAND:
         this.inventoryHandsDropChances[p_184642_1_.getIndex()] = p_184642_2_;
         break;
      case ARMOR:
         this.inventoryArmorDropChances[p_184642_1_.getIndex()] = p_184642_2_;
      }

   }

   public boolean canPickUpLoot() {
      return this.canPickUpLoot;
   }

   public void setCanPickUpLoot(boolean p_98053_1_) {
      this.canPickUpLoot = p_98053_1_;
   }

   public boolean isNoDespawnRequired() {
      return this.persistenceRequired;
   }

   public final boolean processInitialInteract(EntityPlayer p_184230_1_, EnumHand p_184230_2_) {
      if (this.getLeashed() && this.getLeashHolder() == p_184230_1_) {
         this.clearLeashed(true, !p_184230_1_.capabilities.isCreativeMode);
         return true;
      } else {
         ItemStack itemstack = p_184230_1_.getHeldItem(p_184230_2_);
         if (itemstack.getItem() == Items.LEAD && this.canBeLeashedTo(p_184230_1_)) {
            this.setLeashHolder(p_184230_1_, true);
            itemstack.shrink(1);
            return true;
         } else {
            return this.processInteract(p_184230_1_, p_184230_2_) || super.processInitialInteract(p_184230_1_,
                    p_184230_2_);
         }
      }
   }

   protected boolean processInteract(EntityPlayer p_184645_1_, EnumHand p_184645_2_) {
      return false;
   }

   protected void updateLeashedState() {
      if (this.leashNBTTag != null) {
         this.recreateLeash();
      }

      if (this.isLeashed) {
         if (!this.isEntityAlive()) {
            this.clearLeashed(true, true);
         }

         if (this.leashHolder == null || this.leashHolder.isDead) {
            this.clearLeashed(true, true);
         }
      }
   }

   public void clearLeashed(boolean p_110160_1_, boolean p_110160_2_) {
      if (this.isLeashed) {
         this.isLeashed = false;
         this.leashHolder = null;
         if (!this.world.isRemote && p_110160_2_) {
            this.entityDropItem(Items.LEAD);
         }

         if (!this.world.isRemote && p_110160_1_ && this.world instanceof WorldServer) {
            ((WorldServer)this.world).getEntityTracker().sendToTracking(this, new SPacketEntityAttach(this, null));
         }
      }

   }

   public boolean canBeLeashedTo(EntityPlayer p_184652_1_) {
      return !this.getLeashed() && !(this instanceof IMob);
   }

   public boolean getLeashed() {
      return this.isLeashed;
   }

   public Entity getLeashHolder() {
      return this.leashHolder;
   }

   public void setLeashHolder(Entity p_110162_1_, boolean p_110162_2_) {
      this.isLeashed = true;
      this.leashHolder = p_110162_1_;
      if (!this.world.isRemote && p_110162_2_ && this.world instanceof WorldServer) {
         ((WorldServer)this.world).getEntityTracker().sendToTracking(this, new SPacketEntityAttach(this, this.leashHolder));
      }

      if (this.isRiding()) {
         this.dismountRidingEntity();
      }

   }

   public boolean startRiding(Entity p_184205_1_, boolean p_184205_2_) {
      boolean flag = super.startRiding(p_184205_1_, p_184205_2_);
      if (flag && this.getLeashed()) {
         this.clearLeashed(true, true);
      }

      return flag;
   }

   private void recreateLeash() {
      if (this.isLeashed && this.leashNBTTag != null) {
         if (this.leashNBTTag.hasUniqueId("UUID")) {
            UUID uuid = this.leashNBTTag.getUniqueId("UUID");

            for(EntityLivingBase entitylivingbase : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(10.0D))) {
               if (entitylivingbase.getUniqueID().equals(uuid)) {
                  this.setLeashHolder(entitylivingbase, true);
                  break;
               }
            }
         } else if (this.leashNBTTag.hasKey("X", 99) && this.leashNBTTag.hasKey("Y", 99) && this.leashNBTTag.hasKey("Z", 99)) {
            BlockPos blockpos = new BlockPos(this.leashNBTTag.getInteger("X"), this.leashNBTTag.getInteger("Y"), this.leashNBTTag.getInteger("Z"));
            EntityLeashKnot entityleashknot = EntityLeashKnot.getKnotForPosition(this.world, blockpos);
            if (entityleashknot == null) {
               entityleashknot = EntityLeashKnot.createKnot(this.world, blockpos);
            }

            this.setLeashHolder(entityleashknot, true);
         } else {
            this.clearLeashed(false, true);
         }
      }

      this.leashNBTTag = null;
   }

   public boolean replaceItemInInventory(int p_174820_1_, ItemStack p_174820_2_) {
      EntityEquipmentSlot entityequipmentslot;
      if (p_174820_1_ == 98) {
         entityequipmentslot = EntityEquipmentSlot.MAINHAND;
      } else if (p_174820_1_ == 99) {
         entityequipmentslot = EntityEquipmentSlot.OFFHAND;
      } else if (p_174820_1_ == 100 + EntityEquipmentSlot.HEAD.getIndex()) {
         entityequipmentslot = EntityEquipmentSlot.HEAD;
      } else if (p_174820_1_ == 100 + EntityEquipmentSlot.CHEST.getIndex()) {
         entityequipmentslot = EntityEquipmentSlot.CHEST;
      } else if (p_174820_1_ == 100 + EntityEquipmentSlot.LEGS.getIndex()) {
         entityequipmentslot = EntityEquipmentSlot.LEGS;
      } else {
         if (p_174820_1_ != 100 + EntityEquipmentSlot.FEET.getIndex()) {
            return false;
         }

         entityequipmentslot = EntityEquipmentSlot.FEET;
      }

      if (!p_174820_2_.isEmpty() && !isItemStackInSlot(entityequipmentslot, p_174820_2_) && entityequipmentslot != EntityEquipmentSlot.HEAD) {
         return false;
      } else {
         this.setItemStackToSlot(entityequipmentslot, p_174820_2_);
         return true;
      }
   }

   public boolean canPassengerSteer() {
      return this.canBeSteered() && super.canPassengerSteer();
   }

   public static boolean isItemStackInSlot(EntityEquipmentSlot p_184648_0_, ItemStack p_184648_1_) {
      EntityEquipmentSlot entityequipmentslot = getSlotForItemStack(p_184648_1_);
      return entityequipmentslot == p_184648_0_ || entityequipmentslot == EntityEquipmentSlot.MAINHAND && p_184648_0_ == EntityEquipmentSlot.OFFHAND || entityequipmentslot == EntityEquipmentSlot.OFFHAND && p_184648_0_ == EntityEquipmentSlot.MAINHAND;
   }

   public boolean isServerWorld() {
      return super.isServerWorld() && !this.isAIDisabled();
   }

   public void setNoAI(boolean p_94061_1_) {
      byte b0 = this.dataManager.get(AI_FLAGS);
      this.dataManager.set(AI_FLAGS, p_94061_1_ ? (byte)(b0 | 1) : (byte)(b0 & -2));
   }

   public void setLeftHanded(boolean p_184641_1_) {
      byte b0 = this.dataManager.get(AI_FLAGS);
      this.dataManager.set(AI_FLAGS, p_184641_1_ ? (byte)(b0 | 2) : (byte)(b0 & -3));
   }

   public boolean isAIDisabled() {
      return (this.dataManager.get(AI_FLAGS) & 1) != 0;
   }

   public boolean isLeftHanded() {
      return (this.dataManager.get(AI_FLAGS) & 2) != 0;
   }

   public EnumHandSide getPrimaryHand() {
      return this.isLeftHanded() ? EnumHandSide.LEFT : EnumHandSide.RIGHT;
   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      float f = (float)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
      int i = 0;
      if (p_70652_1_ instanceof EntityLivingBase) {
         f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((EntityLivingBase)p_70652_1_).getCreatureAttribute());
         i += EnchantmentHelper.getKnockbackModifier(this);
      }

      boolean flag = p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), f);
      if (flag) {
         if (i > 0 && p_70652_1_ instanceof EntityLivingBase) {
            ((EntityLivingBase)p_70652_1_).knockBack(this, (float)i * 0.5F, (double)MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F)), (double)(-MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F))));
            this.motionX *= 0.6D;
            this.motionZ *= 0.6D;
         }

         int j = EnchantmentHelper.getFireAspectModifier(this);
         if (j > 0) {
            p_70652_1_.setFire(j * 4);
         }

         if (p_70652_1_ instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)p_70652_1_;
            ItemStack itemstack = this.getHeldItemMainhand();
            ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.EMPTY;
            if (!itemstack.isEmpty() && !itemstack1.isEmpty() && itemstack.getItem() instanceof ItemAxe && itemstack1.getItem() == Items.SHIELD) {
               float f1 = 0.25F + (float)EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;
               if (this.rand.nextFloat() < f1) {
                  entityplayer.getCooldownTracker().setCooldown(Items.SHIELD, 100);
                  this.world.setEntityState(entityplayer, (byte)30);
               }
            }
         }

         this.applyEnchantments(this, p_70652_1_);
      }

      return flag;
   }

   protected boolean isInDaylight() {
      if (this.world.isDaytime() && !this.world.isRemote) {
         float f = this.getBrightness();
         BlockPos blockpos = this.getRidingEntity() instanceof EntityBoat ? (new BlockPos(this.posX, (double)Math.round(this.posY), this.posZ)).up() : new BlockPos(this.posX, (double)Math.round(this.posY), this.posZ);
          return f > 0.5F && this.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.world.canSeeSky(blockpos);
      }

      return false;
   }

   protected void handleFluidJump(Tag<Fluid> p_180466_1_) {
      if (this.getNavigator().getCanSwim()) {
         super.handleFluidJump(p_180466_1_);
      } else {
         this.motionY += (double)0.3F;
      }

   }
}
