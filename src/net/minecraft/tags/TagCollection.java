package net.minecraft.tags;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagCollection<T> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = new Gson();
   private static final int JSON_EXTENSION_LENGTH = ".json".length();
   private final Map<ResourceLocation, Tag<T>> tagMap = Maps.newHashMap();
   private final Function<ResourceLocation, T> resourceLocationToItem;
   private final Predicate<ResourceLocation> isValueKnownPredicate;
   private final String resourceLocationPrefix;
   private final boolean preserveOrder;
   private final String itemTypeName;

   public TagCollection(Predicate<ResourceLocation> p_i48235_1_, Function<ResourceLocation, T> p_i48235_2_, String p_i48235_3_, boolean p_i48235_4_, String p_i48235_5_) {
      this.isValueKnownPredicate = p_i48235_1_;
      this.resourceLocationToItem = p_i48235_2_;
      this.resourceLocationPrefix = p_i48235_3_;
      this.preserveOrder = p_i48235_4_;
      this.itemTypeName = p_i48235_5_;
   }

   public void register(Tag<T> p_199912_1_) {
      if (this.tagMap.containsKey(p_199912_1_.getId())) {
         throw new IllegalArgumentException("Duplicate " + this.itemTypeName + " tag '" + p_199912_1_.getId() + "'");
      } else {
         this.tagMap.put(p_199912_1_.getId(), p_199912_1_);
      }
   }

   @Nullable
   public Tag<T> get(ResourceLocation p_199910_1_) {
      return this.tagMap.get(p_199910_1_);
   }

   public Tag<T> getOrCreate(ResourceLocation p_199915_1_) {
      Tag<T> tag = this.tagMap.get(p_199915_1_);
      return tag == null ? new Tag<>(p_199915_1_) : tag;
   }

   public Collection<ResourceLocation> getRegisteredTags() {
      return this.tagMap.keySet();
   }

   @OnlyIn(Dist.CLIENT)
   public Collection<ResourceLocation> getOwningTags(T p_199913_1_) {
      List<ResourceLocation> list = Lists.newArrayList();

      for(Entry<ResourceLocation, Tag<T>> entry : this.tagMap.entrySet()) {
         if (entry.getValue().contains(p_199913_1_)) {
            list.add(entry.getKey());
         }
      }

      return list;
   }

   public void clear() {
      this.tagMap.clear();
   }

   public void reload(IResourceManager p_199909_1_) {
      Map<ResourceLocation, Tag.Builder<T>> map = Maps.newHashMap();

      for(ResourceLocation resourcelocation : p_199909_1_.getAllResourceLocations(this.resourceLocationPrefix, (p_199916_0_) -> {
         return p_199916_0_.endsWith(".json");
      })) {
         String s = resourcelocation.getPath();
         ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(this.resourceLocationPrefix.length() + 1, s.length() - JSON_EXTENSION_LENGTH));

         try {
            for(IResource iresource : p_199909_1_.getAllResources(resourcelocation)) {
               try {
                  JsonObject jsonobject = JsonUtils.fromJson(GSON, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
                  if (jsonobject == null) {
                     LOGGER.error("Couldn't load {} tag list {} from {} in data pack {} as it's empty or null", this.itemTypeName, resourcelocation1, resourcelocation, iresource.getPackName());
                  } else {
                     Tag.Builder<T> builder = map.getOrDefault(resourcelocation1, Tag.Builder.create());
                     builder.deserialize(this.isValueKnownPredicate, this.resourceLocationToItem, jsonobject);
                     map.put(resourcelocation1, builder);
                  }
               } catch (RuntimeException | IOException ioexception) {
                  LOGGER.error("Couldn't read {} tag list {} from {} in data pack {}", this.itemTypeName, resourcelocation1, resourcelocation, iresource.getPackName(), ioexception);
               } finally {
                  IOUtils.closeQuietly((Closeable)iresource);
               }
            }
         } catch (IOException ioexception1) {
            LOGGER.error("Couldn't read {} tag list {} from {}", this.itemTypeName, resourcelocation1, resourcelocation, ioexception1);
         }
      }

      while(!map.isEmpty()) {
         boolean flag = false;
         Iterator<Entry<ResourceLocation, Tag.Builder<T>>> iterator = map.entrySet().iterator();

         while(iterator.hasNext()) {
            Entry<ResourceLocation, Tag.Builder<T>> entry1 = iterator.next();
            if (entry1.getValue().resolve(this::get)) {
               flag = true;
               this.register(entry1.getValue().build(entry1.getKey()));
               iterator.remove();
            }
         }

         if (!flag) {
            for(Entry<ResourceLocation, Tag.Builder<T>> entry2 : map.entrySet()) {
               LOGGER.error("Couldn't load {} tag {} as it either references another tag that doesn't exist, or ultimately references itself", this.itemTypeName, entry2.getKey());
            }
            break;
         }
      }

      for(Entry<ResourceLocation, Tag.Builder<T>> entry : map.entrySet()) {
         this.register(entry.getValue().ordered(this.preserveOrder).build(entry.getKey()));
      }

   }

   public Map<ResourceLocation, Tag<T>> getTagMap() {
      return this.tagMap;
   }
}
