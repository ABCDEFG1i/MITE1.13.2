package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketHeldItemChange implements Packet<INetHandlerPlayClient> {
   private int heldItemHotbarIndex;

   public SPacketHeldItemChange() {
   }

   public SPacketHeldItemChange(int p_i46919_1_) {
      this.heldItemHotbarIndex = p_i46919_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.heldItemHotbarIndex = p_148837_1_.readByte();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.heldItemHotbarIndex);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleHeldItemChange(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getHeldItemHotbarIndex() {
      return this.heldItemHotbarIndex;
   }
}
