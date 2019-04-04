package net.minecraft.block.state.pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import net.minecraft.block.state.BlockWorldState;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class FactoryBlockPattern {
   private static final Joiner COMMA_JOIN = Joiner.on(",");
   private final List<String[]> depth = Lists.newArrayList();
   private final Map<Character, Predicate<BlockWorldState>> symbolMap = Maps.newHashMap();
   private int aisleHeight;
   private int rowWidth;

   private FactoryBlockPattern() {
      this.symbolMap.put(' ', Predicates.alwaysTrue());
   }

   public FactoryBlockPattern aisle(String... p_177659_1_) {
      if (!ArrayUtils.isEmpty(p_177659_1_) && !StringUtils.isEmpty(p_177659_1_[0])) {
         if (this.depth.isEmpty()) {
            this.aisleHeight = p_177659_1_.length;
            this.rowWidth = p_177659_1_[0].length();
         }

         if (p_177659_1_.length != this.aisleHeight) {
            throw new IllegalArgumentException("Expected aisle with height of " + this.aisleHeight + ", but was given one with a height of " + p_177659_1_.length + ")");
         } else {
            for(String s : p_177659_1_) {
               if (s.length() != this.rowWidth) {
                  throw new IllegalArgumentException("Not all rows in the given aisle are the correct width (expected " + this.rowWidth + ", found one with " + s.length() + ")");
               }

               for(char c0 : s.toCharArray()) {
                  if (!this.symbolMap.containsKey(c0)) {
                     this.symbolMap.put(c0, null);
                  }
               }
            }

            this.depth.add(p_177659_1_);
            return this;
         }
      } else {
         throw new IllegalArgumentException("Empty pattern for aisle");
      }
   }

   public static FactoryBlockPattern start() {
      return new FactoryBlockPattern();
   }

   public FactoryBlockPattern where(char p_177662_1_, Predicate<BlockWorldState> p_177662_2_) {
      this.symbolMap.put(p_177662_1_, p_177662_2_);
      return this;
   }

   public BlockPattern build() {
      return new BlockPattern(this.makePredicateArray());
   }

   private Predicate<BlockWorldState>[][][] makePredicateArray() {
      this.checkMissingPredicates();
      Predicate<BlockWorldState>[][][] predicate = (Predicate[][][])Array.newInstance(Predicate.class, this.depth.size(), this.aisleHeight, this.rowWidth);

      for(int i = 0; i < this.depth.size(); ++i) {
         for(int j = 0; j < this.aisleHeight; ++j) {
            for(int k = 0; k < this.rowWidth; ++k) {
               predicate[i][j][k] = this.symbolMap.get((this.depth.get(i))[j].charAt(k));
            }
         }
      }

      return predicate;
   }

   private void checkMissingPredicates() {
      List<Character> list = Lists.newArrayList();

      for(Entry<Character, Predicate<BlockWorldState>> entry : this.symbolMap.entrySet()) {
         if (entry.getValue() == null) {
            list.add(entry.getKey());
         }
      }

      if (!list.isEmpty()) {
         throw new IllegalStateException("Predicates for character(s) " + COMMA_JOIN.join(list) + " are missing");
      }
   }
}
