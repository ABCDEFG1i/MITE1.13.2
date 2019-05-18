package net.minecraft.entity.item;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.loot.ILootContainer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

public abstract class EntityMinecartContainer extends EntityMinecart implements ILockableContainer, ILootContainer {
   private NonNullList<ItemStack> minecartContainerItems = NonNullList.withSize(36, ItemStack.EMPTY);
   public boolean dropContentsWhenDead = true;
   private ResourceLocation lootTable;
   private long lootTableSeed;

   protected EntityMinecartContainer(EntityType<?> p_i48536_1_, World p_i48536_2_) {
      super(p_i48536_1_, p_i48536_2_);
   }

   protected EntityMinecartContainer(EntityType<?> p_i48537_1_, double p_i48537_2_, double p_i48537_4_, double p_i48537_6_, World p_i48537_8_) {
      super(p_i48537_1_, p_i48537_8_, p_i48537_2_, p_i48537_4_, p_i48537_6_);
   }

   public void killMinecart(DamageSource p_94095_1_) {
      super.killMinecart(p_94095_1_);
      if (this.world.getGameRules().getBoolean("doEntityDrops")) {
         InventoryHelper.dropInventoryItems(this.world, this, this);
      }

   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.minecartContainerItems) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getStackInSlot(int p_70301_1_) {
      this.addLoot(null);
      return this.minecartContainerItems.get(p_70301_1_);
   }

   public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
      this.addLoot(null);
      return ItemStackHelper.getAndSplit(this.minecartContainerItems, p_70298_1_, p_70298_2_);
   }

   public ItemStack removeStackFromSlot(int p_70304_1_) {
      this.addLoot(null);
      ItemStack itemstack = this.minecartContainerItems.get(p_70304_1_);
      if (itemstack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.minecartContainerItems.set(p_70304_1_, ItemStack.EMPTY);
         return itemstack;
      }
   }

   public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
      this.addLoot(null);
      this.minecartContainerItems.set(p_70299_1_, p_70299_2_);
      if (!p_70299_2_.isEmpty() && p_70299_2_.getCount() > this.getInventoryStackLimit()) {
         p_70299_2_.setCount(this.getInventoryStackLimit());
      }

   }

   public boolean replaceItemInInventory(int p_174820_1_, ItemStack p_174820_2_) {
      if (p_174820_1_ >= 0 && p_174820_1_ < this.getSizeInventory()) {
         this.setInventorySlotContents(p_174820_1_, p_174820_2_);
         return true;
      } else {
         return false;
      }
   }

   public void markDirty() {
   }

   public boolean isUsableByPlayer(EntityPlayer p_70300_1_) {
      if (this.isDead) {
         return false;
      } else {
         return !(p_70300_1_.getDistanceSq(this) > 64.0D);
      }
   }

   public void openInventory(EntityPlayer p_174889_1_) {
   }

   public void closeInventory(EntityPlayer p_174886_1_) {
   }

   public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
      return true;
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   @Nullable
   public Entity func_212321_a(DimensionType target) {
      this.dropContentsWhenDead = false;
      return super.func_212321_a(target);
   }

   public void setDead() {
      if (this.dropContentsWhenDead) {
         InventoryHelper.dropInventoryItems(this.world, this, this);
      }

      super.setDead();
   }

   public void setDropItemsWhenDead(boolean p_184174_1_) {
      this.dropContentsWhenDead = p_184174_1_;
   }

   protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
      super.writeEntityToNBT(p_70014_1_);
      if (this.lootTable != null) {
         p_70014_1_.setString("LootTable", this.lootTable.toString());
         if (this.lootTableSeed != 0L) {
            p_70014_1_.setLong("LootTableSeed", this.lootTableSeed);
         }
      } else {
         ItemStackHelper.saveAllItems(p_70014_1_, this.minecartContainerItems);
      }

   }

   protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
      super.readEntityFromNBT(p_70037_1_);
      this.minecartContainerItems = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      if (p_70037_1_.hasKey("LootTable", 8)) {
         this.lootTable = new ResourceLocation(p_70037_1_.getString("LootTable"));
         this.lootTableSeed = p_70037_1_.getLong("LootTableSeed");
      } else {
         ItemStackHelper.loadAllItems(p_70037_1_, this.minecartContainerItems);
      }

   }

   public boolean processInitialInteract(EntityPlayer p_184230_1_, EnumHand p_184230_2_) {
      if (!this.world.isRemote) {
         p_184230_1_.displayGUIChest(this);
      }

      return true;
   }

   protected void applyDrag() {
      float f = 0.98F;
      if (this.lootTable == null) {
         int i = 15 - Container.calcRedstoneFromInventory(this);
         f += (float)i * 0.001F;
      }

      this.motionX *= (double)f;
      this.motionY *= 0.0D;
      this.motionZ *= (double)f;
   }

   public int getField(int p_174887_1_) {
      return 0;
   }

   public void setField(int p_174885_1_, int p_174885_2_) {
   }

   public int getFieldCount() {
      return 0;
   }

   public boolean isLocked() {
      return false;
   }

   public void setLockCode(LockCode p_174892_1_) {
   }

   public LockCode getLockCode() {
      return LockCode.EMPTY_CODE;
   }

   public void addLoot(@Nullable EntityPlayer p_184288_1_) {
      if (this.lootTable != null && this.world.getServer() != null) {
         LootTable loottable = this.world.getServer().getLootTableManager().getLootTableFromLocation(this.lootTable);
         this.lootTable = null;
         Random random;
         if (this.lootTableSeed == 0L) {
            random = new Random();
         } else {
            random = new Random(this.lootTableSeed);
         }

         LootContext.Builder lootcontext$builder = (new LootContext.Builder((WorldServer)this.world)).withPosition(new BlockPos(this));
         if (p_184288_1_ != null) {
            lootcontext$builder.withLuck(p_184288_1_.getLuck());
         }

         loottable.fillInventory(this, random, lootcontext$builder.build());
      }

   }

   public void clear() {
      this.addLoot(null);
      this.minecartContainerItems.clear();
   }

   public void setLootTable(ResourceLocation p_184289_1_, long p_184289_2_) {
      this.lootTable = p_184289_1_;
      this.lootTableSeed = p_184289_2_;
   }

   public ResourceLocation getLootTable() {
      return this.lootTable;
   }
}
