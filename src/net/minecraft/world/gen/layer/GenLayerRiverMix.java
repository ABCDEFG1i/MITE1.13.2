package net.minecraft.world.gen.layer;

import net.minecraft.init.Biomes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset0Transformer;

public enum GenLayerRiverMix implements IAreaTransformer2, IDimOffset0Transformer {
   INSTANCE;

   private static final int FROZEN_RIVER = IRegistry.field_212624_m.func_148757_b(Biomes.FROZEN_RIVER);
   private static final int SNOWY_TUNDRA = IRegistry.field_212624_m.func_148757_b(Biomes.SNOWY_TUNDRA);
   private static final int MUSHROOM_FIELDS = IRegistry.field_212624_m.func_148757_b(Biomes.MUSHROOM_FIELDS);
   private static final int MUSHROOM_FIELD_SHORE = IRegistry.field_212624_m.func_148757_b(Biomes.MUSHROOM_FIELD_SHORE);
   private static final int RIVER = IRegistry.field_212624_m.func_148757_b(Biomes.RIVER);

   public int apply(IContext p_202709_1_, AreaDimension p_202709_2_, IArea p_202709_3_, IArea p_202709_4_, int p_202709_5_, int p_202709_6_) {
      int i = p_202709_3_.getValue(p_202709_5_, p_202709_6_);
      int j = p_202709_4_.getValue(p_202709_5_, p_202709_6_);
      if (LayerUtil.isOcean(i)) {
         return i;
      } else if (j == RIVER) {
         if (i == SNOWY_TUNDRA) {
            return FROZEN_RIVER;
         } else {
            return i != MUSHROOM_FIELDS && i != MUSHROOM_FIELD_SHORE ? j & 255 : MUSHROOM_FIELD_SHORE;
         }
      } else {
         return i;
      }
   }
}
