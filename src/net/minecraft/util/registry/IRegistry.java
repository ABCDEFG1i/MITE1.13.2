package net.minecraft.util.registry;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.stats.StatType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IObjectIntIterable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGeneratorType;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface IRegistry<T> extends IObjectIntIterable<T> {
   Logger field_212616_e = LogManager.getLogger();
   IRegistry<IRegistry<?>> field_212617_f = new RegistryNamespaced<>();
   IRegistry<Block> field_212618_g = func_212610_a("block", new RegistryNamespacedDefaultedByKey<>(new ResourceLocation("air")));
   IRegistry<Fluid> field_212619_h = func_212610_a("fluid", new RegistryNamespacedDefaultedByKey<>(new ResourceLocation("empty")));
   IRegistry<PaintingType> field_212620_i = func_212610_a("motive", new RegistryNamespacedDefaultedByKey<>(new ResourceLocation("kebab")));
   IRegistry<PotionType> field_212621_j = func_212610_a("potion", new RegistryNamespacedDefaultedByKey<>(new ResourceLocation("empty")));
   IRegistry<DimensionType> field_212622_k = func_212610_a("dimension_type", new RegistryNamespaced<>());
   IRegistry<ResourceLocation> field_212623_l = func_212610_a("custom_stat", new RegistryNamespaced<>());
   IRegistry<Biome> field_212624_m = func_212610_a("biome", new RegistryNamespaced<>());
   IRegistry<BiomeProviderType<?, ?>> field_212625_n = func_212610_a("biome_source_type", new RegistryNamespaced<>());
   IRegistry<TileEntityType<?>> field_212626_o = func_212610_a("block_entity_type", new RegistryNamespaced<>());
   IRegistry<ChunkGeneratorType<?, ?>> field_212627_p = func_212610_a("chunk_generator_type", new RegistryNamespaced<>());
   IRegistry<Enchantment> field_212628_q = func_212610_a("enchantment", new RegistryNamespaced<>());
   IRegistry<EntityType<?>> field_212629_r = func_212610_a("entity_type", new RegistryNamespaced<>());
   IRegistry<Item> field_212630_s = func_212610_a("item", new RegistryNamespaced<>());
   IRegistry<Potion> field_212631_t = func_212610_a("mob_effect", new RegistryNamespaced<>());
   IRegistry<ParticleType<? extends IParticleData>> field_212632_u = func_212610_a("particle_type", new RegistryNamespaced<>());
   IRegistry<SoundEvent> field_212633_v = func_212610_a("sound_event", new RegistryNamespaced<>());
   IRegistry<StatType<?>> field_212634_w = func_212610_a("stats", new RegistryNamespaced<>());

   static <T> IRegistry<T> func_212610_a(String p_212610_0_, IRegistry<T> p_212610_1_) {
      field_212617_f.func_82595_a(new ResourceLocation(p_212610_0_), p_212610_1_);
      return p_212610_1_;
   }

   static void func_212613_e() {
      field_212617_f.forEach((p_212606_0_) -> {
         if (p_212606_0_.isEmpty()) {
            field_212616_e.error("Registry '{}' was empty after loading", (Object)field_212617_f.func_177774_c(p_212606_0_));
            if (SharedConstants.developmentMode) {
               throw new IllegalStateException("Registry: '" + field_212617_f.func_177774_c(p_212606_0_) + "' is empty, not allowed, fix me!");
            }
         }

         if (p_212606_0_ instanceof RegistryNamespacedDefaultedByKey) {
            ResourceLocation resourcelocation = p_212606_0_.func_212609_b();
            Validate.notNull(p_212606_0_.func_212608_b(resourcelocation), "Missing default of DefaultedMappedRegistry: " + resourcelocation);
         }

      });
   }

   @Nullable
   ResourceLocation func_177774_c(T p_177774_1_);

   T func_82594_a(@Nullable ResourceLocation p_82594_1_);

   ResourceLocation func_212609_b();

   int func_148757_b(@Nullable T p_148757_1_);

   @Nullable
   T func_148754_a(int p_148754_1_);

   Iterator<T> iterator();

   @Nullable
   T func_212608_b(@Nullable ResourceLocation p_212608_1_);

   void func_177775_a(int p_177775_1_, ResourceLocation p_177775_2_, T p_177775_3_);

   void func_82595_a(ResourceLocation p_82595_1_, T p_82595_2_);

   Set<ResourceLocation> func_148742_b();

   boolean isEmpty();

   @Nullable
   T func_186801_a(Random p_186801_1_);

   default Stream<T> stream() {
      return StreamSupport.stream(this.spliterator(), false);
   }

   boolean func_212607_c(ResourceLocation p_212607_1_);
}
