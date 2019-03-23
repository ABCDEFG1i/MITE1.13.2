package net.minecraft.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.advancements.AdventureAdvancements;
import net.minecraft.data.advancements.EndAdvancements;
import net.minecraft.data.advancements.HusbandryAdvancements;
import net.minecraft.data.advancements.NetherAdvancements;
import net.minecraft.data.advancements.StoryAdvancements;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementProvider implements IDataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final DataGenerator generator;
   private final List<Consumer<Consumer<Advancement>>> advancements = ImmutableList.of(new EndAdvancements(), new HusbandryAdvancements(), new AdventureAdvancements(), new NetherAdvancements(), new StoryAdvancements());

   public AdvancementProvider(DataGenerator p_i48869_1_) {
      this.generator = p_i48869_1_;
   }

   public void act(DirectoryCache p_200398_1_) throws IOException {
      Path path = this.generator.getOutputFolder();
      Set<ResourceLocation> set = Sets.newHashSet();
      Consumer<Advancement> consumer = (p_204017_4_) -> {
         if (!set.add(p_204017_4_.getId())) {
            throw new IllegalStateException("Duplicate advancement " + p_204017_4_.getId());
         } else {
            this.saveAdvancement(p_200398_1_, p_204017_4_.copy().serialize(), path.resolve("data/" + p_204017_4_.getId().getNamespace() + "/advancements/" + p_204017_4_.getId().getPath() + ".json"));
         }
      };

      for(Consumer<Consumer<Advancement>> consumer1 : this.advancements) {
         consumer1.accept(consumer);
      }

   }

   private void saveAdvancement(DirectoryCache p_208309_1_, JsonObject p_208309_2_, Path p_208309_3_) {
      try {
         String s = GSON.toJson((JsonElement)p_208309_2_);
         String s1 = HASH_FUNCTION.hashUnencodedChars(s).toString();
         if (!Objects.equals(p_208309_1_.getPreviousHash(p_208309_3_), s1) || !Files.exists(p_208309_3_)) {
            Files.createDirectories(p_208309_3_.getParent());

            try (BufferedWriter bufferedwriter = Files.newBufferedWriter(p_208309_3_)) {
               bufferedwriter.write(s);
            }
         }

         p_208309_1_.func_208316_a(p_208309_3_, s1);
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't save advancement {}", p_208309_3_, ioexception);
      }

   }

   public String getName() {
      return "Advancements";
   }
}
