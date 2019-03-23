package net.minecraft.server.integrated;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.FutureTask;
import java.util.function.BooleanSupplier;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ThreadLanServerPing;
import net.minecraft.command.Commands;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.profiler.Snooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.CryptManager;
import net.minecraft.util.Util;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerDemo;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedDataStorage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class IntegratedServer extends MinecraftServer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft mc;
   private final WorldSettings worldSettings;
   private boolean isGamePaused;
   private int field_195580_l = -1;
   private ThreadLanServerPing lanServerPing;
   private UUID field_211528_n;

   public IntegratedServer(Minecraft p_i46523_1_, String p_i46523_2_, String p_i46523_3_, WorldSettings p_i46523_4_, YggdrasilAuthenticationService p_i46523_5_, MinecraftSessionService p_i46523_6_, GameProfileRepository p_i46523_7_, PlayerProfileCache p_i46523_8_) {
      super(new File(p_i46523_1_.gameDir, "saves"), p_i46523_1_.getProxy(), p_i46523_1_.getDataFixer(), new Commands(false), p_i46523_5_, p_i46523_6_, p_i46523_7_, p_i46523_8_);
      this.setServerOwner(p_i46523_1_.getSession().getUsername());
      this.setFolderName(p_i46523_2_);
      this.setWorldName(p_i46523_3_);
      this.setDemo(p_i46523_1_.isDemo());
      this.canCreateBonusChest(p_i46523_4_.isBonusChestEnabled());
      this.setBuildLimit(256);
      this.setPlayerList(new IntegratedPlayerList(this));
      this.mc = p_i46523_1_;
      this.worldSettings = this.isDemo() ? WorldServerDemo.DEMO_WORLD_SETTINGS : p_i46523_4_;
   }

   public void loadAllWorlds(String p_71247_1_, String p_71247_2_, long p_71247_3_, WorldType p_71247_5_, JsonElement p_71247_6_) {
      this.convertMapIfNeeded(p_71247_1_);
      ISaveHandler isavehandler = this.getActiveAnvilConverter().getSaveLoader(p_71247_1_, this);
      this.setResourcePackFromWorld(this.getFolderName(), isavehandler);
      WorldInfo worldinfo = isavehandler.loadWorldInfo();
      if (worldinfo == null) {
         worldinfo = new WorldInfo(this.worldSettings, p_71247_2_);
      } else {
         worldinfo.setWorldName(p_71247_2_);
      }

      this.func_195560_a(isavehandler.getWorldDirectory(), worldinfo);
      WorldSavedDataStorage worldsaveddatastorage = new WorldSavedDataStorage(isavehandler);
      this.func_212369_a(isavehandler, worldsaveddatastorage, worldinfo, this.worldSettings);
      if (this.func_71218_a(DimensionType.OVERWORLD).getWorldInfo().getDifficulty() == null) {
         this.setDifficultyForAllWorlds(this.mc.gameSettings.difficulty);
      }

      this.func_71222_d(worldsaveddatastorage);
   }

   public boolean init() throws IOException {
      LOGGER.info("Starting integrated minecraft server version 1.13.2");
      this.setOnlineMode(true);
      this.setCanSpawnAnimals(true);
      this.setCanSpawnNPCs(true);
      this.setAllowPvp(true);
      this.setAllowFlight(true);
      LOGGER.info("Generating keypair");
      this.setKeyPair(CryptManager.generateKeyPair());
      this.loadAllWorlds(this.getFolderName(), this.getWorldName(), this.worldSettings.getSeed(), this.worldSettings.getTerrainType(), this.worldSettings.func_205391_j());
      this.setMOTD(this.getServerOwner() + " - " + this.func_71218_a(DimensionType.OVERWORLD).getWorldInfo().getWorldName());
      return true;
   }

   public void func_71217_p(BooleanSupplier p_71217_1_) {
      boolean flag = this.isGamePaused;
      this.isGamePaused = Minecraft.getInstance().getConnection() != null && Minecraft.getInstance().isGamePaused();
      if (!flag && this.isGamePaused) {
         LOGGER.info("Saving and pausing game...");
         this.getPlayerList().saveAllPlayerData();
         this.saveAllWorlds(false);
      }

      FutureTask<?> futuretask;
      if (this.isGamePaused) {
         while((futuretask = this.futureTaskQueue.poll()) != null) {
            Util.runTask(futuretask, LOGGER);
         }
      } else {
         super.func_71217_p(p_71217_1_);
         if (this.mc.gameSettings.renderDistanceChunks != this.getPlayerList().getViewDistance()) {
            LOGGER.info("Changing view distance to {}, from {}", this.mc.gameSettings.renderDistanceChunks, this.getPlayerList().getViewDistance());
            this.getPlayerList().setViewDistance(this.mc.gameSettings.renderDistanceChunks);
         }

         if (this.mc.world != null) {
            WorldInfo worldinfo = this.func_71218_a(DimensionType.OVERWORLD).getWorldInfo();
            WorldInfo worldinfo1 = this.mc.world.getWorldInfo();
            if (!worldinfo.isDifficultyLocked() && worldinfo1.getDifficulty() != worldinfo.getDifficulty()) {
               LOGGER.info("Changing difficulty to {}, from {}", worldinfo1.getDifficulty(), worldinfo.getDifficulty());
               this.setDifficultyForAllWorlds(worldinfo1.getDifficulty());
            } else if (worldinfo1.isDifficultyLocked() && !worldinfo.isDifficultyLocked()) {
               LOGGER.info("Locking difficulty to {}", (Object)worldinfo1.getDifficulty());

               for(WorldServer worldserver : this.func_212370_w()) {
                  if (worldserver != null) {
                     worldserver.getWorldInfo().setDifficultyLocked(true);
                  }
               }
            }
         }
      }

   }

   public boolean canStructuresSpawn() {
      return false;
   }

   public GameType getGameType() {
      return this.worldSettings.getGameType();
   }

   public EnumDifficulty getDifficulty() {
      return this.mc.world.getWorldInfo().getDifficulty();
   }

   public boolean isHardcore() {
      return this.worldSettings.getHardcoreEnabled();
   }

   public boolean allowLoggingRcon() {
      return true;
   }

   public boolean allowLogging() {
      return true;
   }

   public File getDataDirectory() {
      return this.mc.gameDir;
   }

   public boolean isDedicatedServer() {
      return false;
   }

   public boolean shouldUseNativeTransport() {
      return false;
   }

   public void finalTick(CrashReport p_71228_1_) {
      this.mc.crashed(p_71228_1_);
   }

   public CrashReport addServerInfoToCrashReport(CrashReport p_71230_1_) {
      p_71230_1_ = super.addServerInfoToCrashReport(p_71230_1_);
      p_71230_1_.getCategory().addCrashSection("Type", "Integrated Server (map_client.txt)");
      p_71230_1_.getCategory().addDetail("Is Modded", () -> {
         String s = ClientBrandRetriever.getClientModName();
         if (!s.equals("vanilla")) {
            return "Definitely; Client brand changed to '" + s + "'";
         } else {
            s = this.getServerModName();
            if (!"vanilla".equals(s)) {
               return "Definitely; Server brand changed to '" + s + "'";
            } else {
               return Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and both client + server brands are untouched.";
            }
         }
      });
      return p_71230_1_;
   }

   public void setDifficultyForAllWorlds(EnumDifficulty p_147139_1_) {
      super.setDifficultyForAllWorlds(p_147139_1_);
      if (this.mc.world != null) {
         this.mc.world.getWorldInfo().setDifficulty(p_147139_1_);
      }

   }

   public void addServerStatsToSnooper(Snooper p_70000_1_) {
      super.addServerStatsToSnooper(p_70000_1_);
      p_70000_1_.addClientStat("snooper_partner", this.mc.getSnooper().getUniqueID());
   }

   public boolean isSnooperEnabled() {
      return Minecraft.getInstance().isSnooperEnabled();
   }

   public boolean func_195565_a(GameType p_195565_1_, boolean p_195565_2_, int p_195565_3_) {
      try {
         this.getNetworkSystem().addEndpoint((InetAddress)null, p_195565_3_);
         LOGGER.info("Started serving on {}", (int)p_195565_3_);
         this.field_195580_l = p_195565_3_;
         this.lanServerPing = new ThreadLanServerPing(this.getMOTD(), p_195565_3_ + "");
         this.lanServerPing.start();
         this.getPlayerList().setGameType(p_195565_1_);
         this.getPlayerList().setCommandsAllowedForAll(p_195565_2_);
         int i = this.getPermissionLevel(this.mc.player.getGameProfile());
         this.mc.player.setPermissionLevel(i);

         for(EntityPlayerMP entityplayermp : this.getPlayerList().getPlayers()) {
            this.getCommandManager().sendCommandListPacket(entityplayermp);
         }

         return true;
      } catch (IOException var7) {
         return false;
      }
   }

   public void stopServer() {
      super.stopServer();
      if (this.lanServerPing != null) {
         this.lanServerPing.interrupt();
         this.lanServerPing = null;
      }

   }

   public void initiateShutdown() {
      Futures.getUnchecked(this.addScheduledTask(() -> {
         for(EntityPlayerMP entityplayermp : Lists.newArrayList(this.getPlayerList().getPlayers())) {
            if (!entityplayermp.getUniqueID().equals(this.field_211528_n)) {
               this.getPlayerList().playerLoggedOut(entityplayermp);
            }
         }

      }));
      super.initiateShutdown();
      if (this.lanServerPing != null) {
         this.lanServerPing.interrupt();
         this.lanServerPing = null;
      }

   }

   public boolean getPublic() {
      return this.field_195580_l > -1;
   }

   public int getServerPort() {
      return this.field_195580_l;
   }

   public void setGameType(GameType p_71235_1_) {
      super.setGameType(p_71235_1_);
      this.getPlayerList().setGameType(p_71235_1_);
   }

   public boolean isCommandBlockEnabled() {
      return true;
   }

   public int getOpPermissionLevel() {
      return 2;
   }

   public void func_211527_b(UUID p_211527_1_) {
      this.field_211528_n = p_211527_1_;
   }
}
