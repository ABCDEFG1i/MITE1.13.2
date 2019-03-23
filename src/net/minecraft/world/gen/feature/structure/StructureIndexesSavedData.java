package net.minecraft.world.gen.feature.structure;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;

public class StructureIndexesSavedData extends WorldSavedData {
   private LongSet all = new LongOpenHashSet();
   private LongSet remaining = new LongOpenHashSet();

   public StructureIndexesSavedData(String p_i48654_1_) {
      super(p_i48654_1_);
   }

   public void readFromNBT(NBTTagCompound p_76184_1_) {
      this.all = new LongOpenHashSet(p_76184_1_.readLongArray("All"));
      this.remaining = new LongOpenHashSet(p_76184_1_.readLongArray("Remaining"));
   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189551_1_) {
      p_189551_1_.setLongArray("All", this.all.toLongArray());
      p_189551_1_.setLongArray("Remaining", this.remaining.toLongArray());
      return p_189551_1_;
   }

   public void func_201763_a(long p_201763_1_) {
      this.all.add(p_201763_1_);
      this.remaining.add(p_201763_1_);
   }

   public boolean func_208024_b(long p_208024_1_) {
      return this.all.contains(p_208024_1_);
   }

   public boolean func_208023_c(long p_208023_1_) {
      return this.remaining.contains(p_208023_1_);
   }

   public void func_201762_c(long p_201762_1_) {
      this.remaining.remove(p_201762_1_);
   }

   public LongSet getAll() {
      return this.all;
   }
}
