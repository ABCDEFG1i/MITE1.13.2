package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class ArmorStandSilent extends NamedEntityFix {
   public ArmorStandSilent(Schema p_i49672_1_, boolean p_i49672_2_) {
      super(p_i49672_1_, p_i49672_2_, "EntityArmorStandSilentFix", TypeReferences.ENTITY, "ArmorStand");
   }

   public Dynamic<?> func_209650_a(Dynamic<?> p_209650_1_) {
      return p_209650_1_.getBoolean("Silent") && !p_209650_1_.getBoolean("Marker") ? p_209650_1_.remove("Silent") : p_209650_1_;
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      return p_207419_1_.update(DSL.remainderFinder(), this::func_209650_a);
   }
}
