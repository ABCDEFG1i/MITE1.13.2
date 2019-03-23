package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketConfirmTransaction implements Packet<INetHandlerPlayServer> {
   private int windowId;
   private short uid;
   private boolean accepted;

   public CPacketConfirmTransaction() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketConfirmTransaction(int p_i46884_1_, short p_i46884_2_, boolean p_i46884_3_) {
      this.windowId = p_i46884_1_;
      this.uid = p_i46884_2_;
      this.accepted = p_i46884_3_;
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processConfirmTransaction(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.windowId = p_148837_1_.readByte();
      this.uid = p_148837_1_.readShort();
      this.accepted = p_148837_1_.readByte() != 0;
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.windowId);
      p_148840_1_.writeShort(this.uid);
      p_148840_1_.writeByte(this.accepted ? 1 : 0);
   }

   public int getWindowId() {
      return this.windowId;
   }

   public short getUid() {
      return this.uid;
   }
}
