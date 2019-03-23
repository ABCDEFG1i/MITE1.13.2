package net.minecraft.client.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.block.Block;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.GuardianSound;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.GuiScreenDemo;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.gui.toasts.RecipeToast;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.ParticleItemPickup;
import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.client.player.inventory.LocalBlockIntercommunication;
import net.minecraft.client.renderer.debug.DebugRendererNeighborsUpdate;
import net.minecraft.client.renderer.debug.DebugRendererWorldGenAttempts;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.NBTQueryManager;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.client.util.SearchTree;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.NpcMerchant;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityDragonFireball;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityLlamaSpit;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.entity.projectile.EntityTrident;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerHorseChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketAdvancementInfo;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketCamera;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.network.play.server.SPacketCommandList;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketCooldown;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.network.play.server.SPacketDisplayObjective;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.network.play.server.SPacketEntityHeadLook;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketNBTQueryResponse;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketPlaceGhostRecipe;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerLook;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketRecipeBook;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.network.play.server.SPacketResourcePackSend;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketScoreboardObjective;
import net.minecraft.network.play.server.SPacketSelectAdvancementsTab;
import net.minecraft.network.play.server.SPacketServerDifficulty;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketSignEditorOpen;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketSpawnPainting;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.network.play.server.SPacketSpawnPosition;
import net.minecraft.network.play.server.SPacketStatistics;
import net.minecraft.network.play.server.SPacketStopSound;
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraft.network.play.server.SPacketTagsList;
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.network.play.server.SPacketUnloadChunk;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.network.play.server.SPacketUpdateRecipes;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.network.play.server.SPacketUseBed;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.network.play.server.SPacketWindowProperty;
import net.minecraft.network.play.server.SPacketWorldBorder;
import net.minecraft.pathfinding.Path;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.Stat;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityConduit;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class NetHandlerPlayClient implements INetHandlerPlayClient {
   private static final Logger LOGGER = LogManager.getLogger();
   private final NetworkManager netManager;
   private final GameProfile profile;
   private final GuiScreen guiScreenServer;
   private Minecraft client;
   private WorldClient world;
   private boolean doneLoadingTerrain;
   private final Map<UUID, NetworkPlayerInfo> playerInfoMap = Maps.newHashMap();
   private final ClientAdvancementManager advancementManager;
   private final ClientSuggestionProvider clientSuggestionProvider;
   private NetworkTagManager networkTagManager = new NetworkTagManager();
   private final NBTQueryManager nbtQueryManager = new NBTQueryManager(this);
   private final Random avRandomizer = new Random();
   private CommandDispatcher<ISuggestionProvider> commandDispatcher = new CommandDispatcher<>();
   private final RecipeManager recipeManager = new RecipeManager();

   public NetHandlerPlayClient(Minecraft p_i46300_1_, GuiScreen p_i46300_2_, NetworkManager p_i46300_3_, GameProfile p_i46300_4_) {
      this.client = p_i46300_1_;
      this.guiScreenServer = p_i46300_2_;
      this.netManager = p_i46300_3_;
      this.profile = p_i46300_4_;
      this.advancementManager = new ClientAdvancementManager(p_i46300_1_);
      this.clientSuggestionProvider = new ClientSuggestionProvider(this, p_i46300_1_);
   }

   public ClientSuggestionProvider func_195513_b() {
      return this.clientSuggestionProvider;
   }

   public void cleanup() {
      this.world = null;
   }

   public RecipeManager getRecipeManager() {
      return this.recipeManager;
   }

   public void handleJoinGame(SPacketJoinGame p_147282_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147282_1_, this, this.client);
      this.client.playerController = new PlayerControllerMP(this.client, this);
      this.world = new WorldClient(this, new WorldSettings(0L, p_147282_1_.getGameType(), false, p_147282_1_.isHardcoreMode(), p_147282_1_.getWorldType()), p_147282_1_.func_212642_e(), p_147282_1_.getDifficulty(), this.client.profiler);
      this.client.gameSettings.difficulty = p_147282_1_.getDifficulty();
      this.client.loadWorld(this.world);
      this.client.player.dimension = p_147282_1_.func_212642_e();
      this.client.displayGuiScreen(new GuiDownloadTerrain());
      this.client.player.setEntityId(p_147282_1_.getPlayerId());
      this.client.player.setReducedDebug(p_147282_1_.isReducedDebugInfo());
      this.client.playerController.setGameType(p_147282_1_.getGameType());
      this.client.gameSettings.sendSettingsToServer();
      this.netManager.sendPacket(new CPacketCustomPayload(CPacketCustomPayload.BRAND, (new PacketBuffer(Unpooled.buffer())).writeString(ClientBrandRetriever.getClientModName())));
   }

   public void handleSpawnObject(SPacketSpawnObject p_147235_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147235_1_, this, this.client);
      double d0 = p_147235_1_.getX();
      double d1 = p_147235_1_.getY();
      double d2 = p_147235_1_.getZ();
      Entity entity = null;
      if (p_147235_1_.getType() == 10) {
         entity = EntityMinecart.create(this.world, d0, d1, d2, EntityMinecart.Type.getById(p_147235_1_.getData()));
      } else if (p_147235_1_.getType() == 90) {
         Entity entity1 = this.world.getEntityByID(p_147235_1_.getData());
         if (entity1 instanceof EntityPlayer) {
            entity = new EntityFishHook(this.world, (EntityPlayer)entity1, d0, d1, d2);
         }

         p_147235_1_.setData(0);
      } else if (p_147235_1_.getType() == 60) {
         entity = new EntityTippedArrow(this.world, d0, d1, d2);
      } else if (p_147235_1_.getType() == 91) {
         entity = new EntitySpectralArrow(this.world, d0, d1, d2);
      } else if (p_147235_1_.getType() == 94) {
         entity = new EntityTrident(this.world, d0, d1, d2);
      } else if (p_147235_1_.getType() == 61) {
         entity = new EntitySnowball(this.world, d0, d1, d2);
      } else if (p_147235_1_.getType() == 68) {
         entity = new EntityLlamaSpit(this.world, d0, d1, d2, (double)p_147235_1_.getSpeedX() / 8000.0D, (double)p_147235_1_.getSpeedY() / 8000.0D, (double)p_147235_1_.getSpeedZ() / 8000.0D);
      } else if (p_147235_1_.getType() == 71) {
         entity = new EntityItemFrame(this.world, new BlockPos(d0, d1, d2), EnumFacing.byIndex(p_147235_1_.getData()));
         p_147235_1_.setData(0);
      } else if (p_147235_1_.getType() == 77) {
         entity = new EntityLeashKnot(this.world, new BlockPos(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2)));
         p_147235_1_.setData(0);
      } else if (p_147235_1_.getType() == 65) {
         entity = new EntityEnderPearl(this.world, d0, d1, d2);
      } else if (p_147235_1_.getType() == 72) {
         entity = new EntityEnderEye(this.world, d0, d1, d2);
      } else if (p_147235_1_.getType() == 76) {
         entity = new EntityFireworkRocket(this.world, d0, d1, d2, ItemStack.EMPTY);
      } else if (p_147235_1_.getType() == 63) {
         entity = new EntityLargeFireball(this.world, d0, d1, d2, (double)p_147235_1_.getSpeedX() / 8000.0D, (double)p_147235_1_.getSpeedY() / 8000.0D, (double)p_147235_1_.getSpeedZ() / 8000.0D);
         p_147235_1_.setData(0);
      } else if (p_147235_1_.getType() == 93) {
         entity = new EntityDragonFireball(this.world, d0, d1, d2, (double)p_147235_1_.getSpeedX() / 8000.0D, (double)p_147235_1_.getSpeedY() / 8000.0D, (double)p_147235_1_.getSpeedZ() / 8000.0D);
         p_147235_1_.setData(0);
      } else if (p_147235_1_.getType() == 64) {
         entity = new EntitySmallFireball(this.world, d0, d1, d2, (double)p_147235_1_.getSpeedX() / 8000.0D, (double)p_147235_1_.getSpeedY() / 8000.0D, (double)p_147235_1_.getSpeedZ() / 8000.0D);
         p_147235_1_.setData(0);
      } else if (p_147235_1_.getType() == 66) {
         entity = new EntityWitherSkull(this.world, d0, d1, d2, (double)p_147235_1_.getSpeedX() / 8000.0D, (double)p_147235_1_.getSpeedY() / 8000.0D, (double)p_147235_1_.getSpeedZ() / 8000.0D);
         p_147235_1_.setData(0);
      } else if (p_147235_1_.getType() == 67) {
         entity = new EntityShulkerBullet(this.world, d0, d1, d2, (double)p_147235_1_.getSpeedX() / 8000.0D, (double)p_147235_1_.getSpeedY() / 8000.0D, (double)p_147235_1_.getSpeedZ() / 8000.0D);
         p_147235_1_.setData(0);
      } else if (p_147235_1_.getType() == 62) {
         entity = new EntityEgg(this.world, d0, d1, d2);
      } else if (p_147235_1_.getType() == 79) {
         entity = new EntityEvokerFangs(this.world, d0, d1, d2, 0.0F, 0, (EntityLivingBase)null);
      } else if (p_147235_1_.getType() == 73) {
         entity = new EntityPotion(this.world, d0, d1, d2, ItemStack.EMPTY);
         p_147235_1_.setData(0);
      } else if (p_147235_1_.getType() == 75) {
         entity = new EntityExpBottle(this.world, d0, d1, d2);
         p_147235_1_.setData(0);
      } else if (p_147235_1_.getType() == 1) {
         entity = new EntityBoat(this.world, d0, d1, d2);
      } else if (p_147235_1_.getType() == 50) {
         entity = new EntityTNTPrimed(this.world, d0, d1, d2, (EntityLivingBase)null);
      } else if (p_147235_1_.getType() == 78) {
         entity = new EntityArmorStand(this.world, d0, d1, d2);
      } else if (p_147235_1_.getType() == 51) {
         entity = new EntityEnderCrystal(this.world, d0, d1, d2);
      } else if (p_147235_1_.getType() == 2) {
         entity = new EntityItem(this.world, d0, d1, d2);
      } else if (p_147235_1_.getType() == 70) {
         entity = new EntityFallingBlock(this.world, d0, d1, d2, Block.getStateById(p_147235_1_.getData()));
         p_147235_1_.setData(0);
      } else if (p_147235_1_.getType() == 3) {
         entity = new EntityAreaEffectCloud(this.world, d0, d1, d2);
      }

      if (entity != null) {
         EntityTracker.updateServerPosition(entity, d0, d1, d2);
         entity.rotationPitch = (float)(p_147235_1_.getPitch() * 360) / 256.0F;
         entity.rotationYaw = (float)(p_147235_1_.getYaw() * 360) / 256.0F;
         Entity[] aentity = entity.getParts();
         if (aentity != null) {
            int i = p_147235_1_.getEntityID() - entity.getEntityId();

            for(Entity entity2 : aentity) {
               entity2.setEntityId(entity2.getEntityId() + i);
            }
         }

         entity.setEntityId(p_147235_1_.getEntityID());
         entity.setUniqueId(p_147235_1_.getUniqueId());
         this.world.addEntityToWorld(p_147235_1_.getEntityID(), entity);
         if (p_147235_1_.getData() > 0) {
            if (p_147235_1_.getType() == 60 || p_147235_1_.getType() == 91 || p_147235_1_.getType() == 94) {
               Entity entity3 = this.world.getEntityByID(p_147235_1_.getData() - 1);
               if (entity3 instanceof EntityLivingBase && entity instanceof EntityArrow) {
                  EntityArrow entityarrow = (EntityArrow)entity;
                  entityarrow.func_212361_a(entity3);
                  if (entity3 instanceof EntityPlayer) {
                     entityarrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
                     if (((EntityPlayer)entity3).capabilities.isCreativeMode) {
                        entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                     }
                  }
               }
            }

            entity.setVelocity((double)p_147235_1_.getSpeedX() / 8000.0D, (double)p_147235_1_.getSpeedY() / 8000.0D, (double)p_147235_1_.getSpeedZ() / 8000.0D);
         }
      }

   }

   public void handleSpawnExperienceOrb(SPacketSpawnExperienceOrb p_147286_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147286_1_, this, this.client);
      double d0 = p_147286_1_.getX();
      double d1 = p_147286_1_.getY();
      double d2 = p_147286_1_.getZ();
      Entity entity = new EntityXPOrb(this.world, d0, d1, d2, p_147286_1_.getXPValue());
      EntityTracker.updateServerPosition(entity, d0, d1, d2);
      entity.rotationYaw = 0.0F;
      entity.rotationPitch = 0.0F;
      entity.setEntityId(p_147286_1_.getEntityID());
      this.world.addEntityToWorld(p_147286_1_.getEntityID(), entity);
   }

   public void handleSpawnGlobalEntity(SPacketSpawnGlobalEntity p_147292_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147292_1_, this, this.client);
      double d0 = p_147292_1_.getX();
      double d1 = p_147292_1_.getY();
      double d2 = p_147292_1_.getZ();
      Entity entity = null;
      if (p_147292_1_.getType() == 1) {
         entity = new EntityLightningBolt(this.world, d0, d1, d2, false);
      }

      if (entity != null) {
         EntityTracker.updateServerPosition(entity, d0, d1, d2);
         entity.rotationYaw = 0.0F;
         entity.rotationPitch = 0.0F;
         entity.setEntityId(p_147292_1_.getEntityId());
         this.world.addWeatherEffect(entity);
      }

   }

   public void handleSpawnPainting(SPacketSpawnPainting p_147288_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147288_1_, this, this.client);
      EntityPainting entitypainting = new EntityPainting(this.world, p_147288_1_.getPosition(), p_147288_1_.getFacing(), p_147288_1_.func_201063_e());
      entitypainting.setUniqueId(p_147288_1_.getUniqueId());
      this.world.addEntityToWorld(p_147288_1_.getEntityID(), entitypainting);
   }

   public void handleEntityVelocity(SPacketEntityVelocity p_147244_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147244_1_, this, this.client);
      Entity entity = this.world.getEntityByID(p_147244_1_.getEntityID());
      if (entity != null) {
         entity.setVelocity((double)p_147244_1_.getMotionX() / 8000.0D, (double)p_147244_1_.getMotionY() / 8000.0D, (double)p_147244_1_.getMotionZ() / 8000.0D);
      }
   }

   public void handleEntityMetadata(SPacketEntityMetadata p_147284_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147284_1_, this, this.client);
      Entity entity = this.world.getEntityByID(p_147284_1_.getEntityId());
      if (entity != null && p_147284_1_.getDataManagerEntries() != null) {
         entity.getDataManager().setEntryValues(p_147284_1_.getDataManagerEntries());
      }

   }

   public void handleSpawnPlayer(SPacketSpawnPlayer p_147237_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147237_1_, this, this.client);
      double d0 = p_147237_1_.getX();
      double d1 = p_147237_1_.getY();
      double d2 = p_147237_1_.getZ();
      float f = (float)(p_147237_1_.getYaw() * 360) / 256.0F;
      float f1 = (float)(p_147237_1_.getPitch() * 360) / 256.0F;
      EntityOtherPlayerMP entityotherplayermp = new EntityOtherPlayerMP(this.client.world, this.getPlayerInfo(p_147237_1_.getUniqueId()).getGameProfile());
      entityotherplayermp.prevPosX = d0;
      entityotherplayermp.lastTickPosX = d0;
      entityotherplayermp.prevPosY = d1;
      entityotherplayermp.lastTickPosY = d1;
      entityotherplayermp.prevPosZ = d2;
      entityotherplayermp.lastTickPosZ = d2;
      EntityTracker.updateServerPosition(entityotherplayermp, d0, d1, d2);
      entityotherplayermp.setPositionAndRotation(d0, d1, d2, f, f1);
      this.world.addEntityToWorld(p_147237_1_.getEntityID(), entityotherplayermp);
      List<EntityDataManager.DataEntry<?>> list = p_147237_1_.getDataManagerEntries();
      if (list != null) {
         entityotherplayermp.getDataManager().setEntryValues(list);
      }

   }

   public void handleEntityTeleport(SPacketEntityTeleport p_147275_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147275_1_, this, this.client);
      Entity entity = this.world.getEntityByID(p_147275_1_.getEntityId());
      if (entity != null) {
         double d0 = p_147275_1_.getX();
         double d1 = p_147275_1_.getY();
         double d2 = p_147275_1_.getZ();
         EntityTracker.updateServerPosition(entity, d0, d1, d2);
         if (!entity.canPassengerSteer()) {
            float f = (float)(p_147275_1_.getYaw() * 360) / 256.0F;
            float f1 = (float)(p_147275_1_.getPitch() * 360) / 256.0F;
            if (!(Math.abs(entity.posX - d0) >= 0.03125D) && !(Math.abs(entity.posY - d1) >= 0.015625D) && !(Math.abs(entity.posZ - d2) >= 0.03125D)) {
               entity.setPositionAndRotationDirect(entity.posX, entity.posY, entity.posZ, f, f1, 0, true);
            } else {
               entity.setPositionAndRotationDirect(d0, d1, d2, f, f1, 3, true);
            }

            entity.onGround = p_147275_1_.getOnGround();
         }

      }
   }

   public void handleHeldItemChange(SPacketHeldItemChange p_147257_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147257_1_, this, this.client);
      if (InventoryPlayer.isHotbar(p_147257_1_.getHeldItemHotbarIndex())) {
         this.client.player.inventory.currentItem = p_147257_1_.getHeldItemHotbarIndex();
      }

   }

   public void handleEntityMovement(SPacketEntity p_147259_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147259_1_, this, this.client);
      Entity entity = p_147259_1_.getEntity(this.world);
      if (entity != null) {
         entity.serverPosX += (long)p_147259_1_.getX();
         entity.serverPosY += (long)p_147259_1_.getY();
         entity.serverPosZ += (long)p_147259_1_.getZ();
         double d0 = (double)entity.serverPosX / 4096.0D;
         double d1 = (double)entity.serverPosY / 4096.0D;
         double d2 = (double)entity.serverPosZ / 4096.0D;
         if (!entity.canPassengerSteer()) {
            float f = p_147259_1_.isRotating() ? (float)(p_147259_1_.getYaw() * 360) / 256.0F : entity.rotationYaw;
            float f1 = p_147259_1_.isRotating() ? (float)(p_147259_1_.getPitch() * 360) / 256.0F : entity.rotationPitch;
            entity.setPositionAndRotationDirect(d0, d1, d2, f, f1, 3, false);
            entity.onGround = p_147259_1_.getOnGround();
         }

      }
   }

   public void handleEntityHeadLook(SPacketEntityHeadLook p_147267_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147267_1_, this, this.client);
      Entity entity = p_147267_1_.getEntity(this.world);
      if (entity != null) {
         float f = (float)(p_147267_1_.getYaw() * 360) / 256.0F;
         entity.setHeadRotation(f, 3);
      }
   }

   public void handleDestroyEntities(SPacketDestroyEntities p_147238_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147238_1_, this, this.client);

      for(int i = 0; i < p_147238_1_.getEntityIDs().length; ++i) {
         this.world.removeEntityFromWorld(p_147238_1_.getEntityIDs()[i]);
      }

   }

   public void handlePlayerPosLook(SPacketPlayerPosLook p_184330_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_184330_1_, this, this.client);
      EntityPlayer entityplayer = this.client.player;
      double d0 = p_184330_1_.getX();
      double d1 = p_184330_1_.getY();
      double d2 = p_184330_1_.getZ();
      float f = p_184330_1_.getYaw();
      float f1 = p_184330_1_.getPitch();
      if (p_184330_1_.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X)) {
         d0 += entityplayer.posX;
      } else {
         entityplayer.motionX = 0.0D;
      }

      if (p_184330_1_.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y)) {
         d1 += entityplayer.posY;
      } else {
         entityplayer.motionY = 0.0D;
      }

      if (p_184330_1_.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Z)) {
         d2 += entityplayer.posZ;
      } else {
         entityplayer.motionZ = 0.0D;
      }

      if (p_184330_1_.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X_ROT)) {
         f1 += entityplayer.rotationPitch;
      }

      if (p_184330_1_.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y_ROT)) {
         f += entityplayer.rotationYaw;
      }

      entityplayer.setPositionAndRotation(d0, d1, d2, f, f1);
      this.netManager.sendPacket(new CPacketConfirmTeleport(p_184330_1_.getTeleportId()));
      this.netManager.sendPacket(new CPacketPlayer.PositionRotation(entityplayer.posX, entityplayer.getEntityBoundingBox().minY, entityplayer.posZ, entityplayer.rotationYaw, entityplayer.rotationPitch, false));
      if (!this.doneLoadingTerrain) {
         this.client.player.prevPosX = this.client.player.posX;
         this.client.player.prevPosY = this.client.player.posY;
         this.client.player.prevPosZ = this.client.player.posZ;
         this.doneLoadingTerrain = true;
         this.client.displayGuiScreen((GuiScreen)null);
      }

   }

   public void handleMultiBlockChange(SPacketMultiBlockChange p_147287_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147287_1_, this, this.client);

      for(SPacketMultiBlockChange.BlockUpdateData spacketmultiblockchange$blockupdatedata : p_147287_1_.getChangedBlocks()) {
         this.world.func_195597_b(spacketmultiblockchange$blockupdatedata.getPos(), spacketmultiblockchange$blockupdatedata.getBlockState());
      }

   }

   public void handleChunkData(SPacketChunkData p_147263_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147263_1_, this, this.client);
      int i = p_147263_1_.getChunkX();
      int j = p_147263_1_.getChunkZ();
      Chunk chunk = this.world.getChunkProvider().func_212474_a(i, j, p_147263_1_.getReadBuffer(), p_147263_1_.getExtractedSize(), p_147263_1_.isFullChunk());
      this.world.markBlockRangeForRenderUpdate(i << 4, 0, j << 4, (i << 4) + 15, 256, (j << 4) + 15);
      if (!p_147263_1_.isFullChunk() || !(this.world.dimension instanceof OverworldDimension)) {
         chunk.resetRelightChecks();
      }

      for(NBTTagCompound nbttagcompound : p_147263_1_.getTileEntityTags()) {
         BlockPos blockpos = new BlockPos(nbttagcompound.getInteger("x"), nbttagcompound.getInteger("y"), nbttagcompound.getInteger("z"));
         TileEntity tileentity = this.world.getTileEntity(blockpos);
         if (tileentity != null) {
            tileentity.readFromNBT(nbttagcompound);
         }
      }

   }

   public void processChunkUnload(SPacketUnloadChunk p_184326_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_184326_1_, this, this.client);
      int i = p_184326_1_.getX();
      int j = p_184326_1_.getZ();
      this.world.getChunkProvider().unloadChunk(i, j);
      this.world.markBlockRangeForRenderUpdate(i << 4, 0, j << 4, (i << 4) + 15, 256, (j << 4) + 15);
   }

   public void handleBlockChange(SPacketBlockChange p_147234_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147234_1_, this, this.client);
      this.world.func_195597_b(p_147234_1_.getPos(), p_147234_1_.getState());
   }

   public void handleDisconnect(SPacketDisconnect p_147253_1_) {
      this.netManager.closeChannel(p_147253_1_.getReason());
   }

   public void onDisconnect(ITextComponent p_147231_1_) {
      this.client.loadWorld((WorldClient)null);
      if (this.guiScreenServer != null) {
         if (this.guiScreenServer instanceof GuiScreenRealmsProxy) {
            this.client.displayGuiScreen((new DisconnectedRealmsScreen(((GuiScreenRealmsProxy)this.guiScreenServer).getProxy(), "disconnect.lost", p_147231_1_)).getProxy());
         } else {
            this.client.displayGuiScreen(new GuiDisconnected(this.guiScreenServer, "disconnect.lost", p_147231_1_));
         }
      } else {
         this.client.displayGuiScreen(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.lost", p_147231_1_));
      }

   }

   public void sendPacket(Packet<?> p_147297_1_) {
      this.netManager.sendPacket(p_147297_1_);
   }

   public void handleCollectItem(SPacketCollectItem p_147246_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147246_1_, this, this.client);
      Entity entity = this.world.getEntityByID(p_147246_1_.getCollectedItemEntityID());
      EntityLivingBase entitylivingbase = (EntityLivingBase)this.world.getEntityByID(p_147246_1_.getEntityID());
      if (entitylivingbase == null) {
         entitylivingbase = this.client.player;
      }

      if (entity != null) {
         if (entity instanceof EntityXPOrb) {
            this.world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, (this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.35F + 0.9F, false);
         } else {
            this.world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, (this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 1.4F + 2.0F, false);
         }

         if (entity instanceof EntityItem) {
            ((EntityItem)entity).getItem().setCount(p_147246_1_.getAmount());
         }

         this.client.effectRenderer.addEffect(new ParticleItemPickup(this.world, entity, entitylivingbase, 0.5F));
         this.world.removeEntityFromWorld(p_147246_1_.getCollectedItemEntityID());
      }

   }

   public void handleChat(SPacketChat p_147251_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147251_1_, this, this.client);
      this.client.ingameGUI.addChatMessage(p_147251_1_.getType(), p_147251_1_.getChatComponent());
   }

   public void handleAnimation(SPacketAnimation p_147279_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147279_1_, this, this.client);
      Entity entity = this.world.getEntityByID(p_147279_1_.getEntityID());
      if (entity != null) {
         if (p_147279_1_.getAnimationType() == 0) {
            EntityLivingBase entitylivingbase = (EntityLivingBase)entity;
            entitylivingbase.swingArm(EnumHand.MAIN_HAND);
         } else if (p_147279_1_.getAnimationType() == 3) {
            EntityLivingBase entitylivingbase1 = (EntityLivingBase)entity;
            entitylivingbase1.swingArm(EnumHand.OFF_HAND);
         } else if (p_147279_1_.getAnimationType() == 1) {
            entity.performHurtAnimation();
         } else if (p_147279_1_.getAnimationType() == 2) {
            EntityPlayer entityplayer = (EntityPlayer)entity;
            entityplayer.wakeUpPlayer(false, false, false);
         } else if (p_147279_1_.getAnimationType() == 4) {
            this.client.effectRenderer.addParticleEmitter(entity, Particles.CRIT);
         } else if (p_147279_1_.getAnimationType() == 5) {
            this.client.effectRenderer.addParticleEmitter(entity, Particles.ENCHANTED_HIT);
         }

      }
   }

   public void handleUseBed(SPacketUseBed p_147278_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147278_1_, this, this.client);
      p_147278_1_.getPlayer(this.world).trySleep(p_147278_1_.getBedPosition());
   }

   public void handleSpawnMob(SPacketSpawnMob p_147281_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147281_1_, this, this.client);
      double d0 = p_147281_1_.getX();
      double d1 = p_147281_1_.getY();
      double d2 = p_147281_1_.getZ();
      float f = (float)(p_147281_1_.getYaw() * 360) / 256.0F;
      float f1 = (float)(p_147281_1_.getPitch() * 360) / 256.0F;
      EntityLivingBase entitylivingbase = (EntityLivingBase)EntityType.create(p_147281_1_.getEntityType(), this.client.world);
      if (entitylivingbase != null) {
         EntityTracker.updateServerPosition(entitylivingbase, d0, d1, d2);
         entitylivingbase.renderYawOffset = (float)(p_147281_1_.getHeadPitch() * 360) / 256.0F;
         entitylivingbase.rotationYawHead = (float)(p_147281_1_.getHeadPitch() * 360) / 256.0F;
         Entity[] aentity = entitylivingbase.getParts();
         if (aentity != null) {
            int i = p_147281_1_.getEntityID() - entitylivingbase.getEntityId();

            for(Entity entity : aentity) {
               entity.setEntityId(entity.getEntityId() + i);
            }
         }

         entitylivingbase.setEntityId(p_147281_1_.getEntityID());
         entitylivingbase.setUniqueId(p_147281_1_.getUniqueId());
         entitylivingbase.setPositionAndRotation(d0, d1, d2, f, f1);
         entitylivingbase.motionX = (double)((float)p_147281_1_.getVelocityX() / 8000.0F);
         entitylivingbase.motionY = (double)((float)p_147281_1_.getVelocityY() / 8000.0F);
         entitylivingbase.motionZ = (double)((float)p_147281_1_.getVelocityZ() / 8000.0F);
         this.world.addEntityToWorld(p_147281_1_.getEntityID(), entitylivingbase);
         List<EntityDataManager.DataEntry<?>> list = p_147281_1_.getDataManagerEntries();
         if (list != null) {
            entitylivingbase.getDataManager().setEntryValues(list);
         }
      } else {
         LOGGER.warn("Skipping Entity with id {}", (int)p_147281_1_.getEntityType());
      }

   }

   public void handleTimeUpdate(SPacketTimeUpdate p_147285_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147285_1_, this, this.client);
      this.client.world.setTotalWorldTime(p_147285_1_.getTotalWorldTime());
      this.client.world.setWorldTime(p_147285_1_.getWorldTime());
   }

   public void handleSpawnPosition(SPacketSpawnPosition p_147271_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147271_1_, this, this.client);
      this.client.player.setSpawnPoint(p_147271_1_.getSpawnPos(), true);
      this.client.world.getWorldInfo().setSpawn(p_147271_1_.getSpawnPos());
   }

   public void handleSetPassengers(SPacketSetPassengers p_184328_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_184328_1_, this, this.client);
      Entity entity = this.world.getEntityByID(p_184328_1_.getEntityId());
      if (entity == null) {
         LOGGER.warn("Received passengers for unknown entity");
      } else {
         boolean flag = entity.isRidingOrBeingRiddenBy(this.client.player);
         entity.removePassengers();

         for(int i : p_184328_1_.getPassengerIds()) {
            Entity entity1 = this.world.getEntityByID(i);
            if (entity1 != null) {
               entity1.startRiding(entity, true);
               if (entity1 == this.client.player && !flag) {
                  this.client.ingameGUI.setOverlayMessage(I18n.format("mount.onboard", this.client.gameSettings.keyBindSneak.func_197978_k()), false);
               }
            }
         }

      }
   }

   public void handleEntityAttach(SPacketEntityAttach p_147243_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147243_1_, this, this.client);
      Entity entity = this.world.getEntityByID(p_147243_1_.getEntityId());
      Entity entity1 = this.world.getEntityByID(p_147243_1_.getVehicleEntityId());
      if (entity instanceof EntityLiving) {
         if (entity1 != null) {
            ((EntityLiving)entity).setLeashHolder(entity1, false);
         } else {
            ((EntityLiving)entity).clearLeashed(false, false);
         }
      }

   }

   public void handleEntityStatus(SPacketEntityStatus p_147236_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147236_1_, this, this.client);
      Entity entity = p_147236_1_.getEntity(this.world);
      if (entity != null) {
         if (p_147236_1_.getOpCode() == 21) {
            this.client.getSoundHandler().play(new GuardianSound((EntityGuardian)entity));
         } else if (p_147236_1_.getOpCode() == 35) {
            int i = 40;
            this.client.effectRenderer.func_199281_a(entity, Particles.TOTEM_OF_UNDYING, 30);
            this.world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), 1.0F, 1.0F, false);
            if (entity == this.client.player) {
               this.client.entityRenderer.func_190565_a(new ItemStack(Items.TOTEM_OF_UNDYING));
            }
         } else {
            entity.handleStatusUpdate(p_147236_1_.getOpCode());
         }
      }

   }

   public void handleUpdateHealth(SPacketUpdateHealth p_147249_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147249_1_, this, this.client);
      this.client.player.setPlayerSPHealth(p_147249_1_.getHealth());
      this.client.player.getFoodStats().setFoodLevel(p_147249_1_.getFoodLevel());
      this.client.player.getFoodStats().setFoodSaturationLevel(p_147249_1_.getSaturationLevel());
      this.client.player.getFoodStats().setMaxFoodLevel(p_147249_1_.getMaxFoodLevel());
   }

   public void handleSetExperience(SPacketSetExperience p_147295_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147295_1_, this, this.client);
      this.client.player.setXPStats(p_147295_1_.getExperienceBar(), p_147295_1_.getTotalExperience(), p_147295_1_.getLevel());
   }

   public void handleRespawn(SPacketRespawn p_147280_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147280_1_, this, this.client);
      DimensionType dimensiontype = p_147280_1_.func_212643_b();
      if (dimensiontype != this.client.player.dimension) {
         this.doneLoadingTerrain = false;
         Scoreboard scoreboard = this.world.getScoreboard();
         this.world = new WorldClient(this, new WorldSettings(0L, p_147280_1_.getGameType(), false, this.client.world.getWorldInfo().isHardcoreModeEnabled(), p_147280_1_.getWorldType()), p_147280_1_.func_212643_b(), p_147280_1_.getDifficulty(), this.client.profiler);
         this.world.setWorldScoreboard(scoreboard);
         this.client.loadWorld(this.world);
         this.client.player.dimension = dimensiontype;
         this.client.displayGuiScreen(new GuiDownloadTerrain());
      }

      this.client.func_212315_a(p_147280_1_.func_212643_b());
      this.client.playerController.setGameType(p_147280_1_.getGameType());
   }

   public void handleExplosion(SPacketExplosion p_147283_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147283_1_, this, this.client);
      Explosion explosion = new Explosion(this.client.world, (Entity)null, p_147283_1_.getX(), p_147283_1_.getY(), p_147283_1_.getZ(), p_147283_1_.getStrength(), p_147283_1_.getAffectedBlockPositions());
      explosion.doExplosionB(true);
      this.client.player.motionX += (double)p_147283_1_.getMotionX();
      this.client.player.motionY += (double)p_147283_1_.getMotionY();
      this.client.player.motionZ += (double)p_147283_1_.getMotionZ();
   }

   public void handleOpenWindow(SPacketOpenWindow p_147265_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147265_1_, this, this.client);
      EntityPlayerSP entityplayersp = this.client.player;
      if ("minecraft:container".equals(p_147265_1_.getGuiId())) {
         entityplayersp.displayGUIChest(new InventoryBasic(p_147265_1_.getWindowTitle(), p_147265_1_.getSlotCount()));
         entityplayersp.openContainer.windowId = p_147265_1_.getWindowId();
      } else if ("minecraft:villager".equals(p_147265_1_.getGuiId())) {
         entityplayersp.displayVillagerTradeGui(new NpcMerchant(entityplayersp, p_147265_1_.getWindowTitle()));
         entityplayersp.openContainer.windowId = p_147265_1_.getWindowId();
      } else if ("EntityHorse".equals(p_147265_1_.getGuiId())) {
         Entity entity = this.world.getEntityByID(p_147265_1_.getEntityId());
         if (entity instanceof AbstractHorse) {
            entityplayersp.openGuiHorseInventory((AbstractHorse)entity, new ContainerHorseChest(p_147265_1_.getWindowTitle(), p_147265_1_.getSlotCount()));
            entityplayersp.openContainer.windowId = p_147265_1_.getWindowId();
         }
      } else if (!p_147265_1_.hasSlots()) {
         entityplayersp.displayGui(new LocalBlockIntercommunication(p_147265_1_.getGuiId(), p_147265_1_.getWindowTitle()));
         entityplayersp.openContainer.windowId = p_147265_1_.getWindowId();
      } else {
         IInventory iinventory = new ContainerLocalMenu(p_147265_1_.getGuiId(), p_147265_1_.getWindowTitle(), p_147265_1_.getSlotCount());
         entityplayersp.displayGUIChest(iinventory);
         entityplayersp.openContainer.windowId = p_147265_1_.getWindowId();
      }

   }

   public void handleSetSlot(SPacketSetSlot p_147266_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147266_1_, this, this.client);
      EntityPlayer entityplayer = this.client.player;
      ItemStack itemstack = p_147266_1_.getStack();
      int i = p_147266_1_.getSlot();
      this.client.getTutorial().handleSetSlot(itemstack);
      if (p_147266_1_.getWindowId() == -1) {
         entityplayer.inventory.setItemStack(itemstack);
      } else if (p_147266_1_.getWindowId() == -2) {
         entityplayer.inventory.setInventorySlotContents(i, itemstack);
      } else {
         boolean flag = false;
         if (this.client.currentScreen instanceof GuiContainerCreative) {
            GuiContainerCreative guicontainercreative = (GuiContainerCreative)this.client.currentScreen;
            flag = guicontainercreative.getSelectedTabIndex() != ItemGroup.INVENTORY.getIndex();
         }

         if (p_147266_1_.getWindowId() == 0 && p_147266_1_.getSlot() >= 36 && i < 45) {
            if (!itemstack.isEmpty()) {
               ItemStack itemstack1 = entityplayer.inventoryContainer.getSlot(i).getStack();
               if (itemstack1.isEmpty() || itemstack1.getCount() < itemstack.getCount()) {
                  itemstack.setAnimationsToGo(5);
               }
            }

            entityplayer.inventoryContainer.putStackInSlot(i, itemstack);
         } else if (p_147266_1_.getWindowId() == entityplayer.openContainer.windowId && (p_147266_1_.getWindowId() != 0 || !flag)) {
            entityplayer.openContainer.putStackInSlot(i, itemstack);
         }
      }

   }

   public void handleConfirmTransaction(SPacketConfirmTransaction p_147239_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147239_1_, this, this.client);
      Container container = null;
      EntityPlayer entityplayer = this.client.player;
      if (p_147239_1_.getWindowId() == 0) {
         container = entityplayer.inventoryContainer;
      } else if (p_147239_1_.getWindowId() == entityplayer.openContainer.windowId) {
         container = entityplayer.openContainer;
      }

      if (container != null && !p_147239_1_.wasAccepted()) {
         this.sendPacket(new CPacketConfirmTransaction(p_147239_1_.getWindowId(), p_147239_1_.getActionNumber(), true));
      }

   }

   public void handleWindowItems(SPacketWindowItems p_147241_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147241_1_, this, this.client);
      EntityPlayer entityplayer = this.client.player;
      if (p_147241_1_.getWindowId() == 0) {
         entityplayer.inventoryContainer.setAll(p_147241_1_.getItemStacks());
      } else if (p_147241_1_.getWindowId() == entityplayer.openContainer.windowId) {
         entityplayer.openContainer.setAll(p_147241_1_.getItemStacks());
      }

   }

   public void handleSignEditorOpen(SPacketSignEditorOpen p_147268_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147268_1_, this, this.client);
      TileEntity tileentity = this.world.getTileEntity(p_147268_1_.getSignPosition());
      if (!(tileentity instanceof TileEntitySign)) {
         tileentity = new TileEntitySign();
         tileentity.setWorld(this.world);
         tileentity.setPos(p_147268_1_.getSignPosition());
      }

      this.client.player.openEditSign((TileEntitySign)tileentity);
   }

   public void handleUpdateTileEntity(SPacketUpdateTileEntity p_147273_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147273_1_, this, this.client);
      if (this.client.world.isBlockLoaded(p_147273_1_.getPos())) {
         TileEntity tileentity = this.client.world.getTileEntity(p_147273_1_.getPos());
         int i = p_147273_1_.getTileEntityType();
         boolean flag = i == 2 && tileentity instanceof TileEntityCommandBlock;
         if (i == 1 && tileentity instanceof TileEntityMobSpawner || flag || i == 3 && tileentity instanceof TileEntityBeacon || i == 4 && tileentity instanceof TileEntitySkull || i == 6 && tileentity instanceof TileEntityBanner || i == 7 && tileentity instanceof TileEntityStructure || i == 8 && tileentity instanceof TileEntityEndGateway || i == 9 && tileentity instanceof TileEntitySign || i == 10 && tileentity instanceof TileEntityShulkerBox || i == 11 && tileentity instanceof TileEntityBed || i == 5 && tileentity instanceof TileEntityConduit) {
            tileentity.readFromNBT(p_147273_1_.getNbtCompound());
         }

         if (flag && this.client.currentScreen instanceof GuiCommandBlock) {
            ((GuiCommandBlock)this.client.currentScreen).updateGui();
         }
      }

   }

   public void handleWindowProperty(SPacketWindowProperty p_147245_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147245_1_, this, this.client);
      EntityPlayer entityplayer = this.client.player;
      if (entityplayer.openContainer != null && entityplayer.openContainer.windowId == p_147245_1_.getWindowId()) {
         entityplayer.openContainer.updateProgressBar(p_147245_1_.getProperty(), p_147245_1_.getValue());
      }

   }

   public void handleEntityEquipment(SPacketEntityEquipment p_147242_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147242_1_, this, this.client);
      Entity entity = this.world.getEntityByID(p_147242_1_.getEntityID());
      if (entity != null) {
         entity.setItemStackToSlot(p_147242_1_.getEquipmentSlot(), p_147242_1_.getItemStack());
      }

   }

   public void handleCloseWindow(SPacketCloseWindow p_147276_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147276_1_, this, this.client);
      this.client.player.closeScreenAndDropStack();
   }

   public void handleBlockAction(SPacketBlockAction p_147261_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147261_1_, this, this.client);
      this.client.world.addBlockEvent(p_147261_1_.getBlockPosition(), p_147261_1_.getBlockType(), p_147261_1_.getData1(), p_147261_1_.getData2());
   }

   public void handleBlockBreakAnim(SPacketBlockBreakAnim p_147294_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147294_1_, this, this.client);
      this.client.world.sendBlockBreakProgress(p_147294_1_.getBreakerId(), p_147294_1_.getPosition(), p_147294_1_.getProgress());
   }

   public void handleChangeGameState(SPacketChangeGameState p_147252_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147252_1_, this, this.client);
      EntityPlayer entityplayer = this.client.player;
      int i = p_147252_1_.getGameState();
      float f = p_147252_1_.getValue();
      int j = MathHelper.floor(f + 0.5F);
      if (i >= 0 && i < SPacketChangeGameState.MESSAGE_NAMES.length && SPacketChangeGameState.MESSAGE_NAMES[i] != null) {
         entityplayer.sendStatusMessage(new TextComponentTranslation(SPacketChangeGameState.MESSAGE_NAMES[i]), false);
      }

      if (i == 1) {
         this.world.getWorldInfo().setRaining(true);
         this.world.setRainStrength(0.0F);
      } else if (i == 2) {
         this.world.getWorldInfo().setRaining(false);
         this.world.setRainStrength(1.0F);
      } else if (i == 3) {
         this.client.playerController.setGameType(GameType.getByID(j));
      } else if (i == 4) {
         if (j == 0) {
            this.client.player.connection.sendPacket(new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
            this.client.displayGuiScreen(new GuiDownloadTerrain());
         } else if (j == 1) {
            this.client.displayGuiScreen(new GuiWinGame(true, () -> {
               this.client.player.connection.sendPacket(new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
            }));
         }
      } else if (i == 5) {
         GameSettings gamesettings = this.client.gameSettings;
         if (f == 0.0F) {
            this.client.displayGuiScreen(new GuiScreenDemo());
         } else if (f == 101.0F) {
            this.client.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("demo.help.movement", gamesettings.keyBindForward.func_197978_k(), gamesettings.keyBindLeft.func_197978_k(), gamesettings.keyBindBack.func_197978_k(), gamesettings.keyBindRight.func_197978_k()));
         } else if (f == 102.0F) {
            this.client.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("demo.help.jump", gamesettings.keyBindJump.func_197978_k()));
         } else if (f == 103.0F) {
            this.client.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("demo.help.inventory", gamesettings.keyBindInventory.func_197978_k()));
         } else if (f == 104.0F) {
            this.client.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("demo.day.6", gamesettings.keyBindScreenshot.func_197978_k()));
         }
      } else if (i == 6) {
         this.world.playSound(entityplayer, entityplayer.posX, entityplayer.posY + (double)entityplayer.getEyeHeight(), entityplayer.posZ, SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 0.18F, 0.45F);
      } else if (i == 7) {
         this.world.setRainStrength(f);
      } else if (i == 8) {
         this.world.setThunderStrength(f);
      } else if (i == 9) {
         this.world.playSound(entityplayer, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_PUFFER_FISH_STING, SoundCategory.NEUTRAL, 1.0F, 1.0F);
      } else if (i == 10) {
         this.world.spawnParticle(Particles.ELDER_GUARDIAN, entityplayer.posX, entityplayer.posY, entityplayer.posZ, 0.0D, 0.0D, 0.0D);
         this.world.playSound(entityplayer, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.HOSTILE, 1.0F, 1.0F);
      }

   }

   public void handleMaps(SPacketMaps p_147264_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147264_1_, this, this.client);
      MapItemRenderer mapitemrenderer = this.client.entityRenderer.func_147701_i();
      String s = "map_" + p_147264_1_.getMapId();
      MapData mapdata = ItemMap.loadMapData(this.client.world, s);
      if (mapdata == null) {
         mapdata = new MapData(s);
         if (mapitemrenderer.getMapInstanceIfExists(s) != null) {
            MapData mapdata1 = mapitemrenderer.getData(mapitemrenderer.getMapInstanceIfExists(s));
            if (mapdata1 != null) {
               mapdata = mapdata1;
            }
         }

         this.client.world.func_212409_a(DimensionType.OVERWORLD, s, mapdata);
      }

      p_147264_1_.setMapdataTo(mapdata);
      mapitemrenderer.updateMapTexture(mapdata);
   }

   public void handleEffect(SPacketEffect p_147277_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147277_1_, this, this.client);
      if (p_147277_1_.isSoundServerwide()) {
         this.client.world.playBroadcastSound(p_147277_1_.getSoundType(), p_147277_1_.getSoundPos(), p_147277_1_.getSoundData());
      } else {
         this.client.world.playEvent(p_147277_1_.getSoundType(), p_147277_1_.getSoundPos(), p_147277_1_.getSoundData());
      }

   }

   public void handleAdvancementInfo(SPacketAdvancementInfo p_191981_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_191981_1_, this, this.client);
      this.advancementManager.read(p_191981_1_);
   }

   public void handleSelectAdvancementsTab(SPacketSelectAdvancementsTab p_194022_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_194022_1_, this, this.client);
      ResourceLocation resourcelocation = p_194022_1_.getTab();
      if (resourcelocation == null) {
         this.advancementManager.setSelectedTab((Advancement)null, false);
      } else {
         Advancement advancement = this.advancementManager.getAdvancementList().getAdvancement(resourcelocation);
         this.advancementManager.setSelectedTab(advancement, false);
      }

   }

   public void handleCommandList(SPacketCommandList p_195511_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_195511_1_, this, this.client);
      this.commandDispatcher = new CommandDispatcher<>(p_195511_1_.getRoot());
   }

   public void handleStopSound(SPacketStopSound p_195512_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_195512_1_, this, this.client);
      this.client.getSoundHandler().stop(p_195512_1_.func_197703_a(), p_195512_1_.getCategory());
   }

   public void handleTabComplete(SPacketTabComplete p_195510_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_195510_1_, this, this.client);
      this.clientSuggestionProvider.func_197015_a(p_195510_1_.func_197689_a(), p_195510_1_.getSuggestions());
   }

   public void func_199525_a(SPacketUpdateRecipes p_199525_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_199525_1_, this, this.client);
      this.recipeManager.clear();

      for(IRecipe irecipe : p_199525_1_.func_199616_a()) {
         this.recipeManager.addRecipe(irecipe);
      }

      SearchTree<RecipeList> searchtree = (SearchTree)this.client.<RecipeList>getSearchTree(SearchTreeManager.RECIPES);
      searchtree.func_199550_b();
      RecipeBookClient recipebookclient = this.client.player.getRecipeBook();
      recipebookclient.rebuildTable();
      recipebookclient.getRecipes().forEach(searchtree::add);
      searchtree.recalculate();
   }

   public void handlePlayerLook(SPacketPlayerLook p_200232_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_200232_1_, this, this.client);
      Vec3d vec3d = p_200232_1_.func_200531_a(this.world);
      if (vec3d != null) {
         this.client.player.func_200602_a(p_200232_1_.func_201064_a(), vec3d);
      }

   }

   public void handleNBTQueryResponse(SPacketNBTQueryResponse p_211522_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_211522_1_, this, this.client);
      if (!this.nbtQueryManager.handleResponse(p_211522_1_.func_211713_b(), p_211522_1_.getTag())) {
         LOGGER.debug("Got unhandled response to tag query {}", (int)p_211522_1_.func_211713_b());
      }

   }

   public void handleStatistics(SPacketStatistics p_147293_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147293_1_, this, this.client);

      for(Entry<Stat<?>, Integer> entry : p_147293_1_.getStatisticMap().entrySet()) {
         Stat<?> stat = entry.getKey();
         int i = entry.getValue();
         this.client.player.getStatFileWriter().func_150873_a(this.client.player, stat, i);
      }

      if (this.client.currentScreen instanceof IProgressMeter) {
         ((IProgressMeter)this.client.currentScreen).onStatsUpdated();
      }

   }

   public void handleRecipeBook(SPacketRecipeBook p_191980_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_191980_1_, this, this.client);
      RecipeBookClient recipebookclient = this.client.player.getRecipeBook();
      recipebookclient.setGuiOpen(p_191980_1_.isGuiOpen());
      recipebookclient.setFilteringCraftable(p_191980_1_.isFilteringCraftable());
      recipebookclient.setFurnaceGuiOpen(p_191980_1_.isFurnaceGuiOpen());
      recipebookclient.setFurnaceFilteringCraftable(p_191980_1_.isFurnaceFilteringCraftable());
      SPacketRecipeBook.State spacketrecipebook$state = p_191980_1_.getState();
      switch(spacketrecipebook$state) {
      case REMOVE:
         for(ResourceLocation resourcelocation3 : p_191980_1_.getRecipes()) {
            IRecipe irecipe3 = this.recipeManager.getRecipe(resourcelocation3);
            if (irecipe3 != null) {
               recipebookclient.lock(irecipe3);
            }
         }
         break;
      case INIT:
         for(ResourceLocation resourcelocation1 : p_191980_1_.getRecipes()) {
            IRecipe irecipe1 = this.recipeManager.getRecipe(resourcelocation1);
            if (irecipe1 != null) {
               recipebookclient.unlock(irecipe1);
            }
         }

         for(ResourceLocation resourcelocation2 : p_191980_1_.getDisplayedRecipes()) {
            IRecipe irecipe2 = this.recipeManager.getRecipe(resourcelocation2);
            if (irecipe2 != null) {
               recipebookclient.markNew(irecipe2);
            }
         }
         break;
      case ADD:
         for(ResourceLocation resourcelocation : p_191980_1_.getRecipes()) {
            IRecipe irecipe = this.recipeManager.getRecipe(resourcelocation);
            if (irecipe != null) {
               recipebookclient.unlock(irecipe);
               recipebookclient.markNew(irecipe);
               RecipeToast.addOrUpdate(this.client.getToastGui(), irecipe);
            }
         }
      }

      recipebookclient.getRecipes().forEach((p_199527_1_) -> {
         p_199527_1_.updateKnownRecipes(recipebookclient);
      });
      if (this.client.currentScreen instanceof IRecipeShownListener) {
         ((IRecipeShownListener)this.client.currentScreen).recipesUpdated();
      }

   }

   public void handleEntityEffect(SPacketEntityEffect p_147260_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147260_1_, this, this.client);
      Entity entity = this.world.getEntityByID(p_147260_1_.getEntityId());
      if (entity instanceof EntityLivingBase) {
         Potion potion = Potion.getPotionById(p_147260_1_.getEffectId());
         if (potion != null) {
            PotionEffect potioneffect = new PotionEffect(potion, p_147260_1_.getDuration(), p_147260_1_.getAmplifier(), p_147260_1_.getIsAmbient(), p_147260_1_.doesShowParticles(), p_147260_1_.func_205527_h());
            potioneffect.setPotionDurationMax(p_147260_1_.isMaxDuration());
            ((EntityLivingBase)entity).addPotionEffect(potioneffect);
         }
      }
   }

   public void handleTags(SPacketTagsList p_199723_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_199723_1_, this, this.client);
      this.networkTagManager = p_199723_1_.getTags();
      if (!this.netManager.isLocalChannel()) {
         BlockTags.setCollection(this.networkTagManager.getBlocks());
         ItemTags.setCollection(this.networkTagManager.getItems());
         FluidTags.setCollection(this.networkTagManager.getFluids());
      }

   }

   public void handleCombatEvent(SPacketCombatEvent p_175098_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_175098_1_, this, this.client);
      if (p_175098_1_.eventType == SPacketCombatEvent.Event.ENTITY_DIED) {
         Entity entity = this.world.getEntityByID(p_175098_1_.playerId);
         if (entity == this.client.player) {
            this.client.displayGuiScreen(new GuiGameOver(p_175098_1_.deathMessage));
         }
      }

   }

   public void handleServerDifficulty(SPacketServerDifficulty p_175101_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_175101_1_, this, this.client);
      this.client.world.getWorldInfo().setDifficulty(p_175101_1_.getDifficulty());
      this.client.world.getWorldInfo().setDifficultyLocked(p_175101_1_.isDifficultyLocked());
   }

   public void handleCamera(SPacketCamera p_175094_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_175094_1_, this, this.client);
      Entity entity = p_175094_1_.getEntity(this.world);
      if (entity != null) {
         this.client.setRenderViewEntity(entity);
      }

   }

   public void handleWorldBorder(SPacketWorldBorder p_175093_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_175093_1_, this, this.client);
      p_175093_1_.apply(this.world.getWorldBorder());
   }

   public void handleTitle(SPacketTitle p_175099_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_175099_1_, this, this.client);
      SPacketTitle.Type spackettitle$type = p_175099_1_.getType();
      String s = null;
      String s1 = null;
      String s2 = p_175099_1_.getMessage() != null ? p_175099_1_.getMessage().getFormattedText() : "";
      switch(spackettitle$type) {
      case TITLE:
         s = s2;
         break;
      case SUBTITLE:
         s1 = s2;
         break;
      case ACTIONBAR:
         this.client.ingameGUI.setOverlayMessage(s2, false);
         return;
      case RESET:
         this.client.ingameGUI.displayTitle("", "", -1, -1, -1);
         this.client.ingameGUI.setDefaultTitlesTimes();
         return;
      }

      this.client.ingameGUI.displayTitle(s, s1, p_175099_1_.getFadeInTime(), p_175099_1_.getDisplayTime(), p_175099_1_.getFadeOutTime());
   }

   public void handlePlayerListHeaderFooter(SPacketPlayerListHeaderFooter p_175096_1_) {
      this.client.ingameGUI.getTabList().setHeader(p_175096_1_.getHeader().getFormattedText().isEmpty() ? null : p_175096_1_.getHeader());
      this.client.ingameGUI.getTabList().setFooter(p_175096_1_.getFooter().getFormattedText().isEmpty() ? null : p_175096_1_.getFooter());
   }

   public void handleRemoveEntityEffect(SPacketRemoveEntityEffect p_147262_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147262_1_, this, this.client);
      Entity entity = p_147262_1_.getEntity(this.world);
      if (entity instanceof EntityLivingBase) {
         ((EntityLivingBase)entity).removeActivePotionEffect(p_147262_1_.getPotion());
      }

   }

   public void handlePlayerListItem(SPacketPlayerListItem p_147256_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147256_1_, this, this.client);

      for(SPacketPlayerListItem.AddPlayerData spacketplayerlistitem$addplayerdata : p_147256_1_.getEntries()) {
         if (p_147256_1_.getAction() == SPacketPlayerListItem.Action.REMOVE_PLAYER) {
            this.playerInfoMap.remove(spacketplayerlistitem$addplayerdata.getProfile().getId());
         } else {
            NetworkPlayerInfo networkplayerinfo = this.playerInfoMap.get(spacketplayerlistitem$addplayerdata.getProfile().getId());
            if (p_147256_1_.getAction() == SPacketPlayerListItem.Action.ADD_PLAYER) {
               networkplayerinfo = new NetworkPlayerInfo(spacketplayerlistitem$addplayerdata);
               this.playerInfoMap.put(networkplayerinfo.getGameProfile().getId(), networkplayerinfo);
            }

            if (networkplayerinfo != null) {
               switch(p_147256_1_.getAction()) {
               case ADD_PLAYER:
                  networkplayerinfo.setGameType(spacketplayerlistitem$addplayerdata.getGameMode());
                  networkplayerinfo.setResponseTime(spacketplayerlistitem$addplayerdata.getPing());
                  networkplayerinfo.setDisplayName(spacketplayerlistitem$addplayerdata.getDisplayName());
                  break;
               case UPDATE_GAME_MODE:
                  networkplayerinfo.setGameType(spacketplayerlistitem$addplayerdata.getGameMode());
                  break;
               case UPDATE_LATENCY:
                  networkplayerinfo.setResponseTime(spacketplayerlistitem$addplayerdata.getPing());
                  break;
               case UPDATE_DISPLAY_NAME:
                  networkplayerinfo.setDisplayName(spacketplayerlistitem$addplayerdata.getDisplayName());
               }
            }
         }
      }

   }

   public void handleKeepAlive(SPacketKeepAlive p_147272_1_) {
      this.sendPacket(new CPacketKeepAlive(p_147272_1_.getId()));
   }

   public void handlePlayerAbilities(SPacketPlayerAbilities p_147270_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147270_1_, this, this.client);
      EntityPlayer entityplayer = this.client.player;
      entityplayer.capabilities.isFlying = p_147270_1_.isFlying();
      entityplayer.capabilities.isCreativeMode = p_147270_1_.isCreativeMode();
      entityplayer.capabilities.disableDamage = p_147270_1_.isInvulnerable();
      entityplayer.capabilities.allowFlying = p_147270_1_.isAllowFlying();
      entityplayer.capabilities.setFlySpeed((double)p_147270_1_.getFlySpeed());
      entityplayer.capabilities.setWalkSpeed(p_147270_1_.getWalkSpeed());
   }

   public void handleSoundEffect(SPacketSoundEffect p_184327_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_184327_1_, this, this.client);
      this.client.world.playSound(this.client.player, p_184327_1_.getX(), p_184327_1_.getY(), p_184327_1_.getZ(), p_184327_1_.getSound(), p_184327_1_.getCategory(), p_184327_1_.getVolume(), p_184327_1_.getPitch());
   }

   public void handleCustomSound(SPacketCustomSound p_184329_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_184329_1_, this, this.client);
      this.client.getSoundHandler().play(new SimpleSound(p_184329_1_.func_197698_a(), p_184329_1_.getCategory(), p_184329_1_.getVolume(), p_184329_1_.getPitch(), false, 0, ISound.AttenuationType.LINEAR, (float)p_184329_1_.getX(), (float)p_184329_1_.getY(), (float)p_184329_1_.getZ()));
   }

   public void handleResourcePack(SPacketResourcePackSend p_175095_1_) {
      String s = p_175095_1_.getURL();
      String s1 = p_175095_1_.getHash();
      if (this.validateResourcePackUrl(s)) {
         if (s.startsWith("level://")) {
            try {
               String s2 = URLDecoder.decode(s.substring("level://".length()), StandardCharsets.UTF_8.toString());
               File file1 = new File(this.client.gameDir, "saves");
               File file2 = new File(file1, s2);
               if (file2.isFile()) {
                  this.netManager.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.ACCEPTED));
                  Futures.addCallback(this.client.getPackFinder().func_195741_a(file2), this.createDownloadCallback());
                  return;
               }
            } catch (UnsupportedEncodingException var7) {
               ;
            }

            this.netManager.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.FAILED_DOWNLOAD));
         } else {
            ServerData serverdata = this.client.getCurrentServerData();
            if (serverdata != null && serverdata.getResourceMode() == ServerData.ServerResourceMode.ENABLED) {
               this.netManager.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.ACCEPTED));
               Futures.addCallback(this.client.getPackFinder().downloadResourcePack(s, s1), this.createDownloadCallback());
            } else if (serverdata != null && serverdata.getResourceMode() != ServerData.ServerResourceMode.PROMPT) {
               this.netManager.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.DECLINED));
            } else {
               this.client.addScheduledTask(() -> {
                  this.client.displayGuiScreen(new GuiYesNo((p_210146_3_, p_210146_4_) -> {
                     this.client = Minecraft.getInstance();
                     ServerData serverdata1 = this.client.getCurrentServerData();
                     if (p_210146_3_) {
                        if (serverdata1 != null) {
                           serverdata1.setResourceMode(ServerData.ServerResourceMode.ENABLED);
                        }

                        this.netManager.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.ACCEPTED));
                        Futures.addCallback(this.client.getPackFinder().downloadResourcePack(s, s1), this.createDownloadCallback());
                     } else {
                        if (serverdata1 != null) {
                           serverdata1.setResourceMode(ServerData.ServerResourceMode.DISABLED);
                        }

                        this.netManager.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.DECLINED));
                     }

                     ServerList.saveSingleServer(serverdata1);
                     this.client.displayGuiScreen((GuiScreen)null);
                  }, I18n.format("multiplayer.texturePrompt.line1"), I18n.format("multiplayer.texturePrompt.line2"), 0));
               });
            }

         }
      }
   }

   private boolean validateResourcePackUrl(String p_189688_1_) {
      try {
         URI uri = new URI(p_189688_1_);
         String s = uri.getScheme();
         boolean flag = "level".equals(s);
         if (!"http".equals(s) && !"https".equals(s) && !flag) {
            throw new URISyntaxException(p_189688_1_, "Wrong protocol");
         } else if (!flag || !p_189688_1_.contains("..") && p_189688_1_.endsWith("/resources.zip")) {
            return true;
         } else {
            throw new URISyntaxException(p_189688_1_, "Invalid levelstorage resourcepack path");
         }
      } catch (URISyntaxException var5) {
         this.netManager.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.FAILED_DOWNLOAD));
         return false;
      }
   }

   private FutureCallback<Object> createDownloadCallback() {
      return new FutureCallback<Object>() {
         public void onSuccess(@Nullable Object p_onSuccess_1_) {
            NetHandlerPlayClient.this.netManager.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
         }

         public void onFailure(Throwable p_onFailure_1_) {
            NetHandlerPlayClient.this.netManager.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.FAILED_DOWNLOAD));
         }
      };
   }

   public void handleUpdateBossInfo(SPacketUpdateBossInfo p_184325_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_184325_1_, this, this.client);
      this.client.ingameGUI.getBossOverlay().read(p_184325_1_);
   }

   public void handleCooldown(SPacketCooldown p_184324_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_184324_1_, this, this.client);
      if (p_184324_1_.getTicks() == 0) {
         this.client.player.getCooldownTracker().removeCooldown(p_184324_1_.getItem());
      } else {
         this.client.player.getCooldownTracker().setCooldown(p_184324_1_.getItem(), p_184324_1_.getTicks());
      }

   }

   public void handleMoveVehicle(SPacketMoveVehicle p_184323_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_184323_1_, this, this.client);
      Entity entity = this.client.player.getLowestRidingEntity();
      if (entity != this.client.player && entity.canPassengerSteer()) {
         entity.setPositionAndRotation(p_184323_1_.getX(), p_184323_1_.getY(), p_184323_1_.getZ(), p_184323_1_.getYaw(), p_184323_1_.getPitch());
         this.netManager.sendPacket(new CPacketVehicleMove(entity));
      }

   }

   public void handleCustomPayload(SPacketCustomPayload p_147240_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147240_1_, this, this.client);
      ResourceLocation resourcelocation = p_147240_1_.getChannelName();
      PacketBuffer packetbuffer = null;

      try {
         packetbuffer = p_147240_1_.getBufferData();
         if (SPacketCustomPayload.TRADER_LIST.equals(resourcelocation)) {
            try {
               int i = packetbuffer.readInt();
               GuiScreen guiscreen = this.client.currentScreen;
               if (guiscreen instanceof GuiMerchant && i == this.client.player.openContainer.windowId) {
                  IMerchant imerchant = ((GuiMerchant)guiscreen).getMerchant();
                  MerchantRecipeList merchantrecipelist = MerchantRecipeList.readFromBuf(packetbuffer);
                  imerchant.setRecipes(merchantrecipelist);
               }
            } catch (IOException ioexception) {
               LOGGER.error("Couldn't load trade info", (Throwable)ioexception);
            }
         } else if (SPacketCustomPayload.BRAND.equals(resourcelocation)) {
            this.client.player.setServerBrand(packetbuffer.readString(32767));
         } else if (SPacketCustomPayload.BOOK_OPEN.equals(resourcelocation)) {
            EnumHand enumhand = packetbuffer.readEnumValue(EnumHand.class);
            ItemStack itemstack = enumhand == EnumHand.OFF_HAND ? this.client.player.getHeldItemOffhand() : this.client.player.getHeldItemMainhand();
            if (itemstack.getItem() == Items.WRITTEN_BOOK) {
               this.client.displayGuiScreen(new GuiScreenBook(this.client.player, itemstack, false, enumhand));
            }
         } else if (SPacketCustomPayload.DEBUG_PATH.equals(resourcelocation)) {
            int l = packetbuffer.readInt();
            float f = packetbuffer.readFloat();
            Path path = Path.read(packetbuffer);
            this.client.debugRenderer.pathfinding.addPath(l, path, f);
         } else if (SPacketCustomPayload.DEBUG_NEIGHBORS_UPDATE.equals(resourcelocation)) {
            long i1 = packetbuffer.readVarLong();
            BlockPos blockpos1 = packetbuffer.readBlockPos();
            ((DebugRendererNeighborsUpdate)this.client.debugRenderer.neighborsUpdate).addUpdate(i1, blockpos1);
         } else if (SPacketCustomPayload.DEBUG_CAVES.equals(resourcelocation)) {
            BlockPos blockpos = packetbuffer.readBlockPos();
            int k1 = packetbuffer.readInt();
            List<BlockPos> list = Lists.newArrayList();
            List<Float> list1 = Lists.newArrayList();

            for(int j = 0; j < k1; ++j) {
               list.add(packetbuffer.readBlockPos());
               list1.add(packetbuffer.readFloat());
            }

            this.client.debugRenderer.cave.addCave(blockpos, list, list1);
         } else if (SPacketCustomPayload.DEBUG_STRUCTURES.equals(resourcelocation)) {
            int j1 = packetbuffer.readInt();
            MutableBoundingBox mutableboundingbox = new MutableBoundingBox(packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt());
            int l1 = packetbuffer.readInt();
            List<MutableBoundingBox> list2 = Lists.newArrayList();
            List<Boolean> list3 = Lists.newArrayList();

            for(int k = 0; k < l1; ++k) {
               list2.add(new MutableBoundingBox(packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt()));
               list3.add(packetbuffer.readBoolean());
            }

            this.client.debugRenderer.structure.addStructure(mutableboundingbox, list2, list3, j1);
         } else if (SPacketCustomPayload.DEBUG_WORLDGEN_ATTEMPT.equals(resourcelocation)) {
            ((DebugRendererWorldGenAttempts)this.client.debugRenderer.worldGenAttempts).addAttempt(packetbuffer.readBlockPos(), packetbuffer.readFloat(), packetbuffer.readFloat(), packetbuffer.readFloat(), packetbuffer.readFloat(), packetbuffer.readFloat());
            LOGGER.warn("Unknown custom packed identifier: {}", (Object)resourcelocation);
         } else {
            LOGGER.warn("Unknown custom packed identifier: {}", (Object)resourcelocation);
         }
      } finally {
         if (packetbuffer != null) {
            packetbuffer.release();
         }

      }

   }

   public void handleScoreboardObjective(SPacketScoreboardObjective p_147291_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147291_1_, this, this.client);
      Scoreboard scoreboard = this.world.getScoreboard();
      String s = p_147291_1_.getObjectiveName();
      if (p_147291_1_.getAction() == 0) {
         scoreboard.func_199868_a(s, ScoreCriteria.field_96641_b, p_147291_1_.getObjectiveValue(), p_147291_1_.func_199856_d());
      } else if (scoreboard.func_197900_b(s)) {
         ScoreObjective scoreobjective = scoreboard.getObjective(s);
         if (p_147291_1_.getAction() == 1) {
            scoreboard.removeObjective(scoreobjective);
         } else if (p_147291_1_.getAction() == 2) {
            scoreobjective.func_199866_a(p_147291_1_.func_199856_d());
            scoreobjective.func_199864_a(p_147291_1_.getObjectiveValue());
         }
      }

   }

   public void handleUpdateScore(SPacketUpdateScore p_147250_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147250_1_, this, this.client);
      Scoreboard scoreboard = this.world.getScoreboard();
      String s = p_147250_1_.getObjectiveName();
      switch(p_147250_1_.func_197701_d()) {
      case CHANGE:
         ScoreObjective scoreobjective = scoreboard.func_197899_c(s);
         Score score = scoreboard.getOrCreateScore(p_147250_1_.getPlayerName(), scoreobjective);
         score.setScorePoints(p_147250_1_.getScoreValue());
         break;
      case REMOVE:
         scoreboard.removeObjectiveFromEntity(p_147250_1_.getPlayerName(), scoreboard.getObjective(s));
      }

   }

   public void handleDisplayObjective(SPacketDisplayObjective p_147254_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147254_1_, this, this.client);
      Scoreboard scoreboard = this.world.getScoreboard();
      String s = p_147254_1_.getName();
      ScoreObjective scoreobjective = s == null ? null : scoreboard.func_197899_c(s);
      scoreboard.setObjectiveInDisplaySlot(p_147254_1_.getPosition(), scoreobjective);
   }

   public void handleTeams(SPacketTeams p_147247_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147247_1_, this, this.client);
      Scoreboard scoreboard = this.world.getScoreboard();
      ScorePlayerTeam scoreplayerteam;
      if (p_147247_1_.getAction() == 0) {
         scoreplayerteam = scoreboard.createTeam(p_147247_1_.getName());
      } else {
         scoreplayerteam = scoreboard.getTeam(p_147247_1_.getName());
      }

      if (p_147247_1_.getAction() == 0 || p_147247_1_.getAction() == 2) {
         scoreplayerteam.setDisplayName(p_147247_1_.getDisplayName());
         scoreplayerteam.setColor(p_147247_1_.func_200537_f());
         scoreplayerteam.setFriendlyFlags(p_147247_1_.getFriendlyFlags());
         Team.EnumVisible team$enumvisible = Team.EnumVisible.getByName(p_147247_1_.getNameTagVisibility());
         if (team$enumvisible != null) {
            scoreplayerteam.setNameTagVisibility(team$enumvisible);
         }

         Team.CollisionRule team$collisionrule = Team.CollisionRule.getByName(p_147247_1_.getCollisionRule());
         if (team$collisionrule != null) {
            scoreplayerteam.setCollisionRule(team$collisionrule);
         }

         scoreplayerteam.setPrefix(p_147247_1_.getPrefix());
         scoreplayerteam.setSuffix(p_147247_1_.getSuffix());
      }

      if (p_147247_1_.getAction() == 0 || p_147247_1_.getAction() == 3) {
         for(String s : p_147247_1_.getPlayers()) {
            scoreboard.func_197901_a(s, scoreplayerteam);
         }
      }

      if (p_147247_1_.getAction() == 4) {
         for(String s1 : p_147247_1_.getPlayers()) {
            scoreboard.removePlayerFromTeam(s1, scoreplayerteam);
         }
      }

      if (p_147247_1_.getAction() == 1) {
         scoreboard.removeTeam(scoreplayerteam);
      }

   }

   public void handleParticles(SPacketParticles p_147289_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147289_1_, this, this.client);
      if (p_147289_1_.getParticleCount() == 0) {
         double d0 = (double)(p_147289_1_.getParticleSpeed() * p_147289_1_.getXOffset());
         double d2 = (double)(p_147289_1_.getParticleSpeed() * p_147289_1_.getYOffset());
         double d4 = (double)(p_147289_1_.getParticleSpeed() * p_147289_1_.getZOffset());

         try {
            this.world.addParticle(p_147289_1_.func_197699_j(), p_147289_1_.isLongDistance(), p_147289_1_.getXCoordinate(), p_147289_1_.getYCoordinate(), p_147289_1_.getZCoordinate(), d0, d2, d4);
         } catch (Throwable var17) {
            LOGGER.warn("Could not spawn particle effect {}", (Object)p_147289_1_.func_197699_j());
         }
      } else {
         for(int i = 0; i < p_147289_1_.getParticleCount(); ++i) {
            double d1 = this.avRandomizer.nextGaussian() * (double)p_147289_1_.getXOffset();
            double d3 = this.avRandomizer.nextGaussian() * (double)p_147289_1_.getYOffset();
            double d5 = this.avRandomizer.nextGaussian() * (double)p_147289_1_.getZOffset();
            double d6 = this.avRandomizer.nextGaussian() * (double)p_147289_1_.getParticleSpeed();
            double d7 = this.avRandomizer.nextGaussian() * (double)p_147289_1_.getParticleSpeed();
            double d8 = this.avRandomizer.nextGaussian() * (double)p_147289_1_.getParticleSpeed();

            try {
               this.world.addParticle(p_147289_1_.func_197699_j(), p_147289_1_.isLongDistance(), p_147289_1_.getXCoordinate() + d1, p_147289_1_.getYCoordinate() + d3, p_147289_1_.getZCoordinate() + d5, d6, d7, d8);
            } catch (Throwable var16) {
               LOGGER.warn("Could not spawn particle effect {}", (Object)p_147289_1_.func_197699_j());
               return;
            }
         }
      }

   }

   public void handleEntityProperties(SPacketEntityProperties p_147290_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147290_1_, this, this.client);
      Entity entity = this.world.getEntityByID(p_147290_1_.getEntityId());
      if (entity != null) {
         if (!(entity instanceof EntityLivingBase)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entity + ")");
         } else {
            AbstractAttributeMap abstractattributemap = ((EntityLivingBase)entity).getAttributeMap();

            for(SPacketEntityProperties.Snapshot spacketentityproperties$snapshot : p_147290_1_.getSnapshots()) {
               IAttributeInstance iattributeinstance = abstractattributemap.getAttributeInstanceByName(spacketentityproperties$snapshot.getName());
               if (iattributeinstance == null) {
                  iattributeinstance = abstractattributemap.registerAttribute(new RangedAttribute((IAttribute)null, spacketentityproperties$snapshot.getName(), 0.0D, Double.MIN_NORMAL, Double.MAX_VALUE));
               }

               iattributeinstance.setBaseValue(spacketentityproperties$snapshot.getBaseValue());
               iattributeinstance.removeAllModifiers();

               for(AttributeModifier attributemodifier : spacketentityproperties$snapshot.getModifiers()) {
                  iattributeinstance.applyModifier(attributemodifier);
               }
            }

         }
      }
   }

   public void handlePlaceGhostRecipe(SPacketPlaceGhostRecipe p_194307_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_194307_1_, this, this.client);
      Container container = this.client.player.openContainer;
      if (container.windowId == p_194307_1_.func_194313_b() && container.getCanCraft(this.client.player)) {
         IRecipe irecipe = this.recipeManager.getRecipe(p_194307_1_.func_199615_a());
         if (irecipe != null) {
            if (this.client.currentScreen instanceof IRecipeShownListener) {
               GuiRecipeBook guirecipebook = ((IRecipeShownListener)this.client.currentScreen).func_194310_f();
               guirecipebook.setupGhostRecipe(irecipe, container.inventorySlots);
            } else if (this.client.currentScreen instanceof GuiFurnace) {
               ((GuiFurnace)this.client.currentScreen).recipeBook.setupGhostRecipe(irecipe, container.inventorySlots);
            }
         }

      }
   }

   public NetworkManager getNetworkManager() {
      return this.netManager;
   }

   public Collection<NetworkPlayerInfo> getPlayerInfoMap() {
      return this.playerInfoMap.values();
   }

   public NetworkPlayerInfo getPlayerInfo(UUID p_175102_1_) {
      return this.playerInfoMap.get(p_175102_1_);
   }

   @Nullable
   public NetworkPlayerInfo getPlayerInfo(String p_175104_1_) {
      for(NetworkPlayerInfo networkplayerinfo : this.playerInfoMap.values()) {
         if (networkplayerinfo.getGameProfile().getName().equals(p_175104_1_)) {
            return networkplayerinfo;
         }
      }

      return null;
   }

   public GameProfile getGameProfile() {
      return this.profile;
   }

   public ClientAdvancementManager getAdvancementManager() {
      return this.advancementManager;
   }

   public CommandDispatcher<ISuggestionProvider> func_195515_i() {
      return this.commandDispatcher;
   }

   public WorldClient getWorld() {
      return this.world;
   }

   public NetworkTagManager getTags() {
      return this.networkTagManager;
   }

   public NBTQueryManager func_211523_k() {
      return this.nbtQueryManager;
   }
}
