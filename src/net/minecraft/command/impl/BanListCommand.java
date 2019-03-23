package net.minecraft.command.impl;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.management.PlayerList;
import net.minecraft.server.management.UserListEntryBan;
import net.minecraft.util.text.TextComponentTranslation;

public class BanListCommand {
   public static void register(CommandDispatcher<CommandSource> p_198229_0_) {
      p_198229_0_.register(Commands.literal("banlist").requires((p_198233_0_) -> {
         return (p_198233_0_.getServer().getPlayerList().getBannedPlayers().isLanServer() || p_198233_0_.getServer().getPlayerList().getBannedIPs().isLanServer()) && p_198233_0_.hasPermissionLevel(3);
      }).executes((p_198231_0_) -> {
         PlayerList playerlist = p_198231_0_.getSource().getServer().getPlayerList();
         return sendBanList(p_198231_0_.getSource(), Lists.newArrayList(Iterables.concat(playerlist.getBannedPlayers().func_199043_f(), playerlist.getBannedIPs().func_199043_f())));
      }).then(Commands.literal("ips").executes((p_198228_0_) -> {
         return sendBanList(p_198228_0_.getSource(), p_198228_0_.getSource().getServer().getPlayerList().getBannedIPs().func_199043_f());
      })).then(Commands.literal("players").executes((p_198232_0_) -> {
         return sendBanList(p_198232_0_.getSource(), p_198232_0_.getSource().getServer().getPlayerList().getBannedPlayers().func_199043_f());
      })));
   }

   private static int sendBanList(CommandSource p_198230_0_, Collection<? extends UserListEntryBan<?>> p_198230_1_) {
      if (p_198230_1_.isEmpty()) {
         p_198230_0_.sendFeedback(new TextComponentTranslation("commands.banlist.none"), false);
      } else {
         p_198230_0_.sendFeedback(new TextComponentTranslation("commands.banlist.list", p_198230_1_.size()), false);

         for(UserListEntryBan<?> userlistentryban : p_198230_1_) {
            p_198230_0_.sendFeedback(new TextComponentTranslation("commands.banlist.entry", userlistentryban.func_199041_e(), userlistentryban.func_199040_b(), userlistentryban.getBanReason()), false);
         }
      }

      return p_198230_1_.size();
   }
}
