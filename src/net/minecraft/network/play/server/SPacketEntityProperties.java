package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketEntityProperties implements Packet<INetHandlerPlayClient> {
   private int entityId;
   private final List<SPacketEntityProperties.Snapshot> snapshots = Lists.newArrayList();

   public SPacketEntityProperties() {
   }

   public SPacketEntityProperties(int p_i46892_1_, Collection<IAttributeInstance> p_i46892_2_) {
      this.entityId = p_i46892_1_;

      for(IAttributeInstance iattributeinstance : p_i46892_2_) {
         this.snapshots.add(new SPacketEntityProperties.Snapshot(iattributeinstance.getAttribute().getName(), iattributeinstance.getBaseValue(), iattributeinstance.getModifiers()));
      }

   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
      int i = p_148837_1_.readInt();

      for(int j = 0; j < i; ++j) {
         String s = p_148837_1_.readString(64);
         double d0 = p_148837_1_.readDouble();
         List<AttributeModifier> list = Lists.newArrayList();
         int k = p_148837_1_.readVarInt();

         for(int l = 0; l < k; ++l) {
            UUID uuid = p_148837_1_.readUniqueId();
            list.add(new AttributeModifier(uuid, "Unknown synced attribute modifier", p_148837_1_.readDouble(), p_148837_1_.readByte()));
         }

         this.snapshots.add(new SPacketEntityProperties.Snapshot(s, d0, list));
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
      p_148840_1_.writeInt(this.snapshots.size());

      for(SPacketEntityProperties.Snapshot spacketentityproperties$snapshot : this.snapshots) {
         p_148840_1_.writeString(spacketentityproperties$snapshot.getName());
         p_148840_1_.writeDouble(spacketentityproperties$snapshot.getBaseValue());
         p_148840_1_.writeVarInt(spacketentityproperties$snapshot.getModifiers().size());

         for(AttributeModifier attributemodifier : spacketentityproperties$snapshot.getModifiers()) {
            p_148840_1_.writeUniqueId(attributemodifier.getID());
            p_148840_1_.writeDouble(attributemodifier.getAmount());
            p_148840_1_.writeByte(attributemodifier.getOperation());
         }
      }

   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleEntityProperties(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityId() {
      return this.entityId;
   }

   @OnlyIn(Dist.CLIENT)
   public List<SPacketEntityProperties.Snapshot> getSnapshots() {
      return this.snapshots;
   }

   public class Snapshot {
      private final String name;
      private final double baseValue;
      private final Collection<AttributeModifier> modifiers;

      public Snapshot(String p_i47075_2_, double p_i47075_3_, Collection<AttributeModifier> p_i47075_5_) {
         this.name = p_i47075_2_;
         this.baseValue = p_i47075_3_;
         this.modifiers = p_i47075_5_;
      }

      public String getName() {
         return this.name;
      }

      public double getBaseValue() {
         return this.baseValue;
      }

      public Collection<AttributeModifier> getModifiers() {
         return this.modifiers;
      }
   }
}
