package net.minecraft.network.login.server;

import java.io.IOException;
import java.security.PublicKey;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.CryptManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketEncryptionRequest implements Packet<INetHandlerLoginClient> {
   private String hashedServerId;
   private PublicKey publicKey;
   private byte[] verifyToken;

   public SPacketEncryptionRequest() {
   }

   public SPacketEncryptionRequest(String p_i46855_1_, PublicKey p_i46855_2_, byte[] p_i46855_3_) {
      this.hashedServerId = p_i46855_1_;
      this.publicKey = p_i46855_2_;
      this.verifyToken = p_i46855_3_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.hashedServerId = p_148837_1_.readString(20);
      this.publicKey = CryptManager.decodePublicKey(p_148837_1_.readByteArray());
      this.verifyToken = p_148837_1_.readByteArray();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeString(this.hashedServerId);
      p_148840_1_.writeByteArray(this.publicKey.getEncoded());
      p_148840_1_.writeByteArray(this.verifyToken);
   }

   public void processPacket(INetHandlerLoginClient p_148833_1_) {
      p_148833_1_.handleEncryptionRequest(this);
   }

   @OnlyIn(Dist.CLIENT)
   public String getServerId() {
      return this.hashedServerId;
   }

   @OnlyIn(Dist.CLIENT)
   public PublicKey getPublicKey() {
      return this.publicKey;
   }

   @OnlyIn(Dist.CLIENT)
   public byte[] getVerifyToken() {
      return this.verifyToken;
   }
}
