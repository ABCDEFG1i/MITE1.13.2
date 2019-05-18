package net.minecraft.entity.item;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.stats.StatList;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityItem extends Entity {
   private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(EntityItem.class, DataSerializers.ITEM_STACK);
   private int age;
   private int pickupDelay;
   private int health = 5;
   private UUID thrower;
   private UUID owner;
   public float hoverStart = (float)(Math.random() * Math.PI * 2.0D);

   public EntityItem(World p_i1711_1_) {
      super(EntityType.ITEM, p_i1711_1_);
      this.setSize(0.25F, 0.25F);
   }

   public EntityItem(World p_i1709_1_, double p_i1709_2_, double p_i1709_4_, double p_i1709_6_) {
      this(p_i1709_1_);
      this.setPosition(p_i1709_2_, p_i1709_4_, p_i1709_6_);
      this.rotationYaw = (float)(Math.random() * 360.0D);
      this.motionX = (double)((float)(Math.random() * (double)0.2F - (double)0.1F));
      this.motionY = (double)0.2F;
      this.motionZ = (double)((float)(Math.random() * (double)0.2F - (double)0.1F));
   }

   public EntityItem(World p_i1710_1_, double p_i1710_2_, double p_i1710_4_, double p_i1710_6_, ItemStack p_i1710_8_) {
      this(p_i1710_1_, p_i1710_2_, p_i1710_4_, p_i1710_6_);
      this.setItem(p_i1710_8_);
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   protected void registerData() {
      this.getDataManager().register(ITEM, ItemStack.EMPTY);
   }

   public void tick() {
      if (this.getItem().isEmpty()) {
         this.setDead();
      } else {
         super.tick();
         if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
            --this.pickupDelay;
         }

         this.prevPosX = this.posX;
         this.prevPosY = this.posY;
         this.prevPosZ = this.posZ;
         double d0 = this.motionX;
         double d1 = this.motionY;
         double d2 = this.motionZ;
         if (this.areEyesInFluid(FluidTags.WATER)) {
            this.func_203043_v();
         } else if (!this.hasNoGravity()) {
            this.motionY -= (double)0.04F;
         }

         if (this.world.isRemote) {
            this.noClip = false;
         } else {
            this.noClip = this.pushOutOfBlocks(this.posX, (this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0D, this.posZ);
         }

         this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
         boolean flag = (int)this.prevPosX != (int)this.posX || (int)this.prevPosY != (int)this.posY || (int)this.prevPosZ != (int)this.posZ;
         if (flag || this.ticksExisted % 25 == 0) {
            if (this.world.getFluidState(new BlockPos(this)).isTagged(FluidTags.LAVA)) {
               this.motionY = (double)0.2F;
               this.motionX = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
               this.motionZ = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
               this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
            }

            if (!this.world.isRemote) {
               this.searchForOtherItemsNearby();
            }
         }

         float f = 0.98F;
         if (this.onGround) {
            f = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ))).getBlock().getSlipperiness() * 0.98F;
         }

         this.motionX *= (double)f;
         this.motionY *= (double)0.98F;
         this.motionZ *= (double)f;
         if (this.onGround) {
            this.motionY *= -0.5D;
         }

         if (this.age != -32768) {
            ++this.age;
         }

         this.isAirBorne |= this.handleWaterMovement();
         if (!this.world.isRemote) {
            double d3 = this.motionX - d0;
            double d4 = this.motionY - d1;
            double d5 = this.motionZ - d2;
            double d6 = d3 * d3 + d4 * d4 + d5 * d5;
            if (d6 > 0.01D) {
               this.isAirBorne = true;
            }
         }

         if (!this.world.isRemote && this.age >= 6000) {
            this.setDead();
         }

      }
   }

   private void func_203043_v() {
      if (this.motionY < (double)0.06F) {
         this.motionY += (double)5.0E-4F;
      }

      this.motionX *= (double)0.99F;
      this.motionZ *= (double)0.99F;
   }

   private void searchForOtherItemsNearby() {
      for(EntityItem entityitem : this.world.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().grow(0.5D, 0.0D, 0.5D))) {
         this.combineItems(entityitem);
      }

   }

   private boolean combineItems(EntityItem p_70289_1_) {
      if (p_70289_1_ == this) {
         return false;
      } else if (p_70289_1_.isEntityAlive() && this.isEntityAlive()) {
         ItemStack itemstack = this.getItem();
         ItemStack itemstack1 = p_70289_1_.getItem().copy();
         if (this.pickupDelay != 32767 && p_70289_1_.pickupDelay != 32767) {
            if (this.age != -32768 && p_70289_1_.age != -32768) {
               if (itemstack1.getItem() != itemstack.getItem()) {
                  return false;
               } else if (itemstack1.hasTag() ^ itemstack.hasTag()) {
                  return false;
               } else if (itemstack1.hasTag() && !itemstack1.getTag().equals(itemstack.getTag())) {
                  return false;
               } else if (itemstack1.getItem() == null) {
                  return false;
               } else if (itemstack1.getCount() < itemstack.getCount()) {
                  return p_70289_1_.combineItems(this);
               } else if (itemstack1.getCount() + itemstack.getCount() > itemstack1.getMaxStackSize()) {
                  return false;
               } else {
                  itemstack1.grow(itemstack.getCount());
                  p_70289_1_.pickupDelay = Math.max(p_70289_1_.pickupDelay, this.pickupDelay);
                  p_70289_1_.age = Math.min(p_70289_1_.age, this.age);
                  p_70289_1_.setItem(itemstack1);
                  this.setDead();
                  return true;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void setAgeToCreativeDespawnTime() {
      this.age = 4800;
   }

   protected void dealFireDamage(int p_70081_1_) {
      this.attackEntityFrom(DamageSource.IN_FIRE, (float)p_70081_1_);
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else if (!this.getItem().isEmpty() && this.getItem().getItem() == Items.NETHER_STAR && p_70097_1_.isExplosion()) {
         return false;
      } else {
         this.markVelocityChanged();
         this.health = (int)((float)this.health - p_70097_2_);
         if (this.health <= 0) {
            this.setDead();
         }

         return false;
      }
   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      p_70014_1_.setShort("Health", (short)this.health);
      p_70014_1_.setShort("Age", (short)this.age);
      p_70014_1_.setShort("PickupDelay", (short)this.pickupDelay);
      if (this.getThrowerId() != null) {
         p_70014_1_.setTag("Thrower", NBTUtil.createUUIDTag(this.getThrowerId()));
      }

      if (this.getOwnerId() != null) {
         p_70014_1_.setTag("Owner", NBTUtil.createUUIDTag(this.getOwnerId()));
      }

      if (!this.getItem().isEmpty()) {
         p_70014_1_.setTag("Item", this.getItem().write(new NBTTagCompound()));
      }

   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      this.health = p_70037_1_.getShort("Health");
      this.age = p_70037_1_.getShort("Age");
      if (p_70037_1_.hasKey("PickupDelay")) {
         this.pickupDelay = p_70037_1_.getShort("PickupDelay");
      }

      if (p_70037_1_.hasKey("Owner", 10)) {
         this.owner = NBTUtil.getUUIDFromTag(p_70037_1_.getCompoundTag("Owner"));
      }

      if (p_70037_1_.hasKey("Thrower", 10)) {
         this.thrower = NBTUtil.getUUIDFromTag(p_70037_1_.getCompoundTag("Thrower"));
      }

      NBTTagCompound nbttagcompound = p_70037_1_.getCompoundTag("Item");
      this.setItem(ItemStack.loadFromNBT(nbttagcompound));
      if (this.getItem().isEmpty()) {
         this.setDead();
      }

   }

   public void onCollideWithPlayer(EntityPlayer p_70100_1_) {
      if (!this.world.isRemote) {
         ItemStack itemstack = this.getItem();
         Item item = itemstack.getItem();
         int i = itemstack.getCount();
         if (this.pickupDelay == 0 && (this.owner == null || 6000 - this.age <= 200 || this.owner.equals(p_70100_1_.getUniqueID())) && p_70100_1_.inventory.addItemStackToInventory(itemstack)) {
            p_70100_1_.onItemPickup(this, i);
            if (itemstack.isEmpty()) {
               this.setDead();
               itemstack.setCount(i);
            }

            p_70100_1_.func_71064_a(StatList.ITEM_PICKED_UP.func_199076_b(item), i);
         }

      }
   }

   public ITextComponent getName() {
      ITextComponent itextcomponent = this.getCustomName();
      return itextcomponent != null ? itextcomponent : new TextComponentTranslation(this.getItem().getTranslationKey());
   }

   public boolean canBeAttackedWithItem() {
      return false;
   }

   @Nullable
   public Entity func_212321_a(DimensionType target) {
      Entity entity = super.func_212321_a(target);
      if (!this.world.isRemote && entity instanceof EntityItem) {
         ((EntityItem)entity).searchForOtherItemsNearby();
      }

      return entity;
   }

   public ItemStack getItem() {
      return this.getDataManager().get(ITEM);
   }

   public void setItem(ItemStack p_92058_1_) {
      this.getDataManager().set(ITEM, p_92058_1_);
   }

   @Nullable
   public UUID getOwnerId() {
      return this.owner;
   }

   public void setOwnerId(@Nullable UUID p_200217_1_) {
      this.owner = p_200217_1_;
   }

   @Nullable
   public UUID getThrowerId() {
      return this.thrower;
   }

   public void setThrowerId(@Nullable UUID p_200216_1_) {
      this.thrower = p_200216_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public int getAge() {
      return this.age;
   }

   public void setDefaultPickupDelay() {
      this.pickupDelay = 10;
   }

   public void setNoPickupDelay() {
      this.pickupDelay = 0;
   }

   public void setInfinitePickupDelay() {
      this.pickupDelay = 32767;
   }

   public void setPickupDelay(int p_174867_1_) {
      this.pickupDelay = p_174867_1_;
   }

   public boolean cannotPickup() {
      return this.pickupDelay > 0;
   }

   public void setNoDespawn() {
      this.age = -6000;
   }

   public void makeFakeItem() {
      this.setInfinitePickupDelay();
      this.age = 5999;
   }
}
