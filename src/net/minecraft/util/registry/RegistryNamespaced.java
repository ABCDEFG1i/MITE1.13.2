package net.minecraft.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistryNamespaced<V> implements IRegistry<V> {
   protected static final Logger field_148743_a = LogManager.getLogger();
   protected final IntIdentityHashBiMap<V> underlyingIntegerMap = new IntIdentityHashBiMap<>(256);
   protected final BiMap<ResourceLocation, V> field_82596_a = HashBiMap.create();
   protected Object[] field_186802_b;
   private int nextFreeId;

   public void func_177775_a(int p_177775_1_, ResourceLocation p_177775_2_, V p_177775_3_) {
      this.underlyingIntegerMap.put(p_177775_3_, p_177775_1_);
      Validate.notNull(p_177775_2_);
      Validate.notNull(p_177775_3_);
      this.field_186802_b = null;
      if (this.field_82596_a.containsKey(p_177775_2_)) {
         field_148743_a.debug("Adding duplicate key '{}' to registry", p_177775_2_);
      }

      this.field_82596_a.put(p_177775_2_, p_177775_3_);
      if (this.nextFreeId <= p_177775_1_) {
         this.nextFreeId = p_177775_1_ + 1;
      }

   }

   public void func_82595_a(ResourceLocation p_82595_1_, V p_82595_2_) {
      this.func_177775_a(this.nextFreeId, p_82595_1_, p_82595_2_);
   }

   @Nullable
   public ResourceLocation func_177774_c(V p_177774_1_) {
      return this.field_82596_a.inverse().get(p_177774_1_);
   }

   public V func_82594_a(@Nullable ResourceLocation p_82594_1_) {
      throw new UnsupportedOperationException("No default value");
   }

   public ResourceLocation func_212609_b() {
      throw new UnsupportedOperationException("No default key");
   }

   public int func_148757_b(@Nullable V p_148757_1_) {
      return this.underlyingIntegerMap.getId(p_148757_1_);
   }

   @Nullable
   public V func_148754_a(int p_148754_1_) {
      return this.underlyingIntegerMap.get(p_148754_1_);
   }

   public Iterator<V> iterator() {
      return this.underlyingIntegerMap.iterator();
   }

   @Nullable
   public V func_212608_b(@Nullable ResourceLocation p_212608_1_) {
      return this.field_82596_a.get(p_212608_1_);
   }

   public Set<ResourceLocation> func_148742_b() {
      return Collections.unmodifiableSet(this.field_82596_a.keySet());
   }

   public boolean isEmpty() {
      return this.field_82596_a.isEmpty();
   }

   @Nullable
   public V func_186801_a(Random p_186801_1_) {
      if (this.field_186802_b == null) {
         Collection<?> collection = this.field_82596_a.values();
         if (collection.isEmpty()) {
            return null;
         }

         this.field_186802_b = collection.toArray(new Object[collection.size()]);
      }

      return (V)this.field_186802_b[p_186801_1_.nextInt(this.field_186802_b.length)];
   }

   public boolean func_212607_c(ResourceLocation p_212607_1_) {
      return this.field_82596_a.containsKey(p_212607_1_);
   }
}
