package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketChat implements Packet<INetHandlerPlayClient> {
   private ITextComponent chatComponent;
   private ChatType type;

   public SPacketChat() {
   }

   public SPacketChat(ITextComponent p_i46960_1_) {
      this(p_i46960_1_, ChatType.SYSTEM);
   }

   public SPacketChat(ITextComponent p_i47428_1_, ChatType p_i47428_2_) {
      this.chatComponent = p_i47428_1_;
      this.type = p_i47428_2_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.chatComponent = p_148837_1_.readTextComponent();
      this.type = ChatType.byId(p_148837_1_.readByte());
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeTextComponent(this.chatComponent);
      p_148840_1_.writeByte(this.type.getId());
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleChat(this);
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getChatComponent() {
      return this.chatComponent;
   }

   public boolean isSystem() {
      return this.type == ChatType.SYSTEM || this.type == ChatType.GAME_INFO;
   }

   public ChatType getType() {
      return this.type;
   }

   public boolean shouldSkipErrors() {
      return true;
   }
}
