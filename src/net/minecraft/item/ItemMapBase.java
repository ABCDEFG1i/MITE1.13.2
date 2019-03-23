package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

public class ItemMapBase extends Item {
   public ItemMapBase(Item.Properties p_i48514_1_) {
      super(p_i48514_1_);
   }

   public boolean isComplex() {
      return true;
   }

   @Nullable
   public Packet<?> getUpdatePacket(ItemStack p_150911_1_, World p_150911_2_, EntityPlayer p_150911_3_) {
      return null;
   }
}
