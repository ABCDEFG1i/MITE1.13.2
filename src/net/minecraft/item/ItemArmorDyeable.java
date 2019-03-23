package net.minecraft.item;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;

public class ItemArmorDyeable extends ItemArmor {
   public ItemArmorDyeable(IArmorMaterial p_i48509_1_, EntityEquipmentSlot p_i48509_2_, Item.Properties p_i48509_3_) {
      super(p_i48509_1_, p_i48509_2_, p_i48509_3_);
   }

   public boolean hasColor(ItemStack p_200883_1_) {
      NBTTagCompound nbttagcompound = p_200883_1_.getChildTag("display");
      return nbttagcompound != null && nbttagcompound.hasKey("color", 99);
   }

   public int getColor(ItemStack p_200886_1_) {
      NBTTagCompound nbttagcompound = p_200886_1_.getChildTag("display");
      return nbttagcompound != null && nbttagcompound.hasKey("color", 99) ? nbttagcompound.getInteger("color") : 10511680;
   }

   public void removeColor(ItemStack p_200884_1_) {
      NBTTagCompound nbttagcompound = p_200884_1_.getChildTag("display");
      if (nbttagcompound != null && nbttagcompound.hasKey("color")) {
         nbttagcompound.removeTag("color");
      }

   }

   public void setColor(ItemStack p_200885_1_, int p_200885_2_) {
      p_200885_1_.getOrCreateChildTag("display").setInteger("color", p_200885_2_);
   }
}
