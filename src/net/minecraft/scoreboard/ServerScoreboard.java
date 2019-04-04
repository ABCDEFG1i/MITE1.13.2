package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketDisplayObjective;
import net.minecraft.network.play.server.SPacketScoreboardObjective;
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.server.MinecraftServer;

public class ServerScoreboard extends Scoreboard {
   private final MinecraftServer server;
   private final Set<ScoreObjective> addedObjectives = Sets.newHashSet();
   private Runnable[] dirtyRunnables = new Runnable[0];

   public ServerScoreboard(MinecraftServer p_i1501_1_) {
      this.server = p_i1501_1_;
   }

   public void onScoreUpdated(Score p_96536_1_) {
      super.onScoreUpdated(p_96536_1_);
      if (this.addedObjectives.contains(p_96536_1_.getObjective())) {
         this.server.getPlayerList().sendPacketToAllPlayers(new SPacketUpdateScore(ServerScoreboard.Action.CHANGE, p_96536_1_.getObjective().getName(), p_96536_1_.getPlayerName(), p_96536_1_.getScorePoints()));
      }

      this.markSaveDataDirty();
   }

   public void broadcastScoreUpdate(String p_96516_1_) {
      super.broadcastScoreUpdate(p_96516_1_);
      this.server.getPlayerList().sendPacketToAllPlayers(new SPacketUpdateScore(ServerScoreboard.Action.REMOVE, null, p_96516_1_, 0));
      this.markSaveDataDirty();
   }

   public void broadcastScoreUpdate(String p_178820_1_, ScoreObjective p_178820_2_) {
      super.broadcastScoreUpdate(p_178820_1_, p_178820_2_);
      if (this.addedObjectives.contains(p_178820_2_)) {
         this.server.getPlayerList().sendPacketToAllPlayers(new SPacketUpdateScore(ServerScoreboard.Action.REMOVE, p_178820_2_.getName(), p_178820_1_, 0));
      }

      this.markSaveDataDirty();
   }

   public void setObjectiveInDisplaySlot(int p_96530_1_, @Nullable ScoreObjective p_96530_2_) {
      ScoreObjective scoreobjective = this.getObjectiveInDisplaySlot(p_96530_1_);
      super.setObjectiveInDisplaySlot(p_96530_1_, p_96530_2_);
      if (scoreobjective != p_96530_2_ && scoreobjective != null) {
         if (this.getObjectiveDisplaySlotCount(scoreobjective) > 0) {
            this.server.getPlayerList().sendPacketToAllPlayers(new SPacketDisplayObjective(p_96530_1_, p_96530_2_));
         } else {
            this.sendDisplaySlotRemovalPackets(scoreobjective);
         }
      }

      if (p_96530_2_ != null) {
         if (this.addedObjectives.contains(p_96530_2_)) {
            this.server.getPlayerList().sendPacketToAllPlayers(new SPacketDisplayObjective(p_96530_1_, p_96530_2_));
         } else {
            this.addObjective(p_96530_2_);
         }
      }

      this.markSaveDataDirty();
   }

   public boolean func_197901_a(String p_197901_1_, ScorePlayerTeam p_197901_2_) {
      if (super.func_197901_a(p_197901_1_, p_197901_2_)) {
         this.server.getPlayerList().sendPacketToAllPlayers(new SPacketTeams(p_197901_2_, Arrays.asList(p_197901_1_), 3));
         this.markSaveDataDirty();
         return true;
      } else {
         return false;
      }
   }

   public void removePlayerFromTeam(String p_96512_1_, ScorePlayerTeam p_96512_2_) {
      super.removePlayerFromTeam(p_96512_1_, p_96512_2_);
      this.server.getPlayerList().sendPacketToAllPlayers(new SPacketTeams(p_96512_2_, Arrays.asList(p_96512_1_), 4));
      this.markSaveDataDirty();
   }

   public void onScoreObjectiveAdded(ScoreObjective p_96522_1_) {
      super.onScoreObjectiveAdded(p_96522_1_);
      this.markSaveDataDirty();
   }

   public void func_199869_b(ScoreObjective p_199869_1_) {
      super.func_199869_b(p_199869_1_);
      if (this.addedObjectives.contains(p_199869_1_)) {
         this.server.getPlayerList().sendPacketToAllPlayers(new SPacketScoreboardObjective(p_199869_1_, 2));
      }

      this.markSaveDataDirty();
   }

   public void onScoreObjectiveRemoved(ScoreObjective p_96533_1_) {
      super.onScoreObjectiveRemoved(p_96533_1_);
      if (this.addedObjectives.contains(p_96533_1_)) {
         this.sendDisplaySlotRemovalPackets(p_96533_1_);
      }

      this.markSaveDataDirty();
   }

   public void broadcastTeamCreated(ScorePlayerTeam p_96523_1_) {
      super.broadcastTeamCreated(p_96523_1_);
      this.server.getPlayerList().sendPacketToAllPlayers(new SPacketTeams(p_96523_1_, 0));
      this.markSaveDataDirty();
   }

   public void broadcastTeamInfoUpdate(ScorePlayerTeam p_96538_1_) {
      super.broadcastTeamInfoUpdate(p_96538_1_);
      this.server.getPlayerList().sendPacketToAllPlayers(new SPacketTeams(p_96538_1_, 2));
      this.markSaveDataDirty();
   }

   public void broadcastTeamRemove(ScorePlayerTeam p_96513_1_) {
      super.broadcastTeamRemove(p_96513_1_);
      this.server.getPlayerList().sendPacketToAllPlayers(new SPacketTeams(p_96513_1_, 1));
      this.markSaveDataDirty();
   }

   public void addDirtyRunnable(Runnable p_186684_1_) {
      this.dirtyRunnables = Arrays.copyOf(this.dirtyRunnables, this.dirtyRunnables.length + 1);
      this.dirtyRunnables[this.dirtyRunnables.length - 1] = p_186684_1_;
   }

   protected void markSaveDataDirty() {
      for(Runnable runnable : this.dirtyRunnables) {
         runnable.run();
      }

   }

   public List<Packet<?>> getCreatePackets(ScoreObjective p_96550_1_) {
      List<Packet<?>> list = Lists.newArrayList();
      list.add(new SPacketScoreboardObjective(p_96550_1_, 0));

      for(int i = 0; i < 19; ++i) {
         if (this.getObjectiveInDisplaySlot(i) == p_96550_1_) {
            list.add(new SPacketDisplayObjective(i, p_96550_1_));
         }
      }

      for(Score score : this.getSortedScores(p_96550_1_)) {
         list.add(new SPacketUpdateScore(ServerScoreboard.Action.CHANGE, score.getObjective().getName(), score.getPlayerName(), score.getScorePoints()));
      }

      return list;
   }

   public void addObjective(ScoreObjective p_96549_1_) {
      List<Packet<?>> list = this.getCreatePackets(p_96549_1_);

      for(EntityPlayerMP entityplayermp : this.server.getPlayerList().getPlayers()) {
         for(Packet<?> packet : list) {
            entityplayermp.connection.sendPacket(packet);
         }
      }

      this.addedObjectives.add(p_96549_1_);
   }

   public List<Packet<?>> getDestroyPackets(ScoreObjective p_96548_1_) {
      List<Packet<?>> list = Lists.newArrayList();
      list.add(new SPacketScoreboardObjective(p_96548_1_, 1));

      for(int i = 0; i < 19; ++i) {
         if (this.getObjectiveInDisplaySlot(i) == p_96548_1_) {
            list.add(new SPacketDisplayObjective(i, p_96548_1_));
         }
      }

      return list;
   }

   public void sendDisplaySlotRemovalPackets(ScoreObjective p_96546_1_) {
      List<Packet<?>> list = this.getDestroyPackets(p_96546_1_);

      for(EntityPlayerMP entityplayermp : this.server.getPlayerList().getPlayers()) {
         for(Packet<?> packet : list) {
            entityplayermp.connection.sendPacket(packet);
         }
      }

      this.addedObjectives.remove(p_96546_1_);
   }

   public int getObjectiveDisplaySlotCount(ScoreObjective p_96552_1_) {
      int i = 0;

      for(int j = 0; j < 19; ++j) {
         if (this.getObjectiveInDisplaySlot(j) == p_96552_1_) {
            ++i;
         }
      }

      return i;
   }

   public enum Action {
      CHANGE,
      REMOVE
   }
}
