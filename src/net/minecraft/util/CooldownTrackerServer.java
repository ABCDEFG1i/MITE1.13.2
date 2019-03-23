package net.minecraft.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.SPacketCooldown;

public class CooldownTrackerServer extends CooldownTracker {
   private final EntityPlayerMP player;

   public CooldownTrackerServer(EntityPlayerMP p_i46741_1_) {
      this.player = p_i46741_1_;
   }

   protected void notifyOnSet(Item p_185140_1_, int p_185140_2_) {
      super.notifyOnSet(p_185140_1_, p_185140_2_);
      this.player.connection.sendPacket(new SPacketCooldown(p_185140_1_, p_185140_2_));
   }

   protected void notifyOnRemove(Item p_185146_1_) {
      super.notifyOnRemove(p_185146_1_);
      this.player.connection.sendPacket(new SPacketCooldown(p_185146_1_, 0));
   }
}
