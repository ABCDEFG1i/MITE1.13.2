package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketTeams implements Packet<INetHandlerPlayClient> {
   private String name = "";
   private ITextComponent displayName = new TextComponentString("");
   private ITextComponent prefix = new TextComponentString("");
   private ITextComponent suffix = new TextComponentString("");
   private String nameTagVisibility;
   private String collisionRule;
   private TextFormatting color;
   private final Collection<String> players;
   private int action;
   private int friendlyFlags;

   public SPacketTeams() {
      this.nameTagVisibility = Team.EnumVisible.ALWAYS.internalName;
      this.collisionRule = Team.CollisionRule.ALWAYS.name;
      this.color = TextFormatting.RESET;
      this.players = Lists.newArrayList();
   }

   public SPacketTeams(ScorePlayerTeam p_i46907_1_, int p_i46907_2_) {
      this.nameTagVisibility = Team.EnumVisible.ALWAYS.internalName;
      this.collisionRule = Team.CollisionRule.ALWAYS.name;
      this.color = TextFormatting.RESET;
      this.players = Lists.newArrayList();
      this.name = p_i46907_1_.getName();
      this.action = p_i46907_2_;
      if (p_i46907_2_ == 0 || p_i46907_2_ == 2) {
         this.displayName = p_i46907_1_.getDisplayName();
         this.friendlyFlags = p_i46907_1_.getFriendlyFlags();
         this.nameTagVisibility = p_i46907_1_.getNameTagVisibility().internalName;
         this.collisionRule = p_i46907_1_.getCollisionRule().name;
         this.color = p_i46907_1_.getColor();
         this.prefix = p_i46907_1_.getPrefix();
         this.suffix = p_i46907_1_.getSuffix();
      }

      if (p_i46907_2_ == 0) {
         this.players.addAll(p_i46907_1_.getMembershipCollection());
      }

   }

   public SPacketTeams(ScorePlayerTeam p_i46908_1_, Collection<String> p_i46908_2_, int p_i46908_3_) {
      this.nameTagVisibility = Team.EnumVisible.ALWAYS.internalName;
      this.collisionRule = Team.CollisionRule.ALWAYS.name;
      this.color = TextFormatting.RESET;
      this.players = Lists.newArrayList();
      if (p_i46908_3_ != 3 && p_i46908_3_ != 4) {
         throw new IllegalArgumentException("Method must be join or leave for player constructor");
      } else if (p_i46908_2_ != null && !p_i46908_2_.isEmpty()) {
         this.action = p_i46908_3_;
         this.name = p_i46908_1_.getName();
         this.players.addAll(p_i46908_2_);
      } else {
         throw new IllegalArgumentException("Players cannot be null/empty");
      }
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.name = p_148837_1_.readString(16);
      this.action = p_148837_1_.readByte();
      if (this.action == 0 || this.action == 2) {
         this.displayName = p_148837_1_.readTextComponent();
         this.friendlyFlags = p_148837_1_.readByte();
         this.nameTagVisibility = p_148837_1_.readString(40);
         this.collisionRule = p_148837_1_.readString(40);
         this.color = p_148837_1_.readEnumValue(TextFormatting.class);
         this.prefix = p_148837_1_.readTextComponent();
         this.suffix = p_148837_1_.readTextComponent();
      }

      if (this.action == 0 || this.action == 3 || this.action == 4) {
         int i = p_148837_1_.readVarInt();

         for(int j = 0; j < i; ++j) {
            this.players.add(p_148837_1_.readString(40));
         }
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeString(this.name);
      p_148840_1_.writeByte(this.action);
      if (this.action == 0 || this.action == 2) {
         p_148840_1_.writeTextComponent(this.displayName);
         p_148840_1_.writeByte(this.friendlyFlags);
         p_148840_1_.writeString(this.nameTagVisibility);
         p_148840_1_.writeString(this.collisionRule);
         p_148840_1_.writeEnumValue(this.color);
         p_148840_1_.writeTextComponent(this.prefix);
         p_148840_1_.writeTextComponent(this.suffix);
      }

      if (this.action == 0 || this.action == 3 || this.action == 4) {
         p_148840_1_.writeVarInt(this.players.size());

         for(String s : this.players) {
            p_148840_1_.writeString(s);
         }
      }

   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleTeams(this);
   }

   @OnlyIn(Dist.CLIENT)
   public String getName() {
      return this.name;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getDisplayName() {
      return this.displayName;
   }

   @OnlyIn(Dist.CLIENT)
   public Collection<String> getPlayers() {
      return this.players;
   }

   @OnlyIn(Dist.CLIENT)
   public int getAction() {
      return this.action;
   }

   @OnlyIn(Dist.CLIENT)
   public int getFriendlyFlags() {
      return this.friendlyFlags;
   }

   @OnlyIn(Dist.CLIENT)
   public TextFormatting func_200537_f() {
      return this.color;
   }

   @OnlyIn(Dist.CLIENT)
   public String getNameTagVisibility() {
      return this.nameTagVisibility;
   }

   @OnlyIn(Dist.CLIENT)
   public String getCollisionRule() {
      return this.collisionRule;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getPrefix() {
      return this.prefix;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getSuffix() {
      return this.suffix;
   }
}
