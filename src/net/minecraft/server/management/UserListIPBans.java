package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.io.File;
import java.net.SocketAddress;

public class UserListIPBans extends UserList<String, UserListIPBansEntry> {
   public UserListIPBans(File p_i1490_1_) {
      super(p_i1490_1_);
   }

   protected UserListEntry<String> createEntry(JsonObject p_152682_1_) {
      return new UserListIPBansEntry(p_152682_1_);
   }

   public boolean isBanned(SocketAddress p_152708_1_) {
      String s = this.addressToString(p_152708_1_);
      return this.hasEntry(s);
   }

   public boolean func_199044_a(String p_199044_1_) {
      return this.hasEntry(p_199044_1_);
   }

   public UserListIPBansEntry getBanEntry(SocketAddress p_152709_1_) {
      String s = this.addressToString(p_152709_1_);
      return this.getEntry(s);
   }

   private String addressToString(SocketAddress p_152707_1_) {
      String s = p_152707_1_.toString();
      if (s.contains("/")) {
         s = s.substring(s.indexOf(47) + 1);
      }

      if (s.contains(":")) {
         s = s.substring(0, s.indexOf(58));
      }

      return s;
   }
}
