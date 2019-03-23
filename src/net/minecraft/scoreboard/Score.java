package net.minecraft.scoreboard;

import java.util.Comparator;
import javax.annotation.Nullable;

public class Score {
   public static final Comparator<Score> SCORE_COMPARATOR = (p_210221_0_, p_210221_1_) -> {
      if (p_210221_0_.getScorePoints() > p_210221_1_.getScorePoints()) {
         return 1;
      } else {
         return p_210221_0_.getScorePoints() < p_210221_1_.getScorePoints() ? -1 : p_210221_1_.getPlayerName().compareToIgnoreCase(p_210221_0_.getPlayerName());
      }
   };
   private final Scoreboard scoreboard;
   @Nullable
   private final ScoreObjective objective;
   private final String scorePlayerName;
   private int scorePoints;
   private boolean locked;
   private boolean forceUpdate;

   public Score(Scoreboard p_i2309_1_, ScoreObjective p_i2309_2_, String p_i2309_3_) {
      this.scoreboard = p_i2309_1_;
      this.objective = p_i2309_2_;
      this.scorePlayerName = p_i2309_3_;
      this.locked = true;
      this.forceUpdate = true;
   }

   public void increaseScore(int p_96649_1_) {
      if (this.objective.func_96680_c().func_96637_b()) {
         throw new IllegalStateException("Cannot modify read-only score");
      } else {
         this.setScorePoints(this.getScorePoints() + p_96649_1_);
      }
   }

   public void incrementScore() {
      this.increaseScore(1);
   }

   public int getScorePoints() {
      return this.scorePoints;
   }

   public void func_197891_c() {
      this.setScorePoints(0);
   }

   public void setScorePoints(int p_96647_1_) {
      int i = this.scorePoints;
      this.scorePoints = p_96647_1_;
      if (i != p_96647_1_ || this.forceUpdate) {
         this.forceUpdate = false;
         this.getScoreScoreboard().onScoreUpdated(this);
      }

   }

   @Nullable
   public ScoreObjective getObjective() {
      return this.objective;
   }

   public String getPlayerName() {
      return this.scorePlayerName;
   }

   public Scoreboard getScoreScoreboard() {
      return this.scoreboard;
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean p_178815_1_) {
      this.locked = p_178815_1_;
   }
}
