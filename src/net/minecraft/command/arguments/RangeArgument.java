package net.minecraft.command.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.command.CommandSource;
import net.minecraft.network.PacketBuffer;

public interface RangeArgument<T extends MinMaxBounds<?>> extends ArgumentType<T> {
   static RangeArgument.IntRange func_211371_a() {
      return new RangeArgument.IntRange();
   }

   class FloatRange implements RangeArgument<MinMaxBounds.FloatBound> {
      private static final Collection<String> field_211374_a = Arrays.asList("0..5.2", "0", "-5.4", "-100.76..", "..100");

      public MinMaxBounds.FloatBound parse(StringReader p_parse_1_) throws CommandSyntaxException {
         return MinMaxBounds.FloatBound.func_211357_a(p_parse_1_);
      }

      public Collection<String> getExamples() {
         return field_211374_a;
      }

      public static class Serializer extends RangeArgument.Serializer<RangeArgument.FloatRange> {
         public RangeArgument.FloatRange read(PacketBuffer p_197071_1_) {
            return new RangeArgument.FloatRange();
         }
      }
   }

   class IntRange implements RangeArgument<MinMaxBounds.IntBound> {
      private static final Collection<String> field_201321_a = Arrays.asList("0..5", "0", "-5", "-100..", "..100");

      public static MinMaxBounds.IntBound getIntRange(CommandContext<CommandSource> p_211372_0_, String p_211372_1_) {
         return p_211372_0_.getArgument(p_211372_1_, MinMaxBounds.IntBound.class);
      }

      public MinMaxBounds.IntBound parse(StringReader p_parse_1_) throws CommandSyntaxException {
         return MinMaxBounds.IntBound.func_211342_a(p_parse_1_);
      }

      public Collection<String> getExamples() {
         return field_201321_a;
      }

      public static class Serializer extends RangeArgument.Serializer<RangeArgument.IntRange> {
         public RangeArgument.IntRange read(PacketBuffer p_197071_1_) {
            return new RangeArgument.IntRange();
         }
      }
   }

   abstract class Serializer<T extends RangeArgument<?>> implements IArgumentSerializer<T> {
      public void write(T p_197072_1_, PacketBuffer p_197072_2_) {
      }

      public void func_212244_a(T p_212244_1_, JsonObject p_212244_2_) {
      }
   }
}
