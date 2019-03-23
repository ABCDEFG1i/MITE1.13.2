package net.minecraft.inventory;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InventoryHelper {
   private static final Random RANDOM = new Random();

   public static void dropInventoryItems(World p_180175_0_, BlockPos p_180175_1_, IInventory p_180175_2_) {
      dropInventoryItems(p_180175_0_, (double)p_180175_1_.getX(), (double)p_180175_1_.getY(), (double)p_180175_1_.getZ(), p_180175_2_);
   }

   public static void dropInventoryItems(World p_180176_0_, Entity p_180176_1_, IInventory p_180176_2_) {
      dropInventoryItems(p_180176_0_, p_180176_1_.posX, p_180176_1_.posY, p_180176_1_.posZ, p_180176_2_);
   }

   private static void dropInventoryItems(World p_180174_0_, double p_180174_1_, double p_180174_3_, double p_180174_5_, IInventory p_180174_7_) {
      for(int i = 0; i < p_180174_7_.getSizeInventory(); ++i) {
         ItemStack itemstack = p_180174_7_.getStackInSlot(i);
         if (!itemstack.isEmpty()) {
            spawnItemStack(p_180174_0_, p_180174_1_, p_180174_3_, p_180174_5_, itemstack);
         }
      }

   }

   public static void spawnItemStack(World p_180173_0_, double p_180173_1_, double p_180173_3_, double p_180173_5_, ItemStack p_180173_7_) {
      float f = 0.75F;
      float f1 = 0.125F;
      float f2 = RANDOM.nextFloat() * 0.75F + 0.125F;
      float f3 = RANDOM.nextFloat() * 0.75F;
      float f4 = RANDOM.nextFloat() * 0.75F + 0.125F;

      while(!p_180173_7_.isEmpty()) {
         EntityItem entityitem = new EntityItem(p_180173_0_, p_180173_1_ + (double)f2, p_180173_3_ + (double)f3, p_180173_5_ + (double)f4, p_180173_7_.split(RANDOM.nextInt(21) + 10));
         float f5 = 0.05F;
         entityitem.motionX = RANDOM.nextGaussian() * (double)0.05F;
         entityitem.motionY = RANDOM.nextGaussian() * (double)0.05F + (double)0.2F;
         entityitem.motionZ = RANDOM.nextGaussian() * (double)0.05F;
         p_180173_0_.spawnEntity(entityitem);
      }

   }
}
