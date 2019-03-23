package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;

public class DefaultGameModeCommand {
   public static void register(CommandDispatcher<CommandSource> p_198340_0_) {
      LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("defaultgamemode").requires((p_198342_0_) -> {
         return p_198342_0_.hasPermissionLevel(2);
      });

      for(GameType gametype : GameType.values()) {
         if (gametype != GameType.NOT_SET) {
            literalargumentbuilder.then(Commands.literal(gametype.getName()).executes((p_198343_1_) -> {
               return setGameType(p_198343_1_.getSource(), gametype);
            }));
         }
      }

      p_198340_0_.register(literalargumentbuilder);
   }

   private static int setGameType(CommandSource p_198341_0_, GameType p_198341_1_) {
      int i = 0;
      MinecraftServer minecraftserver = p_198341_0_.getServer();
      minecraftserver.setGameType(p_198341_1_);
      if (minecraftserver.getForceGamemode()) {
         for(EntityPlayerMP entityplayermp : minecraftserver.getPlayerList().getPlayers()) {
            if (entityplayermp.interactionManager.getGameType() != p_198341_1_) {
               entityplayermp.setGameType(p_198341_1_);
               ++i;
            }
         }
      }

      p_198341_0_.sendFeedback(new TextComponentTranslation("commands.defaultgamemode.success", p_198341_1_.getDisplayName()), true);
      return i;
   }
}
