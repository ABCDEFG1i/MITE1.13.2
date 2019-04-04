package net.minecraft.scoreboard;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ScorePlayerTeam extends Team {
   private final Scoreboard scoreboard;
   private final String name;
   private final Set<String> membershipSet = Sets.newHashSet();
   private ITextComponent displayName;
   private ITextComponent prefix = new TextComponentString("");
   private ITextComponent suffix = new TextComponentString("");
   private boolean allowFriendlyFire = true;
   private boolean canSeeFriendlyInvisibles = true;
   private Team.EnumVisible nameTagVisibility = Team.EnumVisible.ALWAYS;
   private Team.EnumVisible deathMessageVisibility = Team.EnumVisible.ALWAYS;
   private TextFormatting color = TextFormatting.RESET;
   private Team.CollisionRule collisionRule = Team.CollisionRule.ALWAYS;

   public ScorePlayerTeam(Scoreboard p_i2308_1_, String p_i2308_2_) {
      this.scoreboard = p_i2308_1_;
      this.name = p_i2308_2_;
      this.displayName = new TextComponentString(p_i2308_2_);
   }

   public String getName() {
      return this.name;
   }

   public ITextComponent getDisplayName() {
      return this.displayName;
   }

   public ITextComponent getCommandName() {
      ITextComponent itextcomponent = TextComponentUtils.wrapInSquareBrackets(this.displayName.func_212638_h().applyTextStyle((p_211543_1_) -> {
         p_211543_1_.setInsertion(this.name).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(this.name)));
      }));
      TextFormatting textformatting = this.getColor();
      if (textformatting != TextFormatting.RESET) {
         itextcomponent.applyTextStyle(textformatting);
      }

      return itextcomponent;
   }

   public void setDisplayName(ITextComponent p_96664_1_) {
      if (p_96664_1_ == null) {
         throw new IllegalArgumentException("Name cannot be null");
      } else {
         this.displayName = p_96664_1_;
         this.scoreboard.broadcastTeamInfoUpdate(this);
      }
   }

   public void setPrefix(@Nullable ITextComponent p_207408_1_) {
      this.prefix = p_207408_1_ == null ? new TextComponentString("") : p_207408_1_.func_212638_h();
      this.scoreboard.broadcastTeamInfoUpdate(this);
   }

   public ITextComponent getPrefix() {
      return this.prefix;
   }

   public void setSuffix(@Nullable ITextComponent p_207409_1_) {
      this.suffix = p_207409_1_ == null ? new TextComponentString("") : p_207409_1_.func_212638_h();
      this.scoreboard.broadcastTeamInfoUpdate(this);
   }

   public ITextComponent getSuffix() {
      return this.suffix;
   }

   public Collection<String> getMembershipCollection() {
      return this.membershipSet;
   }

   public ITextComponent format(ITextComponent p_200540_1_) {
      ITextComponent itextcomponent = (new TextComponentString("")).appendSibling(this.prefix).appendSibling(p_200540_1_).appendSibling(this.suffix);
      TextFormatting textformatting = this.getColor();
      if (textformatting != TextFormatting.RESET) {
         itextcomponent.applyTextStyle(textformatting);
      }

      return itextcomponent;
   }

   public static ITextComponent formatMemberName(@Nullable Team p_200541_0_, ITextComponent p_200541_1_) {
      return p_200541_0_ == null ? p_200541_1_.func_212638_h() : p_200541_0_.format(p_200541_1_);
   }

   public boolean getAllowFriendlyFire() {
      return this.allowFriendlyFire;
   }

   public void setAllowFriendlyFire(boolean p_96660_1_) {
      this.allowFriendlyFire = p_96660_1_;
      this.scoreboard.broadcastTeamInfoUpdate(this);
   }

   public boolean getSeeFriendlyInvisiblesEnabled() {
      return this.canSeeFriendlyInvisibles;
   }

   public void setSeeFriendlyInvisiblesEnabled(boolean p_98300_1_) {
      this.canSeeFriendlyInvisibles = p_98300_1_;
      this.scoreboard.broadcastTeamInfoUpdate(this);
   }

   public Team.EnumVisible getNameTagVisibility() {
      return this.nameTagVisibility;
   }

   public Team.EnumVisible getDeathMessageVisibility() {
      return this.deathMessageVisibility;
   }

   public void setNameTagVisibility(Team.EnumVisible p_178772_1_) {
      this.nameTagVisibility = p_178772_1_;
      this.scoreboard.broadcastTeamInfoUpdate(this);
   }

   public void setDeathMessageVisibility(Team.EnumVisible p_178773_1_) {
      this.deathMessageVisibility = p_178773_1_;
      this.scoreboard.broadcastTeamInfoUpdate(this);
   }

   public Team.CollisionRule getCollisionRule() {
      return this.collisionRule;
   }

   public void setCollisionRule(Team.CollisionRule p_186682_1_) {
      this.collisionRule = p_186682_1_;
      this.scoreboard.broadcastTeamInfoUpdate(this);
   }

   public int getFriendlyFlags() {
      int i = 0;
      if (this.getAllowFriendlyFire()) {
         i |= 1;
      }

      if (this.getSeeFriendlyInvisiblesEnabled()) {
         i |= 2;
      }

      return i;
   }

   @OnlyIn(Dist.CLIENT)
   public void setFriendlyFlags(int p_98298_1_) {
      this.setAllowFriendlyFire((p_98298_1_ & 1) > 0);
      this.setSeeFriendlyInvisiblesEnabled((p_98298_1_ & 2) > 0);
   }

   public void setColor(TextFormatting p_178774_1_) {
      this.color = p_178774_1_;
      this.scoreboard.broadcastTeamInfoUpdate(this);
   }

   public TextFormatting getColor() {
      return this.color;
   }
}
