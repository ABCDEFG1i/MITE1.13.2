package net.minecraft.network.play;

import net.minecraft.network.INetHandler;
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

public interface INetHandlerPlayServer extends INetHandler {
   void handleAnimation(CPacketAnimation p_175087_1_);

   void processChatMessage(CPacketChatMessage p_147354_1_);

   void processClientStatus(CPacketClientStatus p_147342_1_);

   void processClientSettings(CPacketClientSettings p_147352_1_);

   void processConfirmTransaction(CPacketConfirmTransaction p_147339_1_);

   void processEnchantItem(CPacketEnchantItem p_147338_1_);

   void processClickWindow(CPacketClickWindow p_147351_1_);

   void processPlaceRecipe(CPacketPlaceRecipe p_194308_1_);

   void processCloseWindow(CPacketCloseWindow p_147356_1_);

   void processCustomPayload(CPacketCustomPayload p_147349_1_);

   void processUseEntity(CPacketUseEntity p_147340_1_);

   void processKeepAlive(CPacketKeepAlive p_147353_1_);

   void processPlayer(CPacketPlayer p_147347_1_);

   void processPlayerAbilities(CPacketPlayerAbilities p_147348_1_);

   void processPlayerDigging(CPacketPlayerDigging p_147345_1_);

   void processEntityAction(CPacketEntityAction p_147357_1_);

   void processInput(CPacketInput p_147358_1_);

   void processHeldItemChange(CPacketHeldItemChange p_147355_1_);

   void processCreativeInventoryAction(CPacketCreativeInventoryAction p_147344_1_);

   void processUpdateSign(CPacketUpdateSign p_147343_1_);

   void processTryUseItemOnBlock(CPacketPlayerTryUseItemOnBlock p_184337_1_);

   void processTryUseItem(CPacketPlayerTryUseItem p_147346_1_);

   void handleSpectate(CPacketSpectate p_175088_1_);

   void handleResourcePackStatus(CPacketResourcePackStatus p_175086_1_);

   void processSteerBoat(CPacketSteerBoat p_184340_1_);

   void processVehicleMove(CPacketVehicleMove p_184338_1_);

   void processConfirmTeleport(CPacketConfirmTeleport p_184339_1_);

   void handleRecipeBookUpdate(CPacketRecipeInfo p_191984_1_);

   void handleSeenAdvancements(CPacketSeenAdvancements p_194027_1_);

   void processTabComplete(CPacketTabComplete p_195518_1_);

   void processUpdateCommandBlock(CPacketUpdateCommandBlock p_210153_1_);

   void processUpdateCommandMinecart(CPacketUpdateCommandMinecart p_210158_1_);

   void processPickItem(CPacketPickItem p_210152_1_);

   void processRenameItem(CPacketRenameItem p_210155_1_);

   void processUpdateBeacon(CPacketUpdateBeacon p_210154_1_);

   void processUpdateStructureBlock(CPacketUpdateStructureBlock p_210157_1_);

   void processSelectTrade(CPacketSelectTrade p_210159_1_);

   void processEditBook(CPacketEditBook p_210156_1_);

   void processNBTQueryEntity(CPacketNBTQueryEntity p_211526_1_);

   void processNBTQueryBlockEntity(CPacketNBTQueryTileEntity p_211525_1_);
}
