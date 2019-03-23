package net.minecraft.resources;

import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FallbackResourceManager implements IResourceManager {
   private static final Logger LOGGER = LogManager.getLogger();
   public final List<IResourcePack> resourcePacks = Lists.newArrayList();
   private final ResourcePackType type;

   public FallbackResourceManager(ResourcePackType p_i47906_1_) {
      this.type = p_i47906_1_;
   }

   public void addResourcePack(IResourcePack p_199021_1_) {
      this.resourcePacks.add(p_199021_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public Set<String> getResourceNamespaces() {
      return Collections.emptySet();
   }

   public IResource getResource(ResourceLocation p_199002_1_) throws IOException {
      this.func_199022_d(p_199002_1_);
      IResourcePack iresourcepack = null;
      ResourceLocation resourcelocation = func_199020_c(p_199002_1_);

      for(int i = this.resourcePacks.size() - 1; i >= 0; --i) {
         IResourcePack iresourcepack1 = this.resourcePacks.get(i);
         if (iresourcepack == null && iresourcepack1.resourceExists(this.type, resourcelocation)) {
            iresourcepack = iresourcepack1;
         }

         if (iresourcepack1.resourceExists(this.type, p_199002_1_)) {
            InputStream inputstream = null;
            if (iresourcepack != null) {
               inputstream = this.func_199019_a(resourcelocation, iresourcepack);
            }

            return new SimpleResource(iresourcepack1.getName(), p_199002_1_, this.func_199019_a(p_199002_1_, iresourcepack1), inputstream);
         }
      }

      throw new FileNotFoundException(p_199002_1_.toString());
   }

   protected InputStream func_199019_a(ResourceLocation p_199019_1_, IResourcePack p_199019_2_) throws IOException {
      InputStream inputstream = p_199019_2_.getResourceStream(this.type, p_199019_1_);
      return (InputStream)(LOGGER.isDebugEnabled() ? new FallbackResourceManager.LeakComplainerInputStream(inputstream, p_199019_1_, p_199019_2_.getName()) : inputstream);
   }

   private void func_199022_d(ResourceLocation p_199022_1_) throws IOException {
      if (p_199022_1_.getPath().contains("..")) {
         throw new IOException("Invalid relative path to resource: " + p_199022_1_);
      }
   }

   public List<IResource> getAllResources(ResourceLocation p_199004_1_) throws IOException {
      this.func_199022_d(p_199004_1_);
      List<IResource> list = Lists.newArrayList();
      ResourceLocation resourcelocation = func_199020_c(p_199004_1_);

      for(IResourcePack iresourcepack : this.resourcePacks) {
         if (iresourcepack.resourceExists(this.type, p_199004_1_)) {
            InputStream inputstream = iresourcepack.resourceExists(this.type, resourcelocation) ? this.func_199019_a(resourcelocation, iresourcepack) : null;
            list.add(new SimpleResource(iresourcepack.getName(), p_199004_1_, this.func_199019_a(p_199004_1_, iresourcepack), inputstream));
         }
      }

      if (list.isEmpty()) {
         throw new FileNotFoundException(p_199004_1_.toString());
      } else {
         return list;
      }
   }

   public Collection<ResourceLocation> getAllResourceLocations(String p_199003_1_, Predicate<String> p_199003_2_) {
      List<ResourceLocation> list = Lists.newArrayList();

      for(IResourcePack iresourcepack : this.resourcePacks) {
         list.addAll(iresourcepack.getAllResourceLocations(this.type, p_199003_1_, Integer.MAX_VALUE, p_199003_2_));
      }

      Collections.sort(list);
      return list;
   }

   static ResourceLocation func_199020_c(ResourceLocation p_199020_0_) {
      return new ResourceLocation(p_199020_0_.getNamespace(), p_199020_0_.getPath() + ".mcmeta");
   }

   static class LeakComplainerInputStream extends InputStream {
      private final InputStream field_198998_a;
      private final String field_198999_b;
      private boolean field_199000_c;

      public LeakComplainerInputStream(InputStream p_i47727_1_, ResourceLocation p_i47727_2_, String p_i47727_3_) {
         this.field_198998_a = p_i47727_1_;
         ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
         (new Exception()).printStackTrace(new PrintStream(bytearrayoutputstream));
         this.field_198999_b = "Leaked resource: '" + p_i47727_2_ + "' loaded from pack: '" + p_i47727_3_ + "'\n" + bytearrayoutputstream;
      }

      public void close() throws IOException {
         this.field_198998_a.close();
         this.field_199000_c = true;
      }

      protected void finalize() throws Throwable {
         if (!this.field_199000_c) {
            FallbackResourceManager.LOGGER.warn(this.field_198999_b);
         }

         super.finalize();
      }

      public int read() throws IOException {
         return this.field_198998_a.read();
      }
   }
}
