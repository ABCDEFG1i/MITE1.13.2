package net.minecraft.network.play.server;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketPlayerListItem implements Packet<INetHandlerPlayClient> {
   private SPacketPlayerListItem.Action action;
   private final List<SPacketPlayerListItem.AddPlayerData> players = Lists.newArrayList();

   public SPacketPlayerListItem() {
   }

   public SPacketPlayerListItem(SPacketPlayerListItem.Action p_i46929_1_, EntityPlayerMP... p_i46929_2_) {
      this.action = p_i46929_1_;

      for(EntityPlayerMP entityplayermp : p_i46929_2_) {
         this.players.add(new SPacketPlayerListItem.AddPlayerData(entityplayermp.getGameProfile(), entityplayermp.ping, entityplayermp.interactionManager.getGameType(), entityplayermp.getTabListDisplayName()));
      }

   }

   public SPacketPlayerListItem(SPacketPlayerListItem.Action p_i46930_1_, Iterable<EntityPlayerMP> p_i46930_2_) {
      this.action = p_i46930_1_;

      for(EntityPlayerMP entityplayermp : p_i46930_2_) {
         this.players.add(new SPacketPlayerListItem.AddPlayerData(entityplayermp.getGameProfile(), entityplayermp.ping, entityplayermp.interactionManager.getGameType(), entityplayermp.getTabListDisplayName()));
      }

   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.action = p_148837_1_.readEnumValue(SPacketPlayerListItem.Action.class);
      int i = p_148837_1_.readVarInt();

      for(int j = 0; j < i; ++j) {
         GameProfile gameprofile = null;
         int k = 0;
         GameType gametype = null;
         ITextComponent itextcomponent = null;
         switch(this.action) {
         case ADD_PLAYER:
            gameprofile = new GameProfile(p_148837_1_.readUniqueId(), p_148837_1_.readString(16));
            int l = p_148837_1_.readVarInt();
            int i1 = 0;

            for(; i1 < l; ++i1) {
               String s = p_148837_1_.readString(32767);
               String s1 = p_148837_1_.readString(32767);
               if (p_148837_1_.readBoolean()) {
                  gameprofile.getProperties().put(s, new Property(s, s1, p_148837_1_.readString(32767)));
               } else {
                  gameprofile.getProperties().put(s, new Property(s, s1));
               }
            }

            gametype = GameType.getByID(p_148837_1_.readVarInt());
            k = p_148837_1_.readVarInt();
            if (p_148837_1_.readBoolean()) {
               itextcomponent = p_148837_1_.readTextComponent();
            }
            break;
         case UPDATE_GAME_MODE:
            gameprofile = new GameProfile(p_148837_1_.readUniqueId(), null);
            gametype = GameType.getByID(p_148837_1_.readVarInt());
            break;
         case UPDATE_LATENCY:
            gameprofile = new GameProfile(p_148837_1_.readUniqueId(), null);
            k = p_148837_1_.readVarInt();
            break;
         case UPDATE_DISPLAY_NAME:
            gameprofile = new GameProfile(p_148837_1_.readUniqueId(), null);
            if (p_148837_1_.readBoolean()) {
               itextcomponent = p_148837_1_.readTextComponent();
            }
            break;
         case REMOVE_PLAYER:
            gameprofile = new GameProfile(p_148837_1_.readUniqueId(), null);
         }

         this.players.add(new SPacketPlayerListItem.AddPlayerData(gameprofile, k, gametype, itextcomponent));
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnumValue(this.action);
      p_148840_1_.writeVarInt(this.players.size());

      for(SPacketPlayerListItem.AddPlayerData spacketplayerlistitem$addplayerdata : this.players) {
         switch(this.action) {
         case ADD_PLAYER:
            p_148840_1_.writeUniqueId(spacketplayerlistitem$addplayerdata.getProfile().getId());
            p_148840_1_.writeString(spacketplayerlistitem$addplayerdata.getProfile().getName());
            p_148840_1_.writeVarInt(spacketplayerlistitem$addplayerdata.getProfile().getProperties().size());

            for(Property property : spacketplayerlistitem$addplayerdata.getProfile().getProperties().values()) {
               p_148840_1_.writeString(property.getName());
               p_148840_1_.writeString(property.getValue());
               if (property.hasSignature()) {
                  p_148840_1_.writeBoolean(true);
                  p_148840_1_.writeString(property.getSignature());
               } else {
                  p_148840_1_.writeBoolean(false);
               }
            }

            p_148840_1_.writeVarInt(spacketplayerlistitem$addplayerdata.getGameMode().getID());
            p_148840_1_.writeVarInt(spacketplayerlistitem$addplayerdata.getPing());
            if (spacketplayerlistitem$addplayerdata.getDisplayName() == null) {
               p_148840_1_.writeBoolean(false);
            } else {
               p_148840_1_.writeBoolean(true);
               p_148840_1_.writeTextComponent(spacketplayerlistitem$addplayerdata.getDisplayName());
            }
            break;
         case UPDATE_GAME_MODE:
            p_148840_1_.writeUniqueId(spacketplayerlistitem$addplayerdata.getProfile().getId());
            p_148840_1_.writeVarInt(spacketplayerlistitem$addplayerdata.getGameMode().getID());
            break;
         case UPDATE_LATENCY:
            p_148840_1_.writeUniqueId(spacketplayerlistitem$addplayerdata.getProfile().getId());
            p_148840_1_.writeVarInt(spacketplayerlistitem$addplayerdata.getPing());
            break;
         case UPDATE_DISPLAY_NAME:
            p_148840_1_.writeUniqueId(spacketplayerlistitem$addplayerdata.getProfile().getId());
            if (spacketplayerlistitem$addplayerdata.getDisplayName() == null) {
               p_148840_1_.writeBoolean(false);
            } else {
               p_148840_1_.writeBoolean(true);
               p_148840_1_.writeTextComponent(spacketplayerlistitem$addplayerdata.getDisplayName());
            }
            break;
         case REMOVE_PLAYER:
            p_148840_1_.writeUniqueId(spacketplayerlistitem$addplayerdata.getProfile().getId());
         }
      }

   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handlePlayerListItem(this);
   }

   @OnlyIn(Dist.CLIENT)
   public List<SPacketPlayerListItem.AddPlayerData> getEntries() {
      return this.players;
   }

   @OnlyIn(Dist.CLIENT)
   public SPacketPlayerListItem.Action getAction() {
      return this.action;
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("action", this.action).add("entries", this.players).toString();
   }

   public enum Action {
      ADD_PLAYER,
      UPDATE_GAME_MODE,
      UPDATE_LATENCY,
      UPDATE_DISPLAY_NAME,
      REMOVE_PLAYER
   }

   public class AddPlayerData {
      private final int ping;
      private final GameType gamemode;
      private final GameProfile profile;
      private final ITextComponent displayName;

      public AddPlayerData(GameProfile p_i46663_2_, int p_i46663_3_, @Nullable GameType p_i46663_4_, @Nullable ITextComponent p_i46663_5_) {
         this.profile = p_i46663_2_;
         this.ping = p_i46663_3_;
         this.gamemode = p_i46663_4_;
         this.displayName = p_i46663_5_;
      }

      public GameProfile getProfile() {
         return this.profile;
      }

      public int getPing() {
         return this.ping;
      }

      public GameType getGameMode() {
         return this.gamemode;
      }

      @Nullable
      public ITextComponent getDisplayName() {
         return this.displayName;
      }

      public String toString() {
         return MoreObjects.toStringHelper(this).add("latency", this.ping).add("gameMode", this.gamemode).add("profile", this.profile).add("displayName", this.displayName == null ? null : ITextComponent.Serializer.toJson(this.displayName)).toString();
      }
   }
}
