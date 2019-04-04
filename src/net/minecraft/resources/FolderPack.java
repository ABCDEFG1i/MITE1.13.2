package net.minecraft.resources;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.Util;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FolderPack extends AbstractResourcePack {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final boolean field_195779_b = Util.getOSType() == Util.EnumOS.WINDOWS;
   private static final CharMatcher field_195780_c = CharMatcher.is('\\');

   public FolderPack(File p_i47914_1_) {
      super(p_i47914_1_);
   }

   public static boolean func_195777_a(File p_195777_0_, String p_195777_1_) throws IOException {
      String s = p_195777_0_.getCanonicalPath();
      if (field_195779_b) {
         s = field_195780_c.replaceFrom(s, '/');
      }

      return s.endsWith(p_195777_1_);
   }

   protected InputStream getInputStream(String p_195766_1_) throws IOException {
      File file1 = this.func_195776_e(p_195766_1_);
      if (file1 == null) {
         throw new ResourcePackFileNotFoundException(this.file, p_195766_1_);
      } else {
         return new FileInputStream(file1);
      }
   }

   protected boolean resourceExists(String p_195768_1_) {
      return this.func_195776_e(p_195768_1_) != null;
   }

   @Nullable
   private File func_195776_e(String p_195776_1_) {
      try {
         File file1 = new File(this.file, p_195776_1_);
         if (file1.isFile() && func_195777_a(file1, p_195776_1_)) {
            return file1;
         }
      } catch (IOException var3) {
      }

      return null;
   }

   public Set<String> getResourceNamespaces(ResourcePackType p_195759_1_) {
      Set<String> set = Sets.newHashSet();
      File file1 = new File(this.file, p_195759_1_.getDirectoryName());
      File[] afile = file1.listFiles((FileFilter)DirectoryFileFilter.DIRECTORY);
      if (afile != null) {
         for(File file2 : afile) {
            String s = getRelativeString(file1, file2);
            if (s.equals(s.toLowerCase(Locale.ROOT))) {
               set.add(s.substring(0, s.length() - 1));
            } else {
               this.onIgnoreNonLowercaseNamespace(s);
            }
         }
      }

      return set;
   }

   public void close() throws IOException {
   }

   public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType p_195758_1_, String p_195758_2_, int p_195758_3_, Predicate<String> p_195758_4_) {
      File file1 = new File(this.file, p_195758_1_.getDirectoryName());
      List<ResourceLocation> list = Lists.newArrayList();

      for(String s : this.getResourceNamespaces(p_195758_1_)) {
         this.func_199546_a(new File(new File(file1, s), p_195758_2_), p_195758_3_, s, list, p_195758_2_ + "/", p_195758_4_);
      }

      return list;
   }

   private void func_199546_a(File p_199546_1_, int p_199546_2_, String p_199546_3_, List<ResourceLocation> p_199546_4_, String p_199546_5_, Predicate<String> p_199546_6_) {
      File[] afile = p_199546_1_.listFiles();
      if (afile != null) {
         for(File file1 : afile) {
            if (file1.isDirectory()) {
               if (p_199546_2_ > 0) {
                  this.func_199546_a(file1, p_199546_2_ - 1, p_199546_3_, p_199546_4_, p_199546_5_ + file1.getName() + "/", p_199546_6_);
               }
            } else if (!file1.getName().endsWith(".mcmeta") && p_199546_6_.test(file1.getName())) {
               try {
                  p_199546_4_.add(new ResourceLocation(p_199546_3_, p_199546_5_ + file1.getName()));
               } catch (ResourceLocationException resourcelocationexception) {
                  LOGGER.error(resourcelocationexception.getMessage());
               }
            }
         }
      }

   }
}
