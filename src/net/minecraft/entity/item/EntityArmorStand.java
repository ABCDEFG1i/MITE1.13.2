package net.minecraft.entity.item;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityArmorStand extends EntityLivingBase {
   private static final Rotations DEFAULT_HEAD_ROTATION = new Rotations(0.0F, 0.0F, 0.0F);
   private static final Rotations DEFAULT_BODY_ROTATION = new Rotations(0.0F, 0.0F, 0.0F);
   private static final Rotations DEFAULT_LEFTARM_ROTATION = new Rotations(-10.0F, 0.0F, -10.0F);
   private static final Rotations DEFAULT_RIGHTARM_ROTATION = new Rotations(-15.0F, 0.0F, 10.0F);
   private static final Rotations DEFAULT_LEFTLEG_ROTATION = new Rotations(-1.0F, 0.0F, -1.0F);
   private static final Rotations DEFAULT_RIGHTLEG_ROTATION = new Rotations(1.0F, 0.0F, 1.0F);
   public static final DataParameter<Byte> STATUS = EntityDataManager.createKey(EntityArmorStand.class, DataSerializers.BYTE);
   public static final DataParameter<Rotations> HEAD_ROTATION = EntityDataManager.createKey(EntityArmorStand.class, DataSerializers.ROTATIONS);
   public static final DataParameter<Rotations> BODY_ROTATION = EntityDataManager.createKey(EntityArmorStand.class, DataSerializers.ROTATIONS);
   public static final DataParameter<Rotations> LEFT_ARM_ROTATION = EntityDataManager.createKey(EntityArmorStand.class, DataSerializers.ROTATIONS);
   public static final DataParameter<Rotations> RIGHT_ARM_ROTATION = EntityDataManager.createKey(EntityArmorStand.class, DataSerializers.ROTATIONS);
   public static final DataParameter<Rotations> LEFT_LEG_ROTATION = EntityDataManager.createKey(EntityArmorStand.class, DataSerializers.ROTATIONS);
   public static final DataParameter<Rotations> RIGHT_LEG_ROTATION = EntityDataManager.createKey(EntityArmorStand.class, DataSerializers.ROTATIONS);
   private static final Predicate<Entity> IS_RIDEABLE_MINECART = (p_200617_0_) -> {
      return p_200617_0_ instanceof EntityMinecart && ((EntityMinecart)p_200617_0_).getMinecartType() == EntityMinecart.Type.RIDEABLE;
   };
   private final NonNullList<ItemStack> handItems = NonNullList.withSize(2, ItemStack.EMPTY);
   private final NonNullList<ItemStack> armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
   private boolean canInteract;
   public long punchCooldown;
   private int disabledSlots;
   private boolean wasMarker;
   private Rotations headRotation = DEFAULT_HEAD_ROTATION;
   private Rotations bodyRotation = DEFAULT_BODY_ROTATION;
   private Rotations leftArmRotation = DEFAULT_LEFTARM_ROTATION;
   private Rotations rightArmRotation = DEFAULT_RIGHTARM_ROTATION;
   private Rotations leftLegRotation = DEFAULT_LEFTLEG_ROTATION;
   private Rotations rightLegRotation = DEFAULT_RIGHTLEG_ROTATION;

   public EntityArmorStand(World p_i45854_1_) {
      super(EntityType.ARMOR_STAND, p_i45854_1_);
      this.noClip = this.hasNoGravity();
      this.setSize(0.5F, 1.975F);
      this.stepHeight = 0.0F;
   }

   public EntityArmorStand(World p_i45855_1_, double p_i45855_2_, double p_i45855_4_, double p_i45855_6_) {
      this(p_i45855_1_);
      this.setPosition(p_i45855_2_, p_i45855_4_, p_i45855_6_);
   }

   protected final void setSize(float p_70105_1_, float p_70105_2_) {
      double d0 = this.posX;
      double d1 = this.posY;
      double d2 = this.posZ;
      float f = this.hasMarker() ? 0.0F : (this.isChild() ? 0.5F : 1.0F);
      super.setSize(p_70105_1_ * f, p_70105_2_ * f);
      this.setPosition(d0, d1, d2);
   }

   public boolean isServerWorld() {
      return super.isServerWorld() && !this.hasNoGravity();
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(STATUS, (byte)0);
      this.dataManager.register(HEAD_ROTATION, DEFAULT_HEAD_ROTATION);
      this.dataManager.register(BODY_ROTATION, DEFAULT_BODY_ROTATION);
      this.dataManager.register(LEFT_ARM_ROTATION, DEFAULT_LEFTARM_ROTATION);
      this.dataManager.register(RIGHT_ARM_ROTATION, DEFAULT_RIGHTARM_ROTATION);
      this.dataManager.register(LEFT_LEG_ROTATION, DEFAULT_LEFTLEG_ROTATION);
      this.dataManager.register(RIGHT_LEG_ROTATION, DEFAULT_RIGHTLEG_ROTATION);
   }

   public Iterable<ItemStack> getHeldEquipment() {
      return this.handItems;
   }

   public Iterable<ItemStack> getArmorInventoryList() {
      return this.armorItems;
   }

   public ItemStack getItemStackFromSlot(EntityEquipmentSlot p_184582_1_) {
      switch(p_184582_1_.getSlotType()) {
      case HAND:
         return this.handItems.get(p_184582_1_.getIndex());
      case ARMOR:
         return this.armorItems.get(p_184582_1_.getIndex());
      default:
         return ItemStack.EMPTY;
      }
   }

   public void setItemStackToSlot(EntityEquipmentSlot p_184201_1_, ItemStack p_184201_2_) {
      switch(p_184201_1_.getSlotType()) {
      case HAND:
         this.playEquipSound(p_184201_2_);
         this.handItems.set(p_184201_1_.getIndex(), p_184201_2_);
         break;
      case ARMOR:
         this.playEquipSound(p_184201_2_);
         this.armorItems.set(p_184201_1_.getIndex(), p_184201_2_);
      }

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

      if (!p_174820_2_.isEmpty() && !EntityLiving.isItemStackInSlot(entityequipmentslot, p_174820_2_) && entityequipmentslot != EntityEquipmentSlot.HEAD) {
         return false;
      } else {
         this.setItemStackToSlot(entityequipmentslot, p_174820_2_);
         return true;
      }
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      NBTTagList nbttaglist = new NBTTagList();

      for(ItemStack itemstack : this.armorItems) {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         if (!itemstack.isEmpty()) {
            itemstack.write(nbttagcompound);
         }

         nbttaglist.add((INBTBase)nbttagcompound);
      }

      p_70014_1_.setTag("ArmorItems", nbttaglist);
      NBTTagList nbttaglist1 = new NBTTagList();

      for(ItemStack itemstack1 : this.handItems) {
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();
         if (!itemstack1.isEmpty()) {
            itemstack1.write(nbttagcompound1);
         }

         nbttaglist1.add((INBTBase)nbttagcompound1);
      }

      p_70014_1_.setTag("HandItems", nbttaglist1);
      p_70014_1_.setBoolean("Invisible", this.isInvisible());
      p_70014_1_.setBoolean("Small", this.isSmall());
      p_70014_1_.setBoolean("ShowArms", this.getShowArms());
      p_70014_1_.setInteger("DisabledSlots", this.disabledSlots);
      p_70014_1_.setBoolean("NoBasePlate", this.hasNoBasePlate());
      if (this.hasMarker()) {
         p_70014_1_.setBoolean("Marker", this.hasMarker());
      }

      p_70014_1_.setTag("Pose", this.readPoseFromNBT());
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      if (p_70037_1_.hasKey("ArmorItems", 9)) {
         NBTTagList nbttaglist = p_70037_1_.getTagList("ArmorItems", 10);

         for(int i = 0; i < this.armorItems.size(); ++i) {
            this.armorItems.set(i, ItemStack.loadFromNBT(nbttaglist.getCompoundTagAt(i)));
         }
      }

      if (p_70037_1_.hasKey("HandItems", 9)) {
         NBTTagList nbttaglist1 = p_70037_1_.getTagList("HandItems", 10);

         for(int j = 0; j < this.handItems.size(); ++j) {
            this.handItems.set(j, ItemStack.loadFromNBT(nbttaglist1.getCompoundTagAt(j)));
         }
      }

      this.setInvisible(p_70037_1_.getBoolean("Invisible"));
      this.setSmall(p_70037_1_.getBoolean("Small"));
      this.setShowArms(p_70037_1_.getBoolean("ShowArms"));
      this.disabledSlots = p_70037_1_.getInteger("DisabledSlots");
      this.setNoBasePlate(p_70037_1_.getBoolean("NoBasePlate"));
      this.setMarker(p_70037_1_.getBoolean("Marker"));
      this.wasMarker = !this.hasMarker();
      this.noClip = this.hasNoGravity();
      NBTTagCompound nbttagcompound = p_70037_1_.getCompoundTag("Pose");
      this.writePoseToNBT(nbttagcompound);
   }

   private void writePoseToNBT(NBTTagCompound p_175416_1_) {
      NBTTagList nbttaglist = p_175416_1_.getTagList("Head", 5);
      this.setHeadRotation(nbttaglist.isEmpty() ? DEFAULT_HEAD_ROTATION : new Rotations(nbttaglist));
      NBTTagList nbttaglist1 = p_175416_1_.getTagList("Body", 5);
      this.setBodyRotation(nbttaglist1.isEmpty() ? DEFAULT_BODY_ROTATION : new Rotations(nbttaglist1));
      NBTTagList nbttaglist2 = p_175416_1_.getTagList("LeftArm", 5);
      this.setLeftArmRotation(nbttaglist2.isEmpty() ? DEFAULT_LEFTARM_ROTATION : new Rotations(nbttaglist2));
      NBTTagList nbttaglist3 = p_175416_1_.getTagList("RightArm", 5);
      this.setRightArmRotation(nbttaglist3.isEmpty() ? DEFAULT_RIGHTARM_ROTATION : new Rotations(nbttaglist3));
      NBTTagList nbttaglist4 = p_175416_1_.getTagList("LeftLeg", 5);
      this.setLeftLegRotation(nbttaglist4.isEmpty() ? DEFAULT_LEFTLEG_ROTATION : new Rotations(nbttaglist4));
      NBTTagList nbttaglist5 = p_175416_1_.getTagList("RightLeg", 5);
      this.setRightLegRotation(nbttaglist5.isEmpty() ? DEFAULT_RIGHTLEG_ROTATION : new Rotations(nbttaglist5));
   }

   private NBTTagCompound readPoseFromNBT() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      if (!DEFAULT_HEAD_ROTATION.equals(this.headRotation)) {
         nbttagcompound.setTag("Head", this.headRotation.writeToNBT());
      }

      if (!DEFAULT_BODY_ROTATION.equals(this.bodyRotation)) {
         nbttagcompound.setTag("Body", this.bodyRotation.writeToNBT());
      }

      if (!DEFAULT_LEFTARM_ROTATION.equals(this.leftArmRotation)) {
         nbttagcompound.setTag("LeftArm", this.leftArmRotation.writeToNBT());
      }

      if (!DEFAULT_RIGHTARM_ROTATION.equals(this.rightArmRotation)) {
         nbttagcompound.setTag("RightArm", this.rightArmRotation.writeToNBT());
      }

      if (!DEFAULT_LEFTLEG_ROTATION.equals(this.leftLegRotation)) {
         nbttagcompound.setTag("LeftLeg", this.leftLegRotation.writeToNBT());
      }

      if (!DEFAULT_RIGHTLEG_ROTATION.equals(this.rightLegRotation)) {
         nbttagcompound.setTag("RightLeg", this.rightLegRotation.writeToNBT());
      }

      return nbttagcompound;
   }

   public boolean canBePushed() {
      return false;
   }

   protected void collideWithEntity(Entity p_82167_1_) {
   }

   protected void collideWithNearbyEntities() {
      List<Entity> list = this.world.func_175674_a(this, this.getEntityBoundingBox(), IS_RIDEABLE_MINECART);

      for(int i = 0; i < list.size(); ++i) {
         Entity entity = list.get(i);
         if (this.getDistanceSq(entity) <= 0.2D) {
            entity.applyEntityCollision(this);
         }
      }

   }

   public EnumActionResult applyPlayerInteraction(EntityPlayer p_184199_1_, Vec3d p_184199_2_, EnumHand p_184199_3_) {
      ItemStack itemstack = p_184199_1_.getHeldItem(p_184199_3_);
      if (!this.hasMarker() && itemstack.getItem() != Items.NAME_TAG) {
         if (!this.world.isRemote && !p_184199_1_.isSpectator()) {
            EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);
            if (itemstack.isEmpty()) {
               EntityEquipmentSlot entityequipmentslot1 = this.getClickedSlot(p_184199_2_);
               EntityEquipmentSlot entityequipmentslot2 = this.isDisabled(entityequipmentslot1) ? entityequipmentslot : entityequipmentslot1;
               if (this.hasItemInSlot(entityequipmentslot2)) {
                  this.swapItem(p_184199_1_, entityequipmentslot2, itemstack, p_184199_3_);
               }
            } else {
               if (this.isDisabled(entityequipmentslot)) {
                  return EnumActionResult.FAIL;
               }

               if (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.HAND && !this.getShowArms()) {
                  return EnumActionResult.FAIL;
               }

               this.swapItem(p_184199_1_, entityequipmentslot, itemstack, p_184199_3_);
            }

            return EnumActionResult.SUCCESS;
         } else {
            return EnumActionResult.SUCCESS;
         }
      } else {
         return EnumActionResult.PASS;
      }
   }

   protected EntityEquipmentSlot getClickedSlot(Vec3d p_190772_1_) {
      EntityEquipmentSlot entityequipmentslot = EntityEquipmentSlot.MAINHAND;
      boolean flag = this.isSmall();
      double d0 = flag ? p_190772_1_.y * 2.0D : p_190772_1_.y;
      EntityEquipmentSlot entityequipmentslot1 = EntityEquipmentSlot.FEET;
      if (d0 >= 0.1D && d0 < 0.1D + (flag ? 0.8D : 0.45D) && this.hasItemInSlot(entityequipmentslot1)) {
         entityequipmentslot = EntityEquipmentSlot.FEET;
      } else if (d0 >= 0.9D + (flag ? 0.3D : 0.0D) && d0 < 0.9D + (flag ? 1.0D : 0.7D) && this.hasItemInSlot(EntityEquipmentSlot.CHEST)) {
         entityequipmentslot = EntityEquipmentSlot.CHEST;
      } else if (d0 >= 0.4D && d0 < 0.4D + (flag ? 1.0D : 0.8D) && this.hasItemInSlot(EntityEquipmentSlot.LEGS)) {
         entityequipmentslot = EntityEquipmentSlot.LEGS;
      } else if (d0 >= 1.6D && this.hasItemInSlot(EntityEquipmentSlot.HEAD)) {
         entityequipmentslot = EntityEquipmentSlot.HEAD;
      } else if (!this.hasItemInSlot(EntityEquipmentSlot.MAINHAND) && this.hasItemInSlot(EntityEquipmentSlot.OFFHAND)) {
         entityequipmentslot = EntityEquipmentSlot.OFFHAND;
      }

      return entityequipmentslot;
   }

   public boolean isDisabled(EntityEquipmentSlot p_184796_1_) {
      return (this.disabledSlots & 1 << p_184796_1_.getSlotIndex()) != 0 || p_184796_1_.getSlotType() == EntityEquipmentSlot.Type.HAND && !this.getShowArms();
   }

   private void swapItem(EntityPlayer p_184795_1_, EntityEquipmentSlot p_184795_2_, ItemStack p_184795_3_, EnumHand p_184795_4_) {
      ItemStack itemstack = this.getItemStackFromSlot(p_184795_2_);
      if (itemstack.isEmpty() || (this.disabledSlots & 1 << p_184795_2_.getSlotIndex() + 8) == 0) {
         if (!itemstack.isEmpty() || (this.disabledSlots & 1 << p_184795_2_.getSlotIndex() + 16) == 0) {
            if (p_184795_1_.capabilities.isCreativeMode && itemstack.isEmpty() && !p_184795_3_.isEmpty()) {
               ItemStack itemstack2 = p_184795_3_.copy();
               itemstack2.setCount(1);
               this.setItemStackToSlot(p_184795_2_, itemstack2);
            } else if (!p_184795_3_.isEmpty() && p_184795_3_.getCount() > 1) {
               if (itemstack.isEmpty()) {
                  ItemStack itemstack1 = p_184795_3_.copy();
                  itemstack1.setCount(1);
                  this.setItemStackToSlot(p_184795_2_, itemstack1);
                  p_184795_3_.shrink(1);
               }
            } else {
               this.setItemStackToSlot(p_184795_2_, p_184795_3_);
               p_184795_1_.setHeldItem(p_184795_4_, itemstack);
            }
         }
      }
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (!this.world.isRemote && !this.isDead) {
         if (DamageSource.OUT_OF_WORLD.equals(p_70097_1_)) {
            this.setDead();
            return false;
         } else if (!this.isInvulnerableTo(p_70097_1_) && !this.canInteract && !this.hasMarker()) {
            if (p_70097_1_.isExplosion()) {
               this.dropContents();
               this.setDead();
               return false;
            } else if (DamageSource.IN_FIRE.equals(p_70097_1_)) {
               if (this.isBurning()) {
                  this.damageArmorStand(0.15F);
               } else {
                  this.setFire(5);
               }

               return false;
            } else if (DamageSource.ON_FIRE.equals(p_70097_1_) && this.getHealth() > 0.5F) {
               this.damageArmorStand(4.0F);
               return false;
            } else {
               boolean flag = p_70097_1_.getImmediateSource() instanceof EntityArrow;
               boolean flag1 = "player".equals(p_70097_1_.getDamageType());
               if (!flag1 && !flag) {
                  return false;
               } else if (p_70097_1_.getTrueSource() instanceof EntityPlayer && !((EntityPlayer)p_70097_1_.getTrueSource()).capabilities.allowEdit) {
                  return false;
               } else if (p_70097_1_.isCreativePlayer()) {
                  this.playBrokenSound();
                  this.playParticles();
                  this.setDead();
                  return false;
               } else {
                  long i = this.world.getTotalWorldTime();
                  if (i - this.punchCooldown > 5L && !flag) {
                     this.world.setEntityState(this, (byte)32);
                     this.punchCooldown = i;
                  } else {
                     this.dropBlock();
                     this.playParticles();
                     this.setDead();
                  }

                  return true;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 32) {
         if (this.world.isRemote) {
            this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.ENTITY_ARMOR_STAND_HIT, this.getSoundCategory(), 0.3F, 1.0F, false);
            this.punchCooldown = this.world.getTotalWorldTime();
         }
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double p_70112_1_) {
      double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;
      if (Double.isNaN(d0) || d0 == 0.0D) {
         d0 = 4.0D;
      }

      d0 = d0 * 64.0D;
      return p_70112_1_ < d0 * d0;
   }

   private void playParticles() {
      if (this.world instanceof WorldServer) {
         ((WorldServer)this.world).spawnParticle(new BlockParticleData(Particles.BLOCK, Blocks.OAK_PLANKS.getDefaultState()), this.posX, this.posY + (double)this.height / 1.5D, this.posZ, 10, (double)(this.width / 4.0F), (double)(this.height / 4.0F), (double)(this.width / 4.0F), 0.05D);
      }

   }

   private void damageArmorStand(float p_175406_1_) {
      float f = this.getHealth();
      f = f - p_175406_1_;
      if (f <= 0.5F) {
         this.dropContents();
         this.setDead();
      } else {
         this.setHealth(f);
      }

   }

   private void dropBlock() {
      Block.spawnAsEntity(this.world, new BlockPos(this), new ItemStack(Items.ARMOR_STAND));
      this.dropContents();
   }

   private void dropContents() {
      this.playBrokenSound();

      for(int i = 0; i < this.handItems.size(); ++i) {
         ItemStack itemstack = this.handItems.get(i);
         if (!itemstack.isEmpty()) {
            Block.spawnAsEntity(this.world, (new BlockPos(this)).up(), itemstack);
            this.handItems.set(i, ItemStack.EMPTY);
         }
      }

      for(int j = 0; j < this.armorItems.size(); ++j) {
         ItemStack itemstack1 = this.armorItems.get(j);
         if (!itemstack1.isEmpty()) {
            Block.spawnAsEntity(this.world, (new BlockPos(this)).up(), itemstack1);
            this.armorItems.set(j, ItemStack.EMPTY);
         }
      }

   }

   private void playBrokenSound() {
      this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_ARMORSTAND_BREAK, this.getSoundCategory(), 1.0F, 1.0F);
   }

   protected float updateDistance(float p_110146_1_, float p_110146_2_) {
      this.prevRenderYawOffset = this.prevRotationYaw;
      this.renderYawOffset = this.rotationYaw;
      return 0.0F;
   }

   public float getEyeHeight() {
      return this.isChild() ? this.height * 0.5F : this.height * 0.9F;
   }

   public double getYOffset() {
      return this.hasMarker() ? 0.0D : (double)0.1F;
   }

   public void travel(float p_191986_1_, float p_191986_2_, float p_191986_3_) {
      if (!this.hasNoGravity()) {
         super.travel(p_191986_1_, p_191986_2_, p_191986_3_);
      }
   }

   public void setRenderYawOffset(float p_181013_1_) {
      this.prevRenderYawOffset = this.prevRotationYaw = p_181013_1_;
      this.prevRotationYawHead = this.rotationYawHead = p_181013_1_;
   }

   public void setRotationYawHead(float p_70034_1_) {
      this.prevRenderYawOffset = this.prevRotationYaw = p_70034_1_;
      this.prevRotationYawHead = this.rotationYawHead = p_70034_1_;
   }

   public void tick() {
      super.tick();
      Rotations rotations = this.dataManager.get(HEAD_ROTATION);
      if (!this.headRotation.equals(rotations)) {
         this.setHeadRotation(rotations);
      }

      Rotations rotations1 = this.dataManager.get(BODY_ROTATION);
      if (!this.bodyRotation.equals(rotations1)) {
         this.setBodyRotation(rotations1);
      }

      Rotations rotations2 = this.dataManager.get(LEFT_ARM_ROTATION);
      if (!this.leftArmRotation.equals(rotations2)) {
         this.setLeftArmRotation(rotations2);
      }

      Rotations rotations3 = this.dataManager.get(RIGHT_ARM_ROTATION);
      if (!this.rightArmRotation.equals(rotations3)) {
         this.setRightArmRotation(rotations3);
      }

      Rotations rotations4 = this.dataManager.get(LEFT_LEG_ROTATION);
      if (!this.leftLegRotation.equals(rotations4)) {
         this.setLeftLegRotation(rotations4);
      }

      Rotations rotations5 = this.dataManager.get(RIGHT_LEG_ROTATION);
      if (!this.rightLegRotation.equals(rotations5)) {
         this.setRightLegRotation(rotations5);
      }

      boolean flag = this.hasMarker();
      if (this.wasMarker != flag) {
         this.updateBoundingBox(flag);
         this.preventEntitySpawning = !flag;
         this.wasMarker = flag;
      }

   }

   private void updateBoundingBox(boolean p_181550_1_) {
      if (p_181550_1_) {
         this.setSize(0.0F, 0.0F);
      } else {
         this.setSize(0.5F, 1.975F);
      }

   }

   protected void updatePotionMetadata() {
      this.setInvisible(this.canInteract);
   }

   public void setInvisible(boolean p_82142_1_) {
      this.canInteract = p_82142_1_;
      super.setInvisible(p_82142_1_);
   }

   public boolean isChild() {
      return this.isSmall();
   }

   public void onKillCommand() {
      this.setDead();
   }

   public boolean isImmuneToExplosions() {
      return this.isInvisible();
   }

   public EnumPushReaction getPushReaction() {
      return this.hasMarker() ? EnumPushReaction.IGNORE : super.getPushReaction();
   }

   private void setSmall(boolean p_175420_1_) {
      this.dataManager.set(STATUS, this.setBit(this.dataManager.get(STATUS), 1, p_175420_1_));
      this.setSize(0.5F, 1.975F);
   }

   public boolean isSmall() {
      return (this.dataManager.get(STATUS) & 1) != 0;
   }

   private void setShowArms(boolean p_175413_1_) {
      this.dataManager.set(STATUS, this.setBit(this.dataManager.get(STATUS), 4, p_175413_1_));
   }

   public boolean getShowArms() {
      return (this.dataManager.get(STATUS) & 4) != 0;
   }

   private void setNoBasePlate(boolean p_175426_1_) {
      this.dataManager.set(STATUS, this.setBit(this.dataManager.get(STATUS), 8, p_175426_1_));
   }

   public boolean hasNoBasePlate() {
      return (this.dataManager.get(STATUS) & 8) != 0;
   }

   private void setMarker(boolean p_181027_1_) {
      this.dataManager.set(STATUS, this.setBit(this.dataManager.get(STATUS), 16, p_181027_1_));
      this.setSize(0.5F, 1.975F);
   }

   public boolean hasMarker() {
      return (this.dataManager.get(STATUS) & 16) != 0;
   }

   private byte setBit(byte p_184797_1_, int p_184797_2_, boolean p_184797_3_) {
      if (p_184797_3_) {
         p_184797_1_ = (byte)(p_184797_1_ | p_184797_2_);
      } else {
         p_184797_1_ = (byte)(p_184797_1_ & ~p_184797_2_);
      }

      return p_184797_1_;
   }

   public void setHeadRotation(Rotations p_175415_1_) {
      this.headRotation = p_175415_1_;
      this.dataManager.set(HEAD_ROTATION, p_175415_1_);
   }

   public void setBodyRotation(Rotations p_175424_1_) {
      this.bodyRotation = p_175424_1_;
      this.dataManager.set(BODY_ROTATION, p_175424_1_);
   }

   public void setLeftArmRotation(Rotations p_175405_1_) {
      this.leftArmRotation = p_175405_1_;
      this.dataManager.set(LEFT_ARM_ROTATION, p_175405_1_);
   }

   public void setRightArmRotation(Rotations p_175428_1_) {
      this.rightArmRotation = p_175428_1_;
      this.dataManager.set(RIGHT_ARM_ROTATION, p_175428_1_);
   }

   public void setLeftLegRotation(Rotations p_175417_1_) {
      this.leftLegRotation = p_175417_1_;
      this.dataManager.set(LEFT_LEG_ROTATION, p_175417_1_);
   }

   public void setRightLegRotation(Rotations p_175427_1_) {
      this.rightLegRotation = p_175427_1_;
      this.dataManager.set(RIGHT_LEG_ROTATION, p_175427_1_);
   }

   public Rotations getHeadRotation() {
      return this.headRotation;
   }

   public Rotations getBodyRotation() {
      return this.bodyRotation;
   }

   @OnlyIn(Dist.CLIENT)
   public Rotations getLeftArmRotation() {
      return this.leftArmRotation;
   }

   @OnlyIn(Dist.CLIENT)
   public Rotations getRightArmRotation() {
      return this.rightArmRotation;
   }

   @OnlyIn(Dist.CLIENT)
   public Rotations getLeftLegRotation() {
      return this.leftLegRotation;
   }

   @OnlyIn(Dist.CLIENT)
   public Rotations getRightLegRotation() {
      return this.rightLegRotation;
   }

   public boolean canBeCollidedWith() {
      return super.canBeCollidedWith() && !this.hasMarker();
   }

   public EnumHandSide getPrimaryHand() {
      return EnumHandSide.RIGHT;
   }

   protected SoundEvent getFallSound(int p_184588_1_) {
      return SoundEvents.ENTITY_ARMOR_STAND_FALL;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_ARMOR_STAND_HIT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_ARMORSTAND_BREAK;
   }

   public void onStruckByLightning(EntityLightningBolt p_70077_1_) {
   }

   public boolean canBeHitWithPotion() {
      return false;
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      if (STATUS.equals(p_184206_1_)) {
         this.setSize(0.5F, 1.975F);
      }

      super.notifyDataManagerChange(p_184206_1_);
   }

   public boolean attackable() {
      return false;
   }
}
