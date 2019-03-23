package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketUpdateSign implements Packet<INetHandlerPlayServer> {
   private BlockPos pos;
   private String[] lines;

   public CPacketUpdateSign() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketUpdateSign(BlockPos p_i49822_1_, ITextComponent p_i49822_2_, ITextComponent p_i49822_3_, ITextComponent p_i49822_4_, ITextComponent p_i49822_5_) {
      this.pos = p_i49822_1_;
      this.lines = new String[]{p_i49822_2_.getString(), p_i49822_3_.getString(), p_i49822_4_.getString(), p_i49822_5_.getString()};
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.pos = p_148837_1_.readBlockPos();
      this.lines = new String[4];

      for(int i = 0; i < 4; ++i) {
         this.lines[i] = p_148837_1_.readString(384);
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.pos);

      for(int i = 0; i < 4; ++i) {
         p_148840_1_.writeString(this.lines[i]);
      }

   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processUpdateSign(this);
   }

   public BlockPos getPosition() {
      return this.pos;
   }

   public String[] getLines() {
      return this.lines;
   }
}
