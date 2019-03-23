package net.minecraft.nbt;

public class NBTSizeTracker {
   public static final NBTSizeTracker INFINITE = new NBTSizeTracker(0L) {
      public void read(long p_152450_1_) {
      }
   };
   private final long max;
   private long read;

   public NBTSizeTracker(long p_i46342_1_) {
      this.max = p_i46342_1_;
   }

   public void read(long p_152450_1_) {
      this.read += p_152450_1_ / 8L;
      if (this.read > this.max) {
         throw new RuntimeException("Tried to read NBT tag that was too big; tried to allocate: " + this.read + "bytes where max allowed: " + this.max);
      }
   }
}
