package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetAttributes extends LootFunction {
   private static final Logger LOGGER = LogManager.getLogger();
   private final SetAttributes.Modifier[] modifiers;

   public SetAttributes(LootCondition[] p_i46624_1_, SetAttributes.Modifier[] p_i46624_2_) {
      super(p_i46624_1_);
      this.modifiers = p_i46624_2_;
   }

   public ItemStack apply(ItemStack p_186553_1_, Random p_186553_2_, LootContext p_186553_3_) {
      for(SetAttributes.Modifier setattributes$modifier : this.modifiers) {
         UUID uuid = setattributes$modifier.uuid;
         if (uuid == null) {
            uuid = UUID.randomUUID();
         }

         EntityEquipmentSlot entityequipmentslot = setattributes$modifier.slots[p_186553_2_.nextInt(setattributes$modifier.slots.length)];
         p_186553_1_.addAttributeModifier(setattributes$modifier.attributeName, new AttributeModifier(uuid, setattributes$modifier.modifierName, (double)setattributes$modifier.amount.generateFloat(p_186553_2_), setattributes$modifier.operation), entityequipmentslot);
      }

      return p_186553_1_;
   }

   static class Modifier {
      private final String modifierName;
      private final String attributeName;
      private final int operation;
      private final RandomValueRange amount;
      @Nullable
      private final UUID uuid;
      private final EntityEquipmentSlot[] slots;

      private Modifier(String p_i46561_1_, String p_i46561_2_, int p_i46561_3_, RandomValueRange p_i46561_4_, EntityEquipmentSlot[] p_i46561_5_, @Nullable UUID p_i46561_6_) {
         this.modifierName = p_i46561_1_;
         this.attributeName = p_i46561_2_;
         this.operation = p_i46561_3_;
         this.amount = p_i46561_4_;
         this.uuid = p_i46561_6_;
         this.slots = p_i46561_5_;
      }

      public JsonObject serialize(JsonSerializationContext p_186592_1_) {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("name", this.modifierName);
         jsonobject.addProperty("attribute", this.attributeName);
         jsonobject.addProperty("operation", getOperationFromStr(this.operation));
         jsonobject.add("amount", p_186592_1_.serialize(this.amount));
         if (this.uuid != null) {
            jsonobject.addProperty("id", this.uuid.toString());
         }

         if (this.slots.length == 1) {
            jsonobject.addProperty("slot", this.slots[0].getName());
         } else {
            JsonArray jsonarray = new JsonArray();

            for(EntityEquipmentSlot entityequipmentslot : this.slots) {
               jsonarray.add(new JsonPrimitive(entityequipmentslot.getName()));
            }

            jsonobject.add("slot", jsonarray);
         }

         return jsonobject;
      }

      public static SetAttributes.Modifier deserialize(JsonObject p_186586_0_, JsonDeserializationContext p_186586_1_) {
         String s = JsonUtils.getString(p_186586_0_, "name");
         String s1 = JsonUtils.getString(p_186586_0_, "attribute");
         int i = getOperationFromInt(JsonUtils.getString(p_186586_0_, "operation"));
         RandomValueRange randomvaluerange = JsonUtils.deserializeClass(p_186586_0_, "amount", p_186586_1_, RandomValueRange.class);
         UUID uuid = null;
         EntityEquipmentSlot[] aentityequipmentslot;
         if (JsonUtils.isString(p_186586_0_, "slot")) {
            aentityequipmentslot = new EntityEquipmentSlot[]{EntityEquipmentSlot.fromString(JsonUtils.getString(p_186586_0_, "slot"))};
         } else {
            if (!JsonUtils.isJsonArray(p_186586_0_, "slot")) {
               throw new JsonSyntaxException("Invalid or missing attribute modifier slot; must be either string or array of strings.");
            }

            JsonArray jsonarray = JsonUtils.getJsonArray(p_186586_0_, "slot");
            aentityequipmentslot = new EntityEquipmentSlot[jsonarray.size()];
            int j = 0;

            for(JsonElement jsonelement : jsonarray) {
               aentityequipmentslot[j++] = EntityEquipmentSlot.fromString(JsonUtils.getString(jsonelement, "slot"));
            }

            if (aentityequipmentslot.length == 0) {
               throw new JsonSyntaxException("Invalid attribute modifier slot; must contain at least one entry.");
            }
         }

         if (p_186586_0_.has("id")) {
            String s2 = JsonUtils.getString(p_186586_0_, "id");

            try {
               uuid = UUID.fromString(s2);
            } catch (IllegalArgumentException var12) {
               throw new JsonSyntaxException("Invalid attribute modifier id '" + s2 + "' (must be UUID format, with dashes)");
            }
         }

         return new SetAttributes.Modifier(s, s1, i, randomvaluerange, aentityequipmentslot, uuid);
      }

      private static String getOperationFromStr(int p_186594_0_) {
         switch(p_186594_0_) {
         case 0:
            return "addition";
         case 1:
            return "multiply_base";
         case 2:
            return "multiply_total";
         default:
            throw new IllegalArgumentException("Unknown operation " + p_186594_0_);
         }
      }

      private static int getOperationFromInt(String p_186595_0_) {
         if ("addition".equals(p_186595_0_)) {
            return 0;
         } else if ("multiply_base".equals(p_186595_0_)) {
            return 1;
         } else if ("multiply_total".equals(p_186595_0_)) {
            return 2;
         } else {
            throw new JsonSyntaxException("Unknown attribute modifier operation " + p_186595_0_);
         }
      }
   }

   public static class Serializer extends LootFunction.Serializer<SetAttributes> {
      public Serializer() {
         super(new ResourceLocation("set_attributes"), SetAttributes.class);
      }

      public void serialize(JsonObject p_186532_1_, SetAttributes p_186532_2_, JsonSerializationContext p_186532_3_) {
         JsonArray jsonarray = new JsonArray();

         for(SetAttributes.Modifier setattributes$modifier : p_186532_2_.modifiers) {
            jsonarray.add(setattributes$modifier.serialize(p_186532_3_));
         }

         p_186532_1_.add("modifiers", jsonarray);
      }

      public SetAttributes deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, LootCondition[] p_186530_3_) {
         JsonArray jsonarray = JsonUtils.getJsonArray(p_186530_1_, "modifiers");
         SetAttributes.Modifier[] asetattributes$modifier = new SetAttributes.Modifier[jsonarray.size()];
         int i = 0;

         for(JsonElement jsonelement : jsonarray) {
            asetattributes$modifier[i++] = SetAttributes.Modifier.deserialize(JsonUtils.getJsonObject(jsonelement, "modifier"), p_186530_2_);
         }

         if (asetattributes$modifier.length == 0) {
            throw new JsonSyntaxException("Invalid attribute modifiers array; cannot be empty");
         } else {
            return new SetAttributes(p_186530_3_, asetattributes$modifier);
         }
      }
   }
}
