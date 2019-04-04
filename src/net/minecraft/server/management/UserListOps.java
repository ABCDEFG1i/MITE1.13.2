package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class UserListOps extends UserList<GameProfile, UserListOpsEntry> {
   public UserListOps(File p_i1152_1_) {
      super(p_i1152_1_);
   }

   protected UserListEntry<GameProfile> createEntry(JsonObject p_152682_1_) {
      return new UserListOpsEntry(p_152682_1_);
   }

   public String[] getKeys() {
      String[] astring = new String[this.func_199043_f().size()];
      int i = 0;

      for(UserListEntry<GameProfile> userlistentry : this.func_199043_f()) {
         astring[i++] = userlistentry.getValue().getName();
      }

      return astring;
   }

   public boolean bypassesPlayerLimit(GameProfile p_183026_1_) {
      UserListOpsEntry userlistopsentry = this.getEntry(p_183026_1_);
      return userlistopsentry != null && userlistopsentry.bypassesPlayerLimit();
   }

   protected String getObjectKey(GameProfile p_152681_1_) {
      return p_152681_1_.getId().toString();
   }
}
