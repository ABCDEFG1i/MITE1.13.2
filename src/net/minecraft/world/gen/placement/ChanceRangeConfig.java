package net.minecraft.world.gen.placement;

public class ChanceRangeConfig implements IPlacementConfig {
   public final float chance;
   public final int field_202489_b;
   public final int minHeight;
   public final int maxHeight;

   public ChanceRangeConfig(float p_i48687_1_, int p_i48687_2_, int p_i48687_3_, int p_i48687_4_) {
      this.chance = p_i48687_1_;
      this.minHeight = p_i48687_2_;
      this.field_202489_b = p_i48687_3_;
      this.maxHeight = p_i48687_4_;
   }
}
