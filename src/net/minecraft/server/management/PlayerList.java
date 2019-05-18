package net.minecraft.server.management;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsManagerServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldServer;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.swing.plaf.DimensionUIResource;
import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class PlayerList {
   public static final File FILE_PLAYERBANS = new File("banned-players.json");
   public static final File FILE_IPBANS = new File("banned-ips.json");
   public static final File FILE_OPS = new File("ops.json");
   public static final File FILE_WHITELIST = new File("whitelist.json");
   private static final Logger LOGGER = LogManager.getLogger();
   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
   private final MinecraftServer server;
   private final List<EntityPlayerMP> players = Lists.newArrayList();
   private final Map<UUID, EntityPlayerMP> uuidToPlayerMap = Maps.newHashMap();
   private final UserListBans bannedPlayers = new UserListBans(FILE_PLAYERBANS);
   private final UserListIPBans bannedIPs = new UserListIPBans(FILE_IPBANS);
   private final UserListOps ops = new UserListOps(FILE_OPS);
   private final UserListWhitelist whiteListedPlayers = new UserListWhitelist(FILE_WHITELIST);
   private final Map<UUID, StatisticsManagerServer> playerStatFiles = Maps.newHashMap();
   private final Map<UUID, PlayerAdvancements> advancements = Maps.newHashMap();
   private IPlayerFileData playerDataManager;
   private boolean whiteListEnforced;
   protected int maxPlayers;
   private int viewDistance;
   private GameType gameType;
   private boolean commandsAllowedForAll;
   private int playerPingIndex;

   public PlayerList(MinecraftServer p_i1500_1_) {
      this.server = p_i1500_1_;
      this.getBannedPlayers().setLanServer(true);
      this.getBannedIPs().setLanServer(true);
      this.maxPlayers = 8;
   }

   public void initializeConnectionToPlayer(NetworkManager p_72355_1_, EntityPlayerMP p_72355_2_) {
      GameProfile gameprofile = p_72355_2_.getGameProfile();
      PlayerProfileCache playerprofilecache = this.server.getPlayerProfileCache();
      GameProfile gameprofile1 = playerprofilecache.getProfileByUUID(gameprofile.getId());
      String s = gameprofile1 == null ? gameprofile.getName() : gameprofile1.getName();
      playerprofilecache.addEntry(gameprofile);
      NBTTagCompound nbttagcompound = this.readPlayerDataFromFile(p_72355_2_);
      p_72355_2_.setWorld(this.server.func_71218_a(p_72355_2_.dimension));
      p_72355_2_.interactionManager.setWorld((WorldServer)p_72355_2_.world);
      String s1 = "local";
      if (p_72355_1_.getRemoteAddress() != null) {
         s1 = p_72355_1_.getRemoteAddress().toString();
      }

      LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", p_72355_2_.getName().getString(), s1, p_72355_2_.getEntityId(), p_72355_2_.posX, p_72355_2_.posY, p_72355_2_.posZ);
      WorldServer worldserver = this.server.func_71218_a(p_72355_2_.dimension);
      WorldInfo worldinfo = worldserver.getWorldInfo();
      this.setPlayerGameTypeBasedOnOther(p_72355_2_, null, worldserver);
      NetHandlerPlayServer nethandlerplayserver = new NetHandlerPlayServer(this.server, p_72355_1_, p_72355_2_);
      nethandlerplayserver.sendPacket(new SPacketJoinGame(p_72355_2_.getEntityId(), p_72355_2_.interactionManager.getGameType(), worldinfo.isHardcoreModeEnabled(), worldserver.dimension.getType(), worldserver.getDifficulty(), this.getMaxPlayers(), worldinfo.getTerrainType(), worldserver.getGameRules().getBoolean("reducedDebugInfo")));
      nethandlerplayserver.sendPacket(new SPacketCustomPayload(SPacketCustomPayload.BRAND, (new PacketBuffer(Unpooled.buffer())).writeString(this.getServerInstance().getServerModName())));
      nethandlerplayserver.sendPacket(new SPacketServerDifficulty(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
      nethandlerplayserver.sendPacket(new SPacketPlayerAbilities(p_72355_2_.capabilities));
      nethandlerplayserver.sendPacket(new SPacketHeldItemChange(p_72355_2_.inventory.currentItem));
      nethandlerplayserver.sendPacket(new SPacketUpdateRecipes(this.server.getRecipeManager().getRecipes()));
      nethandlerplayserver.sendPacket(new SPacketTagsList(this.server.getNetworkTagManager()));
      this.updatePermissionLevel(p_72355_2_);
      p_72355_2_.getStats().markAllDirty();
      p_72355_2_.getRecipeBook().init(p_72355_2_);
      this.sendScoreboard(worldserver.getScoreboard(), p_72355_2_);
      this.server.refreshStatusNextTick();
      ITextComponent itextcomponent;
      if (p_72355_2_.getGameProfile().getName().equalsIgnoreCase(s)) {
         itextcomponent = new TextComponentTranslation("multiplayer.player.joined", p_72355_2_.getDisplayName());
      } else {
         itextcomponent = new TextComponentTranslation("multiplayer.player.joined.renamed", p_72355_2_.getDisplayName(), s);
      }

      this.sendMessage(itextcomponent.applyTextStyle(TextFormatting.YELLOW));
      this.playerLoggedIn(p_72355_2_);
      nethandlerplayserver.setPlayerLocation(p_72355_2_.posX, p_72355_2_.posY, p_72355_2_.posZ, p_72355_2_.rotationYaw, p_72355_2_.rotationPitch);
      this.updateTimeAndWeatherForPlayer(p_72355_2_, worldserver);
      if (!this.server.getResourcePackUrl().isEmpty()) {
         p_72355_2_.loadResourcePack(this.server.getResourcePackUrl(), this.server.getResourcePackHash());
      }

      for(PotionEffect potioneffect : p_72355_2_.getActivePotionEffects()) {
         nethandlerplayserver.sendPacket(new SPacketEntityEffect(p_72355_2_.getEntityId(), potioneffect));
      }

      if (nbttagcompound != null && nbttagcompound.hasKey("RootVehicle", 10)) {
         NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("RootVehicle");
         Entity entity1 = AnvilChunkLoader.readWorldEntity(nbttagcompound1.getCompoundTag("Entity"), worldserver, true);
         if (entity1 != null) {
            UUID uuid = nbttagcompound1.getUniqueId("Attach");
            if (entity1.getUniqueID().equals(uuid)) {
               p_72355_2_.startRiding(entity1, true);
            } else {
               for(Entity entity : entity1.getRecursivePassengers()) {
                  if (entity.getUniqueID().equals(uuid)) {
                     p_72355_2_.startRiding(entity, true);
                     break;
                  }
               }
            }

            if (!p_72355_2_.isRiding()) {
               LOGGER.warn("Couldn't reattach entity to player");
               worldserver.removeEntityDangerously(entity1);

               for(Entity entity2 : entity1.getRecursivePassengers()) {
                  worldserver.removeEntityDangerously(entity2);
               }
            }
         }
      }

      p_72355_2_.addSelfToInternalCraftingInventory();
   }

   protected void sendScoreboard(ServerScoreboard p_96456_1_, EntityPlayerMP p_96456_2_) {
      Set<ScoreObjective> set = Sets.newHashSet();

      for(ScorePlayerTeam scoreplayerteam : p_96456_1_.getTeams()) {
         p_96456_2_.connection.sendPacket(new SPacketTeams(scoreplayerteam, 0));
      }

      for(int i = 0; i < 19; ++i) {
         ScoreObjective scoreobjective = p_96456_1_.getObjectiveInDisplaySlot(i);
         if (scoreobjective != null && !set.contains(scoreobjective)) {
            for(Packet<?> packet : p_96456_1_.getCreatePackets(scoreobjective)) {
               p_96456_2_.connection.sendPacket(packet);
            }

            set.add(scoreobjective);
         }
      }

   }

   public void func_212504_a(WorldServer p_212504_1_) {
      this.playerDataManager = p_212504_1_.getSaveHandler().getPlayerNBTManager();
      p_212504_1_.getWorldBorder().addListener(new IBorderListener() {
         public void onSizeChanged(WorldBorder p_177694_1_, double p_177694_2_) {
            PlayerList.this.sendPacketToAllPlayers(new SPacketWorldBorder(p_177694_1_, SPacketWorldBorder.Action.SET_SIZE));
         }

         public void onTransitionStarted(WorldBorder p_177692_1_, double p_177692_2_, double p_177692_4_, long p_177692_6_) {
            PlayerList.this.sendPacketToAllPlayers(new SPacketWorldBorder(p_177692_1_, SPacketWorldBorder.Action.LERP_SIZE));
         }

         public void onCenterChanged(WorldBorder p_177693_1_, double p_177693_2_, double p_177693_4_) {
            PlayerList.this.sendPacketToAllPlayers(new SPacketWorldBorder(p_177693_1_, SPacketWorldBorder.Action.SET_CENTER));
         }

         public void onWarningTimeChanged(WorldBorder p_177691_1_, int p_177691_2_) {
            PlayerList.this.sendPacketToAllPlayers(new SPacketWorldBorder(p_177691_1_, SPacketWorldBorder.Action.SET_WARNING_TIME));
         }

         public void onWarningDistanceChanged(WorldBorder p_177690_1_, int p_177690_2_) {
            PlayerList.this.sendPacketToAllPlayers(new SPacketWorldBorder(p_177690_1_, SPacketWorldBorder.Action.SET_WARNING_BLOCKS));
         }

         public void onDamageAmountChanged(WorldBorder p_177696_1_, double p_177696_2_) {
         }

         public void onDamageBufferChanged(WorldBorder p_177695_1_, double p_177695_2_) {
         }
      });
   }

   public void preparePlayer(EntityPlayerMP p_72375_1_, @Nullable WorldServer p_72375_2_) {
      WorldServer worldserver = p_72375_1_.getServerWorld();
      if (p_72375_2_ != null) {
         p_72375_2_.getPlayerChunkMap().removePlayer(p_72375_1_);
      }

      worldserver.getPlayerChunkMap().addPlayer(p_72375_1_);
      worldserver.getChunkProvider().func_186025_d((int)p_72375_1_.posX >> 4, (int)p_72375_1_.posZ >> 4, true, true);
      if (p_72375_2_ != null) {
         CriteriaTriggers.CHANGED_DIMENSION.trigger(p_72375_1_, p_72375_2_.dimension.getType(), worldserver.dimension.getType());
         if (p_72375_2_.dimension.getType() == DimensionType.NETHER && p_72375_1_.world.dimension.getType() == DimensionType.OVERWORLD && p_72375_1_.getEnteredNetherPosition() != null) {
            CriteriaTriggers.NETHER_TRAVEL.trigger(p_72375_1_, p_72375_1_.getEnteredNetherPosition());
         }
      }

   }

   public int getEntityViewDistance() {
      return PlayerChunkMap.getFurthestViewableBlock(this.getViewDistance());
   }

   @Nullable
   public NBTTagCompound readPlayerDataFromFile(EntityPlayerMP p_72380_1_) {
      NBTTagCompound nbttagcompound = this.server.func_71218_a(DimensionType.OVERWORLD).getWorldInfo().getPlayerNBTTagCompound();
      NBTTagCompound nbttagcompound1;
      if (p_72380_1_.getName().getString().equals(this.server.getServerOwner()) && nbttagcompound != null) {
         nbttagcompound1 = nbttagcompound;
         p_72380_1_.readFromNBT(nbttagcompound);
         LOGGER.debug("loading single player");
      } else {
         nbttagcompound1 = this.playerDataManager.readPlayerData(p_72380_1_);
      }

      return nbttagcompound1;
   }

   protected void writePlayerData(EntityPlayerMP p_72391_1_) {
      this.playerDataManager.writePlayerData(p_72391_1_);
      StatisticsManagerServer statisticsmanagerserver = this.playerStatFiles.get(p_72391_1_.getUniqueID());
      if (statisticsmanagerserver != null) {
         statisticsmanagerserver.saveStatFile();
      }

      PlayerAdvancements playeradvancements = this.advancements.get(p_72391_1_.getUniqueID());
      if (playeradvancements != null) {
         playeradvancements.save();
      }

   }

   public void playerLoggedIn(EntityPlayerMP p_72377_1_) {
      this.players.add(p_72377_1_);
      this.uuidToPlayerMap.put(p_72377_1_.getUniqueID(), p_72377_1_);
      this.sendPacketToAllPlayers(new SPacketPlayerListItem(SPacketPlayerListItem.Action.ADD_PLAYER, p_72377_1_));
      WorldServer worldserver = this.server.func_71218_a(p_72377_1_.dimension);

      for(int i = 0; i < this.players.size(); ++i) {
         p_72377_1_.connection.sendPacket(new SPacketPlayerListItem(SPacketPlayerListItem.Action.ADD_PLAYER, this.players.get(i)));
      }

      worldserver.spawnEntity(p_72377_1_);
      this.preparePlayer(p_72377_1_, null);
      this.server.getCustomBossEvents().onPlayerLogin(p_72377_1_);
   }

   public void serverUpdateMovingPlayer(EntityPlayerMP p_72358_1_) {
      p_72358_1_.getServerWorld().getPlayerChunkMap().updateMovingPlayer(p_72358_1_);
   }

   public void playerLoggedOut(EntityPlayerMP p_72367_1_) {
      WorldServer worldserver = p_72367_1_.getServerWorld();
      p_72367_1_.addStat(StatList.LEAVE_GAME);
      this.writePlayerData(p_72367_1_);
      if (p_72367_1_.isRiding()) {
         Entity entity = p_72367_1_.getLowestRidingEntity();
         if (entity.isOnePlayerRiding()) {
            LOGGER.debug("Removing player mount");
            p_72367_1_.dismountRidingEntity();
            worldserver.removeEntityDangerously(entity);

            for(Entity entity1 : entity.getRecursivePassengers()) {
               worldserver.removeEntityDangerously(entity1);
            }

            worldserver.getChunk(p_72367_1_.chunkCoordX, p_72367_1_.chunkCoordZ).markDirty();
         }
      }

      worldserver.removeEntity(p_72367_1_);
      worldserver.getPlayerChunkMap().removePlayer(p_72367_1_);
      p_72367_1_.getAdvancements().dispose();
      this.players.remove(p_72367_1_);
      this.server.getCustomBossEvents().onPlayerLogout(p_72367_1_);
      UUID uuid = p_72367_1_.getUniqueID();
      EntityPlayerMP entityplayermp = this.uuidToPlayerMap.get(uuid);
      if (entityplayermp == p_72367_1_) {
         this.uuidToPlayerMap.remove(uuid);
         this.playerStatFiles.remove(uuid);
         this.advancements.remove(uuid);
      }

      this.sendPacketToAllPlayers(new SPacketPlayerListItem(SPacketPlayerListItem.Action.REMOVE_PLAYER, p_72367_1_));
   }

   @Nullable
   public ITextComponent func_206258_a(SocketAddress p_206258_1_, GameProfile p_206258_2_) {
      if (this.bannedPlayers.isBanned(p_206258_2_)) {
         UserListBansEntry userlistbansentry = this.bannedPlayers.getEntry(p_206258_2_);
         ITextComponent itextcomponent1 = new TextComponentTranslation("multiplayer.disconnect.banned.reason", userlistbansentry.getBanReason());
         if (userlistbansentry.getBanEndDate() != null) {
            itextcomponent1.appendSibling(new TextComponentTranslation("multiplayer.disconnect.banned.expiration", DATE_FORMAT.format(userlistbansentry.getBanEndDate())));
         }

         return itextcomponent1;
      } else if (!this.canJoin(p_206258_2_)) {
         return new TextComponentTranslation("multiplayer.disconnect.not_whitelisted");
      } else if (this.bannedIPs.isBanned(p_206258_1_)) {
         UserListIPBansEntry userlistipbansentry = this.bannedIPs.getBanEntry(p_206258_1_);
         ITextComponent itextcomponent = new TextComponentTranslation("multiplayer.disconnect.banned_ip.reason", userlistipbansentry.getBanReason());
         if (userlistipbansentry.getBanEndDate() != null) {
            itextcomponent.appendSibling(new TextComponentTranslation("multiplayer.disconnect.banned_ip.expiration", DATE_FORMAT.format(userlistipbansentry.getBanEndDate())));
         }

         return itextcomponent;
      } else {
         return this.players.size() >= this.maxPlayers && !this.bypassesPlayerLimit(p_206258_2_) ? new TextComponentTranslation("multiplayer.disconnect.server_full") : null;
      }
   }

   public EntityPlayerMP createPlayerForUser(GameProfile p_148545_1_) {
      UUID uuid = EntityPlayer.getUUID(p_148545_1_);
      List<EntityPlayerMP> list = Lists.newArrayList();

      for(int i = 0; i < this.players.size(); ++i) {
         EntityPlayerMP entityplayermp = this.players.get(i);
         if (entityplayermp.getUniqueID().equals(uuid)) {
            list.add(entityplayermp);
         }
      }

      EntityPlayerMP entityplayermp2 = this.uuidToPlayerMap.get(p_148545_1_.getId());
      if (entityplayermp2 != null && !list.contains(entityplayermp2)) {
         list.add(entityplayermp2);
      }

      for(EntityPlayerMP entityplayermp1 : list) {
         entityplayermp1.connection.disconnect(new TextComponentTranslation("multiplayer.disconnect.duplicate_login"));
      }

      PlayerInteractionManager playerinteractionmanager;
      if (this.server.isDemo()) {
         playerinteractionmanager = new DemoPlayerInteractionManager(this.server.func_71218_a(DimensionType.OVERWORLD));
      } else {
         playerinteractionmanager = new PlayerInteractionManager(this.server.func_71218_a(DimensionType.OVERWORLD));
      }

      return new EntityPlayerMP(this.server, this.server.func_71218_a(DimensionType.OVERWORLD), p_148545_1_, playerinteractionmanager);
   }

   public EntityPlayerMP respawnPlayerForUser(EntityPlayerMP p_72368_1_, DimensionType p_72368_2_, boolean p_72368_3_) {
      p_72368_1_.getServerWorld().getEntityTracker().removePlayerFromTrackers(p_72368_1_);
      p_72368_1_.getServerWorld().getEntityTracker().untrack(p_72368_1_);
      p_72368_1_.getServerWorld().getPlayerChunkMap().removePlayer(p_72368_1_);
      this.players.remove(p_72368_1_);
      this.server.func_71218_a(p_72368_1_.dimension).removeEntityDangerously(p_72368_1_);
      BlockPos blockpos = p_72368_1_.getBedLocation();
      boolean flag = p_72368_1_.isSpawnForced();
      p_72368_1_.dimension = p_72368_2_;
      Object playerinteractionmanager;
      if (this.server.isDemo()) {
         playerinteractionmanager = new DemoPlayerInteractionManager(this.server.func_71218_a(p_72368_1_.dimension));
      } else {
         playerinteractionmanager = new PlayerInteractionManager(this.server.func_71218_a(p_72368_1_.dimension));
      }

      EntityPlayerMP entityplayermp = new EntityPlayerMP(this.server, this.server.func_71218_a(p_72368_1_.dimension), p_72368_1_.getGameProfile(), (PlayerInteractionManager)playerinteractionmanager);
      entityplayermp.connection = p_72368_1_.connection;
      entityplayermp.copyFrom(p_72368_1_, p_72368_3_);
      entityplayermp.respawnXpLevel = p_72368_1_.respawnXpLevel;
      entityplayermp.addExperienceLevel(entityplayermp.respawnXpLevel);
      entityplayermp.setEntityId(p_72368_1_.getEntityId());
      entityplayermp.setPrimaryHand(p_72368_1_.getPrimaryHand());

      for (String s : p_72368_1_.getTags()) {
         entityplayermp.addTag(s);
      }

      WorldServer worldserver = this.server.func_71218_a(p_72368_1_.dimension);
      this.setPlayerGameTypeBasedOnOther(entityplayermp, p_72368_1_, worldserver);
      if (blockpos != null) {
         BlockPos blockpos1 = EntityPlayer.getBedSpawnLocation(this.server.func_71218_a(p_72368_1_.dimension), blockpos, flag);
         if (blockpos1 != null) {
            entityplayermp.setLocationAndAngles((double)((float)blockpos1.getX() + 0.5F), (double)((float)blockpos1.getY() + 0.1F), (double)((float)blockpos1.getZ() + 0.5F), 0.0F, 0.0F);
            entityplayermp.setSpawnPoint(blockpos, flag);
         } else {
            entityplayermp.connection.sendPacket(new SPacketChangeGameState(0, 0.0F));
         }
      }

      worldserver.getChunkProvider().func_186025_d((int)entityplayermp.posX >> 4, (int)entityplayermp.posZ >> 4, true, true);

      while(!worldserver.isCollisionBoxesEmpty(entityplayermp, entityplayermp.getEntityBoundingBox()) && entityplayermp.posY < 256.0D) {
         entityplayermp.setPosition(entityplayermp.posX, entityplayermp.posY + 1.0D, entityplayermp.posZ);
      }

      entityplayermp.connection.sendPacket(new SPacketRespawn(entityplayermp.dimension, entityplayermp.world.getDifficulty(), entityplayermp.world.getWorldInfo().getTerrainType(), entityplayermp.interactionManager.getGameType()));
      BlockPos blockpos2 = worldserver.getSpawnPoint();
      entityplayermp.connection.setPlayerLocation(entityplayermp.posX, entityplayermp.posY, entityplayermp.posZ, entityplayermp.rotationYaw, entityplayermp.rotationPitch);
      entityplayermp.connection.sendPacket(new SPacketSpawnPosition(blockpos2));
      entityplayermp.connection.sendPacket(new SPacketSetExperience(entityplayermp.experience, entityplayermp.experienceTotal, entityplayermp.experienceLevel));
      this.updateTimeAndWeatherForPlayer(entityplayermp, worldserver);
      this.updatePermissionLevel(entityplayermp);
      worldserver.getPlayerChunkMap().addPlayer(entityplayermp);
      worldserver.spawnEntity(entityplayermp);
      this.players.add(entityplayermp);
      this.uuidToPlayerMap.put(entityplayermp.getUniqueID(), entityplayermp);
      entityplayermp.addSelfToInternalCraftingInventory();
      entityplayermp.setHealth(entityplayermp.getHealth());
      return entityplayermp;
   }

   public void updatePermissionLevel(EntityPlayerMP p_187243_1_) {
      GameProfile gameprofile = p_187243_1_.getGameProfile();
      int i = this.server.getPermissionLevel(gameprofile);
      this.sendPlayerPermissionLevel(p_187243_1_, i);
   }

   public void func_187242_a(EntityPlayerMP p_187242_1_, DimensionType target) {
      DimensionType dimensiontype = p_187242_1_.dimension;
      WorldServer fromServer = this.server.func_71218_a(p_187242_1_.dimension);
      p_187242_1_.dimension = target;
      WorldServer toServer = this.server.func_71218_a(p_187242_1_.dimension);
      p_187242_1_.connection.sendPacket(new SPacketRespawn(p_187242_1_.dimension, p_187242_1_.world.getDifficulty(), p_187242_1_.world.getWorldInfo().getTerrainType(), p_187242_1_.interactionManager.getGameType()));
      this.updatePermissionLevel(p_187242_1_);
      fromServer.removeEntityDangerously(p_187242_1_);
      p_187242_1_.isDead = false;
      this.func_82448_a(p_187242_1_, dimensiontype, fromServer, toServer);
      this.preparePlayer(p_187242_1_, fromServer);
      p_187242_1_.connection.setPlayerLocation(p_187242_1_.posX, p_187242_1_.posY, p_187242_1_.posZ, p_187242_1_.rotationYaw, p_187242_1_.rotationPitch);
      p_187242_1_.interactionManager.setWorld(toServer);
      p_187242_1_.connection.sendPacket(new SPacketPlayerAbilities(p_187242_1_.capabilities));
      this.updateTimeAndWeatherForPlayer(p_187242_1_, toServer);
      this.syncPlayerInventory(p_187242_1_);

      for(PotionEffect potioneffect : p_187242_1_.getActivePotionEffects()) {
         p_187242_1_.connection.sendPacket(new SPacketEntityEffect(p_187242_1_.getEntityId(), potioneffect));
      }

   }

   public void func_82448_a(Entity invoker, DimensionType fromType, WorldServer from, WorldServer to) {
      double d0 = invoker.posX;
      double d1 = invoker.posZ;
      double d2 = 8.0D;
      float f = invoker.rotationYaw;
      DimensionType targetDim = invoker.dimension;
      from.profiler.startSection("moving");
      //Now the dimension of invoker is target dimension
      if (targetDim == DimensionType.NETHER&&fromType==DimensionType.UNDERWORLD) {
         d0 = MathHelper.clamp(d0 / 8.0D, to.getWorldBorder().minX() + 16.0D, to.getWorldBorder().maxX() - 16.0D);
         d1 = MathHelper.clamp(d1 / 8.0D, to.getWorldBorder().minZ() + 16.0D, to.getWorldBorder().maxZ() - 16.0D);
         invoker.setLocationAndAngles(d0, invoker.posY, d1, invoker.rotationYaw, invoker.rotationPitch);
         if (invoker.isEntityAlive()) {
            from.updateEntityWithOptionalForce(invoker, false);
         }
      } else if (targetDim == DimensionType.UNDERWORLD&&fromType==DimensionType.NETHER) {
         d0 = MathHelper.clamp(d0 * 8.0D, to.getWorldBorder().minX() + 16.0D, to.getWorldBorder().maxX() - 16.0D);
         d1 = MathHelper.clamp(d1 * 8.0D, to.getWorldBorder().minZ() + 16.0D, to.getWorldBorder().maxZ() - 16.0D);
         invoker.setLocationAndAngles(d0, invoker.posY, d1, invoker.rotationYaw, invoker.rotationPitch);
         if (invoker.isEntityAlive()) {
            from.updateEntityWithOptionalForce(invoker, false);
         }
      } else if (fromType==DimensionType.OVERWORLD&&targetDim==DimensionType.UNDERWORLD){
         d0 = MathHelper.clamp(d0, to.getWorldBorder().minX() + 16.0D,
                 to.getWorldBorder().maxX() - 16.0D);
         d1 = MathHelper.clamp(d1, to.getWorldBorder().minZ() + 16.0D,
                 to.getWorldBorder().maxZ() - 16.0D);
         invoker.setLocationAndAngles(d0, invoker.posY, d1, invoker.rotationYaw, invoker.rotationPitch);
         if (invoker.isEntityAlive()) {
            from.updateEntityWithOptionalForce(invoker, false);
         }
      } else  if (fromType==DimensionType.UNDERWORLD&&targetDim==DimensionType.OVERWORLD){
         d0 = MathHelper.clamp(d0, to.getWorldBorder().minX() + 16.0D,
                 to.getWorldBorder().maxX() - 16.0D);
         d1 = MathHelper.clamp(d1, to.getWorldBorder().minZ() + 16.0D,
                 to.getWorldBorder().maxZ() - 16.0D);
         invoker.setLocationAndAngles(d0, invoker.posY, d1, invoker.rotationYaw, invoker.rotationPitch);
         if (invoker.isEntityAlive()) {
            from.updateEntityWithOptionalForce(invoker, false);
         }
      }else {
         BlockPos blockpos;
         if (fromType == DimensionType.THE_END) {
            blockpos = to.getSpawnPoint();
         } else {
            blockpos = to.getSpawnCoordinate();
         }

         d0 = (double) blockpos.getX();
         invoker.posY = (double) blockpos.getY();
         d1 = (double) blockpos.getZ();
         invoker.setLocationAndAngles(d0, invoker.posY, d1, 90.0F, 0.0F);
         if (invoker.isEntityAlive()) {
            from.updateEntityWithOptionalForce(invoker, false);
         }
      }

      from.profiler.endSection();
      if (fromType != DimensionType.THE_END&&!(fromType==DimensionType.OVERWORLD&&targetDim==DimensionType.OVERWORLD)) {
         from.profiler.startSection("placing");
         d0 = (double)MathHelper.clamp((int)d0, -29999872, 29999872);
         d1 = (double)MathHelper.clamp((int)d1, -29999872, 29999872);
         if (invoker.isEntityAlive()) {
            invoker.setLocationAndAngles(d0, invoker.posY, d1, invoker.rotationYaw, invoker.rotationPitch);
            to.getDefaultTeleporter().placeInPortal(invoker, f,fromType);
            to.spawnEntity(invoker);
            to.updateEntityWithOptionalForce(invoker, false);
         }

         from.profiler.endSection();
      }

      invoker.setWorld(to);
   }

   public void tick() {
      if (++this.playerPingIndex > 600) {
         this.sendPacketToAllPlayers(new SPacketPlayerListItem(SPacketPlayerListItem.Action.UPDATE_LATENCY, this.players));
         this.playerPingIndex = 0;
      }

   }

   public void sendPacketToAllPlayers(Packet<?> p_148540_1_) {
      for(int i = 0; i < this.players.size(); ++i) {
         (this.players.get(i)).connection.sendPacket(p_148540_1_);
      }

   }

   public void func_148537_a(Packet<?> p_148537_1_, DimensionType p_148537_2_) {
      for(int i = 0; i < this.players.size(); ++i) {
         EntityPlayerMP entityplayermp = this.players.get(i);
         if (entityplayermp.dimension == p_148537_2_) {
            entityplayermp.connection.sendPacket(p_148537_1_);
         }
      }

   }

   public void sendMessageToAllTeamMembers(EntityPlayer p_177453_1_, ITextComponent p_177453_2_) {
      Team team = p_177453_1_.getTeam();
      if (team != null) {
         for(String s : team.getMembershipCollection()) {
            EntityPlayerMP entityplayermp = this.getPlayerByUsername(s);
            if (entityplayermp != null && entityplayermp != p_177453_1_) {
               entityplayermp.sendMessage(p_177453_2_);
            }
         }

      }
   }

   public void sendMessageToTeamOrAllPlayers(EntityPlayer p_177452_1_, ITextComponent p_177452_2_) {
      Team team = p_177452_1_.getTeam();
      if (team == null) {
         this.sendMessage(p_177452_2_);
      } else {
         for(int i = 0; i < this.players.size(); ++i) {
            EntityPlayerMP entityplayermp = this.players.get(i);
            if (entityplayermp.getTeam() != team) {
               entityplayermp.sendMessage(p_177452_2_);
            }
         }

      }
   }

   public String[] getOnlinePlayerNames() {
      String[] astring = new String[this.players.size()];

      for(int i = 0; i < this.players.size(); ++i) {
         astring[i] = this.players.get(i).getGameProfile().getName();
      }

      return astring;
   }

   public UserListBans getBannedPlayers() {
      return this.bannedPlayers;
   }

   public UserListIPBans getBannedIPs() {
      return this.bannedIPs;
   }

   public void addOp(GameProfile p_152605_1_) {
      this.ops.addEntry(new UserListOpsEntry(p_152605_1_, this.server.getOpPermissionLevel(), this.ops.bypassesPlayerLimit(p_152605_1_)));
      EntityPlayerMP entityplayermp = this.getPlayerByUUID(p_152605_1_.getId());
      if (entityplayermp != null) {
         this.updatePermissionLevel(entityplayermp);
      }

   }

   public void removeOp(GameProfile p_152610_1_) {
      this.ops.removeEntry(p_152610_1_);
      EntityPlayerMP entityplayermp = this.getPlayerByUUID(p_152610_1_.getId());
      if (entityplayermp != null) {
         this.updatePermissionLevel(entityplayermp);
      }

   }

   private void sendPlayerPermissionLevel(EntityPlayerMP p_187245_1_, int p_187245_2_) {
      if (p_187245_1_.connection != null) {
         byte b0;
         if (p_187245_2_ <= 0) {
            b0 = 24;
         } else if (p_187245_2_ >= 4) {
            b0 = 28;
         } else {
            b0 = (byte)(24 + p_187245_2_);
         }

         p_187245_1_.connection.sendPacket(new SPacketEntityStatus(p_187245_1_, b0));
      }

      this.server.getCommandManager().sendCommandListPacket(p_187245_1_);
   }

   public boolean canJoin(GameProfile p_152607_1_) {
      return !this.whiteListEnforced || this.ops.hasEntry(p_152607_1_) || this.whiteListedPlayers.hasEntry(p_152607_1_);
   }

   public boolean canSendCommands(GameProfile p_152596_1_) {
      return this.ops.hasEntry(p_152596_1_) || this.server.isSinglePlayer() && this.server.func_71218_a(DimensionType.OVERWORLD).getWorldInfo().areCommandsAllowed() && this.server.getServerOwner().equalsIgnoreCase(p_152596_1_.getName()) || this.commandsAllowedForAll;
   }

   @Nullable
   public EntityPlayerMP getPlayerByUsername(String p_152612_1_) {
      for(EntityPlayerMP entityplayermp : this.players) {
         if (entityplayermp.getGameProfile().getName().equalsIgnoreCase(p_152612_1_)) {
            return entityplayermp;
         }
      }

      return null;
   }

   public void func_148543_a(@Nullable EntityPlayer p_148543_1_, double p_148543_2_, double p_148543_4_, double p_148543_6_, double p_148543_8_, DimensionType p_148543_10_, Packet<?> p_148543_11_) {
      for(int i = 0; i < this.players.size(); ++i) {
         EntityPlayerMP entityplayermp = this.players.get(i);
         if (entityplayermp != p_148543_1_ && entityplayermp.dimension == p_148543_10_) {
            double d0 = p_148543_2_ - entityplayermp.posX;
            double d1 = p_148543_4_ - entityplayermp.posY;
            double d2 = p_148543_6_ - entityplayermp.posZ;
            if (d0 * d0 + d1 * d1 + d2 * d2 < p_148543_8_ * p_148543_8_) {
               entityplayermp.connection.sendPacket(p_148543_11_);
            }
         }
      }

   }

   public void saveAllPlayerData() {
      for(int i = 0; i < this.players.size(); ++i) {
         this.writePlayerData(this.players.get(i));
      }

   }

   public UserListWhitelist getWhitelistedPlayers() {
      return this.whiteListedPlayers;
   }

   public String[] getWhitelistedPlayerNames() {
      return this.whiteListedPlayers.getKeys();
   }

   public UserListOps getOppedPlayers() {
      return this.ops;
   }

   public String[] getOppedPlayerNames() {
      return this.ops.getKeys();
   }

   public void reloadWhitelist() {
   }

   public void updateTimeAndWeatherForPlayer(EntityPlayerMP p_72354_1_, WorldServer p_72354_2_) {
      WorldBorder worldborder = this.server.func_71218_a(DimensionType.OVERWORLD).getWorldBorder();
      p_72354_1_.connection.sendPacket(new SPacketWorldBorder(worldborder, SPacketWorldBorder.Action.INITIALIZE));
      p_72354_1_.connection.sendPacket(new SPacketTimeUpdate(p_72354_2_.getTotalWorldTime(), p_72354_2_.getWorldTime(), p_72354_2_.getGameRules().getBoolean("doDaylightCycle")));
      BlockPos blockpos = p_72354_2_.getSpawnPoint();
      p_72354_1_.connection.sendPacket(new SPacketSpawnPosition(blockpos));
      if (p_72354_2_.isRaining()) {
         p_72354_1_.connection.sendPacket(new SPacketChangeGameState(1, 0.0F));
         p_72354_1_.connection.sendPacket(new SPacketChangeGameState(7, p_72354_2_.getRainStrength(1.0F)));
         p_72354_1_.connection.sendPacket(new SPacketChangeGameState(8, p_72354_2_.getThunderStrength(1.0F)));
      }

   }

   public void syncPlayerInventory(EntityPlayerMP p_72385_1_) {
      p_72385_1_.sendContainerToPlayer(p_72385_1_.inventoryContainer);
      p_72385_1_.setPlayerHealthUpdated();
      p_72385_1_.connection.sendPacket(new SPacketHeldItemChange(p_72385_1_.inventory.currentItem));
   }

   public int getCurrentPlayerCount() {
      return this.players.size();
   }

   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public String[] getAvailablePlayerDat() {
      return this.server.func_71218_a(DimensionType.OVERWORLD).getSaveHandler().getPlayerNBTManager().getAvailablePlayerDat();
   }

   public boolean isWhiteListEnabled() {
      return this.whiteListEnforced;
   }

   public void setWhiteListEnabled(boolean p_72371_1_) {
      this.whiteListEnforced = p_72371_1_;
   }

   public List<EntityPlayerMP> getPlayersMatchingAddress(String p_72382_1_) {
      List<EntityPlayerMP> list = Lists.newArrayList();

      for(EntityPlayerMP entityplayermp : this.players) {
         if (entityplayermp.getPlayerIP().equals(p_72382_1_)) {
            list.add(entityplayermp);
         }
      }

      return list;
   }

   public int getViewDistance() {
      return this.viewDistance;
   }

   public MinecraftServer getServerInstance() {
      return this.server;
   }

   public NBTTagCompound getHostPlayerData() {
      return null;
   }

   @OnlyIn(Dist.CLIENT)
   public void setGameType(GameType p_152604_1_) {
      this.gameType = p_152604_1_;
   }

   private void setPlayerGameTypeBasedOnOther(EntityPlayerMP p_72381_1_, EntityPlayerMP p_72381_2_, IWorld p_72381_3_) {
      if (p_72381_2_ != null) {
         p_72381_1_.interactionManager.setGameType(p_72381_2_.interactionManager.getGameType());
      } else if (this.gameType != null) {
         p_72381_1_.interactionManager.setGameType(this.gameType);
      }

      p_72381_1_.interactionManager.initializeGameType(p_72381_3_.getWorldInfo().getGameType());
   }

   @OnlyIn(Dist.CLIENT)
   public void setCommandsAllowedForAll(boolean p_72387_1_) {
      this.commandsAllowedForAll = p_72387_1_;
   }

   public void removeAllPlayers() {
      for(int i = 0; i < this.players.size(); ++i) {
         (this.players.get(i)).connection.disconnect(new TextComponentTranslation("multiplayer.disconnect.server_shutdown"));
      }

   }

   public void sendMessage(ITextComponent p_148544_1_, boolean p_148544_2_) {
      this.server.sendMessage(p_148544_1_);
      ChatType chattype = p_148544_2_ ? ChatType.SYSTEM : ChatType.CHAT;
      this.sendPacketToAllPlayers(new SPacketChat(p_148544_1_, chattype));
   }

   public void sendMessage(ITextComponent p_148539_1_) {
      this.sendMessage(p_148539_1_, true);
   }

   public StatisticsManagerServer getPlayerStats(EntityPlayer p_152602_1_) {
      UUID uuid = p_152602_1_.getUniqueID();
      StatisticsManagerServer statisticsmanagerserver = uuid == null ? null : this.playerStatFiles.get(uuid);
      if (statisticsmanagerserver == null) {
         File file1 = new File(this.server.func_71218_a(DimensionType.OVERWORLD).getSaveHandler().getWorldDirectory(), "stats");
         File file2 = new File(file1, uuid + ".json");
         if (!file2.exists()) {
            File file3 = new File(file1, p_152602_1_.getName().getString() + ".json");
            if (file3.exists() && file3.isFile()) {
               file3.renameTo(file2);
            }
         }

         statisticsmanagerserver = new StatisticsManagerServer(this.server, file2);
         this.playerStatFiles.put(uuid, statisticsmanagerserver);
      }

      return statisticsmanagerserver;
   }

   public PlayerAdvancements getPlayerAdvancements(EntityPlayerMP p_192054_1_) {
      UUID uuid = p_192054_1_.getUniqueID();
      PlayerAdvancements playeradvancements = this.advancements.get(uuid);
      if (playeradvancements == null) {
         File file1 = new File(this.server.func_71218_a(DimensionType.OVERWORLD).getSaveHandler().getWorldDirectory(), "advancements");
         File file2 = new File(file1, uuid + ".json");
         playeradvancements = new PlayerAdvancements(this.server, file2, p_192054_1_);
         this.advancements.put(uuid, playeradvancements);
      }

      playeradvancements.setPlayer(p_192054_1_);
      return playeradvancements;
   }

   public void setViewDistance(int p_152611_1_) {
      this.viewDistance = p_152611_1_;

      for(WorldServer worldserver : this.server.func_212370_w()) {
         if (worldserver != null) {
            worldserver.getPlayerChunkMap().setPlayerViewRadius(p_152611_1_);
            worldserver.getEntityTracker().setViewDistance(p_152611_1_);
         }
      }

   }

   public List<EntityPlayerMP> getPlayers() {
      return this.players;
   }

   @Nullable
   public EntityPlayerMP getPlayerByUUID(UUID p_177451_1_) {
      return this.uuidToPlayerMap.get(p_177451_1_);
   }

   public boolean bypassesPlayerLimit(GameProfile p_183023_1_) {
      return false;
   }

   public void reloadResources() {
      for(PlayerAdvancements playeradvancements : this.advancements.values()) {
         playeradvancements.reload();
      }

      this.sendPacketToAllPlayers(new SPacketTagsList(this.server.getNetworkTagManager()));
      SPacketUpdateRecipes spacketupdaterecipes = new SPacketUpdateRecipes(this.server.getRecipeManager().getRecipes());

      for(EntityPlayerMP entityplayermp : this.players) {
         entityplayermp.connection.sendPacket(spacketupdaterecipes);
         entityplayermp.getRecipeBook().init(entityplayermp);
      }

   }

   public boolean commandsAllowedForAll() {
      return this.commandsAllowedForAll;
   }
}
