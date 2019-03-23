package net.minecraft.world.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Collection;
import java.util.Random;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;

public class LootEntryItem extends LootEntry {
   protected final Item item;
   protected final LootFunction[] functions;

   public LootEntryItem(Item p_i46644_1_, int p_i46644_2_, int p_i46644_3_, LootFunction[] p_i46644_4_, LootCondition[] p_i46644_5_) {
      super(p_i46644_2_, p_i46644_3_, p_i46644_5_);
      this.item = p_i46644_1_;
      this.functions = p_i46644_4_;
   }

   public void addLoot(Collection<ItemStack> p_186363_1_, Random p_186363_2_, LootContext p_186363_3_) {
      ItemStack itemstack = new ItemStack(this.item);

      for(LootFunction lootfunction : this.functions) {
         if (LootConditionManager.testAllConditions(lootfunction.getConditions(), p_186363_2_, p_186363_3_)) {
            itemstack = lootfunction.apply(itemstack, p_186363_2_, p_186363_3_);
         }
      }

      if (!itemstack.isEmpty()) {
         if (itemstack.getCount() < this.item.getMaxStackSize()) {
            p_186363_1_.add(itemstack);
         } else {
            int i = itemstack.getCount();

            while(i > 0) {
               ItemStack itemstack1 = itemstack.copy();
               itemstack1.setCount(Math.min(itemstack.getMaxStackSize(), i));
               i -= itemstack1.getCount();
               p_186363_1_.add(itemstack1);
            }
         }
      }

   }

   protected void serialize(JsonObject p_186362_1_, JsonSerializationContext p_186362_2_) {
      if (this.functions != null && this.functions.length > 0) {
         p_186362_1_.add("functions", p_186362_2_.serialize(this.functions));
      }

      ResourceLocation resourcelocation = IRegistry.field_212630_s.func_177774_c(this.item);
      if (resourcelocation == null) {
         throw new IllegalArgumentException("Can't serialize unknown item " + this.item);
      } else {
         p_186362_1_.addProperty("name", resourcelocation.toString());
      }
   }

   public static LootEntryItem deserialize(JsonObject p_186367_0_, JsonDeserializationContext p_186367_1_, int p_186367_2_, int p_186367_3_, LootCondition[] p_186367_4_) {
      Item item = JsonUtils.getItem(p_186367_0_, "name");
      LootFunction[] alootfunction;
      if (p_186367_0_.has("functions")) {
         alootfunction = JsonUtils.deserializeClass(p_186367_0_, "functions", p_186367_1_, LootFunction[].class);
      } else {
         alootfunction = new LootFunction[0];
      }

      return new LootEntryItem(item, p_186367_2_, p_186367_3_, alootfunction, p_186367_4_);
   }
}
