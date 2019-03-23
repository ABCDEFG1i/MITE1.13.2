package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class EntityItemFrameFacing extends NamedEntityFix {
   public EntityItemFrameFacing(Schema p_i49662_1_, boolean p_i49662_2_) {
      super(p_i49662_1_, p_i49662_2_, "EntityItemFrameDirectionFix", TypeReferences.ENTITY, "minecraft:item_frame");
   }

   public Dynamic<?> func_209651_a(Dynamic<?> p_209651_1_) {
      return p_209651_1_.set("Facing", p_209651_1_.createByte(func_210567_a(p_209651_1_.getByte("Facing"))));
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      return p_207419_1_.update(DSL.remainderFinder(), this::func_209651_a);
   }

   private static byte func_210567_a(byte p_210567_0_) {
      switch(p_210567_0_) {
      case 0:
         return 3;
      case 1:
         return 4;
      case 2:
      default:
         return 2;
      case 3:
         return 5;
      }
   }
}
