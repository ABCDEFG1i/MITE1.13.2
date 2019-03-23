package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketBlockAction implements Packet<INetHandlerPlayClient> {
   private BlockPos blockPosition;
   private int instrument;
   private int pitch;
   private Block block;

   public SPacketBlockAction() {
   }

   public SPacketBlockAction(BlockPos p_i46966_1_, Block p_i46966_2_, int p_i46966_3_, int p_i46966_4_) {
      this.blockPosition = p_i46966_1_;
      this.block = p_i46966_2_;
      this.instrument = p_i46966_3_;
      this.pitch = p_i46966_4_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.blockPosition = p_148837_1_.readBlockPos();
      this.instrument = p_148837_1_.readUnsignedByte();
      this.pitch = p_148837_1_.readUnsignedByte();
      this.block = IRegistry.field_212618_g.func_148754_a(p_148837_1_.readVarInt());
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.blockPosition);
      p_148840_1_.writeByte(this.instrument);
      p_148840_1_.writeByte(this.pitch);
      p_148840_1_.writeVarInt(IRegistry.field_212618_g.func_148757_b(this.block));
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleBlockAction(this);
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getBlockPosition() {
      return this.blockPosition;
   }

   @OnlyIn(Dist.CLIENT)
   public int getData1() {
      return this.instrument;
   }

   @OnlyIn(Dist.CLIENT)
   public int getData2() {
      return this.pitch;
   }

   @OnlyIn(Dist.CLIENT)
   public Block getBlockType() {
      return this.block;
   }
}
