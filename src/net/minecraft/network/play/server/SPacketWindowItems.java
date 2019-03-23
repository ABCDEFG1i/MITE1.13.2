package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketWindowItems implements Packet<INetHandlerPlayClient> {
   private int windowId;
   private List<ItemStack> itemStacks;

   public SPacketWindowItems() {
   }

   public SPacketWindowItems(int p_i47317_1_, NonNullList<ItemStack> p_i47317_2_) {
      this.windowId = p_i47317_1_;
      this.itemStacks = NonNullList.withSize(p_i47317_2_.size(), ItemStack.EMPTY);

      for(int i = 0; i < this.itemStacks.size(); ++i) {
         this.itemStacks.set(i, p_i47317_2_.get(i).copy());
      }

   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.windowId = p_148837_1_.readUnsignedByte();
      int i = p_148837_1_.readShort();
      this.itemStacks = NonNullList.withSize(i, ItemStack.EMPTY);

      for(int j = 0; j < i; ++j) {
         this.itemStacks.set(j, p_148837_1_.readItemStack());
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.windowId);
      p_148840_1_.writeShort(this.itemStacks.size());

      for(ItemStack itemstack : this.itemStacks) {
         p_148840_1_.writeItemStack(itemstack);
      }

   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleWindowItems(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getWindowId() {
      return this.windowId;
   }

   @OnlyIn(Dist.CLIENT)
   public List<ItemStack> getItemStacks() {
      return this.itemStacks;
   }
}
