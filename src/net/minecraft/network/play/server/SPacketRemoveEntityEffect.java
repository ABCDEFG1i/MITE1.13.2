package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketRemoveEntityEffect implements Packet<INetHandlerPlayClient> {
   private int entityId;
   private Potion effectId;

   public SPacketRemoveEntityEffect() {
   }

   public SPacketRemoveEntityEffect(int p_i46925_1_, Potion p_i46925_2_) {
      this.entityId = p_i46925_1_;
      this.effectId = p_i46925_2_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
      this.effectId = Potion.getPotionById(p_148837_1_.readUnsignedByte());
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
      p_148840_1_.writeByte(Potion.getIdFromPotion(this.effectId));
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleRemoveEntityEffect(this);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Entity getEntity(World p_186967_1_) {
      return p_186967_1_.getEntityByID(this.entityId);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Potion getPotion() {
      return this.effectId;
   }
}
