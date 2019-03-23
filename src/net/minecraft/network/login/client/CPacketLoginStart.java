package net.minecraft.network.login.client;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginServer;

public class CPacketLoginStart implements Packet<INetHandlerLoginServer> {
   private GameProfile profile;

   public CPacketLoginStart() {
   }

   public CPacketLoginStart(GameProfile p_i46852_1_) {
      this.profile = p_i46852_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.profile = new GameProfile((UUID)null, p_148837_1_.readString(16));
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeString(this.profile.getName());
   }

   public void processPacket(INetHandlerLoginServer p_148833_1_) {
      p_148833_1_.processLoginStart(this);
   }

   public GameProfile getProfile() {
      return this.profile;
   }
}
