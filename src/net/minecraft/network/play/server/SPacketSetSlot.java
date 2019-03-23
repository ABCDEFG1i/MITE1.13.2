package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketSetSlot implements Packet<INetHandlerPlayClient> {
   private int windowId;
   private int slot;
   private ItemStack item = ItemStack.EMPTY;

   public SPacketSetSlot() {
   }

   public SPacketSetSlot(int p_i46951_1_, int p_i46951_2_, ItemStack p_i46951_3_) {
      this.windowId = p_i46951_1_;
      this.slot = p_i46951_2_;
      this.item = p_i46951_3_.copy();
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleSetSlot(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.windowId = p_148837_1_.readByte();
      this.slot = p_148837_1_.readShort();
      this.item = p_148837_1_.readItemStack();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.windowId);
      p_148840_1_.writeShort(this.slot);
      p_148840_1_.writeItemStack(this.item);
   }

   @OnlyIn(Dist.CLIENT)
   public int getWindowId() {
      return this.windowId;
   }

   @OnlyIn(Dist.CLIENT)
   public int getSlot() {
      return this.slot;
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getStack() {
      return this.item;
   }
}
