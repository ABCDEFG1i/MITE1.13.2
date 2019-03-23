package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.dimension.DimensionType;

public class DifficultyCommand {
   private static final DynamicCommandExceptionType FAILED_EXCEPTION = new DynamicCommandExceptionType((p_208823_0_) -> {
      return new TextComponentTranslation("commands.difficulty.failure", p_208823_0_);
   });

   public static void register(CommandDispatcher<CommandSource> p_198344_0_) {
      LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("difficulty");

      for(EnumDifficulty enumdifficulty : EnumDifficulty.values()) {
         literalargumentbuilder.then(Commands.literal(enumdifficulty.getTranslationKey()).executes((p_198347_1_) -> {
            return setDifficulty(p_198347_1_.getSource(), enumdifficulty);
         }));
      }

      p_198344_0_.register(literalargumentbuilder.requires((p_198348_0_) -> {
         return p_198348_0_.hasPermissionLevel(2);
      }).executes((p_198346_0_) -> {
         EnumDifficulty enumdifficulty1 = p_198346_0_.getSource().getWorld().getDifficulty();
         p_198346_0_.getSource().sendFeedback(new TextComponentTranslation("commands.difficulty.query", enumdifficulty1.getDisplayName()), false);
         return enumdifficulty1.getId();
      }));
   }

   public static int setDifficulty(CommandSource p_198345_0_, EnumDifficulty p_198345_1_) throws CommandSyntaxException {
      MinecraftServer minecraftserver = p_198345_0_.getServer();
      if (minecraftserver.func_71218_a(DimensionType.OVERWORLD).getDifficulty() == p_198345_1_) {
         throw FAILED_EXCEPTION.create(p_198345_1_.getTranslationKey());
      } else {
         minecraftserver.setDifficultyForAllWorlds(p_198345_1_);
         p_198345_0_.sendFeedback(new TextComponentTranslation("commands.difficulty.success", p_198345_1_.getDisplayName()), true);
         return 0;
      }
   }
}
