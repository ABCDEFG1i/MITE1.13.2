package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FireworkRocketRecipe extends IRecipeHidden implements ITimedRecipe{
   private static final Ingredient INGREDIENT_PAPER = Ingredient.fromItems(Items.PAPER);
   private static final Ingredient INGREDIENT_GUNPOWDER = Ingredient.fromItems(Items.GUNPOWDER);
   private static final Ingredient INGREDIENT_FIREWORK_STAR = Ingredient.fromItems(Items.FIREWORK_STAR);

   public FireworkRocketRecipe(ResourceLocation p_i48168_1_) {
      super(p_i48168_1_);
   }

   @Override
   public int getCraftingTime(IInventory inventory) {
      NBTTagCompound tag = this.getCraftingResult(inventory).getOrCreateChildTag("Fireworks");
      return tag.getByte("Flight")*900;
   }

   public boolean matches(IInventory p_77569_1_, World p_77569_2_) {
      if (!(p_77569_1_ instanceof InventoryCrafting)) {
         return false;
      } else {
         boolean flag = false;
         int i = 0;

         for(int j = 0; j < p_77569_1_.getSizeInventory(); ++j) {
            ItemStack itemstack = p_77569_1_.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
               if (INGREDIENT_PAPER.test(itemstack)) {
                  if (flag) {
                     return false;
                  }

                  flag = true;
               } else if (INGREDIENT_GUNPOWDER.test(itemstack)) {
                  ++i;
                  if (i > 3) {
                     return false;
                  }
               } else if (!INGREDIENT_FIREWORK_STAR.test(itemstack)) {
                  return false;
               }
            }
         }

         return flag && i >= 1;
      }
   }

   public ItemStack getCraftingResult(IInventory p_77572_1_) {
      ItemStack itemstack = new ItemStack(Items.FIREWORK_ROCKET, 3);
      NBTTagCompound nbttagcompound = itemstack.getOrCreateChildTag("Fireworks");
      NBTTagList nbttaglist = new NBTTagList();
      int i = 0;

      for(int j = 0; j < p_77572_1_.getSizeInventory(); ++j) {
         ItemStack itemstack1 = p_77572_1_.getStackInSlot(j);
         if (!itemstack1.isEmpty()) {
            if (INGREDIENT_GUNPOWDER.test(itemstack1)) {
               ++i;
            } else if (INGREDIENT_FIREWORK_STAR.test(itemstack1)) {
               NBTTagCompound nbttagcompound1 = itemstack1.getChildTag("Explosion");
               if (nbttagcompound1 != null) {
                  nbttaglist.add(nbttagcompound1);
               }
            }
         }
      }

      nbttagcompound.setByte("Flight", (byte)i);
      if (!nbttaglist.isEmpty()) {
         nbttagcompound.setTag("Explosions", nbttaglist);
      }

      return itemstack;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canFit(int p_194133_1_, int p_194133_2_) {
      return p_194133_1_ * p_194133_2_ >= 2;
   }

   public ItemStack getRecipeOutput() {
      return new ItemStack(Items.FIREWORK_ROCKET);
   }

   public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CRAFTING_SPECIAL_FIREWORK_ROCKET;
   }
}
