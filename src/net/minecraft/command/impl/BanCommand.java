package net.minecraft.command.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.UserListBans;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;

public class BanCommand {
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.ban.failed"));

   public static void register(CommandDispatcher<CommandSource> p_198235_0_) {
      p_198235_0_.register(Commands.literal("ban").requires((p_198238_0_) -> {
         return p_198238_0_.getServer().getPlayerList().getBannedPlayers().isLanServer() && p_198238_0_.hasPermissionLevel(3);
      }).then(Commands.argument("targets", GameProfileArgument.gameProfile()).executes((p_198234_0_) -> {
         return banGameProfiles(p_198234_0_.getSource(), GameProfileArgument.getGameProfiles(p_198234_0_, "targets"), (ITextComponent)null);
      }).then(Commands.argument("reason", MessageArgument.message()).executes((p_198237_0_) -> {
         return banGameProfiles(p_198237_0_.getSource(), GameProfileArgument.getGameProfiles(p_198237_0_, "targets"), MessageArgument.getMessage(p_198237_0_, "reason"));
      }))));
   }

   private static int banGameProfiles(CommandSource p_198236_0_, Collection<GameProfile> p_198236_1_, @Nullable ITextComponent p_198236_2_) throws CommandSyntaxException {
      UserListBans userlistbans = p_198236_0_.getServer().getPlayerList().getBannedPlayers();
      int i = 0;

      for(GameProfile gameprofile : p_198236_1_) {
         if (!userlistbans.isBanned(gameprofile)) {
            UserListBansEntry userlistbansentry = new UserListBansEntry(gameprofile, (Date)null, p_198236_0_.getName(), (Date)null, p_198236_2_ == null ? null : p_198236_2_.getString());
            userlistbans.addEntry(userlistbansentry);
            ++i;
            p_198236_0_.sendFeedback(new TextComponentTranslation("commands.ban.success", TextComponentUtils.func_197679_a(gameprofile), userlistbansentry.getBanReason()), true);
            EntityPlayerMP entityplayermp = p_198236_0_.getServer().getPlayerList().getPlayerByUUID(gameprofile.getId());
            if (entityplayermp != null) {
               entityplayermp.connection.disconnect(new TextComponentTranslation("multiplayer.disconnect.banned"));
            }
         }
      }

      if (i == 0) {
         throw FAILED_EXCEPTION.create();
      } else {
         return i;
      }
   }
}
