package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import java.util.List;
import java.util.function.Function;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;

public class ListCommand {
   public static void register(CommandDispatcher<CommandSource> p_198522_0_) {
      p_198522_0_.register(Commands.literal("list").executes((p_198523_0_) -> {
         return listNames(p_198523_0_.getSource());
      }).then(Commands.literal("uuids").executes((p_208202_0_) -> {
         return listUUIDs(p_208202_0_.getSource());
      })));
   }

   private static int listNames(CommandSource p_198524_0_) {
      return listPlayers(p_198524_0_, EntityPlayer::getDisplayName);
   }

   private static int listUUIDs(CommandSource p_208201_0_) {
      return listPlayers(p_208201_0_, EntityPlayer::func_208017_dF);
   }

   private static int listPlayers(CommandSource p_208200_0_, Function<EntityPlayerMP, ITextComponent> p_208200_1_) {
      PlayerList playerlist = p_208200_0_.getServer().getPlayerList();
      List<EntityPlayerMP> list = playerlist.getPlayers();
      ITextComponent itextcomponent = TextComponentUtils.makeList(list, p_208200_1_);
      p_208200_0_.sendFeedback(new TextComponentTranslation("commands.list.players", list.size(), playerlist.getMaxPlayers(), itextcomponent), false);
      return list.size();
   }
}
