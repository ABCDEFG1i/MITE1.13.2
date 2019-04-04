package net.minecraft.network;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.util.concurrent.Futures;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ServerRecipePlacer;
import net.minecraft.item.crafting.ServerRecipePlacerFurnace;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketEditBook;
import net.minecraft.network.play.client.CPacketEnchantItem;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketNBTQueryEntity;
import net.minecraft.network.play.client.CPacketNBTQueryTileEntity;
import net.minecraft.network.play.client.CPacketPickItem;
import net.minecraft.network.play.client.CPacketPlaceRecipe;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketRecipeInfo;
import net.minecraft.network.play.client.CPacketRenameItem;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.client.CPacketSeenAdvancements;
import net.minecraft.network.play.client.CPacketSelectTrade;
import net.minecraft.network.play.client.CPacketSpectate;
import net.minecraft.network.play.client.CPacketSteerBoat;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.client.CPacketUpdateBeacon;
import net.minecraft.network.play.client.CPacketUpdateCommandBlock;
import net.minecraft.network.play.client.CPacketUpdateCommandMinecart;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.network.play.client.CPacketUpdateStructureBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketNBTQueryResponse;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerPlayServer implements INetHandlerPlayServer, ITickable {
   private static final Logger LOGGER = LogManager.getLogger();
   public final NetworkManager netManager;
   private final MinecraftServer server;
   public EntityPlayerMP player;
   private int networkTickCount;
   private long field_194402_f;
   private boolean field_194403_g;
   private long field_194404_h;
   private int chatSpamThresholdCount;
   private int itemDropThreshold;
   private final IntHashMap<Short> pendingTransactions = new IntHashMap<>();
   private double firstGoodX;
   private double firstGoodY;
   private double firstGoodZ;
   private double lastGoodX;
   private double lastGoodY;
   private double lastGoodZ;
   private Entity lowestRiddenEnt;
   private double lowestRiddenX;
   private double lowestRiddenY;
   private double lowestRiddenZ;
   private double lowestRiddenX1;
   private double lowestRiddenY1;
   private double lowestRiddenZ1;
   private Vec3d targetPos;
   private int teleportId;
   private int lastPositionUpdate;
   private boolean floating;
   private int floatingTickCount;
   private boolean vehicleFloating;
   private int vehicleFloatingTickCount;
   private int movePacketCounter;
   private int lastMovePacketCounter;

   public NetHandlerPlayServer(MinecraftServer p_i1530_1_, NetworkManager p_i1530_2_, EntityPlayerMP p_i1530_3_) {
      this.server = p_i1530_1_;
      this.netManager = p_i1530_2_;
      p_i1530_2_.setNetHandler(this);
      this.player = p_i1530_3_;
      p_i1530_3_.connection = this;
   }

   public void tick() {
      this.captureCurrentPosition();
      this.player.playerTick();
      this.player.setPositionAndRotation(this.firstGoodX, this.firstGoodY, this.firstGoodZ, this.player.rotationYaw, this.player.rotationPitch);
      ++this.networkTickCount;
      this.lastMovePacketCounter = this.movePacketCounter;
      if (this.floating) {
         if (++this.floatingTickCount > 80) {
            LOGGER.warn("{} was kicked for floating too long!", this.player.getName().getString());
            this.disconnect(new TextComponentTranslation("multiplayer.disconnect.flying"));
            return;
         }
      } else {
         this.floating = false;
         this.floatingTickCount = 0;
      }

      this.lowestRiddenEnt = this.player.getLowestRidingEntity();
      if (this.lowestRiddenEnt != this.player && this.lowestRiddenEnt.getControllingPassenger() == this.player) {
         this.lowestRiddenX = this.lowestRiddenEnt.posX;
         this.lowestRiddenY = this.lowestRiddenEnt.posY;
         this.lowestRiddenZ = this.lowestRiddenEnt.posZ;
         this.lowestRiddenX1 = this.lowestRiddenEnt.posX;
         this.lowestRiddenY1 = this.lowestRiddenEnt.posY;
         this.lowestRiddenZ1 = this.lowestRiddenEnt.posZ;
         if (this.vehicleFloating && this.player.getLowestRidingEntity().getControllingPassenger() == this.player) {
            if (++this.vehicleFloatingTickCount > 80) {
               LOGGER.warn("{} was kicked for floating a vehicle too long!", this.player.getName().getString());
               this.disconnect(new TextComponentTranslation("multiplayer.disconnect.flying"));
               return;
            }
         } else {
            this.vehicleFloating = false;
            this.vehicleFloatingTickCount = 0;
         }
      } else {
         this.lowestRiddenEnt = null;
         this.vehicleFloating = false;
         this.vehicleFloatingTickCount = 0;
      }

      this.server.profiler.startSection("keepAlive");
      long i = Util.milliTime();
      if (i - this.field_194402_f >= 15000L) {
         if (this.field_194403_g) {
            this.disconnect(new TextComponentTranslation("disconnect.timeout"));
         } else {
            this.field_194403_g = true;
            this.field_194402_f = i;
            this.field_194404_h = i;
            this.sendPacket(new SPacketKeepAlive(this.field_194404_h));
         }
      }

      this.server.profiler.endSection();
      if (this.chatSpamThresholdCount > 0) {
         --this.chatSpamThresholdCount;
      }

      if (this.itemDropThreshold > 0) {
         --this.itemDropThreshold;
      }

      if (this.player.getLastActiveTime() > 0L && this.server.getMaxPlayerIdleMinutes() > 0 && Util.milliTime() - this.player.getLastActiveTime() > (long)(this.server.getMaxPlayerIdleMinutes() * 1000 * 60)) {
         this.disconnect(new TextComponentTranslation("multiplayer.disconnect.idling"));
      }

   }

   public void captureCurrentPosition() {
      this.firstGoodX = this.player.posX;
      this.firstGoodY = this.player.posY;
      this.firstGoodZ = this.player.posZ;
      this.lastGoodX = this.player.posX;
      this.lastGoodY = this.player.posY;
      this.lastGoodZ = this.player.posZ;
   }

   public NetworkManager getNetworkManager() {
      return this.netManager;
   }

   public void disconnect(ITextComponent p_194028_1_) {
      this.netManager.sendPacket(new SPacketDisconnect(p_194028_1_), (p_210161_2_) -> {
         this.netManager.closeChannel(p_194028_1_);
      });
      this.netManager.disableAutoRead();
      Futures.getUnchecked(this.server.addScheduledTask(this.netManager::handleDisconnection));
   }

   public void processInput(CPacketInput p_147358_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147358_1_, this, this.player.getServerWorld());
      this.player.setEntityActionState(p_147358_1_.getStrafeSpeed(), p_147358_1_.getForwardSpeed(), p_147358_1_.isJumping(), p_147358_1_.isSneaking());
   }

   private static boolean isMovePlayerPacketInvalid(CPacketPlayer p_183006_0_) {
      if (Doubles.isFinite(p_183006_0_.getX(0.0D)) && Doubles.isFinite(p_183006_0_.getY(0.0D)) && Doubles.isFinite(p_183006_0_.getZ(0.0D)) && Floats.isFinite(p_183006_0_.getPitch(0.0F)) && Floats.isFinite(p_183006_0_.getYaw(0.0F))) {
         return Math.abs(p_183006_0_.getX(0.0D)) > 3.0E7D || Math.abs(p_183006_0_.getY(0.0D)) > 3.0E7D || Math.abs(p_183006_0_.getZ(0.0D)) > 3.0E7D;
      } else {
         return true;
      }
   }

   private static boolean isMoveVehiclePacketInvalid(CPacketVehicleMove p_184341_0_) {
      return !Doubles.isFinite(p_184341_0_.getX()) || !Doubles.isFinite(p_184341_0_.getY()) || !Doubles.isFinite(p_184341_0_.getZ()) || !Floats.isFinite(p_184341_0_.getPitch()) || !Floats.isFinite(p_184341_0_.getYaw());
   }

   public void processVehicleMove(CPacketVehicleMove p_184338_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_184338_1_, this, this.player.getServerWorld());
      if (isMoveVehiclePacketInvalid(p_184338_1_)) {
         this.disconnect(new TextComponentTranslation("multiplayer.disconnect.invalid_vehicle_movement"));
      } else {
         Entity entity = this.player.getLowestRidingEntity();
         if (entity != this.player && entity.getControllingPassenger() == this.player && entity == this.lowestRiddenEnt) {
            WorldServer worldserver = this.player.getServerWorld();
            double d0 = entity.posX;
            double d1 = entity.posY;
            double d2 = entity.posZ;
            double d3 = p_184338_1_.getX();
            double d4 = p_184338_1_.getY();
            double d5 = p_184338_1_.getZ();
            float f = p_184338_1_.getYaw();
            float f1 = p_184338_1_.getPitch();
            double d6 = d3 - this.lowestRiddenX;
            double d7 = d4 - this.lowestRiddenY;
            double d8 = d5 - this.lowestRiddenZ;
            double d9 = entity.motionX * entity.motionX + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ;
            double d10 = d6 * d6 + d7 * d7 + d8 * d8;
            if (d10 - d9 > 100.0D && (!this.server.isSinglePlayer() || !this.server.getServerOwner().equals(entity.getName().getString()))) {
               LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", entity.getName().getString(), this.player.getName().getString(), d6, d7, d8);
               this.netManager.sendPacket(new SPacketMoveVehicle(entity));
               return;
            }

            boolean flag = worldserver.isCollisionBoxesEmpty(entity, entity.getEntityBoundingBox().shrink(0.0625D));
            d6 = d3 - this.lowestRiddenX1;
            d7 = d4 - this.lowestRiddenY1 - 1.0E-6D;
            d8 = d5 - this.lowestRiddenZ1;
            entity.move(MoverType.PLAYER, d6, d7, d8);
            double d11 = d7;
            d6 = d3 - entity.posX;
            d7 = d4 - entity.posY;
            if (d7 > -0.5D || d7 < 0.5D) {
               d7 = 0.0D;
            }

            d8 = d5 - entity.posZ;
            d10 = d6 * d6 + d7 * d7 + d8 * d8;
            boolean flag1 = false;
            if (d10 > 0.0625D) {
               flag1 = true;
               LOGGER.warn("{} moved wrongly!", entity.getName().getString());
            }

            entity.setPositionAndRotation(d3, d4, d5, f, f1);
            boolean flag2 = worldserver.isCollisionBoxesEmpty(entity, entity.getEntityBoundingBox().shrink(0.0625D));
            if (flag && (flag1 || !flag2)) {
               entity.setPositionAndRotation(d0, d1, d2, f, f1);
               this.netManager.sendPacket(new SPacketMoveVehicle(entity));
               return;
            }

            this.server.getPlayerList().serverUpdateMovingPlayer(this.player);
            this.player.addMovementStat(this.player.posX - d0, this.player.posY - d1, this.player.posZ - d2);
            this.vehicleFloating = d11 >= -0.03125D && !this.server.isFlightAllowed() && !worldserver.checkBlockCollision(entity.getEntityBoundingBox().grow(0.0625D).expand(0.0D, -0.55D, 0.0D));
            this.lowestRiddenX1 = entity.posX;
            this.lowestRiddenY1 = entity.posY;
            this.lowestRiddenZ1 = entity.posZ;
         }

      }
   }

   public void processConfirmTeleport(CPacketConfirmTeleport p_184339_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_184339_1_, this, this.player.getServerWorld());
      if (p_184339_1_.getTeleportId() == this.teleportId) {
         this.player.setPositionAndRotation(this.targetPos.x, this.targetPos.y, this.targetPos.z, this.player.rotationYaw, this.player.rotationPitch);
         this.lastGoodX = this.targetPos.x;
         this.lastGoodY = this.targetPos.y;
         this.lastGoodZ = this.targetPos.z;
         if (this.player.isInvulnerableDimensionChange()) {
            this.player.clearInvulnerableDimensionChange();
         }

         this.targetPos = null;
      }

   }

   public void handleRecipeBookUpdate(CPacketRecipeInfo p_191984_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_191984_1_, this, this.player.getServerWorld());
      if (p_191984_1_.getPurpose() == CPacketRecipeInfo.Purpose.SHOWN) {
         IRecipe irecipe = this.server.getRecipeManager().getRecipe(p_191984_1_.func_199619_b());
         if (irecipe != null) {
            this.player.getRecipeBook().markSeen(irecipe);
         }
      } else if (p_191984_1_.getPurpose() == CPacketRecipeInfo.Purpose.SETTINGS) {
         this.player.getRecipeBook().setGuiOpen(p_191984_1_.isGuiOpen());
         this.player.getRecipeBook().setFilteringCraftable(p_191984_1_.isFilteringCraftable());
         this.player.getRecipeBook().setFurnaceGuiOpen(p_191984_1_.func_202496_e());
         this.player.getRecipeBook().setFurnaceFilteringCraftable(p_191984_1_.func_202497_f());
      }

   }

   public void handleSeenAdvancements(CPacketSeenAdvancements p_194027_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_194027_1_, this, this.player.getServerWorld());
      if (p_194027_1_.getAction() == CPacketSeenAdvancements.Action.OPENED_TAB) {
         ResourceLocation resourcelocation = p_194027_1_.getTab();
         Advancement advancement = this.server.getAdvancementManager().getAdvancement(resourcelocation);
         if (advancement != null) {
            this.player.getAdvancements().setSelectedTab(advancement);
         }
      }

   }

   public void processTabComplete(CPacketTabComplete p_195518_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_195518_1_, this, this.player.getServerWorld());
      StringReader stringreader = new StringReader(p_195518_1_.func_197707_b());
      if (stringreader.canRead() && stringreader.peek() == '/') {
         stringreader.skip();
      }

      ParseResults<CommandSource> parseresults = this.server.getCommandManager().getDispatcher().parse(stringreader, this.player.getCommandSource());
      this.server.getCommandManager().getDispatcher().getCompletionSuggestions(parseresults).thenAccept((p_195519_2_) -> {
         this.netManager.sendPacket(new SPacketTabComplete(p_195518_1_.func_197709_a(), p_195519_2_));
      });
   }

   public void processUpdateCommandBlock(CPacketUpdateCommandBlock p_210153_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_210153_1_, this, this.player.getServerWorld());
      if (!this.server.isCommandBlockEnabled()) {
         this.player.sendMessage(new TextComponentTranslation("advMode.notEnabled"));
      } else if (!this.player.canUseCommandBlock()) {
         this.player.sendMessage(new TextComponentTranslation("advMode.notAllowed"));
      } else {
         CommandBlockBaseLogic commandblockbaselogic = null;
         TileEntityCommandBlock tileentitycommandblock = null;
         BlockPos blockpos = p_210153_1_.getPos();
         TileEntity tileentity = this.player.world.getTileEntity(blockpos);
         if (tileentity instanceof TileEntityCommandBlock) {
            tileentitycommandblock = (TileEntityCommandBlock)tileentity;
            commandblockbaselogic = tileentitycommandblock.getCommandBlockLogic();
         }

         String s = p_210153_1_.getCommand();
         boolean flag = p_210153_1_.func_210363_c();
         if (commandblockbaselogic != null) {
            EnumFacing enumfacing = this.player.world.getBlockState(blockpos).get(BlockCommandBlock.FACING);
            switch(p_210153_1_.getMode()) {
            case SEQUENCE:
               IBlockState iblockstate1 = Blocks.CHAIN_COMMAND_BLOCK.getDefaultState();
               this.player.world.setBlockState(blockpos, iblockstate1.with(BlockCommandBlock.FACING, enumfacing).with(BlockCommandBlock.CONDITIONAL, Boolean.valueOf(p_210153_1_.isConditional())), 2);
               break;
            case AUTO:
               IBlockState iblockstate = Blocks.REPEATING_COMMAND_BLOCK.getDefaultState();
               this.player.world.setBlockState(blockpos, iblockstate.with(BlockCommandBlock.FACING, enumfacing).with(BlockCommandBlock.CONDITIONAL, Boolean.valueOf(p_210153_1_.isConditional())), 2);
               break;
            case REDSTONE:
            default:
               IBlockState iblockstate2 = Blocks.COMMAND_BLOCK.getDefaultState();
               this.player.world.setBlockState(blockpos, iblockstate2.with(BlockCommandBlock.FACING, enumfacing).with(BlockCommandBlock.CONDITIONAL, Boolean.valueOf(p_210153_1_.isConditional())), 2);
            }

            tileentity.validate();
            this.player.world.setTileEntity(blockpos, tileentity);
            commandblockbaselogic.setCommand(s);
            commandblockbaselogic.setTrackOutput(flag);
            if (!flag) {
               commandblockbaselogic.setLastOutput(null);
            }

            tileentitycommandblock.setAuto(p_210153_1_.func_210362_e());
            commandblockbaselogic.updateCommand();
            if (!StringUtils.isNullOrEmpty(s)) {
               this.player.sendMessage(new TextComponentTranslation("advMode.setCommand.success", s));
            }
         }

      }
   }

   public void processUpdateCommandMinecart(CPacketUpdateCommandMinecart p_210158_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_210158_1_, this, this.player.getServerWorld());
      if (!this.server.isCommandBlockEnabled()) {
         this.player.sendMessage(new TextComponentTranslation("advMode.notEnabled"));
      } else if (!this.player.canUseCommandBlock()) {
         this.player.sendMessage(new TextComponentTranslation("advMode.notAllowed"));
      } else {
         CommandBlockBaseLogic commandblockbaselogic = p_210158_1_.getCommandBlock(this.player.world);
         if (commandblockbaselogic != null) {
            commandblockbaselogic.setCommand(p_210158_1_.getCommand());
            commandblockbaselogic.setTrackOutput(p_210158_1_.func_210373_b());
            if (!p_210158_1_.func_210373_b()) {
               commandblockbaselogic.setLastOutput(null);
            }

            commandblockbaselogic.updateCommand();
            this.player.sendMessage(new TextComponentTranslation("advMode.setCommand.success", p_210158_1_.getCommand()));
         }

      }
   }

   public void processPickItem(CPacketPickItem p_210152_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_210152_1_, this, this.player.getServerWorld());
      this.player.inventory.pickItem(p_210152_1_.func_210349_a());
      this.player.connection.sendPacket(new SPacketSetSlot(-2, this.player.inventory.currentItem, this.player.inventory.getStackInSlot(this.player.inventory.currentItem)));
      this.player.connection.sendPacket(new SPacketSetSlot(-2, p_210152_1_.func_210349_a(), this.player.inventory.getStackInSlot(p_210152_1_.func_210349_a())));
      this.player.connection.sendPacket(new SPacketHeldItemChange(this.player.inventory.currentItem));
   }

   public void processRenameItem(CPacketRenameItem p_210155_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_210155_1_, this, this.player.getServerWorld());
      if (this.player.openContainer instanceof ContainerRepair) {
         ContainerRepair containerrepair = (ContainerRepair)this.player.openContainer;
         String s = SharedConstants.filterAllowedCharacters(p_210155_1_.getName());
         if (s.length() <= 35) {
            containerrepair.updateItemName(s);
         }
      }

   }

   public void processUpdateBeacon(CPacketUpdateBeacon p_210154_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_210154_1_, this, this.player.getServerWorld());
      if (this.player.openContainer instanceof ContainerBeacon) {
         ContainerBeacon containerbeacon = (ContainerBeacon)this.player.openContainer;
         Slot slot = containerbeacon.getSlot(0);
         if (slot.getHasStack()) {
            slot.decrStackSize(1);
            IInventory iinventory = containerbeacon.getTileEntity();
            iinventory.setField(1, p_210154_1_.func_210355_a());
            iinventory.setField(2, p_210154_1_.func_210356_b());
            iinventory.markDirty();
         }
      }

   }

   public void processUpdateStructureBlock(CPacketUpdateStructureBlock p_210157_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_210157_1_, this, this.player.getServerWorld());
      if (this.player.canUseCommandBlock()) {
         BlockPos blockpos = p_210157_1_.getPos();
         IBlockState iblockstate = this.player.world.getBlockState(blockpos);
         TileEntity tileentity = this.player.world.getTileEntity(blockpos);
         if (tileentity instanceof TileEntityStructure) {
            TileEntityStructure tileentitystructure = (TileEntityStructure)tileentity;
            tileentitystructure.setMode(p_210157_1_.getMode());
            tileentitystructure.setName(p_210157_1_.getName());
            tileentitystructure.setPosition(p_210157_1_.func_210383_e());
            tileentitystructure.setSize(p_210157_1_.getSize());
            tileentitystructure.setMirror(p_210157_1_.getMirror());
            tileentitystructure.setRotation(p_210157_1_.getRotation());
            tileentitystructure.setMetadata(p_210157_1_.func_210388_i());
            tileentitystructure.setIgnoresEntities(p_210157_1_.func_210389_j());
            tileentitystructure.setShowAir(p_210157_1_.func_210390_k());
            tileentitystructure.setShowBoundingBox(p_210157_1_.func_210387_l());
            tileentitystructure.setIntegrity(p_210157_1_.getIntegrity());
            tileentitystructure.setSeed(p_210157_1_.getSeed());
            if (tileentitystructure.hasName()) {
               String s = tileentitystructure.getName();
               if (p_210157_1_.func_210384_b() == TileEntityStructure.UpdateCommand.SAVE_AREA) {
                  if (tileentitystructure.save()) {
                     this.player.sendStatusMessage(new TextComponentTranslation("structure_block.save_success", s), false);
                  } else {
                     this.player.sendStatusMessage(new TextComponentTranslation("structure_block.save_failure", s), false);
                  }
               } else if (p_210157_1_.func_210384_b() == TileEntityStructure.UpdateCommand.LOAD_AREA) {
                  if (!tileentitystructure.isStructureLoadable()) {
                     this.player.sendStatusMessage(new TextComponentTranslation("structure_block.load_not_found", s), false);
                  } else if (tileentitystructure.load()) {
                     this.player.sendStatusMessage(new TextComponentTranslation("structure_block.load_success", s), false);
                  } else {
                     this.player.sendStatusMessage(new TextComponentTranslation("structure_block.load_prepare", s), false);
                  }
               } else if (p_210157_1_.func_210384_b() == TileEntityStructure.UpdateCommand.SCAN_AREA) {
                  if (tileentitystructure.detectSize()) {
                     this.player.sendStatusMessage(new TextComponentTranslation("structure_block.size_success", s), false);
                  } else {
                     this.player.sendStatusMessage(new TextComponentTranslation("structure_block.size_failure"), false);
                  }
               }
            } else {
               this.player.sendStatusMessage(new TextComponentTranslation("structure_block.invalid_structure_name", p_210157_1_.getName()), false);
            }

            tileentitystructure.markDirty();
            this.player.world.notifyBlockUpdate(blockpos, iblockstate, iblockstate, 3);
         }

      }
   }

   public void processSelectTrade(CPacketSelectTrade p_210159_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_210159_1_, this, this.player.getServerWorld());
      int i = p_210159_1_.func_210353_a();
      Container container = this.player.openContainer;
      if (container instanceof ContainerMerchant) {
         ((ContainerMerchant)container).setCurrentRecipeIndex(i);
      }

   }

   public void processEditBook(CPacketEditBook p_210156_1_) {
      ItemStack itemstack = p_210156_1_.func_210346_a();
      if (!itemstack.isEmpty()) {
         if (ItemWritableBook.isNBTValid(itemstack.getTag())) {
            ItemStack itemstack1 = this.player.getHeldItem(p_210156_1_.func_212644_d());
            if (!itemstack1.isEmpty()) {
               if (itemstack.getItem() == Items.WRITABLE_BOOK && itemstack1.getItem() == Items.WRITABLE_BOOK) {
                  if (p_210156_1_.func_210345_b()) {
                     ItemStack itemstack2 = new ItemStack(Items.WRITTEN_BOOK);
                     itemstack2.setTagInfo("author", new NBTTagString(this.player.getName().getString()));
                     itemstack2.setTagInfo("title", new NBTTagString(itemstack.getTag().getString("title")));
                     NBTTagList nbttaglist = itemstack.getTag().getTagList("pages", 8);

                     for(int i = 0; i < nbttaglist.size(); ++i) {
                        String s = nbttaglist.getStringTagAt(i);
                        ITextComponent itextcomponent = new TextComponentString(s);
                        s = ITextComponent.Serializer.toJson(itextcomponent);
                        nbttaglist.set(i, new NBTTagString(s));
                     }

                     itemstack2.setTagInfo("pages", nbttaglist);
                     EntityEquipmentSlot entityequipmentslot = p_210156_1_.func_212644_d() == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND;
                     this.player.setItemStackToSlot(entityequipmentslot, itemstack2);
                  } else {
                     itemstack1.setTagInfo("pages", itemstack.getTag().getTagList("pages", 8));
                  }
               }

            }
         }
      }
   }

   public void processNBTQueryEntity(CPacketNBTQueryEntity p_211526_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_211526_1_, this, this.player.getServerWorld());
      if (this.player.hasPermissionLevel(2)) {
         Entity entity = this.player.getServerWorld().getEntityByID(p_211526_1_.getEntityId());
         if (entity != null) {
            NBTTagCompound nbttagcompound = entity.writeToNBT(new NBTTagCompound());
            this.player.connection.sendPacket(new SPacketNBTQueryResponse(p_211526_1_.getTransactionId(), nbttagcompound));
         }

      }
   }

   public void processNBTQueryBlockEntity(CPacketNBTQueryTileEntity p_211525_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_211525_1_, this, this.player.getServerWorld());
      if (this.player.hasPermissionLevel(2)) {
         TileEntity tileentity = this.player.getServerWorld().getTileEntity(p_211525_1_.getPosition());
         NBTTagCompound nbttagcompound = tileentity != null ? tileentity.writeToNBT(new NBTTagCompound()) : null;
         this.player.connection.sendPacket(new SPacketNBTQueryResponse(p_211525_1_.getTransactionId(), nbttagcompound));
      }
   }

   public void processPlayer(CPacketPlayer p_147347_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147347_1_, this, this.player.getServerWorld());
      if (isMovePlayerPacketInvalid(p_147347_1_)) {
         this.disconnect(new TextComponentTranslation("multiplayer.disconnect.invalid_player_movement"));
      } else {
         WorldServer worldserver = this.server.func_71218_a(this.player.dimension);
         if (!this.player.queuedEndExit) {
            if (this.networkTickCount == 0) {
               this.captureCurrentPosition();
            }

            if (this.targetPos != null) {
               if (this.networkTickCount - this.lastPositionUpdate > 20) {
                  this.lastPositionUpdate = this.networkTickCount;
                  this.setPlayerLocation(this.targetPos.x, this.targetPos.y, this.targetPos.z, this.player.rotationYaw, this.player.rotationPitch);
               }

            } else {
               this.lastPositionUpdate = this.networkTickCount;
               if (this.player.isRiding()) {
                  this.player.setPositionAndRotation(this.player.posX, this.player.posY, this.player.posZ, p_147347_1_.getYaw(this.player.rotationYaw), p_147347_1_.getPitch(this.player.rotationPitch));
                  this.server.getPlayerList().serverUpdateMovingPlayer(this.player);
               } else {
                  double d0 = this.player.posX;
                  double d1 = this.player.posY;
                  double d2 = this.player.posZ;
                  double d3 = this.player.posY;
                  double d4 = p_147347_1_.getX(this.player.posX);
                  double d5 = p_147347_1_.getY(this.player.posY);
                  double d6 = p_147347_1_.getZ(this.player.posZ);
                  float f = p_147347_1_.getYaw(this.player.rotationYaw);
                  float f1 = p_147347_1_.getPitch(this.player.rotationPitch);
                  double d7 = d4 - this.firstGoodX;
                  double d8 = d5 - this.firstGoodY;
                  double d9 = d6 - this.firstGoodZ;
                  double d10 = this.player.motionX * this.player.motionX + this.player.motionY * this.player.motionY + this.player.motionZ * this.player.motionZ;
                  double d11 = d7 * d7 + d8 * d8 + d9 * d9;
                  if (this.player.isPlayerSleeping()) {
                     if (d11 > 1.0D) {
                        this.setPlayerLocation(this.player.posX, this.player.posY, this.player.posZ, p_147347_1_.getYaw(this.player.rotationYaw), p_147347_1_.getPitch(this.player.rotationPitch));
                     }

                  } else {
                     ++this.movePacketCounter;
                     int i = this.movePacketCounter - this.lastMovePacketCounter;
                     if (i > 5) {
                        LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", this.player.getName().getString(), i);
                        i = 1;
                     }

                     if (!this.player.isInvulnerableDimensionChange() && (!this.player.getServerWorld().getGameRules().getBoolean("disableElytraMovementCheck") || !this.player.isElytraFlying())) {
                        float f2 = this.player.isElytraFlying() ? 300.0F : 100.0F;
                        if (d11 - d10 > (double)(f2 * (float)i) && (!this.server.isSinglePlayer() || !this.server.getServerOwner().equals(this.player.getGameProfile().getName()))) {
                           LOGGER.warn("{} moved too quickly! {},{},{}", this.player.getName().getString(), d7, d8, d9);
                           this.setPlayerLocation(this.player.posX, this.player.posY, this.player.posZ, this.player.rotationYaw, this.player.rotationPitch);
                           return;
                        }
                     }

                     boolean flag2 = worldserver.isCollisionBoxesEmpty(this.player, this.player.getEntityBoundingBox().shrink(0.0625D));
                     d7 = d4 - this.lastGoodX;
                     d8 = d5 - this.lastGoodY;
                     d9 = d6 - this.lastGoodZ;
                     if (this.player.onGround && !p_147347_1_.isOnGround() && d8 > 0.0D) {
                        this.player.jump();
                     }

                     this.player.move(MoverType.PLAYER, d7, d8, d9);
                     this.player.onGround = p_147347_1_.isOnGround();
                     double d12 = d8;
                     d7 = d4 - this.player.posX;
                     d8 = d5 - this.player.posY;
                     if (d8 > -0.5D || d8 < 0.5D) {
                        d8 = 0.0D;
                     }

                     d9 = d6 - this.player.posZ;
                     d11 = d7 * d7 + d8 * d8 + d9 * d9;
                     boolean flag = false;
                     if (!this.player.isInvulnerableDimensionChange() && d11 > 0.0625D && !this.player.isPlayerSleeping() && !this.player.interactionManager.isCreative() && this.player.interactionManager.getGameType() != GameType.SPECTATOR) {
                        flag = true;
                        LOGGER.warn("{} moved wrongly!", this.player.getName().getString());
                     }

                     this.player.setPositionAndRotation(d4, d5, d6, f, f1);
                     this.player.addMovementStat(this.player.posX - d0, this.player.posY - d1, this.player.posZ - d2);
                     if (!this.player.noClip && !this.player.isPlayerSleeping()) {
                        boolean flag1 = worldserver.isCollisionBoxesEmpty(this.player, this.player.getEntityBoundingBox().shrink(0.0625D));
                        if (flag2 && (flag || !flag1)) {
                           this.setPlayerLocation(d0, d1, d2, f, f1);
                           return;
                        }
                     }

                     this.floating = d12 >= -0.03125D;
                     this.floating &= !this.server.isFlightAllowed() && !this.player.capabilities.allowFlying;
                     this.floating &= !this.player.isPotionActive(MobEffects.LEVITATION) && !this.player.isElytraFlying() && !worldserver.checkBlockCollision(this.player.getEntityBoundingBox().grow(0.0625D).expand(0.0D, -0.55D, 0.0D));
                     this.player.onGround = p_147347_1_.isOnGround();
                     this.server.getPlayerList().serverUpdateMovingPlayer(this.player);
                     this.player.handleFalling(this.player.posY - d3, p_147347_1_.isOnGround());
                     this.lastGoodX = this.player.posX;
                     this.lastGoodY = this.player.posY;
                     this.lastGoodZ = this.player.posZ;
                  }
               }
            }
         }
      }
   }

   public void setPlayerLocation(double p_147364_1_, double p_147364_3_, double p_147364_5_, float p_147364_7_, float p_147364_8_) {
      this.setPlayerLocation(p_147364_1_, p_147364_3_, p_147364_5_, p_147364_7_, p_147364_8_, Collections.emptySet());
   }

   public void setPlayerLocation(double p_175089_1_, double p_175089_3_, double p_175089_5_, float p_175089_7_, float p_175089_8_, Set<SPacketPlayerPosLook.EnumFlags> p_175089_9_) {
      double d0 = p_175089_9_.contains(SPacketPlayerPosLook.EnumFlags.X) ? this.player.posX : 0.0D;
      double d1 = p_175089_9_.contains(SPacketPlayerPosLook.EnumFlags.Y) ? this.player.posY : 0.0D;
      double d2 = p_175089_9_.contains(SPacketPlayerPosLook.EnumFlags.Z) ? this.player.posZ : 0.0D;
      float f = p_175089_9_.contains(SPacketPlayerPosLook.EnumFlags.Y_ROT) ? this.player.rotationYaw : 0.0F;
      float f1 = p_175089_9_.contains(SPacketPlayerPosLook.EnumFlags.X_ROT) ? this.player.rotationPitch : 0.0F;
      this.targetPos = new Vec3d(p_175089_1_, p_175089_3_, p_175089_5_);
      if (++this.teleportId == Integer.MAX_VALUE) {
         this.teleportId = 0;
      }

      this.lastPositionUpdate = this.networkTickCount;
      this.player.setPositionAndRotation(p_175089_1_, p_175089_3_, p_175089_5_, p_175089_7_, p_175089_8_);
      this.player.connection.sendPacket(new SPacketPlayerPosLook(p_175089_1_ - d0, p_175089_3_ - d1, p_175089_5_ - d2, p_175089_7_ - f, p_175089_8_ - f1, p_175089_9_, this.teleportId));
   }

   public void processPlayerDigging(CPacketPlayerDigging p_147345_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147345_1_, this, this.player.getServerWorld());
      WorldServer worldserver = this.server.func_71218_a(this.player.dimension);
      BlockPos blockpos = p_147345_1_.getPosition();
      this.player.markPlayerActive();
      switch(p_147345_1_.getAction()) {
      case SWAP_HELD_ITEMS:
         if (!this.player.isSpectator()) {
            ItemStack itemstack = this.player.getHeldItem(EnumHand.OFF_HAND);
            this.player.setHeldItem(EnumHand.OFF_HAND, this.player.getHeldItem(EnumHand.MAIN_HAND));
            this.player.setHeldItem(EnumHand.MAIN_HAND, itemstack);
         }

         return;
      case DROP_ITEM:
         if (!this.player.isSpectator()) {
            this.player.dropItem(false);
         }

         return;
      case DROP_ALL_ITEMS:
         if (!this.player.isSpectator()) {
            this.player.dropItem(true);
         }

         return;
      case RELEASE_USE_ITEM:
         this.player.stopActiveHand();
         return;
      case START_DESTROY_BLOCK:
      case ABORT_DESTROY_BLOCK:
      case STOP_DESTROY_BLOCK:
         double d0 = this.player.posX - ((double)blockpos.getX() + 0.5D);
         double d1 = this.player.posY - ((double)blockpos.getY() + 0.5D) + 1.5D;
         double d2 = this.player.posZ - ((double)blockpos.getZ() + 0.5D);
         double d3 = d0 * d0 + d1 * d1 + d2 * d2;
         if (d3 > 36.0D) {
            return;
         } else if (blockpos.getY() >= this.server.getBuildLimit()) {
            return;
         } else {
            if (p_147345_1_.getAction() == CPacketPlayerDigging.Action.START_DESTROY_BLOCK) {
               if (!this.server.isBlockProtected(worldserver, blockpos, this.player) && worldserver.getWorldBorder().contains(blockpos)) {
                  this.player.interactionManager.startDestroyBlock(blockpos, p_147345_1_.getFacing());
               } else {
                  this.player.connection.sendPacket(new SPacketBlockChange(worldserver, blockpos));
               }
            } else {
               if (p_147345_1_.getAction() == CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                  this.player.interactionManager.stopDestroyBlock(blockpos);
               } else if (p_147345_1_.getAction() == CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK) {
                  this.player.interactionManager.abortDestroyBlock();
               }

               if (!worldserver.getBlockState(blockpos).isAir()) {
                  this.player.connection.sendPacket(new SPacketBlockChange(worldserver, blockpos));
               }
            }

            return;
         }
      default:
         throw new IllegalArgumentException("Invalid player action");
      }
   }

   public void processTryUseItemOnBlock(CPacketPlayerTryUseItemOnBlock p_184337_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_184337_1_, this, this.player.getServerWorld());
      WorldServer worldserver = this.server.func_71218_a(this.player.dimension);
      EnumHand enumhand = p_184337_1_.getHand();
      ItemStack itemstack = this.player.getHeldItem(enumhand);
      BlockPos blockpos = p_184337_1_.getPos();
      EnumFacing enumfacing = p_184337_1_.getDirection();
      this.player.markPlayerActive();
      if (blockpos.getY() < this.server.getBuildLimit() - 1 || enumfacing != EnumFacing.UP && blockpos.getY() < this.server.getBuildLimit()) {
         if (this.targetPos == null && this.player.getDistanceSq((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.5D, (double)blockpos.getZ() + 0.5D) < 64.0D && !this.server.isBlockProtected(worldserver, blockpos, this.player) && worldserver.getWorldBorder().contains(blockpos)) {
            this.player.interactionManager.processRightClickBlock(this.player, worldserver, itemstack, enumhand, blockpos, enumfacing, p_184337_1_.getFacingX(), p_184337_1_.getFacingY(), p_184337_1_.getFacingZ());
         }
      } else {
         ITextComponent itextcomponent = (new TextComponentTranslation("build.tooHigh", this.server.getBuildLimit())).applyTextStyle(TextFormatting.RED);
         this.player.connection.sendPacket(new SPacketChat(itextcomponent, ChatType.GAME_INFO));
      }

      this.player.connection.sendPacket(new SPacketBlockChange(worldserver, blockpos));
      this.player.connection.sendPacket(new SPacketBlockChange(worldserver, blockpos.offset(enumfacing)));
   }

   public void processTryUseItem(CPacketPlayerTryUseItem p_147346_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147346_1_, this, this.player.getServerWorld());
      WorldServer worldserver = this.server.func_71218_a(this.player.dimension);
      EnumHand enumhand = p_147346_1_.getHand();
      ItemStack itemstack = this.player.getHeldItem(enumhand);
      this.player.markPlayerActive();
      if (!itemstack.isEmpty()) {
         this.player.interactionManager.processRightClick(this.player, worldserver, itemstack, enumhand);
      }
   }

   public void handleSpectate(CPacketSpectate p_175088_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_175088_1_, this, this.player.getServerWorld());
      if (this.player.isSpectator()) {
         Entity entity = null;

         for(WorldServer worldserver : this.server.func_212370_w()) {
            entity = p_175088_1_.getEntity(worldserver);
            if (entity != null) {
               break;
            }
         }

         if (entity != null) {
            this.player.teleport((WorldServer)entity.world, entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
         }
      }

   }

   public void handleResourcePackStatus(CPacketResourcePackStatus p_175086_1_) {
   }

   public void processSteerBoat(CPacketSteerBoat p_184340_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_184340_1_, this, this.player.getServerWorld());
      Entity entity = this.player.getRidingEntity();
      if (entity instanceof EntityBoat) {
         ((EntityBoat)entity).setPaddleState(p_184340_1_.getLeft(), p_184340_1_.getRight());
      }

   }

   public void onDisconnect(ITextComponent p_147231_1_) {
      LOGGER.info("{} lost connection: {}", this.player.getName().getString(), p_147231_1_.getString());
      this.server.refreshStatusNextTick();
      this.server.getPlayerList().sendMessage((new TextComponentTranslation("multiplayer.player.left", this.player.getDisplayName())).applyTextStyle(TextFormatting.YELLOW));
      this.player.mountEntityAndWakeUp();
      this.server.getPlayerList().playerLoggedOut(this.player);
      if (this.server.isSinglePlayer() && this.player.getName().getString().equals(this.server.getServerOwner())) {
         LOGGER.info("Stopping singleplayer server as player logged out");
         this.server.initiateShutdown();
      }

   }

   public void sendPacket(Packet<?> p_147359_1_) {
      this.sendPacket(p_147359_1_, null);
   }

   public void sendPacket(Packet<?> p_211148_1_, @Nullable GenericFutureListener<? extends Future<? super Void>> p_211148_2_) {
      if (p_211148_1_ instanceof SPacketChat) {
         SPacketChat spacketchat = (SPacketChat)p_211148_1_;
         EntityPlayer.EnumChatVisibility entityplayer$enumchatvisibility = this.player.getChatVisibility();
         if (entityplayer$enumchatvisibility == EntityPlayer.EnumChatVisibility.HIDDEN && spacketchat.getType() != ChatType.GAME_INFO) {
            return;
         }

         if (entityplayer$enumchatvisibility == EntityPlayer.EnumChatVisibility.SYSTEM && !spacketchat.isSystem()) {
            return;
         }
      }

      try {
         this.netManager.sendPacket(p_211148_1_, p_211148_2_);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Sending packet");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Packet being sent");
         crashreportcategory.addDetail("Packet class", () -> {
            return p_211148_1_.getClass().getCanonicalName();
         });
         throw new ReportedException(crashreport);
      }
   }

   public void processHeldItemChange(CPacketHeldItemChange p_147355_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147355_1_, this, this.player.getServerWorld());
      if (p_147355_1_.getSlotId() >= 0 && p_147355_1_.getSlotId() < InventoryPlayer.getHotbarSize()) {
         this.player.inventory.currentItem = p_147355_1_.getSlotId();
         this.player.markPlayerActive();
      } else {
         LOGGER.warn("{} tried to set an invalid carried item", this.player.getName().getString());
      }
   }

   public void processChatMessage(CPacketChatMessage p_147354_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147354_1_, this, this.player.getServerWorld());
      if (this.player.getChatVisibility() == EntityPlayer.EnumChatVisibility.HIDDEN) {
         this.sendPacket(new SPacketChat((new TextComponentTranslation("chat.cannotSend")).applyTextStyle(TextFormatting.RED)));
      } else {
         this.player.markPlayerActive();
         String s = p_147354_1_.getMessage();
         s = org.apache.commons.lang3.StringUtils.normalizeSpace(s);

         for(int i = 0; i < s.length(); ++i) {
            if (!SharedConstants.isAllowedCharacter(s.charAt(i))) {
               this.disconnect(new TextComponentTranslation("multiplayer.disconnect.illegal_characters"));
               return;
            }
         }

         if (s.startsWith("/")) {
            this.handleSlashCommand(s);
         } else {
            ITextComponent itextcomponent = new TextComponentTranslation("chat.type.text", this.player.getDisplayName(), s);
            this.server.getPlayerList().sendMessage(itextcomponent, false);
         }

         this.chatSpamThresholdCount += 20;
         if (this.chatSpamThresholdCount > 200 && !this.server.getPlayerList().canSendCommands(this.player.getGameProfile())) {
            this.disconnect(new TextComponentTranslation("disconnect.spam"));
         }

      }
   }

   private void handleSlashCommand(String p_147361_1_) {
      this.server.getCommandManager().handleCommand(this.player.getCommandSource(), p_147361_1_);
   }

   public void handleAnimation(CPacketAnimation p_175087_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_175087_1_, this, this.player.getServerWorld());
      this.player.markPlayerActive();
      this.player.swingArm(p_175087_1_.getHand());
   }

   public void processEntityAction(CPacketEntityAction p_147357_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147357_1_, this, this.player.getServerWorld());
      this.player.markPlayerActive();
      switch(p_147357_1_.getAction()) {
      case START_SNEAKING:
         this.player.setSneaking(true);
         break;
      case STOP_SNEAKING:
         this.player.setSneaking(false);
         break;
      case START_SPRINTING:
         this.player.setSprinting(true);
         break;
      case STOP_SPRINTING:
         this.player.setSprinting(false);
         break;
      case STOP_SLEEPING:
         if (this.player.isPlayerSleeping()) {
            this.player.wakeUpPlayer(false, true, true);
            this.targetPos = new Vec3d(this.player.posX, this.player.posY, this.player.posZ);
         }
         break;
      case START_RIDING_JUMP:
         if (this.player.getRidingEntity() instanceof IJumpingMount) {
            IJumpingMount ijumpingmount1 = (IJumpingMount)this.player.getRidingEntity();
            int i = p_147357_1_.getAuxData();
            if (ijumpingmount1.canJump() && i > 0) {
               ijumpingmount1.handleStartJump(i);
            }
         }
         break;
      case STOP_RIDING_JUMP:
         if (this.player.getRidingEntity() instanceof IJumpingMount) {
            IJumpingMount ijumpingmount = (IJumpingMount)this.player.getRidingEntity();
            ijumpingmount.handleStopJump();
         }
         break;
      case OPEN_INVENTORY:
         if (this.player.getRidingEntity() instanceof AbstractHorse) {
            ((AbstractHorse)this.player.getRidingEntity()).openGUI(this.player);
         }
         break;
      case START_FALL_FLYING:
         if (!this.player.onGround && this.player.motionY < 0.0D && !this.player.isElytraFlying() && !this.player.isInWater()) {
            ItemStack itemstack = this.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            if (itemstack.getItem() == Items.ELYTRA && ItemElytra.isUsable(itemstack)) {
               this.player.setElytraFlying();
            }
         } else {
            this.player.clearElytraFlying();
         }
         break;
      default:
         throw new IllegalArgumentException("Invalid client command!");
      }

   }

   public void processUseEntity(CPacketUseEntity p_147340_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147340_1_, this, this.player.getServerWorld());
      WorldServer worldserver = this.server.func_71218_a(this.player.dimension);
      Entity entity = p_147340_1_.getEntityFromWorld(worldserver);
      this.player.markPlayerActive();
      if (entity != null) {
         boolean flag = this.player.canEntityBeSeen(entity);
         double d0 = 36.0D;
         if (!flag) {
            d0 = 9.0D;
         }

         if (this.player.getDistanceSq(entity) < d0) {
            if (p_147340_1_.getAction() == CPacketUseEntity.Action.INTERACT) {
               EnumHand enumhand = p_147340_1_.getHand();
               this.player.interactOn(entity, enumhand);
            } else if (p_147340_1_.getAction() == CPacketUseEntity.Action.INTERACT_AT) {
               EnumHand enumhand1 = p_147340_1_.getHand();
               entity.applyPlayerInteraction(this.player, p_147340_1_.getHitVec(), enumhand1);
            } else if (p_147340_1_.getAction() == CPacketUseEntity.Action.ATTACK) {
               if (entity instanceof EntityItem || entity instanceof EntityXPOrb || entity instanceof EntityArrow || entity == this.player) {
                  this.disconnect(new TextComponentTranslation("multiplayer.disconnect.invalid_entity_attacked"));
                  this.server.logWarning("Player " + this.player.getName().getString() + " tried to attack an invalid entity");
                  return;
               }

               this.player.attackTargetEntityWithCurrentItem(entity);
            }
         }
      }

   }

   public void processClientStatus(CPacketClientStatus p_147342_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147342_1_, this, this.player.getServerWorld());
      this.player.markPlayerActive();
      CPacketClientStatus.State cpacketclientstatus$state = p_147342_1_.getStatus();
      switch(cpacketclientstatus$state) {
      case PERFORM_RESPAWN:
         if (this.player.queuedEndExit) {
            this.player.queuedEndExit = false;
            this.player = this.server.getPlayerList().respawnPlayerForUser(this.player, DimensionType.OVERWORLD, true);
            CriteriaTriggers.CHANGED_DIMENSION.trigger(this.player, DimensionType.THE_END, DimensionType.OVERWORLD);
         } else {
            if (this.player.getHealth() > 0.0F) {
               return;
            }

            this.player = this.server.getPlayerList().respawnPlayerForUser(this.player, DimensionType.OVERWORLD, false);
            if (this.server.isHardcore()) {
               this.player.setGameType(GameType.SPECTATOR);
               this.player.getServerWorld().getGameRules().setOrCreateGameRule("spectatorsGenerateChunks", "false", this.server);
            }
         }
         break;
      case REQUEST_STATS:
         this.player.getStats().sendStats(this.player);
      }

   }

   public void processCloseWindow(CPacketCloseWindow p_147356_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147356_1_, this, this.player.getServerWorld());
      this.player.closeContainer();
   }

   public void processClickWindow(CPacketClickWindow p_147351_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147351_1_, this, this.player.getServerWorld());
      this.player.markPlayerActive();
      if (this.player.openContainer.windowId == p_147351_1_.getWindowId() && this.player.openContainer.getCanCraft(this.player)) {
         if (this.player.isSpectator()) {
            NonNullList<ItemStack> nonnulllist = NonNullList.create();

            for(int i = 0; i < this.player.openContainer.inventorySlots.size(); ++i) {
               nonnulllist.add(this.player.openContainer.inventorySlots.get(i).getStack());
            }

            this.player.sendAllContents(this.player.openContainer, nonnulllist);
         } else {
            ItemStack itemstack1 = this.player.openContainer.slotClick(p_147351_1_.getSlotId(), p_147351_1_.getUsedButton(), p_147351_1_.getClickType(), this.player);
            if (ItemStack.areItemStacksEqual(p_147351_1_.getClickedItem(), itemstack1)) {
               this.player.connection.sendPacket(new SPacketConfirmTransaction(p_147351_1_.getWindowId(), p_147351_1_.getActionNumber(), true));
               this.player.isChangingQuantityOnly = true;
               this.player.openContainer.detectAndSendChanges();
               this.player.updateHeldItem();
               this.player.isChangingQuantityOnly = false;
            } else {
               this.pendingTransactions.addKey(this.player.openContainer.windowId, p_147351_1_.getActionNumber());
               this.player.connection.sendPacket(new SPacketConfirmTransaction(p_147351_1_.getWindowId(), p_147351_1_.getActionNumber(), false));
               this.player.openContainer.setCanCraft(this.player, false);
               NonNullList<ItemStack> nonnulllist1 = NonNullList.create();

               for(int j = 0; j < this.player.openContainer.inventorySlots.size(); ++j) {
                  ItemStack itemstack = this.player.openContainer.inventorySlots.get(j).getStack();
                  nonnulllist1.add(itemstack.isEmpty() ? ItemStack.EMPTY : itemstack);
               }

               this.player.sendAllContents(this.player.openContainer, nonnulllist1);
            }
         }
      }

   }

   public void processPlaceRecipe(CPacketPlaceRecipe p_194308_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_194308_1_, this, this.player.getServerWorld());
      this.player.markPlayerActive();
      if (!this.player.isSpectator() && this.player.openContainer.windowId == p_194308_1_.func_194318_a() && this.player.openContainer.getCanCraft(this.player)) {
         IRecipe irecipe = this.server.getRecipeManager().getRecipe(p_194308_1_.func_199618_b());
         if (this.player.openContainer instanceof ContainerFurnace) {
            (new ServerRecipePlacerFurnace()).place(this.player, irecipe, p_194308_1_.func_194319_c());
         } else {
            (new ServerRecipePlacer()).place(this.player, irecipe, p_194308_1_.func_194319_c());
         }

      }
   }

   public void processEnchantItem(CPacketEnchantItem p_147338_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147338_1_, this, this.player.getServerWorld());
      this.player.markPlayerActive();
      if (this.player.openContainer.windowId == p_147338_1_.getWindowId() && this.player.openContainer.getCanCraft(this.player) && !this.player.isSpectator()) {
         this.player.openContainer.enchantItem(this.player, p_147338_1_.getButton());
         this.player.openContainer.detectAndSendChanges();
      }

   }

   public void processCreativeInventoryAction(CPacketCreativeInventoryAction p_147344_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147344_1_, this, this.player.getServerWorld());
      if (this.player.interactionManager.isCreative()) {
         boolean flag = p_147344_1_.getSlotId() < 0;
         ItemStack itemstack = p_147344_1_.getStack();
         NBTTagCompound nbttagcompound = itemstack.getChildTag("BlockEntityTag");
         if (!itemstack.isEmpty() && nbttagcompound != null && nbttagcompound.hasKey("x") && nbttagcompound.hasKey("y") && nbttagcompound.hasKey("z")) {
            BlockPos blockpos = new BlockPos(nbttagcompound.getInteger("x"), nbttagcompound.getInteger("y"), nbttagcompound.getInteger("z"));
            TileEntity tileentity = this.player.world.getTileEntity(blockpos);
            if (tileentity != null) {
               NBTTagCompound nbttagcompound1 = tileentity.writeToNBT(new NBTTagCompound());
               nbttagcompound1.removeTag("x");
               nbttagcompound1.removeTag("y");
               nbttagcompound1.removeTag("z");
               itemstack.setTagInfo("BlockEntityTag", nbttagcompound1);
            }
         }

         boolean flag1 = p_147344_1_.getSlotId() >= 1 && p_147344_1_.getSlotId() <= 45;
         boolean flag2 = itemstack.isEmpty() || itemstack.getDamage() >= 0 && itemstack.getCount() <= 64 && !itemstack.isEmpty();
         if (flag1 && flag2) {
            if (itemstack.isEmpty()) {
               this.player.inventoryContainer.putStackInSlot(p_147344_1_.getSlotId(), ItemStack.EMPTY);
            } else {
               this.player.inventoryContainer.putStackInSlot(p_147344_1_.getSlotId(), itemstack);
            }

            this.player.inventoryContainer.setCanCraft(this.player, true);
         } else if (flag && flag2 && this.itemDropThreshold < 200) {
            this.itemDropThreshold += 20;
            EntityItem entityitem = this.player.dropItem(itemstack, true);
            if (entityitem != null) {
               entityitem.setAgeToCreativeDespawnTime();
            }
         }
      }

   }

   public void processConfirmTransaction(CPacketConfirmTransaction p_147339_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147339_1_, this, this.player.getServerWorld());
      Short oshort = this.pendingTransactions.lookup(this.player.openContainer.windowId);
      if (oshort != null && p_147339_1_.getUid() == oshort && this.player.openContainer.windowId == p_147339_1_.getWindowId() && !this.player.openContainer.getCanCraft(this.player) && !this.player.isSpectator()) {
         this.player.openContainer.setCanCraft(this.player, true);
      }

   }

   public void processUpdateSign(CPacketUpdateSign p_147343_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147343_1_, this, this.player.getServerWorld());
      this.player.markPlayerActive();
      WorldServer worldserver = this.server.func_71218_a(this.player.dimension);
      BlockPos blockpos = p_147343_1_.getPosition();
      if (worldserver.isBlockLoaded(blockpos)) {
         IBlockState iblockstate = worldserver.getBlockState(blockpos);
         TileEntity tileentity = worldserver.getTileEntity(blockpos);
         if (!(tileentity instanceof TileEntitySign)) {
            return;
         }

         TileEntitySign tileentitysign = (TileEntitySign)tileentity;
         if (!tileentitysign.getIsEditable() || tileentitysign.getPlayer() != this.player) {
            this.server.logWarning("Player " + this.player.getName().getString() + " just tried to change non-editable sign");
            return;
         }

         String[] astring = p_147343_1_.getLines();

         for(int i = 0; i < astring.length; ++i) {
            tileentitysign.func_212365_a(i, new TextComponentString(TextFormatting.getTextWithoutFormattingCodes(astring[i])));
         }

         tileentitysign.markDirty();
         worldserver.notifyBlockUpdate(blockpos, iblockstate, iblockstate, 3);
      }

   }

   public void processKeepAlive(CPacketKeepAlive p_147353_1_) {
      if (this.field_194403_g && p_147353_1_.getKey() == this.field_194404_h) {
         int i = (int)(Util.milliTime() - this.field_194402_f);
         this.player.ping = (this.player.ping * 3 + i) / 4;
         this.field_194403_g = false;
      } else if (!this.player.getName().getString().equals(this.server.getServerOwner())) {
         this.disconnect(new TextComponentTranslation("disconnect.timeout"));
      }

   }

   public void processPlayerAbilities(CPacketPlayerAbilities p_147348_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147348_1_, this, this.player.getServerWorld());
      this.player.capabilities.isFlying = p_147348_1_.isFlying() && this.player.capabilities.allowFlying;
   }

   public void processClientSettings(CPacketClientSettings p_147352_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147352_1_, this, this.player.getServerWorld());
      this.player.handleClientSettings(p_147352_1_);
   }

   public void processCustomPayload(CPacketCustomPayload p_147349_1_) {
   }
}
