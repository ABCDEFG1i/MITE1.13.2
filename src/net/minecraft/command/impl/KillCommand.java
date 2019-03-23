package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TextComponentTranslation;

public class KillCommand {
   public static void register(CommandDispatcher<CommandSource> p_198518_0_) {
      p_198518_0_.register(Commands.literal("kill").requires((p_198521_0_) -> {
         return p_198521_0_.hasPermissionLevel(2);
      }).then(Commands.argument("targets", EntityArgument.multipleEntities()).executes((p_198520_0_) -> {
         return killEntities(p_198520_0_.getSource(), EntityArgument.getEntities(p_198520_0_, "targets"));
      })));
   }

   private static int killEntities(CommandSource p_198519_0_, Collection<? extends Entity> p_198519_1_) {
      for(Entity entity : p_198519_1_) {
         entity.onKillCommand();
      }

      if (p_198519_1_.size() == 1) {
         p_198519_0_.sendFeedback(new TextComponentTranslation("commands.kill.success.single", p_198519_1_.iterator().next().getDisplayName()), true);
      } else {
         p_198519_0_.sendFeedback(new TextComponentTranslation("commands.kill.success.multiple", p_198519_1_.size()), true);
      }

      return p_198519_1_.size();
   }
}
