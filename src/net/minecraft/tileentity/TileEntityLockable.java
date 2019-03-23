package net.minecraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;

public abstract class TileEntityLockable extends TileEntity implements ILockableContainer {
   private LockCode code = LockCode.EMPTY_CODE;

   protected TileEntityLockable(TileEntityType<?> p_i48285_1_) {
      super(p_i48285_1_);
   }

   public void readFromNBT(NBTTagCompound p_145839_1_) {
      super.readFromNBT(p_145839_1_);
      this.code = LockCode.fromNBT(p_145839_1_);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189515_1_) {
      super.writeToNBT(p_189515_1_);
      if (this.code != null) {
         this.code.toNBT(p_189515_1_);
      }

      return p_189515_1_;
   }

   public boolean isLocked() {
      return this.code != null && !this.code.isEmpty();
   }

   public LockCode getLockCode() {
      return this.code;
   }

   public void setLockCode(LockCode p_174892_1_) {
      this.code = p_174892_1_;
   }
}
