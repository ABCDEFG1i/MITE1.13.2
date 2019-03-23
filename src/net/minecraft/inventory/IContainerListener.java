package net.minecraft.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface IContainerListener {
   void sendAllContents(Container p_71110_1_, NonNullList<ItemStack> p_71110_2_);

   void sendSlotContents(Container p_71111_1_, int p_71111_2_, ItemStack p_71111_3_);

   void sendWindowProperty(Container p_71112_1_, int p_71112_2_, int p_71112_3_);

   void sendAllWindowProperties(Container p_175173_1_, IInventory p_175173_2_);
}
