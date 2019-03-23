package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketOpenWindow implements Packet<INetHandlerPlayClient> {
   private int windowId;
   private String inventoryType;
   private ITextComponent windowTitle;
   private int slotCount;
   private int entityId;

   public SPacketOpenWindow() {
   }

   public SPacketOpenWindow(int p_i46954_1_, String p_i46954_2_, ITextComponent p_i46954_3_) {
      this(p_i46954_1_, p_i46954_2_, p_i46954_3_, 0);
   }

   public SPacketOpenWindow(int p_i46955_1_, String p_i46955_2_, ITextComponent p_i46955_3_, int p_i46955_4_) {
      this.windowId = p_i46955_1_;
      this.inventoryType = p_i46955_2_;
      this.windowTitle = p_i46955_3_;
      this.slotCount = p_i46955_4_;
   }

   public SPacketOpenWindow(int p_i46956_1_, String p_i46956_2_, ITextComponent p_i46956_3_, int p_i46956_4_, int p_i46956_5_) {
      this(p_i46956_1_, p_i46956_2_, p_i46956_3_, p_i46956_4_);
      this.entityId = p_i46956_5_;
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleOpenWindow(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.windowId = p_148837_1_.readUnsignedByte();
      this.inventoryType = p_148837_1_.readString(32);
      this.windowTitle = p_148837_1_.readTextComponent();
      this.slotCount = p_148837_1_.readUnsignedByte();
      if (this.inventoryType.equals("EntityHorse")) {
         this.entityId = p_148837_1_.readInt();
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.windowId);
      p_148840_1_.writeString(this.inventoryType);
      p_148840_1_.writeTextComponent(this.windowTitle);
      p_148840_1_.writeByte(this.slotCount);
      if (this.inventoryType.equals("EntityHorse")) {
         p_148840_1_.writeInt(this.entityId);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public int getWindowId() {
      return this.windowId;
   }

   @OnlyIn(Dist.CLIENT)
   public String getGuiId() {
      return this.inventoryType;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getWindowTitle() {
      return this.windowTitle;
   }

   @OnlyIn(Dist.CLIENT)
   public int getSlotCount() {
      return this.slotCount;
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityId() {
      return this.entityId;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasSlots() {
      return this.slotCount > 0;
   }
}
