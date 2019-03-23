package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public interface ISidedInventory extends IInventory {
   int[] getSlotsForFace(EnumFacing p_180463_1_);

   boolean canInsertItem(int p_180462_1_, ItemStack p_180462_2_, @Nullable EnumFacing p_180462_3_);

   boolean canExtractItem(int p_180461_1_, ItemStack p_180461_2_, EnumFacing p_180461_3_);
}
