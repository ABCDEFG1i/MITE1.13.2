package net.minecraft.world.storage.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.properties.EntityProperty;
import net.minecraft.world.storage.loot.properties.EntityPropertyManager;

public class EntityHasProperty implements LootCondition {
   private final EntityProperty[] properties;
   private final LootContext.EntityTarget target;

   public EntityHasProperty(EntityProperty[] p_i46617_1_, LootContext.EntityTarget p_i46617_2_) {
      this.properties = p_i46617_1_;
      this.target = p_i46617_2_;
   }

   public boolean testCondition(Random p_186618_1_, LootContext p_186618_2_) {
      Entity entity = p_186618_2_.getEntity(this.target);
      if (entity == null) {
         return false;
      } else {
         for(EntityProperty entityproperty : this.properties) {
            if (!entityproperty.testProperty(p_186618_1_, entity)) {
               return false;
            }
         }

         return true;
      }
   }

   public static class Serializer extends LootCondition.Serializer<EntityHasProperty> {
      protected Serializer() {
         super(new ResourceLocation("entity_properties"), EntityHasProperty.class);
      }

      public void serialize(JsonObject p_186605_1_, EntityHasProperty p_186605_2_, JsonSerializationContext p_186605_3_) {
         JsonObject jsonobject = new JsonObject();

         for(EntityProperty entityproperty : p_186605_2_.properties) {
            EntityProperty.Serializer<EntityProperty> serializer = EntityPropertyManager.getSerializerFor(entityproperty);
            jsonobject.add(serializer.getName().toString(), serializer.serialize(entityproperty, p_186605_3_));
         }

         p_186605_1_.add("properties", jsonobject);
         p_186605_1_.add("entity", p_186605_3_.serialize(p_186605_2_.target));
      }

      public EntityHasProperty deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         Set<Entry<String, JsonElement>> set = JsonUtils.getJsonObject(p_186603_1_, "properties").entrySet();
         EntityProperty[] aentityproperty = new EntityProperty[set.size()];
         int i = 0;

         for(Entry<String, JsonElement> entry : set) {
            aentityproperty[i++] = EntityPropertyManager.getSerializerForName(new ResourceLocation(entry.getKey())).deserialize(entry.getValue(), p_186603_2_);
         }

         return new EntityHasProperty(aentityproperty, JsonUtils.deserializeClass(p_186603_1_, "entity", p_186603_2_, LootContext.EntityTarget.class));
      }
   }
}
