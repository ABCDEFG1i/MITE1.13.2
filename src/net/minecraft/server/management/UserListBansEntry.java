package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class UserListBansEntry extends UserListEntryBan<GameProfile> {
   public UserListBansEntry(GameProfile p_i1134_1_) {
      this(p_i1134_1_, (Date)null, (String)null, (Date)null, (String)null);
   }

   public UserListBansEntry(GameProfile p_i1135_1_, @Nullable Date p_i1135_2_, @Nullable String p_i1135_3_, @Nullable Date p_i1135_4_, @Nullable String p_i1135_5_) {
      super(p_i1135_1_, p_i1135_4_, p_i1135_3_, p_i1135_4_, p_i1135_5_);
   }

   public UserListBansEntry(JsonObject p_i1136_1_) {
      super(toGameProfile(p_i1136_1_), p_i1136_1_);
   }

   protected void onSerialization(JsonObject p_152641_1_) {
      if (this.getValue() != null) {
         p_152641_1_.addProperty("uuid", this.getValue().getId() == null ? "" : this.getValue().getId().toString());
         p_152641_1_.addProperty("name", this.getValue().getName());
         super.onSerialization(p_152641_1_);
      }
   }

   public ITextComponent func_199041_e() {
      GameProfile gameprofile = this.getValue();
      return new TextComponentString(gameprofile.getName() != null ? gameprofile.getName() : Objects.toString(gameprofile.getId(), "(Unknown)"));
   }

   private static GameProfile toGameProfile(JsonObject p_152648_0_) {
      if (p_152648_0_.has("uuid") && p_152648_0_.has("name")) {
         String s = p_152648_0_.get("uuid").getAsString();

         UUID uuid;
         try {
            uuid = UUID.fromString(s);
         } catch (Throwable var4) {
            return null;
         }

         return new GameProfile(uuid, p_152648_0_.get("name").getAsString());
      } else {
         return null;
      }
   }
}
