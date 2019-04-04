package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.text.TextComponentTranslation;

public abstract class MinMaxBounds<T extends Number> {
   public static final SimpleCommandExceptionType field_196978_b = new SimpleCommandExceptionType(new TextComponentTranslation("argument.range.empty"));
   public static final SimpleCommandExceptionType field_196980_d = new SimpleCommandExceptionType(new TextComponentTranslation("argument.range.swapped"));
   protected final T min;
   protected final T max;

   protected MinMaxBounds(@Nullable T p_i49720_1_, @Nullable T p_i49720_2_) {
      this.min = p_i49720_1_;
      this.max = p_i49720_2_;
   }

   @Nullable
   public T func_196973_a() {
      return this.min;
   }

   @Nullable
   public T func_196977_b() {
      return this.max;
   }

   public boolean isUnbounded() {
      return this.min == null && this.max == null;
   }

   public JsonElement serialize() {
      if (this.isUnbounded()) {
         return JsonNull.INSTANCE;
      } else if (this.min != null && this.min.equals(this.max)) {
         return new JsonPrimitive(this.min);
      } else {
         JsonObject jsonobject = new JsonObject();
         if (this.min != null) {
            jsonobject.addProperty("min", this.min);
         }

         if (this.max != null) {
            jsonobject.addProperty("max", this.min);
         }

         return jsonobject;
      }
   }

   protected static <T extends Number, R extends MinMaxBounds<T>> R func_211331_a(@Nullable JsonElement p_211331_0_, R p_211331_1_, BiFunction<JsonElement, String, T> p_211331_2_, MinMaxBounds.IBoundFactory<T, R> p_211331_3_) {
      if (p_211331_0_ != null && !p_211331_0_.isJsonNull()) {
         if (JsonUtils.isNumber(p_211331_0_)) {
            T t2 = p_211331_2_.apply(p_211331_0_, "value");
            return p_211331_3_.create(t2, t2);
         } else {
            JsonObject jsonobject = JsonUtils.getJsonObject(p_211331_0_, "value");
            T t = jsonobject.has("min") ? p_211331_2_.apply(jsonobject.get("min"), "min") : null;
            T t1 = jsonobject.has("max") ? p_211331_2_.apply(jsonobject.get("max"), "max") : null;
            return p_211331_3_.create(t, t1);
         }
      } else {
         return p_211331_1_;
      }
   }

   protected static <T extends Number, R extends MinMaxBounds<T>> R func_211337_a(StringReader p_211337_0_, MinMaxBounds.IBoundReader<T, R> p_211337_1_, Function<String, T> p_211337_2_, Supplier<DynamicCommandExceptionType> p_211337_3_, Function<T, T> p_211337_4_) throws CommandSyntaxException {
      if (!p_211337_0_.canRead()) {
         throw field_196978_b.createWithContext(p_211337_0_);
      } else {
         int i = p_211337_0_.getCursor();

         try {
            T t = func_196972_a(func_196975_b(p_211337_0_, p_211337_2_, p_211337_3_), p_211337_4_);
            T t1;
            if (p_211337_0_.canRead(2) && p_211337_0_.peek() == '.' && p_211337_0_.peek(1) == '.') {
               p_211337_0_.skip();
               p_211337_0_.skip();
               t1 = func_196972_a(func_196975_b(p_211337_0_, p_211337_2_, p_211337_3_), p_211337_4_);
               if (t == null && t1 == null) {
                  throw field_196978_b.createWithContext(p_211337_0_);
               }
            } else {
               t1 = t;
            }

            if (t == null && t1 == null) {
               throw field_196978_b.createWithContext(p_211337_0_);
            } else {
               return p_211337_1_.create(p_211337_0_, t, t1);
            }
         } catch (CommandSyntaxException commandsyntaxexception) {
            p_211337_0_.setCursor(i);
            throw new CommandSyntaxException(commandsyntaxexception.getType(), commandsyntaxexception.getRawMessage(), commandsyntaxexception.getInput(), i);
         }
      }
   }

   @Nullable
   private static <T extends Number> T func_196975_b(StringReader p_196975_0_, Function<String, T> p_196975_1_, Supplier<DynamicCommandExceptionType> p_196975_2_) throws CommandSyntaxException {
      int i = p_196975_0_.getCursor();

      while(p_196975_0_.canRead() && func_196970_c(p_196975_0_)) {
         p_196975_0_.skip();
      }

      String s = p_196975_0_.getString().substring(i, p_196975_0_.getCursor());
      if (s.isEmpty()) {
         return null;
      } else {
         try {
            return p_196975_1_.apply(s);
         } catch (NumberFormatException var6) {
            throw p_196975_2_.get().createWithContext(p_196975_0_, s);
         }
      }
   }

   private static boolean func_196970_c(StringReader p_196970_0_) {
      char c0 = p_196970_0_.peek();
      if ((c0 < '0' || c0 > '9') && c0 != '-') {
         if (c0 != '.') {
            return false;
         } else {
            return !p_196970_0_.canRead(2) || p_196970_0_.peek(1) != '.';
         }
      } else {
         return true;
      }
   }

   @Nullable
   private static <T> T func_196972_a(@Nullable T p_196972_0_, Function<T, T> p_196972_1_) {
      return p_196972_0_ == null ? null : p_196972_1_.apply(p_196972_0_);
   }

   public static class FloatBound extends MinMaxBounds<Float> {
      public static final MinMaxBounds.FloatBound UNBOUNDED = new MinMaxBounds.FloatBound(null, null);
      private final Double minSquared;
      private final Double maxSquared;

      private static MinMaxBounds.FloatBound func_211352_a(StringReader p_211352_0_, @Nullable Float p_211352_1_, @Nullable Float p_211352_2_) throws CommandSyntaxException {
         if (p_211352_1_ != null && p_211352_2_ != null && p_211352_1_ > p_211352_2_) {
            throw field_196980_d.createWithContext(p_211352_0_);
         } else {
            return new MinMaxBounds.FloatBound(p_211352_1_, p_211352_2_);
         }
      }

      @Nullable
      private static Double square(@Nullable Float p_211350_0_) {
         return p_211350_0_ == null ? null : p_211350_0_.doubleValue() * p_211350_0_.doubleValue();
      }

      private FloatBound(@Nullable Float p_i49717_1_, @Nullable Float p_i49717_2_) {
         super(p_i49717_1_, p_i49717_2_);
         this.minSquared = square(p_i49717_1_);
         this.maxSquared = square(p_i49717_2_);
      }

      public static MinMaxBounds.FloatBound func_211355_b(float p_211355_0_) {
         return new MinMaxBounds.FloatBound(p_211355_0_, null);
      }

      public boolean test(float p_211354_1_) {
         if (this.min != null && this.min > p_211354_1_) {
            return false;
         } else {
            return this.max == null || !(this.max < p_211354_1_);
         }
      }

      public boolean testSquared(double p_211351_1_) {
         if (this.minSquared != null && this.minSquared > p_211351_1_) {
            return false;
         } else {
            return this.maxSquared == null || !(this.maxSquared < p_211351_1_);
         }
      }

      public static MinMaxBounds.FloatBound func_211356_a(@Nullable JsonElement p_211356_0_) {
         return func_211331_a(p_211356_0_, UNBOUNDED, JsonUtils::getFloat, MinMaxBounds.FloatBound::new);
      }

      public static MinMaxBounds.FloatBound func_211357_a(StringReader p_211357_0_) throws CommandSyntaxException {
         return func_211353_a(p_211357_0_, (p_211358_0_) -> {
            return p_211358_0_;
         });
      }

      public static MinMaxBounds.FloatBound func_211353_a(StringReader p_211353_0_, Function<Float, Float> p_211353_1_) throws CommandSyntaxException {
         return func_211337_a(p_211353_0_, MinMaxBounds.FloatBound::func_211352_a, Float::parseFloat, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidFloat, p_211353_1_);
      }
   }

   @FunctionalInterface
   public interface IBoundFactory<T extends Number, R extends MinMaxBounds<T>> {
      R create(@Nullable T p_create_1_, @Nullable T p_create_2_);
   }

   @FunctionalInterface
   public interface IBoundReader<T extends Number, R extends MinMaxBounds<T>> {
      R create(StringReader p_create_1_, @Nullable T p_create_2_, @Nullable T p_create_3_) throws CommandSyntaxException;
   }

   public static class IntBound extends MinMaxBounds<Integer> {
      public static final MinMaxBounds.IntBound UNBOUNDED = new MinMaxBounds.IntBound(null, null);
      private final Long minSquared;
      private final Long maxSquared;

      private static MinMaxBounds.IntBound func_211338_a(StringReader p_211338_0_, @Nullable Integer p_211338_1_, @Nullable Integer p_211338_2_) throws CommandSyntaxException {
         if (p_211338_1_ != null && p_211338_2_ != null && p_211338_1_ > p_211338_2_) {
            throw field_196980_d.createWithContext(p_211338_0_);
         } else {
            return new MinMaxBounds.IntBound(p_211338_1_, p_211338_2_);
         }
      }

      @Nullable
      private static Long square(@Nullable Integer p_211343_0_) {
         return p_211343_0_ == null ? null : p_211343_0_.longValue() * p_211343_0_.longValue();
      }

      private IntBound(@Nullable Integer p_i49716_1_, @Nullable Integer p_i49716_2_) {
         super(p_i49716_1_, p_i49716_2_);
         this.minSquared = square(p_i49716_1_);
         this.maxSquared = square(p_i49716_2_);
      }

      public static MinMaxBounds.IntBound func_211345_a(int p_211345_0_) {
         return new MinMaxBounds.IntBound(p_211345_0_, p_211345_0_);
      }

      public static MinMaxBounds.IntBound func_211340_b(int p_211340_0_) {
         return new MinMaxBounds.IntBound(p_211340_0_, null);
      }

      public boolean test(int p_211339_1_) {
         if (this.min != null && this.min > p_211339_1_) {
            return false;
         } else {
            return this.max == null || this.max >= p_211339_1_;
         }
      }

      public static MinMaxBounds.IntBound func_211344_a(@Nullable JsonElement p_211344_0_) {
         return func_211331_a(p_211344_0_, UNBOUNDED, JsonUtils::getInt, MinMaxBounds.IntBound::new);
      }

      public static MinMaxBounds.IntBound func_211342_a(StringReader p_211342_0_) throws CommandSyntaxException {
         return func_211341_a(p_211342_0_, (p_211346_0_) -> {
            return p_211346_0_;
         });
      }

      public static MinMaxBounds.IntBound func_211341_a(StringReader p_211341_0_, Function<Integer, Integer> p_211341_1_) throws CommandSyntaxException {
         return func_211337_a(p_211341_0_, MinMaxBounds.IntBound::func_211338_a, Integer::parseInt, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidInt, p_211341_1_);
      }
   }
}
