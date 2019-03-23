package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketSpawnMob implements Packet<INetHandlerPlayClient> {
   private int entityId;
   private UUID uniqueId;
   private int type;
   private double x;
   private double y;
   private double z;
   private int velocityX;
   private int velocityY;
   private int velocityZ;
   private byte yaw;
   private byte pitch;
   private byte headPitch;
   private EntityDataManager dataManager;
   private List<EntityDataManager.DataEntry<?>> dataManagerEntries;

   public SPacketSpawnMob() {
   }

   public SPacketSpawnMob(EntityLivingBase p_i46973_1_) {
      this.entityId = p_i46973_1_.getEntityId();
      this.uniqueId = p_i46973_1_.getUniqueID();
      this.type = IRegistry.field_212629_r.func_148757_b(p_i46973_1_.getType());
      this.x = p_i46973_1_.posX;
      this.y = p_i46973_1_.posY;
      this.z = p_i46973_1_.posZ;
      this.yaw = (byte)((int)(p_i46973_1_.rotationYaw * 256.0F / 360.0F));
      this.pitch = (byte)((int)(p_i46973_1_.rotationPitch * 256.0F / 360.0F));
      this.headPitch = (byte)((int)(p_i46973_1_.rotationYawHead * 256.0F / 360.0F));
      double d0 = 3.9D;
      double d1 = p_i46973_1_.motionX;
      double d2 = p_i46973_1_.motionY;
      double d3 = p_i46973_1_.motionZ;
      if (d1 < -3.9D) {
         d1 = -3.9D;
      }

      if (d2 < -3.9D) {
         d2 = -3.9D;
      }

      if (d3 < -3.9D) {
         d3 = -3.9D;
      }

      if (d1 > 3.9D) {
         d1 = 3.9D;
      }

      if (d2 > 3.9D) {
         d2 = 3.9D;
      }

      if (d3 > 3.9D) {
         d3 = 3.9D;
      }

      this.velocityX = (int)(d1 * 8000.0D);
      this.velocityY = (int)(d2 * 8000.0D);
      this.velocityZ = (int)(d3 * 8000.0D);
      this.dataManager = p_i46973_1_.getDataManager();
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
      this.uniqueId = p_148837_1_.readUniqueId();
      this.type = p_148837_1_.readVarInt();
      this.x = p_148837_1_.readDouble();
      this.y = p_148837_1_.readDouble();
      this.z = p_148837_1_.readDouble();
      this.yaw = p_148837_1_.readByte();
      this.pitch = p_148837_1_.readByte();
      this.headPitch = p_148837_1_.readByte();
      this.velocityX = p_148837_1_.readShort();
      this.velocityY = p_148837_1_.readShort();
      this.velocityZ = p_148837_1_.readShort();
      this.dataManagerEntries = EntityDataManager.readEntries(p_148837_1_);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
      p_148840_1_.writeUniqueId(this.uniqueId);
      p_148840_1_.writeVarInt(this.type);
      p_148840_1_.writeDouble(this.x);
      p_148840_1_.writeDouble(this.y);
      p_148840_1_.writeDouble(this.z);
      p_148840_1_.writeByte(this.yaw);
      p_148840_1_.writeByte(this.pitch);
      p_148840_1_.writeByte(this.headPitch);
      p_148840_1_.writeShort(this.velocityX);
      p_148840_1_.writeShort(this.velocityY);
      p_148840_1_.writeShort(this.velocityZ);
      this.dataManager.writeEntries(p_148840_1_);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleSpawnMob(this);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public List<EntityDataManager.DataEntry<?>> getDataManagerEntries() {
      return this.dataManagerEntries;
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
   public int getEntityType() {
      return this.type;
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
   public int getVelocityX() {
      return this.velocityX;
   }

   @OnlyIn(Dist.CLIENT)
   public int getVelocityY() {
      return this.velocityY;
   }

   @OnlyIn(Dist.CLIENT)
   public int getVelocityZ() {
      return this.velocityZ;
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
   public byte getHeadPitch() {
      return this.headPitch;
   }
}
