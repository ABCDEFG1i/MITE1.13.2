package net.minecraft.server.dedicated;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemGroup;
import net.minecraft.network.rcon.IServer;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.network.rcon.RConThreadMain;
import net.minecraft.network.rcon.RConThreadQuery;
import net.minecraft.profiler.Snooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerEula;
import net.minecraft.server.gui.MinecraftServerGui;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.CryptManager;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.DefaultUncaughtExceptionHandlerWithName;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DedicatedServer extends MinecraftServer implements IServer {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Pattern RESOURCE_PACK_SHA1_PATTERN = Pattern.compile("^[a-fA-F0-9]{40}$");
   public final List<PendingCommand> pendingCommandList = Collections.synchronizedList(Lists.newArrayList());
   private RConThreadQuery rconQueryThread;
   private final RConConsoleSource rconConsoleSource = new RConConsoleSource(this);
   private RConThreadMain rconThread;
   private PropertyManager settings;
   private ServerEula eula;
   private boolean canSpawnStructures;
   private GameType gameType;
   private boolean guiIsEnabled;

   public DedicatedServer(File p_i49693_1_, DataFixer p_i49693_2_, YggdrasilAuthenticationService p_i49693_3_, MinecraftSessionService p_i49693_4_, GameProfileRepository p_i49693_5_, PlayerProfileCache p_i49693_6_) {
      super(p_i49693_1_, Proxy.NO_PROXY, p_i49693_2_, new Commands(true), p_i49693_3_, p_i49693_4_, p_i49693_5_, p_i49693_6_);
      Thread thread = new Thread("Server Infinisleeper") {
         {
            this.setDaemon(true);
            this.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(DedicatedServer.LOGGER));
            this.start();
         }

         public void run() {
            while(true) {
               try {
                  Thread.sleep(2147483647L);
               } catch (InterruptedException var2) {
               }
            }
         }
      };
   }

   public boolean init() throws IOException {
      Thread thread = new Thread("Server console handler") {
         public void run() {
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

            String s4;
            try {
               while(!DedicatedServer.this.isServerStopped() && DedicatedServer.this.isServerRunning() && (s4 = bufferedreader.readLine()) != null) {
                  DedicatedServer.this.func_195581_a(s4, DedicatedServer.this.getCommandSource());
               }
            } catch (IOException ioexception1) {
               DedicatedServer.LOGGER.error("Exception handling console input", ioexception1);
            }

         }
      };
      thread.setDaemon(true);
      thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      thread.start();
      LOGGER.info("Starting minecraft server version 1.13.2");
      if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
         LOGGER.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
      }

      LOGGER.info("Loading properties");
      this.settings = new PropertyManager(new File("server.properties"));
      this.eula = new ServerEula(new File("eula.txt"));
      if (!this.eula.hasAcceptedEULA()) {
         LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
         this.eula.createEULAFile();
         return false;
      } else {
         if (this.isSinglePlayer()) {
            this.setHostname("127.0.0.1");
         } else {
            this.setOnlineMode(this.settings.getBooleanProperty("online-mode", true));
            this.setPreventProxyConnections(this.settings.getBooleanProperty("prevent-proxy-connections", false));
            this.setHostname(this.settings.getStringProperty("server-ip", ""));
         }

         this.setCanSpawnAnimals(this.settings.getBooleanProperty("spawn-animals", true));
         this.setCanSpawnNPCs(this.settings.getBooleanProperty("spawn-npcs", true));
         this.setAllowPvp(this.settings.getBooleanProperty("pvp", true));
         this.setAllowFlight(this.settings.getBooleanProperty("allow-flight", false));
         this.setResourcePack(this.settings.getStringProperty("resource-pack", ""), this.loadResourcePackSHA());
         this.setMOTD(this.settings.getStringProperty("motd", "A Minecraft Server"));
         this.setForceGamemode(this.settings.getBooleanProperty("force-gamemode", false));
         this.setPlayerIdleTimeout(this.settings.getIntProperty("player-idle-timeout", 0));
         this.setWhitelistEnabled(this.settings.getBooleanProperty("enforce-whitelist", false));
         if (this.settings.getIntProperty("difficulty", 1) < 0) {
            this.settings.setProperty("difficulty", 0);
         } else if (this.settings.getIntProperty("difficulty", 1) > 3) {
            this.settings.setProperty("difficulty", 3);
         }

         this.canSpawnStructures = this.settings.getBooleanProperty("generate-structures", true);
         int i = this.settings.getIntProperty("gamemode", GameType.SURVIVAL.getID());
         this.gameType = WorldSettings.getGameTypeById(i);
         LOGGER.info("Default game type: {}", this.gameType);
         InetAddress inetaddress = null;
         if (!this.getServerHostname().isEmpty()) {
            inetaddress = InetAddress.getByName(this.getServerHostname());
         }

         if (this.getServerPort() < 0) {
            this.setServerPort(this.settings.getIntProperty("server-port", 25565));
         }

         LOGGER.info("Generating keypair");
         this.setKeyPair(CryptManager.generateKeyPair());
         LOGGER.info("Starting Minecraft server on {}:{}", this.getServerHostname().isEmpty() ? "*" : this.getServerHostname(), this.getServerPort());

         try {
            this.getNetworkSystem().addEndpoint(inetaddress, this.getServerPort());
         } catch (IOException ioexception) {
            LOGGER.warn("**** FAILED TO BIND TO PORT!");
            LOGGER.warn("The exception was: {}", ioexception.toString());
            LOGGER.warn("Perhaps a server is already running on that port?");
            return false;
         }

         if (!this.isServerInOnlineMode()) {
            LOGGER.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
            LOGGER.warn("The server will make no attempt to authenticate usernames. Beware.");
            LOGGER.warn("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
            LOGGER.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
         }

         if (this.convertFiles()) {
            this.getPlayerProfileCache().save();
         }

         if (!PreYggdrasilConverter.tryConvert(this.settings)) {
            return false;
         } else {
            this.setPlayerList(new DedicatedPlayerList(this));
            long j = Util.nanoTime();
            if (this.getFolderName() == null) {
               this.setFolderName(this.settings.getStringProperty("level-name", "world"));
            }

            String s = this.settings.getStringProperty("level-seed", "");
            String s1 = this.settings.getStringProperty("level-type", "DEFAULT");
            String s2 = this.settings.getStringProperty("generator-settings", "");
            long k = (new Random()).nextLong();
            if (!s.isEmpty()) {
               try {
                  long l = Long.parseLong(s);
                  if (l != 0L) {
                     k = l;
                  }
               } catch (NumberFormatException var17) {
                  k = (long)s.hashCode();
               }
            }

            WorldType worldtype = WorldType.byName(s1);
            if (worldtype == null) {
               worldtype = WorldType.DEFAULT;
            }

            this.isCommandBlockEnabled();
            this.getOpPermissionLevel();
            this.isSnooperEnabled();
            this.getNetworkCompressionThreshold();
            this.setBuildLimit(this.settings.getIntProperty("max-build-height", 256));
            this.setBuildLimit((this.getBuildLimit() + 8) / 16 * 16);
            this.setBuildLimit(MathHelper.clamp(this.getBuildLimit(), 64, 256));
            this.settings.setProperty("max-build-height", this.getBuildLimit());
            TileEntitySkull.setProfileCache(this.getPlayerProfileCache());
            TileEntitySkull.setSessionService(this.getMinecraftSessionService());
            PlayerProfileCache.setOnlineMode(this.isServerInOnlineMode());
            LOGGER.info("Preparing level \"{}\"", this.getFolderName());
            JsonObject jsonobject = new JsonObject();
            if (worldtype == WorldType.FLAT) {
               jsonobject.addProperty("flat_world_options", s2);
            } else if (!s2.isEmpty()) {
               jsonobject = JsonUtils.func_212745_a(s2);
            }

            this.loadAllWorlds(this.getFolderName(), this.getFolderName(), k, worldtype, jsonobject);
            long i1 = Util.nanoTime() - j;
            String s3 = String.format(Locale.ROOT, "%.3fs", (double)i1 / 1.0E9D);
            LOGGER.info("Done ({})! For help, type \"help\"", s3);
            if (this.settings.hasProperty("announce-player-achievements")) {
               this.getGameRules().setOrCreateGameRule("announceAdvancements", this.settings.getBooleanProperty("announce-player-achievements", true) ? "true" : "false", this);
               this.settings.removeProperty("announce-player-achievements");
               this.settings.saveProperties();
            }

            if (this.settings.getBooleanProperty("enable-query", false)) {
               LOGGER.info("Starting GS4 status listener");
               this.rconQueryThread = new RConThreadQuery(this);
               this.rconQueryThread.startThread();
            }

            if (this.settings.getBooleanProperty("enable-rcon", false)) {
               LOGGER.info("Starting remote control listener");
               this.rconThread = new RConThreadMain(this);
               this.rconThread.startThread();
            }

            if (this.getMaxTickTime() > 0L) {
               Thread thread1 = new Thread(new ServerHangWatchdog(this));
               thread1.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandlerWithName(LOGGER));
               thread1.setName("Server Watchdog");
               thread1.setDaemon(true);
               thread1.start();
            }

            Items.AIR.fillItemGroup(ItemGroup.SEARCH, NonNullList.create());
            return true;
         }
      }
   }

   public String loadResourcePackSHA() {
      if (this.settings.hasProperty("resource-pack-hash")) {
         if (this.settings.hasProperty("resource-pack-sha1")) {
            LOGGER.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
         } else {
            LOGGER.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
            this.settings.getStringProperty("resource-pack-sha1", this.settings.getStringProperty("resource-pack-hash", ""));
            this.settings.removeProperty("resource-pack-hash");
         }
      }

      String s = this.settings.getStringProperty("resource-pack-sha1", "");
      if (!s.isEmpty() && !RESOURCE_PACK_SHA1_PATTERN.matcher(s).matches()) {
         LOGGER.warn("Invalid sha1 for ressource-pack-sha1");
      }

      if (!this.settings.getStringProperty("resource-pack", "").isEmpty() && s.isEmpty()) {
         LOGGER.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
      }

      return s;
   }

   public void setGameType(GameType p_71235_1_) {
      super.setGameType(p_71235_1_);
      this.gameType = p_71235_1_;
   }

   public boolean canStructuresSpawn() {
      return this.canSpawnStructures;
   }

   public GameType getGameType() {
      return this.gameType;
   }

   public EnumDifficulty getDifficulty() {
      return EnumDifficulty.byId(this.settings.getIntProperty("difficulty", EnumDifficulty.NORMAL.getId()));
   }

   public boolean isHardcore() {
      return this.settings.getBooleanProperty("hardcore", false);
   }

   public CrashReport addServerInfoToCrashReport(CrashReport p_71230_1_) {
      p_71230_1_ = super.addServerInfoToCrashReport(p_71230_1_);
      p_71230_1_.getCategory().addDetail("Is Modded", () -> {
         String s = this.getServerModName();
         return !"vanilla".equals(s) ? "Definitely; Server brand changed to '" + s + "'" : "Unknown (can't tell)";
      });
      p_71230_1_.getCategory().addDetail("Type", () -> {
         return "Dedicated Server (map_server.txt)";
      });
      return p_71230_1_;
   }

   public void systemExitNow() {
      System.exit(0);
   }

   public void func_71190_q(BooleanSupplier p_71190_1_) {
      super.func_71190_q(p_71190_1_);
      this.executePendingCommands();
   }

   public boolean getAllowNether() {
      return this.settings.getBooleanProperty("allow-nether", true);
   }

   public boolean allowSpawnMonsters() {
      return this.settings.getBooleanProperty("spawn-monsters", true);
   }

   public void addServerStatsToSnooper(Snooper p_70000_1_) {
      p_70000_1_.addClientStat("whitelist_enabled", this.getPlayerList().isWhiteListEnabled());
      p_70000_1_.addClientStat("whitelist_count", this.getPlayerList().getWhitelistedPlayerNames().length);
      super.addServerStatsToSnooper(p_70000_1_);
   }

   public boolean isSnooperEnabled() {
      if (this.settings.getBooleanProperty("snooper-enabled", true)) {
      }

      return false;
   }

   public void func_195581_a(String p_195581_1_, CommandSource p_195581_2_) {
      this.pendingCommandList.add(new PendingCommand(p_195581_1_, p_195581_2_));
   }

   public void executePendingCommands() {
      while(!this.pendingCommandList.isEmpty()) {
         PendingCommand pendingcommand = this.pendingCommandList.remove(0);
         this.getCommandManager().handleCommand(pendingcommand.sender, pendingcommand.command);
      }

   }

   public boolean isDedicatedServer() {
      return true;
   }

   public boolean shouldUseNativeTransport() {
      return this.settings.getBooleanProperty("use-native-transport", true);
   }

   public DedicatedPlayerList getPlayerList() {
      return (DedicatedPlayerList)super.getPlayerList();
   }

   public boolean getPublic() {
      return true;
   }

   public int getIntProperty(String p_71327_1_, int p_71327_2_) {
      return this.settings.getIntProperty(p_71327_1_, p_71327_2_);
   }

   public String getStringProperty(String p_71330_1_, String p_71330_2_) {
      return this.settings.getStringProperty(p_71330_1_, p_71330_2_);
   }

   public boolean getBooleanProperty(String p_71332_1_, boolean p_71332_2_) {
      return this.settings.getBooleanProperty(p_71332_1_, p_71332_2_);
   }

   public void setProperty(String p_71328_1_, Object p_71328_2_) {
      this.settings.setProperty(p_71328_1_, p_71328_2_);
   }

   public void saveProperties() {
      this.settings.saveProperties();
   }

   public String getSettingsFilename() {
      File file1 = this.settings.getPropertiesFile();
      return file1 != null ? file1.getAbsolutePath() : "No settings file";
   }

   public String getHostname() {
      return this.getServerHostname();
   }

   public int getPort() {
      return this.getServerPort();
   }

   public String getMotd() {
      return this.getMOTD();
   }

   public void setGuiEnabled() {
      MinecraftServerGui.createServerGui(this);
      this.guiIsEnabled = true;
   }

   public boolean getGuiEnabled() {
      return this.guiIsEnabled;
   }

   public boolean func_195565_a(GameType p_195565_1_, boolean p_195565_2_, int p_195565_3_) {
      return false;
   }

   public boolean isCommandBlockEnabled() {
      return this.settings.getBooleanProperty("enable-command-block", false);
   }

   public int getSpawnProtectionSize() {
      return this.settings.getIntProperty("spawn-protection", super.getSpawnProtectionSize());
   }

   public boolean isBlockProtected(World p_175579_1_, BlockPos p_175579_2_, EntityPlayer p_175579_3_) {
      if (p_175579_1_.dimension.getType() != DimensionType.OVERWORLD) {
         return false;
      } else if (this.getPlayerList().getOppedPlayers().isEmpty()) {
         return false;
      } else if (this.getPlayerList().canSendCommands(p_175579_3_.getGameProfile())) {
         return false;
      } else if (this.getSpawnProtectionSize() <= 0) {
         return false;
      } else {
         BlockPos blockpos = p_175579_1_.getSpawnPoint();
         int i = MathHelper.abs(p_175579_2_.getX() - blockpos.getX());
         int j = MathHelper.abs(p_175579_2_.getZ() - blockpos.getZ());
         int k = Math.max(i, j);
         return k <= this.getSpawnProtectionSize();
      }
   }

   public int getOpPermissionLevel() {
      return this.settings.getIntProperty("op-permission-level", 4);
   }

   public void setPlayerIdleTimeout(int p_143006_1_) {
      super.setPlayerIdleTimeout(p_143006_1_);
      this.settings.setProperty("player-idle-timeout", p_143006_1_);
      this.saveProperties();
   }

   public boolean allowLoggingRcon() {
      return this.settings.getBooleanProperty("broadcast-rcon-to-ops", true);
   }

   public boolean allowLogging() {
      return this.settings.getBooleanProperty("broadcast-console-to-ops", true);
   }

   public int getMaxWorldSize() {
      int i = this.settings.getIntProperty("max-world-size", super.getMaxWorldSize());
      if (i < 1) {
         i = 1;
      } else if (i > super.getMaxWorldSize()) {
         i = super.getMaxWorldSize();
      }

      return i;
   }

   public int getNetworkCompressionThreshold() {
      return this.settings.getIntProperty("network-compression-threshold", super.getNetworkCompressionThreshold());
   }

   protected boolean convertFiles() {
      boolean flag = false;

      for(int i = 0; !flag && i <= 2; ++i) {
         if (i > 0) {
            LOGGER.warn("Encountered a problem while converting the user banlist, retrying in a few seconds");
            this.sleepFiveSeconds();
         }

         flag = PreYggdrasilConverter.convertUserBanlist(this);
      }

      boolean flag1 = false;

      for(int j = 0; !flag1 && j <= 2; ++j) {
         if (j > 0) {
            LOGGER.warn("Encountered a problem while converting the ip banlist, retrying in a few seconds");
            this.sleepFiveSeconds();
         }

         flag1 = PreYggdrasilConverter.convertIpBanlist(this);
      }

      boolean flag2 = false;

      for(int k = 0; !flag2 && k <= 2; ++k) {
         if (k > 0) {
            LOGGER.warn("Encountered a problem while converting the op list, retrying in a few seconds");
            this.sleepFiveSeconds();
         }

         flag2 = PreYggdrasilConverter.convertOplist(this);
      }

      boolean flag3 = false;

      for(int l = 0; !flag3 && l <= 2; ++l) {
         if (l > 0) {
            LOGGER.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
            this.sleepFiveSeconds();
         }

         flag3 = PreYggdrasilConverter.convertWhitelist(this);
      }

      boolean flag4 = false;

      for(int i1 = 0; !flag4 && i1 <= 2; ++i1) {
         if (i1 > 0) {
            LOGGER.warn("Encountered a problem while converting the player save files, retrying in a few seconds");
            this.sleepFiveSeconds();
         }

         flag4 = PreYggdrasilConverter.convertSaveFiles(this, this.settings);
      }

      return flag || flag1 || flag2 || flag3 || flag4;
   }

   private void sleepFiveSeconds() {
      try {
         Thread.sleep(5000L);
      } catch (InterruptedException var2) {
      }
   }

   public long getMaxTickTime() {
      return this.settings.getLongProperty("max-tick-time", TimeUnit.MINUTES.toMillis(1L));
   }

   public String getPlugins() {
      return "";
   }

   public String handleRConCommand(String p_71252_1_) {
      this.rconConsoleSource.resetLog();
      this.getCommandManager().handleCommand(this.rconConsoleSource.func_195540_f(), p_71252_1_);
      return this.rconConsoleSource.getLogContents();
   }
}
