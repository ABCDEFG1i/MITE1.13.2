package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class UserListIPBansEntry extends UserListEntryBan<String> {
   public UserListIPBansEntry(String p_i46330_1_) {
      this(p_i46330_1_, null, null, null, null);
   }

   public UserListIPBansEntry(String p_i1159_1_, @Nullable Date p_i1159_2_, @Nullable String p_i1159_3_, @Nullable Date p_i1159_4_, @Nullable String p_i1159_5_) {
      super(p_i1159_1_, p_i1159_2_, p_i1159_3_, p_i1159_4_, p_i1159_5_);
   }

   public ITextComponent func_199041_e() {
      return new TextComponentString(this.getValue());
   }

   public UserListIPBansEntry(JsonObject p_i46331_1_) {
      super(getIPFromJson(p_i46331_1_), p_i46331_1_);
   }

   private static String getIPFromJson(JsonObject p_152647_0_) {
      return p_152647_0_.has("ip") ? p_152647_0_.get("ip").getAsString() : null;
   }

   protected void onSerialization(JsonObject p_152641_1_) {
      if (this.getValue() != null) {
         p_152641_1_.addProperty("ip", this.getValue());
         super.onSerialization(p_152641_1_);
      }
   }
}
