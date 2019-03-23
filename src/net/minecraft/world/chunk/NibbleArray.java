package net.minecraft.world.chunk;

public class NibbleArray {
   private final byte[] data;

   public NibbleArray() {
      this.data = new byte[2048];
   }

   public NibbleArray(byte[] p_i45646_1_) {
      this.data = p_i45646_1_;
      if (p_i45646_1_.length != 2048) {
         throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + p_i45646_1_.length);
      }
   }

   public int get(int p_76582_1_, int p_76582_2_, int p_76582_3_) {
      return this.getFromIndex(this.getCoordinateIndex(p_76582_1_, p_76582_2_, p_76582_3_));
   }

   public void set(int p_76581_1_, int p_76581_2_, int p_76581_3_, int p_76581_4_) {
      this.setIndex(this.getCoordinateIndex(p_76581_1_, p_76581_2_, p_76581_3_), p_76581_4_);
   }

   private int getCoordinateIndex(int p_177483_1_, int p_177483_2_, int p_177483_3_) {
      return p_177483_2_ << 8 | p_177483_3_ << 4 | p_177483_1_;
   }

   public int getFromIndex(int p_177480_1_) {
      int i = this.getNibbleIndex(p_177480_1_);
      return this.isLowerNibble(p_177480_1_) ? this.data[i] & 15 : this.data[i] >> 4 & 15;
   }

   public void setIndex(int p_177482_1_, int p_177482_2_) {
      int i = this.getNibbleIndex(p_177482_1_);
      if (this.isLowerNibble(p_177482_1_)) {
         this.data[i] = (byte)(this.data[i] & 240 | p_177482_2_ & 15);
      } else {
         this.data[i] = (byte)(this.data[i] & 15 | (p_177482_2_ & 15) << 4);
      }

   }

   private boolean isLowerNibble(int p_177479_1_) {
      return (p_177479_1_ & 1) == 0;
   }

   private int getNibbleIndex(int p_177478_1_) {
      return p_177478_1_ >> 1;
   }

   public byte[] getData() {
      return this.data;
   }
}
