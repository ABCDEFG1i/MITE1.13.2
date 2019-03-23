package net.minecraft.network.play.client;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketSeenAdvancements implements Packet<INetHandlerPlayServer> {
   private CPacketSeenAdvancements.Action action;
   private ResourceLocation tab;

   public CPacketSeenAdvancements() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketSeenAdvancements(CPacketSeenAdvancements.Action p_i47595_1_, @Nullable ResourceLocation p_i47595_2_) {
      this.action = p_i47595_1_;
      this.tab = p_i47595_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public static CPacketSeenAdvancements openedTab(Advancement p_194163_0_) {
      return new CPacketSeenAdvancements(CPacketSeenAdvancements.Action.OPENED_TAB, p_194163_0_.getId());
   }

   @OnlyIn(Dist.CLIENT)
   public static CPacketSeenAdvancements closedScreen() {
      return new CPacketSeenAdvancements(CPacketSeenAdvancements.Action.CLOSED_SCREEN, (ResourceLocation)null);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.action = p_148837_1_.readEnumValue(CPacketSeenAdvancements.Action.class);
      if (this.action == CPacketSeenAdvancements.Action.OPENED_TAB) {
         this.tab = p_148837_1_.readResourceLocation();
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnumValue(this.action);
      if (this.action == CPacketSeenAdvancements.Action.OPENED_TAB) {
         p_148840_1_.writeResourceLocation(this.tab);
      }

   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.handleSeenAdvancements(this);
   }

   public CPacketSeenAdvancements.Action getAction() {
      return this.action;
   }

   public ResourceLocation getTab() {
      return this.tab;
   }

   public static enum Action {
      OPENED_TAB,
      CLOSED_SCREEN;
   }
}
