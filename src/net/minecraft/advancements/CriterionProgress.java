package net.minecraft.advancements;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.network.PacketBuffer;

public class CriterionProgress {
   private static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
   private Date obtained;

   public boolean isObtained() {
      return this.obtained != null;
   }

   public void obtain() {
      this.obtained = new Date();
   }

   public void reset() {
      this.obtained = null;
   }

   public Date getObtained() {
      return this.obtained;
   }

   public String toString() {
      return "CriterionProgress{obtained=" + (this.obtained == null ? "false" : this.obtained) + '}';
   }

   public void write(PacketBuffer p_192150_1_) {
      p_192150_1_.writeBoolean(this.obtained != null);
      if (this.obtained != null) {
         p_192150_1_.writeTime(this.obtained);
      }

   }

   public JsonElement serialize() {
      return this.obtained != null ? new JsonPrimitive(DATE_TIME_FORMATTER.format(this.obtained)) : JsonNull.INSTANCE;
   }

   public static CriterionProgress read(PacketBuffer p_192149_0_) {
      CriterionProgress criterionprogress = new CriterionProgress();
      if (p_192149_0_.readBoolean()) {
         criterionprogress.obtained = p_192149_0_.readTime();
      }

      return criterionprogress;
   }

   public static CriterionProgress func_209541_a(String p_209541_0_) {
      CriterionProgress criterionprogress = new CriterionProgress();

      try {
         criterionprogress.obtained = DATE_TIME_FORMATTER.parse(p_209541_0_);
         return criterionprogress;
      } catch (ParseException parseexception) {
         throw new JsonSyntaxException("Invalid datetime: " + p_209541_0_, parseexception);
      }
   }
}
