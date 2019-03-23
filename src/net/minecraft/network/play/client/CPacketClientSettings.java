package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketClientSettings implements Packet<INetHandlerPlayServer> {
   private String lang;
   private int view;
   private EntityPlayer.EnumChatVisibility chatVisibility;
   private boolean enableColors;
   private int modelPartFlags;
   private EnumHandSide mainHand;

   public CPacketClientSettings() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketClientSettings(String p_i46885_1_, int p_i46885_2_, EntityPlayer.EnumChatVisibility p_i46885_3_, boolean p_i46885_4_, int p_i46885_5_, EnumHandSide p_i46885_6_) {
      this.lang = p_i46885_1_;
      this.view = p_i46885_2_;
      this.chatVisibility = p_i46885_3_;
      this.enableColors = p_i46885_4_;
      this.modelPartFlags = p_i46885_5_;
      this.mainHand = p_i46885_6_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.lang = p_148837_1_.readString(16);
      this.view = p_148837_1_.readByte();
      this.chatVisibility = p_148837_1_.readEnumValue(EntityPlayer.EnumChatVisibility.class);
      this.enableColors = p_148837_1_.readBoolean();
      this.modelPartFlags = p_148837_1_.readUnsignedByte();
      this.mainHand = p_148837_1_.readEnumValue(EnumHandSide.class);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeString(this.lang);
      p_148840_1_.writeByte(this.view);
      p_148840_1_.writeEnumValue(this.chatVisibility);
      p_148840_1_.writeBoolean(this.enableColors);
      p_148840_1_.writeByte(this.modelPartFlags);
      p_148840_1_.writeEnumValue(this.mainHand);
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processClientSettings(this);
   }

   public String getLang() {
      return this.lang;
   }

   public EntityPlayer.EnumChatVisibility getChatVisibility() {
      return this.chatVisibility;
   }

   public boolean isColorsEnabled() {
      return this.enableColors;
   }

   public int getModelPartFlags() {
      return this.modelPartFlags;
   }

   public EnumHandSide getMainHand() {
      return this.mainHand;
   }
}
