package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Smelt extends LootFunction {
   private static final Logger LOGGER = LogManager.getLogger();

   public Smelt(LootCondition[] p_i46619_1_) {
      super(p_i46619_1_);
   }

   public ItemStack apply(ItemStack p_186553_1_, Random p_186553_2_, LootContext p_186553_3_) {
      if (p_186553_1_.isEmpty()) {
         return p_186553_1_;
      } else {
         IRecipe irecipe = findMatchingRecipe(p_186553_3_, p_186553_1_);
         if (irecipe != null) {
            ItemStack itemstack = irecipe.getRecipeOutput();
            if (!itemstack.isEmpty()) {
               ItemStack itemstack1 = itemstack.copy();
               itemstack1.setCount(p_186553_1_.getCount());
               return itemstack1;
            }
         }

         LOGGER.warn("Couldn't smelt {} because there is no smelting recipe", p_186553_1_);
         return p_186553_1_;
      }
   }

   @Nullable
   public static IRecipe findMatchingRecipe(LootContext p_202880_0_, ItemStack p_202880_1_) {
      for(IRecipe irecipe : p_202880_0_.getWorld().getRecipeManager().getRecipes()) {
         if (irecipe instanceof FurnaceRecipe && irecipe.getIngredients().get(0).test(p_202880_1_)) {
            return irecipe;
         }
      }

      return null;
   }

   public static class Serializer extends LootFunction.Serializer<Smelt> {
      protected Serializer() {
         super(new ResourceLocation("furnace_smelt"), Smelt.class);
      }

      public void serialize(JsonObject p_186532_1_, Smelt p_186532_2_, JsonSerializationContext p_186532_3_) {
      }

      public Smelt deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, LootCondition[] p_186530_3_) {
         return new Smelt(p_186530_3_);
      }
   }
}
