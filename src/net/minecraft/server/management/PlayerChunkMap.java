package net.minecraft.server.management;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.Dimension;

public class PlayerChunkMap {
   private static final Predicate<EntityPlayerMP> NOT_SPECTATOR = (p_210471_0_) -> {
      return p_210471_0_ != null && !p_210471_0_.isSpectator();
   };
   private static final Predicate<EntityPlayerMP> CAN_GENERATE_CHUNKS = (p_210472_0_) -> {
      return p_210472_0_ != null && (!p_210472_0_.isSpectator() || p_210472_0_.getServerWorld().getGameRules().getBoolean("spectatorsGenerateChunks"));
   };
   private final WorldServer world;
   private final List<EntityPlayerMP> players = Lists.newArrayList();
   private final Long2ObjectMap<PlayerChunkMapEntry> entryMap = new Long2ObjectOpenHashMap<>(4096);
   private final Set<PlayerChunkMapEntry> dirtyEntries = Sets.newHashSet();
   private final List<PlayerChunkMapEntry> pendingSendToPlayers = Lists.newLinkedList();
   private final List<PlayerChunkMapEntry> entriesWithoutChunks = Lists.newLinkedList();
   private final List<PlayerChunkMapEntry> entries = Lists.newArrayList();
   private int playerViewRadius;
   private long previousTotalWorldTime;
   private boolean sortMissingChunks = true;
   private boolean sortSendToPlayers = true;

   public PlayerChunkMap(WorldServer p_i1176_1_) {
      this.world = p_i1176_1_;
      this.setPlayerViewRadius(p_i1176_1_.getServer().getPlayerList().getViewDistance());
   }

   public WorldServer getWorld() {
      return this.world;
   }

   public Iterator<Chunk> getChunkIterator() {
      final Iterator<PlayerChunkMapEntry> iterator = this.entries.iterator();
      return new AbstractIterator<Chunk>() {
         protected Chunk computeNext() {
            while(true) {
               if (iterator.hasNext()) {
                  PlayerChunkMapEntry playerchunkmapentry = iterator.next();
                  Chunk chunk = playerchunkmapentry.getChunk();
                  if (chunk == null) {
                     continue;
                  }

                  if (!chunk.wasTicked()) {
                     return chunk;
                  }

                  if (!playerchunkmapentry.hasPlayerMatchingInRange(128.0D, PlayerChunkMap.NOT_SPECTATOR)) {
                     continue;
                  }

                  return chunk;
               }

               return this.endOfData();
            }
         }
      };
   }

   public void tick() {
      long i = this.world.getTotalWorldTime();
      if (i - this.previousTotalWorldTime > 8000L) {
         this.previousTotalWorldTime = i;

         for(int j = 0; j < this.entries.size(); ++j) {
            PlayerChunkMapEntry playerchunkmapentry = this.entries.get(j);
            playerchunkmapentry.tick();
            playerchunkmapentry.updateChunkInhabitedTime();
         }
      }

      if (!this.dirtyEntries.isEmpty()) {
         for(PlayerChunkMapEntry playerchunkmapentry2 : this.dirtyEntries) {
            playerchunkmapentry2.tick();
         }

         this.dirtyEntries.clear();
      }

      if (this.sortMissingChunks && i % 4L == 0L) {
         this.sortMissingChunks = false;
         Collections.sort(this.entriesWithoutChunks, (p_210473_0_, p_210473_1_) -> {
            return ComparisonChain.start().compare(p_210473_0_.getClosestPlayerDistance(), p_210473_1_.getClosestPlayerDistance()).result();
         });
      }

      if (this.sortSendToPlayers && i % 4L == 2L) {
         this.sortSendToPlayers = false;
         Collections.sort(this.pendingSendToPlayers, (p_210470_0_, p_210470_1_) -> {
            return ComparisonChain.start().compare(p_210470_0_.getClosestPlayerDistance(), p_210470_1_.getClosestPlayerDistance()).result();
         });
      }

      if (!this.entriesWithoutChunks.isEmpty()) {
         long l = Util.nanoTime() + 50000000L;
         int k = 49;
         Iterator<PlayerChunkMapEntry> iterator = this.entriesWithoutChunks.iterator();

         while(iterator.hasNext()) {
            PlayerChunkMapEntry playerchunkmapentry1 = iterator.next();
            if (playerchunkmapentry1.getChunk() == null) {
               boolean flag = playerchunkmapentry1.hasPlayerMatching(CAN_GENERATE_CHUNKS);
               if (playerchunkmapentry1.providePlayerChunk(flag)) {
                  iterator.remove();
                  if (playerchunkmapentry1.sendToPlayers()) {
                     this.pendingSendToPlayers.remove(playerchunkmapentry1);
                  }

                  --k;
                  if (k < 0 || Util.nanoTime() > l) {
                     break;
                  }
               }
            }
         }
      }

      if (!this.pendingSendToPlayers.isEmpty()) {
         int i1 = 81;
         Iterator<PlayerChunkMapEntry> iterator1 = this.pendingSendToPlayers.iterator();

         while(iterator1.hasNext()) {
            PlayerChunkMapEntry playerchunkmapentry3 = iterator1.next();
            if (playerchunkmapentry3.sendToPlayers()) {
               iterator1.remove();
               --i1;
               if (i1 < 0) {
                  break;
               }
            }
         }
      }

      if (this.players.isEmpty()) {
         Dimension dimension = this.world.dimension;
         if (!dimension.canRespawnHere()) {
            this.world.getChunkProvider().queueUnloadAll();
         }
      }

   }

   public boolean contains(int p_152621_1_, int p_152621_2_) {
      long i = getIndex(p_152621_1_, p_152621_2_);
      return this.entryMap.get(i) != null;
   }

   @Nullable
   public PlayerChunkMapEntry getEntry(int p_187301_1_, int p_187301_2_) {
      return this.entryMap.get(getIndex(p_187301_1_, p_187301_2_));
   }

   private PlayerChunkMapEntry getOrCreateEntry(int p_187302_1_, int p_187302_2_) {
      long i = getIndex(p_187302_1_, p_187302_2_);
      PlayerChunkMapEntry playerchunkmapentry = this.entryMap.get(i);
      if (playerchunkmapentry == null) {
         playerchunkmapentry = new PlayerChunkMapEntry(this, p_187302_1_, p_187302_2_);
         this.entryMap.put(i, playerchunkmapentry);
         this.entries.add(playerchunkmapentry);
         if (playerchunkmapentry.getChunk() == null) {
            this.entriesWithoutChunks.add(playerchunkmapentry);
         }

         if (!playerchunkmapentry.sendToPlayers()) {
            this.pendingSendToPlayers.add(playerchunkmapentry);
         }
      }

      return playerchunkmapentry;
   }

   public void markBlockForUpdate(BlockPos p_180244_1_) {
      int i = p_180244_1_.getX() >> 4;
      int j = p_180244_1_.getZ() >> 4;
      PlayerChunkMapEntry playerchunkmapentry = this.getEntry(i, j);
      if (playerchunkmapentry != null) {
         playerchunkmapentry.blockChanged(p_180244_1_.getX() & 15, p_180244_1_.getY(), p_180244_1_.getZ() & 15);
      }

   }

   public void addPlayer(EntityPlayerMP p_72683_1_) {
      int i = (int)p_72683_1_.posX >> 4;
      int j = (int)p_72683_1_.posZ >> 4;
      p_72683_1_.managedPosX = p_72683_1_.posX;
      p_72683_1_.managedPosZ = p_72683_1_.posZ;

      for(int k = i - this.playerViewRadius; k <= i + this.playerViewRadius; ++k) {
         for(int l = j - this.playerViewRadius; l <= j + this.playerViewRadius; ++l) {
            this.getOrCreateEntry(k, l).addPlayer(p_72683_1_);
         }
      }

      this.players.add(p_72683_1_);
      this.markSortPending();
   }

   public void removePlayer(EntityPlayerMP p_72695_1_) {
      int i = (int)p_72695_1_.managedPosX >> 4;
      int j = (int)p_72695_1_.managedPosZ >> 4;

      for(int k = i - this.playerViewRadius; k <= i + this.playerViewRadius; ++k) {
         for(int l = j - this.playerViewRadius; l <= j + this.playerViewRadius; ++l) {
            PlayerChunkMapEntry playerchunkmapentry = this.getEntry(k, l);
            if (playerchunkmapentry != null) {
               playerchunkmapentry.removePlayer(p_72695_1_);
            }
         }
      }

      this.players.remove(p_72695_1_);
      this.markSortPending();
   }

   private boolean overlaps(int p_72684_1_, int p_72684_2_, int p_72684_3_, int p_72684_4_, int p_72684_5_) {
      int i = p_72684_1_ - p_72684_3_;
      int j = p_72684_2_ - p_72684_4_;
      if (i >= -p_72684_5_ && i <= p_72684_5_) {
         return j >= -p_72684_5_ && j <= p_72684_5_;
      } else {
         return false;
      }
   }

   public void updateMovingPlayer(EntityPlayerMP p_72685_1_) {
      int i = (int)p_72685_1_.posX >> 4;
      int j = (int)p_72685_1_.posZ >> 4;
      double d0 = p_72685_1_.managedPosX - p_72685_1_.posX;
      double d1 = p_72685_1_.managedPosZ - p_72685_1_.posZ;
      double d2 = d0 * d0 + d1 * d1;
      if (!(d2 < 64.0D)) {
         int k = (int)p_72685_1_.managedPosX >> 4;
         int l = (int)p_72685_1_.managedPosZ >> 4;
         int i1 = this.playerViewRadius;
         int j1 = i - k;
         int k1 = j - l;
         if (j1 != 0 || k1 != 0) {
            for(int l1 = i - i1; l1 <= i + i1; ++l1) {
               for(int i2 = j - i1; i2 <= j + i1; ++i2) {
                  if (!this.overlaps(l1, i2, k, l, i1)) {
                     this.getOrCreateEntry(l1, i2).addPlayer(p_72685_1_);
                  }

                  if (!this.overlaps(l1 - j1, i2 - k1, i, j, i1)) {
                     PlayerChunkMapEntry playerchunkmapentry = this.getEntry(l1 - j1, i2 - k1);
                     if (playerchunkmapentry != null) {
                        playerchunkmapentry.removePlayer(p_72685_1_);
                     }
                  }
               }
            }

            p_72685_1_.managedPosX = p_72685_1_.posX;
            p_72685_1_.managedPosZ = p_72685_1_.posZ;
            this.markSortPending();
         }
      }
   }

   public boolean isPlayerWatchingChunk(EntityPlayerMP p_72694_1_, int p_72694_2_, int p_72694_3_) {
      PlayerChunkMapEntry playerchunkmapentry = this.getEntry(p_72694_2_, p_72694_3_);
      return playerchunkmapentry != null && playerchunkmapentry.containsPlayer(p_72694_1_) && playerchunkmapentry.isSentToPlayers();
   }

   public void setPlayerViewRadius(int p_152622_1_) {
      p_152622_1_ = MathHelper.clamp(p_152622_1_, 3, 32);
      if (p_152622_1_ != this.playerViewRadius) {
         int i = p_152622_1_ - this.playerViewRadius;

         for(EntityPlayerMP entityplayermp : Lists.newArrayList(this.players)) {
            int j = (int)entityplayermp.posX >> 4;
            int k = (int)entityplayermp.posZ >> 4;
            if (i > 0) {
               for(int j1 = j - p_152622_1_; j1 <= j + p_152622_1_; ++j1) {
                  for(int k1 = k - p_152622_1_; k1 <= k + p_152622_1_; ++k1) {
                     PlayerChunkMapEntry playerchunkmapentry = this.getOrCreateEntry(j1, k1);
                     if (!playerchunkmapentry.containsPlayer(entityplayermp)) {
                        playerchunkmapentry.addPlayer(entityplayermp);
                     }
                  }
               }
            } else {
               for(int l = j - this.playerViewRadius; l <= j + this.playerViewRadius; ++l) {
                  for(int i1 = k - this.playerViewRadius; i1 <= k + this.playerViewRadius; ++i1) {
                     if (!this.overlaps(l, i1, j, k, p_152622_1_)) {
                        this.getOrCreateEntry(l, i1).removePlayer(entityplayermp);
                     }
                  }
               }
            }
         }

         this.playerViewRadius = p_152622_1_;
         this.markSortPending();
      }
   }

   private void markSortPending() {
      this.sortMissingChunks = true;
      this.sortSendToPlayers = true;
   }

   public static int getFurthestViewableBlock(int p_72686_0_) {
      return p_72686_0_ * 16 - 16;
   }

   private static long getIndex(int p_187307_0_, int p_187307_1_) {
      return (long)p_187307_0_ + 2147483647L | (long)p_187307_1_ + 2147483647L << 32;
   }

   public void entryChanged(PlayerChunkMapEntry p_187304_1_) {
      this.dirtyEntries.add(p_187304_1_);
   }

   public void removeEntry(PlayerChunkMapEntry p_187305_1_) {
      ChunkPos chunkpos = p_187305_1_.getPos();
      long i = getIndex(chunkpos.x, chunkpos.z);
      p_187305_1_.updateChunkInhabitedTime();
      this.entryMap.remove(i);
      this.entries.remove(p_187305_1_);
      this.dirtyEntries.remove(p_187305_1_);
      this.pendingSendToPlayers.remove(p_187305_1_);
      this.entriesWithoutChunks.remove(p_187305_1_);
      Chunk chunk = p_187305_1_.getChunk();
      if (chunk != null) {
         this.getWorld().getChunkProvider().queueUnload(chunk);
      }

   }
}
