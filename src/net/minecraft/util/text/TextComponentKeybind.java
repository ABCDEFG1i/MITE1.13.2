package net.minecraft.util.text;

import java.util.function.Function;
import java.util.function.Supplier;

public class TextComponentKeybind extends TextComponentBase {
   public static Function<String, Supplier<String>> displaySupplierFunction = (p_193635_0_) -> {
      return () -> {
         return p_193635_0_;
      };
   };
   private final String keybind;
   private Supplier<String> displaySupplier;

   public TextComponentKeybind(String p_i47521_1_) {
      this.keybind = p_i47521_1_;
   }

   public String getUnformattedComponentText() {
      if (this.displaySupplier == null) {
         this.displaySupplier = displaySupplierFunction.apply(this.keybind);
      }

      return this.displaySupplier.get();
   }

   public TextComponentKeybind createCopy() {
      return new TextComponentKeybind(this.keybind);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof TextComponentKeybind)) {
         return false;
      } else {
         TextComponentKeybind textcomponentkeybind = (TextComponentKeybind)p_equals_1_;
         return this.keybind.equals(textcomponentkeybind.keybind) && super.equals(p_equals_1_);
      }
   }

   public String toString() {
      return "KeybindComponent{keybind='" + this.keybind + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }

   public String getKeybind() {
      return this.keybind;
   }
}
