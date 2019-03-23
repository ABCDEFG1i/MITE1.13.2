package net.minecraft.scoreboard;

import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.storage.WorldSavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScoreboardSaveData extends WorldSavedData {
   private static final Logger LOGGER = LogManager.getLogger();
   private Scoreboard scoreboard;
   private NBTTagCompound delayedInitNbt;

   public ScoreboardSaveData() {
      this("scoreboard");
   }

   public ScoreboardSaveData(String p_i2310_1_) {
      super(p_i2310_1_);
   }

   public void setScoreboard(Scoreboard p_96499_1_) {
      this.scoreboard = p_96499_1_;
      if (this.delayedInitNbt != null) {
         this.readFromNBT(this.delayedInitNbt);
      }

   }

   public void readFromNBT(NBTTagCompound p_76184_1_) {
      if (this.scoreboard == null) {
         this.delayedInitNbt = p_76184_1_;
      } else {
         this.readObjectives(p_76184_1_.getTagList("Objectives", 10));
         this.scoreboard.func_197905_a(p_76184_1_.getTagList("PlayerScores", 10));
         if (p_76184_1_.hasKey("DisplaySlots", 10)) {
            this.readDisplayConfig(p_76184_1_.getCompoundTag("DisplaySlots"));
         }

         if (p_76184_1_.hasKey("Teams", 9)) {
            this.readTeams(p_76184_1_.getTagList("Teams", 10));
         }

      }
   }

   protected void readTeams(NBTTagList p_96498_1_) {
      for(int i = 0; i < p_96498_1_.size(); ++i) {
         NBTTagCompound nbttagcompound = p_96498_1_.getCompoundTagAt(i);
         String s = nbttagcompound.getString("Name");
         if (s.length() > 16) {
            s = s.substring(0, 16);
         }

         ScorePlayerTeam scoreplayerteam = this.scoreboard.createTeam(s);
         ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(nbttagcompound.getString("DisplayName"));
         if (itextcomponent != null) {
            scoreplayerteam.setDisplayName(itextcomponent);
         }

         if (nbttagcompound.hasKey("TeamColor", 8)) {
            scoreplayerteam.setColor(TextFormatting.getValueByName(nbttagcompound.getString("TeamColor")));
         }

         if (nbttagcompound.hasKey("AllowFriendlyFire", 99)) {
            scoreplayerteam.setAllowFriendlyFire(nbttagcompound.getBoolean("AllowFriendlyFire"));
         }

         if (nbttagcompound.hasKey("SeeFriendlyInvisibles", 99)) {
            scoreplayerteam.setSeeFriendlyInvisiblesEnabled(nbttagcompound.getBoolean("SeeFriendlyInvisibles"));
         }

         if (nbttagcompound.hasKey("MemberNamePrefix", 8)) {
            ITextComponent itextcomponent1 = ITextComponent.Serializer.fromJson(nbttagcompound.getString("MemberNamePrefix"));
            if (itextcomponent1 != null) {
               scoreplayerteam.setPrefix(itextcomponent1);
            }
         }

         if (nbttagcompound.hasKey("MemberNameSuffix", 8)) {
            ITextComponent itextcomponent2 = ITextComponent.Serializer.fromJson(nbttagcompound.getString("MemberNameSuffix"));
            if (itextcomponent2 != null) {
               scoreplayerteam.setSuffix(itextcomponent2);
            }
         }

         if (nbttagcompound.hasKey("NameTagVisibility", 8)) {
            Team.EnumVisible team$enumvisible = Team.EnumVisible.getByName(nbttagcompound.getString("NameTagVisibility"));
            if (team$enumvisible != null) {
               scoreplayerteam.setNameTagVisibility(team$enumvisible);
            }
         }

         if (nbttagcompound.hasKey("DeathMessageVisibility", 8)) {
            Team.EnumVisible team$enumvisible1 = Team.EnumVisible.getByName(nbttagcompound.getString("DeathMessageVisibility"));
            if (team$enumvisible1 != null) {
               scoreplayerteam.setDeathMessageVisibility(team$enumvisible1);
            }
         }

         if (nbttagcompound.hasKey("CollisionRule", 8)) {
            Team.CollisionRule team$collisionrule = Team.CollisionRule.getByName(nbttagcompound.getString("CollisionRule"));
            if (team$collisionrule != null) {
               scoreplayerteam.setCollisionRule(team$collisionrule);
            }
         }

         this.loadTeamPlayers(scoreplayerteam, nbttagcompound.getTagList("Players", 8));
      }

   }

   protected void loadTeamPlayers(ScorePlayerTeam p_96502_1_, NBTTagList p_96502_2_) {
      for(int i = 0; i < p_96502_2_.size(); ++i) {
         this.scoreboard.func_197901_a(p_96502_2_.getStringTagAt(i), p_96502_1_);
      }

   }

   protected void readDisplayConfig(NBTTagCompound p_96504_1_) {
      for(int i = 0; i < 19; ++i) {
         if (p_96504_1_.hasKey("slot_" + i, 8)) {
            String s = p_96504_1_.getString("slot_" + i);
            ScoreObjective scoreobjective = this.scoreboard.getObjective(s);
            this.scoreboard.setObjectiveInDisplaySlot(i, scoreobjective);
         }
      }

   }

   protected void readObjectives(NBTTagList p_96501_1_) {
      for(int i = 0; i < p_96501_1_.size(); ++i) {
         NBTTagCompound nbttagcompound = p_96501_1_.getCompoundTagAt(i);
         ScoreCriteria scorecriteria = ScoreCriteria.func_197911_a(nbttagcompound.getString("CriteriaName"));
         if (scorecriteria != null) {
            String s = nbttagcompound.getString("Name");
            if (s.length() > 16) {
               s = s.substring(0, 16);
            }

            ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(nbttagcompound.getString("DisplayName"));
            ScoreCriteria.RenderType scorecriteria$rendertype = ScoreCriteria.RenderType.func_211839_a(nbttagcompound.getString("RenderType"));
            this.scoreboard.func_199868_a(s, scorecriteria, itextcomponent, scorecriteria$rendertype);
         }
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189551_1_) {
      if (this.scoreboard == null) {
         LOGGER.warn("Tried to save scoreboard without having a scoreboard...");
         return p_189551_1_;
      } else {
         p_189551_1_.setTag("Objectives", this.objectivesToNbt());
         p_189551_1_.setTag("PlayerScores", this.scoreboard.func_197902_i());
         p_189551_1_.setTag("Teams", this.teamsToNbt());
         this.fillInDisplaySlots(p_189551_1_);
         return p_189551_1_;
      }
   }

   protected NBTTagList teamsToNbt() {
      NBTTagList nbttaglist = new NBTTagList();

      for(ScorePlayerTeam scoreplayerteam : this.scoreboard.getTeams()) {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         nbttagcompound.setString("Name", scoreplayerteam.getName());
         nbttagcompound.setString("DisplayName", ITextComponent.Serializer.toJson(scoreplayerteam.getDisplayName()));
         if (scoreplayerteam.getColor().getColorIndex() >= 0) {
            nbttagcompound.setString("TeamColor", scoreplayerteam.getColor().getFriendlyName());
         }

         nbttagcompound.setBoolean("AllowFriendlyFire", scoreplayerteam.getAllowFriendlyFire());
         nbttagcompound.setBoolean("SeeFriendlyInvisibles", scoreplayerteam.getSeeFriendlyInvisiblesEnabled());
         nbttagcompound.setString("MemberNamePrefix", ITextComponent.Serializer.toJson(scoreplayerteam.getPrefix()));
         nbttagcompound.setString("MemberNameSuffix", ITextComponent.Serializer.toJson(scoreplayerteam.getSuffix()));
         nbttagcompound.setString("NameTagVisibility", scoreplayerteam.getNameTagVisibility().internalName);
         nbttagcompound.setString("DeathMessageVisibility", scoreplayerteam.getDeathMessageVisibility().internalName);
         nbttagcompound.setString("CollisionRule", scoreplayerteam.getCollisionRule().name);
         NBTTagList nbttaglist1 = new NBTTagList();

         for(String s : scoreplayerteam.getMembershipCollection()) {
            nbttaglist1.add((INBTBase)(new NBTTagString(s)));
         }

         nbttagcompound.setTag("Players", nbttaglist1);
         nbttaglist.add((INBTBase)nbttagcompound);
      }

      return nbttaglist;
   }

   protected void fillInDisplaySlots(NBTTagCompound p_96497_1_) {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      boolean flag = false;

      for(int i = 0; i < 19; ++i) {
         ScoreObjective scoreobjective = this.scoreboard.getObjectiveInDisplaySlot(i);
         if (scoreobjective != null) {
            nbttagcompound.setString("slot_" + i, scoreobjective.getName());
            flag = true;
         }
      }

      if (flag) {
         p_96497_1_.setTag("DisplaySlots", nbttagcompound);
      }

   }

   protected NBTTagList objectivesToNbt() {
      NBTTagList nbttaglist = new NBTTagList();

      for(ScoreObjective scoreobjective : this.scoreboard.getScoreObjectives()) {
         if (scoreobjective.func_96680_c() != null) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setString("Name", scoreobjective.getName());
            nbttagcompound.setString("CriteriaName", scoreobjective.func_96680_c().func_96636_a());
            nbttagcompound.setString("DisplayName", ITextComponent.Serializer.toJson(scoreobjective.getDisplayName()));
            nbttagcompound.setString("RenderType", scoreobjective.func_199865_f().func_211838_a());
            nbttaglist.add((INBTBase)nbttagcompound);
         }
      }

      return nbttaglist;
   }
}
