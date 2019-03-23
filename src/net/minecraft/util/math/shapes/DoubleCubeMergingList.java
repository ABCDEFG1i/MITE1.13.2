package net.minecraft.util.math.shapes;

import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public final class DoubleCubeMergingList implements IDoubleListMerger {
   private final DoubleRangeList field_212436_a;
   private final int field_197859_a;
   private final int field_197860_b;
   private final int field_197861_c;

   DoubleCubeMergingList(int p_i47687_1_, int p_i47687_2_) {
      this.field_212436_a = new DoubleRangeList((int)VoxelShapes.func_197877_a(p_i47687_1_, p_i47687_2_));
      this.field_197859_a = p_i47687_1_;
      this.field_197860_b = p_i47687_2_;
      this.field_197861_c = IntMath.gcd(p_i47687_1_, p_i47687_2_);
   }

   public boolean func_197855_a(IDoubleListMerger.Consumer p_197855_1_) {
      int i = this.field_197859_a / this.field_197861_c;
      int j = this.field_197860_b / this.field_197861_c;

      for(int k = 0; k <= this.field_212436_a.size(); ++k) {
         if (!p_197855_1_.merge(k / j, k / i, k)) {
            return false;
         }
      }

      return true;
   }

   public DoubleList func_212435_a() {
      return this.field_212436_a;
   }
}
