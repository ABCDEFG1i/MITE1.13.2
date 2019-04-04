package net.minecraft.resources;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleReloadableResourceManager implements IReloadableResourceManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<String, FallbackResourceManager> namespaceResourceManagers = Maps.newHashMap();
   private final List<IResourceManagerReloadListener> reloadListeners = Lists.newArrayList();
   private final Set<String> resourceNamespaces = Sets.newLinkedHashSet();
   private final ResourcePackType type;

   public SimpleReloadableResourceManager(ResourcePackType p_i47905_1_) {
      this.type = p_i47905_1_;
   }

   public void addResourcePack(IResourcePack p_199009_1_) {
      for(String s : p_199009_1_.getResourceNamespaces(this.type)) {
         this.resourceNamespaces.add(s);
         FallbackResourceManager fallbackresourcemanager = this.namespaceResourceManagers.get(s);
         if (fallbackresourcemanager == null) {
            fallbackresourcemanager = new FallbackResourceManager(this.type);
            this.namespaceResourceManagers.put(s, fallbackresourcemanager);
         }

         fallbackresourcemanager.addResourcePack(p_199009_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public Set<String> getResourceNamespaces() {
      return this.resourceNamespaces;
   }

   public IResource getResource(ResourceLocation p_199002_1_) throws IOException {
      IResourceManager iresourcemanager = this.namespaceResourceManagers.get(p_199002_1_.getNamespace());
      if (iresourcemanager != null) {
         return iresourcemanager.getResource(p_199002_1_);
      } else {
         throw new FileNotFoundException(p_199002_1_.toString());
      }
   }

   public List<IResource> getAllResources(ResourceLocation p_199004_1_) throws IOException {
      IResourceManager iresourcemanager = this.namespaceResourceManagers.get(p_199004_1_.getNamespace());
      if (iresourcemanager != null) {
         return iresourcemanager.getAllResources(p_199004_1_);
      } else {
         throw new FileNotFoundException(p_199004_1_.toString());
      }
   }

   public Collection<ResourceLocation> getAllResourceLocations(String p_199003_1_, Predicate<String> p_199003_2_) {
      Set<ResourceLocation> set = Sets.newHashSet();

      for(FallbackResourceManager fallbackresourcemanager : this.namespaceResourceManagers.values()) {
         set.addAll(fallbackresourcemanager.getAllResourceLocations(p_199003_1_, p_199003_2_));
      }

      List<ResourceLocation> list = Lists.newArrayList(set);
      Collections.sort(list);
      return list;
   }

   private void clearResourceNamespaces() {
      this.namespaceResourceManagers.clear();
      this.resourceNamespaces.clear();
   }

   public void reload(List<IResourcePack> p_199005_1_) {
      this.clearResourceNamespaces();
      LOGGER.info("Reloading ResourceManager: {}", p_199005_1_.stream().map(IResourcePack::getName).collect(Collectors.joining(", ")));

      for(IResourcePack iresourcepack : p_199005_1_) {
         this.addResourcePack(iresourcepack);
      }

      if (LOGGER.isDebugEnabled()) {
         this.reloadAllResourcesDebug();
      } else {
         this.triggerReloadListeners();
      }

   }

   public void addReloadListener(IResourceManagerReloadListener p_199006_1_) {
      this.reloadListeners.add(p_199006_1_);
      if (LOGGER.isDebugEnabled()) {
         LOGGER.info(this.reloadResourcesFor(p_199006_1_));
      } else {
         p_199006_1_.onResourceManagerReload(this);
      }

   }

   private void triggerReloadListeners() {
      for(IResourceManagerReloadListener iresourcemanagerreloadlistener : this.reloadListeners) {
         iresourcemanagerreloadlistener.onResourceManagerReload(this);
      }

   }

   private void reloadAllResourcesDebug() {
      LOGGER.info("Reloading all resources! {} listeners to update.", this.reloadListeners.size());
      List<String> list = Lists.newArrayList();
      Stopwatch stopwatch = Stopwatch.createStarted();

      for(IResourceManagerReloadListener iresourcemanagerreloadlistener : this.reloadListeners) {
         list.add(this.reloadResourcesFor(iresourcemanagerreloadlistener));
      }

      stopwatch.stop();
      LOGGER.info("----");
      LOGGER.info("Complete resource reload took {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));

      for(String s : list) {
         LOGGER.info(s);
      }

      LOGGER.info("----");
   }

   private String reloadResourcesFor(IResourceManagerReloadListener p_199007_1_) {
      Stopwatch stopwatch = Stopwatch.createStarted();
      p_199007_1_.onResourceManagerReload(this);
      stopwatch.stop();
      return "Resource reload for " + p_199007_1_.getClass().getSimpleName() + " took " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms";
   }
}
