package net.minecraft.tags;

import java.util.Collection;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;

public class FluidTags {
   private static TagCollection<Fluid> collection = new TagCollection<>((p_206955_0_) -> {
      return false;
   }, (p_206957_0_) -> {
      return null;
   }, "", false, "");
   private static int generation;
   public static final Tag<Fluid> WATER = makeWrapperTag("water");
   public static final Tag<Fluid> LAVA = makeWrapperTag("lava");

   public static void setCollection(TagCollection<Fluid> p_206953_0_) {
      collection = p_206953_0_;
      ++generation;
   }

   private static Tag<Fluid> makeWrapperTag(String p_206956_0_) {
      return new FluidTags.Wrapper(new ResourceLocation(p_206956_0_));
   }

   public static class Wrapper extends Tag<Fluid> {
      private int lastKnownGeneration = -1;
      private Tag<Fluid> cachedTag;

      public Wrapper(ResourceLocation p_i49117_1_) {
         super(p_i49117_1_);
      }

      public boolean contains(Fluid p_199685_1_) {
         if (this.lastKnownGeneration != FluidTags.generation) {
            this.cachedTag = FluidTags.collection.getOrCreate(this.getId());
            this.lastKnownGeneration = FluidTags.generation;
         }

         return this.cachedTag.contains(p_199685_1_);
      }

      public Collection<Fluid> getAllElements() {
         if (this.lastKnownGeneration != FluidTags.generation) {
            this.cachedTag = FluidTags.collection.getOrCreate(this.getId());
            this.lastKnownGeneration = FluidTags.generation;
         }

         return this.cachedTag.getAllElements();
      }

      public Collection<Tag.ITagEntry<Fluid>> getEntries() {
         if (this.lastKnownGeneration != FluidTags.generation) {
            this.cachedTag = FluidTags.collection.getOrCreate(this.getId());
            this.lastKnownGeneration = FluidTags.generation;
         }

         return this.cachedTag.getEntries();
      }
   }
}
