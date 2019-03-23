package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Map.Entry;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameRules;

public class GameRuleCommand {
   public static void register(CommandDispatcher<CommandSource> p_198487_0_) {
      LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("gamerule").requires((p_198491_0_) -> {
         return p_198491_0_.hasPermissionLevel(2);
      });

      for(Entry<String, GameRules.ValueDefinition> entry : GameRules.getDefinitions().entrySet()) {
         literalargumentbuilder.then(Commands.literal(entry.getKey()).executes((p_198489_1_) -> {
            return queryRule(p_198489_1_.getSource(), entry.getKey());
         }).then(entry.getValue().getType().createArgument("value").executes((p_198490_1_) -> {
            return setRule(p_198490_1_.getSource(), entry.getKey(), p_198490_1_);
         })));
      }

      p_198487_0_.register(literalargumentbuilder);
   }

   private static int setRule(CommandSource p_198488_0_, String p_198488_1_, CommandContext<CommandSource> p_198488_2_) {
      GameRules.Value gamerules$value = p_198488_0_.getServer().getGameRules().get(p_198488_1_);
      gamerules$value.getType().updateValue(p_198488_2_, "value", gamerules$value);
      p_198488_0_.sendFeedback(new TextComponentTranslation("commands.gamerule.set", p_198488_1_, gamerules$value.getString()), true);
      return gamerules$value.getInt();
   }

   private static int queryRule(CommandSource p_198492_0_, String p_198492_1_) {
      GameRules.Value gamerules$value = p_198492_0_.getServer().getGameRules().get(p_198492_1_);
      p_198492_0_.sendFeedback(new TextComponentTranslation("commands.gamerule.query", p_198492_1_, gamerules$value.getString()), false);
      return gamerules$value.getInt();
   }
}
