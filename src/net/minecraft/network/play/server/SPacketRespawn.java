package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketRespawn implements Packet<INetHandlerPlayClient> {
   private DimensionType dimensionID;
   private EnumDifficulty difficulty;
   private GameType gameType;
   private WorldType worldType;

   public SPacketRespawn() {
   }

   public SPacketRespawn(DimensionType p_i49824_1_, EnumDifficulty p_i49824_2_, WorldType p_i49824_3_, GameType p_i49824_4_) {
      this.dimensionID = p_i49824_1_;
      this.difficulty = p_i49824_2_;
      this.gameType = p_i49824_4_;
      this.worldType = p_i49824_3_;
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleRespawn(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.dimensionID = DimensionType.getById(p_148837_1_.readInt());
      this.difficulty = EnumDifficulty.byId(p_148837_1_.readUnsignedByte());
      this.gameType = GameType.getByID(p_148837_1_.readUnsignedByte());
      this.worldType = WorldType.byName(p_148837_1_.readString(16));
      if (this.worldType == null) {
         this.worldType = WorldType.DEFAULT;
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeInt(this.dimensionID.getId());
      p_148840_1_.writeByte(this.difficulty.getId());
      p_148840_1_.writeByte(this.gameType.getID());
      p_148840_1_.writeString(this.worldType.func_211888_a());
   }

   @OnlyIn(Dist.CLIENT)
   public DimensionType func_212643_b() {
      return this.dimensionID;
   }

   @OnlyIn(Dist.CLIENT)
   public EnumDifficulty getDifficulty() {
      return this.difficulty;
   }

   @OnlyIn(Dist.CLIENT)
   public GameType getGameType() {
      return this.gameType;
   }

   @OnlyIn(Dist.CLIENT)
   public WorldType getWorldType() {
      return this.worldType;
   }
}
