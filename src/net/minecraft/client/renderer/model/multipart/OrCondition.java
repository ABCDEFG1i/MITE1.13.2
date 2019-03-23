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
public class OrCondition implements ICondition {
   private final Iterable<? extends ICondition> field_188127_c;

   public OrCondition(Iterable<? extends ICondition> p_i46563_1_) {
      this.field_188127_c = p_i46563_1_;
   }

   public Predicate<IBlockState> getPredicate(StateContainer<Block, IBlockState> p_getPredicate_1_) {
      List<Predicate<IBlockState>> list = Streams.stream(this.field_188127_c).map((p_200689_1_) -> {
         return p_200689_1_.getPredicate(p_getPredicate_1_);
      }).collect(Collectors.toList());
      return (p_200690_1_) -> {
         return list.stream().anyMatch((p_212488_1_) -> {
            return p_212488_1_.test(p_200690_1_);
         });
      };
   }
}
