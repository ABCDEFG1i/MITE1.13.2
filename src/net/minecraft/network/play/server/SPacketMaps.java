package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.Collection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketMaps implements Packet<INetHandlerPlayClient> {
   private int mapId;
   private byte mapScale;
   private boolean trackingPosition;
   private MapDecoration[] icons;
   private int minX;
   private int minZ;
   private int columns;
   private int rows;
   private byte[] mapDataBytes;

   public SPacketMaps() {
   }

   public SPacketMaps(int p_i46937_1_, byte p_i46937_2_, boolean p_i46937_3_, Collection<MapDecoration> p_i46937_4_, byte[] p_i46937_5_, int p_i46937_6_, int p_i46937_7_, int p_i46937_8_, int p_i46937_9_) {
      this.mapId = p_i46937_1_;
      this.mapScale = p_i46937_2_;
      this.trackingPosition = p_i46937_3_;
      this.icons = p_i46937_4_.toArray(new MapDecoration[p_i46937_4_.size()]);
      this.minX = p_i46937_6_;
      this.minZ = p_i46937_7_;
      this.columns = p_i46937_8_;
      this.rows = p_i46937_9_;
      this.mapDataBytes = new byte[p_i46937_8_ * p_i46937_9_];

      for(int i = 0; i < p_i46937_8_; ++i) {
         for(int j = 0; j < p_i46937_9_; ++j) {
            this.mapDataBytes[i + j * p_i46937_8_] = p_i46937_5_[p_i46937_6_ + i + (p_i46937_7_ + j) * 128];
         }
      }

   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.mapId = p_148837_1_.readVarInt();
      this.mapScale = p_148837_1_.readByte();
      this.trackingPosition = p_148837_1_.readBoolean();
      this.icons = new MapDecoration[p_148837_1_.readVarInt()];

      for(int i = 0; i < this.icons.length; ++i) {
         MapDecoration.Type mapdecoration$type = p_148837_1_.readEnumValue(MapDecoration.Type.class);
         this.icons[i] = new MapDecoration(mapdecoration$type, p_148837_1_.readByte(), p_148837_1_.readByte(), (byte)(p_148837_1_.readByte() & 15), p_148837_1_.readBoolean() ? p_148837_1_.readTextComponent() : null);
      }

      this.columns = p_148837_1_.readUnsignedByte();
      if (this.columns > 0) {
         this.rows = p_148837_1_.readUnsignedByte();
         this.minX = p_148837_1_.readUnsignedByte();
         this.minZ = p_148837_1_.readUnsignedByte();
         this.mapDataBytes = p_148837_1_.readByteArray();
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.mapId);
      p_148840_1_.writeByte(this.mapScale);
      p_148840_1_.writeBoolean(this.trackingPosition);
      p_148840_1_.writeVarInt(this.icons.length);

      for(MapDecoration mapdecoration : this.icons) {
         p_148840_1_.writeEnumValue(mapdecoration.getType());
         p_148840_1_.writeByte(mapdecoration.getX());
         p_148840_1_.writeByte(mapdecoration.getY());
         p_148840_1_.writeByte(mapdecoration.getRotation() & 15);
         if (mapdecoration.func_204309_g() != null) {
            p_148840_1_.writeBoolean(true);
            p_148840_1_.writeTextComponent(mapdecoration.func_204309_g());
         } else {
            p_148840_1_.writeBoolean(false);
         }
      }

      p_148840_1_.writeByte(this.columns);
      if (this.columns > 0) {
         p_148840_1_.writeByte(this.rows);
         p_148840_1_.writeByte(this.minX);
         p_148840_1_.writeByte(this.minZ);
         p_148840_1_.writeByteArray(this.mapDataBytes);
      }

   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleMaps(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getMapId() {
      return this.mapId;
   }

   @OnlyIn(Dist.CLIENT)
   public void setMapdataTo(MapData p_179734_1_) {
      p_179734_1_.scale = this.mapScale;
      p_179734_1_.trackingPosition = this.trackingPosition;
      p_179734_1_.mapDecorations.clear();

      for(int i = 0; i < this.icons.length; ++i) {
         MapDecoration mapdecoration = this.icons[i];
         p_179734_1_.mapDecorations.put("icon-" + i, mapdecoration);
      }

      for(int j = 0; j < this.columns; ++j) {
         for(int k = 0; k < this.rows; ++k) {
            p_179734_1_.colors[this.minX + j + (this.minZ + k) * 128] = this.mapDataBytes[j + k * this.columns];
         }
      }

   }
}
