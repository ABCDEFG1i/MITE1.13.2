package net.minecraft.data;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.JsonToNBT;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SNBTToNBTConverter implements IDataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final DataGenerator generator;

   public SNBTToNBTConverter(DataGenerator p_i48257_1_) {
      this.generator = p_i48257_1_;
   }

   public void act(DirectoryCache p_200398_1_) throws IOException {
      Path path = this.generator.getOutputFolder();

      for(Path path1 : this.generator.getInputFolders()) {
         Files.walk(path1).filter((p_200422_0_) -> {
            return p_200422_0_.toString().endsWith(".snbt");
         }).forEach((p_200421_4_) -> {
            this.convert(p_200398_1_, p_200421_4_, this.getFileName(path1, p_200421_4_), path);
         });
      }

   }

   public String getName() {
      return "SNBT -> NBT";
   }

   private String getFileName(Path p_200423_1_, Path p_200423_2_) {
      String s = p_200423_1_.relativize(p_200423_2_).toString().replaceAll("\\\\", "/");
      return s.substring(0, s.length() - ".snbt".length());
   }

   private void convert(DirectoryCache p_208314_1_, Path p_208314_2_, String p_208314_3_, Path p_208314_4_) {
      try {
         Path path = p_208314_4_.resolve(p_208314_3_ + ".nbt");

         try (BufferedReader bufferedreader = Files.newBufferedReader(p_208314_2_)) {
            String s = IOUtils.toString((Reader)bufferedreader);
            String s1 = HASH_FUNCTION.hashUnencodedChars(s).toString();
            if (!Objects.equals(p_208314_1_.getPreviousHash(path), s1) || !Files.exists(path)) {
               Files.createDirectories(path.getParent());

               try (OutputStream outputstream = Files.newOutputStream(path)) {
                  CompressedStreamTools.writeCompressed(JsonToNBT.getTagFromJson(s), outputstream);
               }
            }

            p_208314_1_.func_208316_a(path, s1);
         }
      } catch (CommandSyntaxException commandsyntaxexception) {
         LOGGER.error("Couldn't convert {} from SNBT to NBT at {} as it's invalid SNBT", p_208314_3_, p_208314_2_, commandsyntaxexception);
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't convert {} from SNBT to NBT at {}", p_208314_3_, p_208314_2_, ioexception);
      }

   }
}
