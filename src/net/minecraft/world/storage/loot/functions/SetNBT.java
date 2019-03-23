package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class SetNBT extends LootFunction {
   private final NBTTagCompound tag;

   public SetNBT(LootCondition[] p_i46620_1_, NBTTagCompound p_i46620_2_) {
      super(p_i46620_1_);
      this.tag = p_i46620_2_;
   }

   public ItemStack apply(ItemStack p_186553_1_, Random p_186553_2_, LootContext p_186553_3_) {
      p_186553_1_.getOrCreateTag().merge(this.tag);
      return p_186553_1_;
   }

   public static class Serializer extends LootFunction.Serializer<SetNBT> {
      public Serializer() {
         super(new ResourceLocation("set_nbt"), SetNBT.class);
      }

      public void serialize(JsonObject p_186532_1_, SetNBT p_186532_2_, JsonSerializationContext p_186532_3_) {
         p_186532_1_.addProperty("tag", p_186532_2_.tag.toString());
      }

      public SetNBT deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, LootCondition[] p_186530_3_) {
         try {
            NBTTagCompound nbttagcompound = JsonToNBT.getTagFromJson(JsonUtils.getString(p_186530_1_, "tag"));
            return new SetNBT(p_186530_3_, nbttagcompound);
         } catch (CommandSyntaxException commandsyntaxexception) {
            throw new JsonSyntaxException(commandsyntaxexception.getMessage());
         }
      }
   }
}
