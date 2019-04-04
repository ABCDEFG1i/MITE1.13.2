package net.minecraft.tileentity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.ILootContainer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

public abstract class TileEntityLockableLoot extends TileEntityLockable implements ILootContainer {
   protected ResourceLocation lootTable;
   protected long lootTableSeed;
   protected ITextComponent customName;

   protected TileEntityLockableLoot(TileEntityType<?> p_i48284_1_) {
      super(p_i48284_1_);
   }

   public static void setLootTable(IBlockReader p_195479_0_, Random p_195479_1_, BlockPos p_195479_2_, ResourceLocation p_195479_3_) {
      TileEntity tileentity = p_195479_0_.getTileEntity(p_195479_2_);
      if (tileentity instanceof TileEntityLockableLoot) {
         ((TileEntityLockableLoot)tileentity).setLootTable(p_195479_3_, p_195479_1_.nextLong());
      }

   }

   protected boolean checkLootAndRead(NBTTagCompound p_184283_1_) {
      if (p_184283_1_.hasKey("LootTable", 8)) {
         this.lootTable = new ResourceLocation(p_184283_1_.getString("LootTable"));
         this.lootTableSeed = p_184283_1_.getLong("LootTableSeed");
         return true;
      } else {
         return false;
      }
   }

   protected boolean checkLootAndWrite(NBTTagCompound p_184282_1_) {
      if (this.lootTable == null) {
         return false;
      } else {
         p_184282_1_.setString("LootTable", this.lootTable.toString());
         if (this.lootTableSeed != 0L) {
            p_184282_1_.setLong("LootTableSeed", this.lootTableSeed);
         }

         return true;
      }
   }

   public void fillWithLoot(@Nullable EntityPlayer p_184281_1_) {
      if (this.lootTable != null && this.world.getServer() != null) {
         LootTable loottable = this.world.getServer().getLootTableManager().getLootTableFromLocation(this.lootTable);
         this.lootTable = null;
         Random random;
         if (this.lootTableSeed == 0L) {
            random = new Random();
         } else {
            random = new Random(this.lootTableSeed);
         }

         LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer)this.world);
         lootcontext$builder.withPosition(this.pos);
         if (p_184281_1_ != null) {
            lootcontext$builder.withLuck(p_184281_1_.getLuck());
         }

         loottable.fillInventory(this, random, lootcontext$builder.build());
      }

   }

   public ResourceLocation getLootTable() {
      return this.lootTable;
   }

   public void setLootTable(ResourceLocation p_189404_1_, long p_189404_2_) {
      this.lootTable = p_189404_1_;
      this.lootTableSeed = p_189404_2_;
   }

   public boolean hasCustomName() {
      return this.customName != null;
   }

   public void setCustomName(@Nullable ITextComponent p_200226_1_) {
      this.customName = p_200226_1_;
   }

   @Nullable
   public ITextComponent getCustomName() {
      return this.customName;
   }

   public ItemStack getStackInSlot(int p_70301_1_) {
      this.fillWithLoot(null);
      return this.getItems().get(p_70301_1_);
   }

   public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
      this.fillWithLoot(null);
      ItemStack itemstack = ItemStackHelper.getAndSplit(this.getItems(), p_70298_1_, p_70298_2_);
      if (!itemstack.isEmpty()) {
         this.markDirty();
      }

      return itemstack;
   }

   public ItemStack removeStackFromSlot(int p_70304_1_) {
      this.fillWithLoot(null);
      return ItemStackHelper.getAndRemove(this.getItems(), p_70304_1_);
   }

   public void setInventorySlotContents(int p_70299_1_, @Nullable ItemStack p_70299_2_) {
      this.fillWithLoot(null);
      this.getItems().set(p_70299_1_, p_70299_2_);
      if (p_70299_2_.getCount() > this.getInventoryStackLimit()) {
         p_70299_2_.setCount(this.getInventoryStackLimit());
      }

      this.markDirty();
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
      return true;
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
      this.getItems().clear();
   }

   protected abstract NonNullList<ItemStack> getItems();

   protected abstract void setItems(NonNullList<ItemStack> p_199721_1_);
}
