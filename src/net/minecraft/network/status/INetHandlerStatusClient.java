package net.minecraft.network.status;

import net.minecraft.network.INetHandler;
import net.minecraft.network.status.server.SPacketPong;
import net.minecraft.network.status.server.SPacketServerInfo;

public interface INetHandlerStatusClient extends INetHandler {
   void handleServerInfo(SPacketServerInfo p_147397_1_);

   void handlePong(SPacketPong p_147398_1_);
}
