package net.minecraft.resources;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;
import java.util.function.Supplier;

public class FolderPackFinder implements IPackFinder {
   private static final FileFilter FILE_FILTER = (p_195731_0_) -> {
      boolean flag = p_195731_0_.isFile() && p_195731_0_.getName().endsWith(".zip");
      boolean flag1 = p_195731_0_.isDirectory() && (new File(p_195731_0_, "pack.mcmeta")).isFile();
      return flag || flag1;
   };
   private final File folder;

   public FolderPackFinder(File p_i47911_1_) {
      this.folder = p_i47911_1_;
   }

   public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> p_195730_1_, ResourcePackInfo.IFactory<T> p_195730_2_) {
      if (!this.folder.isDirectory()) {
         this.folder.mkdirs();
      }

      File[] afile = this.folder.listFiles(FILE_FILTER);
      if (afile != null) {
         for(File file1 : afile) {
            String s = "file/" + file1.getName();
            T t = ResourcePackInfo.func_195793_a(s, false, this.makePackSupplier(file1), p_195730_2_, ResourcePackInfo.Priority.TOP);
            if (t != null) {
               p_195730_1_.put(s, t);
            }
         }

      }
   }

   private Supplier<IResourcePack> makePackSupplier(File p_195733_1_) {
      return p_195733_1_.isDirectory() ? () -> {
         return new FolderPack(p_195733_1_);
      } : () -> {
         return new FilePack(p_195733_1_);
      };
   }
}
