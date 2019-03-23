package net.minecraft.network.login;

import net.minecraft.network.INetHandler;
import net.minecraft.network.login.client.CPacketCustomPayloadLogin;
import net.minecraft.network.login.client.CPacketEncryptionResponse;
import net.minecraft.network.login.client.CPacketLoginStart;

public interface INetHandlerLoginServer extends INetHandler {
   void processLoginStart(CPacketLoginStart p_147316_1_);

   void processEncryptionResponse(CPacketEncryptionResponse p_147315_1_);

   void func_209526_a(CPacketCustomPayloadLogin p_209526_1_);
}
