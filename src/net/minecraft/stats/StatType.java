package net.minecraft.stats;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StatType<T> implements Iterable<Stat<T>> {
   private final IRegistry<T> registry;
   private final Map<T, Stat<T>> map = new IdentityHashMap<>();

   public StatType(IRegistry<T> p_i49818_1_) {
      this.registry = p_i49818_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_199079_a(T p_199079_1_) {
      return this.map.containsKey(p_199079_1_);
   }

   public Stat<T> func_199077_a(T p_199077_1_, IStatFormater p_199077_2_) {
      return this.map.computeIfAbsent(p_199077_1_, (p_199075_2_) -> {
         return new Stat<>(this, p_199075_2_, p_199077_2_);
      });
   }

   public IRegistry<T> func_199080_a() {
      return this.registry;
   }

   @OnlyIn(Dist.CLIENT)
   public int size() {
      return this.map.size();
   }

   public Iterator<Stat<T>> iterator() {
      return this.map.values().iterator();
   }

   public Stat<T> func_199076_b(T p_199076_1_) {
      return this.func_199077_a(p_199076_1_, IStatFormater.DEFAULT);
   }

   @OnlyIn(Dist.CLIENT)
   public String func_199078_c() {
      return "stat_type." + IRegistry.field_212634_w.func_177774_c(this).toString().replace(':', '.');
   }
}
