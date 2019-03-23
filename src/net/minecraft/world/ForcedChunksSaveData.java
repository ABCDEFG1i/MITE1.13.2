package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;

public class ForcedChunksSaveData extends WorldSavedData {
   private LongSet field_212439_a = new LongOpenHashSet();

   public ForcedChunksSaveData(String p_i49814_1_) {
      super(p_i49814_1_);
   }

   public void readFromNBT(NBTTagCompound p_76184_1_) {
      this.field_212439_a = new LongOpenHashSet(p_76184_1_.readLongArray("Forced"));
   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189551_1_) {
      p_189551_1_.setLongArray("Forced", this.field_212439_a.toLongArray());
      return p_189551_1_;
   }

   public LongSet func_212438_a() {
      return this.field_212439_a;
   }
}
