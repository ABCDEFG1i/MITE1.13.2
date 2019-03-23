package net.minecraft.client.renderer.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockPart {
   public final Vector3f field_178241_a;
   public final Vector3f field_178239_b;
   public final Map<EnumFacing, BlockPartFace> field_178240_c;
   public final BlockPartRotation field_178237_d;
   public final boolean field_178238_e;

   public BlockPart(Vector3f p_i47624_1_, Vector3f p_i47624_2_, Map<EnumFacing, BlockPartFace> p_i47624_3_, @Nullable BlockPartRotation p_i47624_4_, boolean p_i47624_5_) {
      this.field_178241_a = p_i47624_1_;
      this.field_178239_b = p_i47624_2_;
      this.field_178240_c = p_i47624_3_;
      this.field_178237_d = p_i47624_4_;
      this.field_178238_e = p_i47624_5_;
      this.func_178235_a();
   }

   private void func_178235_a() {
      for(Entry<EnumFacing, BlockPartFace> entry : this.field_178240_c.entrySet()) {
         float[] afloat = this.func_178236_a(entry.getKey());
         (entry.getValue()).field_178243_e.func_178349_a(afloat);
      }

   }

   private float[] func_178236_a(EnumFacing p_178236_1_) {
      switch(p_178236_1_) {
      case DOWN:
         return new float[]{this.field_178241_a.getX(), 16.0F - this.field_178239_b.getZ(), this.field_178239_b.getX(), 16.0F - this.field_178241_a.getZ()};
      case UP:
         return new float[]{this.field_178241_a.getX(), this.field_178241_a.getZ(), this.field_178239_b.getX(), this.field_178239_b.getZ()};
      case NORTH:
      default:
         return new float[]{16.0F - this.field_178239_b.getX(), 16.0F - this.field_178239_b.getY(), 16.0F - this.field_178241_a.getX(), 16.0F - this.field_178241_a.getY()};
      case SOUTH:
         return new float[]{this.field_178241_a.getX(), 16.0F - this.field_178239_b.getY(), this.field_178239_b.getX(), 16.0F - this.field_178241_a.getY()};
      case WEST:
         return new float[]{this.field_178241_a.getZ(), 16.0F - this.field_178239_b.getY(), this.field_178239_b.getZ(), 16.0F - this.field_178241_a.getY()};
      case EAST:
         return new float[]{16.0F - this.field_178239_b.getZ(), 16.0F - this.field_178239_b.getY(), 16.0F - this.field_178241_a.getZ(), 16.0F - this.field_178241_a.getY()};
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class Deserializer implements JsonDeserializer<BlockPart> {
      public BlockPart deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
         Vector3f vector3f = this.func_199330_e(jsonobject);
         Vector3f vector3f1 = this.func_199329_d(jsonobject);
         BlockPartRotation blockpartrotation = this.func_178256_a(jsonobject);
         Map<EnumFacing, BlockPartFace> map = this.func_178250_a(p_deserialize_3_, jsonobject);
         if (jsonobject.has("shade") && !JsonUtils.isBoolean(jsonobject, "shade")) {
            throw new JsonParseException("Expected shade to be a Boolean");
         } else {
            boolean flag = JsonUtils.getBoolean(jsonobject, "shade", true);
            return new BlockPart(vector3f, vector3f1, map, blockpartrotation, flag);
         }
      }

      @Nullable
      private BlockPartRotation func_178256_a(JsonObject p_178256_1_) {
         BlockPartRotation blockpartrotation = null;
         if (p_178256_1_.has("rotation")) {
            JsonObject jsonobject = JsonUtils.getJsonObject(p_178256_1_, "rotation");
            Vector3f vector3f = this.func_199328_a(jsonobject, "origin");
            vector3f.mul(0.0625F);
            EnumFacing.Axis enumfacing$axis = this.func_178252_c(jsonobject);
            float f = this.func_178255_b(jsonobject);
            boolean flag = JsonUtils.getBoolean(jsonobject, "rescale", false);
            blockpartrotation = new BlockPartRotation(vector3f, enumfacing$axis, f, flag);
         }

         return blockpartrotation;
      }

      private float func_178255_b(JsonObject p_178255_1_) {
         float f = JsonUtils.getFloat(p_178255_1_, "angle");
         if (f != 0.0F && MathHelper.abs(f) != 22.5F && MathHelper.abs(f) != 45.0F) {
            throw new JsonParseException("Invalid rotation " + f + " found, only -45/-22.5/0/22.5/45 allowed");
         } else {
            return f;
         }
      }

      private EnumFacing.Axis func_178252_c(JsonObject p_178252_1_) {
         String s = JsonUtils.getString(p_178252_1_, "axis");
         EnumFacing.Axis enumfacing$axis = EnumFacing.Axis.byName(s.toLowerCase(Locale.ROOT));
         if (enumfacing$axis == null) {
            throw new JsonParseException("Invalid rotation axis: " + s);
         } else {
            return enumfacing$axis;
         }
      }

      private Map<EnumFacing, BlockPartFace> func_178250_a(JsonDeserializationContext p_178250_1_, JsonObject p_178250_2_) {
         Map<EnumFacing, BlockPartFace> map = this.func_178253_b(p_178250_1_, p_178250_2_);
         if (map.isEmpty()) {
            throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
         } else {
            return map;
         }
      }

      private Map<EnumFacing, BlockPartFace> func_178253_b(JsonDeserializationContext p_178253_1_, JsonObject p_178253_2_) {
         Map<EnumFacing, BlockPartFace> map = Maps.newEnumMap(EnumFacing.class);
         JsonObject jsonobject = JsonUtils.getJsonObject(p_178253_2_, "faces");

         for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
            EnumFacing enumfacing = this.func_178248_a(entry.getKey());
            map.put(enumfacing, p_178253_1_.deserialize(entry.getValue(), BlockPartFace.class));
         }

         return map;
      }

      private EnumFacing func_178248_a(String p_178248_1_) {
         EnumFacing enumfacing = EnumFacing.byName(p_178248_1_);
         if (enumfacing == null) {
            throw new JsonParseException("Unknown facing: " + p_178248_1_);
         } else {
            return enumfacing;
         }
      }

      private Vector3f func_199329_d(JsonObject p_199329_1_) {
         Vector3f vector3f = this.func_199328_a(p_199329_1_, "to");
         if (!(vector3f.getX() < -16.0F) && !(vector3f.getY() < -16.0F) && !(vector3f.getZ() < -16.0F) && !(vector3f.getX() > 32.0F) && !(vector3f.getY() > 32.0F) && !(vector3f.getZ() > 32.0F)) {
            return vector3f;
         } else {
            throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + vector3f);
         }
      }

      private Vector3f func_199330_e(JsonObject p_199330_1_) {
         Vector3f vector3f = this.func_199328_a(p_199330_1_, "from");
         if (!(vector3f.getX() < -16.0F) && !(vector3f.getY() < -16.0F) && !(vector3f.getZ() < -16.0F) && !(vector3f.getX() > 32.0F) && !(vector3f.getY() > 32.0F) && !(vector3f.getZ() > 32.0F)) {
            return vector3f;
         } else {
            throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + vector3f);
         }
      }

      private Vector3f func_199328_a(JsonObject p_199328_1_, String p_199328_2_) {
         JsonArray jsonarray = JsonUtils.getJsonArray(p_199328_1_, p_199328_2_);
         if (jsonarray.size() != 3) {
            throw new JsonParseException("Expected 3 " + p_199328_2_ + " values, found: " + jsonarray.size());
         } else {
            float[] afloat = new float[3];

            for(int i = 0; i < afloat.length; ++i) {
               afloat[i] = JsonUtils.getFloat(jsonarray.get(i), p_199328_2_ + "[" + i + "]");
            }

            return new Vector3f(afloat[0], afloat[1], afloat[2]);
         }
      }
   }
}
