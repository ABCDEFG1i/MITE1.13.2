package net.minecraft.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityJukebox extends TileEntity {
   private ItemStack record = ItemStack.EMPTY;

   public TileEntityJukebox() {
      super(TileEntityType.JUKEBOX);
   }

   public void readFromNBT(NBTTagCompound p_145839_1_) {
      super.readFromNBT(p_145839_1_);
      if (p_145839_1_.hasKey("RecordItem", 10)) {
         this.setRecord(ItemStack.loadFromNBT(p_145839_1_.getCompoundTag("RecordItem")));
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189515_1_) {
      super.writeToNBT(p_189515_1_);
      if (!this.getRecord().isEmpty()) {
         p_189515_1_.setTag("RecordItem", this.getRecord().write(new NBTTagCompound()));
      }

      return p_189515_1_;
   }

   public ItemStack getRecord() {
      return this.record;
   }

   public void setRecord(ItemStack p_195535_1_) {
      this.record = p_195535_1_;
      this.markDirty();
   }
}
