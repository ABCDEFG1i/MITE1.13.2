package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;

public class SimpleDoubleMerger implements IDoubleListMerger {
   private final DoubleList list;

   public SimpleDoubleMerger(DoubleList p_i49559_1_) {
      this.list = p_i49559_1_;
   }

   public boolean func_197855_a(IDoubleListMerger.Consumer p_197855_1_) {
      for(int i = 0; i <= this.list.size(); ++i) {
         if (!p_197855_1_.merge(i, i, i)) {
            return false;
         }
      }

      return true;
   }

   public DoubleList func_212435_a() {
      return this.list;
   }
}
