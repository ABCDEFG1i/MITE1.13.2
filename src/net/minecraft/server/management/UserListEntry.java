package net.minecraft.server.management;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public class UserListEntry<T> {
   @Nullable
   private final T value;

   public UserListEntry(T p_i1146_1_) {
      this.value = p_i1146_1_;
   }

   protected UserListEntry(@Nullable T p_i1147_1_, JsonObject p_i1147_2_) {
      this.value = p_i1147_1_;
   }

   @Nullable
   T getValue() {
      return this.value;
   }

   boolean hasBanExpired() {
      return false;
   }

   protected void onSerialization(JsonObject p_152641_1_) {
   }
}
