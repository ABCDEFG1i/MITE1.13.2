package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import net.minecraft.util.datafix.TypeReferences;

public abstract class TypedEntityRenameHelper extends DataFix {
   private final String field_211312_a;

   public TypedEntityRenameHelper(String p_i49713_1_, Schema p_i49713_2_, boolean p_i49713_3_) {
      super(p_i49713_2_, p_i49713_3_);
      this.field_211312_a = p_i49713_1_;
   }

   public TypeRewriteRule makeRule() {
      TaggedChoiceType<String> taggedchoicetype = (TaggedChoiceType<String>) this.getInputSchema().findChoiceType(TypeReferences.ENTITY);
      TaggedChoiceType<String> taggedchoicetype1 = (TaggedChoiceType<String>) this.getOutputSchema().findChoiceType(TypeReferences.ENTITY);
      Type<Pair<String, String>> type = DSL.named(TypeReferences.ENTITY_NAME.typeName(), DSL.namespacedString());
      if (!Objects.equals(this.getOutputSchema().getType(TypeReferences.ENTITY_NAME), type)) {
         throw new IllegalStateException("Entity name type is not what was expected.");
      } else {
         return TypeRewriteRule.seq(this.fixTypeEverywhere(this.field_211312_a, taggedchoicetype, taggedchoicetype1, (p_211306_3_) -> {
            return (p_211307_3_) -> {
               return p_211307_3_.mapFirst((p_211309_3_) -> {
                  String s = this.func_211311_a(p_211309_3_);
                  Type<?> type1 = taggedchoicetype.types().get(p_211309_3_);
                  Type<?> type2 = taggedchoicetype1.types().get(s);
                  if (!type2.equals(type1, true, true)) {
                     throw new IllegalStateException(String.format("Dynamic type check failed: %s not equal to %s", type2, type1));
                  } else {
                     return s;
                  }
               });
            };
         }), this.fixTypeEverywhere(this.field_211312_a + " for entity name", type, (p_211308_1_) -> {
            return (p_211310_1_) -> {
               return p_211310_1_.mapSecond(this::func_211311_a);
            };
         }));
      }
   }

   protected abstract String func_211311_a(String p_211311_1_);
}
