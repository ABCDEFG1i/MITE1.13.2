package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketEntityTeleport implements Packet<INetHandlerPlayClient> {
   private int entityId;
   private double posX;
   private double posY;
   private double posZ;
   private byte yaw;
   private byte pitch;
   private boolean onGround;

   public SPacketEntityTeleport() {
   }

   public SPacketEntityTeleport(Entity p_i46893_1_) {
      this.entityId = p_i46893_1_.getEntityId();
      this.posX = p_i46893_1_.posX;
      this.posY = p_i46893_1_.posY;
      this.posZ = p_i46893_1_.posZ;
      this.yaw = (byte)((int)(p_i46893_1_.rotationYaw * 256.0F / 360.0F));
      this.pitch = (byte)((int)(p_i46893_1_.rotationPitch * 256.0F / 360.0F));
      this.onGround = p_i46893_1_.onGround;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
      this.posX = p_148837_1_.readDouble();
      this.posY = p_148837_1_.readDouble();
      this.posZ = p_148837_1_.readDouble();
      this.yaw = p_148837_1_.readByte();
      this.pitch = p_148837_1_.readByte();
      this.onGround = p_148837_1_.readBoolean();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
      p_148840_1_.writeDouble(this.posX);
      p_148840_1_.writeDouble(this.posY);
      p_148840_1_.writeDouble(this.posZ);
      p_148840_1_.writeByte(this.yaw);
      p_148840_1_.writeByte(this.pitch);
      p_148840_1_.writeBoolean(this.onGround);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleEntityTeleport(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityId() {
      return this.entityId;
   }

   @OnlyIn(Dist.CLIENT)
   public double getX() {
      return this.posX;
   }

   @OnlyIn(Dist.CLIENT)
   public double getY() {
      return this.posY;
   }

   @OnlyIn(Dist.CLIENT)
   public double getZ() {
      return this.posZ;
   }

   @OnlyIn(Dist.CLIENT)
   public byte getYaw() {
      return this.yaw;
   }

   @OnlyIn(Dist.CLIENT)
   public byte getPitch() {
      return this.pitch;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean getOnGround() {
      return this.onGround;
   }
}
