package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Scoreboard {
   private final Map<String, ScoreObjective> scoreObjectives = Maps.newHashMap();
   private final Map<ScoreCriteria, List<ScoreObjective>> scoreObjectiveCriterias = Maps.newHashMap();
   private final Map<String, Map<ScoreObjective, Score>> entitiesScoreObjectives = Maps.newHashMap();
   private final ScoreObjective[] objectiveDisplaySlots = new ScoreObjective[19];
   private final Map<String, ScorePlayerTeam> teams = Maps.newHashMap();
   private final Map<String, ScorePlayerTeam> teamMemberships = Maps.newHashMap();
   private static String[] displaySlots;

   @OnlyIn(Dist.CLIENT)
   public boolean func_197900_b(String p_197900_1_) {
      return this.scoreObjectives.containsKey(p_197900_1_);
   }

   public ScoreObjective func_197899_c(String p_197899_1_) {
      return this.scoreObjectives.get(p_197899_1_);
   }

   @Nullable
   public ScoreObjective getObjective(@Nullable String p_96518_1_) {
      return this.scoreObjectives.get(p_96518_1_);
   }

   public ScoreObjective func_199868_a(String p_199868_1_, ScoreCriteria p_199868_2_, ITextComponent p_199868_3_, ScoreCriteria.RenderType p_199868_4_) {
      if (p_199868_1_.length() > 16) {
         throw new IllegalArgumentException("The objective name '" + p_199868_1_ + "' is too long!");
      } else if (this.scoreObjectives.containsKey(p_199868_1_)) {
         throw new IllegalArgumentException("An objective with the name '" + p_199868_1_ + "' already exists!");
      } else {
         ScoreObjective scoreobjective = new ScoreObjective(this, p_199868_1_, p_199868_2_, p_199868_3_, p_199868_4_);
         this.scoreObjectiveCriterias.computeIfAbsent(p_199868_2_, (p_197903_0_) -> {
            return Lists.newArrayList();
         }).add(scoreobjective);
         this.scoreObjectives.put(p_199868_1_, scoreobjective);
         this.onScoreObjectiveAdded(scoreobjective);
         return scoreobjective;
      }
   }

   public final void func_197893_a(ScoreCriteria p_197893_1_, String p_197893_2_, Consumer<Score> p_197893_3_) {
      this.scoreObjectiveCriterias.getOrDefault(p_197893_1_, Collections.emptyList()).forEach((p_197906_3_) -> {
         p_197893_3_.accept(this.getOrCreateScore(p_197893_2_, p_197906_3_));
      });
   }

   public boolean entityHasObjective(String p_178819_1_, ScoreObjective p_178819_2_) {
      Map<ScoreObjective, Score> map = this.entitiesScoreObjectives.get(p_178819_1_);
      if (map == null) {
         return false;
      } else {
         Score score = map.get(p_178819_2_);
         return score != null;
      }
   }

   public Score getOrCreateScore(String p_96529_1_, ScoreObjective p_96529_2_) {
      if (p_96529_1_.length() > 40) {
         throw new IllegalArgumentException("The player name '" + p_96529_1_ + "' is too long!");
      } else {
         Map<ScoreObjective, Score> map = this.entitiesScoreObjectives.computeIfAbsent(p_96529_1_, (p_197898_0_) -> {
            return Maps.newHashMap();
         });
         return map.computeIfAbsent(p_96529_2_, (p_197904_2_) -> {
            Score score = new Score(this, p_197904_2_, p_96529_1_);
            score.setScorePoints(0);
            return score;
         });
      }
   }

   public Collection<Score> getSortedScores(ScoreObjective p_96534_1_) {
      List<Score> list = Lists.newArrayList();

      for(Map<ScoreObjective, Score> map : this.entitiesScoreObjectives.values()) {
         Score score = map.get(p_96534_1_);
         if (score != null) {
            list.add(score);
         }
      }

      Collections.sort(list, Score.SCORE_COMPARATOR);
      return list;
   }

   public Collection<ScoreObjective> getScoreObjectives() {
      return this.scoreObjectives.values();
   }

   public Collection<String> func_197897_d() {
      return this.scoreObjectives.keySet();
   }

   public Collection<String> getObjectiveNames() {
      return Lists.newArrayList(this.entitiesScoreObjectives.keySet());
   }

   public void removeObjectiveFromEntity(String p_178822_1_, @Nullable ScoreObjective p_178822_2_) {
      if (p_178822_2_ == null) {
         Map<ScoreObjective, Score> map = this.entitiesScoreObjectives.remove(p_178822_1_);
         if (map != null) {
            this.broadcastScoreUpdate(p_178822_1_);
         }
      } else {
         Map<ScoreObjective, Score> map2 = this.entitiesScoreObjectives.get(p_178822_1_);
         if (map2 != null) {
            Score score = map2.remove(p_178822_2_);
            if (map2.size() < 1) {
               Map<ScoreObjective, Score> map1 = this.entitiesScoreObjectives.remove(p_178822_1_);
               if (map1 != null) {
                  this.broadcastScoreUpdate(p_178822_1_);
               }
            } else if (score != null) {
               this.broadcastScoreUpdate(p_178822_1_, p_178822_2_);
            }
         }
      }

   }

   public Map<ScoreObjective, Score> getObjectivesForEntity(String p_96510_1_) {
      Map<ScoreObjective, Score> map = this.entitiesScoreObjectives.get(p_96510_1_);
      if (map == null) {
         map = Maps.newHashMap();
      }

      return map;
   }

   public void removeObjective(ScoreObjective p_96519_1_) {
      this.scoreObjectives.remove(p_96519_1_.getName());

      for(int i = 0; i < 19; ++i) {
         if (this.getObjectiveInDisplaySlot(i) == p_96519_1_) {
            this.setObjectiveInDisplaySlot(i, null);
         }
      }

      List<ScoreObjective> list = this.scoreObjectiveCriterias.get(p_96519_1_.func_96680_c());
      if (list != null) {
         list.remove(p_96519_1_);
      }

      for(Map<ScoreObjective, Score> map : this.entitiesScoreObjectives.values()) {
         map.remove(p_96519_1_);
      }

      this.onScoreObjectiveRemoved(p_96519_1_);
   }

   public void setObjectiveInDisplaySlot(int p_96530_1_, @Nullable ScoreObjective p_96530_2_) {
      this.objectiveDisplaySlots[p_96530_1_] = p_96530_2_;
   }

   @Nullable
   public ScoreObjective getObjectiveInDisplaySlot(int p_96539_1_) {
      return this.objectiveDisplaySlots[p_96539_1_];
   }

   public ScorePlayerTeam getTeam(String p_96508_1_) {
      return this.teams.get(p_96508_1_);
   }

   public ScorePlayerTeam createTeam(String p_96527_1_) {
      if (p_96527_1_.length() > 16) {
         throw new IllegalArgumentException("The team name '" + p_96527_1_ + "' is too long!");
      } else {
         ScorePlayerTeam scoreplayerteam = this.getTeam(p_96527_1_);
         if (scoreplayerteam != null) {
            throw new IllegalArgumentException("A team with the name '" + p_96527_1_ + "' already exists!");
         } else {
            scoreplayerteam = new ScorePlayerTeam(this, p_96527_1_);
            this.teams.put(p_96527_1_, scoreplayerteam);
            this.broadcastTeamCreated(scoreplayerteam);
            return scoreplayerteam;
         }
      }
   }

   public void removeTeam(ScorePlayerTeam p_96511_1_) {
      this.teams.remove(p_96511_1_.getName());

      for(String s : p_96511_1_.getMembershipCollection()) {
         this.teamMemberships.remove(s);
      }

      this.broadcastTeamRemove(p_96511_1_);
   }

   public boolean func_197901_a(String p_197901_1_, ScorePlayerTeam p_197901_2_) {
      if (p_197901_1_.length() > 40) {
         throw new IllegalArgumentException("The player name '" + p_197901_1_ + "' is too long!");
      } else {
         if (this.getPlayersTeam(p_197901_1_) != null) {
            this.removePlayerFromTeams(p_197901_1_);
         }

         this.teamMemberships.put(p_197901_1_, p_197901_2_);
         return p_197901_2_.getMembershipCollection().add(p_197901_1_);
      }
   }

   public boolean removePlayerFromTeams(String p_96524_1_) {
      ScorePlayerTeam scoreplayerteam = this.getPlayersTeam(p_96524_1_);
      if (scoreplayerteam != null) {
         this.removePlayerFromTeam(p_96524_1_, scoreplayerteam);
         return true;
      } else {
         return false;
      }
   }

   public void removePlayerFromTeam(String p_96512_1_, ScorePlayerTeam p_96512_2_) {
      if (this.getPlayersTeam(p_96512_1_) != p_96512_2_) {
         throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + p_96512_2_.getName() + "'.");
      } else {
         this.teamMemberships.remove(p_96512_1_);
         p_96512_2_.getMembershipCollection().remove(p_96512_1_);
      }
   }

   public Collection<String> getTeamNames() {
      return this.teams.keySet();
   }

   public Collection<ScorePlayerTeam> getTeams() {
      return this.teams.values();
   }

   @Nullable
   public ScorePlayerTeam getPlayersTeam(String p_96509_1_) {
      return this.teamMemberships.get(p_96509_1_);
   }

   public void onScoreObjectiveAdded(ScoreObjective p_96522_1_) {
   }

   public void func_199869_b(ScoreObjective p_199869_1_) {
   }

   public void onScoreObjectiveRemoved(ScoreObjective p_96533_1_) {
   }

   public void onScoreUpdated(Score p_96536_1_) {
   }

   public void broadcastScoreUpdate(String p_96516_1_) {
   }

   public void broadcastScoreUpdate(String p_178820_1_, ScoreObjective p_178820_2_) {
   }

   public void broadcastTeamCreated(ScorePlayerTeam p_96523_1_) {
   }

   public void broadcastTeamInfoUpdate(ScorePlayerTeam p_96538_1_) {
   }

   public void broadcastTeamRemove(ScorePlayerTeam p_96513_1_) {
   }

   public static String getObjectiveDisplaySlot(int p_96517_0_) {
      switch(p_96517_0_) {
      case 0:
         return "list";
      case 1:
         return "sidebar";
      case 2:
         return "belowName";
      default:
         if (p_96517_0_ >= 3 && p_96517_0_ <= 18) {
            TextFormatting textformatting = TextFormatting.fromColorIndex(p_96517_0_ - 3);
            if (textformatting != null && textformatting != TextFormatting.RESET) {
               return "sidebar.team." + textformatting.getFriendlyName();
            }
         }

         return null;
      }
   }

   public static int getObjectiveDisplaySlotNumber(String p_96537_0_) {
      if ("list".equalsIgnoreCase(p_96537_0_)) {
         return 0;
      } else if ("sidebar".equalsIgnoreCase(p_96537_0_)) {
         return 1;
      } else if ("belowName".equalsIgnoreCase(p_96537_0_)) {
         return 2;
      } else {
         if (p_96537_0_.startsWith("sidebar.team.")) {
            String s = p_96537_0_.substring("sidebar.team.".length());
            TextFormatting textformatting = TextFormatting.getValueByName(s);
            if (textformatting != null && textformatting.getColorIndex() >= 0) {
               return textformatting.getColorIndex() + 3;
            }
         }

         return -1;
      }
   }

   public static String[] getDisplaySlotStrings() {
      if (displaySlots == null) {
         displaySlots = new String[19];

         for(int i = 0; i < 19; ++i) {
            displaySlots[i] = getObjectiveDisplaySlot(i);
         }
      }

      return displaySlots;
   }

   public void removeEntity(Entity p_181140_1_) {
      if (p_181140_1_ != null && !(p_181140_1_ instanceof EntityPlayer) && !p_181140_1_.isEntityAlive()) {
         String s = p_181140_1_.getCachedUniqueIdString();
         this.removeObjectiveFromEntity(s, null);
         this.removePlayerFromTeams(s);
      }
   }

   protected NBTTagList func_197902_i() {
      NBTTagList nbttaglist = new NBTTagList();
      this.entitiesScoreObjectives.values().stream().map(Map::values).forEach((p_197894_1_) -> {
         p_197894_1_.stream().filter((p_209546_0_) -> {
            return p_209546_0_.getObjective() != null;
         }).forEach((p_197896_1_) -> {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setString("Name", p_197896_1_.getPlayerName());
            nbttagcompound.setString("Objective", p_197896_1_.getObjective().getName());
            nbttagcompound.setInteger("Score", p_197896_1_.getScorePoints());
            nbttagcompound.setBoolean("Locked", p_197896_1_.isLocked());
            nbttaglist.add(nbttagcompound);
         });
      });
      return nbttaglist;
   }

   protected void func_197905_a(NBTTagList p_197905_1_) {
      for(int i = 0; i < p_197905_1_.size(); ++i) {
         NBTTagCompound nbttagcompound = p_197905_1_.getCompoundTagAt(i);
         ScoreObjective scoreobjective = this.func_197899_c(nbttagcompound.getString("Objective"));
         String s = nbttagcompound.getString("Name");
         if (s.length() > 40) {
            s = s.substring(0, 40);
         }

         Score score = this.getOrCreateScore(s, scoreobjective);
         score.setScorePoints(nbttagcompound.getInteger("Score"));
         if (nbttagcompound.hasKey("Locked")) {
            score.setLocked(nbttagcompound.getBoolean("Locked"));
         }
      }

   }
}
