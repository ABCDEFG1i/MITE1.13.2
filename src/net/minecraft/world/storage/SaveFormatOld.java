package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveFormatOld implements ISaveFormat {
   private static final Logger LOGGER = LogManager.getLogger();
   public final Path savesDirectory;
   protected final Path field_197717_b;
   protected final DataFixer dataFixer;

   public SaveFormatOld(Path p_i49565_1_, Path p_i49565_2_, DataFixer p_i49565_3_) {
      this.dataFixer = p_i49565_3_;

      try {
         Files.createDirectories(Files.exists(p_i49565_1_) ? p_i49565_1_.toRealPath() : p_i49565_1_);
      } catch (IOException ioexception) {
         throw new RuntimeException(ioexception);
      }

      this.savesDirectory = p_i49565_1_;
      this.field_197717_b = p_i49565_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public String getName() {
      return "Old Format";
   }

   @OnlyIn(Dist.CLIENT)
   public List<WorldSummary> getSaveList() throws AnvilConverterException {
      List<WorldSummary> list = Lists.newArrayList();

      for(int i = 0; i < 5; ++i) {
         String s = "World" + (i + 1);
         WorldInfo worldinfo = this.getWorldInfo(s);
         if (worldinfo != null) {
            list.add(new WorldSummary(worldinfo, s, "", worldinfo.getSizeOnDisk(), false));
         }
      }

      return list;
   }

   @OnlyIn(Dist.CLIENT)
   public void flushCache() {
   }

   @Nullable
   public WorldInfo getWorldInfo(String p_75803_1_) {
      File file1 = new File(this.savesDirectory.toFile(), p_75803_1_);
      if (!file1.exists()) {
         return null;
      } else {
         File file2 = new File(file1, "level.dat");
         if (file2.exists()) {
            WorldInfo worldinfo = getWorldData(file2, this.dataFixer);
            if (worldinfo != null) {
               return worldinfo;
            }
         }

         file2 = new File(file1, "level.dat_old");
         return file2.exists() ? getWorldData(file2, this.dataFixer) : null;
      }
   }

   @Nullable
   public static WorldInfo getWorldData(File p_186353_0_, DataFixer p_186353_1_) {
      try {
         NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(p_186353_0_));
         NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("Data");
         NBTTagCompound nbttagcompound2 = nbttagcompound1.hasKey("Player", 10) ? nbttagcompound1.getCompoundTag("Player") : null;
         nbttagcompound1.removeTag("Player");
         int i = nbttagcompound1.hasKey("DataVersion", 99) ? nbttagcompound1.getInteger("DataVersion") : -1;
         return new WorldInfo(NBTUtil.func_210822_a(p_186353_1_, DataFixTypes.LEVEL, nbttagcompound1, i), p_186353_1_, i, nbttagcompound2);
      } catch (Exception exception) {
         LOGGER.error("Exception reading {}", p_186353_0_, exception);
         return null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void renameWorld(String p_75806_1_, String p_75806_2_) {
      File file1 = new File(this.savesDirectory.toFile(), p_75806_1_);
      if (file1.exists()) {
         File file2 = new File(file1, "level.dat");
         if (file2.exists()) {
            try {
               NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file2));
               NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("Data");
               nbttagcompound1.setString("LevelName", p_75806_2_);
               CompressedStreamTools.writeCompressed(nbttagcompound, new FileOutputStream(file2));
            } catch (Exception exception) {
               exception.printStackTrace();
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isNewLevelIdAcceptable(String p_207742_1_) {
      File file1 = new File(this.savesDirectory.toFile(), p_207742_1_);
      if (file1.exists()) {
         return false;
      } else {
         try {
            file1.mkdir();
            file1.delete();
            return true;
         } catch (Throwable throwable) {
            LOGGER.warn("Couldn't make new level", throwable);
            return false;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean deleteWorldDirectory(String p_75802_1_) {
      File file1 = new File(this.savesDirectory.toFile(), p_75802_1_);
      if (!file1.exists()) {
         return true;
      } else {
         LOGGER.info("Deleting level {}", p_75802_1_);

         for(int i = 1; i <= 5; ++i) {
            LOGGER.info("Attempt {}...", i);
            if (deleteFiles(file1.listFiles())) {
               break;
            }

            LOGGER.warn("Unsuccessful in deleting contents.");
            if (i < 5) {
               try {
                  Thread.sleep(500L);
               } catch (InterruptedException var5) {
               }
            }
         }

         return file1.delete();
      }
   }

   @OnlyIn(Dist.CLIENT)
   protected static boolean deleteFiles(File[] p_75807_0_) {
      for(File file1 : p_75807_0_) {
         LOGGER.debug("Deleting {}", file1);
         if (file1.isDirectory() && !deleteFiles(file1.listFiles())) {
            LOGGER.warn("Couldn't delete directory {}", file1);
            return false;
         }

         if (!file1.delete()) {
            LOGGER.warn("Couldn't delete file {}", file1);
            return false;
         }
      }

      return true;
   }

   public ISaveHandler getSaveLoader(String p_197715_1_, @Nullable MinecraftServer p_197715_2_) {
      return new SaveHandler(this.savesDirectory.toFile(), p_197715_1_, p_197715_2_, this.dataFixer);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isConvertible(String p_207743_1_) {
      return false;
   }

   public boolean isOldMapFormat(String p_75801_1_) {
      return false;
   }

   public boolean convertMapFormat(String p_75805_1_, IProgressUpdate p_75805_2_) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canLoadWorld(String p_90033_1_) {
      return Files.isDirectory(this.savesDirectory.resolve(p_90033_1_));
   }

   public File getFile(String p_186352_1_, String p_186352_2_) {
      return this.savesDirectory.resolve(p_186352_1_).resolve(p_186352_2_).toFile();
   }

   @OnlyIn(Dist.CLIENT)
   public Path getWorldFolder(String p_197714_1_) {
      return this.savesDirectory.resolve(p_197714_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public Path getBackupsFolder() {
      return this.field_197717_b;
   }
}
