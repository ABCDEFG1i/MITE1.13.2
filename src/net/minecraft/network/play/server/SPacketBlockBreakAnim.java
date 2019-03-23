package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketBlockBreakAnim implements Packet<INetHandlerPlayClient> {
   private int breakerId;
   private BlockPos position;
   private int progress;

   public SPacketBlockBreakAnim() {
   }

   public SPacketBlockBreakAnim(int p_i46968_1_, BlockPos p_i46968_2_, int p_i46968_3_) {
      this.breakerId = p_i46968_1_;
      this.position = p_i46968_2_;
      this.progress = p_i46968_3_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.breakerId = p_148837_1_.readVarInt();
      this.position = p_148837_1_.readBlockPos();
      this.progress = p_148837_1_.readUnsignedByte();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.breakerId);
      p_148840_1_.writeBlockPos(this.position);
      p_148840_1_.writeByte(this.progress);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleBlockBreakAnim(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getBreakerId() {
      return this.breakerId;
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getPosition() {
      return this.position;
   }

   @OnlyIn(Dist.CLIENT)
   public int getProgress() {
      return this.progress;
   }
}
