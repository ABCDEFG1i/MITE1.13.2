package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketClickWindow implements Packet<INetHandlerPlayServer> {
   private int windowId;
   private int slotId;
   private int packedClickData;
   private short actionNumber;
   private ItemStack clickedItem = ItemStack.EMPTY;
   private ClickType mode;

   public CPacketClickWindow() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketClickWindow(int p_i46882_1_, int p_i46882_2_, int p_i46882_3_, ClickType p_i46882_4_, ItemStack p_i46882_5_, short p_i46882_6_) {
      this.windowId = p_i46882_1_;
      this.slotId = p_i46882_2_;
      this.packedClickData = p_i46882_3_;
      this.clickedItem = p_i46882_5_.copy();
      this.actionNumber = p_i46882_6_;
      this.mode = p_i46882_4_;
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processClickWindow(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.windowId = p_148837_1_.readByte();
      this.slotId = p_148837_1_.readShort();
      this.packedClickData = p_148837_1_.readByte();
      this.actionNumber = p_148837_1_.readShort();
      this.mode = p_148837_1_.readEnumValue(ClickType.class);
      this.clickedItem = p_148837_1_.readItemStack();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.windowId);
      p_148840_1_.writeShort(this.slotId);
      p_148840_1_.writeByte(this.packedClickData);
      p_148840_1_.writeShort(this.actionNumber);
      p_148840_1_.writeEnumValue(this.mode);
      p_148840_1_.writeItemStack(this.clickedItem);
   }

   public int getWindowId() {
      return this.windowId;
   }

   public int getSlotId() {
      return this.slotId;
   }

   public int getUsedButton() {
      return this.packedClickData;
   }

   public short getActionNumber() {
      return this.actionNumber;
   }

   public ItemStack getClickedItem() {
      return this.clickedItem;
   }

   public ClickType getClickType() {
      return this.mode;
   }
}
