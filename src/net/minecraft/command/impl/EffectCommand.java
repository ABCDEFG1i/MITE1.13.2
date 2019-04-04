package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.PotionArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentTranslation;

public class EffectCommand {
   private static final SimpleCommandExceptionType GIVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.effect.give.failed"));
   private static final SimpleCommandExceptionType CLEAR_EVERYTHING_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.effect.clear.everything.failed"));
   private static final SimpleCommandExceptionType CLEAR_SPECIFIC_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.effect.clear.specific.failed"));

   public static void register(CommandDispatcher<CommandSource> p_198353_0_) {
      p_198353_0_.register(Commands.literal("effect").requires((p_198359_0_) -> {
         return p_198359_0_.hasPermissionLevel(2);
      }).then(Commands.literal("clear").then(Commands.argument("targets", EntityArgument.multipleEntities()).executes((p_198352_0_) -> {
         return clearAllEffects(p_198352_0_.getSource(), EntityArgument.getEntities(p_198352_0_, "targets"));
      }).then(Commands.argument("effect", PotionArgument.mobEffect()).executes((p_198356_0_) -> {
         return clearEffect(p_198356_0_.getSource(), EntityArgument.getEntities(p_198356_0_, "targets"), PotionArgument.getMobEffect(p_198356_0_, "effect"));
      })))).then(Commands.literal("give").then(Commands.argument("targets", EntityArgument.multipleEntities()).then(Commands.argument("effect", PotionArgument.mobEffect()).executes((p_198351_0_) -> {
         return addEffect(p_198351_0_.getSource(), EntityArgument.getEntities(p_198351_0_, "targets"), PotionArgument.getMobEffect(p_198351_0_, "effect"),
                 null, 0, true);
      }).then(Commands.argument("seconds", IntegerArgumentType.integer(1, 1000000)).executes((p_198357_0_) -> {
         return addEffect(p_198357_0_.getSource(), EntityArgument.getEntities(p_198357_0_, "targets"), PotionArgument.getMobEffect(p_198357_0_, "effect"), IntegerArgumentType.getInteger(p_198357_0_, "seconds"), 0, true);
      }).then(Commands.argument("amplifier", IntegerArgumentType.integer(0, 255)).executes((p_198350_0_) -> {
         return addEffect(p_198350_0_.getSource(), EntityArgument.getEntities(p_198350_0_, "targets"), PotionArgument.getMobEffect(p_198350_0_, "effect"), IntegerArgumentType.getInteger(p_198350_0_, "seconds"), IntegerArgumentType.getInteger(p_198350_0_, "amplifier"), true);
      }).then(Commands.argument("hideParticles", BoolArgumentType.bool()).executes((p_198358_0_) -> {
         return addEffect(p_198358_0_.getSource(), EntityArgument.getEntities(p_198358_0_, "targets"), PotionArgument.getMobEffect(p_198358_0_, "effect"), IntegerArgumentType.getInteger(p_198358_0_, "seconds"), IntegerArgumentType.getInteger(p_198358_0_, "amplifier"), !BoolArgumentType.getBool(p_198358_0_, "hideParticles"));
      }))))))));
   }

   private static int addEffect(CommandSource p_198360_0_, Collection<? extends Entity> p_198360_1_, Potion p_198360_2_, @Nullable Integer p_198360_3_, int p_198360_4_, boolean p_198360_5_) throws CommandSyntaxException {
      int i = 0;
      int j;
      if (p_198360_3_ != null) {
         if (p_198360_2_.isInstant()) {
            j = p_198360_3_;
         } else {
            j = p_198360_3_ * 20;
         }
      } else if (p_198360_2_.isInstant()) {
         j = 1;
      } else {
         j = 600;
      }

      for(Entity entity : p_198360_1_) {
         if (entity instanceof EntityLivingBase) {
            PotionEffect potioneffect = new PotionEffect(p_198360_2_, j, p_198360_4_, false, p_198360_5_);
            if (((EntityLivingBase)entity).addPotionEffect(potioneffect)) {
               ++i;
            }
         }
      }

      if (i == 0) {
         throw GIVE_FAILED_EXCEPTION.create();
      } else {
         if (p_198360_1_.size() == 1) {
            p_198360_0_.sendFeedback(new TextComponentTranslation("commands.effect.give.success.single", p_198360_2_.func_199286_c(), p_198360_1_.iterator().next().getDisplayName(), j / 20), true);
         } else {
            p_198360_0_.sendFeedback(new TextComponentTranslation("commands.effect.give.success.multiple", p_198360_2_.func_199286_c(), p_198360_1_.size(), j / 20), true);
         }

         return i;
      }
   }

   private static int clearAllEffects(CommandSource p_198354_0_, Collection<? extends Entity> p_198354_1_) throws CommandSyntaxException {
      int i = 0;

      for(Entity entity : p_198354_1_) {
         if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).func_195061_cb()) {
            ++i;
         }
      }

      if (i == 0) {
         throw CLEAR_EVERYTHING_FAILED_EXCEPTION.create();
      } else {
         if (p_198354_1_.size() == 1) {
            p_198354_0_.sendFeedback(new TextComponentTranslation("commands.effect.clear.everything.success.single", p_198354_1_.iterator().next().getDisplayName()), true);
         } else {
            p_198354_0_.sendFeedback(new TextComponentTranslation("commands.effect.clear.everything.success.multiple", p_198354_1_.size()), true);
         }

         return i;
      }
   }

   private static int clearEffect(CommandSource p_198355_0_, Collection<? extends Entity> p_198355_1_, Potion p_198355_2_) throws CommandSyntaxException {
      int i = 0;

      for(Entity entity : p_198355_1_) {
         if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).removePotionEffect(p_198355_2_)) {
            ++i;
         }
      }

      if (i == 0) {
         throw CLEAR_SPECIFIC_FAILED_EXCEPTION.create();
      } else {
         if (p_198355_1_.size() == 1) {
            p_198355_0_.sendFeedback(new TextComponentTranslation("commands.effect.clear.specific.success.single", p_198355_2_.func_199286_c(), p_198355_1_.iterator().next().getDisplayName()), true);
         } else {
            p_198355_0_.sendFeedback(new TextComponentTranslation("commands.effect.clear.specific.success.multiple", p_198355_2_.func_199286_c(), p_198355_1_.size()), true);
         }

         return i;
      }
   }
}
