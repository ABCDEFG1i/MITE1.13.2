package net.minecraft.world.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Collection;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class LootEntryTable extends LootEntry {
   protected final ResourceLocation table;

   public LootEntryTable(ResourceLocation p_i46639_1_, int p_i46639_2_, int p_i46639_3_, LootCondition[] p_i46639_4_) {
      super(p_i46639_2_, p_i46639_3_, p_i46639_4_);
      this.table = p_i46639_1_;
   }

   public void addLoot(Collection<ItemStack> p_186363_1_, Random p_186363_2_, LootContext p_186363_3_) {
      LootTable loottable = p_186363_3_.getLootTableManager().getLootTableFromLocation(this.table);
      p_186363_1_.addAll(loottable.generateLootForPools(p_186363_2_, p_186363_3_));
   }

   protected void serialize(JsonObject p_186362_1_, JsonSerializationContext p_186362_2_) {
      p_186362_1_.addProperty("name", this.table.toString());
   }

   public static LootEntryTable deserialize(JsonObject p_186370_0_, JsonDeserializationContext p_186370_1_, int p_186370_2_, int p_186370_3_, LootCondition[] p_186370_4_) {
      ResourceLocation resourcelocation = new ResourceLocation(JsonUtils.getString(p_186370_0_, "name"));
      return new LootEntryTable(resourcelocation, p_186370_2_, p_186370_3_, p_186370_4_);
   }
}
