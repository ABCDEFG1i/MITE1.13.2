package net.minecraft.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreativeCrafting implements IContainerListener {
   private final Minecraft mc;

   public CreativeCrafting(Minecraft p_i46314_1_) {
      this.mc = p_i46314_1_;
   }

   public void sendAllContents(Container p_71110_1_, NonNullList<ItemStack> p_71110_2_) {
   }

   public void sendSlotContents(Container p_71111_1_, int p_71111_2_, ItemStack p_71111_3_) {
      this.mc.playerController.sendSlotPacket(p_71111_3_, p_71111_2_);
   }

   public void sendWindowProperty(Container p_71112_1_, int p_71112_2_, int p_71112_3_) {
   }

   public void sendAllWindowProperties(Container p_175173_1_, IInventory p_175173_2_) {
   }
}
