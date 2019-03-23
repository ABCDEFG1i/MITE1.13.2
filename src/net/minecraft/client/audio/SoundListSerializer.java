package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;

@OnlyIn(Dist.CLIENT)
public class SoundListSerializer implements JsonDeserializer<SoundList> {
   public SoundList deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
      JsonObject jsonobject = JsonUtils.getJsonObject(p_deserialize_1_, "entry");
      boolean flag = JsonUtils.getBoolean(jsonobject, "replace", false);
      String s = JsonUtils.getString(jsonobject, "subtitle", (String)null);
      List<Sound> list = this.deserializeSounds(jsonobject);
      return new SoundList(list, flag, s);
   }

   private List<Sound> deserializeSounds(JsonObject p_188733_1_) {
      List<Sound> list = Lists.newArrayList();
      if (p_188733_1_.has("sounds")) {
         JsonArray jsonarray = JsonUtils.getJsonArray(p_188733_1_, "sounds");

         for(int i = 0; i < jsonarray.size(); ++i) {
            JsonElement jsonelement = jsonarray.get(i);
            if (JsonUtils.isString(jsonelement)) {
               String s = JsonUtils.getString(jsonelement, "sound");
               list.add(new Sound(s, 1.0F, 1.0F, 1, Sound.Type.FILE, false, false, 16));
            } else {
               list.add(this.deserializeSound(JsonUtils.getJsonObject(jsonelement, "sound")));
            }
         }
      }

      return list;
   }

   private Sound deserializeSound(JsonObject p_188734_1_) {
      String s = JsonUtils.getString(p_188734_1_, "name");
      Sound.Type sound$type = this.deserializeType(p_188734_1_, Sound.Type.FILE);
      float f = JsonUtils.getFloat(p_188734_1_, "volume", 1.0F);
      Validate.isTrue(f > 0.0F, "Invalid volume");
      float f1 = JsonUtils.getFloat(p_188734_1_, "pitch", 1.0F);
      Validate.isTrue(f1 > 0.0F, "Invalid pitch");
      int i = JsonUtils.getInt(p_188734_1_, "weight", 1);
      Validate.isTrue(i > 0, "Invalid weight");
      boolean flag = JsonUtils.getBoolean(p_188734_1_, "preload", false);
      boolean flag1 = JsonUtils.getBoolean(p_188734_1_, "stream", false);
      int j = JsonUtils.getInt(p_188734_1_, "attenuation_distance", 16);
      return new Sound(s, f, f1, i, sound$type, flag1, flag, j);
   }

   private Sound.Type deserializeType(JsonObject p_188732_1_, Sound.Type p_188732_2_) {
      Sound.Type sound$type = p_188732_2_;
      if (p_188732_1_.has("type")) {
         sound$type = Sound.Type.getByName(JsonUtils.getString(p_188732_1_, "type"));
         Validate.notNull(sound$type, "Invalid type");
      }

      return sound$type;
   }
}
