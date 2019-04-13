package net.minecraft.tileentity;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class TileEntityHopper extends TileEntityLockableLoot implements IHopper, ITickable {
   private NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
   private int transferCooldown = -1;
   private long tickedGameTime;

   public TileEntityHopper() {
      super(TileEntityType.HOPPER);
   }

   public void readFromNBT(NBTTagCompound p_145839_1_) {
      super.readFromNBT(p_145839_1_);
      this.inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      if (!this.checkLootAndRead(p_145839_1_)) {
         ItemStackHelper.loadAllItems(p_145839_1_, this.inventory);
      }

      if (p_145839_1_.hasKey("CustomName", 8)) {
         this.setCustomName(ITextComponent.Serializer.fromJson(p_145839_1_.getString("CustomName")));
      }

      this.transferCooldown = p_145839_1_.getInteger("TransferCooldown");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189515_1_) {
      super.writeToNBT(p_189515_1_);
      if (!this.checkLootAndWrite(p_189515_1_)) {
         ItemStackHelper.saveAllItems(p_189515_1_, this.inventory);
      }

      p_189515_1_.setInteger("TransferCooldown", this.transferCooldown);
      ITextComponent itextcomponent = this.getCustomName();
      if (itextcomponent != null) {
         p_189515_1_.setString("CustomName", ITextComponent.Serializer.toJson(itextcomponent));
      }

      return p_189515_1_;
   }

   public int getSizeInventory() {
      return this.inventory.size();
   }

   public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
      this.fillWithLoot(null);
      return ItemStackHelper.getAndSplit(this.getItems(), p_70298_1_, p_70298_2_);
   }

   public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
      this.fillWithLoot(null);
      this.getItems().set(p_70299_1_, p_70299_2_);
      if (p_70299_2_.getCount() > this.getInventoryStackLimit()) {
         p_70299_2_.setCount(this.getInventoryStackLimit());
      }

   }

   public ITextComponent getName() {
      return this.customName != null ? this.customName : new TextComponentTranslation("container.hopper");
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public void tick() {
      if (this.world != null && !this.world.isRemote) {
         --this.transferCooldown;
         this.tickedGameTime = this.world.getTotalWorldTime();
         if (!this.isOnTransferCooldown()) {
            this.setTransferCooldown(0);
            this.updateHopper(() -> pullItems(this));
         }

      }
   }

   private boolean updateHopper(Supplier<Boolean> p_200109_1_) {
      if (this.world != null && !this.world.isRemote) {
         if (!this.isOnTransferCooldown() && this.getBlockState().get(BlockHopper.ENABLED)) {
            boolean flag = false;
            if (!this.isInventoryEmpty()) {
               flag = this.transferItemsOut();
            }

            if (!this.isFull()) {
               flag |= p_200109_1_.get();
            }

            if (flag) {
               this.setTransferCooldown(8);
               this.markDirty();
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean isInventoryEmpty() {
      for(ItemStack itemstack : this.inventory) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public boolean isEmpty() {
      return this.isInventoryEmpty();
   }

   private boolean isFull() {
      for(ItemStack itemstack : this.inventory) {
         if (itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize()) {
            return false;
         }
      }

      return true;
   }

   private boolean transferItemsOut() {
      IInventory iinventory = this.getInventoryForHopperTransfer();
      if (iinventory == null) {
         return false;
      } else {
         EnumFacing enumfacing = this.getBlockState().get(BlockHopper.FACING).getOpposite();
         if (this.isInventoryFull(iinventory, enumfacing)) {
            return false;
         } else {
            for(int i = 0; i < this.getSizeInventory(); ++i) {
               if (!this.getStackInSlot(i).isEmpty()) {
                  ItemStack itemstack = this.getStackInSlot(i).copy();
                  ItemStack itemstack1 = putStackInInventoryAllSlots(this, iinventory, this.decrStackSize(i, 1), enumfacing);
                  if (itemstack1.isEmpty()) {
                     iinventory.markDirty();
                     return true;
                  }

                  this.setInventorySlotContents(i, itemstack);
               }
            }

            return false;
         }
      }
   }

   private boolean isInventoryFull(IInventory p_174919_1_, EnumFacing p_174919_2_) {
      if (p_174919_1_ instanceof ISidedInventory) {
         ISidedInventory isidedinventory = (ISidedInventory)p_174919_1_;
         int[] aint = isidedinventory.getSlotsForFace(p_174919_2_);

         for(int k : aint) {
            ItemStack itemstack1 = isidedinventory.getStackInSlot(k);
            if (itemstack1.isEmpty() || itemstack1.getCount() != itemstack1.getMaxStackSize()) {
               return false;
            }
         }
      } else {
         int i = p_174919_1_.getSizeInventory();

         for(int j = 0; j < i; ++j) {
            ItemStack itemstack = p_174919_1_.getStackInSlot(j);
            if (itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize()) {
               return false;
            }
         }
      }

      return true;
   }

   private static boolean isInventoryEmpty(IInventory p_174917_0_, EnumFacing p_174917_1_) {
      if (p_174917_0_ instanceof ISidedInventory) {
         ISidedInventory isidedinventory = (ISidedInventory)p_174917_0_;
         int[] aint = isidedinventory.getSlotsForFace(p_174917_1_);

         for(int i : aint) {
            if (!isidedinventory.getStackInSlot(i).isEmpty()) {
               return false;
            }
         }
      } else {
         int j = p_174917_0_.getSizeInventory();

         for(int k = 0; k < j; ++k) {
            if (!p_174917_0_.getStackInSlot(k).isEmpty()) {
               return false;
            }
         }
      }

      return true;
   }

   public static boolean pullItems(IHopper p_145891_0_) {
      IInventory iinventory = getSourceInventory(p_145891_0_);
      if (iinventory != null) {
         EnumFacing enumfacing = EnumFacing.DOWN;
         if (isInventoryEmpty(iinventory, enumfacing)) {
            return false;
         }

         if (iinventory instanceof ISidedInventory) {
            ISidedInventory isidedinventory = (ISidedInventory)iinventory;
            int[] aint = isidedinventory.getSlotsForFace(enumfacing);

            for(int i : aint) {
               if (pullItemFromSlot(p_145891_0_, iinventory, i, enumfacing)) {
                  return true;
               }
            }
         } else {
            int j = iinventory.getSizeInventory();

            for(int k = 0; k < j; ++k) {
               if (pullItemFromSlot(p_145891_0_, iinventory, k, enumfacing)) {
                  return true;
               }
            }
         }
      } else {
         for(EntityItem entityitem : getCaptureItems(p_145891_0_)) {
            if (captureItem(p_145891_0_, entityitem)) {
               return true;
            }
         }
      }

      return false;
   }

   private static boolean pullItemFromSlot(IHopper p_174915_0_, IInventory p_174915_1_, int p_174915_2_, EnumFacing p_174915_3_) {
      ItemStack itemstack = p_174915_1_.getStackInSlot(p_174915_2_);
      if (!itemstack.isEmpty() && canExtractItemFromSlot(p_174915_1_, itemstack, p_174915_2_, p_174915_3_)) {
         ItemStack itemstack1 = itemstack.copy();
         ItemStack itemstack2 = putStackInInventoryAllSlots(p_174915_1_, p_174915_0_, p_174915_1_.decrStackSize(p_174915_2_, 1),
                 null);
         if (itemstack2.isEmpty()) {
            p_174915_1_.markDirty();
            return true;
         }

         p_174915_1_.setInventorySlotContents(p_174915_2_, itemstack1);
      }

      return false;
   }

   public static boolean captureItem(IInventory p_200114_0_, EntityItem p_200114_1_) {
      boolean flag = false;
      ItemStack itemstack = p_200114_1_.getItem().copy();
      ItemStack itemstack1 = putStackInInventoryAllSlots(null, p_200114_0_, itemstack, null);
      if (itemstack1.isEmpty()) {
         flag = true;
         p_200114_1_.setDead();
      } else {
         p_200114_1_.setItem(itemstack1);
      }

      return flag;
   }

   public static ItemStack putStackInInventoryAllSlots(@Nullable IInventory p_174918_0_, IInventory p_174918_1_, ItemStack p_174918_2_, @Nullable EnumFacing p_174918_3_) {
      if (p_174918_1_ instanceof ISidedInventory && p_174918_3_ != null) {
         ISidedInventory isidedinventory = (ISidedInventory)p_174918_1_;
         int[] aint = isidedinventory.getSlotsForFace(p_174918_3_);

         for(int k = 0; k < aint.length && !p_174918_2_.isEmpty(); ++k) {
            p_174918_2_ = insertStack(p_174918_0_, p_174918_1_, p_174918_2_, aint[k], p_174918_3_);
         }
      } else {
         int i = p_174918_1_.getSizeInventory();

         for(int j = 0; j < i && !p_174918_2_.isEmpty(); ++j) {
            p_174918_2_ = insertStack(p_174918_0_, p_174918_1_, p_174918_2_, j, p_174918_3_);
         }
      }

      return p_174918_2_;
   }

   private static boolean canInsertItemInSlot(IInventory p_174920_0_, ItemStack p_174920_1_, int p_174920_2_, @Nullable EnumFacing p_174920_3_) {
      if (!p_174920_0_.isItemValidForSlot(p_174920_2_, p_174920_1_)) {
         return false;
      } else {
         return !(p_174920_0_ instanceof ISidedInventory) || ((ISidedInventory)p_174920_0_).canInsertItem(p_174920_2_, p_174920_1_, p_174920_3_);
      }
   }

   private static boolean canExtractItemFromSlot(IInventory p_174921_0_, ItemStack p_174921_1_, int p_174921_2_, EnumFacing p_174921_3_) {
      return !(p_174921_0_ instanceof ISidedInventory) || ((ISidedInventory)p_174921_0_).canExtractItem(p_174921_2_, p_174921_1_, p_174921_3_);
   }

   private static ItemStack insertStack(@Nullable IInventory p_174916_0_, IInventory p_174916_1_, ItemStack p_174916_2_, int p_174916_3_, @Nullable EnumFacing p_174916_4_) {
      ItemStack itemstack = p_174916_1_.getStackInSlot(p_174916_3_);
      if (canInsertItemInSlot(p_174916_1_, p_174916_2_, p_174916_3_, p_174916_4_)) {
         boolean flag = false;
         boolean flag1 = p_174916_1_.isEmpty();
         if (itemstack.isEmpty()) {
            p_174916_1_.setInventorySlotContents(p_174916_3_, p_174916_2_);
            p_174916_2_ = ItemStack.EMPTY;
            flag = true;
         } else if (canCombine(itemstack, p_174916_2_)) {
            int i = p_174916_2_.getMaxStackSize() - itemstack.getCount();
            int j = Math.min(p_174916_2_.getCount(), i);
            p_174916_2_.shrink(j);
            itemstack.grow(j);
            flag = j > 0;
         }

         if (flag) {
            if (flag1 && p_174916_1_ instanceof TileEntityHopper) {
               TileEntityHopper tileentityhopper1 = (TileEntityHopper)p_174916_1_;
               if (!tileentityhopper1.mayTransfer()) {
                  int k = 0;
                  if (p_174916_0_ instanceof TileEntityHopper) {
                     TileEntityHopper tileentityhopper = (TileEntityHopper)p_174916_0_;
                     if (tileentityhopper1.tickedGameTime >= tileentityhopper.tickedGameTime) {
                        k = 1;
                     }
                  }

                  tileentityhopper1.setTransferCooldown(8 - k);
               }
            }

            p_174916_1_.markDirty();
         }
      }

      return p_174916_2_;
   }

   @Nullable
   private IInventory getInventoryForHopperTransfer() {
      EnumFacing enumfacing = this.getBlockState().get(BlockHopper.FACING);
      return getInventoryAtPosition(this.getWorld(), this.pos.offset(enumfacing));
   }

   @Nullable
   public static IInventory getSourceInventory(IHopper p_145884_0_) {
      return getInventoryAtPosition(p_145884_0_.getWorld(), p_145884_0_.getXPos(), p_145884_0_.getYPos() + 1.0D, p_145884_0_.getZPos());
   }

   public static List<EntityItem> getCaptureItems(IHopper p_200115_0_) {
      return p_200115_0_.getCollectionArea().toBoundingBoxList().stream().flatMap((p_200110_1_) -> {
         return p_200115_0_.getWorld().getEntitiesWithinAABB(EntityItem.class, p_200110_1_.offset(p_200115_0_.getXPos() - 0.5D, p_200115_0_.getYPos() - 0.5D, p_200115_0_.getZPos() - 0.5D), EntitySelectors.IS_ALIVE).stream();
      }).collect(Collectors.toList());
   }

   @Nullable
   public static IInventory getInventoryAtPosition(World p_195484_0_, BlockPos p_195484_1_) {
      return getInventoryAtPosition(p_195484_0_, (double)p_195484_1_.getX() + 0.5D, (double)p_195484_1_.getY() + 0.5D, (double)p_195484_1_.getZ() + 0.5D);
   }

   @Nullable
   public static IInventory getInventoryAtPosition(World p_145893_0_, double p_145893_1_, double p_145893_3_, double p_145893_5_) {
      IInventory iinventory = null;
      BlockPos blockpos = new BlockPos(p_145893_1_, p_145893_3_, p_145893_5_);
      IBlockState iblockstate = p_145893_0_.getBlockState(blockpos);
      Block block = iblockstate.getBlock();
      if (block.hasTileEntity()) {
         TileEntity tileentity = p_145893_0_.getTileEntity(blockpos);
         if (tileentity instanceof IInventory) {
            iinventory = (IInventory)tileentity;
            if (iinventory instanceof TileEntityChest && block instanceof BlockChest) {
               iinventory = ((BlockChest)block).getContainer(iblockstate, p_145893_0_, blockpos, true);
            }
         }
      }

      if (iinventory == null) {
         List<Entity> list = p_145893_0_.func_175674_a(
                 null, new AxisAlignedBB(p_145893_1_ - 0.5D, p_145893_3_ - 0.5D, p_145893_5_ - 0.5D, p_145893_1_ + 0.5D, p_145893_3_ + 0.5D, p_145893_5_ + 0.5D), EntitySelectors.HAS_INVENTORY);
         if (!list.isEmpty()) {
            iinventory = (IInventory)list.get(p_145893_0_.rand.nextInt(list.size()));
         }
      }

      return iinventory;
   }

   private static boolean canCombine(ItemStack p_145894_0_, ItemStack p_145894_1_) {
      if (p_145894_0_.getItem() != p_145894_1_.getItem()) {
         return false;
      } else if (p_145894_0_.getDamage() != p_145894_1_.getDamage()) {
         return false;
      } else if (p_145894_0_.getCount() > p_145894_0_.getMaxStackSize()) {
         return false;
      } else {
         return ItemStack.areItemStackTagsEqual(p_145894_0_, p_145894_1_);
      }
   }

   public double getXPos() {
      return (double)this.pos.getX() + 0.5D;
   }

   public double getYPos() {
      return (double)this.pos.getY() + 0.5D;
   }

   public double getZPos() {
      return (double)this.pos.getZ() + 0.5D;
   }

   public void setTransferCooldown(int p_145896_1_) {
      this.transferCooldown = p_145896_1_;
   }

   private boolean isOnTransferCooldown() {
      return this.transferCooldown > 0;
   }

   public boolean mayTransfer() {
      return this.transferCooldown > 8;
   }

   public String getGuiID() {
      return "minecraft:hopper";
   }

   public Container createContainer(InventoryPlayer p_174876_1_, EntityPlayer p_174876_2_) {
      this.fillWithLoot(p_174876_2_);
      return new ContainerHopper(p_174876_1_, this, p_174876_2_);
   }

   protected NonNullList<ItemStack> getItems() {
      return this.inventory;
   }

   protected void setItems(NonNullList<ItemStack> p_199721_1_) {
      this.inventory = p_199721_1_;
   }

   public void onEntityCollision(Entity p_200113_1_) {
      if (p_200113_1_ instanceof EntityItem) {
         BlockPos blockpos = this.getPos();
         if (VoxelShapes.func_197879_c(VoxelShapes.func_197881_a(p_200113_1_.getEntityBoundingBox().offset((double)(-blockpos.getX()), (double)(-blockpos.getY()), (double)(-blockpos.getZ()))), this.getCollectionArea(), IBooleanFunction.AND)) {
            this.updateHopper(() -> {
               return captureItem(this, (EntityItem)p_200113_1_);
            });
         }
      }

   }
}
