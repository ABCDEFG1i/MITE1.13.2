package net.minecraft.client.renderer.model.multipart;

import com.google.common.collect.Streams;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.StateContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AndCondition implements ICondition {
   private final Iterable<? extends ICondition> field_188121_c;

   public AndCondition(Iterable<? extends ICondition> p_i46566_1_) {
      this.field_188121_c = p_i46566_1_;
   }

   public Predicate<IBlockState> getPredicate(StateContainer<Block, IBlockState> p_getPredicate_1_) {
      List<Predicate<IBlockState>> list = Streams.stream(this.field_188121_c).map((p_200683_1_) -> {
         return p_200683_1_.getPredicate(p_getPredicate_1_);
      }).collect(Collectors.toList());
      return (p_212481_1_) -> {
         return list.stream().allMatch((p_212480_1_) -> {
            return p_212480_1_.test(p_212481_1_);
         });
      };
   }
}
