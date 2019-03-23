package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketPlayerListHeaderFooter implements Packet<INetHandlerPlayClient> {
   private ITextComponent header;
   private ITextComponent footer;

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.header = p_148837_1_.readTextComponent();
      this.footer = p_148837_1_.readTextComponent();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeTextComponent(this.header);
      p_148840_1_.writeTextComponent(this.footer);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handlePlayerListHeaderFooter(this);
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getHeader() {
      return this.header;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getFooter() {
      return this.footer;
   }
}
