package net.minecraft.util;

import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StringUtils {
   private static final Pattern PATTERN_CONTROL_CODE = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

   @OnlyIn(Dist.CLIENT)
   public static String ticksToElapsedTime(int p_76337_0_) {
      int i = p_76337_0_ / 20;
      int j = i / 60;
      i = i % 60;
      return i < 10 ? j + ":0" + i : j + ":" + i;
   }

   @OnlyIn(Dist.CLIENT)
   public static String stripControlCodes(String p_76338_0_) {
      return PATTERN_CONTROL_CODE.matcher(p_76338_0_).replaceAll("");
   }

   public static boolean isNullOrEmpty(@Nullable String p_151246_0_) {
      return org.apache.commons.lang3.StringUtils.isEmpty(p_151246_0_);
   }
}
