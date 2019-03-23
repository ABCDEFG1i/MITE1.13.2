package net.minecraft.network;

import java.io.IOException;

public interface Packet<T extends INetHandler> {
   void readPacketData(PacketBuffer p_148837_1_) throws IOException;

   void writePacketData(PacketBuffer p_148840_1_) throws IOException;

   void processPacket(T p_148833_1_);

   default boolean shouldSkipErrors() {
      return false;
   }
}
