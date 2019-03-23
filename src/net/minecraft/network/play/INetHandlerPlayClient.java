package net.minecraft.network.play;

import net.minecraft.network.INetHandler;
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

public interface INetHandlerPlayClient extends INetHandler {
   void handleSpawnObject(SPacketSpawnObject p_147235_1_);

   void handleSpawnExperienceOrb(SPacketSpawnExperienceOrb p_147286_1_);

   void handleSpawnGlobalEntity(SPacketSpawnGlobalEntity p_147292_1_);

   void handleSpawnMob(SPacketSpawnMob p_147281_1_);

   void handleScoreboardObjective(SPacketScoreboardObjective p_147291_1_);

   void handleSpawnPainting(SPacketSpawnPainting p_147288_1_);

   void handleSpawnPlayer(SPacketSpawnPlayer p_147237_1_);

   void handleAnimation(SPacketAnimation p_147279_1_);

   void handleStatistics(SPacketStatistics p_147293_1_);

   void handleRecipeBook(SPacketRecipeBook p_191980_1_);

   void handleBlockBreakAnim(SPacketBlockBreakAnim p_147294_1_);

   void handleSignEditorOpen(SPacketSignEditorOpen p_147268_1_);

   void handleUpdateTileEntity(SPacketUpdateTileEntity p_147273_1_);

   void handleBlockAction(SPacketBlockAction p_147261_1_);

   void handleBlockChange(SPacketBlockChange p_147234_1_);

   void handleChat(SPacketChat p_147251_1_);

   void handleMultiBlockChange(SPacketMultiBlockChange p_147287_1_);

   void handleMaps(SPacketMaps p_147264_1_);

   void handleConfirmTransaction(SPacketConfirmTransaction p_147239_1_);

   void handleCloseWindow(SPacketCloseWindow p_147276_1_);

   void handleWindowItems(SPacketWindowItems p_147241_1_);

   void handleOpenWindow(SPacketOpenWindow p_147265_1_);

   void handleWindowProperty(SPacketWindowProperty p_147245_1_);

   void handleSetSlot(SPacketSetSlot p_147266_1_);

   void handleCustomPayload(SPacketCustomPayload p_147240_1_);

   void handleDisconnect(SPacketDisconnect p_147253_1_);

   void handleUseBed(SPacketUseBed p_147278_1_);

   void handleEntityStatus(SPacketEntityStatus p_147236_1_);

   void handleEntityAttach(SPacketEntityAttach p_147243_1_);

   void handleSetPassengers(SPacketSetPassengers p_184328_1_);

   void handleExplosion(SPacketExplosion p_147283_1_);

   void handleChangeGameState(SPacketChangeGameState p_147252_1_);

   void handleKeepAlive(SPacketKeepAlive p_147272_1_);

   void handleChunkData(SPacketChunkData p_147263_1_);

   void processChunkUnload(SPacketUnloadChunk p_184326_1_);

   void handleEffect(SPacketEffect p_147277_1_);

   void handleJoinGame(SPacketJoinGame p_147282_1_);

   void handleEntityMovement(SPacketEntity p_147259_1_);

   void handlePlayerPosLook(SPacketPlayerPosLook p_184330_1_);

   void handleParticles(SPacketParticles p_147289_1_);

   void handlePlayerAbilities(SPacketPlayerAbilities p_147270_1_);

   void handlePlayerListItem(SPacketPlayerListItem p_147256_1_);

   void handleDestroyEntities(SPacketDestroyEntities p_147238_1_);

   void handleRemoveEntityEffect(SPacketRemoveEntityEffect p_147262_1_);

   void handleRespawn(SPacketRespawn p_147280_1_);

   void handleEntityHeadLook(SPacketEntityHeadLook p_147267_1_);

   void handleHeldItemChange(SPacketHeldItemChange p_147257_1_);

   void handleDisplayObjective(SPacketDisplayObjective p_147254_1_);

   void handleEntityMetadata(SPacketEntityMetadata p_147284_1_);

   void handleEntityVelocity(SPacketEntityVelocity p_147244_1_);

   void handleEntityEquipment(SPacketEntityEquipment p_147242_1_);

   void handleSetExperience(SPacketSetExperience p_147295_1_);

   void handleUpdateHealth(SPacketUpdateHealth p_147249_1_);

   void handleTeams(SPacketTeams p_147247_1_);

   void handleUpdateScore(SPacketUpdateScore p_147250_1_);

   void handleSpawnPosition(SPacketSpawnPosition p_147271_1_);

   void handleTimeUpdate(SPacketTimeUpdate p_147285_1_);

   void handleSoundEffect(SPacketSoundEffect p_184327_1_);

   void handleCustomSound(SPacketCustomSound p_184329_1_);

   void handleCollectItem(SPacketCollectItem p_147246_1_);

   void handleEntityTeleport(SPacketEntityTeleport p_147275_1_);

   void handleEntityProperties(SPacketEntityProperties p_147290_1_);

   void handleEntityEffect(SPacketEntityEffect p_147260_1_);

   void handleTags(SPacketTagsList p_199723_1_);

   void handleCombatEvent(SPacketCombatEvent p_175098_1_);

   void handleServerDifficulty(SPacketServerDifficulty p_175101_1_);

   void handleCamera(SPacketCamera p_175094_1_);

   void handleWorldBorder(SPacketWorldBorder p_175093_1_);

   void handleTitle(SPacketTitle p_175099_1_);

   void handlePlayerListHeaderFooter(SPacketPlayerListHeaderFooter p_175096_1_);

   void handleResourcePack(SPacketResourcePackSend p_175095_1_);

   void handleUpdateBossInfo(SPacketUpdateBossInfo p_184325_1_);

   void handleCooldown(SPacketCooldown p_184324_1_);

   void handleMoveVehicle(SPacketMoveVehicle p_184323_1_);

   void handleAdvancementInfo(SPacketAdvancementInfo p_191981_1_);

   void handleSelectAdvancementsTab(SPacketSelectAdvancementsTab p_194022_1_);

   void handlePlaceGhostRecipe(SPacketPlaceGhostRecipe p_194307_1_);

   void handleCommandList(SPacketCommandList p_195511_1_);

   void handleStopSound(SPacketStopSound p_195512_1_);

   void handleTabComplete(SPacketTabComplete p_195510_1_);

   void func_199525_a(SPacketUpdateRecipes p_199525_1_);

   void handlePlayerLook(SPacketPlayerLook p_200232_1_);

   void handleNBTQueryResponse(SPacketNBTQueryResponse p_211522_1_);
}
