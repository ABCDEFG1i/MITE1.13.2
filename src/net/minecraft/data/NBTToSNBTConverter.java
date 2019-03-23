package net.minecraft.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NBTToSNBTConverter implements IDataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final DataGenerator generator;

   public NBTToSNBTConverter(DataGenerator p_i48258_1_) {
      this.generator = p_i48258_1_;
   }

   public void act(DirectoryCache p_200398_1_) throws IOException {
      Path path = this.generator.getOutputFolder();

      for(Path path1 : this.generator.getInputFolders()) {
         Files.walk(path1).filter((p_200416_0_) -> {
            return p_200416_0_.toString().endsWith(".nbt");
         }).forEach((p_200415_3_) -> {
            this.convert(p_200415_3_, this.getFileName(path1, p_200415_3_), path);
         });
      }

   }

   public String getName() {
      return "NBT to SNBT";
   }

   private String getFileName(Path p_200417_1_, Path p_200417_2_) {
      String s = p_200417_1_.relativize(p_200417_2_).toString().replaceAll("\\\\", "/");
      return s.substring(0, s.length() - ".nbt".length());
   }

   private void convert(Path p_200414_1_, String p_200414_2_, Path p_200414_3_) {
      try {
         NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(Files.newInputStream(p_200414_1_));
         ITextComponent itextcomponent = nbttagcompound.toFormattedComponent("    ", 0);
         String s = itextcomponent.getString();
         Path path = p_200414_3_.resolve(p_200414_2_ + ".snbt");
         Files.createDirectories(path.getParent());

         try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
            bufferedwriter.write(s);
         }

         LOGGER.info("Converted {} from NBT to SNBT", (Object)p_200414_2_);
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't convert {} from NBT to SNBT at {}", p_200414_2_, p_200414_1_, ioexception);
      }

   }
}
