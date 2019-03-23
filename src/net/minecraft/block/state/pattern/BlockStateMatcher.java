package net.minecraft.block.state.pattern;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;

public class BlockStateMatcher implements Predicate<IBlockState> {
   public static final Predicate<IBlockState> ANY = (p_201026_0_) -> {
      return true;
   };
   private final StateContainer<Block, IBlockState> blockstate;
   private final Map<IProperty<?>, Predicate<Object>> propertyPredicates = Maps.newHashMap();

   private BlockStateMatcher(StateContainer<Block, IBlockState> p_i45653_1_) {
      this.blockstate = p_i45653_1_;
   }

   public static BlockStateMatcher forBlock(Block p_177638_0_) {
      return new BlockStateMatcher(p_177638_0_.getStateContainer());
   }

   public boolean test(@Nullable IBlockState p_test_1_) {
      if (p_test_1_ != null && p_test_1_.getBlock().equals(this.blockstate.getOwner())) {
         if (this.propertyPredicates.isEmpty()) {
            return true;
         } else {
            for(Entry<IProperty<?>, Predicate<Object>> entry : this.propertyPredicates.entrySet()) {
               if (!this.matches(p_test_1_, entry.getKey(), entry.getValue())) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   protected <T extends Comparable<T>> boolean matches(IBlockState p_185927_1_, IProperty<T> p_185927_2_, Predicate<Object> p_185927_3_) {
      T t = p_185927_1_.get(p_185927_2_);
      return p_185927_3_.test(t);
   }

   public <V extends Comparable<V>> BlockStateMatcher where(IProperty<V> p_201028_1_, Predicate<Object> p_201028_2_) {
      if (!this.blockstate.getProperties().contains(p_201028_1_)) {
         throw new IllegalArgumentException(this.blockstate + " cannot support property " + p_201028_1_);
      } else {
         this.propertyPredicates.put(p_201028_1_, p_201028_2_);
         return this;
      }
   }
}
