package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.command.arguments.ObjectiveArgument;
import net.minecraft.command.arguments.ObjectiveCriteriaArgument;
import net.minecraft.command.arguments.OperationArgument;
import net.minecraft.command.arguments.ScoreHolderArgument;
import net.minecraft.command.arguments.ScoreboardSlotArgument;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;

public class ScoreboardCommand {
   private static final SimpleCommandExceptionType OBJECTIVE_ALREADY_EXISTS_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.scoreboard.objectives.add.duplicate"));
   private static final SimpleCommandExceptionType DISPLAY_ALREADY_CLEAR_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.scoreboard.objectives.display.alreadyEmpty"));
   private static final SimpleCommandExceptionType DISPLAY_ALREADY_SET_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.scoreboard.objectives.display.alreadySet"));
   private static final SimpleCommandExceptionType ENABLE_TRIGGER_FAILED = new SimpleCommandExceptionType(new TextComponentTranslation("commands.scoreboard.players.enable.failed"));
   private static final SimpleCommandExceptionType ENABLE_TRIGGER_INVALID = new SimpleCommandExceptionType(new TextComponentTranslation("commands.scoreboard.players.enable.invalid"));
   private static final Dynamic2CommandExceptionType SCOREBOARD_PLAYER_NOT_FOUND_EXCEPTION = new Dynamic2CommandExceptionType((p_208907_0_, p_208907_1_) -> {
      return new TextComponentTranslation("commands.scoreboard.players.get.null", p_208907_0_, p_208907_1_);
   });

   public static void register(CommandDispatcher<CommandSource> p_198647_0_) {
      p_198647_0_.register(Commands.literal("scoreboard").requires((p_198650_0_) -> {
         return p_198650_0_.hasPermissionLevel(2);
      }).then(Commands.literal("objectives").then(Commands.literal("list").executes((p_198640_0_) -> {
         return listObjectives(p_198640_0_.getSource());
      })).then(Commands.literal("add").then(Commands.argument("objective", StringArgumentType.word()).then(Commands.argument("criteria", ObjectiveCriteriaArgument.objectiveCriteria()).executes((p_198636_0_) -> {
         return func_198629_a(p_198636_0_.getSource(), StringArgumentType.getString(p_198636_0_, "objective"), ObjectiveCriteriaArgument.func_197161_a(p_198636_0_, "criteria"), new TextComponentString(StringArgumentType.getString(p_198636_0_, "objective")));
      }).then(Commands.argument("displayName", ComponentArgument.component()).executes((p_198649_0_) -> {
         return func_198629_a(p_198649_0_.getSource(), StringArgumentType.getString(p_198649_0_, "objective"), ObjectiveCriteriaArgument.func_197161_a(p_198649_0_, "criteria"), ComponentArgument.getComponent(p_198649_0_, "displayName"));
      }))))).then(Commands.literal("modify").then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.literal("displayname").then(Commands.argument("displayName", ComponentArgument.component()).executes((p_211750_0_) -> {
         return setDisplayName(p_211750_0_.getSource(), ObjectiveArgument.getObjective(p_211750_0_, "objective"), ComponentArgument.getComponent(p_211750_0_, "displayName"));
      }))).then(createRenderTypeArgument()))).then(Commands.literal("remove").then(Commands.argument("objective", ObjectiveArgument.objective()).executes((p_198646_0_) -> {
         return removeObjective(p_198646_0_.getSource(), ObjectiveArgument.getObjective(p_198646_0_, "objective"));
      }))).then(Commands.literal("setdisplay").then(Commands.argument("slot", ScoreboardSlotArgument.scoreboardSlot()).executes((p_198652_0_) -> {
         return clearObjectiveDisplaySlot(p_198652_0_.getSource(), ScoreboardSlotArgument.getScoreboardSlot(p_198652_0_, "slot"));
      }).then(Commands.argument("objective", ObjectiveArgument.objective()).executes((p_198639_0_) -> {
         return setObjectiveDisplaySlot(p_198639_0_.getSource(), ScoreboardSlotArgument.getScoreboardSlot(p_198639_0_, "slot"), ObjectiveArgument.getObjective(p_198639_0_, "objective"));
      }))))).then(Commands.literal("players").then(Commands.literal("list").executes((p_198642_0_) -> {
         return listPlayers(p_198642_0_.getSource());
      }).then(Commands.argument("target", ScoreHolderArgument.singleScoreHolder()).suggests(ScoreHolderArgument.field_201326_a).executes((p_198631_0_) -> {
         return listPlayerScores(p_198631_0_.getSource(), ScoreHolderArgument.func_197211_a(p_198631_0_, "target"));
      }))).then(Commands.literal("set").then(Commands.argument("targets", ScoreHolderArgument.multipleScoreHolder()).suggests(ScoreHolderArgument.field_201326_a).then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.argument("score", IntegerArgumentType.integer()).executes((p_198655_0_) -> {
         return setPlayerScore(p_198655_0_.getSource(), ScoreHolderArgument.getScoreHolder(p_198655_0_, "targets"), ObjectiveArgument.getWritableObjective(p_198655_0_, "objective"), IntegerArgumentType.getInteger(p_198655_0_, "score"));
      }))))).then(Commands.literal("get").then(Commands.argument("target", ScoreHolderArgument.singleScoreHolder()).suggests(ScoreHolderArgument.field_201326_a).then(Commands.argument("objective", ObjectiveArgument.objective()).executes((p_198660_0_) -> {
         return getPlayerScore(p_198660_0_.getSource(), ScoreHolderArgument.func_197211_a(p_198660_0_, "target"), ObjectiveArgument.getObjective(p_198660_0_, "objective"));
      })))).then(Commands.literal("add").then(Commands.argument("targets", ScoreHolderArgument.multipleScoreHolder()).suggests(ScoreHolderArgument.field_201326_a).then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.argument("score", IntegerArgumentType.integer(0)).executes((p_198645_0_) -> {
         return addToPlayerScore(p_198645_0_.getSource(), ScoreHolderArgument.getScoreHolder(p_198645_0_, "targets"), ObjectiveArgument.getWritableObjective(p_198645_0_, "objective"), IntegerArgumentType.getInteger(p_198645_0_, "score"));
      }))))).then(Commands.literal("remove").then(Commands.argument("targets", ScoreHolderArgument.multipleScoreHolder()).suggests(ScoreHolderArgument.field_201326_a).then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.argument("score", IntegerArgumentType.integer(0)).executes((p_198648_0_) -> {
         return removeFromPlayerScore(p_198648_0_.getSource(), ScoreHolderArgument.getScoreHolder(p_198648_0_, "targets"), ObjectiveArgument.getWritableObjective(p_198648_0_, "objective"), IntegerArgumentType.getInteger(p_198648_0_, "score"));
      }))))).then(Commands.literal("reset").then(Commands.argument("targets", ScoreHolderArgument.multipleScoreHolder()).suggests(ScoreHolderArgument.field_201326_a).executes((p_198635_0_) -> {
         return resetPlayerAllScores(p_198635_0_.getSource(), ScoreHolderArgument.getScoreHolder(p_198635_0_, "targets"));
      }).then(Commands.argument("objective", ObjectiveArgument.objective()).executes((p_198630_0_) -> {
         return resetPlayerScore(p_198630_0_.getSource(), ScoreHolderArgument.getScoreHolder(p_198630_0_, "targets"), ObjectiveArgument.getObjective(p_198630_0_, "objective"));
      })))).then(Commands.literal("enable").then(Commands.argument("targets", ScoreHolderArgument.multipleScoreHolder()).suggests(ScoreHolderArgument.field_201326_a).then(Commands.argument("objective", ObjectiveArgument.objective()).suggests((p_198638_0_, p_198638_1_) -> {
         return suggestTriggers(p_198638_0_.getSource(), ScoreHolderArgument.getScoreHolder(p_198638_0_, "targets"), p_198638_1_);
      }).executes((p_198628_0_) -> {
         return enableTrigger(p_198628_0_.getSource(), ScoreHolderArgument.getScoreHolder(p_198628_0_, "targets"), ObjectiveArgument.getObjective(p_198628_0_, "objective"));
      })))).then(Commands.literal("operation").then(Commands.argument("targets", ScoreHolderArgument.multipleScoreHolder()).suggests(ScoreHolderArgument.field_201326_a).then(Commands.argument("targetObjective", ObjectiveArgument.objective()).then(Commands.argument("operation", OperationArgument.operation()).then(Commands.argument("source", ScoreHolderArgument.multipleScoreHolder()).suggests(ScoreHolderArgument.field_201326_a).then(Commands.argument("sourceObjective", ObjectiveArgument.objective()).executes((p_198657_0_) -> {
         return applyScoreOperation(p_198657_0_.getSource(), ScoreHolderArgument.getScoreHolder(p_198657_0_, "targets"), ObjectiveArgument.getWritableObjective(p_198657_0_, "targetObjective"), OperationArgument.getOperation(p_198657_0_, "operation"), ScoreHolderArgument.getScoreHolder(p_198657_0_, "source"), ObjectiveArgument.getObjective(p_198657_0_, "sourceObjective"));
      })))))))));
   }

   private static LiteralArgumentBuilder<CommandSource> createRenderTypeArgument() {
      LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("rendertype");

      for(ScoreCriteria.RenderType scorecriteria$rendertype : ScoreCriteria.RenderType.values()) {
         literalargumentbuilder.then(Commands.literal(scorecriteria$rendertype.func_211838_a()).executes((p_211912_1_) -> {
            return func_211910_a(p_211912_1_.getSource(), ObjectiveArgument.getObjective(p_211912_1_, "objective"), scorecriteria$rendertype);
         }));
      }

      return literalargumentbuilder;
   }

   private static CompletableFuture<Suggestions> suggestTriggers(CommandSource p_198641_0_, Collection<String> p_198641_1_, SuggestionsBuilder p_198641_2_) {
      List<String> list = Lists.newArrayList();
      Scoreboard scoreboard = p_198641_0_.getServer().getWorldScoreboard();

      for(ScoreObjective scoreobjective : scoreboard.getScoreObjectives()) {
         if (scoreobjective.func_96680_c() == ScoreCriteria.field_178791_c) {
            boolean flag = false;

            for(String s : p_198641_1_) {
               if (!scoreboard.entityHasObjective(s, scoreobjective) || scoreboard.getOrCreateScore(s, scoreobjective).isLocked()) {
                  flag = true;
                  break;
               }
            }

            if (flag) {
               list.add(scoreobjective.getName());
            }
         }
      }

      return ISuggestionProvider.suggest(list, p_198641_2_);
   }

   private static int getPlayerScore(CommandSource p_198634_0_, String p_198634_1_, ScoreObjective p_198634_2_) throws CommandSyntaxException {
      Scoreboard scoreboard = p_198634_0_.getServer().getWorldScoreboard();
      if (!scoreboard.entityHasObjective(p_198634_1_, p_198634_2_)) {
         throw SCOREBOARD_PLAYER_NOT_FOUND_EXCEPTION.create(p_198634_2_.getName(), p_198634_1_);
      } else {
         Score score = scoreboard.getOrCreateScore(p_198634_1_, p_198634_2_);
         p_198634_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.players.get.success", p_198634_1_, score.getScorePoints(), p_198634_2_.func_197890_e()), false);
         return score.getScorePoints();
      }
   }

   private static int applyScoreOperation(CommandSource p_198658_0_, Collection<String> p_198658_1_, ScoreObjective p_198658_2_, OperationArgument.IOperation p_198658_3_, Collection<String> p_198658_4_, ScoreObjective p_198658_5_) throws CommandSyntaxException {
      Scoreboard scoreboard = p_198658_0_.getServer().getWorldScoreboard();
      int i = 0;

      for(String s : p_198658_1_) {
         Score score = scoreboard.getOrCreateScore(s, p_198658_2_);

         for(String s1 : p_198658_4_) {
            Score score1 = scoreboard.getOrCreateScore(s1, p_198658_5_);
            p_198658_3_.apply(score, score1);
         }

         i += score.getScorePoints();
      }

      if (p_198658_1_.size() == 1) {
         p_198658_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.players.operation.success.single", p_198658_2_.func_197890_e(), p_198658_1_.iterator().next(), i), true);
      } else {
         p_198658_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.players.operation.success.multiple", p_198658_2_.func_197890_e(), p_198658_1_.size()), true);
      }

      return i;
   }

   private static int enableTrigger(CommandSource p_198644_0_, Collection<String> p_198644_1_, ScoreObjective p_198644_2_) throws CommandSyntaxException {
      if (p_198644_2_.func_96680_c() != ScoreCriteria.field_178791_c) {
         throw ENABLE_TRIGGER_INVALID.create();
      } else {
         Scoreboard scoreboard = p_198644_0_.getServer().getWorldScoreboard();
         int i = 0;

         for(String s : p_198644_1_) {
            Score score = scoreboard.getOrCreateScore(s, p_198644_2_);
            if (score.isLocked()) {
               score.setLocked(false);
               ++i;
            }
         }

         if (i == 0) {
            throw ENABLE_TRIGGER_FAILED.create();
         } else {
            if (p_198644_1_.size() == 1) {
               p_198644_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.players.enable.success.single", p_198644_2_.func_197890_e(), p_198644_1_.iterator().next()), true);
            } else {
               p_198644_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.players.enable.success.multiple", p_198644_2_.func_197890_e(), p_198644_1_.size()), true);
            }

            return i;
         }
      }
   }

   private static int resetPlayerAllScores(CommandSource p_198654_0_, Collection<String> p_198654_1_) {
      Scoreboard scoreboard = p_198654_0_.getServer().getWorldScoreboard();

      for(String s : p_198654_1_) {
         scoreboard.removeObjectiveFromEntity(s, (ScoreObjective)null);
      }

      if (p_198654_1_.size() == 1) {
         p_198654_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.players.reset.all.single", p_198654_1_.iterator().next()), true);
      } else {
         p_198654_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.players.reset.all.multiple", p_198654_1_.size()), true);
      }

      return p_198654_1_.size();
   }

   private static int resetPlayerScore(CommandSource p_198656_0_, Collection<String> p_198656_1_, ScoreObjective p_198656_2_) {
      Scoreboard scoreboard = p_198656_0_.getServer().getWorldScoreboard();

      for(String s : p_198656_1_) {
         scoreboard.removeObjectiveFromEntity(s, p_198656_2_);
      }

      if (p_198656_1_.size() == 1) {
         p_198656_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.players.reset.specific.single", p_198656_2_.func_197890_e(), p_198656_1_.iterator().next()), true);
      } else {
         p_198656_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.players.reset.specific.multiple", p_198656_2_.func_197890_e(), p_198656_1_.size()), true);
      }

      return p_198656_1_.size();
   }

   private static int setPlayerScore(CommandSource p_198653_0_, Collection<String> p_198653_1_, ScoreObjective p_198653_2_, int p_198653_3_) {
      Scoreboard scoreboard = p_198653_0_.getServer().getWorldScoreboard();

      for(String s : p_198653_1_) {
         Score score = scoreboard.getOrCreateScore(s, p_198653_2_);
         score.setScorePoints(p_198653_3_);
      }

      if (p_198653_1_.size() == 1) {
         p_198653_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.players.set.success.single", p_198653_2_.func_197890_e(), p_198653_1_.iterator().next(), p_198653_3_), true);
      } else {
         p_198653_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.players.set.success.multiple", p_198653_2_.func_197890_e(), p_198653_1_.size(), p_198653_3_), true);
      }

      return p_198653_3_ * p_198653_1_.size();
   }

   private static int addToPlayerScore(CommandSource p_198633_0_, Collection<String> p_198633_1_, ScoreObjective p_198633_2_, int p_198633_3_) {
      Scoreboard scoreboard = p_198633_0_.getServer().getWorldScoreboard();
      int i = 0;

      for(String s : p_198633_1_) {
         Score score = scoreboard.getOrCreateScore(s, p_198633_2_);
         score.setScorePoints(score.getScorePoints() + p_198633_3_);
         i += score.getScorePoints();
      }

      if (p_198633_1_.size() == 1) {
         p_198633_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.players.add.success.single", p_198633_3_, p_198633_2_.func_197890_e(), p_198633_1_.iterator().next(), i), true);
      } else {
         p_198633_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.players.add.success.multiple", p_198633_3_, p_198633_2_.func_197890_e(), p_198633_1_.size()), true);
      }

      return i;
   }

   private static int removeFromPlayerScore(CommandSource p_198651_0_, Collection<String> p_198651_1_, ScoreObjective p_198651_2_, int p_198651_3_) {
      Scoreboard scoreboard = p_198651_0_.getServer().getWorldScoreboard();
      int i = 0;

      for(String s : p_198651_1_) {
         Score score = scoreboard.getOrCreateScore(s, p_198651_2_);
         score.setScorePoints(score.getScorePoints() - p_198651_3_);
         i += score.getScorePoints();
      }

      if (p_198651_1_.size() == 1) {
         p_198651_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.players.remove.success.single", p_198651_3_, p_198651_2_.func_197890_e(), p_198651_1_.iterator().next(), i), true);
      } else {
         p_198651_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.players.remove.success.multiple", p_198651_3_, p_198651_2_.func_197890_e(), p_198651_1_.size()), true);
      }

      return i;
   }

   private static int listPlayers(CommandSource p_198661_0_) {
      Collection<String> collection = p_198661_0_.getServer().getWorldScoreboard().getObjectiveNames();
      if (collection.isEmpty()) {
         p_198661_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.players.list.empty"), false);
      } else {
         p_198661_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.players.list.success", collection.size(), TextComponentUtils.makeGreenSortedList(collection)), false);
      }

      return collection.size();
   }

   private static int listPlayerScores(CommandSource p_198643_0_, String p_198643_1_) {
      Map<ScoreObjective, Score> map = p_198643_0_.getServer().getWorldScoreboard().getObjectivesForEntity(p_198643_1_);
      if (map.isEmpty()) {
         p_198643_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.players.list.entity.empty", p_198643_1_), false);
      } else {
         p_198643_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.players.list.entity.success", p_198643_1_, map.size()), false);

         for(Entry<ScoreObjective, Score> entry : map.entrySet()) {
            p_198643_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.players.list.entity.entry", entry.getKey().func_197890_e(), entry.getValue().getScorePoints()), false);
         }
      }

      return map.size();
   }

   private static int clearObjectiveDisplaySlot(CommandSource p_198632_0_, int p_198632_1_) throws CommandSyntaxException {
      Scoreboard scoreboard = p_198632_0_.getServer().getWorldScoreboard();
      if (scoreboard.getObjectiveInDisplaySlot(p_198632_1_) == null) {
         throw DISPLAY_ALREADY_CLEAR_EXCEPTION.create();
      } else {
         scoreboard.setObjectiveInDisplaySlot(p_198632_1_, (ScoreObjective)null);
         p_198632_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.objectives.display.cleared", Scoreboard.getDisplaySlotStrings()[p_198632_1_]), true);
         return 0;
      }
   }

   private static int setObjectiveDisplaySlot(CommandSource p_198659_0_, int p_198659_1_, ScoreObjective p_198659_2_) throws CommandSyntaxException {
      Scoreboard scoreboard = p_198659_0_.getServer().getWorldScoreboard();
      if (scoreboard.getObjectiveInDisplaySlot(p_198659_1_) == p_198659_2_) {
         throw DISPLAY_ALREADY_SET_EXCEPTION.create();
      } else {
         scoreboard.setObjectiveInDisplaySlot(p_198659_1_, p_198659_2_);
         p_198659_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.objectives.display.set", Scoreboard.getDisplaySlotStrings()[p_198659_1_], p_198659_2_.getDisplayName()), true);
         return 0;
      }
   }

   private static int setDisplayName(CommandSource p_211749_0_, ScoreObjective p_211749_1_, ITextComponent p_211749_2_) {
      if (!p_211749_1_.getDisplayName().equals(p_211749_2_)) {
         p_211749_1_.func_199864_a(p_211749_2_);
         p_211749_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.objectives.modify.displayname", p_211749_1_.getName(), p_211749_1_.func_197890_e()), true);
      }

      return 0;
   }

   private static int func_211910_a(CommandSource p_211910_0_, ScoreObjective p_211910_1_, ScoreCriteria.RenderType p_211910_2_) {
      if (p_211910_1_.func_199865_f() != p_211910_2_) {
         p_211910_1_.func_199866_a(p_211910_2_);
         p_211910_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.objectives.modify.rendertype", p_211910_1_.func_197890_e()), true);
      }

      return 0;
   }

   private static int removeObjective(CommandSource p_198637_0_, ScoreObjective p_198637_1_) {
      Scoreboard scoreboard = p_198637_0_.getServer().getWorldScoreboard();
      scoreboard.removeObjective(p_198637_1_);
      p_198637_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.objectives.remove.success", p_198637_1_.func_197890_e()), true);
      return scoreboard.getScoreObjectives().size();
   }

   private static int func_198629_a(CommandSource p_198629_0_, String p_198629_1_, ScoreCriteria p_198629_2_, ITextComponent p_198629_3_) throws CommandSyntaxException {
      Scoreboard scoreboard = p_198629_0_.getServer().getWorldScoreboard();
      if (scoreboard.getObjective(p_198629_1_) != null) {
         throw OBJECTIVE_ALREADY_EXISTS_EXCEPTION.create();
      } else if (p_198629_1_.length() > 16) {
         throw ObjectiveArgument.field_200379_a.create(16);
      } else {
         scoreboard.func_199868_a(p_198629_1_, p_198629_2_, p_198629_3_, p_198629_2_.func_178790_c());
         ScoreObjective scoreobjective = scoreboard.getObjective(p_198629_1_);
         p_198629_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.objectives.add.success", scoreobjective.func_197890_e()), true);
         return scoreboard.getScoreObjectives().size();
      }
   }

   private static int listObjectives(CommandSource p_198662_0_) {
      Collection<ScoreObjective> collection = p_198662_0_.getServer().getWorldScoreboard().getScoreObjectives();
      if (collection.isEmpty()) {
         p_198662_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.objectives.list.empty"), false);
      } else {
         p_198662_0_.sendFeedback(new TextComponentTranslation("commands.scoreboard.objectives.list.success", collection.size(), TextComponentUtils.makeList(collection, ScoreObjective::func_197890_e)), false);
      }

      return collection.size();
   }
}
