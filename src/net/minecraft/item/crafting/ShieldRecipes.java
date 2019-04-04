package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ShieldRecipes extends IRecipeHidden {
   public ShieldRecipes(ResourceLocation p_i48160_1_) {
      super(p_i48160_1_);
   }

   public boolean matches(IInventory p_77569_1_, World p_77569_2_) {
      if (!(p_77569_1_ instanceof InventoryCrafting)) {
         return false;
      } else {
         ItemStack itemstack = ItemStack.EMPTY;
         ItemStack itemstack1 = ItemStack.EMPTY;

         for(int i = 0; i < p_77569_1_.getSizeInventory(); ++i) {
            ItemStack itemstack2 = p_77569_1_.getStackInSlot(i);
            if (!itemstack2.isEmpty()) {
               if (itemstack2.getItem() instanceof ItemBanner) {
                  if (!itemstack1.isEmpty()) {
                     return false;
                  }

                  itemstack1 = itemstack2;
               } else {
                  if (itemstack2.getItem() != Items.SHIELD) {
                     return false;
                  }

                  if (!itemstack.isEmpty()) {
                     return false;
                  }

                  if (itemstack2.getChildTag("BlockEntityTag") != null) {
                     return false;
                  }

                  itemstack = itemstack2;
               }
            }
         }

          return !itemstack.isEmpty() && !itemstack1.isEmpty();
      }
   }

   public ItemStack getCraftingResult(IInventory p_77572_1_) {
      ItemStack itemstack = ItemStack.EMPTY;
      ItemStack itemstack1 = ItemStack.EMPTY;

      for(int i = 0; i < p_77572_1_.getSizeInventory(); ++i) {
         ItemStack itemstack2 = p_77572_1_.getStackInSlot(i);
         if (!itemstack2.isEmpty()) {
            if (itemstack2.getItem() instanceof ItemBanner) {
               itemstack = itemstack2;
            } else if (itemstack2.getItem() == Items.SHIELD) {
               itemstack1 = itemstack2.copy();
            }
         }
      }

      if (itemstack1.isEmpty()) {
         return itemstack1;
      } else {
         NBTTagCompound nbttagcompound = itemstack.getChildTag("BlockEntityTag");
         NBTTagCompound nbttagcompound1 = nbttagcompound == null ? new NBTTagCompound() : nbttagcompound.copy();
         nbttagcompound1.setInteger("Base", ((ItemBanner)itemstack.getItem()).getColor().getId());
         itemstack1.setTagInfo("BlockEntityTag", nbttagcompound1);
         return itemstack1;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canFit(int p_194133_1_, int p_194133_2_) {
      return p_194133_1_ * p_194133_2_ >= 2;
   }

   public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CRAFTING_SPECIAL_SHIELDDECORATION;
   }
}
