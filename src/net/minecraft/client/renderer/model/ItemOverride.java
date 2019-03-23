package net.minecraft.client.renderer.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemOverride {
   private final ResourceLocation field_188028_a;
   private final Map<ResourceLocation, Float> field_188029_b;

   public ItemOverride(ResourceLocation p_i46571_1_, Map<ResourceLocation, Float> p_i46571_2_) {
      this.field_188028_a = p_i46571_1_;
      this.field_188029_b = p_i46571_2_;
   }

   public ResourceLocation func_188026_a() {
      return this.field_188028_a;
   }

   boolean func_188027_a(ItemStack p_188027_1_, @Nullable World p_188027_2_, @Nullable EntityLivingBase p_188027_3_) {
      Item item = p_188027_1_.getItem();

      for(Entry<ResourceLocation, Float> entry : this.field_188029_b.entrySet()) {
         IItemPropertyGetter iitempropertygetter = item.getPropertyGetter(entry.getKey());
         if (iitempropertygetter == null || iitempropertygetter.call(p_188027_1_, p_188027_2_, p_188027_3_) < entry.getValue()) {
            return false;
         }
      }

      return true;
   }

   @OnlyIn(Dist.CLIENT)
   static class Deserializer implements JsonDeserializer<ItemOverride> {
      public ItemOverride deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
         ResourceLocation resourcelocation = new ResourceLocation(JsonUtils.getString(jsonobject, "model"));
         Map<ResourceLocation, Float> map = this.func_188025_a(jsonobject);
         return new ItemOverride(resourcelocation, map);
      }

      protected Map<ResourceLocation, Float> func_188025_a(JsonObject p_188025_1_) {
         Map<ResourceLocation, Float> map = Maps.newLinkedHashMap();
         JsonObject jsonobject = JsonUtils.getJsonObject(p_188025_1_, "predicate");

         for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
            map.put(new ResourceLocation(entry.getKey()), JsonUtils.getFloat(entry.getValue(), entry.getKey()));
         }

         return map;
      }
   }
}
