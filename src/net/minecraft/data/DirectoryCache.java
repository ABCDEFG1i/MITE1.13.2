package net.minecraft.data;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DirectoryCache {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Path outputFolder;
   private final Path cacheFile;
   private int hits;
   private final Map<Path, String> staleFiles = Maps.newHashMap();
   private final Map<Path, String> createdFiles = Maps.newHashMap();

   public DirectoryCache(Path p_i49352_1_, String p_i49352_2_) throws IOException {
      this.outputFolder = p_i49352_1_;
      Path path = p_i49352_1_.resolve(".cache");
      Files.createDirectories(path);
      this.cacheFile = path.resolve(p_i49352_2_);
      this.getFiles().forEach((p_209395_1_) -> {
         String s = this.staleFiles.put(p_209395_1_, "");
      });
      if (Files.isReadable(this.cacheFile)) {
         IOUtils.readLines(Files.newInputStream(this.cacheFile), Charsets.UTF_8).forEach((p_208315_2_) -> {
            int i = p_208315_2_.indexOf(32);
            this.staleFiles.put(p_i49352_1_.resolve(p_208315_2_.substring(i + 1)), p_208315_2_.substring(0, i));
         });
      }

   }

   public void writeCache() throws IOException {
      this.func_209400_b();

      Writer writer;
      try {
         writer = Files.newBufferedWriter(this.cacheFile);
      } catch (IOException ioexception) {
         LOGGER.warn("Unable write cachefile {}: {}", this.cacheFile, ioexception.toString());
         return;
      }

      IOUtils.writeLines(this.createdFiles.entrySet().stream().map((p_208319_1_) -> {
         return (String)p_208319_1_.getValue() + ' ' + this.outputFolder.relativize(p_208319_1_.getKey());
      }).collect(Collectors.toList()), System.lineSeparator(), writer);
      writer.close();
      LOGGER.debug("Caching: cache hits: {}, created: {} removed: {}", this.hits, this.createdFiles.size() - this.hits, this.staleFiles.size());
   }

   @Nullable
   public String getPreviousHash(Path p_208323_1_) {
      return this.staleFiles.get(p_208323_1_);
   }

   public void func_208316_a(Path p_208316_1_, String p_208316_2_) {
      this.createdFiles.put(p_208316_1_, p_208316_2_);
      if (Objects.equals(this.staleFiles.remove(p_208316_1_), p_208316_2_)) {
         ++this.hits;
      }

   }

   public boolean func_208320_b(Path p_208320_1_) {
      return this.staleFiles.containsKey(p_208320_1_);
   }

   private void func_209400_b() throws IOException {
      this.getFiles().forEach((p_208322_1_) -> {
         if (this.func_208320_b(p_208322_1_)) {
            try {
               Files.delete(p_208322_1_);
            } catch (IOException ioexception) {
               LOGGER.debug("Unable to delete: {} ({})", p_208322_1_, ioexception.toString());
            }
         }

      });
   }

   private Stream<Path> getFiles() throws IOException {
      return Files.walk(this.outputFolder).filter((p_209397_1_) -> {
         return !Objects.equals(this.cacheFile, p_209397_1_) && !Files.isDirectory(p_209397_1_);
      });
   }
}
