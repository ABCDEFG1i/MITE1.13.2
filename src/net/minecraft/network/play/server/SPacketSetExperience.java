package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketSetExperience implements Packet<INetHandlerPlayClient> {
   private float experienceBar;
   private int totalExperience;
   private int level;

   public SPacketSetExperience() {
   }

   public SPacketSetExperience(float p_i46912_1_, int p_i46912_2_, int p_i46912_3_) {
      this.experienceBar = p_i46912_1_;
      this.totalExperience = p_i46912_2_;
      this.level = p_i46912_3_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.experienceBar = p_148837_1_.readFloat();
      this.level = p_148837_1_.readVarInt();
      this.totalExperience = p_148837_1_.readVarInt();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeFloat(this.experienceBar);
      p_148840_1_.writeVarInt(this.level);
      p_148840_1_.writeVarInt(this.totalExperience);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleSetExperience(this);
   }

   @OnlyIn(Dist.CLIENT)
   public float getExperienceBar() {
      return this.experienceBar;
   }

   @OnlyIn(Dist.CLIENT)
   public int getTotalExperience() {
      return this.totalExperience;
   }

   @OnlyIn(Dist.CLIENT)
   public int getLevel() {
      return this.level;
   }
}
