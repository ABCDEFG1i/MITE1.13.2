package net.minecraft.world.storage.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTable {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final LootTable EMPTY_LOOT_TABLE = new LootTable(new LootPool[0]);
   private final LootPool[] pools;

   public LootTable(LootPool[] p_i46641_1_) {
      this.pools = p_i46641_1_;
   }

   public List<ItemStack> generateLootForPools(Random p_186462_1_, LootContext p_186462_2_) {
      List<ItemStack> list = Lists.newArrayList();
      if (p_186462_2_.addLootTable(this)) {
         for(LootPool lootpool : this.pools) {
            lootpool.generateLoot(list, p_186462_1_, p_186462_2_);
         }

         p_186462_2_.removeLootTable(this);
      } else {
         LOGGER.warn("Detected infinite loop in loot tables");
      }

      return list;
   }

   public void fillInventory(IInventory p_186460_1_, Random p_186460_2_, LootContext p_186460_3_) {
      List<ItemStack> list = this.generateLootForPools(p_186460_2_, p_186460_3_);
      List<Integer> list1 = this.getEmptySlotsRandomized(p_186460_1_, p_186460_2_);
      this.shuffleItems(list, list1.size(), p_186460_2_);

      for(ItemStack itemstack : list) {
         if (list1.isEmpty()) {
            LOGGER.warn("Tried to over-fill a container");
            return;
         }

         if (itemstack.isEmpty()) {
            p_186460_1_.setInventorySlotContents(list1.remove(list1.size() - 1), ItemStack.EMPTY);
         } else {
            p_186460_1_.setInventorySlotContents(list1.remove(list1.size() - 1), itemstack);
         }
      }

   }

   private void shuffleItems(List<ItemStack> p_186463_1_, int p_186463_2_, Random p_186463_3_) {
      List<ItemStack> list = Lists.newArrayList();
      Iterator<ItemStack> iterator = p_186463_1_.iterator();

      while(iterator.hasNext()) {
         ItemStack itemstack = iterator.next();
         if (itemstack.isEmpty()) {
            iterator.remove();
         } else if (itemstack.getCount() > 1) {
            list.add(itemstack);
            iterator.remove();
         }
      }

      while(p_186463_2_ - p_186463_1_.size() - list.size() > 0 && !list.isEmpty()) {
         ItemStack itemstack2 = list.remove(MathHelper.nextInt(p_186463_3_, 0, list.size() - 1));
         int i = MathHelper.nextInt(p_186463_3_, 1, itemstack2.getCount() / 2);
         ItemStack itemstack1 = itemstack2.split(i);
         if (itemstack2.getCount() > 1 && p_186463_3_.nextBoolean()) {
            list.add(itemstack2);
         } else {
            p_186463_1_.add(itemstack2);
         }

         if (itemstack1.getCount() > 1 && p_186463_3_.nextBoolean()) {
            list.add(itemstack1);
         } else {
            p_186463_1_.add(itemstack1);
         }
      }

      p_186463_1_.addAll(list);
      Collections.shuffle(p_186463_1_, p_186463_3_);
   }

   private List<Integer> getEmptySlotsRandomized(IInventory p_186459_1_, Random p_186459_2_) {
      List<Integer> list = Lists.newArrayList();

      for(int i = 0; i < p_186459_1_.getSizeInventory(); ++i) {
         if (p_186459_1_.getStackInSlot(i).isEmpty()) {
            list.add(i);
         }
      }

      Collections.shuffle(list, p_186459_2_);
      return list;
   }

   public static class Serializer implements JsonDeserializer<LootTable>, JsonSerializer<LootTable> {
      public LootTable deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = JsonUtils.getJsonObject(p_deserialize_1_, "loot table");
         LootPool[] alootpool = JsonUtils.deserializeClass(jsonobject, "pools", new LootPool[0], p_deserialize_3_, LootPool[].class);
         return new LootTable(alootpool);
      }

      public JsonElement serialize(LootTable p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("pools", p_serialize_3_.serialize(p_serialize_1_.pools));
         return jsonobject;
      }
   }
}
