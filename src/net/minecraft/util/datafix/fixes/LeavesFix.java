package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.BitArray;
import net.minecraft.util.datafix.TypeReferences;

public class LeavesFix extends DataFix {
   private static final int[][] field_208425_a = new int[][]{{-1, 0, 0}, {1, 0, 0}, {0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}};
   private static final Object2IntMap<String> field_208434_j = DataFixUtils.make(new Object2IntOpenHashMap<>(), (p_208417_0_) -> {
      p_208417_0_.put("minecraft:acacia_leaves", 0);
      p_208417_0_.put("minecraft:birch_leaves", 1);
      p_208417_0_.put("minecraft:dark_oak_leaves", 2);
      p_208417_0_.put("minecraft:jungle_leaves", 3);
      p_208417_0_.put("minecraft:oak_leaves", 4);
      p_208417_0_.put("minecraft:spruce_leaves", 5);
   });
   private static final Set<String> field_208435_k = ImmutableSet.of("minecraft:acacia_bark", "minecraft:birch_bark", "minecraft:dark_oak_bark", "minecraft:jungle_bark", "minecraft:oak_bark", "minecraft:spruce_bark", "minecraft:acacia_log", "minecraft:birch_log", "minecraft:dark_oak_log", "minecraft:jungle_log", "minecraft:oak_log", "minecraft:spruce_log", "minecraft:stripped_acacia_log", "minecraft:stripped_birch_log", "minecraft:stripped_dark_oak_log", "minecraft:stripped_jungle_log", "minecraft:stripped_oak_log", "minecraft:stripped_spruce_log");

   public LeavesFix(Schema p_i49629_1_, boolean p_i49629_2_) {
      super(p_i49629_1_, p_i49629_2_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
      OpticFinder<?> opticfinder = type.findField("Level");
      OpticFinder<?> opticfinder1 = opticfinder.type().findField("Sections");
      Type<?> type1 = opticfinder1.type();
      if (!(type1 instanceof ListType)) {
         throw new IllegalStateException("Expecting sections to be a list.");
      } else {
         Type<?> type2 = ((ListType)type1).getElement();
         OpticFinder<?> opticfinder2 = DSL.typeFinder(type2);
         return this.fixTypeEverywhereTyped("Leaves fix", type, (p_208422_4_) -> {
            return p_208422_4_.updateTyped(opticfinder, (p_208420_3_) -> {
               int[] aint = new int[]{0};
               Typed<?> typed = p_208420_3_.updateTyped(opticfinder1, (p_208415_3_) -> {
                  Int2ObjectMap<LeavesFix.LeavesSection> int2objectmap = new Int2ObjectOpenHashMap<>(p_208415_3_.getAllTyped(opticfinder2).stream().map((p_212527_1_) -> {
                     return new LeavesFix.LeavesSection(p_212527_1_, this.getInputSchema());
                  }).collect(Collectors.toMap(LeavesFix.Section::func_208456_b, (p_208410_0_) -> {
                     return p_208410_0_;
                  })));
                  if (int2objectmap.values().stream().allMatch(LeavesFix.Section::func_208461_a)) {
                     return p_208415_3_;
                  } else {
                     List<IntSet> list = Lists.newArrayList();

                     for(int i = 0; i < 7; ++i) {
                        list.add(new IntOpenHashSet());
                     }

                     for(LeavesFix.LeavesSection leavesfix$leavessection : int2objectmap.values()) {
                        if (!leavesfix$leavessection.func_208461_a()) {
                           for(int j = 0; j < 4096; ++j) {
                              int k = leavesfix$leavessection.func_208453_a(j);
                              if (leavesfix$leavessection.func_208457_b(k)) {
                                 list.get(0).add(leavesfix$leavessection.func_208456_b() << 12 | j);
                              } else if (leavesfix$leavessection.func_208460_c(k)) {
                                 int l = this.func_208412_a(j);
                                 int i1 = this.func_208409_c(j);
                                 aint[0] |= func_210537_a(l == 0, l == 15, i1 == 0, i1 == 15);
                              }
                           }
                        }
                     }

                     for(int j3 = 1; j3 < 7; ++j3) {
                        IntSet intset = list.get(j3 - 1);
                        IntSet intset1 = list.get(j3);
                        IntIterator intiterator = intset.iterator();

                        while(intiterator.hasNext()) {
                           int k3 = intiterator.nextInt();
                           int l3 = this.func_208412_a(k3);
                           int j1 = this.func_208421_b(k3);
                           int k1 = this.func_208409_c(k3);

                           for(int[] aint1 : field_208425_a) {
                              int l1 = l3 + aint1[0];
                              int i2 = j1 + aint1[1];
                              int j2 = k1 + aint1[2];
                              if (l1 >= 0 && l1 <= 15 && j2 >= 0 && j2 <= 15 && i2 >= 0 && i2 <= 255) {
                                 LeavesFix.LeavesSection leavesfix$leavessection1 = int2objectmap.get(i2 >> 4);
                                 if (leavesfix$leavessection1 != null && !leavesfix$leavessection1.func_208461_a()) {
                                    int k2 = func_208411_a(l1, i2 & 15, j2);
                                    int l2 = leavesfix$leavessection1.func_208453_a(k2);
                                    if (leavesfix$leavessection1.func_208460_c(l2)) {
                                       int i3 = leavesfix$leavessection1.func_208459_d(l2);
                                       if (i3 > j3) {
                                          leavesfix$leavessection1.func_208454_a(k2, l2, j3);
                                          intset1.add(func_208411_a(l1, i2, j2));
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }

                     return p_208415_3_.updateTyped(opticfinder2, (p_208413_1_) -> {
                        return int2objectmap.get(p_208413_1_.get(DSL.remainderFinder()).getInt("Y")).func_208465_a(p_208413_1_);
                     });
                  }
               });
               if (aint[0] != 0) {
                  typed = typed.update(DSL.remainderFinder(), (p_208419_1_) -> {
                     Dynamic<?> dynamic = DataFixUtils.orElse(p_208419_1_.get("UpgradeData"), p_208419_1_.emptyMap());
                     return p_208419_1_.set("UpgradeData", dynamic.set("Sides", p_208419_1_.createByte((byte)(dynamic.getByte("Sides") | aint[0]))));
                  });
               }

               return typed;
            });
         });
      }
   }

   public static int func_208411_a(int p_208411_0_, int p_208411_1_, int p_208411_2_) {
      return p_208411_1_ << 8 | p_208411_2_ << 4 | p_208411_0_;
   }

   private int func_208412_a(int p_208412_1_) {
      return p_208412_1_ & 15;
   }

   private int func_208421_b(int p_208421_1_) {
      return p_208421_1_ >> 8 & 255;
   }

   private int func_208409_c(int p_208409_1_) {
      return p_208409_1_ >> 4 & 15;
   }

   public static int func_210537_a(boolean p_210537_0_, boolean p_210537_1_, boolean p_210537_2_, boolean p_210537_3_) {
      int i = 0;
      if (p_210537_2_) {
         if (p_210537_1_) {
            i |= 2;
         } else if (p_210537_0_) {
            i |= 128;
         } else {
            i |= 1;
         }
      } else if (p_210537_3_) {
         if (p_210537_0_) {
            i |= 32;
         } else if (p_210537_1_) {
            i |= 8;
         } else {
            i |= 16;
         }
      } else if (p_210537_1_) {
         i |= 4;
      } else if (p_210537_0_) {
         i |= 64;
      }

      return i;
   }

   public static final class LeavesSection extends LeavesFix.Section {
      @Nullable
      private IntSet field_212523_f;
      @Nullable
      private IntSet field_212524_g;
      @Nullable
      private Int2IntMap field_212525_h;

      public LeavesSection(Typed<?> p_i49851_1_, Schema p_i49851_2_) {
         super(p_i49851_1_, p_i49851_2_);
      }

      protected boolean func_212508_a() {
         this.field_212523_f = new IntOpenHashSet();
         this.field_212524_g = new IntOpenHashSet();
         this.field_212525_h = new Int2IntOpenHashMap();

         for(int i = 0; i < this.field_208469_d.size(); ++i) {
            Dynamic<?> dynamic = this.field_208469_d.get(i);
            String s = dynamic.getString("Name");
            if (LeavesFix.field_208434_j.containsKey(s)) {
               boolean flag = Objects.equals(dynamic.get("Properties").flatMap((p_212516_0_) -> {
                  return p_212516_0_.get("decayable");
               }).flatMap(Dynamic::getStringValue).orElse(""), "false");
               this.field_212523_f.add(i);
               this.field_212525_h.put(this.func_208464_a(s, flag, 7), i);
               this.field_208469_d.set(i, this.func_209770_a(dynamic, s, flag, 7));
            }

            if (LeavesFix.field_208435_k.contains(s)) {
               this.field_212524_g.add(i);
            }
         }

         return this.field_212523_f.isEmpty() && this.field_212524_g.isEmpty();
      }

      private Dynamic<?> func_209770_a(Dynamic<?> p_209770_1_, String p_209770_2_, boolean p_209770_3_, int p_209770_4_) {
         Dynamic<?> dynamic = p_209770_1_.emptyMap();
         dynamic = dynamic.set("persistent", dynamic.createString(p_209770_3_ ? "true" : "false"));
         dynamic = dynamic.set("distance", dynamic.createString(Integer.toString(p_209770_4_)));
         Dynamic<?> dynamic1 = p_209770_1_.emptyMap();
         dynamic1 = dynamic1.set("Properties", dynamic);
         dynamic1 = dynamic1.set("Name", dynamic1.createString(p_209770_2_));
         return dynamic1;
      }

      public boolean func_208457_b(int p_208457_1_) {
         return this.field_212524_g.contains(p_208457_1_);
      }

      public boolean func_208460_c(int p_208460_1_) {
         return this.field_212523_f.contains(p_208460_1_);
      }

      private int func_208459_d(int p_208459_1_) {
         return this.func_208457_b(p_208459_1_) ? 0 : Integer.parseInt(this.field_208469_d.get(p_208459_1_).get("Properties").flatMap((p_209768_0_) -> {
            return p_209768_0_.get("distance");
         }).flatMap(Dynamic::getStringValue).orElse(""));
      }

      private void func_208454_a(int p_208454_1_, int p_208454_2_, int p_208454_3_) {
         Dynamic<?> dynamic = this.field_208469_d.get(p_208454_2_);
         String s = dynamic.getString("Name");
         boolean flag = Objects.equals(dynamic.get("Properties").flatMap((p_209769_0_) -> {
            return p_209769_0_.get("persistent");
         }).flatMap(Dynamic::getStringValue).orElse(""), "true");
         int i = this.func_208464_a(s, flag, p_208454_3_);
         if (!this.field_212525_h.containsKey(i)) {
            int j = this.field_208469_d.size();
            this.field_212523_f.add(j);
            this.field_212525_h.put(i, j);
            this.field_208469_d.add(this.func_209770_a(dynamic, s, flag, p_208454_3_));
         }

         int l = this.field_212525_h.get(i);
         if (1 << this.field_208470_e.bitsPerEntry() <= l) {
            BitArray bitarray = new BitArray(this.field_208470_e.bitsPerEntry() + 1, 4096);

            for(int k = 0; k < 4096; ++k) {
               bitarray.setAt(k, this.field_208470_e.getAt(k));
            }

            this.field_208470_e = bitarray;
         }

         this.field_208470_e.setAt(p_208454_1_, l);
      }
   }

   public abstract static class Section {
      final Type<Pair<String, Dynamic<?>>> field_208466_a = DSL.named(TypeReferences.BLOCK_STATE.typeName(), DSL.remainderType());
      protected final OpticFinder<List<Pair<String, Dynamic<?>>>> field_208468_c = DSL.fieldFinder("Palette", DSL.list(this.field_208466_a));
      protected final List<Dynamic<?>> field_208469_d;
      protected final int field_208474_i;
      @Nullable
      protected BitArray field_208470_e;

      public Section(Typed<?> p_i49850_1_, Schema p_i49850_2_) {
         if (!Objects.equals(p_i49850_2_.getType(TypeReferences.BLOCK_STATE), this.field_208466_a)) {
            throw new IllegalStateException("Block state type is not what was expected.");
         } else {
            Optional<List<Pair<String, Dynamic<?>>>> optional = p_i49850_1_.getOptional(this.field_208468_c);
            this.field_208469_d = (List)optional.map((p_208463_0_) -> {
               return p_208463_0_.stream().map(Pair::getSecond).collect(Collectors.toList());
            }).orElse(ImmutableList.of());
            Dynamic<?> dynamic = p_i49850_1_.get(DSL.remainderFinder());
            this.field_208474_i = dynamic.getInt("Y");
            this.func_212507_a(dynamic);
         }
      }

      protected void func_212507_a(Dynamic<?> p_212507_1_) {
         if (this.func_212508_a()) {
            this.field_208470_e = null;
         } else {
            long[] along = p_212507_1_.get("BlockStates").flatMap(Dynamic::getLongStream).get().toArray();
            int i = Math.max(4, DataFixUtils.ceillog2(this.field_208469_d.size()));
            this.field_208470_e = new BitArray(i, 4096, along);
         }

      }

      public Typed<?> func_208465_a(Typed<?> p_208465_1_) {
         return this.func_208461_a() ? p_208465_1_ : p_208465_1_.update(DSL.remainderFinder(), (p_212510_1_) -> {
            return p_212510_1_.set("BlockStates", p_212510_1_.createLongList(Arrays.stream(this.field_208470_e.getBackingLongArray())));
         }).set(this.field_208468_c, (Typed)((List)this.field_208469_d).stream().map((p_212509_0_) -> {
            return Pair.of(TypeReferences.BLOCK_STATE.typeName(), p_212509_0_);
         }).collect(Collectors.toList()));
      }

      public boolean func_208461_a() {
         return this.field_208470_e == null;
      }

      public int func_208453_a(int p_208453_1_) {
         return this.field_208470_e.getAt(p_208453_1_);
      }

      protected int func_208464_a(String p_208464_1_, boolean p_208464_2_, int p_208464_3_) {
         return LeavesFix.field_208434_j.get(p_208464_1_) << 5 | (p_208464_2_ ? 16 : 0) | p_208464_3_;
      }

      int func_208456_b() {
         return this.field_208474_i;
      }

      protected abstract boolean func_212508_a();
   }
}
