package net.minecraft.client.renderer.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ModelBlock implements IUnbakedModel {
   private static final Logger field_178313_f = LogManager.getLogger();
   private static final ItemModelGenerator field_209571_g = new ItemModelGenerator();
   private static final FaceBakery field_209572_h = new FaceBakery();
   @VisibleForTesting
   static final Gson field_178319_a = (new GsonBuilder()).registerTypeAdapter(ModelBlock.class, new ModelBlock.Deserializer()).registerTypeAdapter(BlockPart.class, new BlockPart.Deserializer()).registerTypeAdapter(BlockPartFace.class, new BlockPartFace.Deserializer()).registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer()).registerTypeAdapter(ItemTransformVec3f.class, new ItemTransformVec3f.Deserializer()).registerTypeAdapter(ItemCameraTransforms.class, new ItemCameraTransforms.Deserializer()).registerTypeAdapter(ItemOverride.class, new ItemOverride.Deserializer()).create();
   private final List<BlockPart> field_178314_g;
   private final boolean field_178321_h;
   public final boolean field_178322_i;
   private final ItemCameraTransforms field_178320_j;
   private final List<ItemOverride> field_187968_k;
   public String field_178317_b = "";
   @VisibleForTesting
   public final Map<String, String> field_178318_c;
   @VisibleForTesting
   public ModelBlock field_178315_d;
   @VisibleForTesting
   ResourceLocation field_178316_e;

   public static ModelBlock func_178307_a(Reader p_178307_0_) {
      return JsonUtils.fromJson(field_178319_a, p_178307_0_, ModelBlock.class);
   }

   public static ModelBlock func_178294_a(String p_178294_0_) {
      return func_178307_a(new StringReader(p_178294_0_));
   }

   public ModelBlock(@Nullable ResourceLocation p_i46573_1_, List<BlockPart> p_i46573_2_, Map<String, String> p_i46573_3_, boolean p_i46573_4_, boolean p_i46573_5_, ItemCameraTransforms p_i46573_6_, List<ItemOverride> p_i46573_7_) {
      this.field_178314_g = p_i46573_2_;
      this.field_178322_i = p_i46573_4_;
      this.field_178321_h = p_i46573_5_;
      this.field_178318_c = p_i46573_3_;
      this.field_178316_e = p_i46573_1_;
      this.field_178320_j = p_i46573_6_;
      this.field_187968_k = p_i46573_7_;
   }

   public List<BlockPart> func_178298_a() {
      return this.field_178314_g.isEmpty() && this.func_178295_k() ? this.field_178315_d.func_178298_a() : this.field_178314_g;
   }

   private boolean func_178295_k() {
      return this.field_178315_d != null;
   }

   public boolean func_178309_b() {
      return this.func_178295_k() ? this.field_178315_d.func_178309_b() : this.field_178322_i;
   }

   public boolean func_178311_c() {
      return this.field_178321_h;
   }

   public boolean func_178303_d() {
      return this.field_178316_e == null || this.field_178315_d != null && this.field_178315_d.func_178303_d();
   }

   private void func_209566_a(Function<ResourceLocation, IUnbakedModel> p_209566_1_) {
      if (this.field_178316_e != null) {
         IUnbakedModel iunbakedmodel = p_209566_1_.apply(this.field_178316_e);
         if (iunbakedmodel != null) {
            if (!(iunbakedmodel instanceof ModelBlock)) {
               throw new IllegalStateException("BlockModel parent has to be a block model.");
            }

            this.field_178315_d = (ModelBlock)iunbakedmodel;
         }
      }

   }

   public List<ItemOverride> func_187966_f() {
      return this.field_187968_k;
   }

   public ItemOverrideList func_209568_a(ModelBlock p_209568_1_, Function<ResourceLocation, IUnbakedModel> p_209568_2_, Function<ResourceLocation, TextureAtlasSprite> p_209568_3_) {
      return this.field_187968_k.isEmpty() ? ItemOverrideList.field_188022_a : new ItemOverrideList(p_209568_1_, p_209568_2_, p_209568_3_, this.field_187968_k);
   }

   public Collection<ResourceLocation> func_187965_e() {
      Set<ResourceLocation> set = Sets.newHashSet();

      for(ItemOverride itemoverride : this.field_187968_k) {
         set.add(itemoverride.func_188026_a());
      }

      if (this.field_178316_e != null) {
         set.add(this.field_178316_e);
      }

      return set;
   }

   public Collection<ResourceLocation> func_209559_a(Function<ResourceLocation, IUnbakedModel> p_209559_1_, Set<String> p_209559_2_) {
      if (!this.func_178303_d()) {
         Set<ModelBlock> set = Sets.newLinkedHashSet();
         ModelBlock modelblock = this;

         while(true) {
            set.add(modelblock);
            modelblock.func_209566_a(p_209559_1_);
            if (set.contains(modelblock.field_178315_d)) {
               field_178313_f.warn("Found 'parent' loop while loading model '{}' in chain: {} -> {}", modelblock.field_178317_b, set.stream().map((p_209570_0_) -> {
                  return p_209570_0_.field_178317_b;
               }).collect(Collectors.joining(" -> ")), modelblock.field_178315_d.field_178317_b);
               modelblock.field_178316_e = ModelBakery.field_177604_a;
               modelblock.func_209566_a(p_209559_1_);
            }

            modelblock = modelblock.field_178315_d;
            if (modelblock.func_178303_d()) {
               break;
            }
         }
      }

      Set<ResourceLocation> set1 = Sets.newHashSet(new ResourceLocation(this.func_178308_c("particle")));

      for(BlockPart blockpart : this.func_178298_a()) {
         for(BlockPartFace blockpartface : blockpart.field_178240_c.values()) {
            String s = this.func_178308_c(blockpartface.field_178242_d);
            if (Objects.equals(s, MissingTextureSprite.getSprite().getName().toString())) {
               p_209559_2_.add(String.format("%s in %s", blockpartface.field_178242_d, this.field_178317_b));
            }

            set1.add(new ResourceLocation(s));
         }
      }

      this.field_187968_k.forEach((p_209564_4_) -> {
         IUnbakedModel iunbakedmodel = p_209559_1_.apply(p_209564_4_.func_188026_a());
         if (!Objects.equals(iunbakedmodel, this)) {
            set1.addAll(iunbakedmodel.func_209559_a(p_209559_1_, p_209559_2_));
         }
      });
      if (this.func_178310_f() == ModelBakery.field_177606_o) {
         ItemModelGenerator.field_178398_a.forEach((p_209569_2_) -> {
            set1.add(new ResourceLocation(this.func_178308_c(p_209569_2_)));
         });
      }

      return set1;
   }

   public IBakedModel func_209558_a(Function<ResourceLocation, IUnbakedModel> p_209558_1_, Function<ResourceLocation, TextureAtlasSprite> p_209558_2_, ModelRotation p_209558_3_, boolean p_209558_4_) {
      return this.func_209565_a(this, p_209558_1_, p_209558_2_, p_209558_3_, p_209558_4_);
   }

   private IBakedModel func_209565_a(ModelBlock p_209565_1_, Function<ResourceLocation, IUnbakedModel> p_209565_2_, Function<ResourceLocation, TextureAtlasSprite> p_209565_3_, ModelRotation p_209565_4_, boolean p_209565_5_) {
      ModelBlock modelblock = this.func_178310_f();
      if (modelblock == ModelBakery.field_177606_o) {
         return field_209571_g.func_209579_a(p_209565_3_, this).func_209565_a(p_209565_1_, p_209565_2_, p_209565_3_, p_209565_4_, p_209565_5_);
      } else if (modelblock == ModelBakery.field_177616_r) {
         return new BuiltInModel(this.func_181682_g(), this.func_209568_a(p_209565_1_, p_209565_2_, p_209565_3_));
      } else {
         TextureAtlasSprite textureatlassprite = p_209565_3_.apply(new ResourceLocation(this.func_178308_c("particle")));
         SimpleBakedModel.Builder simplebakedmodel$builder = (new SimpleBakedModel.Builder(this, this.func_209568_a(p_209565_1_, p_209565_2_, p_209565_3_))).func_177646_a(textureatlassprite);

         for(BlockPart blockpart : this.func_178298_a()) {
            for(EnumFacing enumfacing : blockpart.field_178240_c.keySet()) {
               BlockPartFace blockpartface = blockpart.field_178240_c.get(enumfacing);
               TextureAtlasSprite textureatlassprite1 = p_209565_3_.apply(new ResourceLocation(this.func_178308_c(blockpartface.field_178242_d)));
               if (blockpartface.field_178244_b == null) {
                  simplebakedmodel$builder.func_177648_a(func_209567_a(blockpart, blockpartface, textureatlassprite1, enumfacing, p_209565_4_, p_209565_5_));
               } else {
                  simplebakedmodel$builder.func_177650_a(p_209565_4_.func_177523_a(blockpartface.field_178244_b), func_209567_a(blockpart, blockpartface, textureatlassprite1, enumfacing, p_209565_4_, p_209565_5_));
               }
            }
         }

         return simplebakedmodel$builder.func_177645_b();
      }
   }

   private static BakedQuad func_209567_a(BlockPart p_209567_0_, BlockPartFace p_209567_1_, TextureAtlasSprite p_209567_2_, EnumFacing p_209567_3_, ModelRotation p_209567_4_, boolean p_209567_5_) {
      return field_209572_h.func_199332_a(p_209567_0_.field_178241_a, p_209567_0_.field_178239_b, p_209567_1_, p_209567_2_, p_209567_3_, p_209567_4_, p_209567_0_.field_178237_d, p_209567_5_, p_209567_0_.field_178238_e);
   }

   public boolean func_178300_b(String p_178300_1_) {
      return !MissingTextureSprite.getSprite().getName().toString().equals(this.func_178308_c(p_178300_1_));
   }

   public String func_178308_c(String p_178308_1_) {
      if (!this.func_178304_d(p_178308_1_)) {
         p_178308_1_ = '#' + p_178308_1_;
      }

      return this.func_178302_a(p_178308_1_, new ModelBlock.Bookkeep(this));
   }

   private String func_178302_a(String p_178302_1_, ModelBlock.Bookkeep p_178302_2_) {
      if (this.func_178304_d(p_178302_1_)) {
         if (this == p_178302_2_.field_178323_b) {
            field_178313_f.warn("Unable to resolve texture due to upward reference: {} in {}", p_178302_1_, this.field_178317_b);
            return MissingTextureSprite.getSprite().getName().toString();
         } else {
            String s = this.field_178318_c.get(p_178302_1_.substring(1));
            if (s == null && this.func_178295_k()) {
               s = this.field_178315_d.func_178302_a(p_178302_1_, p_178302_2_);
            }

            p_178302_2_.field_178323_b = this;
            if (s != null && this.func_178304_d(s)) {
               s = p_178302_2_.field_178324_a.func_178302_a(s, p_178302_2_);
            }

            return s != null && !this.func_178304_d(s) ? s : MissingTextureSprite.getSprite().getName().toString();
         }
      } else {
         return p_178302_1_;
      }
   }

   private boolean func_178304_d(String p_178304_1_) {
      return p_178304_1_.charAt(0) == '#';
   }

   public ModelBlock func_178310_f() {
      return this.func_178295_k() ? this.field_178315_d.func_178310_f() : this;
   }

   public ItemCameraTransforms func_181682_g() {
      ItemTransformVec3f itemtransformvec3f = this.func_181681_a(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND);
      ItemTransformVec3f itemtransformvec3f1 = this.func_181681_a(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
      ItemTransformVec3f itemtransformvec3f2 = this.func_181681_a(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND);
      ItemTransformVec3f itemtransformvec3f3 = this.func_181681_a(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
      ItemTransformVec3f itemtransformvec3f4 = this.func_181681_a(ItemCameraTransforms.TransformType.HEAD);
      ItemTransformVec3f itemtransformvec3f5 = this.func_181681_a(ItemCameraTransforms.TransformType.GUI);
      ItemTransformVec3f itemtransformvec3f6 = this.func_181681_a(ItemCameraTransforms.TransformType.GROUND);
      ItemTransformVec3f itemtransformvec3f7 = this.func_181681_a(ItemCameraTransforms.TransformType.FIXED);
      return new ItemCameraTransforms(itemtransformvec3f, itemtransformvec3f1, itemtransformvec3f2, itemtransformvec3f3, itemtransformvec3f4, itemtransformvec3f5, itemtransformvec3f6, itemtransformvec3f7);
   }

   private ItemTransformVec3f func_181681_a(ItemCameraTransforms.TransformType p_181681_1_) {
      return this.field_178315_d != null && !this.field_178320_j.func_181687_c(p_181681_1_) ? this.field_178315_d.func_181681_a(p_181681_1_) : this.field_178320_j.func_181688_b(p_181681_1_);
   }

   @OnlyIn(Dist.CLIENT)
   static final class Bookkeep {
      public final ModelBlock field_178324_a;
      public ModelBlock field_178323_b;

      private Bookkeep(ModelBlock p_i46223_1_) {
         this.field_178324_a = p_i46223_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<ModelBlock> {
      public ModelBlock deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
         List<BlockPart> list = this.func_178325_a(p_deserialize_3_, jsonobject);
         String s = this.func_178326_c(jsonobject);
         Map<String, String> map = this.func_178329_b(jsonobject);
         boolean flag = this.func_178328_a(jsonobject);
         ItemCameraTransforms itemcameratransforms = ItemCameraTransforms.field_178357_a;
         if (jsonobject.has("display")) {
            JsonObject jsonobject1 = JsonUtils.getJsonObject(jsonobject, "display");
            itemcameratransforms = p_deserialize_3_.deserialize(jsonobject1, ItemCameraTransforms.class);
         }

         List<ItemOverride> list1 = this.func_187964_a(p_deserialize_3_, jsonobject);
         ResourceLocation resourcelocation = s.isEmpty() ? null : new ResourceLocation(s);
         return new ModelBlock(resourcelocation, list, map, flag, true, itemcameratransforms, list1);
      }

      protected List<ItemOverride> func_187964_a(JsonDeserializationContext p_187964_1_, JsonObject p_187964_2_) {
         List<ItemOverride> list = Lists.newArrayList();
         if (p_187964_2_.has("overrides")) {
            for(JsonElement jsonelement : JsonUtils.getJsonArray(p_187964_2_, "overrides")) {
               list.add(p_187964_1_.deserialize(jsonelement, ItemOverride.class));
            }
         }

         return list;
      }

      private Map<String, String> func_178329_b(JsonObject p_178329_1_) {
         Map<String, String> map = Maps.newHashMap();
         if (p_178329_1_.has("textures")) {
            JsonObject jsonobject = p_178329_1_.getAsJsonObject("textures");

            for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
               map.put(entry.getKey(), entry.getValue().getAsString());
            }
         }

         return map;
      }

      private String func_178326_c(JsonObject p_178326_1_) {
         return JsonUtils.getString(p_178326_1_, "parent", "");
      }

      protected boolean func_178328_a(JsonObject p_178328_1_) {
         return JsonUtils.getBoolean(p_178328_1_, "ambientocclusion", true);
      }

      protected List<BlockPart> func_178325_a(JsonDeserializationContext p_178325_1_, JsonObject p_178325_2_) {
         List<BlockPart> list = Lists.newArrayList();
         if (p_178325_2_.has("elements")) {
            for(JsonElement jsonelement : JsonUtils.getJsonArray(p_178325_2_, "elements")) {
               list.add(p_178325_1_.deserialize(jsonelement, BlockPart.class));
            }
         }

         return list;
      }
   }
}
