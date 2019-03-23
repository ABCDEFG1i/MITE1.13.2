package net.minecraft.entity.item;

import javax.annotation.Nullable;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityItemFrame extends EntityHanging {
   private static final Logger PRIVATE_LOGGER = LogManager.getLogger();
   private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(EntityItemFrame.class, DataSerializers.ITEM_STACK);
   private static final DataParameter<Integer> ROTATION = EntityDataManager.createKey(EntityItemFrame.class, DataSerializers.VARINT);
   private float itemDropChance = 1.0F;

   public EntityItemFrame(World p_i1590_1_) {
      super(EntityType.ITEM_FRAME, p_i1590_1_);
   }

   public EntityItemFrame(World p_i45852_1_, BlockPos p_i45852_2_, EnumFacing p_i45852_3_) {
      super(EntityType.ITEM_FRAME, p_i45852_1_, p_i45852_2_);
      this.updateFacingWithBoundingBox(p_i45852_3_);
   }

   public float getEyeHeight() {
      return 0.0F;
   }

   protected void registerData() {
      this.getDataManager().register(ITEM, ItemStack.EMPTY);
      this.getDataManager().register(ROTATION, 0);
   }

   protected void updateFacingWithBoundingBox(EnumFacing p_174859_1_) {
      Validate.notNull(p_174859_1_);
      this.facingDirection = p_174859_1_;
      if (p_174859_1_.getAxis().isHorizontal()) {
         this.rotationPitch = 0.0F;
         this.rotationYaw = (float)(this.facingDirection.getHorizontalIndex() * 90);
      } else {
         this.rotationPitch = (float)(-90 * p_174859_1_.getAxisDirection().getOffset());
         this.rotationYaw = 0.0F;
      }

      this.prevRotationPitch = this.rotationPitch;
      this.prevRotationYaw = this.rotationYaw;
      this.updateBoundingBox();
   }

   protected void updateBoundingBox() {
      if (this.facingDirection != null) {
         double d0 = 0.46875D;
         this.posX = (double)this.hangingPosition.getX() + 0.5D - (double)this.facingDirection.getXOffset() * 0.46875D;
         this.posY = (double)this.hangingPosition.getY() + 0.5D - (double)this.facingDirection.getYOffset() * 0.46875D;
         this.posZ = (double)this.hangingPosition.getZ() + 0.5D - (double)this.facingDirection.getZOffset() * 0.46875D;
         double d1 = (double)this.getWidthPixels();
         double d2 = (double)this.getHeightPixels();
         double d3 = (double)this.getWidthPixels();
         EnumFacing.Axis enumfacing$axis = this.facingDirection.getAxis();
         switch(enumfacing$axis) {
         case X:
            d1 = 1.0D;
            break;
         case Y:
            d2 = 1.0D;
            break;
         case Z:
            d3 = 1.0D;
         }

         d1 = d1 / 32.0D;
         d2 = d2 / 32.0D;
         d3 = d3 / 32.0D;
         this.setEntityBoundingBox(new AxisAlignedBB(this.posX - d1, this.posY - d2, this.posZ - d3, this.posX + d1, this.posY + d2, this.posZ + d3));
      }
   }

   public boolean onValidSurface() {
      if (!this.world.isCollisionBoxesEmpty(this, this.getEntityBoundingBox())) {
         return false;
      } else {
         IBlockState iblockstate = this.world.getBlockState(this.hangingPosition.offset(this.facingDirection.getOpposite()));
         return iblockstate.getMaterial().isSolid() || this.facingDirection.getAxis().isHorizontal() && BlockRedstoneDiode.isDiode(iblockstate) ? this.world.func_175674_a(this, this.getEntityBoundingBox(), IS_HANGING_ENTITY).isEmpty() : false;
      }
   }

   public float getCollisionBorderSize() {
      return 0.0F;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else if (!p_70097_1_.isExplosion() && !this.getDisplayedItem().isEmpty()) {
         if (!this.world.isRemote) {
            this.dropItemOrSelf(p_70097_1_.getTrueSource(), false);
            this.playSound(SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
         }

         return true;
      } else {
         return super.attackEntityFrom(p_70097_1_, p_70097_2_);
      }
   }

   public int getWidthPixels() {
      return 12;
   }

   public int getHeightPixels() {
      return 12;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double p_70112_1_) {
      double d0 = 16.0D;
      d0 = d0 * 64.0D * getRenderDistanceWeight();
      return p_70112_1_ < d0 * d0;
   }

   public void onBroken(@Nullable Entity p_110128_1_) {
      this.playSound(SoundEvents.ENTITY_ITEM_FRAME_BREAK, 1.0F, 1.0F);
      this.dropItemOrSelf(p_110128_1_, true);
   }

   public void playPlaceSound() {
      this.playSound(SoundEvents.ENTITY_ITEM_FRAME_PLACE, 1.0F, 1.0F);
   }

   public void dropItemOrSelf(@Nullable Entity p_146065_1_, boolean p_146065_2_) {
      if (this.world.getGameRules().getBoolean("doEntityDrops")) {
         ItemStack itemstack = this.getDisplayedItem();
         this.setDisplayedItem(ItemStack.EMPTY);
         if (p_146065_1_ instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)p_146065_1_;
            if (entityplayer.capabilities.isCreativeMode) {
               this.removeItem(itemstack);
               return;
            }
         }

         if (p_146065_2_) {
            this.entityDropItem(Items.ITEM_FRAME);
         }

         if (!itemstack.isEmpty() && this.rand.nextFloat() < this.itemDropChance) {
            itemstack = itemstack.copy();
            this.removeItem(itemstack);
            this.entityDropItem(itemstack);
         }

      }
   }

   private void removeItem(ItemStack p_110131_1_) {
      if (p_110131_1_.getItem() == Items.FILLED_MAP) {
         MapData mapdata = ItemMap.getMapData(p_110131_1_, this.world);
         mapdata.func_212441_a(this.hangingPosition, this.getEntityId());
      }

      p_110131_1_.setItemFrame((EntityItemFrame)null);
   }

   public ItemStack getDisplayedItem() {
      return this.getDataManager().get(ITEM);
   }

   public void setDisplayedItem(ItemStack p_82334_1_) {
      this.setDisplayedItemWithUpdate(p_82334_1_, true);
   }

   private void setDisplayedItemWithUpdate(ItemStack p_174864_1_, boolean p_174864_2_) {
      if (!p_174864_1_.isEmpty()) {
         p_174864_1_ = p_174864_1_.copy();
         p_174864_1_.setCount(1);
         p_174864_1_.setItemFrame(this);
      }

      this.getDataManager().set(ITEM, p_174864_1_);
      if (!p_174864_1_.isEmpty()) {
         this.playSound(SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, 1.0F, 1.0F);
      }

      if (p_174864_2_ && this.hangingPosition != null) {
         this.world.updateComparatorOutputLevel(this.hangingPosition, Blocks.AIR);
      }

   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      if (p_184206_1_.equals(ITEM)) {
         ItemStack itemstack = this.getDisplayedItem();
         if (!itemstack.isEmpty() && itemstack.getItemFrame() != this) {
            itemstack.setItemFrame(this);
         }
      }

   }

   public int getRotation() {
      return this.getDataManager().get(ROTATION);
   }

   public void setItemRotation(int p_82336_1_) {
      this.setRotation(p_82336_1_, true);
   }

   private void setRotation(int p_174865_1_, boolean p_174865_2_) {
      this.getDataManager().set(ROTATION, p_174865_1_ % 8);
      if (p_174865_2_ && this.hangingPosition != null) {
         this.world.updateComparatorOutputLevel(this.hangingPosition, Blocks.AIR);
      }

   }

   public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      if (!this.getDisplayedItem().isEmpty()) {
         p_70014_1_.setTag("Item", this.getDisplayedItem().write(new NBTTagCompound()));
         p_70014_1_.setByte("ItemRotation", (byte)this.getRotation());
         p_70014_1_.setFloat("ItemDropChance", this.itemDropChance);
      }

      p_70014_1_.setByte("Facing", (byte)this.facingDirection.getIndex());
   }

   public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      NBTTagCompound nbttagcompound = p_70037_1_.getCompoundTag("Item");
      if (nbttagcompound != null && !nbttagcompound.isEmpty()) {
         ItemStack itemstack = ItemStack.loadFromNBT(nbttagcompound);
         if (itemstack.isEmpty()) {
            PRIVATE_LOGGER.warn("Unable to load item from: {}", (Object)nbttagcompound);
         }

         this.setDisplayedItemWithUpdate(itemstack, false);
         this.setRotation(p_70037_1_.getByte("ItemRotation"), false);
         if (p_70037_1_.hasKey("ItemDropChance", 99)) {
            this.itemDropChance = p_70037_1_.getFloat("ItemDropChance");
         }
      }

      this.updateFacingWithBoundingBox(EnumFacing.byIndex(p_70037_1_.getByte("Facing")));
   }

   public boolean processInitialInteract(EntityPlayer p_184230_1_, EnumHand p_184230_2_) {
      ItemStack itemstack = p_184230_1_.getHeldItem(p_184230_2_);
      if (!this.world.isRemote) {
         if (this.getDisplayedItem().isEmpty()) {
            if (!itemstack.isEmpty()) {
               this.setDisplayedItem(itemstack);
               if (!p_184230_1_.capabilities.isCreativeMode) {
                  itemstack.shrink(1);
               }
            }
         } else {
            this.playSound(SoundEvents.ENTITY_ITEM_FRAME_ROTATE_ITEM, 1.0F, 1.0F);
            this.setItemRotation(this.getRotation() + 1);
         }
      }

      return true;
   }

   public int getAnalogOutput() {
      return this.getDisplayedItem().isEmpty() ? 0 : this.getRotation() % 8 + 1;
   }
}
