package net.minecraft.util;

import java.util.List;
import java.util.Random;

public class WeightedRandom {
   public static int getTotalWeight(List<? extends WeightedRandom.Item> p_76272_0_) {
      int i = 0;
      int j = 0;

      for(int k = p_76272_0_.size(); j < k; ++j) {
         WeightedRandom.Item weightedrandom$item = p_76272_0_.get(j);
         i += weightedrandom$item.itemWeight;
      }

      return i;
   }

   public static <T extends WeightedRandom.Item> T getRandomItem(Random p_76273_0_, List<T> p_76273_1_, int p_76273_2_) {
      if (p_76273_2_ <= 0) {
         throw new IllegalArgumentException();
      } else {
         int i = p_76273_0_.nextInt(p_76273_2_);
         return getRandomItem(p_76273_1_, i);
      }
   }

   public static <T extends WeightedRandom.Item> T getRandomItem(List<T> p_180166_0_, int p_180166_1_) {
      int i = 0;

      for(int j = p_180166_0_.size(); i < j; ++i) {
         T t = p_180166_0_.get(i);
         p_180166_1_ -= t.itemWeight;
         if (p_180166_1_ < 0) {
            return t;
         }
      }

      return null;
   }

   public static <T extends WeightedRandom.Item> T getRandomItem(Random p_76271_0_, List<T> p_76271_1_) {
      return getRandomItem(p_76271_0_, p_76271_1_, getTotalWeight(p_76271_1_));
   }

   public static class Item {
      public int itemWeight;

      public Item(int itemWeight) {
         this.itemWeight = itemWeight;
      }
   }
}
