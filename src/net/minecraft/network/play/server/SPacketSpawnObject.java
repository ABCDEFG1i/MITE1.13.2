package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketSpawnObject implements Packet<INetHandlerPlayClient> {
   private int entityId;
   private UUID uniqueId;
   private double x;
   private double y;
   private double z;
   private int speedX;
   private int speedY;
   private int speedZ;
   private int pitch;
   private int yaw;
   private int type;
   private int data;

   public SPacketSpawnObject() {
   }

   public SPacketSpawnObject(Entity p_i46976_1_, int p_i46976_2_) {
      this(p_i46976_1_, p_i46976_2_, 0);
   }

   public SPacketSpawnObject(Entity p_i46977_1_, int p_i46977_2_, int p_i46977_3_) {
      this.entityId = p_i46977_1_.getEntityId();
      this.uniqueId = p_i46977_1_.getUniqueID();
      this.x = p_i46977_1_.posX;
      this.y = p_i46977_1_.posY;
      this.z = p_i46977_1_.posZ;
      this.pitch = MathHelper.floor(p_i46977_1_.rotationPitch * 256.0F / 360.0F);
      this.yaw = MathHelper.floor(p_i46977_1_.rotationYaw * 256.0F / 360.0F);
      this.type = p_i46977_2_;
      this.data = p_i46977_3_;
      double d0 = 3.9D;
      this.speedX = (int)(MathHelper.clamp(p_i46977_1_.motionX, -3.9D, 3.9D) * 8000.0D);
      this.speedY = (int)(MathHelper.clamp(p_i46977_1_.motionY, -3.9D, 3.9D) * 8000.0D);
      this.speedZ = (int)(MathHelper.clamp(p_i46977_1_.motionZ, -3.9D, 3.9D) * 8000.0D);
   }

   public SPacketSpawnObject(Entity p_i46978_1_, int p_i46978_2_, int p_i46978_3_, BlockPos p_i46978_4_) {
      this(p_i46978_1_, p_i46978_2_, p_i46978_3_);
      this.x = (double)p_i46978_4_.getX();
      this.y = (double)p_i46978_4_.getY();
      this.z = (double)p_i46978_4_.getZ();
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
      this.uniqueId = p_148837_1_.readUniqueId();
      this.type = p_148837_1_.readByte();
      this.x = p_148837_1_.readDouble();
      this.y = p_148837_1_.readDouble();
      this.z = p_148837_1_.readDouble();
      this.pitch = p_148837_1_.readByte();
      this.yaw = p_148837_1_.readByte();
      this.data = p_148837_1_.readInt();
      this.speedX = p_148837_1_.readShort();
      this.speedY = p_148837_1_.readShort();
      this.speedZ = p_148837_1_.readShort();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
      p_148840_1_.writeUniqueId(this.uniqueId);
      p_148840_1_.writeByte(this.type);
      p_148840_1_.writeDouble(this.x);
      p_148840_1_.writeDouble(this.y);
      p_148840_1_.writeDouble(this.z);
      p_148840_1_.writeByte(this.pitch);
      p_148840_1_.writeByte(this.yaw);
      p_148840_1_.writeInt(this.data);
      p_148840_1_.writeShort(this.speedX);
      p_148840_1_.writeShort(this.speedY);
      p_148840_1_.writeShort(this.speedZ);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleSpawnObject(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityID() {
      return this.entityId;
   }

   @OnlyIn(Dist.CLIENT)
   public UUID getUniqueId() {
      return this.uniqueId;
   }

   @OnlyIn(Dist.CLIENT)
   public double getX() {
      return this.x;
   }

   @OnlyIn(Dist.CLIENT)
   public double getY() {
      return this.y;
   }

   @OnlyIn(Dist.CLIENT)
   public double getZ() {
      return this.z;
   }

   @OnlyIn(Dist.CLIENT)
   public int getSpeedX() {
      return this.speedX;
   }

   @OnlyIn(Dist.CLIENT)
   public int getSpeedY() {
      return this.speedY;
   }

   @OnlyIn(Dist.CLIENT)
   public int getSpeedZ() {
      return this.speedZ;
   }

   @OnlyIn(Dist.CLIENT)
   public int getPitch() {
      return this.pitch;
   }

   @OnlyIn(Dist.CLIENT)
   public int getYaw() {
      return this.yaw;
   }

   @OnlyIn(Dist.CLIENT)
   public int getType() {
      return this.type;
   }

   @OnlyIn(Dist.CLIENT)
   public int getData() {
      return this.data;
   }

   public void setSpeedX(int p_149003_1_) {
      this.speedX = p_149003_1_;
   }

   public void setSpeedY(int p_149000_1_) {
      this.speedY = p_149000_1_;
   }

   public void setSpeedZ(int p_149007_1_) {
      this.speedZ = p_149007_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public void setData(int p_149002_1_) {
      this.data = p_149002_1_;
   }
}
