package net.minecraft.client.renderer.model.multipart;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBlockDefinition;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.model.MultipartBakedModel;
import net.minecraft.client.renderer.model.VariantList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Multipart implements IUnbakedModel {
   private final StateContainer<Block, IBlockState> field_188140_b;
   private final List<Selector> field_188139_a;

   public Multipart(StateContainer<Block, IBlockState> p_i49524_1_, List<Selector> p_i49524_2_) {
      this.field_188140_b = p_i49524_1_;
      this.field_188139_a = p_i49524_2_;
   }

   public List<Selector> func_188136_a() {
      return this.field_188139_a;
   }

   public Set<VariantList> func_188137_b() {
      Set<VariantList> set = Sets.newHashSet();

      for(Selector selector : this.field_188139_a) {
         set.add(selector.func_188165_a());
      }

      return set;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof Multipart)) {
         return false;
      } else {
         Multipart multipart = (Multipart)p_equals_1_;
         return Objects.equals(this.field_188140_b, multipart.field_188140_b) && Objects.equals(this.field_188139_a, multipart.field_188139_a);
      }
   }

   public int hashCode() {
      return Objects.hash(this.field_188140_b, this.field_188139_a);
   }

   public Collection<ResourceLocation> func_187965_e() {
      return this.func_188136_a().stream().flatMap((p_209563_0_) -> {
         return p_209563_0_.func_188165_a().func_187965_e().stream();
      }).collect(Collectors.toSet());
   }

   public Collection<ResourceLocation> func_209559_a(Function<ResourceLocation, IUnbakedModel> p_209559_1_, Set<String> p_209559_2_) {
      return this.func_188136_a().stream().flatMap((p_209562_2_) -> {
         return p_209562_2_.func_188165_a().func_209559_a(p_209559_1_, p_209559_2_).stream();
      }).collect(Collectors.toSet());
   }

   @Nullable
   public IBakedModel func_209558_a(Function<ResourceLocation, IUnbakedModel> p_209558_1_, Function<ResourceLocation, TextureAtlasSprite> p_209558_2_, ModelRotation p_209558_3_, boolean p_209558_4_) {
      MultipartBakedModel.Builder multipartbakedmodel$builder = new MultipartBakedModel.Builder();

      for(Selector selector : this.func_188136_a()) {
         IBakedModel ibakedmodel = selector.func_188165_a().func_209558_a(p_209558_1_, p_209558_2_, p_209558_3_, p_209558_4_);
         if (ibakedmodel != null) {
            multipartbakedmodel$builder.func_188648_a(selector.func_188166_a(this.field_188140_b), ibakedmodel);
         }
      }

      return multipartbakedmodel$builder.func_188647_a();
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<Multipart> {
      private final ModelBlockDefinition.ContainerHolder field_209584_a;

      public Deserializer(ModelBlockDefinition.ContainerHolder p_i49520_1_) {
         this.field_209584_a = p_i49520_1_;
      }

      public Multipart deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return new Multipart(this.field_209584_a.func_209574_a(), this.func_188133_a(p_deserialize_3_, p_deserialize_1_.getAsJsonArray()));
      }

      private List<Selector> func_188133_a(JsonDeserializationContext p_188133_1_, JsonArray p_188133_2_) {
         List<Selector> list = Lists.newArrayList();

         for(JsonElement jsonelement : p_188133_2_) {
            list.add(p_188133_1_.deserialize(jsonelement, Selector.class));
         }

         return list;
      }
   }
}
