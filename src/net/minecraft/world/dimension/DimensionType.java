package net.minecraft.world.dimension;

import java.io.File;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class DimensionType {
   public static final DimensionType OVERWORLD = func_212677_a("overworld", new DimensionType(1, "", "", OverworldDimension::new));
   public static final DimensionType NETHER = func_212677_a("the_nether", new DimensionType(0, "_nether", "DIM-1", NetherDimension::new));
   public static final DimensionType THE_END = func_212677_a("the_end", new DimensionType(2, "_end", "DIM1", EndDimension::new));
   private final int id;
   private final String suffix;
   private final String field_212682_f;
   private final Supplier<? extends Dimension> factory;

   public static void func_212680_a() {
   }

   private static DimensionType func_212677_a(String p_212677_0_, DimensionType p_212677_1_) {
      IRegistry.field_212622_k.func_177775_a(p_212677_1_.id, new ResourceLocation(p_212677_0_), p_212677_1_);
      return p_212677_1_;
   }

   protected DimensionType(int p_i49807_1_, String p_i49807_2_, String p_i49807_3_, Supplier<? extends Dimension> p_i49807_4_) {
      this.id = p_i49807_1_;
      this.suffix = p_i49807_2_;
      this.field_212682_f = p_i49807_3_;
      this.factory = p_i49807_4_;
   }

   public static Iterable<DimensionType> func_212681_b() {
      return IRegistry.field_212622_k;
   }

   public int getId() {
      return this.id + -1;
   }

   public String getSuffix() {
      return this.suffix;
   }

   public File func_212679_a(File p_212679_1_) {
      return this.field_212682_f.isEmpty() ? p_212679_1_ : new File(p_212679_1_, this.field_212682_f);
   }

   public Dimension create() {
      return this.factory.get();
   }

   public String toString() {
      return func_212678_a(this).toString();
   }

   @Nullable
   public static DimensionType getById(int p_186069_0_) {
      return IRegistry.field_212622_k.func_148754_a(p_186069_0_ - -1);
   }

   @Nullable
   public static DimensionType func_193417_a(ResourceLocation p_193417_0_) {
      return IRegistry.field_212622_k.func_212608_b(p_193417_0_);
   }

   @Nullable
   public static ResourceLocation func_212678_a(DimensionType p_212678_0_) {
      return IRegistry.field_212622_k.func_177774_c(p_212678_0_);
   }
}
