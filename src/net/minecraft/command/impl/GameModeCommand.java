package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;

public class GameModeCommand {
   public static void register(CommandDispatcher<CommandSource> p_198482_0_) {
      LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("gamemode").requires((p_198485_0_) -> {
         return p_198485_0_.hasPermissionLevel(2);
      });

      for(GameType gametype : GameType.values()) {
         if (gametype != GameType.NOT_SET) {
            literalargumentbuilder.then(Commands.literal(gametype.getName()).executes((p_198483_1_) -> {
               return setGameMode(p_198483_1_, Collections.singleton(p_198483_1_.getSource().asPlayer()), gametype);
            }).then(Commands.argument("target", EntityArgument.multiplePlayers()).executes((p_198486_1_) -> {
               return setGameMode(p_198486_1_, EntityArgument.getPlayers(p_198486_1_, "target"), gametype);
            })));
         }
      }

      p_198482_0_.register(literalargumentbuilder);
   }

   private static void sendGameModeFeedback(CommandSource p_208517_0_, EntityPlayerMP p_208517_1_, GameType p_208517_2_) {
      ITextComponent itextcomponent = new TextComponentTranslation("gameMode." + p_208517_2_.getName());
      if (p_208517_0_.getEntity() == p_208517_1_) {
         p_208517_0_.sendFeedback(new TextComponentTranslation("commands.gamemode.success.self", itextcomponent), true);
      } else {
         if (p_208517_0_.getWorld().getGameRules().getBoolean("sendCommandFeedback")) {
            p_208517_1_.sendMessage(new TextComponentTranslation("gameMode.changed", itextcomponent));
         }

         p_208517_0_.sendFeedback(new TextComponentTranslation("commands.gamemode.success.other", p_208517_1_.getDisplayName(), itextcomponent), true);
      }

   }

   private static int setGameMode(CommandContext<CommandSource> p_198484_0_, Collection<EntityPlayerMP> p_198484_1_, GameType p_198484_2_) {
      int i = 0;

      for(EntityPlayerMP entityplayermp : p_198484_1_) {
         if (entityplayermp.interactionManager.getGameType() != p_198484_2_) {
            entityplayermp.setGameType(p_198484_2_);
            sendGameModeFeedback(p_198484_0_.getSource(), entityplayermp, p_198484_2_);
            ++i;
         }
      }

      return i;
   }
}
