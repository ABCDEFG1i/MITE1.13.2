package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum Half implements IStringSerializable {
   TOP("top"),
   BOTTOM("bottom");

   private final String field_212249_f;

   Half(String p_i49337_3_) {
      this.field_212249_f = p_i49337_3_;
   }

   public String toString() {
      return this.field_212249_f;
   }

   public String getName() {
      return this.field_212249_f;
   }
}
