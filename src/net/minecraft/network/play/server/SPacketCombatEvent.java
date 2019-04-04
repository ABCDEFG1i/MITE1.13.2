package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class SPacketCombatEvent implements Packet<INetHandlerPlayClient> {
   public SPacketCombatEvent.Event eventType;
   public int playerId;
   public int entityId;
   public int duration;
   public ITextComponent deathMessage;

   public SPacketCombatEvent() {
   }

   public SPacketCombatEvent(CombatTracker p_i46931_1_, SPacketCombatEvent.Event p_i46931_2_) {
      this(p_i46931_1_, p_i46931_2_, new TextComponentString(""));
   }

   public SPacketCombatEvent(CombatTracker p_i49825_1_, SPacketCombatEvent.Event p_i49825_2_, ITextComponent p_i49825_3_) {
      this.eventType = p_i49825_2_;
      EntityLivingBase entitylivingbase = p_i49825_1_.getBestAttacker();
      switch(p_i49825_2_) {
      case END_COMBAT:
         this.duration = p_i49825_1_.getCombatDuration();
         this.entityId = entitylivingbase == null ? -1 : entitylivingbase.getEntityId();
         break;
      case ENTITY_DIED:
         this.playerId = p_i49825_1_.getFighter().getEntityId();
         this.entityId = entitylivingbase == null ? -1 : entitylivingbase.getEntityId();
         this.deathMessage = p_i49825_3_;
      }

   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.eventType = p_148837_1_.readEnumValue(SPacketCombatEvent.Event.class);
      if (this.eventType == SPacketCombatEvent.Event.END_COMBAT) {
         this.duration = p_148837_1_.readVarInt();
         this.entityId = p_148837_1_.readInt();
      } else if (this.eventType == SPacketCombatEvent.Event.ENTITY_DIED) {
         this.playerId = p_148837_1_.readVarInt();
         this.entityId = p_148837_1_.readInt();
         this.deathMessage = p_148837_1_.readTextComponent();
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnumValue(this.eventType);
      if (this.eventType == SPacketCombatEvent.Event.END_COMBAT) {
         p_148840_1_.writeVarInt(this.duration);
         p_148840_1_.writeInt(this.entityId);
      } else if (this.eventType == SPacketCombatEvent.Event.ENTITY_DIED) {
         p_148840_1_.writeVarInt(this.playerId);
         p_148840_1_.writeInt(this.entityId);
         p_148840_1_.writeTextComponent(this.deathMessage);
      }

   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleCombatEvent(this);
   }

   public boolean shouldSkipErrors() {
      return this.eventType == SPacketCombatEvent.Event.ENTITY_DIED;
   }

   public enum Event {
      ENTER_COMBAT,
      END_COMBAT,
      ENTITY_DIED
   }
}
