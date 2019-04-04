package net.minecraft.tileentity;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class TileEntityBrewingStand extends TileEntityLockable implements ISidedInventory, ITickable {
   private static final int[] SLOTS_FOR_UP = new int[]{3};
   private static final int[] SLOTS_FOR_DOWN = new int[]{0, 1, 2, 3};
   private static final int[] OUTPUT_SLOTS = new int[]{0, 1, 2, 4};
   private NonNullList<ItemStack> brewingItemStacks = NonNullList.withSize(5, ItemStack.EMPTY);
   private int brewTime;
   private boolean[] filledSlots;
   private Item ingredientID;
   private ITextComponent customName;
   private int fuel;

   public TileEntityBrewingStand() {
      super(TileEntityType.BREWING_STAND);
   }

   public ITextComponent getName() {
      return this.customName != null ? this.customName : new TextComponentTranslation("container.brewing");
   }

   public boolean hasCustomName() {
      return this.customName != null;
   }

   @Nullable
   public ITextComponent getCustomName() {
      return this.customName;
   }

   public void setCustomName(@Nullable ITextComponent p_200224_1_) {
      this.customName = p_200224_1_;
   }

   public int getSizeInventory() {
      return this.brewingItemStacks.size();
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.brewingItemStacks) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public void tick() {
      ItemStack itemstack = this.brewingItemStacks.get(4);
      if (this.fuel <= 0 && itemstack.getItem() == Items.BLAZE_POWDER) {
         this.fuel = 20;
         itemstack.shrink(1);
         this.markDirty();
      }

      boolean flag = this.canBrew();
      boolean flag1 = this.brewTime > 0;
      ItemStack itemstack1 = this.brewingItemStacks.get(3);
      if (flag1) {
         --this.brewTime;
         boolean flag2 = this.brewTime == 0;
         if (flag2 && flag) {
            this.brewPotions();
            this.markDirty();
         } else if (!flag) {
            this.brewTime = 0;
            this.markDirty();
         } else if (this.ingredientID != itemstack1.getItem()) {
            this.brewTime = 0;
            this.markDirty();
         }
      } else if (flag && this.fuel > 0) {
         --this.fuel;
         this.brewTime = 400;
         this.ingredientID = itemstack1.getItem();
         this.markDirty();
      }

      if (!this.world.isRemote) {
         boolean[] aboolean = this.createFilledSlotsArray();
         if (!Arrays.equals(aboolean, this.filledSlots)) {
            this.filledSlots = aboolean;
            IBlockState iblockstate = this.world.getBlockState(this.getPos());
            if (!(iblockstate.getBlock() instanceof BlockBrewingStand)) {
               return;
            }

            for(int i = 0; i < BlockBrewingStand.HAS_BOTTLE.length; ++i) {
               iblockstate = iblockstate.with(BlockBrewingStand.HAS_BOTTLE[i], Boolean.valueOf(aboolean[i]));
            }

            this.world.setBlockState(this.pos, iblockstate, 2);
         }
      }

   }

   public boolean[] createFilledSlotsArray() {
      boolean[] aboolean = new boolean[3];

      for(int i = 0; i < 3; ++i) {
         if (!this.brewingItemStacks.get(i).isEmpty()) {
            aboolean[i] = true;
         }
      }

      return aboolean;
   }

   private boolean canBrew() {
      ItemStack itemstack = this.brewingItemStacks.get(3);
      if (itemstack.isEmpty()) {
         return false;
      } else if (!PotionBrewing.isReagent(itemstack)) {
         return false;
      } else {
         for(int i = 0; i < 3; ++i) {
            ItemStack itemstack1 = this.brewingItemStacks.get(i);
            if (!itemstack1.isEmpty() && PotionBrewing.hasConversions(itemstack1, itemstack)) {
               return true;
            }
         }

         return false;
      }
   }

   private void brewPotions() {
      ItemStack itemstack = this.brewingItemStacks.get(3);

      for(int i = 0; i < 3; ++i) {
         this.brewingItemStacks.set(i, PotionBrewing.doReaction(itemstack, this.brewingItemStacks.get(i)));
      }

      itemstack.shrink(1);
      BlockPos blockpos = this.getPos();
      if (itemstack.getItem().hasContainerItem()) {
         ItemStack itemstack1 = new ItemStack(itemstack.getItem().getContainerItem());
         if (itemstack.isEmpty()) {
            itemstack = itemstack1;
         } else {
            InventoryHelper.spawnItemStack(this.world, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), itemstack1);
         }
      }

      this.brewingItemStacks.set(3, itemstack);
      this.world.playEvent(1035, blockpos, 0);
   }

   public void readFromNBT(NBTTagCompound p_145839_1_) {
      super.readFromNBT(p_145839_1_);
      this.brewingItemStacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      ItemStackHelper.loadAllItems(p_145839_1_, this.brewingItemStacks);
      this.brewTime = p_145839_1_.getShort("BrewTime");
      if (p_145839_1_.hasKey("CustomName", 8)) {
         this.customName = ITextComponent.Serializer.fromJson(p_145839_1_.getString("CustomName"));
      }

      this.fuel = p_145839_1_.getByte("Fuel");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189515_1_) {
      super.writeToNBT(p_189515_1_);
      p_189515_1_.setShort("BrewTime", (short)this.brewTime);
      ItemStackHelper.saveAllItems(p_189515_1_, this.brewingItemStacks);
      if (this.customName != null) {
         p_189515_1_.setString("CustomName", ITextComponent.Serializer.toJson(this.customName));
      }

      p_189515_1_.setByte("Fuel", (byte)this.fuel);
      return p_189515_1_;
   }

   public ItemStack getStackInSlot(int p_70301_1_) {
      return p_70301_1_ >= 0 && p_70301_1_ < this.brewingItemStacks.size() ? this.brewingItemStacks.get(p_70301_1_) : ItemStack.EMPTY;
   }

   public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
      return ItemStackHelper.getAndSplit(this.brewingItemStacks, p_70298_1_, p_70298_2_);
   }

   public ItemStack removeStackFromSlot(int p_70304_1_) {
      return ItemStackHelper.getAndRemove(this.brewingItemStacks, p_70304_1_);
   }

   public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
      if (p_70299_1_ >= 0 && p_70299_1_ < this.brewingItemStacks.size()) {
         this.brewingItemStacks.set(p_70299_1_, p_70299_2_);
      }

   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean isUsableByPlayer(EntityPlayer p_70300_1_) {
      if (this.world.getTileEntity(this.pos) != this) {
         return false;
      } else {
         return !(p_70300_1_.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) > 64.0D);
      }
   }

   public void openInventory(EntityPlayer p_174889_1_) {
   }

   public void closeInventory(EntityPlayer p_174886_1_) {
   }

   public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
      if (p_94041_1_ == 3) {
         return PotionBrewing.isReagent(p_94041_2_);
      } else {
         Item item = p_94041_2_.getItem();
         if (p_94041_1_ == 4) {
            return item == Items.BLAZE_POWDER;
         } else {
            return (item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION || item == Items.GLASS_BOTTLE) && this.getStackInSlot(p_94041_1_).isEmpty();
         }
      }
   }

   public int[] getSlotsForFace(EnumFacing p_180463_1_) {
      if (p_180463_1_ == EnumFacing.UP) {
         return SLOTS_FOR_UP;
      } else {
         return p_180463_1_ == EnumFacing.DOWN ? SLOTS_FOR_DOWN : OUTPUT_SLOTS;
      }
   }

   public boolean canInsertItem(int p_180462_1_, ItemStack p_180462_2_, @Nullable EnumFacing p_180462_3_) {
      return this.isItemValidForSlot(p_180462_1_, p_180462_2_);
   }

   public boolean canExtractItem(int p_180461_1_, ItemStack p_180461_2_, EnumFacing p_180461_3_) {
      if (p_180461_1_ == 3) {
         return p_180461_2_.getItem() == Items.GLASS_BOTTLE;
      } else {
         return true;
      }
   }

   public String getGuiID() {
      return "minecraft:brewing_stand";
   }

   public Container createContainer(InventoryPlayer p_174876_1_, EntityPlayer p_174876_2_) {
      return new ContainerBrewingStand(p_174876_1_, this);
   }

   public int getField(int p_174887_1_) {
      switch(p_174887_1_) {
      case 0:
         return this.brewTime;
      case 1:
         return this.fuel;
      default:
         return 0;
      }
   }

   public void setField(int p_174885_1_, int p_174885_2_) {
      switch(p_174885_1_) {
      case 0:
         this.brewTime = p_174885_2_;
         break;
      case 1:
         this.fuel = p_174885_2_;
      }

   }

   public int getFieldCount() {
      return 2;
   }

   public void clear() {
      this.brewingItemStacks.clear();
   }
}
