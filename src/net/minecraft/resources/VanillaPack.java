package net.minecraft.resources;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VanillaPack implements IResourcePack {
   public static Path field_199754_a;
   private static final Logger LOGGER = LogManager.getLogger();
   public static Class<?> field_211688_b;
   public final Set<String> resourceNamespaces;

   public VanillaPack(String... p_i47912_1_) {
      this.resourceNamespaces = ImmutableSet.copyOf(p_i47912_1_);
   }

   public InputStream getRootResourceStream(String p_195763_1_) throws IOException {
      if (!p_195763_1_.contains("/") && !p_195763_1_.contains("\\")) {
         if (field_199754_a != null) {
            Path path = field_199754_a.resolve(p_195763_1_);
            if (Files.exists(path)) {
               return Files.newInputStream(path);
            }
         }

         return this.func_200010_a(p_195763_1_);
      } else {
         throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
      }
   }

   public InputStream getResourceStream(ResourcePackType p_195761_1_, ResourceLocation p_195761_2_) throws IOException {
      InputStream inputstream = this.func_195782_c(p_195761_1_, p_195761_2_);
      if (inputstream != null) {
         return inputstream;
      } else {
         throw new FileNotFoundException(p_195761_2_.getPath());
      }
   }

   public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType p_195758_1_, String p_195758_2_, int p_195758_3_, Predicate<String> p_195758_4_) {
      Set<ResourceLocation> set = Sets.newHashSet();
      if (field_199754_a != null) {
         try {
            set.addAll(this.func_195781_a(p_195758_3_, "minecraft", field_199754_a.resolve(p_195758_1_.getDirectoryName()).resolve("minecraft"), p_195758_2_, p_195758_4_));
         } catch (IOException var26) {
         }

         if (p_195758_1_ == ResourcePackType.CLIENT_RESOURCES) {
            Enumeration<URL> enumeration = null;

            try {
               enumeration = field_211688_b.getClassLoader().getResources(p_195758_1_.getDirectoryName() + "/minecraft");
            } catch (IOException var25) {
            }

            while(enumeration != null && enumeration.hasMoreElements()) {
               try {
                  URI uri = enumeration.nextElement().toURI();
                  if ("file".equals(uri.getScheme())) {
                     set.addAll(this.func_195781_a(p_195758_3_, "minecraft", Paths.get(uri), p_195758_2_, p_195758_4_));
                  }
               } catch (IOException | URISyntaxException var24) {
               }
            }
         }
      }

      try {
         URL url1 = VanillaPack.class.getResource("/" + p_195758_1_.getDirectoryName() + "/.mcassetsroot");
         if (url1 == null) {
            LOGGER.error("Couldn't find .mcassetsroot, cannot load vanilla resources");
            return set;
         }

         URI uri1 = url1.toURI();
         if ("file".equals(uri1.getScheme())) {
            URL url = new URL(url1.toString().substring(0, url1.toString().length() - ".mcassetsroot".length()) + "minecraft");
            if (url == null) {
               return set;
            }

            Path path = Paths.get(url.toURI());
            set.addAll(this.func_195781_a(p_195758_3_, "minecraft", path, p_195758_2_, p_195758_4_));
         } else if ("jar".equals(uri1.getScheme())) {
            try (FileSystem filesystem = FileSystems.newFileSystem(uri1, Collections.emptyMap())) {
               Path path1 = filesystem.getPath("/" + p_195758_1_.getDirectoryName() + "/minecraft");
               set.addAll(this.func_195781_a(p_195758_3_, "minecraft", path1, p_195758_2_, p_195758_4_));
            }
         } else {
            LOGGER.error("Unsupported scheme {} trying to list vanilla resources (NYI?)", uri1);
         }
      } catch (NoSuchFileException | FileNotFoundException var28) {
      } catch (IOException | URISyntaxException urisyntaxexception) {
         LOGGER.error("Couldn't get a list of all vanilla resources", urisyntaxexception);
      }

      return set;
   }

   private Collection<ResourceLocation> func_195781_a(int p_195781_1_, String p_195781_2_, Path p_195781_3_, String p_195781_4_, Predicate<String> p_195781_5_) throws IOException {
      List<ResourceLocation> list = Lists.newArrayList();
      Iterator<Path> iterator = Files.walk(p_195781_3_.resolve(p_195781_4_), p_195781_1_).iterator();

      while(iterator.hasNext()) {
         Path path = iterator.next();
         if (!path.endsWith(".mcmeta") && Files.isRegularFile(path) && p_195781_5_.test(path.getFileName().toString())) {
            list.add(new ResourceLocation(p_195781_2_, p_195781_3_.relativize(path).toString().replaceAll("\\\\", "/")));
         }
      }

      return list;
   }

   @Nullable
   protected InputStream func_195782_c(ResourcePackType p_195782_1_, ResourceLocation p_195782_2_) {
      String s = "/" + p_195782_1_.getDirectoryName() + "/" + p_195782_2_.getNamespace() + "/" + p_195782_2_.getPath();
      if (field_199754_a != null) {
         Path path = field_199754_a.resolve(p_195782_1_.getDirectoryName() + "/" + p_195782_2_.getNamespace() + "/" + p_195782_2_.getPath());
         if (Files.exists(path)) {
            try {
               return Files.newInputStream(path);
            } catch (IOException var7) {
            }
         }
      }

      try {
         URL url = VanillaPack.class.getResource(s);
         return url != null && FolderPack.func_195777_a(new File(url.getFile()), s) ? VanillaPack.class.getResourceAsStream(s) : null;
      } catch (IOException var6) {
         return VanillaPack.class.getResourceAsStream(s);
      }
   }

   @Nullable
   protected InputStream func_200010_a(String p_200010_1_) {
      return VanillaPack.class.getResourceAsStream("/" + p_200010_1_);
   }

   public boolean resourceExists(ResourcePackType p_195764_1_, ResourceLocation p_195764_2_) {
      InputStream inputstream = this.func_195782_c(p_195764_1_, p_195764_2_);
      boolean flag = inputstream != null;
      IOUtils.closeQuietly(inputstream);
      return flag;
   }

   public Set<String> getResourceNamespaces(ResourcePackType p_195759_1_) {
      return this.resourceNamespaces;
   }

   @Nullable
   public <T> T getMetadata(IMetadataSectionSerializer<T> p_195760_1_) throws IOException {
      try (InputStream inputstream = this.getRootResourceStream("pack.mcmeta")) {
         Object object = AbstractResourcePack.getResourceMetadata(p_195760_1_, inputstream);
         return (T)object;
      } catch (FileNotFoundException | RuntimeException var16) {
         return null;
      }
   }

   public String getName() {
      return "Default";
   }

   public void close() {
   }
}
