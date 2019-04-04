package net.minecraft.tileentity;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class TileEntityDispenser extends TileEntityLockableLoot {
   private static final Random RNG = new Random();
   private NonNullList<ItemStack> stacks = NonNullList.withSize(9, ItemStack.EMPTY);

   protected TileEntityDispenser(TileEntityType<?> p_i48286_1_) {
      super(p_i48286_1_);
   }

   public TileEntityDispenser() {
      this(TileEntityType.DISPENSER);
   }

   public int getSizeInventory() {
      return 9;
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.stacks) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public int getDispenseSlot() {
      this.fillWithLoot(null);
      int i = -1;
      int j = 1;

      for(int k = 0; k < this.stacks.size(); ++k) {
         if (!this.stacks.get(k).isEmpty() && RNG.nextInt(j++) == 0) {
            i = k;
         }
      }

      return i;
   }

   public int addItemStack(ItemStack p_146019_1_) {
      for(int i = 0; i < this.stacks.size(); ++i) {
         if (this.stacks.get(i).isEmpty()) {
            this.setInventorySlotContents(i, p_146019_1_);
            return i;
         }
      }

      return -1;
   }

   public ITextComponent getName() {
      ITextComponent itextcomponent = this.getCustomName();
      return itextcomponent != null ? itextcomponent : new TextComponentTranslation("container.dispenser");
   }

   public void readFromNBT(NBTTagCompound p_145839_1_) {
      super.readFromNBT(p_145839_1_);
      this.stacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      if (!this.checkLootAndRead(p_145839_1_)) {
         ItemStackHelper.loadAllItems(p_145839_1_, this.stacks);
      }

      if (p_145839_1_.hasKey("CustomName", 8)) {
         this.customName = ITextComponent.Serializer.fromJson(p_145839_1_.getString("CustomName"));
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189515_1_) {
      super.writeToNBT(p_189515_1_);
      if (!this.checkLootAndWrite(p_189515_1_)) {
         ItemStackHelper.saveAllItems(p_189515_1_, this.stacks);
      }

      ITextComponent itextcomponent = this.getCustomName();
      if (itextcomponent != null) {
         p_189515_1_.setString("CustomName", ITextComponent.Serializer.toJson(itextcomponent));
      }

      return p_189515_1_;
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public String getGuiID() {
      return "minecraft:dispenser";
   }

   public Container createContainer(InventoryPlayer p_174876_1_, EntityPlayer p_174876_2_) {
      this.fillWithLoot(p_174876_2_);
      return new ContainerDispenser(p_174876_1_, this);
   }

   protected NonNullList<ItemStack> getItems() {
      return this.stacks;
   }

   protected void setItems(NonNullList<ItemStack> p_199721_1_) {
      this.stacks = p_199721_1_;
   }
}
