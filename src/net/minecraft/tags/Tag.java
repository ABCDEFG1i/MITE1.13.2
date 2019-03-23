package net.minecraft.tags;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class Tag<T> {
   private final ResourceLocation resourceLocation;
   private final Set<T> taggedItems;
   private final Collection<Tag.ITagEntry<T>> entries;

   public Tag(ResourceLocation p_i48236_1_) {
      this.resourceLocation = p_i48236_1_;
      this.taggedItems = Collections.emptySet();
      this.entries = Collections.emptyList();
   }

   public Tag(ResourceLocation p_i48224_1_, Collection<Tag.ITagEntry<T>> p_i48224_2_, boolean p_i48224_3_) {
      this.resourceLocation = p_i48224_1_;
      this.taggedItems = (Set<T>)(p_i48224_3_ ? Sets.newLinkedHashSet() : Sets.newHashSet());
      this.entries = p_i48224_2_;

      for(Tag.ITagEntry<T> itagentry : p_i48224_2_) {
         itagentry.populate(this.taggedItems);
      }

   }

   public JsonObject serialize(Function<T, ResourceLocation> p_200571_1_) {
      JsonObject jsonobject = new JsonObject();
      JsonArray jsonarray = new JsonArray();

      for(Tag.ITagEntry<T> itagentry : this.entries) {
         itagentry.serialize(jsonarray, p_200571_1_);
      }

      jsonobject.addProperty("replace", false);
      jsonobject.add("values", jsonarray);
      return jsonobject;
   }

   public boolean contains(T p_199685_1_) {
      return this.taggedItems.contains(p_199685_1_);
   }

   public Collection<T> getAllElements() {
      return this.taggedItems;
   }

   public Collection<Tag.ITagEntry<T>> getEntries() {
      return this.entries;
   }

   public T getRandomElement(Random p_205596_1_) {
      List<T> list = Lists.newArrayList(this.getAllElements());
      return list.get(p_205596_1_.nextInt(list.size()));
   }

   public ResourceLocation getId() {
      return this.resourceLocation;
   }

   public static class Builder<T> {
      private final Set<Tag.ITagEntry<T>> entries = Sets.newLinkedHashSet();
      private boolean preserveOrder;

      public static <T> Tag.Builder<T> create() {
         return new Tag.Builder<>();
      }

      public Tag.Builder<T> add(Tag.ITagEntry<T> p_200575_1_) {
         this.entries.add(p_200575_1_);
         return this;
      }

      public Tag.Builder<T> add(T p_200048_1_) {
         this.entries.add(new Tag.ListEntry<>(Collections.singleton(p_200048_1_)));
         return this;
      }

      @SafeVarargs
      public final Tag.Builder<T> add(T... p_200573_1_) {
         this.entries.add(new Tag.ListEntry<>(Lists.newArrayList(p_200573_1_)));
         return this;
      }

      public Tag.Builder<T> addAll(Collection<T> p_200046_1_) {
         this.entries.add(new Tag.ListEntry<>(p_200046_1_));
         return this;
      }

      public Tag.Builder<T> add(ResourceLocation p_200159_1_) {
         this.entries.add(new Tag.TagEntry<>(p_200159_1_));
         return this;
      }

      public Tag.Builder<T> add(Tag<T> p_200574_1_) {
         this.entries.add(new Tag.TagEntry<>(p_200574_1_));
         return this;
      }

      public Tag.Builder<T> ordered(boolean p_200045_1_) {
         this.preserveOrder = p_200045_1_;
         return this;
      }

      public boolean resolve(Function<ResourceLocation, Tag<T>> p_200160_1_) {
         for(Tag.ITagEntry<T> itagentry : this.entries) {
            if (!itagentry.resolve(p_200160_1_)) {
               return false;
            }
         }

         return true;
      }

      public Tag<T> build(ResourceLocation p_200051_1_) {
         return new Tag<>(p_200051_1_, this.entries, this.preserveOrder);
      }

      public Tag.Builder<T> deserialize(Predicate<ResourceLocation> p_200158_1_, Function<ResourceLocation, T> p_200158_2_, JsonObject p_200158_3_) {
         JsonArray jsonarray = JsonUtils.getJsonArray(p_200158_3_, "values");
         if (JsonUtils.getBoolean(p_200158_3_, "replace", false)) {
            this.entries.clear();
         }

         for(JsonElement jsonelement : jsonarray) {
            String s = JsonUtils.getString(jsonelement, "value");
            if (!s.startsWith("#")) {
               ResourceLocation resourcelocation = new ResourceLocation(s);
               T t = p_200158_2_.apply(resourcelocation);
               if (t == null || !p_200158_1_.test(resourcelocation)) {
                  throw new JsonParseException("Unknown value '" + resourcelocation + "'");
               }

               this.add(t);
            } else {
               this.add(new ResourceLocation(s.substring(1)));
            }
         }

         return this;
      }
   }

   public interface ITagEntry<T> {
      default boolean resolve(Function<ResourceLocation, Tag<T>> p_200161_1_) {
         return true;
      }

      void populate(Collection<T> p_200162_1_);

      void serialize(JsonArray p_200576_1_, Function<T, ResourceLocation> p_200576_2_);
   }

   public static class ListEntry<T> implements Tag.ITagEntry<T> {
      private final Collection<T> taggedItems;

      public ListEntry(Collection<T> p_i48227_1_) {
         this.taggedItems = p_i48227_1_;
      }

      public void populate(Collection<T> p_200162_1_) {
         p_200162_1_.addAll(this.taggedItems);
      }

      public void serialize(JsonArray p_200576_1_, Function<T, ResourceLocation> p_200576_2_) {
         for(T t : this.taggedItems) {
            ResourceLocation resourcelocation = p_200576_2_.apply(t);
            if (resourcelocation == null) {
               throw new IllegalStateException("Unable to serialize an anonymous value to json!");
            }

            p_200576_1_.add(resourcelocation.toString());
         }

      }

      public Collection<T> getTaggedItems() {
         return this.taggedItems;
      }
   }

   public static class TagEntry<T> implements Tag.ITagEntry<T> {
      @Nullable
      private final ResourceLocation id;
      @Nullable
      private Tag<T> tag;

      public TagEntry(ResourceLocation p_i48228_1_) {
         this.id = p_i48228_1_;
      }

      public TagEntry(Tag<T> p_i48229_1_) {
         this.id = p_i48229_1_.getId();
         this.tag = p_i48229_1_;
      }

      public boolean resolve(Function<ResourceLocation, Tag<T>> p_200161_1_) {
         if (this.tag == null) {
            this.tag = p_200161_1_.apply(this.id);
         }

         return this.tag != null;
      }

      public void populate(Collection<T> p_200162_1_) {
         if (this.tag == null) {
            throw new IllegalStateException("Cannot build unresolved tag entry");
         } else {
            p_200162_1_.addAll(this.tag.getAllElements());
         }
      }

      public ResourceLocation getSerializedId() {
         if (this.tag != null) {
            return this.tag.getId();
         } else if (this.id != null) {
            return this.id;
         } else {
            throw new IllegalStateException("Cannot serialize an anonymous tag to json!");
         }
      }

      public void serialize(JsonArray p_200576_1_, Function<T, ResourceLocation> p_200576_2_) {
         p_200576_1_.add("#" + this.getSerializedId());
      }
   }
}
