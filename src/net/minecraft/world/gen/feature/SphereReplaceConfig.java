package net.minecraft.world.gen.feature;

import java.util.List;
import net.minecraft.block.Block;

public class SphereReplaceConfig implements IFeatureConfig {
   public final Block field_202431_a;
   public final int maxRange;
   public final int field_202433_c;
   public final List<Block> field_202434_d;

   public SphereReplaceConfig(Block p_i48684_1_, int maxRange, int p_i48684_3_, List<Block> p_i48684_4_) {
      this.field_202431_a = p_i48684_1_;
      this.maxRange = maxRange;
      this.field_202433_c = p_i48684_3_;
      this.field_202434_d = p_i48684_4_;
   }
}
