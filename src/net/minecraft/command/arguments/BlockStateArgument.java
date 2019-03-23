package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;

public class BlockStateArgument implements ArgumentType<BlockStateInput> {
   private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "foo{bar=baz}");

   public static BlockStateArgument blockState() {
      return new BlockStateArgument();
   }

   public BlockStateInput parse(StringReader p_parse_1_) throws CommandSyntaxException {
      BlockStateParser blockstateparser = (new BlockStateParser(p_parse_1_, false)).parse(true);
      return new BlockStateInput(blockstateparser.func_197249_b(), blockstateparser.func_197254_a().keySet(), blockstateparser.func_197241_c());
   }

   public static BlockStateInput getBlockStateInput(CommandContext<CommandSource> p_197238_0_, String p_197238_1_) {
      return p_197238_0_.getArgument(p_197238_1_, BlockStateInput.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      StringReader stringreader = new StringReader(p_listSuggestions_2_.getInput());
      stringreader.setCursor(p_listSuggestions_2_.getStart());
      BlockStateParser blockstateparser = new BlockStateParser(stringreader, false);

      try {
         blockstateparser.parse(true);
      } catch (CommandSyntaxException var6) {
         ;
      }

      return blockstateparser.getSuggestions(p_listSuggestions_2_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
