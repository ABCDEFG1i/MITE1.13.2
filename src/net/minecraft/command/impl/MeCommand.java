package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentTranslation;

public class MeCommand {
   public static void register(CommandDispatcher<CommandSource> p_198364_0_) {
      p_198364_0_.register(Commands.literal("me").then(Commands.argument("action", StringArgumentType.greedyString()).executes((p_198365_0_) -> {
         p_198365_0_.getSource().getServer().getPlayerList().sendMessage(new TextComponentTranslation("chat.type.emote", p_198365_0_.getSource().getDisplayName(), StringArgumentType.getString(p_198365_0_, "action")));
         return 1;
      })));
   }
}
