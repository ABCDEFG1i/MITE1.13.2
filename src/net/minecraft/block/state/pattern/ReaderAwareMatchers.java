package net.minecraft.block.state.pattern;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public final class ReaderAwareMatchers {
   public static <T> IBlockMatcherReaderAware<T> not(IBlockMatcherReaderAware<T> p_202084_0_) {
      return new ReaderAwareMatchers.NotMatcher<>(p_202084_0_);
   }

   public static <T> IBlockMatcherReaderAware<T> or(IBlockMatcherReaderAware<? super T>... p_202083_0_) {
      return new ReaderAwareMatchers.OrMatcher<T>(toListAssertingNonNull(p_202083_0_));
   }

   private static <T> List<T> toListAssertingNonNull(T... p_202086_0_) {
      return toListAssertingNonNull(Arrays.asList(p_202086_0_));
   }

   private static <T> List<T> toListAssertingNonNull(Iterable<T> p_202085_0_) {
      List<T> list = Lists.newArrayList();

      for(T t : p_202085_0_) {
         list.add(Preconditions.checkNotNull(t));
      }

      return list;
   }

   static class NotMatcher<T> implements IBlockMatcherReaderAware<T> {
      private final IBlockMatcherReaderAware<T> matcher;

      NotMatcher(IBlockMatcherReaderAware<T> p_i48751_1_) {
         this.matcher = Preconditions.checkNotNull(p_i48751_1_);
      }

      public boolean test(@Nullable T p_test_1_, IBlockReader p_test_2_, BlockPos p_test_3_) {
         return !this.matcher.test(p_test_1_, p_test_2_, p_test_3_);
      }
   }

   static class OrMatcher<T> implements IBlockMatcherReaderAware<T> {
      private final List<? extends IBlockMatcherReaderAware<? super T>> matchers;

      private OrMatcher(List<? extends IBlockMatcherReaderAware<? super T>> p_i48749_1_) {
         this.matchers = p_i48749_1_;
      }

      public boolean test(@Nullable T p_test_1_, IBlockReader p_test_2_, BlockPos p_test_3_) {
         for(int i = 0; i < this.matchers.size(); ++i) {
            if (this.matchers.get(i).test(p_test_1_, p_test_2_, p_test_3_)) {
               return true;
            }
         }

         return false;
      }
   }
}
