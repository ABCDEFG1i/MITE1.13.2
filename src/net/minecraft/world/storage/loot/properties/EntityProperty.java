package net.minecraft.world.storage.loot.properties;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public interface EntityProperty {
   boolean testProperty(Random p_186657_1_, Entity p_186657_2_);

   public abstract static class Serializer<T extends EntityProperty> {
      private final ResourceLocation name;
      private final Class<T> propertyClass;

      protected Serializer(ResourceLocation p_i46831_1_, Class<T> p_i46831_2_) {
         this.name = p_i46831_1_;
         this.propertyClass = p_i46831_2_;
      }

      public ResourceLocation getName() {
         return this.name;
      }

      public Class<T> getPropertyClass() {
         return this.propertyClass;
      }

      public abstract JsonElement serialize(T p_186650_1_, JsonSerializationContext p_186650_2_);

      public abstract T deserialize(JsonElement p_186652_1_, JsonDeserializationContext p_186652_2_);
   }
}
