package net.minecraft.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public class FilePack extends AbstractResourcePack {
   public static final Splitter PATH_SPLITTER = Splitter.on('/').omitEmptyStrings().limit(3);
   private ZipFile zipFile;

   public FilePack(File p_i47915_1_) {
      super(p_i47915_1_);
   }

   private ZipFile func_195773_b() throws IOException {
      if (this.zipFile == null) {
         this.zipFile = new ZipFile(this.file);
      }

      return this.zipFile;
   }

   protected InputStream getInputStream(String p_195766_1_) throws IOException {
      ZipFile zipfile = this.func_195773_b();
      ZipEntry zipentry = zipfile.getEntry(p_195766_1_);
      if (zipentry == null) {
         throw new ResourcePackFileNotFoundException(this.file, p_195766_1_);
      } else {
         return zipfile.getInputStream(zipentry);
      }
   }

   public boolean resourceExists(String p_195768_1_) {
      try {
         return this.func_195773_b().getEntry(p_195768_1_) != null;
      } catch (IOException var3) {
         return false;
      }
   }

   public Set<String> getResourceNamespaces(ResourcePackType p_195759_1_) {
      ZipFile zipfile;
      try {
         zipfile = this.func_195773_b();
      } catch (IOException var9) {
         return Collections.emptySet();
      }

      Enumeration<? extends ZipEntry> enumeration = zipfile.entries();
      Set<String> set = Sets.newHashSet();

      while(enumeration.hasMoreElements()) {
         ZipEntry zipentry = enumeration.nextElement();
         String s = zipentry.getName();
         if (s.startsWith(p_195759_1_.getDirectoryName() + "/")) {
            List<String> list = Lists.newArrayList(PATH_SPLITTER.split(s));
            if (list.size() > 1) {
               String s1 = list.get(1);
               if (s1.equals(s1.toLowerCase(Locale.ROOT))) {
                  set.add(s1);
               } else {
                  this.onIgnoreNonLowercaseNamespace(s1);
               }
            }
         }
      }

      return set;
   }

   protected void finalize() throws Throwable {
      this.close();
      super.finalize();
   }

   public void close() {
      if (this.zipFile != null) {
         IOUtils.closeQuietly(this.zipFile);
         this.zipFile = null;
      }

   }

   public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType p_195758_1_, String p_195758_2_, int p_195758_3_, Predicate<String> p_195758_4_) {
      ZipFile zipfile;
      try {
         zipfile = this.func_195773_b();
      } catch (IOException var15) {
         return Collections.emptySet();
      }

      Enumeration<? extends ZipEntry> enumeration = zipfile.entries();
      List<ResourceLocation> list = Lists.newArrayList();
      String s = p_195758_1_.getDirectoryName() + "/";

      while(enumeration.hasMoreElements()) {
         ZipEntry zipentry = enumeration.nextElement();
         if (!zipentry.isDirectory() && zipentry.getName().startsWith(s)) {
            String s1 = zipentry.getName().substring(s.length());
            if (!s1.endsWith(".mcmeta")) {
               int i = s1.indexOf(47);
               if (i >= 0) {
                  String s2 = s1.substring(i + 1);
                  if (s2.startsWith(p_195758_2_ + "/")) {
                     String[] astring = s2.substring(p_195758_2_.length() + 2).split("/");
                     if (astring.length >= p_195758_3_ + 1 && p_195758_4_.test(s2)) {
                        String s3 = s1.substring(0, i);
                        list.add(new ResourceLocation(s3, s2));
                     }
                  }
               }
            }
         }
      }

      return list;
   }
}
