package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;

public abstract class UserListEntryBan<T> extends UserListEntry<T> {
   public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
   protected final Date banStartDate;
   protected final String bannedBy;
   protected final Date banEndDate;
   protected final String reason;

   public UserListEntryBan(T p_i46334_1_, @Nullable Date p_i46334_2_, @Nullable String p_i46334_3_, @Nullable Date p_i46334_4_, @Nullable String p_i46334_5_) {
      super(p_i46334_1_);
      this.banStartDate = p_i46334_2_ == null ? new Date() : p_i46334_2_;
      this.bannedBy = p_i46334_3_ == null ? "(Unknown)" : p_i46334_3_;
      this.banEndDate = p_i46334_4_;
      this.reason = p_i46334_5_ == null ? "Banned by an operator." : p_i46334_5_;
   }

   protected UserListEntryBan(T p_i1174_1_, JsonObject p_i1174_2_) {
      super(p_i1174_1_, p_i1174_2_);

      Date date;
      try {
         date = p_i1174_2_.has("created") ? DATE_FORMAT.parse(p_i1174_2_.get("created").getAsString()) : new Date();
      } catch (ParseException var7) {
         date = new Date();
      }

      this.banStartDate = date;
      this.bannedBy = p_i1174_2_.has("source") ? p_i1174_2_.get("source").getAsString() : "(Unknown)";

      Date date1;
      try {
         date1 = p_i1174_2_.has("expires") ? DATE_FORMAT.parse(p_i1174_2_.get("expires").getAsString()) : null;
      } catch (ParseException var6) {
         date1 = null;
      }

      this.banEndDate = date1;
      this.reason = p_i1174_2_.has("reason") ? p_i1174_2_.get("reason").getAsString() : "Banned by an operator.";
   }

   public String func_199040_b() {
      return this.bannedBy;
   }

   public Date getBanEndDate() {
      return this.banEndDate;
   }

   public String getBanReason() {
      return this.reason;
   }

   public abstract ITextComponent func_199041_e();

   boolean hasBanExpired() {
      return this.banEndDate == null ? false : this.banEndDate.before(new Date());
   }

   protected void onSerialization(JsonObject p_152641_1_) {
      p_152641_1_.addProperty("created", DATE_FORMAT.format(this.banStartDate));
      p_152641_1_.addProperty("source", this.bannedBy);
      p_152641_1_.addProperty("expires", this.banEndDate == null ? "forever" : DATE_FORMAT.format(this.banEndDate));
      p_152641_1_.addProperty("reason", this.reason);
   }
}
