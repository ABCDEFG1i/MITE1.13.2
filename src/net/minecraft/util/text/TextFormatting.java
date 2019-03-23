package net.minecraft.util.text;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum TextFormatting {
   BLACK("BLACK", '0', 0, 0),
   DARK_BLUE("DARK_BLUE", '1', 1, 170),
   DARK_GREEN("DARK_GREEN", '2', 2, 43520),
   DARK_AQUA("DARK_AQUA", '3', 3, 43690),
   DARK_RED("DARK_RED", '4', 4, 11141120),
   DARK_PURPLE("DARK_PURPLE", '5', 5, 11141290),
   GOLD("GOLD", '6', 6, 16755200),
   GRAY("GRAY", '7', 7, 11184810),
   DARK_GRAY("DARK_GRAY", '8', 8, 5592405),
   BLUE("BLUE", '9', 9, 5592575),
   GREEN("GREEN", 'a', 10, 5635925),
   AQUA("AQUA", 'b', 11, 5636095),
   RED("RED", 'c', 12, 16733525),
   LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13, 16733695),
   YELLOW("YELLOW", 'e', 14, 16777045),
   WHITE("WHITE", 'f', 15, 16777215),
   OBFUSCATED("OBFUSCATED", 'k', true),
   BOLD("BOLD", 'l', true),
   STRIKETHROUGH("STRIKETHROUGH", 'm', true),
   UNDERLINE("UNDERLINE", 'n', true),
   ITALIC("ITALIC", 'o', true),
   RESET("RESET", 'r', -1, (Integer)null);

   private static final Map<String, TextFormatting> NAME_MAPPING = Arrays.stream(values()).collect(Collectors.toMap((p_199746_0_) -> {
      return lowercaseAlpha(p_199746_0_.name);
   }, (p_199747_0_) -> {
      return p_199747_0_;
   }));
   private static final Pattern FORMATTING_CODE_PATTERN = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");
   private final String name;
   private final char formattingCode;
   private final boolean fancyStyling;
   private final String controlString;
   private final int colorIndex;
   @Nullable
   private final Integer color;

   private static String lowercaseAlpha(String p_175745_0_) {
      return p_175745_0_.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
   }

   private TextFormatting(String p_i49745_3_, char p_i49745_4_, int p_i49745_5_, @Nullable Integer p_i49745_6_) {
      this(p_i49745_3_, p_i49745_4_, false, p_i49745_5_, p_i49745_6_);
   }

   private TextFormatting(String p_i46292_3_, char p_i46292_4_, boolean p_i46292_5_) {
      this(p_i46292_3_, p_i46292_4_, p_i46292_5_, -1, (Integer)null);
   }

   private TextFormatting(String p_i49746_3_, char p_i49746_4_, boolean p_i49746_5_, int p_i49746_6_, @Nullable Integer p_i49746_7_) {
      this.name = p_i49746_3_;
      this.formattingCode = p_i49746_4_;
      this.fancyStyling = p_i49746_5_;
      this.colorIndex = p_i49746_6_;
      this.color = p_i49746_7_;
      this.controlString = "\u00a7" + p_i49746_4_;
   }

   @OnlyIn(Dist.CLIENT)
   public static String func_211164_a(String p_211164_0_) {
      StringBuilder stringbuilder = new StringBuilder();
      int i = -1;
      int j = p_211164_0_.length();

      while((i = p_211164_0_.indexOf(167, i + 1)) != -1) {
         if (i < j - 1) {
            TextFormatting textformatting = fromFormattingCode(p_211164_0_.charAt(i + 1));
            if (textformatting != null) {
               if (textformatting.isNormalStyle()) {
                  stringbuilder.setLength(0);
               }

               if (textformatting != RESET) {
                  stringbuilder.append((Object)textformatting);
               }
            }
         }
      }

      return stringbuilder.toString();
   }

   public int getColorIndex() {
      return this.colorIndex;
   }

   public boolean isFancyStyling() {
      return this.fancyStyling;
   }

   public boolean isColor() {
      return !this.fancyStyling && this != RESET;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Integer getColor() {
      return this.color;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isNormalStyle() {
      return !this.fancyStyling;
   }

   public String getFriendlyName() {
      return this.name().toLowerCase(Locale.ROOT);
   }

   public String toString() {
      return this.controlString;
   }

   @Nullable
   public static String getTextWithoutFormattingCodes(@Nullable String p_110646_0_) {
      return p_110646_0_ == null ? null : FORMATTING_CODE_PATTERN.matcher(p_110646_0_).replaceAll("");
   }

   @Nullable
   public static TextFormatting getValueByName(@Nullable String p_96300_0_) {
      return p_96300_0_ == null ? null : NAME_MAPPING.get(lowercaseAlpha(p_96300_0_));
   }

   @Nullable
   public static TextFormatting fromColorIndex(int p_175744_0_) {
      if (p_175744_0_ < 0) {
         return RESET;
      } else {
         for(TextFormatting textformatting : values()) {
            if (textformatting.getColorIndex() == p_175744_0_) {
               return textformatting;
            }
         }

         return null;
      }
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static TextFormatting fromFormattingCode(char p_211165_0_) {
      char c0 = Character.toString(p_211165_0_).toLowerCase(Locale.ROOT).charAt(0);

      for(TextFormatting textformatting : values()) {
         if (textformatting.formattingCode == c0) {
            return textformatting;
         }
      }

      return null;
   }

   public static Collection<String> getValidValues(boolean p_96296_0_, boolean p_96296_1_) {
      List<String> list = Lists.newArrayList();

      for(TextFormatting textformatting : values()) {
         if ((!textformatting.isColor() || p_96296_0_) && (!textformatting.isFancyStyling() || p_96296_1_)) {
            list.add(textformatting.getFriendlyName());
         }
      }

      return list;
   }
}
