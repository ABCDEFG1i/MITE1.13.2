package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.regex.Matcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.server.management.UserListIPBans;
import net.minecraft.util.text.TextComponentTranslation;

public class PardonIpCommand {
   private static final SimpleCommandExceptionType IP_INVALID_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.pardonip.invalid"));
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.pardonip.failed"));

   public static void register(CommandDispatcher<CommandSource> p_198553_0_) {
      p_198553_0_.register(Commands.literal("pardon-ip").requires((p_198556_0_) -> {
         return p_198556_0_.getServer().getPlayerList().getBannedIPs().isLanServer() && p_198556_0_.hasPermissionLevel(3);
      }).then(Commands.argument("target", StringArgumentType.word()).suggests((p_198554_0_, p_198554_1_) -> {
         return ISuggestionProvider.suggest(p_198554_0_.getSource().getServer().getPlayerList().getBannedIPs().getKeys(), p_198554_1_);
      }).executes((p_198555_0_) -> {
         return unbanIp(p_198555_0_.getSource(), StringArgumentType.getString(p_198555_0_, "target"));
      })));
   }

   private static int unbanIp(CommandSource p_198557_0_, String p_198557_1_) throws CommandSyntaxException {
      Matcher matcher = BanIpCommand.IP_PATTERN.matcher(p_198557_1_);
      if (!matcher.matches()) {
         throw IP_INVALID_EXCEPTION.create();
      } else {
         UserListIPBans userlistipbans = p_198557_0_.getServer().getPlayerList().getBannedIPs();
         if (!userlistipbans.func_199044_a(p_198557_1_)) {
            throw FAILED_EXCEPTION.create();
         } else {
            userlistipbans.removeEntry(p_198557_1_);
            p_198557_0_.sendFeedback(new TextComponentTranslation("commands.pardonip.success", p_198557_1_), true);
            return 1;
         }
      }
   }
}
