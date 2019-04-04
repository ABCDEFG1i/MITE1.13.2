package net.minecraft.scoreboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Team {
   public boolean isSameTeam(@Nullable Team p_142054_1_) {
      if (p_142054_1_ == null) {
         return false;
      } else {
         return this == p_142054_1_;
      }
   }

   public abstract String getName();

   public abstract ITextComponent format(ITextComponent p_200540_1_);

   @OnlyIn(Dist.CLIENT)
   public abstract boolean getSeeFriendlyInvisiblesEnabled();

   public abstract boolean getAllowFriendlyFire();

   @OnlyIn(Dist.CLIENT)
   public abstract Team.EnumVisible getNameTagVisibility();

   public abstract TextFormatting getColor();

   public abstract Collection<String> getMembershipCollection();

   public abstract Team.EnumVisible getDeathMessageVisibility();

   public abstract Team.CollisionRule getCollisionRule();

   public enum CollisionRule {
      ALWAYS("always", 0),
      NEVER("never", 1),
      PUSH_OTHER_TEAMS("pushOtherTeams", 2),
      PUSH_OWN_TEAM("pushOwnTeam", 3);

      private static final Map<String, Team.CollisionRule> nameMap = Arrays.stream(values()).collect(Collectors.toMap((p_199871_0_) -> {
         return p_199871_0_.name;
      }, (p_199870_0_) -> {
         return p_199870_0_;
      }));
      public final String name;
      public final int id;

      @Nullable
      public static Team.CollisionRule getByName(String p_186686_0_) {
         return nameMap.get(p_186686_0_);
      }

      CollisionRule(String p_i47053_3_, int p_i47053_4_) {
         this.name = p_i47053_3_;
         this.id = p_i47053_4_;
      }

      public ITextComponent func_197907_b() {
         return new TextComponentTranslation("team.collision." + this.name);
      }
   }

   public enum EnumVisible {
      ALWAYS("always", 0),
      NEVER("never", 1),
      HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
      HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

      private static final Map<String, Team.EnumVisible> nameMap = Arrays.stream(values()).collect(Collectors.toMap((p_199873_0_) -> {
         return p_199873_0_.internalName;
      }, (p_199872_0_) -> {
         return p_199872_0_;
      }));
      public final String internalName;
      public final int id;

      @Nullable
      public static Team.EnumVisible getByName(String p_178824_0_) {
         return nameMap.get(p_178824_0_);
      }

      EnumVisible(String p_i45550_3_, int p_i45550_4_) {
         this.internalName = p_i45550_3_;
         this.id = p_i45550_4_;
      }

      public ITextComponent func_197910_b() {
         return new TextComponentTranslation("team.visibility." + this.internalName);
      }
   }
}
