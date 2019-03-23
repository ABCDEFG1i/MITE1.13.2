package net.minecraft.client.renderer.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemTransformVec3f {
   public static final ItemTransformVec3f field_178366_a = new ItemTransformVec3f(new Vector3f(), new Vector3f(), new Vector3f(1.0F, 1.0F, 1.0F));
   public final Vector3f field_178364_b;
   public final Vector3f field_178365_c;
   public final Vector3f field_178363_d;

   public ItemTransformVec3f(Vector3f p_i47622_1_, Vector3f p_i47622_2_, Vector3f p_i47622_3_) {
      this.field_178364_b = new Vector3f(p_i47622_1_);
      this.field_178365_c = new Vector3f(p_i47622_2_);
      this.field_178363_d = new Vector3f(p_i47622_3_);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (this.getClass() != p_equals_1_.getClass()) {
         return false;
      } else {
         ItemTransformVec3f itemtransformvec3f = (ItemTransformVec3f)p_equals_1_;
         return this.field_178364_b.equals(itemtransformvec3f.field_178364_b) && this.field_178363_d.equals(itemtransformvec3f.field_178363_d) && this.field_178365_c.equals(itemtransformvec3f.field_178365_c);
      }
   }

   public int hashCode() {
      int i = this.field_178364_b.hashCode();
      i = 31 * i + this.field_178365_c.hashCode();
      i = 31 * i + this.field_178363_d.hashCode();
      return i;
   }

   @OnlyIn(Dist.CLIENT)
   static class Deserializer implements JsonDeserializer<ItemTransformVec3f> {
      private static final Vector3f field_178362_a = new Vector3f(0.0F, 0.0F, 0.0F);
      private static final Vector3f field_178360_b = new Vector3f(0.0F, 0.0F, 0.0F);
      private static final Vector3f field_178361_c = new Vector3f(1.0F, 1.0F, 1.0F);

      public ItemTransformVec3f deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
         Vector3f vector3f = this.func_199340_a(jsonobject, "rotation", field_178362_a);
         Vector3f vector3f1 = this.func_199340_a(jsonobject, "translation", field_178360_b);
         vector3f1.mul(0.0625F);
         vector3f1.clamp(-5.0F, 5.0F);
         Vector3f vector3f2 = this.func_199340_a(jsonobject, "scale", field_178361_c);
         vector3f2.clamp(-4.0F, 4.0F);
         return new ItemTransformVec3f(vector3f, vector3f1, vector3f2);
      }

      private Vector3f func_199340_a(JsonObject p_199340_1_, String p_199340_2_, Vector3f p_199340_3_) {
         if (!p_199340_1_.has(p_199340_2_)) {
            return p_199340_3_;
         } else {
            JsonArray jsonarray = JsonUtils.getJsonArray(p_199340_1_, p_199340_2_);
            if (jsonarray.size() != 3) {
               throw new JsonParseException("Expected 3 " + p_199340_2_ + " values, found: " + jsonarray.size());
            } else {
               float[] afloat = new float[3];

               for(int i = 0; i < afloat.length; ++i) {
                  afloat[i] = JsonUtils.getFloat(jsonarray.get(i), p_199340_2_ + "[" + i + "]");
               }

               return new Vector3f(afloat[0], afloat[1], afloat[2]);
            }
         }
      }
   }
}
