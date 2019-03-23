package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VariantList implements IUnbakedModel {
   private final List<Variant> field_188115_a;

   public VariantList(List<Variant> p_i46568_1_) {
      this.field_188115_a = p_i46568_1_;
   }

   public List<Variant> func_188114_a() {
      return this.field_188115_a;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ instanceof VariantList) {
         VariantList variantlist = (VariantList)p_equals_1_;
         return this.field_188115_a.equals(variantlist.field_188115_a);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.field_188115_a.hashCode();
   }

   public Collection<ResourceLocation> func_187965_e() {
      return this.func_188114_a().stream().map(Variant::func_188046_a).collect(Collectors.toSet());
   }

   public Collection<ResourceLocation> func_209559_a(Function<ResourceLocation, IUnbakedModel> p_209559_1_, Set<String> p_209559_2_) {
      return this.func_188114_a().stream().map(Variant::func_188046_a).distinct().flatMap((p_209561_2_) -> {
         return p_209559_1_.apply(p_209561_2_).func_209559_a(p_209559_1_, p_209559_2_).stream();
      }).collect(Collectors.toSet());
   }

   @Nullable
   public IBakedModel func_209558_a(Function<ResourceLocation, IUnbakedModel> p_209558_1_, Function<ResourceLocation, TextureAtlasSprite> p_209558_2_, ModelRotation p_209558_3_, boolean p_209558_4_) {
      if (this.func_188114_a().isEmpty()) {
         return null;
      } else {
         WeightedBakedModel.Builder weightedbakedmodel$builder = new WeightedBakedModel.Builder();

         for(Variant variant : this.func_188114_a()) {
            IBakedModel ibakedmodel = p_209558_1_.apply(variant.func_188046_a()).func_209558_a(p_209558_1_, p_209558_2_, variant.func_188048_b(), variant.func_188049_c());
            weightedbakedmodel$builder.func_177677_a(ibakedmodel, variant.func_188047_d());
         }

         return weightedbakedmodel$builder.func_209614_a();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<VariantList> {
      public VariantList deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         List<Variant> list = Lists.newArrayList();
         if (p_deserialize_1_.isJsonArray()) {
            JsonArray jsonarray = p_deserialize_1_.getAsJsonArray();
            if (jsonarray.size() == 0) {
               throw new JsonParseException("Empty variant array");
            }

            for(JsonElement jsonelement : jsonarray) {
               list.add(p_deserialize_3_.deserialize(jsonelement, Variant.class));
            }
         } else {
            list.add(p_deserialize_3_.deserialize(p_deserialize_1_, Variant.class));
         }

         return new VariantList(list);
      }
   }
}
