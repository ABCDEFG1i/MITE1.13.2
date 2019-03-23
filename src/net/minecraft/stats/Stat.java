package net.minecraft.stats;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Stat<T> extends ScoreCriteria {
   private final IStatFormater field_75976_b;
   private final T field_197922_p;
   private final StatType<T> field_197923_q;

   protected Stat(StatType<T> p_i47903_1_, T p_i47903_2_, IStatFormater p_i47903_3_) {
      super(func_197918_a(p_i47903_1_, p_i47903_2_));
      this.field_197923_q = p_i47903_1_;
      this.field_75976_b = p_i47903_3_;
      this.field_197922_p = p_i47903_2_;
   }

   public static <T> String func_197918_a(StatType<T> p_197918_0_, T p_197918_1_) {
      return func_197919_a(IRegistry.field_212634_w.func_177774_c(p_197918_0_)) + ":" + func_197919_a(p_197918_0_.func_199080_a().func_177774_c(p_197918_1_));
   }

   private static <T> String func_197919_a(@Nullable ResourceLocation p_197919_0_) {
      return p_197919_0_.toString().replace(':', '.');
   }

   public StatType<T> func_197921_a() {
      return this.field_197923_q;
   }

   public T func_197920_b() {
      return this.field_197922_p;
   }

   @OnlyIn(Dist.CLIENT)
   public String func_75968_a(int p_75968_1_) {
      return this.field_75976_b.format(p_75968_1_);
   }

   public boolean equals(Object p_equals_1_) {
      return this == p_equals_1_ || p_equals_1_ instanceof Stat && Objects.equals(this.func_96636_a(), ((Stat)p_equals_1_).func_96636_a());
   }

   public int hashCode() {
      return this.func_96636_a().hashCode();
   }

   public String toString() {
      return "Stat{name=" + this.func_96636_a() + ", formatter=" + this.field_75976_b + '}';
   }
}
