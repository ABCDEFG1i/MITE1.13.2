package net.minecraft.client.renderer.model.multipart;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Streams;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.model.VariantList;
import net.minecraft.state.StateContainer;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Selector {
   private final ICondition field_188167_a;
   private final VariantList field_188168_b;

   public Selector(ICondition p_i46562_1_, VariantList p_i46562_2_) {
      if (p_i46562_1_ == null) {
         throw new IllegalArgumentException("Missing condition for selector");
      } else if (p_i46562_2_ == null) {
         throw new IllegalArgumentException("Missing variant for selector");
      } else {
         this.field_188167_a = p_i46562_1_;
         this.field_188168_b = p_i46562_2_;
      }
   }

   public VariantList func_188165_a() {
      return this.field_188168_b;
   }

   public Predicate<IBlockState> func_188166_a(StateContainer<Block, IBlockState> p_188166_1_) {
      return this.field_188167_a.getPredicate(p_188166_1_);
   }

   public boolean equals(Object p_equals_1_) {
      return this == p_equals_1_;
   }

   public int hashCode() {
      return System.identityHashCode(this);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<Selector> {
      public Selector deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
         return new Selector(this.func_188159_b(jsonobject), p_deserialize_3_.deserialize(jsonobject.get("apply"), VariantList.class));
      }

      private ICondition func_188159_b(JsonObject p_188159_1_) {
         return p_188159_1_.has("when") ? func_188158_a(JsonUtils.getJsonObject(p_188159_1_, "when")) : ICondition.TRUE;
      }

      @VisibleForTesting
      static ICondition func_188158_a(JsonObject p_188158_0_) {
         Set<Entry<String, JsonElement>> set = p_188158_0_.entrySet();
         if (set.isEmpty()) {
            throw new JsonParseException("No elements found in selector");
         } else if (set.size() == 1) {
            if (p_188158_0_.has("OR")) {
               List<ICondition> list1 = Streams.stream(JsonUtils.getJsonArray(p_188158_0_, "OR")).map((p_200692_0_) -> {
                  return func_188158_a(p_200692_0_.getAsJsonObject());
               }).collect(Collectors.toList());
               return new OrCondition(list1);
            } else if (p_188158_0_.has("AND")) {
               List<ICondition> list = Streams.stream(JsonUtils.getJsonArray(p_188158_0_, "AND")).map((p_200691_0_) -> {
                  return func_188158_a(p_200691_0_.getAsJsonObject());
               }).collect(Collectors.toList());
               return new AndCondition(list);
            } else {
               return func_188161_b(set.iterator().next());
            }
         } else {
            return new AndCondition(set.stream().map((p_212490_0_) -> {
               return func_188161_b(p_212490_0_);
            }).collect(Collectors.toList()));
         }
      }

      private static ICondition func_188161_b(Entry<String, JsonElement> p_188161_0_) {
         return new PropertyValueCondition(p_188161_0_.getKey(), p_188161_0_.getValue().getAsString());
      }
   }
}
