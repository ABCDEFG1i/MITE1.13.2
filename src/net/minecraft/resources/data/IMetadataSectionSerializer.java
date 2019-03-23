package net.minecraft.resources.data;

import com.google.gson.JsonObject;

public interface IMetadataSectionSerializer<T> {
   String getSectionName();

   T deserialize(JsonObject p_195812_1_);
}
