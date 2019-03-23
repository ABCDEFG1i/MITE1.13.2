package net.minecraft.world.storage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface ISaveFormat {
   DateTimeFormatter BACKUP_DATE_FORMAT = (new DateTimeFormatterBuilder()).appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();

   @OnlyIn(Dist.CLIENT)
   default long createBackup(String p_197713_1_) throws IOException {
      final Path path = this.getWorldFolder(p_197713_1_);
      String s = LocalDateTime.now().format(BACKUP_DATE_FORMAT) + "_" + p_197713_1_;
      int i = 0;
      Path path1 = this.getBackupsFolder();

      try {
         Files.createDirectories(Files.exists(path1) ? path1.toRealPath() : path1);
      } catch (IOException ioexception) {
         throw new RuntimeException(ioexception);
      }

      Path path2;
      while(true) {
         path2 = path1.resolve(s + (i++ > 0 ? "_" + i : "") + ".zip");
         if (!Files.exists(path2)) {
            break;
         }
      }

      try (final ZipOutputStream zipoutputstream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(path2.toFile())))) {
         final Path path3 = Paths.get(p_197713_1_);
         Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path p_visitFile_1_, BasicFileAttributes p_visitFile_2_) throws IOException {
               String s1 = path3.resolve(path.relativize(p_visitFile_1_)).toString();
               ZipEntry zipentry = new ZipEntry(s1);
               zipoutputstream.putNextEntry(zipentry);
               com.google.common.io.Files.asByteSource(p_visitFile_1_.toFile()).copyTo(zipoutputstream);
               zipoutputstream.closeEntry();
               return FileVisitResult.CONTINUE;
            }
         });
         zipoutputstream.close();
      }

      return Files.size(path2);
   }

   @OnlyIn(Dist.CLIENT)
   String getName();

   ISaveHandler getSaveLoader(String p_197715_1_, @Nullable MinecraftServer p_197715_2_);

   @OnlyIn(Dist.CLIENT)
   List<WorldSummary> getSaveList() throws AnvilConverterException;

   @OnlyIn(Dist.CLIENT)
   void flushCache();

   @Nullable
   WorldInfo getWorldInfo(String p_75803_1_);

   @OnlyIn(Dist.CLIENT)
   boolean isNewLevelIdAcceptable(String p_207742_1_);

   @OnlyIn(Dist.CLIENT)
   boolean deleteWorldDirectory(String p_75802_1_);

   @OnlyIn(Dist.CLIENT)
   void renameWorld(String p_75806_1_, String p_75806_2_);

   @OnlyIn(Dist.CLIENT)
   boolean isConvertible(String p_207743_1_);

   boolean isOldMapFormat(String p_75801_1_);

   boolean convertMapFormat(String p_75805_1_, IProgressUpdate p_75805_2_);

   @OnlyIn(Dist.CLIENT)
   boolean canLoadWorld(String p_90033_1_);

   File getFile(String p_186352_1_, String p_186352_2_);

   @OnlyIn(Dist.CLIENT)
   Path getWorldFolder(String p_197714_1_);

   @OnlyIn(Dist.CLIENT)
   Path getBackupsFolder();
}
