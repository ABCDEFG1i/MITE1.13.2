package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.BitArray;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.datafix.TypeReferences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkPaletteFormat extends DataFix {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final BitSet field_199146_b = new BitSet(256);
   private static final BitSet field_199147_c = new BitSet(256);
   private static final Dynamic<?> field_199148_d = BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:pumpkin'}");
   private static final Dynamic<?> field_199149_e = BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:podzol',Properties:{snowy:'true'}}");
   private static final Dynamic<?> field_199150_f = BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:grass_block',Properties:{snowy:'true'}}");
   private static final Dynamic<?> field_199151_g = BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:mycelium',Properties:{snowy:'true'}}");
   private static final Dynamic<?> field_199152_h = BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:sunflower',Properties:{half:'upper'}}");
   private static final Dynamic<?> field_199153_i = BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:lilac',Properties:{half:'upper'}}");
   private static final Dynamic<?> field_199154_j = BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:tall_grass',Properties:{half:'upper'}}");
   private static final Dynamic<?> field_199155_k = BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:large_fern',Properties:{half:'upper'}}");
   private static final Dynamic<?> field_199156_l = BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:rose_bush',Properties:{half:'upper'}}");
   private static final Dynamic<?> field_199157_m = BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:peony',Properties:{half:'upper'}}");
   private static final Map<String, Dynamic<?>> field_199158_n = DataFixUtils.make(Maps.newHashMap(), (p_209306_0_) -> {
      p_209306_0_.put("minecraft:air0", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:flower_pot'}"));
      p_209306_0_.put("minecraft:red_flower0", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_poppy'}"));
      p_209306_0_.put("minecraft:red_flower1", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_blue_orchid'}"));
      p_209306_0_.put("minecraft:red_flower2", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_allium'}"));
      p_209306_0_.put("minecraft:red_flower3", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_azure_bluet'}"));
      p_209306_0_.put("minecraft:red_flower4", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_red_tulip'}"));
      p_209306_0_.put("minecraft:red_flower5", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_orange_tulip'}"));
      p_209306_0_.put("minecraft:red_flower6", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_white_tulip'}"));
      p_209306_0_.put("minecraft:red_flower7", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_pink_tulip'}"));
      p_209306_0_.put("minecraft:red_flower8", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_oxeye_daisy'}"));
      p_209306_0_.put("minecraft:yellow_flower0", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_dandelion'}"));
      p_209306_0_.put("minecraft:sapling0", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_oak_sapling'}"));
      p_209306_0_.put("minecraft:sapling1", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_spruce_sapling'}"));
      p_209306_0_.put("minecraft:sapling2", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_birch_sapling'}"));
      p_209306_0_.put("minecraft:sapling3", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_jungle_sapling'}"));
      p_209306_0_.put("minecraft:sapling4", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_acacia_sapling'}"));
      p_209306_0_.put("minecraft:sapling5", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_dark_oak_sapling'}"));
      p_209306_0_.put("minecraft:red_mushroom0", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_red_mushroom'}"));
      p_209306_0_.put("minecraft:brown_mushroom0", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_brown_mushroom'}"));
      p_209306_0_.put("minecraft:deadbush0", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_dead_bush'}"));
      p_209306_0_.put("minecraft:tallgrass2", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_fern'}"));
      p_209306_0_.put("minecraft:cactus0", BlockStateFlatteningMap.getFixedNBTForID(2240));
   });
   private static final Map<String, Dynamic<?>> field_199159_o = DataFixUtils.make(Maps.newHashMap(), (p_209308_0_) -> {
      func_209300_a(p_209308_0_, 0, "skeleton", "skull");
      func_209300_a(p_209308_0_, 1, "wither_skeleton", "skull");
      func_209300_a(p_209308_0_, 2, "zombie", "head");
      func_209300_a(p_209308_0_, 3, "player", "head");
      func_209300_a(p_209308_0_, 4, "creeper", "head");
      func_209300_a(p_209308_0_, 5, "dragon", "head");
   });
   private static final Map<String, Dynamic<?>> field_199160_p = DataFixUtils.make(Maps.newHashMap(), (p_209298_0_) -> {
      func_209301_a(p_209298_0_, "oak_door", 1024);
      func_209301_a(p_209298_0_, "iron_door", 1136);
      func_209301_a(p_209298_0_, "spruce_door", 3088);
      func_209301_a(p_209298_0_, "birch_door", 3104);
      func_209301_a(p_209298_0_, "jungle_door", 3120);
      func_209301_a(p_209298_0_, "acacia_door", 3136);
      func_209301_a(p_209298_0_, "dark_oak_door", 3152);
   });
   private static final Map<String, Dynamic<?>> field_199161_q = DataFixUtils.make(Maps.newHashMap(), (p_209302_0_) -> {
      for(int i = 0; i < 26; ++i) {
         p_209302_0_.put("true" + i, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:note_block',Properties:{powered:'true',note:'" + i + "'}}"));
         p_209302_0_.put("false" + i, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:note_block',Properties:{powered:'false',note:'" + i + "'}}"));
      }

   });
   private static final Int2ObjectMap<String> field_199162_r = DataFixUtils.make(new Int2ObjectOpenHashMap<>(), (p_209296_0_) -> {
      p_209296_0_.put(0, "white");
      p_209296_0_.put(1, "orange");
      p_209296_0_.put(2, "magenta");
      p_209296_0_.put(3, "light_blue");
      p_209296_0_.put(4, "yellow");
      p_209296_0_.put(5, "lime");
      p_209296_0_.put(6, "pink");
      p_209296_0_.put(7, "gray");
      p_209296_0_.put(8, "light_gray");
      p_209296_0_.put(9, "cyan");
      p_209296_0_.put(10, "purple");
      p_209296_0_.put(11, "blue");
      p_209296_0_.put(12, "brown");
      p_209296_0_.put(13, "green");
      p_209296_0_.put(14, "red");
      p_209296_0_.put(15, "black");
   });
   private static final Map<String, Dynamic<?>> field_199163_s = DataFixUtils.make(Maps.newHashMap(), (p_209304_0_) -> {
      for(Entry<String> entry : field_199162_r.int2ObjectEntrySet()) {
         if (!Objects.equals(entry.getValue(), "red")) {
            func_209307_a(p_209304_0_, entry.getIntKey(), entry.getValue());
         }
      }

   });
   private static final Map<String, Dynamic<?>> field_199164_t = DataFixUtils.make(Maps.newHashMap(), (p_209299_0_) -> {
      for(Entry<String> entry : field_199162_r.int2ObjectEntrySet()) {
         if (!Objects.equals(entry.getValue(), "white")) {
            func_209297_b(p_209299_0_, 15 - entry.getIntKey(), entry.getValue());
         }
      }

   });
   private static final Dynamic<?> field_199165_u = BlockStateFlatteningMap.getFixedNBTForID(0);

   public ChunkPaletteFormat(Schema p_i49676_1_, boolean p_i49676_2_) {
      super(p_i49676_1_, p_i49676_2_);
   }

   private static void func_209300_a(Map<String, Dynamic<?>> p_209300_0_, int p_209300_1_, String p_209300_2_, String p_209300_3_) {
      p_209300_0_.put(p_209300_1_ + "north", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209300_2_ + "_wall_" + p_209300_3_ + "',Properties:{facing:'north'}}"));
      p_209300_0_.put(p_209300_1_ + "east", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209300_2_ + "_wall_" + p_209300_3_ + "',Properties:{facing:'east'}}"));
      p_209300_0_.put(p_209300_1_ + "south", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209300_2_ + "_wall_" + p_209300_3_ + "',Properties:{facing:'south'}}"));
      p_209300_0_.put(p_209300_1_ + "west", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209300_2_ + "_wall_" + p_209300_3_ + "',Properties:{facing:'west'}}"));

      for(int i = 0; i < 16; ++i) {
         p_209300_0_.put(p_209300_1_ + "" + i, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209300_2_ + "_" + p_209300_3_ + "',Properties:{rotation:'" + i + "'}}"));
      }

   }

   private static void func_209301_a(Map<String, Dynamic<?>> p_209301_0_, String p_209301_1_, int p_209301_2_) {
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastlowerleftfalsefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastlowerleftfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastlowerlefttruefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastlowerlefttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastlowerrightfalsefalse", BlockStateFlatteningMap.getFixedNBTForID(p_209301_2_));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastlowerrightfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastlowerrighttruefalse", BlockStateFlatteningMap.getFixedNBTForID(p_209301_2_ + 4));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastlowerrighttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastupperleftfalsefalse", BlockStateFlatteningMap.getFixedNBTForID(p_209301_2_ + 8));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastupperleftfalsetrue", BlockStateFlatteningMap.getFixedNBTForID(p_209301_2_ + 10));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastupperlefttruefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastupperlefttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastupperrightfalsefalse", BlockStateFlatteningMap.getFixedNBTForID(p_209301_2_ + 9));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastupperrightfalsetrue", BlockStateFlatteningMap.getFixedNBTForID(p_209301_2_ + 11));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastupperrighttruefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastupperrighttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northlowerleftfalsefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northlowerleftfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northlowerlefttruefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northlowerlefttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northlowerrightfalsefalse", BlockStateFlatteningMap.getFixedNBTForID(p_209301_2_ + 3));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northlowerrightfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northlowerrighttruefalse", BlockStateFlatteningMap.getFixedNBTForID(p_209301_2_ + 7));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northlowerrighttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northupperleftfalsefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northupperleftfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northupperlefttruefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northupperlefttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northupperrightfalsefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northupperrightfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northupperrighttruefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northupperrighttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southlowerleftfalsefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southlowerleftfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southlowerlefttruefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southlowerlefttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southlowerrightfalsefalse", BlockStateFlatteningMap.getFixedNBTForID(p_209301_2_ + 1));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southlowerrightfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southlowerrighttruefalse", BlockStateFlatteningMap.getFixedNBTForID(p_209301_2_ + 5));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southlowerrighttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southupperleftfalsefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southupperleftfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southupperlefttruefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southupperlefttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southupperrightfalsefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southupperrightfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southupperrighttruefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southupperrighttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westlowerleftfalsefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westlowerleftfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westlowerlefttruefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westlowerlefttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westlowerrightfalsefalse", BlockStateFlatteningMap.getFixedNBTForID(p_209301_2_ + 2));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westlowerrightfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westlowerrighttruefalse", BlockStateFlatteningMap.getFixedNBTForID(p_209301_2_ + 6));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westlowerrighttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westupperleftfalsefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westupperleftfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westupperlefttruefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westupperlefttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westupperrightfalsefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westupperrightfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westupperrighttruefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westupperrighttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
   }

   private static void func_209307_a(Map<String, Dynamic<?>> p_209307_0_, int p_209307_1_, String p_209307_2_) {
      p_209307_0_.put("southfalsefoot" + p_209307_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209307_2_ + "_bed',Properties:{facing:'south',occupied:'false',part:'foot'}}"));
      p_209307_0_.put("westfalsefoot" + p_209307_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209307_2_ + "_bed',Properties:{facing:'west',occupied:'false',part:'foot'}}"));
      p_209307_0_.put("northfalsefoot" + p_209307_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209307_2_ + "_bed',Properties:{facing:'north',occupied:'false',part:'foot'}}"));
      p_209307_0_.put("eastfalsefoot" + p_209307_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209307_2_ + "_bed',Properties:{facing:'east',occupied:'false',part:'foot'}}"));
      p_209307_0_.put("southfalsehead" + p_209307_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209307_2_ + "_bed',Properties:{facing:'south',occupied:'false',part:'head'}}"));
      p_209307_0_.put("westfalsehead" + p_209307_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209307_2_ + "_bed',Properties:{facing:'west',occupied:'false',part:'head'}}"));
      p_209307_0_.put("northfalsehead" + p_209307_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209307_2_ + "_bed',Properties:{facing:'north',occupied:'false',part:'head'}}"));
      p_209307_0_.put("eastfalsehead" + p_209307_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209307_2_ + "_bed',Properties:{facing:'east',occupied:'false',part:'head'}}"));
      p_209307_0_.put("southtruehead" + p_209307_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209307_2_ + "_bed',Properties:{facing:'south',occupied:'true',part:'head'}}"));
      p_209307_0_.put("westtruehead" + p_209307_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209307_2_ + "_bed',Properties:{facing:'west',occupied:'true',part:'head'}}"));
      p_209307_0_.put("northtruehead" + p_209307_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209307_2_ + "_bed',Properties:{facing:'north',occupied:'true',part:'head'}}"));
      p_209307_0_.put("easttruehead" + p_209307_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209307_2_ + "_bed',Properties:{facing:'east',occupied:'true',part:'head'}}"));
   }

   private static void func_209297_b(Map<String, Dynamic<?>> p_209297_0_, int p_209297_1_, String p_209297_2_) {
      for(int i = 0; i < 16; ++i) {
         p_209297_0_.put("" + i + "_" + p_209297_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209297_2_ + "_banner',Properties:{rotation:'" + i + "'}}"));
      }

      p_209297_0_.put("north_" + p_209297_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209297_2_ + "_wall_banner',Properties:{facing:'north'}}"));
      p_209297_0_.put("south_" + p_209297_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209297_2_ + "_wall_banner',Properties:{facing:'south'}}"));
      p_209297_0_.put("west_" + p_209297_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209297_2_ + "_wall_banner',Properties:{facing:'west'}}"));
      p_209297_0_.put("east_" + p_209297_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209297_2_ + "_wall_banner',Properties:{facing:'east'}}"));
   }

   public static String func_209726_a(Dynamic<?> p_209726_0_) {
      return p_209726_0_.getString("Name");
   }

   public static String func_209719_a(Dynamic<?> p_209719_0_, String p_209719_1_) {
      return p_209719_0_.get("Properties").map((p_209710_1_) -> {
         return p_209710_1_.getString(p_209719_1_);
      }).orElse("");
   }

   public static int func_209724_a(IntIdentityHashBiMap<Dynamic<?>> p_209724_0_, Dynamic<?> p_209724_1_) {
      int i = p_209724_0_.getId(p_209724_1_);
      if (i == -1) {
         i = p_209724_0_.add(p_209724_1_);
      }

      return i;
   }

   private Dynamic<?> func_209712_b(Dynamic<?> p_209712_1_) {
      Optional<? extends Dynamic<?>> optional = p_209712_1_.get("Level");
      return optional.isPresent() && optional.get().get("Sections").flatMap(Dynamic::getStream).isPresent() ? p_209712_1_.set("Level", (new ChunkPaletteFormat.UpgradeChunk(optional.get())).func_210058_a()) : p_209712_1_;
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
      Type<?> type1 = this.getOutputSchema().getType(TypeReferences.CHUNK);
      return this.writeFixAndRead("ChunkPalettedStorageFix", type, type1, this::func_209712_b);
   }

   public static int func_210957_a(boolean p_210957_0_, boolean p_210957_1_, boolean p_210957_2_, boolean p_210957_3_) {
      int i = 0;
      if (p_210957_2_) {
         if (p_210957_1_) {
            i |= 2;
         } else if (p_210957_0_) {
            i |= 128;
         } else {
            i |= 1;
         }
      } else if (p_210957_3_) {
         if (p_210957_0_) {
            i |= 32;
         } else if (p_210957_1_) {
            i |= 8;
         } else {
            i |= 16;
         }
      } else if (p_210957_1_) {
         i |= 4;
      } else if (p_210957_0_) {
         i |= 64;
      }

      return i;
   }

   static {
      field_199147_c.set(2);
      field_199147_c.set(3);
      field_199147_c.set(110);
      field_199147_c.set(140);
      field_199147_c.set(144);
      field_199147_c.set(25);
      field_199147_c.set(86);
      field_199147_c.set(26);
      field_199147_c.set(176);
      field_199147_c.set(177);
      field_199147_c.set(175);
      field_199147_c.set(64);
      field_199147_c.set(71);
      field_199147_c.set(193);
      field_199147_c.set(194);
      field_199147_c.set(195);
      field_199147_c.set(196);
      field_199147_c.set(197);
      field_199146_b.set(54);
      field_199146_b.set(146);
      field_199146_b.set(25);
      field_199146_b.set(26);
      field_199146_b.set(51);
      field_199146_b.set(53);
      field_199146_b.set(67);
      field_199146_b.set(108);
      field_199146_b.set(109);
      field_199146_b.set(114);
      field_199146_b.set(128);
      field_199146_b.set(134);
      field_199146_b.set(135);
      field_199146_b.set(136);
      field_199146_b.set(156);
      field_199146_b.set(163);
      field_199146_b.set(164);
      field_199146_b.set(180);
      field_199146_b.set(203);
      field_199146_b.set(55);
      field_199146_b.set(85);
      field_199146_b.set(113);
      field_199146_b.set(188);
      field_199146_b.set(189);
      field_199146_b.set(190);
      field_199146_b.set(191);
      field_199146_b.set(192);
      field_199146_b.set(93);
      field_199146_b.set(94);
      field_199146_b.set(101);
      field_199146_b.set(102);
      field_199146_b.set(160);
      field_199146_b.set(106);
      field_199146_b.set(107);
      field_199146_b.set(183);
      field_199146_b.set(184);
      field_199146_b.set(185);
      field_199146_b.set(186);
      field_199146_b.set(187);
      field_199146_b.set(132);
      field_199146_b.set(139);
      field_199146_b.set(199);
   }

   public static enum Direction {
      DOWN(ChunkPaletteFormat.Direction.Offset.NEGATIVE, ChunkPaletteFormat.Direction.Axis.Y),
      UP(ChunkPaletteFormat.Direction.Offset.POSITIVE, ChunkPaletteFormat.Direction.Axis.Y),
      NORTH(ChunkPaletteFormat.Direction.Offset.NEGATIVE, ChunkPaletteFormat.Direction.Axis.Z),
      SOUTH(ChunkPaletteFormat.Direction.Offset.POSITIVE, ChunkPaletteFormat.Direction.Axis.Z),
      WEST(ChunkPaletteFormat.Direction.Offset.NEGATIVE, ChunkPaletteFormat.Direction.Axis.X),
      EAST(ChunkPaletteFormat.Direction.Offset.POSITIVE, ChunkPaletteFormat.Direction.Axis.X);

      private final ChunkPaletteFormat.Direction.Axis field_210941_g;
      private final ChunkPaletteFormat.Direction.Offset field_210942_h;

      private Direction(ChunkPaletteFormat.Direction.Offset p_i49576_3_, ChunkPaletteFormat.Direction.Axis p_i49576_4_) {
         this.field_210941_g = p_i49576_4_;
         this.field_210942_h = p_i49576_3_;
      }

      public ChunkPaletteFormat.Direction.Offset func_210939_a() {
         return this.field_210942_h;
      }

      public ChunkPaletteFormat.Direction.Axis func_210940_b() {
         return this.field_210941_g;
      }

      public static enum Axis {
         X,
         Y,
         Z;
      }

      public static enum Offset {
         POSITIVE(1),
         NEGATIVE(-1);

         private final int field_210938_c;

         private Offset(int p_i49694_3_) {
            this.field_210938_c = p_i49694_3_;
         }

         public int func_210937_a() {
            return this.field_210938_c;
         }
      }
   }

   static class NibbleArray {
      private final byte[] field_210935_a;

      public NibbleArray() {
         this.field_210935_a = new byte[2048];
      }

      public NibbleArray(byte[] p_i49577_1_) {
         this.field_210935_a = p_i49577_1_;
         if (p_i49577_1_.length != 2048) {
            throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + p_i49577_1_.length);
         }
      }

      public int func_210932_a(int p_210932_1_, int p_210932_2_, int p_210932_3_) {
         int i = this.func_210934_b(p_210932_2_ << 8 | p_210932_3_ << 4 | p_210932_1_);
         return this.func_210933_a(p_210932_2_ << 8 | p_210932_3_ << 4 | p_210932_1_) ? this.field_210935_a[i] & 15 : this.field_210935_a[i] >> 4 & 15;
      }

      private boolean func_210933_a(int p_210933_1_) {
         return (p_210933_1_ & 1) == 0;
      }

      private int func_210934_b(int p_210934_1_) {
         return p_210934_1_ >> 1;
      }
   }

   static class Section {
      private final IntIdentityHashBiMap<Dynamic<?>> field_199210_a = new IntIdentityHashBiMap<>(32);
      private Dynamic<?> field_199211_b;
      private final Dynamic<?> field_199213_d;
      private final boolean field_199214_e;
      private final Int2ObjectMap<IntList> field_199215_f = new Int2ObjectLinkedOpenHashMap<>();
      private final IntList field_199216_g = new IntArrayList();
      public int field_199212_c;
      private final Set<Dynamic<?>> field_199217_h = Sets.newIdentityHashSet();
      private final int[] field_199218_i = new int[4096];

      public Section(Dynamic<?> p_i49575_1_) {
         this.field_199211_b = p_i49575_1_.emptyList();
         this.field_199213_d = p_i49575_1_;
         this.field_199212_c = p_i49575_1_.getInt("Y");
         this.field_199214_e = p_i49575_1_.get("Blocks").isPresent();
      }

      public Dynamic<?> func_210056_a(int p_210056_1_) {
         if (p_210056_1_ >= 0 && p_210056_1_ <= 4095) {
            Dynamic<?> dynamic = this.field_199210_a.get(this.field_199218_i[p_210056_1_]);
            return dynamic == null ? ChunkPaletteFormat.field_199165_u : dynamic;
         } else {
            return ChunkPaletteFormat.field_199165_u;
         }
      }

      public void func_210053_a(int p_210053_1_, Dynamic<?> p_210053_2_) {
         if (this.field_199217_h.add(p_210053_2_)) {
            this.field_199211_b = this.field_199211_b.merge("%%FILTER_ME%%".equals(ChunkPaletteFormat.func_209726_a(p_210053_2_)) ? ChunkPaletteFormat.field_199165_u : p_210053_2_);
         }

         this.field_199218_i[p_210053_1_] = ChunkPaletteFormat.func_209724_a(this.field_199210_a, p_210053_2_);
      }

      public int func_199207_b(int p_199207_1_) {
         if (!this.field_199214_e) {
            return p_199207_1_;
         } else {
            ByteBuffer bytebuffer = this.field_199213_d.get("Blocks").flatMap(Dynamic::getByteBuffer).get();
            ChunkPaletteFormat.NibbleArray chunkpaletteformat$nibblearray = this.field_199213_d.get("Data").flatMap(Dynamic::getByteBuffer).map((p_210055_0_) -> {
               return new ChunkPaletteFormat.NibbleArray(DataFixUtils.toArray(p_210055_0_));
            }).orElseGet(ChunkPaletteFormat.NibbleArray::new);
            ChunkPaletteFormat.NibbleArray chunkpaletteformat$nibblearray1 = this.field_199213_d.get("Add").flatMap(Dynamic::getByteBuffer).map((p_210052_0_) -> {
               return new ChunkPaletteFormat.NibbleArray(DataFixUtils.toArray(p_210052_0_));
            }).orElseGet(ChunkPaletteFormat.NibbleArray::new);
            this.field_199217_h.add(ChunkPaletteFormat.field_199165_u);
            ChunkPaletteFormat.func_209724_a(this.field_199210_a, ChunkPaletteFormat.field_199165_u);
            this.field_199211_b = this.field_199211_b.merge(ChunkPaletteFormat.field_199165_u);

            for(int i = 0; i < 4096; ++i) {
               int j = i & 15;
               int k = i >> 8 & 15;
               int l = i >> 4 & 15;
               int i1 = chunkpaletteformat$nibblearray1.func_210932_a(j, k, l) << 12 | (bytebuffer.get(i) & 255) << 4 | chunkpaletteformat$nibblearray.func_210932_a(j, k, l);
               if (ChunkPaletteFormat.field_199147_c.get(i1 >> 4)) {
                  this.func_199205_a(i1 >> 4, i);
               }

               if (ChunkPaletteFormat.field_199146_b.get(i1 >> 4)) {
                  int j1 = ChunkPaletteFormat.func_210957_a(j == 0, j == 15, l == 0, l == 15);
                  if (j1 == 0) {
                     this.field_199216_g.add(i);
                  } else {
                     p_199207_1_ |= j1;
                  }
               }

               this.func_210053_a(i, BlockStateFlatteningMap.getFixedNBTForID(i1));
            }

            return p_199207_1_;
         }
      }

      private void func_199205_a(int p_199205_1_, int p_199205_2_) {
         IntList intlist = this.field_199215_f.get(p_199205_1_);
         if (intlist == null) {
            intlist = new IntArrayList();
            this.field_199215_f.put(p_199205_1_, intlist);
         }

         intlist.add(p_199205_2_);
      }

      public Dynamic<?> func_210051_a() {
         Dynamic<?> dynamic = this.field_199213_d;
         if (!this.field_199214_e) {
            return dynamic;
         } else {
            dynamic = dynamic.set("Palette", this.field_199211_b);
            int i = Math.max(4, DataFixUtils.ceillog2(this.field_199217_h.size()));
            BitArray bitarray = new BitArray(i, 4096);

            for(int j = 0; j < this.field_199218_i.length; ++j) {
               bitarray.setAt(j, this.field_199218_i[j]);
            }

            dynamic = dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(bitarray.getBackingLongArray())));
            dynamic = dynamic.remove("Blocks");
            dynamic = dynamic.remove("Data");
            dynamic = dynamic.remove("Add");
            return dynamic;
         }
      }
   }

   static final class UpgradeChunk {
      private int field_199227_a;
      private final ChunkPaletteFormat.Section[] field_199228_b = new ChunkPaletteFormat.Section[16];
      private final Dynamic<?> field_199229_c;
      private final int field_199230_d;
      private final int field_199231_e;
      private final Int2ObjectMap<Dynamic<?>> field_199232_f = new Int2ObjectLinkedOpenHashMap<>(16);

      public UpgradeChunk(Dynamic<?> p_i49574_1_) {
         this.field_199229_c = p_i49574_1_;
         this.field_199230_d = p_i49574_1_.getInt("xPos") << 4;
         this.field_199231_e = p_i49574_1_.getInt("zPos") << 4;
         p_i49574_1_.get("TileEntities").flatMap(Dynamic::getStream).ifPresent((p_210061_1_) -> {
            p_210061_1_.forEach((p_210063_1_) -> {
               int l3 = p_210063_1_.getInt("x") - this.field_199230_d & 15;
               int i4 = p_210063_1_.getInt("y");
               int j4 = p_210063_1_.getInt("z") - this.field_199231_e & 15;
               int k4 = i4 << 8 | j4 << 4 | l3;
               if (this.field_199232_f.put(k4, p_210063_1_) != null) {
                  ChunkPaletteFormat.LOGGER.warn("In chunk: {}x{} found a duplicate block entity at position: [{}, {}, {}]", this.field_199230_d, this.field_199231_e, l3, i4, j4);
               }

            });
         });
         boolean flag = p_i49574_1_.getBoolean("convertedFromAlphaFormat");
         p_i49574_1_.get("Sections").flatMap(Dynamic::getStream).ifPresent((p_210062_1_) -> {
            p_210062_1_.forEach((p_210065_1_) -> {
               ChunkPaletteFormat.Section chunkpaletteformat$section1 = new ChunkPaletteFormat.Section(p_210065_1_);
               this.field_199227_a = chunkpaletteformat$section1.func_199207_b(this.field_199227_a);
               this.field_199228_b[chunkpaletteformat$section1.field_199212_c] = chunkpaletteformat$section1;
            });
         });

         for(ChunkPaletteFormat.Section chunkpaletteformat$section : this.field_199228_b) {
            if (chunkpaletteformat$section != null) {
               label258:
               for(java.util.Map.Entry<Integer, IntList> entry : chunkpaletteformat$section.field_199215_f.entrySet()) {
                  int i = chunkpaletteformat$section.field_199212_c << 12;
                  switch(entry.getKey()) {
                  case 2:
                     IntListIterator intlistiterator9 = entry.getValue().iterator();

                     while(true) {
                        if (!intlistiterator9.hasNext()) {
                           continue label258;
                        }

                        int i3 = intlistiterator9.next();
                        i3 = i3 | i;
                        Dynamic<?> dynamic11 = this.func_210064_a(i3);
                        if ("minecraft:grass_block".equals(ChunkPaletteFormat.func_209726_a(dynamic11))) {
                           String s12 = ChunkPaletteFormat.func_209726_a(this.func_210064_a(func_199223_a(i3, ChunkPaletteFormat.Direction.UP)));
                           if ("minecraft:snow".equals(s12) || "minecraft:snow_layer".equals(s12)) {
                              this.func_210060_a(i3, ChunkPaletteFormat.field_199150_f);
                           }
                        }
                     }
                  case 3:
                     IntListIterator intlistiterator8 = entry.getValue().iterator();

                     while(true) {
                        if (!intlistiterator8.hasNext()) {
                           continue label258;
                        }

                        int l2 = intlistiterator8.next();
                        l2 = l2 | i;
                        Dynamic<?> dynamic10 = this.func_210064_a(l2);
                        if ("minecraft:podzol".equals(ChunkPaletteFormat.func_209726_a(dynamic10))) {
                           String s11 = ChunkPaletteFormat.func_209726_a(this.func_210064_a(func_199223_a(l2, ChunkPaletteFormat.Direction.UP)));
                           if ("minecraft:snow".equals(s11) || "minecraft:snow_layer".equals(s11)) {
                              this.func_210060_a(l2, ChunkPaletteFormat.field_199149_e);
                           }
                        }
                     }
                  case 25:
                     IntListIterator intlistiterator7 = entry.getValue().iterator();

                     while(true) {
                        if (!intlistiterator7.hasNext()) {
                           continue label258;
                        }

                        int k2 = intlistiterator7.next();
                        k2 = k2 | i;
                        Dynamic<?> dynamic9 = this.func_210059_c(k2);
                        if (dynamic9 != null) {
                           String s10 = Boolean.toString(dynamic9.getBoolean("powered")) + (byte)Math.min(Math.max(dynamic9.getByte("note"), 0), 24);
                           this.func_210060_a(k2, ChunkPaletteFormat.field_199161_q.getOrDefault(s10, ChunkPaletteFormat.field_199161_q.get("false0")));
                        }
                     }
                  case 26:
                     IntListIterator intlistiterator6 = entry.getValue().iterator();

                     while(true) {
                        if (!intlistiterator6.hasNext()) {
                           continue label258;
                        }

                        int j2 = intlistiterator6.next();
                        j2 = j2 | i;
                        Dynamic<?> dynamic8 = this.func_210066_b(j2);
                        Dynamic<?> dynamic14 = this.func_210064_a(j2);
                        if (dynamic8 != null) {
                           int k3 = dynamic8.getInt("color");
                           if (k3 != 14 && k3 >= 0 && k3 < 16) {
                              String s16 = ChunkPaletteFormat.func_209719_a(dynamic14, "facing") + ChunkPaletteFormat.func_209719_a(dynamic14, "occupied") + ChunkPaletteFormat.func_209719_a(dynamic14, "part") + k3;
                              if (ChunkPaletteFormat.field_199163_s.containsKey(s16)) {
                                 this.func_210060_a(j2, ChunkPaletteFormat.field_199163_s.get(s16));
                              }
                           }
                        }
                     }
                  case 64:
                  case 71:
                  case 193:
                  case 194:
                  case 195:
                  case 196:
                  case 197:
                     IntListIterator intlistiterator5 = entry.getValue().iterator();

                     while(true) {
                        if (!intlistiterator5.hasNext()) {
                           continue label258;
                        }

                        int i2 = intlistiterator5.next();
                        i2 = i2 | i;
                        Dynamic<?> dynamic7 = this.func_210064_a(i2);
                        if (ChunkPaletteFormat.func_209726_a(dynamic7).endsWith("_door")) {
                           Dynamic<?> dynamic13 = this.func_210064_a(i2);
                           if ("lower".equals(ChunkPaletteFormat.func_209719_a(dynamic13, "half"))) {
                              int j3 = func_199223_a(i2, ChunkPaletteFormat.Direction.UP);
                              Dynamic<?> dynamic15 = this.func_210064_a(j3);
                              String s1 = ChunkPaletteFormat.func_209726_a(dynamic13);
                              if (s1.equals(ChunkPaletteFormat.func_209726_a(dynamic15))) {
                                 String s2 = ChunkPaletteFormat.func_209719_a(dynamic13, "facing");
                                 String s3 = ChunkPaletteFormat.func_209719_a(dynamic13, "open");
                                 String s4 = flag ? "left" : ChunkPaletteFormat.func_209719_a(dynamic15, "hinge");
                                 String s5 = flag ? "false" : ChunkPaletteFormat.func_209719_a(dynamic15, "powered");
                                 this.func_210060_a(i2, ChunkPaletteFormat.field_199160_p.get(s1 + s2 + "lower" + s4 + s3 + s5));
                                 this.func_210060_a(j3, ChunkPaletteFormat.field_199160_p.get(s1 + s2 + "upper" + s4 + s3 + s5));
                              }
                           }
                        }
                     }
                  case 86:
                     IntListIterator intlistiterator4 = entry.getValue().iterator();

                     while(true) {
                        if (!intlistiterator4.hasNext()) {
                           continue label258;
                        }

                        int l1 = intlistiterator4.next();
                        l1 = l1 | i;
                        Dynamic<?> dynamic6 = this.func_210064_a(l1);
                        if ("minecraft:carved_pumpkin".equals(ChunkPaletteFormat.func_209726_a(dynamic6))) {
                           String s9 = ChunkPaletteFormat.func_209726_a(this.func_210064_a(func_199223_a(l1, ChunkPaletteFormat.Direction.DOWN)));
                           if ("minecraft:grass_block".equals(s9) || "minecraft:dirt".equals(s9)) {
                              this.func_210060_a(l1, ChunkPaletteFormat.field_199148_d);
                           }
                        }
                     }
                  case 110:
                     IntListIterator intlistiterator3 = entry.getValue().iterator();

                     while(true) {
                        if (!intlistiterator3.hasNext()) {
                           continue label258;
                        }

                        int k1 = intlistiterator3.next();
                        k1 = k1 | i;
                        Dynamic<?> dynamic5 = this.func_210064_a(k1);
                        if ("minecraft:mycelium".equals(ChunkPaletteFormat.func_209726_a(dynamic5))) {
                           String s8 = ChunkPaletteFormat.func_209726_a(this.func_210064_a(func_199223_a(k1, ChunkPaletteFormat.Direction.UP)));
                           if ("minecraft:snow".equals(s8) || "minecraft:snow_layer".equals(s8)) {
                              this.func_210060_a(k1, ChunkPaletteFormat.field_199151_g);
                           }
                        }
                     }
                  case 140:
                     IntListIterator intlistiterator2 = entry.getValue().iterator();

                     while(true) {
                        if (!intlistiterator2.hasNext()) {
                           continue label258;
                        }

                        int j1 = intlistiterator2.next();
                        j1 = j1 | i;
                        Dynamic<?> dynamic4 = this.func_210059_c(j1);
                        if (dynamic4 != null) {
                           String s7 = dynamic4.getString("Item") + dynamic4.getInt("Data");
                           this.func_210060_a(j1, ChunkPaletteFormat.field_199158_n.getOrDefault(s7, ChunkPaletteFormat.field_199158_n.get("minecraft:air0")));
                        }
                     }
                  case 144:
                     IntListIterator intlistiterator1 = entry.getValue().iterator();

                     while(true) {
                        if (!intlistiterator1.hasNext()) {
                           continue label258;
                        }

                        int i1 = intlistiterator1.next();
                        i1 = i1 | i;
                        Dynamic<?> dynamic3 = this.func_210066_b(i1);
                        if (dynamic3 != null) {
                           String s6 = String.valueOf((int)dynamic3.getByte("SkullType"));
                           String s14 = ChunkPaletteFormat.func_209719_a(this.func_210064_a(i1), "facing");
                           String s15;
                           if (!"up".equals(s14) && !"down".equals(s14)) {
                              s15 = s6 + s14;
                           } else {
                              s15 = s6 + String.valueOf(dynamic3.getInt("Rot"));
                           }

                           dynamic3.remove("SkullType");
                           dynamic3.remove("facing");
                           dynamic3.remove("Rot");
                           this.func_210060_a(i1, ChunkPaletteFormat.field_199159_o.getOrDefault(s15, ChunkPaletteFormat.field_199159_o.get("0north")));
                        }
                     }
                  case 175:
                     IntListIterator intlistiterator = entry.getValue().iterator();

                     while(true) {
                        if (!intlistiterator.hasNext()) {
                           continue label258;
                        }

                        int l = intlistiterator.next();
                        l = l | i;
                        Dynamic<?> dynamic2 = this.func_210064_a(l);
                        if ("upper".equals(ChunkPaletteFormat.func_209719_a(dynamic2, "half"))) {
                           Dynamic<?> dynamic12 = this.func_210064_a(func_199223_a(l, ChunkPaletteFormat.Direction.DOWN));
                           String s13 = ChunkPaletteFormat.func_209726_a(dynamic12);
                           if ("minecraft:sunflower".equals(s13)) {
                              this.func_210060_a(l, ChunkPaletteFormat.field_199152_h);
                           } else if ("minecraft:lilac".equals(s13)) {
                              this.func_210060_a(l, ChunkPaletteFormat.field_199153_i);
                           } else if ("minecraft:tall_grass".equals(s13)) {
                              this.func_210060_a(l, ChunkPaletteFormat.field_199154_j);
                           } else if ("minecraft:large_fern".equals(s13)) {
                              this.func_210060_a(l, ChunkPaletteFormat.field_199155_k);
                           } else if ("minecraft:rose_bush".equals(s13)) {
                              this.func_210060_a(l, ChunkPaletteFormat.field_199156_l);
                           } else if ("minecraft:peony".equals(s13)) {
                              this.func_210060_a(l, ChunkPaletteFormat.field_199157_m);
                           }
                        }
                     }
                  case 176:
                  case 177:
                     for(int j : entry.getValue()) {
                        j = j | i;
                        Dynamic<?> dynamic = this.func_210066_b(j);
                        Dynamic<?> dynamic1 = this.func_210064_a(j);
                        if (dynamic != null) {
                           int k = dynamic.getInt("Base");
                           if (k != 15 && k >= 0 && k < 16) {
                              String s = ChunkPaletteFormat.func_209719_a(dynamic1, entry.getKey() == 176 ? "rotation" : "facing") + "_" + k;
                              if (ChunkPaletteFormat.field_199164_t.containsKey(s)) {
                                 this.func_210060_a(j, ChunkPaletteFormat.field_199164_t.get(s));
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

      }

      @Nullable
      private Dynamic<?> func_210066_b(int p_210066_1_) {
         return this.field_199232_f.get(p_210066_1_);
      }

      @Nullable
      private Dynamic<?> func_210059_c(int p_210059_1_) {
         return this.field_199232_f.remove(p_210059_1_);
      }

      public static int func_199223_a(int p_199223_0_, ChunkPaletteFormat.Direction p_199223_1_) {
         switch(p_199223_1_.func_210940_b()) {
         case X:
            int i = (p_199223_0_ & 15) + p_199223_1_.func_210939_a().func_210937_a();
            return i >= 0 && i <= 15 ? p_199223_0_ & -16 | i : -1;
         case Y:
            int j = (p_199223_0_ >> 8) + p_199223_1_.func_210939_a().func_210937_a();
            return j >= 0 && j <= 255 ? p_199223_0_ & 255 | j << 8 : -1;
         case Z:
            int k = (p_199223_0_ >> 4 & 15) + p_199223_1_.func_210939_a().func_210937_a();
            return k >= 0 && k <= 15 ? p_199223_0_ & -241 | k << 4 : -1;
         default:
            return -1;
         }
      }

      private void func_210060_a(int p_210060_1_, Dynamic<?> p_210060_2_) {
         if (p_210060_1_ >= 0 && p_210060_1_ <= 65535) {
            ChunkPaletteFormat.Section chunkpaletteformat$section = this.func_199221_d(p_210060_1_);
            if (chunkpaletteformat$section != null) {
               chunkpaletteformat$section.func_210053_a(p_210060_1_ & 4095, p_210060_2_);
            }
         }
      }

      @Nullable
      private ChunkPaletteFormat.Section func_199221_d(int p_199221_1_) {
         int i = p_199221_1_ >> 12;
         return i < this.field_199228_b.length ? this.field_199228_b[i] : null;
      }

      public Dynamic<?> func_210064_a(int p_210064_1_) {
         if (p_210064_1_ >= 0 && p_210064_1_ <= 65535) {
            ChunkPaletteFormat.Section chunkpaletteformat$section = this.func_199221_d(p_210064_1_);
            return chunkpaletteformat$section == null ? ChunkPaletteFormat.field_199165_u : chunkpaletteformat$section.func_210056_a(p_210064_1_ & 4095);
         } else {
            return ChunkPaletteFormat.field_199165_u;
         }
      }

      public Dynamic<?> func_210058_a() {
         Dynamic<?> dynamic = this.field_199229_c;
         if (this.field_199232_f.isEmpty()) {
            dynamic = dynamic.remove("TileEntities");
         } else {
            dynamic = dynamic.set("TileEntities", dynamic.createList(this.field_199232_f.values().stream()));
         }

         Dynamic<?> dynamic1 = dynamic.emptyMap();
         Dynamic<?> dynamic2 = dynamic.emptyList();

         for(ChunkPaletteFormat.Section chunkpaletteformat$section : this.field_199228_b) {
            if (chunkpaletteformat$section != null) {
               dynamic2 = dynamic2.merge(chunkpaletteformat$section.func_210051_a());
               dynamic1 = dynamic1.set(String.valueOf(chunkpaletteformat$section.field_199212_c), dynamic1.createIntList(Arrays.stream(chunkpaletteformat$section.field_199216_g.toIntArray())));
            }
         }

         Dynamic<?> dynamic3 = dynamic.emptyMap();
         dynamic3 = dynamic3.set("Sides", dynamic3.createByte((byte)this.field_199227_a));
         dynamic3 = dynamic3.set("Indices", dynamic1);
         return dynamic.set("UpgradeData", dynamic3).set("Sections", dynamic2);
      }
   }
}
