package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ItemPredicateArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

public class ClearCommand {
   private static final DynamicCommandExceptionType SINGLE_FAILED_EXCEPTION = new DynamicCommandExceptionType((p_208785_0_) -> {
      return new TextComponentTranslation("clear.failed.single", p_208785_0_);
   });
   private static final DynamicCommandExceptionType MULTIPLE_FAILED_EXCEPTION = new DynamicCommandExceptionType((p_208787_0_) -> {
      return new TextComponentTranslation("clear.failed.multiple", p_208787_0_);
   });

   public static void register(CommandDispatcher<CommandSource> p_198243_0_) {
      p_198243_0_.register(Commands.literal("clear").requires((p_198247_0_) -> {
         return p_198247_0_.hasPermissionLevel(2);
      }).executes((p_198241_0_) -> {
         return clearInventory(p_198241_0_.getSource(), Collections.singleton(p_198241_0_.getSource().asPlayer()), (p_198248_0_) -> {
            return true;
         }, -1);
      }).then(Commands.argument("targets", EntityArgument.multiplePlayers()).executes((p_198245_0_) -> {
         return clearInventory(p_198245_0_.getSource(), EntityArgument.getPlayers(p_198245_0_, "targets"), (p_198242_0_) -> {
            return true;
         }, -1);
      }).then(Commands.argument("item", ItemPredicateArgument.itemPredicate()).executes((p_198240_0_) -> {
         return clearInventory(p_198240_0_.getSource(), EntityArgument.getPlayers(p_198240_0_, "targets"), ItemPredicateArgument.getItemPredicate(p_198240_0_, "item"), -1);
      }).then(Commands.argument("maxCount", IntegerArgumentType.integer(0)).executes((p_198246_0_) -> {
         return clearInventory(p_198246_0_.getSource(), EntityArgument.getPlayers(p_198246_0_, "targets"), ItemPredicateArgument.getItemPredicate(p_198246_0_, "item"), IntegerArgumentType.getInteger(p_198246_0_, "maxCount"));
      })))));
   }

   private static int clearInventory(CommandSource p_198244_0_, Collection<EntityPlayerMP> p_198244_1_, Predicate<ItemStack> p_198244_2_, int p_198244_3_) throws CommandSyntaxException {
      int i = 0;

      for(EntityPlayerMP entityplayermp : p_198244_1_) {
         i += entityplayermp.inventory.clearMatchingItems(p_198244_2_, p_198244_3_);
      }

      if (i == 0) {
         if (p_198244_1_.size() == 1) {
            throw SINGLE_FAILED_EXCEPTION.create(p_198244_1_.iterator().next().getName().getFormattedText());
         } else {
            throw MULTIPLE_FAILED_EXCEPTION.create(p_198244_1_.size());
         }
      } else {
         if (p_198244_3_ == 0) {
            if (p_198244_1_.size() == 1) {
               p_198244_0_.sendFeedback(new TextComponentTranslation("commands.clear.test.single", i, p_198244_1_.iterator().next().getDisplayName()), true);
            } else {
               p_198244_0_.sendFeedback(new TextComponentTranslation("commands.clear.test.multiple", i, p_198244_1_.size()), true);
            }
         } else if (p_198244_1_.size() == 1) {
            p_198244_0_.sendFeedback(new TextComponentTranslation("commands.clear.success.single", i, p_198244_1_.iterator().next().getDisplayName()), true);
         } else {
            p_198244_0_.sendFeedback(new TextComponentTranslation("commands.clear.success.multiple", i, p_198244_1_.size()), true);
         }

         return i;
      }
   }
}
