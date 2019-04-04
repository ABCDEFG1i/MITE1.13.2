package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.UserListIPBans;
import net.minecraft.server.management.UserListIPBansEntry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class BanIpCommand {
   public static final Pattern IP_PATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
   private static final SimpleCommandExceptionType IP_INVALID = new SimpleCommandExceptionType(new TextComponentTranslation("commands.banip.invalid"));
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.banip.failed"));

   public static void register(CommandDispatcher<CommandSource> p_198220_0_) {
      p_198220_0_.register(Commands.literal("ban-ip").requires((p_198222_0_) -> {
         return p_198222_0_.getServer().getPlayerList().getBannedIPs().isLanServer() && p_198222_0_.hasPermissionLevel(3);
      }).then(Commands.argument("target", StringArgumentType.word()).executes((p_198219_0_) -> {
         return banUsernameOrIp(p_198219_0_.getSource(), StringArgumentType.getString(p_198219_0_, "target"), null);
      }).then(Commands.argument("reason", MessageArgument.message()).executes((p_198221_0_) -> {
         return banUsernameOrIp(p_198221_0_.getSource(), StringArgumentType.getString(p_198221_0_, "target"), MessageArgument.getMessage(p_198221_0_, "reason"));
      }))));
   }

   private static int banUsernameOrIp(CommandSource p_198223_0_, String p_198223_1_, @Nullable ITextComponent p_198223_2_) throws CommandSyntaxException {
      Matcher matcher = IP_PATTERN.matcher(p_198223_1_);
      if (matcher.matches()) {
         return banIpAddress(p_198223_0_, p_198223_1_, p_198223_2_);
      } else {
         EntityPlayerMP entityplayermp = p_198223_0_.getServer().getPlayerList().getPlayerByUsername(p_198223_1_);
         if (entityplayermp != null) {
            return banIpAddress(p_198223_0_, entityplayermp.getPlayerIP(), p_198223_2_);
         } else {
            throw IP_INVALID.create();
         }
      }
   }

   private static int banIpAddress(CommandSource p_198224_0_, String p_198224_1_, @Nullable ITextComponent p_198224_2_) throws CommandSyntaxException {
      UserListIPBans userlistipbans = p_198224_0_.getServer().getPlayerList().getBannedIPs();
      if (userlistipbans.func_199044_a(p_198224_1_)) {
         throw FAILED_EXCEPTION.create();
      } else {
         List<EntityPlayerMP> list = p_198224_0_.getServer().getPlayerList().getPlayersMatchingAddress(p_198224_1_);
         UserListIPBansEntry userlistipbansentry = new UserListIPBansEntry(p_198224_1_, null, p_198224_0_.getName(),
                 null, p_198224_2_ == null ? null : p_198224_2_.getString());
         userlistipbans.addEntry(userlistipbansentry);
         p_198224_0_.sendFeedback(new TextComponentTranslation("commands.banip.success", p_198224_1_, userlistipbansentry.getBanReason()), true);
         if (!list.isEmpty()) {
            p_198224_0_.sendFeedback(new TextComponentTranslation("commands.banip.info", list.size(), EntitySelector.func_197350_a(list)), true);
         }

         for(EntityPlayerMP entityplayermp : list) {
            entityplayermp.connection.disconnect(new TextComponentTranslation("multiplayer.disconnect.ip_banned"));
         }

         return list.size();
      }
   }
}
