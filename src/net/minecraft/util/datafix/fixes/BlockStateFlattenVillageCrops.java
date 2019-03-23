package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.stream.Stream;
import net.minecraft.util.datafix.TypeReferences;

public class BlockStateFlattenVillageCrops extends DataFix {
   public BlockStateFlattenVillageCrops(Schema p_i49617_1_, boolean p_i49617_2_) {
      super(p_i49617_1_, p_i49617_2_);
   }

   public TypeRewriteRule makeRule() {
      return this.writeFixAndRead("SavedDataVillageCropFix", this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE), this.getOutputSchema().getType(TypeReferences.STRUCTURE_FEATURE), this::func_209677_a);
   }

   private <T> Dynamic<T> func_209677_a(Dynamic<T> p_209677_1_) {
      return p_209677_1_.update("Children", BlockStateFlattenVillageCrops::func_210590_b);
   }

   private static <T> Dynamic<T> func_210590_b(Dynamic<T> p_210590_0_) {
      return p_210590_0_.getStream().map(BlockStateFlattenVillageCrops::func_210586_a).map(p_210590_0_::createList).orElse(p_210590_0_);
   }

   private static Stream<? extends Dynamic<?>> func_210586_a(Stream<? extends Dynamic<?>> p_210586_0_) {
      return p_210586_0_.<Dynamic<?>>map((p_210587_0_) -> {
         String s = p_210587_0_.getString("id");
         if ("ViF".equals(s)) {
            return func_210588_c(p_210587_0_);
         } else {
            return "ViDF".equals(s) ? func_210589_d(p_210587_0_) : p_210587_0_;
         }
      });
   }

   private static <T> Dynamic<T> func_210588_c(Dynamic<T> p_210588_0_) {
      p_210588_0_ = func_209676_a(p_210588_0_, "CA");
      return func_209676_a(p_210588_0_, "CB");
   }

   private static <T> Dynamic<T> func_210589_d(Dynamic<T> p_210589_0_) {
      p_210589_0_ = func_209676_a(p_210589_0_, "CA");
      p_210589_0_ = func_209676_a(p_210589_0_, "CB");
      p_210589_0_ = func_209676_a(p_210589_0_, "CC");
      return func_209676_a(p_210589_0_, "CD");
   }

   private static <T> Dynamic<T> func_209676_a(Dynamic<T> p_209676_0_, String p_209676_1_) {
      return p_209676_0_.get(p_209676_1_).flatMap(Dynamic::getNumberValue).isPresent() ? p_209676_0_.set(p_209676_1_, BlockStateFlatteningMap.getFixedNBTForID(p_209676_0_.getInt(p_209676_1_) << 4)) : p_209676_0_;
   }
}
