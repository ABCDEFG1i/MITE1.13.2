package net.minecraft.world;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.INameable;

public interface IInteractionObject extends INameable {
   Container createContainer(InventoryPlayer p_174876_1_, EntityPlayer p_174876_2_);

   String getGuiID();

   //For crafting tables , anvils and furnaces
   default int getGuiLevel(){
      return 0;
   }
}
