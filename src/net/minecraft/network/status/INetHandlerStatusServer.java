package net.minecraft.network.status;

import net.minecraft.network.INetHandler;
import net.minecraft.network.status.client.CPacketPing;
import net.minecraft.network.status.client.CPacketServerQuery;

public interface INetHandlerStatusServer extends INetHandler {
   void processPing(CPacketPing p_147311_1_);

   void processServerQuery(CPacketServerQuery p_147312_1_);
}
