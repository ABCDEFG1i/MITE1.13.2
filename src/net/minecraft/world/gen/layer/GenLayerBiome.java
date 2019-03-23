package net.minecraft.world.gen.layer;

import net.minecraft.init.Biomes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

public class GenLayerBiome implements IC0Transformer {
   private static final int BIRCH_FOREST = IRegistry.field_212624_m.func_148757_b(Biomes.BIRCH_FOREST);
   private static final int DESERT = IRegistry.field_212624_m.func_148757_b(Biomes.DESERT);
   private static final int MOUNTAINS = IRegistry.field_212624_m.func_148757_b(Biomes.MOUNTAINS);
   private static final int FOREST = IRegistry.field_212624_m.func_148757_b(Biomes.FOREST);
   private static final int SNOWY_TUNDRA = IRegistry.field_212624_m.func_148757_b(Biomes.SNOWY_TUNDRA);
   private static final int JUNGLE = IRegistry.field_212624_m.func_148757_b(Biomes.JUNGLE);
   private static final int BADLANDS_PLATEAU = IRegistry.field_212624_m.func_148757_b(Biomes.BADLANDS_PLATEAU);
   private static final int WOODED_BADLANDS_PLATEAU = IRegistry.field_212624_m.func_148757_b(Biomes.WOODED_BADLANDS_PLATEAU);
   private static final int MUSHROOM_FIELDS = IRegistry.field_212624_m.func_148757_b(Biomes.MUSHROOM_FIELDS);
   private static final int PLAINS = IRegistry.field_212624_m.func_148757_b(Biomes.PLAINS);
   private static final int GIANT_TREE_TAIGA = IRegistry.field_212624_m.func_148757_b(Biomes.GIANT_TREE_TAIGA);
   private static final int DARK_FOREST = IRegistry.field_212624_m.func_148757_b(Biomes.DARK_FOREST);
   private static final int SAVANNA = IRegistry.field_212624_m.func_148757_b(Biomes.SAVANNA);
   private static final int SWAMP = IRegistry.field_212624_m.func_148757_b(Biomes.SWAMP);
   private static final int TAIGA = IRegistry.field_212624_m.func_148757_b(Biomes.TAIGA);
   private static final int SNOWY_TAIGA = IRegistry.field_212624_m.func_148757_b(Biomes.SNOWY_TAIGA);
   private static final int[] field_202743_q = new int[]{DESERT, FOREST, MOUNTAINS, SWAMP, PLAINS, TAIGA};
   private static final int[] field_202744_r = new int[]{DESERT, DESERT, DESERT, SAVANNA, SAVANNA, PLAINS};
   private static final int[] field_202745_s = new int[]{FOREST, DARK_FOREST, MOUNTAINS, PLAINS, BIRCH_FOREST, SWAMP};
   private static final int[] field_202746_t = new int[]{FOREST, MOUNTAINS, TAIGA, PLAINS};
   private static final int[] field_202747_u = new int[]{SNOWY_TUNDRA, SNOWY_TUNDRA, SNOWY_TUNDRA, SNOWY_TAIGA};
   private final OverworldGenSettings settings;
   private int[] warmBiomes = field_202744_r;

   public GenLayerBiome(WorldType p_i48641_1_, OverworldGenSettings p_i48641_2_) {
      if (p_i48641_1_ == WorldType.DEFAULT_1_1) {
         this.warmBiomes = field_202743_q;
         this.settings = null;
      } else {
         this.settings = p_i48641_2_;
      }

   }

   public int apply(IContext p_202726_1_, int p_202726_2_) {
      if (this.settings != null && this.settings.func_202199_l() >= 0) {
         return this.settings.func_202199_l();
      } else {
         int i = (p_202726_2_ & 3840) >> 8;
         p_202726_2_ = p_202726_2_ & -3841;
         if (!LayerUtil.isOcean(p_202726_2_) && p_202726_2_ != MUSHROOM_FIELDS) {
            switch(p_202726_2_) {
            case 1:
               if (i > 0) {
                  return p_202726_1_.random(3) == 0 ? BADLANDS_PLATEAU : WOODED_BADLANDS_PLATEAU;
               }

               return this.warmBiomes[p_202726_1_.random(this.warmBiomes.length)];
            case 2:
               if (i > 0) {
                  return JUNGLE;
               }

               return field_202745_s[p_202726_1_.random(field_202745_s.length)];
            case 3:
               if (i > 0) {
                  return GIANT_TREE_TAIGA;
               }

               return field_202746_t[p_202726_1_.random(field_202746_t.length)];
            case 4:
               return field_202747_u[p_202726_1_.random(field_202747_u.length)];
            default:
               return MUSHROOM_FIELDS;
            }
         } else {
            return p_202726_2_;
         }
      }
   }
}
