package net.minecraft.world.gen.layer;

import net.minecraft.init.Biomes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.layer.traits.IC1Transformer;

public enum GenLayerRareBiome implements IC1Transformer {
   INSTANCE;

   private static final int PLAINS = IRegistry.field_212624_m.func_148757_b(Biomes.PLAINS);
   private static final int SUNFLOWER_PLAINS = IRegistry.field_212624_m.func_148757_b(Biomes.SUNFLOWER_PLAINS);

   public int apply(IContext p_202716_1_, int p_202716_2_) {
      return p_202716_1_.random(57) == 0 && p_202716_2_ == PLAINS ? SUNFLOWER_PLAINS : p_202716_2_;
   }
}
