package net.minecraft.resources;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IResourceManager {
   @OnlyIn(Dist.CLIENT)
   Set<String> getResourceNamespaces();

   IResource getResource(ResourceLocation p_199002_1_) throws IOException;

   List<IResource> getAllResources(ResourceLocation p_199004_1_) throws IOException;

   Collection<ResourceLocation> getAllResourceLocations(String p_199003_1_, Predicate<String> p_199003_2_);
}
