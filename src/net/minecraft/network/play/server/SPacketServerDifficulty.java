package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketServerDifficulty implements Packet<INetHandlerPlayClient> {
   private EnumDifficulty difficulty;
   private boolean difficultyLocked;

   public SPacketServerDifficulty() {
   }

   public SPacketServerDifficulty(EnumDifficulty p_i46963_1_, boolean p_i46963_2_) {
      this.difficulty = p_i46963_1_;
      this.difficultyLocked = p_i46963_2_;
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleServerDifficulty(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.difficulty = EnumDifficulty.byId(p_148837_1_.readUnsignedByte());
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.difficulty.getId());
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isDifficultyLocked() {
      return this.difficultyLocked;
   }

   @OnlyIn(Dist.CLIENT)
   public EnumDifficulty getDifficulty() {
      return this.difficulty;
   }
}
