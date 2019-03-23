package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;

public abstract class NamedEntityFix extends DataFix {
   private final String field_206373_a;
   private final String field_206374_b;
   private final TypeReference field_206375_c;

   public NamedEntityFix(Schema p_i49625_1_, boolean p_i49625_2_, String p_i49625_3_, TypeReference p_i49625_4_, String p_i49625_5_) {
      super(p_i49625_1_, p_i49625_2_);
      this.field_206373_a = p_i49625_3_;
      this.field_206375_c = p_i49625_4_;
      this.field_206374_b = p_i49625_5_;
   }

   public TypeRewriteRule makeRule() {
      OpticFinder<?> opticfinder = DSL.namedChoice(this.field_206374_b, this.getInputSchema().getChoiceType(this.field_206375_c, this.field_206374_b));
      return this.fixTypeEverywhereTyped(this.field_206373_a, this.getInputSchema().getType(this.field_206375_c), this.getOutputSchema().getType(this.field_206375_c), (p_206371_2_) -> {
         return p_206371_2_.updateTyped(opticfinder, this.getOutputSchema().getChoiceType(this.field_206375_c, this.field_206374_b), this::fix);
      });
   }

   protected abstract Typed<?> fix(Typed<?> p_207419_1_);
}
