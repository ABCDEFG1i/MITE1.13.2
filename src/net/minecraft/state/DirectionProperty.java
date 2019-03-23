package net.minecraft.state;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.EnumFacing;

public class DirectionProperty extends EnumProperty<EnumFacing> {
   protected DirectionProperty(String p_i45650_1_, Collection<EnumFacing> p_i45650_2_) {
      super(p_i45650_1_, EnumFacing.class, p_i45650_2_);
   }

   public static DirectionProperty create(String p_177712_0_, Predicate<EnumFacing> p_177712_1_) {
      return create(p_177712_0_, Arrays.stream(EnumFacing.values()).filter(p_177712_1_).collect(Collectors.toList()));
   }

   public static DirectionProperty create(String p_196962_0_, EnumFacing... p_196962_1_) {
      return create(p_196962_0_, Lists.newArrayList(p_196962_1_));
   }

   public static DirectionProperty create(String p_177713_0_, Collection<EnumFacing> p_177713_1_) {
      return new DirectionProperty(p_177713_0_, p_177713_1_);
   }
}
