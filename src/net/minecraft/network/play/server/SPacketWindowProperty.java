package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketWindowProperty implements Packet<INetHandlerPlayClient> {
   private int windowId;
   private int property;
   private int value;

   public SPacketWindowProperty() {
   }

   public SPacketWindowProperty(int p_i46952_1_, int p_i46952_2_, int p_i46952_3_) {
      this.windowId = p_i46952_1_;
      this.property = p_i46952_2_;
      this.value = p_i46952_3_;
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleWindowProperty(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.windowId = p_148837_1_.readUnsignedByte();
      this.property = p_148837_1_.readShort();
      this.value = p_148837_1_.readShort();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.windowId);
      p_148840_1_.writeShort(this.property);
      p_148840_1_.writeShort(this.value);
   }

   @OnlyIn(Dist.CLIENT)
   public int getWindowId() {
      return this.windowId;
   }

   @OnlyIn(Dist.CLIENT)
   public int getProperty() {
      return this.property;
   }

   @OnlyIn(Dist.CLIENT)
   public int getValue() {
      return this.value;
   }
}
