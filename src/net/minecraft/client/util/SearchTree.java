package net.minecraft.client.util;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SearchTree<T> implements ISearchTree<T> {
   protected SuffixArray<T> byName = new SuffixArray<>();
   protected SuffixArray<T> byDomain = new SuffixArray<>();
   protected SuffixArray<T> byPath = new SuffixArray<>();
   private final Function<T, Iterable<String>> nameFunc;
   private final Function<T, Iterable<ResourceLocation>> idFunc;
   private final List<T> contents = Lists.newArrayList();
   private final Object2IntMap<T> numericContents = new Object2IntOpenHashMap<>();

   public SearchTree(Function<T, Iterable<String>> p_i47612_1_, Function<T, Iterable<ResourceLocation>> p_i47612_2_) {
      this.nameFunc = p_i47612_1_;
      this.idFunc = p_i47612_2_;
   }

   public void recalculate() {
      this.byName = new SuffixArray<>();
      this.byDomain = new SuffixArray<>();
      this.byPath = new SuffixArray<>();

      for(T t : this.contents) {
         this.index(t);
      }

      this.byName.generate();
      this.byDomain.generate();
      this.byPath.generate();
   }

   public void add(T p_194043_1_) {
      this.numericContents.put(p_194043_1_, this.contents.size());
      this.contents.add(p_194043_1_);
      this.index(p_194043_1_);
   }

   public void func_199550_b() {
      this.contents.clear();
      this.numericContents.clear();
   }

   private void index(T p_194042_1_) {
      this.idFunc.apply(p_194042_1_).forEach((p_194039_2_) -> {
         this.byDomain.add(p_194042_1_, p_194039_2_.getNamespace().toLowerCase(Locale.ROOT));
         this.byPath.add(p_194042_1_, p_194039_2_.getPath().toLowerCase(Locale.ROOT));
      });
      this.nameFunc.apply(p_194042_1_).forEach((p_194041_2_) -> {
         this.byName.add(p_194042_1_, p_194041_2_.toLowerCase(Locale.ROOT));
      });
   }

   public List<T> search(String p_194038_1_) {
      int i = p_194038_1_.indexOf(58);
      if (i < 0) {
         return this.byName.search(p_194038_1_);
      } else {
         List<T> list = this.byDomain.search(p_194038_1_.substring(0, i).trim());
         String s = p_194038_1_.substring(i + 1).trim();
         List<T> list1 = this.byPath.search(s);
         List<T> list2 = this.byName.search(s);
         return Lists.newArrayList(new SearchTree.IntersectingIterator<>(list.iterator(), new SearchTree.MergingIterator<>(list1.iterator(), list2.iterator(), this.numericContents), this.numericContents));
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class IntersectingIterator<T> extends AbstractIterator<T> {
      private final PeekingIterator<T> field_195831_a;
      private final PeekingIterator<T> field_195832_b;
      private final Object2IntMap<T> field_195833_c;

      public IntersectingIterator(Iterator<T> p_i47715_1_, Iterator<T> p_i47715_2_, Object2IntMap<T> p_i47715_3_) {
         this.field_195831_a = Iterators.peekingIterator(p_i47715_1_);
         this.field_195832_b = Iterators.peekingIterator(p_i47715_2_);
         this.field_195833_c = p_i47715_3_;
      }

      protected T computeNext() {
         while(this.field_195831_a.hasNext() && this.field_195832_b.hasNext()) {
            int i = Integer.compare(this.field_195833_c.getInt(this.field_195831_a.peek()), this.field_195833_c.getInt(this.field_195832_b.peek()));
            if (i == 0) {
               this.field_195832_b.next();
               return this.field_195831_a.next();
            }

            if (i < 0) {
               this.field_195831_a.next();
            } else {
               this.field_195832_b.next();
            }
         }

         return this.endOfData();
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class MergingIterator<T> extends AbstractIterator<T> {
      private final PeekingIterator<T> leftItr;
      private final PeekingIterator<T> rightItr;
      private final Object2IntMap<T> numbers;

      public MergingIterator(Iterator<T> p_i47606_1_, Iterator<T> p_i47606_2_, Object2IntMap<T> p_i47606_3_) {
         this.leftItr = Iterators.peekingIterator(p_i47606_1_);
         this.rightItr = Iterators.peekingIterator(p_i47606_2_);
         this.numbers = p_i47606_3_;
      }

      protected T computeNext() {
         boolean flag = !this.leftItr.hasNext();
         boolean flag1 = !this.rightItr.hasNext();
         if (flag && flag1) {
            return this.endOfData();
         } else if (flag) {
            return this.rightItr.next();
         } else if (flag1) {
            return this.leftItr.next();
         } else {
            int i = Integer.compare(this.numbers.getInt(this.leftItr.peek()), this.numbers.getInt(this.rightItr.peek()));
            if (i == 0) {
               this.rightItr.next();
            }

            return i <= 0 ? this.leftItr.next() : this.rightItr.next();
         }
      }
   }
}
