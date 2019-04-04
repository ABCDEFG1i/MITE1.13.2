package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.border.WorldBorder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketWorldBorder implements Packet<INetHandlerPlayClient> {
   private SPacketWorldBorder.Action action;
   private int size;
   private double centerX;
   private double centerZ;
   private double targetSize;
   private double diameter;
   private long timeUntilTarget;
   private int warningTime;
   private int warningDistance;

   public SPacketWorldBorder() {
   }

   public SPacketWorldBorder(WorldBorder p_i46921_1_, SPacketWorldBorder.Action p_i46921_2_) {
      this.action = p_i46921_2_;
      this.centerX = p_i46921_1_.getCenterX();
      this.centerZ = p_i46921_1_.getCenterZ();
      this.diameter = p_i46921_1_.getDiameter();
      this.targetSize = p_i46921_1_.getTargetSize();
      this.timeUntilTarget = p_i46921_1_.getTimeUntilTarget();
      this.size = p_i46921_1_.getSize();
      this.warningDistance = p_i46921_1_.getWarningDistance();
      this.warningTime = p_i46921_1_.getWarningTime();
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.action = p_148837_1_.readEnumValue(SPacketWorldBorder.Action.class);
      switch(this.action) {
      case SET_SIZE:
         this.targetSize = p_148837_1_.readDouble();
         break;
      case LERP_SIZE:
         this.diameter = p_148837_1_.readDouble();
         this.targetSize = p_148837_1_.readDouble();
         this.timeUntilTarget = p_148837_1_.readVarLong();
         break;
      case SET_CENTER:
         this.centerX = p_148837_1_.readDouble();
         this.centerZ = p_148837_1_.readDouble();
         break;
      case SET_WARNING_BLOCKS:
         this.warningDistance = p_148837_1_.readVarInt();
         break;
      case SET_WARNING_TIME:
         this.warningTime = p_148837_1_.readVarInt();
         break;
      case INITIALIZE:
         this.centerX = p_148837_1_.readDouble();
         this.centerZ = p_148837_1_.readDouble();
         this.diameter = p_148837_1_.readDouble();
         this.targetSize = p_148837_1_.readDouble();
         this.timeUntilTarget = p_148837_1_.readVarLong();
         this.size = p_148837_1_.readVarInt();
         this.warningDistance = p_148837_1_.readVarInt();
         this.warningTime = p_148837_1_.readVarInt();
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnumValue(this.action);
      switch(this.action) {
      case SET_SIZE:
         p_148840_1_.writeDouble(this.targetSize);
         break;
      case LERP_SIZE:
         p_148840_1_.writeDouble(this.diameter);
         p_148840_1_.writeDouble(this.targetSize);
         p_148840_1_.writeVarLong(this.timeUntilTarget);
         break;
      case SET_CENTER:
         p_148840_1_.writeDouble(this.centerX);
         p_148840_1_.writeDouble(this.centerZ);
         break;
      case SET_WARNING_BLOCKS:
         p_148840_1_.writeVarInt(this.warningDistance);
         break;
      case SET_WARNING_TIME:
         p_148840_1_.writeVarInt(this.warningTime);
         break;
      case INITIALIZE:
         p_148840_1_.writeDouble(this.centerX);
         p_148840_1_.writeDouble(this.centerZ);
         p_148840_1_.writeDouble(this.diameter);
         p_148840_1_.writeDouble(this.targetSize);
         p_148840_1_.writeVarLong(this.timeUntilTarget);
         p_148840_1_.writeVarInt(this.size);
         p_148840_1_.writeVarInt(this.warningDistance);
         p_148840_1_.writeVarInt(this.warningTime);
      }

   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleWorldBorder(this);
   }

   @OnlyIn(Dist.CLIENT)
   public void apply(WorldBorder p_179788_1_) {
      switch(this.action) {
      case SET_SIZE:
         p_179788_1_.setTransition(this.targetSize);
         break;
      case LERP_SIZE:
         p_179788_1_.setTransition(this.diameter, this.targetSize, this.timeUntilTarget);
         break;
      case SET_CENTER:
         p_179788_1_.setCenter(this.centerX, this.centerZ);
         break;
      case SET_WARNING_BLOCKS:
         p_179788_1_.setWarningDistance(this.warningDistance);
         break;
      case SET_WARNING_TIME:
         p_179788_1_.setWarningTime(this.warningTime);
         break;
      case INITIALIZE:
         p_179788_1_.setCenter(this.centerX, this.centerZ);
         if (this.timeUntilTarget > 0L) {
            p_179788_1_.setTransition(this.diameter, this.targetSize, this.timeUntilTarget);
         } else {
            p_179788_1_.setTransition(this.targetSize);
         }

         p_179788_1_.setSize(this.size);
         p_179788_1_.setWarningDistance(this.warningDistance);
         p_179788_1_.setWarningTime(this.warningTime);
      }

   }

   public enum Action {
      SET_SIZE,
      LERP_SIZE,
      SET_CENTER,
      INITIALIZE,
      SET_WARNING_TIME,
      SET_WARNING_BLOCKS
   }
}
