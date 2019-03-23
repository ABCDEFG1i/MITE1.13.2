package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketNBTQueryTileEntity implements Packet<INetHandlerPlayServer> {
   private int transactionId;
   private BlockPos pos;

   public CPacketNBTQueryTileEntity() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketNBTQueryTileEntity(int p_i49756_1_, BlockPos p_i49756_2_) {
      this.transactionId = p_i49756_1_;
      this.pos = p_i49756_2_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.transactionId = p_148837_1_.readVarInt();
      this.pos = p_148837_1_.readBlockPos();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.transactionId);
      p_148840_1_.writeBlockPos(this.pos);
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processNBTQueryBlockEntity(this);
   }

   public int getTransactionId() {
      return this.transactionId;
   }

   public BlockPos getPosition() {
      return this.pos;
   }
}
