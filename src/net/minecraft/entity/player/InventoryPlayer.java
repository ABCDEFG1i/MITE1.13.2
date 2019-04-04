package net.minecraft.entity.player;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.tags.Tag;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class InventoryPlayer implements IInventory {
   public final NonNullList<ItemStack> mainInventory = NonNullList.withSize(36, ItemStack.EMPTY);
   public final NonNullList<ItemStack> armorInventory = NonNullList.withSize(4, ItemStack.EMPTY);
   public final NonNullList<ItemStack> offHandInventory = NonNullList.withSize(1, ItemStack.EMPTY);
   private final List<NonNullList<ItemStack>> allInventories = ImmutableList.of(this.mainInventory, this.armorInventory, this.offHandInventory);
   public int currentItem;
   public EntityPlayer player;
   private ItemStack itemStack = ItemStack.EMPTY;
   private int timesChanged;

   public InventoryPlayer(EntityPlayer p_i1750_1_) {
      this.player = p_i1750_1_;
   }

   public ItemStack getCurrentItem() {
      return isHotbar(this.currentItem) ? this.mainInventory.get(this.currentItem) : ItemStack.EMPTY;
   }

   public static int getHotbarSize() {
      return 9;
   }

   private boolean canMergeStacks(ItemStack p_184436_1_, ItemStack p_184436_2_) {
      return !p_184436_1_.isEmpty() && this.stackEqualExact(p_184436_1_, p_184436_2_) && p_184436_1_.isStackable() && p_184436_1_.getCount() < p_184436_1_.getMaxStackSize() && p_184436_1_.getCount() < this.getInventoryStackLimit();
   }

   private boolean stackEqualExact(ItemStack p_184431_1_, ItemStack p_184431_2_) {
      return p_184431_1_.getItem() == p_184431_2_.getItem() && ItemStack.areItemStackTagsEqual(p_184431_1_, p_184431_2_);
   }

   public int getFirstEmptyStack() {
      for(int i = 0; i < this.mainInventory.size(); ++i) {
         if (this.mainInventory.get(i).isEmpty()) {
            return i;
         }
      }

      return -1;
   }

   @OnlyIn(Dist.CLIENT)
   public void setPickedItemStack(ItemStack p_184434_1_) {
      int i = this.getSlotFor(p_184434_1_);
      if (isHotbar(i)) {
         this.currentItem = i;
      } else {
         if (i == -1) {
            this.currentItem = this.getBestHotbarSlot();
            if (!this.mainInventory.get(this.currentItem).isEmpty()) {
               int j = this.getFirstEmptyStack();
               if (j != -1) {
                  this.mainInventory.set(j, this.mainInventory.get(this.currentItem));
               }
            }

            this.mainInventory.set(this.currentItem, p_184434_1_);
         } else {
            this.pickItem(i);
         }

      }
   }

   public void pickItem(int p_184430_1_) {
      this.currentItem = this.getBestHotbarSlot();
      ItemStack itemstack = this.mainInventory.get(this.currentItem);
      this.mainInventory.set(this.currentItem, this.mainInventory.get(p_184430_1_));
      this.mainInventory.set(p_184430_1_, itemstack);
   }

   public static boolean isHotbar(int p_184435_0_) {
      return p_184435_0_ >= 0 && p_184435_0_ < 9;
   }

   @OnlyIn(Dist.CLIENT)
   public int getSlotFor(ItemStack p_184429_1_) {
      for(int i = 0; i < this.mainInventory.size(); ++i) {
         if (!this.mainInventory.get(i).isEmpty() && this.stackEqualExact(p_184429_1_, this.mainInventory.get(i))) {
            return i;
         }
      }

      return -1;
   }

   public int findSlotMatchingUnusedItem(ItemStack p_194014_1_) {
      for(int i = 0; i < this.mainInventory.size(); ++i) {
         ItemStack itemstack = this.mainInventory.get(i);
         if (!this.mainInventory.get(i).isEmpty() && this.stackEqualExact(p_194014_1_, this.mainInventory.get(i)) && !this.mainInventory.get(i).isDamaged() && !itemstack.isEnchanted() && !itemstack.hasDisplayName()) {
            return i;
         }
      }

      return -1;
   }

   public int getBestHotbarSlot() {
      for(int i = 0; i < 9; ++i) {
         int j = (this.currentItem + i) % 9;
         if (this.mainInventory.get(j).isEmpty()) {
            return j;
         }
      }

      for(int k = 0; k < 9; ++k) {
         int l = (this.currentItem + k) % 9;
         if (!this.mainInventory.get(l).isEnchanted()) {
            return l;
         }
      }

      return this.currentItem;
   }

   @OnlyIn(Dist.CLIENT)
   public void changeCurrentItem(double p_195409_1_) {
      if (p_195409_1_ > 0.0D) {
         p_195409_1_ = 1.0D;
      }

      if (p_195409_1_ < 0.0D) {
         p_195409_1_ = -1.0D;
      }

      for(this.currentItem = (int)((double)this.currentItem - p_195409_1_); this.currentItem < 0; this.currentItem += 9) {
      }

      while(this.currentItem >= 9) {
         this.currentItem -= 9;
      }

   }

   public int clearMatchingItems(Predicate<ItemStack> p_195408_1_, int p_195408_2_) {
      int i = 0;

      for(int j = 0; j < this.getSizeInventory(); ++j) {
         ItemStack itemstack = this.getStackInSlot(j);
         if (!itemstack.isEmpty() && p_195408_1_.test(itemstack)) {
            int k = p_195408_2_ <= 0 ? itemstack.getCount() : Math.min(p_195408_2_ - i, itemstack.getCount());
            i += k;
            if (p_195408_2_ != 0) {
               itemstack.shrink(k);
               if (itemstack.isEmpty()) {
                  this.setInventorySlotContents(j, ItemStack.EMPTY);
               }

               if (p_195408_2_ > 0 && i >= p_195408_2_) {
                  return i;
               }
            }
         }
      }

      if (!this.itemStack.isEmpty() && p_195408_1_.test(this.itemStack)) {
         int l = p_195408_2_ <= 0 ? this.itemStack.getCount() : Math.min(p_195408_2_ - i, this.itemStack.getCount());
         i += l;
         if (p_195408_2_ != 0) {
            this.itemStack.shrink(l);
            if (this.itemStack.isEmpty()) {
               this.itemStack = ItemStack.EMPTY;
            }

            if (p_195408_2_ > 0 && i >= p_195408_2_) {
               return i;
            }
         }
      }

      return i;
   }

   private int storePartialItemStack(ItemStack p_70452_1_) {
      int i = this.storeItemStack(p_70452_1_);
      if (i == -1) {
         i = this.getFirstEmptyStack();
      }

      return i == -1 ? p_70452_1_.getCount() : this.addResource(i, p_70452_1_);
   }

   private int addResource(int p_191973_1_, ItemStack p_191973_2_) {
      Item item = p_191973_2_.getItem();
      int i = p_191973_2_.getCount();
      ItemStack itemstack = this.getStackInSlot(p_191973_1_);
      if (itemstack.isEmpty()) {
         itemstack = new ItemStack(item, 0);
         if (p_191973_2_.hasTag()) {
            itemstack.setTag(p_191973_2_.getTag().copy());
         }

         this.setInventorySlotContents(p_191973_1_, itemstack);
      }

      int j = i;
      if (i > itemstack.getMaxStackSize() - itemstack.getCount()) {
         j = itemstack.getMaxStackSize() - itemstack.getCount();
      }

      if (j > this.getInventoryStackLimit() - itemstack.getCount()) {
         j = this.getInventoryStackLimit() - itemstack.getCount();
      }

      if (j == 0) {
         return i;
      } else {
         i = i - j;
         itemstack.grow(j);
         itemstack.setAnimationsToGo(5);
         return i;
      }
   }

   public int storeItemStack(ItemStack p_70432_1_) {
      if (this.canMergeStacks(this.getStackInSlot(this.currentItem), p_70432_1_)) {
         return this.currentItem;
      } else if (this.canMergeStacks(this.getStackInSlot(40), p_70432_1_)) {
         return 40;
      } else {
         for(int i = 0; i < this.mainInventory.size(); ++i) {
            if (this.canMergeStacks(this.mainInventory.get(i), p_70432_1_)) {
               return i;
            }
         }

         return -1;
      }
   }

   public void tick() {
      for(NonNullList<ItemStack> nonnulllist : this.allInventories) {
         for(int i = 0; i < nonnulllist.size(); ++i) {
            if (!nonnulllist.get(i).isEmpty()) {
               nonnulllist.get(i).inventoryTick(this.player.world, this.player, i, this.currentItem == i);
            }
         }
      }

   }

   public boolean addItemStackToInventory(ItemStack p_70441_1_) {
      return this.add(-1, p_70441_1_);
   }

   public boolean add(int p_191971_1_, ItemStack p_191971_2_) {
      if (p_191971_2_.isEmpty()) {
         return false;
      } else {
         try {
            if (p_191971_2_.isDamaged()) {
               if (p_191971_1_ == -1) {
                  p_191971_1_ = this.getFirstEmptyStack();
               }

               if (p_191971_1_ >= 0) {
                  this.mainInventory.set(p_191971_1_, p_191971_2_.copy());
                  this.mainInventory.get(p_191971_1_).setAnimationsToGo(5);
                  p_191971_2_.setCount(0);
                  return true;
               } else if (this.player.capabilities.isCreativeMode) {
                  p_191971_2_.setCount(0);
                  return true;
               } else {
                  return false;
               }
            } else {
               int i;
               while(true) {
                  i = p_191971_2_.getCount();
                  if (p_191971_1_ == -1) {
                     p_191971_2_.setCount(this.storePartialItemStack(p_191971_2_));
                  } else {
                     p_191971_2_.setCount(this.addResource(p_191971_1_, p_191971_2_));
                  }

                  if (p_191971_2_.isEmpty() || p_191971_2_.getCount() >= i) {
                     break;
                  }
               }

               if (p_191971_2_.getCount() == i && this.player.capabilities.isCreativeMode) {
                  p_191971_2_.setCount(0);
                  return true;
               } else {
                  return p_191971_2_.getCount() < i;
               }
            }
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Adding item to inventory");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being added");
            crashreportcategory.addCrashSection("Item ID", Item.getIdFromItem(p_191971_2_.getItem()));
            crashreportcategory.addCrashSection("Item data", p_191971_2_.getDamage());
            crashreportcategory.addDetail("Item name", () -> {
               return p_191971_2_.getDisplayName().getString();
            });
            throw new ReportedException(crashreport);
         }
      }
   }

   public void placeItemBackInInventory(World p_191975_1_, ItemStack p_191975_2_) {
      if (!p_191975_1_.isRemote) {
         while(!p_191975_2_.isEmpty()) {
            int i = this.storeItemStack(p_191975_2_);
            if (i == -1) {
               i = this.getFirstEmptyStack();
            }

            if (i == -1) {
               this.player.dropItem(p_191975_2_, false);
               break;
            }

            int j = p_191975_2_.getMaxStackSize() - this.getStackInSlot(i).getCount();
            if (this.add(i, p_191975_2_.split(j))) {
               ((EntityPlayerMP)this.player).connection.sendPacket(new SPacketSetSlot(-2, i, this.getStackInSlot(i)));
            }
         }

      }
   }

   public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
      List<ItemStack> list = null;

      for(NonNullList<ItemStack> nonnulllist : this.allInventories) {
         if (p_70298_1_ < nonnulllist.size()) {
            list = nonnulllist;
            break;
         }

         p_70298_1_ -= nonnulllist.size();
      }

      return list != null && !list.get(p_70298_1_).isEmpty() ? ItemStackHelper.getAndSplit(list, p_70298_1_, p_70298_2_) : ItemStack.EMPTY;
   }

   public void deleteStack(ItemStack p_184437_1_) {
      for(NonNullList<ItemStack> nonnulllist : this.allInventories) {
         for(int i = 0; i < nonnulllist.size(); ++i) {
            if (nonnulllist.get(i) == p_184437_1_) {
               nonnulllist.set(i, ItemStack.EMPTY);
               break;
            }
         }
      }

   }

   public ItemStack removeStackFromSlot(int p_70304_1_) {
      NonNullList<ItemStack> nonnulllist = null;

      for(NonNullList<ItemStack> nonnulllist1 : this.allInventories) {
         if (p_70304_1_ < nonnulllist1.size()) {
            nonnulllist = nonnulllist1;
            break;
         }

         p_70304_1_ -= nonnulllist1.size();
      }

      if (nonnulllist != null && !nonnulllist.get(p_70304_1_).isEmpty()) {
         ItemStack itemstack = nonnulllist.get(p_70304_1_);
         nonnulllist.set(p_70304_1_, ItemStack.EMPTY);
         return itemstack;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
      NonNullList<ItemStack> nonnulllist = null;

      for(NonNullList<ItemStack> nonnulllist1 : this.allInventories) {
         if (p_70299_1_ < nonnulllist1.size()) {
            nonnulllist = nonnulllist1;
            break;
         }

         p_70299_1_ -= nonnulllist1.size();
      }

      if (nonnulllist != null) {
         nonnulllist.set(p_70299_1_, p_70299_2_);
      }

   }

   public float getDestroySpeed(IBlockState p_184438_1_) {
      return this.mainInventory.get(this.currentItem).getDestroySpeed(p_184438_1_);
   }

   public NBTTagList writeToNBT(NBTTagList p_70442_1_) {
      for(int i = 0; i < this.mainInventory.size(); ++i) {
         if (!this.mainInventory.get(i).isEmpty()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setByte("Slot", (byte)i);
            this.mainInventory.get(i).write(nbttagcompound);
            p_70442_1_.add(nbttagcompound);
         }
      }

      for(int j = 0; j < this.armorInventory.size(); ++j) {
         if (!this.armorInventory.get(j).isEmpty()) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.setByte("Slot", (byte)(j + 100));
            this.armorInventory.get(j).write(nbttagcompound1);
            p_70442_1_.add(nbttagcompound1);
         }
      }

      for(int k = 0; k < this.offHandInventory.size(); ++k) {
         if (!this.offHandInventory.get(k).isEmpty()) {
            NBTTagCompound nbttagcompound2 = new NBTTagCompound();
            nbttagcompound2.setByte("Slot", (byte)(k + 150));
            this.offHandInventory.get(k).write(nbttagcompound2);
            p_70442_1_.add(nbttagcompound2);
         }
      }

      return p_70442_1_;
   }

   public void readFromNBT(NBTTagList p_70443_1_) {
      this.mainInventory.clear();
      this.armorInventory.clear();
      this.offHandInventory.clear();

      for(int i = 0; i < p_70443_1_.size(); ++i) {
         NBTTagCompound nbttagcompound = p_70443_1_.getCompoundTagAt(i);
         int j = nbttagcompound.getByte("Slot") & 255;
         ItemStack itemstack = ItemStack.loadFromNBT(nbttagcompound);
         if (!itemstack.isEmpty()) {
            if (j >= 0 && j < this.mainInventory.size()) {
               this.mainInventory.set(j, itemstack);
            } else if (j >= 100 && j < this.armorInventory.size() + 100) {
               this.armorInventory.set(j - 100, itemstack);
            } else if (j >= 150 && j < this.offHandInventory.size() + 150) {
               this.offHandInventory.set(j - 150, itemstack);
            }
         }
      }

   }

   public int getSizeInventory() {
      return this.mainInventory.size() + this.armorInventory.size() + this.offHandInventory.size();
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.mainInventory) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      for(ItemStack itemstack1 : this.armorInventory) {
         if (!itemstack1.isEmpty()) {
            return false;
         }
      }

      for(ItemStack itemstack2 : this.offHandInventory) {
         if (!itemstack2.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getStackInSlot(int p_70301_1_) {
      List<ItemStack> list = null;

      for(NonNullList<ItemStack> nonnulllist : this.allInventories) {
         if (p_70301_1_ < nonnulllist.size()) {
            list = nonnulllist;
            break;
         }

         p_70301_1_ -= nonnulllist.size();
      }

      return list == null ? ItemStack.EMPTY : list.get(p_70301_1_);
   }

   public ITextComponent getName() {
      return new TextComponentTranslation("container.inventory");
   }

   @Nullable
   public ITextComponent getCustomName() {
      return null;
   }

   public boolean hasCustomName() {
      return false;
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean canHarvestBlock(IBlockState p_184432_1_) {
      return this.getStackInSlot(this.currentItem).canHarvestBlock(p_184432_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack armorItemInSlot(int p_70440_1_) {
      return this.armorInventory.get(p_70440_1_);
   }

   public void damageArmor(float p_70449_1_) {
      if (!(p_70449_1_ <= 0.0F)) {
         p_70449_1_ = p_70449_1_ / 4.0F;
         if (p_70449_1_ < 1.0F) {
            p_70449_1_ = 1.0F;
         }

         for(int i = 0; i < this.armorInventory.size(); ++i) {
            ItemStack itemstack = this.armorInventory.get(i);
            if (itemstack.getItem() instanceof ItemArmor) {
               itemstack.damageItem((int)p_70449_1_, this.player);
            }
         }

      }
   }

   public void dropAllItems() {
      for(List<ItemStack> list : this.allInventories) {
         for(int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = list.get(i);
            if (!itemstack.isEmpty()) {
               this.player.dropItem(itemstack, true, false);
               list.set(i, ItemStack.EMPTY);
            }
         }
      }

   }

   public void markDirty() {
      ++this.timesChanged;
   }

   @OnlyIn(Dist.CLIENT)
   public int getTimesChanged() {
      return this.timesChanged;
   }

   public void setItemStack(ItemStack p_70437_1_) {
      this.itemStack = p_70437_1_;
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public boolean isUsableByPlayer(EntityPlayer p_70300_1_) {
      if (this.player.isDead) {
         return false;
      } else {
         return !(p_70300_1_.getDistanceSq(this.player) > 64.0D);
      }
   }

   public boolean hasItemStack(ItemStack p_70431_1_) {
      label23:
      for(List<ItemStack> list : this.allInventories) {
         Iterator iterator = list.iterator();

         while(true) {
            if (!iterator.hasNext()) {
               continue label23;
            }

            ItemStack itemstack = (ItemStack)iterator.next();
            if (!itemstack.isEmpty() && itemstack.isItemEqual(p_70431_1_)) {
               break;
            }
         }

         return true;
      }

      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasTag(Tag<Item> p_199712_1_) {
      label23:
      for(List<ItemStack> list : this.allInventories) {
         Iterator iterator = list.iterator();

         while(true) {
            if (!iterator.hasNext()) {
               continue label23;
            }

            ItemStack itemstack = (ItemStack)iterator.next();
            if (!itemstack.isEmpty() && p_199712_1_.contains(itemstack.getItem())) {
               break;
            }
         }

         return true;
      }

      return false;
   }

   public void openInventory(EntityPlayer p_174889_1_) {
   }

   public void closeInventory(EntityPlayer p_174886_1_) {
   }

   public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
      return true;
   }

   public void copyInventory(InventoryPlayer p_70455_1_) {
      for(int i = 0; i < this.getSizeInventory(); ++i) {
         this.setInventorySlotContents(i, p_70455_1_.getStackInSlot(i));
      }

      this.currentItem = p_70455_1_.currentItem;
   }

   public int getField(int p_174887_1_) {
      return 0;
   }

   public void setField(int p_174885_1_, int p_174885_2_) {
   }

   public int getFieldCount() {
      return 0;
   }

   public void clear() {
      for(List<ItemStack> list : this.allInventories) {
         list.clear();
      }

   }

   public void func_201571_a(RecipeItemHelper p_201571_1_) {
      for(ItemStack itemstack : this.mainInventory) {
         p_201571_1_.accountPlainStack(itemstack);
      }

   }
}
