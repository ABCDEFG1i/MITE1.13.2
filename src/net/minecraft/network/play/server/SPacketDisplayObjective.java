package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketDisplayObjective implements Packet<INetHandlerPlayClient> {
   private int position;
   private String scoreName;

   public SPacketDisplayObjective() {
   }

   public SPacketDisplayObjective(int p_i46918_1_, @Nullable ScoreObjective p_i46918_2_) {
      this.position = p_i46918_1_;
      if (p_i46918_2_ == null) {
         this.scoreName = "";
      } else {
         this.scoreName = p_i46918_2_.getName();
      }

   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.position = p_148837_1_.readByte();
      this.scoreName = p_148837_1_.readString(16);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.position);
      p_148840_1_.writeString(this.scoreName);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleDisplayObjective(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getPosition() {
      return this.position;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public String getName() {
      return Objects.equals(this.scoreName, "") ? null : this.scoreName;
   }
}
