package net.minecraft.network.login.client;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.util.CryptManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketEncryptionResponse implements Packet<INetHandlerLoginServer> {
   private byte[] secretKeyEncrypted = new byte[0];
   private byte[] verifyTokenEncrypted = new byte[0];

   public CPacketEncryptionResponse() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketEncryptionResponse(SecretKey p_i46851_1_, PublicKey p_i46851_2_, byte[] p_i46851_3_) {
      this.secretKeyEncrypted = CryptManager.encryptData(p_i46851_2_, p_i46851_1_.getEncoded());
      this.verifyTokenEncrypted = CryptManager.encryptData(p_i46851_2_, p_i46851_3_);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.secretKeyEncrypted = p_148837_1_.readByteArray();
      this.verifyTokenEncrypted = p_148837_1_.readByteArray();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByteArray(this.secretKeyEncrypted);
      p_148840_1_.writeByteArray(this.verifyTokenEncrypted);
   }

   public void processPacket(INetHandlerLoginServer p_148833_1_) {
      p_148833_1_.processEncryptionResponse(this);
   }

   public SecretKey getSecretKey(PrivateKey p_149300_1_) {
      return CryptManager.decryptSharedKey(p_149300_1_, this.secretKeyEncrypted);
   }

   public byte[] getVerifyToken(PrivateKey p_149299_1_) {
      return p_149299_1_ == null ? this.verifyTokenEncrypted : CryptManager.decryptData(p_149299_1_, this.verifyTokenEncrypted);
   }
}
