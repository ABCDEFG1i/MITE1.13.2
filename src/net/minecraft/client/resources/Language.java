package net.minecraft.client.resources;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Language implements Comparable<Language> {
   private final String languageCode;
   private final String region;
   private final String name;
   private final boolean bidirectional;

   public Language(String p_i1303_1_, String p_i1303_2_, String p_i1303_3_, boolean p_i1303_4_) {
      this.languageCode = p_i1303_1_;
      this.region = p_i1303_2_;
      this.name = p_i1303_3_;
      this.bidirectional = p_i1303_4_;
   }

   public String getLanguageCode() {
      return this.languageCode;
   }

   public boolean isBidirectional() {
      return this.bidirectional;
   }

   public String toString() {
      return String.format("%s (%s)", this.name, this.region);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof Language && this.languageCode.equals(((Language) p_equals_1_).languageCode);
      }
   }

   public int hashCode() {
      return this.languageCode.hashCode();
   }

   public int compareTo(Language p_compareTo_1_) {
      return this.languageCode.compareTo(p_compareTo_1_.languageCode);
   }
}
