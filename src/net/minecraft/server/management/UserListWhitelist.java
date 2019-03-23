package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class UserListWhitelist extends UserList<GameProfile, UserListWhitelistEntry> {
   public UserListWhitelist(File p_i1132_1_) {
      super(p_i1132_1_);
   }

   protected UserListEntry<GameProfile> createEntry(JsonObject p_152682_1_) {
      return new UserListWhitelistEntry(p_152682_1_);
   }

   public boolean isWhitelisted(GameProfile p_152705_1_) {
      return this.hasEntry(p_152705_1_);
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
