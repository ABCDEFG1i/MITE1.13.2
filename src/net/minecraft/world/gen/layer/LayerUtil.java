package net.minecraft.world.gen.layer;

import com.google.common.collect.ImmutableList;
import java.util.function.LongFunction;
import net.minecraft.init.Biomes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IContextExtended;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;

public class LayerUtil {
   protected static final int WARM_OCEAN = IRegistry.field_212624_m.func_148757_b(Biomes.WARM_OCEAN);
   protected static final int LUKEWARM_OCEAN = IRegistry.field_212624_m.func_148757_b(Biomes.LUKEWARM_OCEAN);
   protected static final int OCEAN = IRegistry.field_212624_m.func_148757_b(Biomes.OCEAN);
   protected static final int COLD_OCEAN = IRegistry.field_212624_m.func_148757_b(Biomes.COLD_OCEAN);
   protected static final int FROZEN_OCEAN = IRegistry.field_212624_m.func_148757_b(Biomes.FROZEN_OCEAN);
   protected static final int DEEP_WARM_OCEAN = IRegistry.field_212624_m.func_148757_b(Biomes.DEEP_WARM_OCEAN);
   protected static final int DEEP_LUKEWARM_OCEAN = IRegistry.field_212624_m.func_148757_b(Biomes.DEEP_LUKEWARM_OCEAN);
   protected static final int DEEP_OCEAN = IRegistry.field_212624_m.func_148757_b(Biomes.DEEP_OCEAN);
   protected static final int DEEP_COLD_OCEAN = IRegistry.field_212624_m.func_148757_b(Biomes.DEEP_COLD_OCEAN);
   protected static final int DEEP_FROZEN_OCEAN = IRegistry.field_212624_m.func_148757_b(Biomes.DEEP_FROZEN_OCEAN);

   public static <T extends IArea, C extends IContextExtended<T>> IAreaFactory<T> func_202829_a(long p_202829_0_, IAreaTransformer1 p_202829_2_, IAreaFactory<T> p_202829_3_, int p_202829_4_, LongFunction<C> p_202829_5_) {
      IAreaFactory<T> iareafactory = p_202829_3_;

      for(int i = 0; i < p_202829_4_; ++i) {
         iareafactory = p_202829_2_.apply(p_202829_5_.apply(p_202829_0_ + (long)i), iareafactory);
      }

      return iareafactory;
   }

   public static <T extends IArea, C extends IContextExtended<T>> ImmutableList<IAreaFactory<T>> func_202828_a(WorldType p_202828_0_, OverworldGenSettings p_202828_1_, LongFunction<C> p_202828_2_) {
      IAreaFactory<T> iareafactory = GenLayerIsland.INSTANCE.apply(p_202828_2_.apply(1L));
      iareafactory = GenLayerZoom.FUZZY.apply(p_202828_2_.apply(2000L), iareafactory);
      iareafactory = GenLayerAddIsland.INSTANCE.apply(p_202828_2_.apply(1L), iareafactory);
      iareafactory = GenLayerZoom.NORMAL.apply(p_202828_2_.apply(2001L), iareafactory);
      iareafactory = GenLayerAddIsland.INSTANCE.apply(p_202828_2_.apply(2L), iareafactory);
      iareafactory = GenLayerAddIsland.INSTANCE.apply(p_202828_2_.apply(50L), iareafactory);
      iareafactory = GenLayerAddIsland.INSTANCE.apply(p_202828_2_.apply(70L), iareafactory);
      iareafactory = GenLayerRemoveTooMuchOcean.INSTANCE.apply(p_202828_2_.apply(2L), iareafactory);
      IAreaFactory<T> iareafactory1 = OceanLayer.INSTANCE.apply(p_202828_2_.apply(2L));
      iareafactory1 = func_202829_a(2001L, GenLayerZoom.NORMAL, iareafactory1, 6, p_202828_2_);
      iareafactory = GenLayerAddSnow.INSTANCE.apply(p_202828_2_.apply(2L), iareafactory);
      iareafactory = GenLayerAddIsland.INSTANCE.apply(p_202828_2_.apply(3L), iareafactory);
      iareafactory = GenLayerEdge.CoolWarm.INSTANCE.apply(p_202828_2_.apply(2L), iareafactory);
      iareafactory = GenLayerEdge.HeatIce.INSTANCE.apply(p_202828_2_.apply(2L), iareafactory);
      iareafactory = GenLayerEdge.Special.INSTANCE.apply(p_202828_2_.apply(3L), iareafactory);
      iareafactory = GenLayerZoom.NORMAL.apply(p_202828_2_.apply(2002L), iareafactory);
      iareafactory = GenLayerZoom.NORMAL.apply(p_202828_2_.apply(2003L), iareafactory);
      iareafactory = GenLayerAddIsland.INSTANCE.apply(p_202828_2_.apply(4L), iareafactory);
      iareafactory = GenLayerAddMushroomIsland.INSTANCE.apply(p_202828_2_.apply(5L), iareafactory);
      iareafactory = GenLayerDeepOcean.INSTANCE.apply(p_202828_2_.apply(4L), iareafactory);
      iareafactory = func_202829_a(1000L, GenLayerZoom.NORMAL, iareafactory, 0, p_202828_2_);
      int i = 4;
      int j = i;
      if (p_202828_1_ != null) {
         i = p_202828_1_.getBiomeSize();
         j = p_202828_1_.func_202198_k();
      }

      if (p_202828_0_ == WorldType.LARGE_BIOMES) {
         i = 6;
      }

      IAreaFactory<T> lvt_7_1_ = func_202829_a(1000L, GenLayerZoom.NORMAL, iareafactory, 0, p_202828_2_);
      lvt_7_1_ = GenLayerRiverInit.INSTANCE.apply((IContextExtended)p_202828_2_.apply(100L), lvt_7_1_);
      IAreaFactory<T> lvt_8_1_ = (new GenLayerBiome(p_202828_0_, p_202828_1_)).apply(p_202828_2_.apply(200L), iareafactory);
      lvt_8_1_ = func_202829_a(1000L, GenLayerZoom.NORMAL, lvt_8_1_, 2, p_202828_2_);
      lvt_8_1_ = GenLayerBiomeEdge.INSTANCE.apply((IContextExtended)p_202828_2_.apply(1000L), lvt_8_1_);
      IAreaFactory<T> lvt_9_1_ = func_202829_a(1000L, GenLayerZoom.NORMAL, lvt_7_1_, 2, p_202828_2_);
      lvt_8_1_ = GenLayerHills.INSTANCE.apply((IContextExtended)p_202828_2_.apply(1000L), lvt_8_1_, lvt_9_1_);
      lvt_7_1_ = func_202829_a(1000L, GenLayerZoom.NORMAL, lvt_7_1_, 2, p_202828_2_);
      lvt_7_1_ = func_202829_a(1000L, GenLayerZoom.NORMAL, lvt_7_1_, j, p_202828_2_);
      lvt_7_1_ = GenLayerRiver.INSTANCE.apply((IContextExtended)p_202828_2_.apply(1L), lvt_7_1_);
      lvt_7_1_ = GenLayerSmooth.INSTANCE.apply((IContextExtended)p_202828_2_.apply(1000L), lvt_7_1_);
      lvt_8_1_ = GenLayerRareBiome.INSTANCE.apply((IContextExtended)p_202828_2_.apply(1001L), lvt_8_1_);

      for(int k = 0; k < i; ++k) {
         lvt_8_1_ = GenLayerZoom.NORMAL.apply((IContextExtended)p_202828_2_.apply((long)(1000 + k)), lvt_8_1_);
         if (k == 0) {
            lvt_8_1_ = GenLayerAddIsland.INSTANCE.apply((IContextExtended)p_202828_2_.apply(3L), lvt_8_1_);
         }

         if (k == 1 || i == 1) {
            lvt_8_1_ = GenLayerShore.INSTANCE.apply((IContextExtended)p_202828_2_.apply(1000L), lvt_8_1_);
         }
      }

      lvt_8_1_ = GenLayerSmooth.INSTANCE.apply((IContextExtended)p_202828_2_.apply(1000L), lvt_8_1_);
      lvt_8_1_ = GenLayerRiverMix.INSTANCE.apply((IContextExtended)p_202828_2_.apply(100L), lvt_8_1_, lvt_7_1_);
      lvt_8_1_ = GenLayerMixOceans.INSTANCE.apply(p_202828_2_.apply(100L), lvt_8_1_, iareafactory1);
      IAreaFactory<T> iareafactory5 = GenLayerVoronoiZoom.INSTANCE.apply(p_202828_2_.apply(10L), lvt_8_1_);
      return ImmutableList.of(lvt_8_1_, iareafactory5, lvt_8_1_);
   }

   public static GenLayer[] func_202824_a(long p_202824_0_, WorldType p_202824_2_, OverworldGenSettings p_202824_3_) {
      int i = 1;
      int[] aint = new int[1];
      ImmutableList<IAreaFactory<LazyArea>> immutablelist = func_202828_a(p_202824_2_, p_202824_3_, (p_202825_3_) -> {
         ++aint[0];
         return new LazyAreaLayerContext(1, aint[0], p_202824_0_, p_202825_3_);
      });
      GenLayer genlayer = new GenLayer(immutablelist.get(0));
      GenLayer genlayer1 = new GenLayer(immutablelist.get(1));
      GenLayer genlayer2 = new GenLayer(immutablelist.get(2));
      return new GenLayer[]{genlayer, genlayer1, genlayer2};
   }

   public static boolean func_202826_a(int p_202826_0_, int p_202826_1_) {
      if (p_202826_0_ == p_202826_1_) {
         return true;
      } else {
         Biome biome = IRegistry.field_212624_m.func_148754_a(p_202826_0_);
         Biome biome1 = IRegistry.field_212624_m.func_148754_a(p_202826_1_);
         if (biome != null && biome1 != null) {
            if (biome != Biomes.WOODED_BADLANDS_PLATEAU && biome != Biomes.BADLANDS_PLATEAU) {
               if (biome.getBiomeCategory() != Biome.Category.NONE && biome1.getBiomeCategory() != Biome.Category.NONE && biome.getBiomeCategory() == biome1.getBiomeCategory()) {
                  return true;
               } else {
                  return biome == biome1;
               }
            } else {
               return biome1 == Biomes.WOODED_BADLANDS_PLATEAU || biome1 == Biomes.BADLANDS_PLATEAU;
            }
         } else {
            return false;
         }
      }
   }

   protected static boolean isOcean(int p_202827_0_) {
      return p_202827_0_ == WARM_OCEAN || p_202827_0_ == LUKEWARM_OCEAN || p_202827_0_ == OCEAN || p_202827_0_ == COLD_OCEAN || p_202827_0_ == FROZEN_OCEAN || p_202827_0_ == DEEP_WARM_OCEAN || p_202827_0_ == DEEP_LUKEWARM_OCEAN || p_202827_0_ == DEEP_OCEAN || p_202827_0_ == DEEP_COLD_OCEAN || p_202827_0_ == DEEP_FROZEN_OCEAN;
   }

   protected static boolean isShallowOcean(int p_203631_0_) {
      return p_203631_0_ == WARM_OCEAN || p_203631_0_ == LUKEWARM_OCEAN || p_203631_0_ == OCEAN || p_203631_0_ == COLD_OCEAN || p_203631_0_ == FROZEN_OCEAN;
   }
}
