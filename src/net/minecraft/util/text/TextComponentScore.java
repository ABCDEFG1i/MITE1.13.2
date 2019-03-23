package net.minecraft.util.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.EntitySelectorParser;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;

public class TextComponentScore extends TextComponentBase {
   private final String name;
   @Nullable
   private final EntitySelector field_197667_c;
   private final String objective;
   private String value = "";

   public TextComponentScore(String p_i45997_1_, String p_i45997_2_) {
      this.name = p_i45997_1_;
      this.objective = p_i45997_2_;
      EntitySelector entityselector = null;

      try {
         EntitySelectorParser entityselectorparser = new EntitySelectorParser(new StringReader(p_i45997_1_));
         entityselector = entityselectorparser.parse();
      } catch (CommandSyntaxException var5) {
         ;
      }

      this.field_197667_c = entityselector;
   }

   public String getName() {
      return this.name;
   }

   @Nullable
   public EntitySelector func_197666_h() {
      return this.field_197667_c;
   }

   public String getObjective() {
      return this.objective;
   }

   public void setValue(String p_179997_1_) {
      this.value = p_179997_1_;
   }

   public String getUnformattedComponentText() {
      return this.value;
   }

   public void func_197665_b(CommandSource p_197665_1_) {
      MinecraftServer minecraftserver = p_197665_1_.getServer();
      if (minecraftserver != null && minecraftserver.isAnvilFileSet() && StringUtils.isNullOrEmpty(this.value)) {
         Scoreboard scoreboard = minecraftserver.getWorldScoreboard();
         ScoreObjective scoreobjective = scoreboard.getObjective(this.objective);
         if (scoreboard.entityHasObjective(this.name, scoreobjective)) {
            Score score = scoreboard.getOrCreateScore(this.name, scoreobjective);
            this.setValue(String.format("%d", score.getScorePoints()));
         } else {
            this.value = "";
         }
      }

   }

   public TextComponentScore createCopy() {
      TextComponentScore textcomponentscore = new TextComponentScore(this.name, this.objective);
      textcomponentscore.setValue(this.value);
      return textcomponentscore;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof TextComponentScore)) {
         return false;
      } else {
         TextComponentScore textcomponentscore = (TextComponentScore)p_equals_1_;
         return this.name.equals(textcomponentscore.name) && this.objective.equals(textcomponentscore.objective) && super.equals(p_equals_1_);
      }
   }

   public String toString() {
      return "ScoreComponent{name='" + this.name + '\'' + "objective='" + this.objective + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }
}
