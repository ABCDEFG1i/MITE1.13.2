package net.minecraft.resources;

import java.util.List;

public interface IReloadableResourceManager extends IResourceManager {
   void reload(List<IResourcePack> p_199005_1_);

   void addReloadListener(IResourceManagerReloadListener p_199006_1_);
}
