package net.minecraft.server.management;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketUnloadChunk;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerChunkMapEntry {
   private static final Logger LOGGER = LogManager.getLogger();
   private final PlayerChunkMap playerChunkMap;
   private final List<EntityPlayerMP> players = Lists.newArrayList();
   private final ChunkPos pos;
   private short[] changedBlocks = new short[64];
   @Nullable
   private Chunk chunk;
   private int changes;
   private int changedSectionFilter;
   private long lastUpdateInhabitedTime;
   private boolean sentToPlayers;

   public PlayerChunkMapEntry(PlayerChunkMap p_i1518_1_, int p_i1518_2_, int p_i1518_3_) {
      this.playerChunkMap = p_i1518_1_;
      this.pos = new ChunkPos(p_i1518_2_, p_i1518_3_);
      ChunkProviderServer chunkproviderserver = p_i1518_1_.getWorld().getChunkProvider();
      chunkproviderserver.func_212469_a(p_i1518_2_, p_i1518_3_);
      this.chunk = chunkproviderserver.func_186025_d(p_i1518_2_, p_i1518_3_, true, false);
   }

   public ChunkPos getPos() {
      return this.pos;
   }

   public void addPlayer(EntityPlayerMP p_187276_1_) {
      if (this.players.contains(p_187276_1_)) {
         LOGGER.debug("Failed to add player. {} already is in chunk {}, {}", p_187276_1_, this.pos.x, this.pos.z);
      } else {
         if (this.players.isEmpty()) {
            this.lastUpdateInhabitedTime = this.playerChunkMap.getWorld().getTotalWorldTime();
         }

         this.players.add(p_187276_1_);
         if (this.sentToPlayers) {
            this.sendToPlayer(p_187276_1_);
         }

      }
   }

   public void removePlayer(EntityPlayerMP p_187277_1_) {
      if (this.players.contains(p_187277_1_)) {
         if (this.sentToPlayers) {
            p_187277_1_.connection.sendPacket(new SPacketUnloadChunk(this.pos.x, this.pos.z));
         }

         this.players.remove(p_187277_1_);
         if (this.players.isEmpty()) {
            this.playerChunkMap.removeEntry(this);
         }

      }
   }

   public boolean providePlayerChunk(boolean p_187268_1_) {
      if (this.chunk != null) {
         return true;
      } else {
         this.chunk = this.playerChunkMap.getWorld().getChunkProvider().func_186025_d(this.pos.x, this.pos.z, true, p_187268_1_);
         return this.chunk != null;
      }
   }

   public boolean sendToPlayers() {
      if (this.sentToPlayers) {
         return true;
      } else if (this.chunk == null) {
         return false;
      } else if (!this.chunk.isPopulated()) {
         return false;
      } else {
         this.changes = 0;
         this.changedSectionFilter = 0;
         this.sentToPlayers = true;
         if (!this.players.isEmpty()) {
            Packet<?> packet = new SPacketChunkData(this.chunk, 65535);

            for(EntityPlayerMP entityplayermp : this.players) {
               entityplayermp.connection.sendPacket(packet);
               this.playerChunkMap.getWorld().getEntityTracker().sendLeashedEntitiesInChunk(entityplayermp, this.chunk);
            }
         }

         return true;
      }
   }

   public void sendToPlayer(EntityPlayerMP p_187278_1_) {
      if (this.sentToPlayers) {
         p_187278_1_.connection.sendPacket(new SPacketChunkData(this.chunk, 65535));
         this.playerChunkMap.getWorld().getEntityTracker().sendLeashedEntitiesInChunk(p_187278_1_, this.chunk);
      }
   }

   public void updateChunkInhabitedTime() {
      long i = this.playerChunkMap.getWorld().getTotalWorldTime();
      if (this.chunk != null) {
         this.chunk.setInhabitedTime(this.chunk.getInhabitedTime() + i - this.lastUpdateInhabitedTime);
      }

      this.lastUpdateInhabitedTime = i;
   }

   public void blockChanged(int p_187265_1_, int p_187265_2_, int p_187265_3_) {
      if (this.sentToPlayers) {
         if (this.changes == 0) {
            this.playerChunkMap.entryChanged(this);
         }

         this.changedSectionFilter |= 1 << (p_187265_2_ >> 4);
         if (this.changes < 64) {
            short short1 = (short)(p_187265_1_ << 12 | p_187265_3_ << 8 | p_187265_2_);

            for(int i = 0; i < this.changes; ++i) {
               if (this.changedBlocks[i] == short1) {
                  return;
               }
            }

            this.changedBlocks[this.changes++] = short1;
         }

      }
   }

   public void sendPacket(Packet<?> p_187267_1_) {
      if (this.sentToPlayers) {
         for(int i = 0; i < this.players.size(); ++i) {
            (this.players.get(i)).connection.sendPacket(p_187267_1_);
         }

      }
   }

   public void tick() {
      if (this.sentToPlayers && this.chunk != null) {
         if (this.changes != 0) {
            if (this.changes == 1) {
               int i = (this.changedBlocks[0] >> 12 & 15) + this.pos.x * 16;
               int j = this.changedBlocks[0] & 255;
               int k = (this.changedBlocks[0] >> 8 & 15) + this.pos.z * 16;
               BlockPos blockpos = new BlockPos(i, j, k);
               this.sendPacket(new SPacketBlockChange(this.playerChunkMap.getWorld(), blockpos));
               if (this.playerChunkMap.getWorld().getBlockState(blockpos).getBlock().hasTileEntity()) {
                  this.sendBlockEntity(this.playerChunkMap.getWorld().getTileEntity(blockpos));
               }
            } else if (this.changes == 64) {
               this.sendPacket(new SPacketChunkData(this.chunk, this.changedSectionFilter));
            } else {
               this.sendPacket(new SPacketMultiBlockChange(this.changes, this.changedBlocks, this.chunk));

               for(int l = 0; l < this.changes; ++l) {
                  int i1 = (this.changedBlocks[l] >> 12 & 15) + this.pos.x * 16;
                  int j1 = this.changedBlocks[l] & 255;
                  int k1 = (this.changedBlocks[l] >> 8 & 15) + this.pos.z * 16;
                  BlockPos blockpos1 = new BlockPos(i1, j1, k1);
                  if (this.playerChunkMap.getWorld().getBlockState(blockpos1).getBlock().hasTileEntity()) {
                     this.sendBlockEntity(this.playerChunkMap.getWorld().getTileEntity(blockpos1));
                  }
               }
            }

            this.changes = 0;
            this.changedSectionFilter = 0;
         }
      }
   }

   private void sendBlockEntity(@Nullable TileEntity p_187273_1_) {
      if (p_187273_1_ != null) {
         SPacketUpdateTileEntity spacketupdatetileentity = p_187273_1_.getUpdatePacket();
         if (spacketupdatetileentity != null) {
            this.sendPacket(spacketupdatetileentity);
         }
      }

   }

   public boolean containsPlayer(EntityPlayerMP p_187275_1_) {
      return this.players.contains(p_187275_1_);
   }

   public boolean hasPlayerMatching(Predicate<EntityPlayerMP> p_187269_1_) {
      return this.players.stream().anyMatch(p_187269_1_);
   }

   public boolean hasPlayerMatchingInRange(double p_187271_1_, Predicate<EntityPlayerMP> p_187271_3_) {
      int i = 0;

      for(int j = this.players.size(); i < j; ++i) {
         EntityPlayerMP entityplayermp = this.players.get(i);
         if (p_187271_3_.test(entityplayermp) && this.pos.getDistanceSq(entityplayermp) < p_187271_1_ * p_187271_1_) {
            return true;
         }
      }

      return false;
   }

   public boolean isSentToPlayers() {
      return this.sentToPlayers;
   }

   @Nullable
   public Chunk getChunk() {
      return this.chunk;
   }

   public double getClosestPlayerDistance() {
      double d0 = Double.MAX_VALUE;

      for(EntityPlayerMP entityplayermp : this.players) {
         double d1 = this.pos.getDistanceSq(entityplayermp);
         if (d1 < d0) {
            d0 = d1;
         }
      }

      return d0;
   }
}
