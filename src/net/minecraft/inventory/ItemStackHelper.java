package net.minecraft.inventory;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;

public class ItemStackHelper {
   public static ItemStack getAndSplit(List<ItemStack> p_188382_0_, int p_188382_1_, int p_188382_2_) {
      return p_188382_1_ >= 0 && p_188382_1_ < p_188382_0_.size() && !p_188382_0_.get(p_188382_1_).isEmpty() && p_188382_2_ > 0 ? p_188382_0_.get(p_188382_1_).split(p_188382_2_) : ItemStack.EMPTY;
   }

   public static ItemStack getAndRemove(List<ItemStack> p_188383_0_, int p_188383_1_) {
      return p_188383_1_ >= 0 && p_188383_1_ < p_188383_0_.size() ? p_188383_0_.set(p_188383_1_, ItemStack.EMPTY) : ItemStack.EMPTY;
   }

   public static NBTTagCompound saveAllItems(NBTTagCompound p_191282_0_, NonNullList<ItemStack> p_191282_1_) {
      return saveAllItems(p_191282_0_, p_191282_1_, true);
   }

   public static NBTTagCompound saveAllItems(NBTTagCompound p_191281_0_, NonNullList<ItemStack> p_191281_1_, boolean p_191281_2_) {
      NBTTagList nbttaglist = new NBTTagList();

      for(int i = 0; i < p_191281_1_.size(); ++i) {
         ItemStack itemstack = p_191281_1_.get(i);
         if (!itemstack.isEmpty()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setByte("Slot", (byte)i);
            itemstack.write(nbttagcompound);
            nbttaglist.add((INBTBase)nbttagcompound);
         }
      }

      if (!nbttaglist.isEmpty() || p_191281_2_) {
         p_191281_0_.setTag("Items", nbttaglist);
      }

      return p_191281_0_;
   }

   public static void loadAllItems(NBTTagCompound p_191283_0_, NonNullList<ItemStack> p_191283_1_) {
      NBTTagList nbttaglist = p_191283_0_.getTagList("Items", 10);

      for(int i = 0; i < nbttaglist.size(); ++i) {
         NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
         int j = nbttagcompound.getByte("Slot") & 255;
         if (j >= 0 && j < p_191283_1_.size()) {
            p_191283_1_.set(j, ItemStack.loadFromNBT(nbttagcompound));
         }
      }

   }
}
