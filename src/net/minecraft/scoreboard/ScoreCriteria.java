package net.minecraft.scoreboard;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.stats.StatType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextFormatting;

public class ScoreCriteria {
   public static final Map<String, ScoreCriteria> field_96643_a = Maps.newHashMap();
   public static final ScoreCriteria field_96641_b = new ScoreCriteria("dummy");
   public static final ScoreCriteria field_178791_c = new ScoreCriteria("trigger");
   public static final ScoreCriteria field_96642_c = new ScoreCriteria("deathCount");
   public static final ScoreCriteria field_96639_d = new ScoreCriteria("playerKillCount");
   public static final ScoreCriteria field_96640_e = new ScoreCriteria("totalKillCount");
   public static final ScoreCriteria field_96638_f = new ScoreCriteria("health", true, ScoreCriteria.RenderType.HEARTS);
   public static final ScoreCriteria field_186698_h = new ScoreCriteria("food", true, ScoreCriteria.RenderType.INTEGER);
   public static final ScoreCriteria field_186699_i = new ScoreCriteria("air", true, ScoreCriteria.RenderType.INTEGER);
   public static final ScoreCriteria field_186700_j = new ScoreCriteria("armor", true, ScoreCriteria.RenderType.INTEGER);
   public static final ScoreCriteria field_186701_k = new ScoreCriteria("xp", true, ScoreCriteria.RenderType.INTEGER);
   public static final ScoreCriteria field_186702_l = new ScoreCriteria("level", true, ScoreCriteria.RenderType.INTEGER);
   public static final ScoreCriteria[] field_197913_m = new ScoreCriteria[]{new ScoreCriteria("teamkill." + TextFormatting.BLACK.getFriendlyName()), new ScoreCriteria("teamkill." + TextFormatting.DARK_BLUE.getFriendlyName()), new ScoreCriteria("teamkill." + TextFormatting.DARK_GREEN.getFriendlyName()), new ScoreCriteria("teamkill." + TextFormatting.DARK_AQUA.getFriendlyName()), new ScoreCriteria("teamkill." + TextFormatting.DARK_RED.getFriendlyName()), new ScoreCriteria("teamkill." + TextFormatting.DARK_PURPLE.getFriendlyName()), new ScoreCriteria("teamkill." + TextFormatting.GOLD.getFriendlyName()), new ScoreCriteria("teamkill." + TextFormatting.GRAY.getFriendlyName()), new ScoreCriteria("teamkill." + TextFormatting.DARK_GRAY.getFriendlyName()), new ScoreCriteria("teamkill." + TextFormatting.BLUE.getFriendlyName()), new ScoreCriteria("teamkill." + TextFormatting.GREEN.getFriendlyName()), new ScoreCriteria("teamkill." + TextFormatting.AQUA.getFriendlyName()), new ScoreCriteria("teamkill." + TextFormatting.RED.getFriendlyName()), new ScoreCriteria("teamkill." + TextFormatting.LIGHT_PURPLE.getFriendlyName()), new ScoreCriteria("teamkill." + TextFormatting.YELLOW.getFriendlyName()), new ScoreCriteria("teamkill." + TextFormatting.WHITE.getFriendlyName())};
   public static final ScoreCriteria[] field_197914_n = new ScoreCriteria[]{new ScoreCriteria("killedByTeam." + TextFormatting.BLACK.getFriendlyName()), new ScoreCriteria("killedByTeam." + TextFormatting.DARK_BLUE.getFriendlyName()), new ScoreCriteria("killedByTeam." + TextFormatting.DARK_GREEN.getFriendlyName()), new ScoreCriteria("killedByTeam." + TextFormatting.DARK_AQUA.getFriendlyName()), new ScoreCriteria("killedByTeam." + TextFormatting.DARK_RED.getFriendlyName()), new ScoreCriteria("killedByTeam." + TextFormatting.DARK_PURPLE.getFriendlyName()), new ScoreCriteria("killedByTeam." + TextFormatting.GOLD.getFriendlyName()), new ScoreCriteria("killedByTeam." + TextFormatting.GRAY.getFriendlyName()), new ScoreCriteria("killedByTeam." + TextFormatting.DARK_GRAY.getFriendlyName()), new ScoreCriteria("killedByTeam." + TextFormatting.BLUE.getFriendlyName()), new ScoreCriteria("killedByTeam." + TextFormatting.GREEN.getFriendlyName()), new ScoreCriteria("killedByTeam." + TextFormatting.AQUA.getFriendlyName()), new ScoreCriteria("killedByTeam." + TextFormatting.RED.getFriendlyName()), new ScoreCriteria("killedByTeam." + TextFormatting.LIGHT_PURPLE.getFriendlyName()), new ScoreCriteria("killedByTeam." + TextFormatting.YELLOW.getFriendlyName()), new ScoreCriteria("killedByTeam." + TextFormatting.WHITE.getFriendlyName())};
   private final String field_197915_o;
   private final boolean field_197916_p;
   private final ScoreCriteria.RenderType field_197917_q;

   public ScoreCriteria(String p_i47676_1_) {
      this(p_i47676_1_, false, ScoreCriteria.RenderType.INTEGER);
   }

   protected ScoreCriteria(String p_i47677_1_, boolean p_i47677_2_, ScoreCriteria.RenderType p_i47677_3_) {
      this.field_197915_o = p_i47677_1_;
      this.field_197916_p = p_i47677_2_;
      this.field_197917_q = p_i47677_3_;
      field_96643_a.put(p_i47677_1_, this);
   }

   @Nullable
   public static ScoreCriteria func_197911_a(String p_197911_0_) {
      if (field_96643_a.containsKey(p_197911_0_)) {
         return field_96643_a.get(p_197911_0_);
      } else {
         int i = p_197911_0_.indexOf(58);
         if (i < 0) {
            return null;
         } else {
            StatType<?> stattype = IRegistry.field_212634_w.func_212608_b(ResourceLocation.func_195828_a(p_197911_0_.substring(0, i), '.'));
            return stattype == null ? null : func_197912_a(stattype, ResourceLocation.func_195828_a(p_197911_0_.substring(i + 1), '.'));
         }
      }
   }

   @Nullable
   private static <T> ScoreCriteria func_197912_a(StatType<T> p_197912_0_, ResourceLocation p_197912_1_) {
      IRegistry<T> iregistry = p_197912_0_.func_199080_a();
      return iregistry.func_212607_c(p_197912_1_) ? p_197912_0_.func_199076_b(iregistry.func_212608_b(p_197912_1_)) : null;
   }

   public String func_96636_a() {
      return this.field_197915_o;
   }

   public boolean func_96637_b() {
      return this.field_197916_p;
   }

   public ScoreCriteria.RenderType func_178790_c() {
      return this.field_197917_q;
   }

   public static enum RenderType {
      INTEGER("integer"),
      HEARTS("hearts");

      private final String field_211840_c;
      private static final Map<String, ScoreCriteria.RenderType> field_211841_d;

      private RenderType(String p_i49784_3_) {
         this.field_211840_c = p_i49784_3_;
      }

      public String func_211838_a() {
         return this.field_211840_c;
      }

      public static ScoreCriteria.RenderType func_211839_a(String p_211839_0_) {
         return field_211841_d.getOrDefault(p_211839_0_, INTEGER);
      }

      static {
         Builder<String, ScoreCriteria.RenderType> builder = ImmutableMap.builder();

         for(ScoreCriteria.RenderType scorecriteria$rendertype : values()) {
            builder.put(scorecriteria$rendertype.field_211840_c, scorecriteria$rendertype);
         }

         field_211841_d = builder.build();
      }
   }
}
