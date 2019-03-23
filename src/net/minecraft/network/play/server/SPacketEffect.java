package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketEffect implements Packet<INetHandlerPlayClient> {
   private int soundType;
   private BlockPos soundPos;
   private int soundData;
   private boolean serverWide;

   public SPacketEffect() {
   }

   public SPacketEffect(int p_i46940_1_, BlockPos p_i46940_2_, int p_i46940_3_, boolean p_i46940_4_) {
      this.soundType = p_i46940_1_;
      this.soundPos = p_i46940_2_.toImmutable();
      this.soundData = p_i46940_3_;
      this.serverWide = p_i46940_4_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.soundType = p_148837_1_.readInt();
      this.soundPos = p_148837_1_.readBlockPos();
      this.soundData = p_148837_1_.readInt();
      this.serverWide = p_148837_1_.readBoolean();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeInt(this.soundType);
      p_148840_1_.writeBlockPos(this.soundPos);
      p_148840_1_.writeInt(this.soundData);
      p_148840_1_.writeBoolean(this.serverWide);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleEffect(this);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isSoundServerwide() {
      return this.serverWide;
   }

   @OnlyIn(Dist.CLIENT)
   public int getSoundType() {
      return this.soundType;
   }

   @OnlyIn(Dist.CLIENT)
   public int getSoundData() {
      return this.soundData;
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getSoundPos() {
      return this.soundPos;
   }
}
