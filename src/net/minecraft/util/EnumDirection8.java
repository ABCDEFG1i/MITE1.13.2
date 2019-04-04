package net.minecraft.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;

public enum EnumDirection8 {
   NORTH(EnumFacing.NORTH),
   NORTH_EAST(EnumFacing.NORTH, EnumFacing.EAST),
   EAST(EnumFacing.EAST),
   SOUTH_EAST(EnumFacing.SOUTH, EnumFacing.EAST),
   SOUTH(EnumFacing.SOUTH),
   SOUTH_WEST(EnumFacing.SOUTH, EnumFacing.WEST),
   WEST(EnumFacing.WEST),
   NORTH_WEST(EnumFacing.NORTH, EnumFacing.WEST);

   private static final int field_208500_i = 1 << NORTH_WEST.ordinal();
   private static final int field_208501_j = 1 << WEST.ordinal();
   private static final int field_208502_k = 1 << SOUTH_WEST.ordinal();
   private static final int field_208503_l = 1 << SOUTH.ordinal();
   private static final int field_208504_m = 1 << SOUTH_EAST.ordinal();
   private static final int field_208505_n = 1 << EAST.ordinal();
   private static final int field_208506_o = 1 << NORTH_EAST.ordinal();
   private static final int field_208507_p = 1 << NORTH.ordinal();
   private final Set<EnumFacing> directions;

   EnumDirection8(EnumFacing... p_i47954_3_) {
      this.directions = Sets.immutableEnumSet(Arrays.asList(p_i47954_3_));
   }

   public Set<EnumFacing> getDirections() {
      return this.directions;
   }
}
