package net.minecraft.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FrameTimer {
   private final long[] frames = new long[240];
   private int lastIndex;
   private int counter;
   private int index;

   public void addFrame(long p_181747_1_) {
      this.frames[this.index] = p_181747_1_;
      ++this.index;
      if (this.index == 240) {
         this.index = 0;
      }

      if (this.counter < 240) {
         this.lastIndex = 0;
         ++this.counter;
      } else {
         this.lastIndex = this.parseIndex(this.index + 1);
      }

   }

   public int getLagometerValue(long p_181748_1_, int p_181748_3_) {
      double d0 = (double)p_181748_1_ / 1.6666666E7D;
      return (int)(d0 * (double)p_181748_3_);
   }

   public int getLastIndex() {
      return this.lastIndex;
   }

   public int getIndex() {
      return this.index;
   }

   public int parseIndex(int p_181751_1_) {
      return p_181751_1_ % 240;
   }

   public long[] getFrames() {
      return this.frames;
   }
}
