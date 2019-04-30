package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemFireworkRocket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FireworkStarRecipe extends IRecipeHidden implements ITimedRecipe{
   private static final Ingredient INGREDIENT_SHAPE = Ingredient.fromItems(Items.FIRE_CHARGE, Items.FEATHER, Items.GOLD_NUGGET, Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, Items.CREEPER_HEAD, Items.PLAYER_HEAD, Items.DRAGON_HEAD, Items.ZOMBIE_HEAD);
   private static final Ingredient INGREDIENT_FLICKER = Ingredient.fromItems(Items.DIAMOND);
   private static final Ingredient INGREDIENT_TRAIL = Ingredient.fromItems(Items.GLOWSTONE_DUST);
   private static final Map<Item, ItemFireworkRocket.Shape> ITEM_SHAPE_MAP = Util.make(Maps.newHashMap(), (p_209352_0_) -> {
      p_209352_0_.put(Items.FIRE_CHARGE, ItemFireworkRocket.Shape.LARGE_BALL);
      p_209352_0_.put(Items.FEATHER, ItemFireworkRocket.Shape.BURST);
      p_209352_0_.put(Items.GOLD_NUGGET, ItemFireworkRocket.Shape.STAR);
      p_209352_0_.put(Items.SKELETON_SKULL, ItemFireworkRocket.Shape.CREEPER);
      p_209352_0_.put(Items.WITHER_SKELETON_SKULL, ItemFireworkRocket.Shape.CREEPER);
      p_209352_0_.put(Items.CREEPER_HEAD, ItemFireworkRocket.Shape.CREEPER);
      p_209352_0_.put(Items.PLAYER_HEAD, ItemFireworkRocket.Shape.CREEPER);
      p_209352_0_.put(Items.DRAGON_HEAD, ItemFireworkRocket.Shape.CREEPER);
      p_209352_0_.put(Items.ZOMBIE_HEAD, ItemFireworkRocket.Shape.CREEPER);
   });
   private static final Ingredient field_196216_e = Ingredient.fromItems(Items.GUNPOWDER);

   public FireworkStarRecipe(ResourceLocation p_i48166_1_) {
      super(p_i48166_1_);
   }

   @Override
   public int getCraftingTime(IInventory inventory) {
      ItemStack itemStack = this.getCraftingResult(inventory);
      int effects = 0;
      NBTTagCompound nbttagcompound = itemStack.getOrCreateChildTag("Explosion");
      if (nbttagcompound.hasKey("Flicker")) effects++;
      if (nbttagcompound.hasKey("Trail")) effects++;
      if (nbttagcompound.hasKey("Colors")){
         return effects*2000+ nbttagcompound.getIntArray("Colors").length*700;
      }else{
         return effects*2000+100;
      }
   }


   public boolean matches(IInventory p_77569_1_, World p_77569_2_) {
      if (!(p_77569_1_ instanceof InventoryCrafting)) {
         return false;
      } else {
         boolean flag = false;
         boolean flag1 = false;
         boolean flag2 = false;
         boolean flag3 = false;
         boolean flag4 = false;

         for(int i = 0; i < p_77569_1_.getSizeInventory(); ++i) {
            ItemStack itemstack = p_77569_1_.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
               if (INGREDIENT_SHAPE.test(itemstack)) {
                  if (flag2) {
                     return false;
                  }

                  flag2 = true;
               } else if (INGREDIENT_TRAIL.test(itemstack)) {
                  if (flag4) {
                     return false;
                  }

                  flag4 = true;
               } else if (INGREDIENT_FLICKER.test(itemstack)) {
                  if (flag3) {
                     return false;
                  }

                  flag3 = true;
               } else if (field_196216_e.test(itemstack)) {
                  if (flag) {
                     return false;
                  }

                  flag = true;
               } else {
                  if (!(itemstack.getItem() instanceof ItemDye)) {
                     return false;
                  }

                  flag1 = true;
               }
            }
         }

         return flag && flag1;
      }
   }

   public ItemStack getCraftingResult(IInventory p_77572_1_) {
      ItemStack itemstack = new ItemStack(Items.FIREWORK_STAR);
      NBTTagCompound nbttagcompound = itemstack.getOrCreateChildTag("Explosion");
      ItemFireworkRocket.Shape itemfireworkrocket$shape = ItemFireworkRocket.Shape.SMALL_BALL;
      List<Integer> list = Lists.newArrayList();

      for(int i = 0; i < p_77572_1_.getSizeInventory(); ++i) {
         ItemStack itemstack1 = p_77572_1_.getStackInSlot(i);
         if (!itemstack1.isEmpty()) {
            if (INGREDIENT_SHAPE.test(itemstack1)) {
               itemfireworkrocket$shape = ITEM_SHAPE_MAP.get(itemstack1.getItem());
            } else if (INGREDIENT_TRAIL.test(itemstack1)) {
               nbttagcompound.setBoolean("Flicker", true);
            } else if (INGREDIENT_FLICKER.test(itemstack1)) {
               nbttagcompound.setBoolean("Trail", true);
            } else if (itemstack1.getItem() instanceof ItemDye) {
               list.add(((ItemDye)itemstack1.getItem()).getDyeColor().func_196060_f());
            }
         }
      }

      nbttagcompound.setIntArray("Colors", list);
      nbttagcompound.setByte("Type", (byte)itemfireworkrocket$shape.func_196071_a());
      return itemstack;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canFit(int p_194133_1_, int p_194133_2_) {
      return p_194133_1_ * p_194133_2_ >= 2;
   }

   public ItemStack getRecipeOutput() {
      return new ItemStack(Items.FIREWORK_STAR);
   }

   public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CRAFTING_SPECIAL_FIREWORK_STAR;
   }
}
