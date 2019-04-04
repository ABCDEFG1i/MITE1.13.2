package net.minecraft.util;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Session {
   private final String username;
   private final String playerID;
   private final String token;
   private final Session.Type sessionType;

   public Session(String p_i1098_1_, String p_i1098_2_, String p_i1098_3_, String p_i1098_4_) {
      this.username = p_i1098_1_;
      this.playerID = p_i1098_2_;
      this.token = p_i1098_3_;
      this.sessionType = Session.Type.setSessionType(p_i1098_4_);
   }

   public String getSessionID() {
      return "token:" + this.token + ":" + this.playerID;
   }

   public String getPlayerID() {
      return this.playerID;
   }

   public String getUsername() {
      return this.username;
   }

   public String getToken() {
      return this.token;
   }

   public GameProfile getProfile() {
      try {
         UUID uuid = UUIDTypeAdapter.fromString(this.getPlayerID());
         return new GameProfile(uuid, this.getUsername());
      } catch (IllegalArgumentException var2) {
         return new GameProfile(null, this.getUsername());
      }
   }

   @OnlyIn(Dist.CLIENT)
   public enum Type {
      LEGACY("legacy"),
      MOJANG("mojang");

      private static final Map<String, Session.Type> SESSION_TYPES = Arrays.stream(values()).collect(Collectors.toMap((p_199876_0_) -> {
         return p_199876_0_.sessionType;
      }, Function.identity()));
      private final String sessionType;

      Type(String p_i1096_3_) {
         this.sessionType = p_i1096_3_;
      }

      @Nullable
      public static Session.Type setSessionType(String p_152421_0_) {
         return SESSION_TYPES.get(p_152421_0_.toLowerCase(Locale.ROOT));
      }
   }
}
