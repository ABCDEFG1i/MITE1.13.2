package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketEntityAction implements Packet<INetHandlerPlayServer> {
   private int entityID;
   private CPacketEntityAction.Action action;
   private int auxData;

   public CPacketEntityAction() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketEntityAction(Entity p_i46869_1_, CPacketEntityAction.Action p_i46869_2_) {
      this(p_i46869_1_, p_i46869_2_, 0);
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketEntityAction(Entity p_i46870_1_, CPacketEntityAction.Action p_i46870_2_, int p_i46870_3_) {
      this.entityID = p_i46870_1_.getEntityId();
      this.action = p_i46870_2_;
      this.auxData = p_i46870_3_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityID = p_148837_1_.readVarInt();
      this.action = p_148837_1_.readEnumValue(CPacketEntityAction.Action.class);
      this.auxData = p_148837_1_.readVarInt();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityID);
      p_148840_1_.writeEnumValue(this.action);
      p_148840_1_.writeVarInt(this.auxData);
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processEntityAction(this);
   }

   public CPacketEntityAction.Action getAction() {
      return this.action;
   }

   public int getAuxData() {
      return this.auxData;
   }

   public enum Action {
      START_SNEAKING,
      STOP_SNEAKING,
      STOP_SLEEPING,
      START_SPRINTING,
      STOP_SPRINTING,
      START_RIDING_JUMP,
      STOP_RIDING_JUMP,
      OPEN_INVENTORY,
      START_FALL_FLYING
   }
}
