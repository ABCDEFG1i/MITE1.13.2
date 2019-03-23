package net.minecraft.util.text;

public class TextComponentString extends TextComponentBase {
   private final String text;

   public TextComponentString(String p_i45159_1_) {
      this.text = p_i45159_1_;
   }

   public String getText() {
      return this.text;
   }

   public String getUnformattedComponentText() {
      return this.text;
   }

   public TextComponentString createCopy() {
      return new TextComponentString(this.text);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof TextComponentString)) {
         return false;
      } else {
         TextComponentString textcomponentstring = (TextComponentString)p_equals_1_;
         return this.text.equals(textcomponentstring.getText()) && super.equals(p_equals_1_);
      }
   }

   public String toString() {
      return "TextComponent{text='" + this.text + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }
}
