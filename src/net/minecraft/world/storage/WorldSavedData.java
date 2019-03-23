package net.minecraft.world.storage;

import net.minecraft.nbt.NBTTagCompound;

public abstract class WorldSavedData {
   private final String mapName;
   private boolean dirty;

   public WorldSavedData(String p_i2141_1_) {
      this.mapName = p_i2141_1_;
   }

   public abstract void readFromNBT(NBTTagCompound p_76184_1_);

   public abstract NBTTagCompound writeToNBT(NBTTagCompound p_189551_1_);

   public void markDirty() {
      this.setDirty(true);
   }

   public void setDirty(boolean p_76186_1_) {
      this.dirty = p_76186_1_;
   }

   public boolean isDirty() {
      return this.dirty;
   }

   public String getName() {
      return this.mapName;
   }
}
