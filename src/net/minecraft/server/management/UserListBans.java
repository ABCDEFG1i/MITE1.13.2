package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class UserListBans extends UserList<GameProfile, UserListBansEntry> {
   public UserListBans(File p_i1138_1_) {
      super(p_i1138_1_);
   }

   protected UserListEntry<GameProfile> createEntry(JsonObject p_152682_1_) {
      return new UserListBansEntry(p_152682_1_);
   }

   public boolean isBanned(GameProfile p_152702_1_) {
      return this.hasEntry(p_152702_1_);
   }

   public String[] getKeys() {
      String[] astring = new String[this.func_199043_f().size()];
      int i = 0;

      for(UserListEntry<GameProfile> userlistentry : this.func_199043_f()) {
         astring[i++] = userlistentry.getValue().getName();
      }

      return astring;
   }

   protected String getObjectKey(GameProfile p_152681_1_) {
      return p_152681_1_.getId().toString();
   }
}
