package net.minecraft.network.login;

import net.minecraft.network.INetHandler;
import net.minecraft.network.login.server.SPacketCustomPayloadLogin;
import net.minecraft.network.login.server.SPacketDisconnectLogin;
import net.minecraft.network.login.server.SPacketEnableCompression;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import net.minecraft.network.login.server.SPacketLoginSuccess;

public interface INetHandlerLoginClient extends INetHandler {
   void handleEncryptionRequest(SPacketEncryptionRequest p_147389_1_);

   void handleLoginSuccess(SPacketLoginSuccess p_147390_1_);

   void handleDisconnect(SPacketDisconnectLogin p_147388_1_);

   void handleEnableCompression(SPacketEnableCompression p_180464_1_);

   void func_209521_a(SPacketCustomPayloadLogin p_209521_1_);
}
