package net.minecraft.resources;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IResourcePack extends Closeable {
   @OnlyIn(Dist.CLIENT)
   InputStream getRootResourceStream(String p_195763_1_) throws IOException;

   InputStream getResourceStream(ResourcePackType p_195761_1_, ResourceLocation p_195761_2_) throws IOException;

   Collection<ResourceLocation> getAllResourceLocations(ResourcePackType p_195758_1_, String p_195758_2_, int p_195758_3_, Predicate<String> p_195758_4_);

   boolean resourceExists(ResourcePackType p_195764_1_, ResourceLocation p_195764_2_);

   Set<String> getResourceNamespaces(ResourcePackType p_195759_1_);

   @Nullable
   <T> T getMetadata(IMetadataSectionSerializer<T> p_195760_1_) throws IOException;

   String getName();
}
