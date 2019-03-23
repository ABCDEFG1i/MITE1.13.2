package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketBlockChange implements Packet<INetHandlerPlayClient> {
   private BlockPos pos;
   private IBlockState state;

   public SPacketBlockChange() {
   }

   public SPacketBlockChange(IBlockReader p_i48982_1_, BlockPos p_i48982_2_) {
      this.pos = p_i48982_2_;
      this.state = p_i48982_1_.getBlockState(p_i48982_2_);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.pos = p_148837_1_.readBlockPos();
      this.state = Block.BLOCK_STATE_IDS.getByValue(p_148837_1_.readVarInt());
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.pos);
      p_148840_1_.writeVarInt(Block.getStateId(this.state));
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleBlockChange(this);
   }

   @OnlyIn(Dist.CLIENT)
   public IBlockState getState() {
      return this.state;
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getPos() {
      return this.pos;
   }
}
