package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketEntityEquipment implements Packet<INetHandlerPlayClient> {
   private int entityID;
   private EntityEquipmentSlot equipmentSlot;
   private ItemStack itemStack = ItemStack.EMPTY;

   public SPacketEntityEquipment() {
   }

   public SPacketEntityEquipment(int p_i46913_1_, EntityEquipmentSlot p_i46913_2_, ItemStack p_i46913_3_) {
      this.entityID = p_i46913_1_;
      this.equipmentSlot = p_i46913_2_;
      this.itemStack = p_i46913_3_.copy();
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityID = p_148837_1_.readVarInt();
      this.equipmentSlot = p_148837_1_.readEnumValue(EntityEquipmentSlot.class);
      this.itemStack = p_148837_1_.readItemStack();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityID);
      p_148840_1_.writeEnumValue(this.equipmentSlot);
      p_148840_1_.writeItemStack(this.itemStack);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleEntityEquipment(this);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getItemStack() {
      return this.itemStack;
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityID() {
      return this.entityID;
   }

   @OnlyIn(Dist.CLIENT)
   public EntityEquipmentSlot getEquipmentSlot() {
      return this.equipmentSlot;
   }
}
