package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketHeldItemChange implements Packet<INetHandlerPlayServer> {
   private int slotId;

   public CPacketHeldItemChange() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketHeldItemChange(int p_i46864_1_) {
      this.slotId = p_i46864_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.slotId = p_148837_1_.readShort();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeShort(this.slotId);
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processHeldItemChange(this);
   }

   public int getSlotId() {
      return this.slotId;
   }
}
