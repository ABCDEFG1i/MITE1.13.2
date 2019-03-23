package net.minecraft.data;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TagsProvider<T> implements IDataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   protected final DataGenerator generator;
   protected final IRegistry<T> registry;
   protected final Map<Tag<T>, Tag.Builder<T>> tagToBuilder = Maps.newLinkedHashMap();

   protected TagsProvider(DataGenerator p_i49827_1_, IRegistry<T> p_i49827_2_) {
      this.generator = p_i49827_1_;
      this.registry = p_i49827_2_;
   }

   protected abstract void registerTags();

   public void act(DirectoryCache p_200398_1_) throws IOException {
      this.tagToBuilder.clear();
      this.registerTags();
      TagCollection<T> tagcollection = new TagCollection<>((p_200428_0_) -> {
         return false;
      }, (p_200430_0_) -> {
         return (T)null;
      }, "", false, "generated");

      for(Entry<Tag<T>, Tag.Builder<T>> entry : this.tagToBuilder.entrySet()) {
         ResourceLocation resourcelocation = entry.getKey().getId();
         if (!entry.getValue().resolve(tagcollection::get)) {
            throw new UnsupportedOperationException("Unsupported referencing of tags!");
         }

         Tag<T> tag = entry.getValue().build(resourcelocation);
         JsonObject jsonobject = tag.serialize(this.registry::func_177774_c);
         Path path = this.makePath(resourcelocation);
         tagcollection.register(tag);
         this.setCollection(tagcollection);

         try {
            String s = GSON.toJson((JsonElement)jsonobject);
            String s1 = HASH_FUNCTION.hashUnencodedChars(s).toString();
            if (!Objects.equals(p_200398_1_.getPreviousHash(path), s1) || !Files.exists(path)) {
               Files.createDirectories(path.getParent());

               try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
                  bufferedwriter.write(s);
               }
            }

            p_200398_1_.func_208316_a(path, s1);
         } catch (IOException ioexception) {
            LOGGER.error("Couldn't save tags to {}", path, ioexception);
         }
      }

   }

   protected abstract void setCollection(TagCollection<T> p_200429_1_);

   protected abstract Path makePath(ResourceLocation p_200431_1_);

   protected Tag.Builder<T> getBuilder(Tag<T> p_200426_1_) {
      return this.tagToBuilder.computeIfAbsent(p_200426_1_, (p_200427_0_) -> {
         return Tag.Builder.create();
      });
   }
}
