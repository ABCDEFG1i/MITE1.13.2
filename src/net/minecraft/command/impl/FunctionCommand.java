package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.FunctionObject;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.FunctionArgument;
import net.minecraft.util.text.TextComponentTranslation;

public class FunctionCommand {
   public static final SuggestionProvider<CommandSource> FUNCTION_SUGGESTER = (p_198477_0_, p_198477_1_) -> {
      FunctionManager functionmanager = p_198477_0_.getSource().getServer().getFunctionManager();
      ISuggestionProvider.suggestIterable(functionmanager.getTagCollection().getRegisteredTags(), p_198477_1_, "#");
      return ISuggestionProvider.suggestIterable(functionmanager.getFunctions().keySet(), p_198477_1_);
   };

   public static void register(CommandDispatcher<CommandSource> p_198476_0_) {
      p_198476_0_.register(Commands.literal("function").requires((p_198480_0_) -> {
         return p_198480_0_.hasPermissionLevel(2);
      }).then(Commands.argument("name", FunctionArgument.function()).suggests(FUNCTION_SUGGESTER).executes((p_198479_0_) -> {
         return executeFunctions(p_198479_0_.getSource(), FunctionArgument.getFunctions(p_198479_0_, "name"));
      })));
   }

   private static int executeFunctions(CommandSource p_200025_0_, Collection<FunctionObject> p_200025_1_) {
      int i = 0;

      for(FunctionObject functionobject : p_200025_1_) {
         i += p_200025_0_.getServer().getFunctionManager().execute(functionobject, p_200025_0_.withFeedbackDisabled().withMinPermissionLevel(2));
      }

      if (p_200025_1_.size() == 1) {
         p_200025_0_.sendFeedback(new TextComponentTranslation("commands.function.success.single", i, p_200025_1_.iterator().next().getId()), true);
      } else {
         p_200025_0_.sendFeedback(new TextComponentTranslation("commands.function.success.multiple", i, p_200025_1_.size()), true);
      }

      return i;
   }
}
