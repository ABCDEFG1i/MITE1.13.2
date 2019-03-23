package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketSignEditorOpen implements Packet<INetHandlerPlayClient> {
   private BlockPos signPosition;

   public SPacketSignEditorOpen() {
   }

   public SPacketSignEditorOpen(BlockPos p_i46934_1_) {
      this.signPosition = p_i46934_1_;
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleSignEditorOpen(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.signPosition = p_148837_1_.readBlockPos();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.signPosition);
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getSignPosition() {
      return this.signPosition;
   }
}
