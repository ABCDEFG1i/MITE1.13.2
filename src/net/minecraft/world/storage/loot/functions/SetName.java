package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class SetName extends LootFunction {
   private final ITextComponent name;

   public SetName(LootCondition[] p_i48242_1_, @Nullable ITextComponent p_i48242_2_) {
      super(p_i48242_1_);
      this.name = p_i48242_2_;
   }

   public ItemStack apply(ItemStack p_186553_1_, Random p_186553_2_, LootContext p_186553_3_) {
      if (this.name != null) {
         p_186553_1_.setDisplayName(this.name);
      }

      return p_186553_1_;
   }

   public static class Serializer extends LootFunction.Serializer<SetName> {
      public Serializer() {
         super(new ResourceLocation("set_name"), SetName.class);
      }

      public void serialize(JsonObject p_186532_1_, SetName p_186532_2_, JsonSerializationContext p_186532_3_) {
         if (p_186532_2_.name != null) {
            p_186532_1_.add("name", ITextComponent.Serializer.toJsonTree(p_186532_2_.name));
         }

      }

      public SetName deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, LootCondition[] p_186530_3_) {
         ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(p_186530_1_.get("name"));
         return new SetName(p_186530_3_, itextcomponent);
      }
   }
}
